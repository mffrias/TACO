package escj.test38;

class FinalFields {
  static final int x = 16;
  static final boolean b0 = false;
  static final boolean b1 = ('W' < 'Q' ? false : 3 * 5 < 18);
  static final char ch = 'W';
  static final String s = ("hello");
  static final Object t = new Object();
  static final int[] a = new int[false != true ? 24 % 5 * 3 : 3]; // 12
  static final FinalFields[] aa = new FinalFields[a.length];
  static final Object synonymForNull = null;
  static final int synonymForFive = 5;
  static final int[] av10 = {3, 2, 0};
  static final int[] av11 = new int[] {3, 2, 0};
  static final Object[] avo = new Object[] {"hi", "hello"};
  static final Object[] avs = new String[] {"hi", "hello"};
  static final Object[][] avmA = {{null, null}, {"hello"}, null};
  static final Object[] avmB = new Object[12][40];
  static final Object ss0 = "hi";
  static final String ss1 = "hello " + "there";
  static final String ss2 = "hey" + 12.5;  // this generates a String
  static final Object ss3 = (null + "good");  // this generates a String
  static final Object finalcast = (FinalClass)getObject();

  /*@ non_null */ static Object nnx;

  //@ requires x == 16;
  //@ requires !b0;
  //@ requires b1;
  //@ requires ch == 'W';
  //@ requires ch != 'Q';
  //@ requires s != null;
  //@ requires t != null;
  //@ requires \typeof(t) == \type(Object);
  //@ requires a != null;
  //@ requires \typeof(a) == \type(int[]);
  //@ requires a.length == 12;
  //@ requires aa != null;
  //@ requires \typeof(aa) == \type(FinalFields[]);
  //@ requires synonymForNull == null;
  //@ requires synonymForFive == 5;
  //@ requires av10 != null;
  //@ requires \typeof(av10) == \type(int[]);
  //@ requires av10.length == 3;
  //@ requires av11 != null;
  //@ requires \typeof(av11) == \type(int[]);
  //@ requires av11.length == 3;
  //@ requires avo != null;
  //@ requires \typeof(avo) == \type(Object[]);
  //@ requires avo.length == 2;
  //@ requires avs != null;
  //@ requires \typeof(avs) == \type(String[]);
  //@ requires avs.length == 2;
  //@ requires avmA != null;
  //@ requires avmA.length == 3;
  //@ requires \typeof(avmA) == \type(Object[][]);
  //@ requires avmB != null;
  //@ requires avmB.length == 12;  // not 40
  //@ requires \typeof(avmB) == \type(Object[][]);
  //@ requires ss0 != null;
  //@ requires \typeof(ss0) == \type(String);
  //@ requires ss1 != null;
  //@ requires \typeof(ss1) == \type(String);
  //@ requires ss2 != null;
  //@ requires \typeof(ss2) == \type(String);
  //@ requires ss3 != null;
  //@ requires \typeof(ss3) == \type(String);
  //@ requires finalcast == null || finalcast instanceof FinalClass;
  //@ requires finalcast == null || \typeof(finalcast) == \type(FinalClass);
  //@ requires finalcast == (FinalClass)finalcast;
  private static void checkStaticFieldInitializers() {
    /*@ assert av10[1] == 2; */  // this must fail, since the initial value of 2 may be changed
    /*@ assert avmA[0].length == 2; */  // this must fail, for the same reason
  }
  
  final int ix = 16;
  final boolean ib0 = false;
  final boolean ib1 = ('W' < 'Q' ? false : 3 * 5 < 18);
  static final char ich = 'Q';
  final String is = ("hello");
  final Object it = new Object();
  final int[] ia = new int[ix - 4]; // 12
  final FinalFields[] iaa = new FinalFields[a.length];
  final Object anotherSynonymForNull = null;
  static final int anotherSynonymForFive = 5;
  static final int[] iav10 = {3, 2, 0};
  static final int[] iav11 = new int[] {3, 2, 0};
  static final Object[] iavo = new Object[] {"hi", "hello"};
  static final Object[] iavs = new String[] {"hi", "hello"};
  final Object[][] iavmA = {{null, null}, {"hello"}, null};
  final Object[] iavmB = new Object[12][40];
  final Object iss0 = "hi";
  final String iss1 = "hello " + "there";
  final String iss2 = "hey" + 12.5;  // this generates a String
  final Object iss3 = (null + "good");  // this generates a String
  final Object ifinalcast = (FinalClass)getObject();

