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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import ar.edu.jdynalloy.ast.JDynAlloyModule;
import ar.edu.jdynalloy.binding.BindingManager;
import ar.edu.jdynalloy.modifies.ModifiesSolverManager;
import ar.edu.jdynalloy.relevancy.RelevancyAnalysisManager;
import ar.edu.jdynalloy.relevancy.Scene;
import ar.edu.jdynalloy.slicer.SceneSlicerManager;
import ar.edu.jdynalloy.xlator.JDynAlloyBinding;
import ar.edu.taco.TacoConfigurator;
import ar.edu.taco.jdynalloy.JDynAlloyToDynAlloyManager;
import ar.edu.taco.simplejml.JavaToJDynAlloyManager;
import ar.edu.taco.utils.FileUtils;
import ar.uba.dc.rfm.dynalloy.ast.DynalloyModule;

/**
 * @author ggasser
 *
 */
public class JDynAlloyStage implements ITacoStage {
	private static Logger log = Logger.getLogger(JDynAlloyStage.class);
	static final private String OUTPUT_DYNALLOY_EXTENSION = ".dals";
	static final private String OUTPUT_JDYNALLOY_WITH_OUT_MODIFIES_EXTENSION = ".without.modifies.djals";
	static final private String OUTPUT_JDYNALLOY_WITH_OUT_CALLSPEC_EXTENSION = ".without.callspec.djals";
	private boolean removeQuantifiers;
	private boolean javaArithmetic;
	private Object inputToFix = null;

	/**
	 * Returns the value given to {@link #setRemoveQuantifiers(boolean)}.
	 *
	 * @return the stored flag, {@code false} by default
	 */
	public boolean getRemoveQuantifiers(){
		return this.removeQuantifiers;
	}

	/**
	 * Stores a remove-quantifiers flag that {@link #execute()} does not read; jDynAlloy and
	 * DynAlloy take {@code dynalloy.toAlloy.removeQuantifiers} from the configuration.
	 * {@code TacoMain} sets it from {@code TacoConfigurator.getRemoveQuantifiers()}.
	 *
	 * @param rq the flag
	 */
	public void setRemoveQuantifiers(boolean rq){
		this.removeQuantifiers = rq;
	}

	/**
	 * Returns the value given to {@link #setJavaArithmetic(boolean)}.
	 *
	 * @return the stored flag, {@code false} by default
	 */
	public boolean getJavaArithmetic(){
		return this.javaArithmetic;
	}

	/**
	 * Sets whether the bindings ({@code BindingManager}) are built for Java arithmetic.
	 * Only the bindings use it: the relevancy analysis and the modifies solver read
	 * {@code useJavaArithmetic} from the configuration. {@code TacoMain} sets it from that
	 * key too; call before {@link #execute()}.
	 *
	 * @param b whether integers are modelled with Java arithmetic
	 */
	public void setJavaArithmetic(boolean b){
		this.javaArithmetic = b;
	}

	private List<JDynAlloyModule> modules;
	private Vector<DynalloyModule> generatedModules;
	private List<String> outputFileNames;
	private String classToCheck;
	private String methodToCheck;

	/**
	 * Returns the DynAlloy modules produced by the translation. {@code TacoMain} reads from
	 * them, per module and per program, the variables and predicates that come from
	 * arithmetic constraints in invariants and contracts, for {@link DynalloyStage}.
	 *
	 * @return the modules of the last {@link #execute()}, or {@code null} before it
	 */
	public Vector<DynalloyModule> getGeneratedModules(){
		return generatedModules;
	}


	/**
	 * Creates the jDynAlloy stage.
	 *
	 * @param modules every JDynAlloy module of the analysis: those translated from Java, the
	 *        built-in, static-fields and parsed ones
	 * @param classToCheck the class under analysis as the driver gives it, with dots
	 *        ({@code classToCheck} of the overriding properties)
	 * @param methodToCheck {@code methodToCheck} without its parameter types, e.g.
	 *        {@code pkg_Class_contains}; with {@code classToCheck}, it is passed to
	 *        jDynAlloy's {@code JDynAlloyTranslator} (call graph for the recursion unfolding,
	 *        and the translation visitor)
	 * @param inputToFix the input to fix, or {@code null}; with one, jDynAlloy translates no
	 *        {@code requires} (the callees' included) and gives the method under analysis the
	 *        input as its precondition. See docs/SPEC-OUTPUT.md ("inputToFix")
	 */
	public JDynAlloyStage(List<JDynAlloyModule> modules, String classToCheck, String methodToCheck, Object inputToFix) {
		this.modules = modules;
		this.inputToFix = inputToFix;
		this.classToCheck = classToCheck;
		this.methodToCheck = methodToCheck;
	}

