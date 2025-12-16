package genai.chatGPT;
//Prompt
//    //Generate a JML specification for the Factorial class and its methods, specifically the factorial(int n) method, and the spec factorial(int n) method. The factorial(int n)
//    method ensures that the result is within the valid range for a long and correctly computes the factorial, while the model function spec factorial(int n) provides
//    a formal specification for the factorial computation.
public class Factorial {


    /*@
          @   ensures 0 <= n && n <= 20;
          @   ensures  \result == factorial(n);
          @   ensures  1 <= \result && \result <= Long.MAX_VALUE;
          @ also
          @ public exceptional_behavior
          @   requires n < 0 || n > 20;
          @   signals (IllegalArgumentException e) true;
          @*/
    public static long factorial(int n) {
        if (n < 0 || n > 20) {
            throw new IllegalArgumentException("n must be between 0 and 20 inclusive");
        }
        if (n == 0) {
            return 1;
        }
        return n * factorial(n - 1);
    }
}