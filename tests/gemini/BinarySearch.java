package gemini;

public class BinarySearch {

    /*@ public normal_behavior
      @ requires arr != null;
      @ requires \typeof(arr) == \type(int[]);
      @ requires \nonnullelements(arr);
      @ requires (\forall int i; 0 <= i && i < arr.length - 1; arr[i] <= arr[i+1]); // Array must be sorted in ascending order
      @ ensures (\result == -1) ==> !(\exists int i; 0 <= i && i < arr.length; arr[i] == key);
      @ ensures (\result != -1) ==> (0 <= \result && \result < arr.length && arr[\result] == key);
      @ assignable \nothing;
      @*/
    public static int Binary(int[] arr, int key) {
        int low = 0;
        int high = arr.length - 1;

        /*@ loop_invariant 0 <= low && low <= high + 1 && high < arr.length;
          @ loop_invariant (\forall int i; 0 <= i && i < low; arr[i] < key);
          @ loop_invariant (\forall int i; high < i && i < arr.length; arr[i] > key);
          @ loop_invariant (\exists int i; low <= i && i <= high; arr[i] == key) ==> (\exists int i; 0 <= i && i < arr.length; arr[i] == key);
          @ decreases high - low;
          @*/
        while (low <= high) { // Changed condition to low <= high to correctly handle single-element arrays or when key is at the boundaries
            int mid = low + (high - low) / 2; // Prevents potential overflow for large low and high

            if (arr[mid] == key) {
                return mid;
            } else if (arr[mid] < key) {
                low = mid + 1; // Move low past mid
            } else { // arr[mid] > key
                high = mid - 1; // Move high before mid
            }
        }
        return -1; // Key not found
    }
}

/*store here your opinion about the quality of the generated spec
Different from the specifications given by chatGPT, Gemini's JML specifications always seem more intricate and detailed, ensuring every scenario is
covered, however this also creates more confusion while interpreting it.

 */

    /*store here your opinion about the quality of the generated code
    The logic of the implementation is correct, the code is simple and concise. Individually, gemini's approach works effectively.
     */
/*store here the result of the analysis with TACO


 */

    /*if TACO's outcome was "SAT: A failure has been detected", store here
    the source code of the method in folder "generated" that describes the
    counterexample. Otherwise report "UNSAT".

     */

