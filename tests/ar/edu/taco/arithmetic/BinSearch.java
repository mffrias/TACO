package ar.edu.taco.arithmetic;

public class BinSearch {

	/**
	 * @Requires a!=null;
	 * @Ensures throw==null;
	 */
	public static int binarySearch(int[] a, int key) {
		if (a.length<0) {
			return -1;
		}
		/*
		if (a.length!=Integer.MAX_VALUE) {
			return -1;
		}
		*/
	
		int low = 0;
		int high = a.length - 1;
		int mid;
		int midVal ;
		
		while (low <= high)  {
			mid = (low + high) /2; // BUG
			//mid = low + ((high - low) / 2); // CORRECTION
			midVal = a[mid];

			if (midVal < key)
				low = mid + 1;
			else if (midVal > key)
				high = mid - 1;
			else {
				return mid; // key found
			}
		}
		
		return -(low+1);

	}
}
