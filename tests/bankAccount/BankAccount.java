package bankAccount;

public class BankAccount {
  int balance;
  int previousTransaction;
  //@ invariant 0 <= balance;

  //BankAccount-int-
  //@ ensures currentBalance <= 0 ==> balance == 0;
  //@ ensures 0 < currentBalance ==> balance == currentBalance;
  //@ ensures previousTransaction == 0;
  BankAccount(int currentBalance) {
    if (currentBalance > 0) { // if (currentBalance <= 0){
      balance = 0;
    } else {
      balance = currentBalance;
    }
    previousTransaction = 0;
  }

  //BankAccount-int,int-
  //@ ensures currentBalance <= 0 ==> balance == 0;
  //@ ensures 0 < currentBalance ==> balance == currentBalance;
  //@ ensures previousTransaction == previousTransaction_m;
  BankAccount(int currentBalance, int _previousTransaction) {
    if (currentBalance <= 0) {
      balance = 0;
    } else {
      balance = currentBalance;
    }
    previousTransaction = _previousTransaction;
  }

  //getBalance--
  //@ ensures \result == this.balance;
  //@ ensures balance == \old(balance) && previousTransaction == \old(previousTransaction);
  public int getBalance() {
    return this.balance;
  }

  //getPreviousTransaction--
  //@ ensures \result == this.previousTransaction;
  //@ ensures balance == \old(balance) && previousTransaction == \old(previousTransaction);
  public int getPreviousTransaction() {
    return this.previousTransaction;
  }

