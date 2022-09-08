package ase2016.singlylinkedlist;

/**
* SinglyLinkedListInsertBack3Bugs11x10x7x14 is an implementation of singly linked lists with 4 bugs
* injected in lines 11, 10 and 7 and 14 of method insertBack. First bug replaces:
* previous = current;
* with
* previous = null;
* Second bug replaces:
* while (current != null)
* with
* while (current.getNext() != null)
* Third bug replaces:
* SinglyLinkedListNode current = this.header;
* with
* SinglyLinkedListNode current = this.header.next;
* Fourth bug replaces:
* previous.next = freshNode;
* with
* previous = freshNode;
* The bugs to be inserted and the affected lines were randomly chosen, from a set of
* real programming mistakes in linked list implementations. These particular ones appear in:
* http://cslibrary.stanford.edu/105/LinkedListProblems.pdf
* http://giridhar-mb.blogspot.com.ar/2012/11/linked-list-implementation-in-java.html
* http://stackoverflow.com/questions/31664980/deleting-a-specified-link-from-linkedlist
*/

import ase2016.singlylinkedlist.SinglyLinkedListNode;

public class SinglyLinkedListInsertBack4Bugs11x10x7x14 {

    /*@
    @ invariant (\forall SinglyLinkedListNode n; \reach(this.header, SinglyLinkedListNode, next).has(n); \reach(n.next, SinglyLinkedListNode, next).has(n)==false);
    @*/
    public /*@nullable@*/ase2016.singlylinkedlist.SinglyLinkedListNode header;

    public SinglyLinkedListInsertBack4Bugs11x10x7x14() {
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
            ase2016.singlylinkedlist.SinglyLinkedListNode current = this.header.next; //mutGenLimit 1
            ase2016.singlylinkedlist.SinglyLinkedListNode previous = null; //mutGenLimit 0
            //@decreasing \reach(current, SinglyLinkedListNode, next).int_size();
            while (current.getNext() != null) { //mutGenLimit 1
            	previous = null; //mutGenLimit 1
                current = current.next; //mutGenLimit 0
            }
            previous = freshNode; //mutGenLimit 1
        }
    }

}
