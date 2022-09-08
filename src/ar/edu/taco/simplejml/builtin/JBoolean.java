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
package ar.edu.taco.simplejml.builtin;

import static ar.edu.jdynalloy.factory.JExpressionFactory.FALSE_EXPRESSION;
import static ar.edu.jdynalloy.factory.JExpressionFactory.NULL_EXPRESSION;
import static ar.edu.jdynalloy.factory.JExpressionFactory.RETURN_EXPRESSION;
import static ar.edu.jdynalloy.factory.JExpressionFactory.RETURN_VARIABLE;
import static ar.edu.jdynalloy.factory.JExpressionFactory.THIS_EXPRESSION;
import static ar.edu.jdynalloy.factory.JExpressionFactory.THIS_VARIABLE;
import static ar.edu.jdynalloy.factory.JExpressionFactory.TRUE_EXPRESSION;
import static ar.edu.jdynalloy.factory.JPredicateFactory.eq;
import static ar.edu.jdynalloy.xlator.JType.parse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import ar.edu.jdynalloy.ast.JDynAlloyModule;
import ar.edu.jdynalloy.ast.JAlloyProgramBuffer;
import ar.edu.jdynalloy.ast.JAssignment;
import ar.edu.jdynalloy.ast.JAssume;
import ar.edu.jdynalloy.ast.JClassConstraint;
import ar.edu.jdynalloy.ast.JClassInvariant;
import ar.edu.jdynalloy.ast.JField;
import ar.edu.jdynalloy.ast.JObjectConstraint;
import ar.edu.jdynalloy.ast.JObjectInvariant;
import ar.edu.jdynalloy.ast.JProgramDeclaration;
import ar.edu.jdynalloy.ast.JRepresents;
import ar.edu.jdynalloy.ast.JSignature;
import ar.edu.jdynalloy.ast.JSpecCase;
import ar.edu.jdynalloy.ast.JStatement;
import ar.edu.jdynalloy.ast.JVariableDeclaration;
import ar.edu.jdynalloy.buffer.StaticFieldsModuleBuilder;
import ar.edu.jdynalloy.factory.JDynAlloyFactory;
import ar.edu.jdynalloy.factory.JExpressionFactory;
import ar.edu.jdynalloy.factory.JPredicateFactory;
import ar.edu.jdynalloy.factory.JSignatureFactory;
import ar.edu.jdynalloy.xlator.JDynAlloyTyping;
import ar.edu.jdynalloy.xlator.JType;
import ar.edu.taco.simplejml.helpers.ArgEncoder;
import ar.uba.dc.rfm.alloy.AlloyTyping;
import ar.uba.dc.rfm.alloy.AlloyVariable;
import ar.uba.dc.rfm.alloy.ast.expressions.ExprJoin;
import ar.uba.dc.rfm.alloy.ast.expressions.ExprVariable;
import ar.uba.dc.rfm.alloy.ast.formulas.AlloyFormula;
import ar.uba.dc.rfm.alloy.ast.formulas.PredicateFormula;

public class JBoolean implements IBuiltInModule {

	private final JDynAlloyModule module;

	private JBoolean() {
		super();

		JDynAlloyTyping booleanFields = new JDynAlloyTyping();
		List<JField> fields = new LinkedList<JField>();
		
		JField booleanValueField = 
				new JField(new AlloyVariable("booleanValue"), 
						JType.parse("java_lang_Boolean -> one boolean"));
		
		JField trueConstantField = 
				new JField(new AlloyVariable("java_lang_Boolean_TRUE"), 
						JType.parse("ClassFields -> one java_lang_Boolean"));

		JField falseConstantField = 
				new JField(new AlloyVariable("java_lang_Boolean_FALSE"), 
						JType.parse("ClassFields -> one java_lang_Boolean"));

		
		StaticFieldsModuleBuilder.getInstance().addStaticField(trueConstantField);
		StaticFieldsModuleBuilder.getInstance().addStaticField(falseConstantField);
		
		fields.add(booleanValueField);

		JSignature signature = JSignatureFactory.buildClass(false,
				"java_lang_Boolean", booleanFields, "java_lang_Object",
				Collections.<String> emptySet());

		JSignature classSignature;
		classSignature = null;

		JProgramDeclaration booleanConstructor = buildConstructor();
		JProgramDeclaration booleanEquals = buildEquals();
		JProgramDeclaration booleanValue = buildBooleanValue();

		Set<JProgramDeclaration> programs = new HashSet<JProgramDeclaration>();
		programs.add(booleanConstructor);
		programs.add(booleanEquals);
		programs.add(booleanValue);
		this.module = new JDynAlloyModule("java_lang_Boolean", signature,
				classSignature, null, fields,
				Collections.<JClassInvariant> emptySet(),
				Collections.<JClassConstraint> emptySet(),
				Collections.<JObjectInvariant> emptySet(),
				Collections.<JObjectConstraint> emptySet(),
				Collections.<JRepresents> emptySet(), programs, 
				new AlloyTyping(), new ArrayList<AlloyFormula>(), false);

	}

