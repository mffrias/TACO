package escj.test04;

public class C {

    //@requires true;
    //@ensures true;    
    public static void test1() {
	int x;
	if (false) {
	    x = 1;
	} else {
	    x = 2;
	}

	//@ assert x==2;
    }

    //@requires true;
    //@ensures true;    
    public static void test2() {
	int x = 0;
	x = x + 1;
	if (true)
	    x = x + 2;
	if (false)
	    x = x + 4;
	//@ assert x==3;
    }

    //@requires true;
    //@ensures true;
    public static void test03() {
	//failed becouse x can't be null (JML default is non_null)
	int[] x = null;
	//@ assert x instanceof Object;
    }

    //@requires true;
    //@ensures true;    
    public static void test04() {
	int[] x = new int[3];
	//@ assert x instanceof Object;
    }
}
