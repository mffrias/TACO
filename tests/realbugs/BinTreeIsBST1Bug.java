package realbugs;



public class BinTreeIsBST1Bug {

    /*@
    @ invariant (\forall BinTreeNode n;
    @     \reach(root, BinTreeNode, left + right).has(n) == true;
    @     \reach(n.right, BinTreeNode, right + left).has(n) == false &&
    @     \reach(n.left, BinTreeNode, left + right).has(n) == false);
    @   
    @ invariant size == \reach(root, BinTreeNode, left + right).int_size();
    @
    @ invariant (\forall BinTreeNode n;
    @	  \reach(root, BinTreeNode, left + right).has(n) == true;
    @	  (n.left != null ==> n.left.parent == n) && (n.right != null ==> n.right.parent == n));
    @
    @ invariant root != null ==> root.parent == null;
    @*/
    public /*@nullable@*/BinTreeNode root;

    public int size;

    public BinTreeIsBST1Bug() { }

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
        BinTreeNode current = root; //mutGenLimit 0
        //@decreasing \reach(current, BinTreeNode, left+right).int_size();
        while (current != null) {
            if (k < current.key) {
                current = current.left; //mutGenLimit 0
            } else {
                if (k > current.key) {
                    current = current.right; //mutGenLimit 0
                } else {
                    return true; //mutGenLimit 0
                }
            }
        }
        return false; //mutGenLimit 0
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
        BinTreeNode y = null; //mutGenLimit 0
        BinTreeNode x = root; //mutGenLimit 0
        //@decreasing \reach(x, BinTreeNode, left+right).int_size();
        while (x != null) {
            y = x; //mutGenLimit 0
            if (k < x.key) {
                x = x.left; //mutGenLimit 0
            } else {
                if (k > x.key) {
                    x = x.right; //mutGenLimit 0
                } else {
                    return false; //mutGenLimit 0
                }
            }
        }
        x = new BinTreeNode(); //mutGenLimit 0
        x.key = k; //mutGenLimit 0
        if (y == null) {
            root = x; //mutGenLimit 0
        } else {
            if (k < y.key) {
                y.left = x; //mutGenLimit 0
            } else {
                y.right = x; //mutGenLimit 0
            }
        }
        x.parent = y; //mutGenLimit 0
        size += 1; //mutGenLimit 0
        return true; //mutGenLimit 0
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
        BinTreeNode node = root; //mutGenLimit 0
        while (node != null && node.key != element) {
            if (element < node.key) {
                node = node.left; //mutGenLimit 0
            } else {
                if (element > node.key) {
                    node = node.right; //mutGenLimit 0
                }
            }
        }
        if (node == null) {
            return false; //mutGenLimit 0
        } else {
            if (node.left != null && node.right != null) {
                BinTreeNode predecessor = node.left; //mutGenLimit 0
                if (predecessor != null) {
                    while (predecessor.right != null) {
                        predecessor = predecessor.right; //mutGenLimit 0
                    }
                }
                node.key = predecessor.key; //mutGenLimit 0
                node = predecessor; //mutGenLimit 0
            }
        }
        BinTreeNode pullUp; //mutGenLimit 0
        if (node.left == null) {
            pullUp = node.right; //mutGenLimit 0
        } else {
            pullUp = node.left; //mutGenLimit 0
        }
        if (node == root) {
            root = pullUp; //mutGenLimit 0
            if (pullUp != null) {
                pullUp.parent = null; //mutGenLimit 0
            }
        } else {
            if (node.parent.left == node) {
                node.parent.left = pullUp; //mutGenLimit 0
                if (pullUp != null) {
                    pullUp.parent = node.parent; //mutGenLimit 0
                }
            } else {
                node.parent.right = pullUp; //mutGenLimit 0
                if (pullUp != null) {
                    pullUp.parent = node.parent; //mutGenLimit 0
                }
            }
        }
        size--; //mutGenLimit 0
        return true; //mutGenLimit 0
    }
    
    /*
     * The following method is taken and adapted from 
     * https://www.quora.com/Why-am-I-getting-null-pointer-exception-when-I-try-to-implement-my-own-Tree-class-in-Java
     * There is a bug in the conditions for iteratively inserting a value in a binary
     * search tree.
     * Original example has been adapted to take into account size and parent fields.
     */

    /*@
    @ requires true;
    @
    @ ensures (\exists BinTreeNode n;
    @		\old(\reach(root, BinTreeNode, left + right)).has(n) == true;
    @  	n.key == data) ==> size == \old(size);
    @
    @	ensures (\forall BinTreeNode n;
    @		\old(\reach(root, BinTreeNode, left + right)).has(n) == true;
    @  	n.key != data) ==> size == \old(size) + 1;
    @
    @ ensures (\exists BinTreeNode n;
    @     \reach(root, BinTreeNode, left + right).has(n) == true;
    @		n.key == data);
    @
    @ signals (RuntimeException e) false;
    @*/
    public void insertElem( int data ) {
        BinTreeNode toInsert = new BinTreeNode(); //mutGenLimit 0
        toInsert.key = data; //mutGenLimit 0
        if (root == null) {
            root = toInsert; //mutGenLimit 0
            size = 1; //mutGenLimit 0
        } else {
            boolean inserted = false; //mutGenLimit 0
            BinTreeNode current = root; //mutGenLimit 0
            BinTreeNode parent = null; //mutGenLimit 0
            while (current != null && !inserted) { //mutGenLimit 0
                parent = current; //mutGenLimit 0
                if (current.key > data) { //mutGenLimit 1
                    current = current.right; //mutGenLimit 0
                    if (current == null) { //mutGenLimit 0
                        parent.right = toInsert; //mutGenLimit 0
                        toInsert.parent = parent; //mutGenLimit 0
                        inserted = true; //mutGenLimit 0
                        size = size+1; //mutGenLimit 0
                    }
                } else {
                    if (data < current.key) { //mutGenLimit 0
                        current = current.left; //mutGenLimit 0
                        if (current == null) {
                            parent.left = toInsert; //mutGenLimit 0
                            toInsert.parent = parent; //mutGenLimit 0
                            inserted = true; //mutGenLimit 0
                            size = size+1; //mutGenLimit 0
                        }
                    }
                    else {
                    	inserted = true; //mutGenLimit 0
                    }
                }
            }
        }
    }
    
    /*@
    @ requires true;
    @
    @ ensures (\result == true) <==> (\forall BinTreeNode n;
    @     \reach(root, BinTreeNode, left + right).has(n) == true;
    @     (\forall BinTreeNode m;
    @     \reach(n.left, BinTreeNode, left + right).has(m) == true;
    @     m.key <= n.key) &&
    @     (\forall BinTreeNode m;
    @     \reach(n.right, BinTreeNode, left + right).has(m) == true;
    @     m.key > n.key));
    @
    @ signals (RuntimeException e) false;
    @*/ 
    public boolean isBST() {
    	return (root==null || isBSTNode(root, null) != null);
    }
        
    private BinTreeNode isBSTNode(BinTreeNode node, BinTreeNode prev) {
    	if (root != null) {
    		BinTreeNode newPrev = isBSTNode(root.left, prev);
    		if (newPrev==null)
    			return null;
    		if (newPrev != null && root.key <= newPrev.key)
    			return null;
    		return isBSTNode(root.right, root);
    	}
    	else {
        	return prev;    		
    	}
    }


}
