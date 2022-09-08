package roops.core.objects.avltree.old;


/**
 * Implements an AVL tree. Note that all "matching" is based on the compareTo
 * method.
 * 
 * @author Mark Allen Weiss
 */


/**
 * @Invariant all x: AvlNode | x in this.root.*(left @+ right) @- null => 
 * (
 *		(x !in x.^(left @+ right)) && 
 *		(all y: AvlNode | (y in x.left.*(left @+ right) @-null) => y.element < x.element ) && 
 *		(all y: AvlNode | (y in x.right.*(left @+right) @- null) => x.element < y.element ) && 
 *		(x.left=null && x.right=null => x.height=0) && 
 *		(x.left=null && x.right!=null => x.height=1 && x.right.height=0) && 
 *		(x.left!=null && x.right=null => x.height=1 && x.left.height=0) && 
 *		(x.left!=null && x.right!=null => x.height= (x.left.height>x.right.height ? x.left.height : x.right.height )+1 && ( (x.left.height > x.right.height ? x.left.height - x.right.height : x.right.height - x.left.height ))<=1)
 * );
 * 
 */
public class OldAvlTree {

	/**
	 * @Modifies_Everything;
	 * 
	 * @Ensures false;
	 */
    static public void findNodeTest(OldAvlTree tree, int x) {
    	int lit=1;
    	if (tree!=null) {
		  OldAvlNode ret_val = tree.findNode(x);
    	}
	}

	/**
	 * @Modifies_Everything;
	 * 
	 * @Ensures false;
	 */
   static public void fmaxTest(OldAvlTree tree) {
	   int lit=1;
	   if (tree!=null) {
	     OldAvlNode ret_val = tree.fmax();
	   }
   }

	/**
	 * @Modifies_Everything;
	 * 
	 * @Ensures false;
	 */
   static public void fminTest(OldAvlTree tree) {
	   int lit=1;
	   if (tree!=null) {
	     OldAvlNode ret_val = tree.fmin();
	   }
   }

	/** The tree root. */
	private/*@ nullable @*/OldAvlNode root;

	public/*@ nullable @*/OldAvlNode findNode(final int x) {
		return find(x, this.root);
	}


	private OldAvlNode find(final int x, final OldAvlNode arg) {
		OldAvlNode t = arg;
		while (t != null) {
			
			/*{roops.util.Goals.reached(0, roops.util.Verdict.REACHABLE);}*/
			if (x < t.element) {
				
				/*{roops.util.Goals.reached(1, roops.util.Verdict.REACHABLE);}*/
				t = t.left;
			} else if (x > t.element) {
				
				/*{roops.util.Goals.reached(2, roops.util.Verdict.REACHABLE);}*/
				t = t.right;
			} else {
				
				/*{roops.util.Goals.reached(3, roops.util.Verdict.REACHABLE);}*/
				return t; // Match
			}
		}

		/*{roops.util.Goals.reached(4, roops.util.Verdict.REACHABLE);}*/
		return null; // No match
	}

	
	public OldAvlNode fmax() {
		return findMax(this.root);
	}


	private OldAvlNode findMax(final OldAvlNode arg) {
		OldAvlNode t = arg;
		if (t == null) {
			/*{roops.util.Goals.reached(0, roops.util.Verdict.REACHABLE);}*/
			return t;
		}

		while (t.right != null) {
			/*{roops.util.Goals.reached(1, roops.util.Verdict.REACHABLE);}*/
			t = t.right;
		}
		
		/*{roops.util.Goals.reached(2, roops.util.Verdict.REACHABLE);}*/
		return t;
	}



	public OldAvlNode fmin() {
		return findMin(this.root);
	}

	

	private OldAvlNode findMin(final OldAvlNode arg) {
		OldAvlNode t = arg;
		if (t == null) {
			/*{roops.util.Goals.reached(0, roops.util.Verdict.REACHABLE);}*/
			return t;
		}

		while (t.left != null) {
			/*{roops.util.Goals.reached(1, roops.util.Verdict.REACHABLE);}*/
			t = t.left;
		}
		
		/*{roops.util.Goals.reached(2, roops.util.Verdict.REACHABLE);}*/
		return t;
	}


}
