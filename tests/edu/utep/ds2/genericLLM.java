package edu.utep.ds2;

public class genericLLM {
    //@requires digits != null;
    //@ensures \result == (sumVal(digits) == target);
    public static boolean isValidSum(int[] digits, int target) {
        int total = 0;

        for (int i = 0; i < digits.length; i+= 1) {
            total += digits[i];
        }

        return total == target;
    }

    public /*@pure@*/ static int sumVal(int[] values){
        int total = 0 ;
        for (int i = 0; i < values.length-1; i+=1){
            total += values[i];
        }
        return total;
    }
}