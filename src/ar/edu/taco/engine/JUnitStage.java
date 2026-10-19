/**
 * 
 */
package ar.edu.taco.engine;

import org.apache.log4j.Logger;

import ar.edu.taco.TacoConfigurator;
import ar.edu.taco.TacoException;
import ar.edu.taco.junit.RecoveredInformation;
import ar.edu.taco.junit.UnitTestBuilder;

/**
 * @author elgaby
 *
 */
public class JUnitStage implements ITacoStage {
	
	private static Logger log = Logger.getLogger(JUnitStage.class);
	
//	private String classToCheck;
//	private String methodToCheck;
//	private List<JCompilationUnitType> asts;
//	private TacoAnalysisResult tacoAnalysisResult;
	RecoveredInformation recoveredInformation;
	String junitFile;
	
	/**
	 *
	 */
//	public JUnitStage(List<JCompilationUnitType> asts, TacoAnalysisResult tacoAnalysisResult, String classToCheck, String methodToCheck,RecoveredInformation recoveredInformation) {
	public JUnitStage(RecoveredInformation recoveredInformation) {
//		this.asts = asts;
//		this.classToCheck = classToCheck;
//		this.methodToCheck = methodToCheck.substring(0, methodToCheck.lastIndexOf("_"));
//		this.tacoAnalysisResult = tacoAnalysisResult;
		this.recoveredInformation = recoveredInformation;
	}


	/* (non-Javadoc)
	 * @see ar.edu.taco.engine.ITacoStage#execute()
	 */
	/**
	 * Writes the counterexample read by {@link SnapshotStage} as a JUnit test, with
	 * {@code UnitTestBuilder}: class {@code ar.edu.generated.junit.<Class>_<method>_<suffix>}
	 * in {@code generated/ar/edu/generated/junit/}, where {@code <method>} is
	 * {@code methodToCheck} without its parameter types and {@code <suffix>} the
	 * information's file name suffix ({@code StrykerStage.fileSuffix}, which
	 * {@code UnitTestBuilder} then increments: static state shared by every run in the JVM).
	 * An existing file of that name is deleted first. The test rebuilds the input (receiver,
	 * static fields, parameters) and calls the method by reflection; Stryker rewrites it with
	 * {@code TacoMain.editTestFileToCompile}. Nothing is written if {@code noVerify} is set or
	 * the information is invalid (no SAT {@code Check}, or the class to check was not among
	 * the compilation units).
	 *
	 * @throws TacoException wrapping any exception of the test builder (also printed)
	 */
	@Override
	public void execute() throws TacoException {
		try {
//			RecoveredInformation recoveredInformation = new RecoveredInformation();
//			recoveredInformation.setClassToCheck(classToCheck);
//			recoveredInformation.setMethodToCheck(methodToCheck);
			
//			// INFORMATION RECOVERY
//			InformationRecoveryManager informationRecoveryManager = new InformationRecoveryManager(methodToCheck, recoveredInformation);
//			
//			for (JCompilationUnitType aJCompilationUnitType : this.asts) {
//				String unitTypeName = aJCompilationUnitType.packageNameAsString().replace("/", ".") + aJCompilationUnitType.fileNameIdent();
//				
////				if (unitTypeName.equals(classToCheck)) {
//					informationRecoveryManager.processTypeDeclaration(aJCompilationUnitType);
////				}
//			}
			// END INFORMATION RECOVERY
			
			
			if (!TacoConfigurator.getInstance().getNoVerify()) {
				if (!recoveredInformation.isValidInformation()) {
//				if (!tacoAnalysisResult.get_alloy_analysis_result().isSAT() || 
//						!tacoAnalysisResult.get_alloy_analysis_result().getAlloy_solution().getOriginalCommand().startsWith("Check")) {
//					
					log.info("****** JUnit containing the counterexample will not be generated ******");
					log.info("****** JUnit generation: Recovered information is invalid ******");

//					recoveredInformation.setValidInformation(false);
				
				} else {
					log.info("****** Generating Junit test with counterexample ******");
					UnitTestBuilder unitTestBuilder = new UnitTestBuilder(recoveredInformation/*, this.tacoAnalysisResult*/);
					unitTestBuilder.deleteFile(unitTestBuilder.getOutputClassName());
					unitTestBuilder.createUnitTest();
					junitFile = unitTestBuilder.getOutputClassFilename();
				}

					
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new TacoException(e);
		}
		
		
	}

	/**
	 * Returns the absolute path of the test written by the last {@link #execute()}.
	 *
	 * @return the path, or {@code null} if no test was written
	 */
	public String getJunitFileName() {
		return junitFile;
	}
}
