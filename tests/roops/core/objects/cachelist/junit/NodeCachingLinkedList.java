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


package roops.core.objects.cachelist.junit;



/**
 * @Invariant 
 *		( this.header!=null ) &&
 *		( this.header.next!=null ) &&
 *		( this.header.previous!=null ) &&
 *		( this.size==#(this.header.*next @- null)-1 ) &&
 *		( this.size>=0 ) &&
 *		( this.cacheSize <= this.maximumCacheSize ) &&
 *		( this.DEFAULT_MAXIMUM_CACHE_SIZE == 20 ) &&
 *		( this.cacheSize == #(this.firstCachedNode.*next @- null) ) &&
 *		(all m: LinkedListNode | ( m in this.firstCachedNode.*next @- null ) => (
 *				  m !in m.next.*next @- null &&
 *				  m.previous==null &&
 *				  m.value==null )) &&
 *		(all n: LinkedListNode | ( n in this.header.*next @- null ) => (
 *				  n!=null &&
 *				  n.previous!=null &&
 *				  n.previous.next==n &&
 *				  n.next!=null &&
 *				  n.next.previous==n )) ; 
 *
 */
public class NodeCachingLinkedList {

	/**
	 * @Modifies_Everything;
	 * 
	 * @Requires list.size==3 && list.cacheSize=2;
	 * 
	 * @Ensures return==true;
	 */
	static public boolean showInstance(/*@ nullable @*/ NodeCachingLinkedList list) {
		//int i = 3;
		//int j = 2;
		//int k = 20;
		//int h = 1;
		return false;
	}

	/*@ nullable @*/ LinkedListNode header;
	int size;
	private int modCount;

	public int DEFAULT_MAXIMUM_CACHE_SIZE;



	 /*@ nullable @*/ LinkedListNode firstCachedNode;

	/**
	 * The size of the cache.
	 */
    int cacheSize;

	/**
	 * The maximum size of the cache.
	 */
	int maximumCacheSize;
	
	public NodeCachingLinkedList() {}
}
/* end roops.core.objects */
