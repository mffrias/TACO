package edu.utep.ds2;

public class AverageMain {
    //@ requires num1 >= 0 && num2 >= 0;
    //@ ensures \result >= 0;
    public static int calculateAverage(int num1, int num2)
    {
        return (num1 + num2) / 2;
    }

//    // @requires args != null;
//    public static void main(String[] args)
//    {
//        double number1 = 10.5
//        double number2 = 20.2;
//        double average = calculateAverage(number1, number2);
//        System.out.println(average);
//    }
}