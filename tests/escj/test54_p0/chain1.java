package escj.test54_p0;

class chain1 {
  void a() {
    int x;
    x = b(18);
    //@ assert x == 20;
    x = b(19);
    //@ assert x == 20;  // error
  }

  //@ helper
  private int b(int t) {
    return c(t+1);
  }

  //@ helper
  private int c(int t) {
    return d(t+1);
  }

  //@ helper
  private int d(int t) {
    return t;
  }
}
