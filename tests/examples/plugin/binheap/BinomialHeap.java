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
package examples.plugin.binheap;

//import kodkod.util.collections.IdentityHashSet;

public class BinomialHeap {

	//@ ensures \result==true;
	public boolean testExtractMin() {
		BinomialHeapNode node = this.extractMin();
		return false;
	}
	
	private/*@ nullable @*/BinomialHeapNode Nodes;

	private int size;

	public BinomialHeap() {
		Nodes = null;
		size = 0;
	}

	// 2. Find the minimum key
	/**
	 * @Modifies_Everything
	 * 
	 * @Requires some this.nodes ;
	 * @Ensures ( some x: BinomialHeapNode | x in this.nodes && x.key == return
	 *          ) && ( all y : BinomialHeapNode | ( y in this.nodes && y!=return
	 *          ) => return <= y.key ) ;
	 */
	public int findMinimum() {
		return Nodes.findMinNode().key;
	}

	// 3. Unite two binomial heaps
	// helper procedure
	private void merge(/* @ nullable @ */BinomialHeapNode binHeap) {
		BinomialHeapNode temp1 = Nodes, temp2 = binHeap;
		while ((temp1 != null) && (temp2 != null)) {
			if (temp1.degree == temp2.degree) {
				BinomialHeapNode tmp = temp2;
				temp2 = temp2.sibling;
				tmp.sibling = temp1.sibling;
				temp1.sibling = tmp;
				temp1 = tmp.sibling;
			} else {
				if (temp1.degree < temp2.degree) {
					if ((temp1.sibling == null) || (temp1.sibling.degree > temp2.degree)) {
						BinomialHeapNode tmp = temp2;
						temp2 = temp2.sibling;
						tmp.sibling = temp1.sibling;
						temp1.sibling = tmp;
						temp1 = tmp.sibling;
					} else {
						temp1 = temp1.sibling;
					}
				} else {
					BinomialHeapNode tmp = temp1;
					temp1 = temp2;
					temp2 = temp2.sibling;
					temp1.sibling = tmp;
					if (tmp == Nodes) {
						Nodes = temp1;
					}
				}
			}
		}

		if (temp1 == null) {
			temp1 = Nodes;
			while (temp1.sibling != null) {
				temp1 = temp1.sibling;
			}
			temp1.sibling = temp2;
		}

	}

	// another helper procedure
	private void unionNodes(/* @ nullable @ */BinomialHeapNode binHeap) {
		merge(binHeap);

		BinomialHeapNode prevTemp = null, temp = Nodes, nextTemp = Nodes.sibling;

		while (nextTemp != null) {
			if ((temp.degree != nextTemp.degree) || ((nextTemp.sibling != null) && (nextTemp.sibling.degree == temp.degree))) {
				prevTemp = temp;
				temp = nextTemp;
			} else {
				if (temp.key <= nextTemp.key) {
					temp.sibling = nextTemp.sibling;
					nextTemp.parent = temp;
					nextTemp.sibling = temp.child;
					temp.child = nextTemp;
					temp.degree++;
				} else {
					if (prevTemp == null) {
						Nodes = nextTemp;
					} else {
						prevTemp.sibling = nextTemp;
					}
					temp.parent = nextTemp;
					temp.sibling = nextTemp.child;
					nextTemp.child = temp;
					nextTemp.degree++;
					temp = nextTemp;
				}
			}

			nextTemp = temp.sibling;
		}
	}

	// 4. Insert a node with a specific value
	/**
	 * @Modifies_Everything
	 * 
	 * @Ensures some n: BinomialHeapNode | ( n !in @old(this.nodes) &&
	 *          this.nodes = @old(this.nodes) @+ n && n.key = value ) ;
	 */
	public void insert(int value) {
		if (value > 0) {
			BinomialHeapNode temp = new BinomialHeapNode(value);
			if (Nodes == null) {
				Nodes = temp;
				size = 1;
			} else {
				unionNodes(temp);
				size++;
			}
		}
	}

	// 5. Extract the node with the minimum key

	public/* @ nullable @ */BinomialHeapNode extractMin() {
		if (Nodes == null)
			return null;

		BinomialHeapNode temp = Nodes, prevTemp = null;
		BinomialHeapNode minNode = null;

		minNode = Nodes.findMinNode();
		while (temp.key != minNode.key) {
			prevTemp = temp;
			temp = temp.sibling;
		}

		if (prevTemp == null) {
			Nodes = temp.sibling;
		} else {
			prevTemp.sibling = temp.sibling;
		}
		temp = temp.child;
		BinomialHeapNode fakeNode = temp;
		while (temp != null) {
			temp.parent = null;
			temp = temp.sibling;
		}

		if ((Nodes == null) && (fakeNode == null)) {
			size = 0;
		} else {
			if ((Nodes == null) && (fakeNode != null)) {
				Nodes = fakeNode.reverse(null);
				size--;
			} else {
				if ((Nodes != null) && (fakeNode == null)) {
					size--;
				} else {
					unionNodes(fakeNode.reverse(null));
					size--;
				}
			}
		}

		return minNode;
	}

