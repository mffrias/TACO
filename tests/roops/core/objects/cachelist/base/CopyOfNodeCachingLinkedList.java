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


package roops.core.objects.cachelist.base;

import roops.core.objects.linkedlist.base.LinkedListNode;


/**
 * @j2daType
 */
/*@ nullable_by_default @*/
public class CopyOfNodeCachingLinkedList extends Object {


	/**
	 * @j2daField
	 */
	public LinkedListNode header;

	/**
	 * @j2daField
	 */
	public LinkedListNode firstCachedNode;

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

	//@ requires this.maximumCacheSize==this.DEFAULT_MAXIMUM_CACHE_SIZE;
	//@ requires this.cacheSize==20;
	//@ requires this.size==0;
	//@ ensures \result==true;
	/**
	 * @j2daMethod
	 */
	public boolean showInstance() {
		return false;
	}

	
    private boolean lt( int i, int j )
    {
        return i < j;
    }

    private boolean gt( int i, int j )
    {
        return i > j;
    }

    private int dec( int i )
    {
        return i - 1;
    }

    private int inc( int i )
    {
        return i + 1;
    }

    private int div( int a, int b )
    {
        return a / b;
    }

    private boolean eq( int a, int b )
    {
        return a == b;
    }
    
    private boolean neq( boolean a, boolean b )
    {
        return a != b;
    }


    private int add( int a, int b )
    {
        return a + b;
    }

    
	/*@
	  @  requires index>=0 && index<this.size;
	  @  requires this.maximumCacheSize == this.DEFAULT_MAXIMUM_CACHE_SIZE;
	  @  ensures this.size == \old(this.size) - 1;
	  @  ensures (\old(this.cacheSize) < this.maximumCacheSize) ==> (this.cacheSize == \old(this.cacheSize) + 1);
	  @  ensures this.modCount == \old(this.modCount) + 1;
	  @  signals (RuntimeException e) false;
	  @*/
	/**
	 * @j2daMethod
	 */
	public Object removeOk(final int index) {
		LinkedListNode node = null;
		IndexOutOfBoundsException exception = null;
		exception = null;
		if (lt(index,0)) {
			exception = new IndexOutOfBoundsException();
			throw exception;
		}
		if (eq(index,this.size)) {
			exception = new IndexOutOfBoundsException();
			throw exception;
		}
		if (gt(index,this.size)) {
			exception = new IndexOutOfBoundsException();
			throw exception;
		}

		LinkedListNode node1 = null;
		if (lt(index,div(this.size,2))) {
			node1 = this.header.next;
			int currentIndex;
			currentIndex = 0;
			while (lt(currentIndex,index)) {
				node1 = node1.next;
				currentIndex = inc(currentIndex);
			}

		} else {
			node1 = this.header;
			int currentIndex = this.size;
			while (gt(currentIndex,index)) {
				node1 = node1.previous;
				currentIndex = dec(currentIndex);
			}
		}
		node = node1;

		Object oldValue = null;
		oldValue = node.value;

		node.previous.next = node.next; 
		node.next.previous = node.previous;
		this.size = this.size - 1;				
		this.modCount = this.modCount + 1;

		if (this.cacheSize < this.maximumCacheSize) {
			LinkedListNode nextCachedNode;
			nextCachedNode = this.firstCachedNode;
			node.previous = null;
			node.next = nextCachedNode;
			node.value = null;
			this.firstCachedNode = node;
			this.cacheSize = this.cacheSize + 1;
		}

		return oldValue;
	}

	/*@
	  @  requires index>0 && index<this.size;
	  @  requires this.maximumCacheSize == this.DEFAULT_MAXIMUM_CACHE_SIZE;
	  @  ensures (\old(this.cacheSize) < this.maximumCacheSize) ==> (this.cacheSize == \old(this.cacheSize) + 1);
	  @  ensures this.modCount == \old(this.modCount) + 1;
	  @  signals (RuntimeException e) false;
	  @*/
	/**
	 * @j2daMethod
	 */
	public Object removeBuggy(final int index) {
		LinkedListNode node;
		IndexOutOfBoundsException exception;
		exception = null;
		if (index < 0) {
			exception = new IndexOutOfBoundsException();
			throw exception;
		}
		if (false == false && index == this.size) {
			exception = new IndexOutOfBoundsException();
			throw exception;
		}
		if (index > this.size) {
			exception = new IndexOutOfBoundsException();
			throw exception;
		}
	
		LinkedListNode node1;
		if (index < (this.size / 2)) {
			node1 = this.header.next;
			int currentIndex;
			currentIndex = 0;
			while (currentIndex < index) {
				node1 = node1.next;
				currentIndex = currentIndex + 1;
			}
	
		} else {
			node1 = this.header;
			int currentIndex = this.size;
			while (currentIndex > index) {
				node1 = node1.previous;
				currentIndex = currentIndex - 1;
			}
		}
		node = node1;
	
		Object oldValue;
		oldValue = node.value;
	
		node.previous.next = node.next;
		node.next.previous = node.previous;
		this.size = this.size - 1;
		this.modCount = this.modCount + 1;
	
		if (this.cacheSize <= this.maximumCacheSize) {
			LinkedListNode nextCachedNode;
	
			nextCachedNode = this.firstCachedNode;
	
			node.previous = null;
			node.next = nextCachedNode;
			node.value = null;
			this.firstCachedNode = node;
			this.cacheSize = this.cacheSize + 1;
		}
	
		return oldValue;
	}

}
