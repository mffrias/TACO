package ar.edu.taco.jfsl;

/**
 * Case study of Red-Black trees with integer keys.
 * Stripped down and simplified.
 *
 * @author Kuat Yessenov
 */

/**
 * A tree with integer keys.
 *
 * @author Emina Torlak
 */
/**
  * @SpecField nodes : set Node from this.root, this.nodes.left, this.nodes.right, this.nodes.parent, this.nodes.color
  *                | this.nodes = this.root.*(left @+ right) @- null ;
  */
/**
  * @Invariant
  *      ( this.root.parent in null ) && 
  *      ( all x : IntTree | x!=this => no (x.nodes & this.nodes) );
  */
public final class IntTree {

	static /*final*/ boolean BLACK = true;
	private static /*final*/ boolean RED = false;

	private/*@ nullable @*/Node root;

	/*
	 * Creates an empty IntTree.
	 */

	/*
	 * @Ensures no this.nodes ;
	 * @Modifies this.nodes ;
	 */
	public IntTree() {
		root = null;
	}

	/*
	 * Discards all elements from this tree.
	 */
	/**
	 * @Ensures no this.nodes ;
	 * @Modifies IntTree.nodes, this.root ;
	 */
	public final void clear() {
		root = null;
	}

	/*
	 * Returns the node with the given key, or null no such node exists. return
	 */
	/*
	 * @Ensures return @- null = (this.nodes & (Node@key.k)) ;
	 */
	/**
	 * @Ensures ( return == null => (no n: Node | n in this.nodes && n.key==k) ) &&
	 *          ( return != null => (return in this.nodes && return.key==k ) ) ;
	 */
	public final/*@ pure @*/ Node search(int k) {
		Node node = root;
		while (node != null) {
			if (node.key == k)
				return node;
			else if (node.key > k)
				node = node.left;
			else
				node = node.right;
		}
		return node;
	}

	/*
	 * Returns the node whose key is the ceiling of k in this tree, or
	 * null if no such node exists.
	 */
	/*
	 * @Ensures return @- null = 
	 *       {n : this.nodes | n.key >= k && (no m in this.nodes | m.key >= k && m.key < n.key)} ;
	 */
	public final/*@ pure @*/ Node searchGTE(int k) {
		if (root == null)
			return null;
		Node c = root;
		boolean always_true = true;
		while (always_true) {
			if (c.key == k) {
				return c;
			} else if (c.key > k) {
				if (c.left != null)
					c = c.left;
				else
					return c;
			} else {
				if (c.right != null)
					c = c.right;
				else
					return successor(c);
			}
		}
		return null;
	}

	/*
	 * Returns the node whose key is the floor of k in this tree, or
	 * null if no such node exists. return {n: this.nodes | n.key <= k && no n':
	 * this.nodes - n | n'.key <= k && n'.key > n.key }
	 */
	/*
	 * @Ensures (some node in this.nodes | node.key <= k) => return != null &&
	 *               return != null => return.key <= k &&
	 *               return != null => (no node in this.nodes | node.key <= k && node.key > return.key) &&
	 *               return in this.nodes @+ null ;
	 */
	public final/*@ pure @*/ Node searchLTE(int k) {
		if (root == null)
			return null;
		Node f = root;
		boolean always_true=true;
		while (always_true) {
			if (f.key == k)
				return f;
			else if (f.key > k) {
				if (f.left != null)
					f = f.left;
				else
					return predecessor(f);
			} else {
				if (f.right != null)
					f = f.right;
				else
					return f;
			}
		}
		return null;
	}

	/*
	 * Implementation of the tree-predecessor algorithm from CLR. Returns the
	 * given node's predecessor, if it exists. Otherwise returns null. return
	 * the given node's predecessor throws NullPointerException - node = null
	 */
	/* 
	 * @NormalBehavior
	 *   @Requires (some x:this.nodes | x.key < node.key) 
	 *              && node in this.nodes ;
	 *   @Ensures return in {x in this.nodes | x.key < node.key}
     *              && no {x : this.nodes | x.key < node.key && x.key > return.key} ;
     *           
     * @NormalBehavior 
     *   @Requires no x in this.nodes | x.key < node.key 
     *             && node in this.nodes ;
     *   @Ensures return = null ;
     *   
     * @ExceptionalBehavior
	 *    @Requires node = null ;
	 *    @Ensures  throw in NullPointerException ;
     *
	 */
	public final/*@ pure @*/Node predecessor(Node node) {
		if (node.left != null) {
			return max(node.left);
		} else {
			Node n = node;
			Node ancestor = n.parent;
			while (ancestor != null && n == ancestor.left) {
				n = ancestor;
				ancestor = ancestor.parent;
			}
			return ancestor;
		}
	}

