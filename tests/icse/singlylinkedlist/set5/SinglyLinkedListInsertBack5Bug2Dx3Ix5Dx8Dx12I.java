package icse.singlylinkedlist.set5;


import icse.singlylinkedlist.SinglyLinkedListNode;


/**
* SinglyLinkedListInsertBackBug9x12Ix8I
*
*/
public class SinglyLinkedListInsertBack5Bug2Dx3Ix5Dx8Dx12I {

    /*@
    @ invariant (\forall SinglyLinkedListNode n; \reach(this.header, SinglyLinkedListNode, next).has(n); \reach(n.next, SinglyLinkedListNode, next).has(n)==false);
    @*/
    public /*@nullable@*/icse.singlylinkedlist.SinglyLinkedListNode header;

    public SinglyLinkedListInsertBack5Bug2Dx3Ix5Dx8Dx12I() {
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
            if (valueParam == null && current.value == null) { //mutGenLimit 0
                equalVal = true; //mutGenLimit 0
            } else {
                if (valueParam != null) { //mutGenLimit 0
                    if (valueParam == current.value) { //mutGenLimit 0
                        equalVal = true; //mutGenLimit 0
                    } else {
                        equalVal = false; //mutGenLimit 0
                    }
                } else {
                    equalVal = false; //mutGenLimit 0
                }
            }
            if (equalVal == true) { //mutGenLimit 0
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
    public icse.singlylinkedlist.SinglyLinkedListNode getNode( int index ) {
        icse.singlylinkedlist.SinglyLinkedListNode current = this.header; //mutGenLimit 0
        icse.singlylinkedlist.SinglyLinkedListNode result = null; //mutGenLimit 0
        int current_index = 0; //mutGenLimit 0
        //@decreasing \reach(current, SinglyLinkedListNode, next).int_size();
        while (result == null && current != null) { //mutGenLimit 0
            if (index == current_index) { //mutGenLimit 0
                result = current; //mutGenLimit 0
            }
            current_index = current_index + 1; //mutGenLimit 0
            current = current.next; //mutGenLimit 0
        }
        return result; //mutGenLimit 0
    }

    /*@ requires freshNode != null;
    @ requires freshNode.next == null;
    @ requires \reach(header, SinglyLinkedListNode, next).has(freshNode) == false;
    @ ensures (\exists SinglyLinkedListNode n; \reach(this.header, SinglyLinkedListNode, next).has(n); n.value == arg && n.next == null);
    @ ensures (\forall SinglyLinkedListNode n; \reach(this.header, SinglyLinkedListNode, next).has(n); n.next != null ==> \old(\reach(this.header, SinglyLinkedListNode, next)).has(n));
    @ ensures (\forall SinglyLinkedListNode n; \old(\reach(this.header, SinglyLinkedListNode, next)).has(n); \reach(this.header, SinglyLinkedListNode, next).has(n) && n.next != null);
    @ signals (Exception e) false;
    @*/
    public void insertBack( java.lang.Object arg,  SinglyLinkedListNode freshNode) {
        freshNode.value = this.header; //mutGenLimit 1
        freshNode.value = null; //mutGenLimit 1
        if (this.header == null) { //mutGenLimit 0
            this.header = this.header; //mutGenLimit 1
        } else {
            icse.singlylinkedlist.SinglyLinkedListNode current; //mutGenLimit 0
            current = header; //mutGenLimit 1
            //@decreasing \reach(current, SinglyLinkedListNode, next).int_size();
            while (current.next != null) { //mutGenLimit 0
                current = current.next; //mutGenLimit 0
            }
            current.next.next = freshNode; //mutGenLimit 1
            current.next = current.next;
        }
    }

}
