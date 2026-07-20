/* 
 * DynAlloy translator options 
 * --------------------------- 
 * assertionId= null
 * loopUnroll= 3
 * removeQuantifiers= false
 * strictUnrolling= false
 * build_dynalloy_trace= false
 */ 

module while_grammar
open util/integer
one sig null {}
one sig A {}
pred TruePred[] {}

pred equ[l,r:univ] {
  l=r
}

pred not_equ[l,r:univ] {
  l!=r
}
run while_program



pred while_program[
  a_0: A + null,
  f_0: A -> one ( A + null ),
  curr_1: A + null,
  curr_2: A + null,
  curr_3: A + null,
  curr_4: A + null,
  counter_1: Int,
  counter_2: Int,
  counter_3: Int,
  counter_4: Int
]{
  (
    counter_1=0)
  and 
  (
    curr_1=a_0)
  and 
  (
    (
      not_equ[curr_1,
             null]
      and 
      (
        curr_2=curr_1.f_0)
      and 
      (
        counter_2=add[counter_1,1])
    )
    or 
    (
      TruePred[]
      and 
      (
        counter_1=counter_2)
      and 
      (
        curr_1=curr_2)
    )
  )
  and 
  (
    (
      not_equ[curr_2,
             null]
      and 
      (
        curr_3=curr_2.f_0)
      and 
      (
        counter_3=add[counter_2,1])
    )
    or 
    (
      TruePred[]
      and 
      (
        counter_2=counter_3)
      and 
      (
        curr_2=curr_3)
    )
  )
  and 
  (
    (
      not_equ[curr_3,
             null]
      and 
      (
        curr_4=curr_3.f_0)
      and 
      (
        counter_4=add[counter_3,1])
    )
    or 
    (
      TruePred[]
      and 
      (
        counter_3=counter_4)
      and 
      (
        curr_3=curr_4)
    )
  )
  and 
  (
    not (
      not_equ[curr_4,
             null]
    )
  )
  and 
  equ[a_0,
     a_0]

}

