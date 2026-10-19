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

package ar.edu.taco.engine;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import ar.edu.jdynalloy.ast.JDynAlloyModule;
import ar.edu.jdynalloy.xlator.JType;
import ar.edu.taco.TacoConfigurator;
import ar.edu.taco.dynalloy.DynalloyToAlloyManager;
import ar.edu.taco.simplejml.helpers.JavaClassNameNormalizer;
import ar.uba.dc.rfm.alloy.AlloyTyping;
import ar.uba.dc.rfm.alloy.ast.expressions.AlloyExpression;
import ar.uba.dc.rfm.alloy.ast.expressions.ExprVariable;
import ar.uba.dc.rfm.alloy.ast.formulas.AlloyFormula;
import ar.uba.dc.rfm.dynalloy.DynAlloyCompiler;
import ar.uba.dc.rfm.dynalloy.xlator.SpecContext;

/**
 * Translates the DynAlloy model that {@link JDynAlloyStage} wrote
 * ({@code <output_dir>/output.dals}) into Alloy ({@code <output_dir>/output.als}), with
 * dynalloy4 and TACO's plugins ({@code DynalloyToAlloyManager}: scope inference, the
 * {@code check} command and its scopes, symmetry breaking, tight bounds, Java arithmetic).
 * The assertion checked is {@code check_<methodToCheck without parameter types>}. Next comes
 * {@link AlloyStage}.
 *
 * <p>A fixed input changes the translation in two ways, told apart by the static
 * {@link #isCheckAndAfterRunSpec}: for Stryker's feedback the post is negated and the input
 * is not passed to DynAlloy; for the specified output, the assertion has no program and the
 * input is passed on. See docs/SPEC-OUTPUT.md ("inputToFix").
 */
public class DynalloyStage implements ITacoStage {

	static final private String OUTPUT_ALLOY_EXTENSION = ".als";

	List<String> inputDynalloyModulesFileNames;

	private String alloy_filename;

	private SpecContext specContext;

	private DynalloyToAlloyManager dynalloyToAlloyManager;

	private boolean translatingForStryker = false;

	/**
	 * Static: whether TACO computes the specified output for a fixed input
	 * ({@code checkAndRunSpecIfFaulty}). {@link #execute()} copies it into
	 * {@code DynalloyToAlloyManager.isCheckAndAfterRunSpec}, and from there into dynalloy4's
	 * {@code DynAlloyCompiler.isCheckAndAfterRunSpec}, which builds the assertion
	 * {@code pre => not post} without the program. {@code TacoMain.run} sets it before each
	 * translation from {@code TacoMain.isCheckAndRunSpecAssertionSupport}, which is never
	 * reset.
	 */
	public static boolean  isCheckAndAfterRunSpec = false;

	/** The input to fix given to the constructor, or {@code null}. */
	public Object inputToFix = null;


//	private AlloyTyping varsEncodingValueOfArithmeticOperationsInContracts;

//	private List<AlloyFormula> predsEncodingValueOfArithmeticOperationsInContracts;

	/**
	 * This method returns the SpecContext used by the last stage execution
	 * or null if the stage has never been executed.
	 * */
	public SpecContext getSpecContext() {
		return specContext;
	}


	HashMap<String, AlloyTyping> varsAndTheirTypesComingFromArithmeticConstraintsInContractsByProgram = new HashMap<String, AlloyTyping>();
	HashMap<String, List<AlloyFormula>> predsComingFromArithmeticConstraintsInContractsByProgram = new HashMap<String, List<AlloyFormula>>();

	HashMap<String, AlloyTyping> varsAndTheirTypesComingFromArithmeticConstraintsInObjectInvariantsByModule = new HashMap<String,AlloyTyping>();
	HashMap<String, List<AlloyFormula>> predsComingFromArithmeticConstraintsInObjectInvariantsByModule = new HashMap<String, List<AlloyFormula>> ();


	/**
	 * Creates the stage.
	 *
	 * @param inputDynalloyModulesFileNames ignored: {@link #execute()} always reads
	 *        {@code <output_dir>/output.dals}
	 * @param varsFromInvPerMod per DynAlloy module id, the variables that come from
	 *        arithmetic constraints in object invariants
	 * @param predsFromInvPerMod per module id, the predicates on those variables
	 * @param varsFromContractsPerProg per program id, the variables that come from arithmetic
	 *        constraints in its contract
	 * @param predsFromContractsPerProg per program id, the predicates on those variables
	 * @param inputToFix the input to fix, or {@code null}
	 */
	public DynalloyStage(List<String> inputDynalloyModulesFileNames,
						 HashMap<String, AlloyTyping> varsFromInvPerMod,
						 HashMap<String, List<AlloyFormula>> predsFromInvPerMod,
						 HashMap<String, AlloyTyping> varsFromContractsPerProg,
						 HashMap<String, List<AlloyFormula>> predsFromContractsPerProg,
						 Object inputToFix) {
		this.inputDynalloyModulesFileNames = inputDynalloyModulesFileNames;
		this.varsAndTheirTypesComingFromArithmeticConstraintsInObjectInvariantsByModule = varsFromInvPerMod;
		this.predsComingFromArithmeticConstraintsInObjectInvariantsByModule = predsFromInvPerMod;
		this.varsAndTheirTypesComingFromArithmeticConstraintsInContractsByProgram = varsFromContractsPerProg;
		this.predsComingFromArithmeticConstraintsInContractsByProgram = predsFromContractsPerProg;
		this.inputToFix = inputToFix;

		//mfrias4-10/17/2025 The following code was commented to generate a contract-
		//complying i/o instance according to the spec after the program
		//failed with respect to its contract. If Stryker is going to be used
		//this needs to be uncommented.
		// Both uses of inputToFix are now told apart in execute(), once
		// isCheckAndAfterRunSpec has been set.
	}

