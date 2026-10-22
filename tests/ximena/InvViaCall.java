package ximena;

/* Prompt-edit probe (T5 Part 3): the invariant is broken only through a call to another method (case (b),
   "invariant not re-checked on exit"), with the invariant written as a pure CALL. */
public class InvViaCall {
    public int x;

    //@ public invariant isPos();

    /*@ pure @*/ public boolean isPos() { return x > 0; }

    public void setNeg() { x = -1; }

    /* breaks the invariant only through setNeg(); correct translation => SAT */
    //@ ensures true;
    public void breakViaCall() { setNeg(); }

    /* breaks it directly (field write in its own body); => SAT */
    //@ ensures true;
    public void breakDirect() { x = -1; }
}
