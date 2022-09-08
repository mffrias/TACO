package ar.edu.taco.stryker.exceptions;

/**
 * This exception is thrown if a fatal error has occurred while executing a
 * stryker stage. This exception or any of its subclasses indicate that
 * the execution of the stryker stage as well as the rest of the stages
 * should be aborted.
 */
@SuppressWarnings("serial")
public class FatalStrykerStageException extends Exception {

	public FatalStrykerStageException(String msg) {
		super(msg);
	}

}
