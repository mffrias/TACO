package escj.test54_p0;

class Constructor {
  /*@ non_null */ String s;

  int x;
  //@ invariant 0 < x && x <= 10;

  Constructor() {
    initFields("hello", 8);  // okay
  }

  //@ helper
  private void initFields(String sp, int xp) {
    s = sp;
    x = xp;
  }

  void initFieldsNonHelper0(String sp, int xp) {
    s = sp;  // violates non_null
    x = xp;
  }  // violates invariant

  void initFieldsNonHelper1(String sp, int xp) {
    initFields(sp, xp);  // violates non_null inside this call
  }  // violates invariant

  Constructor(String sp, int delta) {
    initFields(sp, 8);  // violates non_null inside this call
    x += delta;
  }  // violates x's invariant
  //@ requires delta >= 0;
  Constructor(int delta) {
    x = delta;
    initFields((String)null + (String)null, x + 1);
    x = x % 10;
    x++;
  }  // okay
}
