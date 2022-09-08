package girish.github.heapbenchmarks;
import girish.github.heapbenchmarks.BNode;

//@ nullable_by_default;
public class BinomialHeap {
	
    public /*@nullable@*/ BNode head; //head of root list.

    public BinomialHeap()
    {
    }

    
    /*@
	 @ invariant true;
	 @*/


    
    /**
     * Inserts element in to the Heap by making heap with one element and merging it.
    */
    public void insert(BNode x)
    {
        BinomialHeap h = new BinomialHeap();
        h.head = x;
        // create a heap with single node and merge it with current tree.
        BinomialHeap newH = this.meld(h);
        head = newH.head;
    }

    public boolean isEmpty(){
        return head == null;
    }

    /**
     * pair binomial trees.
     * y The root of one binomial tree which will be parent.
     * z The root of another binomial tree which will be child;
     */
    public void pair(BNode y, BNode z)
    {
        y.p = z;
        y.sibling = z.child;
        z.child = y;
        z.degree++;
    }

    /**
     *  Merge this heap with another Binomial Heap.
     *  h2 heap to be merged.
    */
    /*@ requires true;
      @
      @*/
    public BinomialHeap meld(BinomialHeap h2)
    {
        BinomialHeap h = new BinomialHeap();
        h.head = mergeRootList(this, h2); //merge root lists for both heaps sorted on degree of tree nodes.
        head = null;
        h2.head = null;

        if (h.head == null)
            return h;

        //Preparing to compare two consective nodes
        BNode prevX = null;
        BNode x = h.head;
        BNode nextX = x.sibling;

        //Merge ones with same degrees. algo from cormen.

        while (nextX != null) {
            //when degree are not equal or this is not last two node when equal move
            //forward.
            if (x.degree != nextX.degree || (nextX.sibling != null && nextX.sibling.degree == x.degree)) {
                prevX = x;
                x = nextX;
            }
            else {
                //if degrees are equal prepare for  compare keys.
                if (x.key < nextX.key) { //when current is less than next
                    x.sibling = nextX.sibling;
                    pair(nextX, x);//combine trees with same degree
                }
                else {
                    // when current is > next.
                    if (prevX == null)
                        h.head = nextX;
                    else
                        prevX.sibling = nextX;

                    pair(x, nextX); //combine trees with same degree
                    x = nextX;
                }
            }

            nextX = x.sibling;
        }

        return h;
    }



    /**
     *Make a root list with head of trees from BinomialHeaps h1 and h2
     *in sorted order of degree.
     */
    /*@ requires true;
      @
      @*/
    public static BNode mergeRootList(BinomialHeap h1, BinomialHeap h2)
    {
        // If either root list is empty, just return the other.
        if (h1.head == null)
            return h2.head;
        else if (h2.head == null)
            return h1.head;
        else {
            BNode head;        // head of merged list
            BNode tail;        // last node added to merged list
            BNode h1Next = h1.head,
                  h2Next = h2.head; // next nodes to be examined in h1 and h2

            //Initialize for comparision
            //head contains smallest one
            if (h1.head.degree <= h2.head.degree) {
                head = h1.head;
                h1Next = h1Next.sibling;
            }
            else {
                head = h2.head;
                h2Next = h2Next.sibling;
            }

            tail = head;
            // Go through both root lists until one of them is
            // exhausted. merge them in to a sorted list based on degree

            while (h1Next != null && h2Next != null) {
                if (h1Next.degree <= h2Next.degree) {
                    tail.sibling = h1Next;
                    h1Next = h1Next.sibling;
                }
                else {
                    tail.sibling = h2Next;
                    h2Next = h2Next.sibling;
                }

                tail = tail.sibling;
            }

            //Merge remaining trees from other list that didn't
            //run out first.
            if (h1Next != null)
                tail.sibling = h1Next;
            else
                tail.sibling = h2Next;

            return head;
        }
    }

