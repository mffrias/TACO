package forArielGodio.absolute.bug01;
public class Absolute {



	/*@    requires true;
	  @    ensures (0 <= \old(num) && \old(num) <= Integer.MAX_VALUE) ==> \result == num;
	  @    ensures (Integer.MIN_VALUE < \old(num) && \old(num) < 0) ==> \result == -num;
	  @*/
	public /*@ pure @*/ int absoluteInt(int num) {
		if (0 > num)
			return num;
		else
			return -num;
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