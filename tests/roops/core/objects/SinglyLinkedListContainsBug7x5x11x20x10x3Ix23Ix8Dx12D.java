package roops.core.objects;


import roops.core.objects.SinglyLinkedListNode;


public class SinglyLinkedListContainsBug7x5x11x20x10x3Ix23Ix8Dx12D {

    /*@
    @ invariant (\forall SinglyLinkedListNode n; \reach(this.header, SinglyLinkedListNode, next).has(n); \reach(n.next, SinglyLinkedListNode, next).has(n)==false);
    @*/
    public /*@nullable@*/roops.core.objects.SinglyLinkedListNode header;

    public SinglyLinkedListContainsBug7x5x11x20x10x3Ix23Ix8Dx12D () {
    }

    /*@
    @ requires true;
    @ ensures (\exists SinglyLinkedListNode n; \old(\reach(this.header, SinglyLinkedListNode, next)).has(n); n.value==valueParam) ==> (\result==true);
    @ ensures (\result == true) ==> (\exists SinglyLinkedListNode n; \old(\reach(this.header, SinglyLinkedListNode, next).has(n)); n.value==valueParam);
    @ ensures (\forall SinglyLinkedListNode n; \old(\reach(this.header, SinglyLinkedListNode, next).has(n)); \old(n.value) == n.value);
    @ signals (RuntimeException e) false;
    @
    @*/
    public boolean contains ( /*@nullable@*/java.lang.Object valueParam,/*@nullable@*/  java.lang.Object customvar_0,/*@nullable@*/  java.lang.Object customvar_1,/*@nullable@*/  java.lang.Object customvar_2,/*@nullable@*/  java.lang.Object customvar_3,/*@nullable@*/  roops.core.objects.SinglyLinkedListNode customvar_4,/*@nullable@*/  roops.core.objects.SinglyLinkedListNode customvar_5,/*@nullable@*/  roops.core.objects.SinglyLinkedListNode customvar_6,/*@nullable@*/  roops.core.objects.SinglyLinkedListNode customvar_7, boolean customvar_8, boolean customvar_9, boolean customvar_10, boolean customvar_11, boolean customvar_12, boolean customvar_13, boolean customvar_14, boolean customvar_15, boolean customvar_16, boolean customvar_17, boolean customvar_18, boolean customvar_19, boolean customvar_20, boolean customvar_21, boolean customvar_22, boolean customvar_23, boolean customvar_24, boolean customvar_25, boolean customvar_26, boolean customvar_27, boolean customvar_28, boolean customvar_29, boolean customvar_30, boolean customvar_31, boolean customvar_32, boolean customvar_33, boolean customvar_34, boolean customvar_35) {
        roops.core.objects.SinglyLinkedListNode current = null;
        boolean result;
        current.value = this.header; //mutGenLimit 1 mutID 1
        result
         = false;
        {
            if ( customvar_32 ) { //mutGenLimit 1 mutID 2
                boolean equalVal;
                if ( customvar_28 ) { //mutGenLimit 1 mutID 3
                    equalVal = customvar_24; //mutGenLimit 1 mutID 4
                } else {
                    if ( customvar_20 ) { //mutGenLimit 1 mutID 5
                        if ( customvar_16 ) { //mutGenLimit 1 mutID 6
                            equalVal = customvar_12; //mutGenLimit 1 mutID 7
                        } else {
                            equalVal = false;
                        }
                    } else {
                        equalVal = false;
                    }
                }
                if ( customvar_8 ) { //mutGenLimit 1 mutID 8
                    result = true;
                }
                customvar_4.value = customvar_0; //mutGenLimit 0 mutID 9
            }
            if ( customvar_33 ) { //mutGenLimit 1 mutID 2
                boolean equalVal;
                if ( customvar_29 ) { //mutGenLimit 1 mutID 3
                    equalVal = customvar_25; //mutGenLimit 1 mutID 4
                } else {
                    if ( customvar_21 ) { //mutGenLimit 1 mutID 5
                        if ( customvar_17 ) { //mutGenLimit 1 mutID 6
                            equalVal = customvar_13; //mutGenLimit 1 mutID 7
                        } else {
                            equalVal = false;
                        }
                    } else {
                        equalVal = false;
                    }
                }
                if ( customvar_9 ) { //mutGenLimit 1 mutID 8
                    result = true;
                }
                customvar_5.value = customvar_1; //mutGenLimit 0 mutID 9
            }
            if ( customvar_34 ) { //mutGenLimit 1 mutID 2
                boolean equalVal;
                if ( customvar_30 ) { //mutGenLimit 1 mutID 3
                    equalVal = customvar_26; //mutGenLimit 1 mutID 4
                } else {
                    if ( customvar_22 ) { //mutGenLimit 1 mutID 5
                        if ( customvar_18 ) { //mutGenLimit 1 mutID 6
                            equalVal = customvar_14; //mutGenLimit 1 mutID 7
                        } else {
                            equalVal = false;
                        }
                    } else {
                        equalVal = false;
                    }
                }
                if ( customvar_10 ) { //mutGenLimit 1 mutID 8
                    result = true;
                }
                customvar_6.value = customvar_2; //mutGenLimit 0 mutID 9
            }
            if ( customvar_35 ) { //mutGenLimit 1 mutID 2
                boolean equalVal;
                if ( customvar_31 ) { //mutGenLimit 1 mutID 3
                    equalVal = customvar_27; //mutGenLimit 1 mutID 4
                } else {
                    if ( customvar_23 ) { //mutGenLimit 1 mutID 5
                        if ( customvar_19 ) { //mutGenLimit 1 mutID 6
                            equalVal = customvar_15; //mutGenLimit 1 mutID 7
                        } else {
                            equalVal = false;
                        }
                    } else {
                        equalVal = false;
                    }
                }
                if ( customvar_11 ) { //mutGenLimit 1 mutID 8
                    result = true;
                }
                customvar_7.value = customvar_3; //mutGenLimit 0 mutID 9
            }
        }
        return result;
    }

