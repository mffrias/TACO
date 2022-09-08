package ase2016.singlylinkedlist;

/**
* SinglyLinkedListInsertBack1Bug is an implementation of singly linked lists with 1 bug
* injected in line 11 of method insertBack. First bug replaces:
* previous = current;
* with
* previous = null;
* The bugs to be inserted and the affected lines were randomly chosen, from a set of
* real programming mistakes in linked list implementations. This particular one appears in:
* http://cslibrary.stanford.edu/105/LinkedListProblems.pdf
*/

import ase2016.singlylinkedlist.SinglyLinkedListNode;

public class SinglyLinkedListInsertBack1Bug11 {

    /*@
    @ invariant (\forall SinglyLinkedListNode n; \reach(this.header, SinglyLinkedListNode, next).has(n); \reach(n.next, SinglyLinkedListNode, next).has(n)==false);
    @*/
    public /*@nullable@*/ase2016.singlylinkedlist.SinglyLinkedListNode header;

    public SinglyLinkedListInsertBack1Bug11() {
    }


    /*@ requires true;
    @ ensures (\exists SinglyLinkedListNode n; \reach(this.header, SinglyLinkedListNode, next).has(n); n.value == arg && n.next == null);
    @ ensures (\forall SinglyLinkedListNode n; \reach(this.header, SinglyLinkedListNode, next).has(n); n.next != null ==> \old(\reach(this.header, SinglyLinkedListNode, next)).has(n));
    @ ensures (\forall SinglyLinkedListNode n; \old(\reach(this.header, SinglyLinkedListNode, next)).has(n); \reach(this.header, SinglyLinkedListNode, next).has(n) && n.next != null);
    @ signals (Exception e) false;
    @*/
    public void insertBack( java.lang.Object arg ) {
        ase2016.singlylinkedlist.SinglyLinkedListNode freshNode = new ase2016.singlylinkedlist.SinglyLinkedListNode(); //mutGenLimit 0
        freshNode.value = arg; //mutGenLimit 0
        freshNode.next = null; //mutGenLimit 0
        if (this.header == null) { //mutGenLimit 0
            this.header = freshNode; //mutGenLimit 0
        } else {
            ase2016.singlylinkedlist.SinglyLinkedListNode current = this.header; //mutGenLimit 0
            ase2016.singlylinkedlist.SinglyLinkedListNode previous = null; //mutGenLimit 0
            //@decreasing \reach(current, SinglyLinkedListNode, next).int_size();
            while (current != null) { //mutGenLimit 0
            	previous = null; //mutGenLimit 1
                current = current.next; //mutGenLimit 0
            }
            previous.next = freshNode; //mutGenLimit 0
        }
    }

}