  /*@ non_null */ Object innx;

  //@ requires ix == 16;
  //@ requires !ib0;
  //@ requires ib1;
  //@ requires ich != 'W';
  //@ requires ich == 'Q';
  //@ requires is != null;
  //@ requires it != null;
  //@ requires \typeof(it) == \type(Object);
  //@ requires ia != null;
  //@ requires \typeof(ia) == \type(int[]);
  //@ requires ia.length == 12;
  //@ requires iaa != null;
  //@ requires \typeof(iaa) == \type(FinalFields[]);
  //@ requires anotherSynonymForNull == null;
  //@ requires anotherSynonymForFive == 5;
  //@ requires iav10 != null;
  //@ requires \typeof(iav10) == \type(int[]);
  //@ requires iav10.length == 3;
  //@ requires iav11 != null;
  //@ requires \typeof(iav11) == \type(int[]);
  //@ requires iav11.length == 3;
  //@ requires iavo != null;
  //@ requires \typeof(iavo) == \type(Object[]);
  //@ requires iavo.length == 2;
  //@ requires iavs != null;
  //@ requires \typeof(iavs) == \type(String[]);
  //@ requires iavs.length == 2;
  //@ requires iavmA != null;
  //@ requires iavmA.length == 3;
  //@ requires \typeof(iavmA) == \type(Object[][]);
  //@ requires iavmB != null;
  //@ requires iavmB.length == 12;  // not 40
  //@ requires \typeof(iavmB) == \type(Object[][]);
  //@ requires iss0 != null;
  //@ requires \typeof(iss0) == \type(String);
  //@ requires iss1 != null;
  //@ requires \typeof(iss1) == \type(String);
  //@ requires iss2 != null;
  //@ requires \typeof(iss2) == \type(String);
  //@ requires iss3 != null;
  //@ requires \typeof(iss3) == \type(String);
  //@ requires ifinalcast == null || ifinalcast instanceof FinalClass;
  //@ requires ifinalcast == null || \typeof(ifinalcast) == \type(FinalClass);
  //@ requires ifinalcast == (FinalClass)ifinalcast;
  private void checkInstanceFieldInitializers() {
    /*@ assert iav10[1] == 2; */  // this must fail, since the initial value of 2 may be changed
    /*@ assert iavmA[0].length == 2; */  // this must fail, for the same reason
  }

  FinalFields() {
    checkStaticFieldInitializers();
    /*@ assert innx != null; */  // this will fail, by design (since innx should not be used here)
    innx = new Object();
    checkInstanceFieldInitializers();
    // just make sure checker gets this far
    int localA, localB;
    localA = localB = 0;
    //@ assert localA < localB;  // warns if checker gets here, which is good
  }

  FinalFields(int j) {
    this();
    checkStaticFieldInitializers();
    /*@ assert innx != null; */
    checkInstanceFieldInitializers();
  }

  void m() {
    checkStaticFieldInitializers();
    checkInstanceFieldInitializers();
  }

  static void p() {
    checkStaticFieldInitializers();
  }

  static void q(/*@ non_null */ FinalFields ffParam) {
    ffParam.checkInstanceFieldInitializers();
    FinalFields ffNew = new FinalFields();
    ffParam.checkInstanceFieldInitializers();
    ffNew.checkInstanceFieldInitializers();
    checkStaticFieldInitializers();
  }

  static Object getObject() {
    return null;
  }
}

final class FinalClass {
}
