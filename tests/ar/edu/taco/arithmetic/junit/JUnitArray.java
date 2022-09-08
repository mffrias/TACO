package ar.edu.taco.arithmetic.junit;

public class JUnitArray {

	/**
	 * 
	 * @Ensures return==true;
	 * @return
	 */
	public boolean array(int[] a) {
		boolean result;
		if (a!=null && a.length==3){
		    int val = a[1];
		    if (val!=0) {
		      result=false;
		    } else {
			  result= true;
		    }
	    }else
			result= true;
		return result;
	}
}
