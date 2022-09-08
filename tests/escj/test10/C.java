package escj.test10;

class C {

//	public static void main(String[] args) {
//
//		int[] a = { 1 };
//		sort2(a);
//	}

	//@ requires a != null && a.length == 2;
	//@ ensures a[0] <= a[1];
	static void sort2(int[] a) {
		if (a[0] > a[1]) {
			int t = a[0];
			a[0] = a[2];
			a[1] = t;
		}
		
	}

	//@ requires a != null && a.length == 3;
	//@ ensures a[0] <= a[1];
	static void sort2again(int[] a) {
		if (a[0] > a[1]) {
			int t = a[0];
			a[0] = a[2];
			a[1] = t;
		}
	}
}
