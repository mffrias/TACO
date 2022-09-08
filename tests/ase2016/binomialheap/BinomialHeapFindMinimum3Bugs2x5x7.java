package ase2016.binomialheap;

import roops.core.objects.BinomialHeapNode;


public class BinomialHeapFindMinimum3Bugs2x5x7 {

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
    public /*@ nullable @*/roops.core.objects.BinomialHeapNode Nodes;

    public int size;

    public BinomialHeapFindMinimum3Bugs2x5x7() {
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
        if (value > 0) { //mutGenLimit 0
            roops.core.objects.BinomialHeapNode insertTemp = new roops.core.objects.BinomialHeapNode(); //mutGenLimit 0
            insertTemp.key = value; //mutGenLimit 0
            if (Nodes == null) { //mutGenLimit 0
                Nodes = insertTemp; //mutGenLimit 0
                size = 1; //mutGenLimit 0
            } else {
                roops.core.objects.BinomialHeapNode temp1 = Nodes; //mutGenLimit 0
                roops.core.objects.BinomialHeapNode temp2 = insertTemp; //mutGenLimit 0
                //@decreasing \reach(temp2, BinomialHeapNode, sibling).int_size();
                while (temp1 != null && temp2 != null) { //mutGenLimit 0
                    if (temp1.degree == temp2.degree) { //mutGenLimit 0
                        roops.core.objects.BinomialHeapNode tmp = temp2; //mutGenLimit 0
                        temp2 = temp2.sibling; //mutGenLimit 0
                        tmp.sibling = temp1.sibling; //mutGenLimit 0
                        temp1.sibling = tmp; //mutGenLimit 0
                        temp1 = tmp.sibling; //mutGenLimit 0
                    } else {
                        if (temp1.degree < temp2.degree) { //mutGenLimit 0
                            if (temp1.sibling == null || temp1.sibling.degree > temp2.degree) { //mutGenLimit 0
                                roops.core.objects.BinomialHeapNode tmp = temp2; //mutGenLimit 0
                                temp2 = temp2.sibling; //mutGenLimit 0
                                tmp.sibling = temp1.sibling; //mutGenLimit 0
                                temp1.sibling = tmp; //mutGenLimit 0
                                temp1 = tmp.sibling; //mutGenLimit 0
                            } else {
                                temp1 = temp1.sibling; //mutGenLimit 0
                            }
                        } else {
                            roops.core.objects.BinomialHeapNode tmp = temp1; //mutGenLimit 0
                            temp1 = temp2; //mutGenLimit 0
                            temp2 = temp2.sibling; //mutGenLimit 0
                            temp1.sibling = tmp; //mutGenLimit 0
                            if (tmp == Nodes) { //mutGenLimit 0
                                Nodes = temp1; //mutGenLimit 0
                            }
                        }
                    }
                }
                if (temp1 == null) { //mutGenLimit 0
                    temp1 = Nodes; //mutGenLimit 0
                    //@decreasing \reach(temp1, BinomialHeapNode, sibling).int_size();
                    while (temp1.sibling != null) { //mutGenLimit 0
                        temp1 = temp1.sibling; //mutGenLimit 0
                    }
                    temp1.sibling = temp2; //mutGenLimit 0
                }
                roops.core.objects.BinomialHeapNode prevTemp = null; //mutGenLimit 0
                roops.core.objects.BinomialHeapNode temp = Nodes; //mutGenLimit 0
                roops.core.objects.BinomialHeapNode nextTemp = Nodes.sibling; //mutGenLimit 0
                //@decreasing \reach(temp, BinomialHeapNode, sibling).int_size();
                while (nextTemp != null) { //mutGenLimit 0
                    if (temp.degree != nextTemp.degree || nextTemp.sibling != null && nextTemp.sibling.degree == temp.degree) { //mutGenLimit 0
                        prevTemp = temp; //mutGenLimit 0
                        temp = nextTemp; //mutGenLimit 0
                    } else {
                        if (temp.key <= nextTemp.key) { //mutGenLimit 0
                            temp.sibling = nextTemp.sibling; //mutGenLimit 0
                            nextTemp.parent = temp; //mutGenLimit 0
                            nextTemp.sibling = temp.child; //mutGenLimit 0
                            temp.child = nextTemp; //mutGenLimit 0
                            temp.degree++; //mutGenLimit 0
                        } else {
                            if (prevTemp == null) { //mutGenLimit 0
                                Nodes = nextTemp; //mutGenLimit 0
                            } else {
                                prevTemp.sibling = nextTemp; //mutGenLimit 0
                            }
                            temp.parent = nextTemp; //mutGenLimit 0
                            temp.sibling = nextTemp.child; //mutGenLimit 0
                            nextTemp.child = temp; //mutGenLimit 0
                            nextTemp.degree++; //mutGenLimit 0
                            temp = nextTemp; //mutGenLimit 0
                        }
                    }
                    nextTemp = temp.sibling; //mutGenLimit 0
                }
                size++; //mutGenLimit 0
            }
        }
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
    @
    @ ensures this.size == \reach(Nodes, BinomialHeapNode, sibling + child).int_size();
    @
    @ ensures ( \forall BinomialHeapNode n; \reach(Nodes, BinomialHeapNode, sibling).has(n); (n.sibling != null ==> n.degree < n.sibling.degree) && (n.parent == null) );
    @
    @ ensures ( \forall BinomialHeapNode n; \reach(Nodes, BinomialHeapNode, sibling).has(n); n.key >= 0 );
    @ ensures ( \forall BinomialHeapNode n; \reach(Nodes, BinomialHeapNode, sibling).has(n); (n.sibling != null ==> n.degree < n.sibling.degree) && (n.parent == null) );
    @ ensures ( \forall BinomialHeapNode n; \reach(Nodes, BinomialHeapNode, sibling).has(n); n.key >= 0 );
    @ ensures \old(Nodes) != null ==> \old(\reach(Nodes, BinomialHeapNode, child + sibling)).has(\result);
    @ ensures (\forall BinomialHeapNode n; \reach(Nodes, BinomialHeapNode, child + sibling).has(n); \result.key <= n.key);
    @ ensures (\forall BinomialHeapNode n; \reach(Nodes, BinomialHeapNode, child + sibling).has(n); \old(n.key) == n.key);
    @*/
    public /* @ nullable @ */roops.core.objects.BinomialHeapNode extractMin() {
        if (Nodes == null) { //mutGenLimit 0
            return null; //mutGenLimit 0
        }
        roops.core.objects.BinomialHeapNode temp = Nodes; //mutGenLimit 0
        roops.core.objects.BinomialHeapNode prevTemp = temp; //mutGenLimit 1
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
        while (temp == null) { //mutGenLimit 1
            temp.parent = null; //mutGenLimit 0
            temp = temp.sibling; //mutGenLimit 0
        }
        if (Nodes == null && fakeNode == null) { //mutGenLimit 0
            size = 0; //mutGenLimit 0
        } else {
            if (Nodes == null && fakeNode != null) { //mutGenLimit 0
            	roops.core.objects.BinomialHeapNode prevBro = null;
            	while (fakeNode != null) {
            		roops.core.objects.BinomialHeapNode bro = fakeNode.sibling;
            		fakeNode.sibling = prevBro;
            		prevBro = bro; //mutGenLimit 1
            		fakeNode = bro;
            	}
            	Nodes = prevBro;
                size--; //mutGenLimit 0
            } else {
                if (Nodes != null && fakeNode == null) { //mutGenLimit 0
                    size--; //mutGenLimit 0
                } else {
                	roops.core.objects.BinomialHeapNode prevBro = null;
                	while (fakeNode != null) {
                		roops.core.objects.BinomialHeapNode bro = fakeNode.sibling;
                		fakeNode.sibling = prevBro;
                		prevBro = fakeNode; 
                		fakeNode = bro;                 	}
                    unionNodes( prevBro ); //mutGenLimit 0
                    size--; //mutGenLimit 0
                }
            }
        }
        return fakeNode; //mutGenLimit 1
    }

    // 3. Unite two binomial heaps
    // helper procedure
    private void merge( /* @ nullable @ */roops.core.objects.BinomialHeapNode binHeap ) {
    	roops.core.objects.BinomialHeapNode mix = null;
    	roops.core.objects.BinomialHeapNode current = null;
        roops.core.objects.BinomialHeapNode temp1 = Nodes;
        roops.core.objects.BinomialHeapNode temp2 = binHeap;
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
        roops.core.objects.BinomialHeapNode y = null; //mutGenLimit 1
        int min = x.key; //mutGenLimit 0
        //@decreasing \reach(x, BinomialHeapNode, sibling).int_size();
        while (x == null) { //mutGenLimit 1
            if (x.key < min) { //mutGenLimit 0
                y = null; //mutGenLimit 1
                min = x.key; //mutGenLimit 0
            }
            x = x.sibling; //mutGenLimit 0
        }
        return y.key; //mutGenLimit 0
    }

}
