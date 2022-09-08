package pldi.treeset;

import pldi.treeset.TreeSetEntry;


public class TreeSet {

/*@
  @ invariant this.RED == false; 
  @	invariant this.BLACK == true;
  @	invariant this.root.parent == null;
  @ invariant this.root != null ==> this.root.color == this.BLACK; 
  @ invariant (\forall TreeSetEntry n; \reach(root, TreeSetEntry, left + right).has(n);  
  @				( n.left != null ==> n.left.parent == n ) &&
  @				( n.right != null ==> n.right.parent == n ) &&
  @				( n.parent != null ==> (n == n.parent.left || n == n.parent.right) ) &&
  @				( \reach(n.parent, TreeSetEntry, parent).has(n) == false ) &&
  @				( \forall TreeSetEntry x; \reach(n.left, TreeSetEntry, left + right).has(x); n.key > x.key ) &&
  @				( \forall TreeSetEntry x; \reach(n.right, TreeSetEntry, left + right).has(x); n.key < x.key ) &&
  @				( (n.color == this.RED && n.parent != null) ==> n.parent.color == this.BLACK ) && 
  @				( ( n.left==null && n.right==null ) ==> ( n.blackHeight==1 ) ) &&
  @				( n.left!=null && n.right==null ==> ( 
  @				      ( n.left.color == this.RED ) && 
  @				      ( n.left.blackHeight == 1 ) && 
  @				      ( n.blackHeight == 1 )  
  @				)) &&
  @				( n.left==null && n.right!=null ==>  ( 
  @				      ( n.right.color == this.RED ) && 
  @				      ( n.right.blackHeight == 1 ) && 
  @				      ( n.blackHeight == 1 ) 
  @				)) && 
  @				( n.left!=null && n.right!=null && n.left.color==this.RED && n.right.color==this.RED ==> ( 
  @				        ( n.left.blackHeight == n.right.blackHeight ) && 
  @				        ( n.blackHeight == n.left.blackHeight ) 
  @				)) && 
  @				( n.left!=null && n.right!=null && n.left.color==this.BLACK && n.right.color==this.BLACK ==> ( 
  @				        ( n.left.blackHeight==n.right.blackHeight ) && 
  @				        ( n.blackHeight==n.left.blackHeight + 1 )  
  @				)) && 
  @				( n.left!=null && n.right!=null && n.left.color==this.RED && n.right.color==this.BLACK ==> ( 
  @				        ( n.left.blackHeight==n.right.blackHeight + 1 ) && 
  @				        ( n.blackHeight==n.left.blackHeight  )  
  @				)) && 
  @				( n.left!=null && n.right!=null && n.left.color==this.BLACK && n.right.color==this.RED ==> ( 
  @				        ( n.right.blackHeight==n.left.blackHeight + 1 ) && 
  @				        ( n.blackHeight==n.right.blackHeight  )  
  @				)) 
  @			) ; 
  @*/


	public /*@ nullable @*/ TreeSetEntry root = null;

	public int size = 0;

	public int modCount = 0;

	boolean RED = false;
	boolean BLACK = true;

	public TreeSet() {
	}


	/*@
	  @ requires true;
	  @ 
	  @ ensures \result == true <==> (\exists TreeSetEntry n; \reach(root, TreeSetEntry, left + right).has(n); n.key == aKey);
	  @
	  @ ensures (\forall TreeSetEntry n; 
	  @		\reach(root, TreeSetEntry, left+right).has(n); 
	  @		\old(\reach(root, TreeSetEntry, left+right)).has(n));
	  @
	  @ ensures (\forall TreeSetEntry n;  
	  @		\old(\reach(root, TreeSetEntry, left+right)).has(n);
	  @		\reach(root, TreeSetEntry, left+right).has(n));
	  @
	  @ signals (RuntimeException e) false;
     @*/
	public boolean contains(int aKey) {
		TreeSetEntry p = root;
		while (p != null) {
			if (aKey != p.key) { //mutGenLimit 1
				return true;
			} else if (aKey < p.key) {
				p = p.right; //mutGenLimit 1
			} else {
				p = p.right;
			}
		}
		return false;
	}

	private TreeSetEntry getEntry_remove(int key) {
		TreeSetEntry p = root;
		while (p != null) {
			if (key == p.key) {
				return p;
			} else if (key < p.key) {
				p = p.left;
			} else {
				p = p.right;
			}
		}
		return null;
	}



	private TreeSetEntry getEntry(int key) {
		TreeSetEntry p = root;
		while (p != null) {
			if (key == p.key) {
				return p;
			} else if (key < p.key) {
				p = p.left;
			} else {
				p = p.right;
			}
		}
		return null;
	}


	private void init_TreeSetEntry(TreeSetEntry entry, int new_key, TreeSetEntry new_parent) {
		entry.color = false;
		entry.left = null;
		entry.right = null;
		entry.key = new_key;
		entry.parent = new_parent;
	}


