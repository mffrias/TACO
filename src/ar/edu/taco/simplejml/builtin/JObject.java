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

import static ar.edu.jdynalloy.factory.JDynAlloyFactory.assign;
import static ar.edu.jdynalloy.factory.JDynAlloyFactory.ifThenElse;
import static ar.edu.jdynalloy.factory.JExpressionFactory.FALSE_EXPRESSION;
import static ar.edu.jdynalloy.factory.JExpressionFactory.NULL_EXPRESSION;
import static ar.edu.jdynalloy.factory.JExpressionFactory.RETURN_EXPRESSION;
import static ar.edu.jdynalloy.factory.JExpressionFactory.RETURN_VARIABLE;
import static ar.edu.jdynalloy.factory.JExpressionFactory.THIS_EXPRESSION;
import static ar.edu.jdynalloy.factory.JExpressionFactory.THIS_VARIABLE;
import static ar.edu.jdynalloy.factory.JExpressionFactory.TRUE_EXPRESSION;
import static ar.edu.jdynalloy.factory.JPredicateFactory.eq;
import static ar.edu.jdynalloy.factory.JPredicateFactory.neq;
import static ar.edu.jdynalloy.factory.JSignatureFactory.BOOLEAN;
import static ar.edu.jdynalloy.xlator.JType.parse;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.Vector;

import ar.edu.jdynalloy.ast.JDynAlloyModule;
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
import ar.edu.jdynalloy.factory.JSignatureFactory;
import ar.edu.jdynalloy.xlator.JDynAlloyTyping;
import ar.edu.taco.TacoConfigurator;
import ar.edu.taco.simplejml.helpers.ArgEncoder;
import ar.uba.dc.rfm.alloy.AlloyTyping;
import ar.uba.dc.rfm.alloy.AlloyVariable;
import ar.uba.dc.rfm.alloy.ast.expressions.ExprVariable;
import ar.uba.dc.rfm.alloy.ast.formulas.AlloyFormula;

public class JObject implements IBuiltInModule {

//	private static final JBindingKey OBJECT_CONSTRUCTOR_KEY = new JBindingKey(
//			"Ljava/lang/Object;.()V");
//	private static final JBindingKey OBJECT_EQUALS_KEY = new JBindingKey(
//			"Ljava/lang/Object;.equals(Ljava/lang/Object;)<>Z");
//	private static final JBindingKey OBJECT_GET_CLASS_KEY = new JBindingKey(
//			"Ljava/lang/Object;.getClass()Ljava/lang/Class<>;");

	private static JProgramDeclaration buildConstructor() {
		ArgEncoder encoder = new ArgEncoder(false, true, false, 0);
		List<JVariableDeclaration> ps = encoder.encode(thisDeclaration,
				JDynAlloyFactory.THROW_DECLARATION, null, Collections
						.<JVariableDeclaration> emptyList());
		return new JProgramDeclaration(false, true, false, "java_lang_Object", "Constructor", ps, Collections
			.<JSpecCase> emptyList(), JDynAlloyFactory
						.initializeThrow(), new AlloyTyping(), new ArrayList<AlloyFormula>());
	}

	private static JVariableDeclaration thisDeclaration = new JVariableDeclaration(
			THIS_VARIABLE, parse("java_lang_Object"));

	private static JProgramDeclaration buildEquals() {
		AlloyVariable oVar = new AlloyVariable("o");

		JVariableDeclaration returnDeclaration = new JVariableDeclaration(
				RETURN_VARIABLE, parse(BOOLEAN.getSignatureId()));
		JVariableDeclaration oDeclaration = new JVariableDeclaration(oVar,
				parse("java_lang_Object"));

		ExprVariable oExpr = new ExprVariable(oVar);
		JStatement body = JDynAlloyFactory.block(JDynAlloyFactory
				.initializeThrow(), ifThenElse(neq(oExpr, NULL_EXPRESSION),
				ifThenElse(eq(THIS_EXPRESSION, oExpr), assign(
						RETURN_EXPRESSION, TRUE_EXPRESSION), assign(
						RETURN_EXPRESSION, FALSE_EXPRESSION)), assign(
						RETURN_EXPRESSION, FALSE_EXPRESSION)));

		ArgEncoder encoder = new ArgEncoder(false, false, true, 1);
		Vector<JVariableDeclaration> encoding = encoder.encode(thisDeclaration,
				JDynAlloyFactory.THROW_DECLARATION, returnDeclaration,
				Collections.<JVariableDeclaration> singletonList(oDeclaration));
		return new JProgramDeclaration(false, false, true, "java_lang_Object", "equals", encoding, Collections
			.<JSpecCase> emptyList(), body, new AlloyTyping(), new ArrayList<AlloyFormula>());

	}

