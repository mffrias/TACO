package ximena;

/* Defect-C edge: the invariant must also hold on an exceptional exit (JML semantics).
   Field-form control in InvDirect3. */
public class InvCall3 {
    public int x;

    //@ public invariant isPos();

    /*@ pure @*/ public boolean isPos() { return x > 0; }

    /* breaks the invariant, then throws: correct => SAT */
    //@ ensures true;
    //@ signals (java.lang.Throwable e) true;
    public void breakThenThrow(int k) {
        if (k > 0) {
            try {
                x = -1;
                throw new RuntimeException();
            } catch (RuntimeException e){
                x = -1;
            }
        }
    }


    public static void main(String[] args){
        InvCall3 c = new InvCall3();
        c.breakThenThrow(3);
    }
}
