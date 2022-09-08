package roops.core.objects;

public class arithmFunny {

    /*@ requires true;
    @  ensures \result == 0;
    @*/
   public int arith(int x, int y){
   	if (x*x + y*y >=0){
   		return 0;
   	} else {
   		return 1;
   	}
   	
   }
	
	
}
