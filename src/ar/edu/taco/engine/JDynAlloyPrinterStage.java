package ar.edu.taco.engine;

import java.io.IOException;
import java.util.List;

import ar.edu.jdynalloy.ast.JDynAlloyModule;
import ar.edu.taco.TacoConfigurator;
import ar.edu.taco.simplejml.JavaToJDynAlloyManager;
import ar.edu.taco.utils.FileUtils;

/**
 * Writes JDynAlloy modules to files, for inspection only: nothing in TACO reads them back.
 * {@code TacoMain.run} runs it on the modules of {@link SimpleJmlStage} (after the JFSL
 * stage), which become {@code <output_dir>/<module id with every _ as />.djals}, e.g.
 * {@code output/icse/singlylinkedlist/set1/SinglyLinkedListContains1Bug25D.djals}
 * ({@code output_dir} defaults to {@code output}). Holds no static state.
 */
public class JDynAlloyPrinterStage implements ITacoStage {

	private List<JDynAlloyModule> modules;

	/**
	 * Creates the stage.
	 *
	 * @param modules the modules to write
	 */
	public JDynAlloyPrinterStage(List<JDynAlloyModule> modules) {
		this.modules = modules;
	}

	/**
	 * Writes each module, printed by {@code JavaToJDynAlloyManager.getModuleOutput}, to its
	 * file under the configured {@code output_dir}. An {@link java.io.IOException} is printed
	 * and that module skipped.
	 */
	@Override
	public void execute() {
		for (JDynAlloyModule module : modules) {
			printToFile(module);
		}

	}

	private static void printToFile(JDynAlloyModule module) {
		String output_dir = TacoConfigurator.getInstance().getOutputDir();
		String filename = output_dir + java.io.File.separator
				+ module.getModuleId().replaceAll("_", "/");
		try {
			String moduleOutput = JavaToJDynAlloyManager
					.getModuleOutput(module);
			String completeOutputFileName = filename + SimpleJmlStage.OUTPUT_JDYNALLOY_EXTENSION;
			FileUtils.writeToFile(completeOutputFileName, moduleOutput);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
