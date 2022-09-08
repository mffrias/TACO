package qu.edu.qa;

public class Euclid {

  /*@ requires true;
	@ ensures (\exists int n; 0<n && n<=n1; \result * n == n1); 
	@*/
	public int Euclides(int n1, int n2) {
        int r = n1 % n2;
        while (r != 0) {
            n1 = n2;
            n2 = r;
            r = n1 % n2;
        }
        return n2;
    }
	
}
