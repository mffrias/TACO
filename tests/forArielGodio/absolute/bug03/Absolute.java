package forArielGodio.absolute.bug03;
public class Absolute {
//	/*@    requires 0 <= num && num <= Short.MAX_VALUE;
//	  @    ensures \result == num;
//	  @ also
//	  @    requires  Short.MIN_VALUE < num && num < 0;
//	  @    ensures \result == -num; @*/
//	public /*@ pure @*/ short absolute(short num) {
//		if (0 <= num)
//			return num;
//		else
//			return (short)-num;	
//	}

	/*@    requires 0 <= num && num <= Integer.MAX_VALUE;
	  @    ensures \result == num;
	  @ also
	  @    requires Integer.MIN_VALUE < num && num < 0;
	  @    ensures \result == -num; @*/
	public /*@ pure @*/ int absoluteInt(int num) {
		if (0 > num)//if (0 <= num)
			return num;
		else
			return -num;
	}

	/*@    requires 0 <= num && num <= Long.MAX_VALUE;
	  @    ensures \result == num;
	  @ also
	  @    requires  Long.MIN_VALUE < num && num < 0;
	  @    ensures \result == -num; @*/
	public /*@ pure @*/ long absoluteLong(long num) {
		if (0 <= num)
			return num;
		else
			return -num;	
	}
}