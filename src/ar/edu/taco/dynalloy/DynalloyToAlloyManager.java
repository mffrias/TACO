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

import ar.edu.jdynalloy.ast.*;
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

/**
 * TACO's bridge to dynalloy4's translation. {@link ar.edu.taco.engine.DynalloyStage} creates
 * one per translation and calls {@link #process_dynalloy_module}, which sets up a
 * {@code DynAlloyCompiler} with TACO's options and plugins and translates
 * {@code output/output.dals} into {@code output/output.als}. The plugins, in the order
 * dynalloy4 applies them:
 * <ul>
 * <li>on the DynAlloy AST, with {@code inferScope}: {@link DynAlloyProgramScopeInferencePlugin}
 *     (the counts) and {@link DynAlloyScopeInferencePlugin} (the scopes);</li>
 * <li>on the DynAlloy AST, always: {@code DynAlloyAppendCommandPlugin} (the {@code check}
 *     command and its scope);</li>
 * <li>on the Alloy AST, with {@code useJavaSBP}: {@code SymmBreakPredPlugin}, and with
 *     {@code useTightUpperBounds} too, {@code UBoundPlugin};</li>
 * <li>on the Alloy text, with {@code useJavaArithmetic}: {@code SkolemizejavaArithPlugin} and
 *     {@code CardinalSizeOfPlugin}.</li>
 * </ul>
 * See docs/ARCHITECTURE.md, and dynalloy4's docs/ARCHITECTURE.md ("Translation").
 *
 * <p>Static state: {@link #isCheckAndAfterRunSpec}.
 */
public class DynalloyToAlloyManager {

	private DynAlloyCompiler compiler;

	private boolean translatingForStryker = false;

	/**
	 * Static: whether this translation computes the specified output for a fixed input
	 * ({@code checkAndRunSpecIfFaulty}). {@code DynalloyStage.execute()} sets it before each
	 * translation, and {@link #process_dynalloy_module} copies it into dynalloy4's static
	 * {@code DynAlloyCompiler.isCheckAndAfterRunSpec}, which makes the assertion
	 * {@code pre => not post}, without the program. See docs/SPEC-OUTPUT.md.
	 */
	public static boolean isCheckAndAfterRunSpec = false;

	/**
	 * Creates a manager for one translation.
	 *
	 * @param forStryker whether to translate for Stryker's feedback checks, for which dynalloy4
	 *        negates the post ({@code (pre and program) => not post}); {@code DynalloyStage} sets
	 *        it when there is an input to fix and {@link #isCheckAndAfterRunSpec} is off. See
	 *        docs/SPEC-OUTPUT.md ("inputToFix").
	 */
	public DynalloyToAlloyManager(boolean forStryker){
		this.translatingForStryker = forStryker;
	}

