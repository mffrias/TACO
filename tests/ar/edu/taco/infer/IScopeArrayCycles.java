package ar.edu.taco.infer;

public class IScopeArrayCycles {

	public IScopeArrayCycles() {}
	
	public /*@ nullable @*/ IScopeArrayCycles next;
	
	public /*@ nullable @*/ Object value; 

	public /*@ nullable @*/ Object[] value_array; 

	public int counter;
	
	public static IScopeArrayCycles static_reference;
	
	//@ ensures \result==true;
	public  boolean show_instance(int i, int j, float k, IScopeArrayCycles another, int[] int_array) {
		if (this.next==null)
			return true;
		
		if (this.value==null)
			return true;
		
		if (this.value_array==null)
			return true;
		
		if (another==null)
			return true;
		
		if (i!=j)
			return true;
		
		if (int_array==null)
			return true;
		
		return false;
	}
}
