package escj.test48;

class C_or {

	C_or() {
	}

	static int m(int i, int j)
	//@ ensures \result == 0;
	{
		boolean b = (i == 1) || (j == 2);
		return i;
	}

	static boolean m2(int i, int j)
	//@ requires i != 1;
	//@ ensures \result == false;
	{
		boolean b = (i == 1) || (j == 2);
		return b;
	}
}
