package ar.edu.taco.jml;

//@ model import org.jmlspecs.models.*; 


/*@ nullable_by_default @*/
public class LinkList {
    
    //@ model instance non_null JMLObjectSequence aSeq;
    
    Value head; //@ in objectState;
    Value tail; //@ in objectState;
    int size; //@ in objectState;
    
    /*@ invariant (head == null && tail == null && size == 0) ||
      @   (head.prev == null && tail.next == null &&
      @    \reach(head, Value, next).int_size() == size &&
      @    \reach(head, Value, next).has(tail) &&
      @    (\forall Value v; \reach(head, Value, next).has(v) ; v.next != null ==> v.next.prev == v));
      @*/
    
    /*@ represents aSeq \such_that
      @  (size == aSeq.int_size()) &&
      @  (head == null ==> aSeq.isEmpty()) &&
      @  (head != null ==> (head == aSeq.get(0) && tail == aSeq.get(size - 1))) &&
      @  (\forall int i ; i >= 0 && i < size - 1; ((Value)aSeq.get(i)).next == aSeq.get(i + 1));
      @*/
    
    /*@ normal_behavior
      @   requires index >= 0 && index < aSeq.int_size();
      @   ensures \result == aSeq.get(index);
      @ also
      @ exceptional_behavior
      @   requires index < 0 || index >= aSeq.int_size();
      @   signals_only IndexOutOfBoundsException;
      @*/
     /*@ pure @*/ Value get_buggy(int index) {
         // check bounds
        if (index < 0 || index >= size ) {
            throw new IndexOutOfBoundsException();
        }
        
        // optimize for common cases
        if (index == 0) return head;
        if (index == size - 1) return tail;

        Value value;
        
        // if index is in front half of list,
        // search from the beginning
        if (index <= (size >> 1)) {
            value = head;
            for (int i = 0; i < index; i++) {
                value = value.next;
            }
        }
        
        // if index is in back half of list,
        // search from the end
        else {
            value = tail;
            
            for (int i = size; i > index; i--) {
                value = value.prev;
            }

        }
        
        return value;
        
    }

     /*@ normal_behavior
     @   requires index >= 0 && index < aSeq.int_size();
     @   ensures \result == aSeq.get(index);
     @ also
     @ exceptional_behavior
     @   requires index < 0 || index >= aSeq.int_size();
     @   signals_only IndexOutOfBoundsException;
     @*/
    /*@ pure @*/ Value get(int index) {
        // check bounds
       if (index < 0 || index >= size ) {
           throw new IndexOutOfBoundsException();
       }
       
       // optimize for common cases
       if (index == 0) return head;
       if (index == size - 1) return tail;

       Value value;
       
       // if index is in front half of list,
       // search from the beginning
       if (index <= (size >> 1)) {
           value = head;
           for (int i = 0; i < index; i++) {
               value = value.next;
           }
       }
       
       // if index is in back half of list,
       // search from the end
       else {
           value = tail;
           
           for (int i = size - 1; i > index; i--) {
               value = value.prev;
           }

       }
       
       return value;
       
   }
     
     
     /*@ nullable_by_default @*/
    public static class Value {
        Value next, prev;
    }
}
