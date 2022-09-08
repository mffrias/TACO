package forArielGodio.findFirstInSorted.bug01;
public class FindFirstInSorted {
    /*@ requires (\forall int j; 0 <= j && j < arr.length;
      @             (\forall int i; 0 <= i && i < j ; arr[i] <= arr[j])); @*/
    //@ ensures \result < arr.length;
    //@ ensures 0 <= \result && \result < arr.length ==> arr[\result] == x && (\forall int i; 0 <= i && i < \result; arr[i] != x);
    //@ ensures \result == -1 ==> (\forall int i; 0 <= i && i < arr.length; arr[i] != x);
	//@ signals (Exception e) false;
    public static int findfirstinsorted(int[] arr, int x) {
        int lo = 0;
        int hi = arr.length;
        //@ decreasing hi-lo;
        while (lo < hi) {
            int mid = (lo + hi) / 2; // check if this is floor division
            if (x == arr[mid] && (mid == 0 || x != arr[mid-1])) {
                return mid;
            } else if (x <= arr[mid]) {
                hi = mid;
            } else { 
                lo = mid - 1; //lo = mid + 1;
            }
        }
        return -1;
    }
//  public static int findfirstinsorted(int[] arr, int x) {
//  int lo = 0;
//  int hi = arr.length;
//  //@ decreasing hi-lo;
//  while (lo < hi) {
//      int mid = (lo + hi) / 2; // check if this is floor division
//      if (x == arr[mid]) {
//    	  if (mid == 0)
//    		  return mid;
//    	  else
//    		  if (x != arr[mid-1])
//    			  return mid;
//      } else if (x <= arr[mid]) {
//          hi = mid;
//      } else { 
//          lo = mid - 1; //lo = mid + 1;
//      }
//  }
//  return -1;
//}

    
    
//    public static void main(String[] args) {
//        forArielGodio.FindFirstInSorted instance = null;
//        int[] arr = new int[12];
//        int x = 4;
//        // Parameter Initialization
//        arr[0] = -16;
//        arr[1] = -16;
//        arr[2] = -14;
//        arr[3] = -14;
//        arr[4] = -14;
//        arr[5] = -14;
//        arr[6] = -14;
//        arr[7] = 0;
//        arr[8] = 3;
//        arr[9] = 4;
//        arr[10] = 9;
//        arr[11] = 9;
//        int i = findfirstinsorted(arr, x);
//        System.out.println(i);
//        
//
//	}
}