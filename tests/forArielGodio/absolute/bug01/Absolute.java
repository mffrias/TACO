package forArielGodio.absolute.bug01;
public class Absolute {
	


	/*@    
	  @    ensures true;
	  @*/
	public /*@ pure @*/ int absoluteInt(int num) {
		int param_num_0;

		param_num_0 = num;
		int[] t_2;

		t_2 = new int[2];
		int[] var_1_array = t_2;
		boolean t_4;
		boolean t_5;
		boolean t_6;
		int t_8;
		boolean t_9;

		t_5 = false;
		assert t_5;
		t_4 = true;
		assert t_4;
		assert true;
		java.lang.RuntimeException t_3;

		t_3 = new java.lang.RuntimeException();
		throw t_3;
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