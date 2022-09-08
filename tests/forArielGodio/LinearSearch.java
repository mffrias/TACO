package forArielGodio;


public class LinearSearch {
	private static int location;
	//@ ensures \result == -1 <==>  (\forall int i; 0 <= i && i < array.length; array[i] != search);
	//@ ensures \result != -1 ==> 0 <= \result && \result < array.length && array[\result] == search; //alt
	//@ ensures (\forall int i; 0 <= i && i < array.length; array[i] == \old(array)[i]);
	//@ signals (Exception e) false;
	public static int linearSearch(int search, int array[]) {
		int c;
		for (c = 0; c < array.length; c++) { // for (c = 0; c < array.length; c++)
			if (array[c] == search) {
				location = c;
				break;
			}
		}
		if (c == array.length) {
			location = -1;
		}
		return location;
	}


	public static void main(String[] args) {
        int search = 0;
        int[] array = new int[0];
        // Parameter Initialization

        
        int i = linearSearch(search, array);
        System.out.println(i);

	}
}
