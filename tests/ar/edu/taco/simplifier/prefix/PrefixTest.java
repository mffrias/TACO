package ar.edu.taco.simplifier.prefix;

public class PrefixTest {
	
	//@ ensures \result == 1;
	private int prefix1TestPass() {
		int b = 1;
		int x = b++;
		return x;
	}
	

	//@ ensures \result == 2;
	private int prefix2TestPass() {
		int x = 2;
		if (x++ > x) {
			return 1;
		} else {
			return 2;
		}	
	}
	
	//@ ensures \result == 4;
	private int prefix3TestPass() {
		int x = 2;
		if (x++>x++) {
			return 1;
		} else {
			return x;
		}
		
	}

	//@ ensures \result == 2;
	private int prefix4TestFail() {
		int x = 2;
		if (x==++x) {
			return 1;
		} else {
			return 2;
		}		
	}
	
	//@ ensures \result == 6;
	private int prefix5TestPass() {
		int x = 2;
		int y = 2;
		if (x==++x && y++==y) {
			return 1;
		} else {
			return x + y;
		}
		
	}
	

	
}
