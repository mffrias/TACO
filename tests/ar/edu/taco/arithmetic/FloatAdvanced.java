package ar.edu.taco.arithmetic;

public class FloatAdvanced {

	/**
	 * @Ensures false;
	 */
	public void Method1_goal0(float i) {
		boolean reach;
		if (i == i) {
			reach = false;
		} else {
			reach = true;
		}
	}

	/**
	 * @Ensures false;
	 */
	public void Method1_goal1(float i) {
		boolean reach;
		if (i == i) {
			reach = true;
		} else {
			reach = false;
		}
	}

	/**
	 * @Ensures false;
	 */
	public void Method2(float i, float j) {
		boolean reach;
		float temp1 = i * j;
		float temp2 = i * j;
		if (temp1 <= temp2) {
			reach = false;
		} else {
			reach = true;
		}
	}

	/**
	 * @Ensures false;
	 */
	public void Method3(float i, float j) {
		boolean reach;
		float temp1 = i / j;
		float temp2 = i / j;
		if (temp1 <= temp2) {
			reach = false;
		} else {
			reach = true;
		}
	}

	/**
	 * @Ensures false;
	 */
	public void Method4(float i) {
		boolean reach;
		float tmp1 = i + 1.0f;
		boolean tmp2 = (tmp1 == i);
		boolean tmp3 = (i <= 3.4028235E38f);
		if (tmp2 == true && tmp3 == true) {
			reach = false;
		} else {
			reach = true;
		}
	}

	/**
	 * @Ensures false;
	 */
	public void Method5(float i) {
		boolean reach;
		float temp1 = 2 * i;
		boolean temp2 = (temp1 == i);
		boolean temp3 = (i > 0f);

		if (temp2 && temp3) {
			reach = false;
		} else {
			reach = true;
		}
	}

	/**
	 * @Ensures false;
	 */
	public void Method6(float x1) {
		boolean reach;
		float tmp1 = 3.4028235E38f - x1;
		if (0.0f == (tmp1)) {
			reach = false;
		} else {
			reach = true;
		}
	}

	/**
	 * @Ensures false;
	 */
	public void Method7(float x1, float x2, float x3, float x4) {

		boolean reach;

		float tmp1 = x4 + 237.76f;
		float tmp2 = x3 / 1.9799f;
		float tmp3 = x1 - x2;
		float tmp4 = tmp2 - 19.839f;
		float tmp5 = tmp4 * tmp1;
		boolean tmp7 = 1713.6f >= tmp3;
		boolean tmp8 = tmp5 >= 194378.3f;
		if (tmp7 || tmp8) {
			reach = false;
		} else {
			reach = true;
		}
	}

	/**
	 * @Ensures false;
	 */
	public void Method8(float x1, float x2, float x3, float x4, float x5,
			float x6, float x7) {

		boolean reach;

		float tmp1 = 114.51f * x1;
		float tmp2 = x3 - x4;
		float tmp3 = x2 / tmp2;
		float tmp4 = tmp1 * tmp3;
		boolean tmp5 = tmp4 < x5;
		boolean tmp6 = x6 <= x7;
		
		if (tmp5 && tmp6) {
			reach = false;
		} else {
			reach = true;
		}
	}

}
