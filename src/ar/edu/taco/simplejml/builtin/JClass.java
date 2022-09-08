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

import java.util.ArrayList;
import java.util.Collections;

import ar.edu.jdynalloy.ast.JDynAlloyModule;
import ar.edu.jdynalloy.ast.JClassConstraint;
import ar.edu.jdynalloy.ast.JClassInvariant;
import ar.edu.jdynalloy.ast.JField;
import ar.edu.jdynalloy.ast.JObjectConstraint;
import ar.edu.jdynalloy.ast.JObjectInvariant;
import ar.edu.jdynalloy.ast.JProgramDeclaration;
import ar.edu.jdynalloy.ast.JRepresents;
import ar.edu.jdynalloy.ast.JSignature;
import ar.edu.jdynalloy.factory.JSignatureFactory;
import ar.edu.jdynalloy.xlator.JDynAlloyTyping;
import ar.uba.dc.rfm.alloy.AlloyTyping;
import ar.uba.dc.rfm.alloy.ast.formulas.AlloyFormula;


public class JClass implements IBuiltInModule {

	private static JClass instance;

	private final JDynAlloyModule module;

	private JClass() {

		String superClass;
		JSignature classSignature;
		//if (DynJAlloyConfig.getInstance().getClassExtendsObject() == true) {
		//	superClass = "Object";
		//	if (DynJAlloyConfig.getInstance().getUseClassSingletons() == true)
		//		classSignature = new JSignature(true, false, "ClassClass",
		//				new DynJAlloyTyping(), false, "Class", null,
		//				Collections.<String> emptySet());
		//	else
		//		classSignature = null;
		//	
		//} else {
			superClass = null;
			classSignature = null;
		//}

		JSignature signature = JSignatureFactory.buildClass(true, "Class",
				new JDynAlloyTyping(), superClass, Collections
						.<String> emptySet());

		module = new JDynAlloyModule("java_lang_Class", signature,
				classSignature, null, Collections.<JField> emptyList(), Collections.<JClassInvariant> emptySet(), Collections.<JClassConstraint> emptySet(), 
				Collections.<JObjectInvariant> emptySet(), Collections.<JObjectConstraint> emptySet(), Collections
						.<JRepresents> emptySet(), Collections
						.<JProgramDeclaration> emptySet(), new AlloyTyping(), 
						new ArrayList<AlloyFormula>(), false);
	}

	public static JClass getInstance() {
		if (instance == null)
			instance = new JClass();
		return instance;
	}

	@Override
	public JDynAlloyModule getModule() {
		return module;
	}

//	@Override
//	public Map<JBindingKey, JProgramDeclaration> getProgramBindings() {
//		return new HashMap<JBindingKey, JProgramDeclaration>();
//	}

}
