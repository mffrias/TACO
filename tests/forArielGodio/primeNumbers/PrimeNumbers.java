package forArielGodio.primeNumbers;

// 105,102,357 prime numbers exist between 1 to Integer.MAX_VALUE. Also, the Integer.MAX_VALUE is a prime number.
public class PrimeNumbers {
  //div-int,int-
  //@ requires 2 <= n;
  //@ requires 2 <= d;
  //@ ensures \result == true <==> n%d == 0;
  public static boolean div(int n, int d) {
    //@ assert d != 0;

    return n % d == 0;
  }

  private /*@ spec_public nullable @*/ int primeArray[];
  //primeList-int-
  //+PREV@ requires n != 2;
  //@ requires 0 < n && n < 7;
  //@ ensures primeArray[0] == 2;
  //@ ensures (\forall int i,j; 0 <= i && i < primeArray.length && 0 <= j && j < primeArray.length; i + 1 == j ==> primeArray[i] < primeArray[j]);
  //@ ensures (\forall int i,j; 0 <= i && i < primeArray.length && 0 <= j && j < primeArray.length; i + 1 == j ==> !(\exists int k; primeArray[i] < k && k < primeArray[j]; (\forall int l; 2 <= l && l <= k/2; k % l != 0)));
  //@ ensures (\forall int i; 0 <= i && i < primeArray.length; (\forall int j; 2 <= j && j <= primeArray[i]/2; primeArray[i] % j != 0));
  //@ ensures primeArray.length == n && \result == primeArray;
  //@ signals (RuntimeException e) false;
  public int[] primeList(int n) {
    int status = 1, num = 3;
    primeArray = new int[n];
    primeArray[0] = 2;

    // decreasing n - count;

    for (int count = 2; count <= n;) {

      // decreasing num - j;

      for (int j = 2; j <= num / 2; j++) {
        if (div(num, j)) {
          status = 0;
          break;
        }
      }

      if (status != 0) {
        primeArray[count - 1] = num;
        count++;
      }
      status = 1;
      for (int j = 2; j <= num / 2; j++) {
        if (div(num, j)) {
          status = 0;
          break;
        }
      }
    }
    return primeArray;
  }
  
//  public static void main( String[] args) {
//	  PrimeNumbers pn = new PrimeNumbers();
//	  int[] primes = pn.primeList(3);
//      int i = 0;
//  }
}
