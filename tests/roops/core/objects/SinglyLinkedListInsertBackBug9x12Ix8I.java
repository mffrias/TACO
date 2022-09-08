package roops.core.objects;


import roops.core.objects.SinglyLinkedListNode;


public class SinglyLinkedListInsertBackBug9x12Ix8I {

/*@
    @ invariant (\forall SinglyLinkedListNode n; \reach(this.header, SinglyLinkedListNode, next).has(n); \reach(n.next, SinglyLinkedListNode, next).has(n)==false);
    @*/    public /*@nullable@*/roops.core.objects.SinglyLinkedListNode header;

    public SinglyLinkedListInsertBackBug9x12Ix8I() {
    }

/*@ 
      @ requires true;
      @ ensures (\exists SinglyLinkedListNode n; \reach(this.header, SinglyLinkedListNode, next).has(n); n.value==valueParam) ==> (\result==true);
      @ ensures (\result == true) ==> (\exists SinglyLinkedListNode n; \reach(this.header, SinglyLinkedListNode, next).has(n); n.value==valueParam);
      @ signals (RuntimeException e) false;
      @ 
      @*/    public boolean contains( /*@nullable@*/java.lang.Object valueParam ) {
        roops.core.objects.SinglyLinkedListNode current; //mutGenLimit 1
        boolean result; //mutGenLimit 1
        current = this.header.next; //mutGenLimit 1
        result = false; //mutGenLimit 1
        while (result == false && current != null) { //mutGenLimit 1
            boolean equalVal; //mutGenLimit 1
            if (valueParam == null && current.value == null) { //mutGenLimit 1
                equalVal = false; //mutGenLimit 1
            } else {
                if (valueParam == null) { //mutGenLimit 1
                    if (valueParam == current) { //mutGenLimit 1
                        equalVal = true; //mutGenLimit 1
                    } else {
                        equalVal = false; //mutGenLimit 1
                    }
                } else {
                    equalVal = false; //mutGenLimit 1
                }
            }
            if (equalVal == true) { //mutGenLimit 1
                result = true; //mutGenLimit 1
            }
            current.next = current.next.next; //mutGenLimit 1
        }
        return result; //mutGenLimit 1
    }

/*@
      @ requires index>=0 && index<\reach(this.header, SinglyLinkedListNode, next).int_size();
      @
      @ ensures \reach(this.header, SinglyLinkedListNode, next).has(\result)==true;
      @ ensures \reach(\result, SinglyLinkedListNode, next).int_size() == \reach(this.header, SinglyLinkedListNode, next).int_size()-index;
      @ signals (RuntimeException e) false;
      @*/    public roops.core.objects.SinglyLinkedListNode getNode( int index ) {
        roops.core.objects.SinglyLinkedListNode current = this.header;
        roops.core.objects.SinglyLinkedListNode result = null;
        int current_index = 0;
        while (result == null && current != null) { //mutGenLimit 2
            if (index == current_index) { //mutGenLimit 2
                result = current; //mutGenLimit 2
            }
            current_index = current_index + 1; //mutGenLimit 2
            current = current.next; //mutGenLimit 2
        }
        return result;
    }

      /*@ requires true;
      @ ensures (\exists SinglyLinkedListNode n; \reach(this.header, SinglyLinkedListNode, next).has(n); n.value == arg && n.next == null);
      @ ensures (\forall SinglyLinkedListNode n; \reach(this.header, SinglyLinkedListNode, next).has(n); n.next != null ==> \old(\reach(this.header, SinglyLinkedListNode, next)).has(n));
      @ ensures (\forall SinglyLinkedListNode n; \old(\reach(this.header, SinglyLinkedListNode, next)).has(n); \reach(this.header, SinglyLinkedListNode, next).has(n) && n.next != null);
      @ signals (Exception e) false;
      @*/    public void insertBack( java.lang.Object arg ) {
        roops.core.objects.SinglyLinkedListNode freshNode = new roops.core.objects.SinglyLinkedListNode();
        freshNode.value = arg; 
        freshNode.next = null;
        if (this.header == null) {
            this.header = freshNode;
        } else {
        	roops.core.objects.SinglyLinkedListNode current = null;
            current.value = this.header; //mutGenLimit 1
            //@decreasing \reach(current, SinglyLinkedListNode, next).int_size();
            while (current.next == null) { //mutGenLimit 1
                current = current.next;
            }
            current.value = freshNode; //mutGenLimit 1
        }
    }

}
