package ar.edu.taco.stryker;

import ar.edu.taco.stryker.exceptions.FatalStrykerStageException;

/**
 * A Stryker stage.
 */
public interface IStrykerStage {

	/**
	 * Executes the stage.
	 * @throws FatalStrykerStageException If a fatal exception occurred. If
	 *     this exception is thrown the execution of this stage should be
	 *     aborted as well as the execution of any following Stryker stage.
	 */
	public void execute() throws FatalStrykerStageException;
}
