/* 
 * DynAlloy translator options 
 * --------------------------- 
 * assertionId= null
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

fun mi_fun[a:A]: A {
  a
}
run program_union
check old_expr_assert



pred old_expr[
  a_0: A + null,
  a_1: A + null,
  a_2: A + null
]{
  equ[a_0,
     null]
  and 
  (
    a_1=A)
  and 
  (
    a_2=a_0)
  and 
  TruePred[]

}



pred program_union[
  a_0: A + null,
  a_1: A + null,
  a_2: A + null,
  b_0: A + null,
  b_1: A + null,
  pre_a_1: A + null
]{
  equ[a_0,
     null]
  and 
  (
    a_1=null)
  and 
  (
    pre_a_1=a_1)
  and 
  (
    a_2=A)
  and 
  (
    (
      equ[a_2,
         A]
      and 
      (
        b_1=a_0)
    )
    or 
    (
      (
        not (
          equ[a_2,
             A]
        )
      )
      and 
      TruePred[]
      and 
      (
        b_0=b_1)
    )
  )
  and 
  TruePred[]
  and 
  equ[b_1,
     pre_a_1]

}



assert old_expr_assert{
all b_0 :  A + null,
    b_1 :  A + null,
    b_2 :  A + null | {
  (
    TruePred[]
    and 
    old_expr[b_0,
            b_1,
            b_2]
  )
  implies 
          equ[b_2,
             A]
  }
}
