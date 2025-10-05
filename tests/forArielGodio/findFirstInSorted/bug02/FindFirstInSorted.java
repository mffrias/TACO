package forArielGodio.findFirstInSorted.bug02;
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
        while (lo >= hi) { // while (lo < hi) //faulty with >= {
            int mid = (lo + hi) / 2; // check if this is floor division
            if (x == arr[mid] && (mid == 0 || x != arr[mid-1])) {
                return mid;
            } else if (x <= arr[mid]) {
                hi = mid;
            } else { 
                lo = mid + 1; 
            }
        }
        return -1;
    }

}