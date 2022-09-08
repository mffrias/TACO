package ar.edu.taco;

public class AssertExample {

	//@ signals (Exception ex) false;
	//@ ensures \result==true ;
	public boolean assertion_method(/*@ nullable @*/ Object o) {
		Object f = o;
		//@ assert f!=null;
		return true;
	}
}
