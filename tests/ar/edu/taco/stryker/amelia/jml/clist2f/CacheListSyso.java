package ar.edu.taco.stryker.amelia.jml.clist2f;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import ar.edu.taco.stryker.amelia.jml.clist2f.CacheListNode;

/**
 * @j2daType
 */
/*@ nullable_by_default @*/
public class CacheListSyso extends Object {

	private PrintWriter out = null;
	
	public CacheListSyso() throws FileNotFoundException{
		out = new PrintWriter(new File("CacheListSyso.java"));
	}

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
		out.println("package ar.edu.taco.stryker.amelia.jml.clist2f;"+

"import ar.edu.taco.stryker.amelia.jml.clist2f.CacheListNode;"+
"/**"+
" * @j2daType"+
" */"+
"/*@ nullable_by_default @*/"+
"public class CacheListSyso extends Object {"+


"	/**"+
"	 * @j2daField"+
"	 */"+
"	public CacheListNode listHeader;"+

"	/**"+
"	 * @j2daField"+
"	 */"+
"	public CacheListNode cacheHeader;"+

"	/**"+
"	 * @j2daField"+
"	 */"+
"	public int maximumCacheSize;"+

"	/**"+
"	 * @j2daField"+
"	 */"+
"	public int cacheSize;"+

	
"	/**"+
"	 * @j2daField"+
"	 */"+
"	public int listSize;"+

	
"	/**"+
"	 * @j2daField"+
"	 */"+
"	public int DEFAULT_CACHE_SIZE;"+

"	/**"+
"	 * @j2daField"+
"	 */"+
"	public int modCount;"+

"	/*@"+
"	  @ invariant this.listHeader!=null &&"+
"	  @           this.listHeader.listNext!=null &&"+
"	  @           this.listHeader.listPrevious!=null &&"+
"	  @"+
"	  @           (\\forall CacheListNode n; \\reach(this.listHeader,CacheListNode,listNext).has(n)==true;"+
"	  @                                   n!=null &&"+
"	  @                                   n.listPrevious!=null &&"+
"	  @                                   n.listPrevious.listNext==n &&"+
"	  @                                   n.listNext!=null &&"+
"	  @                                   n.listNext.listPrevious==n  ) &&"+
"	  @"+
"	  @           this.listSize==\\reach(this.listHeader,CacheListNode,listNext).int_size()-1 &&"+
"	  @           this.listSize>=0;"+
"	  @"+
"	  @ invariant (\\forall CacheListNode m; \\reach(this.cacheHeader,CacheListNode, listNext).has(m)==true;"+
"	  @                                   \\reach(m.listNext,CacheListNode, listNext).has(m)==false &&"+
"	  @                                   m.listPrevious==null "+
"	  @                                   );"+
"	  @"+
"	  @ invariant this.cacheSize <= this.maximumCacheSize;"+
"	  @"+
"	  @ invariant this.DEFAULT_CACHE_SIZE == 5;"+
"	  @"+
"	  @ invariant this.cacheSize == \\reach(this.cacheHeader,CacheListNode, listNext).int_size();"+
"	  @*/"+

"	//@ requires this.maximumCacheSize==this.DEFAULT_CACHE_SIZE;"+
"	//@ requires this.cacheSize==20;"+
"	//@ requires this.listSize==0;"+
"	//@ ensures \\result==true;"+
"	/**"+
"	 * @j2daMethod"+
"	 */"+
"	public boolean showInstance() {"+
"		return false;"+
"	}"+

"	/*@"+
"	  @  requires index>0 && index<this.listSize;"+
"	  @  requires this.maximumCacheSize == this.DEFAULT_CACHE_SIZE;"+
"	  @  ensures (\\old(this.cacheSize) < this.maximumCacheSize) ==> (this.cacheSize == \\old(this.cacheSize) + 1);"+
"	  @  ensures this.modCount == \\old(this.modCount) + 1;"+
"	  @*/"+
"	/**"+
"	 * @j2daMethod"+
"	 */"+
"	public Data removeOk(final int index) {");
		
		out.println("CacheListNode node;");
		CacheListNode node;
		out.println("IndexOutOfBoundsException exception;");
		IndexOutOfBoundsException exception;
		out.println("exception = null;");
		exception = null;
		
		out.println("if (index < 0) {");
		if (index < 0) {
			out.println("exception = new IndexOutOfBoundsException();");
			exception = new IndexOutOfBoundsException();
			out.println("throw exception;");
			out.println("}");
			throw exception;
		}
		out.println("}");
		
		out.println("if (false == false && index == this.listSize) {");
		if (false == false && index == this.listSize) {
			out.println("exception = new IndexOutOfBoundsException();");
			exception = new IndexOutOfBoundsException();
			out.println("throw exception;");
			out.println("}");
			throw exception;
		}
		out.println("}");
		
		out.println("if (index > this.listSize) {");
		if (index > this.listSize) {
			out.println("exception = new IndexOutOfBoundsException();");
			exception = new IndexOutOfBoundsException();
			out.println("throw exception;");
			out.println("}");
			throw exception;
		}
		out.println("}");

