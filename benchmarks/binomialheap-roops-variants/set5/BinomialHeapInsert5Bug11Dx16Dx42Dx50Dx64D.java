package roops.core.objects;


import roops.core.objects.BinomialHeapNode;


public class BinomialHeap {

    /*@
    @ invariant (\forall BinomialHeapNode n; \reach(Nodes, BinomialHeapNode, sibling + child).has(n) == true; n.parent != null ==> n.key >= n.parent.key );
    @ invariant (\forall BinomialHeapNode n; \reach(Nodes, BinomialHeapNode, sibling + child).has(n) == true; n.sibling != null ==> \reach(n.sibling, BinomialHeapNode, sibling + child).has(n) == false );
    @ invariant (\forall BinomialHeapNode n; \reach(Nodes, BinomialHeapNode, sibling + child).has(n) == true; n.child != null ==> \reach(n.child, BinomialHeapNode, sibling + child).has(n) == false );
    @ invariant (\forall BinomialHeapNode n; \reach(Nodes, BinomialHeapNode, sibling + child).has(n) == true; ( n.child != null && n.sibling != null ) ==>
    @                                                  (\forall BinomialHeapNode m; \reach(n.child, BinomialHeapNode, child + sibling).has(m) == true; \reach(n.sibling, BinomialHeapNode, child + sibling).has(m) == false) );
    @ invariant (\forall BinomialHeapNode n; \reach(Nodes, BinomialHeapNode, sibling + child).has(n) == true; ( n.child != null && n.sibling != null ) ==>
    @                                                  (\forall BinomialHeapNode m; \reach(n.sibling, BinomialHeapNode, child + sibling).has(m) == true; \reach(n.child, BinomialHeapNode, child + sibling).has(m) == false) );
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
    public /*@ nullable @*/roops.core.objects.BinomialHeapNode extractMin() {
        if (Nodes == null) { //mutGenLimit 0
            return null; //mutGenLimit 0
        }
        roops.core.objects.BinomialHeapNode temp = Nodes; //mutGenLimit 0
        roops.core.objects.BinomialHeapNode prevTemp = null; //mutGenLimit 0
        roops.core.objects.BinomialHeapNode minNode = null; //mutGenLimit 0
        minNode = Nodes.findMinNode(); //mutGenLimit 0
        //@decreasing \reach(temp, BinomialHeapNode, sibling).int_size();
        while (temp.key != minNode.key) { //mutGenLimit 0
            prevTemp = temp; //mutGenLimit 0
            temp = temp.sibling; //mutGenLimit 0
        }
        if (prevTemp == null) { //mutGenLimit 0
            Nodes = temp.sibling; //mutGenLimit 0
        } else {
            prevTemp.sibling = temp.sibling; //mutGenLimit 0
        }
        temp = temp.child; //mutGenLimit 0
        roops.core.objects.BinomialHeapNode fakeNode = temp; //mutGenLimit 0
        //@decreasing \reach(temp, BinomialHeapNode, sibling).int_size();
        while (temp != null) { //mutGenLimit 0
            temp.parent = null; //mutGenLimit 0
            temp = temp.sibling; //mutGenLimit 0
        }
        if (Nodes == null && fakeNode == null) { //mutGenLimit 0
            size = 0; //mutGenLimit 0
        } else {
            if (Nodes == null && fakeNode != null) { //mutGenLimit 0
                Nodes = fakeNode.reverse( null ); //mutGenLimit 0
                size--; //mutGenLimit 0
            } else {
                if (Nodes != null && fakeNode == null) { //mutGenLimit 0
                    size--; //mutGenLimit 0
                } else {
                    unionNodes( fakeNode.reverse( null ) ); //mutGenLimit 0
                    size--; //mutGenLimit 0
                }
            }
        }
        return minNode; //mutGenLimit 0
    }

    // 3. Unite two binomial heaps
    // helper procedure
    private void merge( /* @ nullable @ */roops.core.objects.BinomialHeapNode binHeap ) {
        roops.core.objects.BinomialHeapNode temp1 = Nodes;
        roops.core.objects.BinomialHeapNode temp2 = binHeap;
        while (temp1 != null && temp2 != null) {
            if (temp1.degree == temp2.degree) {
                roops.core.objects.BinomialHeapNode tmp = temp2;
                temp2 = temp2.sibling;
                tmp.sibling = temp1.sibling;
                temp1.sibling = tmp;
                temp1 = tmp.sibling;
            } else {
                if (temp1.degree < temp2.degree) {
                    if (temp1.sibling == null || temp1.sibling.degree > temp2.degree) {
                        roops.core.objects.BinomialHeapNode tmp = temp2;
                        temp2 = temp2.sibling;
                        tmp.sibling = temp1.sibling;
                        temp1.sibling = tmp;
                        temp1 = tmp.sibling;
                    } else {
                        temp1 = temp1.sibling;
                    }
                } else {
                    roops.core.objects.BinomialHeapNode tmp = temp1;
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
    private void unionNodes( /* @ nullable @ */roops.core.objects.BinomialHeapNode binHeap ) {
        merge( binHeap );
        roops.core.objects.BinomialHeapNode prevTemp = null;
        roops.core.objects.BinomialHeapNode temp = Nodes;
        roops.core.objects.BinomialHeapNode nextTemp = Nodes.sibling;
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
        roops.core.objects.BinomialHeapNode x = Nodes; //mutGenLimit 0
        roops.core.objects.BinomialHeapNode y = Nodes; //mutGenLimit 0
        int min = x.key; //mutGenLimit 0
        //@decreasing \reach(x, BinomialHeapNode, sibling).int_size();
        while (x != null) { //mutGenLimit 0
            if (x.key < min) { //mutGenLimit 0
                y = x; //mutGenLimit 0
                min = x.key; //mutGenLimit 0
            }
            x = x.sibling; //mutGenLimit 0
        }
        return y.key; //mutGenLimit 0
    }

}
