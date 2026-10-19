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

import static ar.edu.jdynalloy.factory.JSignatureFactory.buildLiteralSingleton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import ar.edu.jdynalloy.JDynAlloyConfig;
import ar.edu.jdynalloy.ast.JDynAlloyModule;
import ar.edu.jdynalloy.ast.JClassConstraint;
import ar.edu.jdynalloy.ast.JClassInvariant;
import ar.edu.jdynalloy.ast.JField;
import ar.edu.jdynalloy.ast.JObjectConstraint;
import ar.edu.jdynalloy.ast.JObjectInvariant;
import ar.edu.jdynalloy.ast.JProgramDeclaration;
import ar.edu.jdynalloy.ast.JRepresents;
import ar.edu.jdynalloy.ast.JSignature;
import ar.edu.jdynalloy.ast.JSkip;
import ar.edu.jdynalloy.ast.JSpecCase;
import ar.edu.jdynalloy.ast.JVariableDeclaration;
import ar.edu.jdynalloy.factory.JDynAlloyFactory;
import ar.edu.jdynalloy.factory.JExpressionFactory;
import ar.edu.jdynalloy.xlator.JDynAlloyTyping;
import ar.edu.jdynalloy.xlator.JType;
import ar.edu.taco.simplejml.helpers.ArgEncoder;
import ar.uba.dc.rfm.alloy.AlloyTyping;
import ar.uba.dc.rfm.alloy.ast.formulas.AlloyFormula;

/**
 * Builds the built-in module {@code java_lang_ArithmeticException}, TACO's model of
 * {@code java.lang.ArithmeticException}, like {@link JThrowable}: a {@code one} signature
 * extending {@code java_lang_RuntimeException}, abstract with {@code newExceptionsAreLiterals},
 * which also adds the literal signature {@code java_lang_ArithmeticExceptionLit}; no fields;
 * and a {@code Constructor} with only {@code thiz} and {@code throw} and the body {@code skip}.
 * Added by {@code engine.PrecompiledModules} (key {@code JArithmeticException}).
 *
 * <p>With Java arithmetic and {@code checkArithmeticException}, {@link AuxiliaryConstantsFactory}
 * sets {@code throw} to the literal on an {@code int} or {@code long} division by zero, which
 * needs {@code newExceptionsAreLiterals} (see there).
 *
 * <p>Static state: a singleton, built on the first {@link #getInstance()} and never reset; it
 * reads {@code newExceptionsAreLiterals} only then.
 */
public class JArithmeticException implements IBuiltInModule {


	private static JArithmeticException instance;

	/**
	 * Returns the singleton, building its module on the first call with the configuration then in
	 * effect ({@code newExceptionsAreLiterals}).
	 *
	 * @return the singleton
	 */
	public static JArithmeticException getInstance() {
		if (instance == null)
			instance = new JArithmeticException();
		return instance;
	}

	private final JDynAlloyModule module;

	// private final Map<JBindingKey, JProgramDeclaration> bindings;

	private JArithmeticException() {
		final boolean signatureIsAbstract;
		if (JDynAlloyConfig.getInstance().getNewExceptionsAreLiterals() == true) {
			signatureIsAbstract = true;
		} else
			signatureIsAbstract = false;

		JSignature signature = new JSignature(true, signatureIsAbstract,
				"java_lang_ArithmeticException", new JDynAlloyTyping(), false,
				"java_lang_RuntimeException", null, Collections
						.<String> emptySet(), Collections
						.<AlloyFormula> emptySet() /* facts */, Collections
						.<String> emptyList(), Collections.<String> emptyList());

		JSignature classSignature;
		classSignature = null;

		JVariableDeclaration thisDeclaration = new JVariableDeclaration(
				JExpressionFactory.THIS_VARIABLE, JType
						.parse("java_lang_ArithmeticException"));

		ArgEncoder encoder = new ArgEncoder(false, true, false, 0);
		List<JVariableDeclaration> parameters = encoder.encode(thisDeclaration,
				JDynAlloyFactory.THROW_DECLARATION, null, Collections
						.<JVariableDeclaration> emptyList());
		JProgramDeclaration constructor = new JProgramDeclaration(false, true, false,
				"java_lang_ArithmeticException", "Constructor", parameters,
				Collections.<JSpecCase> emptyList(), new JSkip(), new AlloyTyping(), new ArrayList<AlloyFormula>());

		this.module = new JDynAlloyModule("java_lang_ArithmeticException",
				signature, classSignature, null, Collections
						.<JField> emptyList(), Collections
						.<JClassInvariant> emptySet(), Collections
						.<JClassConstraint> emptySet(), Collections
						.<JObjectInvariant> emptySet(), Collections
						.<JObjectConstraint> emptySet(), Collections
						.<JRepresents> emptySet(), Collections
						.<JProgramDeclaration> singleton(constructor), 
						new AlloyTyping(), new ArrayList<AlloyFormula>(), false);

		if (JDynAlloyConfig.getInstance().getNewExceptionsAreLiterals() == true) {
			JSignature literalSingleton = buildLiteralSingleton("java_lang_ArithmeticException");
			module.setLiteralSingleton(literalSingleton);
		}

	}

	@Override
	public JDynAlloyModule getModule() {
		return module;
	}


}
