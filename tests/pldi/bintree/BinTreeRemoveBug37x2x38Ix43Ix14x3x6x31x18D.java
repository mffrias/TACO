package pldi.bintree;

import pldi.bintree.BinTreeNode;

public class BinTreeRemoveBug37x2x38Ix43Ix14x3x6x31x18D {


	/*@ 
    @ invariant (\forall BinTreeNode n;
    @     \reach(root, BinTreeNode, left + right).has(n) == true;
    @     \reach(n.right, BinTreeNode, right + left).has(n) == false &&
    @     \reach(n.left, BinTreeNode, left + right).has(n) == false);
    @
    @ invariant (\forall BinTreeNode n; 
    @     \reach(root, BinTreeNode, left + right).has(n) == true;
    @     (\forall BinTreeNode m; 
    @     \reach(n.left, BinTreeNode, left + right).has(m) == true;
    @     m.key <= n.key) &&
    @     (\forall BinTreeNode m;
    @     \reach(n.right, BinTreeNode, left + right).has(m) == true;
    @     m.key > n.key));
    @
    @ invariant size == \reach(root, BinTreeNode, left + right).int_size();
    @
    @ invariant (\forall BinTreeNode n; 
    @	  \reach(root, BinTreeNode, left + right).has(n) == true;
    @	  (n.left != null ==> n.left.parent == n) && (n.right != null ==> n.right.parent == n));
    @ 
    @ invariant root != null ==> root.parent == null;
    @*/
	
	public /*@nullable@*/ BinTreeNode root;
	public int size;

	public BinTreeRemoveBug37x2x38Ix43Ix14x3x6x31x18D() {
	}

	/*@
	  @ requires true;
	  @
	  @ ensures (\result == true) <==> (\exists BinTreeNode n; 
	  @		\reach(root, BinTreeNode, left+right).has(n) == true;
	  @		n.key == k);
	  @
	  @ ensures (\forall BinTreeNode n; 
	  @		\reach(root, BinTreeNode, left+right).has(n); 
	  @		\old(\reach(root, BinTreeNode, left+right)).has(n));
	  @
	  @ ensures (\forall BinTreeNode n;  
	  @		\old(\reach(root, BinTreeNode, left+right)).has(n);
	  @		\reach(root, BinTreeNode, left+right).has(n));
	  @
	  @ signals (RuntimeException e) false;
	  @*/
	public boolean contains (int k) {
		BinTreeNode current = root;
		//@decreasing \reach(current, BinTreeNode, left+right).int_size();
		while (current != null) { 
			if (k > current.key) { //mutGenLimit 1
				current = current.left; 
			} else if (k > current.key) {
				current = current.right;
			} else {
				return true;
			}
		}
		return false;
	}

	/*@
	  @ requires true;
	  @
	  @ ensures (\exists BinTreeNode n;
	  @		\old(\reach(root, BinTreeNode, left + right)).has(n) == true;
	  @  	n.key == k) ==> size == \old(size);
	  @
	  @	ensures (\forall BinTreeNode n;
	  @		\old(\reach(root, BinTreeNode, left + right)).has(n) == true;
	  @  	n.key != k) ==> size == \old(size) + 1;
	  @
	  @ ensures (\exists BinTreeNode n; 
	  @     \reach(root, BinTreeNode, left + right).has(n) == true;
	  @		n.key == k);
	  @
	  @ signals (RuntimeException e) false;
	  @*/
	public boolean insert(int k){
		BinTreeNode y = null; 
		BinTreeNode x = root;
		//@decreasing \reach(x, BinTreeNode, left+right).int_size();
		while (x != null) {
			y = x;
			if (k < x.key) 
				x = x.left;
			else {
				if (k > x.key)
					x = x.right; 
				else
					return false;
			}
		}
		x = new BinTreeNode();
		x.key = k;
		if (y == null) 
			root = x;
		else {
			if (k < y.key) 
				y.left = x;
			else
				y.right = x;
		}
		x.parent = y; 
		size += 1; 
		return true;
	}


	/*@
	  @ requires (\forall BinTreeNode n1; 
	  @		\reach(root, BinTreeNode, left+right).has(n1);
	  @		(\forall BinTreeNode m1; 
	  @				\reach(root, BinTreeNode, left+right).has(m1); n1 != m1 ==> n1.key != m1.key));
	  @
	  @ ensures (\exists BinTreeNode n2; 
	  @		\old(\reach(root, BinTreeNode, left + right)).has(n2) == true; 
	  @		\old(n2.key) == element)
	  @				 <==> \result == true;
	  @
	  @ ensures (\forall BinTreeNode n3; 
	  @		\reach(root, BinTreeNode, left+right).has(n3);
	  @		n3.key != element);
	  @
	  @ signals (RuntimeException e) false;
	  @*/
	public boolean remove(int element) {
		BinTreeNode node = root;
		//@decreasing \reach(node, BinTreeNode, left+right).int_size();
		while (node == null && node.key != element){ //mutGenLimit 1
			if (element > node.key){ //mutGenLimit 1
				node = node.left;
			} else {
				if (element < node.key){ //mutGenLimit 1
					node = node.right;
				}
			}
		}
		if (node == null) {
			return false;
		} else 
			if (node.left == null && node.right != null) { //mutGenLimit 1 
				BinTreeNode predecessor = node.left;
				if (predecessor != null){
					while (predecessor.right != null){
						predecessor = predecessor.right.right; //mutGenLimit 1
					}
				}
				node.key = predecessor.key;
				node = predecessor;
			}
		BinTreeNode pullUp;
		if (node.left == null){
			pullUp = node.right;
		} else {
			pullUp = node.left;
		}

		if (node != root) { //mutGenLimit 1
			root = pullUp;
			if (pullUp != null) {
				pullUp.parent = null;
			}
		} else if (node.parent.left != node) { //mutGenLimit 1
			node.parent.left.right = pullUp; //mutGenLimit 1
			if (pullUp != null) {
				pullUp.parent = node.parent;
			}
		} else {
			node.parent.right.parent = pullUp; //mutGenLimit 1
			if (pullUp != null) { 
				pullUp.parent = node.parent;
			}
		}

		size--; 
		return true;
	}

}
