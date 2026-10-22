package ximena;

/* Extra Defect-C controls: an observer with assignable \nothing (the exit sync now WRITES the
   placeholder; this must not create a frame violation), and a non-void method that breaks the
   invariant and returns (the sync is distributed above each return). */
public class InvCall2 {
    public int x;


    //@ public invariant isPos();

    /*@ pure @*/ public boolean isPos() { return x > 0; }

    /* observer, invariant preserved: correct => UNSAT */
    //@ assignable \nothing;
    //@ ensures \result == x;
    public int getX() {
        return x; }

    /* observer vacuity control: correct => SAT */
    //@ assignable \nothing;
    //@ ensures false;
    public int getXFalse() { return x; }

    /* non-void, breaks the invariant on one path and returns: correct => SAT */
    //@ ensures true;
    public int breakAndReturn(int k) { if (k > 0) { x = -k; return 1; } return 0; }

    /* non-void, preserves the invariant on every path: correct => UNSAT */
    //@ ensures true;
    public int keepAndReturn(int k) { if (k > 0) { x = k; return 1; } return 0; }

    /* explicit frame, breaks the invariant: correct => SAT */
    //@ assignable x;
    //@ ensures true;
    public void setNeg() { x = -1; }

    /* explicit frame, keeps the invariant: correct => UNSAT */
    //@ assignable x;
    //@ ensures x == 5;
    public void setFive() { x = 5; }
}
