/* 
 * DynAlloy translator options 
 * --------------------------- 
 * assertionId= null
 * loopUnroll= 10
 * removeQuantifiers= true
 * strictUnrolling= false
 * build_dynalloy_trace= false
 */ 

module example1
open util/integer
pred TruePred[] {}



pred P3[
  z_0: Int,
  z_1: Int,
  bar_1: Int
]{
  (
    bar_1=z_0)
  and 
  (
    z_1=add[z_0,1])

}



pred P1[
  x_0: Int,
  x_1: Int,
  l1_l0_bar_1: Int,
  l2_l0_bar_1: Int,
  l1_foo_1: Int,
  l1_foo_2: Int,
  l1_foo_3: Int,
  l2_foo_1: Int,
  l2_foo_2: Int,
  l2_foo_3: Int
]{
  (
    x_1=add[x_0,1])
  and 
  P2[x_1,
    l1_foo_1,
    l1_foo_2,
    l1_foo_3,
    l1_l0_bar_1]
  and 
  P2[add[x_1,1],
    l2_foo_1,
    l2_foo_2,
    l2_foo_3,
    l2_l0_bar_1]

}



pred P2[
  a_0: Int,
  foo_1: Int,
  foo_2: Int,
  foo_3: Int,
  l0_bar_1: Int
]{
  (
    foo_1=a_0)
  and 
  (
    foo_2=add[foo_1,1])
  and 
  P3[foo_2,
    foo_3,
    l0_bar_1]

}