  //isValid-int-
  //@ ensures \result == true <==> 0 < amount_m;
  //@ ensures balance == \old(balance) && previousTransaction == \old(previousTransaction);
  public boolean isValid(int _amount) {
    if (0 < _amount) {
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
  public boolean isValid(int _balance, int _amount) {
    if (0 <= _balance - _amount) {
      return true;
    } else {
      return false;
    }
  }

  //deposit-int-
  //@ requires 0 < amount ==> amount + balance >= amount;
  //@ ensures 0 < amount ==> balance == \old(balance) + amount && previousTransaction == amount;
  //@ ensures 0 >= amount ==> balance == \old(balance) && previousTransaction == \old(previousTransaction);
  void deposit(int amount) {
    if (isValid(amount)) {
      balance = balance + amount;
      previousTransaction = amount;
    }
  }

  //withdraw-int-
  //@ ensures 0 < amount && 0 <= \old(balance) - amount ==> balance == \old(balance) - amount && previousTransaction == -1 * amount;
  //@ ensures !(0 < amount && 0 <= \old(balance) - amount) ==> balance == \old(balance) && previousTransaction == \old(previousTransaction);
  void withdraw(int amount) {
    if (isValid(amount)) {
      if (isValid(balance, amount)) {
        balance = balance - amount;
        previousTransaction = -amount;
      }
    }
  }

  //checkWithdrawal-int-
  //@ ensures 0 < amount && 0 <= \old(balance) - amount ==> balance == \old(balance) - amount && previousTransaction == -1 * amount;
  //@ ensures 0 < amount && 0 > \old(balance) - amount && 0 <= \old(balance) - 50 ==> balance == \old(balance) - 50 && previousTransaction == -50;
  //@ ensures 0 < amount && 0 > \old(balance) - amount && 0 > \old(balance) - 50 ==> balance == 0 && previousTransaction == \old(-1 * balance);
  //@ ensures 0 >= amount ==> balance == \old(balance) && previousTransaction == \old(previousTransaction);
  void checkWithdrawal(int amount) {
    if (isValid(amount)) {
      if (isValid(balance, amount)) {
        balance = balance - amount;
        previousTransaction = -amount;
      } else {
        int notEnoughMoneyPenalty;
        notEnoughMoneyPenalty = 50;
        int _balance;
        _balance = balance - notEnoughMoneyPenalty;
        if (0 <= _balance) {
          balance = _balance;
          previousTransaction = -notEnoughMoneyPenalty;
        } else {
          previousTransaction = -balance;
          balance = 0;
        }
      }
    }
  }

  //foreignTransfer-int-
  //@ requires amount <= 2045222522; // amount + amount/100*5 >= amount
  //@ ensures 0 < (amount + (amount/100)*5) && 0 <= \old(balance) - (amount + (amount/100)*5) ==> balance == \old(balance) - (amount + (amount/100)*5) && previousTransaction == -1 * (amount + (amount/100)*5);
  //@ ensures !(0 < (amount + (amount/100)*5) && 0 <= \old(balance) - (amount + (amount/100)*5)) ==> balance == \old(balance) && previousTransaction == \old(previousTransaction);
  void foreignTransfer(int amount) {
    int penalty;
    penalty = (amount / 100) * 5;
    amount = amount + penalty;
    if (isValid(amount)) {
      if (isValid(balance, amount)) {
        balance = balance - amount;
        previousTransaction = -amount;
      }
    }
  }

  //foreignDeposit-int-
  //@ requires 0 < (amount - (amount/100)*5) ==> (amount - (amount/100)*5) + balance >= balance;
  //@ ensures 0 < (amount - (amount/100)*5) ==> balance == \old(balance) + (amount - (amount/100)*5) && previousTransaction == (amount - (amount/100)*5);
  //@ ensures !(0 < (amount - (amount/100)*5)) ==> balance == \old(balance) && previousTransaction == \old(previousTransaction);
  void foreignDeposit(int amount) {
    int penalty;
    penalty = (amount / 100) * 5;
    amount = amount - penalty;
    if (isValid(amount)) {
      balance = balance + amount;
      previousTransaction = amount;
    }
  }


  //withdrawByCashBack-int-
  //@ ensures 0 < (amount - (amount/100)*2) && 0 <= \old(balance) - (amount - (amount/100)*2) ==> balance == \old(balance) - (amount - (amount/100)*2) && previousTransaction == -1 * (amount - (amount/100)*2);
  //@ ensures !(0 < (amount - (amount/100)*2) && 0 <= \old(balance) - (amount - (amount/100)*2)) ==> balance == \old(balance) && previousTransaction == \old(previousTransaction);
  void withdrawByCashBack(int amount) {
    int cashback;
    cashback = (amount / 100) * 2;
    amount = amount - cashback;
    if (isValid(amount)) {
      if (isValid(balance, amount)) {
        balance = balance - amount;
        previousTransaction = -amount;
      }
    }
  }

  //ATMWithdraw-int-
  //@ requires amount + 4 >= amount;
  //@ ensures 0 < amount && 0 <= \old(balance) - (amount + 4) ==> balance == \old(balance) - (amount + 4) && previousTransaction == -1 * (amount + 4);
  //@ ensures !(0 < amount && 0 <= \old(balance) - (amount + 4)) ==> balance == \old(balance) && previousTransaction == \old(previousTransaction);
  void ATMWithdraw(int amount) {
    int ATMpenalty = 4;
    if (isValid(amount)) {
      amount += ATMpenalty;
      if (isValid(balance, amount)) {
        balance = balance - amount;
        previousTransaction = -amount;
      }
    }
  }

  //interestAfterYear--
  //@ ensures \old(balance) <= 20000 ==> \result == \old(balance)/100;
  //@ ensures 20000 < \old(balance) && \old(balance) <= 160000 ==> \result == (\old(balance)/100)*2;
  //@ ensures 160000 < \old(balance) && \old(balance) <= 300000 ==> \result == (\old(balance)/100)*3;
  //@ ensures 300000 < \old(balance) ==> \result == (\old(balance)/100)*4;
  //@ ensures balance == \old(balance) && previousTransaction == \old(previousTransaction);
  public int interestAfterYear() {
    int interest;
    interest = 0;
    if (balance <= 20000) {
      interest = balance / 100;
    } else if (balance <= 160000) {
      int _interest;
      _interest = balance / 100;
      interest = _interest * 2;
    } else if (balance <= 300000) {
      int _interest;
      _interest = balance / 100;
      interest = _interest * 3;
    } else {
      int _interest;
      _interest = balance / 100;
      interest = _interest * 4;
    }
    return interest;
  }
//
//  //menu-int,int-
//  //@assignable \everything;
//  //@requires 0 <= option && option <= 9;
//  //@ {|
//  //@    requires option == 1 && isValid(amount);
//  //@    requires amount + balance <= Integer.MAX_VALUE;
//  //@    ensures balance == \old (balance) + amount;
//  //@    ensures previousTransaction == amount;
//
//  //@    also
//
//  //@    requires option == 2 && isValid(amount);
//  //@    requires isValid(balance, amount);
//  //@    ensures balance == \old (balance) - amount;
//  //@    ensures \result == balance;
//  //@    ensures previousTransaction == -amount;
//
//  //@    also
//
//  //@    requires option == 3 && isValid(amount);
//  //@    requires isValid(balance, amount);
//  //@    ensures balance == \old (balance) - amount;
//  //@    ensures previousTransaction == -amount;
//
//  //@    also
//
//  //@    requires option == 3 && isValid(amount);
//  //@    requires !isValid(balance, amount);
//  //@    requires isValid(balance, 50);
//  //@    ensures balance == \old (balance) - 50;
//  //@    ensures previousTransaction == -50;
//
//  //@    also
//
//  //@    requires option == 3 && isValid(amount);
//  //@    requires !isValid(balance, amount);
//  //@    requires !isValid(balance, 50);
//  //@    ensures balance == 0;
//  //@    ensures previousTransaction == \old (-balance);
//
//  //@    also
//
//  //@    requires option == 4;
//  //@    ensures \result == previousTransaction;
//
//  //@    also
//
//  //@    old int _amount =  amount + (amount/100)*5;
//  //@    requires option == 5;
//  //@    requires _amount <= Integer.MAX_VALUE;
//  //@    requires isValid(_amount);
//  //@    requires isValid(balance, _amount);
//  //@    ensures balance == \old (balance) - _amount;
//  //@    ensures previousTransaction == -_amount;
//
//  //@    also
//
//  //@    old int _amount =  amount + (amount/100)*5;
//  //@    requires option == 5;
//  //@    requires _amount <= Integer.MAX_VALUE;
//  //@    requires isValid(_amount);
//  //@    requires !isValid(balance, _amount);
//  //@    ensures balance == \old (balance);
//  //@    ensures previousTransaction == \old (previousTransaction);
//
//  //@    also
//
//  //@    old int _amount =  amount - (amount/100)*2;
//  //@    requires option == 6 && isValid(_amount);
//  //@    requires isValid(balance, _amount);
//  //@    ensures balance == \old (balance) - _amount;
//  //@    ensures previousTransaction == -_amount;
//
//  //@    also
//
//  //@    old int _amount =  amount - (amount/100)*2;
//  //@    requires option == 6 && isValid(_amount);
//  //@    requires !isValid(balance, _amount);
//  //@    ensures balance == \old (balance);
//  //@    ensures previousTransaction == \old (previousTransaction);
//
//  //@    also
//
//  //@    old int _amount =  amount - (amount/100)*5;
//  //@    requires option == 7 && isValid(_amount);
//  //@    requires _amount + balance <= Integer.MAX_VALUE;
//  //@    ensures balance == \old (balance) + _amount;
//  //@    ensures previousTransaction == _amount;
//
//  //@    also
//
//  //@    requires option == 8 && balance <= 20000;
//  //@    ensures \result == balance/100;
//
//  //@    also
//
//  //@    requires option == 8 && 20000 < balance && balance <= 160000;
//  //@    ensures \result == (balance/100)*2;
//
//  //@    also
//
//  //@    requires option == 8 && 160000 < balance && balance <= 300000 ;
//  //@    ensures \result == (balance/100)*3;
//
//  //@    also
//
//  //@    requires option == 8 && 300000 < balance && balance <= Integer.MAX_VALUE;
//  //@    ensures \result == (balance/100)*4;
//
//  //@    also
//
//  //@    requires option == 9;
//  //@    old int ATMpenalty = 4;
//  //@    requires amount + ATMpenalty <= Integer.MAX_VALUE;
//  //@    requires isValid(amount);
//  //@    requires 0 <= balance - amount + ATMpenalty;
//  //@    requires isValid(balance, (amount + ATMpenalty));
//  //@    ensures balance == \old (balance) - (amount + ATMpenalty);
//  //@    ensures previousTransaction == -(amount + ATMpenalty);
//
//  //@   also
//
//  //@   requires option == 0;
//  //@   ensures balance == \old (balance);
//  //@   ensures previousTransaction == \old (previousTransaction);
//  //@ |}
//  int menu(int option, int amount) {
//    int result;
//    result = 0;
//
//    switch (option) {
//    case 1:
//      deposit(amount);
//      result = getBalance();
//      break;
//
//    case 2:
//      withdraw(amount);
//      result = getBalance();
//      break;
//
//    case 3:
//      checkWithdrawal(amount);
//      result = getBalance();
//      break;
//
//    case 4:
//      result = getPreviousTransaction();
//      break;
//
//    case 5:
//      foreignTransfer(amount);
//      result = getBalance();
//      break;
//
//    case 6:
//      withdrawByCashBack(amount);
//      result = getBalance();
//      break;
//
//    case 7:
//      foreignDeposit(amount);
//      result = getBalance();
//      break;
//
//    case 8:
//      result = interestAfterYear();
//      break;
//
//    case 9:
//      ATMWithdraw(amount);
//      result = getBalance();
//      break;
//
//    default:
//      result = getBalance();
//      break;
//    }
//    return result;
//  }
}
