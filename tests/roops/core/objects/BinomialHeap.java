package roops.core.objects;

import java.lang.reflect.Field;

public class BinomialHeap {

	/*@
    @ invariant (\forall BinomialHeapNode n; \reach(Nodes, BinomialHeapNode, sibling + child).has(n) == true; n.parent != null ==> n.key >= n.parent.key );
    @ invariant (\forall BinomialHeapNode n; \reach(Nodes, BinomialHeapNode, sibling + child).has(n) == true; n.sibling != null ==> \reach(n.sibling, BinomialHeapNode, sibling + child).has(n) == false );
    @ invariant (\forall BinomialHeapNode n; \reach(Nodes, BinomialHeapNode, sibling + child).has(n) == true; n.child != null ==> \reach(n.child, BinomialHeapNode, sibling + child).has(n) == false );
    @ invariant (\forall BinomialHeapNode n; \reach(Nodes, BinomialHeapNode, sibling + child).has(n) == true; ( n.child != null && n.sibling != null ) ==>
    @                                                  (\forall BinomialHeapNode m; \reach(n.child, BinomialHeapNode, child + sibling).has(m) == true; \reach(n.sibling, BinomialHeapNode, child + sibling).has(m) == false) );
    @ invariant (\forall BinomialHeapNode n; \reach(Nodes, BinomialHeapNode, sibling + child).has(n) == true; ( n.child != null && n.sibling != null ) ==>
    @                                                  (\forall BinomialHeapNode m; \reach(n.sibling, BinomialHeapNode, child + sibling).has(m); \reach(n.child, BinomialHeapNode, child + sibling).has(m) == false) );
    @ invariant (\forall BinomialHeapNode n; \reach(Nodes, BinomialHeapNode, sibling + child).has(n) == true; n.degree >= 0 );
    @ invariant (\forall BinomialHeapNode n; \reach(Nodes, BinomialHeapNode, sibling + child).has(n) == true; n.child == null ==> n.degree == 0 );
    @ invariant (\forall BinomialHeapNode n; \reach(Nodes, BinomialHeapNode, sibling + child).has(n) == true; n.child != null ==> n.degree == \reach(n.child, BinomialHeapNode, sibling).int_size() );
    @ invariant (\forall BinomialHeapNode n; \reach(Nodes, BinomialHeapNode, sibling + child).has(n) == true; n.child != null ==> \reach(n.child.child, BinomialHeapNode, child + sibling).int_size() == \reach(n.child.sibling, BinomialHeapNode, child + sibling).int_size() );
    @ invariant (\forall BinomialHeapNode n; \reach(Nodes, BinomialHeapNode, sibling + child).has(n) == true; n.child != null ==>
    @                                                  ( \forall BinomialHeapNode m; \reach(n.child, BinomialHeapNode, sibling).has(m) == true; m.parent == n ) );
    @ invariant (\forall BinomialHeapNode n; \reach(Nodes, BinomialHeapNode, sibling + child).has(n) == true; ( n.sibling != null && n.parent != null ) ==> n.degree > n.sibling.degree );
    @
    @ invariant this.size == \reach(Nodes, BinomialHeapNode, sibling + child).int_size();
    @
    @ invariant ( \forall BinomialHeapNode n; \reach(Nodes, BinomialHeapNode, sibling).has(n) == true; (n.sibling != null ==> n.degree < n.sibling.degree) && (n.parent == null) );
    @
    @ invariant ( \forall BinomialHeapNode n; \reach(Nodes, BinomialHeapNode, sibling).has(n) == true; n.key >= 0 );
    @
    @*/
	public /*@ nullable @*/BinomialHeapNode Nodes;

	public int size;

	public BinomialHeap() {
	}

