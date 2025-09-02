package gemini;

public class copyArray {

    /*@
      @ public normal_behavior
      @
      @ // Preconditions:
      @ // Both arrays must not be null.
      @ requires a != null && b != null;
      @
      @ // The start index must be non-negative and not after the end index.
      @ requires 0 <= iBegin && iBegin <= iEnd;
      @
      @ // The range [iBegin..iEnd] must be valid for both arrays.
      @ requires iEnd < a.length && iEnd < b.length;
      @
      @ // Frame Condition:
      @ // The method is only allowed to modify the elements of array 'a'
      @ // within the specified range.
      @ assignable a[iBegin..iEnd];
      @
      @ // Postconditions:
      @ // Ensures that for every index in the range, the element in 'a'
      @ // is now equal to the corresponding element in 'b'.
      @ ensures (\forall int j; iBegin <= j && j <= iEnd; a[j] == b[j]);
      @
      @ // Ensures that all other elements of array 'a' outside the copied
      @ // range remain unchanged.
      @ ensures (\forall int j; 0 <= j && j < a.length && (j < iBegin || j > iEnd); a[j] == \old(a[j]));
      @
      @*/
    public static void CopyArray(int[] b, int[] a, int iBegin, int iEnd) {
        for (int k = iBegin; k <= iEnd; k++) {
            a[k] = b[k];
        }
    }
}


