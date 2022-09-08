package escj.test41;

// This test file performs a rudimentary check that the
// formal parameters mentioned in a modifies clause are
// replaced by the actual parameters of a call.

// Our checker once did this incorrectly.  The name of
// the class in this test file is explained by the fact that
// the error was discovered during a private demo of ESC/Java
// at POPL'99 in San Antonio, TX.

class TexasSubstitution {
  int n;

  //@ modifies n;
  void m() {
  }

  //@ requires x != null && x != this;
  void mGood(TexasSubstitution x) {
    int nx = x.n;
    int nthis = this.n;
    x.m();
    // The following line should generate no warning
    //@ assert nthis == this.n;
  }

  //@ requires x != null && x != this;
  void mBad(TexasSubstitution x) {
    int nx = x.n;
    int nthis = this.n;
    x.m();
    // The following line should generate a warning
    //@ assert nx == x.n;
  }
}
