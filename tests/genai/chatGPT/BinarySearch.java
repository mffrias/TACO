package genai.chatGPT;
/*prompt for generative AI:
Generate a JML specification for method public static int Binary(int[] arr, int key) that takes in an int array and int key
 and starts with an int low and int high, then, while the low value is less than the high, we find the mid value and
 check if the key is at the mid-point in the array. If the mid-point is less than the key, the new low is set
 to the value of mid, high is untouched, and a new mid is computed. Otherwise, if the mid-point is greater than the key,
 the low value is untouched, and the high value is changed to the value of the mid and a new mid is computed.
 This continues until either the key is not found or the array at mid equals the key. The expected output is the index of
 where the key is in the array.
*/


public class BinarySearch {

    /*@
      requires arr != null && arr.length > 0;
      requires (\forall int i; 0 <= i && i < arr.length - 1; arr[i] <= arr[i+1]);
      ensures
        (\result == -1 ==> (\forall int i; 0 <= i && i < arr.length; arr[i] != key)) &&
        (\result != -1 ==> (0 <= \result && \result < arr.length && arr[\result] == key));
      assignable \nothing;
    @*/
    public static int Binary(int[] arr, int key) {
        int low = 0;
        int high = arr.length;

        while (low < high) {
            int mid = (low + high) / 2;

            if (arr[mid] == key) {
                return mid;
            } else if (arr[mid] < key) {
                low = mid + 1;
            } else {
                high = mid;
            }
        }

        return -1;
    }
}
/*store here your opinion about the quality of the generated spec
The specification is concise but more detailed ensuring the code follows certain parameters to avoid issues,
however it appears that it is harder to read and interpret.

 */

    /*store here your opinion about the quality of the generated code
The generated code appears to be correct, changing variables as prompted and following the correct binary search logic.     */

/*store here the result of the analysis with TACO
** Outcome                 : UNSAT: No failures were detected within the given scopes.
    * Solving time            : 7423

00005825  Analysis finished
 */

    /*if TACO's outcome was "SAT: A failure has been detected", store here
    the source code of the method in folder "generated" that describes the
    counterexample. Otherwise report "UNSAT".
    UNSAT
     */