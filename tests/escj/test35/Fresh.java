package escj.test35;

class Fresh {
  int[] a;
  Object f;
  
  //@ ensures \fresh(this)
  Fresh() {
  }

  //@ ensures \fresh(this)
  //@ ensures \fresh(f)
  /*@ ensures \fresh(a) */  // fails to establish this one
  Fresh(int[] p) {
    f = new Object();
    a = p;
  }  // fails to establish postcondition

  //@ requires 0 <= n;
  //@ ensures \fresh(this)
  //@ ensures \fresh(a)
  Fresh(int n) {
    a = new int[n];
  }

  //@ modifies a
  /*@ ensures \fresh(\old(a)) */  // impossible to establish
  void m0() {
    a = new int[10];
  }  // fails to establish impossible postcondition
  
  //@ modifies a
  //@ ensures \fresh(\old(a)) != \fresh(a)
  void m1() {
    a = new int[10];
  }

  //@ ensures \fresh(\result)
  Object m2() {
    return null;
  }  // fails to establish postcondition

  //@ ensures \fresh(\result)
  Object m3() {
    return a;
  }  // fails to establish postcondition

  //@ ensures \fresh(\result)
  Object m4() {
    return new int[12];
  }

  //@ ensures \fresh(\result)
  //@ ensures \fresh(\result.a)
  Fresh m5() {
    return new Fresh(5);
  }
}
