package bug;

import java.lang.reflect.Method;

public class CopyArray {

  //copyArray-int{},int,int,int{}-
  //+PREV@ requires iBegin != 0 || iEnd != 0;
  //@ requires iBegin == 1;	
  //@ requires iEnd == 2;
  //@ requires a[1] == 3;
  //@ requires b[1] == 3	;
  //@ requires a.length == 6;
  //@ requires b.length == 3;
  //@ requires 0 < a.length && 0 < b.length;
  //@ requires 0 <= iBegin && 0 <= iEnd && iBegin <= iEnd;
  //@ requires iBegin < a.length && iBegin < b.length && iEnd < a.length && iEnd < b.length;
  //@ ensures (\forall int i; 0 <= i && i < b.length; b[i] == \old(b[i])); //alt
  //@ ensures (\forall int i; 0 <= i && i < iBegin; a[i] == \old(a[i])); //alt
  //@ ensures (\forall int i; iBegin <= i && i < iEnd; a[i] == b[i]);
  //@ ensures (\forall int i; iEnd <= i && i < a.length; a[i] == \old(a[i])); //alt
  //@ signals (RuntimeException e) false;
  public static void copyArray(int[] b, int iBegin, int iEnd, int[] a) {
    int k = iBegin;
    if (true)
      return;
    while (iEnd - k >= 0) { // while (iEnd - k > 0) 
      a[k] = b[k];
      k = k + 1;
    }
  }
  
  
  public static void main(String[] args) {
      int[] b = new int[5];
      int iBegin = 0;
      int iEnd = 1;
      int[] a = new int[2];
      // Parameter Initialization
      b[0] = -6;
      b[1] = 7;
      b[2] = 7;
      b[3] = -3;
      b[4] = -2;
      a[0] = 0;
      a[1] = -2;
      
      copyArray(b, iBegin, iEnd, a);
  }
  
  
}
