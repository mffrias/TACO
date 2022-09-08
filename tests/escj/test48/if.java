package escj.test48;

class C_if {
    
    C_if() {}

    static int m(int i)
    //@ ensures \result == 0
    {
	if (i == 1) {
	    return 0;
	}
	else if (i == 2) {
	    return 0;
	}
	else {
	    return 2;
	}
    }
}
