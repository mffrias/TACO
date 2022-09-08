//
// Copyright (C) 2006 United States Government as represented by the
// Administrator of the National Aeronautics and Space Administration
// (NASA).  All Rights Reserved.
// 
// This software is distributed under the NASA Open Source Agreement
// (NOSA), version 1.3.  The NOSA has been approved by the Open Source
// Initiative.  See the file NOSA-1.3-JPF at the top of the distribution
// directory tree for the complete NOSA document.
// 
// THE SUBJECT SOFTWARE IS PROVIDED "AS IS" WITHOUT ANY WARRANTY OF ANY
// KIND, EITHER EXPRESSED, IMPLIED, OR STATUTORY, INCLUDING, BUT NOT
// LIMITED TO, ANY WARRANTY THAT THE SUBJECT SOFTWARE WILL CONFORM TO
// SPECIFICATIONS, ANY IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR
// A PARTICULAR PURPOSE, OR FREEDOM FROM INFRINGEMENT, ANY WARRANTY THAT
// THE SUBJECT SOFTWARE WILL BE ERROR FREE, OR ANY WARRANTY THAT
// DOCUMENTATION, IF PROVIDED, WILL CONFORM TO THE SUBJECT SOFTWARE.
//
package examples.fiboheap;

//FibHeap.java
//from : http://sciris.shu.edu/~borowski/Puzzle/Puzzle.html
/**
 * @SpecField nodes : set Node from this.min, this.min.left, this.min.right , this.min.parent , this.min.child | 
 *                    this.nodes = this.min.*(left @+ right @+ parent @+ child) @- null ;
 *
 */
/**
 * @Invariant ( all node: Node | node in this.min.*(child @+ right) => ( ( node
 *            !in (node.*right @- node).child.*(child @+ right) ) && ( ( node in
 *            this.min.*right ) => (node.parent==null && this.min.cost <=
 *            node.cost ) ) && ( node.right != null ) && ( node.right.left ==
 *            node ) && ( node.degree = #(node.child.*right @- null) ) && ( all
 *            m: Node | ( m in node.child.*(child @+ right) => node.cost <=
 *            m.cost ) ) && ( node.child != null =>
 *            node.(child.*right).parent==node ) ) ) &&
 * 
 *            ( this.n = #(this.min.*(child @+ right) @- null) ) ;
 */
public class FibHeap {
	public/* @ nullable @ */Node min;

	public int n;

	public FibHeap() {
	}

	private void cascadingCut(Node y) {
		Node z = y.parent;
		if (z != null)
			if (!y.mark) {
				y.mark = true;
			} else {
				cut(y, z);
				cascadingCut(z);
			}
		else {

		}
	}

	private void consolidate() {
		int D = n + 1;
		Object[] A = new Object[D];
		for (int i = 0; i < D; i++) {
			A[i] = null;
		}

		int k = 0;
		Node x = min;
		if (x != null) {
			k++;
			for (x = x.right; x != min; x = x.right) {
				k++;
			}
		}
		while (k > 0) {
			int d = x.degree;
			Node rightNode = x.right;

			while (A[d] != null) {

				if (!(A[d] instanceof Node)) {
					throw new ClassCastException();
				}
				Node y = (Node) A[d];
				
				if (x.cost > y.cost) {

					Node temp = y;
					y = x;
					x = temp;
				} else

					link(y, x);
				A[d] = null;
				d++;
			}

			A[d] = x;
			x = rightNode;
			k--;
		}

		min = null;
		for (int i = 0; i < D; i++)
			if (A[i] != null)
				if (min != null) {

					if (!(A[i] instanceof Node)) {
						throw new ClassCastException();
					} 
					Node node = (Node) A[i];

					node.left.right = node.right;
					node.right.left = node.left;
					node.left = min;
					node.right = min.right;
					min.right = node;
					node.right.left = node;
					if (node.cost < min.cost) {
						

						if (!(A[i] instanceof Node)) {
							throw new ClassCastException();
						}						
						min = (Node) A[i];
						
					} else {

					}
				} else {


					if (!(A[i] instanceof Node)) {
						throw new ClassCastException();
					}
					min = (Node) A[i];
				}
	}

