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

package ar.edu.taco.dynalloy;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import antlr.RecognitionException;
import antlr.TokenStreamException;

import ar.edu.jdynalloy.ast.JDynAlloyModule;
import ar.edu.jdynalloy.ast.JField;
import ar.edu.jdynalloy.ast.JProgramDeclaration;
import ar.edu.jdynalloy.xlator.JType;
import ar.edu.taco.TacoConfigurator;
import ar.edu.taco.TacoException;
import ar.edu.taco.alloy.CardinalSizeOfPlugin;
import ar.edu.taco.alloy.bound.UBoundPlugin;
import ar.edu.taco.alloy.sbp.SymmBreakPredPlugin;
import ar.edu.taco.alloy.sk.SkolemizejavaArithPlugin;
import ar.uba.dc.rfm.alloy.AlloyTyping;
import ar.uba.dc.rfm.alloy.AlloyVariable;
import ar.uba.dc.rfm.alloy.ast.AlloyModule;
import ar.uba.dc.rfm.alloy.ast.expressions.AlloyExpression;
import ar.uba.dc.rfm.alloy.ast.expressions.ExprVariable;
import ar.uba.dc.rfm.alloy.ast.formulas.AlloyFormula;
import ar.uba.dc.rfm.dynalloy.DynAlloyCompiler;
import ar.uba.dc.rfm.dynalloy.DynAlloyOptions;
import ar.uba.dc.rfm.dynalloy.parser.AssertionNotFound;
import ar.uba.dc.rfm.dynalloy.xlator.SpecContext;

public class DynalloyToAlloyManager {

	private DynAlloyCompiler compiler;

	private boolean translatingForStryker = false;

	public static boolean isCheckAndAfterRunSpec = false;

	public DynalloyToAlloyManager(boolean forStryker){
		this.translatingForStryker = forStryker;
	}

	public SpecContext process_dynalloy_module(String inputFilename, String outputFilename, String assertionId,
			HashMap<String, AlloyTyping> varsFromInvPerMod, 
			HashMap<String, List<AlloyFormula>> predsFromInvPerMod,
			HashMap<String, AlloyTyping> varsFromContractsPerProg,
			HashMap<String, List<AlloyFormula>> predsFromContractsPerProg) {
		SpecContext result = null;

		// optional fields
		final TacoConfigurator TACO_CONFIGURATION = TacoConfigurator.getInstance();

		int unroll = TACO_CONFIGURATION.getDynAlloyToAlloyLoopUnroll();

		boolean strictUnrolling = TACO_CONFIGURATION.getBoolean(TacoConfigurator.DYNALLOY_TO_ALLOY_STRICT_UNROLLING, false/* strictUnrolling */);

		boolean removeQuantifiers = TACO_CONFIGURATION.getRemoveQuantifiers();

		boolean removeExitWhileGuard = TACO_CONFIGURATION.getRemoveExitWhileGuard();

		try {
			compiler = new DynAlloyCompiler();

			if (TacoConfigurator.getInstance().getInferScope() == true) {

				DynAlloyProgramScopeInferencePlugin program_scope_inference_plugin = new DynAlloyProgramScopeInferencePlugin();
				compiler.addDynAlloyASTPlugin(program_scope_inference_plugin);

				DynAlloyScopeInferencePlugin final_scope_inference_plugin = new DynAlloyScopeInferencePlugin();
				final_scope_inference_plugin.setProgramScopeInferencePlugin(program_scope_inference_plugin);
				final_scope_inference_plugin.setJDynAlloyModules(src_jdynalloy_modules);
				compiler.addDynAlloyASTPlugin(final_scope_inference_plugin);
				
			}

			compiler.addDynAlloyASTPlugin(new DynAlloyAppendCommandPlugin());		


			if (TacoConfigurator.getInstance().getUseJavaSBP() == true) {
				SymmBreakPredPlugin plugin = new SymmBreakPredPlugin();
				plugin.setSourceJDynAlloyModules(this.src_jdynalloy_modules);
				compiler.addAlloyASTPlugin(plugin);

				if (TacoConfigurator.getInstance().getUseTightUpperBounds() == true) {
					UBoundPlugin upperBoundPlugin = new UBoundPlugin();
					upperBoundPlugin.setSourceJDynAlloyModules(this.src_jdynalloy_modules);
					compiler.addAlloyASTPlugin(upperBoundPlugin);
				}
			}

			if (TacoConfigurator.getInstance().getUseJavaArithmetic() == true) {
				SkolemizejavaArithPlugin sk_plugin = new SkolemizejavaArithPlugin();
				compiler.addAlloyStringPlugin(sk_plugin);

				CardinalSizeOfPlugin cardinal_plugin = new CardinalSizeOfPlugin();
				compiler.addAlloyStringPlugin(cardinal_plugin);
			}
			
			DynAlloyOptions options = new DynAlloyOptions();
			options.setModuleUnderAnalysis(this.src_jdynalloy_modules.get(0).getModuleId());
			options.setAssertionToCheck(assertionId);
			options.setStrictUnroll(strictUnrolling);
			options.setRemoveQuantifier(removeQuantifiers);
			options.setLoopUnroll(unroll);
			options.setRunAlloyAnalyzer(false);
			options.setBuildDynAlloyTrace(false);
			options.setRemoveExitWhileGuard(removeExitWhileGuard);

			DynAlloyCompiler.isCheckAndAfterRunSpec = DynalloyToAlloyManager.isCheckAndAfterRunSpec;

			AlloyModule alloyAST = compiler.compile(inputFilename, outputFilename, options, 
					varsFromInvPerMod, 
					predsFromInvPerMod,
					varsFromContractsPerProg,
					predsFromContractsPerProg, 
					this.translatingForStryker);

			result = compiler.getSpecContext();

		} catch (RecognitionException e) {
			throw new TacoException(e.getMessage());

		} catch (TokenStreamException e) {
			throw new TacoException(e.getMessage());

		} catch (IOException e) {
			throw new TacoException(e.getMessage());

		} catch (AssertionNotFound e) {
			throw new TacoException(e.getMessage());
		}

		return result;
	}

	private List<JDynAlloyModule> src_jdynalloy_modules;

	public void setSourceJDynAlloyModules(List<JDynAlloyModule> src_jdynalloy_modules) {
		this.src_jdynalloy_modules = src_jdynalloy_modules;
	}

	public DynAlloyCompiler getCompiler() {
		return compiler;
	}

}
