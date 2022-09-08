//
// Copyright (C) 2006 United States Government as represented by the
// Administrator of the National Aeronautics and Space Administration
// (NASA).  All Rights Reserved.
// 
// This software is distributed under the NASA Open Source Agreement
// (NOSA), version 1.3.  The NOSA has been approved by the Open Source
// Initiative.  See the file NOSA-1.3-JPF at the top of the distribution
// directory tree for the complete NOSA document.
// 
// THE SUBJECT SOFTWARE IS PROVIDED "AS IS" WITHOUT ANY WARRANTY OF ANY
// KIND, EITHER EXPRESSED, IMPLIED, OR STATUTORY, INCLUDING, BUT NOT
// LIMITED TO, ANY WARRANTY THAT THE SUBJECT SOFTWARE WILL CONFORM TO
// SPECIFICATIONS, ANY IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR
// A PARTICULAR PURPOSE, OR FREEDOM FROM INFRINGEMENT, ANY WARRANTY THAT
// THE SUBJECT SOFTWARE WILL BE ERROR FREE, OR ANY WARRANTY THAT
// DOCUMENTATION, IF PROVIDED, WILL CONFORM TO THE SUBJECT SOFTWARE.
//
package roops.core.objects.bintree;


/**
 * 
 * @Invariant all n : BinTreeNode | n in this.root.*(left @+ right ) => ( 
 *            ( n !in n.^(left @+ right) ) && 
 *            ( all m: BinTreeNode | m in n.left.*(left @+right) => m.key < n.key ) && 
 *            ( all m: BinTreeNode | m in n.right.*(left @+right) => n.key < m.key ) && 
 *            ( n.left!=null => n.left.parent=n ) &&
 *            ( n.right!=null=> n.right.parent=n ) && 
 *            ( n=this.root => n.parent=null ) ) ;
 * 
 */
public class BinTree {

	/**
	 * @Modifies_Everything;
	 * 
	 * @Ensures false;
	 */
	static public void addTest(/*@ nullable @*/ BinTree tree, int x) {
		if (tree!=null) {
		  tree.add(x);
		}
	}

	/**
	 * @Modifies_Everything;
	 * 
	 * @Ensures false;
	 */
	static public void findTest(/*@ nullable @*/ BinTree tree, int x) {
		boolean ret_val;
		if (tree!=null) {
		  ret_val = tree.find(x);
		}
	}

	/**
	 * @Modifies_Everything;
	 * 
	 * @Ensures false;
	 */
	static public void removeTest(/*@ nullable @*/ BinTree tree, /*@ nullable @*/ BinTreeNode z) {
		BinTreeNode ret_val;
		if (tree!=null && z!=null) {
		  ret_val = tree.remove(z);
		}
	}	

	
	private /*@ nullable @*/ BinTreeNode root;


	public void add(int x) {
		BinTreeNode current = root;

		if (root == null) {
			/*{roops.util.Goals.reached(0);}*/
			root = new BinTreeNode();
			initNode(root,x);
			return;
		}

		while (current.key != x) {
			
			/*{roops.util.Goals.reached(1);}*/
			
			if (x < current.key) {
				
				/*{roops.util.Goals.reached(2);}*/
				
				if (current.left == null) {
					/*{roops.util.Goals.reached(3);}*/
					current.left = new BinTreeNode();
					initNode(current.left,x);
				} else {
					/*{roops.util.Goals.reached(4);}*/
					current = current.left;
				}
			} else {
				/*{roops.util.Goals.reached(5);}*/
				
				if (current.right == null) {
					/*{roops.util.Goals.reached(6);}*/
					current.right = new BinTreeNode();
					initNode(current.right,x);
				} else {
					/*{roops.util.Goals.reached(7);}*/
					current = current.right;
				}
			}
		}
		
		/*{roops.util.Goals.reached(8);}*/
	}


	private void initNode(BinTreeNode node, int x) {
		node.key = x;
		node.left = null;
		node.right = null;
	}

	public boolean find(int x) {
		BinTreeNode current = root;

		while (current != null) {
			/*{roops.util.Goals.reached(0, roops.util.Verdict.REACHABLE);}*/
			
			if (current.key == x) {
				/*{roops.util.Goals.reached(1, roops.util.Verdict.REACHABLE);}*/
				return true;
			}

			if (x < current.key) {
				/*{roops.util.Goals.reached(2, roops.util.Verdict.REACHABLE);}*/
				current = current.left;
			} else {
				/*{roops.util.Goals.reached(3, roops.util.Verdict.REACHABLE);}*/
				current = current.right;
			}
		}

		/*{roops.util.Goals.reached(4, roops.util.Verdict.REACHABLE);}*/
		return false;
	}

	private BinTreeNode treeMinimum(final BinTreeNode x_param) {
		BinTreeNode x = x_param;
		while (x.left != null) {
			/*{roops.util.Goals.reached(15, roops.util.Verdict.REACHABLE);}*/
			x = x.left;
		}
		/*{roops.util.Goals.reached(16, roops.util.Verdict.REACHABLE);}*/
		return x;
	}

	private BinTreeNode treeSuccessor(final BinTreeNode x_param) {
		BinTreeNode x = x_param;
		BinTreeNode result;
		if (x.right != null) {
			/*{roops.util.Goals.reached(11, roops.util.Verdict.REACHABLE);}*/
			result = treeMinimum(x.right);
		} else {
			/*{roops.util.Goals.reached(12, roops.util.Verdict.UNREACHABLE);}*/
			BinTreeNode y = x.parent;
			while (y != null && x == y.right) {
				/*{roops.util.Goals.reached(13, roops.util.Verdict.UNREACHABLE);}*/
				x = y;
				y = y.parent;
			}

			result = y;
		}
		/*{roops.util.Goals.reached(14, roops.util.Verdict.REACHABLE);}*/
		return result;
	}

	
	public BinTreeNode remove(final BinTreeNode z) {
		BinTreeNode y = null;
		if (z.left == null || z.right == null) {
			/*{roops.util.Goals.reached(0, roops.util.Verdict.REACHABLE);}*/
			y = z;
		} else {
			/*{roops.util.Goals.reached(1, roops.util.Verdict.REACHABLE);}*/
			y = treeSuccessor(z);
		}

		BinTreeNode x = null;
		if (y.left != null) {
			/*{roops.util.Goals.reached(2, roops.util.Verdict.REACHABLE);}*/
			x = y.left;
		} else {
			/*{roops.util.Goals.reached(3, roops.util.Verdict.REACHABLE);}*/
			x = y.right;
		}

		if (x != null) {
			/*{roops.util.Goals.reached(4, roops.util.Verdict.REACHABLE);}*/
			x.parent = y.parent;
		}

		if (y.parent == null) {
			/*{roops.util.Goals.reached(5, roops.util.Verdict.REACHABLE);}*/
			this.root = x;
		} else {
			/*{roops.util.Goals.reached(6, roops.util.Verdict.REACHABLE);}*/
			if (y == y.parent.left){
				/*{roops.util.Goals.reached(7, roops.util.Verdict.REACHABLE);}*/
				y.parent.left = x;
			}else{
				/*{roops.util.Goals.reached(8, roops.util.Verdict.REACHABLE);}*/
				y.parent.right = x;
			}
		}

		if (y != z) {
			/*{roops.util.Goals.reached(9, roops.util.Verdict.REACHABLE);}*/
			z.key = y.key;
		}

		/*{roops.util.Goals.reached(10, roops.util.Verdict.REACHABLE);}*/
		return y;
	}

}
