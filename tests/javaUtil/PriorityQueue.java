/*
 * Copyright (c) 2003, 2010, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */
package javaUtil;

public class PriorityQueue {

    //@ invariant size <= queue.length;
    // invariant size >= 1 ==> queue[0] >= queue[0];
    // invariant size >= 2 ==> queue[1] >= queue[0];
    // invariant size >= 3 ==> queue[2] >= queue[0];
    // invariant size >= 4 ==> queue[3] >= queue[1];
    // invariant !(\exists int i; 0<i && i<size; queue[i/2] < queue[i]);
    //@ invariant size > 0 ==> (\forall int i; 0<=i && i<size; queue[0] >= queue[i]);

//    private static final long serialVersionUID = -7720805057305804111L;

    private static final int DEFAULT_INITIAL_CAPACITY = 11;


    public transient int[] queue;


    public int size = 0;


//    private final Comparator comparator;


    public transient int modCount = 0;


    //@ requires true;
    //@ ensures this.queue.length == DEFAULT_INITIAL_CAPACITY;
    //@ ensures this.size == 0;
    public PriorityQueue() {
        this(DEFAULT_INITIAL_CAPACITY);
    }


    //@ requires initialCapacity >= 1;
    //@ ensures this.queue.length == initialCapacity;
    //@ ensures this.size == 0;
    public PriorityQueue(int initialCapacity) {
        if (initialCapacity < 1)
            throw new IllegalArgumentException();
        this.queue = new int[initialCapacity];
    }


//    public PriorityQueue(int initialCapacity) {
//        // Note: This restriction of at least one is not actually needed,
//        // but continues for 1.5 compatibility
//        if (initialCapacity < 1)
//            throw new IllegalArgumentException();
//        this.queue = new int[initialCapacity];
////        this.comparator = comparator;
//    }


//    @SuppressWarnings("unchecked")
//    public PriorityQueue(Collection c) {
//        if (c instanceof SortedSet) {
//            SortedSet ss = (SortedSet) c;
//            this.comparator = (Comparator) ss.comparator();
//            initElementsFromCollection(ss);
//        }
//        else if (c instanceof PriorityQueue) {
//            PriorityQueue pq = (PriorityQueue) c;
//            this.comparator = (Comparator) pq.comparator();
//            initFromPriorityQueue(pq);
//        }
//        else {
//            this.comparator = null;
//            initFromCollection(c);
//        }
//    }


//    @SuppressWarnings("unchecked")
//    public PriorityQueue(PriorityQueue c) {
//        this.comparator = (Comparator) c.comparator();
//        initFromPriorityQueue(c);
//    }


//    @SuppressWarnings("unchecked")
//    public PriorityQueue(SortedSet c) {
//        this.comparator = (Comparator) c.comparator();
//        initElementsFromCollection(c);
//    }

//    private void initFromPriorityQueue(PriorityQueue c) {
//        if (c.getClass() == PriorityQueue.class) {
//            this.queue = c.toArray();
//            this.size = c.size();
//        } else {
//            initFromCollection(c);
//        }
//    }

//    private void initElementsFromCollection(Collection c) {
//        Object[] a = c.toArray();
//        // If c.toArray incorrectly doesn't return Object[], copy it.
//        if (a.getClass() != Object[].class)
//            a = Arrays.copyOf(a, a.length, Object[].class);
//        int len = a.length;
//        if (len == 1 || this.comparator != null)
//            for (int i = 0; i < len; i++)
//                if (a[i] == null)
//                    throw new NullPointerException();
//        this.queue = a;
//        this.size = a.length;
//    }


//    private void initFromCollection(Collection c) {
//        initElementsFromCollection(c);
//        heapify();
//    }


    private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;


    private void grow(int minCapacity) {
        int oldCapacity = queue.length;
        // Double size if small; else grow by 50%
        int newCapacity = oldCapacity + ((oldCapacity < 64) ?
                (oldCapacity + 2) :
                (oldCapacity >> 1));
        // overflow-conscious code
        if (newCapacity - MAX_ARRAY_SIZE > 0)
            newCapacity = hugeCapacity(minCapacity);

        int[] newArray = new int[newCapacity];
        for (int i = 0; i < size; i++) {
            newArray[i] = queue[i];
        }
        queue = newArray;
    }

    private static int hugeCapacity(int minCapacity) {
        if (minCapacity < 0) // overflow
            throw new RuntimeException();
        return (minCapacity > MAX_ARRAY_SIZE) ?
                Integer.MAX_VALUE :
                MAX_ARRAY_SIZE;
    }


    public boolean add(int e) {
        return offer(e);
    }

//       ensures (\exists int i; 0 <= i && i < size; this.queue[i] == e);
//       ensures (\forall int i; 0 <= i && i < \old(size);
//        (\exists int j; 0 <= j && j < size; queue[j] == \old(queue[i]) ) );
//       ensures size == \old(size) + 1;


