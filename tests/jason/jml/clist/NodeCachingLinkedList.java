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
package jason.jml.clist;


//@ model import org.jmlspecs.models.*;

/**
 * An flatted/sliced version of 
 * org.apache.commons.collections.list.NodeCachingLinkedList
 */
public class NodeCachingLinkedList {

	protected/*@ nullable @*/Node header;

	protected transient int size;

	protected transient int modCount;

    protected transient Node firstCachedNode;
	
    protected transient int cacheSize;

    protected int maximumCacheSize;
    
    protected static final int DEFAULT_MAXIMUM_CACHE_SIZE = 20;
    
	/*@
	  @ invariant header!=null && 
	  @           header.next!=null && 
	  @           header.previous!=null &&
	  @
	  @           (\forall Node n; \reach(header,Node,next).has(n); 
	  @                                   n!=null && 
	  @                                   n.previous!=null && 
	  @                                   n.previous.next==n && 
	  @                                   n.next!=null && 
	  @                                   n.next.previous==n  ) &&
	  @
	  @           this.size==\reach(this.header,Node,next).int_size()-1 &&
	  @           this.size>=0;
	  @ 
	  @ invariant (\forall Node m; \reach(firstCachedNode, Node, next).has(m); 
	  @                                   !\reach(m.next, Node, next).has(m) &&
	  @                                   m.previous==null &&
	  @                                   m.nodeValue==null
	  @                                   ); 
	  @
	  @ invariant cacheSize <= maximumCacheSize;
	  @
	  @ invariant cacheSize == \reach(firstCachedNode, Node, next).int_size();
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

	private/*@ helper @*/void super_removeNode(/*@ nullable @*/Node node) {
		node.previous.next = node.next;
		node.next.previous = node.previous;
		size--;
		modCount++;
	}
	
    private /*@ helper @*/ void removeNode(/*@ nullable @*/Node node) {
        super_removeNode(node);
        addNodeToCache(node);
    }
    
    private /*@ helper @*/ void addNodeToCache(Node node) {
        if (isCacheFull()) {
            // don't cache the node.
            return;
        }
        // clear the node's contents and add it to the cache.
        Node nextCachedNode = firstCachedNode;
        node.previous = null;
        node.next = nextCachedNode;
        node.nodeValue = null;
        firstCachedNode = node;
        cacheSize++;
    }
    
    private /*@ helper @*/ boolean isCacheFull() {
        return cacheSize >= maximumCacheSize;
    }

}
