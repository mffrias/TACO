package ximena;

/* Control for InvViaCall: the same invariant written on the field. */
public class InvViaCallField {
    public int x;

    //@ public invariant x > 0;

    public void setNeg() { x = -1; }

    /* breaks the invariant only through setNeg(); correct => SAT (T5 Part 3 (b) measured UNSAT on stock) */
    //@ ensures true;
    public void breakViaCall() { setNeg(); }
}
