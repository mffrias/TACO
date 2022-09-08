/*
 * @(#)TreeMap.java	1.56 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package roops.core.objects;


import java.util.HashSet;
import java.util.ArrayList;

/**
 * @Invariant ( this.RED==false ) && 
 *		( this.BLACK==true ) &&
 *		( this.root.parent in null ) &&
 *		( this.root!=null => this.root.color = this.BLACK ) && 
 *		( all n: TreeSetEntry | n in this.root.*(left @+ right @+ parent) @- null => ( 
 *				(n.key != null ) &&
 *				( n.left != null => n.left.parent = n ) &&
 *				( n.right != null => n.right.parent = n ) &&
 *				( n.parent != null => n in n.parent.(left @+ right) ) &&
 *				( n !in n.^parent ) &&
 *				( all x : TreeSetEntry | (( x in n.left.^(left @+ right) @+ n.left @- null ) => ( n.key > x.key )) ) &&
 *				( all x : TreeSetEntry | (( x in n.right.^(left @+ right) @+ n.right @- null ) => ( x.key > n.key ))) &&
 *				( n.color = this.RED && n.parent != null => n.parent.color = this.BLACK ) && 
 *				( ( n.left=null && n.right=null ) => ( n.blackHeight=1 ) ) &&
 *				( n.left!=null && n.right=null => ( 
 *				      ( n.left.color = this.RED ) && 
 *				      ( n.left.blackHeight = 1 ) && 
 *				      ( n.blackHeight = 1 )  
 *				)) &&
 *				( n.left=null && n.right!=null =>  ( 
 *				      ( n.right.color = this.RED ) && 
 *				      ( n.right.blackHeight = 1 ) && 
 *				      ( n.blackHeight = 1 ) 
 *				 )) && 
 *				( n.left!=null && n.right!=null && n.left.color=this.RED && n.right.color=this.RED => ( 
 *				        ( n.left.blackHeight = n.right.blackHeight ) && 
 *				        ( n.blackHeight = n.left.blackHeight ) 
 *				)) && 
 *				( n.left!=null && n.right!=null && n.left.color=this.BLACK && n.right.color=this.BLACK => ( 
 *				        ( n.left.blackHeight=n.right.blackHeight ) && 
 *				        ( n.blackHeight=n.left.blackHeight + 1 )  
 *				)) && 
 *				( n.left!=null && n.right!=null && n.left.color=this.RED && n.right.color=this.BLACK => ( 
 *				        ( n.left.blackHeight=n.right.blackHeight + 1 ) && 
 *				        ( n.blackHeight = n.left.blackHeight  )  
 *				)) && 
 *				( n.left!=null && n.right!=null && n.left.color=this.BLACK && n.right.color=this.RED => ( 
 *				        ( n.right.blackHeight=n.left.blackHeight + 1 ) && 
 *				        ( n.blackHeight = n.right.blackHeight  )  )) 
 *				)) ; 
 */
public class CopyOfTreeSet {

	
	/**
	 * @Modifies_Everything;
	 * 
	 * @Ensures false;
	 */
	static public void addTest(/*@ nullable @*/ CopyOfTreeSet treeSet, int aKey) {
		if (treeSet!=null) {
		  boolean ret_val = treeSet.add(aKey);
		}
	}

	
	/**
	 * @Modifies_Everything;
	 * 
	 * @Ensures false;
	 */
	static public void containsTest(/*@ nullable @*/ CopyOfTreeSet treeSet, int aKey) {
		if (treeSet!=null) {
		  boolean ret_val = treeSet.contains(aKey);
		}
	}

	/**
	 * @Modifies_Everything;
	 * 
	 * @Ensures false;
	 */
	static public void removeTest(/*@ nullable @*/ CopyOfTreeSet treeSet, int aKey) {
		if (treeSet!=null) {
		  boolean ret_val = treeSet.remove(aKey);
		}
	}

	/*@ nullable @*/ TreeSetEntry root = null;

	/**
	 * The number of entries in the tree
	 */
	private int size = 0;

	/**
	 * The number of structural modifications to the tree.
	 */
	private int modCount = 0;

	/*static final */ boolean RED = false;
	/*static final */ boolean BLACK = true;




	public boolean contains(int aKey) {
		return getEntry(aKey) != null;
	}

