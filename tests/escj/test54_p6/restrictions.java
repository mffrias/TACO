package escj.test54_p6;

class C7 {

	private/*@ helper */int m(int i) {
		return 34;
	}

	private int q() {
		return 22;
	}
}

class D7 extends C7 {

	int k;

	private/*@ helper */int q() {
		return 24;
	}

	private/*@ helper */int m(int i) {
		return 43;
	}
}
