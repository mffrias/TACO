package forArielGodio.fibonacci.bug01;

//@ model import org.jmlspecs.lang.*;
//@ model import org.jmlspecs.lang.*;


public class Fibonacci extends java.lang.Object {

  private /*@ spec_public @*/ long[] forArielGodio_fibonacci_bug01_Fibonacci_fib;
  /*@ invariant 2  <=  this.forArielGodio_fibonacci_bug01_Fibonacci_fib.length && this.forArielGodio_fibonacci_bug01_Fibonacci_fib.length  <=  93;
    @*/

  /*@ 
    @ requires ((long)this.forArielGodio_fibonacci_bug01_Fibonacci_fib[0])  ==  0L && ((long)this.forArielGodio_fibonacci_bug01_Fibonacci_fib[1])  ==  1L;
    @ ensures (\forall int i; 2  <=  i && i  <  this.forArielGodio_fibonacci_bug01_Fibonacci_fib.length; ((long)this.forArielGodio_fibonacci_bug01_Fibonacci_fib[i])  ==  ((long)this.forArielGodio_fibonacci_bug01_Fibonacci_fib[i - 1]) + ((long)this.forArielGodio_fibonacci_bug01_Fibonacci_fib[i - 2]));
    @ ensures (\forall int i; 2  <=  i && i  <  this.forArielGodio_fibonacci_bug01_Fibonacci_fib.length; (\forall int j; 2  <=  j && j  <  i; ((long)this.forArielGodio_fibonacci_bug01_Fibonacci_fib[j])  <  ((long)this.forArielGodio_fibonacci_bug01_Fibonacci_fib[i])));
    @ signals (java.lang.Exception e) false;
    @*/
  public void fibCompute() {
    {
      {
        int var_1_index = 2;

        {
          boolean t_31;
          boolean t_32;
          boolean t_33;

          t_31 = var_1_index  <  this.forArielGodio_fibonacci_bug01_Fibonacci_fib.length;
          {
            assert ! t_31;
          }
          t_32 = var_1_index  <  this.forArielGodio_fibonacci_bug01_Fibonacci_fib.length;
          t_33 = ! t_32;
          assert t_33;
        }
      }
    }
  }

}
