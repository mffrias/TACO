package ximena;

public class LoopAssert {

    //@ requires j > 7;
    //@ ensures \result == 0;
    //@signals (AssertionError a) false;
    public int loop(int j){
        while (j < 10){
            j++;
        }
        return 0;
    }

}