	private TreeSetEntry getEntry_remove(int key) {
		TreeSetEntry p = root;
		while (p != null) {

			/*{roops.util.Goals.reached(0, roops.util.Verdict.REACHABLE);}*/
			
			if (key == p.key) {
				
				/*{roops.util.Goals.reached(1, roops.util.Verdict.REACHABLE);}*/
				return p;
			} else if (key < p.key) {
				
				/*{roops.util.Goals.reached(2, roops.util.Verdict.REACHABLE);}*/
				p = p.left;
			} else {
				
				/*{roops.util.Goals.reached(3, roops.util.Verdict.REACHABLE);}*/
				p = p.right;
			}
		}
		/*{roops.util.Goals.reached(4, roops.util.Verdict.REACHABLE);}*/
		return null;
	}



	private TreeSetEntry getEntry(int key) {
		TreeSetEntry p = root;
		while (p != null) {

			/*{roops.util.Goals.reached(0, roops.util.Verdict.REACHABLE);}*/
			
			if (key == p.key) {
				
				/*{roops.util.Goals.reached(1, roops.util.Verdict.REACHABLE);}*/
				return p;
			} else if (key < p.key) {
				
				/*{roops.util.Goals.reached(2, roops.util.Verdict.REACHABLE);}*/
				p = p.left;
			} else {
				
				/*{roops.util.Goals.reached(3, roops.util.Verdict.REACHABLE);}*/
				p = p.right;
			}
		}
		/*{roops.util.Goals.reached(4, roops.util.Verdict.REACHABLE);}*/
		return null;
	}


	private void init_TreeSetEntry(TreeSetEntry entry, int new_key, TreeSetEntry new_parent) {
		entry.color = false;
		entry.left = null;
		entry.right = null;
		entry.key = new_key;
		entry.parent = new_parent;
	}
	

	public boolean add(int aKey) {
		TreeSetEntry t = root;

		if (t == null) {
			incrementSize();
			root = new TreeSetEntry();
			init_TreeSetEntry(root, aKey, null);
			
			/*{roops.util.Goals.reached(1, roops.util.Verdict.REACHABLE);}*/
			return false;
		}

		boolean boolean_true= true;
		while (boolean_true) {
			
			if (aKey == t.key) {
				
				/*{roops.util.Goals.reached(2, roops.util.Verdict.REACHABLE);}*/
				return true;
			} else if (aKey < t.key) {
				
				if (t.left != null) {
					
					/*{roops.util.Goals.reached(3, roops.util.Verdict.REACHABLE);}*/
					t = t.left;
				} else {
					
					/*{roops.util.Goals.reached(4, roops.util.Verdict.REACHABLE);}*/
					incrementSize();
					t.left = new TreeSetEntry();
					init_TreeSetEntry(t.left, aKey, t);
					
					fixAfterInsertion(t.left);
					
					/*{roops.util.Goals.reached(5, roops.util.Verdict.REACHABLE);}*/
					return false;
				}
			} else { // cmp > 0
				
				/*{roops.util.Goals.reached(6, roops.util.Verdict.REACHABLE);}*/
				
				if (t.right != null) {
					
					/*{roops.util.Goals.reached(7, roops.util.Verdict.REACHABLE);}*/
					t = t.right;
				} else {
					
					/*{roops.util.Goals.reached(8, roops.util.Verdict.REACHABLE);}*/
					incrementSize();
					t.right = new TreeSetEntry();
					init_TreeSetEntry(t.right, aKey, t);
					fixAfterInsertion(t.right);
					
					/*{roops.util.Goals.reached(9, roops.util.Verdict.REACHABLE);}*/
					return false;
				}
			}
		}
		/*{roops.util.Goals.reached(26, roops.util.Verdict.UNREACHABLE);}*/
		return false;
	}

