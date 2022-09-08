package amelia.jml.slist;

//@ model import org.jmlspecs.models.JMLObjectSequence;
//@ model import org.jmlspecs.models.JMLObjectSet;

/**
 * @j2daType
 */
public class SList extends Object {
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

	/**
	 * @j2daField
	 */
	/*@ nullable @*/ SNode head;

	/**
	 * @j2daMethod
	 */
	// 
	/*@
	  @ ensures (\exists int i; 
	  @                i>=0 && i<this.myseq.int_size()
	  @                && this.myseq.get(i) instanceof SNode  
	  @                && ((SNode)this.myseq.get(i)).nodeValue==value_param) 
	  @         <==> \result == true;
	  @*/
	boolean contains(/*@ nullable @*/ Data value_param) {
		SNode current;
		boolean result;

		current = this.head;
		result = false;
		while (result == false && current != null) {
			boolean equalVal;

			// begin equalVal = this.jml_isEqualValue(current.nodeValue, value_param);
			if (value_param == null && current.nodeValue == null)
				equalVal = true;
			else if (value_param != null)

				if (value_param == current.nodeValue)
					equalVal = true;
				else
					equalVal = false;
			else
				equalVal = false;
			// end equalVal = this.jml_isEqualValue(current.nodeValue, value_param);

			if (equalVal == true) {
				result = true;
			}
			current = current.next;
		}
		return result;
	}

	/**
	 * @j2daMethod
	 */
	/*@
	  @ requires index>=0 && index<this.myseq.int_size();
	  @
	  @ ensures \result==this.myseq.get(index); 
	  @*/
	/*@ nullable @*/ SNode getNode(int index) {
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

	/**
	 * @j2daMethod
	 * @param data TODO
	 */
	/*@
	  @ requires freshNode!=null 
	  @          && !(\exists int i; i>=0 && i<this.myseq.int_size() && this.myseq.get(i)==freshNode);
	  @
	  @ ensures this.myseq.int_size()==\old(this.myseq).int_size()+1 &&
	  @         this.myseq.get(this.myseq.int_size()-1) == freshNode && 
	  @         (\forall int i; i>=0 && i<\old(this.myseq).int_size(); this.myseq.get(i) == \old(this.myseq).get(i)) ;
	  @*/
	void insertBack(/*@ nullable @*/ Data data, /*@ nullable @*/ SNode freshNode) {
		freshNode.nodeValue = data;
		freshNode.next = null;

		if (this.head == null)
			this.head = freshNode;
		else {
			SNode current;
			current = this.head;
			while (current.next != null) {
				current = current.next;
			}
			current.next = freshNode;
		}
	}

	/*@
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
	/**
	 * @j2daMethod
	 */
	/*@ pure @*/int indexOf(/*@ nullable @*/Data value_param) {
		int i;
		SNode node;
		i = 0;
		node = this.head;
		int indexFound;
		indexFound = -1;
		while (indexFound == -1 && node != null) {
			Data myData;
			myData = node.nodeValue;
			boolean equalValue;
			// begin equalValue = this.jml_isEqualValue(myData, value_param);
			if (myData == null && value_param == null)
				equalValue = true;
			else if (myData != null) {
				if (myData == value_param)
					equalValue = true;
				else
					equalValue = false;

			} else
				equalValue = false;
			// end equalValue = this.jml_isEqualValue(myData, value_param);

			if (equalValue == true) {
				indexFound = i;
			}
			i = i + 1;
			node = node.next;
		}
		return indexFound;
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
	/**
	 * @j2daMethod
	 */
	void remove(int index) {
		SNode current;
		current = this.head;
		SNode previous;
		previous = null;
		SNode result;
		result = null;
		int current_index;
		current_index = 0;
		while (result == null && current != null) {
			if (index == current_index)
				result = current;
			else {
				current_index = current_index + 1;
				previous = current;
				current = current.next;
			}
		}

		if (previous == null)
			this.head = current.next;
		else
			previous.next = current.next;
	}

	/*@
	  @ ensures this.myseq.int_size()==\old(this.myseq).int_size() &&
	  @         (\forall int i; i>=0 && i<this.myseq.int_size() ;                           
	  @                         this.myseq.get(i)==\old(this.myseq.get(this.myseq.int_size() - i)));
	  @*/
	void reverse() {
		SNode ln1, ln2, ln3, ln4;

		if (head == null)
			return;

		ln1 = head;
		ln2 = head.next;
		ln3 = null;

		while (ln2 != null) {
			ln4 = ln2.next;
			ln1.next = ln3;
			ln3 = ln1;
			ln1 = ln2;
			ln2 = ln4;
		}

		head = ln1;
		ln1.next = ln3;
	}
}
