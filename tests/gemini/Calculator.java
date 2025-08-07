package gemini;
//Prompt: Generate a JML specification and a method implementation that takes two
// numbers and an operator, then does the correct math operation based on the operator. It uses a switch to check if
// the operator is +, -, *, /, or %, and returns the result. If the operator isn’t valid, it returns -1.
public class Calculator {
    //Generated JML spec:

     /*@
      @ requires op == '+' || op == '-' || op == '*' || op == '/' || op == '%';
      @ ensures (op == '+') ==> \result == num1 + num2;
      @ ensures (op == '-') ==> \result == num1 - num2;
      @ ensures (op == '*') ==> \result == num1 * num2;
      @ ensures (op == '/') ==> \result == num1 / num2;
      @ ensures (op == '%') ==> \result == num1 % num2;
      @ ensures \result == -1;
     @*/

    //Generated code:
    public static double calculate(double num1, double num2, char op) {
        switch (op) {
            case '+':
                return num1 + num2;
            case '-':
                return num1 - num2;
            case '*':
                return num1 * num2;
            case '/':
                // Add a check for division by zero for robustness, though not in JML spec.
                if (num2 == 0) {
                    System.err.println("Error: Division by zero is not allowed.");
                    return Double.NaN; // Or handle as an error
                }
                return num1 / num2;
            case '%':
                if (num2 == 0) {
                    System.err.println("Error: Modulo by zero is not allowed.");
                    return Double.NaN; // Or handle as an error
                }
                return num1 % num2;
            default:
                // This case handles all invalid operators
                return -1;
        }
    }
}
/*store here your opinion about the quality of the generated spec
As expected, the specifications provided by gemini are detailed, it includes preconditions and postconditions. They are less concise, ensuring that the method runs effectively.
Additionally, the JML parser seems to have issues interpreting these specifications.
 */

    /*store here your opinion about the quality of the generated code
The code approach for this problem was individually tested, and it works effectively, following the right logic.
/*store here the result of the analysis with TACO
14:25:07,649 ERROR JmlParser:59 - org.multijava.util.compiler.CompilationAbortedException

 */

    /*if TACO's outcome was "SAT: A failure has been detected", store here
    the source code of the method in folder "generated" that describes the
    counterexample. Otherwise report "UNSAT".

     */
