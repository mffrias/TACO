package escj.test39;

// test the relative (i.e., op=) form of the
// shift operations (these are the m0,m1,m2 tests, which should pass with
// no warnings.

// Also test that the long shift operations are not confounded with
// the int shift operations (these are tests p0,p1, and p2, which
// should produce warnings).

// Also test inclusion of various shift axioms from the universal background
// predicate

class shift {

int n;

//test unsigned right shift
//@modifies n;
//@ensures n == (\old(n)>>>i);
void m0( int i) {
n>>>=i;
//n= (n>>>i);
}

//@ensures n == (\old(n)<<i);
//@modifies n;
void m1(int i) {
n<<=i;
//n= (n<<i);
}

//@ensures n == (\old(n)>>i);
//@modifies n;
void m2(int i) {
n>>=i;
//n= (n>>i);
}

void p0(int k,int m) {
long l= m;
//@ assert (l >> k) == (m >> k);
}

void p1(int k,int m) {
long l= m;
//@ assert (l >>> k) == (m >>> k);
}

void p2(int k,int m) {
long l= m;
//@ assert (l << k) == (m << k);
}

  //@ requires m == l;
  void p3(int k, int m, long l) {
    //@ assert (l << k) == (m << k);
  }

  //@ requires m == l;
  //@ requires 0 <= k && k < 32;
  void p4(int k, int m, long l) {
    // The following is actually true in Java, but ESC/Java doesn't know it
    //@ assert (l << k) == (m << k);
  }

  //@ ensures 1 <= \result;
  int a0(int n) {
    return 1 << n;  // Post violation if n is multiple of 32 minus 1
  }

  //@ ensures 1 <= \result;
  long a1(int n) {
    return 1L << n;  // Post violation if n is multiple of 64 minus 1
  }

  /*@ requires (0 <= n && n < 31) ||
               (!b && 0 <= n && n < 63) ||
	       (7 <= n && n <= 10); */
  //@ ensures 1 <= \result;
  long a2(boolean b, int n) {
    if (b) {
      return 1 << n;
    } else {
      return 1L << n;
    }
  }
}
