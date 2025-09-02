package gemini;
//Prompt:
/*
Generate a JML specification and code implementation for the method public static long that takes in two integers, n and r, in order to compute the number of ways
to choose r elements from a set of n elements without considering order.
 */
public class Combinations {
    //Generated JML spec:

    /*@
      @ requires n >= 0 && r >= 0 && n >= r;
      @ ensures \result == (\product long i; 0 <= i && i < r; (n - i)) / (\product long i; 1 <= i && i <= r; i);
      @ assignable \nothing;
      @*/
    //Generated Code:
    public static long combinations(int n, int r) {
        // Handle invalid input according to the JML specification's requires clause
        if (r < 0 || n < r) {
            throw new IllegalArgumentException("Invalid input: n and r must be non-negative, and n must be greater than or equal to r.");
        }

        // Base cases
        if (r == 0 || r == n) {
            return 1;
        }

        // Optimization: C(n, r) = C(n, n-r)
        // This ensures r is the smaller number, reducing the number of loop iterations.
        if (r > n / 2) {
            r = n - r;
        }

        long result = 1;

        // Calculate (n * (n-1) * ... * (n-r+1)) / (r * (r-1) * ... * 1)
        // The multiplication and division are interleaved to keep the intermediate
        // results as small as possible to prevent overflow.
        for (int i = 1; i <= r; i++) {
            result = result * (n - i + 1) / i;
        }

        return result;
    }
}
/*store here your opinion about the quality of the generated spec
Different to the chatGPT-Generated specifications, Gemini generated more intricate
code, it is more specific, making it difficult for the parser to interpret the specifications.

 */

    /*store here your opinion about the quality of the generated code
The code, contrasting with chatGPT, handles different cases, from error handling to the base case handling.
/*store here the result of the analysis with TACO
     ar.edu.taco.TacoException: Cannot subtract elements from types JavaPrimitiveIntegerValue and JavaPrimitiveLongValue

 */

    /*if TACO's outcome was "SAT: A failure has been detected", store here
    the source code of the method in folder "generated" that describes the
    counterexample. Otherwise report "UNSAT".

     */