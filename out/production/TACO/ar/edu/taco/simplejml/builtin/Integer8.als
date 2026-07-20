/*
Authors: Pablo Abad and Marcelo Frias

PREDICATES:
pred_java_primitive_integer_value_abs
pred_java_primitive_integer_value_decrement
pred_java_primitive_integer_value_add
pred_java_primitive_integer_value_sub
pred_java_primitive_integer_value_div
pred_java_primitive_integer_value_div_rem
pred_java_primitive_integer_value_eq
pred_java_primitive_integer_value_eq_zero
pred_java_primitive_integer_value_gt
pred_java_primitive_integer_value_gt_zero
pred_java_primitive_integer_value_gte
pred_java_primitive_integer_value_gte_zero
pred_java_primitive_integer_value_lt
pred_java_primitive_integer_value_lt_zero
pred_java_primitive_integer_value_lte_zero
pred_java_primitive_integer_value_lte
pred_java_primitive_integer_value_mul
pred_java_primitive_integer_value_negate
pred_java_primitive_integer_value_neq
pred_java_primitive_integer_value_sshr

MARKER PREDICATES
pred_java_primitive_integer_value_mul_marker

FUNCTIONS:
fun_java_primitive_integer_value_add
fun_java_primitive_integer_value_div
fun_java_primitive_integer_value_mul
fun_java_primitive_integer_value_negate
fun_java_primitive_integer_value_rem
fun_java_primitive_integer_value_sshr
fun_java_primitive_integer_value_sub
*/



// PREDICATES


abstract sig boolean {}
one sig true extends boolean {}
one sig false extends boolean {}
pred isTrue[b: boolean] { b in true }

pred isFalse[b: boolean] { b in false }

pred TruePred[] {}
pred FalsePred[] { not TruePred[] }

fun Not[b: boolean] : boolean {
  boolean - b
}

fun And[b1, b2: boolean] : boolean {
  subset_[b1 + b2, true]
}

fun Or[b1, b2: boolean] : boolean {
  subset_[true, b1 + b2]
}

fun Xor[b1, b2: boolean] : boolean {
  subset_[boolean, b1 + b2]
}

fun Nand[b1, b2: boolean] : boolean {
  subset_[false, b1 + b2]
}

fun Nor[b1, b2: boolean] : boolean {
  subset_[b1 + b2, false]
}

fun subset_[s1, s2: set boolean] : boolean {
  (s1 in s2) => true else false
}

pred equ[l,r:univ] {l=r} 
pred neq[l,r:univ] {l!=r}


sig JavaPrimitiveIntegerValue {
   b00: boolean,
   b01: boolean,
   b02: boolean,
   b03: boolean,
   b04: boolean,
   b05: boolean,
   b06: boolean,
   b07: boolean
}


fun AdderCarry[a: boolean, b: boolean, cin: boolean]: boolean {
Or[ And[a,b], And[cin, Xor[a,b]]]
}

fun AdderSum[a: boolean, b: boolean, cin: boolean]: boolean {
Xor[Xor[a, b], cin]
}



pred pred_java_primitive_integer_value_gte[a: JavaPrimitiveIntegerValue, b: JavaPrimitiveIntegerValue] {
  pred_java_primitive_integer_value_gt[a, b] or pred_java_primitive_integer_value_eq[a, b]
}

pred pred_java_primitive_integer_value_lt[a: JavaPrimitiveIntegerValue, b: JavaPrimitiveIntegerValue] { 
  not pred_java_primitive_integer_value_gte[a, b] 
} 

pred pred_java_primitive_integer_value_lte[a: JavaPrimitiveIntegerValue, b: JavaPrimitiveIntegerValue] {
  not pred_java_primitive_integer_value_gt[a, b]
}

pred pred_java_primitive_integer_value_neq[a: JavaPrimitiveIntegerValue, b: JavaPrimitiveIntegerValue] {
  not pred_java_primitive_integer_value_eq[a, b]
}

pred pred_java_primitive_integer_value_gt[a: JavaPrimitiveIntegerValue, b: JavaPrimitiveIntegerValue] {
   (a.b07 in true and b.b07 in false) 
   or (a.b07 = b.b07) and (a.b06 in true and b.b06 in false) 
   or (a.b07 = b.b07) and (a.b06 = b.b06) and (a.b05 in true and b.b05 in false) 
   or (a.b07 = b.b07) and (a.b06 = b.b06) and (a.b05 = b.b05) and (a.b04 in true and b.b04 in false) 
   or (a.b07 = b.b07) and (a.b06 = b.b06) and (a.b05 = b.b05) and (a.b04 = b.b04) and (a.b03 in true and b.b03 in false) 
   or (a.b07 = b.b07) and (a.b06 = b.b06) and (a.b05 = b.b05) and (a.b04 = b.b04) and (a.b03 = b.b03) and (a.b02 in true and b.b02 in false) 
   or (a.b07 = b.b07) and (a.b06 = b.b06) and (a.b05 = b.b05) and (a.b04 = b.b04) and (a.b03 = b.b03) and (a.b02 = b.b02) and (a.b01 in true and b.b01 in false) 
   or (a.b07 = b.b07) and (a.b06 = b.b06) and (a.b05 = b.b05) and (a.b04 = b.b04) and (a.b03 = b.b03) and (a.b02 = b.b02) and (a.b01 = b.b01) and (a.b00 in true and b.b00 in false) 
}


