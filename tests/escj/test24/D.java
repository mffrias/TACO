package escj.test24;

class D {

    static int m()
	//@ ensures \result == 0;
	{
	    return 0;
	}
}
