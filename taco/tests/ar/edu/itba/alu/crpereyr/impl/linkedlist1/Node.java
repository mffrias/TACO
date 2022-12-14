package ar.edu.itba.alu.crpereyr.impl.linkedlist1;

public class Node {
    // reference to the next node in the chain,
    // or null if there isn't one.
    public /*@ nullable */ Node next;
    // data carried by this node.
    // could be of any type you need.
    public  /*@ nullable */ Object data;

    // Node constructor
    public Node() {
        next = null;
        data = null;
    }


    // Node constructor
    public Node(Object dataValue) {
        next = null;
        data = dataValue;
    }

    // another Node constructor if we want to
    // specify the node to point to.
    public Node(Object dataValue, Node nextValue) {
        next = nextValue;
        data = dataValue;
    }

    // these methods should be self-explanatory
    public Object getData() {
        return data;
    }

    public void setData(Object dataValue) {
        data = dataValue;
    }

    public Node getNext() {
        return next;
    }

    public void setNext(Node nextValue) {
        next = nextValue;
    }
}
