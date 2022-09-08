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
package ar.edu.taco.simplejml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.multijava.mjc.JCompilationUnitType;

import ar.edu.jdynalloy.ast.JDynAlloyModule;
import ar.edu.jdynalloy.ast.JDynAlloyPrinter;
import ar.edu.jdynalloy.xlator.JType;
import ar.edu.taco.TacoConfigurator;
import ar.edu.taco.simplejml.helpers.PackedListOfJDynAlloyModule_InvariantVarsAndPreds;
import ar.uba.dc.rfm.alloy.AlloyTyping;
import ar.uba.dc.rfm.alloy.ast.expressions.AlloyExpression;
import ar.uba.dc.rfm.alloy.ast.expressions.ExprVariable;
import ar.uba.dc.rfm.alloy.ast.formulas.AlloyFormula;

/**
 * @author ggasser
 * 
 */
public class JavaToJDynAlloyManager {
	private final Map<String, List<String>> modulesObjectState = new HashMap<String, List<String>>();
	private final Map<String, List<String>> modulesNoStaticFields = new HashMap<String, List<String>>();
	private final SimpleJmlToJDynAlloyContext simpleJmlToJDynAlloyContext = new SimpleJmlToJDynAlloyContext();
	private final AlloyTyping varsEncodingValueOfArithmeticOperationsInRequiresAndEnsures = new AlloyTyping();
	private final List<AlloyFormula> predsEncodingValueOfArithmeticOperationsInRequiresAndEnsures = new ArrayList<AlloyFormula>();
	private final AlloyTyping varsEncodingValueOfArithmeticOperationsInObjectInvariants = new AlloyTyping();
	private final List<AlloyFormula> predsEncodingValueOfArithmeticOperationsInObjectInvariants = new ArrayList<AlloyFormula>();
	private final List<JCompilationUnitType> compilationUnits = new ArrayList<JCompilationUnitType>();
	
	
	public JavaToJDynAlloyManager(List<JCompilationUnitType> compilation_units) {
		this.compilationUnits.addAll(compilation_units);
	}

	public static String getModuleOutput(JDynAlloyModule module) {
		StringBuffer sb = new StringBuffer();

		String modHeader = headerComment(module.getSignature().getSignatureId());
		String modBody = (String) module.accept(new JDynAlloyPrinter(TacoConfigurator.getInstance().getUseJavaArithmetic()));
		sb.append(modHeader);
		sb.append(modBody);

		return sb.toString();
	}

	private static String headerComment(String fragmentId) {
		return "//-------------- " + fragmentId + " --------------//" + "\n";
	}

	public SimpleJmlToJDynAlloyContext getSimpleJmlToJDynAlloyContext() {
		return this.simpleJmlToJDynAlloyContext;
	}

	public List<JDynAlloyModule> processCompilationUnit(JCompilationUnitType unit) {
		JDynAlloyASTVisitor astVisitor = new JDynAlloyASTVisitor(
				this.modulesObjectState, 
				this.modulesNoStaticFields, 
				this.simpleJmlToJDynAlloyContext, 
				this.varsEncodingValueOfArithmeticOperationsInRequiresAndEnsures,
				this.predsEncodingValueOfArithmeticOperationsInRequiresAndEnsures,
				this.varsEncodingValueOfArithmeticOperationsInObjectInvariants,
				this.predsEncodingValueOfArithmeticOperationsInObjectInvariants,
				this.compilationUnits);

		unit.accept(astVisitor);
		
		

		List<JDynAlloyModule> dynJAlloyModules = astVisitor.getModules();
		
//		PackedListOfJDynAlloyModule_InvariantVarsAndPreds p = 
//				new PackedListOfJDynAlloyModule_InvariantVarsAndPreds(dynJAlloyModules, 
//							astVisitor.getVarsEncodingValueOfArithmeticOperationsInObjectInvariants(), 
//							astVisitor.getPredsEncodingValueOfArithmeticOperationsInObjectInvariants());
		
		return dynJAlloyModules;
	}
}
