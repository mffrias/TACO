package escj.test20B;

public class A extends B {
	public A() {
	}

	public Object m_result1() {
		return null; // warning: NonNullResult
	}

	public Object m_result2(/*@nullable*/Object o) {
		return o; // warning: NonNullResult
	}

	public Object m_result3() {
		return "";
	}

	// overrides
	public /*@non_null*/Object m_result4() { // warning: nullable ignored
		return "";
	}

	public /*@nullable*/Object m_result5() {
		return null;
	}

	public int m_result_ok() {
		return 0;
	}

	public Object m_param_result1(Object o) {
		return o;
	}

	public void m_param1(int i) {
		i++;
	}

}
