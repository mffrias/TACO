package ar.edu.taco.stryker.exceptions;

/**
 * This exception is thrown if a fatal error has occurred while executing a
 * stryker stage. This exception or any of its subclasses indicate that
 * the execution of the stryker stage as well as the rest of the stages
 * should be aborted.
 * <p>
 * It is declared by {@link ar.edu.taco.stryker.IStrykerStage#execute()} and
 * {@link ar.edu.taco.stryker.StrykerInitialStage#execute()}, and
 * {@link ar.edu.taco.engine.StrykerStage} catches it and prints the stack trace. Nothing in this
 * repository currently throws it.
 */
@SuppressWarnings("serial")
public class FatalStrykerStageException extends Exception {

	/**
	 * @param msg the detail message
	 */
	public FatalStrykerStageException(String msg) {
		super(msg);
	}

}
