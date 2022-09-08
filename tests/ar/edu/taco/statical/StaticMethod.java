package ar.edu.taco.statical;

public class StaticMethod {

	//@ requires arg==null;
	//@ ensures \result!=null;
	public static /*@ nullable @*/ Object nullArg(/*@ nullable @*/ Object arg) {
		return null;
	}
}
