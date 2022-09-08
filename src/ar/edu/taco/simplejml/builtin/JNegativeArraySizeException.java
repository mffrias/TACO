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
import ar.edu.jdynalloy.xlator.JDynAlloyTyping;
import ar.uba.dc.rfm.alloy.AlloyTyping;
import ar.uba.dc.rfm.alloy.ast.formulas.AlloyFormula;

public class JNegativeArraySizeException implements IBuiltInModule {

	private static JNegativeArraySizeException instance;

	public static JNegativeArraySizeException getInstance() {
		if (instance == null)
			instance = new JNegativeArraySizeException();
		return instance;
	}

	private final JDynAlloyModule module;

	private JNegativeArraySizeException() {

		final boolean signatureIsAbstract;
		if (JDynAlloyConfig.getInstance().getNewExceptionsAreLiterals() == true) {
			signatureIsAbstract = true;
		} else
			signatureIsAbstract = false;

		JSignature signature = new JSignature(true, signatureIsAbstract,
				"java_lang_NegativeArraySizeException", new JDynAlloyTyping(),
				false, "java_lang_RuntimeException", null, Collections
				.<String> emptySet(), Collections
				.<AlloyFormula> emptySet() /* facts */, Collections
				.<String> emptyList(), Collections.<String> emptyList());

		JSignature classSignature;
		classSignature = null;


		this.module = new JDynAlloyModule(
				"java_lang_NegativeArraySizeException", signature,
				classSignature, null, Collections.<JField> emptyList(),
				Collections.<JClassInvariant> emptySet(), Collections
				.<JClassConstraint> emptySet(), Collections
				.<JObjectInvariant> emptySet(), Collections
				.<JObjectConstraint> emptySet(), Collections
				.<JRepresents> emptySet(), Collections
				.<JProgramDeclaration> emptySet(), 
				new AlloyTyping(), new ArrayList<AlloyFormula>(), false);

		if (JDynAlloyConfig.getInstance().getNewExceptionsAreLiterals() == true) {

			JSignature literalSingleton = buildLiteralSingleton("java_lang_NegativeArraySizeException");
			module.setLiteralSingleton(literalSingleton);
		}

	}

	@Override
	public JDynAlloyModule getModule() {
		return module;
	}

}
