package ar.edu.taco.infer;

public class IScopeNoCycles {

	public int int_value_1;
	
	public int int_value_2;
	
	public long long_value;
	
	public /*@ nullable @*/ Object object_value;
	
	public boolean boolean_value;
	
	//@ ensures \result==true;
	public boolean show_instance(IScopeNoCycles that, Object another_object, long long_val) {
		if (that==null)
			return true;
		
		if (that.int_value_1==that.int_value_2)
			return true;
		
		if (this.long_value==long_val)
			return true;
		
		if (another_object==null)
			return true;
		
		return false;
	}
	
}
