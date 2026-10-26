package ximena;

/* Prompt-edit probe (T5 §2.3 last bullet): an exception thrown INSIDE a loop, with the loop still able to
   continue. The unrolling assert after the loop must not replace it with an AssertionError. */
public class SigOnly {
    public int n;

    /* correct; exceptional case pins IOOBE only; correct translation => UNSAT */
    /*@ public normal_behavior
      @   requires i >= 0;
      @   ensures \result == 3;
      @ also public exceptional_behavior
      @   requires i < 0;
      @   signals_only java.lang.IndexOutOfBoundsException;
      @*/
    public int walk(int i) {
        int k = 0;
        while (k < 3) {
            if (i < 0) throw new IndexOutOfBoundsException();
            k++;
        }
        return k;
    }

    /* same contract, also admits AssertionError (the old workaround); => UNSAT */
    /*@ public normal_behavior
      @   requires i >= 0;
      @   ensures \result == 3;
      @ also public exceptional_behavior
      @   requires i < 0;
      @   signals_only java.lang.IndexOutOfBoundsException, java.lang.AssertionError;
      @*/
    public int walkAE(int i) {
        int k = 0;
        while (k < 3) {
            if (i < 0) throw new IndexOutOfBoundsException();
            k++;
        }
        return k;
    }

    /* control: throws the WRONG type inside the loop; => SAT */
    /*@ public normal_behavior
      @   requires i >= 0;
      @   ensures \result == 3;
      @ also public exceptional_behavior
      @   requires i < 0;
      @   signals_only java.lang.IndexOutOfBoundsException;
      @*/
    public int walkWrong(int i) {
        int k = 0;
        while (k < 3) {
            if (i < 0) throw new RuntimeException();
            k++;
        }
        return k;
    }
}