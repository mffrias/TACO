package ximena;

//@ model import org.jmlspecs.lang.*;


public class InvCall4_1 extends java.lang.Object {

  public int ximena_InvCall4_x;
  public boolean ximena_InvCall4_returnVarFromSpecMethodCall_0;
  /*@ invariant this.ximena_InvCall4_returnVarFromSpecMethodCall_0;
    @*/

  public /*@ pure @*/ boolean isPos() {
    {
      {
        boolean t_1;

        t_1 = this.ximena_InvCall4_x  >  0;
        if (true) return t_1;
      }
    }

    return false;
  }


  /*@ 
    @ ensures true;
    @ signals (java.lang.Throwable e) true;
    @*/
  public void breakThenThrow() {
    {
      boolean t_3;

      {
        boolean rightHandSideFormerAssume_1 = this.isPos();


        /*@ assume this.ximena_InvCall4_returnVarFromSpecMethodCall_0  ==  rightHandSideFormerAssume_1;
          @*/
      }
      {
        java.lang.RuntimeException t_2;

        this.ximena_InvCall4_x = -1;
        t_2 = new java.lang.RuntimeException();
        if (true) {
          throw t_2;
        }
      }
      t_3 = this.isPos();
      this.ximena_InvCall4_returnVarFromSpecMethodCall_0 = t_3;
    }
  }


  /*@ 
    @ ensures true;
    @ signals (java.lang.Throwable e) true;
    @*/
  public void keepThenThrow() {
    {
      java.lang.RuntimeException t_4;

      this.ximena_InvCall4_x = 3;
      t_4 = new java.lang.RuntimeException();
      throw t_4;
    }
  }


  public InvCall4_1() {
    this.ximena_InvCall4_x = (byte)0;
  }

}
