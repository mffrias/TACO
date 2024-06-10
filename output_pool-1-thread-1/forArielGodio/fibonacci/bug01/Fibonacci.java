package forArielGodio.fibonacci.bug01;

//@ model import org.jmlspecs.lang.*;
//@ model import org.jmlspecs.lang.*;


public class Fibonacci extends java.lang.Object {

  private /*@ spec_public @*/ long[] forArielGodio_fibonacci_bug01_Fibonacci_fib;
  /*@ invariant 2  <=  this.forArielGodio_fibonacci_bug01_Fibonacci_fib.length && this.forArielGodio_fibonacci_bug01_Fibonacci_fib.length  <=  93;
    @*/

  /*@ 
    @ ensures ((long)this.forArielGodio_fibonacci_bug01_Fibonacci_fib[0])  ==  0L && ((long)this.forArielGodio_fibonacci_bug01_Fibonacci_fib[1])  ==  1L;
    @*/
  public Fibonacci() {
    super();
    {
      long[] t_1;

      t_1 = new long[2];
      this.forArielGodio_fibonacci_bug01_Fibonacci_fib = t_1;
      this.forArielGodio_fibonacci_bug01_Fibonacci_fib[0] = 0L;
      this.forArielGodio_fibonacci_bug01_Fibonacci_fib[1] = 1L;
    }
  }


  /*@ 
    @ requires 2  <=  size && size  <=  93;
    @ ensures ((long)this.forArielGodio_fibonacci_bug01_Fibonacci_fib[0])  ==  0L && ((long)this.forArielGodio_fibonacci_bug01_Fibonacci_fib[1])  ==  1L;
    @ ensures (\forall int i; 2  <=  i && i  <  this.forArielGodio_fibonacci_bug01_Fibonacci_fib.length; ((long)this.forArielGodio_fibonacci_bug01_Fibonacci_fib[i])  ==  0L);
    @*/
  /*@ spec_public @*/ Fibonacci(int size) {
    super();
    {
      boolean t_4;
      boolean t_5;
      boolean t_6;

      {
        t_5 = 2  <=  size;
        if (t_5) {
          {
            {
              t_6 = size  <=  93;
              if (t_6) {
                {
                  t_4 = true;
                }
              } else {
                {
                  t_4 = false;
                }
              }
            }
          }
        } else {
          {
            t_4 = false;
          }
        }
      }
      if (t_4) {
        {
          {
            {
              {
                {
                  long[] t_2;

                  t_2 = new long[size];
                  this.forArielGodio_fibonacci_bug01_Fibonacci_fib = t_2;
                  this.forArielGodio_fibonacci_bug01_Fibonacci_fib[0] = 0L;
                  this.forArielGodio_fibonacci_bug01_Fibonacci_fib[1] = 1L;
                }
              }
            }
          }
        }
      } else {
        {
          {
            {
              {
                {
                  java.lang.IllegalArgumentException t_3;

                  t_3 = new java.lang.IllegalArgumentException();
                  throw t_3;
                }
              }
            }
          }
        }
      }
    }
  }


  /*@ 
    @ requires 0  <=  index && index  <  this.forArielGodio_fibonacci_bug01_Fibonacci_fib.length;
    @ ensures \result  ==  ((long)this.forArielGodio_fibonacci_bug01_Fibonacci_fib[index]);
    @*/
  public /*@ pure @*/ long getFib(int index) {
    {
      int param_index_0;

      param_index_0 = index;
      {
        long t_7;

        t_7 = this.forArielGodio_fibonacci_bug01_Fibonacci_fib[param_index_0];
        if (true) return t_7;
      }
    }

    return 0L;
  }


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

          if (t_31) {
            {
              {
                {
                  {
                    {
                      boolean t_30;

                      {
                        int t_8;
                        long t_9;
                        int t_10;
                        long t_11;
                        int t_12;

                        t_8 = var_1_index - 2;
                        t_9 = this.forArielGodio_fibonacci_bug01_Fibonacci_fib[t_8];
                        t_10 = var_1_index + 1;
                        t_11 = this.forArielGodio_fibonacci_bug01_Fibonacci_fib[t_10];
                        this.forArielGodio_fibonacci_bug01_Fibonacci_fib[var_1_index] = t_9 + t_11;
                        t_12 = var_1_index;
                        var_1_index = var_1_index + 1;
                      }
                      t_30 = var_1_index  <  this.forArielGodio_fibonacci_bug01_Fibonacci_fib.length;
                      if (t_30) {
                        {
                          {
                            {
                              {
                                {
                                  boolean t_29;

                                  {
                                    int t_13;
                                    long t_14;
                                    int t_15;
                                    long t_16;
                                    int t_17;

                                    t_13 = var_1_index - 2;
                                    t_14 = this.forArielGodio_fibonacci_bug01_Fibonacci_fib[t_13];
                                    t_15 = var_1_index + 1;
                                    t_16 = this.forArielGodio_fibonacci_bug01_Fibonacci_fib[t_15];
                                    this.forArielGodio_fibonacci_bug01_Fibonacci_fib[var_1_index] = t_14 + t_16;
                                    t_17 = var_1_index;
                                    var_1_index = var_1_index + 1;
                                  }
                                  t_29 = var_1_index  <  this.forArielGodio_fibonacci_bug01_Fibonacci_fib.length;
                                  if (t_29) {
                                    {
                                      {
                                        {
                                          {
                                            {
                                              boolean t_28;

                                              {
                                                int t_18;
                                                long t_19;
                                                int t_20;
                                                long t_21;
                                                int t_22;

                                                t_18 = var_1_index - 2;
                                                t_19 = this.forArielGodio_fibonacci_bug01_Fibonacci_fib[t_18];
                                                t_20 = var_1_index + 1;
                                                t_21 = this.forArielGodio_fibonacci_bug01_Fibonacci_fib[t_20];
                                                this.forArielGodio_fibonacci_bug01_Fibonacci_fib[var_1_index] = t_19 + t_21;
                                                t_22 = var_1_index;
                                                var_1_index = var_1_index + 1;
                                              }
                                              t_28 = var_1_index  <  this.forArielGodio_fibonacci_bug01_Fibonacci_fib.length;
                                              if (t_28) {
                                                {
                                                  {
                                                    {
                                                      {
                                                        {
                                                          int t_23;
                                                          long t_24;
                                                          int t_25;
                                                          long t_26;
                                                          int t_27;

                                                          t_23 = var_1_index - 2;
                                                          t_24 = this.forArielGodio_fibonacci_bug01_Fibonacci_fib[t_23];
                                                          t_25 = var_1_index + 1;
                                                          t_26 = this.forArielGodio_fibonacci_bug01_Fibonacci_fib[t_25];
                                                          this.forArielGodio_fibonacci_bug01_Fibonacci_fib[var_1_index] = t_24 + t_26;
                                                          t_27 = var_1_index;
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
          t_32 = var_1_index  <  this.forArielGodio_fibonacci_bug01_Fibonacci_fib.length;
          t_33 = ! t_32;
          assert t_33;
        }
      }
    }
  }


  /*@ 
    @ requires true;
    @ ensures \result  ==  0;
    @*/
  public int mfrias(int i) {
    {
      int param_i_1;

      param_i_1 = i;
      {
        boolean t_35;

        t_35 = param_i_1  >  0;

        if (t_35) {
          {
            {
              {
                {
                  {
                    int t_34;

                    t_34 = param_i_1 + 1;
                    if (true) return t_34;
                  }
                }
              }
            }
          }
        }
        if (true) return 0;
      }
    }

    return 0;
  }

}
