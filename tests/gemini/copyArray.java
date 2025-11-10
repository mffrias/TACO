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

/*store here your opinion about the quality of the generated spec
Although specifications are longer than usual. They follow a good logic that complies with the parser.  */

    /*store here your opinion about the quality of the generated code
The code is short and concise, it works  with the JML specifications/*store here the result of the analysis with TACO

00000000  Starting Alloy Analyzer via command line interface

    * Input spec file         : output/output.als

00000001  Parsing and typechecking

Warning #1
    * Command type            : check
== is redundant, because the left and right expressions always have the same value.
    * Command label           : check_gemini_copyArray_CopyArray

Left type = {this/true}
00003847  Translating Alloy to Kodkod
Right type = {this/true}

Warning #2
== is redundant, because the left and right expressions always have the same value.
Left type = {this/true}
Right type = {this/true}
    * Solver                  : sat4j
    * Bit width               : 2
    * Max sequence            : 0
    * Skolem depth            : 0
    * Symmbreaking            : 20

00004501  Translating Kodkod to CNF

    * Primary vars            : 1250
    * Total vars              : 69208
    * Clauses                 : 167909

00006768  Solving

    * Outcome                 : UNSAT: No failures were detected within the given scopes.
    * Solving time            : 139666

00143864  Analysis finished


junit.framework.AssertionFailedError: The method should have counterexample.
Expected :true
Actual   :false
 */

    /*if TACO's outcome was "SAT: A failure has been detected", store here
    the source code of the method in folder "generated" that describes the
    counterexample. Otherwise report "UNSAT".
UNSAT
     */



