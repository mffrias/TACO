package ar.edu.taco.stryker.api.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.Charset;
import java.security.DigestOutputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Scanner;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.multijava.mjc.JCompilationUnitType;

import ar.edu.jdynalloy.JDynAlloySemanticException;
import ar.edu.taco.TacoAnalysisResult;
import ar.edu.taco.TacoConfigurator;
import ar.edu.taco.TacoMain;
import ar.edu.taco.TacoNotImplementedYetException;
import ar.edu.taco.engine.JUnitStage;
import ar.edu.taco.engine.SnapshotStage;
import ar.edu.taco.engine.StrykerStage;
import ar.edu.taco.jml.parser.JmlParser;
import ar.edu.taco.junit.RecoveredInformation;
import ar.edu.taco.stryker.MethodToCheck;
import ar.edu.taco.stryker.api.impl.MuJavaController.MsgDigest;
import ar.edu.taco.stryker.api.impl.input.DarwinistInput;
import ar.edu.taco.stryker.api.impl.input.MuJavaFeedback;
import ar.edu.taco.stryker.api.impl.input.MuJavaInput;
import ar.edu.taco.utils.FileUtils;
import ar.uba.dc.rfm.dynalloy.analyzer.AlloyAnalysisResult;

import com.google.common.collect.Lists;
import com.google.common.io.Files;
// to be removed later when editFile is replaced in this class.

/**
 * Validates fix candidates with TACO and computes the variabilization feedback that lets Stryker
 * prune mutants. It has two roles in the repair pipeline:
 * <ol>
 * <li><b>Candidate validation</b>, on this controller's own thread. Candidates are mutants that
 * passed runtime assertion checking (RAC) in {@link OpenJMLController}; they arrive through
 * queue 3 of {@link StrykerStage#weedQueue}. {@link #validateCandidate(DarwinistInput)} runs TACO
 * bounded verification on each one. SAT (TACO found a counterexample, so the candidate is still
 * buggy) stores the counterexample as a new JUnit input and asks for feedback
 * ({@link #queueFalseCandidate}); UNSAT sends the candidate to {@link #lastRACAnalysis}, which
 * re-runs every stored counterexample with RAC before accepting it in {@link #fixFound}.</li>
 * <li><b>Pruning feedback</b> (thesis Chapter 3, Algorithm {@code getFeedback}).
 * {@link #computateFeedback(DarwinistInput)} is called by {@link MuJavaController} on
 * <em>its</em> thread when it dequeues a {@link MuJavaInput} whose {@code computateFeedback} flag
 * is set. It variabilizes the failing mutant against one failing input and returns the
 * {@link MuJavaInput} that tells MuJavaController which sibling mutants it may skip.</li>
 * </ol>
 *
 * <p><b>Lifecycle and threading.</b> Singleton ({@link #getInstance()}), started by
 * {@link SimpleStrykerAPI#start()} with one worker thread. The queue and {@code enqueueTask}
 * inherited from {@link AbstractBaseController} are not used; input comes from
 * {@link WeedQueue#takeFrom3()}. {@link #computateFeedback} and {@link #validateCandidate} are
 * {@code synchronized} on the singleton, so the two threads never run TACO at the same time nor
 * overwrite the subject file concurrently. This matters because every TACO run made by Stryker
 * happens in one of these two methods and TACO keeps global state (for example the
 * {@link TacoConfigurator} and {@link JmlParser} singletons). {@link SimpleStrykerAPI#fixBug}
 * polls this controller's {@code willShutdown} flag, so shutting this controller down is what
 * ends a repair run for the caller.
 *
 * <p><b>Files.</b> TACO reads the class under analysis from its source path, i.e. the subject
 * file under {@code tests/} ({@link DarwinistInput#getOriginalFilename()}). To analyse a
 * candidate or a variabilized method, both TACO-running methods back the subject file up to
 * {@code <originalFilename>_temp}, copy the file to analyse over it, compile it in place with the
 * system Java compiler (output discarded), run TACO and finally copy the backup back. The restore
 * is not in a {@code finally} block: if an exception escapes, or when the run ends inside
 * {@link #fixFound} (see there), the subject file keeps the last version copied over it. Other
 * files written: {@code StrykerLastReports.txt} and {@code StrykerLastFixFound.java} in the
 * working directory ({@link #fixFound}), a {@code sequential/} copy of each mutant whose feedback
 * is computed ({@link #computateFeedback}), and new JUnit counterexamples
 * ({@link #generateJunitFile}).
 *
 * <p>Counterexamples are shared with the other controllers through the static arrays
 * {@code StrykerStage.junitInputs} and {@code StrykerStage.junitFiles}
 * ({@code StrykerStage.MAX_COUNTEREXAMPLES} slots, allocated in {@link TacoMain#run}); slot 0
 * holds the counterexample of the initial TACO check. The fill
 * index {@code StrykerStage.indexToLastJUnitInput} is never reset, so this holds only for the
 * first repair run in a JVM.
 */
public class DarwinistController extends AbstractBaseController<DarwinistInput> {

    private static Logger log = Logger.getLogger(DarwinistController.class);

    private static DarwinistController instance;

    /** Filenames of the candidates accepted as fixes, in the order they were found. */
    private List<String> resolvedBugs = Lists.newArrayList();

    public static final String PATH_SEP = System.getProperty("path.separator");
    public static final String FILE_SEP = System.getProperty("file.separator");
    public static final String MUTANTS_DEST_PACKAGE = "ar.edu.itba.stryker.mutants";

    synchronized static DarwinistController getInstance() {
        if(instance == null) {
            instance = new DarwinistController();
        }
        return instance;
    }

    /**
     * Runs each RAC test of {@link #lastRACAnalysis} so that it can be bounded by a timeout;
     * replaced by a new single-thread executor after every timeout.
     */
    ExecutorService executor = RacTimeout.newExecutor();

    /** Thread executing the current RAC test, for {@link RacTimeout#endTimedOutRun}. */
    private Thread runningThread = null;

    private DarwinistController() {

    }

