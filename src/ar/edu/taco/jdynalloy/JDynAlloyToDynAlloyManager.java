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
package ar.edu.taco.jdynalloy;

import java.util.List;
import java.util.Map;
import java.util.Vector;

import ar.edu.jdynalloy.ast.JDynAlloyModule;
import ar.edu.jdynalloy.xlator.DynAlloyLinker;
import ar.edu.jdynalloy.xlator.JDynAlloyBinding;
import ar.edu.jdynalloy.xlator.JDynAlloyTranslator;
import ar.edu.taco.TacoConfigurator;
import ar.uba.dc.rfm.dynalloy.ast.DynalloyModule;

/**
 * @author elgaby
 * 
 */
public class JDynAlloyToDynAlloyManager {
	
	private Object inputToFix = null;
	
	Vector<DynalloyModule> dynalloyModules = new Vector<DynalloyModule>();

	private String classToCheck;

	private String methodToCheck;
	
	
	public Vector<DynalloyModule> getDynalloyModules(){
		return this.dynalloyModules;
	}

	
	public JDynAlloyToDynAlloyManager(String classToCheck, String methodToCheck, Object inputToFix){
		this.inputToFix = inputToFix;
		this.classToCheck = classToCheck;
		this.methodToCheck = methodToCheck;
	}
	
	public Map<String, String> process(List<JDynAlloyModule> modules, JDynAlloyBinding dynJAlloyBinding) {

		// JDynAlloy -> Dynalloy
		JDynAlloyTranslator translator = new JDynAlloyTranslator(dynJAlloyBinding, this.classToCheck, this.methodToCheck, inputToFix);
		translator.setRemoveQuantifiers(TacoConfigurator.getInstance().getRemoveQuantifiers());
		Vector<DynalloyModule> dynalloyModules = translator.translateAll( modules.toArray(new JDynAlloyModule[0]), TacoConfigurator.getInstance().getUseJavaArithmetic());

		// Keep generated Dynalloy modules
		this.dynalloyModules = dynalloyModules;
		
		// Dynalloy -> String
		DynAlloyLinker linker = new DynAlloyLinker();
		Map<String, String> result = linker.link(dynalloyModules);
		return result;
	}


}
