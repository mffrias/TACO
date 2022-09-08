package escj.test15;

/** This test file contains a few tests that check that
 ** the InitialState function equates variables with
 ** their @pre versions.
 **/

public class initstate {

  static int g;

  //@ requires 0 <= g;
  //@ ensures 0 <= \result;
  int m0_ok() {
    return g;
  }

  //@ requires g < 10;
  //@ ensures \result < 10;
  int m1_fail() {
    g= g + 1;
    return g;
  }

  // This method fails for the following reason:  There is no modifies
  // clause, and in particular "g" does not appear in the modifies clause.
  // Therefore, "\old(g)" in the postcondition is the same as just "g". 
  // However escjava will issue a caution.
  //@ requires g < 10;
  //@ ensures \old(g) < 10;
  void m2_fail() {
    g= g + 1;
  }

  // Here's the fix
  //@ requires g < 10;
  //@ modifies g;
  //@ ensures \old(g) < 10;
  void m3_ok() {
    g= g + 1;
  }
}
