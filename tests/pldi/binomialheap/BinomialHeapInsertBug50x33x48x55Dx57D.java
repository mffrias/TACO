package pldi.binomialheap;


import pldi.binomialheap.BinomialHeapNode;


public class BinomialHeapInsertBug50x33x48x55Dx57D {

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
    @
    @*/
    public /*@ nullable @*/pldi.binomialheap.BinomialHeapNode Nodes;

    public int size;

    public BinomialHeapInsertBug50x33x48x55Dx57D () {
    }

    /*@
    @ requires true;
    @ ensures (\forall BinomialHeapNode n; \old(\reach(Nodes, BinomialHeapNode, child + sibling)).has(n); \reach(Nodes, BinomialHeapNode, child + sibling).has(n) && \old(n.key) == n.key);
    @ ensures value > 0 ==> (\exists BinomialHeapNode n; !\old(\reach(Nodes, BinomialHeapNode, child + sibling)).has(n); \reach(Nodes, BinomialHeapNode, child + sibling).has(n) && n.key == value);
    @ ensures value > 0 ==> size == \old(size) + 1;
    @ signals (Exception e) false;
    @
    @*/
    public void insert ( int value,/*@nullable@*/  pldi.binomialheap.BinomialHeapNode customvar_0,/*@nullable@*/  pldi.binomialheap.BinomialHeapNode customvar_1,/*@nullable@*/  pldi.binomialheap.BinomialHeapNode customvar_2,/*@nullable@*/  pldi.binomialheap.BinomialHeapNode customvar_3,/*@nullable@*/  pldi.binomialheap.BinomialHeapNode customvar_4,/*@nullable@*/  pldi.binomialheap.BinomialHeapNode customvar_5,/*@nullable@*/  pldi.binomialheap.BinomialHeapNode customvar_6,/*@nullable@*/  pldi.binomialheap.BinomialHeapNode customvar_7,/*@nullable@*/  pldi.binomialheap.BinomialHeapNode customvar_8,/*@nullable@*/  pldi.binomialheap.BinomialHeapNode customvar_9,/*@nullable@*/  pldi.binomialheap.BinomialHeapNode customvar_10,/*@nullable@*/  pldi.binomialheap.BinomialHeapNode customvar_11,/*@nullable@*/  pldi.binomialheap.BinomialHeapNode customvar_12,/*@nullable@*/  pldi.binomialheap.BinomialHeapNode customvar_13,/*@nullable@*/  pldi.binomialheap.BinomialHeapNode customvar_14,/*@nullable@*/  pldi.binomialheap.BinomialHeapNode customvar_15, boolean customvar_16, boolean customvar_17, boolean customvar_18, boolean customvar_19,/*@nullable@*/  pldi.binomialheap.BinomialHeapNode customvar_20, boolean customvar_21, boolean customvar_22, boolean customvar_23, boolean customvar_24) {
        if ( value > 0 ) {
            pldi.binomialheap.BinomialHeapNode insTemp = new pldi.binomialheap.BinomialHeapNode ();
            insTemp.key = value;
            if ( Nodes == null ) {
                Nodes = insTemp;
                size = 1;
            } else {
                pldi.binomialheap.BinomialHeapNode temp1 = Nodes;
                pldi.binomialheap.BinomialHeapNode temp2 = insTemp;
                {
                    if ( temp1 != null && temp2 != null ) {
                        if ( temp1.degree == temp2.degree ) {
                            pldi.binomialheap.BinomialHeapNode tmp = temp2;
                            temp2 = temp2.sibling;
                            tmp.sibling = temp1.sibling;
                            temp1.sibling = tmp;
                            temp1 = tmp.sibling;
                        } else {
                            if ( temp1.degree < temp2.degree ) {
                                if ( temp1.sibling == null || temp1.sibling.degree > temp2.degree ) {
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
                                if ( customvar_21 ) { //mutGenLimit 1 mutID 1
                                    Nodes = temp1;
                                }
                            }
                        }
                    }
                    if ( temp1 != null && temp2 != null ) {
                        if ( temp1.degree == temp2.degree ) {
                            pldi.binomialheap.BinomialHeapNode tmp = temp2;
                            temp2 = temp2.sibling;
                            tmp.sibling = temp1.sibling;
                            temp1.sibling = tmp;
                            temp1 = tmp.sibling;
                        } else {
                            if ( temp1.degree < temp2.degree ) {
                                if ( temp1.sibling == null || temp1.sibling.degree > temp2.degree ) {
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
                                if ( customvar_22 ) { //mutGenLimit 1 mutID 1
                                    Nodes = temp1;
                                }
                            }
                        }
                    }
                    if ( temp1 != null && temp2 != null ) {
                        if ( temp1.degree == temp2.degree ) {
                            pldi.binomialheap.BinomialHeapNode tmp = temp2;
                            temp2 = temp2.sibling;
                            tmp.sibling = temp1.sibling;
                            temp1.sibling = tmp;
                            temp1 = tmp.sibling;
                        } else {
                            if ( temp1.degree < temp2.degree ) {
                                if ( temp1.sibling == null || temp1.sibling.degree > temp2.degree ) {
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
                                if ( customvar_23 ) { //mutGenLimit 1 mutID 1
                                    Nodes = temp1;
                                }
                            }
                        }
                    }
                    if ( temp1 != null && temp2 != null ) {
                        if ( temp1.degree == temp2.degree ) {
                            pldi.binomialheap.BinomialHeapNode tmp = temp2;
                            temp2 = temp2.sibling;
                            tmp.sibling = temp1.sibling;
                            temp1.sibling = tmp;
                            temp1 = tmp.sibling;
                        } else {
                            if ( temp1.degree < temp2.degree ) {
                                if ( temp1.sibling == null || temp1.sibling.degree > temp2.degree ) {
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
                                if ( customvar_24 ) { //mutGenLimit 1 mutID 1
                                    Nodes = temp1;
                                }
                            }
                        }
                    }
                }
                if ( temp1 == null ) {
                    temp1 = Nodes;
                    {
                        if ( temp1.sibling != null ) {
                            temp1 = temp1.sibling;
                        }
                        if ( temp1.sibling != null ) {
                            temp1 = temp1.sibling;
                        }
                        if ( temp1.sibling != null ) {
                            temp1 = temp1.sibling;
                        }
                        if ( temp1.sibling != null ) {
                            temp1 = temp1.sibling;
                        }
                    }
                    temp1.sibling = temp2;
                }
                pldi.binomialheap.BinomialHeapNode prevTemp = null;
                pldi.binomialheap.BinomialHeapNode temp = Nodes;
                pldi.binomialheap.BinomialHeapNode nextTemp = customvar_20; //mutGenLimit 1 mutID 2
                {
                    if ( nextTemp != null ) {
                        if ( customvar_16 ) { //mutGenLimit 1 mutID 3
                            prevTemp = temp;
                            temp = nextTemp;
                        } else {
                            if ( temp.key <= nextTemp.key ) {
                                customvar_12.sibling = customvar_8; //mutGenLimit 1 mutID 4
                                nextTemp
                                .parent = temp;
                                customvar_4.sibling = customvar_0; //mutGenLimit 0 mutID 5
                                temp
                                .child = nextTemp;
                                temp.degree ++;
                            } else {
                                if ( prevTemp == null ) {
                                    Nodes = nextTemp;
                                } else {
                                    prevTemp.sibling = nextTemp;
                                }
                                temp.parent = nextTemp;
                                temp.sibling = nextTemp.child;
                                nextTemp.child = temp;
                                nextTemp.degree ++;
                                temp = nextTemp;
                            }
                        }
                        nextTemp = temp.sibling;
                    }
                    if ( nextTemp != null ) {
                        if ( customvar_17 ) { //mutGenLimit 1 mutID 3
                            prevTemp = temp;
                            temp = nextTemp;
                        } else {
                            if ( temp.key <= nextTemp.key ) {
                                customvar_13.sibling = customvar_9; //mutGenLimit 1 mutID 4
                                nextTemp
                                .parent = temp;
                                customvar_5.sibling = customvar_1; //mutGenLimit 0 mutID 5
                                temp
                                .child = nextTemp;
                                temp.degree ++;
                            } else {
                                if ( prevTemp == null ) {
                                    Nodes = nextTemp;
                                } else {
                                    prevTemp.sibling = nextTemp;
                                }
                                temp.parent = nextTemp;
                                temp.sibling = nextTemp.child;
                                nextTemp.child = temp;
                                nextTemp.degree ++;
                                temp = nextTemp;
                            }
                        }
                        nextTemp = temp.sibling;
                    }
                    if ( nextTemp != null ) {
                        if ( customvar_18 ) { //mutGenLimit 1 mutID 3
                            prevTemp = temp;
                            temp = nextTemp;
                        } else {
                            if ( temp.key <= nextTemp.key ) {
                                customvar_14.sibling = customvar_10; //mutGenLimit 1 mutID 4
                                nextTemp
                                .parent = temp;
                                customvar_6.sibling = customvar_2; //mutGenLimit 0 mutID 5
                                temp
                                .child = nextTemp;
                                temp.degree ++;
                            } else {
                                if ( prevTemp == null ) {
                                    Nodes = nextTemp;
                                } else {
                                    prevTemp.sibling = nextTemp;
                                }
                                temp.parent = nextTemp;
                                temp.sibling = nextTemp.child;
                                nextTemp.child = temp;
                                nextTemp.degree ++;
                                temp = nextTemp;
                            }
                        }
                        nextTemp = temp.sibling;
                    }
                    if ( nextTemp != null ) {
                        if ( customvar_19 ) { //mutGenLimit 1 mutID 3
                            prevTemp = temp;
                            temp = nextTemp;
                        } else {
                            if ( temp.key <= nextTemp.key ) {
                                customvar_15.sibling = customvar_11; //mutGenLimit 1 mutID 4
                                nextTemp
                                .parent = temp;
                                customvar_7.sibling = customvar_3; //mutGenLimit 0 mutID 5
                                temp
                                .child = nextTemp;
                                temp.degree ++;
                            } else {
                                if ( prevTemp == null ) {
                                    Nodes = nextTemp;
                                } else {
                                    prevTemp.sibling = nextTemp;
                                }
                                temp.parent = nextTemp;
                                temp.sibling = nextTemp.child;
                                nextTemp.child = temp;
                                nextTemp.degree ++;
                                temp = nextTemp;
                            }
                        }
                        nextTemp = temp.sibling;
                    }
                }
                size ++;
            }
        }
    }
}