    /*@ 
     @ requires (\forall BNode n; \reach(head, BNode, sibling + child).has(n); n.p != null ==> n.key >= n.p.key );
    @ requires (\forall BNode n; \reach(head, BNode, sibling + child).has(n); n.sibling != null ==> \reach(n.sibling, BNode, sibling + child).has(n) == false );
    @ requires (\forall BNode n; \reach(head, BNode, sibling + child).has(n); n.child != null ==> \reach(n.child, BNode, sibling + child).has(n) == false );
    @ requires (\forall BNode n; \reach(head, BNode, sibling + child).has(n); ( n.child != null && n.sibling != null ) ==>
    @                                                  (\forall BNode m; \reach(n.child, BNode, child + sibling).has(m); \reach(n.sibling, BNode, child + sibling).has(m) == false) );
    @ requires (\forall BNode n; \reach(head, BNode, sibling + child).has(n); ( n.child != null && n.sibling != null ) ==>
    @                                                  (\forall BNode m; \reach(n.sibling, BNode, child + sibling).has(m); \reach(n.child, BNode, child + sibling).has(m) == false) );
    @ requires (\forall BNode n; \reach(head, BNode, sibling + child).has(n); n.degree >= 0 );
    @ requires (\forall BNode n; \reach(head, BNode, sibling + child).has(n); n.child == null ==> n.degree == 0 );
    @ requires (\forall BNode n; \reach(head, BNode, sibling + child).has(n); n.child != null ==> n.degree == \reach(n.child, BNode, sibling).int_size() );
    @ requires (\forall BNode n; \reach(head, BNode, sibling + child).has(n); n.child != null ==> \reach(n.child.child, BNode, child + sibling).int_size() == \reach(n.child.sibling, BNode, child + sibling).int_size() );
    @ requires (\forall BNode n; \reach(head, BNode, sibling + child).has(n); n.child != null ==>
    @                                                  ( \forall BNode m; \reach(n.child, BNode, sibling).has(m); m.p == n ) );
    @ requires (\forall BNode n; \reach(head, BNode, sibling + child).has(n); ( n.sibling != null && n.p != null ) ==> n.degree > n.sibling.degree );
    @ requires ( \forall BNode n; \reach(head, BNode, sibling).has(n); (n.sibling != null ==> n.degree < n.sibling.degree) && (n.p == null) );
    @ requires ( \forall BNode n; \reach(head, BNode, sibling).has(n); n.key >= 0 );
    @ 
    @ ensures (\forall BNode n; \reach(head, BNode, sibling + child).has(n); n.p != null ==> n.key >= n.p.key );
    @ ensures (\forall BNode n; \reach(head, BNode, sibling + child).has(n); n.sibling != null ==> \reach(n.sibling, BNode, sibling + child).has(n) == false );
    @ ensures (\forall BNode n; \reach(head, BNode, sibling + child).has(n); n.child != null ==> \reach(n.child, BNode, sibling + child).has(n) == false );
    @ ensures (\forall BNode n; \reach(head, BNode, sibling + child).has(n); ( n.child != null && n.sibling != null ) ==>
    @                                                  (\forall BNode m; \reach(n.child, BNode, child + sibling).has(m); \reach(n.sibling, BNode, child + sibling).has(m) == false) );
    @ ensures (\forall BNode n; \reach(head, BNode, sibling + child).has(n); ( n.child != null && n.sibling != null ) ==>
    @                                                  (\forall BNode m; \reach(n.sibling, BNode, child + sibling).has(m); \reach(n.child, BNode, child + sibling).has(m) == false) );
    @ ensures (\forall BNode n; \reach(head, BNode, sibling + child).has(n); n.degree >= 0 );
    @ ensures (\forall BNode n; \reach(head, BNode, sibling + child).has(n); n.child == null ==> n.degree == 0 );
    @ ensures (\forall BNode n; \reach(head, BNode, sibling + child).has(n); n.child != null ==> n.degree == \reach(n.child, BNode, sibling).int_size() );
    @ ensures (\forall BNode n; \reach(head, BNode, sibling + child).has(n); n.child != null ==> \reach(n.child.child, BNode, child + sibling).int_size() == \reach(n.child.sibling, BNode, child + sibling).int_size() );
    @ ensures (\forall BNode n; \reach(head, BNode, sibling + child).has(n); n.child != null ==>
    @                                                  ( \forall BNode m; \reach(n.child, BNode, sibling).has(m); m.p == n ) );
    @ ensures (\forall BNode n; \reach(head, BNode, sibling + child).has(n); ( n.sibling != null && n.p != null ) ==> n.degree > n.sibling.degree );
    @ ensures ( \forall BNode n; \reach(head, BNode, sibling).has(n); (n.sibling != null ==> n.degree < n.sibling.degree) && (n.p == null) );
    @ ensures ( \forall BNode n; \reach(head, BNode, sibling).has(n); n.key >= 0 );
    @ ensures (\forall BNode n; \reach(head, BNode, child + sibling).has(n); \old(n.key) == n.key);
    @ ensures \old(head) == null ==> \result == -1;
    @ ensures \old(head) != null ==> (\exists BNode n; \old(\reach(head, BNode, child + sibling)).has(n); n.key == \result);
    @ ensures (\forall BNode n; \old(\reach(head, BNode, child + sibling)).has(n) && \old(n.key) != \result; \reach(head, BNode, child + sibling).has(n));
    @ ensures (\forall BNode n; \reach(head, BNode, child + sibling).has(n); \result <= n.key);
    @
    @     
    @ signals (Exception e) false;
    @*/
    public int removeMin()
    {
        // for an empty binomial heap.
        if (head == null)
            return -1;

        BNode x = head;
        BNode y = x.sibling; // current node
        BNode pred = x;      // y prev
        BNode xPred = null;  // x prev

        // Determine the node x with the minimum key in the root list.
        while (y != null) {
            if (y.key < x.key) {
                x = y;
                xPred = pred;
            }
            pred = y;
            y = y.sibling;
        }
        //remove node x from root list.
        if (x == head)
            head = x.sibling;
        else
            xPred.sibling = x.sibling;

        //Add children to new Bheap and meld with existing tree.
        BinomialHeap h = new BinomialHeap();

        BNode z = x.child;
        while (z != null) {
            BNode next = z.sibling;
            z.sibling = h.head;
            h.head = z;
            z = next;
        }
        BinomialHeap newH = this.meld(h);
        head = newH.head;
        return x.key;
    }

