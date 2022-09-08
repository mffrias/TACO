package escj.test28;

class O124 {
	/* tests invariant optimizations O1, O2 and O4. All are tested in by
	   the GC for the chk() methods.
	
	   after call, assumption is forall s. (Jpre[s] || allocated[s]) => Jpost[s]
	   
	   O1. if vars of inv are not modified, drop Jpre[s]
	   O2. if fresh is not mentioned in spec and not constructor call,
	       don't bump alloc, and drop "allocated[s]"

	   O4. at end of body only assert invariants whose vars are modified
	*/

	int i, j;

	//@ invariant i>0;
	//@ invariant j>0;

	O124() {
		i = 1;
		j = 1;
	}

	//@ modifies i;
	void modi() {
		i = 2;
	}

	void chk() {
		modi();
	}
}
