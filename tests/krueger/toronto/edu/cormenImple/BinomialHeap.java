package krueger.toronto.edu.cormenImple;
import krueger.toronto.edu.cormenImple.BinomialHeapNode;

//@nullable_by_default;
public class BinomialHeap {
	/** The head of the singly linked root list. */
	public /*@ nullable @*/ BinomialHeapNode head;

	/** Creates an empty binomial heap. */
	public BinomialHeap() {
		head = null; // make an empty root list
	}



	//@ invariant true;


	/**
	 * Returns the <code>String</code> representation of this binomial heap,
	 * based on the objects in the nodes. It represents the depth of each node
	 * by two spaces per depth preceding the <code>String</code>
	 * representation of the node.
	 */
	//  public String print() {
	//    String result = "";
	//    
	//    BinomialHeapNode x = head;
	//    while (x != null) {
	//      result += x.walk(0);
	//      x = x.sibling;
	//    }
	//    
	//    return result;
	//  }


	/**
	 * Inserts a dynamic set element into the binomial heap.
	 * 
	 * param k
	 *            The key to be inserted.
	 * return A handle to the inserted item.
	 */
	public void insert(int k) {
		BinomialHeapNode x = new BinomialHeapNode(k);
		BinomialHeap hPrime = new BinomialHeap();
		hPrime.head = x;
		BinomialHeap newHeap = (BinomialHeap) this.union(hPrime);
		head = newHeap.head;
	}



	/**
	 * Returns the object whose key is minimum.
	 */
	public int minimum() {
		// Since we do not have a value for infinity that is inherent
		// in the Comparable interface, we simply start off with the
		// minimum element being the one pointed to by head. We have
		// to first check that head is not null.
		if (head == null)
			return -1; // empty heap, hence no minimum
		else {
			BinomialHeapNode min = head; // min takes the role of both min and y
			BinomialHeapNode x = min.sibling;

			while (x != null) {
				if (x.key < min.key)
					min = x;
				x = x.sibling;
			}

			return min.key;
		}
	}

	/**
	 * Removes and returns the smallest key in the binomial heap.
	 */

	/*@ 
	@ requires head == null;
    @ signals (Exception e) false;
    @*/
	public int extractMin() {
		// Special case for an empty binomial heap.
		if (head == null)
			return -1;

		// Find the root x with the minimum key in the root list.
		BinomialHeapNode x = head; // node with minimum key
//		BinomialHeapNode y = x.sibling; // current node being examined
//		BinomialHeapNode ypred = x; // y's predecessor
		BinomialHeapNode xPred = null; // predecessor of x

//		while (y != null) {
//			if (y.key < x.key) {
//				x = y;
//				xPred = ypred;
//			}
//			ypred = y;
//			y = y.sibling;
//		}

		removeFromRootList(x, xPred);
//		//    x.handle = null;
		return 0;

	}

	/**
	 * Helper method to remove a node from the root list.
	 * 
	 * param x
	 *            The node to remove from the root list.
	 * param predecessor
	 *            The predecessor of <code>x</code> in the root list, or
	 *            <code>null</code> if <code>x</code> is the first node in
	 *            the root list.
	 */
	public void removeFromRootList(BinomialHeapNode x, BinomialHeapNode predecessor) {
		// Splice out x.
		if (x == head)
			head = x.sibling;
		else
			predecessor.sibling = x.sibling;

		BinomialHeap hPrime = new BinomialHeap();

		// Reverse the order of x's children, setting hPrime.head to
		// point to the head of the resulting list.
		BinomialHeapNode z = x.child;
		while (z != null) {
			BinomialHeapNode next = z.sibling;
			z.sibling = hPrime.head;
			hPrime.head = z;
			z = next;
		}

		BinomialHeap newHeap = this.union(hPrime);
		head = newHeap.head;
	}

	/**
	 * Creates a new binomial heap that contains all the elements of two
	 * binomial heaps. One of the original binomial heaps is the object on which
	 * this method is called; the other is specified by the parameter. The two
	 * original binomial heaps should no longer be used after this operation.
	 * 
	 * <p>
	 * 
	 * param h2
	 *            The binomial heap to be merged with this one.
	 * return The new binomial heap that contains all the elements of this
	 *         binomial heap and <code>h2</code>.
	 */
	public BinomialHeap union(BinomialHeap h2) {
		BinomialHeap h = new BinomialHeap();
		h.head = THeapMerge(this, h2);
		head = null; // no longer using the...
		h2.head = null; // ...two input lists

		if (h.head == null)
			return h;

		BinomialHeapNode prevX = null;
		BinomialHeapNode x = h.head;
		BinomialHeapNode nextX = x.sibling;

		while (nextX != null) {
			if (x.degree != nextX.degree
					|| (nextX.sibling != null && nextX.sibling.degree == x.degree)) {
				// Cases 1 and 2.
				prevX = x;
				x = nextX;
			} else {
				if (x.key < nextX.key) {
					// Case 3.
					x.sibling = nextX.sibling;
					binomialLink(nextX, x);
				} else {
					// Case 4.
					if (prevX == null)
						h.head = nextX;
					else
						prevX.sibling = nextX;

					binomialLink(x, nextX);
					x = nextX;
				}
			}

			nextX = x.sibling;
		}

		return h;
	}

