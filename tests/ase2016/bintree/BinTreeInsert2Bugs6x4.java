package ase2016.bintree;

/**
* BinTreeInsert1Bug6 is an implementation of binary search trees with two bugs
* injected in lines 6 and 4 of method insert. First bug replaces:
* if (x.key > k) 
* with
* if (x.key < k) 
* Second bug replaces:
* while (x != null)
* with
* while (root != null)
* The bugs to be inserted and the affected lines were randomly chosen, from a set of
* real programming mistakes in binary tree implementations. These particular ones appear in:
* https://www.quora.com/Why-am-I-getting-null-pointer-exception-when-I-try-to-implement-my-own-Tree-class-in-Java
* http://www.geeksforgeeks.org/a-program-to-check-if-a-binary-tree-is-bst-or-not/
*/

import ase2016.bintree.BinTreeNode;


public class BinTreeInsert2Bugs6x4 {

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
    public /*@nullable@*/ase2016.bintree.BinTreeNode root;

    public int size;

    public BinTreeInsert2Bugs6x4() {
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
    public boolean insert( int k ) {
        ase2016.bintree.BinTreeNode y = null; //mutGenLimit 0
        ase2016.bintree.BinTreeNode x = this.root; //mutGenLimit 0
        //@decreasing \reach(x, BinTreeNode, left+right).int_size();
        while (root != null) { //mutGenLimit 1
            y = x; //mutGenLimit 0
            if (x.key < k) { //mutGenLimit 1
                x = x.left; //mutGenLimit 0
            } else {
                if (k > x.key) { //mutGenLimit 0
                    x = x.right; //mutGenLimit 0
                } else {
                    return false; //mutGenLimit 0
                }
            }
        }
        x = new ase2016.bintree.BinTreeNode(); //mutGenLimit 0
        x.key = k; //mutGenLimit 0
        if (y == null) { //mutGenLimit 0
            root = x; //mutGenLimit 0
        } else {
            if (k < y.key) { //mutGenLimit 0
                y.left = x; //mutGenLimit 0
            } else {
                y.right = x; //mutGenLimit 0
            }
        }
        x.parent = y; //mutGenLimit 0
        size += 1; //mutGenLimit 0
        return true; //mutGenLimit 0
    }

}
