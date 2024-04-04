/*
 * TACO: Translation of Annotated COde
 * Copyright (c) 2010 Universidad de Buenos Aires
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA,
 * 02110-1301, USA
 */
package ar.edu.taco.simplejml.helpers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;

import org.jmlspecs.checker.JmlConstructorDeclaration;
import org.jmlspecs.checker.JmlFormalParameter;
import org.jmlspecs.checker.JmlMethodDeclaration;
import org.multijava.mjc.CType;
import org.multijava.mjc.CVoidType;
import org.multijava.mjc.JCompilationUnitType;
import org.multijava.mjc.JFormalParameter;

import ar.edu.jdynalloy.ast.JModifies;
import ar.edu.jdynalloy.ast.JPostcondition;
import ar.edu.jdynalloy.ast.JPrecondition;
import ar.edu.jdynalloy.ast.JProgramDeclaration;
import ar.edu.jdynalloy.ast.JSpecCase;
import ar.edu.jdynalloy.ast.JVariableDeclaration;
import ar.edu.jdynalloy.buffer.DynJMLAlloyModuleBuffer;
import ar.edu.jdynalloy.factory.JDynAlloyFactory;
import ar.edu.jdynalloy.factory.JExpressionFactory;
import ar.edu.jdynalloy.xlator.JType;
import ar.edu.taco.TacoException;
import ar.edu.taco.simplejml.JDynAlloyASTVisitor;
import ar.edu.taco.simplejml.JmlExpressionVisitor;
import ar.edu.taco.simplejml.JmlBaseExpressionVisitor.JmlMethodDeclarationResult;
import ar.uba.dc.rfm.alloy.AlloyTyping;
import ar.uba.dc.rfm.alloy.AlloyVariable;
import ar.uba.dc.rfm.alloy.ast.expressions.AlloyExpression;
import ar.uba.dc.rfm.alloy.ast.expressions.ExprJoin;
import ar.uba.dc.rfm.alloy.ast.expressions.ExprVariable;
import ar.uba.dc.rfm.alloy.ast.formulas.AlloyFormula;
import ar.uba.dc.rfm.alloy.ast.formulas.EqualsFormula;
import ar.uba.dc.rfm.alloy.ast.formulas.NotFormula;

/**
 * @author elgaby
 * 
 */
public class MethodDeclarationSolver {
	
	
	
