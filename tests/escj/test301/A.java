package escj.test301;

// static methods with bodies

public class A {

  /*@ requires i >= 0;
    @ ensures \result == (i/i)*2; // divZero warning
    @*/
  public static int test0(int i) {
    return 2;
  } // Postcondition not established warning

  /*@ normal_behavior
    @  requires i > 0;
    @  ensures \result == (i/i)*2; // no divZero warning because of precondition
    @*/
  public static int test1(int i) {
    return 2;
  } // Postcondition not established warning

  /*@ exceptional_behavior
    @  requires i<0;
    @  signals (RuntimeException e) i<0; // null(charArray.owner) because of invariant
    @*/
  public static int test2(int i) throws RuntimeException {
    throw new RuntimeException();
  }

  /*@ exceptional_behavior
    @  requires i<0;
    @  signals (RuntimeException e) i/i == i/i; // no divZero warning because of precondition
    @                                           // null(charArray.owner) because of invariant
    @*/
  public static int test3(int i) throws RuntimeException {
    throw new RuntimeException();
  }

  /*@ exceptional_behavior
    @  requires i <= 0;
    @  signals (RuntimeException e) i/i == i/i; // divZero warning
    @                                           // null(charArray.owner) because of invariant
    @*/
  public static int test4(int i) throws RuntimeException {
    throw new RuntimeException();
  }

  /*@ normal_behavior
    @  requires i>=0;
    @  ensures \result == (i/i)*2; // divZero warning
    @ also exceptional_behavior
    @  requires i<0;
    @  signals (RuntimeException e) i/i == i/i; // no divZero because of precondition
    @                                           // null(charArray.owner) because of invariant
    @*/
  public static int test5(int i) throws RuntimeException {
    if(i<0)
      throw new RuntimeException();
    return 2;      
  } // Postcondition not established warning

  /*@ normal_behavior
    @  requires i>0;
    @  ensures \result == (i/i)*2; // no divZero warning because of precondition
    @ also exceptional_behavior
    @  requires i<=0;
    @  signals (RuntimeException e) i/i == i/i; // divZero warnings
    @                                           // null(charArray.owner) because of invariant    
    @*/
  public static int test6(int i) throws RuntimeException {
    if(i<=0)
      throw new RuntimeException();
    return (i/i)*2;
  }
	
  //@ requires i/i==i/i; // expect: ZeroDiv
  public static int test7(int i) {
    return (i/i)*2;
  }

}
