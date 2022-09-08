package pldi.singlylinkedlist;

/*@nullable_by_default@*/
public class SinglyLinkedListNode {

	public /*@ nullable @*/ SinglyLinkedListNode next;
	
	public /*@ nullable @*/ Object value;
	
	public SinglyLinkedListNode() {}
}
