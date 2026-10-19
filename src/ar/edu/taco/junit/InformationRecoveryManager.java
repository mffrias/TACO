package ar.edu.taco.junit;

import org.multijava.mjc.JCompilationUnitType;

/**
 * Recovers from the JML AST of the class under analysis the names that the counterexample
 * snapshot and test need: its fields, its static fields and the parameters of
 * {@code methodToCheck}. It runs an {@link InformationRecoveryVisitor} over a compilation unit
 * and copies the results into a {@link RecoveredInformation}.
 *
 * <p>{@code ar.edu.taco.engine.SnapshotStage.execute()} creates one per snapshot and calls
 * {@link #processTypeDeclaration} for the unit whose name is {@code classToCheck}, among the
 * units of the TACO run that found the counterexample (docs/FIXES-FOR-MASTER.md, S12). If no
 * unit matches, the information stays invalid, and no snapshot or test is made. See
 * docs/ARCHITECTURE.md, section 1.
 */
public class InformationRecoveryManager {
	private String methodToCheck;
	private RecoveredInformation recoveredInformation;

	/**
	 * Creates a manager for one method under analysis.
	 *
	 * @param methodToCheck the method's qualified name without its parameter types
	 *        ({@code pkg_Class_method}), as {@code SnapshotStage} passes it
	 * @param recoveredInformation where {@link #processTypeDeclaration} stores what it finds
	 */
	public InformationRecoveryManager(String methodToCheck, RecoveredInformation recoveredInformation) {
		this.methodToCheck = methodToCheck;
		this.recoveredInformation = recoveredInformation;
	}

	/**
	 * Visits {@code aJCompilationUnitType} with a new {@link InformationRecoveryVisitor} and
	 * stores the results in the {@link RecoveredInformation}: marks it valid, adds the field and
	 * static field names by class (replacing the entries of classes already there) and replaces
	 * the list of parameter names. The visitor reads {@code TacoConfigurator}.
	 *
	 * @param aJCompilationUnitType the compilation unit of the class under analysis
	 */
	public void processTypeDeclaration(JCompilationUnitType aJCompilationUnitType) {
		InformationRecoveryVisitor informationRecoveryVisitor = new InformationRecoveryVisitor(this.methodToCheck);

		aJCompilationUnitType.accept(informationRecoveryVisitor);
		
		this.recoveredInformation.setValidInformation(true);
		this.recoveredInformation.addFieldsName(informationRecoveryVisitor.getFieldsName());
		this.recoveredInformation.addStaticFieldsName(informationRecoveryVisitor.getStaticFieldsName());
		this.recoveredInformation.setMethodParametersNames(informationRecoveryVisitor.getMethodParametersNames());
	}

}
