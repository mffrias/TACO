/* 
 * DynAlloy translator options 
 * --------------------------- 
 * assertionId= newGrammarAssert
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
check newGrammarAssert



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


pred newGrammar[
  a_0: A + null,
  a_1: A + null,
  a_2: A + null,
  a_3: A + null,
  a_4: A + null,
  a_5: A + null,
  a_6: A + null,
  f_0: A -> one ( A + null ),
  f_1: A -> one ( A + null )
]{
  equ[a_0,
     null]
  and 
  (
    (
      equ[a_0,
         A]
      and 
      clear[a_1]
    )
    or 
    (
      TruePred[]
      and 
      (
        a_0=a_1)
    )
  )
  and 
  (
    (
      equ[a_1,
         A]
      and 
      clear[a_2]
    )
    or 
    (
      TruePred[]
      and 
      (
        a_1=a_2)
    )
  )
  and 
  (
    (
      equ[a_2,
         A]
      and 
      clear[a_3]
    )
    or 
    (
      TruePred[]
      and 
      (
        a_2=a_3)
    )
  )
  and 
  equ[a_3,
     null]
  and 
  (
    (
      equ[a_3,
         null]
      and 
      (
        a_4=null)
      and 
      fill[a_6]
    )
    or 
    (
      (
        not (
          equ[a_3,
             null]
        )
      )
      and 
      clear[a_4]
      and 
      (
        a_5=A)
      and 
      clear[a_6]
    )
  )
  and 
  (
    f_1=(f_0)++((a_6)->(null)))

}



assert newGrammarAssert{
all b_0 :  A + null,
    b_1 :  A + null,
    b_2 :  A + null,
    b_3 :  A + null,
    b_4 :  A + null,
    b_5 :  A + null,
    b_6 :  A + null,
    f_0 :  A -> one ( A + null ),
    f_1 :  A -> one ( A + null ) | {
  (
    TruePred[]
    and 
    newGrammar[b_0,
              b_1,
              b_2,
              b_3,
              b_4,
              b_5,
              b_6,
              f_0,
              f_1]
  )
  implies 
          equ[b_0,
             A]
  }
}
