package escj.test54_p2;

class C3 {


	private /*@ helper */static int m(int i) {
		return 34;
	}
}

class D3 extends C3 {

	static int q(int i) {
		return m(i);
	}

	private/*@ helper */static int m(int i) {
		return 43;
	}
}
