package forArielGodio.copyArray.bug03;
public class CopyArray {
	//@ requires 0 < a.length && 0 < b.length;
	//@ requires 0 <= iBegin && 0 <= iEnd && iBegin <= iEnd;
	//@ requires iBegin < a.length && iBegin < b.length && iEnd < a.length && iEnd < b.length;
	//@ ensures (\forall int i; iBegin <= i && i < iEnd; a[i] == b[i]);
	//@ ensures (\forall int i; 0 <= i && i < iBegin; a[i] == \old(a[i]));
	//@ ensures (\forall int i; iEnd <= i && i < a.length; a[i] == \old(a[i]));
	//@ signals (Exception e) false;
	public static void copyArray(int[] b, int iBegin, int iEnd, int[] a) {
		int k = iBegin;
		//@ decreasing iEnd  - k;
		while (iEnd - k <= 0) {//while (iEnd - k > 0) {
			a[k] = b[k];
			k = k + 1 ;
		}
	}
}


	//	public static void main(String[] args) {
	//			int[] b = new int[7];
	//	        int iBegin = 0;
	//	        int iEnd = 1;
	//	        int[] a = new int[3];
	//	        // Parameter Initialization
	//	        b[0] = 4;
	//	        b[1] = 4;
	//	        b[2] = -2;
	//	        b[3] = 4;
	//	        b[4] = -2;
	//	        b[5] = -1;
	//	        b[6] = -1;
	//	        a[0] = -7;
	//	        a[1] = 7;
	//	        a[2] = -2;
	//	        copyArray(b, iBegin, iEnd, a);
	//	        System.out.println("");
	//	}

