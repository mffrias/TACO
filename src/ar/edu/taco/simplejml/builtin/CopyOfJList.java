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

import static ar.edu.jdynalloy.factory.JSignatureFactory.buildClass;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ar.edu.jdynalloy.ast.JDynAlloyModule;
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
import ar.edu.jdynalloy.factory.JTypeFactory;
import ar.edu.jdynalloy.xlator.JType;
import ar.edu.taco.simplejml.helpers.ArgEncoder;
import ar.uba.dc.rfm.alloy.AlloyTyping;
import ar.uba.dc.rfm.alloy.AlloyVariable;
import ar.uba.dc.rfm.alloy.ast.expressions.ExprJoin;
import ar.uba.dc.rfm.alloy.ast.expressions.ExprVariable;



public class CopyOfJList implements IBuiltInModule {

	public static final AlloyVariable CONTAINS_FIELD = new AlloyVariable(
			"List_contains");

	private static CopyOfJList instance;

	private static final JVariableDeclaration THIS_DECLARATION = new JVariableDeclaration(
			JExpressionFactory.THIS_VARIABLE, JType.parse("java_util_List"));


	private final JDynAlloyModule module;

	private CopyOfJList() {
		JSignature signature = buildClass("java_util_List", "java_lang_Object");

		JSignature classSignature;
			classSignature = null;

		JField containsField = new JField(CONTAINS_FIELD, JType
				.parse("java_util_List->(seq univ)"));

		JProgramDeclaration linkedListCtor = buildLinkedListConstructor();

		JProgramDeclaration listAdd = buildListAdd();

		JProgramDeclaration listRemove = buildListRemove();

		JProgramDeclaration listContains = buildListContains();

		JProgramDeclaration listGet = buildListGet();

		Set<JProgramDeclaration> programs = new HashSet<JProgramDeclaration>();
		programs.add(linkedListCtor);
		programs.add(listAdd);
		programs.add(listRemove);
		programs.add(listContains);
		programs.add(listGet);

		module = new JDynAlloyModule("java_util_List", signature,
				classSignature, null, Collections.<JField> singletonList(containsField),
				Collections.<JClassInvariant> emptySet(), Collections.<JClassConstraint> emptySet(), 
				Collections.<JObjectInvariant> emptySet(), Collections.<JObjectConstraint> emptySet(), Collections
						.<JRepresents> emptySet(), programs, new AlloyTyping(), null, false); //the last null seems to be wrong, but this module will vanish soon.

	}

	public static CopyOfJList getInstance() {
		if (instance == null)
			instance = new CopyOfJList();

		return instance;
	}

	private JProgramDeclaration buildLinkedListConstructor() {

		ArgEncoder encoder = new ArgEncoder(false, true, false, 0);
		List<JVariableDeclaration> ps = encoder.encode(THIS_DECLARATION,
				JDynAlloyFactory.THROW_DECLARATION, null, Collections
						.<JVariableDeclaration> emptyList());

		ExprJoin this_contains = ExprJoin.join(THIS_DECLARATION.getVariable(),
				CONTAINS_FIELD);

		JStatement body = JDynAlloyFactory.block(JDynAlloyFactory
				.initializeThrow(), new JAssume(JPredicateFactory
				.emptyList(this_contains)));

		JProgramDeclaration hashSetCtor = new JProgramDeclaration(false, true, false, "java_util_LinkedList", "Constructor", ps, Collections.<JSpecCase> emptyList(), body, null, null);

		return hashSetCtor;

	}

	private JProgramDeclaration buildListAdd() {

		AlloyVariable eVariable = new AlloyVariable("e");
		JVariableDeclaration eDeclaration = new JVariableDeclaration(eVariable,
				JTypeFactory.buildReference(JObject.getSignatureId()));

		ArgEncoder encoder = new ArgEncoder(false, false, false, 1);
		List<JVariableDeclaration> ps = encoder.encode(THIS_DECLARATION,
				JDynAlloyFactory.THROW_DECLARATION, null, Collections
						.<JVariableDeclaration> singletonList(eDeclaration));

		ExprJoin this_contains = ExprJoin.join(THIS_DECLARATION.getVariable(),
				CONTAINS_FIELD);

		JStatement body = JDynAlloyFactory.block(JDynAlloyFactory
				.initializeThrow(), JDynAlloyFactory.assign(this_contains,
				JExpressionFactory.listAdd(this_contains, new ExprVariable(
						eVariable))));

		JProgramDeclaration setAdd = new JProgramDeclaration(false, false, false, "java_util_List", "add", ps, Collections.<JSpecCase> emptyList(), body, null, null);

		return setAdd;
	}

