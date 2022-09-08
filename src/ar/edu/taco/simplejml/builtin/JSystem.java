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
import ar.edu.jdynalloy.ast.JHavoc;
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
import ar.edu.taco.TacoConfigurator;
import ar.edu.taco.simplejml.helpers.ArgEncoder;
import ar.uba.dc.rfm.alloy.AlloyTyping;
import ar.uba.dc.rfm.alloy.AlloyVariable;
import ar.uba.dc.rfm.alloy.ast.expressions.ExprJoin;
import ar.uba.dc.rfm.alloy.ast.expressions.ExprVariable;
import ar.uba.dc.rfm.alloy.ast.formulas.AlloyFormula;
import ar.uba.dc.rfm.alloy.ast.formulas.PredicateFormula;

public class JSystem implements IBuiltInModule {

	private final JDynAlloyModule module;

	private JSystem() {
		super();

		JDynAlloyTyping systemFields = new JDynAlloyTyping();
		List<JField> fields = new LinkedList<JField>();

		JField outField = 
				new JField(new AlloyVariable("java_lang_System_out"), 
						JType.parse("ClassFields -> java_io_PrintStream"));


		StaticFieldsModuleBuilder.getInstance().addStaticField(outField);

		JSignature signature = JSignatureFactory.buildClass(false,
				"java_lang_System", systemFields, "java_lang_Object",
				Collections.<String> emptySet());

		JSignature classSignature;
		classSignature = null;

		//		JProgramDeclaration Constructor = buildConstructor();
		JProgramDeclaration currentTimeMillis = buildCurrentTimeMillis();
		//		JProgramDeclaration booleanValue = buildBooleanValue();

		Set<JProgramDeclaration> programs = new HashSet<JProgramDeclaration>();
		//		programs.add(booleanConstructor);
		programs.add(currentTimeMillis);
		//		programs.add(booleanValue);
		this.module = new JDynAlloyModule("java_lang_System", signature,
				classSignature, null, fields,
				Collections.<JClassInvariant> emptySet(),
				Collections.<JClassConstraint> emptySet(),
				Collections.<JObjectInvariant> emptySet(),
				Collections.<JObjectConstraint> emptySet(),
				Collections.<JRepresents> emptySet(), programs, 
				new AlloyTyping(), new ArrayList<AlloyFormula>(), false);

	}

	private JProgramDeclaration buildCurrentTimeMillis() {
		JVariableDeclaration thisDeclaration = new JVariableDeclaration(
				THIS_VARIABLE, parse("java_lang_System"));

		JVariableDeclaration returnDeclaration;
		if (TacoConfigurator.getInstance().getUseJavaArithmetic() == true){
			returnDeclaration = new JVariableDeclaration(RETURN_VARIABLE, parse("Int"));
		} else {
			returnDeclaration = new JVariableDeclaration(RETURN_VARIABLE, parse("JavaPrimitiveLongValue"));
		}


		ArgEncoder encoder = new ArgEncoder(true, false, true, 0);
		List<JVariableDeclaration> ps = encoder.encode(thisDeclaration,
				JDynAlloyFactory.THROW_DECLARATION, returnDeclaration,
				Collections.<JVariableDeclaration> emptyList());

		JStatement body = JDynAlloyFactory.block(JDynAlloyFactory
				.initializeThrow(), new JHavoc(RETURN_EXPRESSION));
		
		JProgramDeclaration currentTimeMillis = new JProgramDeclaration(false, false, true,
				"java_lang_System", "currentTimeMillis", ps,
				Collections.<JSpecCase> emptyList(), body, new AlloyTyping(), new ArrayList<AlloyFormula>());

		return currentTimeMillis;
	}






	private static JSystem instance = null;

	public static JSystem getInstance() {
		if (instance == null)
			instance = new JSystem();
		return instance;
	}

	@Override
	public JDynAlloyModule getModule() {
		return module;
	}


}
