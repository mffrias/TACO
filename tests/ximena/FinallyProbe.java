package ximena;

/* Probe: does a finally block run when an exception is in flight?
   In Java the finally restores x = 5, so the invariant holds on the exceptional exit and
   the correct answer is UNSAT. SAT means the finally block was skipped. */
public class FinallyProbe {
    public int x;

    //@ public invariant x > 0;

    //@ ensures true;
    //@ signals (java.lang.Throwable e) true;
    public void m(int k) {
        try {
            x = -1;
            if (k > 0) { throw new RuntimeException(); }
        } finally {
            x = 5;
        }
    }

    /* mNormal: forced path k <= 0 */
    //@ requires k <= 0;
    //@ ensures true;
    //@ signals (java.lang.Throwable e) true;
    public void mNormal(int k) {
        try {
            x = -1;
            if (k > 0) { throw new RuntimeException(); }
        } finally {
            x = 5;
        }
    }

    /* mThrows: forced path k > 0 */
    //@ requires k > 0;
    //@ ensures true;
    //@ signals (java.lang.Throwable e) true;
    public void mThrows(int k) {
        try {
            x = -1;
            if (k > 0) { throw new RuntimeException(); }
        } finally {
            x = 5;
        }
    }
}