package escj.test48;

class C_cont {
    
	C_cont() {}

    static int m(int x)
    //@ ensures \result == 0
    {
	for(int i=0; i < x; i++) {
	    continue;
	}
	return 1;
    }
}
