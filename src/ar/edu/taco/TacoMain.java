/*
 * TACO: Translation of Annotated COde
 * Copyright (c) 2010 Universidad de Buenos Aires
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA,
 * 02110-1301, USA
 */

package ar.edu.taco;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.*;
import java.util.jar.Attributes;
import java.util.jar.Attributes.Name;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import ar.edu.taco.utils.*;
import ar.edu.taco.utils.jml.JmlAstDeterminizerVisitor;
import ar.edu.taco.utils.JCompilationUnitTypeWrapper;
import ar.edu.taco.utils.Message;
import ar.edu.taco.utils.TranslateThread;

import ar.edu.taco.utils.WindowList;
import ar.edu.taco.utils.output_manager.DeleteOutputFiles;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.multijava.mjc.JCompilationUnitType;

import ar.edu.jdynalloy.JDynAlloyConfig;
import ar.edu.jdynalloy.MethodToCheckNotFoundException;
import ar.edu.taco.engine.JmlStage;
import ar.edu.taco.jml.JmlToSimpleJmlContext;
import ar.edu.taco.jml.parser.JmlParser;
import ar.edu.taco.simplejml.SimpleJmlToJDynAlloyContext;

/**
 * <p>Runs the TACO analysis.</p>
 * <p>The configuration options must be stated through the configuration file
 * whose name expects the methods <code>ar.edu.taco.TacoMain.run</code>.
 * Those configurations can be overridden by the sencond argument of
 * <code>ar.edu.taco.TacoMain.run(String, Properties)</code>.</p>
 * <h3>Integers</h3>
 * <p>TACO can analyse code using Alloy integers or Java-like Integers.
 * In either case, the meaning of the bitwidth value is the same: a bound
 * in the count of numbers TACO will deal with. In particular, it states that
 * the range of integers used in the analysis include from -2^{bitwidth-1}
 * to 2^{bitwidth-1}-1.</p>
 * <p>Besides that, TACO can try to infer the value of the scopes to be used
 * for the analysis. If the bitwidth is setted to a non positive integer
 * <b>and</b> the scope inferring feature is activated, the bitwidth is also
 * inferred. Otherwise, the bitwidth value setted is used.</p>
 *
 * @author unknown (jgaleotti?)
 */
public class TacoMain {

    private static Logger log = Logger.getLogger(TacoMain.class);

    public static final String OUTPUT_SIMPLIFIED_JAVA_EXTENSION = ".java";
    private static final String CMD = "Taco";
    private static final String HEADER = "Taco static analysis tool.";
    private static final String FOOTER = "For questions and comments please write to jgaleotti AT dc DOT uba DOT ar";
    public static final String PATH_SEP = System.getProperty("path.separator");
    public static final String FILE_SEP = System.getProperty("file.separator");

    private static Object inputToFix;

