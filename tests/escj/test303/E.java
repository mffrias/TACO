package escj.test303;

public class E {

  //@ invariant this.f>=88; 
  //@ invariant f/f == f/f; // divZero warning
  public int f;

  //@ invariant a.length>0; // null(a) warning
  public int [] a;

  /*@ ensures \result == this.f+1;
    @*/
  public int test0() {
    return this.f+1;
  } 

  /*@ requires ff>=99;
    @ ensures this.f == ff;
    @*/
  public int test1(int ff) {
    this.f=ff;
    return -1;
  }

  /*@ normal_behavior
    @  requires ff>=99;
    @  requires aa.length>0; //null(aa) warning
    @  ensures this.f==ff;
    @  ensures this.a==aa;
    @*/
  public void test2(int ff, int [] aa) {
    this.f=ff;
    this.a=aa;
  }

  /*@ normal_behavior
    @  requires ff>=99;
    @  requires aa.length>0;
    @  ensures this.f==ff;
    @  ensures this.a==aa;
    @*/
  public void test3(int ff, /*@non_null*/int [] aa) {
    this.f=ff;
    this.a=aa;
  }


  /*@ normal_behavior
    @  requires ff>=99;
    @  requires aa.length>0; //null(aa) warning
    @  ensures this.f==ff;
    @  ensures this.a==aa;
    @*/
  public E(int ff, int [] aa) {
    this.f=ff;
    this.a=aa;
  }
}
