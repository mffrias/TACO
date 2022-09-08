package escj.test35;

class SmallFresh {
  int[] a;
  Object f;

  //@ ensures !\fresh(o);
  void p0(Object o) {
  }
  
  //@ ensures !\fresh(f);
  void p1() {
  }
  
  //@ modifies f;
  //@ ensures \fresh(\old(f)) != \fresh(f);
  void m0() {
    f = new Object();
  }
  
  //@ modifies a;
  //@ ensures \fresh(\old(a)) != \fresh(a);
  void m1() {
    a = new int[10];
  }
}
