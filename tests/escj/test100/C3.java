package escj.test100;

class C3 {
	int y;
	int z;

	void Foo() {
		int x = 0;
		/*@ 
		  @ loop_invariant y == \old(y); 
		  @ loop_invariant x == \old(x);
		  @ loop_invariant (\forall C3 o; o != this ==> o.z == \old(o.z)); 
		  @ loop_invariant (\forall C3 o; o.y == \old(o.y)); 
		  @ loop_invariant (\forall C3 o; o.z == \old(o.z)); 
		  @*/
		while (true) {
			y = y + 1 - 1;
			x = x + 1 - 1;
			z = z + 1;
		}
	}

	void Goo() {
		C3 x;
		//@ assume (\forall C3 a; a.y == a.z); 
		/*@ 
		  @ loop_invariant (\forall C3 a; !\fresh(a) ==> a.y == a.z); 
		  @*/
		while (true) {
			x = new C3();
			x.y = 1;
			x.z = 0;
		}
	}

	void Loo() {
		C3 x;
		//@ loop_invariant (\forall C3 a; \fresh(a) ==> a.y == a.z); 
		while (true) {
			x = new C3();
			x.y = 0;
			x.z = 0;
		}
	}

	//@ ensures (\forall C3 a; \fresh(a) ==> a == this); 
	public C3() {
	}
}
