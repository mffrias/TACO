package ar.edu.taco.stryker.api.impl;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;
import org.junit.Test;

import ar.edu.taco.engine.StrykerStage;
import ar.edu.taco.stryker.api.impl.input.DarwinistInput;
import ar.edu.taco.stryker.api.impl.input.MuJavaFeedback;
import ar.edu.taco.stryker.api.impl.input.MuJavaInput;
import ar.edu.taco.stryker.api.impl.input.OpenJMLInput;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * RAC filter of the Stryker repair pipeline: runs the stored counterexamples against each
 * mutant with runtime assertion checking (RAC) and forwards only the survivors to TACO.
 *
 * <p>Despite its name, this class does not use OpenJML. {@link MuJavaController} (and
 * {@link UnskippableMuJavaController}, through it) writes each batch of sibling mutants as a
 * single class with one method per mutant, and compiles that class with JML4C
 * ({@code lib/stryker/jml4c.jar}), which turns the JML contracts into runtime checks. Each
 * {@link OpenJMLInput} names one mutant method of such a batch
 * ({@link OpenJMLInput#getRacMethod()}); this controller invokes the JUnit counterexamples in
 * {@link StrykerStage#junitInputs} on it and classifies the mutant as passing or failing.
 *
 * <p><b>Inputs and outputs.</b> Consumes queue 2 of {@link StrykerStage#weedQueue} and
 * acknowledges every input it finishes. A mutant that passes all attempted counterexamples
 * becomes a {@link DarwinistInput} on queue 3 ({@link DarwinistController}, TACO validation).
 * A mutant that fails produces a {@link MuJavaInput} on queue 1 ({@link MuJavaController}):
 * a feedback request when {@link MuJavaController#feedbackOn} is set, otherwise a request for
 * the next sibling. Inputs whose {@link MuJavaFeedback#isStop()} is set come from
 * {@link UnskippableMuJavaController} and are validate-only: they can still reach queue 3,
 * but a failure produces no follow-up work. See {@link #getRunnable()} for the details.
 *
 * <p><b>Threading and lifecycle.</b> Singleton ({@link #getInstance()}), started by
 * {@link SimpleStrykerAPI#start()} with a single worker thread. Each counterexample runs on
 * a separate single-thread {@link #executor} so that it can be timed out; a run that times
 * out is killed with the deprecated {@link Thread#stop()} and the executor is replaced. The
 * round-robin cursor {@link #curJunitIndex} and {@link #consumedMutants} are static and never
 * reset, so they carry over if Stryker runs more than once in the same JVM.
 *
 * <p>The JML4C runtime is located as {@code lib/stryker/jml4c.jar} under {@code user.dir},
 * so the working directory must be the repository root.
 */
public class OpenJMLController extends AbstractBaseController<OpenJMLInput> {

    public static final String FILE_SEP = System.getProperty("file.separator");

    public static final String PATH_SEP = System.getProperty("path.separator");

    /**
     * Package suffix of the JML4C batch classes. Not read in this class; the value actually
     * used to build batches is {@link MuJavaController#MUTANTS_DEST_PACKAGE}.
     */
    public static final String MUTANTS_DEST_PACKAGE = "ar.edu.itba.stryker.mutants";

    private static Logger log = Logger.getLogger(OpenJMLController.class);

    /**
     * Round-robin cursor into {@link StrykerStage#junitInputs}: the slot of the next
     * counterexample to try. Shared by all mutants; advanced only after a passing run and
     * wrapped to 0 at the end of the array or at the first empty slot.
     */
    private static int curJunitIndex = 0;

    //    private static final String CLASSPATH = System.getProperty("java.class.path");

    private static OpenJMLController instance;

    /** Number of inputs processed so far (logged only). */
    private static int consumedMutants = 0;

    /**
     * Returns the singleton, creating it on first use. It does nothing until
     * {@link #start()} is called (done by {@link SimpleStrykerAPI#start()}).
     *
     * @return the shared controller
     */
    public synchronized static OpenJMLController getInstance() {
        if (instance == null) {
            instance = new OpenJMLController();
        }
        return instance;
    }

    //	private JUnitCore junit;
    /**
     * Runs one counterexample at a time so that the worker can wait with a timeout. Replaced
     * by a fresh executor after every timeout.
     */
    private ExecutorService executor = Executors.newSingleThreadExecutor(); //Executors.newFixedThreadPool(10);

    /** Thread running the current counterexample; stopped with {@link Thread#stop()} on timeout. */
    private Thread runningThread = null;

    private OpenJMLController() {
        //		try {
        //			ClassLoaderTools.addFile(System.getProperty("user.dir")+FILE_SEP+"generated"+FILE_SEP);
        //		} catch (IOException e) {
        //			e.printStackTrace();
        //		}
    }

    /**
     * Returns the worker loop, which runs until {@link #willShutdown} is set. For each
     * {@link OpenJMLInput} taken from queue 2:
     * <ol>
     * <li><b>Locate the compiled batch.</b> If the feedback {@code isStop()}, the JML4C
     *     source file and package are the ones recorded in the feedback
     *     ({@code getUnskippableOJML4CFilename()}/{@code getUnskippableOJML4CPackage()});
     *     otherwise they are taken from the father in {@link MuJavaController#getFathers()}.
     *     If the file name is {@code null} the controller calls {@link #shutdown()} and leaves
     *     the loop without acknowledging the input.</li>
     * <li><b>Run counterexamples.</b> Up to {@code Math.min(StrykerStage.indexToLastJUnitInput, 9)}
     *     classes from {@link StrykerStage#junitInputs} are tried in round-robin order starting
     *     at {@link #curJunitIndex}, stopping at the first failure. For each one, its first
     *     {@code @Test} method found by reflection is invoked with
     *     {@code (classpath, batchClassName, input.getRacMethod())}, where the classpath is the
     *     batch's source root plus {@code lib/stryker/jml4c.jar}. The test (rewritten by
     *     {@link ar.edu.taco.TacoMain#editTestFileToCompile}) loads the batch class in its own
     *     class loader and calls that mutant method on the counterexample.</li>
     * <li><b>Classify each run</b> (3-second limit):
     *   <ul>
     *   <li>returns normally: pass;</li>
     *   <li>times out: counted as a <em>pass</em> and recorded in
     *       {@link StrykerStage#timeoutMutations};</li>
     *   <li>{@link InvocationTargetException} whose printed stack trace contains both
     *       {@code org.jmlspecs.jml4.rac.runtime.JML} and {@code Error}: RAC violation, fail;</li>
     *   <li>any other {@code InvocationTargetException} (null pointer, {@code ThreadDeath} or
     *       anything else): exception in the mutated method, fail, recorded in
     *       {@link StrykerStage#nullPointerExceptionMutations};</li>
     *   <li>{@link IllegalAccessException}, {@link IllegalArgumentException} or any other
     *       {@link Throwable} inside the task: fail.</li>
     *   </ul>
     *   The cursor advances only after a pass, so the counterexample that killed a mutant is
     *   the first one tried on the next mutant. If waiting for the result is interrupted or
     *   fails otherwise, the previous attempt's verdict is reused (initially pass).</li>
     * <li><b>Route the mutant.</b>
     *   <ul>
     *   <li>Passed every attempted counterexample: a {@link DarwinistInput} carrying the
     *       invocation arguments is enqueued to queue 3, whether or not the feedback
     *       {@code isStop()}. {@link DarwinistController} validates it with TACO and, on UNSAT,
     *       re-runs all stored counterexamples with those same arguments.</li>
     *   <li>Failed and not {@code isStop()}, with {@link MuJavaController#feedbackOn}: a
     *       {@link MuJavaInput} with {@code computateFeedback} set is enqueued to queue 1. It
     *       wraps a {@link DarwinistInput} marked for sequential processing, with the failing
     *       counterexample file from {@link StrykerStage#junitFiles} and a copy of the
     *       overriding properties in which {@code attemptToCorrectBug},
     *       {@code generateUnitTestCase} and {@code generateRun} become {@code false},
     *       {@code generateCheck} becomes {@code true} (only keys already present are
     *       rewritten; {@code methodToCheck} stays the driver's, and
     *       {@code DarwinistController.variablize} names the variabilized method).</li>
     *   <li>Failed and not {@code isStop()}, without feedback: a {@link MuJavaInput} for the
     *       same mutant whose feedback has {@code fatherable}, {@code getSibling} and
     *       {@code mutateRight} set is enqueued to queue 1.</li>
     *   <li>Failed and {@code isStop()}: nothing is enqueued.</li>
     *   </ul></li>
     * <li><b>Acknowledge</b> with {@code weedQueue.ack(1)}. If no queued or in-flight work is
     *     left in any queue, {@code shutdownNow()} is called on all four controllers.</li>
     * </ol>
     * With no counterexample loaded ({@code indexToLastJUnitInput == 0}) nothing is attempted,
     * so the mutant is neither forwarded nor answered with feedback. Exceptions thrown while
     * running or routing a mutant are printed and the input is still acknowledged; a runtime
     * exception while locating the batch ends the worker (printed by the outer handler)
     * without an acknowledgement.
     *
     * <p>When the loop exits through the shutdown flag, an interrupt or a missing file name,
     * it enqueues a {@link DarwinistInput} whose fields are all {@code null}/{@code false} to
     * queue 3; {@link DarwinistController} has no special handling for such an input.
     *
     * @return the worker loop for the single worker thread
     */
    @Override
    protected Runnable getRunnable() {
        return new Runnable() {

            @SuppressWarnings("deprecation")
            @Override
            public void run() {
                try {
                    while (!willShutdown.get()) {
                        try {
                            //int j = 0;
                            log.debug("Taking new input from queue");
                            OpenJMLInput input = StrykerStage.weedQueue.takeFrom2();

                            log.debug("Took from queue");
//                            log.debug("Queue size: "+StrykerStage.weedQueue.size2());
                            MuJavaInput father;
                            String tempFilename = null;
                            String packageToWrite = null;
                            
                            // Validate-only inputs from UnskippableMuJavaController carry their batch location in the feedback.
                            if (input.getFeedback().isStop()) {
                                father = UnskippableMuJavaController.getInstance().getFathers().get(input.getFeedback().getFatherIndex());
                                tempFilename = input.getFeedback().getUnskippableOJML4CFilename();
                                packageToWrite = input.getFeedback().getUnskippableOJML4CPackage();
                            } else {
                                father = MuJavaController.getInstance().getFathers().get(input.getFeedback().getFatherIndex());
                                tempFilename = father.getJml4cFilename();
                                packageToWrite = father.getJml4cPackage();
                            }
                            if(tempFilename == null) {
                                log.debug("filename was null");
                                shutdown();
                                break;
                            }
                            log.debug("Took: "+input);

                            final String fileClasspath = tempFilename.substring(
                                    0, tempFilename.lastIndexOf(packageToWrite.replaceAll("\\.", FILE_SEP)));
                            Boolean result = true;
                            final String qualifiedName = editFileToPassToNextStage(tempFilename);

                            String newFileClasspath = fileClasspath + PATH_SEP + System.getProperty("user.dir")+FILE_SEP+"lib/stryker/jml4c.jar";

                            //                            log.debug("compiled file with exit code = "+exitValue);
                            try {
                                log.info("preparing to run a test... "+packageToWrite+"."+MuJavaController.obtainClassNameFromFileName(tempFilename));

                                Class<?>[] junitInputs = StrykerStage.junitInputs;

                                Set<String> candidateMethods = Sets.newHashSet();
                                Map<String, String> failedMethods = Maps.newHashMap();
                                Set<String> nullPointerMethods = Sets.newHashSet();
                                Set<String> timeoutMethods = Sets.newHashSet();
                                Boolean threadTimeout = false;
                                String methodName = input.getRacMethod();

                                int maxNumberAttemptedInputs = Math.min(StrykerStage.indexToLastJUnitInput, 9);
                                log.debug("maxNumberAttemptedInputs: "+maxNumberAttemptedInputs);
                                boolean failed = false;

                                for (int attempted = 0; attempted < maxNumberAttemptedInputs && !failed; attempted++){
                                    Class<?> junitInputClass = junitInputs[curJunitIndex];
                                    Method[] methods = junitInputClass.getMethods();
                                    Method methodToRun = null;
                                    for(Method m : methods) {
                                        if(m.isAnnotationPresent(Test.class)) {
                                            methodToRun = m;
                                            break;
                                        }
                                    }
                                    final Method methodToRunInCallable = methodToRun; 
                                    methodToRunInCallable.setAccessible(true);
                                    final Object oToRun =  junitInputClass.newInstance();
                                    final Object[] inputToInvoke = new Object[]{newFileClasspath, qualifiedName, methodName};
                                    Callable<Boolean> task = new Callable<Boolean>() {
                                        public Boolean call() throws InvocationTargetException {
                                            Boolean result = false;
                                            try {
                                                runningThread = Thread.currentThread();
                                                long timeprev = System.currentTimeMillis();
                                                methodToRunInCallable.invoke(oToRun, inputToInvoke);
                                                long timepost = System.currentTimeMillis();
                                                result = true;
                                                log.debug("time taken: "+(timepost - timeprev));
                                            } catch (IllegalAccessException e) {
                                                log.debug("Entered IllegalAccessException");
                                                //                                                e.printStackTrace();
                                            } catch (IllegalArgumentException e) {
                                                log.debug("Entered IllegalArgumentException");
                                                //                                                e.printStackTrace();
                                            } catch (InvocationTargetException e) {
                                                //                                                e.printStackTrace();
                                                log.debug("Entered InvocationTargetException");
                                                log.debug("QUIT BECAUSE OF JML RAC");
                                                String retValue = null;
                                                StringWriter sw = null;
                                                PrintWriter pw = null;
                                                try {
                                                    sw = new StringWriter();
                                                    pw = new PrintWriter(sw);
                                                    e.printStackTrace(pw);
                                                    retValue = sw.toString();
                                                    //                                                        System.out.println(retValue);
                                                    //                                                        System.out.println("------------------------------------------------------------------------------------------------");
                                                } finally {
                                                    try {
                                                        if(pw != null)  pw.close();
                                                        if(sw != null)  sw.close();
                                                    } catch (IOException ignore) {}
                                                }
                                                if (retValue.contains("org.jmlspecs.jml4.rac.runtime.JML") && retValue.contains("Error")) {
                                                    //                                                    System.out.println(retValue);
                                                    //                                                    System.out.println("Fallo RAC!!");
                                                    result = false;
                                                    //                                                } else if (retValue.contains("JMLExitExceptionalPostconditionError")) { 
                                                    //                                                    result = null;
                                                    //                                                } else if (retValue.contains("JMLInvariantError")) {
                                                    //                                                    result = false;
                                                } else if (retValue.contains("NullPointerException")) {
                                                    //                                                    System.out.println("NULL POINTER EXCEPTION EN RAC!!!!!!!!!!!!");
                                                    result = null;
                                                } else if (retValue.contains("ThreadDeath")) {
                                                    //                                                    System.out.println("THREAD DEATH EN RAC!!!!!!!!!!!!!!!!");
                                                    result = null;
                                                    result = null;
                                                } else {
                                                    log.error("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" +
                                                            "\nFAILED METHODDDD FOR NO REASON!!!!!!!!!!!!!!!!!!!!" +
                                                            "\n!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                                                    e.printStackTrace();
                                                    result = null;
                                                }
                                            } catch (Throwable e) {
                                                log.debug("Entered throwable");
                                                //                                                System.out.println("THROWABLEEE!!!!!!!!!!!!!!!!!!!!!!");
                                                e.printStackTrace();
                                                return false;
                                            }
                                            return result;
                                        }
                                    };
                                    threadTimeout = false;
                                    Long nanoPrev = System.currentTimeMillis();
                                    Future<Boolean> future = executor.submit(task);
                                    try {
                                        result = future.get(3000, TimeUnit.MILLISECONDS);
                                    } catch (TimeoutException ex) {
                                        // A timeout counts as a pass; kill the runaway test and start a fresh executor.
                                        result = true;
                                        threadTimeout = true;
                                        runningThread.stop();
                                        executor.shutdownNow();
                                        executor = Executors.newSingleThreadExecutor();
                                        // handle the timeout
                                    } catch (InterruptedException e) {
                                        log.debug("Interrupted");
                                        // handle the interrupts
                                    } catch (ExecutionException e) {
                                        // handle other exceptions
                                        log.debug("Excecution Exception");
                                    } catch (Throwable e) {
                                        log.debug("Exception");
                                        // handle other exceptions
                                    } finally {
                                        future.cancel(true); // may or may not desire this	
                                    }
                                    StrykerStage.racMillis += (System.currentTimeMillis() - nanoPrev);
                                    log.info("test ran");
                                    if (result == null) {
                                        log.warn("TEST FAILED BECAUSE OF AN EXCEPTION IN MUTATED METHOD: :( for file: " + 
                                                tempFilename + ", method: "+methodName + ", input: " + curJunitIndex);
                                        failed = true;
                                        nullPointerMethods.add(methodName);
                                        String junitfile = StrykerStage.junitFiles[curJunitIndex];
                                        failedMethods.put(methodName, junitfile);
                                    } else if (!result) {
                                        log.warn("TEST FAILED: :( for file: " + tempFilename + ", method: "+methodName + ", input: " + curJunitIndex);
                                        String junitfile = StrykerStage.junitFiles[curJunitIndex];
                                        failedMethods.put(methodName, junitfile);
                                        failed = true;
                                    } else {
                                        if (threadTimeout) {
                                            timeoutMethods.add(methodName);
                                            log.warn("Timeout Mutation " + input.getFilename() + " for method: " + timeoutMethods.iterator().next());
                                        }
                                        // Passed the last input to attempt: forward as a fix candidate.
                                        if (attempted + 1 == maxNumberAttemptedInputs) {
                                            log.warn("TEST PASSED: :) for file: " + tempFilename + ", method: "+methodName + ", input: " + curJunitIndex);
                                            DarwinistInput output = null;
                                            output = new DarwinistInput(input.getFilename(), 
                                                    input.getOriginalFilename(), input.getConfigurationFile(), 
                                                    input.getMethod(), input.getOverridingProperties(), qualifiedName, 
                                                    inputToInvoke, false, null, null, null, null, null, 
                                                    input.getFeedback(), input.getMutantsToApply(), input.getSyncObject());
                                            output.setRacMethod(input.getRacMethod());
                                            StrykerStage.weedQueue.enqueue3(output);
                                            StrykerStage.candidatesQueuedToDarwinist++;
                                            candidateMethods.add(methodName);
                                            log.debug("Enqueded task to Darwinist Controller");
                                        } else {
                                            log.debug("TEST CANDIDATE TO PASS :), for file: " + tempFilename 
                                                    + ", method: "+methodName + ", input: " + curJunitIndex);
                                            log.debug("The class to be used in OpenJMLController is: "
                                                    +junitInputClass.getName());
                                        }
                                        curJunitIndex++;
                                        if (curJunitIndex == junitInputs.length || junitInputs[curJunitIndex] == null){
                                            curJunitIndex = 0;
                                        }
                                    }
                                }	

                                consumedMutants++;

                                log.warn("Mutants consumed by RAC: "+ consumedMutants);

                                if (!nullPointerMethods.isEmpty()) {
                                    log.warn("Null Pointer Mutation: " + input.getFilename() + " for method: " + nullPointerMethods.iterator().next());
                                } else if (!candidateMethods.isEmpty()) {
                                    log.warn("Candidate Mutation: " + input.getFilename() + " for method: " + candidateMethods.iterator().next());
                                } else if (!failedMethods.isEmpty()){
                                    log.warn("Postcondition Failed Mutation: " + input.getFilename() + " for method: " + failedMethods.keySet().iterator().next());
                                } else {
                                    log.warn("Unidentified Mutation: " + input.getFilename() + " for method: unidentified");
                                }

                                if (!input.getFeedback().isStop()) {

                                    if (failed) {
                                        if (!input.getFeedback().isStop()) {
                                            if (MuJavaController.feedbackOn) {
                                                final Properties props = new Properties();
                                                Properties oldProps = input.getOverridingProperties();
                                                for(Entry<Object,Object> o : oldProps.entrySet()){
                                                    if(o.getKey().equals("attemptToCorrectBug")) {
                                                        props.put(o.getKey(), "false");
                                                    } else if (o.getKey().equals("generateUnitTestCase")) {
                                                        props.put(o.getKey(), "false");
                                                    } else if (o.getKey().equals("generateCheck")) {
                                                        props.put(o.getKey(), "true");
                                                    } else if (o.getKey().equals("generateRun")) {
                                                        props.put(o.getKey(), "false");
                                                    } else {
                                                        props.put(o.getKey(), o.getValue());
                                                    }
                                                }
                                                //TODO Ver si el primer argumento no tiene que ser filename
                                                DarwinistInput darwinistInput = new DarwinistInput(
                                                        input.getFilename(), 
                                                        input.getOriginalFilename(), 
                                                        input.getConfigurationFile(), 
                                                        input.getMethod(), 
                                                        props, 
                                                        null, 
                                                        null,
                                                        true, 
                                                        input.getMethod(),
                                                        failedMethods.get(methodName),
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
                                                MuJavaInput mujavainput = new MuJavaInput(
                                                        input.getFilename(), input.getMethod(), 
                                                        input.getMutantsToApply(), new AtomicInteger(0), 
                                                        input.getConfigurationFile(), input.getOverridingProperties(), 
                                                        input.getOriginalFilename(), input.getSyncObject(),
                                                        input.getFullyQualifiedClassName(), input.getMethodUnderAnalysis());
                                                MuJavaFeedback feedback = input.getFeedback();
                                                feedback.setFatherable(true);
                                                feedback.setGetSibling(true);
                                                feedback.setMutateRight(true);
                                                mujavainput.setMuJavaFeedback(feedback);
                                                StrykerStage.weedQueue.enqueue1(mujavainput);
                                            }
                                        }
                                        StrykerStage.postconditionFailedMutations++;
                                    }
                                }

                                if (!timeoutMethods.isEmpty()) {
                                    //                                    StrykerStage.mutationsQueuedToDarwinistForSeq -= timeoutMethods.size();
                                    //                                    StrykerStage.postconditionFailedMutations -= timeoutMethods.size();
                                    StrykerStage.timeoutMutations += timeoutMethods.size();

                                    //                                        for (String string : timeoutMethods) {
                                    //                                            StrykerStage.timeoutMutations++;
                                    //                                            input = map.get(string);
                                    //                                            MuJavaInput mujavainput = new MuJavaInput(wrapper.getOldFilename(), string, input.getJunitInputs(), input.getMutantsToApply(), new AtomicInteger(0), input.getConfigurationFile(), input.getOverridingProperties(), input.getOriginalFilename(), input.getSyncObject());
                                    //                                            MuJavaFeedback feedback = input.getFeedback();
                                    //                                            feedback.setMutateUntilLine(0);
                                    //                                            mujavainput.setMuJavaFeedback(feedback);
                                    //                                            mujavainput.getMuJavaFeedback().setFatherable(true);
                                    //                                            MuJavaController.getInstance().enqueueTask(mujavainput);
                                    //                                        }
                                }

                                if (!nullPointerMethods.isEmpty()) {
                                    //                                                StrykerStage.mutationsQueuedToDarwinistForSeq -= nullPointerMethods.size();
                                    StrykerStage.postconditionFailedMutations -= nullPointerMethods.size();
                                    StrykerStage.nullPointerExceptionMutations += nullPointerMethods.size();
                                    //                                        for (String string : nullPointerMethods) {
                                    //                                            StrykerStage.nullPointerExceptionMutations++;
                                    //                                            input = map.get(string);
                                    //                                            MuJavaInput mujavainput = new MuJavaInput(wrapper.getOldFilename(), string, input.getJunitInputs(), input.getMutantsToApply(), new AtomicInteger(0), input.getConfigurationFile(), input.getOverridingProperties(), input.getOriginalFilename(), input.getSyncObject());
                                    //                                            MuJavaFeedback feedback = input.getFeedback();
                                    //                                            feedback.setMutateUntilLine(0);
                                    //                                            mujavainput.setMuJavaFeedback(feedback);
                                    //                                            mujavainput.getMuJavaFeedback().setFatherable(true);
                                    //                                            MuJavaController.getInstance().enqueueTask(mujavainput);
                                    //                                        }
                                }
                            } catch (IllegalArgumentException e) {
                                //                                System.out.println(e.getMessage());
                                e.printStackTrace();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } catch (InterruptedException e) {
                            break;
                            //e.printStackTrace();
                        }
                        if (StrykerStage.weedQueue.ack(1) == 0) {
                            UnskippableMuJavaController.getInstance().shutdownNow();
                            MuJavaController.getInstance().shutdownNow();
                            DarwinistController.getInstance().shutdownNow();
                            OpenJMLController.getInstance().shutdownNow();
                        }
                    }
                    log.warn("Shutting down Darwinist Controller");
                    DarwinistInput output = new DarwinistInput(
                            null, null, null, null, null, null, null, false, null, null, null, null, null, null, null, null);
                    StrykerStage.weedQueue.enqueue3(output);
                    //DarwinistController.getInstance().shutdown();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            /**
             * Returns the fully qualified name of the JML4C batch class stored in
             * {@code filename}. Despite its name, it does not edit any file: the package is
             * the directory path from {@link MuJavaController#batchPackageStart(String)} up to
             * the file, with separators turned into dots, and the simple name is the file name
             * without extension.
             *
             * @param filename path of the JML4C-adapted batch source file
             * @return the batch class name, passed to the counterexample test so it can load
             *         the class
             */
            public String editFileToPassToNextStage(String filename) {
                String classPackage = filename.substring(MuJavaController.batchPackageStart(filename), filename.lastIndexOf(FILE_SEP)).replaceAll(FILE_SEP, ".");

                return classPackage+"."+MuJavaController.obtainClassNameFromFileName(filename);
            }

        };
    }

    /**
     * Appends a URL to the system class loader by calling the protected
     * {@code URLClassLoader.addURL} reflectively. This only works when the system class loader
     * is a {@link URLClassLoader}, as on Java 7 and 8. Failures are printed and logged at debug
     * level, never thrown.
     *
     * <p>Unused: there is no caller in {@code src/} or {@code unittest/}.
     *
     * @param u the URL to add
     */
    public static void addUrl(URL u) {
        URLClassLoader sysloader = (URLClassLoader) ClassLoader
                .getSystemClassLoader();
        Class<URLClassLoader> sysclass = URLClassLoader.class;

        try {
            Method method = sysclass.getDeclaredMethod("addURL", URL.class);
            method.setAccessible(true);
            method.invoke(sysloader, new Object[] { u });
            sysloader = null;
        } catch (Throwable t) {
            t.printStackTrace();
            try {
                throw new IOException(
                        "Error, could not add URL to system classloader");
            } catch (IOException e) {
                log.debug(e.getMessage());
            }
        }
    }

    @Override
    protected int getQtyOfThreads() {
        return 1;
    }

}
