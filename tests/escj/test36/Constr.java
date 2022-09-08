package escj.test36;

public class Constr {
  int x;
  //@ invariant 5 <= x && x < 10;

  // This constructor fails, since the zero-equivalent value for "x"
  // does not meet the invariant
  Constr() {
  }

  // This constructor fails, because it doesn't list "NullPointerException"
  // in its throws set.
  Constr(double d) {
    throw new NullPointerException();
  }

  // This constructor should be just fine
  Constr(char ch) throws NullPointerException {
    throw new NullPointerException();
  }

  // This constructor should be just fine, too
  Constr(boolean b) throws NullPointerException, CException {
    throw new CException();
  }

  // This constructor is fine also
  Constr(double d0, double d1) throws Throwable {
    throw new CException();
  }

  // This constructor is not fine, because it destroys the "Constr" invariant
  // of its argument
  //@ requires c != null;
  Constr(Constr c) throws CException {
    c.x = 2;
    throw new CException();
  }

  // This constructor is not fine, since it may break the invariant of the "Constr"
  // object it allocates
  Constr(int y) throws CException {
    Constr c = new Constr();
    c.x = y;
    throw new CException();
  }
  
  // This constructor is fine, since the argument "c" couldn't possibly be
  // the object "this" being constructed
  Constr(Constr c, int y) {
    x = 8;
    if (c == this) {
      c.x = 2;
    }
  }

  // This constructor is fine, and declares that it never terminates normally
  //@ ensures false;
  Constr(double d0, double d1, double d2) throws CException {
    throw new CException();
  }
}

class ConstrSub extends Constr {
  int y;
  //@ invariant y < 0;

  // This constructor should be fine
  ConstrSub() {
    y = -x;
  }

  // This constructor is fine.  If the superclass constructor terminates with an
  // exception, so will this constructor
  ConstrSub(int k, int m) throws Throwable {
    super(k == m);
    y -= 4;  //@ assert y == -4;
  }
  
  // This constructor is not fine, since it doesn't assign a negative value to "y"
  ConstrSub(char ch) {
  }

  // This constructor is fine, because the call to the superclass constructor never
  // terminates normally
  ConstrSub(double d) throws Throwable {
    super(d, d, d);
  }
}

class ConstrClient {
  // This method is fine
  void m0() {
    Constr c = new Constr();
    //@ assert 5 <= c.x;
  }

  // This method is fine
  void m1() {
    Constr c = null;
    boolean b = false;
    try {
      c = new ConstrSub(2.7);
    } catch (CException ce) {
      b = true;
    } catch (Throwable t) {
      b = true;
    }
    //@ assert !b ==> c != null && 5 <= c.x;
    //@ assert b ==> c == null;
  }

  // This method is fine
  void m2() {
    try {
      Constr c = new Constr(2.7, 3.1, -1.0);
      //@ assert false;
    } catch (Throwable t) {
    }
  }

  // This method is fine
  void m3() {
    Constr c = new Constr();
    //@ assert 5 <= c.x;
    try {
      c = new Constr(c);
    } catch (CException ce) {
    }
    //@ assert 5 <= c.x;
  }
}

class CException extends Throwable {
}
