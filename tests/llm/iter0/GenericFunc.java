package llm.iter0;
public class GenericFunc {

    public float att;
    public /*@ nullable @*/ GenericFunc next;

    public GenericFunc(){}

    public /*@pure@*/ boolean isTrue(float f){
        return true;
    }

    public /*@pure@*/ boolean isFalse(float f){
        return false;
    }

    //@   requires (\forall GenericFunc x; x.next == null; !Float.isNaN(x.att)) && \reach(this.next, GenericFunc, next).int_size() == 0;
    //@   ensures this.isFalse(num1) == true;
    //@   signals (Exception e) false;
    public float func(float num1, float num2) {

        return 0.0f;
        // (num1 + num2) / 3.0f;

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