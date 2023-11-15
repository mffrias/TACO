package bug;

public class PruebaDecreasing {



	public static boolean div(int n, int d) {

		return n * d == 0;
	} 

	
	public /*@ nullable @*/ int primeArray[];

	
	
	//@ requires true;
	//@ ensures true;
	public int[] primeList(int n) {
		int status = 1;
		int num = 3; 
		primeArray = new int[n];
		primeArray[0] = 2;

		//@ decreasing 15 - count;
		for (int count = 2; count <= n; count++) {

			//@ decreasing num - j;
			for (int j = 2; j <= num / 2; j++) {
				if (div(num, j)) {
					status = 0;
					break;
				}
			}

			if (status != 0) {
				primeArray[count - 1] = num;
				count++;
			}
			status = 1;
			num++;
		}
		return primeArray;
	}
}