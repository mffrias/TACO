package ar.edu.taco.junit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jmlspecs.checker.JmlClassDeclaration;
import org.jmlspecs.checker.JmlCompilationUnit;
import org.jmlspecs.checker.JmlConstructorDeclaration;
import org.jmlspecs.checker.JmlFieldDeclaration;
import org.jmlspecs.checker.JmlMethodDeclaration;
import org.multijava.mjc.JFieldDeclarationType;
import org.multijava.mjc.JFormalParameter;
import org.multijava.mjc.JTypeDeclarationType;

import ar.edu.taco.TacoConfigurator;
import ar.edu.taco.junit.RecoveredInformation.StaticFieldInformation;
import ar.edu.taco.utils.jml.JmlAstTransverseStatementVisitor;

public class InformationRecoveryVisitor extends JmlAstTransverseStatementVisitor {

	private String methodToCheck;
	private List<String> methodParametersNames;

	private Map<String, List<String>> fieldsName;
	private Map<String, List<StaticFieldInformation>> staticFieldsName;

	private String currentClass = null;

	public InformationRecoveryVisitor(String methodToCheck) {
		this.methodToCheck = methodToCheck;
		this.fieldsName = new HashMap<String, List<String>>();
		this.staticFieldsName = new HashMap<String, List<StaticFieldInformation>>();
		this.methodParametersNames = new ArrayList<String>();
	}

	public Map<String, List<String>> getFieldsName() {
		return fieldsName;
	}

	public Map<String, List<StaticFieldInformation>> getStaticFieldsName() {
		return staticFieldsName;
	}

	/**
	 * 
	 * @return
	 */
	public List<String> getMethodParametersNames() {
		return methodParametersNames;
	}

	@Override
	public void visitJmlCompilationUnit(JmlCompilationUnit jmlCompilationUnit) {
		for (JTypeDeclarationType aJTypeDeclarationType : jmlCompilationUnit.typeDeclarations()) {
			aJTypeDeclarationType.accept(this);
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public void visitJmlClassDeclaration(JmlClassDeclaration jmlClassDeclaration) {
		// Transverse Inner Classes
		if (jmlClassDeclaration.inners() != null) {
			for (JmlClassDeclaration innerClassDeclaration : (ArrayList<JmlClassDeclaration>) jmlClassDeclaration.inners()) {
				innerClassDeclaration.accept(this);

			}
		}

		this.currentClass = jmlClassDeclaration.getCClass().getJavaName().replace("$", ".");

		for (JFieldDeclarationType jFieldDeclarationType : jmlClassDeclaration.fields()) {
			jFieldDeclarationType.accept(this);
		}

		for (JmlMethodDeclaration methodDeclaration : (ArrayList<JmlMethodDeclaration>) jmlClassDeclaration.methods()) {
			methodDeclaration.accept(this);
		}
	}

	@Override
	public void visitJmlFieldDeclaration(JmlFieldDeclaration jFieldDeclaration) {

		if (jFieldDeclaration.getField().isFieldStatic()) {
			StaticFieldInformation staticFieldInformation = new RecoveredInformation.StaticFieldInformation();
			String javaName = jFieldDeclaration.getField().getJavaName();
			staticFieldInformation.setClassName(javaName.substring(0, javaName.lastIndexOf(".")));
			staticFieldInformation.setFieldName(jFieldDeclaration.ident());

			if (!staticFieldsName.containsKey(this.currentClass)) {
				staticFieldsName.put(this.currentClass, new ArrayList<RecoveredInformation.StaticFieldInformation>());
			}


			this.staticFieldsName.get(this.currentClass).add(staticFieldInformation);
		} else {
			if (!fieldsName.containsKey(this.currentClass)) {
				fieldsName.put(this.currentClass, new ArrayList<String>());
			}

			this.fieldsName.get(this.currentClass).add(jFieldDeclaration.ident());
		}

	}

	@Override
	public void visitJmlMethodDeclaration(JmlMethodDeclaration jMethodDeclaration) {
		String methodName = jMethodDeclaration.ident();

		Boolean inputEqualsConsructorToCheck = methodName.equals(this.methodToCheck);
		String originalMethodNameWithParameterTypes = TacoConfigurator.getInstance().getMethodToCheck();
		String parameterTypes = originalMethodNameWithParameterTypes.substring(originalMethodNameWithParameterTypes.indexOf('('));
		String paramTypesWithoutParenthesis = parameterTypes.substring(1, parameterTypes.length()-1);
		String[] theTypes = paramTypesWithoutParenthesis.split(",");
		if (theTypes.length == jMethodDeclaration.parameters().length){
			int index = 0;
			for (JFormalParameter aJFormalParameter : jMethodDeclaration.parameters()){
				String s1 = aJFormalParameter.typeToString();
				String s2 = theTypes[index];
				if (!s1.equals(s2))
					inputEqualsConsructorToCheck = false;
				index++;
			}
		} else {
			inputEqualsConsructorToCheck = false;
		}
		if (inputEqualsConsructorToCheck) {
			for (JFormalParameter aJFormalParameter : jMethodDeclaration.parameters()) {
				this.methodParametersNames.add(aJFormalParameter.ident());
			}			
		}
	}	

	@Override
	public void visitJmlConstructorDeclaration(JmlConstructorDeclaration jmlConstructorDeclaration) {
		String methodName = jmlConstructorDeclaration.ident();

		Boolean inputEqualsConsructorToCheck = methodName.equals(this.methodToCheck);
		String originalMethodNameWithParameterTypes = TacoConfigurator.getInstance().getMethodToCheck();
		String parameterTypes = originalMethodNameWithParameterTypes.substring(originalMethodNameWithParameterTypes.indexOf('('));
		String paramTypesWithoutParenthesis = parameterTypes.substring(1, parameterTypes.length()-1);
		String[] theTypes = paramTypesWithoutParenthesis.split(",");
		if (theTypes.length == jmlConstructorDeclaration.parameters().length){
			int index = 0;
			for (JFormalParameter aJFormalParameter : jmlConstructorDeclaration.parameters()){
				String s1 = aJFormalParameter.typeToString();
				String s2 = theTypes[index];
				if (!s1.equals(s2))
					inputEqualsConsructorToCheck = false;
				index++;
			}
		} else {
			inputEqualsConsructorToCheck = false;
		}
		if (inputEqualsConsructorToCheck) {
			for (JFormalParameter aJFormalParameter : jmlConstructorDeclaration.parameters()) {
				this.methodParametersNames.add(aJFormalParameter.ident());
			}			
		}
	}
}