    /*@ requires true;
      @ ensures (\exists int i; 0<=i && i<size; queue[i] == e);
      @ ensures (\forall int i; 0<=i && i<\old(size); (\exists int j; 0<=j && j<size; queue[j] == queue[i]));
      @ ensures modCount == \old(modCount) + 1;
      @ ensures size == \old(size) + 1;
      @ signals (Exception) false;
      @ signals (AssertionError) true;
      @*/
    public boolean offer(int e) {
//        if (e == null)
//            throw new NullPointerException();
//        modCount++;    // added bug
        int i = size;
        if (i >= queue.length) {

            int minCapacity = i + 1;
            int oldCapacity = queue.length;
            // Double size if small; else grow by 50%
            int newCapacity = oldCapacity + ((oldCapacity < 64) ?
                    (oldCapacity + 2) :
                    (oldCapacity / 2));
            // overflow-conscious code
            if (newCapacity - MAX_ARRAY_SIZE > 0)
                newCapacity = hugeCapacity(minCapacity);

            int[] newArray = new int[newCapacity];
            int j = 0;
            while (j < size) {
                newArray[j] = queue[j];
                j++;
            }

            queue = newArray;
        }
        if (i == 0)
            queue[0] = e;
        else {
//            siftUp(i, e);

            int k = i;
            int x = e;

            int key = x;

            while (k > 0) {
                int parent = (k - 1) / 2;
                int ee = queue[parent];
                if (key >= ee) {
                    break;
                }
                queue[k] = ee;
                k = parent;
            }
            queue[k] = key;
        }


        size = i + 1;
        return true;
    }

//    public Object peek() {
//        if (size == 0)
//            return null;
//        return (Object) queue[0];
//    }


    /*@ requires true;
      @ ensures \result == -1 <==> (\forall int i; 0<=i && i<size; queue[i] != o);
      @ ensures (0<= \result && \result < size) ==> queue[\result] == o;
      @*/
    public int indexOf(int o) {

//        if (o != null) {
        for (int i = 0; i < size; i++)
            if (o == queue[i])
                return i;
//        }
        return -1;
    }


//    public boolean remove(Object o) {
//        int i = indexOf(o);
//        if (i == -1)
//            return false;
//        else {
//            removeAt(i);
//            return true;
//        }
//    }


//    boolean removeEq(Object o) {
//        for (int i = 0; i < size; i++) {
//            if (o == queue[i]) {
//                removeAt(i);
//                return true;
//            }
//        }
//        return false;
//    }


//    public boolean contains(Object o) {
//        return indexOf(o) != -1;
//    }


//    public Object[] toArray() {
//        return Arrays.copyOf(queue, size);
//    }


//    public Object[] toArray(Object[] a) {
//        if (a.length < size)
//            // Make a new array of a's runtime type, but my contents:
//            return (Object[]) Arrays.copyOf(queue, size, a.getClass());
//        System.arraycopy(queue, 0, a, 0, size);
//        if (a.length > size)
//            a[size] = null;
//        return a;
//    }


//    public javaUtil.Iterator iterator() {
//        return new Itr();
//    }

//    private final class Itr implements Iterator {
//        /**
//         * Index (into queue array) of element to be returned by
//         * subsequent call to next.
//         */
//        private int cursor = 0;
//
//        /**
//         * Index of element returned by most recent call to next,
//         * unless that element came from the forgetMeNot list.
//         * Set to -1 if element is deleted by a call to remove.
//         */
//        private int lastRet = -1;
//
//        /**
//         * A queue of elements that were moved from the unvisited portion of
//         * the heap into the visited portion as a result of "unlucky" element
//         * removals during the iteration.  (Unlucky element removals are those
//         * that require a siftup instead of a siftdown.)  We must visit all of
//         * the elements in this list to complete the iteration.  We do this
//         * after we've completed the "normal" iteration.
//         *
//         * We expect that most iterations, even those involving removals,
//         * will not need to store elements in this field.
//         */
////        private ArrayDeque forgetMeNot = null;
//
//        /**
//         * Element returned by the most recent call to next iff that
//         * element was drawn from the forgetMeNot list.
//         */
//        private Object lastRetElt = null;
//
//        /**
//         * The modCount value that the iterator believes that the backing
//         * Queue should have.  If this expectation is violated, the iterator
//         * has detected concurrent modification.
//         */
//        private int expectedModCount = modCount;
//
////        public boolean hasNext() {
////            return cursor < size ||
////                (forgetMeNot != null && !forgetMeNot.isEmpty());
////        }
//
////        public Object next() {
////            if (expectedModCount != modCount)
////                throw new ConcurrentModificationException();
////            if (cursor < size)
////                return (Object) queue[lastRet = cursor++];
////            if (forgetMeNot != null) {
////                lastRet = -1;
////                lastRetElt = forgetMeNot.poll();
////                if (lastRetElt != null)
////                    return lastRetElt;
////            }
////            throw new NoSuchElementException();
////        }

//        public void remove() {
//            if (expectedModCount != modCount)
//                throw new ConcurrentModificationException();
//            if (lastRet != -1) {
//                Object moved = PriorityQueue.this.removeAt(lastRet);
//                lastRet = -1;
//                if (moved == null)
//                    cursor--;
//                else {
//                    if (forgetMeNot == null)
//                        forgetMeNot = new ArrayDeque();
//                    forgetMeNot.add(moved);
//                }
//            } else if (lastRetElt != null) {
//                PriorityQueue.this.removeEq(lastRetElt);
//                lastRetElt = null;
//            } else {
//                throw new IllegalStateException();
//            }
//            expectedModCount = modCount;
//        }
//    }

