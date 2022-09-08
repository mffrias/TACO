package ar.edu.taco.arithmetic.simple;

public class C2 {

	//@ ensures \result==true;
	public boolean m2(int x1, int x2, int x3, int x4, int x5, int x6, int x7, int x8, int x9, int x11, int x12, int x13, int x14, int x15, int x16, int x17,
			int x18, int x10) {
		boolean result;
		if ((((x1 < (1 * (x2 % (x3 / (2 + ((3 * (x4 / (x5 - 4))) * ((x6 * 5) + (((6 + x7) / x8) / (1))))))))) || (x9 == x10)) | (((2) < x11) & (x12 >= x13)))
				& ((x14 < x15) | ((x16 - x17) == x18))) {
			result = true;
		} else {
			result = false;
		}
		return result;
	}
}
