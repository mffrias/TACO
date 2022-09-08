package escj.test07;

public class inc {
	public void m1() {
		int x = 0;
		x++;
		//@ assert x == 1;
	}

	public void m2() {
		int x = 0;
		++x;
		//@ assert x == 1;
	}

	public void m3() {
		int x = 0;
		x--;
		//@ assert x == -1;
	}

	public void m4() {
		int x = 0;
		--x;
		//@ assert x == -1;
	}

	public void m5() {
		int x = 0;
		int y = x++;
		//@ assert y == 0;
	}

	public void m6() {
		int x = 0;
		int y = ++x;
		//@ assert y == 1;
	}

	public void m7() {
		int x = 0;
		int y = x--;
		//@ assert y == 0;
	}

	public void m8() {
		int x = 0;
		int y = --x;
		//@ assert y == -1;
	}
}
