package ar.edu.taco.utils.threads;

import ar.edu.jdynalloy.ast.JDynAlloyModule;
import ar.edu.taco.TacoAnalysisResult;
import ar.edu.taco.TacoConfigurator;
import ar.edu.taco.TacoException;
import ar.edu.taco.TacoMain;
import ar.edu.taco.engine.*;
import ar.edu.taco.jfsl.JfslStage;
import ar.edu.taco.jml.JmlToSimpleJmlContext;
import ar.edu.taco.junit.RecoveredInformation;
import ar.edu.taco.stryker.api.impl.MuJavaController;
import ar.edu.taco.utils.FileUtils;
import ar.uba.dc.rfm.alloy.AlloyTyping;
import ar.uba.dc.rfm.alloy.ast.formulas.AlloyFormula;
import ar.uba.dc.rfm.dynalloy.DynAlloyCompiler;
import ar.uba.dc.rfm.dynalloy.analyzer.AlloyAnalysisResult;
import ar.uba.dc.rfm.dynalloy.ast.DynalloyModule;
import ar.uba.dc.rfm.dynalloy.ast.ProgramDeclaration;
import org.apache.log4j.Logger;
import org.multijava.mjc.JCompilationUnitType;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.DigestOutputStream;
import java.security.MessageDigest;
import java.util.*;
import java.util.concurrent.Callable;

import ar.edu.taco.simplejml.SimpleJmlToJDynAlloyContext;

public class TranslateThread implements Callable<TacoAnalysisResult> {
    JCompilationUnitType JUnit;
    SimpleJmlToJDynAlloyContext simpleJmlToJDynAlloyContext;
    Object inputToFix;
    JmlToSimpleJmlContext jmlToSimpleJmlContext;
    Properties overridingProperties;
    Logger log;
    TacoAnalysisResult translatedAnalysisResult;
    List<JCompilationUnitType> compilation_units;
    String classToCheck;
    String methodToCheck;
    String sourceRootDir;
    String configFile;
    String FILE_SEP;
    public TranslateThread(
            JCompilationUnitType simpleUnit, JmlToSimpleJmlContext jmlSimpleContext,
            Properties tacoProperties, Logger tacoLog, TacoAnalysisResult tacoResult,
            Object tacoInputFix, List<JCompilationUnitType> tacoCompUnit, String tacoClassToCheck,
            String tacoMethodToCheck, String tacoSourceRootDir, String tacoConfigFile,
            String tacoFILE_SEP){

        this.JUnit = simpleUnit;
        this.jmlToSimpleJmlContext = jmlSimpleContext;
        this.overridingProperties = tacoProperties;
        this.log = tacoLog;
        this.translatedAnalysisResult = tacoResult;
        this.inputToFix = tacoInputFix;
        this.compilation_units = tacoCompUnit;
        this.classToCheck = tacoClassToCheck;
        this.methodToCheck = tacoMethodToCheck;
        this.sourceRootDir = tacoSourceRootDir;
        this.configFile = tacoConfigFile;
        this.FILE_SEP = tacoFILE_SEP;
    }

    @Override
    public TacoAnalysisResult call() throws Exception {
        System.out.println("Thread Start: " + Thread.currentThread().getName());
        List<String> fileNames = null;
        JCompilationUnitType units = null;

        List<JCompilationUnitType>simplified_compilation_units = new ArrayList<JCompilationUnitType>();
        List<JCompilationUnitType> theDeterminizedUnitTypeList = new ArrayList<JCompilationUnitType>();
        theDeterminizedUnitTypeList.add(JUnit);
        fileNames = TacoMain.write_simplified_compilation_units(theDeterminizedUnitTypeList);
        units = TacoMain.parse_simplified_compilation_units(fileNames).remove(0);
        simplified_compilation_units.add(units);

        System.out.println("test1");
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


        translatedAnalysisResult = new TacoAnalysisResult(alloy_analysis_result);

        String junitFile = null;

        if (TacoConfigurator.getInstance().getGenerateUnitTestCase() || TacoConfigurator.getInstance().getAttemptToCorrectBug()) {
            // Begin JUNIT Generation Stage
            if (translatedAnalysisResult.get_alloy_analysis_result().isSAT())
                System.out.println("JUnit generation: started");

            SnapshotStage snapshotStage = new SnapshotStage(compilation_units, translatedAnalysisResult, classToCheck, methodToCheck);
            try {
                snapshotStage.execute();
                RecoveredInformation recoveredInformation = snapshotStage.getRecoveredInformation();
                recoveredInformation.setFileNameSuffix(StrykerStage.fileSuffix);
                JUnitStage jUnitStage = new JUnitStage(recoveredInformation);
                jUnitStage.execute();
                junitFile = jUnitStage.getJunitFileName();
                if (translatedAnalysisResult.get_alloy_analysis_result().isSAT())
                    System.out.println("         ... and finished.");

            } catch (TacoException e) {
                System.out.println("");
                System.out.println(e.getMessage());
            }
            // End JUNIT Generation Stage
        } else {
            log.info("****** JUnit with counterexample values will not be generated. ******* ");
            if (translatedAnalysisResult.get_alloy_analysis_result().isSAT())
                System.out.println("JUnit generation: skipped even though a bug/execution exists");
            if (!TacoConfigurator.getInstance().getGenerateUnitTestCase()) {
                log.info("****** generateUnitTestCase=false ******* ");
            }

        }

        if (TacoConfigurator.getInstance().getBuildJavaTrace()) {
            if (translatedAnalysisResult.get_alloy_analysis_result().isSAT()) {
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
            if (translatedAnalysisResult.get_alloy_analysis_result().isSAT() &&
                    translatedAnalysisResult.get_alloy_analysis_result().getAlloy_solution().getOriginalCommand().startsWith("Check")) {
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
                    currentJunit = TacoMain.editTestFileToCompile(junitFile, classToCheck, packageToWrite, methodToCheck);

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
                    Class<?> clazz = cl2.loadClass(packageToWrite + "." + TacoMain.obtainClassNameFromFileName(junitFile));
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
                    MuJavaController.MsgDigest msgDigest = new MuJavaController.MsgDigest(digest);
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
        //System.out.println("Subproblems Left : " + splitProblems.size());
        System.out.println("Thread Finish: " + Thread.currentThread().getName());
        return translatedAnalysisResult;
    }
}