		out.println("CacheListNode node1;");
		CacheListNode node1;
		out.println("if (index < (this.listSize / 2)) {");
		if (index < (this.listSize / 2)) {
			out.println("node1 = this.listHeader.listNext;");
			node1 = this.listHeader.listNext;
			out.println("int currentIndex;");
			int currentIndex;
			out.println("currentIndex = 0;");
			currentIndex = 0;
			out.println("while (currentIndex < index) {");
			while (currentIndex < index) {
				out.println("node1 = node1.listNext;");
				node1 = node1.listNext;
				out.println("currentIndex = currentIndex + 1;");
				currentIndex = currentIndex + 1;
			}
			out.println("}");
		out.println("} else {");
		} else {
		out.println("} else {");
			out.println("node1 = this.listHeader;");
			node1 = this.listHeader;
			out.println("int currentIndex = this.listSize;");
			int currentIndex = this.listSize;
			
			out.println("while (currentIndex > index) {");
			while (currentIndex > index) {
				out.println("node1 = node1.listPrevious;");
				node1 = node1.listPrevious;
				out.println("currentIndex = currentIndex - 1;");
				currentIndex = currentIndex - 1;
			}
			out.println("}");
		}
		out.println("}");
		
		out.println("node = node1;");
		node = node1;

		out.println("Data oldValue;");
		Data oldValue;
		out.println("oldValue = node.nodeValue;");
		oldValue = node.nodeValue;

		out.println("node.listPrevious.listNext = node.listNext;");
		node.listPrevious.listNext = node.listNext; // aca debia ser listNext
		out.println("node.listNext.listPrevious = node.listPrevious;");
		node.listNext.listPrevious = node.listPrevious;
		out.println("this.listSize = this.listSize - 1;");
		this.listSize = this.listSize - 1;				// aca debia ser - 1
		out.println("this.modCount = this.modCount + 1;");
		this.modCount = this.modCount + 1;

		out.println("if (this.cacheSize < this.maximumCacheSize) {");
		if (this.cacheSize < this.maximumCacheSize) {
			out.println("CacheListNode nextCachedNode;");
			CacheListNode nextCachedNode;

			out.println("nextCachedNode = this.cacheHeader;");
			nextCachedNode = this.cacheHeader;

			out.println("node.listPrevious = null;");
			node.listPrevious = null;
			out.println("node.listNext = nextCachedNode;");
			node.listNext = nextCachedNode;
			out.println("node.nodeValue = null;");
			node.nodeValue = null;
			out.println("this.cacheHeader = node;");
			this.cacheHeader = node;
			out.println("this.cacheSize = this.cacheSize + 1;");
			this.cacheSize = this.cacheSize + 1;
		}
		out.println("}");

		out.println("return oldValue;");
		out.println("}");
		
		
		out.println("	/*@"+
"	  @  requires index>=0 && index<this.listSize;"+
"	  @  requires this.maximumCacheSize == this.DEFAULT_CACHE_SIZE;"+
"	  @*/"+
"	/**"+
"	 * @j2daMethod"+
"	 */"+
"	public Data removeBuggy(final int index) {"+
"		CacheListNode node;"+
"		IndexOutOfBoundsException exception;"+
"		exception = null;"+
"		if (index < 0) {"+
"			exception = new IndexOutOfBoundsException();"+
"			throw exception;"+
"		}"+
"		if (false == false && index == this.listSize) {"+
"			exception = new IndexOutOfBoundsException();"+
"			throw exception;"+
"		}"+
"		if (index > this.listSize) {"+
"			exception = new IndexOutOfBoundsException();"+
"			throw exception;"+
"		}"+
	
"		CacheListNode node1;"+
"		if (index < (this.listSize / 2)) {"+
"			node1 = this.listHeader.listNext;"+
"			int currentIndex;"+
"			currentIndex = 0;"+
"			while (currentIndex < index) {"+
"				node1 = node1.listNext;"+
"				currentIndex = currentIndex + 1;"+
"			}"+
	
"		} else {"+
"			node1 = this.listHeader;"+
"			int currentIndex = this.listSize;"+
"			while (currentIndex > index) {"+
"				node1 = node1.listPrevious;"+
"				currentIndex = currentIndex - 1;"+
"			}"+
"		}"+
"		node = node1;"+
	
"		Data oldValue;"+
"		oldValue = node.nodeValue;"+
	
"		node.listPrevious.listNext = node.listNext;"+
"		node.listNext.listPrevious = node.listPrevious;"+
"		this.listSize = this.listSize - 1;"+
"		this.modCount = this.modCount + 1;"+
	
"		if (this.cacheSize <= this.maximumCacheSize) {"+
"			CacheListNode nextCachedNode;"+
	
"			nextCachedNode = this.cacheHeader;"+
	
"			node.listPrevious = null;"+
"			node.listNext = nextCachedNode;"+
"			node.nodeValue = null;"+
"			this.cacheHeader = node;"+
"			this.cacheSize = this.cacheSize + 1;"+
"		}"+
	
"		return oldValue;"+
"	}"+

"}");
		
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