	// 6. Decrease a key value
	public void decreaseKeyValue(int old_value, int new_value) {
		BinomialHeapNode temp = Nodes.findANodeWithKey(old_value);
		decreaseKeyNode(temp, new_value);
	}

	/**
	 * 
	 * @Modifies_Everything
	 * 
	 * @Requires node in this.nodes && node.key >= new_value ;
	 * 
	 * @Ensures (some other: BinomialHeapNode | other in this.nodes &&
	 *          other!=node && @old(other.key)=@old(node.key)) ? this.nodes.key
	 *          = @old(this.nodes.key) @+ new_value : this.nodes.key =
	 * @old(this.nodes.key) @- @old(node.key) @+ new_value ;
	 */
	public void decreaseKeyNode(final BinomialHeapNode node, final int new_value) {
		if (node == null)
			return;
		
		BinomialHeapNode y = node;
		y.key = new_value;
		BinomialHeapNode z = node.parent;

		while ((z != null) && (node.key < z.key)) {
			int z_key = y.key;
			y.key = z.key;
			z.key = z_key;

			y = z;
			z = z.parent;
		}
	}

	// 7. Delete a node with a certain key
	public void delete(int value) {
		if ((Nodes != null) && (Nodes.findANodeWithKey(value) != null)) {
			decreaseKeyValue(value, findMinimum() - 1);
			extractMin();
		}
	}

	/*
	public static void main(String args[]) {

		BinomialHeapNode n0 = new BinomialHeapNode(14);
		BinomialHeapNode n1 = new BinomialHeapNode(14);
		BinomialHeapNode n2 = new BinomialHeapNode(13);
		BinomialHeapNode n3 = new BinomialHeapNode(14);
		BinomialHeapNode n4 = new BinomialHeapNode(14);
		BinomialHeapNode n5 = new BinomialHeapNode(14);
		BinomialHeapNode n6 = new BinomialHeapNode(14);
		BinomialHeapNode n7 = new BinomialHeapNode(13);
		BinomialHeapNode n8 = new BinomialHeapNode(14);
		BinomialHeapNode n9 = new BinomialHeapNode(13);
		BinomialHeapNode n10 = new BinomialHeapNode(13);
		BinomialHeapNode n11 = new BinomialHeapNode(14);
		BinomialHeapNode n12 = new BinomialHeapNode(14);

		BinomialHeap b0 = new BinomialHeap();
		b0.size = 13;
		b0.Nodes = n0;

		n0.sibling = n1;
		n0.name = "n0";

		n1.sibling = n2;
		n1.child = n3;
		n1.degree = 2;
		n1.name = "n1";

		n2.child = n4;
		n2.degree = 3;
		n2.name = "n2";
		
		n3.parent = n1;
		n3.sibling = n5;
		n3.child = n6;
		n3.degree = 1;
		n3.name = "n3";

		n4.parent = n2;
		n4.sibling = n7;
		n4.child = n8;
		n4.degree = 2;
		n4.name = "n4";

		n5.parent = n1;
		n5.name = "n5";

		n6.parent = n3;
		n6.name = "n6";

		n7.parent = n2;
		n7.sibling = n9;
		n7.child = n10;
		n7.degree = 1;
		n7.name = "n7";

		n8.parent = n4;
		n8.sibling = n11;
		n8.child = n12;
		n8.degree = 1;
		n8.name = "n8";

		n9.parent = n2;
		n9.name = "n9";
		
		n10.parent = n7;
		n10.name = "n10";
		
		n11.parent = n4;
		n11.name = "n11";
		
		n12.parent = n8;
		n12.name = "n12";
		
		int pre_size = count_nodes(b0);

		b0.extractMin();
		
		int post_size = count_nodes(b0);
		
		System.out.println(pre_size);
		System.out.println(post_size);

	}
	*/

	/*
	private static int count_nodes(BinomialHeap b0) {
		IdentityHashSet<BinomialHeapNode> already_visited = new IdentityHashSet<BinomialHeapNode>();
		IdentityHashSet<BinomialHeapNode> to_be_visited = new IdentityHashSet<BinomialHeapNode>();

		if (b0.Nodes != null)
			to_be_visited.add(b0.Nodes);

		while (!to_be_visited.isEmpty()) {
			BinomialHeapNode node = to_be_visited.iterator().next();
			to_be_visited.remove(node);
			already_visited.add(node);

			BinomialHeapNode parent = node.parent;
			BinomialHeapNode child = node.child;
			BinomialHeapNode sibling = node.sibling;

			if (parent != null && !already_visited.contains(parent)) {
				to_be_visited.add(parent);
			}
			if (child != null && !already_visited.contains(child)) {
				to_be_visited.add(child);
			}
			if (sibling != null && !already_visited.contains(sibling)) {
				to_be_visited.add(sibling);
			}

		}

		return already_visited.size();
	}
	*/

}
// end of class BinomialHeap
