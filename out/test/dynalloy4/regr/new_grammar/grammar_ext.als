/* 
 * DynAlloy translator options 
 * --------------------------- 
 * assertionId= grammarExtAssert
 * loopUnroll= 3
 * removeQuantifiers= false
 * strictUnrolling= false
 * build_dynalloy_trace= false
 */ 

module new_grammar
one sig null {}
one sig A {}
pred TruePred[] {}

pred equ[l,r:univ] {
  l=r
}
fun someA[]: A {
  A
}
run grammarExt
check grammarExtAssert



pred clear[
  b_1: A + null
]{
  TruePred[]
  and 
  equ[b_1,
     null]
}


pred fill[
  b_1: A + null
]{
  TruePred[]
  and 
  equ[b_1,
     A]
}


pred grammarExt[
  a_0: A + null,
  a_1: A + null,
  a_2: A + null,
  a_3: A + null,
  a_4: A + null
]{
  (
    a_1=((a_0) & (null)))
  and 
  (
    a_2=((A+null) & (a_1)))
  and 
  (
    a_3=((A+null) & (A)))
  and 
  (
    a_4=((((A+null) & (A))) & (A+null)))
  and 
  TruePred[]

}



assert grammarExtAssert{
all b_0 :  A + null,
    b_1 :  A + null,
    b_2 :  A + null,
    b_3 :  A + null,
    b_4 :  A + null | {
  (
    TruePred[]
    and 
    grammarExt[b_0,
              b_1,
              b_2,
              b_3,
              b_4]
  )
  implies 
          equ[b_0,
             A]
  }
}
