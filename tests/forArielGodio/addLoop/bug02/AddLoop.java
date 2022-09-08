package forArielGodio.addLoop.bug02;
public class AddLoop {
    //@ requires Integer.MIN_VALUE <= x + y && x + y <= Integer.MAX_VALUE && y != Integer.MIN_VALUE;
    //@ ensures \result == x + y;
    public static int addLoop(int x, int y) {
        int sum = x;
        if (y > 0) {
            int n = y;
            //@ decreasing n;
            while (n >= 0) {//while (n > 0) {
                sum = sum + 1;
                n = n - 1;
            }
        } else {
            int n = -y;
            //@ decreasing n;
            while (n > 0) {
                sum = sum - 1;
                n = n - 1;
            }
        }
        return sum;
    }
}