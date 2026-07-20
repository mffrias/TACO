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
abstract sig A {}
one sig A1 extends A {}
one sig A2 extends A {}
one sig A3 extends A {}
pred TruePred[] {}

pred equ[l,r:univ] {
  l=r
}
check old_expr_assert



pred old_expr[
  a_0: A + null,
  a_1: A + null,
  a_2: A + null,
  a_3: A + null,
  a_4: A + null,
  a_5: A + null
]{
  (
    a_1=A1)
  and 
  (
    a_2=null)
  and 
  (
    a_3=A2)
  and 
  (
    a_4=A3)
  and 
  (
    a_5=a_0)
  and 
  equ[a_0,
     null]
  and 
  TruePred[]

}



assert old_expr_assert{
all b_0 :  A + null,
    b_1 :  A + null,
    b_2 :  A + null,
    b_3 :  A + null,
    b_4 :  A + null,
    b_5 :  A + null | {
  (
    equ[b_0,
       null]
    and 
    old_expr[b_0,
            b_1,
            b_2,
            b_3,
            b_4,
            b_5]
  )
  implies 
          equ[b_5,
             A]
  }
}
