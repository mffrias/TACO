package escj.test64;

class T {
	T() {
		int x;
		x = m0(); //@ assert x == 0;
		x = m1(); //@ assert x == 1;
		x = m2(); //@ assert x == 2;  // warning
		x = m3(); //@ assert x == 3;  // warning
	}

	T(int y) {
		int x;
		x = this.m0(); //@ assert x == 0;
		x = (this).m0(); //@ assert x == 0;
		x = ((T) this).m0(); //@ assert x == 0;
		T t = this;
		x = t.m0(); //@ assert x == 0;  // warning
	}

	T(int z0, int z1) {
		this(z0 + z1);
		int x = m0(); //@ assert x == 0;  // warning
	}

	private int m0() {
		return 0;
	}

	final int m1() {
		return 1;
	}

	static int m2() {
		return 2;
	}

	int m3() {
		return 3;
	}

	int m4() {
		return 4;
	}

	int m5() {
		return 5;
	}
}

class R extends T {
	R() {
		int x;
		x = m3();
		//@ assert 0 <= x;  // warning
		x = m4();
		//@ assert x == 14;
		x = m5();
		//@ assert x == 5;  // warning
		x = super.m4();
		//@ assert 0 <= x;  // warning
		x = super.m5();
		//@ assert x == 5;  // warning
	}

	int m3() {
		return 13;
	}

	final int m4() {
		return 14;
	}
}

final class S extends T {
	S() {
		int x;
		x = m3(); 
		//@ assert x == 3;  // warning
		x = m4(); 
		//@ assert x == 24;
		x = m5(); 
		//@ assert x == 25;
	}

	final int m4() {
		return 24;
	}

	int m5() {
		return 25;
	}
}
