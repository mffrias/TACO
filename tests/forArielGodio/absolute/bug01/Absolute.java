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
		int i = 3/num;

		int j = i++;
		return i;
	}

	/*@    ensures (0L <= \old(num) && \old(num) <= Long.MAX_VALUE) ==> \result == num;
	  @    ensures (Long.MIN_VALUE < \old(num) && \old(num) < 0L) ==> \result == -num;
	  @*/
	public /*@ pure @*/ long absoluteLong(long num) {
		return 0;
	}
}