	/**
	 *
	 * @param methodDeclaration
	 * @param scope
	 * @param buffer
	 * @return
	 */
	public static JProgramDeclaration getMethodDeclaration(JmlMethodDeclaration methodDeclaration, DynJMLAlloyModuleBuffer buffer, 
			Map<String, List<String>> modulesObjectState, Map<String, 
			List<String>> modulesNoStaticFields,
			AlloyTyping varsResultOfArithmeticOperationsInObjectInvariants,
			List<AlloyFormula> predsResultOfArithmeticOperationsInObjectInvariants,
			List<JCompilationUnitType> compilationUnits) {
		CTypeAdapter typeAdapter = new CTypeAdapter();

		List<JSpecCase> jSpecCases = new ArrayList<JSpecCase>();
		AlloyTyping varsResultOfArithmeticOperationsInRequiresAndEnsures = new AlloyTyping();
		ArrayList<AlloyFormula> predsResultOfArithmeticOperationsInRequiresAndEnsures = new ArrayList<AlloyFormula>();

		translateJmlAnnotations(methodDeclaration, jSpecCases, buffer, modulesObjectState, modulesNoStaticFields, 
				varsResultOfArithmeticOperationsInRequiresAndEnsures, predsResultOfArithmeticOperationsInRequiresAndEnsures,
				compilationUnits);

		List<JPrecondition> nullityRequires = new ArrayList<JPrecondition>();
		List<JPostcondition> nullityEnsures = new ArrayList<JPostcondition>();

		List<JVariableDeclaration> arguments = MethodDeclarationSolver.getMethodArguments(methodDeclaration.parameters(), nullityRequires, nullityEnsures);

		String programId = String.valueOf(methodDeclaration.ident());
		if (methodDeclaration.isConstructor()) {
			// mfrias + Constructor			
			//			programId = "Constructor";
			programId = methodDeclaration.ident();
		}


		//mfrias-25/7/2013: Aca puede quedar desfasada la cantidad de parametros.
		int argumentsLength = methodDeclaration.parameters().length + varsResultOfArithmeticOperationsInRequiresAndEnsures.varSet().size()
				+ varsResultOfArithmeticOperationsInObjectInvariants.varSet().size();

		ArgEncoder call = new ArgEncoder(methodDeclaration.isStatic(), methodDeclaration.isConstructor(), !isVoidType(methodDeclaration.returnType()),
				argumentsLength);

		JVariableDeclaration leftDeclaration = new JVariableDeclaration(JExpressionFactory.THIS_VARIABLE, buffer.getThisType());

		JVariableDeclaration returnDeclaration = null;


		if (methodDeclaration.isConstructor()) {
			returnDeclaration = new JVariableDeclaration(JExpressionFactory.RETURN_VARIABLE, buffer.getThisType());
		} else if (!isVoidType(methodDeclaration.returnType())) {
			JType parameterType = typeAdapter.translate(methodDeclaration.returnType());
			returnDeclaration = new JVariableDeclaration(JExpressionFactory.RETURN_VARIABLE, parameterType);
			if (methodDeclaration.isDeclaredNonNull()) {
				AlloyExpression varPrimedExpression = new ExprVariable(new AlloyVariable(returnDeclaration.getVariable().getVariableId(), true));
				AlloyFormula postNullityFormula = new NotFormula(new EqualsFormula(varPrimedExpression, JExpressionFactory.NULL_EXPRESSION));

				nullityEnsures.add(new JPostcondition(postNullityFormula));
			}
		}


		if (nullityRequires.size() > 0 || nullityEnsures.size() > 0) {
			for (JSpecCase jSpecCase : jSpecCases) {
				jSpecCase.getRequires().addAll(nullityRequires);
			}
			//jSpecCases.add(new JSpecCase(nullityRequires, nullityEnsures, emptyModifies));
		}

		Vector<JVariableDeclaration> variableDeclaration;
		variableDeclaration = call.encode(leftDeclaration, JDynAlloyFactory.THROW_DECLARATION, returnDeclaration, arguments);

//		System.out.println("Method Id: " + methodDeclaration.ident());
		JProgramDeclaration programDeclaration = new JProgramDeclaration(
				methodDeclaration.isAbstract(), 
				methodDeclaration.isConstructor(),
				JmlMethodDeclaration.hasFlag(methodDeclaration.modifiers(), JmlMethodDeclaration.ACC_PURE),
				buffer.getSignatureId(), 
				programId, 
				variableDeclaration, 
				jSpecCases, 
				null /* program */, 
				varsResultOfArithmeticOperationsInRequiresAndEnsures, 
				predsResultOfArithmeticOperationsInRequiresAndEnsures);

		return programDeclaration;
	}


