package escj.test303a;

public class E2 {

  //@ invariant a.length>0; // null(a) warning
  public int [] a;

  /*@ normal_behavior
    @  requires aa.length>0; //null(aa) warning
    @  ensures this.a==aa;
    @*/
  public E2(int [] aa) {
    this.a=aa;
  }

  /*@ normal_behavior
    @  requires aa.length>0; //null(aa) warning
    @  ensures this.a==aa;
    @*/
  public void test2(int [] aa) {
    this.a=aa;
  }

  /*@ normal_behavior
    @  requires aa.length>0;
    @  ensures this.a==aa;
    @*/
  public void test3(/*@non_null*/int [] aa) {
    this.a=aa;
  }
}
