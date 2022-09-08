package jason.jml.clist;

/**
 * A node within the linked list.
 * <p>
 * From Commons Collections 3.1, all access to the <code>value</code> property
 * is via the methods on this class.
 */

class Node extends Object {

	/*@ nullable @*/Node previous;

	/*@ nullable @*/Node next;

	/*@ nullable @*/DataObject nodeValue;

}