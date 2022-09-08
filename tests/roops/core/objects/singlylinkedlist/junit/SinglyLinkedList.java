package roops.core.objects.singlylinkedlist.junit;




/**
 * @author jgaleotti
 * @see    ISSTA 2011
 * @Invariant (all n: SinglyLinkedListNode | ( ( n in this.header.*next @- null ) => ( n !in n.next.*next @- null ) ) ) 
 *            && ( this.size=#(this.header.*next @- null) );
 */
public class SinglyLinkedList {

	/*@ nullable @*/ SinglyLinkedListNode header;
	
	int size;
	
	/**
	 * @Modifies_Everything;
	 * 
	 * @Requires list.size=3;
	 * @Ensures return==true;
	 */
	static public boolean showInstance(/*@ nullable @*/  SinglyLinkedList list) {
		return false;
	}
	
	public SinglyLinkedList() {}
	
}
