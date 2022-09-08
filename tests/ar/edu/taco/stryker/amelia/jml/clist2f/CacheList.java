package ar.edu.taco.stryker.amelia.jml.clist2f;

import ar.edu.taco.stryker.amelia.jml.clist2f.CacheListNode;

/**
 * @j2daType
 */
/*@ nullable_by_default @*/
public class CacheList extends Object {


	/**
	 * @j2daField
	 */
	public CacheListNode listHeader;

	/**
	 * @j2daField
	 */
	public CacheListNode cacheHeader;

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
	public int listSize;

	
	/**
	 * @j2daField
	 */
	public int DEFAULT_CACHE_SIZE;

	/**
	 * @j2daField
	 */
	public int modCount;

	/*@
	  @ invariant this.listHeader!=null &&
	  @           this.listHeader.listNext!=null &&
	  @           this.listHeader.listPrevious!=null &&
	  @
	  @           (\forall CacheListNode n; \reach(this.listHeader,CacheListNode,listNext).has(n)==true;
	  @                                   n!=null &&
	  @                                   n.listPrevious!=null &&
	  @                                   n.listPrevious.listNext==n &&
	  @                                   n.listNext!=null &&
	  @                                   n.listNext.listPrevious==n  ) &&
	  @
	  @           this.listSize==\reach(this.listHeader,CacheListNode,listNext).int_size()-1 &&
	  @           this.listSize>=0;
	  @
	  @ invariant (\forall CacheListNode m; \reach(this.cacheHeader,CacheListNode, listNext).has(m)==true;
	  @                                   \reach(m.listNext,CacheListNode, listNext).has(m)==false &&
	  @                                   m.listPrevious==null 
	  @                                   );
	  @
	  @ invariant this.cacheSize <= this.maximumCacheSize;
	  @
	  @ invariant this.DEFAULT_CACHE_SIZE == 5;
	  @
	  @ invariant this.cacheSize == \reach(this.cacheHeader,CacheListNode, listNext).int_size();
	  @*/

	//@ requires this.maximumCacheSize==this.DEFAULT_CACHE_SIZE;
	//@ requires this.cacheSize==20;
	//@ requires this.listSize==0;
	//@ ensures \result==true;
	/**
	 * @j2daMethod
	 */
	public boolean showInstance() {
		return false;
	}

	/*@
	  @  requires index>0 && index<this.listSize;
	  @  requires this.maximumCacheSize == this.DEFAULT_CACHE_SIZE;
	  @  ensures (\old(this.cacheSize) < this.maximumCacheSize) ==> (this.cacheSize == \old(this.cacheSize) + 1);
	  @  ensures this.modCount == \old(this.modCount) + 1;
	  @*/
	/**
	 * @j2daMethod
	 */
	public Data removeOk(final int index) {
		CacheListNode node = null;
		IndexOutOfBoundsException exception = null;
		exception = null;
		if (index < 0) {
			exception = new IndexOutOfBoundsException();
			throw exception;
		}
		if (false == false && index == this.listSize) {
			exception = new IndexOutOfBoundsException();
			throw exception;
		}
		if (index > this.listSize) {
			exception = new IndexOutOfBoundsException();
			throw exception;
		}

		CacheListNode node1 = null;
		if (index < (this.listSize / 2)) {
			node1 = this.listHeader.listNext;
			int currentIndex;
			currentIndex = 0;
			while (currentIndex < index) {
				node1 = node1.listNext;
				currentIndex = currentIndex + 1;
			}

		} else {
			node1 = this.listHeader;
			int currentIndex = this.listSize;
			while (currentIndex > index) {
				node1 = node1.listPrevious;
				currentIndex = currentIndex - 1;
			}
		}
		node = node1;

		Data oldValue = null;
		oldValue = node.nodeValue;

		node.listPrevious.listNext = node.listNext; // aca debia ser listNext
		node.listNext.listPrevious = node.listPrevious;
		this.listSize = this.listSize + 1;				// aca debia ser - 1
		this.modCount = this.modCount + 1;

		if (this.cacheSize < this.maximumCacheSize) {
			CacheListNode nextCachedNode;

			nextCachedNode = this.cacheHeader;

			node.listPrevious = null;
			node.listNext = nextCachedNode;
			node.nodeValue = null;
			this.cacheHeader = node;
			this.cacheSize = this.cacheSize + 1;
		}

		return oldValue;
	}

	/*@
	  @  requires index>=0 && index<this.listSize;
	  @  requires this.maximumCacheSize == this.DEFAULT_CACHE_SIZE;
	  @*/
	/**
	 * @j2daMethod
	 */
	public Data removeBuggy(final int index) {
		CacheListNode node;
		IndexOutOfBoundsException exception;
		exception = null;
		if (index < 0) {
			exception = new IndexOutOfBoundsException();
			throw exception;
		}
		if (false == false && index == this.listSize) {
			exception = new IndexOutOfBoundsException();
			throw exception;
		}
		if (index > this.listSize) {
			exception = new IndexOutOfBoundsException();
			throw exception;
		}
	
		CacheListNode node1;
		if (index < (this.listSize / 2)) {
			node1 = this.listHeader.listNext;
			int currentIndex;
			currentIndex = 0;
			while (currentIndex < index) {
				node1 = node1.listNext;
				currentIndex = currentIndex + 1;
			}
	
		} else {
			node1 = this.listHeader;
			int currentIndex = this.listSize;
			while (currentIndex > index) {
				node1 = node1.listPrevious;
				currentIndex = currentIndex - 1;
			}
		}
		node = node1;
	
		Data oldValue;
		oldValue = node.nodeValue;
	
		node.listPrevious.listNext = node.listNext;
		node.listNext.listPrevious = node.listPrevious;
		this.listSize = this.listSize - 1;
		this.modCount = this.modCount + 1;
	
		if (this.cacheSize <= this.maximumCacheSize) {
			CacheListNode nextCachedNode;
	
			nextCachedNode = this.cacheHeader;
	
			node.listPrevious = null;
			node.listNext = nextCachedNode;
			node.nodeValue = null;
			this.cacheHeader = node;
			this.cacheSize = this.cacheSize + 1;
		}
	
		return oldValue;
	}

}