	public static JProgramDeclaration getPureMethodDeclaration(JmlMethodDeclaration methodDeclaration, DynJMLAlloyModuleBuffer buffer, 
			Map<String, List<String>> modulesObjectState, Map<String, 
			List<String>> modulesNoStaticFields,
			AlloyTyping varsResultOfArithmeticOperationsInObjectInvariants,
			List<AlloyFormula> predsResultOfArithmeticOperationsInObjectInvariants,
			List<JCompilationUnitType> compilationUnits) {
		CTypeAdapter typeAdapter = new CTypeAdapter();

		List<JSpecCase> jSpecCases = new ArrayList<JSpecCase>();
		AlloyTyping varsResultOfArithmeticOperationsInRequiresAndEnsures = new AlloyTyping();
		ArrayList<AlloyFormula> predsResultOfArithmeticOperationsInRequiresAndEnsures = new ArrayList<AlloyFormula>();

		translateJmlAnnotations(methodDeclaration, jSpecCases, buffer, modulesObjectState, modulesNoStaticFields, 
				varsResultOfArithmeticOperationsInRequiresAndEnsures, predsResultOfArithmeticOperationsInRequiresAndEnsures,
				compilationUnits);

		List<JPrecondition> nullityRequires = new ArrayList<JPrecondition>();
		List<JPostcondition> nullityEnsures = new ArrayList<JPostcondition>();

		List<JVariableDeclaration> arguments = MethodDeclarationSolver.getMethodArguments(methodDeclaration.parameters(), nullityRequires, nullityEnsures);

		String programId = String.valueOf(methodDeclaration.ident());


		//mfrias-25/7/2013: Aca puede quedar desfasada la cantidad de parametros.
		int argumentsLength = methodDeclaration.parameters().length + varsResultOfArithmeticOperationsInRequiresAndEnsures.varSet().size()
				+ varsResultOfArithmeticOperationsInObjectInvariants.varSet().size();

		ArgEncoder call = new ArgEncoder(methodDeclaration.isStatic(), methodDeclaration.isConstructor(), !isVoidType(methodDeclaration.returnType()),
				argumentsLength);

		JVariableDeclaration leftDeclaration = new JVariableDeclaration(JExpressionFactory.THIS_VARIABLE, buffer.getThisType());

		JVariableDeclaration returnDeclaration = null;


		if (!isVoidType(methodDeclaration.returnType())) {
			JType parameterType = typeAdapter.translate(methodDeclaration.returnType());
			returnDeclaration = new JVariableDeclaration(JExpressionFactory.RETURN_VARIABLE, parameterType);
			if (methodDeclaration.isDeclaredNonNull()) {
				AlloyExpression varPrimedExpression = new ExprVariable(new AlloyVariable(returnDeclaration.getVariable().getVariableId(), true));
				AlloyFormula postNullityFormula = new NotFormula(new EqualsFormula(varPrimedExpression, JExpressionFactory.NULL_EXPRESSION));

				nullityEnsures.add(new JPostcondition(postNullityFormula));
			}
		}

		//Pure methods do not accept modifies.
		for (JSpecCase jSpecCase : jSpecCases) {
			jSpecCase.getModifies().clear();
		}


		if (nullityRequires.size() > 0 || nullityEnsures.size() > 0) {
			for (JSpecCase jSpecCase : jSpecCases) {
				jSpecCase.getRequires().addAll(nullityRequires);
			}
			//jSpecCases.add(new JSpecCase(nullityRequires, nullityEnsures, emptyModifies));
		}

		Vector<JVariableDeclaration> variableDeclaration;
		variableDeclaration = call.encodePure(leftDeclaration, returnDeclaration, arguments);

		JProgramDeclaration programDeclaration = new JProgramDeclaration(
				methodDeclaration.isAbstract(), 
				methodDeclaration.isConstructor(),
				methodDeclaration.getMethod().isPure(),
				buffer.getSignatureId(), 
				programId, 
				variableDeclaration, 
				jSpecCases, 
				null /* program */, 
				varsResultOfArithmeticOperationsInRequiresAndEnsures, 
				predsResultOfArithmeticOperationsInRequiresAndEnsures);

		return programDeclaration;
	}



