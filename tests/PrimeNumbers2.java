
//@ model import org.jmlspecs.lang.*;


public class PrimeNumbers2 extends java.lang.Object {

  private /*@ spec_public @*/ /*@ nullable @*/ int[] primeArray;

  /*@
    @ requires 2  <=  n;
    @ requires 2  <=  d;
    @ ensures \result  ==  true <==> n % d  ==  0;
    @*/
  public static boolean div(int n, int d) {

    /*@ assert d  !=  0;
      @*/

    return n % d  ==  0;
  }


  /*@
    @ requires 0  <  n && n  <  7;
    @ ensures primeArray[0]  ==  2;
    @ ensures (\forall int i, j; 0  <=  i && i  <  primeArray.length && 0  <=  j && j  <  primeArray.length; i + 1  ==  j ==> primeArray[i]  <  primeArray[j]);
    @ ensures (\forall int i, j; 0  <=  i && i  <  primeArray.length && 0  <=  j && j  <  primeArray.length; i + 1  ==  j ==> !( (\exists int k; primeArray[i]  <  k && k  <  primeArray[j]; (\forall int l; 2  <=  l && l  <=  k / 2; k % l  !=  0)) ));
    @ ensures (\forall int i; 0  <=  i && i  <  primeArray.length; (\forall int j; 2  <=  j && j  <=  primeArray[i] / 2; primeArray[i] % j  !=  0));
    @ ensures primeArray.length  ==  n && \result  ==  primeArray;
    @ signals (java.lang.RuntimeException e) false;
    @*/
  public int[] primeList(int n) {
    {
      int var_1_status = 1, var_2_num = 3;

      this.primeArray = new int[n];
      this.primeArray[0] = 2;
      {
        int var_3_count = 2;

        {
          {

            if (var_3_count  <=  n) {
              {
                {


                  if (var_1_status  !=  0) {
                    {
                      var_3_count++;
                    }
                  }
                  {
                    int var_4_k_2_6 = 2;

                    {
                      {

                        if (var_4_k_2_6  <=  var_2_num) {
                          {
                            {

                              var_2_num--;
                              var_4_k_2_6++;
                            }
                            if (var_4_k_2_6  <=  var_2_num) {
                              {
                                {

                                  var_2_num--;
                                  var_4_k_2_6++;
                                }
                                if (var_4_k_2_6  <=  var_2_num) {
                                  {
                                    {

                                      var_2_num--;
                                      var_4_k_2_6++;
                                    }
                                    if (var_4_k_2_6  <=  var_2_num) {
                                      {
                                        {

                                          var_2_num--;
                                          var_4_k_2_6++;
                                        }
                                        if (var_4_k_2_6  <=  var_2_num) {
                                          {
                                            {

                                              var_2_num--;
                                              var_4_k_2_6++;
                                            }
                                            if (var_4_k_2_6  <=  var_2_num) {
                                              {

                                                var_2_num--;
                                                var_4_k_2_6++;
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
                        assert !( var_4_k_2_6  <=  var_2_num );
                      }
                    }
                  }
                }
                if (var_3_count  <=  n) {
                  {
                    {


                      if (var_1_status  !=  0) {
                        {
                          var_3_count++;
                        }
                      }
                      {
                        int var_5_k_2_5 = 2;

                        {
                          {

                            if (var_5_k_2_5  <=  var_2_num) {
                              {
                                {

                                  var_2_num--;
                                  var_5_k_2_5++;
                                }
                                if (var_5_k_2_5  <=  var_2_num) {
                                  {
                                    {

                                      var_2_num--;
                                      var_5_k_2_5++;
                                    }
                                    if (var_5_k_2_5  <=  var_2_num) {
                                      {
                                        {

                                          var_2_num--;
                                          var_5_k_2_5++;
                                        }
                                        if (var_5_k_2_5  <=  var_2_num) {
                                          {
                                            {

                                              var_2_num--;
                                              var_5_k_2_5++;
                                            }
                                            if (var_5_k_2_5  <=  var_2_num) {
                                              {
                                                {

                                                  var_2_num--;
                                                  var_5_k_2_5++;
                                                }
                                                if (var_5_k_2_5  <=  var_2_num) {
                                                  {

                                                    var_2_num--;
                                                    var_5_k_2_5++;
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
                            assert !( var_5_k_2_5  <=  var_2_num );
                          }
                        }
                      }
                    }
                    if (var_3_count  <=  n) {
                      {
                        {


                          if (var_1_status  !=  0) {
                            {
                              var_3_count++;
                            }
                          }
                          {
                            int var_6_k_2_4 = 2;

                            {
                              {

                                if (var_6_k_2_4  <=  var_2_num) {
                                  {
                                    {

                                      var_2_num--;
                                      var_6_k_2_4++;
                                    }
                                    if (var_6_k_2_4  <=  var_2_num) {
                                      {
                                        {

                                          var_2_num--;
                                          var_6_k_2_4++;
                                        }
                                        if (var_6_k_2_4  <=  var_2_num) {
                                          {
                                            {

                                              var_2_num--;
                                              var_6_k_2_4++;
                                            }
                                            if (var_6_k_2_4  <=  var_2_num) {
                                              {
                                                {

                                                  var_2_num--;
                                                  var_6_k_2_4++;
                                                }
                                                if (var_6_k_2_4  <=  var_2_num) {
                                                  {
                                                    {

                                                      var_2_num--;
                                                      var_6_k_2_4++;
                                                    }
                                                    if (var_6_k_2_4  <=  var_2_num) {
                                                      {

                                                        var_2_num--;
                                                        var_6_k_2_4++;
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
                                assert !( var_6_k_2_4  <=  var_2_num );
                              }
                            }
                          }
                        }
                        if (var_3_count  <=  n) {
                          {
                            {


                              if (var_1_status  !=  0) {
                                {
                                  var_3_count++;
                                }
                              }
                              {
                                int var_7_k_2_3 = 2;

                                {
                                  {

                                    if (var_7_k_2_3  <=  var_2_num) {
                                      {
                                        {

                                          var_2_num--;
                                          var_7_k_2_3++;
                                        }
                                        if (var_7_k_2_3  <=  var_2_num) {
                                          {
                                            {

                                              var_2_num--;
                                              var_7_k_2_3++;
                                            }
                                            if (var_7_k_2_3  <=  var_2_num) {
                                              {
                                                {

                                                  var_2_num--;
                                                  var_7_k_2_3++;
                                                }
                                                if (var_7_k_2_3  <=  var_2_num) {
                                                  {
                                                    {

                                                      var_2_num--;
                                                      var_7_k_2_3++;
                                                    }
                                                    if (var_7_k_2_3  <=  var_2_num) {
                                                      {
                                                        {

                                                          var_2_num--;
                                                          var_7_k_2_3++;
                                                        }
                                                        if (var_7_k_2_3  <=  var_2_num) {
                                                          {

                                                            var_2_num--;
                                                            var_7_k_2_3++;
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
                                    assert !( var_7_k_2_3  <=  var_2_num );
                                  }
                                }
                              }
                            }
                            if (var_3_count  <=  n) {
                              {
                                {


                                  if (var_1_status  !=  0) {
                                    {
                                      var_3_count++;
                                    }
                                  }
                                  {
                                    int var_8_k_2_2 = 2;

                                    {
                                      {

                                        if (var_8_k_2_2  <=  var_2_num) {
                                          {
                                            {

                                              var_2_num--;
                                              var_8_k_2_2++;
                                            }
                                            if (var_8_k_2_2  <=  var_2_num) {
                                              {
                                                {

                                                  var_2_num--;
                                                  var_8_k_2_2++;
                                                }
                                                if (var_8_k_2_2  <=  var_2_num) {
                                                  {
                                                    {

                                                      var_2_num--;
                                                      var_8_k_2_2++;
                                                    }
                                                    if (var_8_k_2_2  <=  var_2_num) {
                                                      {
                                                        {

                                                          var_2_num--;
                                                          var_8_k_2_2++;
                                                        }
                                                        if (var_8_k_2_2  <=  var_2_num) {
                                                          {
                                                            {

                                                              var_2_num--;
                                                              var_8_k_2_2++;
                                                            }
                                                            if (var_8_k_2_2  <=  var_2_num) {
                                                              {

                                                                var_2_num--;
                                                                var_8_k_2_2++;
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
                                        assert !( var_8_k_2_2  <=  var_2_num );
                                      }
                                    }
                                  }
                                }
                                if (var_3_count  <=  n) {
                                  {


                                    if (var_1_status  !=  0) {
                                      {
                                        var_3_count++;
                                      }
                                    }
                                    {
                                      int var_9_k_2_1 = 2;

                                      {
                                        {

                                          if (var_9_k_2_1  <=  var_2_num) {
                                            {
                                              {

                                                var_2_num--;
                                                var_9_k_2_1++;
                                              }
                                              if (var_9_k_2_1  <=  var_2_num) {
                                                {
                                                  {

                                                    var_2_num--;
                                                    var_9_k_2_1++;
                                                  }
                                                  if (var_9_k_2_1  <=  var_2_num) {
                                                    {
                                                      {

                                                        var_2_num--;
                                                        var_9_k_2_1++;
                                                      }
                                                      if (var_9_k_2_1  <=  var_2_num) {
                                                        {
                                                          {

                                                            var_2_num--;
                                                            var_9_k_2_1++;
                                                          }
                                                          if (var_9_k_2_1  <=  var_2_num) {
                                                            {
                                                              {

                                                                var_2_num--;
                                                                var_9_k_2_1++;
                                                              }
                                                              if (var_9_k_2_1  <=  var_2_num) {
                                                                {

                                                                  var_2_num--;
                                                                  var_9_k_2_1++;
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
                                          assert !( var_9_k_2_1  <=  var_2_num );
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
            assert !( var_3_count  <=  n );
          }
        }
      }

      return this.primeArray;
    }
  }


  public PrimeNumbers2() {
  }

}
