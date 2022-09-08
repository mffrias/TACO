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
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
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

public class JInteger implements IBuiltInModule {

//	private static final JBindingKey INTEGER_CONSTRUCTOR_BINDING_KEY = new JBindingKey(
//			"Ljava/lang/Integer;.(I)V");
//	private static final JBindingKey INTEGER_INTVALUE_BINDING_KEY = new JBindingKey(
//			"Ljava/lang/Integer;.intValue()I");
//	private static final JBindingKey INTEGER_EQUALS_BINDING_KEY = new JBindingKey(
//			"Ljava/lang/Integer;.equals(Ljava/lang/Object;)Z");
//
//	private final Map<JBindingKey, JProgramDeclaration> bindings;
	private final JDynAlloyModule module;

	private JInteger() {
		super();

		JDynAlloyTyping integerFields = new JDynAlloyTyping();
		List<JField> fields = new LinkedList<JField>();

		//if (DynJAlloyConfig.getInstance().getDynamicJavaLangFields() == true)
		//	fields.add(new JField(INT_VALUE, parse("Integer->one Int")));
		//else
			integerFields.put(INT_VALUE, parse("Int"));

		JSignature signature = JSignatureFactory.buildClass(false, "java_lang_Integer",
				integerFields, "java_lang_Object", Collections.<String> emptySet());

		JSignature classSignature;
		//if (DynJAlloyConfig.getInstance().getUseClassSingletons() == true)
		//	classSignature = new JSignature(true, false, "IntegerClass",
		//			new DynJAlloyTyping(), false, "Class", null, Collections
		//					.<String> emptySet());
		//else
			classSignature = null;

		JProgramDeclaration integerConstructor = buildConstructor();
		JProgramDeclaration integerEquals = buildEquals();
		JProgramDeclaration integerIntValue = buildIntValue();

		Set<JProgramDeclaration> programs = new HashSet<JProgramDeclaration>();
		programs.add(integerConstructor);
		programs.add(integerEquals);
		programs.add(integerIntValue);
		this.module = new JDynAlloyModule("java_lang_Integer", signature,
				classSignature, null, fields, Collections.<JClassInvariant> emptySet(), Collections.<JClassConstraint> emptySet(), 
				Collections.<JObjectInvariant> emptySet(), Collections.<JObjectConstraint> emptySet(), Collections.
				<JRepresents> emptySet(), programs, 
				new AlloyTyping(), new ArrayList<AlloyFormula>(),
				false);

//		this.bindings = new HashMap<JBindingKey, JProgramDeclaration>();
//		bindings.put(INTEGER_CONSTRUCTOR_BINDING_KEY, integerConstructor);
//		bindings.put(INTEGER_INTVALUE_BINDING_KEY, integerIntValue);
//		bindings.put(INTEGER_EQUALS_BINDING_KEY, integerEquals);
	}

	private JProgramDeclaration buildIntValue() {
		JVariableDeclaration thisDeclaration = new JVariableDeclaration(
				THIS_VARIABLE, parse("java_lang_Integer"));

		JVariableDeclaration returnDeclaration = new JVariableDeclaration(
				RETURN_VARIABLE, parse("Int"));

		ArgEncoder encoder = new ArgEncoder(false, false, true, 0);
		List<JVariableDeclaration> ps = encoder.encode(thisDeclaration,
				JDynAlloyFactory.THROW_DECLARATION, returnDeclaration,
				Collections.<JVariableDeclaration> emptyList());

		JAssignment body = new JAssignment(RETURN_EXPRESSION, new ExprJoin(
				THIS_EXPRESSION, INT_VALUE_EXPR));

		JStatement intValueBody = JDynAlloyFactory.block(JDynAlloyFactory
				.initializeThrow(), body);

		JProgramDeclaration intValueInteger = new JProgramDeclaration(false, false, true,
				"java_lang_Integer", "intValue", ps, Collections
				.<JSpecCase> emptyList(), intValueBody, new AlloyTyping(), new ArrayList<AlloyFormula>());

		return intValueInteger;
	}

