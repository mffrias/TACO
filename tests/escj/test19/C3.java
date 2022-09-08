package escj.test19;

class C0 {
	int[] a;
	int n;

	C0(int[] input) {
		n = input.length; // null dereference
		a = new int[n];
		System.arraycopy(input, 0, a, 0, n);
	}

	// ...

}

class C1 {
	int[] a;
	int n;

	//@ requires input != null;
	C1(int[] input) {
		n = input.length;
		a = new int[n];
		System.arraycopy(input, 0, a, 0, n);
	}

	int extractMin() {
		int m = Integer.MAX_VALUE;
		int mi = 0;
		for (int i = 0; i < n; i++) {
			if (a[i] < m) { // null dereference
				mi = i;
				m = a[i];
			}
		}
		if (n != 0) {
			// Not yet implemented in checker:  n--;
			n = n - 1;
			a[mi] = a[n];
		}
		return m;
	}
}

class C2 {
	int[] a;
	//@ invariant a != null;
	int n;

	//@ requires input != null;
	C2(int[] input) {
		n = input.length;
		a = new int[n];
		System.arraycopy(input, 0, a, 0, n);
	}

	int extractMin() {
		int m = Integer.MAX_VALUE;
		int mi = 0;
		for (int i = 0; i < n; i++) {
			if (a[i] < m) { // array bounds error
				mi = i;
				m = a[i];
			}
		}
		if (n != 0) {
			// Not yet implemented in checker:  n--;
			n = n - 1;
			a[mi] = a[n];
		}
		return m;
	}
}

public class C3 {
	int[] a;
	//@ invariant a != null;
	int n;

	//@ invariant 0 <= n && n <= a.length;

	//@ requires input != null;
	C3(int[] input) {
		n = input.length;
		a = new int[n];
		System.arraycopy(input, 0, a, 0, n);
	}

	int extractMin() {
		int m = Integer.MAX_VALUE;
		int mi = 0;
		for (int i = 0; i < n; i++) {
			if (a[i] < m) {
				mi = i;
				m = a[i];
			}
		}
		if (n != 0) {
			// Not yet implemented in checker:  n--;
			n = n - 1;
			a[mi] = a[n];
		}
		return m;
	}
}
