package forArielGodio.absolute.bug01;
public class Absolute {



	/*@
	  @    ensures (0 <= \old(num) && \old(num) <= Integer.MAX_VALUE) ==> \result == num;
	  @    ensures (Integer.MIN_VALUE < \old(num) && \old(num) < 0) ==> \result == -num;
	  @    signals (Exception e) false;
	  @*/
	public /*@ pure @*/ int absoluteInt(int num) {
//		int[] A = new int[5];
//		if (0 > A[num])//if (0 <= num)
//			return num;
//		else
//			return -num;

			int param_num_0;

			param_num_0 = num;
			int[] t_1;

			t_1 = new int[5];
			int[] var_1_A = t_1;
			boolean t_3;
			boolean t_4;
			boolean t_5;
			int t_7;
			boolean t_8;

			t_4 = param_num_0  <  0;
			assert ! t_4;
			t_5 = param_num_0  >  var_1_A.length;
			assert t_5;
			t_3 = true;
			assert t_3;
			java.lang.RuntimeException t_2;

			t_2 = new java.lang.RuntimeException();
			if (true)
				throw t_2;
			return 0;

	}

	/*@    ensures (0L <= \old(num) && \old(num) <= Long.MAX_VALUE) ==> \result == num;
	  @    ensures (Long.MIN_VALUE < \old(num) && \old(num) < 0L) ==> \result == -num;
	  @*/
	public /*@ pure @*/ long absoluteLong(long num) {
		if (0 <= num)
			return num;
		else
			return -num;
	}
}