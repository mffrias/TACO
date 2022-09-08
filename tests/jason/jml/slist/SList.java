package jason.jml.slist;

//@ model import org.jmlspecs.models.JMLObjectSequence;
//@ model import org.jmlspecs.models.JMLObjectSet;

public class SList {
	/*@
	  @ invariant (\forall SNode n; \reach(this.head, SNode, next).has(n); !\reach(n.next, SNode, next).has(n)); 
	  @*/

	//@ model instance non_null JMLObjectSequence myseq;
	/*@ represents myseq \such_that
	  @  (\reach(this.head,SNode,next).int_size() == this.myseq.int_size()) &&
	  @  (this.head == null ==> this.myseq.isEmpty()) &&
	  @  (this.head != null ==> (this.head == this.myseq.get(0))) &&
	  @  (\forall int i ; i >= 0 && i < (this.myseq.int_size() - 1); myseq.get(i + 1) == ((SNode)myseq.get(i)).next );
	  @*/

	/*@ nullable @*/SNode head;

	/*@
	  @ assignable \nothing; 
	  @*/
	private/*@ helper @*/boolean isEqualValue(DataObject value1, DataObject value2) {
		return (value1 == value2);
	}

	// 
	/*@
	  @ ensures (\exists int i; 
	  @                i>=0 && i<this.myseq.int_size()
	  @                && this.myseq.get(i) instanceof SNode  
	  @                && ((SNode)this.myseq.get(i)).nodeValue==value_param) 
	  @         <==> \result == true;
	  @*/
	boolean contains(DataObject value_param) {
		SNode current = this.head;
		boolean result = false;
		while (result == false && current != null) {
			result = isEqualValue(value_param, current.nodeValue);
			current = current.next;
		}
		return result;
	}

	/*@
	  @ requires index>=0 && index<this.myseq.int_size();
	  @ ensures \result==this.myseq.get(index); 
	  @*/
	SNode getNode(int index) {
		SNode current = this.head;
		SNode result = null;
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
	  @          && !(\exists int i; i>=0 && i<this.myseq.int_size() && this.myseq.get(i)==freshNode);
	  @
	  @ ensures this.myseq.int_size()==\old(this.myseq).int_size()+1 &&
	  @         this.myseq.get(this.myseq.int_size()-1) == freshNode && 
	  @         (\forall int i; i>=0 && i<\old(this.myseq).int_size(); this.myseq.get(i) == \old(this.myseq).get(i)) ;
	  @*/
	void insertBack(DataObject data, SNode freshNode) {
		freshNode.nodeValue = data;
		freshNode.next = null;

		if (this.head == null)
			this.head = freshNode;
		else {
			SNode current = this.head;
			while (current.next != null) {
				current = current.next;
			}
			current.next = freshNode;
		}
	}

	/*@
	  @
	  @ assignable \nothing;
	  @
	  @ ensures (\exists int i; 
	  @               i>=0 && 
	  @               i<this.myseq.int_size() && 
	  @               ((SNode)this.myseq.get(i)).nodeValue==value_param )
	  @ 
	  @         ==> ( 
	  @               \result>=0 && 
	  @               \result<this.myseq.int_size() && 
	  @               ((SNode)this.myseq.get(\result)).nodeValue==value_param
	  @             );
	  @
	  @ ensures (\forall int i; 
	  @                i>=0 && 
	  @                i<this.myseq.int_size() && 
	  @                ((SNode)this.myseq.get(i)).nodeValue!=value_param )
	  @            
	  @         ==> \result == -1;
	  @*/
	int indexOf(DataObject value_param) {
		int i = 0;
		SNode node = this.head;
		int indexOf = -1;
		while (indexOf == -1 && node != null) {
			if (isEqualValue(value_param, node.nodeValue))
				indexOf = i;
			i++;
			node = node.next;
		}
		return indexOf;
	}

	/*@
	  @ requires index>=0 && index<this.myseq.int_size();
	  @
	  @ ensures this.myseq.int_size()==\old(this.myseq).int_size()-1 &&
	  @         (\forall int i; i>=0 && i<index;                           
	  @                         this.myseq.get(i)==\old(this.myseq).get(i)) &&
	  @         (\forall int j; j>=index && j<this.myseq.int_size(); 
	  @                         this.myseq.get(j)==\old(this.myseq).get(j+1) ) ;
	  @*/
	void remove(int index) {
		SNode current = this.head;
		SNode previous = null;
		int current_index = 0;
		while (index != current_index && current != null) {
			current_index++;
			previous = current;
			current = current.next;
		}

		if (previous == null)
			this.head = current.next;
		else
			previous.next = current.next;
	}

}
