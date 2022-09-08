package escj.test38;

class TestConstants {

  static final boolean t = true;

  static final int i = 1;
  static final char c = 'a';
  static final byte b = 2;
  static final short s = 3;
  static final long l = 4;

  static final float f = 100;
  static final double d = 1.3;

  static final String ss = "hello";
  static final String sr = null;

  static final String[] arg = { "hello", "there" };

  static final long minLong = 0x8000000000000000L;


  // no errors in this one...
  void testPos() {
    //@ assert t;

    //@ assert i+c+b+s+l == 1+'a'+2+3+4;

    //@ assert f == 100;
    //@ assert d == 1.3;

    ss.equals(arg[0]);
    ss.equals(arg[1]);

    // Make sure we can handle converting minLong to a symbolic string:
    //@ assert minLong == minLong;
  }


  void testNeg1() {
    sr.equals("hello");		// error
  }

  void testNeg2() {
    ss.equals(arg[3]);		// error
  }
}

class EqualConstants {
  static final int X = 12;
  static final int Y = X;
  static final int Z = X + 1 + Y;

  void m0() {
    //@ assert X == 12;
  }

  void m1() {
    //@ assert Y == 12;
  }

  void m2() {
    //@ assert X == Y;
  }

  void m3() {
    //@ assert Z == 25;
  }
}
