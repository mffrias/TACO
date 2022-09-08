package ayvmyc1o2017;

public class SLL {


	/*@ invariant (\forall Node n; 
	  @ 	\reach(head, Node, next).has(n);
	  @ 	!\reach(n.next, Node, next).has(n)
	  @	);
	  @ invariant \reach(head, Node, next).int_size() == size;
	 */

	public Node head;
	public int size;


	/*@ requires true;
	  @ ensures \result <==> 
	  @ 	(\exists Node n; 
	  @			\old(\reach(head, Node, next)).has(n);
	  @			n.key == value);
	  @ 
	  @*/
	public boolean contains(int value){
		Node current = head;

		//@decreasing \reach(current, Node, next).int_size();
		while (current != null && current.key != value){
			current = current.next;
		}
		if (current != null){
			return true;
		}
		return false;
	}

}
