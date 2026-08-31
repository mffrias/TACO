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

	/*@    requires num != Integer.MIN_VALUE;
	  @    ensures \result >=0;
	  @*/
	public /*@ pure @*/ int absoluteInt(int num) {
		if (0 <= num)
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

//	public static void main(String[] args){
//		Absolute a = new Absolute();
//		int i = 2147483647 + 1;
//		System.out.println(a.absoluteInt(i));
//	}

}