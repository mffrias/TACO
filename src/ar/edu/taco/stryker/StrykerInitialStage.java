package ar.edu.taco.stryker;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

import mujava.api.MutationOperator;

import org.apache.log4j.Logger;

import ar.edu.taco.stryker.api.StrykerAPI;
import ar.edu.taco.stryker.api.impl.SimpleStrykerAPI;
import ar.edu.taco.stryker.exceptions.FatalStrykerStageException;

/**
 * Stage to launch the stryker process that will attemp to fix the discovered bug.
 *
 * <p>Created and executed by {@link ar.edu.taco.engine.StrykerStage#execute()}. It only
 * bridges to the API: it creates a {@link SimpleStrykerAPI}, starts its controllers and
 * calls {@link StrykerAPI#fixBug}, which blocks until the search ends. The counterexamples
 * that drive the search must already be in
 * {@link ar.edu.taco.engine.StrykerStage#junitInputs}.
 *
 * <p>The outcome is only logged, at WARN level: {@code "No fix was found: result = null"},
 * {@code "No fix was found: result = empty"} or {@code "FIX FOUND!!! Look for file [...]"}
 * with the list of fix files. The detailed report of each fix ({@code StrykerLastReports.txt})
 * and the last fix found ({@code StrykerLastFixFound.java}) are written to {@code user.dir} by
 * {@link ar.edu.taco.stryker.api.impl.DarwinistController}.
 */

public class StrykerInitialStage implements IStrykerStage {


	private static Logger log = Logger.getLogger(StrykerInitialStage.class);

	// The class to be mutated
	private File classToMutate;

	// The class name to be mutated
	private String classNameToMutate;

	// The method to be mutated
	private String methodToMutate;

	// The mutant operators to use
	private HashSet<MutationOperator> mutOps;

	// The amount of generations wanted
	private AtomicInteger generationsWanted;
	
	private String configFile;
	
	private Properties overridingProperties;
	
	private int maxMethodsInFile;
	
	/**
	 * Stores the arguments for {@link StrykerAPI#fixBug}; see that method for their meaning.
	 */
	public StrykerInitialStage(File classToMutate, String classNameToMutate,
			String methodToMutate, HashSet<MutationOperator> mutOps, AtomicInteger generationsWanted, String configFile, Properties overridingProperties, int maxMethodsInFile) {
		this.classToMutate = classToMutate;
		this.classNameToMutate = classNameToMutate;
		this.methodToMutate = methodToMutate;
		this.mutOps = mutOps;
		this.generationsWanted = generationsWanted;
		this.configFile = configFile;
		this.overridingProperties = overridingProperties;
		this.maxMethodsInFile = maxMethodsInFile;
	}

	/**
	 * Starts a new {@link SimpleStrykerAPI}, runs {@link StrykerAPI#fixBug} (blocking) and
	 * logs the result. Never actually throws {@link FatalStrykerStageException}.
	 */
	@Override
	public void execute() throws FatalStrykerStageException {
		StrykerAPI api = new SimpleStrykerAPI();
		log.info("***** Starting Stryker Controllers *****");
		api.start();
		List<String> result = api.fixBug(classToMutate, classNameToMutate, methodToMutate, mutOps, generationsWanted, configFile, overridingProperties, maxMethodsInFile);
		if(result == null) {
			log.warn("****** No fix was found: result = null ******");
		} else if (result.isEmpty()) {
			log.warn("****** No fix was found: result = empty ******");
		} else {
			log.warn(String.format("****** FIX FOUND!!! Look for file %s ******", result));
		}
	}

}
