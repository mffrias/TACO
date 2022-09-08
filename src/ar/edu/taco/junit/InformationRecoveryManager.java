package ar.edu.taco.junit;

import org.multijava.mjc.JCompilationUnitType;

public class InformationRecoveryManager {
	private String methodToCheck;
	private RecoveredInformation recoveredInformation;

	public InformationRecoveryManager(String methodToCheck, RecoveredInformation recoveredInformation) {
		this.methodToCheck = methodToCheck;
		this.recoveredInformation = recoveredInformation;
	}

	public void processTypeDeclaration(JCompilationUnitType aJCompilationUnitType) {
		InformationRecoveryVisitor informationRecoveryVisitor = new InformationRecoveryVisitor(this.methodToCheck);

		aJCompilationUnitType.accept(informationRecoveryVisitor);
		
		this.recoveredInformation.setValidInformation(true);
		this.recoveredInformation.addFieldsName(informationRecoveryVisitor.getFieldsName());
		this.recoveredInformation.addStaticFieldsName(informationRecoveryVisitor.getStaticFieldsName());
		this.recoveredInformation.setMethodParametersNames(informationRecoveryVisitor.getMethodParametersNames());
	}

}
