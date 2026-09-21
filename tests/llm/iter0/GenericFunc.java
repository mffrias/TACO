package llm.iter0;
public class GenericFunc {

    public Object[] ff;


//    //@ requires 0 <= i && i < ff.length;
//    //@ ensures \result == ff[i];
//    public Object check(int i){
//        return ff[i];
//    }

    //@ requires true;
    //@ ensures \result != null;

    public static Object check(int i){
        return null;
    }


    //@ public normal_behavior
    //@ requires true;
    //@ assignable \nothing;
    //@ ensures \result == (num1 + num2) / 2.0f;
    //@ also public exceptional_behavior
    //@ requires false;
    //@ signals (Throwable t) false;
    public float func(float num1, float num2) {
        return (num1 + num2) / 2.0f;
    }

//    public static void main(String[] args) {
//        float number1 = 10.5f;
//        float number2 = 20.2f;
//        float average = func(number1, number2);
//        System.out.println(average);
//    }
}