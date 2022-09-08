package forArielGodio;
public class SwapInArray {
    /*@ requires 0 <= x && x < array.length && 0 <= y && y < array.length; 
	  @ ensures \old(array[x]) == array[y] && \old(array[y]) == array[x];
	  @ ensures array.length == \old(array.length); @*/
	public void swap(int x, int y,  int array[]) {
	  int temp;     
          temp = array[x];
          array[x] = array[y];
          array[y] = temp;
       }
    }