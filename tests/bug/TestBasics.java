package bug;

public class TestBasics {

	
	public int[] v;
	
	
	//@ requires true;
	//@ ensures true;
	public void test9(int[] w){
		/*@loop_invariant i>=0;@*/
		//@loop_invariant (\forall int j; 0<=j && j<i; w[i] == 0);
		for(int i=0; i<w.length; i++){
			w[i]=0;
		}
	}
}
