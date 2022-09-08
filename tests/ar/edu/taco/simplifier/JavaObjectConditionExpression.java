package ar.edu.taco.simplifier;

public class JavaObjectConditionExpression {

	private /*@ nullable @*/ JavaObjectConditionExpression next;
	
	//@ ensures \result != null;
	public /*@ nullable @*/ JavaObjectConditionExpression use_condition_expr(/*@ nullable @*/ JavaObjectConditionExpression n) {
		JavaObjectConditionExpression ret_val;
		ret_val = (n==null) ? null : n.next;
		return ret_val;
	}
}
