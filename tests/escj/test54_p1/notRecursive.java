package escj.test54_p1;

class C2 {

	private/*@ helper */void m() {
		q();
	}

	final private/*@ helper */ void q() {
		r();
	}

	final void r() {
		q();
	}
}
