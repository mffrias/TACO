package escj.test11;

abstract public class State {

	/*@ non_null @*/int[] vector;

	protected State(/*@ non_null @*/int[] vector) {
		//@ set owner = this;
		this.vector = vector;
	} // error: OwnerNull

	protected State(Object x) {
		if (x instanceof int[]) {
			//@ set owner = x;
			this.vector = (int[]) x;
		} else {
			this.vector = new int[10];
		}
	} // error: OwnerNull on one path

	//@ requires 0 <= y;
	protected State(Object x, int y) {
		this.vector = new int[y];
		if (x instanceof int[]) {
			//@ set x.owner = this;  // no confusion that 'x' might be 'this'
			this.vector = (int[]) x;
		} else if (x != null) {
			//@ set x.owner = this;  // no confusion that 'x' might be 'this'
		}
	}
}
