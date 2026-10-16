package ar.edu.taco.stryker.api.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import ar.edu.taco.stryker.api.impl.input.DarwinistInput;
import ar.edu.taco.stryker.api.impl.input.MuJavaInput;
import ar.edu.taco.stryker.api.impl.input.OpenJMLInput;

/**
 * The three work queues that connect Stryker's controllers, plus the bookkeeping that tells
 * when the search is over. The run-wide instance is
 * {@link ar.edu.taco.engine.StrykerStage#weedQueue}, a
 * {@code WeedQueue<MuJavaInput, OpenJMLInput, DarwinistInput>}:
 * <ul>
 * <li>queue 1 ({@code A}, {@link MuJavaInput}): consumed by {@link MuJavaController}
 *     (fatherize mutants, feedback and next-sibling requests). Fed by
 *     {@link UnskippableMuJavaController}, {@link OpenJMLController},
 *     {@link DarwinistController} and MuJavaController itself.</li>
 * <li>queue 2 ({@code B}, {@link OpenJMLInput}): consumed by {@link OpenJMLController}
 *     (RAC filtering). Fed by MuJavaController and UnskippableMuJavaController.</li>
 * <li>queue 3 ({@code C}, {@link DarwinistInput}): consumed by {@link DarwinistController}
 *     (TACO validation of fix candidates). Fed by OpenJMLController.</li>
 * </ul>
 *
 * <p><b>Take/ack protocol.</b> {@code takeFromN()} blocks until an element is available and
 * makes it the queue's in-flight element; until it is acknowledged, further
 * {@code takeFromN()} calls return that same element. When the consumer has finished an
 * element, and has already enqueued any follow-up work, it calls {@link #ack(int)} with the
 * 0-based queue index (0, 1, 2 for queues 1, 2, 3). {@code ack} returns the number of
 * elements enqueued and not yet acknowledged over the three queues (queued, being taken or
 * in flight), and every controller treats 0 as "search space exhausted" and shuts all
 * controllers down. The local queue of
 * {@link UnskippableMuJavaController} (see {@link AbstractBaseController}) is not counted.
 *
 * <p><b>Threading.</b> The in-flight slot is per queue, not per thread, so each queue must
 * have a single consumer thread (every controller runs one worker). Enqueues, acks and
 * {@link #clearQueues()} are serialized on this object; takes are serialized per queue.
 * Each queue counts an element from its enqueue to its ack, so the count never misses an
 * element that another thread is in the middle of taking. (Until 2026 the count was computed
 * from the queue and the in-flight slot, and an element being taken was invisible for a
 * moment: a run could end early and report "No fix was found".)
 *
 * @param <A> element type of queue 1
 * @param <B> element type of queue 2
 * @param <C> element type of queue 3
 */
public final class WeedQueue<A, B, C> {

    private final QueueContainer<A> queue1 = new QueueContainer<A>();
    private final QueueContainer<B> queue2 = new QueueContainer<B>();
    private final QueueContainer<C> queue3 = new QueueContainer<C>();

    /**
     * A blocking queue plus a one-element "in flight" slot holding the element taken but not
     * yet acknowledged.
     *
     * @param <T> element type
     */
    final static class QueueContainer<T> {

        private final BlockingQueue<T> queue = new LinkedBlockingQueue<T>();
        /** Element taken and not yet acknowledged, or {@code null}. */
        private T current;

        /** Work items enqueued and not yet acked: queued, being taken, or in flight. */
        private final AtomicInteger outstanding = new AtomicInteger();

        /**
         * Returns the in-flight element if there is one; otherwise blocks until an element
         * is available, removes it and makes it the in-flight element. The container's
         * monitor is held while blocking.
         *
         * @return the in-flight element
         * @throws InterruptedException if interrupted while waiting
         */
        public synchronized T take() throws InterruptedException {
            if (current != null) {
                return current;
            }

            current = queue.take();
            return current;
        }

        public void enqueue(T elem) {
            outstanding.incrementAndGet();
            queue.add(elem);
        }

