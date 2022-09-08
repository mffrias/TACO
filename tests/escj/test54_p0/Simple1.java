package escj.test54_p0;

class Simple1 {
  Simple1() {
  }

  int x;
  
  void m() {
    x = 5;
    int y = n();
    //@ assert y == 5;
  }

  //@ helper
  private int n() {
    //@ assert 0 <= x;
    return x;
  }
}
