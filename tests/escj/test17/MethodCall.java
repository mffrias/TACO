package escj.test17;

public class MethodCall {

//  //@ ensures false;
//  static MethodCall loopForever() {
//    while (true)
//      ;
//  }
//
//  //@ requires r0 != null && r1 != null;
//  static void pairaNonnulls(Object r0, Object r1) {
//  }
//
  void m0_ok(MethodCall mc) {
    if (mc != null) {
      //BEGIN: Changed by DPD
      mc = null;	
      mc.m0_ok(mc);      // okay
      //END: Changed by DPD
      //ORIGNAL: mc.m0_ok(mc = null);      // okay
      
    }
  }
//
//  void m1_fail(MethodCall mc) {
//    (mc = null).m1_fail(mc);      // null dereference
//  }
//
//  void m2_ok(MethodCall mc) {
//    mc = null;
//    mc.m2_ok(loopForever());    // does not generate error, since "loopForever" doesn't return
//  }
//
//  void m3_ok(MethodCall mc) {
//    if (mc != null) {
//      mc.m2_ok(loopForever());
//      // next line does not generate error, since "loopForever" doesn't return
//      //@ assert false;
//    }
//  }
//
//  void m4_ok() {
//    /********  the following line currently crashes the checker
//    MethodCall.m4_ok();  // this is not allowed in Java (can't make static
//                         // reference to instance method)
//    *********/
//  }
//
//  static void m5_ok() {
//    MethodCall.m5_ok();
//    m5_ok();
//  }
//
//  void m6_ok(MethodCall mc) {
//    // Note that "m5_ok" is static, so "mc" will be ignored, and it doesn't
//    // matter if "mc" is null.
//    mc.m5_ok();  
//  }
//
//  MethodCall f;
//  
//  void m7_fail(MethodCall mc) {
//    // Note that "m5_ok" is static, so "mc.f" will be evaluated (and then ignored).
//    // However, the evaluation of "mc.f" may fail, since "mc" may be null.
//    (mc.f).m5_ok();
//  }
//
//  void new0_ok() {
//    MethodCall mc = new MethodCall();
//  }
//
//  //@ ensures \result != null;
//  MethodCall new1_ok() {
//    MethodCall mc = new MethodCall();
//    return mc;
//  }
//
//  //@ ensures \result == null;
//  MethodCall new2_fail() {
//    MethodCall mc = new MethodCall();
//    return mc;
//  }
//
//  /* The following test cases actually test field dereferences, not
//   * method calls, but at the time these tests were written, both
//   * parts of the checker suffered from a similar problem, so the
//   * test cases ended up here.
//   */
//
//  int x;
//  static int g;
//  
//  int f0_ok() {
//    return x;
//  }
//
//  /****  The following method currently crashes the checker
//  int f1_ok() {
//    return MethodCall.x;
//  }
//  ****/
//
//  int f2_ok() {
//    return g;
//  }
//
//  int f3_ok() {
//    return MethodCall.g;
//  }
//
//  int f4_ok() {
//    MethodCall mc = this;
//    int z = g;
//    int y = (mc = null).g;
//    //@ assert mc == null;
//    //@ assert y == z;
//    return z + y;
//  }
//
//}
//
//class SubMethodCall extends MethodCall {
//
//  // override
//  void m4_ok() {
//    m4_ok();
//    m5_ok();
//    super.m4_ok();
//    super.m5_ok();
//    this.m4_ok();
//    this.m5_ok();
//    MethodCall.m5_ok();
//    // MethodCall.m4_ok();  // not allowed by Java
//  }
//
//  static void m5sub_ok() {
//    m5sub_ok();
//    SubMethodCall.m5sub_ok();
//  }
//  
//  int f5_ok() {
//    return super.x;
//  }
//
//  int f6_ok() {
//    return super.g;
//  }
  
}
