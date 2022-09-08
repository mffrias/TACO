package realbugs;


import realbugs.SinglyLinkedListNodeInt;


public class SinglyLinkedListIntSortedInsert1Bug {

    /*@
    @ invariant (\forall SinglyLinkedListNodeInt n; \reach(this.header, SinglyLinkedListNodeInt, next).has(n); \reach(n.next, SinglyLinkedListNodeInt, next).has(n)==false);
    @*/
	
    public /*@nullable@*/ realbugs.SinglyLinkedListNodeInt header;

    public SinglyLinkedListIntSortedInsert1Bug() {
        
    }


    /*@ requires newNode!=null;
    @ requires (\forall SinglyLinkedListNodeInt n; \reach(this.header, SinglyLinkedListNodeInt, next).has(n); n!=newNode);
    @ requires (\forall SinglyLinkedListNodeInt n; \reach(this.header, SinglyLinkedListNodeInt, next).has(n) && n.next!=null; n.value<=n.next.value);
    @ ensures (\forall SinglyLinkedListNodeInt n; \reach(this.header, SinglyLinkedListNodeInt, next).has(n) && n.next!=null; n.value<=n.next.value);
    @ ensures (\forall SinglyLinkedListNodeInt n; \old(\reach(this.header, SinglyLinkedListNodeInt, next)).has(n); \reach(this.header, SinglyLinkedListNodeInt, next).has(n) && \old(n.value)==n.value);
	@ ensures (\reach(this.header, SinglyLinkedListNodeInt, next).has(newNode));
    @ signals (Exception e) false;
    @*/
    public void sortedInsert(/*@nullable@*/ realbugs.SinglyLinkedListNodeInt newNode) {
    	realbugs.SinglyLinkedListNodeInt currentRef = this.header;
    	realbugs.SinglyLinkedListNodeInt prevRef = null;
       while (currentRef!=null && currentRef.value<newNode.value) {
    	   prevRef = currentRef;
           currentRef = currentRef.next;
       } 
       newNode.next = currentRef.next; //mutGenLimit 1
       if (prevRef==null) {
    	   this.header = newNode;
       }
       else {
    	   prevRef.next = newNode;
       }
    }



}
