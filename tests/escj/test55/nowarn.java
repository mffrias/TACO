package escj.test55;

class C_nowarn {
	public int i;
}

class D {
	int m(C_nowarn c) {
		return c.i; //@ nowarn Null;
	}
}