	/*
	 * Implementation of the tree-successor algorithm from CLR. Returns the
	 * given node's successor, if it exists. Otherwise returns null.
	 */
	/*
	 * @Requires node in this.nodes ;
	 * @Ensures
	 *       (some x:this.nodes | x.key > node.key) => return != null &&
	 *       return != null => return.key > node.key && (no x:this.nodes | x.key > node.key && x.key < return.key) &&
	 *       return in this.nodes @+ null ;
	 * @Throws NullPointerException : node = null ;
	 */
	public final/*@ pure @*/ Node successor(Node node) {
		if (node.right != null) {
			return min(node.right);
		} else {
			Node n = node;
			Node ancestor = n.parent;
			while (ancestor != null && n == ancestor.right) {
				n = ancestor;
				ancestor = ancestor.parent;
			}
			return ancestor;
		}
	}

	/*
	 * Returns the node with the smallest key. return key.(min(this.nodes.key))
	 */
	/*
	 * @Returns some this.nodes ? {x in this.nodes | no y in this.nodes | y.key < x.key} : null ;
	 */
	public final/*@ pure @*/ Node minAll() {
		Node start = root;
		if (start != null) {
			while (start.left != null) {
				start = start.left;
			}
		}
		return start;
	}

	/*
	 * Returns the node with the largest key. return key.(max(this.nodes.key))
	 */
	/*
	 * @Returns some this.nodes ? {x in this.nodes | no y in this.nodes | y.key > x.key} : null ;
	 */
	public final/*@ pure @*/ Node maxAll() {
		// XXX: added a case for root = null since max() behaviour is unspecified for null values of root
		Node result;
		if (this.root == null )
			result = null;
		else
			result = max(root);
		return result;
	}

	/*
	 * Returns the leftmost node in the subtree rooted at start. The behavior of
	 * this method is unspecified if the given node is not in this tree.
	 * requires node in this.nodes return {n: start.*left | no n.left }
	 */
	/*
	 * @Requires start in this.nodes ;
	 * @Returns {n : start.^left + start | n.left = null} ;
	 * @Ensures no x in start.^(left @+ right) @+ start @- null | x.key < return.key ;
	 */
	private final /*@ pure @*/ Node min(Node start) {
		if (start != null) {
			while (start.left != null) {
				start = start.left;
			}
		}
		return start;
	}

	/*
	 * Returns the rightmost in the subtree rooted at start. The behavior of
	 * this method is unspecified if the given node is not in this tree.
	 * requires node in this.nodes return {n: start.*left | no n.right }
	 */
	/*
	 * @Requires start in this.nodes ;
	 * @Returns {n : start.^right + start | n.right = null} ;
	 * @Ensures no x in start.^(left @+ right) @+ start @- null | x.key > return.key ;
	 */
	private final /*@ pure @*/ Node max(Node start) {
		if (start != null) {
			while (start.right != null) {
				start = start.right;
			}
		}
		return start;
	}

	/*
	 * Replaces the old node, o, with the given new node, n, in this tree.
	 *
	 */
	private/*@ helper @*/final void replace(Node o, Node n) {
		n.color = o.color;
		n.parent = o.parent;
		n.left = o.left;
		n.right = o.right;
		if (o.left != null) {
			o.left.parent = n;
		}
		if (o.right != null) {
			o.right.parent = n;
		}
		if (o.parent == null) {
			root = n;
		} else if (o == o.parent.left) {
			o.parent.left = n;
		} else {
			o.parent.right = n;
		}
		o.parent = o.left = o.right = null;
	}

