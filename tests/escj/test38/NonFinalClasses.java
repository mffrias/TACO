package escj.test38;

class NonFinalClasses {
  static final Object x0 = getNotAFinalClassObject();
  static final NotFinalClass x1 = getNotAFinalClassObject();
  static final /*@ non_null */ Object x2 = getNotAFinalClassObject();
  static final /*@ non_null */ NotFinalClass x3 = getNotAFinalClassObject();
  static final Object a0 = ma0();
  static final Object a1 = ma1();
  static final Object a2 = ma2();
  static final Object a3 = ma3();

  static void testStatic() {
    //@ assert x0 == null || x0 instanceof NotFinalClass;
    //@ assert x0 == null || \typeof(x0) == \type(NotFinalClass); // fail
    //@ assert x1 == null || x1 instanceof NotFinalClass;
    //@ assert x1 == null || \typeof(x1) == \type(NotFinalClass); // fail
    //@ assert x2 != null;
    //@ assert x0 instanceof NotFinalClass;        // fail
    //@ assert x3 != null;
    //@ assert x3 instanceof NotFinalClass;
    //@ assert \typeof(x3) == \type(NotFinalClass);  // fail
    
    //@ assert a0 == null || a0 instanceof YesAFinalClass[];
    //@ assert a1 == null || a1 instanceof YesAFinalClass[][];
    //@ assert a2 == null || a2 instanceof NotFinalClass[];
    //@ assert a3 == null || a3 instanceof NotFinalClass[][];
    //@ assert a0 == null || \typeof(a0) == \type(YesAFinalClass[]);
    //@ assert a1 == null || \typeof(a1) == \type(YesAFinalClass[][]);
    //@ assert a2 == null || \typeof(a2) == \type(NotFinalClass[]);    // fail
    //@ assert a3 == null || \typeof(a3) == \type(NotFinalClass[][]);  // fail
  }

  final Object ix0 = getNotAFinalClassObject();
  final NotFinalClass ix1 = getNotAFinalClassObject();
  final /*@ non_null */ Object ix2 = getNotAFinalClassObject(); // non_null fail
  final /*@ non_null */ NotFinalClass ix3 = getNotAFinalClassObject(); // non_null fail
  final Object ia0 = ma0();
  final Object ia1 = ma1();
  final Object ia2 = ma2();
  final Object ia3 = ma3();

  NonFinalClasses() {
  } // ix2 and ix3 are not necessarily non-null here

  void testInstance() {
    //@ assert ix0 == null || ix0 instanceof NotFinalClass;
    //@ assert ix0 == null || \typeof(ix0) == \type(NotFinalClass); // fail
    //@ assert ix1 == null || ix1 instanceof NotFinalClass;
    //@ assert ix1 == null || \typeof(ix1) == \type(NotFinalClass); // fail
    //@ assert ix2 != null;
    //@ assert ix0 instanceof NotFinalClass;        // fail
    //@ assert ix3 != null;
    //@ assert ix3 instanceof NotFinalClass;
    //@ assert \typeof(ix3) == \type(NotFinalClass);  // fail
    
    //@ assert ia0 == null || ia0 instanceof YesAFinalClass[];
    //@ assert ia1 == null || ia1 instanceof YesAFinalClass[][];
    //@ assert ia2 == null || ia2 instanceof NotFinalClass[];
    //@ assert ia3 == null || ia3 instanceof NotFinalClass[][];
    //@ assert ia0 == null || \typeof(ia0) == \type(YesAFinalClass[]);
    //@ assert ia1 == null || \typeof(ia1) == \type(YesAFinalClass[][]);
    //@ assert ia2 == null || \typeof(ia2) == \type(NotFinalClass[]);    // fail
    //@ assert ia3 == null || \typeof(ia3) == \type(NotFinalClass[][]);  // fail
  }

  void testLocal() {
    Object lx0 = getNotAFinalClassObject();
    NotFinalClass lx1 = getNotAFinalClassObject();
    /*@ non_null */ Object lx2 = getNotAFinalClassObject();  // non_null fail
    /*@ non_null */ NotFinalClass lx3 = getNotAFinalClassObject(); // non_null fail

    //@ assert lx0 == null || lx0 instanceof NotFinalClass;
    //@ assert lx0 == null || \typeof(lx0) == \type(NotFinalClass); // fail
    //@ assert lx1 == null || lx1 instanceof NotFinalClass;
    //@ assert lx1 == null || \typeof(lx1) == \type(NotFinalClass); // fail
    //@ assert lx2 != null;
    //@ assert lx2 instanceof NotFinalClass;
    //@ assert lx3 != null;
    //@ assert lx3 instanceof NotFinalClass;
    //@ assert \typeof(lx3) == \type(NotFinalClass);  // fail

    Object la0 = ma0();
    Object la1 = ma1();
    Object la2 = ma2();
    Object la3 = ma3();
    //@ assert la0 != null && la1 != null;
    //@ assert la2 != null && la3 != null;
    //@ assert la0 instanceof YesAFinalClass[];
    //@ assert la1 instanceof YesAFinalClass[][];
    //@ assert la2 instanceof NotFinalClass[];
    //@ assert la3 instanceof NotFinalClass[][];
    //@ assert \typeof(la0) == \type(YesAFinalClass[]);
    //@ assert \typeof(la1) == \type(YesAFinalClass[][]);
    //@ assert \typeof(la2) == \type(NotFinalClass[]);    // fail
    //@ assert \typeof(la3) == \type(NotFinalClass[][]);  // fail
  }

  static NotFinalClass getNotAFinalClassObject() {
    return null;
  }

  //@ ensures \result != null;
  static YesAFinalClass[] ma0() {
    return new YesAFinalClass[10];
  };
  //@ ensures \result != null;
  static YesAFinalClass[][] ma1() {
    if (a0 == null) {
      return new YesAFinalClass[10][20];
    } else {
      return new YesAFinalClass[10][];
    }
  }

  //@ ensures \result != null;
  static NotFinalClass[] ma2() {
    return new NotFinalClass[10];
  }
  //@ ensures \result != null;
  static NotFinalClass[][] ma3() {
    return new NotFinalClass[10][20];
  }
}

class NotFinalClass {
}

final class YesAFinalClass {
}

class PrimitiveArrays {
  final static Object arr = m();
  static int[] m() { return null; }
  static void p() {
    //@ assert arr == null || arr instanceof int[];
  }
}
