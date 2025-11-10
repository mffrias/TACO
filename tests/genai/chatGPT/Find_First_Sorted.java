package genai.chatGPT;
//Prompt: Generate a JML specification for a method that takes in a sorted integer array arr and an integer x, and returns
// the index of the first occurrence of x in the array. If x is not found, the method returns -1. The method uses binary search
// and ensures that the array is sorted in non-decreasing order.
public class Find_First_Sorted {
    /*@
  @ requires arr != null;                                         // Array must not be null
  @ requires arr.length >= 0;                                     // Valid array length
  @
  @ // The array must be sorted in non-decreasing order
  @ requires (\forall int i; 0 <= i && i < arr.length - 1;
  @              arr[i] <= arr[i + 1]);
  @
  @ ensures
  @   // Case 1: x is found  result is a valid index with arr[result] == x
  @   (\exists int i; 0 <= i && i < arr.length; arr[i] == x) ==>
  @      (0 <= \result && \result < arr.length &&
  @       arr[\result] == x &&
  @       (\forall int j; 0 <= j && j < \result; arr[j] != x));
  @
  @ ensures
  @   // Case 2: x not found  result == -1
  @   !(\exists int i; 0 <= i && i < arr.length; arr[i] == x) ==> \result == -1;
  @
  @ signals (Exception e) false;                                  // No exceptions should occur
  @*/
    public static int binarySearchFirst(int[] arr, int x) {
        int low = 0;
        int high = arr.length - 1;
        int result = -1;
        while (low <= high) {
            int mid = (low + high) / 2;
            if (arr[mid] == x) {
                result = mid;
                high = mid - 1;  // continue searching left for first occurrence
            } else if (arr[mid] < x) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }
        return result;
    }
}

/*store here your opinion about the quality of the generated spec
The generated JML specifications where thorough and clear. where no problems were found.
 */

    /*store here your opinion about the quality of the generated code
?The generated code is a good approach for the binary search and verified by the JML specifications
/*store here the result of the analysis with TACO

0
    /*if TACO's outcome was "SAT: A failure has been detected", store here
    the source code of the method in folder "generated" that describes the
    counterexample. Otherwise report "UNSAT".
        UNSAT
     */