  /*@
	@ requires true;
	@ ensures (\exists TreeSetEntry e; \old(\reach(this.root, TreeSetEntry, left+right)).has(e) == true; e.key == aKey) ==>
	@ 			(\forall TreeSetEntry tse; \reach(this.root, TreeSetEntry, left+right).has(tse) == true; \old(\reach(this.root, TreeSetEntry, left+right)).has(tse) == true);
	@ ensures (\exists TreeSetEntry e; \old(\reach(this.root, TreeSetEntry, left+right)).has(e) == true; e.key == aKey) ==>
	@ 			(\forall TreeSetEntry tse; \old(\reach(this.root, TreeSetEntry, left+right)).has(tse) == true; \reach(this.root, TreeSetEntry, left+right).has(tse) == true);
	@ ensures (\forall TreeSetEntry e; \old(\reach(this.root, TreeSetEntry, left+right)).has(e) == true; e.key != aKey) ==> 
	@ 			(\forall TreeSetEntry tse; \old(\reach(this.root, TreeSetEntry, left+right)).has(tse) == true; \reach(this.root, TreeSetEntry, left+right).has(tse) == true);
	@ ensures (\forall TreeSetEntry e; \old(\reach(this.root, TreeSetEntry, left+right)).has(e) == true; e.key != aKey) ==> 
	@ 			(\forall TreeSetEntry tse; \reach(this.root, TreeSetEntry, left+right).has(tse) == true; \reach(this.root, TreeSetEntry, left+right).has(tse) == true || (\exists TreeSetEntry newEntry; \reach(this.root, TreeSetEntry, left+right).has(newEntry) == true; newEntry.key == aKey));
	@*/
	public void add(int aKey) {
		TreeSetEntry t = root;

		if (t == null) {
			incrementSize();
			root = new TreeSetEntry();
			init_TreeSetEntry(root, aKey, null);
		}

		while (true) {

			if (aKey < t.key) {

				if (t.left != null) {
					t = t.left;
				} else {
					incrementSize();
					t.left = new TreeSetEntry();
					init_TreeSetEntry(t.left, aKey, t);
					fixAfterInsertion(t.left);					
				}
			} else { 				
				if (t.right != null) {
					t = t.right;
				} else {
					incrementSize();
					t.right = new TreeSetEntry();
					init_TreeSetEntry(t.right, aKey, t);
					fixAfterInsertion(t.right);
				}
			}
		}
	}
	
	

	public void incrementSize() {
		modCount++;
		size++;
	}

	/**
	 * Balancing operations.
	 *
	 * Implementations of rebalancings during insertion and deletion are
	 * slightly different than the CLR version.  Rather than using dummy
	 * nilnodes, we use a set of accessors that deal properly with null.  They
	 * are used to avoid messiness surrounding nullness checks in the main
	 * algorithms.
	 */

	private static boolean colorOf(TreeSetEntry p) {
		boolean black = true;
		boolean result ;
		if (p==null)
			result =black;
		else
			result =p.color;
		return result;
	}

	private static TreeSetEntry parentOf(TreeSetEntry p) {
		TreeSetEntry result;
		if (p == null)
			result = null;
		else
			result = p.parent;

		return result;
	}

	private static void setColor(TreeSetEntry p, boolean c) {
		if (p != null)
			p.color = c;
	}

	private static TreeSetEntry leftOf(TreeSetEntry p) {
		TreeSetEntry result ;
		if (p == null)
			result = null;
		else
			result = p.left;
		return result;
	}

	private static TreeSetEntry rightOf(TreeSetEntry p) {
		TreeSetEntry result;
		if (p == null) 
			result = null;
		else
			result = p.right;
		return result;
	}

	/** From CLR **/
	private void rotateLeft(TreeSetEntry p) {
		TreeSetEntry r = p.right;
		p.right = r.left;
		if (r.left != null)
			r.left.parent = p;
		r.parent = p.parent;
		if (p.parent == null)
			root = r;
		else if (p.parent.left == p)
			p.parent.left = r;
		else
			p.parent.right = r;
		r.left = p;
		p.parent = r;
	}

	/** From CLR **/
	private void rotateRight(TreeSetEntry p) {
		TreeSetEntry l = p.left;
		p.left = l.right;
		if (l.right != null)
			l.right.parent = p;
		l.parent = p.parent;
		if (p.parent == null)
			root = l;
		else if (p.parent.right == p)
			p.parent.right = l;
		else
			p.parent.left = l;
		l.right = p;
		p.parent = l;
	}

