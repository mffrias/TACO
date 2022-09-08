package examples.avltree.plus;



/**
 * @Invariant all x: AVLNode | x in this.root.*(left @+ right) @- null => 
 * (
 *		(x !in x.^(left @+ right)) && 
 *		(all y: AVLNode | (y in x.left.*(left @+ right) @-null) => y.element < x.element ) && 
 *		(all y: AVLNode | (y in x.right.*(left @+right) @- null) => x.element < y.element ) &&
 *       
 *      (x.left!=null => x.left.parent==x) &&
 *      (x.right!=null => x.right.parent==x) &&
 *      (x==this.root => x.parent==null) &&
 *      
 *		((x.left=null && x.right=null) => (x.height=1 && x.balance==0)) && 
 *		((x.left=null && x.right!=null) => (x.height=2 && x.right.height=1 && x.balance==x.right.height)) && 
 *		((x.left!=null && x.right=null) => (x.height=2 && x.left.height=1 && x.balance==0 - x.left.height)) && 
 *		((x.left!=null && x.right!=null) => (( x.height= (x.left.height>x.right.height ? x.left.height : x.right.height )+1 && ( (x.left.height > x.right.height ? x.left.height - x.right.height : x.right.height - x.left.height ))<=1 ) && (x.balance==x.right.height-x.left.height) ))
 * );
 * 
 */
/**
 * @SpecField nodes : set AVLNode from this.root | this.nodes = this.root.*(left @+ right @+ parent) @- null ;
 *
 */
public class AVL {

	
	public /*@ nullable @*/ AVLNode root;

	/**
	 * @Ensures return==true; 
	 */
	public boolean show_instance() {
		return false;
	}
	

	/**
	 * @Modifies_Everything 
	 * 
	 * @Ensures aKey == aKey ;
	 * Ensures @old(this.nodes.element) @- aKey = this.nodes.element ;  
	 */
	public void remove(final int aKey) {
		
		AVLNode node_containing_key;
		node_containing_key = findNodeContainingKeyToRemove(aKey);
		
		if (node_containing_key!=null) {
			
			AVLNode rebalance_start_node = null;
			if (node_containing_key.left==null && node_containing_key.right==null) {
				// leaf
				
				if (node_containing_key.parent!=null) {
				  AVLNode parentOf_node_containing_key = node_containing_key.parent;
				  
				  if (parentOf_node_containing_key.left==node_containing_key) {
					  parentOf_node_containing_key.left = null;
				  } else {
					  parentOf_node_containing_key.right = null;
				  }
				  rebalance_start_node= parentOf_node_containing_key;

				} else {
					root = null;
				}
					
			} else if (node_containing_key.left==null) {
				// no left
				
				if (node_containing_key.parent!=null) {
                  AVLNode parentOf_node_to_remove = node_containing_key.parent;
				  if (parentOf_node_to_remove.left==node_containing_key) {
					  parentOf_node_to_remove.left = node_containing_key.right;
				  } else {
					  parentOf_node_to_remove.right= node_containing_key.right;
				  }
				  if (node_containing_key.right!=null) {
					  node_containing_key.right.parent=parentOf_node_to_remove;
				  }
				  rebalance_start_node =  parentOf_node_to_remove;
				  
				} else {
					root = node_containing_key.right;
					root.parent = null;
				}
			} else {
				//assert node_to_remove.left!=null
				AVLNode max_node = findMaxNode(node_containing_key.left);
				node_containing_key.element = max_node.element;
				
				if (max_node.parent==node_containing_key) {
					node_containing_key.left = max_node.left;
					if (max_node.left!=null) {
						max_node.left.parent = node_containing_key;
					}
				} else {
					max_node.parent.right = max_node.left;
					if (max_node.left!=null) {
						max_node.left.parent = max_node.parent;
					}
				}
				rebalance_start_node= max_node.parent;
				
			}
			
			if (rebalance_start_node!=null) {
				this.root = rebalance(rebalance_start_node);
			}
			
		}
	}


	private AVLNode rebalance(final AVLNode update_start_node) {
		AVLNode iter= update_start_node;
		AVLNode prev= null;
		while (iter!=null) {
			int heightOf_left = heightOf(iter.left);
			int heightOf_right = heightOf(iter.right);

			if (heightOf_left > heightOf_right) {
				iter.height =heightOf_left+1; 
			} else {
				iter.height = heightOf_right+1;
			}
			iter.balance = heightOf_right-heightOf_left;
			
			if (iter.balance > 1 || iter.balance <-1) {
			
 			  if (isRightHeavy(iter)) {
				if (iter.right!=null) {
					if (isLeftHeavy(iter.right)) {
						iter = doubleLeftRotation(iter);
					} else {
						iter = singleLeftRotation(iter);
					}
				}
			  } else if (isLeftHeavy(iter)) {
				if (iter.left!=null) {
					if (isRightHeavy(iter.left)) {
						iter = doubleRightRotation(iter);
					} else {
						iter = singleRightRotation(iter);
					}
				}
			  }
			}
			prev = iter;
			iter = iter.parent;
		}
		AVLNode new_root = prev;
		return new_root;
	}