    /*@
    @ requires index>=0 && index<\reach(this.header, SinglyLinkedListNode, next).int_size();
    @
    @ ensures \reach(this.header, SinglyLinkedListNode, next).has(\result)==true;
    @ ensures \reach(\result, SinglyLinkedListNode, next).int_size() == \reach(this.header, SinglyLinkedListNode, next).int_size()-index;
    @ signals (RuntimeException e) false;
    @*/
    public roops.core.objects.SinglyLinkedListNode getNode ( int index) {
        roops.core.objects.SinglyLinkedListNode current = this.header;
        roops.core.objects.SinglyLinkedListNode result = null;
        int current_index = 0;
        while ( result == null && current != null ) { //mutGenLimit 2
            if ( index == current_index ) { //mutGenLimit 2
                result = current; //mutGenLimit 2
            }
            current_index = current_index + 1; //mutGenLimit 2
            current
             = current.next; //mutGenLimit 2
        }
        return result;
    }

    /*@ requires true;
    @ ensures (\exists SinglyLinkedListNode n; \reach(this.header, SinglyLinkedListNode, next).has(n); n.value == arg && n.next == null);
    @ ensures (\forall SinglyLinkedListNode n; \reach(this.header, SinglyLinkedListNode, next).has(n); n.next != null ==> \old(\reach(this.header, SinglyLinkedListNode, next)).has(n));
    @*/
    public void insertBack ( java.lang.Object arg) {
        roops.core.objects.SinglyLinkedListNode freshNode = new roops.core.objects.SinglyLinkedListNode ();
        freshNode.value = arg; //mutGenLimit 2
        freshNode
        .next = null; //mutGenLimit 2
        if (
         this.header == null ) {
            this.header = freshNode;
        } else {
            roops.core.objects.SinglyLinkedListNode current;
            current = this.header; //mutGenLimit 2
            while ( 
            current.next != null ) { //mutGenLimit 2
                current = current.next;
            }
            current.next = freshNode;
        } //mutGenLimit 2
    }
}
