package edu.utep.ds2;
public class BinarySearch {




	/*@
	@ requires arr != null;
	@ ensures \result <==> (\exists int i; 0 <= i && i < length(arr); arr[i] == val); @*/
	public boolean bs(int val, int[] arr) {
		// 1. Handle edge cases
		if (arr == null || arr.length == 0) {
			return false;
		}

		int left = 0;
		int right = arr.length - 1;

		// 2. Loop while the search interval is valid
		while (left <= right) {
			// Calculate mid to avoid integer overflow
			int mid = left + (right - left) / 2;

			if (arr[mid] == val) {
				return true; // Target found
			} else if (arr[mid] < val) {
				left = mid + 1; // Target is in the right half
			} else {
				right = mid - 1; // Target is in the left half
			}
		}

		// 3. Target was not found
		return false;
	}

	//@ requires true;
	//@ pure
	public int length(int[] arr ){
		return arr.length;
	}

}