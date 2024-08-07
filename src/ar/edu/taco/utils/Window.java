package ar.edu.taco.utils;

public class Window {
    int valA;
    int valB;
    int valDiff;
    int numSAT;

    /*
    *
    * Window Wrapper to collect the information on problems solved in the
    * given window
    *
    * Receive window values per window period and add them to a list inside WindowList
    *
    * Wrapper object contains both window values and difference
    *
    */
    public Window(){}

    public Window(int diffA, int diffB, int SAT){
        this.valA = diffA;
        this.valB = diffB;
        this.numSAT = SAT;
        this.valDiff = Math.abs(valB - valA);
    }

    public int getValA(){
        return valA;
    }
    public int getValB(){
        return  valB;
    }
    public int getSAT() {return numSAT;}
    public int getValDiff(){
        return valDiff;
    }

}
