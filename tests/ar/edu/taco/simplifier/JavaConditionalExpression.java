package ar.edu.taco.simplifier;

public class JavaConditionalExpression {

	//@ ensures \result==a || \result==b;
	public int max(int a, int b) {
		int result;
		result = (a > b) ? a : b;
		return result;
	}
}
