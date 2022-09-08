package escj.test58_p1;

interface A {
	//@ ghost instance public int a;
	//@ invariant 10 <= a;
}

interface B {
	//@ ghost instance public int b;
	//@ invariant 10 <= b;
}

interface C {
	//@ ghost instance public int c;
	//@ invariant 10 <= c;
}

interface D {
	//@ ghost instance public int d;
	//@ invariant 10 <= d;
}

class Super implements A, B {
}

class Body extends Super implements A, C {
	int k;

	//@ invariant 10 <= k;

	void m() {
		//@ set a = a;
		//@ set b = b;
		//@ set c = c;
		if (this instanceof D) {
			D d = (D) this;
			//@ set d.d = d.d;
		}
		k = k;
		if (this instanceof Sub) {
			Sub sub = (Sub) this;
			sub.s = sub.s;
		}
	}

	Body() {
		if (this instanceof Sub) {
			Sub sub = (Sub) this;
			//@ assert 10 <= sub.s;  // should fail, since invariant is declared in
			// Sub, Sub is a subclass of Body, and Body != Sub
		}
	} //@ nowarn Invariant;

	Body(int q) {
		//@ assert 10 <= k;  // should fail, since no sibling is called
	} //@ nowarn Invariant

	Body(double q) {
		this((int) q);
		//@ assert 10 <= k;  // should succeed, since invariant is declared in Body
		// and a sibling constructor is called
	} // all invariants should pass, too

	Body(int q0, int q1) {
		this(q0 + q1);
		if (this instanceof D) {
			D thisD = (D) this;
			//@ assert 10 <= thisD.d;  // should fail, since the invariant is declared in
			// interface D, which is not a superinterface of Body,
			// and this Body constructor calls a sibling
		}
	} //@ nowarn Invariant;

	Body(int q0, int q1, int q2) {
		if (this instanceof C) {
			C thisC = (C) this;
			//@ assert 10 <= thisC.c;  // should fail, since C is a superinterface of
			// Body, but not a superinterface of Body's superclass,
			// and this Body constructor does not call a sibling
		}
	} //@ nowarn Invariant;

	Body(double q0, int q1) {
		//@ assert 10 <= this.a;
		//@ assert 10 <= this.b;
	} //@ nowarn Invariant;
}

class Sub extends Body implements D {
	int s;
	//@ invariant 10 <= s;
}
