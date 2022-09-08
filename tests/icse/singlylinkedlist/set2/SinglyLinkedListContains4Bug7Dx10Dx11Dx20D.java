package icse.singlylinkedlist.set2;


import icse.singlylinkedlist.SinglyLinkedListNode;


/**
* SinglyLinkedListInsertBackBug9x12Ix8I
*
*/
public class SinglyLinkedListContains4Bug7Dx10Dx11Dx20D {

    /*@
    @ invariant (\forall SinglyLinkedListNode n; \reach(this.header, SinglyLinkedListNode, next).has(n); \reach(n.next, SinglyLinkedListNode, next).has(n)==false);
    @*/
    public /*@nullable@*/icse.singlylinkedlist.SinglyLinkedListNode header;

    public SinglyLinkedListContains4Bug7Dx10Dx11Dx20D () {
    }

    /*@
    @ requires true;
    @ ensures (\exists SinglyLinkedListNode n; \reach(this.header, SinglyLinkedListNode, next).has(n); n.value==valueParam) ==> (\result==true);
    @ ensures (\result == true) ==> (\exists SinglyLinkedListNode n; \reach(this.header, SinglyLinkedListNode, next).has(n); n.value==valueParam);
    @ signals (RuntimeException e) false;
    @
    @*/
    public boolean contains( /*@nullable@*/java.lang.Object valueParam ) {
        icse.singlylinkedlist.SinglyLinkedListNode current;
        boolean result;
        current = this.header; //mutGenLimit 0
        result = false; //mutGenLimit 0
        //@decreasing \reach(current, SinglyLinkedListNode, next).int_size();
        while (result == false && current != null) { //mutGenLimit 0
            boolean equalVal;
            if (valueParam == null && current.value != null) { //mutGenLimit 1
                equalVal = true; //mutGenLimit 0
            } else {
                if (valueParam == null) { //mutGenLimit 1
                    if (current == current.value) { //mutGenLimit 1
                        equalVal = true; //mutGenLimit 0
                    } else {
                        equalVal = false; //mutGenLimit 0
                    }
                } else {
                    equalVal = false; //mutGenLimit 0
                }
            }
            if (true == true) { //mutGenLimit 1
                result = true; //mutGenLimit 0
            }
            current = current.next; //mutGenLimit 0
        }
        return result; //mutGenLimit 0
    }

    /*@
    @ requires index>=0 && index<\reach(this.header, SinglyLinkedListNode, next).int_size();
    @
    @ ensures \reach(this.header, SinglyLinkedListNode, next).has(\result)==true;
    @ ensures \reach(\result, SinglyLinkedListNode, next).int_size() == \reach(this.header, SinglyLinkedListNode, next).int_size()-index;
    @ signals (RuntimeException e) false;
    @*/
    public icse.singlylinkedlist.SinglyLinkedListNode getNode ( int index) {
        icse.singlylinkedlist.SinglyLinkedListNode current = this.header;
        icse.singlylinkedlist.SinglyLinkedListNode result = null;
        int current_index = 0;
        while ( result == null && current != null ) {
            if ( index == current_index ) {
                result = current;
            }
            current_index = current_index + 1;
            current = current.next;
        }
        return result;
    }

    /*@ requires true;
    @ ensures (\exists SinglyLinkedListNode n; \reach(this.header, SinglyLinkedListNode, next).has(n); n.value == arg && n.next == null);
    @ ensures (\forall SinglyLinkedListNode n; \reach(this.header, SinglyLinkedListNode, next).has(n); n.next != null ==> \old(\reach(this.header, SinglyLinkedListNode, next)).has(n));
    @ ensures (\forall SinglyLinkedListNode n; \old(\reach(this.header, SinglyLinkedListNode, next)).has(n); \reach(this.header, SinglyLinkedListNode, next).has(n) && n.next != null);
    @ signals (Exception e) false;
    @*/
    public void insertBack ( java.lang.Object arg) {
        icse.singlylinkedlist.SinglyLinkedListNode freshNode = new icse.singlylinkedlist.SinglyLinkedListNode ();
        freshNode.value = arg;
        freshNode.next = null;
        if ( this.header == null ) {
            this.header = freshNode;
        } else {
            icse.singlylinkedlist.SinglyLinkedListNode current;
            current = this.header;
            while ( current.next != null ) {
                current = current.next;
            }
            current.next = freshNode;
        }
    }
}
