package escj.test58_p5;

class Vanilla {
  int k;

  Vanilla() {
  }
}

class Sub extends Vanilla {
  //@ invariant 0 < k;
  
  Sub() {
  }  // error:  fails to establish invariant 0 < k

  //@ requires 0 < x;
  Sub(int x) {
    k = x;
  }
}

interface A {
  //@ invariant 10 < ((Vanilla)this).k;
}

interface B {
  //@ invariant ((Vanilla)this).k % 2 == 0;
}

class Sub2 extends Vanilla implements A {
  Sub2() {
  }  // error:  fails to establish invariant 10 < k
}

// The first constructor has "k" as a target, so it will pull in the
// invariant about "k".  The second constructor tests that the invariant
// is NOT pulled in on behalf of it being pulled in for the first constructor.
class Sub3 extends Vanilla implements A {
  Sub3() {
    k = 22;
  }
  Sub3(int x) {
  }  // error:  fails to establish invariant 10 < k
}

class Sub4 extends Sub implements A, B {
  Sub4() {
  }  // error:  fails to establish both 10 < k and k % 2 == 0

  //@ requires 10 < x && x % 2 == 0;
  Sub4(int x) {
    k = x;
  }
}

class SubSub extends Sub2 implements A, B {
  SubSub() {
  }  // error:  fails to establish k % 2 == 0  (superclass establishes 10 < k)

  //@ requires 10 < x && x % 2 == 0;
  SubSub(int x) {
    k = x;
  }
}
