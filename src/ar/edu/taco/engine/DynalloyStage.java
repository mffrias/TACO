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

public class DynalloyStage implements ITacoStage {

	static final private String OUTPUT_ALLOY_EXTENSION = ".als";

	List<String> inputDynalloyModulesFileNames;

	private String alloy_filename;

	private SpecContext specContext;

	private DynalloyToAlloyManager dynalloyToAlloyManager;

	private boolean translatingForStryker = false;

	public static boolean  isCheckAndAfterRunSpec = false;


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

		//mfrias4-10/17/2025 The following code was commented to generate a contract-
		//complying i/o instance according to the spec after the program
		//failed with respect to its contract. If Stryker is going to be used
		//this needs to be uncommented.
//		if (inputToFix != null)
//			translatingForStryker = true;
	}

	@Override
	public void execute() {
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
		String assertion_id = "check_" + classToCheckNormalizer.getQualifiedClassName() + "_"
				+ methodToCheckWithoutTyping;

		dynalloyToAlloyManager.setSourceJDynAlloyModules(this.src_jdynalloy_modules);
		specContext = dynalloyToAlloyManager.process_dynalloy_module(dynalloy_filename , alloy_filename, assertion_id,
				varsAndTheirTypesComingFromArithmeticConstraintsInObjectInvariantsByModule,
				predsComingFromArithmeticConstraintsInObjectInvariantsByModule,
				varsAndTheirTypesComingFromArithmeticConstraintsInContractsByProgram,
				predsComingFromArithmeticConstraintsInContractsByProgram);
	}

	public String get_alloy_filename() {
		return alloy_filename;
	}


	private List<JDynAlloyModule> src_jdynalloy_modules;

	public void setSourceJDynAlloy(List<JDynAlloyModule> jdynalloy_modules) {
		src_jdynalloy_modules = jdynalloy_modules;
	}

	public DynAlloyCompiler getDynAlloyCompiler() {
		return dynalloyToAlloyManager.getCompiler();
	}
}
