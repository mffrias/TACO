package forArielGodio.inverse.bug01;
public class Inverse {
	//@ requires x.length <= 4 && y.length <= 4; //add this to have short arrays
    //@ ensures \result == false ==> ((x.length != y.length) || (\exists int i; 0 <= i && i < x.length; x[i] != y[x.length - 1 -i]));
    //@ ensures \result == true ==> x.length == y.length && (\forall int i; 0 <= i && i < x.length; x[i] == y[x.length - 1 - i]);
    public static boolean inverse(int[] x, int[] y) {
        if (x.length == y.length) return false; // if (x.length != y.length) return false;
        int index = 0;
        //@ decreases x.length - index;
        while (index < x.length) {
            if (x[index] != y[x.length - 1 - index]) {
                return false;
            } else {
                index = index + 1;
            }
        }
        return true;
    }
}