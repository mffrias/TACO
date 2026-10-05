package llm.iter0;
public class GenericFunc {

    public /*@nullable@*/ GenericFunc next;
    public /*@nullable@*/ GenericFunc prev;


    //@ public normal_behavior
    //@ requires !Float.isNaN(num1) && !Float.isNaN(num2);
    //@ requires num1 != Float.POSITIVE_INFINITY && num2 != Float.POSITIVE_INFINITY;
    //@ assignable \nothing;
    //@ ensures \result == (num1 + num2) / 2.0f;
    //@ ensures this.next != \old(this.next);
    //@ also public exceptional_behavior
    //@ requires false;
    //@ signals (Throwable t) false;
    public float func(float num1, float num2) {
        this.next = new GenericFunc();
        this.prev = new GenericFunc();
        return (num1 + num2) / 3.0f;
    }

    public static void main(String[] args) {
        GenericFunc calculator = new GenericFunc();
        float number1 = 10.5f;
        float number2 = 20.0f;
        float average = calculator.func(number1, number2);
        System.out.println(average);
    }
}