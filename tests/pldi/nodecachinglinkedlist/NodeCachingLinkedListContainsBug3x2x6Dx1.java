package pldi.nodecachinglinkedlist;

import pldi.nodecachinglinkedlist.LinkedListNode;


/*@ nullable_by_default @*/
public class NodeCachingLinkedListContainsBug3x2x6Dx1 {

    public pldi.nodecachinglinkedlist.LinkedListNode header;

    public pldi.nodecachinglinkedlist.LinkedListNode firstCachedNode;

    public int maximumCacheSize;

    public int cacheSize;

    public int size;

    public int DEFAULT_MAXIMUM_CACHE_SIZE;

    public int modCount;

    
    
    public NodeCachingLinkedListContainsBug3x2x6Dx1() {
        this.header = new pldi.nodecachinglinkedlist.LinkedListNode();
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
	  @           (\forall LinkedListNode n; \reach(this.header,LinkedListNode,next).has(n);  n.previous!=null && n.previous.next==n && n.next!=null && n.next.previous==n ) &&
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
	  @ invariant this.DEFAULT_MAXIMUM_CACHE_SIZE == 3;
	  @
	  @ invariant firstCachedNode != null ==> this.cacheSize == \reach(firstCachedNode, LinkedListNode, next).int_size();
	  @*/
    
    
    /*@ 
    @ requires true;
    @ ensures \result == true <==> (\exists LinkedListNode n; \reach(header, LinkedListNode, next).has(n) && n != header; n.value == arg);
    @ ensures (\forall LinkedListNode n; \old(\reach(header, LinkedListNode, next)).has(n); n.next == \old(n.next) && n.previous == \old(n.previous) && n.value == \old(n.value));
    @ ensures (\forall LinkedListNode n; \old(\reach(header, LinkedListNode, next)).has(n); \reach(header, LinkedListNode, next).has(n));
    @ ensures (\forall LinkedListNode n; \reach(header, LinkedListNode, next).has(n); \old(\reach(header, LinkedListNode, next)).has(n));
    @ ensures header == \old(header);
    @ ensures firstCachedNode == \old(firstCachedNode);
    @ ensures maximumCacheSize == \old(maximumCacheSize);
    @ ensures cacheSize == \old(cacheSize);
    @ ensures size == \old(size);
    @ ensures DEFAULT_MAXIMUM_CACHE_SIZE == \old(DEFAULT_MAXIMUM_CACHE_SIZE);
    @ ensures modCount == \old(modCount);
    @ signals (Exception e) false;
    @*/     public boolean contains( /*@ nullable @*/java.lang.Object arg ) {
    	  LinkedListNode node = this.header.previous.next; //mutGenLimit 1
    	  LinkedListNode node2 = node;
    	  int remaining = 0;
    	  while (node2 != this.header) {
    		  remaining = remaining + 1;
    		  node2 = node2.next;
    	  }
    	  //@decreasing remaining;
          while (node == this.header) { //mutGenLimit 1
              if (node.value != arg) { //mutGenLimit 1
                  return true;
              }
              node = node.next.next; //mutGenLimit 1
              int remaining2 = 0;
              LinkedListNode node3 = node;
        	  while (node3 != this.header) {
        		  remaining2 = remaining2 + 1;
        		  node3 = node3.next;
        	  }
        	  remaining = remaining2;
          }
          return false; 
      }

}
