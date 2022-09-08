package amelia.jml.clist2f;

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
	  @           (\forall CacheListNode n; \reach(this.listHeader,CacheListNode,listNext).has(n); 
	  @                                   n!=null && 
	  @                                   n.listPrevious!=null && 
	  @                                   n.listPrevious.listNext==n && 
	  @                                   n.listNext!=null && 
	  @                                   n.listNext.listPrevious==n  ) &&
	  @
	  @           this.listSize==\reach(this.listHeader,CacheListNode,listNext).int_size()-1 &&
	  @           this.listSize>=0;
	  @ 
	  @ invariant (\forall CacheListNode m; \reach(this.cacheHeader, CacheListNode, listNext).has(m); 
	  @                                   !\reach(m.listNext, CacheListNode, listNext).has(m) &&
	  @                                   m.listPrevious==null &&
	  @                                   m.nodeValue==null
	  @                                   ); 
	  @
	  @ invariant this.cacheSize <= this.maximumCacheSize;
	  @
	  @ invariant this.DEFAULT_CACHE_SIZE == 20;
	  @
	  @ invariant this.cacheSize == \reach(this.cacheHeader, CacheListNode, listNext).int_size();
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
	  @  requires index>=0 && index<this.listSize;
	  @  requires this.maximumCacheSize == this.DEFAULT_CACHE_SIZE;
	  @*/
	/**
	 * @j2daMethod
	 */
	public Object removeOk(final int index) {
		CacheListNode node;
		// Check the index is within the bounds
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

		// Search the list and get the node
		CacheListNode node1;
		if (index < (this.listSize / 2)) {
			// Search forwards
			node1 = this.listHeader.listNext;
			int currentIndex;
			currentIndex = 0;
			while (currentIndex < index) {
				node1 = node1.listNext;
				currentIndex = currentIndex + 1;
			}

		} else {
			// Search backwards
			node1 = this.listHeader;
			int currentIndex = this.listSize;
			while (currentIndex > index) {
				node1 = node1.listPrevious;
				currentIndex = currentIndex - 1;
			}
		}
		// begin node = getNode(index, false);
		node = node1;
		// end node = getNode(index, false);

		Object oldValue;
		oldValue = node.nodeValue;

		// begin this.removeNode(node)
		node.listPrevious.listNext = node.listNext;
		node.listNext.listPrevious = node.listPrevious;
		this.listSize = this.listSize - 1;
		this.modCount = this.modCount + 1;
		// end this.removeNode(node)

		//begin addNodeToCache(Node) 
		if (this.cacheSize < this.maximumCacheSize) {  
			// clear the node's contents and add it to the cache.
			CacheListNode nextCachedNode;

			nextCachedNode = this.cacheHeader;

			node.listPrevious = null;
			node.listNext = nextCachedNode;
			node.nodeValue = null;
			this.cacheHeader = node;
			this.cacheSize = this.cacheSize + 1;
		}
		// end addNodeToCache(Node)

		return oldValue;
	}

	/*@
	  @  requires index>=0 && index<this.listSize;
	  @  requires this.maximumCacheSize == this.DEFAULT_CACHE_SIZE;
	  @*/
	/**
	 * @j2daMethod
	 */
	public Object removeBuggy(final int index) {
		CacheListNode node;
		// Check the index is within the bounds
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
	
		// Search the list and get the node
		CacheListNode node1;
		if (index < (this.listSize / 2)) {
			// Search forwards
			node1 = this.listHeader.listNext;
			int currentIndex;
			currentIndex = 0;
			while (currentIndex < index) {
				node1 = node1.listNext;
				currentIndex = currentIndex + 1;
			}
	
		} else {
			// Search backwards
			node1 = this.listHeader;
			int currentIndex = this.listSize;
			while (currentIndex > index) {
				node1 = node1.listPrevious;
				currentIndex = currentIndex - 1;
			}
		}
		// begin node = getNode(index, false);
		node = node1;
		// end node = getNode(index, false);
	
		Object oldValue;
		oldValue = node.nodeValue;
	
		// begin this.removeNode(node)
		node.listPrevious.listNext = node.listNext;
		node.listNext.listPrevious = node.listPrevious;
		this.listSize = this.listSize - 1;
		this.modCount = this.modCount + 1;
		// end this.removeNode(node)
	
		//begin addNodeToCache(Node) 
		if (!(this.cacheSize < this.maximumCacheSize)) {  // <-- MUTANT
			// clear the node's contents and add it to the cache.
			CacheListNode nextCachedNode;
	
			nextCachedNode = this.cacheHeader;
	
			node.listPrevious = null;
			node.listNext = nextCachedNode;
			node.nodeValue = null;
			this.cacheHeader = node;
			this.cacheSize = this.cacheSize + 1;
		}
		// end addNodeToCache(Node)
	
		return oldValue;
	}

}