	/**
	 * Links one binomial tree to another.
	 * 
	 * param y
	 *            The root of one binomial tree.
	 * param z
	 *            The root of another binomial tree; this root becomes the
	 *            parent of <code>y</code>.
	 */
	private void binomialLink(BinomialHeapNode y, BinomialHeapNode z) {
		y.p = z;
		y.sibling = z.child;
		z.child = y;
		z.degree++;
	}

	/**
	 * Merges the root lists of two binomial heaps together into a single root
	 * list. The degrees in the merged root list appear in monotonically
	 * increasing order.
	 * 
	 * param h1
	 *            One binomial heap.
	 * param h2
	 *            The other binomial heap.
	 * return The head of the merged list.
	 */
	private static BinomialHeapNode THeapMerge(BinomialHeap h1, BinomialHeap h2) {
		// If either root list is empty, just return the other.
		if (h1.head == null)
			return h2.head;
		else if (h2.head == null)
			return h1.head;
		else {
			// Neither root list is empty. Scan through both, always
			// using the node whose degree is smallest of those not
			// yet taken.
			BinomialHeapNode head; // head of merged list
			BinomialHeapNode tail; // last node added to merged list
			BinomialHeapNode h1Next = h1.head, h2Next = h2.head; // next nodes to be
			// examined in h1 and h2

			if (h1.head.degree <= h2.head.degree) {
				head = h1.head;
				h1Next = h1Next.sibling;
			} else {
				head = h2.head;
				h2Next = h2Next.sibling;
			}

			tail = head;

			// Go through both root lists until one of them is
			// exhausted.
			while (h1Next != null && h2Next != null) {
				if (h1Next.degree <= h2Next.degree) {
					tail.sibling = h1Next;
					h1Next = h1Next.sibling;
				} else {
					tail.sibling = h2Next;
					h2Next = h2Next.sibling;
				}

				tail = tail.sibling;
			}

			// The above loop ended because exactly one of the root
			// lists was exhuasted. Splice the remainder of whichever
			// root list was not exhausted onto the list we're
			// constructing.
			if (h1Next != null)
				tail.sibling = h1Next;
			else
				tail.sibling = h2Next;

			return head; // all done!
		}
	}

	/**
	 * Decreases the key of a node.
	 * 
	 * param x
	 * 			The node whose key is to be decreased.
	 * param k
	 *            The new key.
	 * throws KeyUpdateException
	 *             if the new key is greater than the current key.
	 */
	public void decreaseKey(BinomialHeapNode x, int k)
			throws Exception
	{    
		// Make sure that the key value does not increase.
		if (k > x.key)
			throw new Exception();

		x.key = k; // update x's key
		bubbleUp(x, false); // bubble it up until it's in the right place
	}

	/**
	 * Bubbles the value in node up in the binomial heap. Because this procedure
	 * moves objects around within nodes, it has to update handles, too, so that
	 * the caller's idea of where a handle points is still accurate.
	 * 
	 * param x
	 *            The node whose value is to be bubbled up.
	 * param toRoot
	 *            If <code>true</code>, the value is bubbled all the way to
	 *            the root. If <code>false</code>, the value bubbles up until
	 *            its key is less than or equal to its parent's key, or until it
	 *            becomes the root.
	 * return A reference to the node in which the value originally in
	 *         <code>x</code> ends up.
	 */
	public BinomialHeapNode bubbleUp(BinomialHeapNode x, boolean toRoot) {
		BinomialHeapNode y = x;
		BinomialHeapNode z = y.p;

		while (z != null && (toRoot || y.key < z.key)) {
			// Exchange the contents of y and z, and update their
			// handles.
			int yKey = y.key;
			y.key = z.key;
			z.key = yKey;

			//      y.handle.node = z;
			//      z.handle.node = y;
			//      
			//      Handle yHandle = y.handle;
			//      y.handle = z.handle;
			//      z.handle = yHandle;

			// Go up one more level.
			y = z;
			z = y.p;
		}

		return y; // this is where x's key ended up
	}


	//		public static void main(String[] args) {
	//			BinomialHeap bh = new BinomialHeap();
	//			BinomialHeapNode n1 = new BinomialHeapNode(12);
	//			BinomialHeapNode n2 = new BinomialHeapNode(14);
	//			bh.head = n1;
	//			n1.child = n2;
	//			n1.degree = 1;
	//			n1.p = null;
	//			n1.sibling = null;
	//			n2.child = null;
	//			n2.degree = 0;
	//			n2.p = n1;
	//			n2.sibling = null;
	//			int res = bh.extractMin();
	//			System.out.println(res);
	//		}
}