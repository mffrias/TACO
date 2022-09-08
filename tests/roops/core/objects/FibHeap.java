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
package roops.core.objects;

//FibHeap.java
//from : http://sciris.shu.edu/~borowski/Puzzle/Puzzle.html


/**
 * @Invariant ( all node: FibHeapNode | node in this.min.*(child @+ right) => ( 
 *              ( node !in (node.*right @- node).child.*(child @+ right) ) && 
 *              ( ( node in this.min.*right ) => (node.parent==null && this.min.cost <= node.cost ) ) && 
 *              ( node.right != null ) && 
 *              ( node.right.left == node ) && 
 *              ( node.degree = #(node.child.*right @- null) ) && 
 *              ( all m: FibHeapNode | ( m in node.child.*(child @+ right) => node.cost <= m.cost ) ) && 
 *              ( node.child != null => node.(child.*right).parent==node ) ) 
 *            ) &&
 * 
 *            ( this.n = #(this.min.*(child @+ right) @- null) ) ;
 */
public class FibHeap {


	/**
	 * @Modifies_Everything;
	 * 
	 * @Ensures false;
	 */
	static public void insertNodeTest(/*@ nullable @*/ FibHeap fibHeap, /*@ nullable @*/ FibHeapNode toInsert) {
		if (fibHeap!=null && toInsert!=null) {
		  FibHeapNode ret_val = fibHeap.insertNode(toInsert);
		}
	}

	/**
	 * @Modifies_Everything;
	 * 
	 * @Ensures false;
	 */
	static public void minimumTest(/*@ nullable @*/ FibHeap fibHeap) {
		if (fibHeap!=null) {
		  FibHeapNode ret_val = fibHeap.minimum();
		}
	}

	/**
	 * @Modifies_Everything;
	 * 
	 * @Ensures false;
	 */
	static public void removeMinTest(/*@ nullable @*/ FibHeap fibHeap) {
		if (fibHeap!=null) {
		  FibHeapNode ret_val = fibHeap.removeMin();
		}
	}

	
	/*@ nullable @*/FibHeapNode min;

	int n;

	private void consolidate() {
		int D = n + 1;
		Object[] A = new Object[D];
		for (int i = 0; i < D; i++) {
			
			/*{roops.util.Goals.reached(6, roops.util.Verdict.REACHABLE);}*/
			A[i] = null;
		}
		

		int k = 0;
		FibHeapNode x = min;
		if (x != null) {
			
			/*{roops.util.Goals.reached(7, roops.util.Verdict.REACHABLE);}*/
			k++;
			x = x.right;
			while (x != min) {
				
				/*{roops.util.Goals.reached(8, roops.util.Verdict.REACHABLE);}*/
				k++;
				x = x.right;
			}
		} else {
			/*{roops.util.Goals.reached(9, roops.util.Verdict.UNREACHABLE);}*/
		}
		
		
		while (k > 0) {
			
			/*{roops.util.Goals.reached(10, roops.util.Verdict.REACHABLE);}*/
			int d = x.degree;
			FibHeapNode rightNode = x.right;

			while (A[d] != null) {

				/*{roops.util.Goals.reached(11, roops.util.Verdict.REACHABLE);}*/
				if (!(A[d] instanceof FibHeapNode)) {
					
					/*{roops.util.Goals.reached(12,roops.util.Verdict.UNREACHABLE);}*/
					throw new ClassCastException();
				}
				FibHeapNode y = (FibHeapNode) A[d];
				
				if (x.cost > y.cost) {

					/*{roops.util.Goals.reached(13, roops.util.Verdict.REACHABLE);}*/
					FibHeapNode temp = y;
					y = x;
					x = temp;
				} else {
				    /*{roops.util.Goals.reached(14, roops.util.Verdict.REACHABLE);}*/
					link(y, x);
				}
				A[d] = null;
				d++;
			}

			A[d] = x;
			x = rightNode;
			k--;
		}

		
		min = null;
		for (int i = 0; i < D; i++) {
			
			/*{roops.util.Goals.reached(15, roops.util.Verdict.REACHABLE);}*/
			if (A[i] != null) {
				
				/*{roops.util.Goals.reached(16, roops.util.Verdict.REACHABLE);}*/
				if (min != null) {

					/*{roops.util.Goals.reached(17, roops.util.Verdict.REACHABLE);}*/
					if (!(A[i] instanceof FibHeapNode)) {
						
						/*{roops.util.Goals.reached(18, roops.util.Verdict.UNREACHABLE);}*/
						throw new ClassCastException();
					} 
					FibHeapNode node = (FibHeapNode) A[i];

					node.left.right = node.right;
					node.right.left = node.left;
					node.left = min;
					node.right = min.right;
					min.right = node;
					node.right.left = node;
					if (node.cost < min.cost) {
						

						if (!(A[i] instanceof FibHeapNode)) {
							/*{roops.util.Goals.reached(19,roops.util.Verdict.UNREACHABLE);}*/
							throw new ClassCastException();
						}						
						min = (FibHeapNode) A[i];
						
					} else {
						/*{roops.util.Goals.reached(20, roops.util.Verdict.REACHABLE);}*/
					}
				} else {

					/*{roops.util.Goals.reached(21, roops.util.Verdict.REACHABLE);}*/
					if (!(A[i] instanceof FibHeapNode)) {
						
						/*{roops.util.Goals.reached(22, roops.util.Verdict.UNREACHABLE);}*/
						throw new ClassCastException();
					}
					min = (FibHeapNode) A[i];
				}
			} else {
				/*{roops.util.Goals.reached(23, roops.util.Verdict.REACHABLE);}*/
			}
		}
		
		/*{roops.util.Goals.reached(24, roops.util.Verdict.REACHABLE);}*/
	}