	private void cut(Node x, Node y) {
		x.left.right = x.right;
		x.right.left = x.left;
		y.degree--;
		if (y.child == x) {

			y.child = x.right;
		} else {

		}
		if (y.degree == 0) {

			y.child = null;
		} else {

		}
		x.left = min;
		x.right = min.right;
		min.right = x;
		x.right.left = x;
		x.parent = null;
		x.mark = false;
	}

	public void decreaseKey(Node x, int c) {
		if (c > x.cost) {
			throw new RuntimeException();
		}
		x.cost = c;
		Node y = x.parent;

		if ((y != null) && (x.cost < y.cost)) {
			cut(x, y);
			cascadingCut(y);
		}

		if (x.cost < min.cost)
			min = x;
	}

	public void delete(Node node) {
		int min_val_minus_1 = -2147483647;
		int min_value = min_val_minus_1 - 1;
		decreaseKey(node, min_value);
		removeMin();
	}

	public boolean empty() {
		return min == null;
	}

	public void insert(int c) {
		Node n = new Node(c);
		insertNode(n);
	}

	/**
	 * @Modifies_Everything
	 * 
	 * @Requires toInsert!=null && toInsert !in this.nodes && toInsert.child ==
	 *           null && toInsert.parent == null && toInsert.left == toInsert &&
	 *           toInsert.right ==toInsert && toInsert.cost == 0 &&
	 *           toInsert.degree == 0 && toInsert.mark == false ;
	 * 
	 * @Ensures this.nodes == @old(this.nodes) @+ toInsert && return == toInsert
	 *          ;
	 */
	public/* @ nullable @ */Node insertNode(/* @ nullable@ */Node toInsert) {
		if (min != null) {
			toInsert.left = min;
			toInsert.right = min.right;
			min.right = toInsert;
			toInsert.right.left = toInsert;
			if (toInsert.cost < min.cost) {

				min = toInsert;
			} else {

			}
		} else {
			min = toInsert;

		}
		n++;
		return toInsert;
	}

	private void link(Node node1, Node node2) {
		node1.left.right = node1.right;
		node1.right.left = node1.left;
		node1.parent = node2;
		if (node2.child == null) {

			node2.child = node1;
			node1.right = node1;
			node1.left = node1;
		} else {

			node1.left = node2.child;
			node1.right = node2.child.right;
			node2.child.right = node1;
			node1.right.left = node1;
		}
		node2.degree++;
		node1.mark = false;
	}

	/**
	 * @Modifies_Everything
	 * @Ensures some this.nodes => ( ( return in this.nodes ) && ( no node: Node
	 *          | node in this.nodes && node.cost < return.cost ) ) && ( no
	 *          this.nodes => return == null );
	 */
	public/* @ nullable @ */Node minimum() {
		return min;
	}

	/**
	 * @Modifies_Everything
	 * 
	 * @Ensures some this.nodes => ( ( return in @old(this.nodes) ) && (
	 *          this.nodes = @old(this.nodes) @- return ) && ( no node : Node |
	 *          node in this.nodes && node.cost < return.cost ) ) && (no
	 *          this.nodes => return = null) ;
	 */
	public Node removeMin() {
		Node z = min;
		if (z != null) {
			int i = z.degree;
			Node x = z.child;
			while (i > 0) {

				Node nextChild = x.right;
				x.left.right = x.right;
				x.right.left = x.left;
				x.left = min;
				x.right = min.right;
				min.right = x;
				x.right.left = x;
				x.parent = null;
				x = nextChild;
				i--;
			}
			z.left.right = z.right;
			z.right.left = z.left;
			if (z == z.right) {

				min = null;
			} else {

				min = z.right;
				consolidate();
			}

			n--;
		}
		return z;
	}

	public int size() {
		return n;
	}

	public static FibHeap union(FibHeap heap1, FibHeap heap2) {
		FibHeap heap = new FibHeap();
		if ((heap1 != null) && (heap2 != null)) {
			heap.min = heap1.min;
			if (heap.min != null) {
				if (heap2.min != null) {
					heap.min.right.left = heap2.min.left;
					heap2.min.left.right = heap.min.right;
					heap.min.right = heap2.min;
					heap2.min.left = heap.min;
					if (heap2.min.cost < heap1.min.cost)
						heap.min = heap2.min;
				}
			} else
				heap.min = heap2.min;
			heap.n = heap1.n + heap2.n;
		}
		return heap;
	}

	//@ ensures \result==true;
	public boolean showInstance() {
		if (this.n!=7)
			return true;
		
		return false;
	}
}
