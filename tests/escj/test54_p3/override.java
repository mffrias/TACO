package escj.test54_p3;

class C4 {

	private int q() {
		return 22;
	}
}

class D4 extends C4 {

	private/*@ helper */final int q() {
		return 24;
	}
}
