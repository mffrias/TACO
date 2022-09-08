package examples.linkedlist.base;

/**
 * A node within the linked list.
 * <p>
 * From Commons Collections 3.1, all access to the <code>value</code> property
 * is via the methods on this class.
 */
class LinkedListNode {

    /** A pointer to the node before this node */
    public /*@ nullable @*/LinkedListNode previous;
    /** A pointer to the node after this node */
    public /*@ nullable @*/LinkedListNode next;
    /** The object contained within this node */
    public /*@ nullable @*/Object value;

    /**
     * Constructs a new header node.
     */
    protected LinkedListNode() {
        super();
        previous = this;
        next = this;
    }

    /**
     * Constructs a new node.
     * 
     * @param value  the value to store
     */
    protected LinkedListNode(Object value) {
        super();
        this.value = value;
    }
    
    /**
     * Constructs a new node.
     * 
     * @param previous  the previous node in the list
     * @param next  the next node in the list
     * @param value  the value to store
     */
    protected LinkedListNode(LinkedListNode previous, LinkedListNode next, Object value) {
        super();
        this.previous = previous;
        this.next = next;
        this.value = value;
    }
    
    /**
     * Gets the value of the node.
     * 
     * @return the value
     * @since Commons Collections 3.1
     */
    protected /*@ pure @*/Object getValue() {
        return value;
    }
    
    /**
     * Sets the value of the node.
     * 
     * @param value  the value
     * @since Commons Collections 3.1
     */
    protected void setValue(Object value) {
        this.value = value;
    }
    
    /**
     * Gets the previous node.
     * 
     * @return the previous node
     * @since Commons Collections 3.1
     */
    protected LinkedListNode getPreviousNode() {
        return previous;
    }
    
    /**
     * Sets the previous node.
     * 
     * @param previous  the previous node
     * @since Commons Collections 3.1
     */
    protected void setPreviousNode(LinkedListNode previous) {
        this.previous = previous;
    }
    
    /**
     * Gets the next node.
     * 
     * @return the next node
     * @since Commons Collections 3.1
     */
    protected LinkedListNode getNextNode() {
        return next;
    }
    
    /**
     * Sets the next node.
     * 
     * @param next  the next node
     * @since Commons Collections 3.1
     */
    protected void setNextNode(LinkedListNode next) {
        this.next = next;
    }
}