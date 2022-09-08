package escj.test07;

/*@ nullable_by_default @*/
class setfield extends setfieldsuper {
  static int classvar; // Used for testing by fieldref class
  int[] arrayvar;

  //@ensures true;
  public static void m1(setfield o) {
    //@ assume o != null;
	setfield.classvar = 10;
    o.instancevar = 10;
    //@ assert 20 == o.instancevar + setfield.classvar;
  }

  //@ensures true;
  public static void m2(setfield o) {
    //@ assume o != null;
    //@ assume o.arrayvar != null;
    //@ assume o.arrayvar.length == 10;
    o.arrayvar[9] = 10;
    //@ assert 10 == o.arrayvar[9];
  }
}
