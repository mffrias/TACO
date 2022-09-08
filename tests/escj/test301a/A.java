package escj.test301a;

// static methods with bodies

public class A {

  private static void testAssertionInBody1(int i) {
      //@ assert i/i == i/i; // expect: ZeroDiv
  }
  
  private static void testAssertionInBody2ForAll_div(int i) {
      // No range:
      //@ assert (\forall int i; i != 0 ==> i/i == i/i);
      //@ assert (\forall int i; i/i == i/i); // expect: ZeroDiv

      // Trivial range:
      //@ assert (\forall int i; true; i != 0 ==> i/i == i/i);
      //@ assert (\forall int i; true; i/i == i/i); // expect: ZeroDiv

      // Proper range:
      //@ assert (\forall int i; i != 0; i/i == i/i);
      //@ assert (\forall int i; i == 0 | i != 0; i/i == i/i); // expect: ZeroDiv
  }
  
  
  private static void testAssertionInBody2ForAll_deref(int i) {
      // No range:
      //@ assert (\forall Object o; o != null ==> o.getClass() == o.getClass());
      //@ assert (\forall Object o; o.getClass() == o.getClass()); // expect: Null

      // Range:
      //@ assert (\forall Object o; o != null; o.getClass() == o.getClass());
      //@ assert (\forall Object o; true;      o.getClass() == o.getClass()); // expect: Null

      // Unfortunately, ESC/Java2 does not yet support the following syntax ...
      //+@ assert (\forall non_null Object o; o.getClass() == o.getClass());
  }
  
  private static void testAssertionInBody2Exists(int i) {
      // No range:
      //@ assert (\exists int i; i != 0 && i/i == i/i); // no IDC error, but unprovable by ATPs.
      //@ assert (\exists int i; i/i == i/i); // expect: ZeroDiv

      // Trivial range:
      //@ assert (\exists int i; true; i != 0 && i/i == i/i); // no IDC error, but unprovable ...
      //@ assert (\exists int i; true; i/i == i/i); // expect: ZeroDiv

      // Proper range:
      //@ assert (\exists int i; i != 0; i/i == i/i); // no IDC error, but unprovable by ATPs.
      //@ assert (\exists int i; i == 0 | i != 0; i/i == i/i); // expect: ZeroDiv
  }

    //@ requires i > 0 && (\forall int j; j >= i; i/(j-i) == i/(j-i));
    //@ requires i > 0 && (\forall int j; j > i;  i/(j-i) == i/(j-i));
    private static void testPre1(int i) { }

    static int si;
    //@ static invariant (\exists Object o; o.getClass() == o.getClass()) || si >= 0;
}
