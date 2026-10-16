package ar.edu.taco.stryker.api.impl;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import ar.edu.taco.stryker.api.Controller;

/**
 * Common skeleton of Stryker's four controllers ({@link UnskippableMuJavaController},
 * {@link MuJavaController}, {@link OpenJMLController} and {@link DarwinistController}): a
 * fixed thread pool that runs {@link #getRunnable()} on {@link #getQtyOfThreads()} threads,
 * a local task queue and a shutdown flag.
 *
 * <p>Only {@link UnskippableMuJavaController} reads the local {@link #queue}, which
 * {@link #enqueueTask(Object)} feeds. The other three controllers ignore it and consume from
 * {@link ar.edu.taco.engine.StrykerStage#weedQueue} instead.
 *
 * <p>Lifecycle: the subclasses are singletons. {@link #start()} creates the queue, the flag
 * and the pool, so they are {@code null} until it is called, and calling it again replaces
 * them without touching any other state. Worker loops check {@link #willShutdown} between
 * items. All current subclasses use one worker thread.
 *
 * @param <T> type of the tasks in the local queue
 */
public abstract class AbstractBaseController<T> implements Controller<T> {

	/** Local task queue, created by {@link #start()}. Only read by {@link UnskippableMuJavaController}. */
	protected BlockingQueue<T> queue;
	/**
	 * Set by {@link #shutdown()} and {@link #shutdownNow()}; worker loops exit when they see it.
	 * {@link SimpleStrykerAPI#fixBug} polls the flag of {@link DarwinistController} to detect
	 * the end of the run.
	 */
	protected AtomicBoolean willShutdown;
	/** Worker pool, created by {@link #start()}. */
	private ExecutorService service;
	
	
	/**
	 * Sets {@link #willShutdown} and shuts the pool down without interrupting the workers. A
	 * worker blocked on an empty queue stays blocked until it receives an element. Queued
	 * tasks are not drained: workers stop after the item they are processing.
	 */
	@Override
	public void shutdown() {
		willShutdown.set(true);
		service.shutdown();
	}
	
	/**
	 * Sets {@link #willShutdown}, clears the local {@link #queue} (not the weed queue) and
	 * interrupts the workers. Workers blocked on a take get an {@link InterruptedException},
	 * which their loops treat as a signal to exit.
	 */
	@Override
	public void shutdownNow() {
		willShutdown.set(true);
		queue.clear();
		service.shutdownNow();
	}

	/**
	 * Creates a new local queue, a cleared {@link #willShutdown} flag and a fixed pool of
	 * {@link #getQtyOfThreads()} threads, each running a new {@link #getRunnable()}.
	 */
	@Override
	public void start() {
		queue = new LinkedBlockingQueue<T>();
		willShutdown = new AtomicBoolean(false);
		service = Executors.newFixedThreadPool(getQtyOfThreads());
		for (int i = 0 ; i < getQtyOfThreads() ; i++) {
			service.execute(getRunnable());
		}
	}

	/**
	 * Adds a task to the local {@link #queue}; tasks are silently dropped once
	 * {@link #willShutdown} is set.
	 */
	@Override
	public void enqueueTask(T task) {
		if(!willShutdown.get()) {
			queue.add(task);
		}
	}
	
	/**
	 * @return the worker loop; called once per worker thread by {@link #start()}. It should
	 *         exit when {@link #willShutdown} is set or the thread is interrupted.
	 */
	protected abstract Runnable getRunnable();

	/** @return the number of worker threads started by {@link #start()} */
	protected abstract int getQtyOfThreads();
}
