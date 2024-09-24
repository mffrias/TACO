package ar.edu.taco.utils;

import ar.edu.jdynalloy.ast.JDynAlloyModule;
import ar.edu.taco.TacoAnalysisResult;
import ar.edu.taco.TacoConfigurator;
import ar.edu.taco.TacoException;
import ar.edu.taco.TacoMain;
import ar.edu.taco.engine.*;
import ar.edu.taco.jfsl.JfslStage;
import ar.edu.taco.jml.JmlToSimpleJmlContext;
import ar.edu.taco.jml.parser.JmlParser;
//import ar.edu.taco.jml.JmlParser.TypeCheckerMain;
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
import org.jmlspecs.jmlrac.JavaAndJmlPrettyPrint2;
import org.multijava.mjc.Debug;
import org.multijava.mjc.JCompilationUnitType;
import org.multijava.mjc.JTypeDeclarationType;
//import org.multijava.mjc.Main.ExpectedGF;
//import org.multijava.mjc.Main.ExpectedIndifferent;
//import org.multijava.mjc.Main.ExpectedResult;
//import org.multijava.mjc.Main.ExpectedType;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.DigestOutputStream;
import java.security.MessageDigest;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;

import ar.edu.taco.simplejml.SimpleJmlToJDynAlloyContext;

public class TranslateCallable implements Callable<TacoAnalysisResult> {


    //  public JmlParser threadParserInstance = new JmlParser();
    //
    //	public TypeCheckerMain threadTypeCheckerMainInstance = threadParserInstance.new TypeCheckerMain();
    //
    //	public Debug threadDebug = new Debug();
    //
    //	public ExpectedIndifferent threadExpectedIndifferent = new org.multijava.mjc.Main().new ExpectedIndifferent(0);
    //
    //	public ExpectedResult threadExpectedResult = new org.multijava.mjc.Main().new ExpectedResult();
    //
    //	public ExpectedType threadExpectedType = new org.multijava.mjc.Main().new ExpectedType();
    //
    //	public ExpectedGF threadExpectedGF = new org.multijava.mjc.Main().new ExpectedGF();


    JCompilationUnitTypeWrapper JUnitWrapped;
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

    Semaphore semJmlParser;
    Semaphore semJava2JDyn;
    Semaphore semJDyn2Dyn;
    Semaphore semJUnitConstruction;

    ConcurrentLinkedQueue<Message> theSharedQueue;


    public TranslateCallable(Semaphore semJmlParser, Semaphore semJava2JDyn, Semaphore semJDyn2Dyn, JCompilationUnitTypeWrapper simpleUnit, JmlToSimpleJmlContext jmlSimpleContext,
                             Properties tacoProperties, Logger tacoLog, TacoAnalysisResult tacoResult,
                             Object tacoInputFix, String tacoClassToCheck,
                             String tacoMethodToCheck, String tacoSourceRootDir, String tacoConfigFile,
                             String tacoFILE_SEP) {

        this.JUnitWrapped = simpleUnit;
        this.jmlToSimpleJmlContext = jmlSimpleContext;
        this.overridingProperties = tacoProperties;
        this.log = tacoLog;
        this.translatedAnalysisResult = tacoResult;
        this.inputToFix = tacoInputFix;
        this.classToCheck = tacoClassToCheck;
        this.methodToCheck = tacoMethodToCheck;
        this.sourceRootDir = tacoSourceRootDir;
        this.configFile = tacoConfigFile;
        this.FILE_SEP = tacoFILE_SEP;
        this.semJmlParser = semJmlParser;
        this.semJava2JDyn = semJava2JDyn;
        this.semJDyn2Dyn = semJDyn2Dyn;
        this.semJUnitConstruction = semJUnitConstruction;
    }

