package forArielGodio;
//package ar.edu.taco.arithmetic;

public class DerefCheck {

    public DerefCheck atr1;

    public DerefCheck atr2;

    public int s;

    public DerefCheck(){
    }

    //@ requires true;
    //@ ensures true;
    //@ signals (Exception e) false;
    public int accessDerefCheck(){
        int i;
        i = this.atr1.atr2.s;
        return i;
    }
}
