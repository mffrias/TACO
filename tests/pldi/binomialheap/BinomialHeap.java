package pldi.binomialheap;


import pldi.binomialheap.BinomialHeapNode;


public class BinomialHeap {

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

    public BinomialHeap () {
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
        if (value > 0) { //mutGenLimit 1
            BinomialHeapNode insertTemp = new BinomialHeapNode();  //mutGenLimit 1
            insertTemp.key = value;  //mutGenLimit 1
            if (Nodes == null) {  //mutGenLimit 1
                Nodes = insertTemp;  //mutGenLimit 1
                size = 1;  //mutGenLimit 1
            } else {
                BinomialHeapNode temp1 = Nodes; //mutGenLimit 1 
                BinomialHeapNode temp2 = insertTemp; //mutGenLimit 1
                //@decreasing \reach(temp2, BinomialHeapNode, sibling).int_size();
                while ((temp1 != null) && (temp2 != null)) { //mutGenLimit 1 
                    if (temp1.degree == temp2.degree) { //mutGenLimit 1
                        BinomialHeapNode tmp = temp2; //mutGenLimit 1
                        temp2 = temp2.sibling; //mutGenLimit 1
                        tmp.sibling = temp1.sibling; //mutGenLimit 1
                        temp1.sibling = tmp; //mutGenLimit 1
                        temp1 = tmp.sibling; //mutGenLimit 1
                    } else {
                        if (temp1.degree < temp2.degree) { //mutGenLimit 1
                            if ((temp1.sibling == null) || (temp1.sibling.degree > temp2.degree)) { //mutGenLimit 1
                                BinomialHeapNode tmp = temp2; //mutGenLimit 1
                                temp2 = temp2.sibling; //mutGenLimit 1
                                tmp.sibling = temp1.sibling; //mutGenLimit 1
                                temp1.sibling = tmp; //mutGenLimit 1
                                temp1 = tmp.sibling; //mutGenLimit 1
                            } else {
                                temp1 = temp1.sibling; //mutGenLimit 1
                            }
                        } else {
                            BinomialHeapNode tmp = temp1; //mutGenLimit 1
                            temp1 = temp2; //mutGenLimit 1
                            temp2 = temp2.sibling; //mutGenLimit 1
                            temp1.sibling = tmp; //mutGenLimit 1
                            if (tmp == Nodes) { //mutGenLimit 1
                                Nodes = temp1; //mutGenLimit 1
                            }
                        }
                    }
                }
                if (temp1 == null) { //mutGenLimit 1
                    temp1 = Nodes; //mutGenLimit 1
                    //@decreasing \reach(temp1, BinomialHeapNode, sibling).int_size();
                    while (temp1.sibling != null) { //mutGenLimit 1 
                        temp1 = temp1.sibling; //mutGenLimit 1
                    }
                    temp1.sibling = temp2; //mutGenLimit 1
                }
                BinomialHeapNode prevTemp = null; //mutGenLimit 1
                BinomialHeapNode temp = Nodes; //mutGenLimit 1
                BinomialHeapNode nextTemp = Nodes.sibling; //mutGenLimit 1
                //@decreasing \reach(temp, BinomialHeapNode, sibling).int_size();
                while (nextTemp != null) { //mutGenLimit 1
                    if ((temp.degree != nextTemp.degree) || ((nextTemp.sibling != null) && (nextTemp.sibling.degree == temp.degree))) { //mutGenLimit 1
                        prevTemp = temp; //mutGenLimit 1
                        temp = nextTemp; //mutGenLimit 1
                    } else {
                        if (temp.key <= nextTemp.key) { //mutGenLimit 1
                            temp.sibling = nextTemp.sibling; //mutGenLimit 1
                            nextTemp.parent = temp; //mutGenLimit 1
                            nextTemp.sibling = temp.child; //mutGenLimit 1
                            temp.child = nextTemp; //mutGenLimit 1
                            temp.degree++; //mutGenLimit 1
                        } else {
                            if (prevTemp == null) { //mutGenLimit 1
                                Nodes = nextTemp; //mutGenLimit 1
                            } else {
                                prevTemp.sibling = nextTemp; //mutGenLimit 1
                            }
                            temp.parent = nextTemp; //mutGenLimit 1
                            temp.sibling = nextTemp.child; //mutGenLimit 1
                            nextTemp.child = temp; //mutGenLimit 1
                            nextTemp.degree++; //mutGenLimit 1
                            temp = nextTemp; //mutGenLimit 1
                        }
                    }
                    nextTemp = temp.sibling; //mutGenLimit 1
                }
                size++; //mutGenLimit 1
            }
        }
    }

    
  /*@ requires true;
  @ ensures \old(Nodes) != null ==> \old(\reach(Nodes, BinomialHeapNode, child + sibling)).has(\result);
  @ ensures (\forall BinomialHeapNode n; \reach(Nodes, BinomialHeapNode, child + sibling).has(n); \result.key <= n.key);
  @ ensures (\forall BinomialHeapNode n; \reach(Nodes, BinomialHeapNode, child + sibling).has(n); \old(n.key) == n.key);
  @*/
    public/* @ nullable @ */BinomialHeapNode extractMin() {
        if (Nodes == null) { //mutGenLimit 1
            return null; //mutGenLimit 1
        }
        BinomialHeapNode temp = Nodes, prevTemp = null; //mutGenLimit 1
        BinomialHeapNode minNode = null; //mutGenLimit 1
        minNode = Nodes.findMinNode(); //mutGenLimit 1
        //@decreasing \reach(temp, BinomialHeapNode, sibling).int_size();
        while (temp.key != minNode.key) { //mutGenLimit 1
            prevTemp = temp; //mutGenLimit 1
            temp = temp.sibling; //mutGenLimit 1
        }
        if (prevTemp == null) { //mutGenLimit 1
            Nodes = temp.sibling; //mutGenLimit 1
        } else {
            prevTemp.sibling = temp.sibling; //mutGenLimit 1
        }
        temp = temp.child; //mutGenLimit 1
        BinomialHeapNode fakeNode = temp; //mutGenLimit 1
        //@decreasing \reach(temp, BinomialHeapNode, sibling).int_size();
        while (temp != null) { //mutGenLimit 1
            temp.parent = null; //mutGenLimit 1
            temp = temp.sibling; //mutGenLimit 1
        }
        if ((Nodes == null) && (fakeNode == null)) { //mutGenLimit 1
            size = 0; //mutGenLimit 1
        } else {
            if ((Nodes == null) && (fakeNode != null)) { //mutGenLimit 1
                Nodes = fakeNode.reverse(null); //mutGenLimit 1
                size--; //mutGenLimit 1
            } else {
                if ((Nodes != null) && (fakeNode == null)) { //mutGenLimit 1
                    size--; //mutGenLimit 1
                } else {
                    unionNodes(fakeNode.reverse(null)); //mutGenLimit 1
                    size--; //mutGenLimit 1
                }
            }
        }

        return minNode; //mutGenLimit 1
    }

    
    // 3. Unite two binomial heaps
    // helper procedure
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
    
    
    // another helper procedure
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
    BinomialHeapNode x = Nodes; //mutGenLimit 1
    BinomialHeapNode y = Nodes; //mutGenLimit 1
    int min = x.key; //mutGenLimit 1
    //@decreasing \reach(x, BinomialHeapNode, sibling).int_size();
    while (x != null) { //mutGenLimit 1
        if (x.key < min) { //mutGenLimit 1
            y = x; //mutGenLimit 1
            min = x.key; //mutGenLimit 1
        }
        x = x.sibling; //mutGenLimit 1
    }
    return y.key; //mutGenLimit 1
   }
 
}
