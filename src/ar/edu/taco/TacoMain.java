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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.Charset;
import java.security.DigestOutputStream;
import java.security.MessageDigest;
import java.util.*;
import java.util.jar.Attributes;
import java.util.jar.Attributes.Name;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import ar.edu.taco.utils.jml.JmlAstDeterminizerVisitor;

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
import org.jmlspecs.jmlrac.JavaAndJmlPrettyPrint2;
import org.multijava.mjc.JCompilationUnitType;
import org.multijava.mjc.JTypeDeclarationType;

import ar.edu.jdynalloy.JDynAlloyConfig;
import ar.edu.jdynalloy.MethodToCheckNotFoundException;
import ar.edu.jdynalloy.ast.JDynAlloyModule;
import ar.edu.taco.engine.AlloyStage;
import ar.edu.taco.engine.DynalloyStage;
import ar.edu.taco.engine.JDynAlloyParsingStage;
import ar.edu.taco.engine.JDynAlloyPrinterStage;
import ar.edu.taco.engine.JDynAlloyStage;
import ar.edu.taco.engine.JUnitStage;
import ar.edu.taco.engine.JavaTraceStage;
import ar.edu.taco.engine.JmlStage;
import ar.edu.taco.engine.PrecompiledModules;
import ar.edu.taco.engine.SimpleJmlStage;
import ar.edu.taco.engine.SnapshotStage;
import ar.edu.taco.engine.StrykerStage;
import ar.edu.taco.jfsl.JfslStage;
import ar.edu.taco.jml.JmlToSimpleJmlContext;
import ar.edu.taco.jml.parser.JmlParser;
import ar.edu.taco.junit.RecoveredInformation;
import ar.edu.taco.simplejml.SimpleJmlToJDynAlloyContext;
import ar.edu.taco.stryker.api.impl.MuJavaController.MsgDigest;
import ar.edu.taco.utils.FileUtils;
import ar.uba.dc.rfm.alloy.AlloyTyping;
import ar.uba.dc.rfm.alloy.ast.formulas.AlloyFormula;
import ar.uba.dc.rfm.dynalloy.DynAlloyCompiler;
import ar.uba.dc.rfm.dynalloy.analyzer.AlloyAnalysisResult;
import ar.uba.dc.rfm.dynalloy.ast.DynalloyModule;
import ar.uba.dc.rfm.dynalloy.ast.ProgramDeclaration;

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

    static final private String OUTPUT_SIMPLIFIED_JAVA_EXTENSION = ".java";
    private static final String CMD = "Taco";
    private static final String HEADER = "Taco static analysis tool.";
    private static final String FOOTER = "For questions and comments please write to jgaleotti AT dc DOT uba DOT ar";
    public static final String PATH_SEP = System.getProperty("path.separator");
    public static final String FILE_SEP = System.getProperty("file.separator");

    private Object inputToFix;

    /**
     * @param args
     */
    @SuppressWarnings({"static-access"})
    public static void main(String[] args) {
        @SuppressWarnings("unused")
        int loopUnrolling = 3;

        String tacoVersion = getManifestAttribute(Attributes.Name.IMPLEMENTATION_VERSION);
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
        if (configFile == null) {
            throw new IllegalArgumentException("Config file not found, please verify option -cf");
        }

        List<JCompilationUnitType> compilation_units = null;
        String classToCheck = null;
        String methodToCheck = overridingProperties.getProperty(TacoConfigurator.METHOD_TO_CHECK_FIELD);

        // Start configurator
        JDynAlloyConfig.reset();
        JDynAlloyConfig.buildConfig(configFile, overridingProperties);


        SimpleJmlToJDynAlloyContext simpleJmlToJDynAlloyContext;
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
        boolean compilationSuccess = JmlParser.getInstance().initialize(sourceRootDir, userDir /* Unused */, files);

        if (!compilationSuccess) {
            return null; //this means compilation failed;
        }

        compilation_units = JmlParser.getInstance().getCompilationUnits();
        // END JAVA PARSING

        // BEGIN SIMPLIFICATION
        JmlStage aJavaCodeSimplifier = new JmlStage(compilation_units);
        aJavaCodeSimplifier.execute();
        JmlToSimpleJmlContext jmlToSimpleJmlContext = aJavaCodeSimplifier.getJmlToSimpleJmlContext();
        List<JCompilationUnitType> simplified_compilation_units = aJavaCodeSimplifier.get_simplified_compilation_units();

        // END SIMPLIFICATION

        // BEGIN AST DETERMINIZER

        Queue<JCompilationUnitType> splitProblems = removeNonDeterminism(simplified_compilation_units, 4096);
        System.out.println("The total number of queued problems is " + splitProblems.size());




        List<String> fileNames = null;

        JCompilationUnitType units = null;

        // END AST DETERMINIZER

        TacoAnalysisResult tacoAnalysisResult = null;
        while (!splitProblems.isEmpty()) {
            simplified_compilation_units = new ArrayList<JCompilationUnitType>();
            List<JCompilationUnitType> theDeterminizedUnitTypeList = new ArrayList<JCompilationUnitType>();
            theDeterminizedUnitTypeList.add(splitProblems.poll());
            fileNames = write_simplified_compilation_units(theDeterminizedUnitTypeList);
            units = parse_simplified_compilation_units(fileNames).remove(0);
            simplified_compilation_units.add(units);


            // BEGIN JAVA TO JDYNALLOY TRANSLATION
            // JDynAlloy modules have Alloy contracts and dynAlloy programs
            SimpleJmlStage aJavaToJDynAlloyTranslator = new SimpleJmlStage(simplified_compilation_units);
            //HERE IS WHERE THE PREDS AND VARS ARE PRODUCED
            aJavaToJDynAlloyTranslator.execute();
            // END JAVA TO JDYNALLOY TRANSLATION

            simpleJmlToJDynAlloyContext = aJavaToJDynAlloyTranslator.getSimpleJmlToJDynAlloyContext();

            // JFSL TO JDYNALLOY TRANSLATION
            JfslStage aJfslToDynJAlloyTranslator = new JfslStage(simplified_compilation_units, aJavaToJDynAlloyTranslator.getModules(), jmlToSimpleJmlContext,
                    simpleJmlToJDynAlloyContext);
            aJfslToDynJAlloyTranslator.execute();

            aJfslToDynJAlloyTranslator = null;
            // END JFSL TO JDYNALLOY TRANSLATION

            // PRINT JDYNALLOY
            JDynAlloyPrinterStage printerStage = new JDynAlloyPrinterStage(aJavaToJDynAlloyTranslator.getModules());
            printerStage.execute();
            printerStage = null;
            // END PRINT JDYNALLOY

            List<JDynAlloyModule> jdynalloy_modules = new ArrayList<JDynAlloyModule>();
            jdynalloy_modules.addAll(aJavaToJDynAlloyTranslator.getModules());

//        } else {
//            simpleJmlToJDynAlloyContext = null;
//        }

            // JDYNALLOY BUILT-IN MODULES
            PrecompiledModules precompiledModules = null;
            if (this.inputToFix != null) {
                precompiledModules = new PrecompiledModules((HashMap<String, Object>) inputToFix);
            } else {
                precompiledModules = new PrecompiledModules();
            }
            precompiledModules.execute();
            jdynalloy_modules.addAll(precompiledModules.getModules());
            // END JDYNALLOY BUILT-IN MODULES

            // JDYNALLOY STATIC FIELDS CLASS
            JDynAlloyModule staticFieldsModule = precompiledModules.generateStaticFieldsModule();
            jdynalloy_modules.add(staticFieldsModule);
            /**/
            staticFieldsModule = null;
            // END JDYNALLOY STATIC FIELDS CLASS

            // JDYNALLOY PARSING
            if (TacoConfigurator.getInstance().getBoolean(TacoConfigurator.JDYNALLOY_PARSER_ENABLED, TacoConfigurator.JDYNALLOY_PARSER_ENABLED_DEFAULT)) {
                log.info("****** START: Parsing JDynAlloy files ****** ");
                JDynAlloyParsingStage jDynAlloyParser = new JDynAlloyParsingStage(jdynalloy_modules);
                jDynAlloyParser.execute();
                jdynalloy_modules.addAll(jDynAlloyParser.getParsedModules());
                /**/
                jDynAlloyParser = null;
                log.info("****** END: Parsing JDynAlloy files ****** ");
            } else {
                log.info("****** INFO: Parsing JDynAlloy is disabled (hint enablet it using 'jdynalloy.parser.enabled') ****** ");
            }
            // END JDYNALLOY PARSING

            // BEGIN JDYNALLOY TO DYNALLOY TRANSLATION
            String methodToCheckWithoutTyping = overridingProperties.getProperty("methodToCheck").substring(0, overridingProperties.getProperty("methodToCheck").indexOf('('));
            JDynAlloyStage dynJAlloyToDynAlloyTranslator = new JDynAlloyStage(jdynalloy_modules, overridingProperties.getProperty("classToCheck"), methodToCheckWithoutTyping, inputToFix);
            dynJAlloyToDynAlloyTranslator.setJavaArithmetic(TacoConfigurator.getInstance().getUseJavaArithmetic());
            dynJAlloyToDynAlloyTranslator.setRemoveQuantifiers(TacoConfigurator.getInstance().getRemoveQuantifiers());
            dynJAlloyToDynAlloyTranslator.execute();
            // END JDYNALLOY TO DYNALLOY TRANSLATION

            AlloyAnalysisResult alloy_analysis_result = null;
            DynalloyStage dynalloyToAlloy = null;

            // GRAB PREDICATES COMING FROM ARITHMETIC EXPRESSIONS
            HashMap<String, AlloyTyping> varsAndTheirTypesComingFromArithmeticConstraintsInContractsByProgram = new HashMap<String, AlloyTyping>();
            HashMap<String, List<AlloyFormula>> predsComingFromArithmeticConstraintsInContractsByProgram = new HashMap<String, List<AlloyFormula>>();

            HashMap<String, AlloyTyping> varsAndTheirTypesComingFromArithmeticConstraintsInObjectInvariantsByModule = new HashMap<String, AlloyTyping>();
            HashMap<String, List<AlloyFormula>> predsComingFromArithmeticConstraintsInObjectInvariantsByModule = new HashMap<String, List<AlloyFormula>>();

            for (DynalloyModule dm : dynJAlloyToDynAlloyTranslator.getGeneratedModules()) {
                String modName = dm.getModuleId();
                varsAndTheirTypesComingFromArithmeticConstraintsInObjectInvariantsByModule.put(modName, dm.getVarsComingFromArithmeticConstraintsInObjectInvariants());
                predsComingFromArithmeticConstraintsInObjectInvariantsByModule.put(modName, dm.getPredsComingFromArithmeticConstraintsInObjectInvariants());
                Set<ProgramDeclaration> progs = dm.getPrograms();
                for (ProgramDeclaration pd : progs) {
                    varsAndTheirTypesComingFromArithmeticConstraintsInContractsByProgram.put(pd.getProgramId(), pd.getVarsFromArithInContracts());
                    predsComingFromArithmeticConstraintsInContractsByProgram.put(pd.getProgramId(), pd.getPredsFromArithInContracts());
                }
            }

            // DYNALLOY TO ALLOY TRANSLATION
            if (TacoConfigurator.getInstance().getBoolean(TacoConfigurator.DYNALLOY_TO_ALLOY_ENABLE)) {

                dynalloyToAlloy = new DynalloyStage(dynJAlloyToDynAlloyTranslator.getOutputFileNames(),
                        varsAndTheirTypesComingFromArithmeticConstraintsInObjectInvariantsByModule,
                        predsComingFromArithmeticConstraintsInObjectInvariantsByModule,
                        varsAndTheirTypesComingFromArithmeticConstraintsInContractsByProgram,
                        predsComingFromArithmeticConstraintsInContractsByProgram, inputToFix);

                dynalloyToAlloy.setSourceJDynAlloy(dynJAlloyToDynAlloyTranslator.getPrunedModules());
                dynalloyToAlloy.execute();
                // DYNALLOY TO ALLOY TRANSLATION

                log.info("****** Transformation process finished ****** ");

                if (TacoConfigurator.getInstance().getNoVerify() == false) {
                    // Starts dynalloy to alloy tranlation and alloy verification

                    AlloyStage alloy_stage = new AlloyStage(dynalloyToAlloy.get_alloy_filename());
                    dynalloyToAlloy = null;
                    
                    long initTime = System.nanoTime();

                    alloy_stage.execute();
                    
                    long finalTime = System.nanoTime();
                    
                    long elapsedTimeInSeconds = (finalTime - initTime) / 1000000000;
                    
                    System.out.println("Ellapsed time: " + elapsedTimeInSeconds +"s");

                    alloy_analysis_result = alloy_stage.get_analysis_result();
                    /**/
                    alloy_stage = null;
                }
            }
            

            tacoAnalysisResult = new TacoAnalysisResult(alloy_analysis_result);

            String junitFile = null;

            if (TacoConfigurator.getInstance().getGenerateUnitTestCase() || TacoConfigurator.getInstance().getAttemptToCorrectBug()) {
                // Begin JUNIT Generation Stage
                if (tacoAnalysisResult.get_alloy_analysis_result().isSAT())
                    System.out.println("JUnit generation: started");

                SnapshotStage snapshotStage = new SnapshotStage(compilation_units, tacoAnalysisResult, classToCheck, methodToCheck);
                try {
                    snapshotStage.execute();
                    RecoveredInformation recoveredInformation = snapshotStage.getRecoveredInformation();
                    recoveredInformation.setFileNameSuffix(StrykerStage.fileSuffix);
                    JUnitStage jUnitStage = new JUnitStage(recoveredInformation);
                    jUnitStage.execute();
                    junitFile = jUnitStage.getJunitFileName();
                    if (tacoAnalysisResult.get_alloy_analysis_result().isSAT())
                        System.out.println("         ... and finished.");

                } catch (TacoException e) {
                    System.out.println("");
                    System.out.println(e.getMessage());
                }
                // End JUNIT Generation Stage
            } else {
                log.info("****** JUnit with counterexample values will not be generated. ******* ");
                if (tacoAnalysisResult.get_alloy_analysis_result().isSAT())
                    System.out.println("JUnit generation: skipped even though a bug/execution exists");
                if (!TacoConfigurator.getInstance().getGenerateUnitTestCase()) {
                    log.info("****** generateUnitTestCase=false ******* ");
                }

            }

            if (TacoConfigurator.getInstance().getBuildJavaTrace()) {
                if (tacoAnalysisResult.get_alloy_analysis_result().isSAT()) {
                    log.info("****** START: Java Trace Generation ****** ");
                    DynAlloyCompiler compiler = dynalloyToAlloy.getDynAlloyCompiler();
                    JavaTraceStage javaTraceStage = new JavaTraceStage(compiler.getSpecContext(), alloy_analysis_result, false);
                    javaTraceStage.execute();
                    //				DynAlloySolution dynAlloySolution = javaTraceStage.getDynAlloySolution();
                    //				List<TraceStep> trace = dynAlloySolution.getTrace();

                    log.info("****** FINISH: Java Trace Generation ****** ");
                }
            } else {
                log.info("****** Java Trace will not be generated. ******* ");
                log.info("****** generateJavaTrace=false ******* ");
            }

            if (TacoConfigurator.getInstance().getAttemptToCorrectBug()) {
                if (tacoAnalysisResult.get_alloy_analysis_result().isSAT() &&
                        tacoAnalysisResult.get_alloy_analysis_result().getAlloy_solution().getOriginalCommand().startsWith("Check")) {
                    log.info("****** START: Stryker ****** ");
                    methodToCheck = overridingProperties.getProperty(TacoConfigurator.METHOD_TO_CHECK_FIELD);
                    sourceRootDir = TacoConfigurator.getInstance().getString(
                            TacoConfigurator.JMLPARSER_SOURCE_PATH_STR);
                    StrykerStage strykerStage = new StrykerStage(compilation_units, sourceRootDir, classToCheck,
                            methodToCheck, configFile, overridingProperties,
                            TacoConfigurator.getInstance().getMaxStrykerMethodsForFile());
                    StrykerStage.junitInputs = new Class<?>[50];
                    StrykerStage.junitFiles = new String[50];

                    try {
                        String currentJunit = null;

                        String tempFilename = junitFile.substring(0, junitFile.lastIndexOf(FILE_SEP) + 1) /*+ FILE_SEP*/;
                        String packageToWrite = "ar.edu.output.junit";
                        String fileClasspath = tempFilename.substring(0, tempFilename.lastIndexOf(new String("ar.edu.generated.junit").replaceAll("\\.", FILE_SEP)));
                        fileClasspath = fileClasspath.replaceFirst("generated", "output");
                        //					String currentClasspath = System.getProperty("java.class.path")+PATH_SEP+fileClasspath/*+PATH_SEP+System.getProperty("user.dir")+FILE_SEP+"generated"*/;
                        currentJunit = editTestFileToCompile(junitFile, classToCheck, packageToWrite, methodToCheck);

                        File[] file1 = new File[]{new File(currentJunit)};
                        JavaCompiler javaCompiler = ToolProvider.getSystemJavaCompiler();
                        StandardJavaFileManager fileManager = javaCompiler.getStandardFileManager(null, null, null);
                        Iterable<? extends JavaFileObject> compilationUnit1 =
                                fileManager.getJavaFileObjectsFromFiles(Arrays.asList(file1));
                        javaCompiler.getTask(null, fileManager, null, null, null, compilationUnit1).call();
                        fileManager.close();
                        javaCompiler = null;
                        file1 = null;
                        fileManager = null;

                        ///*mfrias*/		int compilationResult =	javaCompiler.run(null, null, null /*new NullOutputStream()*/, new String[]{"-classpath", currentClasspath, currentJunit});
                        ///**/				javaCompiler = null;
                        //					if(compilationResult == 0) {
                        log.warn("junit counterexample compilation succeded");
                        ClassLoader cl = ClassLoader.getSystemClassLoader();
                        @SuppressWarnings("resource")
                        ClassLoader cl2 = new URLClassLoader(new URL[]{new File(fileClasspath).toURI().toURL()}, cl);
                        //						ClassLoaderTools.addFile(fileClasspath);
                        Class<?> clazz = cl2.loadClass(packageToWrite + "." + obtainClassNameFromFileName(junitFile));
                        //						Method[] meth = clazz.getMethods();
                        //						log.info("preparing to add a class containing a test input to the pool... "+packageToWrite+"."+MuJavaController.obtainClassNameFromFileName(junitFile));
                        //						Result result = null;
                        //						final Object oToRun = clazz.newInstance();
                        DigestOutputStream dos;
                        File duplicatesTempFile = null;
                        String content = null;
                        try {
                            content = FileUtils.readFile(junitFile);
                        } catch (Exception e) {
                            throw new IllegalArgumentException("invalid or null file");
                        }
                        try {
                            duplicatesTempFile = File.createTempFile("forDuplicatesJunit", null);
                            dos = new DigestOutputStream(new FileOutputStream(duplicatesTempFile, false), MessageDigest.getInstance("MD5"));
                            dos.write(content.getBytes());
                            dos.flush();
                            dos.close();
                        } catch (Exception e) {
                            throw new IllegalArgumentException("exception thrown while trying to compute digest in class VariablizedSATVerdicts");
                        }
                        byte[] digest = dos.getMessageDigest().digest();
                        MsgDigest msgDigest = new MsgDigest(digest);
                        StrykerStage.junitFilesHash.put(msgDigest, junitFile);
                        StrykerStage.junitInputs[StrykerStage.indexToLastJUnitInput] = clazz;
                        StrykerStage.junitFiles[StrykerStage.indexToLastJUnitInput] = junitFile;
                        StrykerStage.indexToLastJUnitInput++;
                        cl = null;
                        cl2 = null;

                        //
                        //					} else {
                        //						log.warn("compilation failed");
                        //					}
                        //							File originalFile = new File(tempFilename);
                        //							originalFile.delete();

                    } catch (ClassNotFoundException e) {
                        //							e.printStackTrace();
                    } catch (IOException e) {
                        //							e.printStackTrace();
                    } catch (IllegalArgumentException e) {
                        //							e.printStackTrace();
                    } catch (Exception e) {
                        //							e.printStackTrace();
                    }

                    strykerStage.execute();

                    log.info("****** FINISH: Stryker ****** ");
                }
            } else {
                log.info("****** BugFix will not be generated. ******* ");
                log.info("****** attemptToCorrectBug=false ******* ");
            }
            System.out.println("Subproblems Left : " + splitProblems.size());
        }
        return tacoAnalysisResult;
    }

    public Queue<JCompilationUnitType> removeNonDeterminism(List<JCompilationUnitType> simpleUnits, int size) {
        JmlAstDeterminizerVisitor theDeterminizer = new JmlAstDeterminizerVisitor();

        JCompilationUnitType simpleDeterminizedUnitType = simpleUnits.remove(0);

        JCompilationUnitType dUnitType = null;

        JCompilationUnitType thenUnit = null;
        JCompilationUnitType elseUnit = null;

//        Queue<JCompilationUnitType> theDeterminizedUnitTypeList = new ArrayList<>();

        Queue<JCompilationUnitType> problems = new LinkedList<JCompilationUnitType>();
        Queue<JCompilationUnitType> newProblems = new LinkedList<JCompilationUnitType>();
        problems.offer(simpleDeterminizedUnitType);

        boolean somethingWasSplit = false;
        while (problems.size() + newProblems.size() < size) {
            
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
                newProblems = new LinkedList<JCompilationUnitType>();
                somethingWasSplit = false;
            }
        }

        problems.addAll(newProblems);

        return problems;

    }

    /**
     *
     */
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

    public static String editTestFileToCompile(String junitFile, String sourceClassName, String classPackage, String methodName) {
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

    private List<JCompilationUnitType> parse_simplified_compilation_units(List<String> files) {
        String canonical_outdir_path;
        try {
            File output_dir = new File(TacoConfigurator.getInstance().getOutputDir());
            canonical_outdir_path = output_dir.getCanonicalPath();
        } catch (IOException e) {
            throw new TacoException("canonical path couldn't be computed " + e.getMessage());
        }

        JmlParser theParserInstance = JmlParser.getInstance();
        theParserInstance.initialize(canonical_outdir_path, System.getProperty("user.dir") + System.getProperty("file.separator") + "bin" /* Unused */,
                files);

        return theParserInstance.getCompilationUnits();

    }

    private List<String> write_simplified_compilation_units(List<JCompilationUnitType> newAsts) {
        List<String> files = new LinkedList<String>();
        String canonical_path = makeCanonicalPath();

        for (JCompilationUnitType compilation_unit : newAsts) {
            assert compilation_unit.typeDeclarations().length == 1;
            JTypeDeclarationType typeDeclaration = compilation_unit.typeDeclarations()[0];
            String filename = canonical_path + java.io.File.separator + typeDeclaration.getCClass().getJavaName().replaceAll("\\.", "/");
            files.add(typeDeclaration.getCClass().getJavaName());
            try {
                FileUtils.writeToFile(filename + OUTPUT_SIMPLIFIED_JAVA_EXTENSION, JavaAndJmlPrettyPrint2.print(compilation_unit));
            } catch (IOException e) {
                throw new RuntimeException("DYNJALLOY ERROR! " + e.getMessage());
            }
        }
        return files;
    }

    private String makeCanonicalPath() {
        String output_dir = TacoConfigurator.getInstance().getOutputDir();
        File out_dir_dir = new File(output_dir);

        if (!out_dir_dir.exists()) {
            out_dir_dir.mkdirs();
        }

        String canonical_path;
        try {
            canonical_path = out_dir_dir.getCanonicalPath();
        } catch (IOException e1) {
            throw new TacoException("path couldn't be found: " + out_dir_dir);
        }
        return canonical_path;
    }
}