	private static final AlloyVariable INT_VALUE = new AlloyVariable("intValue");
	private static final ExprVariable INT_VALUE_EXPR = new ExprVariable(
			INT_VALUE);

	private JProgramDeclaration buildEquals() {
		AlloyVariable obj = new AlloyVariable("obj");

		JVariableDeclaration thisDeclaration = new JVariableDeclaration(
				THIS_VARIABLE, parse("java_lang_Integer"));
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
		//if (DynJAlloyConfig.getInstance().getUseClassSingletons() == true)
		//	typeCondition = eq(classOf(objExpr), new ExprConstant(null,
		//			"IntegerClass"));
		//else
			typeCondition = JPredicateFactory.instanceOf(objExpr, "java_lang_Integer");

		buffer.openIf(typeCondition);
		buffer.assign(RETURN_VARIABLE, FALSE_EXPRESSION);
		buffer.switchToElseIf();
		buffer.openIf(eq(new ExprJoin(THIS_EXPRESSION, INT_VALUE_EXPR),
				new ExprJoin(objExpr, INT_VALUE_EXPR)));
		buffer.assign(RETURN_VARIABLE, TRUE_EXPRESSION);
		buffer.switchToElseIf();
		buffer.assign(RETURN_VARIABLE, FALSE_EXPRESSION);
		buffer.closeIf();
		buffer.closeIf();
		buffer.closeIf();

		ArgEncoder encoder = new ArgEncoder(false, false, true, 1);
		Vector<JVariableDeclaration> ps = encoder.encode(thisDeclaration,
				JDynAlloyFactory.THROW_DECLARATION, returnDeclaration,
				Collections
						.<JVariableDeclaration> singletonList(objDeclaration));

		JStatement equalsBody = JDynAlloyFactory.block(JDynAlloyFactory
				.initializeThrow(), buffer.toJAlloyProgram());

		return new JProgramDeclaration(false, false, true, "java_lang_Integer", "equals", ps, Collections
			.<JSpecCase> emptyList(), equalsBody, new AlloyTyping(), new ArrayList<AlloyFormula>());
	}

	private JProgramDeclaration buildConstructor() {

		JVariableDeclaration thisDeclaration = new JVariableDeclaration(
				JExpressionFactory.THIS_VARIABLE, JType.parse("java_lang_Integer"));

		final AlloyVariable value = new AlloyVariable("value");
		final ExprVariable valueExpr = new ExprVariable(value);

		JVariableDeclaration valueDeclaration = new JVariableDeclaration(value,
				JType.parse("Int"));

		ArgEncoder encoder = new ArgEncoder(false, true, false, 1);
		List<JVariableDeclaration> ps = encoder
				.encode(
						thisDeclaration,
						JDynAlloyFactory.THROW_DECLARATION,
						null,
						Collections
								.<JVariableDeclaration> singletonList(valueDeclaration));

		ExprJoin this_intValue = new ExprJoin(THIS_EXPRESSION, INT_VALUE_EXPR);
		JStatement body;
		//if (DynJAlloyConfig.getInstance().getDynamicJavaLangFields() == true) {
		//	body = new JAssignment(this_intValue, valueExpr);
		//} else
			body = new JAssume(JPredicateFactory.eq(this_intValue, valueExpr));

		JStatement constructor = JDynAlloyFactory.block(JDynAlloyFactory
				.initializeThrow(), body);

		JProgramDeclaration constructorInteger = new JProgramDeclaration(false, true, false,
				"java_lang_Integer", "Constructor", ps, Collections
				.<JSpecCase> emptyList(), constructor, new AlloyTyping(), new ArrayList<AlloyFormula>());

		return constructorInteger;
	}

	private static JInteger instance = null;

	public static JInteger getInstance() {
		if (instance == null)
			instance = new JInteger();
		return instance;
	}

	@Override
	public JDynAlloyModule getModule() {
		return module;
	}

//	@Override
//	public Map<JBindingKey, JProgramDeclaration> getProgramBindings() {
//		return bindings;
//	    return null;
//	}

}
