/* 
 * DynAlloy translator options 
 * --------------------------- 
 * assertionId= null
 * loopUnroll= 10
 * removeQuantifiers= true
 * strictUnrolling= false
 * build_dynalloy_trace= false
 */ 

module collidingLocalsProgram
open util/integer
pred TruePred[] {}



pred P1[
  x_0: Int,
  x_1: Int,
  y_0: Int,
  y_1: Int,
  l2_l0_local_1: Int,
  l1_l0_local_1: Int,
  l1_local_1: Int,
  l1_local_2: Int,
  l1_local_3: Int,
  l2_local_1: Int,
  l2_local_2: Int,
  l2_local_3: Int,
  l3_local_1: Int
]{
  (
    x_1=add[x_0,1])
  and 
  P2[add[x_1,1],
    l1_local_1,
    l1_local_2,
    l1_local_3,
    l1_l0_local_1]
  and 
  P2[add[x_1,2],
    l2_local_1,
    l2_local_2,
    l2_local_3,
    l2_l0_local_1]
  and 
  P3[y_0,
    y_1,
    l3_local_1]

}



pred P2[
  param_0: Int,
  local_1: Int,
  local_2: Int,
  local_3: Int,
  l0_local_1: Int
]{
  (
    local_1=param_0)
  and 
  (
    local_2=add[local_1,1])
  and 
  P3[local_2,
    local_3,
    l0_local_1]

}



pred P3[
  param_0: Int,
  param_1: Int,
  local_1: Int
]{
  (
    param_1=add[param_0,1])
  and 
  (
    local_1=param_1)

}

