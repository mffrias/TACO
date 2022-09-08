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
	 * @param asts
	 * @param classToCheck
	 * @param methodToCheck
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

	public String getJunitFileName() {
		return junitFile;
	}
}
