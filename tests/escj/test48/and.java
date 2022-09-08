package escj.test48;

class C_and {

	C_and() {
	}

	static int m(int i, int j)
	//@ ensures \result == 0;
	{
		boolean b = (j == 2) && (i == 1);
		return i;
	}
}