	private final JDynAlloyModule module;
//	private final Map<JBindingKey, JProgramDeclaration> bindings;

	public static JObject getInstance() {
		if (instance == null)
			instance = new JObject();
		return instance;
	}

	public static String getSignatureId() {
		return getInstance().getModule().getSignature().getSignatureId();
	}

	@Override
	public JDynAlloyModule getModule() {
		return module;
	}

//	@Override
//	public Map<JBindingKey, JProgramDeclaration> getProgramBindings() {
//		return bindings;
//	}

//	@SuppressWarnings("unused")
//	private static JProgramDeclaration buildGetClass() {
//		JVariableDeclaration returnDeclaration = new JVariableDeclaration(
//				RETURN_VARIABLE, parse("Class"));
//
//		JStatement body = JDynAlloyFactory.block(JDynAlloyFactory
//				.initializeThrow(), assign(RETURN_EXPRESSION,
//				classOf(THIS_EXPRESSION)));
//
//		ArgEncoder encoder = new ArgEncoder(false, false, true, 0);
//		Vector<JVariableDeclaration> encoding = encoder.encode(thisDeclaration,
//				JDynAlloyFactory.THROW_DECLARATION, returnDeclaration,
//				Collections.<JVariableDeclaration> emptyList());
//		return new JProgramDeclaration(false, "java_lang_Object", "getClass", encoding, Collections.<JSpecCase> emptyList(), body);
//
//	}

	private static JObject instance = null;

	private JObject() {
		super();		

		JDynAlloyTyping fields = new JDynAlloyTyping();
		final JSignature classSignature;
		Set<JProgramDeclaration> programs = new HashSet<JProgramDeclaration>();
//		Map<JBindingKey, JProgramDeclaration> programBindings = new HashMap<JBindingKey, JProgramDeclaration>();

		//if (config.getUseClassSingletons() == true) {
		//	fields.put(new AlloyVariable("class"), JType.parse("Class"));
		//	classSignature = new JSignature(true, false, "ObjectClass",
		//			new DynJAlloyTyping(), false, "Class", null, Collections
		//					.<String> emptySet());
		//	JProgramDeclaration objectGetClass = buildGetClass();
		//	programs.add(objectGetClass);
		//	programBindings.put(OBJECT_GET_CLASS_KEY, objectGetClass);
		//} else {
			classSignature = null;
		//}

		boolean isAbstract;
		if (TacoConfigurator.getInstance().getAbstractSignatureObject() == true)
			isAbstract = true;
		else
			isAbstract = false;

		final JSignature signature = JSignatureFactory.buildClass(isAbstract,
				"java_lang_Object", fields, null, new HashSet<String>());

		JProgramDeclaration objectConstructor = buildConstructor();
		programs.add(objectConstructor);

		JProgramDeclaration objectEquals = buildEquals();
		programs.add(objectEquals);

		this.module = new JDynAlloyModule("java_lang_Object", signature,
				classSignature, null, Collections.<JField> emptyList(), Collections.<JClassInvariant> emptySet(), Collections.<JClassConstraint> emptySet(), 
				Collections.<JObjectInvariant> emptySet(), Collections.<JObjectConstraint> emptySet(), Collections
						.<JRepresents> emptySet(), programs, new AlloyTyping(), 
						new ArrayList<AlloyFormula>(), false);

//		programBindings.put(OBJECT_CONSTRUCTOR_KEY, objectConstructor);
//		programBindings.put(OBJECT_EQUALS_KEY, objectEquals);

//		this.bindings = programBindings;
	}
}
