package edu.utep.ds2;

public class AverageMain {

    public int stateToTestPure;
    public /*@ nullable @*/ AverageMain next;


    /*@ invariant stateToTestPure + 1 >= 4; @*/


    //@ requires this.stateToTestPure + 2  > 0;
    //@ ensures this.stateToTestPure == \old(stateToTestPure) + 1;
    //@ ensures a == \old(a) - 1;
    //@ ensures this.next != null;
    //@ signals (Throwable t) false;
    public int calculateAverage(int a, int b)
    {
        a = a + 1;
        stateToTestPure++;
        stateToTestPure++;
        stateToTestPure++;

        return (stateToTestPure + b)/2;
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
        return;
    }

}