	/*@
    @ requires true;
    @ ensures (\forall BinomialHeapNode n; \old(\reach(Nodes, BinomialHeapNode, child + sibling)).has(n) == true; \reach(Nodes, BinomialHeapNode, child + sibling).has(n) == true && \old(n.key) == n.key);
    @ ensures value > 0 ==> (\exists BinomialHeapNode n; \old(\reach(Nodes, BinomialHeapNode, child + sibling)).has(n) == false; \reach(Nodes, BinomialHeapNode, child + sibling).has(n) && n.key == value);
    @ ensures value > 0 ==> size == \old(size) + 1;
    @ signals (Exception e) false;
    @
    @*/
	public void insert(int value) {
		if (value > 0) {

			BinomialHeapNode temp = new BinomialHeapNode();
			temp.key = value;

			if (Nodes == null) {

				Nodes = temp;
				size = 1;
			} else {

				unionNodes(temp);
				size++;
			}
		} 
	}



	private static BinomialHeapNode findMinNode(BinomialHeapNode arg) {
		BinomialHeapNode x = arg;
		BinomialHeapNode y = arg;
		int min = x.key;

		while (x != null) {

			if (x.key < min) {
				y = x;
				min = x.key;
			}
			x = x.sibling;
		}

		return y;
	}

	


	/*@ requires true;
    @ ensures \old(Nodes) != null ==> \old(\reach(Nodes, BinomialHeapNode, child + sibling)).has(\result) == true;
    @ ensures (\forall BinomialHeapNode n; \reach(Nodes, BinomialHeapNode, child + sibling).has(n) == true; \result.key <= n.key);
    @ ensures (\forall BinomialHeapNode n; \reach(Nodes, BinomialHeapNode, child + sibling).has(n) == true; \old(n.key) == n.key);
    @ signals (Exception e) false;
    @*/
	public /* @ nullable @ */BinomialHeapNode extractMin() {
//		boolean cover = true;
		if (Nodes == null) 
			return null;

		BinomialHeapNode temp = Nodes;
		BinomialHeapNode prevTemp = null;
		BinomialHeapNode minNode = findMinNode(Nodes);
		while (temp.key != minNode.key) {
			prevTemp = temp;
			temp = temp.sibling;
		}

		if (prevTemp == null) {
			Nodes = temp.sibling;
		} else {
			prevTemp.sibling = temp.sibling;
		}
		temp = temp.child;
		BinomialHeapNode fakeNode = temp;
		while (temp != null) {
			temp.parent = null;
			temp = temp.sibling;
		}

		if ((Nodes == null) && (fakeNode == null)) {
			size = 0;
		} else {
			if ((Nodes == null) && (fakeNode != null)) {
				Nodes = fakeNode.reverse(null);
//				cover = false;
				size--;
			} else {
				if ((Nodes != null) && (fakeNode == null)) {
					size--;
				} else {
					unionNodes(fakeNode.reverse(null));
					size--;
				}
			}
		}

		return minNode;
	}
	
	
	


	// 3. Unite two binomial heaps
	// helper procedure
	private void merge( /* @ nullable @ */BinomialHeapNode binHeap ) {

		BinomialHeapNode temp1 = Nodes;
		BinomialHeapNode temp2 = binHeap;
		while ((temp1 != null) && (temp2 != null)) {
			if (temp1.degree == temp2.degree) {
				BinomialHeapNode tmp = temp2;
				temp2 = temp2.sibling;
				tmp.sibling = temp1.sibling;
				temp1.sibling = tmp;
				temp1 = tmp.sibling;
			} else {
				if (temp1.degree < temp2.degree) {
					if ((temp1.sibling == null)
							|| (temp1.sibling.degree > temp2.degree)) {
						BinomialHeapNode tmp = temp2;
						temp2 = temp2.sibling;
						tmp.sibling = temp1.sibling;
						temp1.sibling = tmp;
						temp1 = tmp.sibling;
					} else {
						temp1 = temp1.sibling;
					}
				} else {
					BinomialHeapNode tmp = temp1;
					temp1 = temp2;
					temp2 = temp2.sibling;
					temp1.sibling = tmp;
					if (tmp == Nodes) {
						Nodes = temp1;
					} 
				}
			}
		}

		if (temp1 == null) {
			temp1 = Nodes;
			while (temp1.sibling != null) {
				temp1 = temp1.sibling;
			}
			temp1.sibling = temp2;
		} 

	}

