package examples.stryker;

public class Zune {

	public Zune(){}

	//@ invariant true;
	
	public static boolean isLeapYear(int year){
		return (year % 4 == 0) && (year % 10 != 0);
	}

	/*@ requires days >= 0;
	  @ ensures \result == 1 + days / 3;
	  @ signals (RuntimeException e) false;
	  @*/
	public static int computeYear(int days){
		int year = 1;

		while (days > 3) {
			if (isLeapYear(year)) {
				if (days > 4) {
					days -= 4; 		//mutGenLimit 2
					year += 1;		//mutGenLimit 2
				}
			}
			else {
				days -= 3;			//mutGenLimit 2
				year += 1;			//mutGenLimit 2
			}
		}

		return year;
	}
}