	/**
	 * Translates a DynAlloy file into an Alloy file with dynalloy4, with TACO's options and
	 * plugins (see the class comment). Reads the configuration:
	 * {@code dynalloy.toAlloy.loopUnroll}, {@code dynalloy.toAlloy.strictUnrolling},
	 * {@code dynalloy.toAlloy.removeQuantifiers}, {@code removeExitWhileGuard} and the switches
	 * of the plugins. The module under analysis is
	 * the first source module. The formulas given to the scope inference
	 * ({@code DynAlloyOptions.setProgramSpecs}) are its object invariants and the {@code requires}
	 * and {@code ensures} of every spec case of its programs whose id is {@code assertionId}
	 * without {@code check_}: those of every overload of the method, since overloads share their
	 * id (see docs/FIXES-FOR-MASTER.md, N8). Copies {@link #isCheckAndAfterRunSpec} into
	 * {@code DynAlloyCompiler.isCheckAndAfterRunSpec}, writes {@code outputFilename}, and
	 * replaces the compiler that {@link #getCompiler()} returns.
	 *
	 * <p>Assumes that {@link #setSourceJDynAlloyModules(List)} was called, with the module under
	 * analysis first, and that {@code assertionId} starts with {@code check_}.
	 *
	 * @param inputFilename the DynAlloy file ({@code output/output.dals})
	 * @param outputFilename the Alloy file to write ({@code output/output.als})
	 * @param assertionId the assertion to translate, {@code check_<package>_<Class>_<method>}
	 * @param varsFromInvPerMod per module id, the variables that come from arithmetic
	 *        constraints in object invariants
	 * @param predsFromInvPerMod per module id, the predicates on those variables
	 * @param varsFromContractsPerProg per program id, the variables that come from arithmetic
	 *        constraints in its contract
	 * @param predsFromContractsPerProg per program id, the predicates on those variables
	 * @param inputToFix passed on to dynalloy4, which only tests it for {@code null}; see
	 *        dynalloy4's docs/ARCHITECTURE.md ("What TACO passes in")
	 * @return dynalloy4's {@code SpecContext} of the translation, which knows every action and
	 *         program
	 * @throws TacoException if dynalloy4 cannot read, parse or write the files, or does not find
	 *         the assertion; the message is kept, the cause is not
	 */
	public SpecContext process_dynalloy_module(String inputFilename, String outputFilename, String assertionId,
			HashMap<String, AlloyTyping> varsFromInvPerMod, 
			HashMap<String, List<AlloyFormula>> predsFromInvPerMod,
			HashMap<String, AlloyTyping> varsFromContractsPerProg,
			HashMap<String, List<AlloyFormula>> predsFromContractsPerProg, Object inputToFix) {
		SpecContext result = null;

		// optional fields
		final TacoConfigurator TACO_CONFIGURATION = TacoConfigurator.getInstance();

		int unroll = TACO_CONFIGURATION.getDynAlloyToAlloyLoopUnroll();

		boolean strictUnrolling = TACO_CONFIGURATION.getBoolean(TacoConfigurator.DYNALLOY_TO_ALLOY_STRICT_UNROLLING, false/* strictUnrolling */);

		boolean removeQuantifiers = TACO_CONFIGURATION.getRemoveQuantifiers();

		boolean removeExitWhileGuard = TACO_CONFIGURATION.getRemoveExitWhileGuard();

		try {
			compiler = new DynAlloyCompiler();

			DynAlloyOptions options = new DynAlloyOptions();
			options.setModuleUnderAnalysis(this.src_jdynalloy_modules.get(0).getModuleId());
//			Set<AlloyFormula> specFormulas = new HashSet<AlloyFormula>();
			JDynAlloyModule theModule = this.src_jdynalloy_modules.get(0);
			Set<JProgramDeclaration> thePrograms = theModule.getPrograms();
			Set<AlloyFormula> specFormulas = new HashSet<AlloyFormula>();
			for (JObjectInvariant oi : theModule.getObjectInvariants()){
				specFormulas.add(oi.getFormula()); //once for the invariant in the precondition
//				specFormulas.add(oi.getFormula()); //once for the invariant in the postcondition
			}
			for (JProgramDeclaration progDecl : thePrograms) {
				String fullyQualifiedMethodName = progDecl.getProgramId();
				if (assertionId.substring(6).equals(fullyQualifiedMethodName)) {
					List<JSpecCase> theSpecCases = progDecl.getSpecCases();
					for (JSpecCase specCase : theSpecCases) {
						List<JPrecondition> preconds = specCase.getRequires();
						for (JPrecondition pre : preconds) {
							specFormulas.add(pre.getFormula());
						}
						List<JPostcondition> postconds = specCase.getEnsures();
						for (JPostcondition post : postconds) {
							specFormulas.add(post.getFormula());
						}
					}
				}
			}


			options.setProgramSpecs(specFormulas);
			options.setAssertionToCheck(assertionId);
			options.setStrictUnroll(strictUnrolling);
			options.setRemoveQuantifier(removeQuantifiers);
			options.setLoopUnroll(unroll);
			options.setRunAlloyAnalyzer(false);
			options.setBuildDynAlloyTrace(false);
			options.setRemoveExitWhileGuard(removeExitWhileGuard);

			if (TacoConfigurator.getInstance().getInferScope() == true) {

				DynAlloyProgramScopeInferencePlugin program_scope_inference_plugin = new DynAlloyProgramScopeInferencePlugin(options.getProgramSpecs());
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
			

			DynAlloyCompiler.isCheckAndAfterRunSpec = DynalloyToAlloyManager.isCheckAndAfterRunSpec;

			AlloyModule alloyAST = compiler.compile(inputFilename, outputFilename, options, 
					varsFromInvPerMod, 
					predsFromInvPerMod,
					varsFromContractsPerProg,
					predsFromContractsPerProg, 
					this.translatingForStryker,
					inputToFix);

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

	/**
	 * Sets the JDynAlloy modules that the DynAlloy model came from
	 * ({@code DynalloyStage.setSourceJDynAlloy}), which the scope inference and the
	 * symmetry-breaking and tight-bounds plugins read. The first one is taken as the module under
	 * analysis. Call before {@link #process_dynalloy_module}.
	 *
	 * @param src_jdynalloy_modules the source modules, the module under analysis first
	 */
	public void setSourceJDynAlloyModules(List<JDynAlloyModule> src_jdynalloy_modules) {
		this.src_jdynalloy_modules = src_jdynalloy_modules;
	}

	/**
	 * Returns the compiler of the last {@link #process_dynalloy_module} call, whose
	 * {@code SpecContext} {@code JavaTraceStage} needs
	 * ({@code DynalloyStage.getDynAlloyCompiler()}).
	 *
	 * @return the compiler, or {@code null} before the first call
	 */
	public DynAlloyCompiler getCompiler() {
		return compiler;
	}

}
