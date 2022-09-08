package ar.edu.taco.skunk;

public  class Node {
    public int value;
    /*@ nullable @*/ public Node next;

    public Node(int value) {
        this.value = value;
        this.next = null;
    }
}
