package ar.edu.taco.jfsl;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import org.jmlspecs.checker.JmlClassDeclaration;
import org.jmlspecs.checker.JmlCompilationUnit;
import org.jmlspecs.checker.JmlConstructorDeclaration;
import org.jmlspecs.checker.JmlInterfaceDeclaration;
import org.jmlspecs.checker.JmlMethodDeclaration;
import org.jmlspecs.checker.JmlTypeDeclaration;
import org.multijava.javadoc.JavadocComment;
import org.multijava.mjc.JCompilationUnit;
import org.multijava.mjc.JCompilationUnitType;
import org.multijava.mjc.JFormalParameter;
import org.multijava.mjc.JTypeDeclarationType;

import ar.edu.jdynalloy.ast.JDynAlloyModule;
import ar.edu.jdynalloy.ast.JField;
import ar.edu.jdynalloy.ast.JModifies;
import ar.edu.jdynalloy.ast.JObjectInvariant;
import ar.edu.jdynalloy.ast.JPostcondition;
import ar.edu.jdynalloy.ast.JPrecondition;
import ar.edu.jdynalloy.ast.JProgramDeclaration;
import ar.edu.jdynalloy.ast.JRepresents;
import ar.edu.jdynalloy.ast.JSpecCase;
import ar.edu.jdynalloy.xlator.JType;
import ar.edu.taco.TacoException;
import ar.edu.taco.utils.jml.JmlAstTransverseStatementVisitor;
import ar.uba.dc.rfm.alloy.AlloyVariable;
import ar.uba.dc.rfm.alloy.ast.expressions.AlloyExpression;
import ar.uba.dc.rfm.alloy.ast.formulas.AlloyFormula;

public class JfslTranslatorJmlVisitor extends JmlAstTransverseStatementVisitor {

	public JfslTranslatorJmlVisitor(JfslToJDynAlloyEnv env) {
		super();
		this.env = env;
	}

	@Override
	public void visitJmlClassDeclaration(JmlClassDeclaration jmlClassDeclaration) {
		this.visitJmlTypeDeclaration(jmlClassDeclaration);
	}

	private final JfslToJDynAlloyEnv env;

	private void visitJfslMethodSpecification(
			JmlMethodDeclaration jmlMethodDeclaration,
			JProgramDeclaration jprogramDeclaration) {

		Vector<String> parameterIds = new Vector<String>();
		for (JFormalParameter parameter : jmlMethodDeclaration.parameters()) {
			String parameterIden = parameter.ident();
			parameterIds.add(parameterIden);
		}

		JavadocComment javadocComment = jmlMethodDeclaration.javadocComment();

		if (javadocComment != null) {
			String javadocText = javadocComment.text();

			JavadocParser javadocParser = new JavadocParser(env);
			JfslMethodSpecification method_specification = javadocParser
					.parse_method_comments(javadocText);

			for (JfslBehaviorCase behavior_case : method_specification.spec_cases) {
				List<JPrecondition> requires_list = new LinkedList<JPrecondition>();
				for (AlloyFormula requires_formula : behavior_case.requires) {
					requires_list.add(new JPrecondition(requires_formula));
				}

				List<JPostcondition> ensures_list = new LinkedList<JPostcondition>();
				for (AlloyFormula ensures_formula : behavior_case.ensures) {
					ensures_list.add(new JPostcondition(ensures_formula));
				}

				List<JModifies> modifies_list = new LinkedList<JModifies>();
				if (behavior_case.modifies_everything) {
					modifies_list.add(JModifies.buildModifiesEverything());
				} else {
					for (AlloyExpression modifies_expression : behavior_case.modifies) {
						modifies_list.add(new JModifies(modifies_expression));
					}
				}

				JSpecCase spec_case = new JSpecCase(requires_list,
						ensures_list, modifies_list);
				jprogramDeclaration.getSpecCases().add(spec_case);
			}
		}
	}

	@Override
	public void visitJmlInterfaceDeclaration(
			JmlInterfaceDeclaration jmlInterfaceDeclaration) {
		this.visitJmlTypeDeclaration(jmlInterfaceDeclaration);
	}