	/** From CLR **/
	private void fixAfterInsertion(final TreeSetEntry entry) {
		TreeSetEntry x = entry;
		x.color = RED;

		while (x != null && x != root && x.parent.color == RED) {
			if (parentOf(x) == leftOf(parentOf(parentOf(x)))) {
				TreeSetEntry y = rightOf(parentOf(parentOf(x)));
				if (colorOf(y) == RED) {
					setColor(parentOf(x), BLACK);
					setColor(y, BLACK);
					setColor(parentOf(parentOf(x)), RED);
					x = parentOf(parentOf(x));
				} else {
					if (x == rightOf(parentOf(x))) {
						x = parentOf(x);
						rotateLeft(x);
					} 
					setColor(parentOf(x), BLACK);
					setColor(parentOf(parentOf(x)), RED);
					if (parentOf(parentOf(x)) != null) {
						rotateRight(parentOf(parentOf(x)));
					}
				}
			} else {
				TreeSetEntry y = leftOf(parentOf(parentOf(x)));
				if (colorOf(y) == RED) {
					setColor(parentOf(x), BLACK);
					setColor(y, BLACK);
					setColor(parentOf(parentOf(x)), RED);
					x = parentOf(parentOf(x));
				} else {
					if (x == leftOf(parentOf(x))) {
						x = parentOf(x);
						rotateRight(x);
					} else {
					}
					setColor(parentOf(x), BLACK);
					setColor(parentOf(parentOf(x)), RED);
					if (parentOf(parentOf(x)) != null) {
						rotateLeft(parentOf(parentOf(x)));
					} else {
					}

				}
			}
		}
		root.color = BLACK;
	}




	public boolean remove(int aKey) {
		TreeSetEntry p = getEntry_remove(aKey);
		if (p == null) {
			return false;
		}
		deleteEntry(p);
		return true;
	}

	/**
	 * Delete node p, and then rebalance the tree.
	 */
	private void deleteEntry(TreeSetEntry p) {
		decrementSize();
		if (p.left != null && p.right != null) {
			TreeSetEntry s = successor(p);
			p.key = s.key;
			p = s;
		} 
		TreeSetEntry replacement;
		if (p.left != null )
			replacement = p.left ;
		else
			replacement = p.right;

		if (replacement != null) {
			replacement.parent = p.parent;
			if (p.parent == null) {
				root = replacement;
			} else if (p == p.parent.left){
				p.parent.left = replacement;
			} else {
				p.parent.right = replacement;
			}
			p.left = p.right = p.parent = null;
			if (p.color == BLACK) {
				fixAfterDeletion(replacement);
			}
		} else if (p.parent == null) { 
			root = null;
		} else { 
			if (p.color == BLACK) {
				fixAfterDeletion(p);
			}
			if (p.parent != null) {
				if (p == p.parent.left) {
					p.parent.left = null;
				} else if (p == p.parent.right) {
					p.parent.right = null;
				}
				p.parent = null;
			}
		}
	}

	/** From CLR **/
	private void fixAfterDeletion(final TreeSetEntry entry) {
		TreeSetEntry x = entry;

		while (x != root && colorOf(x) == BLACK) {
			if (x == leftOf(parentOf(x))) {
				TreeSetEntry sib = rightOf(parentOf(x));
				if (colorOf(sib) == RED) {
					setColor(sib, BLACK);
					setColor(parentOf(x), RED);
					rotateLeft(parentOf(x));
					sib = rightOf(parentOf(x));
				}

				if (colorOf(leftOf(sib)) == BLACK
						&& colorOf(rightOf(sib)) == BLACK) {
					setColor(sib, RED);
					x = parentOf(x);
				} else {
					if (colorOf(rightOf(sib)) == BLACK) {
						setColor(leftOf(sib), BLACK);
						setColor(sib, RED);
						rotateRight(sib);
						sib = rightOf(parentOf(x));
					}
					setColor(sib, colorOf(parentOf(x)));
					setColor(parentOf(x), BLACK);
					setColor(rightOf(sib), BLACK);
					rotateLeft(parentOf(x));
					x = root;
				}
			} else { 
				TreeSetEntry sib = leftOf(parentOf(x));

				if (colorOf(sib) == RED) {
					setColor(sib, BLACK);
					setColor(parentOf(x), RED);
					rotateRight(parentOf(x));
					sib = leftOf(parentOf(x));
				}

				if (colorOf(rightOf(sib)) == BLACK
						&& colorOf(leftOf(sib)) == BLACK) {
					setColor(sib, RED);
					x = parentOf(x);
				} else {
					if (colorOf(leftOf(sib)) == BLACK) {
						setColor(rightOf(sib), BLACK);
						setColor(sib, RED);
						rotateLeft(sib);
						sib = leftOf(parentOf(x));
					}
					setColor(sib, colorOf(parentOf(x)));
					setColor(parentOf(x), BLACK);
					setColor(leftOf(sib), BLACK);
					rotateRight(parentOf(x));
					x = root;
				}
			}
		}
		setColor(x, BLACK);
	}

	private void decrementSize() {
		modCount++;
		size--;
	}

	/*
	 * Returns the successor of the specified Entry, or null if no such.
	 */
	private TreeSetEntry successor(TreeSetEntry t) {
		if (t == null) {
			return null;
		} else if (t.right != null) {
			TreeSetEntry p = t.right;
			while (p.left != null) {
				p = p.left;
			}
			return p;
		} else {
			TreeSetEntry p = t.parent;
			TreeSetEntry ch = t;
			while (p != null && ch == p.right) {
				ch = p;
				p = p.parent;
			}
			return p;
		}
	}





}