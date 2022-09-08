package examples.singlylinkedlist.arithmetic;


/**
 * @Invariant all n: SinglyLinkedListNode | ( ( n in this.header.*next @- null ) => ( n !in n.next.*next @- null ) ) ;
 */
public class SinglyLinkedList {

	/*@ nullable @*/SinglyLinkedListNode header;

	/**
	 * @Modifies_Everything
	 * 
	 * @Ensures true ;
	 */
	public boolean contains(/*@ nullable @*/Object value_param) {
		SinglyLinkedListNode current;
		boolean result;

		current = this.header;
		result = false;
		while (result == false && current != null) {
			boolean equalVal;

			// begin equalVal = this.jml_isEqualValue(current.nodeValue,
			// value_param);
			if (value_param == null && current.value == null)
				equalVal = true;
			else if (value_param != null)

				if (value_param == current.value)
					equalVal = true;
				else
					equalVal = false;
			else
				equalVal = false;
			// end equalVal = this.jml_isEqualValue(current.nodeValue,
			// value_param);

			if (equalVal == true) {
				result = true;
			}
			current = current.next;
		}
		return result;
	}

	/**
	 * @Modifies_Everything
	 *
	 * @Ensures true ;
	 */
	public void remove(int index) {
		SinglyLinkedListNode current;
		current = this.header;
		SinglyLinkedListNode previous;
		previous = null;
		int current_index;
		current_index = 0;
		
		boolean found = false;
		
		while (found==false && current != null) {
			if (index == current_index) {
				found = true;
			} else {
				current_index = current_index + 1;
				previous = current;
				current = current.next;
			}
		}
		
		if (previous == null)
			this.header = current.next;
		else
			previous.next = current.next;
		
	}

	/**
	 * @Modifies_Everything
	 * 
	 * @Ensures true;
	 */
	public void insertBack(/*@ nullable @*/Object arg) {
		SinglyLinkedListNode freshNode = new SinglyLinkedListNode();
		freshNode.value = arg;
		freshNode.next = null;

		if (this.header == null)
			this.header = freshNode;
		else {
			SinglyLinkedListNode current;
			current = this.header;
			while (current.next != null) {
				current = current.next;
			}
			current.next = freshNode;
		}
	}

	
	
}
