package ximena;

public class Chain {

    /*@ nullable @*/ public Chain left;

    /*@ pure @*/
    public boolean hasLeft() {
        return this.left != null;
    }

    /*@ requires p != null;
      @ requires p.left == null || p.left.left == null;
      @ assignable \nothing;
      @ ensures \result == p;
      @*/
    public Chain walkViaCall(Chain p) {
        while (p.hasLeft()) {
            p = p.left;
        }
        return p;
    }

    /*@ requires p != null;
      @ requires p.left == null || p.left.left == null;
      @ assignable \nothing;
      @ ensures \result == p;
      @*/
    public Chain walkViaField(Chain p) {
        while (p.left != null) {
            p = p.left;
        }
        return p;
    }


    public int inc(int i){
        i++;
        return i;
    }
    //@ requires true;
    //@ ensures \result == i+2;
    public int testingNameNormalizer( int i){
        this.inc(i);
        this.inc(i);
        return i;
    }
}