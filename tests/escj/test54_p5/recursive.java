package escj.test54_p5;

class C6 {

	/*@ helper */private void m() {
		q();
	}

	private void n() {
		m();
	}

	private/*@ helper */final void q() {
		r();
	}

	private/*@ helper */final void r() {
		q();
	}
}
