package forArielGodio.bubbleSort.bug01;
import forArielGodio.bubbleSort.bug01.SwapInArray;

public class BubbleSort {

	public static void main(String[] args){
		System.out.println(p(5));
	}

	public static int p(int n){
		if (n <= 2)
			return n;
		else {
			if (n % 2 == 0){
				return 3*p(n-1);
			} else {
				return p(n-2) + 3;
			}
//			return p(n-2) + 4;
		}
	}
	
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
	

//	public static void main(String[] args) {
//			P(4);
//	}


	public static void P(int n){
		if (n == 0){
			System.out.print("Pe!");
		} else {
			if (n > 3) {
				System.out.print("Oh ");
			}
			System.out.print("Pedro, ");
			P(n-1);
			if (n > 5) {
				System.out.print("I walk on water,");
				P(n - 1);
			} else {
				if (n < 0){
					System.out.print("But I ain't no Jesus");
					P(n + 1);
				}
			}
		}
	}
}