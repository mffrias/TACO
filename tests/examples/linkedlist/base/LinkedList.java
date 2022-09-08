/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package examples.linkedlist.base;

//@ model import org.jmlspecs.models.*;

/**
 * An abstract implementation of a linked list which provides numerous points for
 * subclasses to override.
 * <p>
 * Overridable methods are provided to change the storage node and to change how
 * nodes are added to and removed. Hopefully, all you need for unusual subclasses
 * is here.
 * 
 * @since Commons Collections 3.0
 * @version $Revision: 1.3 $ $Date: 2010/05/21 19:52:28 $
 *
 * @author Rich Dougherty
 * @author Phil Steitz
 * @author Stephen Colebourne
 */
public class LinkedList {

	/*
	 * Implementation notes:
	 * - a standard circular doubly-linked list
	 * - a marker node is stored to mark the start and the end of the list
	 * - node creation and removal always occurs through createNode() and
	 *   removeNode().
	 * - a modification count is kept, with the same semantics as
	 * {@link java.util.LinkedList}.
	 * - respects {@link AbstractList#modCount}
	 */

	/**
	 * A {@link LinkedListNode} which indicates the start and end of the list and does not
	 * hold a value. The value of <code>next</code> is the first item in the
	 * list. The value of of <code>previous</code> is the last item in the list.
	 */
	public/*@ nullable @*/transient LinkedListNode header;
	/** The size of the list */
	protected transient int size;
	/** Modification count for iterators */
	protected transient int modCount;

	/*@
	  @ invariant this.header!=null && 
	  @           this.header.next!=null && 
	  @           this.header.previous!=null &&
	  @
	  @           (\forall LinkedListNode n; \reach(this.header,LinkedListNode,next).has(n); 
	  @                                   n!=null && 
	  @                                   n.previous!=null && 
	  @                                   n.previous.next==n && 
	  @                                   n.next!=null && 
	  @                                   n.next.previous==n ) &&
	  @
	  @           this.size==\reach(this.header,LinkedListNode,next).int_size()-1 &&
	  @           this.size>=0;
	  @ 
	  @*/

	//@ public model instance non_null JMLObjectSequence myseq;
	/*@ public represents this.myseq \such_that
	  @    (
	  @    ( this.myseq.int_size()==\reach(this.header, LinkedListNode, next).int_size() ) &&
	  @  
	  @    ( this.myseq.int_size() > 0 ==> this.myseq.get(0) == this.header ) && 
	  @  
	  @    (\forall int j; 0<=j && j< this.myseq.int_size() -1 ;
	  @      ((LinkedListNode) this.myseq.get(j)).next == this.myseq.get(j+1)
	  @    )
	  @    );
	  @*/

	/**
	 * Constructor that does nothing intended for deserialization.
	 * <p>
	 * If this constructor is used by a serializable subclass then the init()
	 * method must be called.
	 */
	public LinkedList() {
		init();
		this.size = 0;
	}

	/**
	 * The equivalent of a default constructor, broken out so it can be called
	 * by any constructor and by <code>readObject</code>.
	 * Subclasses which override this method should make sure they call super,
	 * so the list is initialised properly.
	 */
	protected void init() {
		header = createHeaderNode();
	}

	//-----------------------------------------------------------------------
	public int size() {
		return size;
	}

	public boolean isEmpty() {
		return (size() == 0);
	}

	public Object get(int index) {
		LinkedListNode node = getNode(index, false);
		return node.getValue();
	}

	//-----------------------------------------------------------------------
	public/*@ pure @*/int indexOf(Object value) {
		int i = 0;
		for (LinkedListNode node = header.next; node != header; node = node.next) {
			if (isEqualValue(node.getValue(), value)) {
				return i;
			}
			i++;
		}
		return -1;
	}

	public int lastIndexOf(Object value) {
		int i = size - 1;
		for (LinkedListNode node = header.previous; node != header; node = node.previous) {
			if (isEqualValue(node.getValue(), value)) {
				return i;
			}
			i--;
		}
		return -1;
	}

