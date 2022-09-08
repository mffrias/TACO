package ar.edu.itba.keepArith;

public class KeepArith {


	/*@ requires i1 > 0 && i2 > 0 && i3 > 0 && i4 > 0 && i5 > 0;
	  @ ensures \result != 0;
	 @*/
	public static int intArithTest(int i1, int i2, int i3, int i4, int i5){
		return i1 * i2 * i3 * i4 * i5;
	}



	/*@ requires i1 > 0L && i2 > 0L && i3 > 0L && i4 > 0L && i5 > 0L;
	  @ ensures \result >= 0L;
	 @*/
	public static long longArithTest(long i1, long i2, long i3, long i4, long i5){
		return i1 + i2 + i3 + i4 + i5;
	}



	/*@ requires f1 != Float.POSITIVE_INFINITY && f1 != Float.NEGATIVE_INFINITY  && f1 == 0.0000001f && f2 == 300000f;
	  @ ensures \result == 300000.0000001f;
	 @*/
	public static float floatArithTest(float f1, float f2){
			return f1 + f2;
	}










//			public static void main(String[] args) {
//		        int i1 = 2131848615;
//		        int i2 = 688141056;
//		        int i3 = 702246949;
//		        int i4 = 1635778560;
//		        int i5 = 151540434;
//				System.out.println(intArithTest(i1, i2, i3, i4, i5));
//			}


//		public static void main(String[] args) {
//	        float f1 = 1.7014118E38f;
//	        float f2 = 1.7014118E38f;
//	
//			float f = floatArithTest(f1, f2);
//			System.out.println(f == 3000f);
//			System.out.println(f);
//		}


//		public static void main(String[] args) {
//	        long i1 = 769658676322304L;
//	        long i2 = 5656683860235167237L;
//	        long i3 = 650075256953716224L;
//	        long i4 = 650075256953716224L;
//	        long i5 = 5656683860235167237L;
//			long l = longArithTest(i1, i2, i3, i4, i5);
//			System.out.println(l);
//		}
}
