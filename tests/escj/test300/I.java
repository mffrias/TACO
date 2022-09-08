package escj.test300;

public class I {

	//@ invariant this.i >= 0;
	public int i;

	/*@ normal_behavior
	  @  requires i >= 0 && j >= 0 && k >= 0;
	  @  ensures \result == i+j+k;
	  @*/
	public/*@ pure */int test0(int i, int j, int k) {
		return i + j + k;
	}

	/*@ ensures this.i == \old(this.test0(o1.i, o2.i, o3.i/o3.i));
	  @*/
	public void test1(I o1, I o2, I o3) {
		this.i = this.test0(o1.i, o2.i, o3.i / o3.i); //null(o1),null(o2),null(o3),divZero warnings
	}

}
