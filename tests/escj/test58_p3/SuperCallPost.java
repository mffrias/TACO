package escj.test58_p3;

class Super {
  int k;

  Super() {  // is not required to establish invariant declared in subclass
  }
}

class SuperCallPost extends Super {
  //@ invariant 10 <= k;

  SuperCallPost() {
    super();
  }

  //@ requires 20 <= x && x <= Integer.MAX_VALUE;
  SuperCallPost(long x) {
    super();
    k = (int)x;
  }
}
