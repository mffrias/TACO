package llm.iter0;
public class GenericFunc {

    int field;
    public GenericFunc(){}


    //@   requires num1 == 1;
    //@   ensures \result == (num1 + num2) / 3;
    //@   signals (Throwable e) false;
    public int func(int num1, int num2, GenericFunc g) {
        return (num1 + num2) / 2;
    }

    //@ requires num1 > 0;
    //@ ensures \result == 5;
    public int gimme5(int num1, int b){
        GenericFunc aaa = new GenericFunc();
        return 5*(num1 + b);
    }

//    public static genericFunc func2(float n){
//        return new genericFunc();
//    }

//    public static void main(String[] args) {
//        float number1 = 10.5f;
//        float number2 = 25.2f;
//        float average = func(number1, number2);
//        System.out.println(average);
//    }
}