	public FibHeapNode insertNode(FibHeapNode toInsert) {
		if (min != null) {
			
			/*{roops.util.Goals.reached(0, roops.util.Verdict.REACHABLE);}*/
			toInsert.left = min;
			toInsert.right = min.right;
			min.right = toInsert;
			toInsert.right.left = toInsert;
			if (toInsert.cost < min.cost) {
				
				/*{roops.util.Goals.reached(1, roops.util.Verdict.REACHABLE);}*/
				min = toInsert;
			} else {

				/*{roops.util.Goals.reached(2, roops.util.Verdict.REACHABLE);}*/
			}
		} else {
			
			/*{roops.util.Goals.reached(3, roops.util.Verdict.REACHABLE);}*/
			min = toInsert;

		}
		n++;
		
		/*{roops.util.Goals.reached(4);}*/
		return toInsert;
	}

	private void link(FibHeapNode node1, FibHeapNode node2) {
		node1.left.right = node1.right;
		node1.right.left = node1.left;
		node1.parent = node2;
		if (node2.child == null) {

			node2.child = node1;
			node1.right = node1;
			node1.left = node1;
		} else {

			node1.left = node2.child;
			node1.right = node2.child.right;
			node2.child.right = node1;
			node1.right.left = node1;
		}
		node2.degree++;
		node1.mark = false;
	}

	

	public FibHeapNode minimum() {
		/*{roops.util.Goals.reached(0, roops.util.Verdict.REACHABLE);}*/
		return min;
	}

	
	public FibHeapNode removeMin() {
		FibHeapNode z = min;
		if (z != null) {
			
			/*{roops.util.Goals.reached(0, roops.util.Verdict.REACHABLE);}*/
			int i = z.degree;
			FibHeapNode x = z.child;
			while (i > 0) {

				/*{roops.util.Goals.reached(1, roops.util.Verdict.REACHABLE);}*/
				FibHeapNode nextChild = x.right;
				x.left.right = x.right;
				x.right.left = x.left;
				x.left = min;
				x.right = min.right;
				min.right = x;
				x.right.left = x;
				x.parent = null;
				x = nextChild;
				i--;
			} 
			
			/*{roops.util.Goals.reached(2, roops.util.Verdict.REACHABLE);}*/
			z.left.right = z.right;
			z.right.left = z.left;
			if (z == z.right) {

				/*{roops.util.Goals.reached(3, roops.util.Verdict.REACHABLE);}*/
				min = null;
			} else {

				/*{roops.util.Goals.reached(4, roops.util.Verdict.REACHABLE);}*/
				min = z.right;
				consolidate();
			}

			n--;
		}
		
		/*{roops.util.Goals.reached(5, roops.util.Verdict.REACHABLE);}*/
		return z;
	}
}
