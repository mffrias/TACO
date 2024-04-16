package forArielGodio.absolute.bug02;
public class Absolute {
//	/*@    requires 0 <= num && num <= Short.MAX_VALUE;
//	  @    ensures \result == num;
//	  @ also
//	  @    requires  Short.MIN_VALUE < num && num < 0;
//	  @    ensures \result == -num; @*/
//	public /*@ pure @*/ short Absolute(short num) {
//		if (0 <= num)
//			return num;
//		else
//			return (short)num;//return (short)-num;	
//	}
	
	
	
	//The bug is in the class that uses short. I am mimicking the bug in the other two methods.

	/*@    
	  @    ensures (0 <= \old(num) && \old(num) <= Integer.MAX_VALUE) ==> \result == num;
	  @    ensures (Integer.MIN_VALUE < \old(num) && \old(num) < 0) ==> \result == -num; @*/
	public /*@ pure @*/ int absoluteInt(int num) {
		if (0 <= num)
			return num;
		else
			return num;
	}

	
	
	/*@    ensures (0L <= \old(num) && \old(num) <= Long.MAX_VALUE) ==> \result == num;
	  @    ensures (Long.MIN_VALUE < \old(num) && \old(num) < 0L) ==> \result == -num; @*/
	public /*@ pure @*/ long absoluteLong(long num) {
		if (0 <= num)
			return num;
		else
			return num;	
	}
}