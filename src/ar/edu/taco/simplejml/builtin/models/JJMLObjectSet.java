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
import ar.edu.jdynalloy.factory.JTypeFactory;
import ar.edu.jdynalloy.xlator.JDynAlloyTyping;
import ar.edu.jdynalloy.xlator.JType;
import ar.edu.taco.TacoConfigurator;
import ar.edu.taco.simplejml.builtin.IBuiltInModule;
import ar.edu.taco.simplejml.builtin.JObject;
import ar.edu.taco.simplejml.helpers.ArgEncoder;
import ar.uba.dc.rfm.alloy.AlloyTyping;
import ar.uba.dc.rfm.alloy.AlloyVariable;
import ar.uba.dc.rfm.alloy.ast.expressions.AlloyExpression;
import ar.uba.dc.rfm.alloy.ast.expressions.ExprJoin;
import ar.uba.dc.rfm.alloy.ast.expressions.ExprVariable;
import ar.uba.dc.rfm.alloy.ast.formulas.AlloyFormula;

/**
 * Builds the built-in module {@code org_jmlspecs_models_JMLObjectSet}, TACO's model of JML's
 * {@code JMLObjectSet}: a signature with no parent and two pure programs with specifications,
 * {@code has(e)}, which returns whether the set contains {@code e} ({@code fun_set_contains}),
 * and {@code int_size()} ({@code fun_set_size}). With {@code JMLObjectSetToAlloySet} (on in
 * {@code config/genericTest.properties}) the signature is abstract, the module declares the
 * field {@code JMLObjectSet_contains: org_jmlspecs_models_JMLObjectSet -> set univ}, and the
 * programs apply those functions to {@code thiz} itself; without it there is no field, and the
 * programs apply them to {@code thiz.JMLObjectSet_contains}. Added by
 * {@code engine.PrecompiledModules} (key {@code JJMLObjectSet}).
 *
 * <p>Unclear: as in {@link JJMLObjectSequence#getInstance()}, the field is declared only when
 * the programs do not use it; whether the condition is inverted is an open question.
 *
 * <p>Static state: a singleton, built on the first {@link #getInstance()} and never reset; it
 * reads {@code JMLObjectSetToAlloySet} only then. Building it also builds the singleton of
 * {@code JObject}, the type of {@code e}.
 */
public class JJMLObjectSet implements IBuiltInModule {

    public static final AlloyVariable CONTAINS_FIELD = new AlloyVariable("JMLObjectSet_contains");

    private static final JVariableDeclaration THIS_DECLARATION = new JVariableDeclaration(JExpressionFactory.THIS_VARIABLE, JType
	    .parse("org_jmlspecs_models_JMLObjectSet"));

    private static JJMLObjectSet instance;

    /**
     * Returns the singleton, building its module on the first call with the configuration then in
     * effect ({@code JMLObjectSetToAlloySet}).
     *
     * @return the singleton
     */
    public static JJMLObjectSet getInstance() {
	if (instance == null)
	    instance = new JJMLObjectSet();

	return instance;
    }

    // private final Map<JBindingKey, JProgramDeclaration> bindings;

    private final JDynAlloyModule module;

    private JJMLObjectSet() {
	//JSignature signature = buildClass("org_jmlspecs_models_JMLObjectSet", "java_lang_Object");
    boolean isAbstract;
    if (TacoConfigurator.getInstance().getJMLObjectSetToAlloySet()) {
    	isAbstract = true;
    } else {
    	isAbstract = false;
    }
	JSignature signature = JSignatureFactory.buildClass(isAbstract, "org_jmlspecs_models_JMLObjectSet",
			new JDynAlloyTyping(), null, Collections
					.<String> emptySet());

	JSignature classSignature;
	classSignature = null;

	List<JField> field_list = new LinkedList<JField>();	
	if (TacoConfigurator.getInstance().getJMLObjectSetToAlloySet()) {
		JField containsField = new JField(CONTAINS_FIELD, JType.parse("org_jmlspecs_models_JMLObjectSet->(set univ)"));
		field_list.add(containsField);
	} 

	JProgramDeclaration setHas = buildSetHas();

	JProgramDeclaration setIntSize = buildSetSize();
	Set<JProgramDeclaration> programs = new HashSet<JProgramDeclaration>();
	programs.add(setHas);
	programs.add(setIntSize);

	module = new JDynAlloyModule("org_jmlspecs_models_JMLObjectSet", signature, classSignature, null, field_list,
			Collections.<JClassInvariant> emptySet(), Collections.<JClassConstraint> emptySet(), 
			Collections.<JObjectInvariant> emptySet(), Collections.<JObjectConstraint> emptySet(), 
			Collections.<JRepresents> emptySet(), programs, new AlloyTyping(), new ArrayList<AlloyFormula>(),
			false);

    }

