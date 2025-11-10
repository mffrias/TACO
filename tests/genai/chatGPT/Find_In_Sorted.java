package genai.chatGPT;

public class Find_In_Sorted {
    /*@
  @ requires arr != null;                                                // Array must not be null
  @ requires 0 <= start && start <= end && end <= arr.length;            // Range must be valid
  @
  @ // The specified range must be sorted in non-decreasing order
  @ requires (\forall int i; start <= i && i < end - 1;
  @              arr[i] <= arr[i + 1]);
  @
  @ ensures
  @   // Case 1: x is found within [start, end)
  @   (\exists int i; start <= i && i < end; arr[i] == x) ==>
  @      (start <= \result && \result < end &&
  @       arr[\result] == x);
  @
  @ ensures
  @   // Case 2: x is not found within [start, end)
  @   !(\exists int i; start <= i && i < end; arr[i] == x) ==> \result == -1;
  @
  @ // The method should not modify the array
  @ assignable \nothing;
  @
  @ // No exceptions should occur
  @ signals (Exception e) false;
  @*/
    public static int binarySearchInRange(int[] arr, int x, int start, int end) {
        int low = start;
        int high = end - 1;
        while (low <= high) {
            int mid = (low + high) / 2;
            if (arr[mid] == x) {
                return mid;
            } else if (arr[mid] < x) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }
        return -1;
    }
}
