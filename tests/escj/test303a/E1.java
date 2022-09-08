package escj.test303a;

public class E1 {

  //@ invariant this.f>=88; 
  //@ invariant f/f == f/f; // divZero warning
  public int f;

  /*@ normal_behavior
    @  requires ff>=99;
    @  ensures this.f==ff;
    @*/
  public E1(int ff) {
    this.f=ff;
  }

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
    @  ensures this.f==ff;
    @*/
  public void test2(int ff) {
    this.f=ff;
  }

  /*@ normal_behavior
    @  requires ff>=99;
    @  ensures this.f==ff;
    @*/
  public void test3(int ff) {
    this.f=ff;
  }
}
