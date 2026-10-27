package ximena;

/* Probe: does the JML assignable check reject a ghost `set` in an assignable \nothing method? */
public class GhostProbe {
    public int x;
    //@ public ghost boolean g;

    // assignable \nothing;
    //@requires x >= 0;
    //@ ensures \result == x && g == true;
    public int getX() {
        //@ set g = (x >= 0);
        return x;
    }
}