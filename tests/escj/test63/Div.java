package escj.test63;

class Div {

	//@ requires lo <= hi;
	//@ requires 0 <= lo; // FIXME - should not need this assumption
	void m(int lo, int hi) {
		int mid = (lo + hi) / 2;
		//@ assert lo <= mid;
		//@ assert mid <= hi;
	}
}
