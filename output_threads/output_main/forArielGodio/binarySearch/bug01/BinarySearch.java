package forArielGodio.binarySearch.bug01;

//@ model import org.jmlspecs.lang.*;


public class BinarySearch extends java.lang.Object {


  /*@ 
    @ requires (\forall int j; 0  <=  j && j  <  arr.length; (\forall int i; 0  <=  i && i  <  j; ((int)arr[i])  <=  ((int)arr[j])));
    @ ensures \result  ==  -1 <==> (\forall int i; 0  <=  i && i  <  arr.length; ((int)arr[i])  !=  key);
    @ ensures (0  <=  \result && \result  <  arr.length) ==> ((int)arr[\result])  ==  key;
    @*/
  public static int binary(int[] arr, int key) {
    {
      int[] param_arr_0;

      param_arr_0 = arr;
      int param_key_1;

      param_key_1 = key;
      {
        {
          boolean t_16;

          t_16 = arr.length  !=  0;
          if (t_16) {
            {
              {
                {
                  {
                    {
                      if (true) return -1;
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
                      int t_1;
                      boolean t_15;
                      int var_1_low = 0;
                      int var_2_high = arr.length;

                      t_1 = var_2_high / 2;
                      int var_3_mid = t_1;

                      {
                        boolean t_6;
                        boolean t_7;
                        int t_8;
                        boolean t_9;
                        boolean t_10;
                        boolean t_11;
                        int t_12;
                        boolean t_13;
                        boolean t_14;

                        {
                          t_7 = var_1_low  <  var_2_high;
                          if (t_7) {
                            {
                              {
                                t_8 = param_arr_0[var_3_mid];
                                t_9 = t_8  !=  param_key_1;
                                if (t_9) {
                                  {
                                    t_6 = true;
                                  }
                                } else {
                                  {
                                    t_6 = false;
                                  }
                                }
                              }
                            }
                          } else {
                            {
                              t_6 = false;
                            }
                          }
                        }

                        if (t_6) {
                          {
                            {
                              {
                                {
                                  {
                                    int t_2;
                                    boolean t_3;
                                    int t_4;
                                    int t_5;

                                    t_2 = param_arr_0[var_3_mid];
                                    t_3 = t_2  <  param_key_1;

                                    if (t_3) {
                                      {
                                        {
                                          {
                                            {
                                              {
                                                var_1_low = var_3_mid + 1;
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
                                                var_2_high = var_3_mid;
                                              }
                                            }
                                          }
                                        }
                                      }
                                    }
                                    t_4 = var_2_high - var_1_low;
                                    t_5 = t_4 / 2;
                                    var_3_mid = var_1_low + t_5;
                                  }
                                }
                              }
                            }
                          }
                        }
                        {
                          t_11 = var_1_low  <  var_2_high;
                          if (t_11) {
                            {
                              {
                                t_12 = param_arr_0[var_3_mid];
                                t_13 = t_12  !=  param_key_1;
                                if (t_13) {
                                  {
                                    t_10 = true;
                                  }
                                } else {
                                  {
                                    t_10 = false;
                                  }
                                }
                              }
                            }
                          } else {
                            {
                              t_10 = false;
                            }
                          }
                        }
                        t_14 = ! t_10;
                        assert t_14;
                      }
                      t_15 = var_1_low  >=  var_2_high;

                      if (t_15) {
                        {
                          {
                            {
                              {
                                {
                                  if (true) return -1;
                                }
                              }
                            }
                          }
                        }
                      }
                      if (true) return var_3_mid;
                    }
                  }
                }
              }
            }
          }
        }
      }
    }

    return (byte)0;
  }


  public BinarySearch() {
  }

}
