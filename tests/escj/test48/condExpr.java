package escj.test48;

class C_condExpr {

	C_condExpr() {
	}

	static int m(int i)
	//@ ensures \result == 0;
	{
		int result;
		result = (i == 1 ? 1 : 0);
		return result;
	}
}
