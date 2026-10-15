package ar.edu.taco.stryker.api.impl;

import java.io.File;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import ar.edu.taco.stryker.api.impl.input.DarwinistInput;
import ar.edu.taco.stryker.api.impl.input.MuJavaInput;
import ar.edu.taco.stryker.api.impl.input.OpenJMLInput;

public final class WeedQueue<A, B, C> {

    private final QueueContainer<A> queue1 = new QueueContainer<A>();
    private final QueueContainer<B> queue2 = new QueueContainer<B>();
    private final QueueContainer<C> queue3 = new QueueContainer<C>();

    final static class QueueContainer<T> {

        private final BlockingQueue<T> queue = new LinkedBlockingQueue<T>();
        private T current;

        public synchronized T take() throws InterruptedException {
            if (current != null) {
                return current;
            }

            current = queue.take();
            return current;
        }

        public void enqueue(T elem) {
            queue.add(elem);
        }

        public synchronized int ack() {
            //            System.out.println("INSIDE WQ QC ACK");
            if (current == null) {
                throw new IllegalStateException("Cannot acknoledge null current value");
            }
            current = null;
            return queue.size();
        }

        public int size() {
            return queue.size() + ((current == null) ? 0 : 1);
        }

        public void clear() {
            queue.clear();
        }

    }

    public A takeFrom1() throws InterruptedException {
        return queue1.take();
    }

    public B takeFrom2() throws InterruptedException {
        return queue2.take();
    }

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
