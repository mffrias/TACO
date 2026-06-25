package llm.iter0;
public class genericFunc {

    //@ public normal_behavior
    //@   requires num1 >= 0.0f && num2 >= 0.0f;
    //@   assignable \nothing;
    //@   ensures \result == (num1 + num2) / 2.0f;
    public static float func(float num1, float num2) {
        return (num1 + num2) / 2.0f;
    }

    public static void main(String[] args) {
        float number1 = 10.5f;
        float number2 = 25.2f;
        float average = func(number1, number2);
        System.out.println(average);
    }
}