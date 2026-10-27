package ximena;

/* Frame control: an assignable \nothing method that writes a field must still be rejected by
   the JML check on BOTH builds (the fix widens the frame only by TACO's own placeholder). */
public class FrameViol {
    public int x;

    //@ public invariant isPos();

    /*@ pure @*/ public boolean isPos() { return x > 0; }

    //@ assignable \nothing;
    //@ ensures true;
    public void sneaky() { x = 7; }
}