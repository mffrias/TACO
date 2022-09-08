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
package ar.edu.taco.simplejml.builtin.models;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import ar.edu.jdynalloy.ast.JDynAlloyModule;
import ar.edu.jdynalloy.ast.JClassConstraint;
import ar.edu.jdynalloy.ast.JClassInvariant;
import ar.edu.jdynalloy.ast.JField;
import ar.edu.jdynalloy.ast.JModifies;
import ar.edu.jdynalloy.ast.JObjectConstraint;
import ar.edu.jdynalloy.ast.JObjectInvariant;
import ar.edu.jdynalloy.ast.JPostcondition;
import ar.edu.jdynalloy.ast.JPrecondition;
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
import ar.edu.taco.TacoConfigurator;
import ar.edu.taco.simplejml.builtin.IBuiltInModule;
import ar.edu.taco.simplejml.helpers.ArgEncoder;
import ar.uba.dc.rfm.alloy.AlloyTyping;
import ar.uba.dc.rfm.alloy.AlloyVariable;
import ar.uba.dc.rfm.alloy.ast.expressions.AlloyExpression;
import ar.uba.dc.rfm.alloy.ast.expressions.ExprIntLiteral;
import ar.uba.dc.rfm.alloy.ast.expressions.ExprJoin;
import ar.uba.dc.rfm.alloy.ast.expressions.ExprVariable;
import ar.uba.dc.rfm.alloy.ast.formulas.AlloyFormula;
import ar.uba.dc.rfm.alloy.ast.formulas.AndFormula;
import ar.uba.dc.rfm.alloy.ast.formulas.PredicateFormula;

/**
 * @author elgaby
 * 
 */
public class JJMLObjectSequence implements IBuiltInModule {

	public static final AlloyVariable CONTAINS_FIELD = new AlloyVariable("JMLObjectSequence_contains");

	private static JJMLObjectSequence instance;

	private static final JVariableDeclaration THIS_DECLARATION = new JVariableDeclaration(JExpressionFactory.THIS_VARIABLE, JType
			.parse("org_jmlspecs_models_JMLObjectSequence"));

	private final JDynAlloyModule module;

	private JJMLObjectSequence() {
		//JSignature signature = buildClass("org_jmlspecs_models_JMLObjectSequence", "java_lang_Object");
//		JSignature signature = buildClass("org_jmlspecs_models_JMLObjectSequence", null);
	    boolean isAbstract;
	    if (TacoConfigurator.getInstance().getJMLObjectSequenceToAlloySequence()) {
	    	isAbstract = true;
	    } else {
	    	isAbstract = false;
	    }
		JSignature signature = JSignatureFactory.buildClass(isAbstract, "org_jmlspecs_models_JMLObjectSequence",
				new JDynAlloyTyping(), null, Collections
						.<String> emptySet());
		
		JSignature classSignature;
		// if (DynJAlloyConfig.getInstance().getUseClassSingletons() == true)
		// classSignature = buildClassSingleton(signature.getSignatureId());
		// else
		classSignature = null;

		
		List<JField> field_list = new LinkedList<JField>();	
		if (TacoConfigurator.getInstance().getJMLObjectSequenceToAlloySequence()) {
			JField containsField = new JField(CONTAINS_FIELD, JType.parse("org_jmlspecs_models_JMLObjectSequence->(seq univ)"));
			field_list.add(containsField);
		} 
		// JProgramDeclaration linkedListCtor = buildLinkedListConstructor();
		//
		// JProgramDeclaration listAdd = buildListAdd();
		//
		// JProgramDeclaration listRemove = buildListRemove();
		//
		// JProgramDeclaration listContains = buildListContains();

		JProgramDeclaration listGet = buildListGet();

		JProgramDeclaration listIntSize = buildListSize();

		JProgramDeclaration listIsEmpty = buildListIsEmpty();

		Set<JProgramDeclaration> programs = new HashSet<JProgramDeclaration>();
		// programs.add(linkedListCtor);
		// programs.add(listAdd);
		// programs.add(listRemove);
		// programs.add(listContains);
		programs.add(listGet);
		programs.add(listIntSize);
		programs.add(listIsEmpty);

		module = new JDynAlloyModule("org_jmlspecs_models_JMLObjectSequence", signature, classSignature, null, field_list,
				Collections.<JClassInvariant> emptySet(), Collections.<JClassConstraint> emptySet(), 
				Collections.<JObjectInvariant> emptySet(), Collections.<JObjectConstraint> emptySet(), 
				Collections.<JRepresents> emptySet(), programs, new AlloyTyping(), 
				new ArrayList<AlloyFormula>(), false);

	}

	public static JJMLObjectSequence getInstance() {
		if (instance == null)
			instance = new JJMLObjectSequence();

		return instance;
	}

