package ar.edu.taco.engine;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

import mujava.api.MutationOperator;

import org.apache.log4j.Logger;
import org.multijava.mjc.JCompilationUnitType;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import ar.edu.taco.stryker.MethodToCheck;
import ar.edu.taco.stryker.StrykerInitialStage;
import ar.edu.taco.stryker.api.impl.MuJavaController.MsgDigest;
import ar.edu.taco.stryker.api.impl.OpenJMLController;
import ar.edu.taco.stryker.api.impl.WeedQueue;
import ar.edu.taco.stryker.api.impl.input.DarwinistInput;
import ar.edu.taco.stryker.api.impl.input.MuJavaInput;
import ar.edu.taco.stryker.api.impl.input.OpenJMLInput;
import ar.edu.taco.stryker.exceptions.FatalStrykerStageException;
import ar.edu.taco.utils.FileUtils;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * Implements the Stryker stage. In this stage a fix for the bug found in the
 * method to check will be automatically obtained (if possible).
 *
 * <p>{@link ar.edu.taco.TacoMain#run(String, Properties)} creates this stage when
 * {@code attemptToCorrectBug} is set and the TACO check is SAT, loads the counterexample into
 * {@link #junitInputs}, and calls {@link #execute()}, which blocks until the search ends.
 *
 * <p>Besides the stage itself, this class holds, in static fields, the run-wide state that
 * the Stryker controllers share:
 * <ul>
 * <li>the counterexample pool: {@link #junitInputs}, {@link #junitFiles},
 *     {@link #junitFilesHash}, {@link #indexToLastJUnitInput} and {@link #fileSuffix};</li>
 * <li>the work queues between controllers: {@link #weedQueue};</li>
 * <li>timing and counting statistics, printed in each fix report by
 *     {@link ar.edu.taco.stryker.api.impl.DarwinistController}.</li>
 * </ul>
 * These fields are plain, unsynchronized statics updated from several controller threads, so
 * the statistics are approximate. They are never reset (only {@link #junitInputs} and
 * {@link #junitFiles} are reallocated by {@code TacoMain}), so one Stryker run per JVM is
 * assumed.
 */
public class StrykerStage implements ITacoStage {

	private static final String FILE_SEP = System.getProperty("file.separator");

	private static Logger log = Logger.getLogger(StrykerStage.class);

	// The class name under analysis in which a bug was found. E.g: BinTree
	private String classToCheck;

	// The class file under analysis in which a bug was found.
	private File classToCheckFile;

	// The method under analysis in which a bug was found.
	// (Plain name, e.g. "contains" for "contains(java.lang.Object)".)
	private String methodToCheck;

	@SuppressWarnings("unused")
	private List<JCompilationUnitType> asts;

	/**
	 * Report file in {@code user.dir}. The constructor truncates it; each fix found appends a
	 * report to it ({@link ar.edu.taco.stryker.api.impl.DarwinistController} uses its own
	 * copy of this file name).
	 */
	public static String STRYKER_REPORTS_FILENAME = "StrykerLastReports.txt";

	// ---- Counterexample pool ----

	/** Capacity of the counterexample pool ({@link #junitInputs}, {@link #junitFiles}). */
	public static final int MAX_COUNTEREXAMPLES = 10000;

	/**
	 * Compiled JUnit counterexample classes (package {@code ar.edu.output.junit}, compiled from
	 * the copies made by {@link ar.edu.taco.TacoMain#editTestFileToCompile}). Slots
	 * {@code 0..indexToLastJUnitInput-1} are filled. {@code TacoMain.run} allocates {@link #MAX_COUNTEREXAMPLES} slots
	 * and stores the counterexample of the original TACO check in the first free slot;
	 * {@link ar.edu.taco.stryker.api.impl.DarwinistController} appends the counterexample of
	 * each fix candidate that TACO refutes (except validate-only ones), unless it is a
	 * duplicate or the array is full.
	 * Read by {@link OpenJMLController} (RAC filtering) and by DarwinistController (final RAC
	 * check of a candidate, and to fix the input of feedback analyses).
	 */
	public static Class<?>[] junitInputs;
    /**
     * Parallel to {@link #junitInputs}: path of the original generated JUnit source of each
     * slot (under {@code generated/ar/edu/generated/junit/}, not the rewritten copy). Used to
     * name the counterexample that killed a mutant in feedback requests.
     */
    public static String[] junitFiles;
    /** MD5 digest of a generated JUnit source's content to its path; used to skip duplicate counterexamples. */
    public static Map<MsgDigest, String> junitFilesHash = Maps.newConcurrentMap();
	/** Number of filled slots in {@link #junitInputs}, that is, the index of the next free slot. */
	public static int indexToLastJUnitInput = 0;
	/**
	 * Suffix for the next generated JUnit class name; incremented by
	 * {@link ar.edu.taco.junit.UnitTestBuilder} after writing each test, so successive
	 * counterexamples get distinct files.
	 */
	public static int fileSuffix = 0;
	
	// ---- Timing statistics (milliseconds; reported by DarwinistController) ----

	/** Reference time for the reports' total time: set when this class is initialized (first use of its statics). */
	public static long initialMillis = System.currentTimeMillis();
	/** Time spent compiling: JML4C batch compilations and javac compilations in DarwinistController. */
	public static long compilationMillis = 0;
    /** Time spent in TACO calls made by DarwinistController. */
    public static long tacoMillis = 0;
    /** Time spent running counterexamples with RAC (OpenJMLController and DarwinistController). */
    public static long racMillis = 0;
    /** Time spent generating mutants with MuJava. */
    public static long muJavaMillis = 0;

    // ---- Counting statistics (reported by DarwinistController) ----

    /**
     * Grows on each failed JML4C batch compilation by the total size of the father's sets of
     * non-compilable mutation indexes, so indexes may be counted more than once.
     */
    public static int nonCompilableMutationIndexesFound = 0;
	/** Mutants enqueued to {@link OpenJMLController} (queue 2). */
	public static int mutationsQueuedToOJMLC = 0;
    /** Feedback requests enqueued (by OpenJMLController and DarwinistController). */
    public static int mutationsQueuedToDarwinistForSeq = 0;
    /** Mutants that passed RAC and were enqueued to DarwinistController as fix candidates. */
    public static int candidatesQueuedToDarwinist = 0;
    /** Mutants produced by MuJavaController's batch builder, counted before duplicate removal. */
    public static int mutationsGenerated = 0;
    /** Mutants removed from a batch because JML4C could not compile them. */
    public static int nonCompilableMutations = 0;
    /** Mutants discarded because an identical file was already generated. */
    public static int duplicateMutations = 0;
    /** Mutants skipped thanks to feedback or known non-compilable indexes. */
    public static int prunedMutations = 0;
    /** Inputs not used as fathers because their feedback marked them as not fatherable. */
    public static int prunedFathers = 0;
    /**
     * Mutants that threw an exception other than a JML RAC error during RAC (not only
     * {@code NullPointerException}).
     */
    public static int nullPointerExceptionMutations = 0;
    /**
     * Mutants that failed RAC, not counting validate-only inputs, minus the RAC exceptions
     * counted in {@link #nullPointerExceptionMutations} (which are subtracted for validate-only
     * inputs too).
     */
    public static int postconditionFailedMutations = 0;
    /** Mutants that hit the RAC timeout on at least one counterexample. */
    public static int timeoutMutations = 0;
    /** Fix candidates rejected by DarwinistController (TACO still SAT, or the final RAC check failed). */
    public static int falseCandidates = 0;
    /** Times feedback made MuJavaController skip at least one mutant when computing the next sibling. */
    public static int relevantFeedbacksFound = 0;
    /** Longest TACO call made by DarwinistController. */
    public static long maxSolvingTime = 0;
    /** Shortest TACO call made by DarwinistController. */
    public static long minSolvingTime = Long.MAX_VALUE;
    /** Number of TACO calls made by DarwinistController. */
    public static long totalSolvings = 0;
    
    
    /**
     * The three queues connecting the controllers: 1 to MuJavaController, 2 to
     * OpenJMLController, 3 to DarwinistController. Its acknowledgement count also decides
     * when the search is exhausted; see {@link WeedQueue}.
     */
    public static WeedQueue<MuJavaInput, OpenJMLInput, DarwinistInput> weedQueue = new WeedQueue<>();
    
	private String configFile;

	private Properties overridingProperties;
	
	private int maxMethodsInFile;
	
	/**
	 * Passed to {@link StrykerInitialStage} as the number of generations wanted. It is copied
	 * into some MuJava inputs but never read, so it does not bound the search.
	 */
	public static AtomicInteger generationsWanted = new AtomicInteger(10);

	/**
	 * Creates a new Stryker stage with the information given.
	 * 
	 * <p>Side effect: truncates {@link #STRYKER_REPORTS_FILENAME} in {@code user.dir}.
	 *
	 * @param asts
	 *            the parsed compilation units; stored but unused.
	 * @param sourceRootDir
	 *            the root dir of the source under consideration. The class to
	 *            check should be under this dir.
	 * @param classToCheck
	 *            the class under analysis in which a bug was found, fully
	 *            qualified (e.g. {@code ar.edu.taco.BinTree}); used to locate
	 *            its {@code .java} file under {@code sourceRootDir}, which must
	 *            exist (otherwise a {@link RuntimeException} is thrown).
	 * @param methodToCheck
	 *            the method under analysis in which a bug was found, as TACO's
	 *            {@code methodToCheck} (e.g. {@code contains(java.lang.Object)});
	 *            only its plain name is kept.
	 * @param configFile
	 *            The configuration file
	 * @param overridingProperties
	 *            The properties
	 * @param maxMethodsInFile
	 *            passed on to {@link StrykerInitialStage}; see
	 *            {@link ar.edu.taco.stryker.api.StrykerAPI#fixBug}.
	 */
	// TODO agregar los parametros necesarios para las otras partes
	public StrykerStage(List<JCompilationUnitType> asts, String sourceRootDir,
			String classToCheck, String methodToCheck, String configFile,
			Properties overridingProperties, int maxMethodsInFile) {
		this.asts = asts;
		this.overridingProperties = overridingProperties;
		int lastDotIndex = classToCheck.lastIndexOf(".");
		if (lastDotIndex != -1) {
			// The name comes with the package such as: ar.edu.taco.BinTree
			this.classToCheck = classToCheck.substring(lastDotIndex + 1,
					classToCheck.length());
		} else {
			this.classToCheck = classToCheck;
		}
		this.classToCheckFile = obtainJavaFileOfClassToCheck(sourceRootDir,
				classToCheck);
		this.methodToCheck = MethodToCheck.methodName(methodToCheck);
		this.configFile = configFile;
		this.maxMethodsInFile = maxMethodsInFile;

        try {
            FileUtils.writeToFile(System.getProperty("user.dir")+
                    OpenJMLController.FILE_SEP + STRYKER_REPORTS_FILENAME, "");
        } catch (IOException e) {
            // TODO: Define what to do!
        }

		// things to obtain input
		// recoveredInformation = new RecoveredInformation();
		// recoveredInformation.setClassToCheck(classToCheck);
		// recoveredInformation.setMethodToCheck(this.methodToCheck);
		//
		// // INFORMATION RECOVERY
		// InformationRecoveryManager informationRecoveryManager = new
		// InformationRecoveryManager(
		// this.methodToCheck, recoveredInformation);
		//
		// for (JCompilationUnitType aJCompilationUnitType : this.asts) {
		// String unitTypeName = aJCompilationUnitType.packageNameAsString()
		// .replace("/", ".") + aJCompilationUnitType.fileNameIdent();
		//
		// if (unitTypeName.equals(classToCheck)) {
		// informationRecoveryManager
		// .processTypeDeclaration(aJCompilationUnitType);
		// }
		// }
		// UnitTestBuilder unitTestBuilder = new
		// UnitTestBuilder(recoveredInformation);
		// unitTestBuilder.deleteFile(unitTestBuilder.getOutputClassName());
		// try {
		// unitTestBuilder.createUnitTest();
		// junitFile = unitTestBuilder.getOutputClassFilename();
		// } catch (IllegalArgumentException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// } catch (IllegalAccessException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// } catch (InstantiationException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
	}

	/**
	 * Runs the repair search and blocks until it ends: builds the mutation operator set and
	 * executes a {@link StrykerInitialStage} for the class file, simple class name and method
	 * name computed by the constructor. A {@link FatalStrykerStageException} is printed, not
	 * rethrown. The outcome is only logged (see {@link StrykerInitialStage}).
	 *
	 * <p>The operator set is hard-coded (18 MuJava operators): {@code PRVOL_SMART},
	 * {@code PRVOR_REFINED}, {@code PRVOU_REFINED}, {@code AODS}, {@code AODU}, {@code AOIS},
	 * {@code AOIU}, {@code AORB}, {@code AORS}, {@code AORU}, {@code ASRS}, {@code COD},
	 * {@code COI}, {@code COR}, {@code LOD}, {@code LOR}, {@code ROR} and {@code SOR}
	 * ({@code LOI} is commented out). Note that
	 * {@link ar.edu.taco.stryker.api.impl.UnskippableMuJavaController} builds the inputs it
	 * hands to MuJavaController with its own hard-coded copy of this set, so a change here
	 * should be mirrored there.
	 */
	@Override
	public void execute() {
		// TODO: get this from a property file.
		HashSet<MutationOperator> mutOps = Sets.newHashSet();
		// Basic mutators
		// for(Mutant m : Mutant.values()) {
		// mutOps.add(m);
		// }
		
//		mutOps.add(Mutant.EAM);
//		mutOps.add(Mutant.EMM);
//		mutOps.add(Mutant.EOA);
//		mutOps.add(Mutant.EOC);
//		mutOps.add(Mutant.ISD);
//		mutOps.add(Mutant.ISD_SMART);
//		mutOps.add(Mutant.ISI);
//		mutOps.add(Mutant.ISI_SMART);
//		mutOps.add(Mutant.JTD);
//		mutOps.add(Mutant.JTI);
//		mutOps.add(Mutant.JTI_SMART);
//		mutOps.add(Mutant.OAN);
//		mutOps.add(Mutant.OAN_RELAXED);
//		mutOps.add(Mutant.PRVOL);
//		mutOps.add(Mutant.PRVOR);
//		mutOps.add(Mutant.PRVOR_SMART);
//		mutOps.add(Mutant.PRVOU);
//		mutOps.add(Mutant.PRVOU_SMART);
		mutOps.add(MutationOperator.PRVOL_SMART);
        mutOps.add(MutationOperator.PRVOR_REFINED);
        mutOps.add(MutationOperator.PRVOU_REFINED);
		mutOps.add(MutationOperator.AODS);
		mutOps.add(MutationOperator.AODU);
		mutOps.add(MutationOperator.AOIS);
		mutOps.add(MutationOperator.AOIU);
		mutOps.add(MutationOperator.AORB);
		mutOps.add(MutationOperator.AORS);
		mutOps.add(MutationOperator.AORU);
		mutOps.add(MutationOperator.ASRS);
		mutOps.add(MutationOperator.COD);
		mutOps.add(MutationOperator.COI);
		mutOps.add(MutationOperator.COR);
		mutOps.add(MutationOperator.LOD);
//		mutOps.add(MutationOperator.LOI);
		mutOps.add(MutationOperator.LOR);
		mutOps.add(MutationOperator.ROR);
		mutOps.add(MutationOperator.SOR);	
		
		//WORKING SET
//		mutOps.add(Mutant.PRVOR);
//		mutOps.add(Mutant.PRVOU);
//		mutOps.add(Mutant.AODS);
//		mutOps.add(Mutant.AODU);
//		mutOps.add(Mutant.AOIS);
//		mutOps.add(Mutant.AOIU);
//		mutOps.add(Mutant.AORB);
//		mutOps.add(Mutant.AORS);
//		mutOps.add(Mutant.AORU);
//		mutOps.add(Mutant.ASRS);
//		mutOps.add(Mutant.COD);
//		mutOps.add(Mutant.COI);
//		mutOps.add(Mutant.COR);
//		mutOps.add(Mutant.LOD);
//		mutOps.add(Mutant.LOI);
//		mutOps.add(Mutant.LOR);
//		mutOps.add(Mutant.ROR);
//		mutOps.add(Mutant.SOR);

		
		//EVERYTHING ELSE
//		mutOps.add(Mutant.AMC);
//		mutOps.add(Mutant.PRVOL);
//		mutOps.add(Mutant.ROR);
////		mutOps.add(Mutant.PRVOL);
//        mutOps.add(Mutant.PRVOR);
//        mutOps.add(Mutant.PRVOU);
//		mutOps.add(Mutant.PRVV);		
//		mutOps.add(Mutant.AORB);
//		mutOps.add(Mutant.EAM);
//		mutOps.add(Mutant.EMM);
//		mutOps.add(Mutant.EOA);
//		mutOps.add(Mutant.EOC);
//		mutOps.add(Mutant.AMC);
//		mutOps.add(Mutant.IHD);
//		mutOps.add(Mutant.IHI);
//		mutOps.add(Mutant.IOD);
//		mutOps.add(Mutant.IOP);
//		mutOps.add(Mutant.IOR);
//		mutOps.add(Mutant.IPC);
//		mutOps.add(Mutant.ISD);
//		mutOps.add(Mutant.ISI);
//		mutOps.add(Mutant.ISK);
//		mutOps.add(Mutant.JTD);
//		mutOps.add(Mutant.JDC);
//		mutOps.add(Mutant.JID);
//		mutOps.add(Mutant.JSD);
//		mutOps.add(Mutant.JSI);
//		mutOps.add(Mutant.JTI);
//		mutOps.add(Mutant.OAN);
//		mutOps.add(Mutant.OMD);
//		mutOps.add(Mutant.OMR);
//		mutOps.add(Mutant.PCC);
//		mutOps.add(Mutant.PCI);
//		mutOps.add(Mutant.PMD);
//		mutOps.add(Mutant.PNC);
//		mutOps.add(Mutant.PPD);
//		mutOps.add(Mutant.AODS);
//		mutOps.add(Mutant.AODU);
//		mutOps.add(Mutant.AOIS);
//		mutOps.add(Mutant.AOIU);
//		mutOps.add(Mutant.AORS);
//		mutOps.add(Mutant.AORU);
//		mutOps.add(Mutant.ASRS);
//		mutOps.add(Mutant.COD);
//		mutOps.add(Mutant.COI);
//		mutOps.add(Mutant.COR);
//		mutOps.add(Mutant.LOD);
//		mutOps.add(Mutant.LOI);
//		mutOps.add(Mutant.LOR);
//		mutOps.add(Mutant.SOR);
		
		

		// TODO: CHANGE THIS TO STRYKERINITIALSTAGE
		// IStrykerStage mutantsGenerationStage = new
		// MuJavaBasedMutantsGenerationStage(
		// classToCheckFile, classToCheck, methodToCheck, mutOps,
		// generationsWanted);
		// log.info("---- START: Mutants Generation Stage ----");
		// try {
		// mutantsGenerationStage.execute();
		// } catch (FatalStrykerStageException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// log.info("---- Finish: Mutants Generation Stage ----");
		// // TODO el resto de los stages

		StrykerInitialStage stage = new StrykerInitialStage(classToCheckFile, classToCheck, 
				methodToCheck, mutOps, generationsWanted, configFile, overridingProperties, 
				maxMethodsInFile);
		log.info("---- START:Stryker Stage ----");
		try {
			stage.execute();
		} catch (FatalStrykerStageException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		log.info("---- Finish: Stryker Stage ----");

	}

	/**
	 * Obtains a File object that corresponds to the .java file of the given
	 * classname. The given classname must also have the package information
	 * (e.g: ar.edu.taco.BinTree). This information will be used to locate the
	 * file using the sourceRootDir given as the starting place. The .java file
	 * must be located under sourceRootDir. If otherwise, a RuntimeException
	 * will be thrown.
	 * 
	 * @param sourceRootDir
	 *            the base dir. The .java file must be located under this
	 *            directory.
	 * @param className
	 *            the complete name of the class to locate. Must also have
	 *            package information (e.g: ar.edu.taco.BinTree).
	 * @return the File object that corresponds to the .java file of the given
	 *         class.
	 */
	private File obtainJavaFileOfClassToCheck(String sourceRootDir,
			String className) {
		File f = new File(sourceRootDir);
		if (!f.exists()) {
			throw new RuntimeException("Source path does not exist: "
					+ sourceRootDir);
		}
		String cu = ((className.contains("$")) ? className.substring(0,
				className.indexOf("$")) : className).replace(".", FILE_SEP);

		StringBuilder sb = new StringBuilder();
		sb.append(f.getAbsolutePath()).append(FILE_SEP).append(cu)
				.append(".java");
		String filename = sb.toString();
		File ret = new File(filename);
		if (!ret.exists()) {
			throw new RuntimeException("Java file not found for " + filename);
		}
		return ret;
	}

}