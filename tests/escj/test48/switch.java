package escj.test48;

class C_switch {
    
    C_switch() {}

    static int m(int i)
    //@ ensures \result == 0
    {
	int x = 1;
	
	switch (i) {
	case 1:
	    return 0;
	case 2:
	    x = 0;
	case 3:
	    return x;
	default:
	    return 0;
	}
    }

    static int m2(int i)
    //@ ensures \result == 0
    {
	int x = 1;
	
	switch (i) {
	case 1:
	    return 0;
	case 2:
	case 3:
	    x = 0;
	    return x;
	default:
	    break;
	}
	return 1;
    }
}
