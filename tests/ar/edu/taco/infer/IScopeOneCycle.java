package ar.edu.taco.infer;

public class IScopeOneCycle {
	
	public IScopeOneCycle() {}
	
	public IScopeOneCycle next;
	
	public Object data;
	
	//@ ensures \result==true;
	public boolean show_instance() {
		if (next==null)
			return true;
		
		if (data==null)
			return true;
		
		return false;
	}

}