    /**
     * Main loop of this controller's single worker thread. Until {@code willShutdown} is set, it
     * takes the next {@link DarwinistInput} from queue 3 and either
     * <ul>
     * <li>forwards it to queue 1 wrapped in a {@link MuJavaInput} with
     * {@code computateFeedback = true}, if {@link DarwinistInput#isForSeqProcessing()}, so that
     * the feedback is computed on MuJavaController's thread. No current producer puts such inputs
     * on queue 3 (feedback requests go straight to queue 1 from {@link OpenJMLController},
     * {@link #queueFalseCandidate} and {@link #processFalseCandidate}), so this branch appears
     * unused; or</li>
     * <li>validates it as a fix candidate with {@link #validateCandidate(DarwinistInput)}.</li>
     * </ul>
     * It then acknowledges the input ({@code ack(2)}); if nothing is left queued or in flight in
     * any of the three queues, it shuts all controllers down and clears the queues.
     *
     * <p>Only {@link InterruptedException} is caught (that is how {@code shutdownNow()} wakes the
     * thread up); any other exception ends the loop without acknowledging the input. When
     * {@link OpenJMLController}'s loop ends it enqueues a {@link DarwinistInput} whose fields are
     * all {@code null}; if this loop ever takes it before shutting down,
     * {@link #validateCandidate} fails with a {@link NullPointerException}.
     */
    @Override
    protected Runnable getRunnable() {
        return new Runnable() {

            @Override
            public void run() {
                while (!willShutdown.get()) {
                    DarwinistInput input = null;
                    try {
                        input = StrykerStage.weedQueue.takeFrom3();
//                        log.debug("Queue size: "+StrykerStage.weedQueue.size3());

                        if (input.isForSeqProcessing()) {
                            MuJavaInput inputForFeedback = new MuJavaInput(null, null, null, null, null, null, null, null, null, null);                
                            inputForFeedback.setComputateFeedback(true);
                            inputForFeedback.setInputForFeedback(input);
                            inputForFeedback.setFullyQualifiedClassName(input.getFullyQualifiedClassName());
                            inputForFeedback.setMethodUnderAnalysis(input.getMethod());
                            StrykerStage.weedQueue.enqueue1(inputForFeedback);
                        } else {
                            validateCandidate(input);
                        }
                        if (StrykerStage.weedQueue.ack(2) == 0) {
                            UnskippableMuJavaController.getInstance().shutdownNow();
                            MuJavaController.getInstance().shutdownNow();
                            OpenJMLController.getInstance().shutdownNow();
                            DarwinistController.getInstance().shutdown();
                            StrykerStage.weedQueue.clearQueues();
                        }
                    } catch (InterruptedException e) {
                        //                      e.printStackTrace();
                    }
                }
            }
        };
    }
    /**
     * Variabilization loop (thesis Chapter 3, Algorithm {@code getFeedback}) for one failing
     * mutant. Each iteration:
     * <ol>
     * <li>calls {@link VariablizationData#variablizeNext(DarwinistInput)}, which replaces one more
     * side of a mutable statement (bottom-up and cumulatively; right-hand side first, then the
     * left-hand side if there is one) by fresh {@code customvar_N} parameters and writes the
     * result to {@code input.getSeqVariablizedFilename()}. If nothing more can be variabilized the
     * loop ends with {@code reachedUnvariablizableExpression = true};</li>
     * <li>prefixes with a JML {@code nullable} annotation every parameter on the method's
     * declaration line that contains {@code customvar} and is not of a primitive type, presumably
     * so that TACO may also choose {@code null} for it. The declaration line is any line that
     * contains both the method name and {@code "public"}, and the primitive test is a substring
     * match on the parameter text;</li>
     * <li>copies the result over the subject file, compiles it and runs {@code tacoMain} (built
     * with the failing input fixed, see {@link #getTacoMainWithFixedInput(String)}), unless
     * {@link VariablizedSATVerdicts} already holds a verdict for the same source. Before each
     * call, {@code methodToCheck} is set to the variabilized method's signature: the driver's
     * parameter types followed by the customvar types so far
     * ({@link VariablizationData#getVariablizedParameterTypes()}, see
     * {@link ar.edu.taco.stryker.MethodToCheck}).</li>
     * </ol>
     * UNSAT means that no values of the variabilized expressions make the method satisfy its
     * contract on that input, so no mutation of those statements can make it pass that input, and
     * the loop goes on; SAT stops the loop.
     *
     * <p>Results stored in {@code vdata}: {@code uncompilable} (javac failed, TACO returned
     * {@code null}, or TACO threw {@link JDynAlloySemanticException} or
     * {@link TacoNotImplementedYetException}); {@code reachedUnvariablizableExpression}; and,
     * unless uncompilable, {@code UNSAT} as the negation of the last verdict.
     *
     * <p>Quirks: any other exception from TACO is logged and stops the loop as if that step were
     * SAT (nothing is cached), so only the statements already shown UNSAT are skipped. (Until 2026
     * it counted as UNSAT and the loop went on, which could prune the whole search.) A cache hit still updates
     * the TACO statistics in {@link StrykerStage} as if TACO had run. The values written to
     * {@code vdata} when TACO returns {@code null} do not reach the feedback:
     * {@code reachedUnvariablizableExpression} is overwritten after the loop and
     * {@link #computateFeedback} takes its uncompilable branch. The subject file is restored from
     * {@code <originalFilename>_temp} only if this method returns normally.
     *
     * @param tacoMain TACO instance with the failing input fixed
     * @param vdata variabilization state of this mutant; updated in place
     * @param input the feedback request (mutant, method, configuration and properties)
     * @throws IOException if a file copy or read fails
     */
    private void variablize(TacoMain tacoMain, VariablizationData vdata, DarwinistInput input) throws IOException {
        TacoAnalysisResult analysis_result = null;
        boolean satVerdict = false;
        boolean reachedUnvariablizableExpression = false;
        boolean notCompilable = false;

        String filename = input.getSeqVariablizedFilename();
        String originalFilename = input.getOriginalFilename();

        File originalFile = new File(originalFilename);

        File newFile = new File(originalFilename+"_temp");
        newFile.createNewFile();

        Files.copy(originalFile, newFile);

        final String configurationFile = input.getConfigurationFile();
        final Properties props = new Properties(input.getOverridingProperties());
        Properties oldProps = input.getOverridingProperties();
        for(Entry<Object,Object> o : oldProps.entrySet()){
            props.put(o.getKey(), o.getValue());
        }
        props.put("attemptToCorrectBug",false);
        props.put("generateUnitTestCase",false);
        // Variabilization appends customvar parameters to the method, so TACO has to be
        // told the signature of the variabilized method before each check.
        final String originalMethodToCheck = oldProps.getProperty(TacoConfigurator.METHOD_TO_CHECK_FIELD);

        while (satVerdict==false) {

            //Analizar con TACO el metodo actual, previa variabilizacion
            //Los que dan SAT, avisarle a MuJavaController (estoy haciendo CHECK)
            //Los que que dan UNSAT, a variabilizar (estoy haciendo CHECK)
            if (!vdata.variablizeNext(input)) {
                //No hay mas que variabilizar, no tiene solucion
                //                                    System.out.println("No hay solucion");
                reachedUnvariablizableExpression = true;
                break;
            }

            String fileContent = FileUtils.readFile(filename);

            String lines[] = fileContent.split("\n");
            String newFileContent = "";
            for(int i = 0; i < lines.length; ++i) {
                String line = lines[i];
                if (line.contains(input.getMethod()) && line.contains("public")) {
                    String parametersLine = line.substring(line.indexOf('(') + 1, line.indexOf(')'));
                    String parameters[] = parametersLine.split(",");
                    String newParametersLine = "";
                    for (int j = 0; j < parameters.length; ++j) {
                        String parameter = parameters[j];
                        if (parameter.contains("customvar") && !parameter.contains("nullable") && !parameter.contains("char") && !parameter.contains("boolean") && !parameter.contains("double") 
                                && !parameter.contains("float") && !parameter.contains("long") && !parameter.contains("int")  
                                && !parameter.contains("short") && !parameter.contains("byte")) {
                            parameter = "/*@nullable@*/ " + parameter;
                        }
                        if (j != 0) {
                            newParametersLine += ",";
                        }
                        newParametersLine += parameter;
                    }
                    line = line.replace(parametersLine, newParametersLine);
                }
                newFileContent += line + "\n";
            }

            FileUtils.writeToFile(filename, newFileContent);

            File newTestFile = new File(filename);
            newFile.createNewFile();

            Files.copy(newTestFile, originalFile);


            String currentClasspath = System.getProperty("java.class.path");
            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
            long nanoPrev = System.currentTimeMillis();
            int compilationResult = compiler.run(null, new NullOutputStream(), new NullOutputStream(), 
                    new String[]{"-classpath", currentClasspath, originalFilename});
            StrykerStage.compilationMillis += System.currentTimeMillis() - nanoPrev;
            /**/                    compiler = null;
            if(compilationResult == 0){
                log.debug("Compilation is successful: "+filename);
                //                                    System.out.println("Por arrancar TACO...");
                nanoPrev = System.currentTimeMillis();
                if (VariablizedSATVerdicts.getInstance().containsFile(newTestFile)) {
                    //                  analysis_result = VariablizedSATVerdicts.getInstance().get(newTestFile);
                    satVerdict = VariablizedSATVerdicts.getInstance().get(newTestFile);

                    log.warn("AVOIDED ONE SAT CHECK. THE OUTCOME IS: " + (satVerdict ? "SAT" : "UNSAT"));
                } else {    
                    try {
                        List<String> parameterTypes = new ArrayList<String>(MethodToCheck.parameterTypes(originalMethodToCheck));
                        parameterTypes.addAll(vdata.getVariablizedParameterTypes());
                        props.put(TacoConfigurator.METHOD_TO_CHECK_FIELD,
                                MethodToCheck.of(MethodToCheck.methodName(originalMethodToCheck), parameterTypes));
                        log.warn("========================================================================PREVIOUS TO CALL OF TACOMAIN");
                        analysis_result = tacoMain.run(configurationFile, props);
                        if (analysis_result == null) {
                            satVerdict = false;
                            vdata.setStillFatherable(false);
                            vdata.setReachedUnvariablizableExpression(true);
                            vdata.setLastVariablizedMutID(null);
                            notCompilable = true;
                            break;
                        }
                        satVerdict = analysis_result.get_alloy_analysis_result().isSAT();
                        VariablizedSATVerdicts.getInstance().put(newTestFile, satVerdict);
                        log.warn("STORING A NEW VARIABLIZATION SAT CHECK");

                    } catch (JDynAlloySemanticException e) {
                        log.warn("Variablization: TACO threw JDynAlloySemanticException, assuming non-compilable and skipping");
                        notCompilable = true;
                        break;
                    } catch (TacoNotImplementedYetException e) {
                        log.warn("Variablization: TACO threw TacoNotImplementedYetException, assuming non-compilable and skipping");
                        notCompilable = true;
                        break;
                    } catch (Exception e) {
                        // An error proves nothing: stop here as if this step were SAT, so only the
                        // statements already shown UNSAT are skipped. Counting it as UNSAT went on to
                        // prune every statement, e.g. when the fixed input holds a value that does
                        // not fit in Alloy's integers (a serialVersionUID).
                        log.error("Variablization: TACO failed, stopping the feedback here: " + e);
                        satVerdict = true;
                        break;
//                        e.printStackTrace();
                    }
                }
                Long tacoTime = System.currentTimeMillis() - nanoPrev;
                StrykerStage.tacoMillis += tacoTime;
                StrykerStage.totalSolvings++;
                StrykerStage.maxSolvingTime = StrykerStage.maxSolvingTime > tacoTime ? StrykerStage.maxSolvingTime : tacoTime;
                StrykerStage.minSolvingTime = StrykerStage.minSolvingTime < tacoTime ? StrykerStage.minSolvingTime : tacoTime;
                //                analysisResult = analysis_result.get_alloy_analysis_result();
            } else {
                //Hubo error de compilacion
                notCompilable = true;
                break;
            }
            //            if (analysisResult.isUNSAT()) {
            //                log.warn("UNSAT Variablization");
            //                //                if (!vdata.isLastVariablizedMutIDRight()) {
            //                //                    vdata.setLastVariablizedMutIDRight(true);
            //                //                    break;
            //                //                }
            //            }
        }

        log.debug("Restoring file: "+originalFile);
        Files.copy(newFile, originalFile);
        vdata.setUncompilable(notCompilable);
        vdata.setReachedUnvariablizableExpression(reachedUnvariablizableExpression);
        if (!notCompilable) {
            vdata.setUNSAT(!satVerdict);
        }
    }