	private JProgramDeclaration buildBooleanValue() {
		JVariableDeclaration thisDeclaration = new JVariableDeclaration(
				THIS_VARIABLE, parse("java_lang_Boolean"));

		JVariableDeclaration returnDeclaration = new JVariableDeclaration(
				RETURN_VARIABLE, parse("boolean"));

		ArgEncoder encoder = new ArgEncoder(false, false, true, 0);
		List<JVariableDeclaration> ps = encoder.encode(thisDeclaration,
				JDynAlloyFactory.THROW_DECLARATION, returnDeclaration,
				Collections.<JVariableDeclaration> emptyList());

		JStatement body = JDynAlloyFactory.block(JDynAlloyFactory
				.initializeThrow(), new JAssignment(RETURN_EXPRESSION,
				new ExprJoin(THIS_EXPRESSION, BOOLEAN_VALUE_EXPR)));
		JProgramDeclaration booleanValue = new JProgramDeclaration(false, false, false,
				"java_lang_Boolean", "booleanValue", ps,
				Collections.<JSpecCase> emptyList(), body, new AlloyTyping(), new ArrayList<AlloyFormula>());

		return booleanValue;
	}

	private static final AlloyVariable BOOLEAN_VALUE = new AlloyVariable(
			"booleanValue");
	private static final ExprVariable BOOLEAN_VALUE_EXPR = new ExprVariable(
			BOOLEAN_VALUE);

	private JProgramDeclaration buildEquals() {
		AlloyVariable obj = new AlloyVariable("obj");

		JVariableDeclaration thisDeclaration = new JVariableDeclaration(
				THIS_VARIABLE, parse("java_lang_Boolean"));
		JVariableDeclaration objDeclaration = new JVariableDeclaration(obj,
				parse("java_lang_Object"));
		JVariableDeclaration returnDeclaration = new JVariableDeclaration(
				RETURN_VARIABLE, parse("boolean"));

		JAlloyProgramBuffer buffer = new JAlloyProgramBuffer();
		ExprVariable objExpr = new ExprVariable(obj);
		buffer.openIf(eq(objExpr, NULL_EXPRESSION));
		buffer.assign(RETURN_VARIABLE, FALSE_EXPRESSION);
		buffer.switchToElseIf();

		PredicateFormula typeCondition;
		typeCondition = JPredicateFactory.instanceOf(objExpr,
				"java_lang_Boolean");

		buffer.openIf(typeCondition);
		buffer.assign(RETURN_VARIABLE, FALSE_EXPRESSION);
		buffer.switchToElseIf();
		buffer.openIf(eq(new ExprJoin(THIS_EXPRESSION, BOOLEAN_VALUE_EXPR),
				new ExprJoin(objExpr, BOOLEAN_VALUE_EXPR)));
		buffer.assign(RETURN_VARIABLE, TRUE_EXPRESSION);
		buffer.switchToElseIf();
		buffer.assign(RETURN_VARIABLE, FALSE_EXPRESSION);
		buffer.closeIf();
		buffer.closeIf();
		buffer.closeIf();

		JStatement body = JDynAlloyFactory.block(
				JDynAlloyFactory.initializeThrow(), buffer.toJAlloyProgram());

		ArgEncoder encoder = new ArgEncoder(false, false, true, 1);
		Vector<JVariableDeclaration> ps = encoder.encode(thisDeclaration,
				JDynAlloyFactory.THROW_DECLARATION, returnDeclaration,
				Collections
						.<JVariableDeclaration> singletonList(objDeclaration));
		return new JProgramDeclaration(false, false, true, "java_lang_Boolean", "equals",
				ps, Collections.<JSpecCase> emptyList(), body, new AlloyTyping(), new ArrayList<AlloyFormula>());
	}

	private JProgramDeclaration buildConstructor() {
		JVariableDeclaration thisDeclaration = new JVariableDeclaration(
				JExpressionFactory.THIS_VARIABLE,
				JType.parse("java_lang_Boolean"));

		final AlloyVariable value = new AlloyVariable("value");
		final ExprVariable valueExpr = new ExprVariable(value);

		JVariableDeclaration valueDeclaration = new JVariableDeclaration(value,
				JType.parse("boolean"));

		ArgEncoder encoder = new ArgEncoder(false, true, false, 1);
		List<JVariableDeclaration> ps = encoder
				.encode(thisDeclaration,
						JDynAlloyFactory.THROW_DECLARATION,
						null,
						Collections
								.<JVariableDeclaration> singletonList(valueDeclaration));

		ExprJoin thiz_booleanValue = new ExprJoin(THIS_EXPRESSION,
				BOOLEAN_VALUE_EXPR);
		JStatement body;
		// if (DynJAlloyConfig.getInstance().getDynamicJavaLangFields() == true)
		// body = new JAssignment(thiz_booleanValue, valueExpr);
		// else
		body = new JAssume(JPredicateFactory.eq(thiz_booleanValue, valueExpr));

		body = JDynAlloyFactory.block(JDynAlloyFactory.initializeThrow(), body);

		JProgramDeclaration constructor = new JProgramDeclaration(false, true, false,
				"java_lang_Boolean", "Constructor", ps,
				Collections.<JSpecCase> emptyList(), body, new AlloyTyping(), new ArrayList<AlloyFormula>());

		return constructor;
	}

	private static JBoolean instance = null;

	public static JBoolean getInstance() {
		if (instance == null)
			instance = new JBoolean();
		return instance;
	}

	@Override
	public JDynAlloyModule getModule() {
		return module;
	}


}
