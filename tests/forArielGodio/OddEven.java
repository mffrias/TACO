package forArielGodio;
public class OddEven {	
    	 //@ ensures \result <==>  x%2 == 0;
    	 //@ ensures !\result <==> x%2 != 0;
	 public /*@ pure @*/ boolean isEven(int x) { 
         	return x*2 == 0; // return x%2 == 0;
       	 } 

    	 //@ ensures !\result <==> x%2 == 0;
    	 //@ ensures \result <==>  x%2 != 0;
	 public /*@ pure @*/ boolean isOdd(int x) { 
         	return x%2 != 0;
       	 } 
}