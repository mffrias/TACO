package wg1point9;


import wg1point9.LinkedListNode;

/*@ nullable_by_default @*/
public class NodeCachingLinkedList {

    public LinkedListNode header;

    public LinkedListNode firstCachedNode;

    public int maximumCacheSize;

    public int cacheSize;

    public int size;

    public int DEFAULT_MAXIMUM_CACHE_SIZE;

    public int modCount;

    public NodeCachingLinkedList() {
        this.header = new LinkedListNode();
        this.header.next = this.header;
        this.header.previous = this.header;
        this.firstCachedNode = null;
        this.size = 0;
        this.cacheSize = 0;
        this.DEFAULT_MAXIMUM_CACHE_SIZE = 2;
        this.maximumCacheSize = 2;
        this.modCount = 0;
    }

    /*@
    @ invariant this.header!=null &&
    @           this.header.next!=null &&
    @           this.header.previous!=null &&
    @
    @           (\forall LinkedListNode n; \reach(this.header,LinkedListNode,next).has(n); n!=null && n.previous!=null && n.previous.next==n && n.next!=null && n.next.previous==n ) &&
    @
    @           this.size + 1 == \reach(this.header,LinkedListNode,next).int_size() &&
    @           this.size>=0;
    @
    @ invariant (\forall LinkedListNode m; \reach(this.firstCachedNode, LinkedListNode, next).has(m);
    @                                   \reach(m.next, LinkedListNode, next).has(m)==false &&
    @                                   m.previous==null
    @                                   );
    @
    @ invariant this.cacheSize <= this.maximumCacheSize;
    @
    @ invariant this.maximumCacheSize == this.DEFAULT_MAXIMUM_CACHE_SIZE;
    @
    @ invariant this.DEFAULT_MAXIMUM_CACHE_SIZE == 2;
    @
    @ invariant this.cacheSize == \reach(this.firstCachedNode, LinkedListNode, next).int_size();
    @*/
    
    
    /*@
    @  requires index>=0 && index<this.size;
    @  ensures this.size == \old(this.size) - 1;
    @  ensures \old(cacheSize) < maximumCacheSize ==> cacheSize == \old(cacheSize) + 1;
    @  ensures this.modCount == \old(this.modCount) + 1;
    @  ensures (index == 0 && size >= 0) ==> \result == \old(this.header.next.value);
    @  ensures (index == 1 && size >= 1) ==> \result == \old(this.header.next.next.value);
    @  ensures (index == 2 && size >= 2) ==> \result == \old(this.header.next.next.next.value);
    @  ensures (index == 3 && size >= 3) ==> \result == \old(this.header.next.next.next.next.value);
    @  ensures (\forall LinkedListNode n; \reach(header, LinkedListNode, next).has(n); \old(\reach(header, LinkedListNode, next)).has(n));
    @  ensures (\exists LinkedListNode n; \old(\reach(header, LinkedListNode, next)).has(n); \reach(header, LinkedListNode, next).has(n) == false);
    @  ensures (\forall LinkedListNode n; \old(\reach(firstCachedNode, LinkedListNode, next)).has(n); \reach(firstCachedNode, LinkedListNode, next).has(n));
    @  signals (RuntimeException e) false;
    @*/
    public /*@nullable@*/java.lang.Object remove( final int index ) {
        LinkedListNode node = null; 
        if (index < 0) { 
            throw new java.lang.RuntimeException();
        }
        if (index == this.size) { 
            throw new java.lang.RuntimeException();
        }
        if (index > this.size) { 
            throw new java.lang.IndexOutOfBoundsException();
        }
        if (index < this.size / 2) { 
            node = this.header.next; 
            int currentIndex = 0; 
            //@decreasing index - currentIndex;
            while (currentIndex < index) { 
                node = node.next; 
                currentIndex++; 
            }
        } else {
            node = this.header; 
            int currentIndex = this.size; 
            //@decreasing currentIndex - index;
            while (currentIndex > index) { 
                node = node.previous; 
                currentIndex--; 
            }
        }
        java.lang.Object oldValue; 
        oldValue = node.next; //mutGenLimit 1
        node.previous.next = node.next;  
        node.next.previous = node.previous; 
        
        this.size = this.size - 1; //Further added fault, not marked as a possibly faulty statement
        
        this.modCount = this.modCount - 1; //mutGenLimit 1
        if (this.cacheSize < this.maximumCacheSize) {
            LinkedListNode nextCachedNode; 
            nextCachedNode = this.firstCachedNode; 
            node.previous = null; 
            node.next = nextCachedNode; 
            node.value = null; 
            this.firstCachedNode = node; 
            this.cacheSize = this.cacheSize - 1; //mutGenLimit 1
        }
        return oldValue; 
    }

    /*@ requires true;
    @ ensures size == \old(size) + 1;
    @ ensures modCount == \old(modCount) + 1;
    @ ensures ( \forall LinkedListNode n; \old(\reach(header, LinkedListNode, next)).has(n); \reach(header, LinkedListNode, next).has(n));
    @ ensures ( \forall LinkedListNode n; \reach(header, LinkedListNode, next).has(n) && n != header.next; \old(\reach(header, LinkedListNode, next)).has(n) );
    @ ensures ( header.next.value == o );
    @ ensures \result == true;
    @*/
    public boolean addFirst( java.lang.Object o ) {
        LinkedListNode newNode = new LinkedListNode(); //mutGenLimit 0
        newNode.value = o; //mutGenLimit 0
        LinkedListNode insertBeforeNode = this.header.next; //mutGenLimit 0
        newNode.next = insertBeforeNode; //mutGenLimit 0
        newNode.previous = insertBeforeNode.previous; //mutGenLimit 0
        insertBeforeNode.previous.next.previous = newNode; //mutGenLimit 0
        insertBeforeNode.previous = newNode; //mutGenLimit 0
        this.size++; //mutGenLimit 0
        this.modCount++; //mutGenLimit 0
        return true; //mutGenLimit 0
    }

    /*@
    @ requires true;
    @ ensures \result == true <==> (\exists LinkedListNode n; \reach(header, LinkedListNode, next).has(n) && n != header; n.value == arg);
    @ ensures \old(\reach(header, LinkedListNode, next)) == \reach(header, LinkedListNode, next);
    @*/
    public /*@ pure @*/boolean contains( /*@ nullable @*/java.lang.Object arg ) {
        LinkedListNode node = header.next; //mutGenLimit 0
        int counter = 0; //mutGenLimit 0
        //@decreasing size - counter;
        while (node != header && node.value != arg) { //mutGenLimit 0
            node = node.next; //mutGenLimit 0
            counter++; //mutGenLimit 0
        }
        if (node != header && node.value == arg) { //mutGenLimit 0
            return true; //mutGenLimit 0
        }
        return false; //mutGenLimit 0
    }

}
