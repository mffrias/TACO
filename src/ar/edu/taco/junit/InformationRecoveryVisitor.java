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

/**
 * Collects, from one JML compilation unit, the names that {@link InformationRecoveryManager}
 * stores in a {@link RecoveredInformation}: the instance fields and the static fields of each
 * class (nested classes included), and the parameter names of the method under analysis. The
 * visit methods return nothing; the results are read with {@link #getFieldsName()},
 * {@link #getStaticFieldsName()} and {@link #getMethodParametersNames()}.
 *
 * <p>Both maps are keyed by the class's Java name with its {@code $} replaced by
 * {@code .}; a static field's class name is the field's Java name up to its last dot.
 *
 * <p>The method under analysis is recognised by its name and its parameter types:
 * {@code TacoConfigurator.getClassToCheck()} (the class, dots as underscores), an underscore
 * and the method's name must equal the {@code methodToCheck} given to the constructor, and the
 * declared types must equal those listed in {@code TacoConfigurator.getMethodToCheck()} with
 * their underscores turned back into dots, so a type whose name has an underscore never
 * matches (docs/METHOD-NAMING.md). Constructors are never recognised under the new naming (see
 * {@link #visitJmlConstructorDeclaration}). The visitor reads the {@code TacoConfigurator}
 * singleton, so it must run while the configuration of the TACO run being reported is loaded.
 */
public class InformationRecoveryVisitor extends JmlAstTransverseStatementVisitor {

	private String methodToCheck;
	private List<String> methodParametersNames;

	private Map<String, List<String>> fieldsName;
	private Map<String, List<StaticFieldInformation>> staticFieldsName;

	private String currentClass = null;

	/**
	 * Creates a visitor for one method under analysis.
	 *
	 * @param methodToCheck the method's qualified name without its parameter types
	 *        ({@code pkg_Class_method}), as {@code SnapshotStage} passes it
	 */
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

	/**
	 * Appends the method's parameter names to the result if it is the method under analysis (see
	 * the class comment for the test). The test uses the class under analysis, not the current
	 * class, so a method of a nested class with the same name and parameter types matches too.
	 * Assumes that {@code TacoConfigurator.getMethodToCheck()} contains {@code '('}.
	 *
	 * @param jMethodDeclaration the method to check
	 */
	@Override
	public void visitJmlMethodDeclaration(JmlMethodDeclaration jMethodDeclaration) {
		String methodName = jMethodDeclaration.ident();
		String typedMethodName = TacoConfigurator.getInstance().getClassToCheck() + "_" + methodName;
		Boolean inputEqualsConsructorToCheck = typedMethodName.equals(this.methodToCheck);
		String originalMethodNameWithParameterTypes = TacoConfigurator.getInstance().getMethodToCheck();
		String parameterTypes = originalMethodNameWithParameterTypes.substring(originalMethodNameWithParameterTypes.indexOf('('));
		String paramTypesWithoutParenthesis = parameterTypes.substring(1, parameterTypes.length()-1);
		String[] theTypes = paramTypesWithoutParenthesis.split(",");
		if (theTypes.length == jMethodDeclaration.parameters().length){
			int index = 0;
			for (JFormalParameter aJFormalParameter : jMethodDeclaration.parameters()){
				String s1 = aJFormalParameter.typeToString();
				String s2 = theTypes[index].replaceAll("_", ".");
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

	/**
	 * Does for a constructor what {@link #visitJmlMethodDeclaration} does for a method, but
	 * compares the constructor's bare name ({@code ident()}, the class's simple name) with the
	 * qualified {@code methodToCheck}, which never equals it under the new naming, and compares
	 * the types without turning their underscores into dots. So no constructor's parameter names
	 * are recorded, and {@code SnapshotBuilder.createSnapshot()} fails for a constructor with
	 * parameters. Unclear: whether this is intended (docs/OPEN-QUESTIONS.md, item 6).
	 *
	 * @param jmlConstructorDeclaration the constructor to check
	 */
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