	/*@
	  @ ensures (\exists int i; 0<i && i<this.myseq.int_size() && 
	  @                         ((LinkedListNode)this.myseq.get(i)).value==arg) 
	  @          <==> \result == true;
	  @*/
	public/*@ pure @*/boolean contains(/*@ nullable @*/Object arg) {
		return indexOf(arg) != -1;
	}

	//-----------------------------------------------------------------------
	public boolean add(Object value) {
		addLast(value);
		return true;
	}

	public void add(int index, Object value) {
		LinkedListNode node = getNode(index, true);
		addNodeBefore(node, value);
	}

	//-----------------------------------------------------------------------

	 // modifies \everything;
	/*@
	  @ requires index>=0 && index<this.myseq.int_size();
	  @
	  @
	  @ ensures this.myseq.int_size()==\old(this.myseq).int_size()-1 &&
	  @         (\forall int i; 0<=i && i<=index;                           
	  @                         this.myseq.get(i)==\old(this.myseq).get(i)) &&
	  @         (\forall int j; index<j && j<this.myseq.int_size(); 
	  @                         this.myseq.get(j)==\old(this.myseq).get(j+1) ) ;
	  @*/
	public/*@ nullable @*/Object removeIndex(int index) {
		LinkedListNode node = getNode(index, false);
		Object oldValue = node.getValue();
		removeNode(node);
		return oldValue;
	}

	public boolean remove(Object value) {
		for (LinkedListNode node = header.next; node != header; node = node.next) {
			if (isEqualValue(node.getValue(), value)) {
				removeNode(node);
				return true;
			}
		}
		return false;
	}

	public Object set(int index, Object value) {
		LinkedListNode node = getNode(index, false);
		Object oldValue = node.getValue();
		updateNode(node, value);
		return oldValue;
	}

	public void clear() {
		removeAllNodes();
	}

	//-----------------------------------------------------------------------
	public Object getFirst() {
		LinkedListNode node = header.next;
		if (node == header) {
			throw new RuntimeException();
		}
		return node.getValue();
	}

	public Object getLast() {
		LinkedListNode node = header.previous;
		if (node == header) {
			throw new RuntimeException();
		}
		return node.getValue();
	}

	public boolean addFirst(Object o) {
		addNodeAfter(header, o);
		return true;
	}

	  //  modifies \everything; 
	/*@
	  @
	  @ 
	  @ ensures this.myseq.int_size()==\old(this.myseq).int_size()+1 &&
	  @         (\forall int w; 0<=w && w<(this.myseq.int_size()-1); this.myseq.get(w) == \old(this.myseq).get(w)) &&
	  @         ((LinkedListNode)this.myseq.get(this.myseq.int_size()-1)).value==o;
	  @*/
	public boolean addLast(/*@ nullable @*/Object o) {
		addNodeBefore(header, o);
		return true;
	}

	public Object removeFirst() {
		LinkedListNode node = header.next;
		if (node == header) {
			throw new RuntimeException();
		}
		Object oldValue = node.getValue();
		removeNode(node);
		return oldValue;
	}

	public Object removeLast() {
		LinkedListNode node = header.previous;
		if (node == header) {
			throw new RuntimeException();
		}
		Object oldValue = node.getValue();
		removeNode(node);
		return oldValue;
	}

	//-----------------------------------------------------------------------
	/**
	 * Compares two values for equals.
	 * This implementation uses the equals method.
	 * Subclasses can override this to match differently.
	 * 
	 * @param value1  the first value to compare, may be null
	 * @param value2  the second value to compare, may be null
	 * @return true if equal
	 */
	protected/*@ pure @*/boolean isEqualValue(Object value1, Object value2) {
		return (value1 == value2 || (value1 == null ? false : value1
				.equals(value2)));
	}

