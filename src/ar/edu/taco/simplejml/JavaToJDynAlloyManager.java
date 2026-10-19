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
import java.util.List;
import java.util.Map;

import org.multijava.mjc.JCompilationUnitType;

import ar.edu.jdynalloy.ast.JDynAlloyModule;
import ar.edu.jdynalloy.ast.JDynAlloyPrinter;
import ar.edu.taco.TacoConfigurator;
import ar.uba.dc.rfm.alloy.AlloyTyping;
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
	private final Object inputToFix;


	/**
	 * Creates the translator of one run of {@code engine.SimpleJmlStage}, the stage that turns
	 * the simplified Java + JML AST into JDynAlloy modules (see docs/ARCHITECTURE.md, the stage
	 * table). The compilation units are then translated one by one with
	 * {@link #processCompilationUnit}; the maps of each class's fields, the
	 * {@link SimpleJmlToJDynAlloyContext} and the lists that collect the encodings of arithmetic
	 * are shared by all of them. The class holds no static state, but the visitors it uses do
	 * (see {@link JDynAlloyASTVisitor}).
	 *
	 * @param compilation_units every simplified compilation unit of the run (copied into a new
	 *     list); each visitor passes them on to {@code helpers.MethodDeclarationSolver}
	 * @param inputToFix the input to fix, a {@code HashMap<String, Object>} with the receiver
	 *     under {@code "thiz"} and one entry per parameter, or {@code null}. {@code TacoMain}
	 *     passes one only in the second run of {@code checkAndRunSpecIfFaulty}; it becomes a
	 *     {@code requires} of the method under analysis (see docs/SPEC-OUTPUT.md)
	 */
	public JavaToJDynAlloyManager(List<JCompilationUnitType> compilation_units, Object inputToFix) {
		this.compilationUnits.addAll(compilation_units);
		this.inputToFix = inputToFix;
	}

	/**
	 * Returns a module as JDynAlloy text: a header line
	 * {@code //-------------- <signature> --------------//} followed by what
	 * {@code JDynAlloyPrinter} prints, with Java arithmetic if {@code useJavaArithmetic} is set
	 * (read from {@link TacoConfigurator}). {@code engine.JDynAlloyPrinterStage},
	 * {@code engine.JDynAlloyStage} and {@code engine.PrecompiledModules} write it to the
	 * {@code .djals} files under {@code output/}.
	 *
	 * @param module a module, translated or built in
	 * @return the module's text
	 */
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

	/**
	 * Returns the map between the simplified declarations and the modules and programs translated
	 * from them, filled by {@link #processCompilationUnit}. {@code engine.SimpleJmlStage} hands
	 * it to {@code jfsl.JfslStage}.
	 *
	 * @return the context of this manager (not a copy)
	 */
	public SimpleJmlToJDynAlloyContext getSimpleJmlToJDynAlloyContext() {
		return this.simpleJmlToJDynAlloyContext;
	}

	/**
	 * Translates one simplified compilation unit into JDynAlloy modules, one per class or
	 * interface it declares, with a new {@link JDynAlloyASTVisitor} that shares this manager's
	 * maps, context and arithmetic lists. Classes translated earlier are thus known to later ones
	 * (for {@code assignable} clauses that name another class's {@code objectState} or fields).
	 * Each declaration and its module or program are recorded in
	 * {@link #getSimpleJmlToJDynAlloyContext()}. The translation reads {@link TacoConfigurator}
	 * ({@code methodToCheck}, {@code useJavaArithmetic}, {@code checkNullDereference}, ...).
	 *
	 * @param unit a compilation unit after the JML simplification passes of
	 *     {@code engine.JmlStage}
	 * @return the modules of the unit, an inner class before the class around it
	 */
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
		astVisitor.setInputToFix(this.inputToFix);

		unit.accept(astVisitor);
		
		

		List<JDynAlloyModule> dynJAlloyModules = astVisitor.getModules();
		
//		PackedListOfJDynAlloyModule_InvariantVarsAndPreds p = 
//				new PackedListOfJDynAlloyModule_InvariantVarsAndPreds(dynJAlloyModules, 
//							astVisitor.getVarsEncodingValueOfArithmeticOperationsInObjectInvariants(), 
//							astVisitor.getPredsEncodingValueOfArithmeticOperationsInObjectInvariants());
		
		return dynJAlloyModules;
	}
}
