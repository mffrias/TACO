package escj.test07;

class divmod {
	public static void m1() {
		int y = 4 / 2;
	}

	public static void m2() {
		int y = 4 % 2;
	}

	public static void m3() {
		int y = 4;
		y /= 2;
	}

	public static void m4() {
		int y = 4;
		y %= 2;
	}

	public static void m5() {
		int y = 4;
		int x = (y %= 2) + y;
		x = x + (x /= 2);
	}
}