	// another helper procedure
	private void unionNodes( /* @ nullable @ */BinomialHeapNode binHeap ) {
		merge(binHeap);

		BinomialHeapNode prevTemp = null;
		BinomialHeapNode temp = Nodes;
		BinomialHeapNode nextTemp = Nodes.sibling;

		while (nextTemp != null) {
			if ((temp.degree != nextTemp.degree)
					|| ((nextTemp.sibling != null) && (nextTemp.sibling.degree == temp.degree))) {
				prevTemp = temp;
				temp = nextTemp;
			} else {
				if (temp.key <= nextTemp.key) {
					temp.sibling = nextTemp.sibling;
					nextTemp.parent = temp;
					nextTemp.sibling = temp.child;
					temp.child = nextTemp;
					temp.degree++;
				} else {
					if (prevTemp == null) {
						Nodes = nextTemp;
					} else {
						prevTemp.sibling = nextTemp;
					}
					temp.parent = nextTemp;
					temp.sibling = nextTemp.child;
					nextTemp.child = temp;
					nextTemp.degree++;
					temp = nextTemp;
				}
			}

			nextTemp = temp.sibling;
		}
	}

	/*@ requires Nodes != null;
    @ ensures (\exists BinomialHeapNode n; \reach(Nodes, BinomialHeapNode, child + sibling).has(n) == true; n.key == \result);
    @ ensures (\forall BinomialHeapNode n; \reach(Nodes, BinomialHeapNode, child + sibling).has(n) == true; \result <= n.key);
    @ ensures (\forall BinomialHeapNode n; \reach(Nodes, BinomialHeapNode, child + sibling).has(n) == true; \old(n.key) == n.key);
    @ ensures (\forall BinomialHeapNode n; \reach(Nodes, BinomialHeapNode, child + sibling).has(n) == true; \old(n.degree) == n.degree);
    @ ensures (\forall BinomialHeapNode n; \reach(Nodes, BinomialHeapNode, child + sibling).has(n) == true; \old(n.parent) == n.parent);
    @ ensures (\forall BinomialHeapNode n; \reach(Nodes, BinomialHeapNode, child + sibling).has(n) == true; \old(n.sibling) == n.sibling);
    @ ensures (\forall BinomialHeapNode n; \reach(Nodes, BinomialHeapNode, child + sibling).has(n) == true; \old(n.child) == n.child);
    @ signals (Exception e) false;
    @*/
	public int findMinimum() {
		BinomialHeapNode x = Nodes;
		BinomialHeapNode y = Nodes;
		int min = x.key; 
		//decreasing \reach(x, BinomialHeapNode, sibling).int_size();
		while (x != null) {
			if (x.key < min) { 
				y = x; 
				min = x.key; 
			}
			x = x.sibling;
		}
		return y.key;
	}
	
	
//	public static void main(String[] args) {
//    roops.core.objects.BinomialHeap instance = new roops.core.objects.BinomialHeap();
//    roops.core.objects.BinomialHeapNode _BinomialHeapNode_1 = new roops.core.objects.BinomialHeapNode();
//    roops.core.objects.BinomialHeapNode _BinomialHeapNode_2 = new roops.core.objects.BinomialHeapNode();
//    // Fields Initialization for 'instance'
//    // Fields Initialization for '_BinomialHeapNode_1'
//    _BinomialHeapNode_1.key = 0;
//    _BinomialHeapNode_1.degree = 1;
//    _BinomialHeapNode_1.parent = null;
//    _BinomialHeapNode_1.sibling = null;
//    // Fields Initialization for '_BinomialHeapNode_2'
//    _BinomialHeapNode_2.key = 9;
//    _BinomialHeapNode_2.degree = 0;
//    _BinomialHeapNode_2.parent = _BinomialHeapNode_1;
//    _BinomialHeapNode_2.sibling = null;
//    _BinomialHeapNode_2.child = null;
//    _BinomialHeapNode_1.child = _BinomialHeapNode_2;
//    instance.Nodes = _BinomialHeapNode_1;
//    instance.size  = 2;
//    boolean covered = instance.extractMin();
//}

}
