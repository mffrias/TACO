package edu.utep.ds2;

public class AverageMain {

    int stateToTestPure;



    //@ requires true;
    //@ ensures \result >= 0;
    public int calculateAverage()
    {
        int[][] bidim = new int[3][4];
        int a = bidim[2][2];
        return 1;
    }

    //@ requires n != Integer.MIN_VALUE;
    //@ ensures \result == n || \result == -n;
    //@ ensures \result >= 0;
    public /*@pure@*/ int abs(int n){
        if (n >= 0){
            return n;
        } else
            return -n;
    }

    private void decreaseAttr() {
        this.stateToTestPure--;
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