    /**
     * @param args
     */
    @SuppressWarnings({"static-access"})
    public static void main(String[] args) {

        @SuppressWarnings("unused")
        int loopUnrolling = 3;

        String tacoVersion = getManifestAttribute(Name.IMPLEMENTATION_VERSION);
        String tacoCreatedBy = getManifestAttribute(new Name("Created-By"));

        System.out.println("TACO: Taco static analysis tool.");
        System.out.println("Created By: " + tacoCreatedBy);
        System.out.println("Version: " + tacoVersion);
        System.out.println("");
        System.out.println("");

        Option helpOption = new Option("h", "help", false, "print this message");
        Option versionOption = new Option("v", "version", false, "shows version");

        Option configFileOption = OptionBuilder.withArgName("path").withLongOpt("configFile").hasArg().withDescription("set the configuration file")
                .create("cf");
        Option classToCheckOption = OptionBuilder.withArgName("classname").withLongOpt("classToCheck").hasArg().withDescription("set the class to be checked")
                .create('c');
        Option methodToCheckOption = OptionBuilder.withArgName("methodname").withLongOpt("methodToCheck").hasArg()
                .withDescription("set the method to be checked").create('m');
        Option dependenciesOption = OptionBuilder.withArgName("classname").withLongOpt("dependencies").hasArgs()
                .withDescription("additional sources to be parsed").create('d');
        Option relevantClassesOption = OptionBuilder.withArgName("classname").withLongOpt("relevantClasses").hasArgs()
                .withDescription("Set the relevant classes to be used").create("rd");
        Option loopsOptions = OptionBuilder.withArgName("integer").withLongOpt("unroll").hasArg().withDescription("set number of loop unrollings").create('u');
        Option bitOptions = OptionBuilder.withArgName("integer").withLongOpt("width").hasArg().withDescription("set bit width").create('w');
        Option instOptions = OptionBuilder.withArgName("integer").withLongOpt("bound").hasArg().withDescription("set class bound").create('b');
        Option skolemizeOption = OptionBuilder.withLongOpt("skolemize").withDescription("set whether or not skolemize").create("sk");
        Option simulateOption = OptionBuilder.withLongOpt("simulate").withDescription("run method instead of checking").create("r");
        Option modularReasoningOption = OptionBuilder.withLongOpt("modularReasoning").withDescription("check method using modular reasoning").create("mr");
        Option relevancyAnalysisOption = OptionBuilder.withLongOpt("relevancyAnalysis").withDescription("calculate the needed relevantClasses").create("ra");
        Option scopeRestrictionOption = OptionBuilder.withLongOpt("scopeRestriction").withDescription("restrict signature scope to value set in -b option")
                .create("sr");
        /*
         * Option noVerifyOption = OptionBuilder.withLongOpt(
         * "noVerify").withDescription(
         * "builds output but does not invoke verification engine").create(
         * "nv");
         */
        Options options = new Options();
        options.addOption(helpOption);
        options.addOption(versionOption);
        options.addOption(configFileOption);
        options.addOption(classToCheckOption);
        options.addOption(methodToCheckOption);
        options.addOption(dependenciesOption);
        options.addOption(relevantClassesOption);
        options.addOption(loopsOptions);
        options.addOption(bitOptions);
        options.addOption(instOptions);
        options.addOption(skolemizeOption);
        options.addOption(simulateOption);
        options.addOption(modularReasoningOption);
        options.addOption(relevancyAnalysisOption);
        options.addOption(scopeRestrictionOption);
        // options.addOption(noVerifyOption)

        String configFileArgument = null;
        Properties overridingProperties = new Properties();
        TacoCustomScope tacoScope = new TacoCustomScope();

        // create the parser
        CommandLineParser parser = new PosixParser();

        try {
            // parse the command line arguments
            CommandLine line = parser.parse(options, args);

            // help
            if (line.hasOption(helpOption.getOpt())) {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp(120, CMD, HEADER, options, FOOTER, true);
                return;
            }

            // version
            if (line.hasOption(versionOption.getOpt())) {
                System.out.println(FOOTER);
                System.out.println("");
                return;
            }

            // Configuration file
            if (line.hasOption(configFileOption.getOpt())) {
                configFileArgument = line.getOptionValue(configFileOption.getOpt());
            }

            // class to check
            if (line.hasOption(classToCheckOption.getOpt())) {
                overridingProperties.put(TacoConfigurator.CLASS_TO_CHECK_FIELD, line.getOptionValue(classToCheckOption.getOpt()));
            }

            // method to check
            if (line.hasOption(methodToCheckOption.getOpt())) {
                String methodtoCheck = line.getOptionValue(methodToCheckOption.getOpt());

                if (!methodtoCheck.matches("^[A-Za-z0-9_-]+_[0-9]")) {
                    methodtoCheck = methodtoCheck + "_0";
                }
                overridingProperties.put(TacoConfigurator.METHOD_TO_CHECK_FIELD, methodtoCheck);
            }

            // Dependencies classes
            if (line.hasOption(dependenciesOption.getOpt())) {
                String dependenciesClasses = "";
                for (String aDependencyClass : line.getOptionValues(dependenciesOption.getOpt())) {
                    dependenciesClasses += aDependencyClass;
                }
                overridingProperties.put(TacoConfigurator.CLASSES_FIELD, dependenciesClasses);
            }

            // Relevant classes
            if (line.hasOption(relevantClassesOption.getOpt())) {
                String relevantClasses = "";
                for (String aRelevantClass : line.getOptionValues(relevantClassesOption.getOpt())) {
                    relevantClasses += aRelevantClass;
                }
                overridingProperties.put(TacoConfigurator.RELEVANT_CLASSES, relevantClasses);
            }

            // Loop unrolling
            if (line.hasOption(loopsOptions.getOpt())) {
                loopUnrolling = Integer.parseInt(line.getOptionValue(loopsOptions.getOpt()));
            }

            // Int bitwidth
            if (line.hasOption(bitOptions.getOpt())) {
                String alloy_bitwidth_str = line.getOptionValue(bitOptions.getOpt());
                overridingProperties.put(TacoConfigurator.BITWIDTH, alloy_bitwidth_str);
                int alloy_bitwidth = new Integer(alloy_bitwidth_str);
                tacoScope.setAlloyBitwidth(alloy_bitwidth);
            }

            // instances scope
            if (line.hasOption(instOptions.getOpt())) {
                String assertionsArguments = "for " + line.getOptionValue(instOptions.getOpt());
                overridingProperties.put(TacoConfigurator.ASSERTION_ARGUMENTS, assertionsArguments);
            }

            // Skolemize
            if (line.hasOption(skolemizeOption.getOpt())) {
                overridingProperties.put(TacoConfigurator.SKOLEMIZE_INSTANCE_INVARIANT, false);
                overridingProperties.put(TacoConfigurator.SKOLEMIZE_INSTANCE_ABSTRACTION, false);
            }

            // Simulation
            if (line.hasOption(simulateOption.getOpt())) {
                overridingProperties.put(TacoConfigurator.INCLUDE_SIMULATION_PROGRAM_DECLARATION, true);
                overridingProperties.put(TacoConfigurator.GENERATE_CHECK, false);
                overridingProperties.put(TacoConfigurator.GENERATE_RUN, false);
            }

            // Modular Reasoning
            if (line.hasOption(modularReasoningOption.getOpt())) {
                overridingProperties.put(TacoConfigurator.MODULAR_REASONING, true);
            }

            // Relevancy Analysis
            if (line.hasOption(relevancyAnalysisOption.getOpt())) {
                overridingProperties.put(TacoConfigurator.RELEVANCY_ANALYSIS, true);
            }

        } catch (ParseException e) {
            System.err.println("Command line parsing failed: " + e.getMessage());
        }

        try {
            System.out.println("****** Starting Taco (version. " + tacoVersion + ") ****** ");
            System.out.println("");

            File file = new File("config/log4j.xml");
            if (file.exists()) {
                DOMConfigurator.configure("config/log4j.xml");
            } else {
                System.err.println("log4j:WARN File config/log4j.xml not found");
            }

            TacoMain main = new TacoMain(null);

            // BUILD TacoScope

            main.run(configFileArgument, overridingProperties);

        } catch (IllegalArgumentException e) {
            System.err.println("Error found:");
            System.err.println(e.getMessage());
        } catch (MethodToCheckNotFoundException e) {
            System.err.println("Error found:");
            System.err.println("Method to check was not found. Please verify config file, or add -m option");
        } catch (TacoException e) {
            System.err.println("Error found:");
            System.err.println(e.getMessage());
        }
    }

