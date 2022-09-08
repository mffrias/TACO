package icse.binomialheap.set5;


import icse.binomialheap.BinomialHeapNode;


public class BinomialHeapExtractMin1Bug1D {

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
    @ invariant this.size == \reach(Nodes, BinomialHeapNode, sibling + child).int_size();
    @
    @ invariant ( \forall BinomialHeapNode n; \reach(Nodes, BinomialHeapNode, sibling).has(n); (n.sibling != null ==> n.degree < n.sibling.degree) && (n.parent == null) );
    @
    @ invariant ( \forall BinomialHeapNode n; \reach(Nodes, BinomialHeapNode, sibling).has(n); n.key >= 0 );
    @
    @*/
    public /*@ nullable @*/icse.binomialheap.BinomialHeapNode Nodes;

    public int size;

    public BinomialHeapExtractMin1Bug1D() {
    }

    /*@
    @ requires true;
    @ ensures (\forall BinomialHeapNode n; \old(\reach(Nodes, BinomialHeapNode, child + sibling)).has(n); \reach(Nodes, BinomialHeapNode, child + sibling).has(n) && \old(n.key) == n.key);
    @ ensures value > 0 ==> (\exists BinomialHeapNode n; !\old(\reach(Nodes, BinomialHeapNode, child + sibling)).has(n); \reach(Nodes, BinomialHeapNode, child + sibling).has(n) && n.key == value);
    @ ensures value > 0 ==> size == \old(size) + 1;
    @ signals (Exception e) false;
    @
    @*/
    public void insert( int value ) {
        if (value > 0) {
            icse.binomialheap.BinomialHeapNode insertTemp = new icse.binomialheap.BinomialHeapNode();
            insertTemp.key = value;
            if (Nodes == null) {
                Nodes = insertTemp;
                size = 1;
            } else {
                icse.binomialheap.BinomialHeapNode temp1 = Nodes;
                icse.binomialheap.BinomialHeapNode temp2 = insertTemp;
                //@decreasing \reach(temp2, BinomialHeapNode, sibling).int_size();
                while (temp1 != null && temp2 != null) {
                    if (temp1.degree == temp2.degree) {
                        icse.binomialheap.BinomialHeapNode tmp = temp2;
                        temp2 = temp2.sibling;
                        tmp.sibling = temp1.sibling;
                        temp1.sibling = tmp;
                        temp1 = tmp.sibling;
                    } else {
                        if (temp1.degree < temp2.degree) {
                            if (temp1.sibling == null || temp1.sibling.degree > temp2.degree) {
                                icse.binomialheap.BinomialHeapNode tmp = temp2;
                                temp2 = temp2.sibling;
                                tmp.sibling = temp1.sibling;
                                temp1.sibling = tmp;
                                temp1 = tmp.sibling;
                            } else {
                                temp1 = temp1.sibling;
                            }
                        } else {
                            icse.binomialheap.BinomialHeapNode tmp = temp1;
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
                    //@decreasing \reach(temp1, BinomialHeapNode, sibling).int_size();
                    while (temp1.sibling != null) {
                        temp1 = temp1.sibling;
                    }
                    temp1.sibling = temp2;
                }
                icse.binomialheap.BinomialHeapNode prevTemp = null;
                icse.binomialheap.BinomialHeapNode temp = Nodes;
                icse.binomialheap.BinomialHeapNode nextTemp = Nodes.sibling;
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
                            temp.parent = nextTemp;
                            temp.sibling = nextTemp.child;
                            nextTemp.child = temp;
                            nextTemp.degree++;
                            temp = nextTemp;
                        }
                    }
                    nextTemp = temp.sibling;
                }
                size++;
            }
        }
    }

    /*@ requires true;
    @ ensures \old(Nodes) != null ==> \old(\reach(Nodes, BinomialHeapNode, child + sibling)).has(\result);
    @ ensures (\forall BinomialHeapNode n; \reach(Nodes, BinomialHeapNode, child + sibling).has(n); \result.key <= n.key);
    @ ensures (\forall BinomialHeapNode n; \reach(Nodes, BinomialHeapNode, child + sibling).has(n); \old(n.key) == n.key);
    @*/
    public /* @ nullable @ */icse.binomialheap.BinomialHeapNode extractMin() {
        if (Nodes.child == this.Nodes) { //mutGenLimit 0
            return null;
        }
        icse.binomialheap.BinomialHeapNode temp = Nodes;
        icse.binomialheap.BinomialHeapNode prevTemp = null;
        icse.binomialheap.BinomialHeapNode minNode = null;
        minNode = Nodes.findMinNode();
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
        icse.binomialheap.BinomialHeapNode fakeNode = temp;
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

    // 3. Unite two binomial heaps
    // helper procedure
    private void merge( /* @ nullable @ */icse.binomialheap.BinomialHeapNode binHeap ) {
        icse.binomialheap.BinomialHeapNode temp1 = Nodes;
        icse.binomialheap.BinomialHeapNode temp2 = binHeap;
        while (temp1 != null && temp2 != null) {
            if (temp1.degree == temp2.degree) {
                icse.binomialheap.BinomialHeapNode tmp = temp2;
                temp2 = temp2.sibling;
                tmp.sibling = temp1.sibling;
                temp1.sibling = tmp;
                temp1 = tmp.sibling;
            } else {
                if (temp1.degree < temp2.degree) {
                    if (temp1.sibling == null || temp1.sibling.degree > temp2.degree) {
                        icse.binomialheap.BinomialHeapNode tmp = temp2;
                        temp2 = temp2.sibling;
                        tmp.sibling = temp1.sibling;
                        temp1.sibling = tmp;
                        temp1 = tmp.sibling;
                    } else {
                        temp1 = temp1.sibling;
                    }
                } else {
                    icse.binomialheap.BinomialHeapNode tmp = temp1;
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
    private void unionNodes( /* @ nullable @ */icse.binomialheap.BinomialHeapNode binHeap ) {
        merge( binHeap );
        icse.binomialheap.BinomialHeapNode prevTemp = null;
        icse.binomialheap.BinomialHeapNode temp = Nodes;
        icse.binomialheap.BinomialHeapNode nextTemp = Nodes.sibling;
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

    /*@ requires Nodes != null;
    @ ensures (\exists BinomialHeapNode n; \reach(Nodes, BinomialHeapNode, child + sibling).has(n); n.key == \result);
    @ ensures (\forall BinomialHeapNode n; \reach(Nodes, BinomialHeapNode, child + sibling).has(n); \result <= n.key);
    @ ensures (\forall BinomialHeapNode n; \reach(Nodes, BinomialHeapNode, child + sibling).has(n); \old(n.key) == n.key);
    @ ensures (\forall BinomialHeapNode n; \reach(Nodes, BinomialHeapNode, child + sibling).has(n); \old(n.degree) == n.degree);
    @ ensures (\forall BinomialHeapNode n; \reach(Nodes, BinomialHeapNode, child + sibling).has(n); \old(n.parent) == n.parent);
    @ ensures (\forall BinomialHeapNode n; \reach(Nodes, BinomialHeapNode, child + sibling).has(n); \old(n.sibling) == n.sibling);
    @ ensures (\forall BinomialHeapNode n; \reach(Nodes, BinomialHeapNode, child + sibling).has(n); \old(n.child) == n.child);
    @ signals (Exception e) false;
    @*/
    public int findMinimum() {
        icse.binomialheap.BinomialHeapNode x = Nodes;
        icse.binomialheap.BinomialHeapNode y = Nodes;
        int min = x.key;
        //@decreasing \reach(x, BinomialHeapNode, sibling).int_size();
        while (x != null) {
            if (x.key < min) {
                y = x;
                min = x.key;
            }
            x = x.sibling;
        }
        return y.key;
    }

}