	/**
	 * Returns the DynAlloy files for {@link DynalloyStage}.
	 *
	 * @return one path, {@code <output_dir>/output.dals}; {@code null} before
	 *         {@link #execute()}
	 */
	public List<String> getOutputFileNames() {
		return outputFileNames;
	}

	/**
	 * Returns the JDynAlloy modules as {@link #execute()} left them: sliced to the relevant
	 * classes (with {@code relevancyAnalisys}) and with their modifies clauses resolved.
	 * {@code TacoMain} gives them to {@link DynalloyStage#setSourceJDynAlloy(List)}.
	 *
	 * @return the modules; before {@link #execute()}, the list given to the constructor
	 */
	public List<JDynAlloyModule> getPrunedModules() {
		return modules;
	}



	/**
	 * Translates the JDynAlloy modules into DynAlloy, with jDynAlloy:
	 * <ol>
	 * <li>builds the bindings ({@code BindingManager}), with the recursion unfolding bound
	 *     {@code dynalloy.toAlloy.loopUnroll};</li>
	 * <li>with {@code relevancyAnalisys}: computes the relevant classes
	 *     ({@code RelevancyAnalysisManager}), stores them in the configuration's
	 *     {@code relevantClasses}, replacing the configured ones, and slices away the modules,
	 *     fields and programs of the others ({@code SceneSlicerManager});</li>
	 * <li>resolves the modifies clauses ({@code ModifiesSolverManager}); with the log level of
	 *     this class at DEBUG or TRACE, writes the result as
	 *     {@code <output_dir>/<module path>.without.modifies.djals};</li>
	 * <li>builds the bindings again and translates ({@code JDynAlloyToDynAlloyManager}),
	 *     which also unfolds recursion and applies the input to fix;</li>
	 * <li>writes each DynAlloy module to {@code <output_dir>/<module id with every _ as />.dals}
	 *     and all of them, the {@code prelude} first, to {@code <output_dir>/output.dals}, the
	 *     file the next stage reads.</li>
	 * </ol>
	 * I/O errors when writing are printed, not thrown.
	 */
	@Override
	public void execute() {

		outputFileNames = new ArrayList<String>();

		//relevancy analysis
		TacoConfigurator tacoConfigurator = TacoConfigurator.getInstance();

		JDynAlloyBinding dynJAlloyBinding;
		dynJAlloyBinding = regenerateBindings(tacoConfigurator.getDynAlloyToAlloyLoopUnroll());

		List<String> relevantClassesList = null;
		Scene relevantAnalysisScene = null;
		if (tacoConfigurator.getRelevancyAnalysis()) {
			log.debug("Starting relevancy analysis");
			RelevancyAnalysisManager relevancyAnalysisManager = new RelevancyAnalysisManager();
			relevancyAnalysisManager.setBitWidth(TacoConfigurator.getInstance().getBitwidth());
			relevancyAnalysisManager.setJavaArithmetic(tacoConfigurator.getUseJavaArithmetic());
			boolean isJavaArithmetic = tacoConfigurator.getUseJavaArithmetic();
			String relevantClasses = relevancyAnalysisManager.process(this.modules, dynJAlloyBinding, isJavaArithmetic);

			relevantAnalysisScene = relevancyAnalysisManager.getScene();

			tacoConfigurator.setProperty(TacoConfigurator.RELEVANT_CLASSES, relevantClasses);
			relevantClassesList = tacoConfigurator.getRelevantClasses();
			log.debug("Relevant Classes: " + relevantClassesList);
		}

		// slice irrelevant modules, fields & programs
		if (relevantClassesList != null && relevantAnalysisScene != null) {
			SceneSlicerManager sceneSlicerManager = new SceneSlicerManager();
			this.modules = sceneSlicerManager.process(this.modules, relevantClassesList, relevantAnalysisScene);
		}

		// Modifies Solver
		ModifiesSolverManager modifiesSolverManager = new ModifiesSolverManager();
		this.modules = modifiesSolverManager.process(this.modules, (JDynAlloyBinding) null, TacoConfigurator.getInstance().getUseJavaArithmetic());



		// activate setting "debug" logger lever for this class log4j
		// configuration!
		if (log.getLevel() == Level.DEBUG || log.getLevel() == Level.TRACE) {
			printToFile(this.modules, OUTPUT_JDYNALLOY_WITH_OUT_MODIFIES_EXTENSION);
		}

		dynJAlloyBinding = regenerateBindings(tacoConfigurator.getDynAlloyToAlloyLoopUnroll());

//		// activate setting "debug" logger lever for this class log4j
//		// configuration!
//		if (log.getLevel() == Level.DEBUG || log.getLevel() == Level.TRACE) {
//			printToFile(this.modules, OUTPUT_JDYNALLOY_WITH_OUT_CALLSPEC_EXTENSION);
//		}
//
//		dynJAlloyBinding = regenerateBindings(tacoConfigurator.getDynAlloyToAlloyLoopUnroll());

		JDynAlloyToDynAlloyManager dynJAlloyToDynAlloyManager = new JDynAlloyToDynAlloyManager(this.classToCheck, this.methodToCheck, inputToFix);
		Map<String, String> output = dynJAlloyToDynAlloyManager.process(this.modules, dynJAlloyBinding);

		this.generatedModules = dynJAlloyToDynAlloyManager.getDynalloyModules();

		for (Entry<String, String> entry : output.entrySet()) {
			String moduleName = entry.getKey();
			String moduleBody = entry.getValue();

			String output_dir = TacoConfigurator.getInstance().getOutputDir();

			String moduleFilename = output_dir + java.io.File.separator + moduleName.replaceAll("_", "/") + OUTPUT_DYNALLOY_EXTENSION;
			try {
				FileUtils.writeToFile(moduleFilename, moduleBody);
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

//mfrias-mffrias 04082013: place where collected preds and vars can be updated in the structure.


		// print output in DynAlloy
		String output_dir = TacoConfigurator.getInstance().getOutputDir() ;
		String filename = output_dir + java.io.File.separator + "output" + OUTPUT_DYNALLOY_EXTENSION;

		try {
			writeDynAlloyOutput(filename, output);
		} catch (IOException e) {
			e.printStackTrace();
		}

		// add to output current dynalloy module
		outputFileNames.add(filename);

	}

	private JDynAlloyBinding regenerateBindings(int unfoldScope) {
		BindingManager bindingManager;
		JDynAlloyBinding dynJAlloyBinding;
		bindingManager = new BindingManager(modules);
		bindingManager.setJavaArithmetic(javaArithmetic);
		bindingManager.execute();
		dynJAlloyBinding = bindingManager.getDynJAlloyBinding();
		dynJAlloyBinding.unfoldScopeForRecursiveCode = unfoldScope;
		return dynJAlloyBinding;
	}

	private void writeDynAlloyOutput(String path, Map<String, String> output) throws IOException {

		StringBuffer sb = new StringBuffer();

		// the firt entry must be the prelude entry
		String preludeModuleBody = output.get("prelude");
		appendModule(sb, "prelude", preludeModuleBody);

		for (Entry<String, String> outputEntry : output.entrySet()) {
			// skip prelude entry
			if (!outputEntry.getKey().equals("prelude")) {
				String moduleHeader = outputEntry.getKey();
				String moduleBody = outputEntry.getValue();
				appendModule(sb, moduleHeader, moduleBody);
			}
		}

		FileUtils.writeToFile(path, sb.toString());
	}

	private void appendModule(StringBuffer sb, String moduleHeader, String moduleBody) {
		sb.append(headerComment(moduleHeader));
		sb.append(moduleBody);
	}

	private String headerComment(String fragmentId) {
		return "//-------------- " + fragmentId + "--------------//" + "\n";
	}

	private void printToFile(List<JDynAlloyModule> modules, String extension) {
		for (JDynAlloyModule dynJAlloyModule : modules) {
			printToFile(dynJAlloyModule, extension);
		}
	}

	private void printToFile(JDynAlloyModule module, String extension) {
		String output_dir = TacoConfigurator.getInstance().getOutputDir();
		String filePath = output_dir + java.io.File.separator + module.getModuleId().replaceAll("_", "/") + extension;
		try {
			String moduleOutput = JavaToJDynAlloyManager.getModuleOutput(module);
			FileUtils.writeToFile(filePath, moduleOutput);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
