package ase2016.bintree;

/**
* BinTreeRemove4Bugs10x4x8x3 is an implementation of binary search trees with four bugs
* injected in lines 40 and 4 and 8 and 3 of method remove. First bug replaces:
* node.parent.left = pullUp;
* with
* node.parent.right = pullUp; 
* Second bug replaces:
* if (element < node.key)
* with
* if (node.key < element)
* Third bug replaces:
* node = node.right;
* with 
* node = node.left;
* Fourth bug replaces:
* while (node != null && node.key != element)
* with 
* while (root != null && node.key != element)
* The bug to be inserted and the affected line were randomly chosen, from a set of
* real programming mistakes in binary tree implementations. These particular ones appear in:
* http://stackoverflow.com/questions/33975618/problems-with-binary-tree-node-removal-java
* https://www.quora.com/Why-am-I-getting-null-pointer-exception-when-I-try-to-implement-my-own-Tree-class-in-Java
* http://www.geeksforgeeks.org/write-an-efficient-c-function-to-convert-a-tree-into-its-mirror-tree/
* http://www.geeksforgeeks.org/a-program-to-check-if-a-binary-tree-is-bst-or-not/
*/

import ase2016.bintree.BinTreeNode;


public class BinTreeRemove4Bugs40x4x8x3 {

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

    public BinTreeRemove4Bugs40x4x8x3() {
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
    public boolean remove( int element ) { //mutGenLimit 0
        ase2016.bintree.BinTreeNode node = root; //mutGenLimit 0
        //@decreasing \reach(node, BinTreeNode, left+right).int_size();
        while (root != null && node.key != element) { //mutGenLimit 1
            if (node.key < element) { //mutGenLimit 1
                node = node.left; //mutGenLimit 0
            } else {
                if (element > node.key) { //mutGenLimit 0
                    node = node.left; //mutGenLimit 1
                }
            }
        }
        if (node == null) { //mutGenLimit 0
            return false; //mutGenLimit 0
        } else {
            if (node.left != null && node.right != null) { //mutGenLimit 0
                ase2016.bintree.BinTreeNode predecessor = node.left; //mutGenLimit 0
                if (predecessor != null) { //mutGenLimit 0
                	//@decreasing \reach(predecessor, BinTreeNode, right).int_size(); 
                    while (predecessor.right != null) { //mutGenLimit 0
                        predecessor = predecessor.right; //mutGenLimit 0
                    }
                }
                node.key = predecessor.key; //mutGenLimit 0
                node = predecessor; //mutGenLimit 0
            }
        }
        ase2016.bintree.BinTreeNode pullUp; //mutGenLimit 0
        if (node.left == null) { //mutGenLimit 0
            pullUp = node.right; //mutGenLimit 0
        } else {
            pullUp = node.left; //mutGenLimit 0
        }
        if (node == root) { //mutGenLimit 0
            root = pullUp; //mutGenLimit 0
            if (pullUp != null) { //mutGenLimit 0
                pullUp.parent = null; //mutGenLimit 0
            }
        } else {
            if (node.parent.left == node) { //mutGenLimit 0
                node.parent.right = pullUp; //mutGenLimit 1
                if (pullUp != null) { //mutGenLimit 0
                    pullUp.parent = node.parent; //mutGenLimit 0
                }
            } else {
                node.parent.right = pullUp; //mutGenLimit 0
                if (pullUp != null) { //mutGenLimit 0
                    pullUp.parent = node.parent; //mutGenLimit 0
                }
            }
        }
        size = size - 1; //mutGenLimit 0
        return true; //mutGenLimit 0
    }

}
