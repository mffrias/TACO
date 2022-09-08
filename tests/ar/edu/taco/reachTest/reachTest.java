package ar.edu.taco.reachTest;

public class reachTest {

	public reachTest field1;
	
	public reachTest field2;
	
	
	/*@ invariant (\forall reachTest n; \reach(this, reachTest, field1).has(n)==true; 
	 * \reach(n.field1, reachTest, field1).has(n) == false && 
	 * \reach(n.field2, reachTest, field2).has(n) == false);
	@*/
	/*@ invariant \reach(this, reachTest, field1 + field2).has(this)==true; @*/
	
	/*@ requires true; 
	  @ ensures true; 
	  @*/
	public void testReachMethod(){}
	
}
