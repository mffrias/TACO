package ase2016.binomialheap;



import ase2016.binomialheap.BinomialHeapNode;


public class BinomialHeapUnionNodes2Bugs {

	/*@
	 @ invariant true;
	 @*/
	
    public /*@ nullable @*/ase2016.binomialheap.BinomialHeapNode Nodes;

    public int size;

    public BinomialHeapUnionNodes2Bugs() {
    }



    // 3. Unite two binomial heaps
    // helper procedure
    private void merge( /* @ nullable @ */ase2016.binomialheap.BinomialHeapNode binHeap ) {
    	ase2016.binomialheap.BinomialHeapNode mix = null;
    	ase2016.binomialheap.BinomialHeapNode current = null;
        ase2016.binomialheap.BinomialHeapNode temp1 = Nodes;
        ase2016.binomialheap.BinomialHeapNode temp2 = binHeap;
        if (temp1 == null) {
        	mix = temp2;        	
        }
        else {
        	if (temp2 == null) {
            	mix = temp1;
        	}
        	else {
        		if (temp1.degree <= temp2.degree) {
        			mix = temp1;
        			temp1 = temp1.sibling;
        		}
        		else {
        			mix = temp2;
        			temp2 = temp2.sibling;
        		}
        		current = mix;
        		while (temp1 != null && temp2 != null) {
                    if (temp1.degree <= temp2.degree) {
                    	current.sibling = temp1;
                    	current = current.sibling;
                    	temp1 = temp1.sibling;
                    } else {
                    	current.sibling = temp2;
                    	current = current.sibling;
                    	temp2 = temp2.sibling;
                    }
        		}
        		while (temp1 != null) {
        			current.sibling = temp1;
        			current = current.sibling;
        			temp1 = temp1.sibling;
        		}
        		while (temp2 != null) {
        			current.sibling = temp2;
        			current = current.sibling;
        			temp2 = temp2.sibling;
        		}
        		current.sibling = null;	
        	}
        }
        Nodes = mix;
    }

