package forArielGodio.absolute.bug01;

//@ model import org.jmlspecs.lang.*;


public class Absolute extends java.lang.Object {


	/*@
      @ ensures (0  <=  \old(num) && \old(num)  <=  2147483647) ==> \result  ==  num;
      @ ensures (-2147483648  <  \old(num) && \old(num)  <  0) ==> \result  ==  - num;
      @ signals (java.lang.Exception e) false;
      @*/
	public /*@ pure @*/ int absoluteInt(int num) {
		int param_num_0;

		param_num_0 = num;
		int[] t_1;

		t_1 = new int[5];
		int[] var_1_A = t_1;

		{
			boolean t_3;
			boolean t_4;
			boolean t_5;
			int t_7;
			boolean t_8;

			{
				t_4 = param_num_0 < 0;
				if (t_4) {
					t_3 = true;
				} else {
					t_5 = param_num_0 > var_1_A.length;
					if (t_5) {
						t_3 = true;
					} else {
						t_3 = false;
					}
				}
			}

			if (t_3) {
				java.lang.IndexOutOfBoundsException t_2;

				t_2 = new java.lang.IndexOutOfBoundsException();
				throw t_2;
			}
			t_7 = var_1_A[param_num_0];
			t_8 = 0  >  t_7;
			if (t_8) {
				if (true) return param_num_0;
			} else {
				int t_6;

				t_6 = - param_num_0;

				if (true) return t_6;
			}
		}
		return 0;
	}


	/*@
      @ ensures (0L  <=  \old(num) && \old(num)  <=  9223372036854775807L) ==> \result  ==  num;
      @ ensures (-9223372036854775808L  <  \old(num) && \old(num)  <  0L) ==> \result  ==  - num;
      @*/
	public /*@ pure @*/ long absoluteLong(long num) {
		long param_num_1;

		param_num_1 = num;

		return 0L;
	}


	public Absolute() {
	}

}
