package paperSATinSE;

public class BT {
	
	/*@  
	  @ invariant (\forall BTnode n;
      @     \reach(root, BTnode, left + right).has(n) == true;
      @     \reach(n.right, BTnode, right + left).has(n) == false &&
      @     \reach(n.left, BTnode, left + right).has(n) == false);	
      @
      @ invariant (\forall BTnode n; \reach(root, BTnode, left+right).has(n); 
      @     (\forall BTnode m; \reach(n.left, BTnode, left+right).has(m); 
      @          !\reach(n.right, BTnode, left+right).has(m)));
      @
      @ invariant (\forall BTnode n; \reach(root, BTnode, left+right).has(n); 
      @     (\forall BTnode m; \reach(n.right, BTnode, left+right).has(m); 
      @          !\reach(n.left, BTnode, left+right).has(m)));
	  @*/
	public /*@ nullable @*/ BTnode root;
	
	/*@ requires true;
	  @ ensures \result == root;
	  @*/
	public /*@ nullable @*/ BTnode getRoot(){
		return root;
	}
}