	/**
	 * 
	 * @param constructorDeclaration
	 * @param buffer
	 * @param compilationUnits 
	 * @return
	 */
	public static JProgramDeclaration getConstructorDeclaration(
			JmlConstructorDeclaration constructorDeclaration, 
			DynJMLAlloyModuleBuffer buffer, Map<String, List<String>> modulesObjectState, 
			Map<String, List<String>> modulesNoStaticFields, 
			AlloyTyping varsResultOfArithmeticOperationsInObjectInvariants,
			List<AlloyFormula> predsResultOfArithmeticOperationsInObjectInvariants,
			List<JCompilationUnitType> compilationUnits) {

		List<JSpecCase> jSpecCases = new ArrayList<JSpecCase>();

		AlloyTyping varsResultOfArithmeticOperationsInRequiresAndEnsures = new AlloyTyping();
		ArrayList<AlloyFormula> predsResultOfArithmeticOperationsInRequiresAndEnsures = new ArrayList<AlloyFormula>();

		translateJmlAnnotations(constructorDeclaration, jSpecCases, buffer, modulesObjectState, modulesNoStaticFields,
				varsResultOfArithmeticOperationsInRequiresAndEnsures, 
				predsResultOfArithmeticOperationsInRequiresAndEnsures,
				compilationUnits);

		List<JPrecondition> nullityRequires = new ArrayList<JPrecondition>();
		List<JPostcondition> nullityEnsures = new ArrayList<JPostcondition>();

		List<JVariableDeclaration> arguments = MethodDeclarationSolver.getMethodArguments(constructorDeclaration.parameters(), nullityRequires, nullityEnsures);
		if (nullityRequires.size() > 0 || nullityEnsures.size() > 0) {
			for (JSpecCase jSpecCase : jSpecCases) {
				jSpecCase.getRequires().addAll(nullityRequires);
			}
		}

		for (JSpecCase jSpecCase : jSpecCases) {
			jSpecCase.getModifies().clear();
		}

		String classJavaName = JDynAlloyASTVisitor.getCurrentModuleName();

		AlloyExpression prefixVariable = JExpressionFactory.THIS_EXPRESSION;

		JavaClassNameNormalizer classNameNormalizer = new JavaClassNameNormalizer(classJavaName);

		String normalizedClassName = classNameNormalizer.getQualifiedClassName();

		if (modulesNoStaticFields.containsKey(normalizedClassName)) {
			List<String> fieldsNames = modulesNoStaticFields.get(normalizedClassName);
			List<JModifies> modifies = new ArrayList<JModifies>();
			for (String fieldName : fieldsNames) {
				AlloyExpression variable = ExprVariable.buildExprVariable(fieldName);
				variable = new ExprJoin(prefixVariable, variable);

				modifies.add(new JModifies(variable));					
			}
			for (JSpecCase jSpecCase : jSpecCases) {
				jSpecCase.getModifies().addAll(modifies);
			}

		} else {
			throw new TacoException("Class " + classJavaName + " must be added in config as a relevant class");
		}


		String programId = String.valueOf(constructorDeclaration.ident());
		//		if (constructorDeclaration.isConstructor()) {
		//			//mfrias + Constructor
		//			programId = "Constructor";
		//		}

		int argumentsLength = (arguments != null) ? arguments.size() : 0;

		ArgEncoder call = new ArgEncoder(constructorDeclaration.isStatic(), constructorDeclaration.isConstructor(), false /* returnType */, argumentsLength);

		JVariableDeclaration leftDeclaration = new JVariableDeclaration(JExpressionFactory.THIS_VARIABLE, buffer.getThisType());

		JVariableDeclaration returnDeclaration = null;

		if (constructorDeclaration.isConstructor()) {
			returnDeclaration = new JVariableDeclaration(JExpressionFactory.RETURN_VARIABLE, buffer.getThisType());
		}

		Vector<JVariableDeclaration> variableDeclaration = call.encode(leftDeclaration, JDynAlloyFactory.THROW_DECLARATION, returnDeclaration, arguments);
		JProgramDeclaration programDeclaration = new JProgramDeclaration(constructorDeclaration.isAbstract(), true,  false, buffer.getSignatureId(), programId, variableDeclaration, 
				jSpecCases, null /* program */, varsResultOfArithmeticOperationsInRequiresAndEnsures, predsResultOfArithmeticOperationsInRequiresAndEnsures);

		return programDeclaration;
	}

