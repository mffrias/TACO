package genai.chatGPT;
//Prompts:
// Constructor 1:  Generate a JML specification and the constructor Fibonacci(), which initializes the fib array with the first two Fibonacci numbers, where fib[0] == 0 and fib[1] == 1. The constructor ensures that the fib array has
// at least two elements and that these values are set correctly.
//constructor 2:  Generate a JML specification and  the constructor Fibonacci(int size), which initializes the fib array to a given size,
// where 2 <= size <= 93. The constructor ensures that the first two elements of the array are fib[0] == 0 and fib[1] == 1, and initializes all other elements of the array with default values.
//Method 1: Generate a JML specification for the method getFib(int index), which retrieves the Fibonacci number at the specified index from the fib array. The method ensures that the index is within the valid bounds of the array, and the result returned is fib[index].
//Method 2: Generate a JML specification for the method fibCompute(), which computes the Fibonacci sequence for the entire fib array
// starting from index 2. The method ensures that each Fibonacci number is the sum of the two preceding numbers. It also ensures that the
// Fibonacci numbers are strictly increasing. The method modifies the array from index 2 onwards and ensures that no overflow occurs during the computation.
public class Fibbonaci {
    public int[] fib;

    /*@ public invariant fib != null ==> fib.length >= 2 && fib.length <= 93;
      @ public invariant fib != null ==> fib[0] == 0 && fib[1] == 1;
      @*/

    /*@ ensures fib != null;
      @ ensures fib.length >= 2;
      @ ensures fib[0] == 0;
      @ ensures fib[1] == 1;
      @*/
    public Fibbonaci() {
        fib = new int[2];
        fib[0] = 0;
        fib[1] = 1;
    }

    /*@ requires 2 <= size && size <= 93;
      @ ensures fib != null;
      @ ensures fib.length == size;
      @ ensures fib[0] == 0;
      @ ensures fib[1] == 1;
      @ ensures (\forall int i; 2 <= i && i < fib.length; fib[i] == 0);
      @*/
    public Fibbonaci(int size) {
        fib = new int[size];
        fib[0] = 0;
        fib[1] = 1;
    }

    /*@ requires fib != null;
      @ requires 0 <= index && index < fib.length;
      @ ensures \result == fib[index];
      @*/
    public /*@ pure @*/ int getFib(int index) {
        return fib[index];
    }

    /*@ requires fib != null;
      @ requires fib.length >= 2;
      @ assignable fib;
      @ ensures (\forall int i; 2 <= i && i < fib.length; fib[i] == fib[i-1] + fib[i-2]);
      @ ensures (\forall int i; 1 <= i && i < fib.length; fib[i] > fib[i-1]);
      @ ensures (\forall int i; 0 <= i && i < fib.length; fib[i] >= 0);
      @*/
    public void fibCompute() {
        for (int i = 2; i < fib.length; i++) {
            fib[i] = fib[i - 1] + fib[i - 2];
        }
    }
}

/*store here your opinion about the quality of the generated spec

 */

    /*store here your opinion about the quality of the generated code

/*store here the result of the analysis with TACO

 */

    /*if TACO's outcome was "SAT: A failure has been detected", store here
    the source code of the method in folder "generated" that describes the
    counterexample. Otherwise report "UNSAT".
UNSAT
     */