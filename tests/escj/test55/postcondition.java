package escj.test55;

class C_post {
	int i;

	int m0(int l) {
		return m1(l);
	}

	//@ requires k == 5;
	//@ ensures \result == 5;
	int m1(int k) {
		return k;
	}

}
