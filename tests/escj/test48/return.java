package escj.test48;

class C_ret {

	C_ret() {
	}

	static int m(boolean b)
	//@ ensures \result == 0
	{
		if (b)
			return 1;
		else
			return 0;
	}

	static int n(boolean b)
	//@ ensures \result == 0
	{
		try {
			if (b)
				return 0;
			else
				return 1;
		} finally {
			return 2;
		}
	}
}