	/**
	 * 
	 * @param methodDeclaration
	 * @return
	 */
	private static List<JVariableDeclaration> getMethodArguments(JFormalParameter[] parameters, List<JPrecondition> nullityRequires, List<JPostcondition> nullityEnsures) {
		List<JVariableDeclaration> processedArguments = null;
		if (parameters != null) {
			processedArguments = new ArrayList<JVariableDeclaration>();
			for (JFormalParameter parameter : parameters) {
				CTypeAdapter typeAdapter = new CTypeAdapter();
				JType jType = typeAdapter.translate(parameter.getType());


				JVariableDeclaration variableDeclaration = new JVariableDeclaration(AlloyVariable.buildAlloyVariable(parameter.ident()), jType);
				processedArguments.add(variableDeclaration);

				// If the arguments is set as non_null, we'll add an assume class to specify it.
				if (parameter instanceof JmlFormalParameter) {
					if (parameter.isDeclaredNonNull()) {
						AlloyExpression varExpression = new ExprVariable(variableDeclaration.getVariable());
						AlloyFormula preNullityFormula = new NotFormula(new EqualsFormula(varExpression, JExpressionFactory.NULL_EXPRESSION));

						AlloyExpression varPrimedExpression = new ExprVariable(new AlloyVariable(variableDeclaration.getVariable().getVariableId(), true));
						AlloyFormula postNullityFormula = new NotFormula(new EqualsFormula(varPrimedExpression, JExpressionFactory.NULL_EXPRESSION));

						nullityRequires.add(new JPrecondition(preNullityFormula));
						nullityEnsures.add(new JPostcondition(postNullityFormula));				
					}
				}
			}
		}
		return processedArguments;
	}


	private static boolean isVoidType(CType type) {
		return type instanceof CVoidType;
	}

	private static void translateJmlAnnotations(JmlMethodDeclaration jmlMethodDeclaration, List<JSpecCase> jSpecCases, DynJMLAlloyModuleBuffer buffer, 
			Map<String, List<String>> modulesObjectState, Map<String, List<String>> modulesNoStaticFields, 
			AlloyTyping varsAndTheirTypesFromMathOperatorsInRequiresAndEnsures,
			List<AlloyFormula> predsFromMathOperatorsInRequiresAndEnsures, 
			List<JCompilationUnitType> compilationUnits) {

		JmlExpressionVisitor jmlExpressionVisitor = new JmlExpressionVisitor(compilationUnits);
		jmlExpressionVisitor.setBuffer(buffer);
		jmlExpressionVisitor.setContractTranslation(true);
		jmlExpressionVisitor.setModulesObjectState(modulesObjectState);
		jmlExpressionVisitor.setModulesNoStaticFields(modulesNoStaticFields);
		if (jmlMethodDeclaration.hasSpecification()) {
			jmlMethodDeclaration.methodSpecification().accept(jmlExpressionVisitor);			
		}

		JmlMethodDeclarationResult jmlMethodDeclarationResult = jmlExpressionVisitor.getJmlMethodDeclarationResult();
		
		for (AlloyVariable av : jmlExpressionVisitor.getVarsEncodingValueOfArithmeticOperationsInRequiresAndEnsures()){
			av.setIsVariableFromContract();
			varsAndTheirTypesFromMathOperatorsInRequiresAndEnsures.put(av, jmlExpressionVisitor.getVarsEncodingValueOfArithmeticOperationsInRequiresAndEnsures().get(av));
			
		}
		predsFromMathOperatorsInRequiresAndEnsures.addAll(jmlExpressionVisitor.getPredsEncodingValueOfArithmeticOperationsInRequiresAndEnsures());


		for (Iterator<JSpecCase> it = jmlMethodDeclarationResult.getJSpecCases(); it.hasNext();) {
			JSpecCase jSpecCase = it.next();
			jSpecCases.add(jSpecCase);
		}

		jmlMethodDeclaration.modifiers();

	}
}
