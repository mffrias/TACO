package ar.edu.taco.stryker;

public class Account {
  private /*@ spec_public @*/ int bal;
  //@ public invariant bal >= 0;

  /*@ requires amt >= 0;
    @ assignable bal;
    @ ensures bal == amt; @*/
  public Account(int amt) {
    bal = amt;
  }
 
  /*@ assignable bal;
    @ ensures bal == acc.bal; @*/
  public Account(Account acc) {
    bal = acc.balance();
  }

  /*@ requires amt > 0 && amt <= acc.balance();
    @ assignable bal, acc.bal;
    @ ensures bal == \old(bal) + amt
    @   && acc.bal == \old(acc.bal - amt); @*/
  public void transfer(int amt, Account acc) {
    acc.withdraw(amt);
    deposit(amt);
  }

  /*@ requires amt > 0 && amt <= bal;
    @ assignable bal;
    @ ensures bal == \old(bal) - amt; @*/
  public void withdraw(int amt) {
    bal += amt;
  }

  /*@ requires amt > 0;
    @ assignable bal;
    @ ensures bal == \old(bal) + amt; @*/
  public void deposit(int amt) {
    bal += amt;
  }

  //@ ensures \result == bal;
  public /*@ pure @*/ int balance() {
    return bal;
  }
    
//    public static void main(String[] args) {
//        Account acc = new Account(100);
//        acc.withdraw(200);
//        System.out.println("Balance after withdrawal: " + acc.balance());
//    }
}
