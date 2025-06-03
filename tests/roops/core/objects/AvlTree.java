package roops.core.objects;

import java.lang.reflect.Method;

/**
 * A JML annotated version of class kiasan.examples.avltree.AvlTree
 */

/* @nullable_by_default@ */
public class AvlTree {

	public /*@nullable@*/ AvlNode root;

	public int size;

	public AvlTree() {
	}

	/*@
	@ invariant (\forall AvlNode x; \reach(this.root, AvlNode, left + right).has(x) == true;
	@    (\reach(x.left, AvlNode, right + left).has(x) == false) &&
	@    (\reach(x.right, AvlNode, right + left).has(x) == false));
	@
	@ invariant (\forall AvlNode x; \reach(this.root, AvlNode, left + right).has(x) == true;
	@    ((x.left == null && x.right == null) ==> x.height == 0));
	@
	@ invariant (\forall AvlNode x; \reach(this.root, AvlNode, left + right).has(x) == true;
	@    ((x.left == null && x.right != null) ==> (x.height == 1 && x.right.height == 0)));
	@
	@ invariant (\forall AvlNode x; \reach(this.root, AvlNode, left + right).has(x) == true;
	@	 ((x.left != null && x.right == null) ==> (x.height == 1 && x.left.height == 0)));
	@
	@ invariant (\forall AvlNode x; \reach(this.root, AvlNode, left + right).has(x) == true;
	@    ((x.left != null && x.right != null && x.left.height > x.right.height)
	@       ==> (x.height == x.left.height + 1 && x.left.height - x.right.height <= 1)));
	@
	@ invariant (\forall AvlNode x; \reach(this.root, AvlNode, left + right).has(x) == true;
	@   ((x.left != null && x.right != null && x.left.height <= x.right.height)
	@       ==> (x.height == x.right.height + 1 && x.right.height - x.left.height <= 1)));
	@
	@ invariant (\forall AvlNode x; \reach(this.root, AvlNode, left + right).has(x);
	@    (\forall AvlNode y; \reach(x.left, AvlNode, right + left).has(y) == true; y.element < x.element) &&
	@    (\forall AvlNode y; \reach(x.right, AvlNode, right + left).has(y) == true; x.element < y.element) );
	@
	@ invariant size == \reach(this.root, AvlNode, left + right).int_size();
	@*/

	/*@
	@ requires true;
	@ ensures (\exists AvlNode n; 
	@      \reach(root, AvlNode, left + right).has(n) == true; 
	@      n.element==x) ==> \result==x;
	@ ensures (\forall AvlNode n; 
	@      \reach(root, AvlNode, left + right).has(n) == true; 
	@      n.element!=x) ==> \result==-1;
	@ signals (Exception e) false;
	@*/
	public int find(final int x) {
		AvlNode n = root;
		while (n != null) {
			if (x < n.element) {
				n = n.left;
			} else {
				if (x > n.element) {
					n = n.right;
				} else {
					return n.element;
				}
			}
		}
		return -1;
	}

	/*@
	@ ensures \reach(root, AvlNode, left + right).int_size()==0 ==> \result==-1;
	@
	@ ensures \reach(root, AvlNode, left + right).int_size()>0 ==> 
	@                   (\exists AvlNode min_node; 
	@                      \reach(root, AvlNode, left + right).has(min_node);
	@                         min_node.element==\result && 
	@                         (\forall AvlNode node; \reach(root, AvlNode, left + right).has(node); \result <= node.element )
	@                   );
	@ signals (Exception e) false;
	@*/
	public int findMin() {
		AvlNode n = root;
		if (n == null) {
			return -1;
		}
		while (n.left != null) {
			n = n.left;
		}
		return n.element;
	}



	/*@ requires true;
	@ 
	@ ensures \reach(root, AvlNode, left + right).int_size()==0 ==> \result==-1;
	@ ensures \reach(root, AvlNode, left + right).int_size()>0 ==> 
	@               (\exists AvlNode max_node;
	@                  \reach(root, AvlNode, left + right).has(max_node);
	@                  max_node.element==\result &&
    @                     (\forall AvlNode node; \reach(root, AvlNode, left + right).has(node) ;
    @                        \result >= node.element )
	@               ); 
	@ signals (Exception e) false;
	@*/
	public int findMax() {
		AvlNode n = root;
		if (n == null) {
			return -1;
		}
		//@ decreasing n.height;
		while (n.right != null) {
			n = n.right;
		}
		return n.element;
	}

