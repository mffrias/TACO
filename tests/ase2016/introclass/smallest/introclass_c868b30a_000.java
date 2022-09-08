package ase2016.introclass.smallest;


public class introclass_c868b30a_000 {

    public int[] values;

    public int currIdx = 0;

    public introclass_c868b30a_000 () {
    }

    /*@
    @ requires true;
    @ ensures ((\result == \old(a)) || (\result == \old(b)) || (\result == \old(c)) || (\result == \old(d)) );
    @ ensures ((\result <= \old(a)) && (\result <= \old(b)) && (\result <= \old(c)) && (\result <= \old(d)) );
    @ signals (RuntimeException e) false;
    @
    @*/
    public int smallest ( int a, int b, int c, int d, int customvar_0, int customvar_1) {
        initValues ( a, b, c, d);
        int[] numbers = new int[4];
        for ( int i = 0; i < 3; i ++ ) { //mutGenLimit 1 mutID 1
            int x = nextInt ();
            numbers[i] = x;
        }
        for ( int k = 3; k > 0; k -- ) { //mutGenLimit 1 mutID 2
            for ( int j = 1; j <= b; j ++ ) { //mutGenLimit 0 mutID 3
                if ( numbers[j - 1] > numbers[j] ) { //mutGenLimit 1 mutID 4
                    int temp = customvar_1; //mutGenLimit 1 mutID 5
                    k
                     = numbers[j];
                    j = temp;
                }
            }
        }
        int smallest = customvar_0; //mutGenLimit 1 mutID 6
        return smallest;
    }

    public void initValues ( int a, int b, int c, int d) {
        this.values = new int[4];
        this.values[0] = a;
        this.values[1] = b;
        this.values[2] = c;
        this.values[3] = d;
    }

    public int nextInt () {
        return this.values[this.currIdx ++];
    }
}