	@SuppressWarnings("unchecked")
	private void visitJmlTypeDeclaration(JmlTypeDeclaration jmlTypeDeclaration) {
		if (!this.env.containsSimpleJmlNodeMap(jmlTypeDeclaration))
			throw new IllegalStateException(
					"couldn't find JDynAlloy module for type "
							+ jmlTypeDeclaration.getCClass().getJavaName());

		JDynAlloyModule jmodule = this.env.get_module(jmlTypeDeclaration);
		this.visitJfslTypeSpecification(jmlTypeDeclaration, jmodule);

		if (jmlTypeDeclaration.inners() != null) {
			for (JmlClassDeclaration innerClassDeclaration : (ArrayList<JmlClassDeclaration>) jmlTypeDeclaration
					.inners()) {
				innerClassDeclaration.accept(this);
			}
		}

		for (JmlMethodDeclaration method : (ArrayList<JmlMethodDeclaration>) jmlTypeDeclaration
				.methods()) {
			method.accept(this);
		}
	}

	@Override
	public void visitJmlMethodDeclaration(
			JmlMethodDeclaration jmlMethodDeclaration) {
		if (!this.env.containsSimpleJmlNodeMap(jmlMethodDeclaration))
			throw new TacoException(
					"couldn't find JDynAlloy program declaration for java method "
							+ jmlMethodDeclaration.ident());

		JProgramDeclaration jprogramDeclaration = this.env
				.get_program_declaration(jmlMethodDeclaration);
		
		jprogramDeclaration.setPure(jmlMethodDeclaration.getMethod().isPure());

		this.visitJfslMethodSpecification(jmlMethodDeclaration,
				jprogramDeclaration);
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}

	private void visitJfslTypeSpecification(
			JmlTypeDeclaration jmlTypeDeclaration, JDynAlloyModule jmodule) {
		JavadocComment javadocComment = jmlTypeDeclaration.javadocComment();

		if (javadocComment != null) {

			String javadocText = javadocComment.text();

			JavadocParser javadocParser = new JavadocParser(env);
			JfslClassSpecification jfsl_class_specification = javadocParser
					.parse_class_comments(javadocText);

			for (AlloyFormula invariant_formula : jfsl_class_specification.invariant) {
				JObjectInvariant object_invariant = new JObjectInvariant(
						invariant_formula);
				jmodule.getObjectInvariants().add(object_invariant);
			}

			for (JfslSpecField jfsl_spec_field : jfsl_class_specification.spec_fields) {

				String className = jmodule.getSignature().getSignatureId();

				JType specFieldRelType;
				if (jfsl_spec_field.expressionType.isSet()) {

					String range = jfsl_spec_field.expressionType
							.singletonFrom();
					String source = String.format("%s->(%s)", className, range);
					specFieldRelType = JType.parse(source);

				} else if (jfsl_spec_field.expressionType.isSequence()) {

					String range = jfsl_spec_field.expressionType
							.singletonFrom();
					String source = String.format("%s->seq(%s)", className,
							range);
					specFieldRelType = JType.parse(source);

				} else {

					String source = String.format("%s->one(%s)", className,
							jfsl_spec_field.expressionType.toString());
					specFieldRelType = JType.parse(source);
				}

				JField jfield = new JField(new AlloyVariable(
						jfsl_spec_field.fieldId), specFieldRelType);

				JRepresents spec_field = new JRepresents(
						jfsl_spec_field.expression,
						jfsl_spec_field.expressionType, jfsl_spec_field.formula);

				jmodule.getFields().add(jfield);
				jmodule.getRepresents().add(spec_field);
			}
		}
	}

	@Override
	public void visitJmlConstructorDeclaration(
			JmlConstructorDeclaration jmlConstructorDeclaration) {

		if (!this.env.containsSimpleJmlNodeMap(jmlConstructorDeclaration))
			throw new IllegalStateException(
					"couldn't find JDynAlloy program declaration for java method "
							+ jmlConstructorDeclaration.ident());

		JProgramDeclaration jprogramDeclaration = this.env
				.get_program_declaration(jmlConstructorDeclaration);

		this.visitJfslMethodSpecification(jmlConstructorDeclaration,
				jprogramDeclaration);

	}

	public void visitCompilationUnitType(JCompilationUnitType n) {
		JTypeDeclarationType[] type_declarations = n.typeDeclarations();
		for (int i = 0; i < type_declarations.length; i++) {
			JTypeDeclarationType jTypeDeclarationType = type_declarations[i];
			jTypeDeclarationType.accept(this);
		}
	}

	@Override
	public void visitCompilationUnit(JCompilationUnit n) {
		visitCompilationUnitType(n);
	}

	@Override
	public void visitJmlCompilationUnit(JmlCompilationUnit n) {
		visitCompilationUnitType(n);
	}



}
