package escj.test100;

class C1 {
	/*@ non_null */int[] a;
	/*@ non_null */int[] b;

	/*@ invariant (\forall int i; 0 <= i && i < b.length ==> b[i] != 0); */

	C1() {
		/*@ 
		  @ loop_invariant i >= 0;
		  @ loop_invariant (\forall int j; 0 <= j && j < i ==> b[j] != 0);
		  @ loop_invariant (\forall int [] z; z != b ==> (\forall int j; z[j] == \old(z[j])));
		  @*/
		for (int i = 0; i < b.length; i++) {
			b[i] = 1;
		}
	}

	/*@ ensures (\forall int i; 0 <= i && i < a.length ==> a[i] == 0); */
	void Zero() {
		b = b;
		/*@ 
		  @ loop_invariant i >= 0;
		  @ loop_invariant (\forall int j; 0 <= j && j < i ==> a[j] == 0); 
		  @*/
		for (int i = 0; i < a.length; i++) {
			a[i] = 0;
		}
	}

	/*@ ensures (\forall int i; 0 <= i && i < a.length ==> a[i] == 0); */
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
			a[i] = 0;
			a = a;
		}
	} // warning expected

}