    /**
     * Builds a {@link TacoMain} whose analysis is restricted to one stored counterexample input.
     * The input is looked up among the first {@code StrykerStage.indexToLastJUnitInput} entries of
     * {@code StrykerStage.junitInputs} by comparing {@code junitFilename} with the path of the
     * generated JUnit source of each class ({@code <user.dir>/generated/...}, derived from the
     * class name with every {@code "output"} replaced by {@code "generated"}); {@code null}
     * selects slot 0, the counterexample of the initial TACO check. The class is instantiated
     * reflectively and its public {@code theData} map (emitted by
     * {@link ar.edu.taco.junit.JUnitPrettyPrinter}) is passed to {@link TacoMain#TacoMain(HashMap)}.
     * TACO hands the map down its translation pipeline as {@code inputToFix}; the fixed-input
     * contract itself is built in the JDynAlloy translator, which is not part of this source tree.
     * The thesis describes it as {@code requires this == this0 && args == args0; ensures !post}.
     *
     * @param junitFilename path of the generated JUnit source of the counterexample, or
     *        {@code null}
     * @return the TACO instance, or {@code null} if reflection fails; with {@code null} the first
     *         TACO call made by {@link #variablize} throws, which stops the feedback there
     * @throws IllegalArgumentException if {@code junitFilename} matches no stored input (callers
     *         only pass stored ones; see {@link #queueFalseCandidate})
     */
    private TacoMain getTacoMainWithFixedInput(String junitFilename) throws IllegalArgumentException {
        Class<?>[] inputs = StrykerStage.junitInputs;
        int index = 0;
        if (junitFilename != null){
            String location = System.getProperty("user.dir") + System.getProperty("file.separator") + 
                    "generated" + System.getProperty("file.separator");
            // Map the loaded class (package ar.edu.output.junit) back to its generated file; only
            // the class name is rewritten, so the working directory may contain "output".
            while (index < StrykerStage.indexToLastJUnitInput && inputs[index] != null && 
                    !(location + inputs[index].getName()
                            .replace(".", System.getProperty("file.separator"))
                            .replace("output", "generated")+".java").equals(junitFilename)){
                index++;
            }
            if (index >= inputs.length || inputs[index] == null)
                throw new IllegalArgumentException("Counterexample " + junitFilename + " is not in the counterexample pool");
        } else {
            index = 0;
        }
        Class<?> claz = inputs[index];
        Object o1;
        try {
            o1 = claz.getConstructor((Class<?>[])null).newInstance();
            Field fi = claz.getDeclaredField("theData");
            @SuppressWarnings("unchecked")
            HashMap<String, Object> o2 = (HashMap<String,Object>)fi.get(o1);
            return new TacoMain(o2);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException
                | NoSuchMethodException | SecurityException | NoSuchFieldException e) {
            //TODO manage exceptions
            return null;
        }
    }

