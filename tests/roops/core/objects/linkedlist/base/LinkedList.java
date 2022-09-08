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
package roops.core.objects.linkedlist.base;

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
	
	/**
	 * @Modifies_Everything;
	 * 
	 * @Ensures false;
	 */
	static public void addLastTest(/*@ nullable @*/ LinkedList list, /*@ nullable @*/ Object o) {
		if (list!=null) {
		  boolean ret_val = list.addLast(o);
		}
	}

	/**
	 * @Modifies_Everything;
	 * 
	 * @Ensures false;
	 */
	static public void containsTest(/*@ nullable @*/ LinkedList list, /*@ nullable @*/ Object arg) {
		if (list!=null) {
		  boolean ret_val = list.contains(arg);
		}
	}

	/**
	 * @Modifies_Everything;
	 * 
	 * @Ensures false;
	 */
	static public void removeIndexTest(/*@ nullable @*/ LinkedList list, int index) {
		if (list!=null) {
		  Object ret_val = list.removeIndex(index);
		}
	}


	public /*@ nullable @*/LinkedListNode header;
	
	/** The size of the list */
	public int size;
	
	/** Modification count for iterators */
	public int modCount;

	private void init() {
		header = createHeaderNode();
	}


	//-----------------------------------------------------------------------
	private int indexOf(Object value) {
		int i = 0;
		for (LinkedListNode node = header.next; node != header; node = node.next) {
			/*{roops.util.Goals.reached(0, roops.util.Verdict.REACHABLE);}*/
			if (isEqualValue(node.value, value)) {
				/*{roops.util.Goals.reached(1, roops.util.Verdict.REACHABLE);}*/
				return i;
			}
			i++;
		}
		/*{roops.util.Goals.reached(2, roops.util.Verdict.REACHABLE);}*/
		return -1;
	}

	
	public boolean contains(Object arg) {
		return indexOf(arg) != -1;
	}

	
	public Object removeIndex(int index) {
		LinkedListNode node = getNode(index, false);
		Object oldValue = node.value;
		removeNode(node);
		
		/*{roops.util.Goals.reached(9, roops.util.Verdict.REACHABLE);}*/
		return oldValue;
	}

	//@ ensures false;
	public boolean addLast(Object o) {
		addNodeBefore(header, o);
		return true;
	}

	private boolean isEqualValue(Object value1, Object value2) {
		boolean ret_val;
		if (value1 == value2) {
			ret_val=true;
		} else {
			if (value1==null) {
				ret_val = false;
			} else {
				ret_val = value1.equals(value2);
			}
		}
		return ret_val;
	}

	/**
	 * Creates a new node with previous, next and element all set to null.
	 * This implementation creates a new empty Node.
	 * Subclasses can override this to create a different class.
	 * 
	 * @return  newly created node
	 */
	private LinkedListNode createHeaderNode() {
		LinkedListNode linkedListNode = new LinkedListNode();
		linkedListNode.next = linkedListNode;
		linkedListNode.previous = linkedListNode;
		return linkedListNode;
	}

	/**
	 * Creates a new node with the specified properties.
	 * This implementation creates a new Node with data.
	 * Subclasses can override this to create a different class.
	 * 
	 * @param value  value of the new node
	 */
	private LinkedListNode createNode(Object value) {
		/*{roops.util.Goals.reached(0, roops.util.Verdict.REACHABLE);}*/
		LinkedListNode node = new LinkedListNode();
		node.previous = node;
		node.next = node;
		node.value = value;
		return node;
	}

	

	private void addNodeBefore(LinkedListNode node, Object value) {
		LinkedListNode newNode = createNode(value);
		/*{roops.util.Goals.reached(1, roops.util.Verdict.REACHABLE);}*/
		
		addNode(newNode, node);
	}

	private void addNode(LinkedListNode nodeToInsert,
			LinkedListNode insertBeforeNode) {
		nodeToInsert.next = insertBeforeNode;
		nodeToInsert.previous = insertBeforeNode.previous;
		insertBeforeNode.previous.next = nodeToInsert;
		insertBeforeNode.previous = nodeToInsert;
		size++;
		modCount++;
		
		/*{roops.util.Goals.reached(2, roops.util.Verdict.REACHABLE);}*/
	}


	private void removeNode(LinkedListNode node) {
		node.previous.next = node.next;
		node.next.previous = node.previous;
		size--;
		modCount++;
		/*{roops.util.Goals.reached(8, roops.util.Verdict.REACHABLE);}*/
	}


	private LinkedListNode getNode(int index, boolean endMarkerAllowed)
			throws IndexOutOfBoundsException {
		// Check the index is within the bounds
		if (index < 0) {
			/*{roops.util.Goals.reached(0, roops.util.Verdict.REACHABLE);}*/
			throw new IndexOutOfBoundsException();
		}
		if (!endMarkerAllowed && index == size) {
			/*{roops.util.Goals.reached(1, roops.util.Verdict.REACHABLE);}*/
			throw new IndexOutOfBoundsException();
		}
		if (index > size) {
			/*{roops.util.Goals.reached(2, roops.util.Verdict.REACHABLE);}*/
			throw new IndexOutOfBoundsException();
		}
		// Search the list and get the node
		LinkedListNode node;
		int size_div_2 = size >> 1;
		
		if (index < size_div_2) {
			/*{roops.util.Goals.reached(3, roops.util.Verdict.REACHABLE);}*/
			// Search forwards
			node = header.next;
			for (int currentIndex = 0; currentIndex < index; currentIndex++) {
				/*{roops.util.Goals.reached(4, roops.util.Verdict.REACHABLE);}*/
				node = node.next;
			}
		} else {
			
			/*{roops.util.Goals.reached(5, roops.util.Verdict.REACHABLE);}*/
			
			// Search backwards
			node = header;
			for (int currentIndex = size; currentIndex > index; currentIndex--) {
				/*{roops.util.Goals.reached(6, roops.util.Verdict.REACHABLE);}*/
				node = node.previous;
			}
		}
		/*{roops.util.Goals.reached(7, roops.util.Verdict.REACHABLE);}*/
		return node;
	}

	public LinkedList() {}
}