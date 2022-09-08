package escj.test304;

public class G {

	/*@non_null */public int[] a;

	/*@ requires a.length >= 0;  // null(a) warning
	  @ ensures this.a == a;
	  @*/
	public G(int[] a) {
		this.a = a;
	}

	/*@ requires a.length == this.a.length;
	  @ ensures this.a[i] == \old(this.a[i]+v);
	  @*/
	public void test0(int i, int v) {
		this.a[i] += v; // idxNeg(i), idx2Large(i)
	}

	/*@ requires a.length == this.a.length;
	  @ requires i>=0 && i<this.a.length;
	  @ ensures this.a[i] == \old(this.a[i]+v);
	  @*/
	public void test1(int i, int v) {
		this.a[i] += v; // idxNeg(i), idx2Large(i)
	}

}