	/**
	 * Updates the node with a new value.
	 * This implementation sets the value on the node.
	 * Subclasses can override this to record the change.
	 * 
	 * @param node  node to update
	 * @param value  new value of the node
	 */
	protected void updateNode(LinkedListNode node, Object value) {
		node.setValue(value);
	}

	/**
	 * Creates a new node with previous, next and element all set to null.
	 * This implementation creates a new empty Node.
	 * Subclasses can override this to create a different class.
	 * 
	 * @return  newly created node
	 */
	protected LinkedListNode createHeaderNode() {
		return new LinkedListNode();
	}

	/**
	 * Creates a new node with the specified properties.
	 * This implementation creates a new Node with data.
	 * Subclasses can override this to create a different class.
	 * 
	 * @param value  value of the new node
	 */
	protected LinkedListNode createNode(Object value) {
		return new LinkedListNode(value);
	}

	/**
	 * Creates a new node with the specified object as its 
	 * <code>value</code> and inserts it before <code>node</code>.
	 * <p>
	 * This implementation uses {@link #createNode(Object)} and
	 * {@link #addNode(Node.Node,Node.Node)}.
	 *
	 * @param node  node to insert before
	 * @param value  value of the newly added node
	 * @throws NullPointerException if <code>node</code> is null
	 */
	protected void addNodeBefore(LinkedListNode node, Object value) {
		LinkedListNode newNode = createNode(value);
		addNode(newNode, node);
	}

	/**
	 * Creates a new node with the specified object as its 
	 * <code>value</code> and inserts it after <code>node</code>.
	 * <p>
	 * This implementation uses {@link #createNode(Object)} and
	 * {@link #addNode(Node.Node,Node.Node)}.
	 * 
	 * @param node  node to insert after
	 * @param value  value of the newly added node
	 * @throws NullPointerException if <code>node</code> is null
	 */
	protected void addNodeAfter(LinkedListNode node, Object value) {
		LinkedListNode newNode = createNode(value);
		addNode(newNode, node.next);
	}

	/**
	 * Inserts a new node into the list.
	 *
	 * @param nodeToInsert  new node to insert
	 * @param insertBeforeNode  node to insert before
	 * @throws NullPointerException if either node is null
	 */
	protected void addNode(LinkedListNode nodeToInsert,
			LinkedListNode insertBeforeNode) {
		nodeToInsert.next = insertBeforeNode;
		nodeToInsert.previous = insertBeforeNode.previous;
		insertBeforeNode.previous.next = nodeToInsert;
		insertBeforeNode.previous = nodeToInsert;
		size++;
		modCount++;
	}

	/**
	 * Removes the specified node from the list.
	 *
	 * @param node  the node to remove
	 * @throws NullPointerException if <code>node</code> is null
	 */
	protected void removeNode(LinkedListNode node) {
		node.previous.next = node.next;
		node.next.previous = node.previous;
		size--;
		modCount++;
	}

	/**
	 * Removes all nodes by resetting the circular list marker.
	 */
	protected void removeAllNodes() {
		header.next = header;
		header.previous = header;
		size = 0;
		modCount++;
	}

	/**
	 * Gets the node at a particular index.
	 * 
	 * @param index  the index, starting from 0
	 * @param endMarkerAllowed  whether or not the end marker can be returned if
	 * startIndex is set to the list's size
	 * @throws IndexOutOfBoundsException if the index is less than 0; equal to
	 * the size of the list and endMakerAllowed is false; or greater than the
	 * size of the list
	 */
	protected LinkedListNode getNode(int index, boolean endMarkerAllowed)
			throws IndexOutOfBoundsException {
		// Check the index is within the bounds
		if (index < 0) {
			throw new IndexOutOfBoundsException();
		}
		if (!endMarkerAllowed && index == size) {
			throw new IndexOutOfBoundsException();
		}
		if (index > size) {
			throw new IndexOutOfBoundsException();
		}
		// Search the list and get the node
		LinkedListNode node;
		if (index < (size / 2)) {
			// Search forwards
			node = header.next;
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
		return node;
	}

}