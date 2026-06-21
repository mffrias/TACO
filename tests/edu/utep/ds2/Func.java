package edu.utep.ds2;
public class Func {
    //@ public normal_behavior
    //@   requires true; // No specific preconditions are imposed on the input numbers
    //@   ensures \result == (num1 + num2) / 2.0f; // The result is the arithmetic mean of the two numbers
    public float func(float num1, float num2) {
        return (num1 + num2) / 2.0f;
    }


}