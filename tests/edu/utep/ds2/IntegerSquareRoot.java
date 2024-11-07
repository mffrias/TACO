package edu.utep.ds2;

public class IntegerSquareRoot {

    // Method to find the integer square root using binary search
// ensures (\result + 1) * (\result + 1) > number;
    // ensures \result * \result <= number;
    // ensures \result * \result <= number;

    //@ requires number >= 0;
    //@ ensures (\result + 1) * (\result + 1) > number;
    //@ signals (Exception e) false;
    public static int integerSquareRoot(int number) {

        // Special cases for 0 and 1
        if (number == 0 || number == 1) {
            return number;
        }

        int low = 1;
        int high = number;
        int result = 0;

        // Perform binary search
        while (low <= high) {
            int mid = (low + high) / 2;

            // Check if mid*mid is equal to the number
            if (mid == number / mid) {
                return mid; // Exact square root found
            }
            // If mid*mid is less than the number, narrow to upper half
            else if (mid < number / mid) {
                low = mid + 1;
                result = mid; // mid is a possible candidate for the integer square root
            }
            // If mid*mid is greater than the number, narrow to lower half
            else {
                high = mid - 1;
            }
        }

        return result; // Return the largest mid such that mid*mid <= number
    }


    public static void main(String[] args){
        int res = integerSquareRoot(3);
        System.out.println(res);
    }
}