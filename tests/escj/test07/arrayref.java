package escj.test07;

class arrayref {
  //@requires true;
  //@ensures true;   
  public static int m1(int[] a) {
    //@ assume a != null;
    //@ assume a.length == 10;
    return a[1];
  }

//@requires true;
//@ensures true;     
  public static int m2(int[] a) {
    //@ assume a != null;
    //@ assume a.length == 10;
    int x = 1;
    int y = x + 1;
    return a[x] + a[x];
  }
}
