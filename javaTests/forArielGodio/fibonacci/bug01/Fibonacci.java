package forArielGodio.fibonacci.bug01;

//@ model import org.jmlspecs.lang.*;
//@ model import org.jmlspecs.lang.*;


public class Fibonacci extends Object {

  private /*@ spec_public @*/ long[] forArielGodio_fibonacci_bug01_Fibonacci_fib;
  /*@ invariant 2  <=  this.forArielGodio_fibonacci_bug01_Fibonacci_fib.length && this.forArielGodio_fibonacci_bug01_Fibonacci_fib.length  <=  93;
    @*/

  /*@ 
    @ requires ((long)this.forArielGodio_fibonacci_bug01_Fibonacci_fib[0])  ==  0L && ((long)this.forArielGodio_fibonacci_bug01_Fibonacci_fib[1])  ==  1L;
    @ signals (java.lang.Exception e) false;
    @ signals (java.lang.AssertionError e) true;
    @*/
  public void fibCompute() {
    {
      {
        int var_1_index = 2;

        {
          boolean t_37;
          boolean t_38;
          boolean t_39;

          t_37 = var_1_index  <  this.forArielGodio_fibonacci_bug01_Fibonacci_fib.length;
          {
            assert t_37;
            {
              {
                {
                  {
                    {
                      {
                        boolean t_36;

                        {
                          int t_2;
                          long t_3;
                          int t_4;
                          long t_5;
                          int t_6;

                          t_2 = var_1_index - 2;
                          t_3 = this.forArielGodio_fibonacci_bug01_Fibonacci_fib[t_2];
                          t_4 = var_1_index + 1;
                          t_5 = this.forArielGodio_fibonacci_bug01_Fibonacci_fib[t_4];
                          this.forArielGodio_fibonacci_bug01_Fibonacci_fib[var_1_index] = t_3 + t_5;
                          t_6 = var_1_index;
                          var_1_index = var_1_index + 1;
                        }
                        t_36 = var_1_index  <  this.forArielGodio_fibonacci_bug01_Fibonacci_fib.length;
                        if (t_36) {
                          {
                            {
                              {
                                {
                                  {
                                    boolean t_35;

                                    {
                                      int t_7;
                                      long t_8;
                                      int t_9;
                                      long t_10;
                                      int t_11;

                                      t_7 = var_1_index - 2;
                                      t_8 = this.forArielGodio_fibonacci_bug01_Fibonacci_fib[t_7];
                                      t_9 = var_1_index + 1;
                                      t_10 = this.forArielGodio_fibonacci_bug01_Fibonacci_fib[t_9];
                                      this.forArielGodio_fibonacci_bug01_Fibonacci_fib[var_1_index] = t_8 + t_10;
                                      t_11 = var_1_index;
                                      var_1_index = var_1_index + 1;
                                    }
                                    t_35 = var_1_index  <  this.forArielGodio_fibonacci_bug01_Fibonacci_fib.length;
                                    if (t_35) {
                                      {
                                        {
                                          {
                                            {
                                              {
                                                boolean t_34;

                                                {
                                                  int t_12;
                                                  long t_13;
                                                  int t_14;
                                                  long t_15;
                                                  int t_16;

                                                  t_12 = var_1_index - 2;
                                                  t_13 = this.forArielGodio_fibonacci_bug01_Fibonacci_fib[t_12];
                                                  t_14 = var_1_index + 1;
                                                  t_15 = this.forArielGodio_fibonacci_bug01_Fibonacci_fib[t_14];
                                                  this.forArielGodio_fibonacci_bug01_Fibonacci_fib[var_1_index] = t_13 + t_15;
                                                  t_16 = var_1_index;
                                                  var_1_index = var_1_index + 1;
                                                }
                                                t_34 = var_1_index  <  this.forArielGodio_fibonacci_bug01_Fibonacci_fib.length;
                                                if (t_34) {
                                                  {
                                                    {
                                                      {
                                                        {
                                                          {
                                                            boolean t_33;

                                                            {
                                                              int t_17;
                                                              long t_18;
                                                              int t_19;
                                                              long t_20;
                                                              int t_21;

                                                              t_17 = var_1_index - 2;
                                                              t_18 = this.forArielGodio_fibonacci_bug01_Fibonacci_fib[t_17];
                                                              t_19 = var_1_index + 1;
                                                              t_20 = this.forArielGodio_fibonacci_bug01_Fibonacci_fib[t_19];
                                                              this.forArielGodio_fibonacci_bug01_Fibonacci_fib[var_1_index] = t_18 + t_20;
                                                              t_21 = var_1_index;
                                                              var_1_index = var_1_index + 1;
                                                            }
                                                            t_33 = var_1_index  <  this.forArielGodio_fibonacci_bug01_Fibonacci_fib.length;
                                                            if (t_33) {
                                                              {
                                                                {
                                                                  {
                                                                    {
                                                                      {
                                                                        boolean t_32;

                                                                        {
                                                                          int t_22;
                                                                          long t_23;
                                                                          int t_24;
                                                                          long t_25;
                                                                          int t_26;

                                                                          t_22 = var_1_index - 2;
                                                                          t_23 = this.forArielGodio_fibonacci_bug01_Fibonacci_fib[t_22];
                                                                          t_24 = var_1_index + 1;
                                                                          t_25 = this.forArielGodio_fibonacci_bug01_Fibonacci_fib[t_24];
                                                                          this.forArielGodio_fibonacci_bug01_Fibonacci_fib[var_1_index] = t_23 + t_25;
                                                                          t_26 = var_1_index;
                                                                          var_1_index = var_1_index + 1;
                                                                        }
                                                                        t_32 = var_1_index  <  this.forArielGodio_fibonacci_bug01_Fibonacci_fib.length;
                                                                        if (t_32) {
                                                                          {
                                                                            {
                                                                              {
                                                                                {
                                                                                  {
                                                                                    int t_27;
                                                                                    long t_28;
                                                                                    int t_29;
                                                                                    long t_30;
                                                                                    int t_31;

                                                                                    t_27 = var_1_index - 2;
                                                                                    t_28 = this.forArielGodio_fibonacci_bug01_Fibonacci_fib[t_27];
                                                                                    t_29 = var_1_index + 1;
                                                                                    t_30 = this.forArielGodio_fibonacci_bug01_Fibonacci_fib[t_29];
                                                                                    this.forArielGodio_fibonacci_bug01_Fibonacci_fib[var_1_index] = t_28 + t_30;
                                                                                    t_31 = var_1_index;
                                                                                    var_1_index = var_1_index + 1;
                                                                                  }
                                                                                }
                                                                              }
                                                                            }
                                                                          }
                                                                        }
                                                                      }
                                                                    }
                                                                  }
                                                                }
                                                              }
                                                            }
                                                          }
                                                        }
                                                      }
                                                    }
                                                  }
                                                }
                                              }
                                            }
                                          }
                                        }
                                      }
                                    }
                                  }
                                }
                              }
                            }
                          }
                        }
                      }
                    }
                  }
                }
              }
            }
          }
          t_38 = var_1_index  <  this.forArielGodio_fibonacci_bug01_Fibonacci_fib.length;
          t_39 = ! t_38;
          assert t_39;
        }
      }
    }
  }

}
