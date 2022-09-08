package forArielGodio.binarySearch.bug02;
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
            int mid =  high / 2;//int mid =  high / 2;

            //@ decreasing high - low;
            while (low < high && arr[mid] != key) {
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
    
    
    public static void main(String[] args) {
        forArielGodio.binarySearch.bug02.BinarySearch instance = null;
        int[] arr = new int[1];
        int key = 443114814;
        // Parameter Initialization
        arr[0] = -1;
        System.out.println(binary(arr, key));

	}
}