    /**
     * Computes the pruning feedback for a mutant that failed on a known input and returns the
     * {@link MuJavaInput} that MuJavaController should process next (sibling generation guided by
     * the feedback). Called by {@link MuJavaController} on its own thread.
     *
     * <p>Preparation: copies the mutant ({@code input.getFilename()}) to a {@code sequential/}
     * directory next to it (the path is built by splitting on {@code '/'} and using the file name
     * as a {@code replaceFirst} pattern); numbers its mutable lines with 1-based {@code mutID}
     * comments ({@link StrykerJavaFileInstrumenter#insertMutIDs(DarwinistInput)}); drops every
     * line containing {@code //@decreasing} or {@code /*@decreasing}; unrolls the method's
     * {@code while} loops with TACO's loop-unroll bound ({@link LoopUnrollTransformation});
     * collects the variabilizable expressions
     * ({@link VariablizationData#preprocessVariabilization2}); builds TACO with the failing input
     * {@code input.getSeqMethodInput()} fixed; and runs {@link #variablize} unless there is
     * nothing to variabilize. {@link IOException}s are only printed; exceptions from
     * {@link #getTacoMainWithFixedInput(String)} propagate to the caller.
     *
     * <p>Result: the returned input carries {@code input.getFeedback()}, mutated in place, with
     * {@code getSibling = true} and:
     * <ul>
     * <li>if nothing was variabilizable, or variabilization ended at an expression it cannot
     * variabilize (every step so far UNSAT): {@code UNSAT = true}, {@code mutateRight} from the
     * last step (default {@code true}) and {@code skipUntilMutID = lastVariablizedMutID - 1},
     * which turns the 1-based mutID into a 0-based index into {@code feedback.getMutableLines()}.
     * If no step was taken at all, {@code mutableLines.size() - 1} (the bottom-most mutable line,
     * as that list is ascending) is used instead, still with {@code UNSAT = true} although TACO
     * was never called;</li>
     * <li>if a step did not compile or TACO could not translate it: no skipping
     * ({@code skipUntilMutID = null}, {@code mutateRight = true}, {@code fatherable = true});</li>
     * <li>if a step gave SAT: {@code skipUntilMutID = lastVariablizedMutID - 1},
     * {@code mutateRight} from the side variabilized last and {@code UNSAT = false}.</li>
     * </ul>
     * In the first and last cases {@code fatherable} comes from
     * {@link VariablizationData#isStillFatherable()} (or is {@code true} if that is unset) when
     * {@link MuJavaController#fatherizationPruningOn} is set, and is {@code true} otherwise.
     * {@link MuJavaController#queueNextRelevantSibling(MuJavaInput)} maps {@code skipUntilMutID} to
     * a position of the line-mutation-index array. The {@code else if (unvariablizable)} branch is
     * unreachable, since the first condition already covers that case.
     *
     * @param input feedback request: the failing mutant and the failing input
     *        ({@code getSeqMethodInput()})
     * @return the input for MuJavaController, with the feedback attached
     */
    public synchronized MuJavaInput computateFeedback(DarwinistInput input) {
        final String oldFilename = input.getFilename();

        final String seqFileName = oldFilename.replaceFirst(
                oldFilename.split("/")[oldFilename.split("/").length - 1], "sequential/" +
                        oldFilename.split("/")[oldFilename.split("/").length - 1]);

        try {
            input.setSeqFilesPrefix(seqFileName);
            String content = FileUtils.readFile(input.getFilename());
            FileUtils.writeToFile(input.getSeqFilesPrefix(), content); //copiar de filename a seqfilesprefix
            StrykerJavaFileInstrumenter.insertMutIDs(input);
            content = FileUtils.readFile(input.getSeqFilesPrefix());
            String newContent = "";
            String lines[] = content.split("\n");
            for (int i = 0; i < lines.length; ++i) {
                if (!lines[i].contains("//@decreasing") && !lines[i].contains("/*@decreasing")) {
                    newContent += lines[i] + "\n";
                }
            }
            FileUtils.writeToFile(input.getSeqFilesPrefix(), newContent);
            LoopUnrollTransformation.javaUnroll(TacoConfigurator.getInstance().getDynAlloyToAlloyLoopUnroll(), 
                    input.getMethod(), input.getSeqFilesPrefix(), input.getSeqFilesPrefix());
        } catch (IOException e) {
            log.error("Unknown error while unrolling code for variablization");
            e.printStackTrace();
        }

        VariablizationData vdata = VariablizationData.preprocessVariabilization2(input);
        boolean unvariablizable = !vdata.isVariablizable();

        input.setSeqVariablizedFilename(input.getSeqFilesPrefix());

        TacoMain tacoMain = getTacoMainWithFixedInput(input.getSeqMethodInput());

        if (!unvariablizable) {
            try {
                variablize(tacoMain, vdata, input);
            } catch (IOException e) {
                //TODO what now?
                e.printStackTrace();
            }
        }
        
//        new File(seqFileName).delete(); //No es necesario

        MuJavaInput mujavainput = new MuJavaInput(input.getFilename(), input.getMethod(), 
                input.getMutantsToApply(), new AtomicInteger(0), input.getConfigurationFile(), 
                input.getOverridingProperties(), input.getOriginalFilename(), input.getSyncObject(),
                input.getFullyQualifiedClassName(), input.getMethod());
        mujavainput.setOldFilename(input.getOldFilename());
        MuJavaFeedback feedback = input.getFeedback();
        if (unvariablizable || vdata.getReachedUnvariablizableExpression()) {
            feedback.setGetSibling(true);
            if (vdata.isLastVariablizedMutIDRight() != null) {
                feedback.setMutateRight(vdata.isLastVariablizedMutIDRight());
            } else {
                feedback.setMutateRight(true);
            }
            if (vdata.getLastVariablizedMutID() != null) {
                feedback.setSkipUntilMutID(vdata.getLastVariablizedMutID() - 1);
            } else {
                feedback.setSkipUntilMutID(input.getFeedback().getMutableLines().size() - 1);
            }
            feedback.setUNSAT(true);
            if (MuJavaController.fatherizationPruningOn) {
                if (vdata.isStillFatherable() != null) {
                    feedback.setFatherable(vdata.isStillFatherable());
                } else {
                    feedback.setFatherable(true);
                }
            } else {
                feedback.setFatherable(true);
            }
        } else if (unvariablizable) {
            feedback.setFatherable(false);
            feedback.setGetSibling(true);
            feedback.setSkipUntilMutID(null);
            feedback.setMutateRight(true);

        } else if (vdata.isUncompilable()) {
            feedback.setFatherable(true);
            feedback.setGetSibling(true);
            feedback.setSkipUntilMutID(null);
            feedback.setMutateRight(true);
        } else {
            feedback.setGetSibling(true);
            feedback.setMutateRight(vdata.isLastVariablizedMutIDRight());
            feedback.setSkipUntilMutID(vdata.getLastVariablizedMutID() - 1);
            feedback.setUNSAT(vdata.isUNSAT());
            if (MuJavaController.fatherizationPruningOn) {
                feedback.setFatherable(vdata.isStillFatherable());
            } else {
                feedback.setFatherable(true);
            }
        }
        mujavainput.setMuJavaFeedback(feedback);
        return mujavainput;
    }

