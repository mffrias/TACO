/****************************************************************************
Author: Juan Pablo Galeotti and Marcelo Frias, Relational Formal Methods 
Group, University of Buenos Aires and Buenos Aires Institute of Technology,
Buenos Aires, Argentina.

ROOPS class implementing the apache.commons.collections class NodeCachingLinkedList.
It uses an auxiliary class LinkedListNode. Method removeIndex has been modified by
adding a goal that requires the cache list to be full to be covered. This means that
22 nodes are required in the cache part of the structure. 
A bug has been seeded in method isCacheFull. The bug allows to remove a node from the
NodeCachingLinkedList using method removeIndex and end up with an overflown cache. 
This state is captured by goal 10. The input NodeCachingLinkedList must have 23 nodes
in its cache linked list.

The class has annotations in JFSL [1] given as ROOPS comments. In particular, a class
invariant is provided.

[1] http://sdg.csail.mit.edu/forge/plugin.html
 ****************************************************************************/


package roops.core.objects;

import roops.core.objects.LinkedListNodeJML;


/**
 * @j2daType
 */
/*@ nullable_by_default @*/
public class NodeCachingLinkedListJML {


	/**
	 * @j2daField
	 */
	public LinkedListNodeJML header;

	/**
	 * @j2daField
	 */
	public LinkedListNodeJML firstCachedNode;

	/**
	 * @j2daField
	 */
	public int maximumCacheSize;

	/**
	 * @j2daField
	 */
	public int cacheSize;


	/**
	 * @j2daField
	 */
	public int size;


	/**
	 * @j2daField
	 */
	public int DEFAULT_MAXIMUM_CACHE_SIZE;

	/**
	 * @j2daField
	 */
	public int modCount;

	public NodeCachingLinkedListJML(){
		this.header = new LinkedListNodeJML();
		this.header.next = this.header;
		this.header.previous = this.header;
		this.firstCachedNode = null;
		this.size = 0;
		this.cacheSize = 0;
		this.DEFAULT_MAXIMUM_CACHE_SIZE = 3;
		this.maximumCacheSize = 3;
		this.modCount = 0;
	}



	/**
	 * @j2daMethod
	 */
	public boolean showInstance() {
		return false;
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
	  @ invariant this.DEFAULT_MAXIMUM_CACHE_SIZE == 3;
	  @
	  @ invariant this.cacheSize == \reach(this.firstCachedNode, LinkedListNode, next).int_size();
	  @*/










	/*@
	  @  requires index>=0 && index<this.size;
	  @  requires this.maximumCacheSize == this.DEFAULT_MAXIMUM_CACHE_SIZE;
	  @
	  @  ensures this.size == \old(this.size) - 1;
	  @  ensures (\old(this.cacheSize) < this.maximumCacheSize) ==> (this.cacheSize == \old(this.cacheSize) + 1);
	  @  ensures this.modCount == \old(this.modCount) + 1;
	  @
	  @  ensures index==0 ==> \result==\old(this.header.next.value);
	  @  ensures index==1 ==> \result==\old(this.header.next.next.value);
	  @  ensures index==2 ==> \result==\old(this.header.next.next.next.value);
	  @  ensures index==3 ==> \result==\old(this.header.next.next.next.next.value);
	  @  ensures index==4 ==> \result==\old(this.header.next.next.next.next.next.value);
	  @  ensures index==5 ==> \result==\old(this.header.next.next.next.next.next.next.value);
	  @  signals (Exception e) false;
	  @*/
	/**
	 * @j2daMethod
	 */
	public /*@nullable@*/ Object remove(final int index) {
		LinkedListNodeJML node = null;
		// Check the index is within the bounds
		if (index < 0) {
			throw new RuntimeException();
		}
		if (index == size) {
			throw new RuntimeException();
		}
		if (index > size) {
			throw new IndexOutOfBoundsException();
		}
		// Search the list and get the node
		if (index < (size / 2)) {
			// Search forwards
			node = header.next.next; //mutGenLimit 1
			for (int currentIndex = 0; currentIndex < index; currentIndex++) {
				node = node.next;
			}
		} else {
			// Search backwards
			node = header;
			for (int currentIndex = size; currentIndex > index; currentIndex--) {
				node = node.previous;
			}
		}

		Object oldValue;
		oldValue = node.value;	
		node.previous.next = node.next; 
		node.next.previous = node.previous;		
		this.size = this.size - 1;
		this.modCount = this.modCount - 1;//mutGenLimit 1
		if (this.cacheSize < this.maximumCacheSize) {
			LinkedListNodeJML nextCachedNode;
			nextCachedNode = this.firstCachedNode;
			node.previous = null;
			node.next = nextCachedNode;
			node.value = null;
			this.firstCachedNode = node;
			this.cacheSize = this.cacheSize - 1;//mutGenLimit 1
		}
		return oldValue;
	}

	//	




}
