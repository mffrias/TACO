package forArielGodio;
public class AddLoop2 {

    public static int addLoop(int x, int y) {
        int sum = x;
        if (y <= 0) {//if (y > 0) {
            int n = y;
            //@ decreasing n;
            while (n > 0) {
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