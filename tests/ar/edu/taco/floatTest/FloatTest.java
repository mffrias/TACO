package ar.edu.taco.floatTest;

public class FloatTest {
	
	int att;

	public FloatTest() {
		// TODO Auto-generated constructor stub
	}


	
	
   /*@ requires true;
	 @ ensures \result == 7;
	 @*/
	public int add(){		
		Integer i = new Integer(7);
		int j = i.intValue();
		return j;
	}
	

	
	/*@ requires i == 6;
	 @ ensures \result == i;
	 @*/
	public int buggyVar(int i){
		String a = new String("abc");
		return a.length();
	}

}
