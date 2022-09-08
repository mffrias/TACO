package ar.edu.taco.stryker.api;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

import mujava.api.MutationOperator;

public interface StrykerAPI {

	/**
	 * Configures all the initial configuration and prepares the environment to run upon a new bug. 
	 */
	void start();
	
	/**
	 * Attempts to fix a bug
	 *  
	 * @param junitFile The file that contains all the statements that will make the class fail.
	 * @param classToMutate The class to mutate
	 * @param classNameToMutate the given classNameToMutate should correspond to the class name
	 *     only; without package information (e.g: BinTree)
	 * @param methodToMutate The method to mutate
	 * @param mutOps should correspond to MuJava's mutant operators (e.g: "AOIS", "ROR")
	 * @param generationsWanted the amount of generations of mutants to generate
	 * @param overridingProperties The overriding properties
	 * @return The filenames with the bug fixed or empty if a fix could not be found.
	 */
	List<String> fixBug(File classToMutate, String classNameToMutate, String methodToMutate, 
			HashSet<MutationOperator> mutOps, AtomicInteger generationsWanted, String configFile, 
			Properties overridingProperties, int maxMethodsInFile);
	
}
