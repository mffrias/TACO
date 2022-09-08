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
package examples.bintree.base;



/**
 * 
 * @Invariant all n : Node | n in this.root.*(left @+ right ) => ( 
 *            ( n !in n.^(left @+ right) ) && 
 *            ( all m: Node | m in n.left.*(left @+right) => m.key < n.key ) && 
 *            ( all m: Node | m in n.right.*(left @+right) => n.key < m.key ) && 
 *            ( n.left!=null => n.left.parent=n ) &&
 *            ( n.right!=null=> n.right.parent=n ) && 
 *            ( n=this.root => n.parent=null ) ) ;
 * 
 * @SpecField nodes : set Node from this.root, this.root.left, this.root.right |
 *            this.nodes = this.root.*(left @+ right) @- null ;
 * 
 */
public class BinTree {

	private /*@ nullable @*/ Node root;

	public BinTree() {
		root = null;
	}

	/**
	 * @Modifies_Everything
	 * @Requires node !in this.nodes && node.left==null && node.right==null && node.parent==null && node.key==0;
	 * @Ensures some n: Node | @old(this.nodes) @+ n = this.nodes && n.key==x ;
	 */
	public void add(int x, Node node) {
		Node current = root;

		if (root == null) {
			node.key = x;
			root = node;
		} else {
		  while (current.key != x) {
			if (x < current.key) {
				if (current.left == null) {
					node.key = x;
					node.parent = current;
					current.left = node;
				} else {
					current = current.left;
				}
			} else {
				if (current.right == null) {
					node.key = x;
					node.parent = current;
					current.right = node;
				} else {
					current = current.right;
				}
			}
		  }
		}
	}

	/**
	 * @Modifies_Everything
	 * @Ensures return==true <=> (some n: Node | n in this.nodes && n.key == x )
	 *          ;
	 */
	public/* @ pure @ */boolean find(int x) {
		Node current = root;

		while (current != null) {

			if (current.key == x) {
				return true;
			}

			if (x < current.key) {
				current = current.left;
			} else {
				current = current.right;
			}
		}

		return false;
	}

	public Node treeMinimum(final Node x_param) {
		Node x = x_param;
		while (x.left != null) {
			x = x.left;
		}
		return x;
	}

	public Node treeSuccessor(final Node x_param) {
		Node x = x_param;
		Node result;
		if (x.right != null) {
			result = treeMinimum(x.right);
		} else {
			Node y = x.parent;
			while (y != null && x == y.right) {
				x = y;
				y = y.parent;
			}
			result = y;
		}
		return result;
	}


	/**
	 * @Modifies_Everything
	 * @Requires z in this.nodes;
	 * @Ensures @old(this.nodes) = return @+ this.nodes && return !in this.nodes ;
	 */
	public Node remove(final Node z) {
		Node y = null;
		if (z.left == null || z.right == null) {
			y = z;
		} else {
			y = treeSuccessor(z);
		}

		Node x = null;
		if (y.left != null) {
			x = y.left;
		} else {
			x = y.right;
		}

		if (x != null) {
			x.parent = y.parent;
		}

		if (y.parent == null) {
			this.root = x;
		} else {
			if (y == y.parent.left)
				y.parent.left = x;
			else
				y.parent.right = x;
		}

		if (y != z) {
			z.key = y.key;
		}

		return y;
	}

}
