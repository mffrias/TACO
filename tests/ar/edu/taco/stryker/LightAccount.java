package ar.edu.taco.stryker;
import java.util.List;
import java.util.ArrayList;

public class LightAccount {
  private /*@ spec_public @*/ int bal;
  //@ public invariant bal >= 0;

  /*@ requires amt >= 0;
    @ assignable bal;
    @ ensures bal == amt; @*/
  public LightAccount(int amt) {
    bal = amt;
  }
  
  public LightAccount() {
	  bal = 0;
  }
 
  /*@ requires amt > 0 && amt <= bal;
  @ assignable bal;
  @ ensures bal == \old(bal) - amt; @*/ 
  public void withdraw(int amt) {
    bal = bal - amt;
  }

  //@ ensures \result == bal;
  public /*@ pure @*/ int balance() {
    return bal;
  }
    
}
