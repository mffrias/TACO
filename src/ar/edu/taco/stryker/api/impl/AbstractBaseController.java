package ar.edu.taco.stryker.api.impl;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import ar.edu.taco.stryker.api.Controller;

public abstract class AbstractBaseController<T> implements Controller<T> {

	protected BlockingQueue<T> queue;
	protected AtomicBoolean willShutdown;
	private ExecutorService service;
	
	
	@Override
	public void shutdown() {
		willShutdown.set(true);
		service.shutdown();
	}
	
	@Override
	public void shutdownNow() {
		willShutdown.set(true);
		queue.clear();
		service.shutdownNow();
	}

	@Override
	public void start() {
		queue = new LinkedBlockingQueue<T>();
		willShutdown = new AtomicBoolean(false);
		service = Executors.newFixedThreadPool(getQtyOfThreads());
		for (int i = 0 ; i < getQtyOfThreads() ; i++) {
			service.execute(getRunnable());
		}
	}

	@Override
	public void enqueueTask(T task) {
		if(!willShutdown.get()) {
			queue.add(task);
		}
	}
	
	protected abstract Runnable getRunnable();

	protected abstract int getQtyOfThreads();
}