    public TacoMain(HashMap<String, Object> inputToFix) {
        this.inputToFix = inputToFix;
    }

    public void run(String configFile) throws IllegalArgumentException {
        this.run(configFile, new Properties());
    }

    /**
     * @param configFile
     * @param overridingProperties Properties that overrides properties file's values
     */

    @SuppressWarnings("unchecked")
    public TacoAnalysisResult run(String configFile, Properties overridingProperties) throws IllegalArgumentException {
        TacoAnalysisResult tacoAnalysisResult;
        tacoAnalysisResult = null;

        int numTests = 10;
        boolean increaseTimeout = false;

        int timeout = 5;

//        for (int i = 0; i < 5 * numTests; i++) {

//            if(i % 5 == 0 && i != 0) increaseTimeout = true;

//            System.out.println("TEST NUMBER: " + i);
//            System.out.println();

            // parent directory where output files are stored
            String parentDirectory = "/root/testing_TACO/TACO/output_threads";
            File parentFolder = new File(parentDirectory);

            // do you want to delete output files?
            boolean deleteOutput = true;
            boolean deleteSuccess = false;
            try {
                // delete all output files
                if (parentFolder.isDirectory() && deleteOutput) {
                    DeleteOutputFiles.run();
                    deleteSuccess = true;
                } else System.out.println("Not a valid directory!!!!!");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            if (deleteSuccess) System.out.println("---FINISHED DELETING OUTPUT FILES---");
            else System.out.println("---FAILED TO DELETE FILES---");

            if (configFile == null) {
                throw new IllegalArgumentException("Config file not found, please verify option -cf");
            }

            List<JCompilationUnitType> compilation_units = null;
            String classToCheck = null;
            String methodToCheck = overridingProperties.getProperty(TacoConfigurator.METHOD_TO_CHECK_FIELD);

            // Start configurator
            JDynAlloyConfig.reset();
            JDynAlloyConfig.buildConfig(configFile, overridingProperties);


            SimpleJmlToJDynAlloyContext simpleJmlToJDynAlloyContext = null;
            //if (TacoConfigurator.getInstance().getBoolean(TacoConfigurator.JMLPARSER_ENABLED, TacoConfigurator.JMLPARSER_ENABLED_DEFAULT)) {
            // JAVA PARSING
            String sourceRootDir = TacoConfigurator.getInstance().getString(TacoConfigurator.JMLPARSER_SOURCE_PATH_STR);

            if (TacoConfigurator.getInstance().getString(TacoConfigurator.CLASS_TO_CHECK_FIELD) == null) {
                throw new TacoException("Config key 'CLASS_TO_CHECK_FIELD' is mandatory. Please check your config file or add the -c parameter");
            }
            List<String> files = new ArrayList<String>(Arrays.asList(JDynAlloyConfig.getInstance().getClasses()));
            classToCheck = TacoConfigurator.getInstance().getString(TacoConfigurator.CLASS_TO_CHECK_FIELD);
            if (!files.contains(classToCheck)) {
                files.add(classToCheck);
            }


            String userDir = System.getProperty("user.dir") + System.getProperty("file.separator") + "bin";
            JmlParser theParser = new JmlParser();

            boolean compilationSuccess = theParser.initialize(sourceRootDir, userDir /* Unused */, files);

            if (!compilationSuccess) {
                return null; //this means compilation failed;
            }

            compilation_units = theParser.getCompilationUnits();
            // END JAVA PARSING

            // BEGIN SIMPLIFICATION
            JmlStage aJavaCodeSimplifier = new JmlStage(compilation_units);
            aJavaCodeSimplifier.execute();
            JmlToSimpleJmlContext jmlToSimpleJmlContext = aJavaCodeSimplifier.getJmlToSimpleJmlContext();
            List<JCompilationUnitType> simplified_compilation_units = aJavaCodeSimplifier.get_simplified_compilation_units();


            // END SIMPLIFICATION

            Queue<JCompilationUnitTypeWrapper> pendingProblems = new ConcurrentLinkedQueue<JCompilationUnitTypeWrapper>();

            Queue<JCompilationUnitTypeWrapper> problemsToFurtherDeterminize = new ConcurrentLinkedQueue<JCompilationUnitTypeWrapper>();
            JCompilationUnitTypeWrapper initialTask = new JCompilationUnitTypeWrapper(simplified_compilation_units);
            //		problemsToFurtherDeterminize.offer(initialTask);

            tacoAnalysisResult = null;

            int numSAT = 0;
            int numUNSAT = 0;
            int numErrors = 0;
            int numAttended = 0;
            int pendingSize = pendingProblems.size();
            int toSplitSize = 0;
            int numInterrupted = 0;
            int numDiscarded = 0;

            // -------------BEGIN EXECUTOR SERVICE-----------


            // make lists
            //		List<Callable<TacoAnalysisResult>> translateThreadList = new ArrayList<>();
            //
            //		List<Future<TacoAnalysisResult>> futureThreadList = new ArrayList<>();

            Semaphore semJmlParser = new Semaphore(1);
            Semaphore semJava2JDyn = new Semaphore(1);
            Semaphore semJDyn2Dyn = new Semaphore(1);
            Semaphore semJUnitConstruction = new Semaphore(1);

            int numProcessorThreads = 10;

            // create executor service for thread processing
            //		ExecutorService translationService = Executors.newFixedThreadPool(numProcessorThreads);
            //		ThreadPoolExecutor pool = (ThreadPoolExecutor) translationService;

            int maxPendingQueueSize = 20 * numProcessorThreads;
            int minPendingQueueSize = 4 * numProcessorThreads;


            //		Set<ar.edu.taco.utils.TranslateThread> theAvailableThreadsPool = new HashSet<ar.edu.taco.utils.TranslateThread>();
            ConcurrentLinkedQueue<Message> theSharedQueue = new ConcurrentLinkedQueue<Message>();

            //		for (int numThreads = 0; numThreads < numProcessorThreads; numThreads++) {
            //			ar.edu.taco.utils.TranslateThread tt = new ar.edu.taco.utils.TranslateThread(theSharedQueue, semJmlParser, semJava2JDyn, semJDyn2Dyn, null, jmlToSimpleJmlContext, overridingProperties, log, tacoAnalysisResult, inputToFix, classToCheck, methodToCheck, sourceRootDir, configFile, FILE_SEP, 0);
            //			theAvailableThreadsPool.add(tt);
            //		}


//            if(increaseTimeout) {
//                timeout = timeout + 5;
//                System.out.println("Increased timeout: " + timeout);
//            }

            int timeoutDeterminizedPrograms = Integer.MAX_VALUE;
            String space = "   ";
            pendingProblems.add(initialTask);
            int theRunningThreads = 0;

            long initialTime = System.currentTimeMillis();
            long previousTime = initialTime;
            long previousUpdateTime = initialTime;
            String title = "";
            String content = "";

            int updateTime = 3;
            int numFinishedLastWindow = 0;
            int numFinishedPreviousWindow = 0;
            int numFinished = 0;

            Window windowValWrapper;
            WindowList winList = new WindowList(timeout);

            while (!pendingProblems.isEmpty() || !problemsToFurtherDeterminize.isEmpty() || !theSharedQueue.isEmpty() || theRunningThreads > 0) {
                //-------BEGIN TRANSLATION THREAD PROCESS
                int numPending = pendingProblems.size();
                int numToSplit = problemsToFurtherDeterminize.size();

                if (!theSharedQueue.isEmpty()) {

                    Message m = theSharedQueue.poll();

//				System.out.println("Message in TacoMain " +
//						m.theResult + " " +
//						m.TO + " " +
//						m.theWorkingThread.getCompilationUnitWrapper().getDeterminized() +  " " +
//						m.theWorkingThread.getCompilationUnitWrapper().getTimeout());


                    TranslateThread theEmployedThread = m.theWorkingThread;

                    if (m.TO && !m.getTheWorkingThread().getCompilationUnitWrapper().getDeterminized()) {
                        //					JCompilationUnitType theCU = theEmployedThread.getCompilationUnitWrapper().getUnit();
                        JCompilationUnitTypeWrapper theWrappedCU = theEmployedThread.getCompilationUnitWrapper();
                        problemsToFurtherDeterminize.offer(theWrappedCU);
                        numInterrupted++;
                    } else {
                        if (m.TO && m.getTheWorkingThread().getCompilationUnitWrapper().getDeterminized()) {
                            numDiscarded++;
                        } else {
                            if (m.theResult) { //using true to mode SAT
                                System.out.println("SAT WAS DETECTED");
                                numSAT++;
                                numFinished++;
                            } else {
                                System.out.println("UNSAT WAS DETECTED");
                                numUNSAT++; //using false to model UNSAT
                                numFinished++;
                            }
                        }
                    }
                    theRunningThreads--;
                }

                if (!problemsToFurtherDeterminize.isEmpty() && partitionAllowed(minPendingQueueSize, maxPendingQueueSize, pendingProblems.size())) {
                    JCompilationUnitTypeWrapper toDeterminize = problemsToFurtherDeterminize.poll();
                    int num_Problems = numDeterminizedProblems(minPendingQueueSize, maxPendingQueueSize, pendingProblems.size());
                    ConcurrentLinkedQueue<JCompilationUnitTypeWrapper> moreDeterminizedProblems = removeNonDeterminism(toDeterminize, num_Problems);
                    int numNewProblems = moreDeterminizedProblems.size();
                    if (moreDeterminizedProblems.size() == 1) {
                        JCompilationUnitTypeWrapper determinized = moreDeterminizedProblems.poll();
                        determinized.setDeterminized();
                        pendingProblems.add(determinized);
                    } else {
                        pendingProblems.addAll(moreDeterminizedProblems);
                    }

                    String splittedInfo = String.format("split             new %1$8d", numNewProblems);
                    System.out.println(splittedInfo);


                }


                //			boolean someFreeThread = theRunningThreads < numProcessorThreads;
                boolean someFreeThread = theRunningThreads < numProcessorThreads;
                if (!pendingProblems.isEmpty() && someFreeThread) {
                    JCompilationUnitTypeWrapper determinizedWrapped = pendingProblems.poll();
                    int problemTO = timeout;
                    if (determinizedWrapped.getDeterminized()) {
                        problemTO = timeoutDeterminizedPrograms;
                    }
                    determinizedWrapped.setTimeout(problemTO);

                    //				ar.edu.taco.utils.TranslateThread theCurrentThread = new ar.edu.taco.utils.TranslateThread(theSharedQueue, semJmlParser, semJava2JDyn, semJDyn2Dyn, semJUnitConstruction, determinizedWrapped, jmlToSimpleJmlContext, overridingProperties, log, tacoAnalysisResult, inputToFix, classToCheck, methodToCheck, sourceRootDir, configFile, FILE_SEP, 0);
                    //				Callable<TacoAnalysisResult> translationThread =
                    //						new ar.edu.taco.utils.TranslateThread(semJmlParser, semJava2JDyn, semJDyn2Dyn, determinizedUnit, jmlToSimpleJmlContext,overridingProperties,log,tacoAnalysisResult,inputToFix,compilation_units,classToCheck,methodToCheck,sourceRootDir,configFile,FILE_SEP);
                    TranslateThread translateThread =
                            new TranslateThread(theSharedQueue, semJmlParser, semJava2JDyn, semJDyn2Dyn, semJUnitConstruction, determinizedWrapped, jmlToSimpleJmlContext, overridingProperties, log, tacoAnalysisResult, inputToFix, classToCheck, methodToCheck, sourceRootDir, configFile, FILE_SEP, timeout);

                    theRunningThreads++;
                    numAttended++;
                    translateThread.start();
                }

                long currentTime = System.currentTimeMillis() - initialTime;

                if (System.currentTimeMillis() >= previousUpdateTime + 1000 * updateTime) {
                    previousUpdateTime = System.currentTimeMillis();
                    numFinishedPreviousWindow = numFinishedLastWindow;
                    numFinishedLastWindow = numFinished;

                    windowValWrapper = new Window(numFinishedPreviousWindow, numFinishedLastWindow, numSAT);
                    winList.addWLVals(windowValWrapper);

                    System.out.println();
                    System.out.println("                                                                                                                                                                                                                               Previous: " + numFinishedPreviousWindow + "          Current: " + numFinishedLastWindow);
                    numFinished = 0;
                }

                if (System.currentTimeMillis() >= previousTime + 1000) {
                    previousTime = System.currentTimeMillis();
                    toSplitSize = problemsToFurtherDeterminize.size();
                    numErrors = numAttended - numSAT - numUNSAT - numInterrupted - numDiscarded - theRunningThreads;


                    title = makeTitle("Time ellapsed", "Num Attended", "Num SAT", "Num UNSAT", "Num Unknown", "Num Errors", "Num Interrupted", "Num Pending", "Num To Split", "Num Running Threads", "TO");
                    content = makeContent(currentTime / 1000, numAttended, numSAT, numUNSAT, numDiscarded, numErrors, numInterrupted, numPending, toSplitSize, theRunningThreads, timeout);

                    System.out.println();
                    System.out.println(title);
                    System.out.println(content);
                    System.out.println();

                }
            }
            winList.printVals();

            System.out.println();

            System.out.println("Mean: " + winList.getMeanVal());
            System.out.println("Range: " + winList.getRangeVal());
            System.out.println("Minimum Difference: " + winList.getMinVal());
            System.out.println("Maximum Difference: " + winList.getMaxVal());

            winList.writeToFile();

            increaseTimeout = false;
//        }
        return tacoAnalysisResult;
    }


    private String makeContent(long elapsedTime, int numAttended, int numSAT, int numUNSAT, int numDiscarded, int numErrors,
                               int numInterrupted, int numPending, int toSplitSize, int runningThreads, int timeout) {
        // TODO Auto-generated method stub
        String theSpace = "";
        String theContent = String.format("%2$12d %1$4s %3$12d %1$4s %4$7d %1$4s %5$9d %1$4s %11$11d %1$4s %6$10d %1$4s %7$15d %1$4s %8$11d %1$4s %9$12d %1$4s %10$19d %1$4s %12$6d", theSpace, elapsedTime, numAttended, numSAT, numUNSAT, numErrors, numInterrupted, numPending, toSplitSize, runningThreads, numDiscarded, timeout);

        return theContent;
    }

    private String makeTitle(String elapsed, String attended, String numSAT, String numUNSAT, String nunDiscarded, String numErrors,
                             String numInterrupted, String numPending, String toSplitSize, String numRunningThreads, String timeout) {
        // TODO Auto-generated method stub
        String reportTitle = "Report";
        String elapsedTitle = "Elapsed time";
        String numAttendedTitle = "Num Attended";
        String NumSATTitle = "Num SAT";
        String NumUNSATTitle = "Num UNSAT";
        String numDiscardedTitle = "Num Unknown";
        String numErrorsTitle = "Num Errors";
        String numInterruptedTitle = "Num Interrupted";
        String numPendingTitle = "Num Pending";
        String numToSplitTitle = "Num to Split";
        String numRunnThreadsTitle = "Num Running Threads";
        String theSpace = "";
        String theTitle = String.format("%2$12s %1$4s %3$12s %1$4s %4$7s %1$4s %5$9s %1$4s %11$11s %1$4s %6$10s %1$4s %7$15s %1$4s %8$11s %1$4s %9$12s %1$4s %10$19s %1$4s %12$6s", theSpace, elapsedTitle, numAttendedTitle, NumSATTitle, NumUNSATTitle, numErrorsTitle, numInterruptedTitle, numPendingTitle, numToSplitTitle, numRunnThreadsTitle, numDiscardedTitle, timeout);
        return theTitle;
    }

    private int numDeterminizedProblems(int minPendingQueueSize, int maxPendingQueueSize, int size) {
        // TODO Auto-generated method stub
        return maxPendingQueueSize - size;
    }

    private boolean partitionAllowed(int minPendingQueueSize, int maxPendingQueueSize, int size) {
        // TODO Auto-generated method stub
        return size < minPendingQueueSize;
    }

    public ConcurrentLinkedQueue<JCompilationUnitTypeWrapper> removeNonDeterminism(JCompilationUnitTypeWrapper
                                                                                           simpleUnit, int size) {
        JmlAstDeterminizerVisitor theDeterminizer = new JmlAstDeterminizerVisitor();

        JCompilationUnitType simpleDeterminizedUnitType =  null;
        List<JCompilationUnitType> remainingUnitTypes = new LinkedList<JCompilationUnitType>();
        for (JCompilationUnitType unit : simpleUnit.getUnit()){
            String classToCheck = TacoConfigurator.getInstance().getString(TacoConfigurator.CLASS_TO_CHECK_FIELD);
            String unitDeclaresType = (unit.packageNameAsString()).replace(System.getProperty("file.separator"),".") + unit.fileNameIdent();
            if (classToCheck.equals(unitDeclaresType)){
                simpleDeterminizedUnitType = unit;
            } else {
                remainingUnitTypes.add(unit);
            }
        }


        JCompilationUnitType dUnitType = null;

        JCompilationUnitType thenUnit = null;
        JCompilationUnitType elseUnit = null;

        // Queue<JCompilationUnitType> theDeterminizedUnitTypeList = new ArrayList<>();

        ConcurrentLinkedQueue<JCompilationUnitType> problems = new ConcurrentLinkedQueue<JCompilationUnitType>();
        ConcurrentLinkedQueue<JCompilationUnitType> newProblems = new ConcurrentLinkedQueue<JCompilationUnitType>();
        problems.offer(simpleDeterminizedUnitType);

        boolean somethingWasSplit = false;
        while (problems.size() > 0 && problems.size() + newProblems.size() < size) {

            dUnitType = (JCompilationUnitType) problems.poll();
            theDeterminizer.setIsSplit(false);
            dUnitType.accept(theDeterminizer);

            if (theDeterminizer.isSplit()) {
                somethingWasSplit = true;
                thenUnit = (JCompilationUnitType) theDeterminizer.getQueue().poll();
                elseUnit = (JCompilationUnitType) theDeterminizer.getQueue().poll();
                newProblems.offer(thenUnit);
                newProblems.offer(elseUnit);
            } else {
                newProblems.offer(dUnitType);
                thenUnit = (JCompilationUnitType) theDeterminizer.getQueue().poll();
                thenUnit = (JCompilationUnitType) theDeterminizer.getQueue().poll();
            }

            if (problems.isEmpty() && !somethingWasSplit) {
                break;
            }

            if (problems.isEmpty()) {
                problems = newProblems;
                newProblems = new ConcurrentLinkedQueue<JCompilationUnitType>();
                somethingWasSplit = false;
            }
        }

        problems.addAll(newProblems);

        ConcurrentLinkedQueue<JCompilationUnitTypeWrapper> theWrappedProblems = new ConcurrentLinkedQueue<JCompilationUnitTypeWrapper>();

        for (JCompilationUnitType p : problems) {
            LinkedList<JCompilationUnitType> determinizedAndFriends = new LinkedList<>();
            determinizedAndFriends.addAll(remainingUnitTypes);
            determinizedAndFriends.add(p);
            JCompilationUnitTypeWrapper wrapped_p = new JCompilationUnitTypeWrapper(determinizedAndFriends);
            theWrappedProblems.offer(wrapped_p);
        }


        return theWrappedProblems;

    }

    private static String getManifestAttribute(Name name) {
        String manifestAttributeValue = "Undefined";
        try {

            String jarFileName = System.getProperty("java.class.path").split(System.getProperty("path.separator"))[0];
            JarFile jar = new JarFile(jarFileName);
            Manifest manifest = jar.getManifest();

            Attributes mainAttributes = manifest.getMainAttributes();
            manifestAttributeValue = mainAttributes.getValue(name);
            jar.close();
        } catch (IOException e) {
        }

        return manifestAttributeValue;
    }

    public static String editTestFileToCompile(String junitFile, String sourceClassName, String
            classPackage, String methodName) {
        String tmpDir = junitFile.substring(0, junitFile.lastIndexOf(FILE_SEP));
        tmpDir = tmpDir.replaceAll("generated", "output");
        File destFile = new File(tmpDir, obtainClassNameFromFileName(junitFile) + /*"_temp" +*/ ".java");
        String packageSentence = "package " + classPackage + ";\n";
        int posLastUnderscore = methodName.lastIndexOf("_");
        methodName = methodName.substring(0, posLastUnderscore);
        try {
            destFile.createNewFile();
            FileOutputStream fos = new FileOutputStream(destFile);
            boolean packageAlreadyWritten = false;
            Scanner scan = new Scanner(new File(junitFile));
            scan.useDelimiter("\n");
            boolean nextToTest = false;
            String str = null;
            boolean reachedSecondConstructorFromAnalyzedClass = false;
            boolean translatingGetInstance = true;
            while (scan.hasNext()) {
                str = scan.next();
                if (nextToTest) {
                    str = str.replace("()", "(String fileClasspath, String className, String methodName) throws IllegalAccessException, InvocationTargetException, ClassNotFoundException, InstantiationException, MalformedURLException");
                    fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
                    nextToTest = false;
                    //				} else if (str.contains("public class")){
                    //					int posOpeningBrace = str.indexOf("{");
                    //					str = str.substring(0, posOpeningBrace-1);
                    //					str = str + "_temp {";
                    //					fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
                } else if (str.contains("package") && !packageAlreadyWritten) {
                    fos.write(packageSentence.getBytes(Charset.forName("UTF-8")));
                    str = "           import java.util.Arrays;";
                    fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
                    str = "           import java.net.URL;";
                    fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
                    str = "           import java.net.URLClassLoader;";
                    fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
                    str = "           import java.net.MalformedURLException;";
                    fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
                    str = "           import java.io.File;";
                    fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
                    str = "           import java.lang.reflect.InvocationTargetException;";
                    fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
                    packageAlreadyWritten = true;
                } else if (str.contains("import") && !packageAlreadyWritten) {
                    fos.write(packageSentence.getBytes(Charset.forName("UTF-8")));
                    fos.write((scan.next() + "\n").getBytes(Charset.forName("UTF-8")));
                    packageAlreadyWritten = true;
                } else if (str.contains("new " + sourceClassName + "(") && !reachedSecondConstructorFromAnalyzedClass
                        && !translatingGetInstance) {
                    //		          str = "        try {";
                    //		          fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
                    str = "           String[] classpaths = fileClasspath.split(System.getProperty(\"path.separator\"));";
                    fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
                    str = "           URL[] urls = new URL[classpaths.length];";
                    fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
                    str = "           for (int i = 0 ; i < classpaths.length ; ++i) {";
                    fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
                    str = "              urls[i] = new File(classpaths[i]).toURI().toURL();";
                    fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
                    str = "           }";
                    fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
                    str = "           ClassLoader cl2 = new URLClassLoader(urls);";
                    fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
                    //		          str = "           ClassLoaderTools.addFile(fileClasspath);";
                    //		          fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
                    str = "           Class<?> clazz = cl2.loadClass(className);";
                    fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
                    str = "           Constructor<?>[] c = clazz.getDeclaredConstructors();";
                    fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
                    str = "           Object instance = null;";
                    fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
                    str = "           	Class<?>[] parameterTypes = null;";
                    fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
                    str = "           	Object[] paramValues = null;";
                    fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
                    str = "           	Constructor<?> co = null;";
                    fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
                    str = "           if (c.length > 0) {";
                    fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
                    str = "           	co = c[0];";
                    fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
                    str = "           	co.setAccessible(true);";
                    fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
                    str = "           	parameterTypes = co.getParameterTypes();";
                    fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
                    str = "           	paramValues = new Object[co.getParameterTypes().length];";
                    fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
                    str = "             for (int paramIndexer = 0; paramIndexer<parameterTypes.length; paramIndexer++){";
                    fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
                    str = "           		if (parameterTypes[paramIndexer].isPrimitive()){";
                    fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
                    str = "           			String typeSimpleName = parameterTypes[paramIndexer].getSimpleName();";
                    fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
                    str = "           			if (typeSimpleName.equals(\"boolean\")) {";
                    fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
                    str = "                         paramValues[paramIndexer] = false;";
                    fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
                    str = "                     } else if (typeSimpleName.endsWith(\"byte\")) {";
                    fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
                    str = "                       	paramValues[paramIndexer] = 0;";
                    fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
                    str = "                     } else if (typeSimpleName.endsWith(\"char\")) {";
                    fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
                    str = "                       	paramValues[paramIndexer] = 0;";
                    fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
                    str = "                     } else if (typeSimpleName.endsWith(\"double\")) {";
                    fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
                    str = "                       	paramValues[paramIndexer] = 0.0d;";
                    fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
                    str = "                     } else if (typeSimpleName.endsWith(\"float\")) {";
                    fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
                    str = "                       	paramValues[paramIndexer] = 0.0f;";
                    fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
                    str = "                     } else if (typeSimpleName.endsWith(\"int\")) {";
                    fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
                    str = "                       	paramValues[paramIndexer] = 0;";
                    fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
                    str = "                     } else if (typeSimpleName.endsWith(\"long\")) {";
                    fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
                    str = "                       	paramValues[paramIndexer] = 0L;";
                    fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
                    str = "                     } else if (typeSimpleName.endsWith(\"short\")) {";
                    fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
                    str = "                       	paramValues[paramIndexer] = 0;";
                    fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
                    str = "                     } else {";
                    fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
                    str = "                         System.out.println(\"ERROR: Undefined primitive type.\");";
                    fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
                    str = "                     }";
                    fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
                    str = "           		} else {";
                    fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
                    str = "           			paramValues[paramIndexer] = null;";
                    fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
                    str = "           		}";
                    fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
                    str = "           	}";
                    fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
                    str = "           	try {";
                    fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
                    str = "             	String dataCall = co.getName() + Arrays.toString(paramValues);";
                    fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
                    str = "             	System.out.println(dataCall);";
                    fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
                    str = "           	    instance = co.newInstance(paramValues);";
                    fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
                    str = "           	} catch (InstantiationException e) {";
                    fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
                    str = "           		e.printStackTrace();";
                    fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
                    str = "           	}";
                    fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
                    str = "           } else {";
                    fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
                    str = "           	System.out.println(\"The class under analysis has no constructors, and at least one should exist.\");";
                    fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
                    str = "           }";
                    fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
                    //                    str = "           Object instance = clazz.newInstance();";
                    //                    fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
                    reachedSecondConstructorFromAnalyzedClass = true;
                } else if (str.contains("//endGetInstance")) {
                    fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
                    translatingGetInstance = false;
                    reachedSecondConstructorFromAnalyzedClass = false;
                } else if (str.contains("Class<?> clazz;")) {
                } else if (str.contains("new " + sourceClassName + "(") && !translatingGetInstance) {
                    String backup = str;
                    String objectName = backup.split("[ ]+")[2];
                    str = "             Object " + objectName + " = null;";
                    fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
                    str = "           	try {";
                    fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
                    str = "             	String dataCall = co.getName() + Arrays.toString(paramValues);";
                    fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
                    str = "             	System.out.println(dataCall);";
                    fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
                    str = "           	   " + objectName + " = co.newInstance(paramValues);";
                    fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
                    str = "           	} catch (InstantiationException e) {";
                    fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
                    str = "           		e.printStackTrace();";
                    fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
                    str = "           	}";
                    fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
                } else if (str.contains("} catch (ClassNotFoundException e) {")) {
                    str = str.replace("ClassNotFoundException", "Exception");
                    fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
                } else if (str.matches(".*(?i)[\\.a-z0-9\\_]*" + sourceClassName + "(?=[^a-z0-9\\_\\.]).*")) {
                    str = str.replaceAll("(?i)[\\.a-z0-9\\_]*" + sourceClassName + "(?=[^a-z0-9\\_\\.])", /*classPackage+"."+*/sourceClassName);
                    str = str.replace("\"" + methodName + "\"", "methodName");
                    str = str.replace("\"" + sourceClassName + "\"", "clazz");
                    //					str = str.replace("(", "(fileClasspath, ");
                    fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
                } else if (str.contains("e.printStackTrace();")) {
                    //					fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
                    fos.write(("           throw(new java.lang.RuntimeException(e));" + "\n").getBytes(Charset.forName("UTF-8")));
                    //					fos.write(("throw e;" + "\n").getBytes(Charset.forName("UTF-8")));
                } else if (str.contains("private Method getAccessibleMethod")) {
                    str = str.replace("(String className, ", "(Class<?> clazz, ");
                    //					str = str.replace(") {", ") throws MalformedURLException {");
                    fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
                } else if (str.contains("private Constructor<?> getAccessibleConstructor")) {
                    str = str.replace("(String className, ", "(Class<?> clazz, ");
                    //                  str = str.replace(") {", ") throws MalformedURLException {");
                    fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
                } else if (str.contains("method.invoke(instance,")) {
                    fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
                    str = "           instance = null;";
                    fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
                    str = "           method = null;";
                    fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));

                } else if (str.contains("methodToCheck = clazz.getDeclaredMethod(methodName, parameterTypes);")) {
                    fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
                } else if (str.contains("clazz = Class.forName(className);")) {
                    //					str = "           ClassLoader cl = ClassLoader.getSystemClassLoader();";
                    //					fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
                    //					str = "           final ClassLoader cl2 = new URLClassLoader(new URL[]{new File(fileClasspath).toURI().toURL()}, cl);";
                    //					fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
                    //					str = "           clazz = cl2.loadClass(className);";
                    //					fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
                    //					str = "           System.out.println(\"actual class inside method: \"+clazz.getName());";
                    //					fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
                } else {
                    if (str.contains("@Test")) {
                        nextToTest = true;
                    }
                    //					if (!scan.hasNext()){
                    //						String s = "        } catch (ClassNotFoundException e){";
                    //						fos.write((s + "\n").getBytes(Charset.forName("UTF-8")));
                    //						s = "        } catch (InstantiationException e){}";
                    //						fos.write((s + "\n").getBytes(Charset.forName("UTF-8")));
                    //					}
                    fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
                }
            }
            fos.close();
            scan.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return destFile.toString();

    }

