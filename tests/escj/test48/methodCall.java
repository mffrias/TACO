package escj.test48;

class C_call {

	C_call() {
	}

	int m(int i)
	//@ ensures \result == 0;
	{
		try {
			n(i);
			return 0;
		} catch (Exception e) {
			return 1;
		}
	}

	void n(int i) throws Exception {
		throw new Exception();
	}
}
