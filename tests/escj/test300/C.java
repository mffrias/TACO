package escj.test300;
// Constructors with bodies

public class C {
  
  public int i;

  /*@ requires i>=0;
    @ ensures this.i == i/i; // divZero warning
    @*/
  public C(int i) {
    this.i = 1;
  } // Postcondition not established warning

  /*@ requires i>=0;
    @ ensures this.i == i+j+k;
    @*/
  public C(int i, int j, int k) {
    this.i = i+j+k;
  }

  /*@ ensures this.i == o.i/o.i; // null, divZero warning
    @*/
  public C(C o) {
    this.i = 1;
  } // Postcondition not established warning

  /*@ requires o.i >= 0;  // null warning
    @ ensures this.i == o.i; // no null warning - reported in precondition
    @*/
  public C(C o, int i) {
    this.i = i;
  } // Postcondition not established warning

  /*@ requires o.i >= 0; // null warning
    @ ensures this.i == o1.i; // null warning
    @*/
  public C(C o, C o1, int i) {
    this.i = i;
  } // Postcondition not established warning

  /*@ normal_behavior
    @  requires i >= 0;
    @  ensures this.i == o.i; // null warning
    @*/
  public C(C o, int i, int j) {
    this.i = i;
  } // Postcondition not established warning

  /*@ exceptional_behavior
    @  requires i >= 0;
    @  signals (RuntimeException e) o.i/o.i == o.i/o.i; // null(o), divZero warnings
    @                                                   // invariant null(charArray.owner) warning
    @*/
  public C(C o, int i, int j, int k) throws RuntimeException {
    throw new RuntimeException();
  } // Postcondition not established warning
  
  /*@ normal_behavior 
    @  requires i>=0;
    @  ensures this.i == o.i+o.i; // no null warnings because of body
    @ also exceptional_behavior
    @  requires i<0;
    @  signals (RuntimeException e) o.i/o.i == 1; // null, divZero warning
    @                                             // invariant null(charArray.owner) warning
    @*/
  public C(C o, C o1, int i, int j) throws RuntimeException {
    if (i<0)
      throw new RuntimeException();
    this.i = o.i+o1.i; // null (o), null (o1) warnings
  } // Postcondition not established warning

}
