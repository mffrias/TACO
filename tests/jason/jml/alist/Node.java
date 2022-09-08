package jason.jml.alist;

/**
 * A node within the linked list.
 * <p>
 * From Commons Collections 3.1, all access to the <code>value</code> property
 * is via the methods on this class.
 */

class Node extends Object {

	/*@ nullable @*/Node previous;

	/*@ nullable @*/Node next;

	public /*@ nullable @*/DataObject nodeValue;

}