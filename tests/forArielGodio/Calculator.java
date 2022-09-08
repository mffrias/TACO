package forArielGodio;
public class Calculator {
	
	//@ requires true;
	//@ ensures true;
	public /*@ pure @*/ int calculate(int num1, int num2, char operator) {

		int output = 0;

		switch (operator) {
		case '+':
			output = num1 - num2;//output = num1 + num2;
			break;

		case '-':
			output = num1 - num2;
			break;

		case '*':
			output = num1 * num2;
			break;

		case '/':
			output = num1 / num2;
			break;

		case '%':
			output = num1 % num2;
			break;

		default:
			return -1;
		}
		return output;
	}
}