    public int size() {
        return size;
    }


//    public void clear() {
//        modCount++;
//        for (int i = 0; i < size; i++)
//            queue[i] = null;
//        size = 0;
//    }


    /*@ requires size > 0;
      @ ensures \result == \old(queue[0]);
      @ ensures modCount == \old(modCount) + 1;
      @ ensures size == \old(size) - 1;
      @ ensures (\forall int i; 0<i && i<\old(size); (\exists int j; 0<=j && j<size; \old(queue[i]) == queue[j]));
      @ ensures (\forall int i; 0<=i && i<size; (\exists int j; 0<j && j<\old(size); queue[i] == \old(queue[j])));
      @ signals (Exception e) false;
      @ signals (AssertionError) true;
      @*/
    public int poll() {
//        if (size == 0)
//            return -1;
        int s = --size;
        modCount++;
        int result = queue[0];
        int x = queue[s];
        queue[s] = 0;
        if (s != 0) {


//            siftDown(0, x);

            int k = 0;
            int key = x;
            int half = size / 2;        // loop while a non-leaf
            while (k < half) {
                int child = (k * 2);// + 1; // assume left child is least
                int c = queue[child];
                int right = child + 1;
                if (right < size && c > queue[right]) {
                    child = right;
                    c = queue[right];
                }
                if (key <= c)
                    break;
                queue[k] = c;
                k = child;
            }
            queue[k] = key;
        }

        return result;
    }


//    private Object removeAt(int i) {
//        assert i >= 0 && i < size;
//        modCount++;
//        int s = --size;
//        if (s == i) // removed last element
//            queue[i] = null;
//        else {
//            Object moved = (Object) queue[s];
//            queue[s] = null;
//            siftDown(i, moved);
//            if (queue[i] == moved) {
//                siftUp(i, moved);
//                if (queue[i] != moved)
//                    return moved;
//            }
//        }
//        return null;
//    }


    private void siftUp(int k, int x) {
//        if (comparator != null)
//            siftUpUsingComparator(k, x);
//        else
        siftUpComparable(k, x);
    }

//    private void siftUpComparable(int k, int x) {
//        boolean exitLoop = false;
//        int key = x;
//        while (!exitLoop && k > 0) {
//            int parent = (k - 1) / 2;
//            int e = queue[parent];
//            if (key >= e)
//                exitLoop = true;
//            if (!exitLoop) {
//                queue[k] = e;
//                k = parent;
//            }
//        }
//        queue[k] = key;
//    }

    private void siftUpComparable(int k, int x) {
        int key = x;
        while (k > 0) {
            int parent = (k - 1) / 2;
            int e = queue[parent];
            if (key >= e) {
                break;
            }
            queue[k] = e;
            k = parent;
        }
        queue[k] = key;
    }

//    private void siftUpUsingComparator(int k, Object x) {
//        while (k > 0) {
//            int parent = (k - 1) >>> 1;
//            Object e = queue[parent];
//            if (comparator.compare(x, (Object) e) >= 0)
//                break;
//            queue[k] = e;
//            k = parent;
//        }
//        queue[k] = x;
//    }


//    private void siftDown(int k, Object x) {
//        if (comparator != null)
//            siftDownUsingComparator(k, x);
//        else
//            siftDownComparable(k, x);
//    }

    private void siftDownComparable(int k, int x) {
        int key = x;
        int half = size / 2;        // loop while a non-leaf
        while (k < half) {
            int child = (k / 2) + 1; // assume left child is least
            int c = queue[child];
            int right = child + 1;
            if (right < size && c > queue[right]) {
                child = right;
                c = queue[right];
            }
            if (key <= c)
                break;
            queue[k] = c;
            k = child;
        }
        queue[k] = key;
    }

//    private void siftDownUsingComparator(int k, Object x) {
//        int half = size >>> 1;
//        while (k < half) {
//            int child = (k << 1) + 1;
//            Object c = queue[child];
//            int right = child + 1;
//            if (right < size &&
//                comparator.compare((Object) c, (Object) queue[right]) > 0)
//                c = queue[child = right];
//            if (comparator.compare(x, (Object) c) <= 0)
//                break;
//            queue[k] = c;
//            k = child;
//        }
//        queue[k] = x;
//    }


//    private void heapify() {
//        for (int i = (size >>> 1) - 1; i >= 0; i--)
//            siftDown(i, (Object) queue[i]);
//    }


}
