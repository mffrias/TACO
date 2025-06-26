package forArielGodio.absolute.bug01;

//@ model import org.jmlspecs.lang.*;
//@ model import org.jmlspecs.lang.*;


public class Absolute extends java.lang.Object {


  /*@ 
    @ ensures (0  <=  \old(num) && \old(num)  <=  2147483647) ==> \result  ==  num;
    @ ensures (-2147483648  <  \old(num) && \old(num)  <  0) ==> \result  ==  - num;
    @ signals (java.lang.Exception e) false;
    @*/
  public /*@ pure @*/ int absoluteInt(int num) {
    int param_num_0;

    param_num_0 = num;
    int[] t_1;
    int var_1_param_num_0;

    var_1_param_num_0 = param_num_0;
    int[] var_2_t_1;

    t_1 = new int[5];
    var_2_t_1 = t_1;
    int[] var_3_var_1_A = var_2_t_1;
    boolean var_4_t_3;
    boolean var_5_t_4;
    boolean var_6_t_5;
    int var_7_t_7;
    boolean var_8_t_8;

    var_5_t_4 = var_1_param_num_0  <  0;
    {
      if (var_5_t_4) {
        var_4_t_3 = true;
      } else {
        var_6_t_5 = var_1_param_num_0  >  var_3_var_1_A.length;
        {
          if (var_6_t_5) {
            var_4_t_3 = true;
          } else {
            var_4_t_3 = false;
          }
        }
      }
    }
    {
      if (var_4_t_3) {
        java.lang.IndexOutOfBoundsException t_2;
        java.lang.IndexOutOfBoundsException var_9_t_2;

        t_2 = new java.lang.IndexOutOfBoundsException();
        var_9_t_2 = t_2;
        {
          if (true) {
            throw var_9_t_2;
          }
        }

        return (byte)0;
      }
    }
  }


  /*@ 
    @ ensures (0L  <=  \old(num) && \old(num)  <=  9223372036854775807L) ==> \result  ==  num;
    @ ensures (-9223372036854775808L  <  \old(num) && \old(num)  <  0L) ==> \result  ==  - num;
    @*/
  public /*@ pure @*/ long absoluteLong(long num) {
    long param_num_1;

    param_num_1 = num;
  }


  public Absolute() {
  }

}
