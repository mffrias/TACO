package bug;

public class BankAccount {
  public int balance;
  public int previousTransaction;
  //@ invariant 0 <= balance;

  //isValidA-int-
  //@ ensures \result == true <==> 0 < amount_m;
  //@ ensures balance == \old(balance) && previousTransaction == \old(previousTransaction);
  public boolean isValidA(int amount_m) {
    if (0 < amount_m) {
      return true;
    } else {
      return false;
    }
  }

  //isValid-int,int-
  //@ requires 0 < amount_m;
  //@ requires 0 <= balance_m;
  //@ ensures \result == true <==> 0 <= balance_m - amount_m;
  //@ ensures balance == \old(balance) && previousTransaction == \old(previousTransaction);
  public boolean isValid(int balance_m, int amount_m) {
    if (0 <= balance_m - amount_m) {
      return true;
    } else {
      return false;
    }
  }

  //withdraw-int-
  //@ requires -4 <= balance && balance <= 3;
  //@ requires -4 <= previousTransaction && previousTransaction <= 3;
  //@ requires -4 <= amount && amount <= 3;
  //@ requires (\exists int i; i == -amount; true);
  // ensures 0 < amount && 0 <= \old(balance) - amount ==> balance == \old(balance) - amount && previousTransaction == -1 * amount;
  //@ ensures 0 < amount && 0 <= \old(balance) - amount ==> balance == \old(balance) - amount && previousTransaction == -amount;
  //@ ensures !(0 < amount && 0 <= \old(balance) - amount) ==> balance == \old(balance) && previousTransaction == \old(previousTransaction);
  void withdraw(int amount) {
    if (isValidA(amount)) {
      if (isValid(balance, amount)) {
        balance = balance - amount;
        previousTransaction = -amount;
      }
    }
  }
}