package ar.edu.taco.skunk;

import ar.edu.taco.skunk.Node;

public class SList {

    /*@ invariant (\forall Node node; \reach(this.head, Node, next).has(node) == true;
    @ \reach(node.next, Node, next).has(node) == false);
    @*/

    public SList() {
    }

    public Node head;

//    public void insertElement(int value) {
//
//        if (head == null) {
//            head = new Node(value);
//        }
//
//        Node aux = head;
//
//        while (aux.next != null)
//            aux = aux.next;
//        aux.next = new Node(value);
//    }

    
    
    /*@ requires true;
      @ ensures \result == true <==>
      @			(\exists Node node; \reach(this.head, Node, next).has(node) == true; node.value == v);
      @
      @ signals (Exception e) false;
      @*/

    public boolean hasElement(int v) {
		Node current = head;
		while(current != null){
			if(current.value == v){
				return true;
			}
		//	current = current.next;
		}
		return false;
	}
	

//    public static void main(String[] args) {
//        SList s = new SList();
//        s.insertElement(1);
//        s.insertElement(2);
//        s.insertElement(3);
//        System.out.println(s.hasElement(1));
//        System.out.println(s.hasElement(2));
//        System.out.println(s.hasElement(3));
//        System.out.println(s.hasElement(4));
//    }
}