	private JProgramDeclaration buildListSize() {

		JVariableDeclaration returnDeclaration = new JVariableDeclaration(JExpressionFactory.RETURN_VARIABLE, JType.parse("Int"));

		ArgEncoder encoder = new ArgEncoder(false, false, true, 1);
		List<JVariableDeclaration> ps = encoder.encode(THIS_DECLARATION, JDynAlloyFactory.THROW_DECLARATION, returnDeclaration,
				new ArrayList<JVariableDeclaration>());

		AlloyExpression this_contains;
		if (TacoConfigurator.getInstance().getJMLObjectSequenceToAlloySequence()) {
			this_contains = new ExprVariable(THIS_DECLARATION.getVariable());
		} else {
			this_contains = ExprJoin.join(THIS_DECLARATION.getVariable(), CONTAINS_FIELD);
		}

		JStatement body = JDynAlloyFactory.block(JDynAlloyFactory.initializeThrow(), JDynAlloyFactory.assign(JExpressionFactory.RETURN_VARIABLE,
				JExpressionFactory.listSize(this_contains)));

		JPostcondition postcondition = new JPostcondition(JPredicateFactory.eq(JExpressionFactory.PRIMED_RETURN_EXPRESSION, JExpressionFactory
				.listSize(this_contains)));
		JSpecCase specCase = new JSpecCase(Collections.<JPrecondition> emptyList(), Collections.singletonList(postcondition), Collections
				.<JModifies> emptyList());

		JProgramDeclaration listSize = new JProgramDeclaration(false, false, true, "org_jmlspecs_models_JMLObjectSequence", "int_size", ps, Collections
				.singletonList(specCase), body, new AlloyTyping(), new ArrayList<AlloyFormula>());

		return listSize;
	}

	private JProgramDeclaration buildListIsEmpty() {

		JVariableDeclaration returnDeclaration = new JVariableDeclaration(JExpressionFactory.RETURN_VARIABLE, JType.parse("boolean"));

		ArgEncoder encoder = new ArgEncoder(false, false, true, 1);
		List<JVariableDeclaration> ps = encoder.encode(THIS_DECLARATION, JDynAlloyFactory.THROW_DECLARATION, returnDeclaration,
				new ArrayList<JVariableDeclaration>());

		AlloyExpression this_contains;
		if (TacoConfigurator.getInstance().getJMLObjectSequenceToAlloySequence()) {
			this_contains = new ExprVariable(THIS_DECLARATION.getVariable());
		} else {
			this_contains = ExprJoin.join(THIS_DECLARATION.getVariable(), CONTAINS_FIELD);
		}

		JStatement body = JDynAlloyFactory.block(JDynAlloyFactory.initializeThrow(), JDynAlloyFactory.assign(JExpressionFactory.RETURN_VARIABLE,
				JExpressionFactory.listEmpty(this_contains)));

		JPostcondition postcondition = new JPostcondition(JPredicateFactory.eq(JExpressionFactory.PRIMED_RETURN_EXPRESSION, JExpressionFactory
				.listEmpty(this_contains)));
		JSpecCase specCase = new JSpecCase(Collections.<JPrecondition> emptyList(), Collections.singletonList(postcondition), Collections
				.<JModifies> emptyList());

		JProgramDeclaration listSize = new JProgramDeclaration(false, false, true, "org_jmlspecs_models_JMLObjectSequence", "isEmpty", ps, Collections
				.singletonList(specCase), body, new AlloyTyping(), new ArrayList<AlloyFormula>());

		return listSize;
	}

	@Override
	public JDynAlloyModule getModule() {
		return module;
	}

	// @Override
	// public Map<JBindingKey, JProgramDeclaration> getProgramBindings() {
	// return bindings;
	// }

	private JProgramDeclaration buildListGet() {

		AlloyExpression this_contains;
		if (TacoConfigurator.getInstance().getJMLObjectSequenceToAlloySequence()) {
			this_contains = new ExprVariable(THIS_DECLARATION.getVariable());
		} else {
			this_contains = ExprJoin.join(THIS_DECLARATION.getVariable(), CONTAINS_FIELD);
		}

		AlloyVariable indexVar = new AlloyVariable("index");

		JVariableDeclaration indexDecl = new JVariableDeclaration(indexVar, JType.parse("Int"));

		JVariableDeclaration returnDeclaration = new JVariableDeclaration(JExpressionFactory.RETURN_VARIABLE, JType.parse("univ"));

		ArgEncoder encoder = new ArgEncoder(false, false, true, 1);
		List<JVariableDeclaration> ps = encoder.encode(THIS_DECLARATION, JDynAlloyFactory.THROW_DECLARATION, returnDeclaration, Collections
				.<JVariableDeclaration> singletonList(indexDecl));

		ExprVariable indexExpr = new ExprVariable(indexVar);

		JStatement body = JDynAlloyFactory.block(JDynAlloyFactory.initializeThrow(), JDynAlloyFactory.assign(JExpressionFactory.RETURN_EXPRESSION,
				JExpressionFactory.listGet(this_contains, indexExpr)));

		PredicateFormula preFormula1 = JPredicateFactory.alloy_int_gte(indexExpr, new ExprIntLiteral(0));
		PredicateFormula preFormula2 = JPredicateFactory.alloy_int_lt(indexExpr, JExpressionFactory.listSize(this_contains));
		
		JPrecondition precondition = new JPrecondition(new AndFormula(preFormula1, preFormula2));
		JPostcondition postcondition = new JPostcondition(JPredicateFactory.eq(JExpressionFactory.PRIMED_RETURN_EXPRESSION, JExpressionFactory.listGet(
				this_contains, indexExpr)));
		JSpecCase specCase = new JSpecCase(Collections.singletonList(precondition), Collections.singletonList(postcondition), Collections
				.<JModifies> emptyList());

		JProgramDeclaration seqGet = new JProgramDeclaration(false, false, true, "org_jmlspecs_models_JMLObjectSequence", "get", ps, Collections.singletonList(specCase),
				body, new AlloyTyping(), new ArrayList<AlloyFormula>());

		return seqGet;
	}

}
