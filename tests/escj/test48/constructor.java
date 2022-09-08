package escj.test48;

class C_ctor {
    
	C_ctor() throws Exception 
    {
	throw new Exception();
    }

    int m(int i)
    //@ ensures \result == 0;
    {
	try {
		C_ctor c = new C_ctor();
	    return 0;
	}
	catch (Exception e) {
	    return 1;
	}
    }
}
