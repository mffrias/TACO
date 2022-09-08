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
package jason.jml.alist;

//@ model import org.jmlspecs.models.*;

/**
 * A sliced version of class
 * org.apache.commons.collections.list.AbstractLinkedList
 */
public class AbstractLinkedList {

	protected/*@ nullable @*/Node header;

	protected transient int size;

	protected transient int modCount;

	/*@
	  @ invariant this.header!=null && 
	  @           this.header.next!=null && 
	  @           this.header.previous!=null &&
	  @
	  @           (\forall Node n; \reach(this.header,Node,next).has(n); 
	  @                                   n!=null && 
	  @                                   n.previous!=null && 
	  @                                   n.previous.next==n && 
	  @                                   n.next!=null && 
	  @                                   n.next.previous==n ) &&
	  @
	  @           this.size==\reach(this.header,Node,next).int_size()-1 &&
	  @           this.size>=0;
	  @ 
	  @*/

	//@ public model non_null JMLObjectSequence myseq;
	/*@ represents this.myseq \such_that
	  @    (
	  @    ( this.myseq.int_size()==\reach(this.header, Node, next).int_size() ) &&
	  @  
	  @    ( this.myseq.int_size() > 0 ==> this.myseq.get(0) == this.header ) && 
	  @  
	  @    (\forall int j; 0<=j && j< this.myseq.int_size() -1 ;
	  @      ((Node) this.myseq.get(j)).next == this.myseq.get(j+1)
	  @    )
	  @    );
	  @*/

	/*@
	  @ assignable \nothing; 
	  @*/
	private/*@ helper @*/int indexOf(/*@ nullable @*/DataObject value_param) {
		int i = 0;
		for (Node node = header.next; node != header; node = node.next) {
			if (isEqualValue(node.nodeValue, value_param)) {
				return i;
			}
			i++;
		}
		return -1;
	}

	/*@
	  @ assignable \nothing;
	  @ 
	  @ ensures (\exists int i; 0<i && i<this.myseq.int_size() && 
	  @                         ((Node)this.myseq.get(i)).nodeValue==value_param) 
	  @          <==> \result == true;
	  @*/
	public boolean contains(/*@ nullable @*/DataObject value_param) {
		return indexOf(value_param) != -1;
	}

	/*@
	  @
	  @ assignable \everything;
	  @
	  @ ensures this.myseq.int_size()==\old(this.myseq).int_size()+1 &&
	  @         ((Node)this.myseq.get(this.myseq.int_size()-1)).nodeValue==value_param && 
	  @         (\forall int w; 0<=w && w<(this.myseq.int_size()-1); this.myseq.get(w) == \old(this.myseq).get(w)) &&
	  @         ((Node)this.myseq.get(this.myseq.int_size()-1)).nodeValue==value_param;
	  @*/
	public boolean add(/*@ nullable @*/DataObject value_param) {
		addLast(value_param);
		return true;
	}

	private/*@ helper @*/boolean addLast(/*@ nullable @*/DataObject o) {
		addNodeBefore(header, o);
		return true;
	}

	private/*@ helper @*/void addNodeBefore(/*@ nullable @*/Node node, /*@ nullable @*/
	DataObject value) {
		Node newNode = createNode(value);
		addNode(newNode, node);
	}

	private/*@ helper @*//*@ nullable @*/Node createNode(
	/*@ nullable @*/DataObject value_param) {
		Node node = new Node();
		node.nodeValue = value_param;
		return node;
	}

	private/*@ helper @*/void addNode(/*@ nullable @*/Node nodeToInsert, /*@ nullable @*/
	Node insertBeforeNode) {
		nodeToInsert.next = insertBeforeNode;
		nodeToInsert.previous = insertBeforeNode.previous;
		insertBeforeNode.previous.next = nodeToInsert;
		insertBeforeNode.previous = nodeToInsert;
		size++;
		modCount++;
	}

	private/*@ helper @*//*@ nullable @*/Node getNode(int index,
			boolean endMarkerAllowed) throws IndexOutOfBoundsException {

		if (index < 0)
			throw new IndexOutOfBoundsException("Couldn't get the node: "
					+ "index (" + index + ") less than zero.");

		if (!endMarkerAllowed && index == this.size)
			throw new IndexOutOfBoundsException("Couldn't get the node: "
					+ "index (" + index + ") is the size of the list.");

		if (index > this.size)
			throw new IndexOutOfBoundsException("Couldn't get the node: "
					+ "index (" + index + ") greater than the size of the "
					+ "list (" + size + ").");

		Node node;
		if (index < (this.size / 2)) {

			node = this.header.next;
			for (int currentIndex = 0; currentIndex < index; currentIndex++) {
				node = node.next;
			}

		} else {

			node = this.header;
			for (int currentIndex = size; currentIndex > index; currentIndex--) {
				node = node.previous;
				currentIndex = currentIndex - 1;
			}
		}
		return node;
	}

	/*@
	  @ requires index>=0 && index<this.myseq.int_size();
	  @
	  @ assignable \everything;
	  @ 
	  @ ensures this.myseq.int_size()==\old(this.myseq).int_size()-1 &&
	  @         (\forall int i; 0<=i && i<=index;                           
	  @                         this.myseq.get(i)==\old(this.myseq).get(i)) &&
	  @         (\forall int j; index<j && j<this.myseq.int_size(); 
	  @                         this.myseq.get(j)==\old(this.myseq).get(j+1) ) ;
	  @*/
	public/*@ nullable @*/DataObject remove(int index) {
		Node node = getNode(index, false);
		DataObject oldValue = node.nodeValue;
		removeNode(node);
		return oldValue;
	}

	private/*@ helper @*/void removeNode(/*@ nullable @*/Node node) {
		node.previous.next = node.next;
		node.next.previous = node.previous;
		size--;
		modCount++;
	}

	/*@
	  @ assignable \nothing; 
	  @*/
	private/*@ helper @*/boolean isEqualValue(/*@ nullable @*/DataObject value1, /*@ nullable @*/
	DataObject value2) {
		return value1 == value2;
	}

}
