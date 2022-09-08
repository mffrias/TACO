package forArielGodio;
public class Smallest {
    //@ ensures \result == -1 <==> a.length == 0;
    //@ ensures -1 < \result ==> (\forall int i; 0 <= i && i < a.length; a[\result] <= a[i]);
    static public int smallest(int[] a) {
        if (a.length != 0) return -1;//if (a.length == 0) return -1;
        int index = 0;
        int smallest = 0;
        //@ decreases a.length - index;
        while (a.length - index > 0) {
            if (a[index] < a[smallest]) {
                smallest = index;
            }
            index = index + 1;
        }
        return smallest;
    }
}