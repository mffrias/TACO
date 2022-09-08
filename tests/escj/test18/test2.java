package escj.test18;

class test0 {

  int sumArray(int[] a) {
    int s = 0;
    for (int i = 1; i <= a.length; i++) {
      s += a[i];
    }
    return s;
  }
}

class test1 {

  //@ requires a != null;
  int sumArray(int[] a) {
    int s = 0;
    for (int i = 1; i <= a.length; i++) {
      s += a[i];
    }
    return s;
  }
}

class test2 {

  //@ requires a != null;
  int sumArray(int[] a) {
    int s = 0;
    for (int i = 0; i < a.length; i++) {
      s += a[i];
    }
    return s;
  }
}
