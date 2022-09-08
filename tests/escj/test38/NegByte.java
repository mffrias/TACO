package escj.test38;

class NegByte {
  static final byte b = (byte)0xFF;  // caused escjava 1.2.2 to crash

  //@ ensures \result == -1;
  int m() {
    //@ assert a == a;  // make sure 'a' gets placed in back pred
    //@ assert b == b;  // make sure 'b' gets placed in back pred
    byte c = (byte)0xFF;
    return c;
  }  // escjava does not know that dynamically casting 0xFF to byte yields -1

  static final Object[] a = new Object[(byte)0xFF];  // caused escjava 1.2.2 to crash

  Object[] a0 = new Object[(byte)0x3F];
  Object[] a1 = new Object[(byte)0xF3];  // neg array size!
}