    /*@
    @ requires (\forall BinomialHeapNode n; \reach(Nodes, BinomialHeapNode, sibling + child).has(n); n.parent != null ==> n.key >= n.parent.key );
    @ requires (\forall BinomialHeapNode n; \reach(Nodes, BinomialHeapNode, sibling + child).has(n); n.sibling != null ==> \reach(n.sibling, BinomialHeapNode, sibling + child).has(n) == false );
    @ requires (\forall BinomialHeapNode n; \reach(Nodes, BinomialHeapNode, sibling + child).has(n); n.child != null ==> \reach(n.child, BinomialHeapNode, sibling + child).has(n) == false );
    @ requires (\forall BinomialHeapNode n; \reach(Nodes, BinomialHeapNode, sibling + child).has(n); ( n.child != null && n.sibling != null ) ==>
    @                                                  (\forall BinomialHeapNode m; \reach(n.child, BinomialHeapNode, child + sibling).has(m); \reach(n.sibling, BinomialHeapNode, child + sibling).has(m) == false) );
    @ requires (\forall BinomialHeapNode n; \reach(Nodes, BinomialHeapNode, sibling + child).has(n); ( n.child != null && n.sibling != null ) ==>
    @                                                  (\forall BinomialHeapNode m; \reach(n.sibling, BinomialHeapNode, child + sibling).has(m); \reach(n.child, BinomialHeapNode, child + sibling).has(m) == false) );
    @ requires (\forall BinomialHeapNode n; \reach(Nodes, BinomialHeapNode, sibling + child).has(n); n.degree >= 0 );
    @ requires (\forall BinomialHeapNode n; \reach(Nodes, BinomialHeapNode, sibling + child).has(n); n.child == null ==> n.degree == 0 );
    @ requires (\forall BinomialHeapNode n; \reach(Nodes, BinomialHeapNode, sibling + child).has(n); n.child != null ==> n.degree == \reach(n.child, BinomialHeapNode, sibling).int_size() );
    @ requires (\forall BinomialHeapNode n; \reach(Nodes, BinomialHeapNode, sibling + child).has(n); n.child != null ==> \reach(n.child.child, BinomialHeapNode, child + sibling).int_size() == \reach(n.child.sibling, BinomialHeapNode, child + sibling).int_size() );
    @ requires (\forall BinomialHeapNode n; \reach(Nodes, BinomialHeapNode, sibling + child).has(n); n.child != null ==>
    @                                                  ( \forall BinomialHeapNode m; \reach(n.child, BinomialHeapNode, sibling).has(m); m.parent == n ) );
    @ requires (\forall BinomialHeapNode n; \reach(Nodes, BinomialHeapNode, sibling + child).has(n); ( n.sibling != null && n.parent != null ) ==> n.degree > n.sibling.degree );
    @
    @ requires this.size == \reach(Nodes, BinomialHeapNode, sibling + child).int_size();
    @
    @ requires ( \forall BinomialHeapNode n; \reach(Nodes, BinomialHeapNode, sibling).has(n); (n.sibling != null ==> n.degree < n.sibling.degree) && (n.parent == null) );
    @
    @ requires ( \forall BinomialHeapNode n; \reach(Nodes, BinomialHeapNode, sibling).has(n); n.key >= 0 );
    @ requires (\forall BinomialHeapNode n; \reach(binHeap, BinomialHeapNode, sibling + child).has(n); n.parent != null ==> n.key >= n.parent.key );
    @ requires (\forall BinomialHeapNode n; \reach(binHeap, BinomialHeapNode, sibling + child).has(n); n.sibling != null ==> \reach(n.sibling, BinomialHeapNode, sibling + child).has(n) == false );
    @ requires (\forall BinomialHeapNode n; \reach(binHeap, BinomialHeapNode, sibling + child).has(n); n.child != null ==> \reach(n.child, BinomialHeapNode, sibling + child).has(n) == false );
    @ requires (\forall BinomialHeapNode n; \reach(binHeap, BinomialHeapNode, sibling + child).has(n); ( n.child != null && n.sibling != null ) ==>
    @                                                  (\forall BinomialHeapNode m; \reach(n.child, BinomialHeapNode, child + sibling).has(m); \reach(n.sibling, BinomialHeapNode, child + sibling).has(m) == false) );
    @ requires (\forall BinomialHeapNode n; \reach(binHeap, BinomialHeapNode, sibling + child).has(n); ( n.child != null && n.sibling != null ) ==>
    @                                                  (\forall BinomialHeapNode m; \reach(n.sibling, BinomialHeapNode, child + sibling).has(m); \reach(n.child, BinomialHeapNode, child + sibling).has(m) == false) );
    @ requires (\forall BinomialHeapNode n; \reach(binHeap, BinomialHeapNode, sibling + child).has(n); n.degree >= 0 );
    @ requires (\forall BinomialHeapNode n; \reach(binHeap, BinomialHeapNode, sibling + child).has(n); n.child == null ==> n.degree == 0 );
    @ requires (\forall BinomialHeapNode n; \reach(binHeap, BinomialHeapNode, sibling + child).has(n); n.child != null ==> n.degree == \reach(n.child, BinomialHeapNode, sibling).int_size() );
    @ requires (\forall BinomialHeapNode n; \reach(binHeap, BinomialHeapNode, sibling + child).has(n); n.child != null ==> \reach(n.child.child, BinomialHeapNode, child + sibling).int_size() == \reach(n.child.sibling, BinomialHeapNode, child + sibling).int_size() );
    @ requires (\forall BinomialHeapNode n; \reach(binHeap, BinomialHeapNode, sibling + child).has(n); n.child != null ==>
    @                                                  ( \forall BinomialHeapNode m; \reach(n.child, BinomialHeapNode, sibling).has(m); m.parent == n ) );
    @ requires (\forall BinomialHeapNode n; \reach(binHeap, BinomialHeapNode, sibling + child).has(n); ( n.sibling != null && n.parent != null ) ==> n.degree > n.sibling.degree );
    @
    @
    @ requires ( \forall BinomialHeapNode n; \reach(binHeap, BinomialHeapNode, sibling).has(n); (n.sibling != null ==> n.degree < n.sibling.degree) && (n.parent == null) );
    @
    @ requires ( \forall BinomialHeapNode n; \reach(binHeap, BinomialHeapNode, sibling).has(n); n.key >= 0 );
    @ requires ( \forall BinomialHeapNode n; \reach(binHeap, BinomialHeapNode, sibling+child).has(n); !\reach(Nodes, BinomialHeapNode, sibling+child).has(n));
    @ requires ( \forall BinomialHeapNode n; \reach(Nodes, BinomialHeapNode, sibling+child).has(n); !\reach(binHeap, BinomialHeapNode, sibling+child).has(n));
	@ ensures (\forall BinomialHeapNode n; \reach(Nodes, BinomialHeapNode, sibling + child).has(n); n.parent != null ==> n.key >= n.parent.key );
    @ ensures (\forall BinomialHeapNode n; \reach(Nodes, BinomialHeapNode, sibling + child).has(n); n.sibling != null ==> \reach(n.sibling, BinomialHeapNode, sibling + child).has(n) == false );
    @ ensures (\forall BinomialHeapNode n; \reach(Nodes, BinomialHeapNode, sibling + child).has(n); n.child != null ==> \reach(n.child, BinomialHeapNode, sibling + child).has(n) == false );
    @ ensures (\forall BinomialHeapNode n; \reach(Nodes, BinomialHeapNode, sibling + child).has(n); ( n.child != null && n.sibling != null ) ==>
    @                                                  (\forall BinomialHeapNode m; \reach(n.child, BinomialHeapNode, child + sibling).has(m); \reach(n.sibling, BinomialHeapNode, child + sibling).has(m) == false) );
    @ ensures (\forall BinomialHeapNode n; \reach(Nodes, BinomialHeapNode, sibling + child).has(n); ( n.child != null && n.sibling != null ) ==>
    @                                                  (\forall BinomialHeapNode m; \reach(n.sibling, BinomialHeapNode, child + sibling).has(m); \reach(n.child, BinomialHeapNode, child + sibling).has(m) == false) );
    @ ensures (\forall BinomialHeapNode n; \reach(Nodes, BinomialHeapNode, sibling + child).has(n); n.degree >= 0 );
    @ ensures (\forall BinomialHeapNode n; \reach(Nodes, BinomialHeapNode, sibling + child).has(n); n.child == null ==> n.degree == 0 );
    @ ensures (\forall BinomialHeapNode n; \reach(Nodes, BinomialHeapNode, sibling + child).has(n); n.child != null ==> n.degree == \reach(n.child, BinomialHeapNode, sibling).int_size() );
    @ ensures (\forall BinomialHeapNode n; \reach(Nodes, BinomialHeapNode, sibling + child).has(n); n.child != null ==> \reach(n.child.child, BinomialHeapNode, child + sibling).int_size() == \reach(n.child.sibling, BinomialHeapNode, child + sibling).int_size() );
    @ ensures (\forall BinomialHeapNode n; \reach(Nodes, BinomialHeapNode, sibling + child).has(n); n.child != null ==>
    @                                                  ( \forall BinomialHeapNode m; \reach(n.child, BinomialHeapNode, sibling).has(m); m.parent == n ) );
    @ ensures (\forall BinomialHeapNode n; \reach(Nodes, BinomialHeapNode, sibling + child).has(n); ( n.sibling != null && n.parent != null ) ==> n.degree > n.sibling.degree );
    @ ensures ( \forall BinomialHeapNode n; \reach(Nodes, BinomialHeapNode, sibling).has(n); (n.sibling != null ==> n.degree < n.sibling.degree) && (n.parent == null) );
    @ ensures ( \forall BinomialHeapNode n; \reach(Nodes, BinomialHeapNode, sibling).has(n); n.key >= 0 );
    @ ensures ( \forall BinomialHeapNode n; \old(\reach(Nodes, BinomialHeapNode, sibling + child)).has(n); \reach(Nodes, BinomialHeapNode, sibling + child).has(n));
    @ ensures ( \forall BinomialHeapNode n; \old(\reach(binHeap, BinomialHeapNode, sibling + child)).has(n); \reach(Nodes, BinomialHeapNode, sibling + child).has(n));
    @*/
    public void unionNodes( /* @ nullable @ */ase2016.binomialheap.BinomialHeapNode binHeap ) {
        merge( binHeap );
        ase2016.binomialheap.BinomialHeapNode prevTemp = null;
        ase2016.binomialheap.BinomialHeapNode temp = null; //mutGenLimit 1
        ase2016.binomialheap.BinomialHeapNode nextTemp = Nodes.sibling; 
        //@decreasing \reach(temp, BinomialHeapNode, sibling).int_size();
        while (nextTemp != null) {
            if (temp.degree != nextTemp.degree || nextTemp.sibling != null && nextTemp.sibling.degree == temp.degree) {
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
                    temp.parent = prevTemp; //mutGenLimit 1
                    temp.sibling = nextTemp.child;
                    nextTemp.child = temp;
                    nextTemp.degree++;
                    temp = prevTemp; 
                }
            }
            nextTemp = temp.sibling; 
        }
    }


}
