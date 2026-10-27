package ximena;

/* Defect-C edge: a void method that ENDS in an unconditional throw. On stock, the exit sync is
   appended after the throw and javac rejects the simplified code (unreachable statement). */
public class InvCall4 {
    public int x;

    //@ public invariant isPos();

    /*@ pure @*/ public boolean isPos() { return x > 0; }

    /* breaks the invariant then always throws: correct => SAT */
    //@ ensures true;
    //@ signals (java.lang.Throwable e) true;
    public void breakThenThrow() { x = -1; throw new RuntimeException(); }

    /* keeps the invariant then always throws: correct => UNSAT */
    //@ ensures true;
    //@ signals (java.lang.Throwable e) true;
    public void keepThenThrow() { x = 3; throw new RuntimeException(); }
}