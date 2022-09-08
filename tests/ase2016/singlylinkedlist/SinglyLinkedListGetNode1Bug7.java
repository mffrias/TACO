package ase2016.singlylinkedlist;

/**
* SinglyLinkedListGetNode1Bug7 is an implementation of singly linked lists with a bug
* injected in line 7 of method getNode. This bug replaces:
* result = current;
* with
* result = current.next;
* The bug to be inserted and the affected line were randomly chosen, from a set of
* real programming mistakes in linked list implementations. This particular one appears in:
* http://cslibrary.stanford.edu/105/LinkedListProblems.pdf
*/

import ase2016.singlylinkedlist.SinglyLinkedListNode;

public class SinglyLinkedListGetNode1Bug7 {

    /*@
    @ invariant (\forall SinglyLinkedListNode n; \reach(this.header, SinglyLinkedListNode, next).has(n); \reach(n.next, SinglyLinkedListNode, next).has(n)==false);
    @*/
    public /*@nullable@*/ase2016.singlylinkedlist.SinglyLinkedListNode header;

    public SinglyLinkedListGetNode1Bug7() {
    }

    /*@
    @ requires index>=0 && index<\reach(this.header, SinglyLinkedListNode, next).int_size();
    @
    @ ensures \reach(this.header, SinglyLinkedListNode, next).has(\result)==true;
    @ ensures \reach(\result, SinglyLinkedListNode, next).int_size() == \reach(this.header, SinglyLinkedListNode, next).int_size()-index;
    @ signals (RuntimeException e) false;
    @*/
    public ase2016.singlylinkedlist.SinglyLinkedListNode getNode( int index ) {
        ase2016.singlylinkedlist.SinglyLinkedListNode current = this.header; //mutGenLimit 0
        ase2016.singlylinkedlist.SinglyLinkedListNode result = null; //mutGenLimit 0
        int current_index = 0; //mutGenLimit 0
        //@decreasing \reach(current, SinglyLinkedListNode, next).int_size();
        while (result == null && current != null) { //mutGenLimit 0
            if (index == current_index) { //mutGenLimit 0
                result = current.next; //mutGenLimit 1
            }
            current_index = current_index + 1; //mutGenLimit 0
            current = current.next; //mutGenLimit 0
        }
        return result; //mutGenLimit 0
    }

}
