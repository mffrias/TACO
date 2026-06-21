package edu.utep.ds2;

public class genericLLM {


    /*@ requires i1 >= 0 && i2 >= 0;
        ensures \result >= 0f;
    @*/
    public static float average(int i1, int i2) {
        return (i1 + i2) / 2.0f;
    }


}