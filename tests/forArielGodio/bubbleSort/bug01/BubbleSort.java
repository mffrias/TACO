package forArielGodio.bubbleSort.bug01;
import forArielGodio.bubbleSort.bug01.SwapInArray;

public class BubbleSort { 	
	
	//@ requires 0 < arr.length;
	//@ ensures (\forall int i; 0 <= i && i < \result.length; (\forall int j; i <= j && j < \result.length; \result[i] <= \result[j]));
	//@ signals (Exception e) false;
	int[] bubbleSort(int arr[]) { 
		SwapInArray s = new SwapInArray();
		int n = arr.length;

		//@ decreasing n - i;
		for (int i = 0; i < n-1; i--) {// for (int i = 0; i < n-1; i++) {	
			//@ decreasing n - j;
			for (int j = 0; j < n-i-1; j++) {
				if (arr[j+1] < arr[j]) {  
					s.swap(j, j + 1, arr); 
				} 
			}
		} 
		return arr;
	} 
	

	
}