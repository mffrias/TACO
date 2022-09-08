package escj.test55;

class C_flags {
	int i;

	void m0(Integer l) {
		int q = l.intValue();
		m1(l);
	}

	void m1(Integer k) {
		m2(k);
	}

	private void m2(Integer j) {
		i = j.intValue();
	}

}
