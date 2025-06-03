package genai.chatGPT;

public class Absolute {

    /*
        Store here the prompt used

     */

    //@ requires true;
    //@ ensures \result >= 0 && (\result == num || \result == -num);
    public int absolute(int num) {
        if (num < 0) {
            return -num;
        } else {
            return num;
        }
    }


    /*store here your opinion about the quality of the generated spec

     */

    /*store here your opinion about the quality of the generated code

     */

    /*store here the result of the analysis with TACO

     */

    /*if TACO's outcome was "SAT: A failure has been detected", store here
    the source code of the method in folder "generated" that describes the
    counterexample. Otherwise report "UNSAT".

     */




    public static void main(String[] args){
        Absolute a = new Absolute();
        int num = -2147483648;
        System.out.println(a.absolute(num));
    }

}
