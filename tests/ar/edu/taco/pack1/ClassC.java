package ar.edu.taco.pack1;

import ar.edu.taco.pack2.ClassD;

public class ClassC {

	public /*@ nullable @*/ ClassC f;
	
	//@ ensures \result==true;
	public boolean show_instance(ClassD my_clazzD) {
		
		if (my_clazzD.f==null)
			return true;
		 
		if (this.f==null)
			return true;
		
		int[] array = new int[10];
		
		int int_ret_val = ClassD.static_method_with_array(array);
		
		return false;
	}
}
