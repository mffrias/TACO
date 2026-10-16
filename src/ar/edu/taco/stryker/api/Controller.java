package ar.edu.taco.stryker.api;

/**
 * A Stryker pipeline stage that processes tasks on its own worker threads. The only
 * implementation is {@link ar.edu.taco.stryker.api.impl.AbstractBaseController}, whose four
 * subclasses are the controllers wired together by
 * {@link ar.edu.taco.stryker.api.impl.SimpleStrykerAPI}.
 *
 * @param <T> type of the tasks accepted by {@link #enqueueTask(Object)}
 */
public interface Controller<T> {

	/**
	 * Shutdowns the controller. 
	 * It may take a while to shutdown completely because there may be tasks to process.
	 * After calling shutdown, the controller will no longer accept tasks.
	 * Note: in {@link ar.edu.taco.stryker.api.impl.AbstractBaseController} the previously
	 * enqueued tasks are <em>not</em> processed: the workers stop after the item they are
	 * handling, and a worker blocked waiting for input is not interrupted.
	 */
	void shutdown();
	
	/**
	 * Starts the controller.
	 * This method will set all the basic configuration.
	 * It must be called before {@link #enqueueTask(Object)}, {@link #shutdown()} or
	 * {@link #shutdownNow()}.
	 */
	void start();
	
	/**
	 * Enqueue tasks to the controller.
	 * Only {@link ar.edu.taco.stryker.api.impl.UnskippableMuJavaController} reads the tasks
	 * enqueued this way; the other controllers are fed through
	 * {@link ar.edu.taco.engine.StrykerStage#weedQueue}.
	 * @param task The task to enqueue.
	 */
	void enqueueTask(T task);
	
	/**
	 * Stops the controller immediately: no new tasks are accepted, the tasks enqueued through
	 * {@link #enqueueTask(Object)} are discarded (the shared weed queue is not touched) and the
	 * worker threads are interrupted.
	 */
	public void shutdownNow();
	
}