        /**
         * Clears the in-flight slot.
         *
         * @return the number of elements still queued, excluding the in-flight one (ignored
         *         by {@link WeedQueue#ack(int)})
         * @throws IllegalStateException if there is no in-flight element
         */
        public synchronized int ack() {
            //            System.out.println("INSIDE WQ QC ACK");
            if (current == null) {
                throw new IllegalStateException("Cannot acknoledge null current value");
            }
            current = null;
            outstanding.decrementAndGet();
            return queue.size();
        }

        /** @return elements enqueued and not yet acknowledged (queued, being taken or in flight) */
        public int size() {
            return outstanding.get();
        }

        /** Drops the queued elements, but not the in-flight one. Unused. */
        public void clear() {
            outstanding.addAndGet(-queue.drainTo(new ArrayList<T>()));
        }

    }

    /**
     * Takes from queue 1 ({@link MuJavaController}); see the class comment for the in-flight
     * semantics.
     *
     * @return the in-flight element of queue 1
     * @throws InterruptedException if interrupted while waiting
     */
    public A takeFrom1() throws InterruptedException {
        return queue1.take();
    }

    /**
     * Takes from queue 2 ({@link OpenJMLController}); see the class comment for the in-flight
     * semantics.
     *
     * @return the in-flight element of queue 2
     * @throws InterruptedException if interrupted while waiting
     */
    public B takeFrom2() throws InterruptedException {
        return queue2.take();
    }

    /**
     * Takes from queue 3 ({@link DarwinistController}); see the class comment for the
     * in-flight semantics.
     *
     * @return the in-flight element of queue 3
     * @throws InterruptedException if interrupted while waiting
     */
    public C takeFrom3() throws InterruptedException {
        return queue3.take();
    }

    public synchronized void enqueue1(A elem) {
        queue1.enqueue(elem);
    }

    public synchronized void enqueue2(B elem) {
        queue2.enqueue(elem);
    }

    public synchronized void enqueue3(C elem) {
        queue3.enqueue(elem);
    }

    /**
     * Acknowledges the in-flight element of a queue, which must have been taken by the caller
     * and fully processed, including enqueueing its follow-up work.
     *
     * @param i 0-based queue index: 0 for queue 1, 1 for queue 2, 2 for queue 3
     * @return the number of queued plus in-flight elements over the three queues after the
     *         acknowledgement; the controllers shut everything down when it is 0
     * @throws IllegalArgumentException if {@code i} is not 0, 1 or 2
     * @throws IllegalStateException if that queue has no in-flight element
     */
    public synchronized int ack(int i) {
        //        System.out.println("INSIDE WQ ACK FOR " + i);
        switch (i) {
            case 0:
                queue1.ack();
                break;
            case 1:
                queue2.ack();
                break;
            case 2:
                queue3.ack();
                break;
            default:
                throw new IllegalArgumentException("Index is not valid: " + i);
        }
        //        System.out.println("ABOUT TO EXIT INSIDE WQ ACK FOR " + i);
        return size();
    }

