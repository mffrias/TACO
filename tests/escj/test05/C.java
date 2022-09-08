package escj.test05;

public class C {
    
    //@requires true;
    //@ensures true;        
	public static void test1() {
		int x = 1;
		x = x + x;
		x = x + x;
		x = x + x;
		x = x + x;
		x = x + x;
		x = x + x;
		x = x + x;
		x = x + x;
		x = x + x;
		x = x + x;
		x = x + x;
		x = x + x;
		//@ assert x==4096;
	}
}
