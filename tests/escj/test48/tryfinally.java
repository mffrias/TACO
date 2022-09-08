package escj.test48;

class tryfinally {
	//@ ensures \result != 2;
	int m0() {
		int x = 0;
		try {
			x++;
		} finally {
			x++;
		}
		return x; // trace label
	}

	//@ ensures \result != 2;
	int m1() {
		int x = 0;
		try {
			x++;
		} finally {
			x++;
			return x; // trace label
		}
	}

	//@ ensures \result != 2;
	int m2() {
		int x = 0;
		while (true) { // trace label
			try {
				x++;
				break; // trace label
			} finally { // trace label
				x++;
			} // trace label
		}
		return x; // trace label
	}

	//@ ensures \result != 2;
	int m3() {
		int x = 0;
		while (true) { // trace label
			try {
				x++;
				break; // trace label
			} finally { // trace label
				x++;
				return x; // trace label
			} // no trace label
		}
	}

	//@ ensures \result != 2;
	int m4() {
		int x = 0;
		while (true) { // trace label
			try {
				x++;
				return 2; // trace label
			} finally { // trace label
				x++;
			} // trace label
		}
	}

	//@ ensures \result != 2;
	int m5() {
		int x = 0;
		while (true) { // trace label
			try {
				x++;
			} finally {
				x++;
				throw new RuntimeException(); // trace label
			}
		}
	}

	//@ ensures \result != 2;
	int m6() {
		int x = 0;
		while (true) { // trace label
			try {
				x++;
				throw new RuntimeException(); // trace label
			} finally { // trace label
				x++;
			} // trace label
		}
	}

	//@ ensures \result != 2;
	int m7() {
		int x = 0;
		while (true) { // trace label
			try {
				x++;
				throw new RuntimeException(); // trace label
			} finally { // trace label
				x++;
				throw new RuntimeException(); // trace label
			}
		}
	}

	//@ ensures \result != 2;
	int m8() {
		int x = 0;
		while (true) { // trace label
			try {
				x++;
				continue; // trace label
			} finally { // trace label
				x++;
				throw new RuntimeException(); // trace label
			}
		}
	}
}
