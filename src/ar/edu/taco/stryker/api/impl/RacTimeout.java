package ar.edu.taco.stryker.api.impl;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

/**
 * Ends runtime assertion checking (RAC) runs that time out, without {@code Thread.stop()}.
 *
 * <p>RAC runs a mutant on a counterexample in a worker thread and gives up after a few seconds
 * (a timeout counts as a pass; see {@link OpenJMLController} and
 * {@link DarwinistController}). A mutant that loops forever used to be killed with
 * {@code Thread.stop()}, which throws {@code UnsupportedOperationException} on Java 20 and later.
 * Instead, {@link RacTimeoutInstrumenter} inserts a call to {@link #check()} at the start of
 * every method and loop body of the mutant batches compiled with JML4C, and
 * {@link #endTimedOutRun} interrupts the worker: its next {@link #check()} throws
 * {@link Cancelled} and the run unwinds. A worker that does not stop within
 * {@link #GRACE_MILLIS}, because it is looping in code without checks (a helper class of the
 * subject, or the JDK), is stopped with {@code Thread.stop()} where the JVM still supports it,
 * and abandoned otherwise.
 */
public final class RacTimeout {

    private static final Logger log = Logger.getLogger(RacTimeout.class);

    /** How long a timed-out worker may take to stop after being interrupted. */
    static final long GRACE_MILLIS = 2000;

    /**
     * Thrown by {@link #check()} in a RAC run that was cancelled. An {@link Error}, so that mutant
     * code that catches {@link Exception} does not swallow it.
     */
    public static final class Cancelled extends Error {
        private static final long serialVersionUID = 1L;

        Cancelled() {
            super("RAC run cancelled after a timeout");
        }
    }

    private static final ThreadFactory DAEMON_THREADS = new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r, "stryker-rac");
            t.setDaemon(true);
            return t;
        }
    };

    private RacTimeout() {
    }

    /**
     * Called by instrumented mutant code: throws {@link Cancelled} if the current thread has been
     * interrupted, which only happens to a RAC worker whose run timed out. Does not clear the
     * interrupt, so every later check of that run throws too.
     */
    public static void check() {
        if (Thread.currentThread().isInterrupted()) {
            throw new Cancelled();
        }
    }

    /**
     * A single-thread executor for RAC runs, with a daemon thread so that an abandoned worker
     * never keeps the JVM alive.
     */
    public static ExecutorService newExecutor() {
        return Executors.newSingleThreadExecutor(DAEMON_THREADS);
    }

    /**
     * Ends a RAC run that timed out and returns the executor to use from now on. Interrupts the
     * executor's worker and waits up to {@link #GRACE_MILLIS} for it to finish. If it does not,
     * the worker is stopped with {@code Thread.stop()} (Java 19 and earlier) or abandoned (Java 20
     * and later, where it keeps running until the JVM exits).
     *
     * @param executor the executor running the timed-out run; it is shut down
     * @param worker the thread running it, or {@code null} if unknown
     * @param what the file or method being checked, for the log
     * @return a fresh executor
     */
    @SuppressWarnings("deprecation")
    public static ExecutorService endTimedOutRun(ExecutorService executor, Thread worker, String what) {
        executor.shutdownNow();
        boolean ended = false;
        try {
            ended = executor.awaitTermination(GRACE_MILLIS, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        if (!ended && worker != null) {
            try {
                worker.stop();
                log.warn("RAC run of " + what
                        + " did not stop when interrupted; stopped it with Thread.stop()");
            } catch (UnsupportedOperationException e) {
                log.warn("RAC run of " + what + " did not stop when interrupted, and this JVM has no "
                        + "Thread.stop(): abandoning its thread, which keeps running until the JVM exits");
            }
        }
        return newExecutor();
    }
}
