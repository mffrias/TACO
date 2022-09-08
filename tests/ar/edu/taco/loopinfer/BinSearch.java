package ar.edu.taco.loopinfer;

public class BinSearch {

	// requires a!=null;
	// requires (\forall  int i, j ; 0<=i && i<j && j<a.length ; a[i]<=a[j]);
	// ensures \result>=-1 && \result < a.length;
	public int bin_search(int[] a, int key) {
		int low = 0;
		int high = a.length;
		int result =-1;

		boolean exit_while = false;
		while (low < high && !exit_while) {
			
			int mid = low + (high - low) / 2;
			int midVal = a[mid];

			if (midVal < key) {
				low = mid + 1;
			} else if (key < midVal) {
				high = mid;
			} else {
				result = mid; // key found
				exit_while = true;
			}
		}
		// key not present
		return result;
	}

}