	/*
	 * Implementation of the CLR insertion algorithm.
	 *  
	 */
	/*
	 * @Requires z.key !in this.nodes.key &&
	 *        z.(parent @+ left @+ right) = null && 
	 *        no root.z ;
	 * @Ensures this.nodes = @old(this.nodes @+ z) ;
	 * @Modifies this.nodes, z.* ;
	 */
	public final void insert(Node z) {
		Node yy = null;
		for (Node x = root; x != null;) {
			yy = x;
			if (x.key > z.key)
				x = x.left;
			else
				x = x.right;
		}

		z.parent = yy;
		z.left = z.right = null;
		if (yy == null) {
			root = z;
		} else {
			z.color = RED;
			if (yy.key > z.key) {
				yy.left = z;
			} else {
				yy.right = z;
			}

			// insert fix up
			while (z != null && z != root && z.parent.color == RED) {
				Node n = (z == null ? null : z.parent);
				Node n5 = (n == null ? null : n.parent);
				if ((z == null ? null : z.parent) == (n5 == null ? null
						: n5.left)) {
					Node n1 = (z == null ? null : z.parent);
					Node n6 = (n1 == null ? null : n1.parent);
					Node y = (n6 == null ? null : n6.right);
					if ((y == null ? BLACK : y.color) == RED) {
						Node n4 = (z == null ? null : z.parent);
						if (n4 != null)
							n4.color = BLACK;
						if (y != null)
							y.color = BLACK;
						Node n2 = (z == null ? null : z.parent);
						Node n7 = (n2 == null ? null : n2.parent);
						if (n7 != null)
							n7.color = RED;
						Node n3 = (z == null ? null : z.parent);
						z = n3 == null ? null : n3.parent;
					} else {
						Node n7 = (z == null ? null : z.parent);
						if (z == (n7 == null ? null : n7.right)) {
							z = z == null ? null : z.parent;
							rotateLeft(z);
						}
						Node n8 = (z == null ? null : z.parent);
						if (n8 != null)
							n8.color = BLACK;
						Node n2 = (z == null ? null : z.parent);
						Node n9 = (n2 == null ? null : n2.parent);
						if (n9 != null)
							n9.color = RED;
						Node n3 = (z == null ? null : z.parent);
						if ((n3 == null ? null : n3.parent) != null) {
							Node n4 = (z == null ? null : z.parent);
							rotateRight((n4 == null ? null : n4.parent));
						}
					}
				} else {
					Node n1 = (z == null ? null : z.parent);
					Node n6 = (n1 == null ? null : n1.parent);
					Node y = (n6 == null ? null : n6.left);
					if ((y == null ? BLACK : y.color) == RED) {
						Node n4 = (z == null ? null : z.parent);
						if (n4 != null)
							n4.color = BLACK;
						if (y != null)
							y.color = BLACK;
						Node n2 = (z == null ? null : z.parent);
						Node n7 = (n2 == null ? null : n2.parent);
						if (n7 != null)
							n7.color = RED;
						Node n3 = (z == null ? null : z.parent);
						z = n3 == null ? null : n3.parent;
					} else {
						Node n7 = (z == null ? null : z.parent);
						if (z == (n7 == null ? null : n7.left)) {
							z = z == null ? null : z.parent;
							rotateRight(z);
						}
						Node n8 = (z == null ? null : z.parent);
						if (n8 != null)
							n8.color = BLACK;
						Node n2 = (z == null ? null : z.parent);
						Node n9 = (n2 == null ? null : n2.parent);
						if (n9 != null)
							n9.color = RED;
						Node n3 = (z == null ? null : z.parent);
						if ((n3 == null ? null : n3.parent) != null) {
							Node n4 = (z == null ? null : z.parent);
							rotateLeft((n4 == null ? null : n4.parent));
						}
					}
				}
			}
			root.color = BLACK;
		}
	}

	/*
	 * A slightly modified implementation of the CLR deletion algorithm.
	 * requires z in this.nodes effects this.nodes' = this.nodes - z
	 */
	/*
	 * @Requires z in this.nodes  ;
	 * @Effects this.nodes = @old(this.nodes @- z) ;
	 * @Modifies this.nodes ;
	 */
	public final void delete(Node z) {
		Node y = (z.left == null || z.right == null ? z : successor(z));
		Node x = (y.left != null ? y.left : y.right);

		Node yparent = y.parent;
		Node n = y.parent;
		final boolean yleft = (y == (n == null ? null : n.left));
		final boolean ycolor = y.color;

		if (x != null) {
			x.parent = yparent;
		}

		if (yparent == null) {
			root = x;
		} else if (yleft) {
			yparent.left = x;
		} else {
			yparent.right = x;
		}

		if (y != z) {
			replace(z, y);
		}

		if (ycolor == BLACK) {
			if (x != null) {
				deleteFixUp(x);
			} else if (yparent != null) { // z is not the only node

				if (z == yparent)
					yparent = y; // y, z's successor, is z's right child
				z.color = BLACK;
				z.left = z.right = null;
				z.parent = yparent;
				if (yleft) {
					yparent.left = z;
				} else {
					yparent.right = z;
				}

				deleteFixUp(z);
				if (z == z.parent.left) {
					z.parent.left = null;
				} else {
					z.parent.right = null;
				}
			}
		}

		z.left = z.right = z.parent = null; // cut z out of the tree by nulling
		// out its pointers
	}

