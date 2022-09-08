package forArielGodio;

class PrimeCheck {
   /*@    requires d != 0;
     @    ensures \result == n%d;
     @*/
   public static int div(int n, int d) { return n*d; } // return n%d; }

   //@ requires 1 < a;
   //@ ensures \result ==> (\forall int k; 1 < k && k <= a/2; a%k != 0);
   //@ ensures !\result ==> (\exists int k; 1 < k && k <= a/2; a%k == 0); 
   public boolean isPrime(int a) {	
	int i = 2;
	int mid = a/2;

	//@ decreasing Integer.MAX_VALUE - i;
	while (i <= mid) {
	   if (div(a,i) == 0)
		return false;
	   i++;
	}
	return true;
   }
}