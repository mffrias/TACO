package escj.test68;

// These test cases (or versions from which these were derived) provided
// by Mike Whalen.  (Many of these tests failed in previous ESC/Java versions.)

class bing {
}

class MyVector {
	void add(Object e) {
	}
}

class mwhalen {
	//@ requires 0 <= n;
	void test0(int n) {
		int[] a = new int[n];

		for (int i = 0; i <= n; i++) {
			a[i] = 5;
		}
	}

	void test1() {
		bing[] bingArray = new bing[1];

		for (int i = 0; i <= 1; i++) {
			bingArray[i] = new bing();
		}
	}

	void test2() {
		bing[] bingArray = new bing[5];

		for (int i = 0; i <= 5; i++) {
			bingArray[i] = new bing();
		}
	}

	void test3(bing oo) {
		bing[] bingArray = new bing[5];

		for (int i = 0; i <= 5; i++) {
			bingArray[i] = oo;
		}
	}

	void test4(bing oo) {
		bing[] bingArray = new bing[5];

		bingArray[0] = new bing();
		bingArray[1] = new bing();
		bingArray[2] = new bing();
		bingArray[3] = new bing();
		bingArray[4] = new bing();
		bingArray[5] = new bing();
	}

	//@ requires 0 <= n;
	void test5(int n) {
		bing[] bingArray = new bing[n];

		for (int i = 0; i <= n; i++) {
			bingArray[i] = new bing();
		}
	}

	void test6(int n, boolean b) {
		bing[] bingArray = new bing[5];

		for (int i = 0; i <= 5; i++) {
			//@ assert n != i;
			bingArray[i] = new bing();
		}
		//@ assert b;
	}

	void test7(int n, boolean b) {
		bing[] bingArray = new bing[5];

		for (int i = 0; i <= 5; i++) {
			//@ assert i < 2;
			bingArray[i] = new bing();
		}
		//@ assert b;
	}

	void test8(int n, boolean b) {
		bing[] bingArray = new bing[5];

		for (int i = 0; i <= 5; i++) {
			//@ assert i < 1;
			bingArray[i] = new bing();
		}
		//@ assert b;
	}

	void test9(int n, boolean b) {
		bing[] bingArray = new bing[5];

		for (int i = 0; i <= 5; i++) {
			//@ assert i < 2;
			P();
			Object oo = bingArray[i];
		}
		//@ assert b;
	}

	int xx;

	//@ modifies xx;
	//@ ensures xx == \old(xx)+1;
	void P() {
		xx++;
	}

	void test10() {
		int[][] intArray = new int[5][];

		for (int i = 0; i < 5; i++) {
			intArray[i] = new int[5];
		}

		//@ assert false;
	}

	void test11() {
		for (int i = 0; i < 2; i++) {
			int[] a = new int[5];
		}

		//@ assert false;
	}

	void test12() {
		for (int i = 0; i < 2; i++) {
			Object[] a = new Object[5];
		}

		//@ assert false;
	}

	void test13() {
		for (int i = 0; i < 2; i++) {
			Object o = new Object();
		}

		//@ unreachable;
	}

	void test14() {
		int x = 0;
		for (int i = 0; i < 2; i++) {
			x++;
		}

		//@ unreachable;
	}

	void test15() {
		int[][] intArray = new int[5][];

		intArray[0] = new int[5];
		intArray[1] = new int[5];
		intArray[2] = new int[5];
		intArray[3] = new int[5];
		intArray[4] = new int[5];

		for (int i = 0; i <= 5; i++) {
			for (int j = 0; j <= 5; j++) {
				intArray[i][j] = -1;
			}
		}
	}

	//@ requires intArray != null;
	//@ requires intArray.length == 5;
	//@ requires intArray[0] != null;
	//@ requires intArray[0].length == 5;
	void test16(int[][] intArray) {
		for (int i = 0; i <= 5; i++) {
			for (int j = 0; j <= 5; j++) {
				intArray[i][j] = -1;
			}
		}
	}

	void test17() {
		int x = 0;
		for (int i = 0; i < 2; i++) {
			x++;
		}

		//@ assert false;
	}

	void test50() {
		MyVector v = new MyVector();

		for (int i = 0; i < 5; i++) {
			v.add(new bing());
		}

		//@ assert false;
	}

	void test51() {
		MyVector v = new MyVector();

		v.add(new bing());
		v.add(new bing());
		v.add(new bing());
		v.add(new bing());
		v.add(new bing());

		//@ assert false;
	}
}
