package edu.utep.ds2;
public class BinarySearch {
	//@ requires (\forall int j; 0 <= j && j < arr.length; (\forall int i; 0 <= i && i < j ;arr[i] <= arr[j]));
	//@ ensures \result == false <==> (\forall int i; 0 <= i && i < arr.length; arr[i] != key);
	//@ signals (Exception e) false;
	public static boolean binary(int[] arr, int key) {
		int lo = 0;
		int hi = arr.length-1;
		int middle;
		//@ decreasing hi - lo;
		while (lo <= hi) {
			middle = (lo + hi) / 2;
			if (arr[middle] == key) {
				return true;
			} else {
				if (arr[middle] > key) {
					hi = middle - 1;
				} else 
					lo = middle + 1;
			}
		}
		return false;
	}


	public static void main(String[] args) {
	    int[] arr = new int[1074999999];
	    int key = 1009668096;
	    // Parameter Initialization
	    arr[0] = -1939075073;
	    arr[1] = -1939075073;
	    arr[2] = -969537536;
	    binary(arr, key);
	}
}