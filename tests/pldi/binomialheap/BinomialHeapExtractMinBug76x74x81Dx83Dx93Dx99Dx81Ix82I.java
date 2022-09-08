package pldi.binomialheap;


import pldi.binomialheap.BinomialHeapNode;


public class BinomialHeapExtractMinBug76x74x81Dx83Dx93Dx99Dx81Ix82I {

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




    public /*@ nullable @*/pldi.binomialheap.BinomialHeapNode Nodes;

    public int size;

    public BinomialHeapExtractMinBug76x74x81Dx83Dx93Dx99Dx81Ix82I () {
    }

    //    /*@ requires Nodes != null;
    //        @ ensures (\exists BinomialHeapNode n; \reach(Nodes, BinomialHeapNode, child + sibling).has(n); n.key == \result);
    //        @ ensures (\forall BinomialHeapNode n; \reach(Nodes, BinomialHeapNode, child + sibling).has(n); \result <= n.key);
    //        @ ensures (\forall BinomialHeapNode n; \reach(Nodes, BinomialHeapNode, child + sibling).has(n); \old(n.key) == n.key);
    //        @ ensures (\forall BinomialHeapNode n; \reach(Nodes, BinomialHeapNode, child + sibling).has(n); \old(n.degree) == n.degree);
    //        @ ensures (\forall BinomialHeapNode n; \reach(Nodes, BinomialHeapNode, child + sibling).has(n); \old(n.parent) == n.parent);
    //        @ ensures (\forall BinomialHeapNode n; \reach(Nodes, BinomialHeapNode, child + sibling).has(n); \old(n.sibling) == n.sibling);
    //        @ ensures (\forall BinomialHeapNode n; \reach(Nodes, BinomialHeapNode, child + sibling).has(n); \old(n.child) == n.child);
    //        @ signals (Exception e) false;
    //        @*/
    //    public int findMinimum() {
    //        BinomialHeapNode x = Nodes;
    //        BinomialHeapNode y = Nodes;
    //        int min = x.key;
    //        //@ decreasing \reach(x, BinomialHeapNode, sibling).int_size();
    //        while (x != null) {
    //            if (x.key < min) {
    //                y = x;
    //                min = x.key;
    //            }
    //            x = x.sibling;
    //        }
    //        return y.key;
    //    }







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
            BinomialHeapNode insertTemp = new BinomialHeapNode(); 
            insertTemp.key = value; 
            if (Nodes == null) { 
                Nodes = insertTemp; 
                size = 1; 
            } else {
                BinomialHeapNode temp1 = Nodes; 
                BinomialHeapNode temp2 = insertTemp; 
                //@ decreasing \reach(temp2, BinomialHeapNode, sibling).int_size();
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
                    //@ decreasing \reach(temp1, BinomialHeapNode, sibling).int_size();
                    while (temp1.sibling != null) { 
                        temp1 = temp1.sibling; 
                    }
                    temp1.sibling = temp2; 
                }
                BinomialHeapNode prevTemp = null; 
                BinomialHeapNode temp = Nodes; 
                BinomialHeapNode nextTemp = Nodes.sibling; //mutGenLimit 0 
                //@ decreasing \reach(temp, BinomialHeapNode, sibling).int_size();
                while (nextTemp != null) { 
                    if ((temp.degree != nextTemp.degree) || ((nextTemp.sibling != null) && (nextTemp.sibling.degree == temp.degree))) { 
                        prevTemp = temp; 
                        temp = nextTemp; 
                    } else {
                        if (temp.key <= nextTemp.key) { 
                            temp.sibling = nextTemp.sibling; //mutGenLimit 0
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

    
    

    /*@ requires true;
      @ ensures \old(Nodes) != null ==> \old(\reach(Nodes, BinomialHeapNode, child + sibling)).has(\result);
      @ ensures (\forall BinomialHeapNode n; \reach(Nodes, BinomialHeapNode, child + sibling).has(n); \result.key <= n.key);
      @ ensures (\forall BinomialHeapNode n; \reach(Nodes, BinomialHeapNode, child + sibling).has(n); \old(n.key) == n.key);
      @*/
    public /*@nullable@*/ BinomialHeapNode extractMin() {
        if (Nodes == null) {
            return null;
        }
        BinomialHeapNode temp = Nodes;
        BinomialHeapNode prevTemp = null;
        BinomialHeapNode minNode = null;
        minNode = Nodes.findMinNode();
        //@ decreasing \reach(temp, BinomialHeapNode, sibling).int_size();
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
        //@ decreasing \reach(temp, BinomialHeapNode, sibling).int_size();
        while (temp != null) {
            temp.parent = null;
            temp = temp.sibling;
        }
        if ((Nodes == null) && (fakeNode == null)) {
            size = 0;
        } else {
            if ((Nodes == null) && (fakeNode != null)) {
                Nodes = fakeNode.reverse(null);
                size--;
            } else {
                if ((Nodes != null) && (fakeNode == null)) {
                    size--;
                } else {
                    BinomialHeapNode binHeap = fakeNode.reverse(null);
                    BinomialHeapNode temp1 = Nodes;
                    BinomialHeapNode temp2 = binHeap;
                    //@ decreasing \reach(temp1, BinomialHeapNode, sibling).int_size() + \reach(temp2, BinomialHeapNode, sibling).int_size();
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
                        //@ decreasing \reach(temp1, BinomialHeapNode, sibling).int_size();
                        while (temp1.sibling != null) {
                            temp1 = temp1.sibling;
                        }
                        temp1.sibling = temp2;
                    }
                    BinomialHeapNode prevTempUnionNodes = null;
                    BinomialHeapNode tempUnionNodes = Nodes;
                    BinomialHeapNode nextTemp = Nodes.findMinNode().parent; //mutGenLimit 1
                    //@ decreasing \reach(nextTemp, BinomialHeapNode, sibling).int_size();
                    while (nextTemp != null) {
                        if ((tempUnionNodes.degree == nextTemp.degree) || ((nextTemp.sibling != null) && (nextTemp.sibling.degree == tempUnionNodes.degree))) { //mutGenLimit 1
                            prevTempUnionNodes = tempUnionNodes;
                            tempUnionNodes = nextTemp;
                        } else {
                            if (tempUnionNodes.key <= nextTemp.key) {
                            	tempUnionNodes.sibling.parent = nextTemp.sibling.findMinNode(); //mutGenLimit 2
                            	nextTemp.parent.child = tempUnionNodes; //mutGenLimit 1
                                nextTemp.sibling = tempUnionNodes.child.parent; //mutGenLimit 1
                                tempUnionNodes.child = nextTemp;
                                tempUnionNodes.degree++;
                            } else {
                                if (prevTempUnionNodes == null) {
                                    Nodes = nextTemp;
                                } else {
                                    prevTempUnionNodes.sibling = nextTemp;
                                }
                                tempUnionNodes.parent = nextTemp;
                                tempUnionNodes.sibling = nextTemp.child.sibling; //mutGenLimit 1
                                nextTemp.child = tempUnionNodes;
                                nextTemp.degree++;
                                tempUnionNodes = nextTemp;
                            }
                        }
                        nextTemp = tempUnionNodes.findMinNode().sibling; //mutGenLimit 1
                    }
                    size--;
                }
            }
        }
        return minNode;
    }

}
