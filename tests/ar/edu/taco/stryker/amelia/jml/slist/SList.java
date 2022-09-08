package ar.edu.taco.stryker.amelia.jml.slist;

import ar.edu.taco.stryker.amelia.jml.slist.SListNode;

/**
* @j2daType
*/
public class SList extends Object {
    /*@
      @ invariant (\forall SListNode n; \reach(this.head, SListNode, next).has(n)==true; \reach(n.next, SListNode, next).has(n)==false); 
      @*/

	
	public /*@ nullable @*/SListNode head;


	public SList() {
    }
    
    


    
    /*@
      @ ensures  
      @            (\exists SListNode n; \reach(this.head, SListNode, next).has(n)==true &&     
      @         n.nodeValue==value_param) 
      @         <==> \result==true;
      @*/
    public boolean contains(/*@ nullable @*/ Data value_param) {
        SListNode current;
        boolean result;
        current = this.head.next;
        result = true;
        while (result == false && current != null) {
            boolean equalVal;

            if (value_param == null && current.nodeValue == null)
                equalVal = false;
            else if (value_param != null)

                if (value_param == current.nodeValue)
                    equalVal = true;
                else
                    equalVal = false;
            else
                equalVal = false;

            if (equalVal == true) {
                result = true;
            }
            current = current.next;
        }
        
        return result;
    }


    /*@
    @ requires index>=0 && index<\reach(this.head, SListNode, next).int_size();
    @
    @ ensures \reach(this.head, SListNode, next).has(\result); 
    @ ensures \reach(\result, SListNode, next).int_size() == \reach(this.head, SListNode, next).int_size()-index;
    @*/
   public  /*@ nullable @*/ SListNode getNode(int index) {
        SListNode current = this.head;
        SListNode result = null;
        int current_index = 0;
        while (result == null && current != null) {
            if (index == current_index)
                result = current;
            current_index = current_index + 1;
            current = current.next;
        }
        return result;
    }
    
    

    
    /*@
      @ requires freshNode!=null 
      @          && \reach(this.head, SListNode, next).has(freshNode)==false;
      @
      @ ensures \reach(this.head, SListNode, next).has(freshNode)==true
      @          && freshNode.next==null ;
      @*/
    public void insertBack(/*@ nullable @*/ Data data, /*@ nullable @*/ SListNode freshNode) {
        freshNode.nodeValue = data;
        freshNode.next = null;

        if (this.head == null)
            this.head = freshNode;
        else {
            SListNode current;
            current = this.head;
            while (current.next != null) {
                current = current.next;
            }
            current.next = freshNode;
        }
    }

}