package ar.edu.taco.arithmetic;

public class DerefCheck {

    public DerefCheck atr1;

    public DerefCheck atr2;

    public int s;

    public DerefCheck(){
    }

    public int accessDerefCheck(){
        int i = this.atr1.atr2.s;
        return i;
    }


}
