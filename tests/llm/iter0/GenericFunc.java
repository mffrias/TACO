package llm.iter0;
public class GenericFunc {

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

    public static void main(String[] args) {
        GenericFunc calculator = new GenericFunc();
        float number1 = 10.5f;
        float number2 = 20.0f;
        float average = calculator.func(number1, number2);
        System.out.println(average);
    }
}