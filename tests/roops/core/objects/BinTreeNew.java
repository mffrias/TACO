package roops.core.objects;

import roops.core.objects.BinTreeNode;

public class BinTree {


	/*@
    @ invariant (\forall BinTreeNode n;
    @     \reach(root, BinTreeNode, left + right).has(n) == true;
    @     \reach(n.right, BinTreeNode, right + left).has(n) == false &&
    @     \reach(n.left, BinTreeNode, left + right).has(n) == false);
    @
    @ invariant (\forall BinTreeNode n;
    @     \reach(root, BinTreeNode, left + right).has(n) == true;
    @     (\forall BinTreeNode m; \reach(n.left, BinTreeNode, left + right).has(m) == true; m.key <= n.key) &&
    @     (\forall BinTreeNode m; \reach(n.right, BinTreeNode, left + right).has(m) == true; m.key > n.key));
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

	public BinTree() {
	}

	/*@
    @ requires root != null;
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
	public boolean contains( int k ) {
		BinTreeNode current = root;
		while (current != null) {
			if (current.key < k) { //mutGenLimit 1
				current = current.left;
			} else {
				if (k > current.key) {
					current = current.right;
				} else {
					return true;
				}
			}
		}
		return false;
	}



	/*@ requires true;
	  @ ensures (\exists BinTreeNode n; \reach(root, BinTreeNode, left+right).has(n); n.key == k);
	  @ signals (Exception e) false;
	  @*/
	public boolean insert(int k){
		BinTreeNode current = root; 
		while (current != null) { 
			if (current.key > k) { 
				if (current.left == null){
					BinTreeNode newNode = new BinTreeNode();
					newNode.parent = current;
					current.left = newNode;
					newNode.key = k;
					size++;
					return true;
				}
				current = current.left;
			} else {
				if (k > current.key) {
					if (current.right == null){
						BinTreeNode newNode = new BinTreeNode();
						newNode.parent = current;
						current.right = newNode;
						newNode.key = k;
						size++;
						return true;
					}
					current = current.right;
				} else {
					return false;
				}
			}
		}
		BinTreeNode newNode = new BinTreeNode();
		root = newNode;
		newNode.key = k;
		size++;
		return true;
	}
//
//
//


}
