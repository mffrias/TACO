package escj.test95;

class Ghost95 {
	//@ ghost public int[] a;
	//@ ghost public boolean b;

	//@ invariant b && (b == true); 
	//@ invariant  a.length == 2;
	//@ invariant (\forall int i; 0 <= i && i < a.length ==> a[i] == 0);

	Ghost95() {
		int[] ok = { 0, 0 };
		//@ set a= ok; 
		//@ set b= true;
	}

	void m() {
		//@ assert (a.length == 2) && b;

	}
}
