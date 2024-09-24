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
        calculateDifference();
    }

    public void calculateDifference(){
        int diff = (valB - valA);

        if(diff < 0) diff = 0;

        this.valDiff = diff;
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
