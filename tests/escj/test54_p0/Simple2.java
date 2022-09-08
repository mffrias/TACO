package escj.test54_p0;

class Simple2 {
  Simple2() {
  }

  int x;

  void m() {
    int y = n();
    //@ assert y == 5;
  }

  //@ requires -10 <= x;
  //@ helper
  private int n() {
    //@ assert 0 <= x;
    return x;
  }

  //@ ensures x == 5;
  private void p() {
    q0();
    q1();
  }

  //@ helper
  private void q0() {
  }

  //@ helper
  private void q1() {
    return;
  }
}
