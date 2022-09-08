package ase2016.bintree;

/**
* BinTreeContains3Bugs4x3x7 is an implementation of binary search trees with 3 bugs
* injected in lines 4 and 3 and 7 of method contains. First bug replaces:
* if (k < current.key) 
* with
* if (current.key < k) 
* Second bug replaces:
* while (current != null)
* with 
* while (root != null)
* Third bug replaces
* if (k > current.key)
* with 
* if (k > root.key)
* The bug to be inserted and the affected line were randomly chosen, from a set of
* real programming mistakes in binary tree implementations. This particular one appears in:
* https://www.quora.com/Why-am-I-getting-null-pointer-exception-when-I-try-to-implement-my-own-Tree-class-in-Java
*/

import ase2016.bintree.BinTreeNode;

public class BinTreeContains3Bugs4x3x7 {

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

    public BinTreeContains3Bugs4x3x7() {
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
    public boolean contains( int k ) {
        ase2016.bintree.BinTreeNode current = root; //mutGenLimit 0
        //@decreasing \reach(current, BinTreeNode, left+right).int_size();
        while (root != null) { //mutGenLimit 1
            if (current.key < k) { //mutGenLimit 1
                current = current.left; //mutGenLimit 0
            } else {
                if (k > root.key) { //mutGenLimit 1
                    current = current.right; //mutGenLimit 0
                } else {
                    return true; //mutGenLimit 0
                }
            }
        }
        return false; //mutGenLimit 0
    }

}
