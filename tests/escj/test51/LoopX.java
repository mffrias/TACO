package escj.test51;

class LoopX {

  void m05(int[] a) {
    for (int i = 0;
	 i < a.length;  // fails only with loop >= 0.5
	 i++) {
    }
  }

  void m10(int x) {
    for (int i = 0; i < x; i++) {
      /*@ assert false; */  // fails only with loop >= 1.0
    }
  }

  void m15(int x) {
    boolean b = true;
    for (int i = 0; i < x; i++) {
      if (i == 0)
	b = false;
    }
    /*@ assert b; */  // fails only with loop >= 1.5
  }

  void m20(int x) {
    for (int i = 0; i < x; i++) {
      /*@ assert i != 1; */  // fails only with loop >= 2.0
    }
  }
  
  void m25(int x) {
    int s = 0;
    int i = 0;
    while (i < x) {
      s += 10;
      i++;
    }
    /*@ assert s <= 10; */  // fails only with loop >= 2.5
  }
  
  void m30(int x) {
    int s = 0;
    for (int i = 0; i < x; i++) {
      for (int j = 0; j < i; j++) {
	s += 2;
	/*@ assert s < 6; */  // fails only with loop >= 3.0
      }
    }
  }

  void m35(int x) {
    int s = 0;
    for (int i = 0; i < x; i++) {
      for (int j = 0; j < i; j++) {
	s += 2;
      }
    }
    /*@ assert s <= 5; */  // fails only with loop >= 3.5
  }

  void m40() {
    int i = 0;
    do {
      i++;
    } while (i < 4);
    /*@ assert i < 4; */  // fails only with loop >= 4.0
  }
  
  //@ requires a != null && 4 <= a.length;
  void m45(int x, int[] a) {
    int i = 0;
    while (i < x) {
      i++;
    }
    int y = a[i];  // fails only with loop >= 4.5
  }
  
}
