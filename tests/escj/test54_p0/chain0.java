package escj.test54_p0;

class chain0 {
  void a() {
    int x = b(18);
    //@ assert x == 18;  // warning, due to finite inlining
  }

  //@ helper
  private int b(int t) {
    return c(t);
  }

  //@ helper
  private int c(int t) {
    return c(t);
  }
}
