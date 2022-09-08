package escj.test07;

public class newarray {
	//@ requires n >= 0;
	void m1(int n) {
		int[] ai = new int[n];
		long[] al = new long[n];
		char[] ac = new char[n];
		byte[] ab = new byte[n];
		short[] as = new short[n];


		boolean[] abool = new boolean[n];

		Object[] aobj = new Object[n];
		String[] astr = new String[n];

		int[][] aai = new int[n][];
		Object[][] aaobj = new Object[n][];
	}

	//@ requires n >= 0 ;
	void m2(int n) {
		int[][] aai = new int[n][n];
		long[][][] aaal = new long[n][n][];
		byte[][][] aaab = new byte[n][n][n];
		boolean[][][][] aaaabool = new boolean[n][n][n][n];
	}

	//@ requires n >= 0 ;
	void m3(int n) {
		Object[][][][] aaaaobj = new Object[n++][n++][n++][n++];
	}

	//@ requires n >= 0 ;
	void m4(int n) {
		boolean[] abool = new boolean[n];
		//@ assert abool.length == n;
	}

	//@ requires n >= 0 ;
	void m5(int n) {
		boolean[][] aabool = new boolean[n][n];
		//@ assert aabool[1].length == n;
	}
}
