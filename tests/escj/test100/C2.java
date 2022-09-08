package escj.test100;

class C2 {
	/*@ non_null */static int[] a = new int[10];
	/*@ non_null */static int[] b = new int[10];

	/*@ 
	  @ invariant (\forall int i; 0 <= i && i < b.length ==> b[i] != 0); 
	  @ */

	C2() {
		/*@ 
		  @ loop_invariant i >= 0;
		  @ loop_invariant (\forall int j; 0 <= j && j < i ==> b[j] != 0); 
		  @*/
		for (int i = 0; i < b.length; i++) {
			b[i] = 1;
		}

		//@ assert (\forall int i; 0 <= i && i < b.length ==> b[i] != 0); 
	}

	//@ ensures (\forall int i; 0 <= i && i < a.length ==> a[i] == 0); 
	void Zero() {
		/*@ 
		  @ loop_invariant i >= 0;
		  @ loop_invariant (\forall int j; 0 <= j && j < i ==> a[j] == 0); 
		  @ */
		for (int i = 0; i < a.length; i++) {
			a[i] = 0;
		}
	}

	//@ ensures (\forall int i; 0 <= i && i < a.length ==> a[i] == 0); 
	void ZeroBad1() {
		for (int i = 0; i < a.length; i++) {
			a[i] = 0; // warning expected 
		}
	} // warning expected 

	void ZeroBad2() {
		/*@ 
		  @ loop_invariant i >= 0;
		  @ loop_invariant (\forall int j; 0 <= j && j < i ==> a[j] == 0); 
		  @*/
		for (int i = 0; i < a.length; i++) {
			a[i] = b[i];
			a[i] = 0;
			a = a;
		}
	} // warning expected

}
