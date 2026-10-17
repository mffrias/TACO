package ximena;

public class MutRecursive {


    public int field1;
    public Object field2;

    //@ requires true;
    //@ ensures \result == true <==> n%2 == 0;
    //@signals (Exception e) false;
    public static boolean isEven(int n, MutRecursive m){
        if (n == 0)
            return true;
        else
            return isOdd(n-1);
    }

    //@ requires true;
    //@ensures true;
    //@signals (Exception e) false;
    public static boolean isOdd(int n){
        if (n == 0)
            return false;
        else
            return false;//isEven(n-1);
    }


    //@ requires true;
    //@ ensures field2 != null;
    public MutRecursive(){
        field2 = new MutRecursive(0);
    }

    public MutRecursive(int i){
    }


//    public static void main(String[] args){
//        MutRecursive m = new MutRecursive();
//
//    }

}
