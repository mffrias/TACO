package escj.test48;

class verbose {
	int x;

	//@ invariant x == 0;

	void m(int y) {
		x = y;
		n(y);
	}

	private/*@ helper */void n(int y) {
	}

	void p(int y) {
		m(y);
		x = y + 1;
	}
}
