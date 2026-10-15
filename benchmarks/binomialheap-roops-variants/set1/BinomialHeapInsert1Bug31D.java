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
    public /*@ nullable @*/roops.core.objects.BinomialHeapNode Nodes;

    public int size;

    public BinomialHeap () {
    }

    /*@
    @ requires insertTemp.key == value;
    @ requires insertTemp.sibling == null;
    @ requires insertTemp.child == null;
    @ requires insertTemp.parent == null;
    @ requires insertTemp.degree == 0;
    @ requires \reach(Nodes, BinomialHeapNode, child+sibling).has(insertTemp) == false;
    @ ensures (\forall BinomialHeapNode n; \old(\reach(Nodes, BinomialHeapNode, child + sibling)).has(n) == true; \reach(Nodes, BinomialHeapNode, child + sibling).has(n) == true && \old(n.key) == n.key);
    @ ensures value > 0 ==> (\exists BinomialHeapNode n; \old(\reach(Nodes, BinomialHeapNode, child + sibling)).has(n) == false; \reach(Nodes, BinomialHeapNode, child + sibling).has(n) == true && n.key == value);
    @ ensures value > 0 ==> size == \old(size) + 1;
    @ signals (Exception e) false;
    @
    @*/
    public void insert( int value, roops.core.objects.BinomialHeapNode insertTemp ) {
        if (value > 0) { //mutGenLimit 0
            insertTemp.key = value; //mutGenLimit 0
            if (Nodes == null) { //mutGenLimit 0
                Nodes = insertTemp; //mutGenLimit 0
                size = 1; //mutGenLimit 0
            } else {
                roops.core.objects.BinomialHeapNode mergeTemp1 = Nodes; //mutGenLimit 0
                roops.core.objects.BinomialHeapNode mergeTemp2 = insertTemp; //mutGenLimit 0
                //@decreasing \reach(mergeTemp2, BinomialHeapNode, sibling).int_size();
                while (mergeTemp1 != null && mergeTemp2 != null) { //mutGenLimit 0
                    if (mergeTemp1.degree == mergeTemp2.degree) { //mutGenLimit 0
                        roops.core.objects.BinomialHeapNode mergeTmp = mergeTemp2; //mutGenLimit 0
                        mergeTemp2 = mergeTemp2.sibling; //mutGenLimit 0
                        mergeTmp.sibling = mergeTemp1.sibling; //mutGenLimit 0
                        mergeTemp1.sibling = mergeTmp; //mutGenLimit 0
                        mergeTemp1 = mergeTmp.sibling; //mutGenLimit 0
                    } else {
                        if (mergeTemp1.degree < mergeTemp2.degree) { //mutGenLimit 0
                            if (mergeTemp1.sibling == null || mergeTemp1.sibling.degree > mergeTemp2.degree) { //mutGenLimit 0
                                roops.core.objects.BinomialHeapNode mergeTmp = mergeTemp2; //mutGenLimit 0
                                mergeTemp2 = mergeTemp2.sibling; //mutGenLimit 0
                                mergeTmp.sibling = mergeTemp1.sibling; //mutGenLimit 0
                                mergeTemp1.sibling = mergeTmp; //mutGenLimit 0
                                mergeTemp1 = mergeTmp.sibling; //mutGenLimit 0
                            } else {
                                mergeTemp1 = mergeTemp1.sibling; //mutGenLimit 0
                            }
                        } else {
                            roops.core.objects.BinomialHeapNode mergeTmp = mergeTemp1; //mutGenLimit 0
                            mergeTemp1 = mergeTemp2; //mutGenLimit 0
                            mergeTemp2 = mergeTmp.sibling; //mutGenLimit 1
                            mergeTemp1.sibling = mergeTmp; //mutGenLimit 0
                            if (mergeTmp == Nodes) { //mutGenLimit 0
                                Nodes = mergeTemp1; //mutGenLimit 0
                            }
                        }
                    }

                }
                if (mergeTemp1 == null) { //mutGenLimit 0
                    mergeTemp1 = Nodes; //mutGenLimit 0
                    while (mergeTemp1.sibling != null) { //mutGenLimit 0
                        mergeTemp1 = mergeTemp1.sibling; //mutGenLimit 0
                    }
                    mergeTemp1.sibling = mergeTemp2; //mutGenLimit 0
                }
                roops.core.objects.BinomialHeapNode prevTempUnionNodes = null; //mutGenLimit 0
                roops.core.objects.BinomialHeapNode tempUnionNodes = Nodes; //mutGenLimit 0
                roops.core.objects.BinomialHeapNode nextTempUnionNodes = Nodes.sibling; //mutGenLimit 0
                //@decreasing \reach(tempUnionNodes, BinomialHeapNode, sibling).int_size();
                while (nextTempUnionNodes != null) { //mutGenLimit 0
                    if ((tempUnionNodes.degree != nextTempUnionNodes.degree) || ((nextTempUnionNodes.sibling != null) && (nextTempUnionNodes.sibling.degree == tempUnionNodes.degree))) { //mutGenLimit 0
                        prevTempUnionNodes = tempUnionNodes; //mutGenLimit 0
                        tempUnionNodes = nextTempUnionNodes; //mutGenLimit 0
                    } else {
                        if (tempUnionNodes.key <= nextTempUnionNodes.key) { //mutGenLimit 0
                            tempUnionNodes.sibling = nextTempUnionNodes.sibling; //mutGenLimit 0
                            nextTempUnionNodes.parent = tempUnionNodes; //mutGenLimit 0
                            nextTempUnionNodes.sibling = tempUnionNodes.child; //mutGenLimit 0
                            tempUnionNodes.child = nextTempUnionNodes; //mutGenLimit 0
                            tempUnionNodes.degree++; //mutGenLimit 0
                        } else {
                            if (prevTempUnionNodes == null) { //mutGenLimit 0
                                Nodes = nextTempUnionNodes; //mutGenLimit 0
                            } else {
                                prevTempUnionNodes.sibling = nextTempUnionNodes; //mutGenLimit 0
                            }
                            tempUnionNodes.parent = nextTempUnionNodes; //mutGenLimit 0
                            tempUnionNodes.sibling = nextTempUnionNodes.child; //mutGenLimit 0
                            nextTempUnionNodes.child = tempUnionNodes; //mutGenLimit 0
                            nextTempUnionNodes.degree++; //mutGenLimit 0
                            tempUnionNodes = nextTempUnionNodes; //mutGenLimit 0
                        }
                    }
                    nextTempUnionNodes = tempUnionNodes.sibling; //mutGenLimit 0
                }
                size++; //mutGenLimit 0
            }
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
   	//@decreasing \reach(x, BinomialHeapNode, sibling).int_size();
   	while (x != null) {
   		if (x.key > min) { //mutGenLimit 1
   			y = x;
   			min = x.key--; //mutGenLimit 1
   		}
   		x = x.sibling.sibling; //mutGenLimit 1
   	}
   	return y.key++; //mutGenLimit 1
   }







}