	private JProgramDeclaration buildListContains() {

		AlloyVariable eVariable = new AlloyVariable("e");

		JVariableDeclaration eDeclaration = new JVariableDeclaration(eVariable,
				JTypeFactory.buildReference(JObject.getSignatureId()));

		JVariableDeclaration returnDeclaration = new JVariableDeclaration(
				JExpressionFactory.RETURN_VARIABLE, JType.parse("boolean"));

		ArgEncoder encoder = new ArgEncoder(false, false, true, 1);
		List<JVariableDeclaration> ps = encoder.encode(THIS_DECLARATION,
				JDynAlloyFactory.THROW_DECLARATION, returnDeclaration,
				Collections.<JVariableDeclaration> singletonList(eDeclaration));

		ExprJoin this_contains = ExprJoin.join(THIS_DECLARATION.getVariable(),
				CONTAINS_FIELD);

		JStatement body = JDynAlloyFactory.block(JDynAlloyFactory
				.initializeThrow(), JDynAlloyFactory.assign(
				JExpressionFactory.RETURN_EXPRESSION, JExpressionFactory
						.listContains(this_contains,
								new ExprVariable(eVariable))));

		JProgramDeclaration setRemove = new JProgramDeclaration(false, false, false, "java_util_List", "contains", ps, Collections.<JSpecCase> emptyList(), body, null, null);

		return setRemove;
	}

	private JProgramDeclaration buildListRemove() {

		AlloyVariable indexVar = new AlloyVariable("index");
		JVariableDeclaration indexDecl = new JVariableDeclaration(indexVar,
				JType.parse("Int"));

		ArgEncoder encoder = new ArgEncoder(false, false, false, 1);
		List<JVariableDeclaration> ps = encoder.encode(THIS_DECLARATION,
				JDynAlloyFactory.THROW_DECLARATION, null, Collections
						.<JVariableDeclaration> singletonList(indexDecl));

		ExprJoin this_contains = ExprJoin.join(THIS_DECLARATION.getVariable(),
				CONTAINS_FIELD);

		JStatement body = JDynAlloyFactory.block(JDynAlloyFactory
				.initializeThrow(), JDynAlloyFactory.assign(this_contains,
				JExpressionFactory.listRemove(this_contains, new ExprVariable(
						indexVar))));

		JProgramDeclaration setRemove = new JProgramDeclaration(false, false, false, "java_util_List", "remove", ps, Collections.<JSpecCase> emptyList(), body, null, null);

		return setRemove;
	}

	@Override
	public JDynAlloyModule getModule() {
		return module;
	}

//	@Override
//	public Map<JBindingKey, JProgramDeclaration> getProgramBindings() {
//		return bindings;
//	}

	private JProgramDeclaration buildListGet() {

		AlloyVariable indexVar = new AlloyVariable("index");

		JVariableDeclaration indexDecl = new JVariableDeclaration(indexVar,
				JType.parse("Int"));

		JVariableDeclaration returnDeclaration = new JVariableDeclaration(
				JExpressionFactory.RETURN_VARIABLE, JType.parse("univ"));

		ArgEncoder encoder = new ArgEncoder(false, false, true, 1);
		List<JVariableDeclaration> ps = encoder.encode(THIS_DECLARATION,
				JDynAlloyFactory.THROW_DECLARATION, returnDeclaration,
				Collections.<JVariableDeclaration> singletonList(indexDecl));

		ExprJoin this_entries = ExprJoin.join(THIS_DECLARATION.getVariable(),
				CONTAINS_FIELD);

		ExprVariable indexExpr = new ExprVariable(indexVar);

		JStatement body = JDynAlloyFactory.block(JDynAlloyFactory
				.initializeThrow(), JDynAlloyFactory.assign(
				JExpressionFactory.RETURN_EXPRESSION, JExpressionFactory
						.listGet(this_entries, indexExpr)));

		JProgramDeclaration mapGet = new JProgramDeclaration(false, false, false, "java_util_List", "get", ps, Collections.<JSpecCase> emptyList(), body, null, null);

		return mapGet;
	}

}
