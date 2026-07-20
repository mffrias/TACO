/* 
 * DynAlloy translator options 
 * --------------------------- 
 * assertionId= null
 * loopUnroll= 3
 * removeQuantifiers= false
 * strictUnrolling= false
 * build_dynalloy_trace= false
 */ 

module pepe
pred TruePred[] {}
run test_01
run test_02
run test_03
run test_04
run test_05
run test_06

/* test_07 */ 
pred equ[l,r:univ] {l=r}
run test_07

/* test_08 */
run test_08

/* test_09 */
run test_08

/* test_09 */
run test_09

/*test_10*/
run test_10
run test_11

/* test_12 */
sig T {}
sig S {}
pred myPre[a,b:univ]{}
pred myPost[a,b,c,d: univ] {}
abstract sig boolean {}
one sig true extends boolean {}
one sig false extends boolean {}
check test_12
run test_13

/* test_14 */
pred myEmptyPred[] {}
run test_14

/* test_15 */
pred myEmptyPred2[] {}
run test_15

pred myPre1[x,y:univ]{}
pred myPred[x,y,z:univ]{}
check test_16

/* test_17 */
sig A {}
/* test_18 */
sig Q {}
sig W {}
sig Y { f: Q, r: W }
/* test_19 */
sig Z { f: Q, r: W }
{ some Z }
run test_20
sig R {}
run test_21
run test_22



pred test_13[
  a_0: T,
  a_1: T,
  b_0: T,
  b_1: T
]{
  myPre[a_0,
       b_0]
  and 
  myPost[a_0,
        b_0,
        a_1,
        b_1]
}


pred myAction[

]{
  TruePred[]
  and 
  TruePred[]
}


pred skipOtherAction[

]{
  TruePred[]
  and 
  TruePred[]
}


pred updateVariable[
  l_1: univ,
  r_0: univ
]{
  TruePred[]
  and 
  equ[l_1,
     r_0]
}


pred myAction1[

]{
  TruePred[]
  and 
  TruePred[]
}


pred postAction[

]{
  TruePred[]
  and 
  TruePred[]
}


pred myAction2[

]{
  TruePred[]
  and 
  TruePred[]
}


pred test_02[
]{
  TruePred[]
}



pred test_15[
]{
  myEmptyPred[]
  and 
  myEmptyPred2[]

}



pred test_01[
]{
  TruePred[]
}



pred test_22[
  a_0: univ,
  a_1: univ,
  b_0: univ,
  b_1: univ,
  temp_1: univ
]{
  updateVariable[temp_1,
                a_0]
  and 
  updateVariable[a_1,
                b_0]
  and 
  updateVariable[b_1,
                temp_1]

}



pred test_03[
]{
  TruePred[]
  and 
  TruePred[]

}



pred test_04[
]{
  TruePred[]
  or 
  TruePred[]

}



pred test_11[
]{
  (
    TruePred[]
    or 
    TruePred[]
  )
  and 
  (
    TruePred[]
    or 
    TruePred[]
  )
  and 
  (
    TruePred[]
    or 
    TruePred[]
  )

}



pred test_07[
  i_1: Int
]{
  updateVariable[i_1,
                7]

}



pred test_06[
]{
  TruePred[]
  and 
  (
    TruePred[]
    or 
    TruePred[]
  )
  and 
  TruePred[]

}



pred test_08[
]{
  skipOtherAction[]
}



pred test_14[
]{
  myEmptyPred[]
}



pred test_20[
]{
  myAction1[]
}



pred test_09[
]{
  myAction[]
}



pred test_21[
]{
  myAction1[]
}



pred test_05[
]{
  (
    TruePred[]
    and 
    TruePred[]
  )
  or 
  (
    TruePred[]
    and 
    TruePred[]
  )

}



pred test_10[
]{
  myAction[]
  and 
  TruePred[]
  and 
  myAction2[]

}



assert test_12{
all a_0 :  T,
    b_0 :  T | {
  (
    myPre[a_0,
         b_0]
    and 
    myAction1[]
  )
  implies 
          myPost[a_0,
                b_0,
                a_0,
                b_0]
  }
}


assert test_16{
all a_0 :  T,
    b_0 :  T | {
  (
    myPre1[a_0,
          b_0]
    and 
    myPred[a_0,
          b_0,
          true]
  )
  implies 
          myPost[a_0,
                b_0,
                a_0,
                b_0]
  }
}
