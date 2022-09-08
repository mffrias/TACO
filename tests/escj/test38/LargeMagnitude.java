package escj.test38;

class LargeMagnitude {
  void m0() {
    //@ assert 0 <= 2000000;
    //@ assert -2000000 <= 0;
    //@ assert -2000000 < 2000000;
    //@ assert -8000000000L < -7999999999L;
    //@ assert -7999999999L <= -7999999999L;
    //@ assert -7999999999L <= 2;
    //@ assert -7999999999L <= 2000000;
    //@ assert -18 < 1234567890123L;
    //@ assert Long.MIN_VALUE < Integer.MIN_VALUE;
    //@ assert Integer.MIN_VALUE < Integer.MAX_VALUE;
    //@ assert Integer.MAX_VALUE < Long.MAX_VALUE;
    //@ assert 0x80000000 < 0x8f129400;
    //@ assert 0x8000000000000000L < 0x8f12940000000000L;
    //@ assert 0 < 0x7fffFFF;
    //@ assert 0 < 0x7fffFFFffffFFFFL;
  }

  void m1() {
    long x = -7999999999L;
    //@ assert x < -7800648;
    //@ assert x - 1 == -8000000000L;  // escjava doesn't know this one
    //@ assert -2147483648 == 0x80000000;
    //@ assert -2147483647 == 0x80000001;  // escjava doesn't know this one
    //@ assert -2000000 == 0xffe17b80;  // escjava doesn't know this one
  }

  final int k0 = 18;
  final int k1 = 1500000;
  static final int k2 = 2200000;
  final long k3 = 55000111;

  //@ requires 0 <= in;
  //@ ensures 0 <= \result;
  long m2(long in, int choice) {
    switch (choice) {
      case 0:
	return in + k0;
      case 1:
	return in + k1;
      case 2:
	return in + k2;
      case 3:
	return in + k3;
      default:
	return in;
    }
  }

  //@ requires 0 <= in;
  //@ ensures 0 <= \result;
  long m3(long in, int choice) {
    return in + choice;
  }  // error:  return value may be negative

  void m4() {
    //@ assert k0 < k1;
    //@ assert k1 < k2;
    //@ assert k2 < k3;

    //@ assert k0 < 20 && 20 < k1;
    //@ assert k1 < 1700000 && 1700000 < k2;
    //@ assert k2 < 55000100 && 55000100 < k3;
  }
}
