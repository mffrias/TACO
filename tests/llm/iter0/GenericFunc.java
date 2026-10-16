package llm.iter0;
public class GenericFunc {

    public /*@nullable@*/ GenericFunc next;
    public /*@nullable@*/ GenericFunc prev;


    //@ requires true;
    //@ ensures \result == (num1 + num2) / 2;
    //@ signals (Throwable t) false;
    public static int func(int num1, int num2) {
//        this.next = new GenericFunc();
//        this.prev = new GenericFunc();
        return (num1 + num2) / 3;
    }

//    public static void main(String[] args) {
//        GenericFunc calculator = new GenericFunc();
//        float number1 = 10.5f;
//        float number2 = 20.0f;
//        float average = calculator.func(number1, number2);
//        System.out.println(average);
//    }
}