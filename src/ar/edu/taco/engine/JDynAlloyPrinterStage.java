package ar.edu.taco.engine;

import java.io.IOException;
import java.util.List;

import ar.edu.jdynalloy.ast.JDynAlloyModule;
import ar.edu.taco.TacoConfigurator;
import ar.edu.taco.simplejml.JavaToJDynAlloyManager;
import ar.edu.taco.utils.FileUtils;

public class JDynAlloyPrinterStage implements ITacoStage {

	private List<JDynAlloyModule> modules;

	public JDynAlloyPrinterStage(List<JDynAlloyModule> modules) {
		this.modules = modules;
	}

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
			FileUtils.writeToFile(filename
					+ SimpleJmlStage.OUTPUT_JDYNALLOY_EXTENSION, moduleOutput);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