	private void incrementSize() {
		/*{roops.util.Goals.reached(0, roops.util.Verdict.REACHABLE);}*/
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
			
			/*{roops.util.Goals.reached(10, roops.util.Verdict.REACHABLE);}*/
			
			if (parentOf(x) == leftOf(parentOf(parentOf(x)))) {
				
				/*{roops.util.Goals.reached(11, roops.util.Verdict.REACHABLE);}*/
				TreeSetEntry y = rightOf(parentOf(parentOf(x)));
				if (colorOf(y) == RED) {
					
					/*{roops.util.Goals.reached(12, roops.util.Verdict.REACHABLE);}*/
					setColor(parentOf(x), BLACK);
					setColor(y, BLACK);
					setColor(parentOf(parentOf(x)), RED);
					x = parentOf(parentOf(x));
				} else {
					
					/*{roops.util.Goals.reached(13, roops.util.Verdict.REACHABLE);}*/
					if (x == rightOf(parentOf(x))) {
						
						/*{roops.util.Goals.reached(14, roops.util.Verdict.REACHABLE);}*/
						x = parentOf(x);
						rotateLeft(x);
					} else {
						/*{roops.util.Goals.reached(15, roops.util.Verdict.REACHABLE);}*/
					}
					setColor(parentOf(x), BLACK);
					setColor(parentOf(parentOf(x)), RED);
					if (parentOf(parentOf(x)) != null) {
						/*{roops.util.Goals.reached(16, roops.util.Verdict.REACHABLE);}*/
						rotateRight(parentOf(parentOf(x)));
					} else {
						/*{roops.util.Goals.reached(17, roops.util.Verdict.UNREACHABLE);}*/ //source: CLR
					}
				}
			} else {
				
				/*{roops.util.Goals.reached(18, roops.util.Verdict.REACHABLE);}*/
				TreeSetEntry y = leftOf(parentOf(parentOf(x)));
				if (colorOf(y) == RED) {
					
					/*{roops.util.Goals.reached(19, roops.util.Verdict.REACHABLE);}*/
					setColor(parentOf(x), BLACK);
					setColor(y, BLACK);
					setColor(parentOf(parentOf(x)), RED);
					x = parentOf(parentOf(x));
				} else {
					
					/*{roops.util.Goals.reached(20, roops.util.Verdict.REACHABLE);}*/
					if (x == leftOf(parentOf(x))) {
						
						/*{roops.util.Goals.reached(21, roops.util.Verdict.REACHABLE);}*/
						x = parentOf(x);
						rotateRight(x);
					} else {
						/*{roops.util.Goals.reached(22, roops.util.Verdict.REACHABLE);}*/
					}
					setColor(parentOf(x), BLACK);
					setColor(parentOf(parentOf(x)), RED);
					if (parentOf(parentOf(x)) != null) {
						/*{roops.util.Goals.reached(23, roops.util.Verdict.REACHABLE);}*/
						rotateLeft(parentOf(parentOf(x)));
					} else {
						/*{roops.util.Goals.reached(24, roops.util.Verdict.UNREACHABLE);}*/ //source: CLR
					}
					
				}
			}
		}
		/*{roops.util.Goals.reached(25, roops.util.Verdict.REACHABLE);}*/
		root.color = BLACK;
	}


	
	
	public boolean remove(int aKey) {
		TreeSetEntry p = getEntry_remove(aKey);
		if (p == null) {
			/*{roops.util.Goals.reached(5, roops.util.Verdict.REACHABLE);}*/
			return false;
		}
		
		
		deleteEntry(p);
		
		/*{roops.util.Goals.reached(32);}*/
		return true;
	}

	/**
	 * Delete node p, and then rebalance the tree.
	 */
	private void deleteEntry(TreeSetEntry p) {
		decrementSize();

		// If strictly internal, copy successor's element to p and then make p
		// point to successor.
		if (p.left != null && p.right != null) {
			
			/*{roops.util.Goals.reached(6, roops.util.Verdict.REACHABLE);}*/
			TreeSetEntry s = successor(p);
			p.key = s.key;

			p = s;
		} // p has 2 children

		// Start fixup at replacement node, if it exists.
		TreeSetEntry replacement;
		if (p.left != null )
		  replacement = p.left ;
		else
		  replacement = p.right;

		if (replacement != null) {
			
			/*{roops.util.Goals.reached(12, roops.util.Verdict.REACHABLE);}*/
			
			// Link replacement to parent
			replacement.parent = p.parent;
			if (p.parent == null) {
				
				/*{roops.util.Goals.reached(13, roops.util.Verdict.REACHABLE);}*/
				root = replacement;
			} else if (p == p.parent.left){
				
				/*{roops.util.Goals.reached(14, roops.util.Verdict.REACHABLE);}*/
				p.parent.left = replacement;
		   } else {
			   
			    /*{roops.util.Goals.reached(15, roops.util.Verdict.REACHABLE);}*/
				p.parent.right = replacement;
		   }

			// Null out links so they are OK to use by fixAfterDeletion.
			p.left = p.right = p.parent = null;

			// Fix replacement
			if (p.color == BLACK) {
				
				/*{roops.util.Goals.reached(16, roops.util.Verdict.REACHABLE);}*/
				fixAfterDeletion(replacement);
			}
		} else if (p.parent == null) { // return if we are the only node.
			
			/*{roops.util.Goals.reached(26, roops.util.Verdict.REACHABLE);}*/
			root = null;
		} else { //  No children. Use self as phantom replacement and unlink.
			if (p.color == BLACK) {
				
				/*{roops.util.Goals.reached(27, roops.util.Verdict.REACHABLE);}*/
				fixAfterDeletion(p);
			}

			if (p.parent != null) {
				
				/*{roops.util.Goals.reached(28, roops.util.Verdict.REACHABLE);}*/
				if (p == p.parent.left) {
					
					/*{roops.util.Goals.reached(29, roops.util.Verdict.REACHABLE);}*/
					p.parent.left = null;
				} else if (p == p.parent.right) {
					
					/*{roops.util.Goals.reached(30, roops.util.Verdict.REACHABLE);}*/
					p.parent.right = null;
				}
				
				/*{roops.util.Goals.reached(31, roops.util.Verdict.REACHABLE);}*/
				p.parent = null;
			}
		}
	}

	/** From CLR **/
	private void fixAfterDeletion(final TreeSetEntry entry) {
		TreeSetEntry x = entry;

		while (x != root && colorOf(x) == BLACK) {
			
			/*{roops.util.Goals.reached(17, roops.util.Verdict.REACHABLE);}*/
			
			if (x == leftOf(parentOf(x))) {
				
				/*{roops.util.Goals.reached(18, roops.util.Verdict.REACHABLE);}*/
				TreeSetEntry sib = rightOf(parentOf(x));

				if (colorOf(sib) == RED) {
					
					/*{roops.util.Goals.reached(19, roops.util.Verdict.REACHABLE);}*/
					setColor(sib, BLACK);
					setColor(parentOf(x), RED);
					rotateLeft(parentOf(x));
					sib = rightOf(parentOf(x));
				}

				if (colorOf(leftOf(sib)) == BLACK
						&& colorOf(rightOf(sib)) == BLACK) {
					
					/*{roops.util.Goals.reached(20, roops.util.Verdict.REACHABLE);}*/
					
					setColor(sib, RED);
					x = parentOf(x);
				} else {
					if (colorOf(rightOf(sib)) == BLACK) {
						
						/*{roops.util.Goals.reached(21, roops.util.Verdict.REACHABLE);}*/
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
			} else { // symmetric
				TreeSetEntry sib = leftOf(parentOf(x));

				if (colorOf(sib) == RED) {
					
					/*{roops.util.Goals.reached(22, roops.util.Verdict.REACHABLE);}*/
					setColor(sib, BLACK);
					setColor(parentOf(x), RED);
					rotateRight(parentOf(x));
					sib = leftOf(parentOf(x));
				}

				if (colorOf(rightOf(sib)) == BLACK
						&& colorOf(leftOf(sib)) == BLACK) {
					
					/*{roops.util.Goals.reached(23, roops.util.Verdict.REACHABLE);}*/
					setColor(sib, RED);
					x = parentOf(x);
				} else {
					if (colorOf(leftOf(sib)) == BLACK) {
						
						/*{roops.util.Goals.reached(24, roops.util.Verdict.REACHABLE);}*/
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

		/*{roops.util.Goals.reached(25, roops.util.Verdict.REACHABLE);}*/
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
			
			/*{roops.util.Goals.reached(7, roops.util.Verdict.UNREACHABLE);}*/ //always t!=null
			return null;
		} else if (t.right != null) {
			TreeSetEntry p = t.right;
			while (p.left != null) {
				/*{roops.util.Goals.reached(8, roops.util.Verdict.REACHABLE);}*/
				p = p.left;
			}
			
			/*{roops.util.Goals.reached(9, roops.util.Verdict.REACHABLE);}*/
			return p;
		} else {
			TreeSetEntry p = t.parent;
			TreeSetEntry ch = t;
			while (p != null && ch == p.right) {
				/*{roops.util.Goals.reached(10, roops.util.Verdict.UNREACHABLE);}*/ //always t.right != null
				ch = p;
				p = p.parent;
			}
			/*{roops.util.Goals.reached(11, roops.util.Verdict.UNREACHABLE);}*/ //always t.right != null
			return p;
		}
	}

	
	
  /*@  
	@ requires true;
	@ ensures false;
	@*/
	public boolean repOK() {
		if (root == null)
			return size == 0;

		if (root.parent != null)
			return false;

		HashSet<TreeSetEntry> visited = new HashSet<TreeSetEntry>();
		visited.add(root);
		ArrayList<TreeSetEntry> workList = new ArrayList<TreeSetEntry>();
		workList.add(root);
	
		while (workList.size() > 0) {

			TreeSetEntry current = (TreeSetEntry) workList.get(0);
			workList.remove(0);

			TreeSetEntry cl = current.left;
			if (cl != null) {
				if (!visited.add(cl))
					return false;
				if (cl.parent != current)
					return false;
				workList.add(cl);
			}
			TreeSetEntry cr = current.right;
			if (cr != null) {
				if (!visited.add(cr))
					return false;
				if (cr.parent != current)
					return false;
				workList.add(cr);
			}
		}

		if (visited.size() != size)
			return false;

		return true;
	}



	
}