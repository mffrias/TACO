package pldi.binomialheap;


import pldi.binomialheap.BinomialHeapNode;


public class BinomialHeapNoInliningExtractMinBug7Dx21Dx10Dx20Ix15Ix27Dx38 {

    /*@
    @ invariant (\forall BinomialHeapNode n; \reach(Nodes, BinomialHeapNode, sibling + child).has(n); n.parent != null ==> n.key >= n.parent.key );
    @ invariant (\forall BinomialHeapNode n; \reach(Nodes, BinomialHeapNode, sibling + child).has(n); n.sibling != null ==> \reach(n.sibling, BinomialHeapNode, sibling + child).has(n) == false );
    @ invariant (\forall BinomialHeapNode n; \reach(Nodes, BinomialHeapNode, sibling + child).has(n); n.child != null ==> \reach(n.child, BinomialHeapNode, sibling + child).has(n) == false );
    @ invariant (\forall BinomialHeapNode n; \reach(Nodes, BinomialHeapNode, sibling + child).has(n); ( n.child != null && n.sibling != null ) ==>
    @                                                  (\forall BinomialHeapNode m; \reach(n.child, BinomialHeapNode, child + sibling).has(m); \reach(n.sibling, BinomialHeapNode, child + sibling).has(m) == false) );
    @ invariant (\forall BinomialHeapNode n; \reach(Nodes, BinomialHeapNode, sibling + child).has(n); ( n.child != null && n.sibling != null ) ==>
    @                                                  (\forall BinomialHeapNode m; \reach(n.sibling, BinomialHeapNode, child + sibling).has(m); \reach(n.child, BinomialHeapNode, child + sibling).has(m) == false) );
    @ invariant (\forall BinomialHeapNode n; \reach(Nodes, BinomialHeapNode, sibling + child).has(n); n.degree >= 0 );
    @ invariant (\forall BinomialHeapNode n; \reach(Nodes, BinomialHeapNode, sibling + child).has(n); n.child == null ==> n.degree == 0 );
    @ invariant (\forall BinomialHeapNode n; \reach(Nodes, BinomialHeapNode, sibling + child).has(n); n.child != null ==> n.degree == \reach(n.child, BinomialHeapNode, sibling).int_size() );
    @ invariant (\forall BinomialHeapNode n; \reach(Nodes, BinomialHeapNode, sibling + child).has(n); n.child != null ==> \reach(n.child.child, BinomialHeapNode, child + sibling).int_size() == \reach(n.child.sibling, BinomialHeapNode, child + sibling).int_size() );
    @ invariant (\forall BinomialHeapNode n; \reach(Nodes, BinomialHeapNode, sibling + child).has(n); n.child != null ==>
    @                                                  ( \forall BinomialHeapNode m; \reach(n.child, BinomialHeapNode, sibling).has(m); m.parent == n ) );
    @ invariant (\forall BinomialHeapNode n; \reach(Nodes, BinomialHeapNode, sibling + child).has(n); ( n.sibling != null && n.parent != null ) ==> n.degree > n.sibling.degree );
    @
    @
    @ invariant ( \forall BinomialHeapNode n; \reach(Nodes, BinomialHeapNode, sibling).has(n); (n.sibling != null ==> n.degree < n.sibling.degree) && (n.parent == null) );
    @
    @*/




    public /*@ nullable @*/pldi.binomialheap.BinomialHeapNode Nodes;

    public int size;

    public BinomialHeapNoInliningExtractMinBug7Dx21Dx10Dx20Ix15Ix27Dx38 () {
    }


    
    /*@ requires true;
    @ ensures \old(Nodes) != null ==> \old(\reach(Nodes, BinomialHeapNode, child + sibling)).has(\result);
    @ ensures \old(Nodes) != null ==> (\forall BinomialHeapNode n; \reach(Nodes, BinomialHeapNode, child + sibling).has(n); \result.key <= n.key);
    @ ensures \old(Nodes) == null ==> \result == null;
    @ ensures (\forall BinomialHeapNode n; \reach(Nodes, BinomialHeapNode, child + sibling).has(n); \old(n.key) == n.key);
    @ signals (Exception e) false;
    @*/
    public/* @ nullable @ */BinomialHeapNode extractMin() {
        if (Nodes == null) { //mutGenLimit 0
            return null; //mutGenLimit 0
        }
        BinomialHeapNode temp = Nodes; //mutGenLimit 0
        BinomialHeapNode prevTemp = null; //mutGenLimit 0
        BinomialHeapNode minNode = null; //mutGenLimit 0
        minNode = Nodes.findMinNode().sibling; //mutGenLimit 1
        //@decreasing \reach(temp, BinomialHeapNode, sibling).int_size();
        while (temp.key != minNode.key) { //mutGenLimit 0
            prevTemp = temp; //mutGenLimit 0
            temp = temp.findMinNode().child; //mutGenLimit 1
        }
        if (prevTemp == null) { //mutGenLimit 0
            Nodes = temp.sibling; //mutGenLimit 0
        } else {
        	prevTemp.sibling.parent = temp.sibling; //mutGenLimit 1
        }
        temp = temp.child; //mutGenLimit 0
        BinomialHeapNode fakeNode = temp; //mutGenLimit 0
        //@decreasing \reach(temp, BinomialHeapNode, sibling).int_size();
        while (temp != null) { //mutGenLimit 0
        	temp.findMinNode().parent = null; //mutGenLimit 1
            temp = temp.sibling.findMinNode(); //mutGenLimit 1
        }
        if ((Nodes == null) && (fakeNode == null)) { //mutGenLimit 0
            size = 0; //mutGenLimit 0
        } else {
            if ((Nodes == null) && (fakeNode != null)) { //mutGenLimit 0
                Nodes = fakeNode.reverse( null ).parent; //mutGenLimit 1
                size--; //mutGenLimit 0
            } else {
                if ((Nodes != null) && (fakeNode == null)) { //mutGenLimit 0
                    size--; //mutGenLimit 0
                } else {
                    unionNodes(fakeNode.reverse(null)); //mutGenLimit 0
                    size--; //mutGenLimit 0
                }
            }
        }
        return minNode.parent; //mutGenLimit 1
    }

    
    private void merge(/* @ nullable @ */BinomialHeapNode binHeap) {
        BinomialHeapNode temp1 = Nodes, temp2 = binHeap;
        while ((temp1 != null) && (temp2 != null)) {
            if (temp1.degree == temp2.degree) {
                BinomialHeapNode tmp = temp2;
                temp2 = temp2.sibling;
                tmp.sibling = temp1.sibling;
                temp1.sibling = tmp;
                temp1 = tmp.sibling;
            } else {
                if (temp1.degree < temp2.degree) {
                    if ((temp1.sibling == null) || (temp1.sibling.degree > temp2.degree)) {
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
    
    
    private void unionNodes(/* @ nullable @ */BinomialHeapNode binHeap) {
        merge(binHeap);

        BinomialHeapNode prevTemp = null, temp = Nodes, nextTemp = Nodes.sibling;

        while (nextTemp != null) {
            if ((temp.degree != nextTemp.degree) || ((nextTemp.sibling != null) && (nextTemp.sibling.degree == temp.degree))) {
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
    
    
    
    
}
