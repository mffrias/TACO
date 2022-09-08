package pldi.bintree;

import pldi.bintree.BinTreeNode;

public class BinTreeInsertBug5x8x20x6Dx9Dx21Ix23Ix26I {


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

	public BinTreeInsertBug5x8x20x6Dx9Dx21Ix23Ix26I() {
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
			if (k < current.key) {
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
			if (k > x.key) //mutGenLimit 1
				x = x.left.right; //mutGenLimit 1
			else {
				if (k < x.key) //mutGenLimit 1
					x = x.right.parent; //mutGenLimit 1
				else
					return false;
			}
		}
		x = new BinTreeNode();
		x.key = k;
		if (y == null) 
			root = x;
		else {
			if (k > y.key) //mutGenLimit 1
				y.left.right = x; //mutGenLimit 1
			else
				y.right.right = x; //mutGenLimit 1
		}
		x.parent.right = y; //mutGenLimit 1
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
