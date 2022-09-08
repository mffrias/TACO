package pldi.bintree;

import pldi.bintree.BinTreeNode;

public class BinTreeContainsBug3x6x4Dx7Dx2x4Ix7Ix1x9x13 {


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

	public BinTreeContainsBug3x6x4Dx7Dx2x4Ix7Ix1x9x13() {
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
		BinTreeNode current = root.right; //mutGenLimit 1
		//@decreasing \reach(current, BinTreeNode, left+right).int_size();
		while (current == null) { //mutGenLimit 1
			if (k > current.key) { //mutGenLimit 1
				current.right = current.left.right; //mutGenLimit 2 
			} else if (k < current.key) { //mutGenLimit 1
				current.right = current.right.parent; //mutGenLimit 2
			} else {
				return !true; //mutGenLimit 1
			}
		}
		return !false; //mutGenLimit 1
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
		x.parent = x; //mutGenLimit 1
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
		while (node != null && node.key != element){
			if (element < node.key){
				node = node.left;
			} else {
				if (element > node.key){
					node = node.right;
				}
			}
		}
		if (node == null) {
			return false;
		} else 
			if (node.left != null && node.right != null) {
				BinTreeNode predecessor = node.left;
				if (predecessor != null){
					while (predecessor.right != null){
						predecessor = predecessor.right;
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

		if (node == root) {
			root = pullUp;
			if (pullUp != null) {
				pullUp.parent = null;
			}
		} else if (node.parent.left == node) {
			node.parent.left = pullUp;
			if (pullUp != null) {
				pullUp.parent = node.parent;
			}
		} else {
			node.parent.right = pullUp;
			if (pullUp != null) { 
				pullUp.parent = node.parent;
			}
		}

		size++; //mutGenLimit 1
		return true;
	}

}
