package forArielGodio.findFirstInSorted.bug01;
public class FindFirstInSorted {
	/*@ requires (\forall int j; 0 <= j && j < arr.length;
      @             (\forall int i; 0 <= i && i < j ; arr[i] <= arr[j])); @*/
	//@ signals (AssertionError) true;
	//@ signals (IndexOutOfBoundsException) false;
	public static int findfirstinsorted(int[] arr, int x) {

		
//        int lo = 0;
//        int hi = arr.length;
//        //@ decreasing hi-lo;
//        while (lo < hi) { 
//            int mid = (lo + hi) / 2; // check if this is floor division
//            if (x == arr[mid] && (mid == 0 || x != arr[mid-1])) {
//                return mid;
//            } else if (x <= arr[mid]) {
//                hi = mid;
//            } else { 
//                lo = mid + 1; 
//            }
//        }
//        return -1;
//    }
		
		
		
		
		
		
		
		
		
		
//				int lo = 0;
//				int hi = arr.length;
//				int mid;
//				if (lo < hi) {
//					mid = (lo + hi) / 2;
//					if (x > arr[mid]) {
//						lo = mid + 1;
//					} else {
//						return -10;
//					}
//				} else {
//					return -10;
//				}
//				if (lo < hi) {
//					mid = (lo + hi) / 2;
//					if (x > arr[mid]) {
//						lo = mid + 1;
//					} else {
//						return -10;
//					}
//				} else {
//					return -10;
//				}
//				if (lo < hi) {
//					mid = (lo + hi) / 2;
//					if (x > arr[mid]) {
//						lo = mid + 1;
//					} else {
//						return -10;
//					}
//				} else {
//					return -10;
//				}
//				return -1;

		int lo = 0;
		int hi = arr.length;
		int mid;
		assert (lo < hi);
		mid = (lo + hi) / 2;
		assert (x > arr[mid]);
		lo = mid + 1;
		assert (lo < hi);
		mid = (lo + hi) / 2;
		assert (x > arr[mid]);
		lo = mid + 1;
		assert (lo < hi);
		mid = (lo + hi) / 2;
		assert (x > arr[mid]);
		lo = mid + 1;
		return -1;

	}


	//    public static void main(String[] args) {
	//        int[] arr = new int[2];
	//        int x = -593886814;
	//        // Parameter Initialization
	//        arr[0] = -1006108413;
	//        arr[1] = -728104542;
	//        findfirstinsorted(arr, x);
	//	}

}