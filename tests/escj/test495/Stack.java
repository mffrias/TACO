package escj.test495;

/**
 * Name: Ryan Miller
 * AndrewID: rdmiller
 * TimeSpent: 7:25 - 
 */

/**
 * Array-based implementation of the stack.
 */
public class Stack {

	/**
	 * Construct the stack.
	 */
	/*@
	  @ requires capacity > 0;
	  @ ensures theArray != null && topOfStack == -1;
	  @*/
	public Stack(int capacity) {
		theArray = new Object[capacity];
		topOfStack = -1;
		//@ set theArray.owner = this;
	}

	/**
	 * Test if the stack is logically empty.
	 */
	/*@ 
	  @ ensures \result == (topOfStack == -1);
	  @*/
	public/*@ pure */boolean isEmpty() {
		return topOfStack == -1;
	}

	/**
	 * Test if the stack is logically full.
	 */
	/*@ 
	  @ ensures \result == (topOfStack >= theArray.length - 1);
	  @*/
	public/*@ pure */boolean isFull() {
		return topOfStack >= theArray.length - 1;
	}

	/**
	 * Get the most recently inserted item in the stack.
	 * Does not alter the stack.
	 */

	public/*@ pure */Object top() {
		if (isEmpty())
			return null;
		//@ assert topOfStack >= 0;
		return theArray[topOfStack];
	}

	/**
	 * Remove the most recently inserted item from the stack.
	 */
	/*@ 
	  @ requires !isEmpty();
	  @ ensures topOfStack >= -1;
	  @ ensures (\forall int j; (0 <= j && j < topOfStack)
		         ==> theArray[j] == \old(theArray[j]));
	  @ modifies this.topOfStack, this.theArray[*];
	  @*/
	public void pop() {
		theArray[topOfStack--] = null;
	}

	/**
	 * Insert a new item into the stack, if not already full.
	 */
	/*@ 
	  @ requires !isFull();
	  @ ensures topOfStack >= 0;
	  @ ensures (\forall int j; (0 <= j && j < \old(topOfStack))
				 ==> theArray[j] == \old(theArray[j]));
	  @ modifies this.topOfStack, this.theArray[*];
	  @*/
	public void push(Object x) {
		theArray[++topOfStack] = x;
	}

	/*@ invariant \typeof(this.theArray) == \type(java.lang.Object[]); */
	/*@ invariant theArray.owner == this; */
	/*@ invariant theArray != null; */
	/*@ spec_public */private Object[] theArray;

	/*@ invariant -1 <= topOfStack && topOfStack < theArray.length; */
	/*@ spec_public */private int topOfStack;
}
