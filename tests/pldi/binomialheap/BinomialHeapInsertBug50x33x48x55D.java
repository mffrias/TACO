package pldi.binomialheap;


import pldi.binomialheap.BinomialHeapNode;


public class BinomialHeapInsertBug50x33x48x55D {

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

    public BinomialHeapInsertBug50x33x48x55D () {
    }







    /*@
      @ requires true;
      @ ensures (\forall BinomialHeapNode n; \old(\reach(Nodes, BinomialHeapNode, child + sibling)).has(n); \reach(Nodes, BinomialHeapNode, child + sibling).has(n) && \old(n.key) == n.key);
      @ ensures value > 0 ==> (\exists BinomialHeapNode n; !\old(\reach(Nodes, BinomialHeapNode, child + sibling)).has(n); \reach(Nodes, BinomialHeapNode, child + sibling).has(n) && n.key == value);
      @ ensures value > 0 ==> size == \old(size) + 1;
      @ signals (Exception e) false;
      @ 
      @*/
    public void insert(int value) {
        if (value > 0) { 
            BinomialHeapNode insTemp = new BinomialHeapNode(); 
            insTemp.key = value; 
            if (Nodes == null) { 
                Nodes = insTemp; 
                size = 1; 
            } else {
                BinomialHeapNode temp1 = Nodes; 
                BinomialHeapNode temp2 = insTemp; 
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
                            if (tmp != Nodes) { //mutGenLimit 1
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
                BinomialHeapNode prevTemp = null; 
                BinomialHeapNode temp = Nodes; 
                BinomialHeapNode nextTemp = Nodes.findMinNode().parent; //mutGenLimit 1 
                //@decreasing \reach(temp, BinomialHeapNode, sibling).int_size();
                while (nextTemp != null) { 
                    if ((temp.degree == nextTemp.degree) || ((nextTemp.sibling != null) && (nextTemp.sibling.degree == temp.degree))) { //mutGenLimit 1
                        prevTemp = temp; 
                        temp = nextTemp; 
                    } else {
                        if (temp.key <= nextTemp.key) { 
                            temp.sibling = nextTemp.sibling.findMinNode(); //mutGenLimit 1
                            nextTemp.parent = temp; //mutGenLimit 0
                            nextTemp.sibling = temp.child; //mutGenLimit 0
                            temp.child = nextTemp; 
                            temp.degree++; 
                        } else {
                            if (prevTemp == null) { 
                                Nodes = nextTemp; 
                            } else {
                                prevTemp.sibling = nextTemp; 
                            }
                            temp.parent = nextTemp; 
                            temp.sibling = nextTemp.child; //mutGenLimit 0
                            nextTemp.child = temp; 
                            nextTemp.degree++; 
                            temp = nextTemp; 
                        }
                    }
                    nextTemp = temp.sibling; //mutGenLimit 0
                }
                size++; 
            }
        }
    }

    
    


}
