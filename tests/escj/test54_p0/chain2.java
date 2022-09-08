package escj.test54_p0;

class chain2 {
  void m() {
    p();
  }

  //@ helper
  private void p() {
    p();
  }
}
