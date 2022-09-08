package escj.test44;

class LiteralTypes {
  void m() {
    Object[] a = new Object[3];
    a[0] = "foo";
    a[1] = null;
    a[2] = LiteralTypes.class;
  }
}
