package ar.edu.itba.complex;

public class Complex {
	public static Complex I = new Complex(0.0f, 1.0f);
	public static Complex NaN = new Complex(0.0f / 0.0f, 0.0f / 0.0f);
	public static Complex INF = new Complex(1.0f / 0.0f, 1.0f / 0.0f);
	public static Complex ONE = new Complex(1.0f, 0.0f);
	public static Complex ZERO = new Complex(0.0f, 0.0f);

	public float imaginary;
	public  float real;
	public   boolean isNaN;
	public   boolean isInfinite;

	/*@ ensures true; @*/
	public Complex(float real, float imaginary) {
		this.real = real;
		this.imaginary = imaginary;

		isNaN = !(real == real) || !(imaginary == imaginary);
		isInfinite = !isNaN &&
				(real == 1.0f/0.0f || real == -1.0f/0.0f || imaginary == 1.0f/0.0f || imaginary == -1.0f/0.0f);
	}


	/*@ requires real == -1.0f/0.0f; 
	 @ ensures false;
	 @*/
	
	public Complex reciprocal() {
		if (isNaN) {
			return NaN; //mutGenLimit 1
		}
		return null;

//		if (real == 0.0f && imaginary == 0.0f) {
//			return NaN; //mutGenLimit 1
//		}
//
//		if (isInfinite) {
//			return ZERO; //mutGenLimit 1
//		}
//
//		if (abs(real) < abs(imaginary)) {
//			float q = real / imaginary;
//			float scale =  1.0f / (real * q + imaginary);
//			return createComplex(scale * q, 0.0f - scale);
//		} else {
//			float q = imaginary / real;
//			float scale = 1.0f / (imaginary * q + real);
//			return createComplex(scale, (0.0f - scale) * q);
//		}
	}


	public Complex createComplex(float realPart,
			float imaginaryPart) {
		return new Complex(realPart, imaginaryPart);
	}

	public static float abs( float x) {
		if (x < 0.0f)
			return 0.0f-x;
		else
			if (x == 0.0f)
				return 0.0f;
			else 
				return x;
		//        return (x < 0.0f) ? -x : (x == 0.0f) ? 0.0f : x;
	}


}
