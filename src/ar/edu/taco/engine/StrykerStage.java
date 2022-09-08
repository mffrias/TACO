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

import ar.edu.taco.stryker.StrykerInitialStage;
import ar.edu.taco.stryker.api.impl.MuJavaController.MsgDigest;
import ar.edu.taco.stryker.api.impl.OpenJMLController;
import ar.edu.taco.stryker.exceptions.FatalStrykerStageException;
import ar.edu.taco.utils.FileUtils;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * Implements the Stryker stage. In this stage a fix for the bug found in the
 * method to check will be automatically obtained (if possible).
 */
public class StrykerStage implements ITacoStage {

	private static final String FILE_SEP = System.getProperty("file.separator");

	private static Logger log = Logger.getLogger(StrykerStage.class);

	// The class name under analysis in which a bug was found. E.g: BinTree
	private String classToCheck;

	// The class file under analysis in which a bug was found.
	private File classToCheckFile;

	// The method under analysis in which a bug was found.
	private String methodToCheck;

	@SuppressWarnings("unused")
	private List<JCompilationUnitType> asts;

	public static String STRYKER_REPORTS_FILENAME = "StrykerLastReports.txt";
	public static Class<?>[] junitInputs;
    public static String[] junitFiles;
    public static Map<MsgDigest, String> junitFilesHash = Maps.newConcurrentMap();
	public static int indexToLastJUnitInput = 0;
	public static int fileSuffix = 0;
	
	public static long initialMillis = System.currentTimeMillis();
	public static long compilationMillis = 0;
    public static long tacoMillis = 0;
    public static long racMillis = 0;
    public static long muJavaMillis = 0;

    public static int nonCompilableMutationIndexesFound = 0;
	public static int mutationsQueuedToOJMLC = 0;
    public static int mutationsQueuedToDarwinistForSeq = 0;
    public static int candidatesQueuedToDarwinist = 0;
    public static int mutationsGenerated = 0;
    public static int nonCompilableMutations = 0;
    public static int duplicateMutations = 0;
    public static int prunedMutations = 0;
    public static int prunedFathers = 0;
    public static int nullPointerExceptionMutations = 0;
    public static int postconditionFailedMutations = 0;
    public static int timeoutMutations = 0;
    public static int falseCandidates = 0;
    public static int relevantFeedbacksFound = 0;
    public static long maxSolvingTime = 0;
    public static long minSolvingTime = Long.MAX_VALUE;
    public static long totalSolvings = 0;
    
    
	private String configFile;

	private Properties overridingProperties;
	
	private int maxMethodsInFile;
	
	public static AtomicInteger generationsWanted = new AtomicInteger(10);

	/**
	 * Creates a new Stryker stage with the information given.
	 * 
	 * @param sourceRootDir
	 *            the root dir of the source under consideration. The class to
	 *            check should be under this dir.
	 * @param classToCheck
	 *            the class under analysis in which a bug was found.
	 * @param methodToCheck
	 *            the method under analysis in which a bug was found.
	 * @param configFile
	 *            The configuration file
	 * @param overridingProperties
	 *            The properties
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
		this.methodToCheck = methodToCheck.substring(0,
				methodToCheck.lastIndexOf("_"));
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