    /**
     * Validates a fix candidate (a mutant that passed RAC on the inputs tried by
     * {@link OpenJMLController}) by bounded verification of the whole contract with TACO.
     *
     * <p>Backs the subject file up to {@code <originalFilename>_temp}, copies the candidate over it,
     * compiles it in place (no {@code -d}, so the class file is written next to the source) and
     * runs a fresh {@link TacoMain} with {@code attemptToCorrectBug} and
     * {@code generateUnitTestCase} disabled. Then:
     * <ul>
     * <li>javac fails: only logged; nothing is sent back to MuJavaController for this mutant;</li>
     * <li>TACO returns {@code null} (its JML parser rejected the file) or throws: MuJavaController
     * is asked for the next sibling without skipping; the mutant is marked not fatherable only in
     * the {@code null} case;</li>
     * <li>SAT, i.e. TACO found a counterexample: {@link #queueFalseCandidate}, or the candidate is
     * just counted as false when there is no Alloy result or its feedback is {@code stop}ped;</li>
     * <li>UNSAT, i.e. the candidate passes bounded verification: {@link #lastRACAnalysis}.</li>
     * </ul>
     * Feedback objects with {@code stop} set come from {@link UnskippableMuJavaController}, which
     * sends its mutants through RAC and validation without asking for feedback; such inputs are
     * never re-enqueued. The subject file is then restored, unless an exception escapes or the run
     * ends inside {@link #fixFound}.
     *
     * @param input the candidate
     */
    private synchronized void validateCandidate(DarwinistInput input) {
        TacoMain tacoMain = new TacoMain(null);

        String filename;
        String originalFilename;

        filename = input.getFilename();
        originalFilename = input.getOriginalFilename();

        final String configurationFile = input.getConfigurationFile();
        final Properties props = new Properties(input.getOverridingProperties());
        Properties oldProps = input.getOverridingProperties();
        for(Entry<Object,Object> o : oldProps.entrySet()){
            props.put(o.getKey(), o.getValue());
        }

        File originalFile = new File(originalFilename);
        //                      originalFile.delete();

        File newFile = new File(originalFilename+"_temp");
        try {
            newFile.createNewFile();
            Files.copy(originalFile, newFile);

            File newTestFile = new File(filename);
            newTestFile.createNewFile();

            Files.copy(newTestFile, originalFile);
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        String currentClasspath = System.getProperty("java.class.path");
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        long nanoPrev = System.currentTimeMillis();
        int compilationResult = compiler.run(null, new NullOutputStream(), new NullOutputStream(), 
                new String[]{"-classpath", currentClasspath, originalFilename});
        StrykerStage.compilationMillis += System.currentTimeMillis() - nanoPrev;
        compiler = null;
        if(compilationResult == 0){
            log.debug("Compilation is successful: "+filename);
            props.put("attemptToCorrectBug",false);
            props.put("generateUnitTestCase",false);
            TacoAnalysisResult analysis_result = null;
            boolean compiles = true;
            boolean stillFatherable = true;
            try {
                nanoPrev = System.currentTimeMillis();
                analysis_result = tacoMain.run(configurationFile, props);
                if (analysis_result == null) {
                    stillFatherable = false;
                    compiles = false;
                }
                Long tacoTime = System.currentTimeMillis() - nanoPrev;
                StrykerStage.totalSolvings++;
                StrykerStage.tacoMillis += System.currentTimeMillis() - nanoPrev;
                StrykerStage.maxSolvingTime = StrykerStage.maxSolvingTime > tacoTime ? StrykerStage.maxSolvingTime : tacoTime;
                StrykerStage.minSolvingTime = StrykerStage.minSolvingTime < tacoTime ? StrykerStage.minSolvingTime : tacoTime;
            } catch (JDynAlloySemanticException e) {
                log.warn("Candidate Validation: TACO threw JDynAlloySemanticException, assuming non-compilable and skipping");
                //                e.printStackTrace();
                compiles = false;
            } catch (TacoNotImplementedYetException e) {
                log.warn("Candidate Validation: TACO threw TacoNotImplementedYetException, assuming non-compilable and skipping");
                //                e.printStackTrace();
                compiles = false;
            } catch (Exception e) {
                log.error("Candidate Validation: Unknown Error in TACO.");
                e.printStackTrace();
                compiles = false;
            }

            if (!compiles) {
                MuJavaInput mujavainput = new MuJavaInput(input.getFilename(), input.getMethod(), 
                        input.getMutantsToApply(), new AtomicInteger(0), input.getConfigurationFile(), 
                        input.getOverridingProperties(), input.getOriginalFilename(), input.getSyncObject(),
                        input.getFullyQualifiedClassName(), input.getMethod());

                MuJavaFeedback prevFeedback = input.getFeedback();

                prevFeedback.setFatherable(stillFatherable);
                prevFeedback.setGetSibling(true);
                prevFeedback.setSkipUntilMutID(null);
                prevFeedback.setMutateRight(true);
                mujavainput.setMuJavaFeedback(prevFeedback);

                try {
                    log.debug("Restoring file: "+originalFile);
                    Files.copy(newFile, originalFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (!prevFeedback.isStop()) {
                    StrykerStage.weedQueue.enqueue1(mujavainput);
                }
                
                return;
            } 

            AlloyAnalysisResult analysisResult = analysis_result.get_alloy_analysis_result();
            if(analysisResult == null || analysisResult.isSAT()) {
                log.debug(filename + " didn't solve the problem");

                log.debug("Valor 1: " + (StrykerStage.indexToLastJUnitInput - 1));
                log.debug("Valor 2: " + StrykerStage.indexToLastJUnitInput);

                if (analysisResult != null && !input.getFeedback().isStop()){
                    queueFalseCandidate(input, analysis_result, props, tacoMain.getCompilationUnits());
                } else {
                    StrykerStage.falseCandidates++;
                }
            } else {
                if (analysisResult.isUNSAT()){
                    lastRACAnalysis(input);
                }
            }   
        }else{
            log.info("Compilation Failed");
        } 
        try {
            log.debug("Restoring file: "+originalFile);
            Files.copy(newFile, originalFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles a candidate that TACO showed to be still buggy (SAT in {@link #validateCandidate}).
     * Counts it as a false candidate and turns TACO's counterexample into a new stored JUnit
     * input ({@link #generateJunitFile}), so later candidates are also checked against it.
     * Then, if {@link MuJavaController#feedbackOn} and the counterexample is in the pool, it
     * requests pruning feedback for this candidate and the new counterexample: a
     * {@link DarwinistInput} with
     * {@code forSeqProcessing = true} and {@code seqMethodInput} set to the counterexample, whose
     * properties copy the overriding properties but force {@code attemptToCorrectBug=false},
     * {@code generateUnitTestCase=false}, {@code generateCheck=true}, {@code generateRun=false}
     * (only for keys already present; {@code methodToCheck} stays the driver's, see
     * {@link #variablize}), is wrapped in a
     * {@link MuJavaInput} with {@code computateFeedback = true} and enqueued to queue 1. Without
     * feedback, or when the counterexample could not be stored (pool full, or its test class did
     * not load; feedback looks counterexamples up in the pool), it just asks MuJavaController for
     * the next sibling, so the search goes on without pruning for this candidate.
     *
     * <p>{@code classToCheck} is read from the global {@link TacoConfigurator} of the TACO run that
     * just finished. Exceptions inside the {@code try} block are swallowed silently. A
     * {@link RuntimeException} from {@link #generateJunitFile} (e.g. TACO's snapshot cannot
     * instantiate an object of the counterexample) is logged and the candidate gets no feedback;
     * until 2026 it escaped and ended this controller's thread, hanging the run.
     *
     * @param input the false candidate
     * @param analysis_result the SAT result holding the counterexample
     * @param props properties of that TACO run (source of {@code methodToCheck})
     * @param compilationUnits the compilation units that TACO run parsed
     *        ({@link TacoMain#getCompilationUnits()}), which the snapshot needs
     */
    public void queueFalseCandidate(DarwinistInput input, TacoAnalysisResult analysis_result, Properties props,
            List<JCompilationUnitType> compilationUnits) {
        StrykerStage.falseCandidates++;
        String classToCheck = TacoConfigurator.getInstance().getString(TacoConfigurator.CLASS_TO_CHECK_FIELD);
        String methodToCheck = props.getProperty(TacoConfigurator.METHOD_TO_CHECK_FIELD);

        String junitFile;
        try {
            junitFile = generateJunitFile(compilationUnits, analysis_result, classToCheck, methodToCheck);
        } catch (RuntimeException e) {
            // E.g. TACO's snapshot cannot instantiate an object of the counterexample. An escaping
            // exception used to end this controller's thread and hang the run; go on without
            // feedback for this candidate instead (see below).
            log.warn("Could not turn TACO's counterexample into a test: " + e, e);
            junitFile = null;
        }

        try {

            log.debug("In effect, junit test generation was successful");

            // Feedback runs TACO with the input fixed to this counterexample, which is looked up
            // in the pool. If it could not be stored (pool full, or its test class did not load),
            // go on without pruning for this candidate instead of failing the feedback request.
            boolean stored = isStoredCounterexample(junitFile);
            if (MuJavaController.feedbackOn && !stored) {
                log.warn("Counterexample " + junitFile + " is not in the pool: no feedback for this candidate");
            }
            if (MuJavaController.feedbackOn && stored) {

                final Properties props2 = new Properties();
                Properties oldProps2 = input.getOverridingProperties();
                for(Entry<Object,Object> o : oldProps2.entrySet()){
                    if(o.getKey().equals("attemptToCorrectBug")) {
                        props2.put(o.getKey(), "false");
                    } else if (o.getKey().equals("generateUnitTestCase")) {
                        props2.put(o.getKey(), "false");
                    } else if (o.getKey().equals("generateCheck")) {
                        props2.put(o.getKey(), "true");
                    } else if (o.getKey().equals("generateRun")) {
                        props2.put(o.getKey(), "false");
                    } else {
                        props2.put(o.getKey(), o.getValue());
                    }
                }
                //TODO Ver si el primer argumento no tiene que ser filename
                DarwinistInput darwinistInput = new DarwinistInput(
                        input.getFilename(), 
                        input.getOriginalFilename(), 
                        input.getConfigurationFile(), 
                        input.getMethod(), 
                        props2, 
                        input.getFullyQualifiedClassName(), 
                        null,
                        true, 
                        input.getMethod(),
                        junitFile,
                        null,
                        null,
                        null,
                        input.getFeedback(),
                        input.getMutantsToApply(),
                        input.getSyncObject()
                        );
                darwinistInput.setRacMethod(input.getRacMethod());
                MuJavaInput inputForFeedback = new MuJavaInput(null, null, null, null, null, null, null, null, null, null);
                inputForFeedback.setComputateFeedback(true);
                inputForFeedback.setInputForFeedback(darwinistInput);
                StrykerStage.weedQueue.enqueue1(inputForFeedback);
                StrykerStage.mutationsQueuedToDarwinistForSeq++;                                                

            } else {
                MuJavaInput mujavainput = new MuJavaInput(input.getFilename(), 
                        input.getMethod(), input.getMutantsToApply(), 
                        new AtomicInteger(0), input.getConfigurationFile(), 
                        input.getOverridingProperties(), input.getOriginalFilename(), 
                        input.getSyncObject(), input.getFullyQualifiedClassName(), input.getMethod());
                MuJavaFeedback feedback = input.getFeedback();
                feedback.setFatherable(true);
                feedback.setGetSibling(true);
                feedback.setMutateRight(true);
                mujavainput.setMuJavaFeedback(feedback);
                StrykerStage.weedQueue.enqueue1(mujavainput);
            }
        } catch (IllegalArgumentException e) {
            //                                              e.printStackTrace();
        } catch (Exception e) {
            //                                              e.printStackTrace();
        }

    }

    /**
     * Whether {@code junitFile} is a counterexample stored in the pool
     * ({@code StrykerStage.junitFilesHash}), so that feedback can be computed with it.
     */
    private static boolean isStoredCounterexample(String junitFile) {
        return junitFile != null && StrykerStage.junitFilesHash.containsValue(junitFile);
    }

    /**
     * Turns a SAT TACO result into a stored counterexample. Builds a JUnit test from the Alloy
     * instance ({@link SnapshotStage}, {@link JUnitStage}, using {@code compilation_units}, the
     * units parsed by the TACO run that produced the result), rewrites it into package
     * {@code ar.edu.output.junit} with {@link TacoMain#editTestFileToCompile}, compiles it (a
     * failure is logged) and loads the class with a new {@link URLClassLoader}. Duplicates are detected by the MD5 of the generated
     * source ({@code StrykerStage.junitFilesHash}): a new input is appended to
     * {@code StrykerStage.junitInputs} and {@code junitFiles} if a slot is free (the arrays hold
     * {@code StrykerStage.MAX_COUNTEREXAMPLES} inputs and are never grown; once they are full a
     * warning is logged and new inputs are not stored); for a duplicate,
     * the new file is deleted and the stored copy's filename is returned. Class-loading errors are
     * printed and the unstored filename is returned.
     *
     * @return path of the generated JUnit source (or of its stored duplicate)
     * @throws IllegalArgumentException if the generated file cannot be read or hashed
     */
    @SuppressWarnings("resource")
    private String generateJunitFile(List<JCompilationUnitType> compilation_units, TacoAnalysisResult analysis_result,
            String classToCheck, String methodToCheck) {
        String junitFile = null;

        SnapshotStage snapshotStage = new SnapshotStage(
                compilation_units, analysis_result, classToCheck, methodToCheck);
        snapshotStage.execute();


        RecoveredInformation recoveredInformation = snapshotStage.getRecoveredInformation();
        recoveredInformation.setFileNameSuffix(StrykerStage.fileSuffix);


        JUnitStage jUnitStage = new JUnitStage(recoveredInformation);
        jUnitStage.execute();

        junitFile = jUnitStage.getJunitFileName();

        String currentJunit = null;

        String tempFilename = junitFile.substring(0, junitFile.lastIndexOf(FILE_SEP)+1) /*+ FILE_SEP*/; 
        String packageToWrite = "ar.edu.output.junit";
        String fileClasspath = tempFilename.substring(0, tempFilename.lastIndexOf(
                new String("ar.edu.generated.junit").replaceAll("\\.", FILE_SEP)));
        fileClasspath = TacoMain.replaceBelowWorkingDir(fileClasspath, "generated", "output", false);
        currentJunit = TacoMain.editTestFileToCompile(junitFile, classToCheck, packageToWrite, methodToCheck);

        File[] file1 = new File[]{new File(currentJunit)};
        JavaCompiler javaCompiler = ToolProvider.getSystemJavaCompiler();
        StandardJavaFileManager fileManager = javaCompiler.getStandardFileManager(null, null, null);
        Iterable<? extends JavaFileObject> compilationUnit1 =
                fileManager.getJavaFileObjectsFromFiles(Arrays.asList(file1));
        Boolean compiled = javaCompiler.getTask(null, fileManager, null, null, null, compilationUnit1).call();
        if (!Boolean.TRUE.equals(compiled)) {
            log.error("The counterexample test " + currentJunit + " does not compile, so the counterexample is not added to the pool. See the compiler errors above.");
        }
        try {
            fileManager.close();
        } catch (IOException e1) {
            // TODO: Define what to do!
            e1.printStackTrace();
        }
        javaCompiler = null;
        file1 = null;
        fileManager = null;

        //                                      if(compilationResult == 0) {
        log.debug("junit counterexample compilation succeded");
        ClassLoader cl = ClassLoader.getSystemClassLoader();
        ClassLoader cl2;
        try {
            cl2 = new URLClassLoader(new URL[]{new File(fileClasspath).toURI().toURL()}, cl);
            //                                      ClassLoaderTools.addFile(fileClasspath);
            String classToLoad = packageToWrite+"."+TacoMain.obtainClassNameFromFileName(junitFile);
            Class<?> clazz = cl2.loadClass(classToLoad);
            cl = null;
            cl2 = null;
            //                                          log.warn("The class just stored is: "+clazz.getName());
            log.info("preparing to store a test class... "+packageToWrite+"." + 
                    MuJavaController.obtainClassNameFromFileName(junitFile));
            //                                          Result result = null;
            //                                          final Object oToRun = clazz.newInstance();
            DigestOutputStream dos;
            File duplicatesTempFile = null;
            String content = null;
            try {
                content = FileUtils.readFile(junitFile);
            }
            catch (Exception e) {
                throw new IllegalArgumentException("invalid or null file");
            }
            try {
                duplicatesTempFile = File.createTempFile("forDuplicatesJunit", null);
                dos = new DigestOutputStream(new FileOutputStream(duplicatesTempFile, false), MessageDigest.getInstance("MD5"));
                dos.write(content.getBytes());
                dos.flush();
                dos.close();
                duplicatesTempFile.delete();
            }
            catch (Exception e) {
                throw new IllegalArgumentException("exception thrown while trying to compute digest in class VariablizedSATVerdicts");
            }
            byte[] digest = dos.getMessageDigest().digest();
            MsgDigest msgDigest = new MsgDigest(digest);
            if (!StrykerStage.junitFilesHash.containsKey(msgDigest)) {
                if (StrykerStage.indexToLastJUnitInput < StrykerStage.junitInputs.length) {
                    StrykerStage.junitFilesHash.put(msgDigest, junitFile);
                    StrykerStage.junitInputs[StrykerStage.indexToLastJUnitInput] = clazz;
                    StrykerStage.junitFiles[StrykerStage.indexToLastJUnitInput] = junitFile;
                    StrykerStage.indexToLastJUnitInput++;
                } else {
                    log.warn("The counterexample pool is full (" + StrykerStage.junitInputs.length
                            + " inputs): new counterexamples are no longer stored");
                }
            } else {
                new File(junitFile).delete();
                junitFile = StrykerStage.junitFilesHash.get(msgDigest);
            }
        } catch (MalformedURLException e1) {
            // TODO: Define what to do!
            e1.printStackTrace();
        } catch (ClassNotFoundException e1) {
            // TODO: Define what to do!
            e1.printStackTrace();
        }

        return junitFile;
    }

    /**
     * Final check for a candidate that passed TACO verification (UNSAT). Runs every stored
     * counterexample ({@code StrykerStage.junitInputs} up to the first empty slot) against the
     * candidate with JML runtime assertion checking: the {@code @Test} method of each JUnit class
     * is invoked with {@code input.getParametersFromOpenJML()} (classpath, qualified class name
     * and method name of the JML4C-compiled candidate prepared by {@link OpenJMLController}), on
     * {@link #executor} with a 5-second timeout. Any exception from the test counts as a failure
     * and stops the loop. A timeout counts as a pass: the test is cancelled with
     * {@link RacTimeout#endTimedOutRun} and the executor replaced. If the test class cannot be
     * instantiated the error is printed and that input also counts as passed. If every input
     * passes, the candidate is a fix ({@link #fixFound}); otherwise, unless its feedback is
     * {@code stop}ped, {@link #processFalseCandidate}.
     *
     * <p>Note: the failing input is recorded with {@code input.setSeqMethodInput(...)} only when
     * {@code Future.get} itself throws. An ordinary RAC failure is reported through the task's
     * return value, so the input is not recorded and the feedback requested afterwards normally
     * falls back to slot 0 (see {@link #getTacoMainWithFixedInput(String)}).
     *
     * @param input the candidate
     */
    private void lastRACAnalysis(DarwinistInput input) {
        //candidate solution found. We will now check all stored inputs and if the candidate passes all, then it becomes a FIX. 
        //Otherwise, we discard this fix candidate.

        log.warn("U  U  N   N  SSSS  AAAA  TTTTT");
        log.warn("U  U  NN  N  S     A  A    T");
        log.warn("U  U  N N N  SSSS  AAAA    T");
        log.warn("U  U  N  NN     S  A  A    T");
        log.warn("UUUU  N   N  SSSS  A  A    T");

        Class<?>[] junitInputs = StrykerStage.junitInputs;
        //                                        String junitFiles[] = StrykerStage.junitFiles;

        final Object[] inputToInvoke = input.getParametersFromOpenJML();
        boolean failed = false;
        for (int i = 0; i < junitInputs.length && junitInputs[i] != null && !failed; i++){

            Class<?> clazz = junitInputs[i];
            Method[] methods = clazz.getMethods();

            Method methodToRun = null;
            for(Method m : methods) {
                if(m.isAnnotationPresent(Test.class)) {
                    methodToRun = m;
                }
            }
            final Method methodToRunInCallable = methodToRun; 
            log.debug("methodToRunInCallable: "+methodToRunInCallable);
            methodToRunInCallable.setAccessible(true);
            Long nanoPrev = System.currentTimeMillis();
            try {
                final Object oToRun = clazz.newInstance();
                final String methodName = input.getMethod();
                log.debug("methodName: " + methodName);
                Callable<Boolean> task = new Callable<Boolean>() {
                    public Boolean call() {
                        boolean failed = true;
                        try {
                            runningThread = Thread.currentThread();
                            //                                                      log.debug("fileClasspath :" + fileClasspath);
                            //                                                      log.debug("qualifiedName :" + qualifiedName);
                            //                                                      log.debug("methodName :" + methodName);
                            //                                                      Object[] inputToInvoke = new Object[]{fileClasspath, qualifiedName, methodName};
                            long timeprev = System.currentTimeMillis();
                            methodToRunInCallable.invoke(oToRun, inputToInvoke);
                            long timepost = System.currentTimeMillis();
                            failed = false;
                            log.debug("time taken: "+(timepost - timeprev));
                        } catch (IllegalAccessException e) {
                            log.debug("Entered IllegalAccessException");
                            failed = true;
                            //                                                      e.printStackTrace();
                        } catch (IllegalArgumentException e) {
                            log.debug("Entered IllegalArgumentException");
                            failed = true;
                            //                                                      e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            log.debug("Entered InvocationTargetException");
                            //                                                    e.printStackTrace();  
                            log.warn("FIX CANDIDATE QUIT BECAUSE OF JML RAC");
                            failed = true;
                        } catch (Throwable e) {
                            log.debug("Entered throwable");
                            //                                                      e.printStackTrace();
                            failed = true;
                        }
                        return failed;
                    }
                };

                Future<Boolean> future = executor.submit(task);
                failed = (Boolean)future.get(5000, TimeUnit.MILLISECONDS);
            } catch (InstantiationException | IllegalAccessException e1) {
                // TODO: Define what to do!
                e1.printStackTrace();
            } catch (TimeoutException ex) {
                failed = false;
                //                input.setSeqMethodInput(StrykerStage.junitFiles[i]);
                //                log.warn("timeouted file: "+input.getFilename());
                executor = RacTimeout.endTimedOutRun(executor, runningThread,
                        input.getFilename() + ", method " + input.getMethod());
                // handle the timeout
            } catch (InterruptedException e) {
                log.debug("Interrupted");
                failed = true;
                input.setSeqMethodInput(StrykerStage.junitFiles[i]);
                // handle the interrupts
            } catch (ExecutionException e) {
                // handle other exceptions
                log.debug("Excecution Exception");
                input.setSeqMethodInput(StrykerStage.junitFiles[i]);
                failed = true;
            } catch (Throwable e) {
                log.debug("Exception");
                input.setSeqMethodInput(StrykerStage.junitFiles[i]);
                failed = true;
                // handle other exceptions
            }

            StrykerStage.racMillis += System.currentTimeMillis() - nanoPrev;

        }

        if (!failed){
            fixFound(input);
        } else {
            if (!input.getFeedback().isStop()) {
                processFalseCandidate(input);
            } else {
                StrykerStage.falseCandidates++;
            }
        }
    }

    /**
     * Builds the report appended to {@code StrykerLastReports.txt}: the fix's filename, father
     * index and line-mutation indexes, plus a snapshot of the global counters in
     * {@link StrykerStage}. The counters are updated from several threads without
     * synchronization, so the figures are approximate; "TACO Calls" also counts variabilization
     * steps answered by {@link VariablizedSATVerdicts}.
     *
     * @param input the accepted candidate
     * @return the report text
     */
    public String generateReport(DarwinistInput input) { 
        String report = "";

        report += "-----------------------STRYKER REPORT-----------------------\n";
        if (MuJavaController.feedbackOn) {
            report += "------------------------FEEDBACK: ON------------------------\n";
        } else {
            report += "------------------------FEEDBACK: OFF-----------------------\n";
        }
        String indexes = "[ ";
        for (Integer lineMutationIndex : input.getFeedback().getLineMutationIndexes()) {
            indexes += lineMutationIndex + " ";
        }
        indexes += "]";
        report += "\nFix Filename: " + input.getFilename() + "\n";
        report += "Solution Father Index: " + input.getFeedback().getFatherIndex() + " and LineMutationIndexes: " + indexes + "\n\n";
        report += "STRYKER STATS WHEN THIS FIX WAS FOUND:\n";
        report += "BEAR IN MIND THAT THIS RUNS MULTITHREADED, THEREFORE THE SUM OF TIME OF THE PARTS IS NOT EQUAL TO THE TOTAL TIME SPENT\n";
        report += "Total Time (millis): " + (System.currentTimeMillis() - StrykerStage.initialMillis) + "\n";
        report += "Compilation Time (millis): " + StrykerStage.compilationMillis + "\n";
        report += "TACO Calls: " + StrykerStage.totalSolvings + "\n";
        report += "TACO Time (millis): " + StrykerStage.tacoMillis + "\n";
        report += "TACO Avg Time (millis): " + (StrykerStage.tacoMillis / StrykerStage.totalSolvings) + "\n";
        report += "TACO Max Time (millis): " + StrykerStage.maxSolvingTime + "\n";
        report += "TACO Min Time (millis): " + StrykerStage.minSolvingTime + "\n";
        report += "RAC Time (millis): " + StrykerStage.racMillis + "\n";
        report += "MuJava Time (millis): " + StrykerStage.muJavaMillis + "\n";
        report += "Amount of generated Mutants: " + StrykerStage.mutationsGenerated + "\n";
        report += "Amount of duplicate Mutants: " + StrykerStage.duplicateMutations + "\n";
        report += "Amount of non-compilable Mutants: " + StrykerStage.nonCompilableMutations + "\n";
        report += "Amount of non-compilable Line Mutation Indexes: " + StrykerStage.nonCompilableMutationIndexesFound + "\n";
        report += "Amount of Mutants pruned: " + StrykerStage.prunedMutations + "\n";
        report += "Amount of Fathers pruned: " + StrykerStage.prunedFathers + "\n";
        report += "Amount of Relevant Computated Feedbacks: " + StrykerStage.relevantFeedbacksFound + "\n";
        report += "Amount of Mutants fatherized: " + MuJavaController.getInstance().getFathers().size() + "\n";
        report += "Amount of Mutants enqueued to OJMLController: " + StrykerStage.mutationsQueuedToOJMLC + "\n";
        report += "Amount of Mutants that failed in postcondition in RAC: " + StrykerStage.postconditionFailedMutations + "\n";
        report += "Amount of Mutants that throwed NPExcp in RAC: " + StrykerStage.nullPointerExceptionMutations + "\n";
        report += "Amount of Mutants that timeouted in RAC: " + StrykerStage.timeoutMutations + "\n";
        report += "Amount of Mutants enqueued to DController for Feedback: " + StrykerStage.mutationsQueuedToDarwinistForSeq + "\n";
        report += "Amount of Mutants enqueued to DController as Candidates: " + StrykerStage.candidatesQueuedToDarwinist + "\n";
        report += "Amount of Mutants Candidates that give SAT in DController: " + StrykerStage.falseCandidates + "\n";
        report += "------------------------END OF REPORT-------------------------\n\n";

        return report;
    }

    /**
     * Records an accepted fix: adds the candidate's filename to {@link #getResolvedBugs()},
     * appends a {@link #generateReport report} to {@code StrykerLastReports.txt} (emptied when
     * {@link StrykerStage} is constructed) and overwrites {@code StrykerLastFixFound.java} with the
     * candidate's source, both in {@code user.dir}; I/O errors are ignored.
     *
     * <p>If {@link MuJavaController#finishOnFirstFix} is set (the default), it shuts all
     * controllers down (this one with {@code shutdown()}, which lets {@link SimpleStrykerAPI#fixBug}
     * return) and calls {@link WeedQueue#clearQueues()}. That method loops while a queue's size is
     * non-zero, and a queue's {@code take()} keeps returning its un-acknowledged current element;
     * since the input being validated here has not been acknowledged yet, the call appears never
     * to return. As a result the restore step of {@link #validateCandidate} is not reached and the
     * fix stays in the subject file under {@code tests/}. Otherwise it asks MuJavaController for
     * the next sibling (no skipping) and the search goes on.
     *
     * @param input the accepted candidate
     */
    private void fixFound(DarwinistInput input) {
        resolvedBugs.add(input.getFilename());
        log.error("Solution: "+input.getFilename());

        String report = generateReport(input);

        try {
            FileUtils.appendToFile(System.getProperty("user.dir")+OpenJMLController.FILE_SEP +
                    "StrykerLastReports.txt", report);
            FileUtils.writeToFile(System.getProperty("user.dir")+OpenJMLController.FILE_SEP +
                    "StrykerLastFixFound.java", FileUtils.readFile(input.getFilename()));
        } catch (IOException e) {
            // TODO: Define what to do!
        }

        log.warn(report);

        if (MuJavaController.finishOnFirstFix) { 
            UnskippableMuJavaController.getInstance().shutdownNow();
            MuJavaController.getInstance().shutdownNow();
            OpenJMLController.getInstance().shutdownNow();
            shutdown();
            StrykerStage.weedQueue.clearQueues();
//            queue.clear();
            return;
        }

        MuJavaInput mujavainput = new MuJavaInput(input.getFilename(), input.getMethod(), 
                input.getMutantsToApply(), new AtomicInteger(0), input.getConfigurationFile(), 
                input.getOverridingProperties(), input.getOriginalFilename(), input.getSyncObject(),
                input.getFullyQualifiedClassName(), input.getMethod());
        mujavainput.setOldFilename(input.getOldFilename());
        MuJavaFeedback feedback = input.getFeedback();
        feedback.setFatherable(true);
        feedback.setGetSibling(true);
        feedback.setSkipUntilMutID(null);
        feedback.setMutateRight(true);
        mujavainput.setMuJavaFeedback(feedback);
        if (!feedback.isStop()) {
            StrykerStage.weedQueue.enqueue1(mujavainput);
        }
    }

    /**
     * Handles a candidate that passed TACO but failed RAC on a stored counterexample in
     * {@link #lastRACAnalysis}. Counts it as a false candidate; with
     * {@link MuJavaController#feedbackOn} it requests pruning feedback for it (same mechanism as
     * {@link #queueFalseCandidate}, but with the candidate's overriding properties unchanged and
     * the failing input taken from {@code input.getSeqMethodInput()}, which is normally
     * {@code null}, i.e. slot 0); otherwise it asks MuJavaController for the next sibling.
     *
     * @param input the rejected candidate
     */
    private void processFalseCandidate(DarwinistInput input) {
        StrykerStage.falseCandidates++;
        log.error("Failed Solution: "+input.getFilename());

        if (MuJavaController.feedbackOn) {
            DarwinistInput darwinistInput = new DarwinistInput(
                    input.getFilename(), 
                    input.getOriginalFilename(), 
                    input.getConfigurationFile(), 
                    input.getMethod(), 
                    input.getOverridingProperties(), 
                    input.getFullyQualifiedClassName(), 
                    null,
                    true, 
                    input.getMethod(),
                    input.getSeqMethodInput(),
                    null,
                    null,
                    null,
                    input.getFeedback(),
                    input.getMutantsToApply(),
                    input.getSyncObject()
                    );
            darwinistInput.setRacMethod(input.getRacMethod());
            MuJavaInput inputForFeedback = new MuJavaInput(null, null, null, null, null, null, null, null, null, null);
            inputForFeedback.setComputateFeedback(true);
            inputForFeedback.setInputForFeedback(darwinistInput);
            StrykerStage.weedQueue.enqueue1(inputForFeedback);
            StrykerStage.mutationsQueuedToDarwinistForSeq++;                                                
            log.debug("Adding task for Feedback Processing");
        } else {
            MuJavaInput mujavainput = new MuJavaInput(input.getFilename(), 
                    input.getMethod(), input.getMutantsToApply(), new AtomicInteger(0), 
                    input.getConfigurationFile(), input.getOverridingProperties(), 
                    input.getOriginalFilename(), input.getSyncObject(), input.getFullyQualifiedClassName(),
                    input.getMethod());
            MuJavaFeedback feedback = input.getFeedback();
            feedback.setFatherable(true);
            feedback.setGetSibling(true);
            feedback.setMutateRight(true);
            mujavainput.setMuJavaFeedback(feedback);
            StrykerStage.weedQueue.enqueue1(mujavainput);
        }
    }

    @Override
    protected int getQtyOfThreads() {
        return 1;
    }

    /**
     * @return the live list of filenames of the candidates accepted as fixes so far; this is what
     *         {@link SimpleStrykerAPI#fixBug} returns
     */
    public List<String> getResolvedBugs() {
        return resolvedBugs;
    }


    /**
     * Copies {@code inputFile} to {@code outputFile}, replacing every line that contains
     * {@code "package"} with a declaration of {@code classPackage} followed by imports of
     * {@code java.net.URL}, {@code java.net.URLClassLoader}, {@code java.net.MalformedURLException},
     * {@code java.io.File} and {@code java.lang.reflect.InvocationTargetException}.
     * {@code sourceClassName} and {@code methodName} are ignored. Not called anywhere in
     * {@code src/} or {@code unittest/}; {@link #generateJunitFile} uses
     * {@link TacoMain#editTestFileToCompile} instead.
     *
     * @return {@code outputFile}
     */
    public static String editTestFileToCompile(
            String inputFile, String outputFile, String sourceClassName, String classPackage, String methodName) {
        //      String tmpDir = inputFile.substring(0, inputFile.lastIndexOf(FILE_SEP));
        //      tmpDir = tmpDir.replaceAll("generated", "output");
        File source = new File(inputFile);
        File dest = new File(outputFile);

        String packageSentence = "package "+classPackage+";\n";
        //  int posLastUnderscore = methodName.lastIndexOf("_");
        //  methodName = methodName.substring(0, posLastUnderscore);
        try {
            dest.createNewFile();
            FileOutputStream fos = new FileOutputStream(dest);
            Scanner scan = new Scanner(source);
            scan.useDelimiter("\n");
            String str = null;
            while(scan.hasNext()){
                str = scan.next();
                if (str.contains("package")){
                    fos.write(packageSentence.getBytes(Charset.forName("UTF-8")));
                    str = "           import java.net.URL;";
                    fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
                    str = "           import java.net.URLClassLoader;";
                    fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
                    str = "           import java.net.MalformedURLException;";
                    fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
                    str = "           import java.io.File;";
                    fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
                    str = "           import java.lang.reflect.InvocationTargetException;";
                } 

                fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
            }

            fos.close();
            scan.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return outputFile;

    }


}