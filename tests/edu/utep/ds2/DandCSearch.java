package edu.utep.ds2;
public class DandCSearch {
	//@ requires lo == 0 && hi == array.length - 1;
	//@ ensures \result == -1 <==> (\forall int i; 0 <= i && i < array.length; array[i] != target);
	//@ ensures \result != -1 <==> (0 <= \result && \result < array.length && array[\result] == target);
	//@ signals (Exception e) false;
	public static int search(int[] array, int target, int lo, int hi) {
		if (lo > hi) {
			return -1; // Target not found
		}

		// Find the middle index
		int mid = (lo + hi) / 2;

		// Check if target is present at mid
		if (array[mid] == target) {
			return mid;
		}

		// Recur for the left half if target is smaller than mid
		if (target < array[mid]) {
			return search(array, target, lo, mid - 1);
		}

		// Recur for the right half if target is greater than mid
		return search(array, target, mid + 1, hi);
	}

	public static void main(String[] args) {
		int[] array = new int[2];
		int target = 0;
		int lo = 0;
		int hi = 1;
		// Parameter Initialization
		array[0] = 3;
		array[1] = 0;
		int i = search(array, target, lo, hi);
		System.out.println(i);
	}


}