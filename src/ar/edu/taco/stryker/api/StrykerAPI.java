package ar.edu.taco.stryker.api;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

import mujava.api.MutationOperator;

/**
 * Entry point of the Stryker repair engine. The only implementation is
 * {@link ar.edu.taco.stryker.api.impl.SimpleStrykerAPI}, used by
 * {@link ar.edu.taco.stryker.StrykerInitialStage}. Call {@link #start()} once, then
 * {@link #fixBug}.
 */
public interface StrykerAPI {

	/**
	 * Configures all the initial configuration and prepares the environment to run upon a new bug. 
	 */
	void start();
	
	/**
	 * Attempts to fix a bug. Blocks until the search ends, either because a fix was found
	 * or because there is nothing left to try.
	 *  
	 * The counterexamples used to filter mutants are not a parameter: they are read from
	 * {@link ar.edu.taco.engine.StrykerStage#junitInputs}, which must be filled beforehand.
	 *
	 * @param classToMutate The class to mutate (the {@code .java} file of the subject)
	 * @param classNameToMutate the given classNameToMutate should correspond to the class name
	 *     only; without package information (e.g: BinTree). Ignored by
	 *     {@link ar.edu.taco.stryker.api.impl.SimpleStrykerAPI}.
	 * @param methodToMutate The method to mutate (plain name, without parameter types)
	 * @param mutOps should correspond to MuJava's mutant operators (e.g: "AOIS", "ROR")
	 * @param generationsWanted the amount of generations of mutants to generate. Passed along
	 *     with the inputs but, in the current implementation, never read.
	 * @param configFile the TACO configuration file, used to verify fix candidates
	 * @param overridingProperties The overriding properties. Expected to contain
	 *     {@code classToCheck} and {@code methodToCheck}, which the implementation reads;
	 *     they are the base of the properties of every TACO call made during the search.
	 * @param maxMethodsInFile maximum number of mutant methods per batch file; forwarded to
	 *     {@link ar.edu.taco.stryker.api.impl.MuJavaController#setMaxMethodsInFile(int)}, which
	 *     currently has no effect (batch sizes are fixed in the controllers)
	 * @return The filenames with the bug fixed or empty if a fix could not be found.
	 */
	List<String> fixBug(File classToMutate, String classNameToMutate, String methodToMutate, 
			HashSet<MutationOperator> mutOps, AtomicInteger generationsWanted, String configFile, 
			Properties overridingProperties, int maxMethodsInFile);
	
}