pred pred_java_primitive_integer_value_eq_zero[a: JavaPrimitiveIntegerValue] {
   a.b00 in false 
   a.b01 in false 
   a.b02 in false 
   a.b03 in false 
   a.b04 in false 
   a.b05 in false 
   a.b06 in false 
   a.b07 in false 
}


pred pred_java_primitive_integer_value_eq[a: JavaPrimitiveIntegerValue, b: JavaPrimitiveIntegerValue] {
   a.b00 = b.b00
   a.b01 = b.b01
   a.b02 = b.b02
   a.b03 = b.b03
   a.b04 = b.b04
   a.b05 = b.b05
   a.b06 = b.b06
   a.b07 = b.b07
}


pred pred_java_primitive_integer_value_sub[a: JavaPrimitiveIntegerValue, b: JavaPrimitiveIntegerValue, result: JavaPrimitiveIntegerValue, overflow: boolean] { 
	pred_java_primitive_integer_value_add[result, b, a, overflow]
}

pred pred_java_primitive_integer_value_add[a: JavaPrimitiveIntegerValue, b: JavaPrimitiveIntegerValue, result: JavaPrimitiveIntegerValue, overflow: boolean] { 
   let c_0 = false | 
   let s_0 = AdderSum[a.b00, b.b00, c_0] | 
   let c_1 = AdderCarry[a.b00, b.b00, c_0] | 
   let s_1 = AdderSum[a.b01, b.b01, c_1] | 
   let c_2 = AdderCarry[a.b01, b.b01, c_1] | 
   let s_2 = AdderSum[a.b02, b.b02, c_2] | 
   let c_3 = AdderCarry[a.b02, b.b02, c_2] | 
   let s_3 = AdderSum[a.b03, b.b03, c_3] | 
   let c_4 = AdderCarry[a.b03, b.b03, c_3] | 
   let s_4 = AdderSum[a.b04, b.b04, c_4] | 
   let c_5 = AdderCarry[a.b04, b.b04, c_4] | 
   let s_5 = AdderSum[a.b05, b.b05, c_5] | 
   let c_6 = AdderCarry[a.b05, b.b05, c_5] | 
   let s_6 = AdderSum[a.b06, b.b06, c_6] | 
   let c_7 = AdderCarry[a.b06, b.b06, c_6] | 
   let s_7 = AdderSum[a.b07, b.b07, c_7] | 
   let c_8 = AdderCarry[a.b07, b.b07, c_7] | 
      result.b00 in s_0 and
      result.b01 in s_1 and
      result.b02 in s_2 and
      result.b03 in s_3 and
      result.b04 in s_4 and
      result.b05 in s_5 and
      result.b06 in s_6 and
      result.b07 in s_7 and
      overflow = (Xor[c_8, c_7])
}



// FUNCTIONS

fun fun_java_primitive_integer_value_add[a: JavaPrimitiveIntegerValue, b: JavaPrimitiveIntegerValue]: JavaPrimitiveIntegerValue { 
  {result: JavaPrimitiveIntegerValue | some overflow: boolean | pred_java_primitive_integer_value_add[a,b,result,overflow]}
}

fun fun_java_primitive_integer_value_sub[a: JavaPrimitiveIntegerValue, b: JavaPrimitiveIntegerValue]: JavaPrimitiveIntegerValue {
 {result: JavaPrimitiveIntegerValue | some overflow: boolean | pred_java_primitive_integer_value_add[b,result,a,overflow] }
}

pred pred_java_primitive_integer_value_mul_marker[
  left    : JavaPrimitiveIntegerValue, 
  right   : JavaPrimitiveIntegerValue, 
  result  : JavaPrimitiveIntegerValue, 
  overflow: boolean] 
{
--marker predicate (empty body)
}

pred pred_java_primitive_integer_value_div_rem_marker[
  left     : JavaPrimitiveIntegerValue, 
  right    : JavaPrimitiveIntegerValue, 
  result   : JavaPrimitiveIntegerValue, 
  remainder: JavaPrimitiveIntegerValue] 
{
--marker predicate (empty body)
}
