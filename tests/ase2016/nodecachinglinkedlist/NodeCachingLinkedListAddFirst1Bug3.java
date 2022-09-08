package ase2016.nodecachinglinkedlist;

/** 
* NodeCachingLinkedList1Bug3 is an implementation of node caching linked lists with a bug
* injected in line 3 of method addFirst. This bug replaces:
* newNode.next = insertBeforeNode;
* with
* newNode.next = null; 
* The bug to be inserted and the affected line were randomly chosen, from a set of
* real programming mistakes in doubly linked lists or singly linked lists implementations. 
* This particular one appears in:
* http://www.java-forums.org/new-java/13590-basic-circular-linked-list-addfirst-method-works-improperly.html
*/


import ase2016.nodecachinglinkedlist.LinkedListNode;



/**
* @j2daType
*//*@ nullable_by_default @*/
public class NodeCachingLinkedListAddFirst1Bug3 {

    public ase2016.nodecachinglinkedlist.LinkedListNode header;

    public ase2016.nodecachinglinkedlist.LinkedListNode firstCachedNode;

    public int maximumCacheSize;

    public int cacheSize;

    public int size;

    public int DEFAULT_MAXIMUM_CACHE_SIZE;

    public int modCount;

    public NodeCachingLinkedListAddFirst1Bug3() {
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
    
    

    /*@ requires newNode != null;
    @ requires newNode.next == null;
    @ requires newNode.previous == null;
    @ requires newNode.value == null;
    @ requires \reach(header, LinkedListNode, next).has(newNode) == false;
    @ requires \reach(firstCachedNode, LinkedListNode, next).has(newNode) == false;
    @ ensures size == \old(size) + 1;
    @ ensures modCount == \old(modCount) + 1;
    @ ensures ( \forall LinkedListNode n; \old(\reach(header, LinkedListNode, next)).has(n); \reach(header, LinkedListNode, next).has(n));
    @ ensures ( \forall LinkedListNode n; \reach(header, LinkedListNode, next).has(n) && n != header.next; \old(\reach(header, LinkedListNode, next)).has(n) );
    @ ensures ( header.next.value == o );
    @ ensures \result == true;
    @*/
    public boolean addFirst( java.lang.Object o, LinkedListNode newNode ) {
        newNode.value = o; //mutGenLimit 0
        ase2016.nodecachinglinkedlist.LinkedListNode insertBeforeNode = this.header.next; //mutGenLimit 0
        newNode.next = null; //mutGenLimit 1
        newNode.previous = insertBeforeNode.previous; //mutGenLimit 0
        insertBeforeNode.previous.next = newNode; //mutGenLimit 0
        insertBeforeNode.previous = newNode; //mutGenLimit 0
        this.size++; //mutGenLimit 0
        this.modCount++; //mutGenLimit 0
        return true; //mutGenLimit 0
    }


}
