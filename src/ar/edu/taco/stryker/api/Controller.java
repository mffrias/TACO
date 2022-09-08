package ar.edu.taco.stryker.api;

public interface Controller<T> {

	/**
	 * Shutdowns the controller. 
	 * It may take a while to shutdown completely because there may be tasks to process.
	 * After calling shutdown, the controller will no longer accept tasks, 
	 * and will process all the previously enqueue tasks.
	 */
	void shutdown();
	
	/**
	 * Starts the controller.
	 * This method will set all the basic configuration.
	 */
	void start();
	
	/**
	 * Enqueue tasks to the controller.
	 * @param task The task to enqueue.
	 */
	void enqueueTask(T task);
	
	public void shutdownNow();
	
}
