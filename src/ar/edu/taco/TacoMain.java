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
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.Charset;
import java.security.DigestOutputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.Attributes.Name;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import ar.edu.taco.engine.*;
import ar.edu.taco.stryker.MethodToCheck;
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
import ar.edu.jdynalloy.ast.JDynAlloyModule;
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
 * <p>The configuration options are read from the configuration file whose
 * name is passed to <code>ar.edu.taco.TacoMain.run</code>.
 * Those configurations can be overridden by the second argument of
 * <code>ar.edu.taco.TacoMain.run(String, Properties)</code>.</p>
 * <h3>Pipeline</h3>
 * <p>{@link #run(String, Properties)} translates the class under analysis in
 * stages: Java and JML parsing, JML simplification, translation to JDynAlloy,
 * JDynAlloy to DynAlloy, DynAlloy to Alloy and the Alloy analysis. A SAT
 * result of a check is a counterexample to the method's contract. Optionally
 * the counterexample is turned into a JUnit test and, when
 * <code>attemptToCorrectBug</code> is set, the Stryker repair engine
 * ({@link StrykerStage}) searches for a fix. While searching, Stryker calls
 * <code>run</code> again, with <code>attemptToCorrectBug=false</code>, to
 * verify fix candidates and to compute feedback.</p>
 * <p>A <code>TacoMain</code> created with a non-null <code>inputToFix</code>
 * map analyses the method with its input fixed to those values. Stryker's
 * {@link ar.edu.taco.stryker.api.impl.DarwinistController} does so with a
 * stored counterexample, for its feedback: {@link PrecompiledModules} adds the
 * literals of the input's values, {@link JDynAlloyStage} turns the input into
 * the precondition, and {@link DynalloyStage} translates the check for Stryker.
 * <code>checkAndRunSpecIfFaulty</code> (<code>isCheckAndRunSpecAssertionSupport</code>)
 * also passes one, to compute the spec's output for a hardcoded input; then
 * PrecompiledModules and DynalloyStage do not apply Stryker's translation, and
 * the JUnit step writes tests for the initial and the final state.</p>
 * <h3>Environment</h3>
 * <p>This TACO runs on Java 8, and Stryker also on Java 21. A Stryker run
 * needs a Java 7 class library for the JML tools ({@link Java7ClassLibrary}),
 * and MiniSat 2.2 (<code>-Ddynalloy.solver=minisat</code>): with DynAlloy's
 * default solver, SAT4J, some checks of a fix do not finish. Relative paths
 * such as <code>config/</code>, <code>generated/</code> and
 * <code>output/</code>, and Stryker's <code>lib/stryker/jml4c.jar</code> and
 * report files, are resolved against the working directory
 * (<code>user.dir</code>), which must therefore be the repository root.
 * Stryker keeps run-wide state in singletons and static fields that are never
 * reset, so only one Stryker run per JVM is supported.</p>
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
 *
 */
public class TacoMain {

    private static Logger log = Logger.getLogger(TacoMain.class);

    private static final String CMD = "Taco";
    private static final String HEADER = "Taco static analysis tool.";
    private static final String FOOTER = "For questions and comments please write to jgaleotti AT dc DOT uba DOT ar";
    public static final String PATH_SEP = System.getProperty("path.separator");
    public static final String FILE_SEP = System.getProperty("file.separator");

    public static final String OUTPUT_SIMPLIFIED_JAVA_EXTENSION = ".java";

    private Object inputToFix;

    public String outputJunitFile = "";

    private List<JCompilationUnitType> compilationUnits = new ArrayList<JCompilationUnitType>();

    public static boolean isCheckAndRunSpecAssertionSupport = false;

    /**
     * @param args
     */
    @SuppressWarnings({ "static-access" })
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

    public TacoMain(HashMap<String, Object> inputToFix){
        this.inputToFix = inputToFix;
    }

    /**
     * The compilation units parsed by the last {@link #run}, one per class it
     * translated. Sources are parsed one file at a time, so the JmlParser
     * singleton only keeps the last file's units afterwards; Stryker needs the
     * class under analysis among them to turn a counterexample into a test.
     */
    public List<JCompilationUnitType> getCompilationUnits() {
        return compilationUnits;
    }


    public void run(String configFile) throws IllegalArgumentException {
        this.run(configFile, new Properties());
    }

    /**
     * Runs one TACO analysis and, if requested and a bug is found, the Stryker repair.
     *
     * <p>Steps, in order:
     * <ol>
     * <li>Builds the configuration from {@code configFile} and
     *     {@code overridingProperties} ({@link JDynAlloyConfig#buildConfig}). With
     *     {@code attemptToCorrectBug}, checks that Stryker can run
     *     ({@link Java7ClassLibrary#checkForStryker()}) and throws
     *     {@link IllegalStateException} otherwise.</li>
     * <li>If {@code jmlParser.enabled} (default {@code true}): parses the class to check and the
     *     configured classes under {@code jmlParser.sourcePathStr}, one file at a time, returning
     *     {@code null} if one does not compile (the units are kept, see
     *     {@link #getCompilationUnits()}); then simplifies JML ({@link JmlStage}), translates to
     *     JDynAlloy ({@link SimpleJmlStage}, {@link JfslStage}) and prints the JDynAlloy
     *     modules.</li>
     * <li>Adds the built-in and static-field JDynAlloy modules ({@link PrecompiledModules}; for a
     *     Stryker feedback check, with the literals of the fixed input), optionally parses
     *     JDynAlloy files ({@code jdynalloy.parser.enabled}) and translates everything to
     *     DynAlloy ({@link JDynAlloyStage}).</li>
     * <li>If {@code dynalloy.toAlloy.enable}: translates to Alloy ({@link DynalloyStage}) and,
     *     unless {@code noVerify}, runs the Alloy analysis ({@link AlloyStage}).</li>
     * <li>If {@code generateUnitTestCase} or {@code attemptToCorrectBug}: without an
     *     {@code inputToFix}, turns the counterexample, if any, into a JUnit test under
     *     {@code generated/} ({@link SnapshotStage}, {@link JUnitStage}); with one (the
     *     computation of the spec's output for a hardcoded input), writes tests for the initial
     *     and the final state ({@link JUnitStageFinalState}). A {@link TacoException} there is
     *     printed and ignored.</li>
     * <li>If {@code buildJavaTrace} and the result is SAT: builds the Java trace.</li>
     * <li>If {@code attemptToCorrectBug} and the result is SAT for a command starting with
     *     {@code Check}: loads the JUnit counterexample into {@link StrykerStage#junitInputs}
     *     and runs {@link StrykerStage#execute()}, which blocks until the search ends. The
     *     outcome is only logged; the return value is still the original analysis.</li>
     * </ol>
     * The steps after the analysis assume that it ran: with {@code noVerify} or without
     * {@code dynalloy.toAlloy.enable}, the {@code isSAT()} call of the JUnit step throws a
     * {@link NullPointerException}.
     * 
     * @param configFile
     *            path of the TACO configuration file; must not be {@code null}
     * @param overridingProperties
     *            Properties that overrides properties file's values. For Stryker,
     *            {@code methodToCheck} must be given here, with its parameter types
     *            (e.g. {@code contains(java.lang.Object)}): the Stryker block reads it from these
     *            properties only.
     * @return the result of the Alloy analysis (of the original method, even if Stryker ran),
     *         or {@code null} if the sources do not compile
     * @throws IllegalArgumentException if {@code configFile} is {@code null}
     * @throws IllegalStateException if {@code attemptToCorrectBug} is on and Stryker cannot run
     *         on this JVM (see {@link Java7ClassLibrary#checkForStryker()})
     */

    @SuppressWarnings("unchecked")
    public TacoAnalysisResult run(String configFile, Properties overridingProperties) throws IllegalArgumentException {
        if (configFile == null) {
            throw new IllegalArgumentException("Config file not found, please verify option -cf");
        }

        List<JCompilationUnitType> compilation_units = new ArrayList<>();
        this.compilationUnits = compilation_units;
        String classToCheck = null;
        String methodToCheck = overridingProperties.getProperty(TacoConfigurator.METHOD_TO_CHECK_FIELD);

        // Start configurator
        JDynAlloyConfig.reset();
        JDynAlloyConfig.buildConfig(configFile, overridingProperties);
        // Without a Java 7 class library JML4C compiles no mutant and the repair never ends.
        if (TacoConfigurator.getInstance().getAttemptToCorrectBug()) {
            Java7ClassLibrary.checkForStryker();
        }

        List<JDynAlloyModule> jdynalloy_modules = new ArrayList<JDynAlloyModule>();
        SimpleJmlToJDynAlloyContext simpleJmlToJDynAlloyContext;
        if (TacoConfigurator.getInstance().getBoolean(TacoConfigurator.JMLPARSER_ENABLED, TacoConfigurator.JMLPARSER_ENABLED_DEFAULT)) {
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

            //BEGIN JAVA PARSING (one file at a time)
            for (String fileName : files) {
                ArrayList<String> aFile = new ArrayList<String>();
                aFile.add(fileName);
                String userDir = System.getProperty("user.dir") + System.getProperty("file.separator") + "bin";
                boolean compilationSuccess = JmlParser.getInstance().initialize(sourceRootDir, userDir /* Unused */, aFile);

                if (!compilationSuccess) {
                    return null; //this means compilation failed;
                }

                compilation_units.addAll(JmlParser.getInstance().getCompilationUnits());
            }
            // END JAVA PARSING

            // BEGIN SIMPLIFICATION
            JmlStage aJavaCodeSimplifier = new JmlStage(compilation_units);
            JmlStage.isCheckAndAfterRunSpec = isCheckAndRunSpecAssertionSupport;
            aJavaCodeSimplifier.execute();
            JmlToSimpleJmlContext jmlToSimpleJmlContext = aJavaCodeSimplifier.getJmlToSimpleJmlContext();
            List<JCompilationUnitType> simplified_compilation_units = aJavaCodeSimplifier.get_simplified_compilation_units();

            // END SIMPLIFICATION

            // BEGIN JAVA TO JDYNALLOY TRANSLATION
            // JDynAlloy modules have Alloy contracts and dynAlloy programs
            SimpleJmlStage aJavaToJDynAlloyTranslator = new SimpleJmlStage(simplified_compilation_units);
            //HERE IS WHERE THE PREDS AND VARS ARE PRODUCED
            if (isCheckAndRunSpecAssertionSupport && inputToFix != null){
                aJavaToJDynAlloyTranslator.setInputToFix(inputToFix);
            }
            aJavaToJDynAlloyTranslator.execute();
            //inputToFix = null;
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

            jdynalloy_modules.addAll(aJavaToJDynAlloyTranslator.getModules());

        } else {
            simpleJmlToJDynAlloyContext = null;
        }

        // JDYNALLOY BUILT-IN MODULES
        PrecompiledModules precompiledModules = null;
        // Stryker's feedback fixes the input to a counterexample; with Java arithmetic, the
        // literal modules of its int, long and float values have to be among the built-in
        // modules. The computation of the spec's output for a hardcoded input
        // (isCheckAndRunSpecAssertionSupport) also passes an inputToFix, and does not use them.
        if (this.inputToFix != null && !isCheckAndRunSpecAssertionSupport){
            precompiledModules = new PrecompiledModules((HashMap<String, Object>)inputToFix);
        } else {
            precompiledModules = new PrecompiledModules();
        }
        precompiledModules.execute();
        jdynalloy_modules.addAll(precompiledModules.getModules());
        // END JDYNALLOY BUILT-IN MODULES

        // JDYNALLOY STATIC FIELDS CLASS
        JDynAlloyModule staticFieldsModule = precompiledModules.generateStaticFieldsModule();
        jdynalloy_modules.add(staticFieldsModule);
        /**/	staticFieldsModule = null;
        // END JDYNALLOY STATIC FIELDS CLASS

        // JDYNALLOY PARSING
        if (TacoConfigurator.getInstance().getBoolean(TacoConfigurator.JDYNALLOY_PARSER_ENABLED, TacoConfigurator.JDYNALLOY_PARSER_ENABLED_DEFAULT)) {
            log.info("****** START: Parsing JDynAlloy files ****** ");
            JDynAlloyParsingStage jDynAlloyParser = new JDynAlloyParsingStage(jdynalloy_modules);
            jDynAlloyParser.execute();
            jdynalloy_modules.addAll(jDynAlloyParser.getParsedModules());
            /**/		jDynAlloyParser = null;
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

        HashMap<String, AlloyTyping> varsAndTheirTypesComingFromArithmeticConstraintsInObjectInvariantsByModule = new HashMap<String,AlloyTyping>();
        HashMap<String, List<AlloyFormula>> predsComingFromArithmeticConstraintsInObjectInvariantsByModule = new HashMap<String, List<AlloyFormula>> ();

        for (DynalloyModule dm : dynJAlloyToDynAlloyTranslator.getGeneratedModules()){
            String modName = dm.getModuleId();
            varsAndTheirTypesComingFromArithmeticConstraintsInObjectInvariantsByModule.put(modName, dm.getVarsComingFromArithmeticConstraintsInObjectInvariants());
            predsComingFromArithmeticConstraintsInObjectInvariantsByModule.put(modName, dm.getPredsComingFromArithmeticConstraintsInObjectInvariants());
            Set<ProgramDeclaration> progs = dm.getPrograms();
            for (ProgramDeclaration pd : progs){
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
            DynalloyStage.isCheckAndAfterRunSpec = TacoMain.isCheckAndRunSpecAssertionSupport;
            dynalloyToAlloy.execute();
            // DYNALLOY TO ALLOY TRANSLATION

            log.info("****** Transformation process finished ****** ");

            if (TacoConfigurator.getInstance().getNoVerify() == false) {
                // Starts dynalloy to alloy tranlation and alloy verification

                AlloyStage alloy_stage = new AlloyStage(dynalloyToAlloy.get_alloy_filename());
                dynalloyToAlloy = null;
                alloy_stage.execute();

                alloy_analysis_result = alloy_stage.get_analysis_result();
                /**/			alloy_stage = null;
            }
        }

        TacoAnalysisResult tacoAnalysisResult = new TacoAnalysisResult(alloy_analysis_result);

        String junitFile = null;

        if (TacoConfigurator.getInstance().getGenerateUnitTestCase() || TacoConfigurator.getInstance().getAttemptToCorrectBug()) {
            // Begin JUNIT Generation Stage
            if (tacoAnalysisResult.get_alloy_analysis_result().isSAT() && inputToFix == null) {
                System.out.println("JUnit input generation: started");
                try {
                    SnapshotStage snapshotStage = new SnapshotStage(compilation_units, tacoAnalysisResult, classToCheck, methodToCheck);
                    snapshotStage.setInputToFix(null);
                    snapshotStage.execute();
                    RecoveredInformation recoveredInformation = snapshotStage.getRecoveredInformation();

                    recoveredInformation.setFileNameSuffix(StrykerStage.fileSuffix);
                    JUnitStage jUnitStage = new JUnitStage(recoveredInformation);
                    jUnitStage.execute();
                    junitFile = jUnitStage.getJunitFileName();

                    outputJunitFile = junitFile;

                    if (tacoAnalysisResult.get_alloy_analysis_result().isSAT())
                        System.out.println("         ... and finished.");

                } catch (TacoException e){
                    System.out.println("");
                    System.out.println(e.getMessage());
                }
            } else if (inputToFix != null) {
                System.out.println("JUnit spec output generation: started");
                try {

                    SnapshotStage snapshotStageIni = new SnapshotStage(compilation_units, tacoAnalysisResult, classToCheck, methodToCheck);
                    snapshotStageIni.setInputToFix(null);
                    snapshotStageIni.execute();
                    RecoveredInformation recoveredInformationIni = snapshotStageIni.getRecoveredInformation();
                    recoveredInformationIni.setFileNameSuffix(StrykerStage.fileSuffix);
                    JUnitStage jUnitStage = new JUnitStage(recoveredInformationIni);
                    jUnitStage.execute();
                    junitFile = jUnitStage.getJunitFileName();
                    outputJunitFile = junitFile;

                    SnapshotStage snapshotStageFin = new SnapshotStage(compilation_units, tacoAnalysisResult, classToCheck, methodToCheck);
                    snapshotStageFin.setInputToFix(inputToFix);
                    snapshotStageFin.execute();
                    RecoveredInformation recoveredInformationFin = snapshotStageFin.getRecoveredInformation();
                    recoveredInformationFin.setFileNameSuffix(StrykerStage.fileSuffix);
                    JUnitStageFinalState jUnitStageFinalState = new JUnitStageFinalState(recoveredInformationFin);
                    jUnitStageFinalState.execute();
                    junitFile = jUnitStageFinalState.getJunitFileName();
                    outputJunitFile = junitFile;

                    if (tacoAnalysisResult.get_alloy_analysis_result().isSAT())
                        System.out.println("         ... and finished.");

                } catch (TacoException e){
                    System.out.println("");
                    System.out.println(e.getMessage());
                }
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

        // STRYKER: only for a bug, i.e. a SAT result of a "Check" command.
        if (TacoConfigurator.getInstance().getAttemptToCorrectBug()) {
            if (tacoAnalysisResult.get_alloy_analysis_result().isSAT() &&
                    tacoAnalysisResult.get_alloy_analysis_result().getAlloy_solution().getOriginalCommand().startsWith("Check")) {
                log.info("****** START: Stryker ****** ");
                // Read from the overriding properties only (e.g. "contains(java.lang.Object)"); StrykerStage keeps the plain name.
                methodToCheck = overridingProperties.getProperty(TacoConfigurator.METHOD_TO_CHECK_FIELD);
                String sourceRootDir = TacoConfigurator.getInstance().getString(
                        TacoConfigurator.JMLPARSER_SOURCE_PATH_STR);
                StrykerStage strykerStage = new StrykerStage(compilation_units, sourceRootDir, classToCheck,
                        methodToCheck, configFile, overridingProperties,
                        TacoConfigurator.getInstance().getMaxStrykerMethodsForFile());
                // Counterexample pool shared with the Stryker controllers, with room for 50 tests.
                // indexToLastJUnitInput is not reset here.
                StrykerStage.junitInputs = new Class<?>[StrykerStage.MAX_COUNTEREXAMPLES];
                StrykerStage.junitFiles = new String[StrykerStage.MAX_COUNTEREXAMPLES];

                // Load the JUnit counterexample of this analysis into the pool: rewrite it into
                // output/ (package ar.edu.output.junit, see editTestFileToCompile), compile the copy
                // in place, load it from output/ and record the original file and its MD5 digest.
                try {
                    String currentJunit = null;

                    String tempFilename = junitFile.substring(0, junitFile.lastIndexOf(FILE_SEP)+1) /*+ FILE_SEP*/;
                    String packageToWrite = "ar.edu.output.junit";
                    String fileClasspath = tempFilename.substring(0, tempFilename.lastIndexOf(new String("ar.edu.generated.junit").replaceAll("\\.", FILE_SEP)));
                    fileClasspath = replaceBelowWorkingDir(fileClasspath, "generated", "output", false);
                    //					String currentClasspath = System.getProperty("java.class.path")+PATH_SEP+fileClasspath/*+PATH_SEP+System.getProperty("user.dir")+FILE_SEP+"generated"*/;
                    currentJunit = editTestFileToCompile(junitFile, classToCheck, packageToWrite, methodToCheck);

                    File[] file1 = new File[]{new File(currentJunit)};
                    JavaCompiler javaCompiler = ToolProvider.getSystemJavaCompiler();
                    StandardJavaFileManager fileManager = javaCompiler.getStandardFileManager(null, null, null);
                    Iterable<? extends JavaFileObject> compilationUnit1 =
                            fileManager.getJavaFileObjectsFromFiles(Arrays.asList(file1));
                    Boolean compiled = javaCompiler.getTask(null, fileManager, null, null, null, compilationUnit1).call();
                    fileManager.close();
                    javaCompiler = null;
                    file1 = null;
                    fileManager = null;

                    ///*mfrias*/		int compilationResult =	javaCompiler.run(null, null, null /*new NullOutputStream()*/, new String[]{"-classpath", currentClasspath, currentJunit});
                    ///**/				javaCompiler = null;
                    //					if(compilationResult == 0) {
                    if (Boolean.TRUE.equals(compiled)) {
                        log.warn("junit counterexample compilation succeded");
                    } else {
                        log.error("The counterexample test " + currentJunit + " does not compile, so the counterexample is not added to the pool. See the compiler errors above.");
                    }
                    ClassLoader cl = ClassLoader.getSystemClassLoader();
                    @SuppressWarnings("resource")
                    ClassLoader cl2 = new URLClassLoader(new URL[]{new File(fileClasspath).toURI().toURL()}, cl);
                    //						ClassLoaderTools.addFile(fileClasspath);
                    Class<?> clazz = cl2.loadClass(packageToWrite+"."+obtainClassNameFromFileName(junitFile));
                    //						Method[] meth = clazz.getMethods();
                    //						log.info("preparing to add a class containing a test input to the pool... "+packageToWrite+"."+MuJavaController.obtainClassNameFromFileName(junitFile));
                    //						Result result = null;
                    //						final Object oToRun = clazz.newInstance();
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

                // Failures above are swallowed (and the javac result is not checked): Stryker runs
                // anyway, but with an empty pool OpenJMLController attempts no counterexample, so no
                // mutant ever reaches DarwinistController. Blocks until the search ends.
                strykerStage.execute();

                log.info("****** FINISH: Stryker ****** ");
            }
        } else {
            log.info("****** BugFix will not be generated. ******* ");
            log.info("****** attemptToCorrectBug=false ******* ");
        }

        return tacoAnalysisResult;
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

    /**
     * Replaces {@code target} with {@code replacement} only in the part of {@code path} below
     * the working directory (the repository root). Counterexample tests live under
     * {@code generated/} and their adapted copies under {@code output/}; rewriting the whole
     * absolute path broke every run whose repository path itself contained "generated".
     * Paths outside the working directory are rewritten as a whole, as before.
     *
     * @param all replace every occurrence, or only the first one
     */
    public static String replaceBelowWorkingDir(String path, String target, String replacement, boolean all) {
        String root = System.getProperty("user.dir");
        String prefix = "";
        String rest = path;
        if (path.startsWith(root)) {
            prefix = root;
            rest = path.substring(root.length());
        }
        if (all) {
            return prefix + rest.replace(target, replacement);
        }
        int index = rest.indexOf(target);
        if (index < 0) {
            return path;
        }
        return prefix + rest.substring(0, index) + replacement + rest.substring(index + target.length());
    }

    /**
     * Rewrites a generated JUnit counterexample so that it can run against any compiled
     * version of the class under test, in particular Stryker's JML4C-compiled mutant batches.
     * Used by {@link #run(String, Properties)} for the first counterexample and by
     * {@link ar.edu.taco.stryker.api.impl.DarwinistController} for later ones.
     *
     * <p>The copy keeps the file name and goes to the original's directory with every
     * {@code generated} in the path replaced by {@code output}, e.g.
     * {@code generated/ar/edu/generated/junit/X.java} becomes
     * {@code output/ar/edu/output/junit/X.java} (directories are created if missing). Line by
     * line, the rewrite:
     * <ul>
     * <li>replaces the package declaration with {@code package <classPackage>;} plus the
     *     imports the new code needs;</li>
     * <li>changes the {@code @Test} method to take
     *     {@code (String fileClasspath, String className, String methodName)} and to declare
     *     the reflection exceptions;</li>
     * <li>after the {@code //endGetInstance} marker, replaces the first
     *     {@code new <sourceClassName>(...)} with code that loads {@code className} through a
     *     new {@link URLClassLoader} over the {@code fileClasspath} entries and instantiates it
     *     with its first declared constructor and default arguments ({@code false}, zero or
     *     {@code null}); later such lines reuse that constructor;</li>
     * <li>makes the reflective lookup helpers take the loaded {@code Class<?>} instead of a
     *     class name, and, on lines that mention the class under test, replaces the class and
     *     method name literals with {@code clazz} and {@code methodName};</li>
     * <li>replaces {@code e.printStackTrace();} with
     *     {@code throw(new java.lang.RuntimeException(e));}, so that an exception raised by the
     *     method under test, including a JML RAC error, propagates out of the test instead of
     *     being swallowed ({@link ar.edu.taco.stryker.api.impl.OpenJMLController} relies on
     *     this); widens {@code catch (ClassNotFoundException e)} to {@code catch (Exception e)};
     *     and clears {@code instance} and {@code method} after the invocation.</li>
     * </ul>
     * The rewrite depends on the exact layout written by
     * {@link ar.edu.taco.junit.UnitTestBuilder} and {@link ar.edu.taco.junit.JUnitPrettyPrinter}.
     * I/O errors are printed, not thrown.
     *
     * @param junitFile path of the generated test (under a {@code generated} directory)
     * @param sourceClassName the class under test as written in the test; callers pass the
     *        fully qualified {@code classToCheck}
     * @param classPackage package of the copy ({@code ar.edu.output.junit} in all callers)
     * @param methodName the {@code methodToCheck} (e.g. {@code contains(java.lang.Object)});
     *        its plain name ({@link MethodToCheck#methodName}) is what is matched
     * @return the path of the copy, returned even if writing it failed
     */
    public static String editTestFileToCompile(String junitFile, String sourceClassName, String classPackage, String methodName) {
        String tmpDir = junitFile.substring(0, junitFile.lastIndexOf(FILE_SEP));
        tmpDir = replaceBelowWorkingDir(tmpDir, "generated", "output", true);
        File destFile = new File(tmpDir,obtainClassNameFromFileName(junitFile)+ /*"_temp" +*/ ".java");
        String packageSentence = "package "+classPackage+";\n";
        methodName = MethodToCheck.methodName(methodName);
        try {
            // output/ is not versioned, so it does not exist in a fresh checkout.
            destFile.getParentFile().mkdirs();
            destFile.createNewFile();
            FileOutputStream fos = new FileOutputStream(destFile);
            boolean packageAlreadyWritten = false;
            Scanner scan = new Scanner(new File(junitFile));
            scan.useDelimiter("\n");
            boolean nextToTest = false;
            String str = null;
            boolean reachedSecondConstructorFromAnalyzedClass = false;
            boolean translatingGetInstance = true;
            while(scan.hasNext()){
                str = scan.next();
                if( nextToTest ) {
                    str = str.replace("()","(String fileClasspath, String className, String methodName) throws IllegalAccessException, InvocationTargetException, ClassNotFoundException, InstantiationException, MalformedURLException");
                    fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
                    nextToTest = false;
                    //				} else if (str.contains("public class")){
                    //					int posOpeningBrace = str.indexOf("{");
                    //					str = str.substring(0, posOpeningBrace-1);
                    //					str = str + "_temp {";
                    //					fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
                } else if( str.contains("package") && !packageAlreadyWritten){
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
                } else if (str.contains("new " + sourceClassName+"(") && !reachedSecondConstructorFromAnalyzedClass
                        && !translatingGetInstance){
                    //		          str = "        try {";
                    //		          fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
                    // The test method also declares the parameters of the method under test as locals
                    // (e.g. "int c = -6;" for IntroClass's median(int a, int b, int c)), so the locals
                    // introduced here carry a "stryker" prefix: a clash makes the test fail to compile
                    // and the counterexample is lost.
                    str = "           String[] strykerClasspaths = fileClasspath.split(System.getProperty(\"path.separator\"));";
                    fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
                    str = "           URL[] strykerUrls = new URL[strykerClasspaths.length];";
                    fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
                    str = "           for (int i = 0 ; i < strykerClasspaths.length ; ++i) {";
                    fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
                    str = "              strykerUrls[i] = new File(strykerClasspaths[i]).toURI().toURL();";
                    fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
                    str = "           }";
                    fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
                    str = "           ClassLoader strykerLoader = new URLClassLoader(strykerUrls);";
                    fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
                    //		          str = "           ClassLoaderTools.addFile(fileClasspath);";
                    //		          fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
                    str = "           Class<?> clazz = strykerLoader.loadClass(className);";
                    fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
                    str = "           Constructor<?>[] strykerConstructors = clazz.getDeclaredConstructors();";
                    fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
                    // Object, not the class under test: the test reaches the instance through
                    // reflection (updateValue, method.invoke), and assigning newInstance()'s result
                    // to a variable of the class type does not compile without a cast.
                    str = "           Object instance = null;";
                    fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
                    str = "           	Class<?>[] strykerParameterTypes = null;";
                    fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
                    str = "           	Object[] strykerParamValues = null;";
                    fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
                    str = "           	Constructor<?> strykerConstructor = null;";
                    fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
                    str = "           if (strykerConstructors.length > 0) {";
                    fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
                    str = "           	strykerConstructor = strykerConstructors[0];";
                    fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
                    str = "           	strykerConstructor.setAccessible(true);";
                    fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
                    str = "           	strykerParameterTypes = strykerConstructor.getParameterTypes();";
                    fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
                    str = "           	strykerParamValues = new Object[strykerConstructor.getParameterTypes().length];";
                    fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
                    str = "             for (int paramIndexer = 0; paramIndexer<strykerParameterTypes.length; paramIndexer++){";
                    fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
                    str = "           		if (strykerParameterTypes[paramIndexer].isPrimitive()){";
                    fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
                    str = "           			String typeSimpleName = strykerParameterTypes[paramIndexer].getSimpleName();";
                    fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
                    str = "           			if (typeSimpleName.equals(\"boolean\")) {";
                    fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
                    str = "                         strykerParamValues[paramIndexer] = false;";
                    fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
                    str = "                     } else if (typeSimpleName.endsWith(\"byte\")) {";
                    fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
                    str = "                       	strykerParamValues[paramIndexer] = 0;";
                    fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
                    str = "                     } else if (typeSimpleName.endsWith(\"char\")) {";
                    fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
                    str = "                       	strykerParamValues[paramIndexer] = 0;";
                    fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
                    str = "                     } else if (typeSimpleName.endsWith(\"double\")) {";
                    fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
                    str = "                       	strykerParamValues[paramIndexer] = 0.0d;";
                    fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
                    str = "                     } else if (typeSimpleName.endsWith(\"float\")) {";
                    fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
                    str = "                       	strykerParamValues[paramIndexer] = 0.0f;";
                    fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
                    str = "                     } else if (typeSimpleName.endsWith(\"int\")) {";
                    fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
                    str = "                       	strykerParamValues[paramIndexer] = 0;";
                    fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
                    str = "                     } else if (typeSimpleName.endsWith(\"long\")) {";
                    fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
                    str = "                       	strykerParamValues[paramIndexer] = 0L;";
                    fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
                    str = "                     } else if (typeSimpleName.endsWith(\"short\")) {";
                    fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
                    str = "                       	strykerParamValues[paramIndexer] = 0;";
                    fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
                    str = "                     } else {";
                    fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
                    str = "                         System.out.println(\"ERROR: Undefined primitive type.\");";
                    fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
                    str = "                     }";
                    fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
                    str = "           		} else {";
                    fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
                    str = "           			strykerParamValues[paramIndexer] = null;";
                    fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
                    str = "           		}";
                    fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
                    str = "           	}";
                    fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
                    str = "           	try {";
                    fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
                    str = "             	String dataCall = strykerConstructor.getName() + Arrays.toString(strykerParamValues);";
                    fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
//                    str = "             	System.out.println(dataCall);";
//                    fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
                    str = "           	    instance = strykerConstructor.newInstance(strykerParamValues);";
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
                } else if (str.contains("new " + sourceClassName+"(") && ! translatingGetInstance) {
                    String backup = str;
                    String objectName = backup.split("[ ]+")[2];
                    str = "             Object " + objectName + " = null;";
                    fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
                    str = "           	try {";
                    fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
                    str = "             	String dataCall = strykerConstructor.getName() + Arrays.toString(strykerParamValues);";
                    fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
//                    str = "             	System.out.println(dataCall);";
//                    fos.write((str + "\n").getBytes(Charset.forName("UTF-8")));
                    str = "           	   " + objectName + " = strykerConstructor.newInstance(strykerParamValues);";
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
                } else if( str.matches(".*(?i)[\\.a-z0-9\\_]*"+sourceClassName+"(?=[^a-z0-9\\_\\.]).*")){
                    str = str.replaceAll("(?i)[\\.a-z0-9\\_]*"+sourceClassName+"(?=[^a-z0-9\\_\\.])", /*classPackage+"."+*/sourceClassName);
                    str = str.replace("\""+methodName+"\"", "methodName");
                    str = str.replace("\""+sourceClassName+"\"", "clazz");
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

    public static final int NOT_PRESENT = -1;

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
}


