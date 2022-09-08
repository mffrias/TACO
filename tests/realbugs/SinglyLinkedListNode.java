package realbugs;

/*@nullable_by_default@*/
public class SinglyLinkedListNode {

	public /*@ nullable @*/ SinglyLinkedListNode next;
	
	public /*@ nullable @*/ Object value;
	
	public SinglyLinkedListNode() {}
		
	public SinglyLinkedListNode getNext() {
		return next;
	}
	
	public void setNext(SinglyLinkedListNode newNext) {
		next = newNext;
	}
}