    /**
     * Intended to drain the three queues at the end of a run and delete the temporary files
     * of the drained elements: for each element, its own file and, when it has feedback, the
     * files of its father in {@link MuJavaController#getFathers()} (the father's source,
     * its children batch file, and the files in its JML4C batch directory together with that
     * directory). Elements of queue 1 also get their own children and JML4C files deleted.
     * All exceptions are swallowed. Called by {@link DarwinistController} after an
     * acknowledgement returns 0 and when a fix is found with
     * {@link MuJavaController#finishOnFirstFix}.
     *
     * <p><b>Caveat:</b> elements are taken but never acknowledged, and
     * {@link QueueContainer#take()} keeps returning the in-flight element. So if any queue is
     * non-empty or has an in-flight element when this method is called, the corresponding
     * loop never ends (an acknowledgement from another thread would need this object's
     * monitor, which is held). That is the case when it is called from
     * {@link DarwinistController} on a fix: the candidate being validated is still in flight
     * in queue 3. After an acknowledgement returned 0 the queues are normally empty and the
     * method returns at once, unless the sentinel that {@link OpenJMLController} enqueues to
     * queue 3 when it is interrupted gets there first. Since the caller sets its shutdown flag
     * beforehand, {@link SimpleStrykerAPI#fixBug} still returns; only the
     * {@link DarwinistController} worker thread is left looping. Fathers are always looked up
     * in MuJavaController's list, even
     * for validate-only elements whose father index refers to
     * {@link UnskippableMuJavaController#getFathers()}.
     */
    public synchronized void clearQueues() {
        while (queue1.size() != 0) {
            try {
                MuJavaInput input = (MuJavaInput)queue1.take();
                new File(input.getFilename()).delete();
                if (input.getChildrenFilename() != null) {
                    new File(input.getChildrenFilename()).delete();
                }
                if (input.getJml4cFilename() != null) {
                    File wrapperFile = new File(input.getJml4cFilename().substring(0, input.getJml4cFilename().lastIndexOf(OpenJMLController.FILE_SEP) + 1)); //Limpio el wrapper
                    for(File file: wrapperFile.listFiles()) {
                        file.delete();
                    }
                    wrapperFile.delete();
                }
                
                if (input.getMuJavaFeedback() != null) {
                    MuJavaInput father = MuJavaController.getInstance().getFathers().get(input.getMuJavaFeedback().getFatherIndex());
                    new File(father.getFilename()).delete();
                    if (father.getChildrenFilename() != null) {
                        new File(father.getChildrenFilename()).delete();
                    }
                    if (father.getJml4cFilename() != null) {
                        File wrapperFile = new File(father.getJml4cFilename().substring(0, father.getJml4cFilename().lastIndexOf(OpenJMLController.FILE_SEP) + 1)); //Limpio el wrapper
                        for(File file: wrapperFile.listFiles()) {
                            file.delete();
                        }
                        wrapperFile.delete();
                    }
                }
            } catch (Exception e) {
                // TODO: Define what to do!
            }
        }
        while (queue2.size() != 0) {
            try {
                OpenJMLInput input = (OpenJMLInput)queue2.take();
                new File(input.getFilename()).delete();
                if (input.getFeedback() != null) {
                    MuJavaInput father = MuJavaController.getInstance().getFathers().get(input.getFeedback().getFatherIndex());
                    new File(father.getFilename()).delete();
                    if (father.getChildrenFilename() != null) {
                        new File(father.getChildrenFilename()).delete();
                    }
                    if (father.getJml4cFilename() != null) {
                        File wrapperFile = new File(father.getJml4cFilename().substring(0, father.getJml4cFilename().lastIndexOf(OpenJMLController.FILE_SEP) + 1)); //Limpio el wrapper
                        for(File file: wrapperFile.listFiles()) {
                            file.delete();
                        }
                        wrapperFile.delete();
                    }
                }
            } catch (Exception e) {
                // TODO: Define what to do!
            }
        }
        while (queue3.size() != 0) {
            try {
                DarwinistInput input = (DarwinistInput)queue3.take();
                new File(input.getFilename()).delete();
                if (input.getFeedback() != null) {
                    MuJavaInput father = MuJavaController.getInstance().getFathers().get(input.getFeedback().getFatherIndex());
                    new File(father.getFilename()).delete();
                    if (father.getChildrenFilename() != null) {
                        new File(father.getChildrenFilename()).delete();
                    }
                    if (father.getJml4cFilename() != null) {
                        File wrapperFile = new File(father.getJml4cFilename().substring(0, father.getJml4cFilename().lastIndexOf(OpenJMLController.FILE_SEP) + 1)); //Limpio el wrapper
                        for(File file: wrapperFile.listFiles()) {
                            file.delete();
                        }
                        wrapperFile.delete();
                    }
                }
            } catch (Exception e) {
                // TODO: Define what to do!
            }
        }

    }

    /** @return queued plus in-flight elements over the three queues */
    private int size() {
        //        System.out.println("Calculo size1");
        int size1 = queue1.size();
        //        System.out.println("Calculo size2");
        int size2 = queue2.size();
        //        System.out.println("Calculo size3");
        int size3 = queue3.size();
        //        System.out.println("Calcule los 3 size");
        return size1 + size2 + size3;
    }

}
