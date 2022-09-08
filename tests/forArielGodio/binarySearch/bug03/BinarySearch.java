package forArielGodio.binarySearch.bug03;
public class BinarySearch {
    //@ requires (\forall int j; 0 <= j && j < arr.length; (\forall int i; 0 <= i && i < j ;arr[i] <= arr[j]));
    //@ ensures \result == -1 <==> (\forall int i; 0 <= i && i < arr.length; arr[i] != key);
    //@ ensures 0 <= \result && \result < arr.length ==> arr[\result] == key;
	//@ signals (Exception e) false;
    public static int binary(int[] arr, int key) {
        if (arr.length == 0) {
            return -1;
        } else {
            int low = 0;
            int high = arr.length;
            int mid =  high / 2;

            //@ decreasing high - low;
            while (low <= high && arr[mid] != key) {//while (low < high && arr[mid] != key) {
                if (arr[mid] < key) {
                    low = mid + 1;
                } else {
                    high = mid;
                }
                mid = low + (high - low) / 2;
            }
            if (low >= high) {
                return -1;
            }
            return mid;
        }
    }
    
    
//    public static void main(String[] args) {
//        forArielGodio.binarySearch.bug03.BinarySearch instance = null;
//        int[] arr = new int[2];
//        int key = -1;
//        // Parameter Initialization
//        arr[0] = -547398978;
//        arr[1] = -285505921;
//        int i = binary(arr, key);
//	}
    
}

//arr = [0]
// x = 1
// lo = 0
// hi = 1
// mid = 0

// lo = 1
// mid = 1