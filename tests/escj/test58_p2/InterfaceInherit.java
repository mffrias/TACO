package escj.test58_p2;

interface A {
	//@ ghost instance public int a;
	//@ invariant -10 < a && a != 0 && a < 10;
	//@ ghost instance public int aZero;
	//@ invariant aZero < 10;
}

interface B {
	//@ public ghost instance non_null int[] b;
}

interface C extends A, B {
}

interface D extends A {
	//@ ghost instance public int d;
	//@ invariant d != 0 && d <= a;
	//@ ghost instance public int dZero;
	//@ invariant 0 <= dZero;
	//@ invariant dZero <= aZero;
}

class InterfaceInherit implements A, C {
	//@ ghost public int x;
	//@ invariant 10 <= x && x < 100;
	//@ ghost public int xZero;
	//@ invariant 0 <= xZero && xZero < 100;

	//@ public  ghost non_null  Object y;

	/*@ non_null */Object w;

	int z;
	//@ invariant 1 <= z;
	//@ invariant z < a;
	int zZero;

	//@ invariant -10 <= zZero;
	//@ invariant zZero <= aZero;

	InterfaceInherit(int bad) {
	}

	InterfaceInherit(double good) {
		//@ set a = 8;
		int[] bb = new int[120];
		//@ set b = bb;
		//@ set x = a + 50;
		Object yy = new Object();
		//@ set y = yy;
		z = 7;
		w = bb;
	}

	void m(boolean b) {
		A a = this;
		//@ set a.a = 12;
		if (b) {
			p(a);
		}
	}

	void p(A a) {
	}
}

class Sub extends InterfaceInherit implements D {
	Sub(int bad) {
		super(bad);
	}

	Sub(double good) {
		super(good);
		//@ set d = a;
	}

	Sub(Object yy, int xx) {
		super(0);
		//@ set d = a;
		//@ set x = xx;
		//@ set y = yy;
	}

	//@ requires 45 <= xx && xx < 56;
	Sub(/*@ non_null */Object yy, int xx, char zz) {
		super(0);
		//@ assert -10 <= zZero;
		//@ assert -10 <= aZero;
		//@ assert zZero < 10;
		//@ set aZero = (zZero == -10 ? 0 : (aZero < 0 ? -aZero : aZero));
		//@ set dZero = aZero;
		//@ set d = a;
		//@ set x = xx;
		//@ set y = yy;
	}
}
