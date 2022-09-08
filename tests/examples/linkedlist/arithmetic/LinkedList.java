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
package examples.linkedlist.arithmetic;

/**
 * @Invariant 
 *		( this.header!=null ) &&
 *		( this.header.next!=null ) &&
 *		( this.header.previous!=null ) &&
 *		( this.size==#(this.header.*next @- null)-1 ) &&
 *		( this.size>=0 ) &&
 *		(all n: LinkedListNode | ( n in this.header.*next @- null ) => (
 *				  n!=null &&
 *				  n.previous!=null &&
 *				  n.previous.next==n &&
 *				  n.next!=null &&
 *				  n.next.previous==n )) ; 
 *
 */
public class LinkedList {


	public/*@ nullable @*/transient LinkedListNode header;
	/** The size of the list */

	protected transient int size;
	/** Modification count for iterators */
	
	protected transient int modCount;



	public LinkedList() {
		init();
		this.size = 0;
	}


	protected void init() {
		header = createHeaderNode();
	}


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


	public int indexOf(Object value) {
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


	/**
	 * @Modifies_Everything
	 * 
	 * @Ensures true;
	 */
	public boolean contains(/*@ nullable @*/Object arg) {
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

	/**
	 * @Modifies_Everything
	 * 
	 * @Ensures true;
	 */
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

	/**
	 * @Modifies_Everything
	 * 
	 * @Ensures true;
	 */
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


	protected/*@ pure @*/boolean isEqualValue(Object value1, Object value2) {
		return (value1 == value2 || (value1 == null ? false : value1
				.equals(value2)));
	}


	protected void updateNode(LinkedListNode node, Object value) {
		node.setValue(value);
	}


	protected LinkedListNode createHeaderNode() {
		return new LinkedListNode();
	}


	protected LinkedListNode createNode(Object value) {
		return new LinkedListNode(value);
	}


	protected void addNodeBefore(LinkedListNode node, Object value) {
		LinkedListNode newNode = createNode(value);
		addNode(newNode, node);
	}


	protected void addNodeAfter(LinkedListNode node, Object value) {
		LinkedListNode newNode = createNode(value);
		addNode(newNode, node.next);
	}


	protected void addNode(LinkedListNode nodeToInsert,
			LinkedListNode insertBeforeNode) {
		nodeToInsert.next = insertBeforeNode;
		nodeToInsert.previous = insertBeforeNode.previous;
		insertBeforeNode.previous.next = nodeToInsert;
		insertBeforeNode.previous = nodeToInsert;
		size++;
		modCount++;
	}


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
		int size_div_2 = size >> 1;
		if (index < (size_div_2)) {
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