	/**
	 * Translates {@code <output_dir>/output.dals} into {@code <output_dir>/output.als}
	 * ({@link #get_alloy_filename()}), checking the assertion
	 * {@code check_<methodToCheck before its '('>}, as read from the configuration's
	 * {@code methodToCheck} property. The translation is for Stryker
	 * ({@code (pre and program) => not post}, so that SAT means that some value of the
	 * variabilized parts makes the input satisfy the contract) when there is an input to fix
	 * and {@link #isCheckAndAfterRunSpec} is off; the input itself is passed to DynAlloy only
	 * when not translating for Stryker. Sets the static
	 * {@code DynalloyToAlloyManager.isCheckAndAfterRunSpec}. Assumes
	 * {@link #setSourceJDynAlloy(List)} was called: {@code DynalloyToAlloyManager} takes the
	 * first of those modules as the module under analysis, and gives its invariants and the
	 * pre- and postconditions of its program named like the assertion to the scope inference.
	 *
	 * @throws ar.edu.taco.TacoException if dynalloy4 fails to parse or translate the model, or the
	 *         assertion is not found
	 */
	@Override
	public void execute() {
		// A fixed input comes either from Stryker's feedback (variabilization), which
		// needs the translation for Stryker, or from the computation of the spec's output
		// for a hardcoded input (isCheckAndAfterRunSpec), which must not get it.
		this.translatingForStryker = inputToFix != null && !isCheckAndAfterRunSpec;
		dynalloyToAlloyManager = new DynalloyToAlloyManager(this.translatingForStryker);
		DynalloyToAlloyManager.isCheckAndAfterRunSpec = isCheckAndAfterRunSpec;

		String output_dir = TacoConfigurator.getInstance().getOutputDir() ;
		alloy_filename = output_dir + java.io.File.separator + "output" + OUTPUT_ALLOY_EXTENSION;
		String dynalloy_filename = output_dir + java.io.File.separator + "output.dals";
		inputDynalloyModulesFileNames = Collections.singletonList(dynalloy_filename);

		JavaClassNameNormalizer classToCheckNormalizer = new JavaClassNameNormalizer(TacoConfigurator.getInstance().getString(
				TacoConfigurator.CLASS_TO_CHECK_FIELD));
		int finalPos = TacoConfigurator.getInstance().getString(TacoConfigurator.METHOD_TO_CHECK_FIELD).indexOf('(');
		String methodToCheckWithoutTyping = TacoConfigurator.getInstance().getString(TacoConfigurator.METHOD_TO_CHECK_FIELD).substring(0, finalPos);
		String assertion_id = "check_" /*+ classToCheckNormalizer.getQualifiedClassName() + "_"*/
				+ methodToCheckWithoutTyping;

		dynalloyToAlloyManager.setSourceJDynAlloyModules(this.src_jdynalloy_modules);
		// DynAlloy uses the fixed input only for the spec's output (b58e74b in dynalloy4, a block of
		// DynalloyXlatorVisitor that widens index ranges); Stryker's translation must not get it.
		Object inputToFixForDynalloy = this.translatingForStryker ? null : inputToFix;
		specContext = dynalloyToAlloyManager.process_dynalloy_module(dynalloy_filename , alloy_filename, assertion_id,
				varsAndTheirTypesComingFromArithmeticConstraintsInObjectInvariantsByModule,
				predsComingFromArithmeticConstraintsInObjectInvariantsByModule,
				varsAndTheirTypesComingFromArithmeticConstraintsInContractsByProgram,
				predsComingFromArithmeticConstraintsInContractsByProgram, inputToFixForDynalloy);
	}

	/**
	 * Returns the Alloy file written by the last {@link #execute()}.
	 *
	 * @return {@code <output_dir>/output.als}, or {@code null} before {@link #execute()}
	 */
	public String get_alloy_filename() {
		return alloy_filename;
	}


	private List<JDynAlloyModule> src_jdynalloy_modules;

	/**
	 * Sets the JDynAlloy modules that the DynAlloy model came from
	 * ({@link JDynAlloyStage#getPrunedModules()}), which the scope inference and the
	 * symmetry-breaking and tight-bounds plugins read. The first module is taken as the module
	 * under analysis. Call before {@link #execute()}.
	 *
	 * @param jdynalloy_modules the source modules
	 */
	public void setSourceJDynAlloy(List<JDynAlloyModule> jdynalloy_modules) {
		src_jdynalloy_modules = jdynalloy_modules;
	}

	/**
	 * Returns the DynAlloy compiler of the last {@link #execute()}, whose spec context
	 * {@link JavaTraceStage} needs.
	 *
	 * @return the compiler; call only after {@link #execute()} (before it, this throws a
	 *         {@link NullPointerException})
	 */
	public DynAlloyCompiler getDynAlloyCompiler() {
		return dynalloyToAlloyManager.getCompiler();
	}
}
