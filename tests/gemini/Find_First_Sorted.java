package gemini;

public class Find_First_Sorted {
    /*@
      @ requires arr != null;
      @ requires (\forall int i; 0 <= i && i < arr.length - 1; arr[i] <= arr[i+1]);
      @ ensures \result == -1
      @         ==> (\forall int i; 0 <= i && i < arr.length; arr[i] != x);
      @ ensures \result >= 0
      @         ==> (0 <= \result && \result < arr.length);
      @ ensures \result >= 0
      @         ==> arr[\result] == x;
      @ ensures \result >= 0
      @         ==> (\forall int i; 0 <= i && i < \result; arr[i] != x);
      @ assignable \nothing;
      @*/

    public static /*@ pure @*/ int findFirstOccurrence(int[] arr, int x) {
        if (arr == null) {
            return -1; // Should be prevented by JML precondition, but safe to include.
        }

        int low = 0;
        int high = arr.length - 1;
        int result = -1; // Stores the potential first index found

        // Binary search to find the *leftmost* occurrence
        /*@
          @ maintaining low >= 0 && high < arr.length;
          @ maintaining low <= high + 1;
          @ maintaining result == -1 || (0 <= result && result < arr.length && arr[result] == x);
          @ // If x is in the array, it must be in the range arr[low..high]
          @ maintaining (\forall int i; 0 <= i && i < low; arr[i] != x || arr[i] < x);
          @ maintaining (\forall int i; high < i && i < arr.length; arr[i] != x || arr[i] > x);
          @ decreases high - low;
          @*/
        while (low <= high) {
            int mid = low + (high - low) / 2; // Prevents overflow

            if (arr[mid] == x) {
                // Found an occurrence! Store it.
                result = mid;

                // Continue searching the LEFT subarray to find an even *earlier* occurrence.
                high = mid - 1;

            } else if (arr[mid] < x) {
                // The target must be in the right half.
                low = mid + 1;
            } else { // arr[mid] > x
                // The target must be in the left half.
                high = mid - 1;
            }
        }

        return result;
    }
}