	// -------------------- insert -------------------//


	/*@
	@ requires (\forall AvlNode n; \reach(this.root, AvlNode, left+right).has(n) == true; n.element != x);
	@ ensures (\exists AvlNode n; \reach(this.root, AvlNode, left+right).has(n) == true; n.element == x);
	@ ensures \reach(this.root, AvlNode, left+right).int_size() == \old(\reach(this.root, AvlNode, left+right)).int_size() + 1;
 	@ signals (Exception e) false;
 	@*/
	public void insert(int x) {
		AvlNode n = new AvlNode();
		n.element = x;
		if (root == null){
			root = n;
		} else {
			root = privateInsert(root, n);
		}
		size++;
	}




	private AvlNode privateInsert(/*@nullable@*/AvlNode n, AvlNode aux) {
		if (n == null){
			return aux;
		} else {
			if (aux.element < n.element) {
				n.left = privateInsert(n.left, aux);
				if (AvlTree.height(n.left) - AvlTree.height(n.right) == 2) {
					if (aux.element < n.left.element) {
						n = AvlTree.rotateWithLeftChild(n);
					} else {
						n = AvlTree.doubleWithLeftChild(n);
					}
				}
			} else {
				if (aux.element > n.element) {
					n.right = privateInsert(n.right, aux);
					if (AvlTree.height(n.right) - AvlTree.height(n.left) == 2) {
						if (aux.element > n.right.element) {
							n = AvlTree.rotateWithRightChild(n);
						} else {
							n = AvlTree.doubleWithRightChild(n);
						}
					}
				}
			}
		}

		n.height = AvlTree.max(AvlTree.height(n.left), AvlTree.height(n.right)) + 1;
		return n;
	}

	private static AvlNode doubleWithLeftChild( AvlNode k3) {
		k3.left = AvlTree.rotateWithRightChild(k3.left);
		return AvlTree.rotateWithLeftChild(k3);
	}

	private static AvlNode doubleWithRightChild( AvlNode k1) {
		k1.right = AvlTree.rotateWithLeftChild(k1.right);
		return AvlTree.rotateWithRightChild(k1);
	}

	//@ assignable \nothing;
	private static int height( AvlNode t) {
		return t == null ? -1 : t.height;
	}

	//@ assignable \nothing;
	private static int max( int lhs,  int rhs) {
		return lhs > rhs ? lhs : rhs;
	}

	private static AvlNode rotateWithLeftChild( AvlNode y) {
		AvlNode k1 = y.left;
		y.left = k1.right;
		k1.right = y;
		y.height = AvlTree.max(AvlTree.height(y.left),
				AvlTree.height(y.right)) + 1;
		k1.height = AvlTree.max(AvlTree.height(k1.left), y.height) + 1;
		return k1;
	}

	private static AvlNode rotateWithRightChild( AvlNode k1) {
		AvlNode k2 = k1.right;
		k1.right = k2.left;
		k2.left = k1;
		k1.height = AvlTree.max(AvlTree.height(k1.left),
				AvlTree.height(k1.right)) + 1;
		k2.height = AvlTree.max(AvlTree.height(k2.right), k1.height) + 1;
		return k2;
	}


	/*@
	 @ requires true;
	 @ ensures \result == false; 
	 @*/
	public boolean generateTreeInstance() {
		if (size == 20)
			return true;
		else
			return false;
	}


	public static void main(String[] args){
		roops.core.objects.AvlTree avlTree0 = new roops.core.objects.AvlTree();
		int int1 = avlTree0.size;
		int int2 = avlTree0.findMin();
		boolean boolean3 = avlTree0.generateTreeInstance();
		int int5 = avlTree0.find((int) (short) 0);
		avlTree0.insert((int) (short) 10);
		int int8 = avlTree0.findMin();
		int int9 = avlTree0.findMax();
		// during test generation this statement threw an exception of type java.lang.NullPointerException in error
		avlTree0.insert((-1));
	}

}