    /**
     * Decreases the key of a node. Last arguement we pass the index of nodes
     * to make search/contains operation in dijkstra O(1)
     */
    public void decreaseKey(int vertex, int k, BNode[] dist)
    {
        BNode x = dist[vertex];//get node from dist
        x.key = k; // update key
        BNode y = x;
        BNode z = y.p;

        //move node up if it voilates heap property
        while (z != null && (y.key < z.key )) {
            // Exchange the contents of y and z
            // mangles references to dist.
            int v = y.key;
            y.key = z.key;
            z.key = v;
            v = y.vertex;
            y.vertex = z.vertex;
            z.vertex = v;

            //Swap the nodes in the array of dist which holds references as we are just
            //exchanging contents.
            //Useful for contains operation required in dijkstra.
            dist[z.vertex] = z;
            dist[y.vertex] = y;

            y = z;
            z = y.p;
        }
    }
    
    
    public static void main(String[] args) {
		BinomialHeap bh = new BinomialHeap();
		BNode n1 = new BNode(15, 1);
		BNode n2 = new BNode(14, 5);
		n1.child = n2;
		n1.degree = 1;
		n1.p = null;
		n1.sibling = null;
		n2.child = null;
		n2.degree = 0;
		n2.p = n1;
		n2.sibling = null;
		bh.head = n1;
		int i = bh.removeMin();

		System.out.println(bh.head.key);
	}
}