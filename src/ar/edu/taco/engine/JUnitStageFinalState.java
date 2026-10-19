/**
 *
 */
package ar.edu.taco.engine;

import ar.edu.taco.junit.UnitTestBuilderFinalState;
import org.apache.log4j.Logger;

import ar.edu.taco.TacoConfigurator;
import ar.edu.taco.TacoException;
import ar.edu.taco.junit.RecoveredInformation;
import ar.edu.taco.junit.UnitTestBuilder;

/**
 * @author elgaby
 *
 */
public class JUnitStageFinalState implements ITacoStage {

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
    public JUnitStageFinalState(RecoveredInformation recoveredInformation) {
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
     * Writes the final state of a computed specified output as a JUnit class, with
     * {@code UnitTestBuilderFinalState}: class
     * {@code ar.edu.generated.junit.<Class>Test_<method>_<suffix>} in
     * {@code generated/ar/edu/generated/junit/}, named like {@link JUnitStage}'s tests but
     * with {@code Test} after the class name; it declares the final receiver, parameters and
     * {@code result}, calls the method, and asserts nothing. {@code TacoMain.run} runs it, after
     * {@link JUnitStage}, when the analysis had an input to fix, on a
     * {@link SnapshotStage} that read the post-state. Increments the static
     * {@code StrykerStage.fileSuffix}, deletes an existing file of the same name first, and
     * writes nothing if {@code noVerify} is set or the information is invalid. See
     * docs/SPEC-OUTPUT.md ("checkAndRunSpecIfFaulty", step 3).
     *
     * @throws TacoException wrapping any exception of the test builder (also printed)
     */
    @Override
    public void  execute() throws TacoException {
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
//                    UnitTestBuilder unitTestBuilder = new UnitTestBuilder(recoveredInformation);
//                    unitTestBuilder.deleteFile(unitTestBuilder.getOutputClassName());
//                    unitTestBuilder.createUnitTest();
//                    unitTestBuilder.increaseSuffix();

                    UnitTestBuilderFinalState unitTestBuilderFinalState = new UnitTestBuilderFinalState (recoveredInformation/*, this.tacoAnalysisResult*/);
                    unitTestBuilderFinalState.deleteFile(unitTestBuilderFinalState.getOutputClassName());
                    unitTestBuilderFinalState.createUnitTest();
                    junitFile = unitTestBuilderFinalState .getOutputClassFilename();
                }


            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new TacoException(e);
        }


    }

    /**
     * Returns the absolute path of the class written by the last {@link #execute()}.
     *
     * @return the path, or {@code null} if nothing was written
     */
    public String getJunitFileName() {
        return junitFile;
    }
}