    private JProgramDeclaration buildSetSize() {

	JVariableDeclaration returnDeclaration = new JVariableDeclaration(JExpressionFactory.RETURN_VARIABLE, JType.parse("Int"));

	ArgEncoder encoder = new ArgEncoder(false, false, true, 1);
	List<JVariableDeclaration> ps = encoder.encode(THIS_DECLARATION, JDynAlloyFactory.THROW_DECLARATION, returnDeclaration,
		new ArrayList<JVariableDeclaration>());

	AlloyExpression this_contains;
	if (TacoConfigurator.getInstance().getJMLObjectSetToAlloySet()) {
		this_contains = new ExprVariable(THIS_DECLARATION.getVariable());
	} else {
		this_contains = ExprJoin.join(THIS_DECLARATION.getVariable(), CONTAINS_FIELD);
	}
	JStatement body = JDynAlloyFactory.block(JDynAlloyFactory.initializeThrow(), JDynAlloyFactory.assign(JExpressionFactory.RETURN_VARIABLE,
		JExpressionFactory.setSize(this_contains)));

	JPostcondition postcondition = new JPostcondition(JPredicateFactory.eq(JExpressionFactory.PRIMED_RETURN_EXPRESSION, JExpressionFactory
		.setSize(this_contains)));
	JSpecCase specCase = new JSpecCase(Collections.<JPrecondition> emptyList(), Collections.singletonList(postcondition), Collections
		.<JModifies> emptyList());

	JProgramDeclaration setSize = new JProgramDeclaration(false, false, true, "org_jmlspecs_models_JMLObjectSet", "int_size", ps, Collections.singletonList(specCase),
		body, new AlloyTyping(), new ArrayList<AlloyFormula>());

	return setSize;
    }

    private JProgramDeclaration buildSetHas() {

	AlloyVariable eVariable = new AlloyVariable("e");

	JVariableDeclaration eDeclaration = new JVariableDeclaration(eVariable, JTypeFactory.buildReference(JObject.getSignatureId()));

	JVariableDeclaration returnDeclaration = new JVariableDeclaration(JExpressionFactory.RETURN_VARIABLE, JType.parse("boolean"));

	ArgEncoder encoder = new ArgEncoder(false, false, true, 1);
	List<JVariableDeclaration> ps = encoder.encode(THIS_DECLARATION, JDynAlloyFactory.THROW_DECLARATION, returnDeclaration, Collections
		.<JVariableDeclaration> singletonList(eDeclaration));
	
	AlloyExpression this_contains;
	if (TacoConfigurator.getInstance().getJMLObjectSetToAlloySet()) {
		this_contains = new ExprVariable(THIS_DECLARATION.getVariable());
	} else {
		this_contains = ExprJoin.join(THIS_DECLARATION.getVariable(), CONTAINS_FIELD);
	}
	
	JStatement body = JDynAlloyFactory.block(JDynAlloyFactory.initializeThrow(), JDynAlloyFactory.assign(JExpressionFactory.RETURN_EXPRESSION,
		JExpressionFactory.setContains(this_contains, new ExprVariable(eVariable))));

	JPostcondition postcondition = new JPostcondition(JPredicateFactory.eq(JExpressionFactory.PRIMED_RETURN_EXPRESSION, JExpressionFactory.setContains(
		this_contains, new ExprVariable(eVariable))));
	JSpecCase specCase = new JSpecCase(Collections.<JPrecondition> emptyList(), Collections.singletonList(postcondition), Collections
		.<JModifies> emptyList());

	JProgramDeclaration setHas = new JProgramDeclaration(false, false, true, "org_jmlspecs_models_JMLObjectSet", "has", ps, Collections.singletonList(specCase), 
			body, new AlloyTyping(), new ArrayList<AlloyFormula>());

	return setHas;
    }

    @Override
    public JDynAlloyModule getModule() {
	return module;
    }
}
