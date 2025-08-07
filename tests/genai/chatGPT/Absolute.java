package genai.chatGPT;

public class Absolute {

    /*
Prompt:
write  a java program and JML instructions that takes in an integer
and returns the absolute value.
     */
/*
Store here the java code generated.
*/

    /**
     * @ public normal_behavior
     * @ requires true;
     * @ ensures \result == (x < 0 ? -x : x);
     * @
     */
    public int absolute(int x) {
        if (x >= 0) {
            return x;
        } else {
            return -x;
        }
    }
/*


    /*store here your opinion about the quality of the generated spec


     */

    /*store here your opinion about the quality of the generated code
    The generated code follows a good logic that seems to work with different edge cases.
     */

    /*store here the result of the analysis with TACO
The analysis output: -2147483648
     */

    /*if TACO's outcome was "SAT: A failure has been detected", store here
    the source code of the method in folder "generated" that describes the
    counterexample. Otherwise report "UNSAT".
    public static void main(java.lang.String[] args) {
    java.lang.String[] param_args_1;

    param_args_1 = args;
    {
      genai.chatGPT.Absolute t_3;
      int t_4;

      t_3 = new genai.chatGPT.Absolute();
      genai.chatGPT.Absolute var_1_a = t_3;
      int var_2_num = -2147483648;

      t_4 = var_1_a.absolute(var_2_num);
      System.out.println(t_4);
    }
  }


     */


    public static void main(String[] args) {
//        Absolute a = new Absolute();
//        int num = -2147483648;
//        System.out.println(a.absolute(num));
//    }

    }
}
