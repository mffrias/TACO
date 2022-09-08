package pldi.binomialheap;


import pldi.binomialheap.BinomialHeapNode;


public class BinomialHeapNoInliningExtractMinBug7D {

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

    public BinomialHeapNoInliningExtractMinBug7D() {
    }

    /*@ requires true;
    @ ensures \old(Nodes) != null ==> \old(\reach(Nodes, BinomialHeapNode, child + sibling)).has(\result);
    @ ensures \old(Nodes) != null ==> (\forall BinomialHeapNode n; \reach(Nodes, BinomialHeapNode, child + sibling).has(n); \result.key <= n.key);
    @ ensures \old(Nodes) == null ==> \result == null;
    @ ensures (\forall BinomialHeapNode n; \reach(Nodes, BinomialHeapNode, child + sibling).has(n); \old(n.key) == n.key);
    @ signals (Exception e) false;
    @*/
    public /* @ nullable @ */pldi.binomialheap.BinomialHeapNode extractMin() {
        if (Nodes == null) {
            return null;
        }
        pldi.binomialheap.BinomialHeapNode temp = Nodes;
        pldi.binomialheap.BinomialHeapNode prevTemp = null;
        pldi.binomialheap.BinomialHeapNode minNode = null;
        minNode = Nodes.findMinNode().findMinNode().findMinNode(); //mutGenLimit 0
        //@decreasing \reach(temp, BinomialHeapNode, sibling).int_size();
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
        pldi.binomialheap.BinomialHeapNode fakeNode = temp;
        //@decreasing \reach(temp, BinomialHeapNode, sibling).int_size();
        while (temp != null) {
            temp.parent = null;
            temp = temp.sibling;
        }
        if (Nodes == null && fakeNode == null) {
            size = 0;
        } else {
            if (Nodes == null && fakeNode != null) {
                Nodes = fakeNode.reverse( null );
                size--;
            } else {
                if (Nodes != null && fakeNode == null) {
                    size--;
                } else {
                    unionNodes( fakeNode.reverse( null ) );
                    size--;
                }
            }
        }
        return minNode;
    }

    private void merge( /* @ nullable @ */pldi.binomialheap.BinomialHeapNode binHeap ) {
        pldi.binomialheap.BinomialHeapNode temp1 = Nodes;
        pldi.binomialheap.BinomialHeapNode temp2 = binHeap;
        while (temp1 != null && temp2 != null) {
            if (temp1.degree == temp2.degree) {
                pldi.binomialheap.BinomialHeapNode tmp = temp2;
                temp2 = temp2.sibling;
                tmp.sibling = temp1.sibling;
                temp1.sibling = tmp;
                temp1 = tmp.sibling;
            } else {
                if (temp1.degree < temp2.degree) {
                    if (temp1.sibling == null || temp1.sibling.degree > temp2.degree) {
                        pldi.binomialheap.BinomialHeapNode tmp = temp2;
                        temp2 = temp2.sibling;
                        tmp.sibling = temp1.sibling;
                        temp1.sibling = tmp;
                        temp1 = tmp.sibling;
                    } else {
                        temp1 = temp1.sibling;
                    }
                } else {
                    pldi.binomialheap.BinomialHeapNode tmp = temp1;
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

    private void unionNodes( /* @ nullable @ */pldi.binomialheap.BinomialHeapNode binHeap ) {
        merge( binHeap );
        pldi.binomialheap.BinomialHeapNode prevTemp = null;
        pldi.binomialheap.BinomialHeapNode temp = Nodes;
        pldi.binomialheap.BinomialHeapNode nextTemp = Nodes.sibling;
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