    @Override
    public TacoAnalysisResult call() throws Exception {

//        JCompilationUnitType JUnit = null;
//        List<JCompilationUnitType> theJUnits = this.JUnitWrapped.theUnit;
//
//        for (JCompilationUnitType unit : theJUnits){
//            if (unit.declaresType(TacoConfigurator.getInstance().getString(TacoConfigurator.CLASS_TO_CHECK_FIELD))){
//                JUnit = unit;
//            }
//        }
//
//        TacoAnalysisResult translatedAnalysisResult = new TacoAnalysisResult();
//        translatedAnalysisResult.setCompilationUnit(JUnit);
//
////        String fileNameIdent = JUnit.fileNameIdent();
//
        List<String> fileNames = null;
        List<JCompilationUnitType> units = null;

        List<JCompilationUnitType> simplified_compilation_units = new ArrayList<JCompilationUnitType>();
        List<JCompilationUnitType> theDeterminizedUnitTypeList = this.JUnitWrapped.theUnit;

        try {
            semJmlParser.acquire();
            fileNames = write_simplified_compilation_units(theDeterminizedUnitTypeList);

            //This index 0 that was used below (removed now) strongly depends on the order the user lists the typed in the
            //driver for the analysis. Needs to be improved.

            units = parse_simplified_compilation_units(fileNames);
        } catch (InterruptedException exc) {
            System.out.println(exc);
        } finally {
            semJmlParser.release();
        }
        //  System.out.println("!!Parse Simplified Compilation Units Complete for thread: " + Thread.currentThread().getName());
        simplified_compilation_units.addAll(units);

        // BEGIN JAVA TO JDYNALLOY TRANSLATION
        //  System.out.println("<-----Beginning JDynaAlloy Translation----->");

        // JDynAlloy modules have Alloy contracts and dynAlloy programs

        SimpleJmlStage aJavaToJDynAlloyTranslator = new SimpleJmlStage(simplified_compilation_units);
        //HERE IS WHERE THE PREDS AND VARS ARE PRODUCED
        try {
            semJava2JDyn.acquire();
            aJavaToJDynAlloyTranslator.execute();
        } catch (InterruptedException exc) {
            System.out.println(exc);
        } finally {
            semJava2JDyn.release();
        }
        // END JAVA TO JDYNALLOY TRANSLATION
        //  System.out.println("<>-----End JDynaAlloy Translation-----");

        simpleJmlToJDynAlloyContext = aJavaToJDynAlloyTranslator.getSimpleJmlToJDynAlloyContext();

        // JFSL TO JDYNALLOY TRANSLATION
        //  System.out.println("<-----Beginning JFSL Translation----->");

        JfslStage aJfslToDynJAlloyTranslator = new JfslStage(simplified_compilation_units, aJavaToJDynAlloyTranslator.getModules(), jmlToSimpleJmlContext,
                simpleJmlToJDynAlloyContext);


        aJfslToDynJAlloyTranslator.execute();


        aJfslToDynJAlloyTranslator = null;

        //  System.out.println("<-----End JFSL Translation----->");

        // END JFSL TO JDYNALLOY TRANSLATION

        // PRINT JDYNALLOY

        //  System.out.println("<-----Print JDynaAlloy Translation----->");

        JDynAlloyPrinterStage printerStage = new JDynAlloyPrinterStage(aJavaToJDynAlloyTranslator.getModules());

        printerStage.execute();

        printerStage = null;


        // END PRINT JDYNALLOY

        //  System.out.println("<-----End Print JDynaAlloy Translation----->");

        List<JDynAlloyModule> jdynalloy_modules = new ArrayList<JDynAlloyModule>();
        jdynalloy_modules.addAll(aJavaToJDynAlloyTranslator.getModules());

        //        } else {
        //            simpleJmlToJDynAlloyContext = null;
        //        }

        // JDYNALLOY BUILT-IN MODULES
        //  System.out.println("<-----Begin JDynaAllow built-in modules----->");

        PrecompiledModules precompiledModules = null;
        if (this.inputToFix != null) {
            precompiledModules = new PrecompiledModules((HashMap<String, Object>) inputToFix);
        } else {
            precompiledModules = new PrecompiledModules();
        }
        precompiledModules.execute();
        jdynalloy_modules.addAll(precompiledModules.getModules());
        //  System.out.println("<-----End JDynaAlloy built-in modules----->");
        // END JDYNALLOY BUILT-IN MODULES

        // JDYNALLOY STATIC FIELDS CLASS
        //  System.out.println("<-----Begin JDynaAlloy static fields class----->");

        JDynAlloyModule staticFieldsModule = precompiledModules.generateStaticFieldsModule();
        jdynalloy_modules.add(staticFieldsModule);
        /**/
        staticFieldsModule = null;


        //  System.out.println("<-----End JDynaAlloy static fields----->");

        // END JDYNALLOY STATIC FIELDS CLASS

        // JDYNALLOY PARSING
        //  System.out.println("<-----Begin JDynaAlloy parsing----->");

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


        //  System.out.println("<-----End JDynaAlloy parsing----->");
        // END JDYNALLOY PARSING

        // BEGIN JDYNALLOY TO DYNALLOY TRANSLATION
        //  System.out.println("<-----Begin DynAlloy translation----->");

        String methodToCheckWithoutTyping = overridingProperties.getProperty("methodToCheck").substring(0, overridingProperties.getProperty("methodToCheck").indexOf('('));
        JDynAlloyStage jDynAlloyToDynAlloyTranslator = new JDynAlloyStage(jdynalloy_modules, overridingProperties.getProperty("classToCheck"), methodToCheckWithoutTyping, inputToFix);
        jDynAlloyToDynAlloyTranslator.setJavaArithmetic(TacoConfigurator.getInstance().getUseJavaArithmetic());
        jDynAlloyToDynAlloyTranslator.setRemoveQuantifiers(TacoConfigurator.getInstance().getRemoveQuantifiers());


        HashMap<String, AlloyTyping> varsAndTheirTypesComingFromArithmeticConstraintsInContractsByProgram = new HashMap<String, AlloyTyping>();
        HashMap<String, List<AlloyFormula>> predsComingFromArithmeticConstraintsInContractsByProgram = new HashMap<String, List<AlloyFormula>>();

        HashMap<String, AlloyTyping> varsAndTheirTypesComingFromArithmeticConstraintsInObjectInvariantsByModule = new HashMap<String, AlloyTyping>();
        HashMap<String, List<AlloyFormula>> predsComingFromArithmeticConstraintsInObjectInvariantsByModule = new HashMap<String, List<AlloyFormula>>();

        try {
            semJDyn2Dyn.acquire();
            jDynAlloyToDynAlloyTranslator.execute();
        } catch (InterruptedException exc) {
            System.out.println(exc);
        } finally {
            semJDyn2Dyn.release();
        }


        //System.out.println("<-----End DynAlloy translation----->");
        // END JDYNALLOY TO DYNALLOY TRANSLATION


        // GRAB PREDICATES COMING FROM ARITHMETIC EXPRESSIONS

        for (DynalloyModule dm : jDynAlloyToDynAlloyTranslator.getGeneratedModules()) {
            String modName = dm.getModuleId();
            varsAndTheirTypesComingFromArithmeticConstraintsInObjectInvariantsByModule.put(modName, dm.getVarsComingFromArithmeticConstraintsInObjectInvariants());
            predsComingFromArithmeticConstraintsInObjectInvariantsByModule.put(modName, dm.getPredsComingFromArithmeticConstraintsInObjectInvariants());
            Set<ProgramDeclaration> progs = dm.getPrograms();
            for (ProgramDeclaration pd : progs) {
                varsAndTheirTypesComingFromArithmeticConstraintsInContractsByProgram.put(pd.getProgramId(), pd.getVarsFromArithInContracts());
                predsComingFromArithmeticConstraintsInContractsByProgram.put(pd.getProgramId(), pd.getPredsFromArithInContracts());
            }
        }

        AlloyAnalysisResult alloy_analysis_result = null;
        DynalloyStage dynalloyToAlloy = null;


        // DYNALLOY TO ALLOY TRANSLATION
        //  System.out.println("<-----Begin Alloy translation----->");
        if (TacoConfigurator.getInstance().getBoolean(TacoConfigurator.DYNALLOY_TO_ALLOY_ENABLE)) {

            dynalloyToAlloy = new DynalloyStage(jDynAlloyToDynAlloyTranslator.getOutputFileNames(),
                    varsAndTheirTypesComingFromArithmeticConstraintsInObjectInvariantsByModule,
                    predsComingFromArithmeticConstraintsInObjectInvariantsByModule,
                    varsAndTheirTypesComingFromArithmeticConstraintsInContractsByProgram,
                    predsComingFromArithmeticConstraintsInContractsByProgram, inputToFix);

            dynalloyToAlloy.setSourceJDynAlloy(jDynAlloyToDynAlloyTranslator.getPrunedModules());


            dynalloyToAlloy.execute();


            // DYNALLOY TO ALLOY TRANSLATION

            //  System.out.println("<-----End Alloy translation----->");

            log.info("****** Transformation process finished ****** ");

            if (TacoConfigurator.getInstance().getNoVerify() == false) {
                // Starts dynalloy to alloy tranlation and alloy verification

                AlloyStage alloy_stage = new AlloyStage(dynalloyToAlloy.get_alloy_filename());
                dynalloyToAlloy = null;

                long initTime = System.nanoTime();


                //				try {
                //					sem.acquire();
                //				alloy_stage.execute();
                //				} catch (InterruptedException exc) {
                //					System.out.println(exc);
                //				} finally {
                //					sem.release();
                //				}

                String fileToAnalyze = makeCanonicalPath() + "/output.als";
                String fileNameToLookForSAT = fileToAnalyze.replace("output.als", "verdictSAT.txt");
                String fileNameToLookForUNSAT = fileToAnalyze.replace("output.als", "verdictUNSAT.txt");
                File fileToLookForSAT = new File(fileNameToLookForSAT);
                File fileToLookForUNSAT = new File(fileNameToLookForUNSAT);

                ProcessBuilder pb = new ProcessBuilder();
                pb.redirectErrorStream(true);
                pb.command("/usr/bin/java", "-Xss300m", "-jar", "/Users/mfrias/eclipse-workspace-new/FreshTACO1/TACO/lib/alloyRunner.jar", fileToAnalyze);


                try {
                    long initTimeMillis = System.currentTimeMillis();
                    Process process = pb.start();

                    String pId = Thread.currentThread().getName().substring(5, Thread.currentThread().getName().length() - 9);
                    String action = "start";
                    boolean isDeterminized = JUnitWrapped.getDeterminized();
                    int TOinSecs = JUnitWrapped.getTimeout();
                    String startContent = String.format("pid   %1$6s      stat %2$7s    det %3$5s      to: %4$5d", pId, action, isDeterminized, TOinSecs);
                    System.out.println(startContent);


//					BufferedReader reader =
//							new BufferedReader(new InputStreamReader(process.getInputStream()));
//					StringBuilder builder = new StringBuilder();
//					String line = null;


                    while ((System.currentTimeMillis() - initTimeMillis) / 1000 < this.JUnitWrapped.getTimeout()) {
//						System.out.println("TOTOTO: " + (System.currentTimeMillis() - initTimeMillis)/1000 + " OUT OF " + this.JUnitWrapped.getTimeout());
                        try {
//							if ((reader.ready() && (line = reader.readLine()) != null)) {
//								System.out.println(line.toString());
//								builder.append(line);
//								builder.append(System.getProperty("line.separator"));
//							}

                            //							int i = process.exitValue();
                            //							System.out.println("The exit value is " + i);
                            //							if (i == 0) {


//							String fileNameToLookFor = fileToAnalyze.replace("output.als", "verdict.txt");
//							File fileToLookFor = new File(fileNameToLookFor);
                            pId = Thread.currentThread().getName().substring(5, Thread.currentThread().getName().length() - 9);
                            isDeterminized = JUnitWrapped.getDeterminized();
                            TOinSecs = JUnitWrapped.getTimeout();
                            long runTime = 0;

                            if (fileToLookForSAT.exists()) {
                                action = "ended";
                                runTime = (System.nanoTime() - initTime) / 1000000000;
                                this.getCompilationUnitWrapper().setOutput(true);  // outcome was SAT

                                String EndSatContent = String.format("pid   %1$6s      stat %2$7s    det %3$5s      to: %4$5d      runT: %5$6d     outcome SAT ", pId, action, isDeterminized, TOinSecs, runTime);
                                System.out.println(EndSatContent);
                                return null;
                            } else {
                                if (fileToLookForUNSAT.exists()) {

                                    action = "ended";
                                    runTime = (System.nanoTime() - initTime) / 1000000000;

                                    String EndUNSatContent = String.format("pid   %1$6s      stat %2$7s    det %3$5s      to: %4$5d      runT: %5$6d     outcome UNSAT ", pId, action, isDeterminized, TOinSecs, runTime);
                                    System.out.println(EndUNSatContent);
                                    return null;
                                }
                            }


                            //							}
                        } catch (IllegalThreadStateException e) {
                            //								System.out.println("Not yet finished");
                        }
                    }
                    long finalTime = System.nanoTime();

                    long elapsedTimeInSeconds = (finalTime - initTime) / 1000000000;
                    this.getCompilationUnitWrapper().setTimeOuted();

                    pId = Thread.currentThread().getName().substring(5, Thread.currentThread().getName().length() - 9);
                    action = "interr";
                    isDeterminized = JUnitWrapped.getDeterminized();
                    TOinSecs = JUnitWrapped.getTimeout();
                    long runTime = (System.nanoTime() - initTime) / 1000000000;
                    String EndInterruptedContent = String.format("pid   %1$6s      stat %2$7s    det %3$5s      to: %4$5d    runT: %5$6d    outcome timeouted ", pId, action, isDeterminized, TOinSecs, runTime);
                    System.out.println(EndInterruptedContent);

                    process.destroy();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                alloy_analysis_result = alloy_stage.get_analysis_result();
                /**/
                alloy_stage = null;
            }
        }


        //		translatedAnalysisResult.setAlloyAnalysisResult(alloy_analysis_result);
        //
        //		String junitFile = null;
        //
        //		if (TacoConfigurator.getInstance().getGenerateUnitTestCase() || TacoConfigurator.getInstance().getAttemptToCorrectBug()) {
        //			// Begin JUNIT Generation Stage
        //			if (translatedAnalysisResult.get_alloy_analysis_result().isSAT())
        //				System.out.println("JUnit generation: started");
        //
        //			SnapshotStage snapshotStage = new SnapshotStage(compilation_units, translatedAnalysisResult, classToCheck, methodToCheck);
        //			try {
        //				semJUnitConstruction.acquire();
        //				snapshotStage.execute();
        //				RecoveredInformation recoveredInformation = snapshotStage.getRecoveredInformation();
        //				recoveredInformation.setFileNameSuffix(StrykerStage.fileSuffix);
        //				JUnitStage jUnitStage = new JUnitStage(recoveredInformation);
        //				jUnitStage.execute();
        //				junitFile = jUnitStage.getJunitFileName();
        //				if (translatedAnalysisResult.get_alloy_analysis_result().isSAT())
        //					System.out.println("         ... and finished.");
        //
        //			} catch (TacoException e) {
        //				System.out.println("");
        //				System.out.println(e.getMessage());
        //			} finally {
        //				semJUnitConstruction.release();
        //			}
        //			// End JUNIT Generation Stage
        //		} else {
        //			log.info("****** JUnit with counterexample values will not be generated. ******* ");
        //			if (translatedAnalysisResult.get_alloy_analysis_result().isSAT())
        //				System.out.println("JUnit generation: skipped even though a bug/execution exists");
        //			if (!TacoConfigurator.getInstance().getGenerateUnitTestCase()) {
        //				log.info("****** generateUnitTestCase=false ******* ");
        //			}
        //
        //		}
        //
        //		if (TacoConfigurator.getInstance().getBuildJavaTrace()) {
        //			if (translatedAnalysisResult.get_alloy_analysis_result().isSAT()) {
        //				log.info("****** START: Java Trace Generation ****** ");
        //				DynAlloyCompiler compiler = dynalloyToAlloy.getDynAlloyCompiler();
        //				JavaTraceStage javaTraceStage = new JavaTraceStage(compiler.getSpecContext(), alloy_analysis_result, false);
        //				javaTraceStage.execute();
        //				//				DynAlloySolution dynAlloySolution = javaTraceStage.getDynAlloySolution();
        //				//				List<TraceStep> trace = dynAlloySolution.getTrace();
        //
        //				log.info("****** FINISH: Java Trace Generation ****** ");
        //			}
        //		} else {
        //			log.info("****** Java Trace will not be generated. ******* ");
        //			log.info("****** generateJavaTrace=false ******* ");
        //		}
        //
        //		if (TacoConfigurator.getInstance().getAttemptToCorrectBug()) {
        //			if (translatedAnalysisResult.get_alloy_analysis_result().isSAT() &&
        //					translatedAnalysisResult.get_alloy_analysis_result().getAlloy_solution().getOriginalCommand().startsWith("Check")) {
        //				log.info("****** START: Stryker ****** ");
        //				methodToCheck = overridingProperties.getProperty(TacoConfigurator.METHOD_TO_CHECK_FIELD);
        //				sourceRootDir = TacoConfigurator.getInstance().getString(
        //						TacoConfigurator.JMLPARSER_SOURCE_PATH_STR);
        //				StrykerStage strykerStage = new StrykerStage(compilation_units, sourceRootDir, classToCheck,
        //						methodToCheck, configFile, overridingProperties,
        //						TacoConfigurator.getInstance().getMaxStrykerMethodsForFile());
        //				StrykerStage.junitInputs = new Class<?>[50];
        //				StrykerStage.junitFiles = new String[50];
        //
        //				try {
        //					String currentJunit = null;
        //
        //					String tempFilename = junitFile.substring(0, junitFile.lastIndexOf(FILE_SEP) + 1) /*+ FILE_SEP*/;
        //					String packageToWrite = "ar.edu.output.junit";
        //					String fileClasspath = tempFilename.substring(0, tempFilename.lastIndexOf(new String("ar.edu.generated.junit").replaceAll("\\.", FILE_SEP)));
        //					fileClasspath = fileClasspath.replaceFirst("generated", "output");
        //					//					String currentClasspath = System.getProperty("java.class.path")+PATH_SEP+fileClasspath/*+PATH_SEP+System.getProperty("user.dir")+FILE_SEP+"generated"*/;
        //					currentJunit = TacoMain.editTestFileToCompile(junitFile, classToCheck, packageToWrite, methodToCheck);
        //
        //					File[] file1 = new File[]{new File(currentJunit)};
        //					JavaCompiler javaCompiler = ToolProvider.getSystemJavaCompiler();
        //					StandardJavaFileManager fileManager = javaCompiler.getStandardFileManager(null, null, null);
        //					Iterable<? extends JavaFileObject> compilationUnit1 =
        //							fileManager.getJavaFileObjectsFromFiles(Arrays.asList(file1));
        //					javaCompiler.getTask(null, fileManager, null, null, null, compilationUnit1).call();
        //					fileManager.close();
        //					javaCompiler = null;
        //					file1 = null;
        //					fileManager = null;
        //
        //					///*mfrias*/		int compilationResult =	javaCompiler.run(null, null, null /*new NullOutputStream()*/, new String[]{"-classpath", currentClasspath, currentJunit});
        //					///**/				javaCompiler = null;
        //					//					if(compilationResult == 0) {
        //					log.warn("junit counterexample compilation succeded");
        //					ClassLoader cl = ClassLoader.getSystemClassLoader();
        //					@SuppressWarnings("resource")
        //					ClassLoader cl2 = new URLClassLoader(new URL[]{new File(fileClasspath).toURI().toURL()}, cl);
        //					//						ClassLoaderTools.addFile(fileClasspath);
        //					Class<?> clazz = cl2.loadClass(packageToWrite + "." + TacoMain.obtainClassNameFromFileName(junitFile));
        //					//						Method[] meth = clazz.getMethods();
        //					//						log.info("preparing to add a class containing a test input to the pool... "+packageToWrite+"."+MuJavaController.obtainClassNameFromFileName(junitFile));
        //					//						Result result = null;
        //					//						final Object oToRun = clazz.newInstance();
        //					DigestOutputStream dos;
        //					File duplicatesTempFile = null;
        //					String content = null;
        //					try {
        //						content = FileUtils.readFile(junitFile);
        //					} catch (Exception e) {
        //						throw new IllegalArgumentException("invalid or null file");
        //					}
        //					try {
        //						duplicatesTempFile = File.createTempFile("forDuplicatesJunit", null);
        //						dos = new DigestOutputStream(new FileOutputStream(duplicatesTempFile, false), MessageDigest.getInstance("MD5"));
        //						dos.write(content.getBytes());
        //						dos.flush();
        //						dos.close();
        //					} catch (Exception e) {
        //						throw new IllegalArgumentException("exception thrown while trying to compute digest in class VariablizedSATVerdicts");
        //					}
        //					byte[] digest = dos.getMessageDigest().digest();
        //					MuJavaController.MsgDigest msgDigest = new MuJavaController.MsgDigest(digest);
        //					StrykerStage.junitFilesHash.put(msgDigest, junitFile);
        //					StrykerStage.junitInputs[StrykerStage.indexToLastJUnitInput] = clazz;
        //					StrykerStage.junitFiles[StrykerStage.indexToLastJUnitInput] = junitFile;
        //					StrykerStage.indexToLastJUnitInput++;
        //					cl = null;
        //					cl2 = null;
        //
        //					//
        //					//					} else {
        //					//						log.warn("compilation failed");
        //					//					}
        //					//							File originalFile = new File(tempFilename);
        //					//							originalFile.delete();
        //
        //				} catch (ClassNotFoundException e) {
        //					//							e.printStackTrace();
        //				} catch (IOException e) {
        //					//							e.printStackTrace();
        //				} catch (IllegalArgumentException e) {
        //					//							e.printStackTrace();
        //				} catch (Exception e) {
        //					//							e.printStackTrace();
        //				}
        //
        //				strykerStage.execute();
        //
        //				log.info("****** FINISH: Stryker ****** ");
        //			}
        //		} else {
        //			log.info("****** BugFix will not be generated. ******* ");
        //			log.info("****** attemptToCorrectBug=false ******* ");
        //		}
        //		//	System.out.println("Subproblems Left : " + splitProblems.size());
        //		//	System.out.println("Thread Finish: " + Thread.currentThread().getName());
        //		System.out.println("CALLABLE");
        //		System.out.println("Is NonNull in Callable: " + (translatedAnalysisResult != null));
        return translatedAnalysisResult;
    }


    public List<JCompilationUnitType> parse_simplified_compilation_units(List<String> files) {
        //  for (String s : files)
        //  System.out.println("input file name: " + s);

        String canonical_outdir_path = makeCanonicalPath();

        //  System.out.println("target file name: " + canonical_outdir_path);
        //        try {
        //            String theActualOutputDir = TacoConfigurator.getInstance().getOutputDir();
        //            File output_dir = new File(theActualOutputDir);
        //            canonical_outdir_path = output_dir.getCanonicalPath();
        //        } catch (IOException e) {
        //            throw new TacoException("canonical path couldn't be computed " + e.getMessage());
        //        }

        JmlParser theParserInstance = new JmlParser();
        String filename = System.getProperty("user.dir") + System.getProperty("file.separator") + "bin";
        theParserInstance.initialize(canonical_outdir_path, filename,
                files);

        return theParserInstance.getCompilationUnits();

    }

    public List<String> write_simplified_compilation_units(List<JCompilationUnitType> newAsts) {
        List<String> files = new LinkedList<String>();
        String canonical_path = makeCanonicalPath();
        //	System.out.println("Canonical Path: " + canonical_path);
        for (JCompilationUnitType compilation_unit : newAsts) {
            assert compilation_unit.typeDeclarations().length == 1;
            JTypeDeclarationType typeDeclaration = compilation_unit.typeDeclarations()[0];
            String filename = canonical_path + File.separator + typeDeclaration.getCClass().getJavaName().replaceAll("\\.", "/");

            //  System.out.println("Write files: " + filename);

            files.add(typeDeclaration.getCClass().getJavaName());
            try {
                FileUtils.writeToFile(filename + TacoMain.OUTPUT_SIMPLIFIED_JAVA_EXTENSION, JavaAndJmlPrettyPrint2.print(compilation_unit));
            } catch (IOException e) {
                throw new RuntimeException("DYNJALLOY ERROR! " + e.getMessage());
            }
        }
        return files;
    }

    private String makeCanonicalPath() {
        String output_dir = "output_threads/" + TacoConfigurator.getInstance().getOutputDir() + "_" + Thread.currentThread().getName();
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

    public JCompilationUnitTypeWrapper getCompilationUnitWrapper() {
        // TODO Auto-generated method stub
        return this.JUnitWrapped;
    }

    public void setCompilationUnitWrapper(JCompilationUnitTypeWrapper determinizedUnit) {
        // TODO Auto-generated method stub
        this.JUnitWrapped = determinizedUnit;
    }
}