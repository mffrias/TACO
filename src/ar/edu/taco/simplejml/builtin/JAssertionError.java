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
import static ar.edu.jdynalloy.factory.JSignatureFactory.buildLiteralSingleton;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import ar.edu.jdynalloy.JDynAlloyConfig;
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
import ar.edu.jdynalloy.ast.JSkip;
import ar.edu.jdynalloy.ast.JVariableDeclaration;
import ar.edu.jdynalloy.factory.JDynAlloyFactory;
import ar.edu.jdynalloy.factory.JExpressionFactory;
import ar.edu.jdynalloy.xlator.JDynAlloyTyping;
import ar.edu.jdynalloy.xlator.JType;
import ar.edu.taco.simplejml.helpers.ArgEncoder;
import ar.uba.dc.rfm.alloy.AlloyTyping;
import ar.uba.dc.rfm.alloy.ast.formulas.AlloyFormula;

public class JAssertionError implements IBuiltInModule {

	private static JAssertionError instance;

	public static JAssertionError getInstance() {
		if (instance == null)
			instance = new JAssertionError();
		return instance;
	}

	private final JDynAlloyModule module;

	private JAssertionError() {

		final boolean signatureIsAbstract;
		if (JDynAlloyConfig.getInstance().getNewExceptionsAreLiterals() == true) {
			signatureIsAbstract = true;
		} else
			signatureIsAbstract = false;

		JSignature signature = buildClass(signatureIsAbstract, "java_lang_AssertionError",
				new JDynAlloyTyping(), "java_lang_Error", Collections
						.<String> emptySet());

		
//		JSignature signature = new JSignature(true, signatureIsAbstract,
//				"java_lang_AssertionError", new JDynAlloyTyping(),
//				false, "java_lang_Error", null, Collections
//				.<String> emptySet(), Collections
//				.<AlloyFormula> emptySet() /* facts */, Collections
//				.<String> emptyList(), Collections.<String> emptyList());


		JSignature classSignature;
//		if (JDynAlloyConfig.getInstance().getUseClassSingletons() == true)
//			classSignature = new JSignature(true, false,
//					"java_lang_AssertionErrorClass",
//					new JDynAlloyTyping(), false, "Class", null, Collections
//					.<String> emptySet(), Collections
//					.<AlloyFormula> emptySet() /* facts */,
//					Collections.<String> emptyList(), Collections
//					.<String> emptyList());
//		else
			classSignature = null;

		JVariableDeclaration thisDeclaration = new JVariableDeclaration(
				JExpressionFactory.THIS_VARIABLE, JType
				.parse("java_lang_AssertionError"));

		ArgEncoder encoder = new ArgEncoder(false, true, false, 0);
		List<JVariableDeclaration> parameters = encoder.encode(thisDeclaration,
				JDynAlloyFactory.THROW_DECLARATION, null, Collections
				.<JVariableDeclaration> emptyList());
		
		JProgramDeclaration constructor = JProgramDeclaration
				.buildJProgramDeclaration(false, true, false,
						"java_lang_AssertionError", "Constructor",
						parameters, Collections.<JPrecondition> emptySet(),
						Collections.<JModifies> emptySet(), Collections
						.<JPostcondition> emptySet(), new JSkip(), new AlloyTyping(), new ArrayList<AlloyFormula>());

		this.module = new JDynAlloyModule(
				"java_lang_AssertionError", signature,
				classSignature, null, Collections.<JField> emptyList(),
				Collections.<JClassInvariant> emptySet(), Collections
				.<JClassConstraint> emptySet(), Collections
				.<JObjectInvariant> emptySet(), Collections
				.<JObjectConstraint> emptySet(), Collections
				.<JRepresents> emptySet(), Collections
				.<JProgramDeclaration> singleton(constructor), 
				new AlloyTyping(), new ArrayList<AlloyFormula>(), false);

		if (JDynAlloyConfig.getInstance().getNewExceptionsAreLiterals() == true) {
			JSignature literalSingleton = buildLiteralSingleton("java_lang_AssertionError");
			module.setLiteralSingleton(literalSingleton);
		}

	}

	@Override
	public JDynAlloyModule getModule() {
		return module;
	}


}