	private static boolean isRightHeavy(final AVLNode node) {
		boolean result;
		if (node.balance>0)
			result =true;
		else
			result=false;
		return result;
	}

	private static boolean isLeftHeavy(final AVLNode node) {
		boolean result;
		if (node.balance<0)
			result =true;
		else
			result=false;
		return result;
	}


	private static int heightOf(final AVLNode node) {
		int heightOf;
		if (node==null)
			heightOf = 0;
		else
			heightOf = node.height;
		return heightOf;
	}


	private static AVLNode findMaxNode(final AVLNode start_node) {
		AVLNode max = start_node;
		AVLNode curr = start_node.right;
		while (curr!=null) {
			max = curr;
			curr = curr.right;
		}
		return max;
	}


	private AVLNode findNodeContainingKeyToRemove(final int aKey) {
		AVLNode  node_to_remove = this.root;
		boolean found = false;
		while (!found && node_to_remove!=null) {
			if (aKey < node_to_remove.element) {
				node_to_remove = node_to_remove.left;
			} else if (node_to_remove.element <aKey){
				node_to_remove = node_to_remove.right;
			} else {
				found = true;
			}
		}
		return node_to_remove ;
	}


	/**
	 * Double rotate binary tree node: first left child with its right child; then
	 * node k3 with new left child. For AVL trees, this is a double rotation for
	 * case 2. Update heights, then return new root.
	 */
	private static AVLNode doubleRightRotation(final AVLNode c) {
		AVLNode a = c.left;
		c.left = singleLeftRotation(a);
		AVLNode b = singleRightRotation(c);
		return b;
	}


	/**
	 * Double rotate binary tree node: first right child with its left child; then
	 * node k1 with new right child. For AVL trees, this is a double rotation for
	 * case 3. Update heights, then return new root.
	 */
	private static AVLNode doubleLeftRotation(final AVLNode a) {
		AVLNode c = a.right;
		a.right = singleRightRotation(c);
		AVLNode b = singleLeftRotation(a);
		return b;
	}


	/**
	 * Return maximum of lhs and rhs.
	 */
	private static int max(final int lhs, final int rhs) {
		int result;
		if (lhs > rhs )
			result = lhs;
		else
			result = rhs;
		return result;
	}


	/**
	 * Rotate binary tree node with left child. For AVL trees, this is a single
	 * rotation for case 1. Update heights, then return new root.
	 */
	private static AVLNode singleRightRotation(final AVLNode c) {
		final AVLNode b = c.left;
		// perform right rotation
		c.left = b.right;
		b.right = c;
		// update parent
		if (c.parent!=null) {
			if (c.parent.left==c) {
				c.parent.left = b;
			} else {
				c.parent.right = b;
			}
		}
		b.parent = c.parent;
		c.parent = b;
		if (c.left!=null) {
			c.left.parent= c;
		}
        // update height
		int heightOf_c_left = heightOf(c.left);
		int heightOf_c_right = heightOf(c.right);
		c.height = max(heightOf_c_left, heightOf_c_right) + 1;
		int heightOf_b_left = heightOf(b.left);
		int heightOf_b_right = heightOf(b.right);
		b.height = max(heightOf_b_left, c.height) + 1;
		// update balance
		b.balance = heightOf_b_right-heightOf_b_left;
		c.balance = heightOf_c_right-heightOf_c_left;
		
		return b;
	}


	/**
	 * Rotate binary tree node with right child. For AVL trees, this is a single
	 * rotation for case 4. Update heights, then return new root.
	 */
	private static AVLNode singleLeftRotation(final AVLNode a) {
		final AVLNode b = a.right;
		// perform left rotation
		a.right = b.left;
		b.left = a;
		// update parent
		if (a.parent!=null) {
			if (a.parent.left==a) {
				a.parent.left = b;
			} else {
				a.parent.right = b;
			}
		}
		b.parent = a.parent;
		a.parent = b;
		if (a.right!=null) {
			a.right.parent = a;
		}
        // update height
		int heightOf_a_left = heightOf(a.left);
		int heightOf_a_right = heightOf(a.right);
		a.height = max(heightOf_a_left, heightOf_a_right) + 1;
		int heightOf_b_right = heightOf(b.right);
		int heightOf_b_left = heightOf(b.left);
		b.height = max(heightOf_b_right, a.height) + 1;
		// update balance
		b.balance = heightOf_b_right-heightOf_b_left;
		a.balance = heightOf_a_right-heightOf_a_left;

		return b;
	}
	
}