	/*
	 * From CLR.
	 */
	private/*@ helper @*/void deleteFixUp(Node x) {
		while (x != root && (x == null ? BLACK : x.color) == BLACK) {
			Node n = (x == null ? null : x.parent);
			if (x == (n == null ? null : n.left)) {
				Node n1 = (x == null ? null : x.parent);
				Node sib = (n1 == null ? null : n1.right);

				if ((sib == null ? BLACK : sib.color) == RED) {
					if (sib != null)
						sib.color = BLACK;
					Node n5 = (x == null ? null : x.parent);
					if (n5 != null)
						n5.color = RED;
					rotateLeft((x == null ? null : x.parent));
					Node n2 = (x == null ? null : x.parent);
					sib = n2 == null ? null : n2.right;
				}
				Node n3 = (sib == null ? null : sib.left);
				Node n4 = (sib == null ? null : sib.right);

				if ((n3 == null ? BLACK : n3.color) == BLACK
						&& (n4 == null ? BLACK : n4.color) == BLACK) {
					if (sib != null)
						sib.color = RED;
					x = x == null ? null : x.parent;
				} else {
					Node n5 = (sib == null ? null : sib.right);
					if ((n5 == null ? BLACK : n5.color) == BLACK) {
						Node n6 = (sib == null ? null : sib.left);
						if (n6 != null)
							n6.color = BLACK;
						if (sib != null)
							sib.color = RED;
						rotateRight(sib);
						Node n2 = (x == null ? null : x.parent);
						sib = n2 == null ? null : n2.right;
					}
					Node n2 = (x == null ? null : x.parent);
					if (sib != null)
						sib.color = (n2 == null ? BLACK : n2.color);
					Node n6 = (x == null ? null : x.parent);
					if (n6 != null)
						n6.color = BLACK;
					Node n7 = (sib == null ? null : sib.right);
					if (n7 != null)
						n7.color = BLACK;
					rotateLeft((x == null ? null : x.parent));
					x = root;
				}
			} else { // symmetric
				Node n1 = (x == null ? null : x.parent);
				Node sib = (n1 == null ? null : n1.left);

				if ((sib == null ? BLACK : sib.color) == RED) {
					if (sib != null)
						sib.color = BLACK;
					Node n5 = (x == null ? null : x.parent);
					if (n5 != null)
						n5.color = RED;
					rotateRight((x == null ? null : x.parent));
					Node n2 = (x == null ? null : x.parent);
					sib = n2 == null ? null : n2.left;
				}
				Node n3 = (sib == null ? null : sib.right);
				Node n4 = (sib == null ? null : sib.left);

				if ((n3 == null ? BLACK : n3.color) == BLACK
						&& (n4 == null ? BLACK : n4.color) == BLACK) {
					if (sib != null)
						sib.color = RED;
					x = x == null ? null : x.parent;
				} else {
					Node n5 = (sib == null ? null : sib.left);
					if ((n5 == null ? BLACK : n5.color) == BLACK) {
						Node n6 = (sib == null ? null : sib.right);
						if (n6 != null)
							n6.color = BLACK;
						if (sib != null)
							sib.color = RED;
						rotateLeft(sib);
						Node n2 = (x == null ? null : x.parent);
						sib = n2 == null ? null : n2.left;
					}
					Node n2 = (x == null ? null : x.parent);
					if (sib != null)
						sib.color = (n2 == null ? BLACK : n2.color);
					Node n6 = (x == null ? null : x.parent);
					if (n6 != null)
						n6.color = BLACK;
					Node n7 = (sib == null ? null : sib.left);
					if (n7 != null)
						n7.color = BLACK;
					rotateRight((x == null ? null : x.parent));
					x = root;
				}
			}
		}

		if (x != null)
			x.color = BLACK;

	}

	private/*@ helper @*/void rotateLeft(Node x) {
		Node y = x.right;
		x.right = y.left;
		if (y.left != null)
			y.left.parent = x;
		y.parent = x.parent;
		if (x.parent == null)
			root = y;
		else if (x.parent.left == x)
			x.parent.left = y;
		else
			x.parent.right = y;
		y.left = x;
		x.parent = y;
	}

	private/*@ helper @*/void rotateRight(Node x) {
		Node y = x.left;
		x.left = y.right;
		if (y.right != null)
			y.right.parent = x;
		y.parent = x.parent;
		if (x.parent == null)
			root = y;
		else if (x.parent.right == x)
			x.parent.right = y;
		else
			x.parent.left = y;
		y.right = x;
		x.parent = y;
	}
}