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
		if (i >= 0){
			if(j >= 0){
				j = i;
			}
		} else {
			i = j;
		}
		return num;

//		int x = 1;
//		int y = 2;
//		int sum = x;
//		if (y <= 0) {
//			int n = y;
//		} else {
//			int n = -y;
//		}
//		return sum;

	}

	/*@    ensures (0L <= \old(num) && \old(num) <= Long.MAX_VALUE) ==> \result == num;
	  @    ensures (Long.MIN_VALUE < \old(num) && \old(num) < 0L) ==> \result == -num;
	  @*/
	public /*@ pure @*/ long absoluteLong(long num) {
		return 0;
	}
}