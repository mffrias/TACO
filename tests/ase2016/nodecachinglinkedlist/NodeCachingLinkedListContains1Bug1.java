package ase2016.nodecachinglinkedlist;

/** 
* NodeCachingLinkedListContains1Bug1 is an implementation of node caching linked lists with a bug
* injected in line 1 of method contains. This bug replaces:
* node = header.next;
* with 
* node = header;
* The bug to be inserted and the affected line were randomly chosen, from a set of
* real programming mistakes in doubly linked lists or singly linked lists implementations. 
* This particular one appears in:
* http://cslibrary.stanford.edu/105/LinkedListProblems.pdf
* Notice the use of variable counter. This is not a redundant variable, it is
* use in the JML variant loop condition.
*/

import ase2016.nodecachinglinkedlist.LinkedListNode;


/**
* @j2daType
*//*@ nullable_by_default @*/
public class NodeCachingLinkedListContains1Bug1 {

    public ase2016.nodecachinglinkedlist.LinkedListNode header;

    public ase2016.nodecachinglinkedlist.LinkedListNode firstCachedNode;

    public int maximumCacheSize;

    public int cacheSize;

    public int size;

    public int DEFAULT_MAXIMUM_CACHE_SIZE;

    public int modCount;

    public NodeCachingLinkedListContains1Bug1() {
        this.header = new ase2016.nodecachinglinkedlist.LinkedListNode();
        this.header.next = this.header;
        this.header.previous = this.header;
        this.firstCachedNode = null;
        this.size = 0;
        this.cacheSize = 0;
        this.DEFAULT_MAXIMUM_CACHE_SIZE = 3;
        this.maximumCacheSize = 3;
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
    @ invariant this.DEFAULT_MAXIMUM_CACHE_SIZE == 3;
    @
    @ invariant this.cacheSize == \reach(this.firstCachedNode, LinkedListNode, next).int_size();
    @*/
    
    

    /*@
    @ requires true;
    @ ensures \result == true <==> (\exists LinkedListNode n; \reach(header, LinkedListNode, next).has(n) && n != header; n.value == arg);
    @ ensures (\forall LinkedListNode n; \old(\reach(header, LinkedListNode, next)).has(n); \reach(header, LinkedListNode, next).has(n));
    @ ensures (\forall LinkedListNode n; \reach(header, LinkedListNode, next).has(n); \old(\reach(header, LinkedListNode, next)).has(n));
    @ signals (Exception e) false;
    @*/
    public /*@ pure @*/boolean contains( /*@ nullable @*/java.lang.Object arg ) {
        ase2016.nodecachinglinkedlist.LinkedListNode node = header; //mutGenLimit 1
        int counter = 0; //mutGenLimit 0
        //@decreasing size - counter;
        while (node != header && node.value != arg) { //mutGenLimit 0
            node = node.next; //mutGenLimit 0
            counter = counter +1; //mutGenLimit 0
        }
        if (node != header && node.value == arg) { //mutGenLimit 0
            return true; //mutGenLimit 0
        }
        return false; //mutGenLimit 0
    }

}