    private static final int NOT_PRESENT = -1;

    public static String obtainClassNameFromFileName(String fileName) {
        int lastBackslash = fileName.lastIndexOf("/");
        int lastDot = fileName.lastIndexOf(".");

        if (lastBackslash == NOT_PRESENT) {
            lastBackslash = 0;
        } else {
            lastBackslash += 1;
        }
        if (lastDot == NOT_PRESENT) {
            lastDot = fileName.length();
        }

        return fileName.substring(lastBackslash, lastDot);
    }

    //    public static List<JCompilationUnitType> parse_simplified_compilation_units(List<String> files) {
    //        //  for (String s : files)
    //            //  System.out.println("input file name: " + s);
    //
    //        String canonical_outdir_path = makeCanonicalPath();
    //
    //        //  System.out.println("target file name: " + canonical_outdir_path);
    ////        try {
    ////            String theActualOutputDir = TacoConfigurator.getInstance().getOutputDir();
    ////            File output_dir = new File(theActualOutputDir);
    ////            canonical_outdir_path = output_dir.getCanonicalPath();
    ////        } catch (IOException e) {
    ////            throw new TacoException("canonical path couldn't be computed " + e.getMessage());
    ////        }
    //
    //        JmlParser theParserInstance = new JmlParser();
    //        theParserInstance.initialize(canonical_outdir_path, System.getProperty("user.dir") + System.getProperty("file.separator") + "bin" /* Unused */,
    //                files);
    //
    //        return theParserInstance.getCompilationUnits();
    //
    //    }
    //
    //    public static List<String> write_simplified_compilation_units(List<JCompilationUnitType> newAsts) {
    //        List<String> files = new LinkedList<String>();
    //        String canonical_path = makeCanonicalPath();
    //        //  System.out.println("Canonical Path: " + canonical_path);
    //        for (JCompilationUnitType compilation_unit : newAsts) {
    //            assert compilation_unit.typeDeclarations().length == 1;
    //            JTypeDeclarationType typeDeclaration = compilation_unit.typeDeclarations()[0];
    //            String filename = canonical_path + File.separator + typeDeclaration.getCClass().getJavaName().replaceAll("\\.", "/");
    //
    //            //  System.out.println("Write files: " + filename);
    //
    //            files.add("output_" + Thread.currentThread().getName() + "." + typeDeclaration.getCClass().getJavaName());
    //            try {
    //                FileUtils.writeToFile(filename + OUTPUT_SIMPLIFIED_JAVA_EXTENSION, JavaAndJmlPrettyPrint2.print(compilation_unit));
    //            } catch (IOException e) {
    //                throw new RuntimeException("DYNJALLOY ERROR! " + e.getMessage());
    //            }
    //        }
    //        return files;
    //    }
    //
    //    private static String makeCanonicalPath() {
    //        String output_dir = TacoConfigurator.getInstance().getOutputDir() + "_" + Thread.currentThread().getName();
    //        File out_dir_dir = new File(output_dir);
    //
    //        if (!out_dir_dir.exists()) {
    //            out_dir_dir.mkdirs();
    //        }
    //
    //        String canonical_path;
    //        try {
    //            canonical_path = out_dir_dir.getCanonicalPath();
    //        } catch (IOException e1) {
    //            throw new TacoException("path couldn't be found: " + out_dir_dir);
    //        }
    //        return canonical_path;
    //    }




}