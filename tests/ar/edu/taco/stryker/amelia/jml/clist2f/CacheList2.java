// This is mutant program.
// Author : ysma

package ar.edu.taco.stryker.amelia.jml.clist2f;


import ar.edu.taco.stryker.amelia.jml.clist2f.CacheListNode;


/**
 * @j2daType
 */
/*@ nullable_by_default @*/



public class CacheList2 extends java.lang.Object
{

/**
	 * @j2daField
	 */


    public ar.edu.taco.stryker.amelia.jml.clist2f.CacheListNode listHeader;

/**
	 * @j2daField
	 */


    public ar.edu.taco.stryker.amelia.jml.clist2f.CacheListNode cacheHeader;

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
	  @ invariant this.DEFAULT_CACHE_SIZE == 3;
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







    public boolean showInstance()
    {
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

    private int add( int a, int b )
    {
        return a + b;
    }

/*@
	  @  requires index>0 && index<this.listSize;
	  @  requires this.maximumCacheSize == this.DEFAULT_CACHE_SIZE;
	  @  ensures (\old(this.cacheSize) < this.maximumCacheSize) ==> (this.cacheSize == \old(this.cacheSize) + 1);
	  @  ensures this.modCount == \old(this.modCount) + 1;
	  @  signals (Exception e) false;
	  @*/
/**
	 * @j2daMethod
	 */



    public ar.edu.taco.stryker.amelia.jml.clist2f.Data removeOk( final int index )
    {
        ar.edu.taco.stryker.amelia.jml.clist2f.CacheListNode node;
        java.lang.IndexOutOfBoundsException exception;
        exception = null;
        if (lt( index, 0 )) {
            exception = new java.lang.IndexOutOfBoundsException();
            throw exception;
        }
        if (eq( index, this.listSize )) {
            exception = new java.lang.IndexOutOfBoundsException();
            throw exception;
        }
        if (gt( index, this.listSize )) {
            exception = new java.lang.IndexOutOfBoundsException();
            throw exception;
        }
        ar.edu.taco.stryker.amelia.jml.clist2f.CacheListNode node1;
        if (lt( index, div( this.listSize, 2 ) )) {
            node1 = this.listHeader.listNext;
            int currentIndex;
            currentIndex = 0;
            while (lt( currentIndex, index )) {
                node1 = node1.listNext;
                currentIndex = inc( currentIndex );
            }
        } else {
            node1 = this.listHeader;
            int currentIndex = this.listSize;
            while (gt( currentIndex, index )) {
                node1 = node1.listPrevious;
                currentIndex = dec( currentIndex );
            }
        }
        node = node1;
        ar.edu.taco.stryker.amelia.jml.clist2f.Data oldValue;
        oldValue = node.nodeValue;
        node.listPrevious.listNext = node.listNext;
        node.listNext.listPrevious = node.listPrevious;
//Bug 1: - -> +
        this.listSize = this.listSize + 1;
//Bug 2: + -> -
        this.modCount = this.modCount - 1;
//Bug 3: < -> <=
        if (this.cacheSize <= this.maximumCacheSize) {
            ar.edu.taco.stryker.amelia.jml.clist2f.CacheListNode nextCachedNode;
            nextCachedNode = this.cacheHeader;
            node.listPrevious = null;
            node.listNext = nextCachedNode;
            node.nodeValue = null;
            this.cacheHeader = node;
//Bug 4: + -> -
            this.cacheSize = this.cacheSize - 1;
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



    public ar.edu.taco.stryker.amelia.jml.clist2f.Data removeBuggy( final int index )
    {
        ar.edu.taco.stryker.amelia.jml.clist2f.CacheListNode node;
        java.lang.IndexOutOfBoundsException exception;
        exception = null;
        if (index < 0) {
            exception = new java.lang.IndexOutOfBoundsException();
            throw exception;
        }
        if (false == false && index == this.listSize) {
            exception = new java.lang.IndexOutOfBoundsException();
            throw exception;
        }
        if (index > this.listSize) {
            exception = new java.lang.IndexOutOfBoundsException();
            throw exception;
        }
        ar.edu.taco.stryker.amelia.jml.clist2f.CacheListNode node1;
        if (index < this.listSize / 2) {
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
        ar.edu.taco.stryker.amelia.jml.clist2f.Data oldValue;
        oldValue = node.nodeValue;
        node.listPrevious.listNext = node.listNext;
        node.listNext.listPrevious = node.listPrevious;
        this.listSize = this.listSize - 1;
        this.modCount = this.modCount + 1;
        if (lt(this.cacheSize, this.maximumCacheSize)) {
            ar.edu.taco.stryker.amelia.jml.clist2f.CacheListNode nextCachedNode;
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
