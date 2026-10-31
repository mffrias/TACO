package ximena;

/* DEFECT B, reproducer.  A `return` inside the loop.  The method always terminates
   normally, so `signals (java.lang.Throwable e) false;` MUST be UNSAT.  Measured: SAT.
   After a return, DynAlloy's `exit_stmt_reached` guard skips the re-evaluation of the
   loop guard, so the temporaries the appended `assert <guard now false>` reads are
   left unassigned; the solver is then free to choose values that make that assert
   fail, which sets throw := AssertionErrorLit.
   NoReturnInLoop is the control -- same algorithm, no return inside the loop. */
public class ReturnInLoop {

    /*@ requires 0 <= n && n <= 3;
      @ assignable \nothing;
      @ ensures true;
      @ signals (java.lang.Throwable e) false;
      @*/
    public int firstPositive(int n) {
        int k = n;
        while (k > 0) {
            if (k == 1) {
                return k;
            }
            k = k - 1;
        }
        return 0;
    }
}