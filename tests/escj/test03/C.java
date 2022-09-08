package escj.test03;

public class C {
	
	//@requires true;
	void test0() {
		// skip
	}

	//@requires true;
	void test1() {
		int x = 0;
		//@ assert x == 0;
	}

	//@requires true;	
	void test2() {
		int x = 1;
		//@ assert x == 0; // ERROR
	}

	//@requires true;
	void test3() {
		int x = 0;
		//@ assume x == 0;
	}

	//@requires true;
	void test4() {
		int x = 0;
		//@ assume x == 0;
	}

	//@requires true;
	void test5() {
		int x = 0;
		//@ assert x == 1; // ERROR
	}

	//@requires true;
	void test6() {
		int x = 0;
		//@ assume x == 1;
	}

	//@requires true;
	void test7() {
		int x = 0;
		//@ assume (\forall int i; i>0);
		//@ assume (\forall C c; c == c);
	}
}
