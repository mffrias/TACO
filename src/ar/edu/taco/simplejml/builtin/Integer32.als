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
pred_java_primitive_integer_value_set_has_negative
pred_java_primitive_integer_value_lte
pred_java_primitive_integer_value_mul
pred_java_primitive_integer_value_negate
pred_java_primitive_integer_value_neq
pred_java_primitive_integer_value_sshr
pred_cast_char_to_int (cast in char x int)
pred_java_primitive_char_value_addCharCharToJavaPrimitiveIntegerValue (+ : char x char -> int)
pred_java_primitive_char_value_addCharIntToJavaPrimitiveIntegerValue (+ : char x int -> int)
pred_java_primitive_char_value_addIntCharToJavaPrimitiveIntegerValue (+ : int x char -> int)


MARKER PREDICATES
pred_java_primitive_integer_value_add_marker
pred_java_primitive_integer_value_mul_marker

FUNCTIONS:
fun_java_primitive_integer_value_add
fun_java_primitive_integer_value_div
fun_java_primitive_integer_value_mul
fun_java_primitive_integer_value_negate
fun_java_primitive_integer_value_rem
fun_java_primitive_integer_value_sshr
fun_java_primitive_integer_value_sub
fun_cast_char_to_int (cast : char -> int)
fun_java_primitive_char_value_addCharCharToJavaPrimitiveIntegerValue (+ : char x char -> int)
fun_java_primitive_char_value_addCharCharIntToJavaPrimitiveIntegerValue (+ : char x int -> int)
fun_java_primitive_char_value_addIntCharToJavaPrimitiveIntegerValue (+ : int x char -> int
fun_java_primitive_char_value_subCharCharToJavaPrimitiveIntegerValue (- : char x char -> int)
fun_java_primitive_char_value_subCharIntToJavaPrimitiveIntegerValue (- : char x int -> int)
fun_java_primitive_char_value_subIntCharToJavaPrimitiveIntegerValue (- : int x char -> int)
*/

// PREDICATES

pred pred_java_primitive_integer_value_sshr[a: JavaPrimitiveIntegerValue, 
                                          ret: JavaPrimitiveIntegerValue] {
 a.b31= ret.b31 
 false= ret.b30
 a.b30= ret.b29 
 a.b29= ret.b28 
 a.b28= ret.b27 
 a.b27= ret.b26 
 a.b26= ret.b25 
 a.b25= ret.b24 
 a.b24= ret.b23 
 a.b23= ret.b22 
 a.b22= ret.b21 
 a.b21= ret.b20 
 a.b20= ret.b19 
 a.b19= ret.b18 
 a.b18= ret.b17 
 a.b17= ret.b16 
 a.b16= ret.b15 
 a.b15= ret.b14 
 a.b14= ret.b13 
 a.b13= ret.b12 
 a.b12= ret.b11 
 a.b11= ret.b10 
 a.b10= ret.b09 
 a.b09= ret.b08 
 a.b08= ret.b07 
 a.b07= ret.b06 
 a.b06= ret.b05 
 a.b05= ret.b04 
 a.b04= ret.b03 
 a.b03= ret.b02 
 a.b02= ret.b01 
 a.b01= ret.b00 
}


pred pred_java_primitive_integer_value_negate[a: JavaPrimitiveIntegerValue, b: JavaPrimitiveIntegerValue] { 
   a.b00 in b.b00 
   a.b01 in Xor [ Not[b.b01]  , And[Not[b.b00], Xor[Not[b.b00], a.b00]]] 
   a.b02 in Xor [ Not[b.b02]  , And[Not[b.b01], Xor[Not[b.b01], a.b01]]] 
   a.b03 in Xor [ Not[b.b03]  , And[Not[b.b02], Xor[Not[b.b02], a.b02]]] 
   a.b04 in Xor [ Not[b.b04]  , And[Not[b.b03], Xor[Not[b.b03], a.b03]]] 
   a.b05 in Xor [ Not[b.b05]  , And[Not[b.b04], Xor[Not[b.b04], a.b04]]] 
   a.b06 in Xor [ Not[b.b06]  , And[Not[b.b05], Xor[Not[b.b05], a.b05]]] 
   a.b07 in Xor [ Not[b.b07]  , And[Not[b.b06], Xor[Not[b.b06], a.b06]]] 
   a.b08 in Xor [ Not[b.b08]  , And[Not[b.b07], Xor[Not[b.b07], a.b07]]] 
   a.b09 in Xor [ Not[b.b09]  , And[Not[b.b08], Xor[Not[b.b08], a.b08]]] 
   a.b10 in Xor [ Not[b.b10]  , And[Not[b.b09], Xor[Not[b.b09], a.b09]]] 
   a.b11 in Xor [ Not[b.b11]  , And[Not[b.b10], Xor[Not[b.b10], a.b10]]] 
   a.b12 in Xor [ Not[b.b12]  , And[Not[b.b11], Xor[Not[b.b11], a.b11]]] 
   a.b13 in Xor [ Not[b.b13]  , And[Not[b.b12], Xor[Not[b.b12], a.b12]]] 
   a.b14 in Xor [ Not[b.b14]  , And[Not[b.b13], Xor[Not[b.b13], a.b13]]] 
   a.b15 in Xor [ Not[b.b15]  , And[Not[b.b14], Xor[Not[b.b14], a.b14]]] 
   a.b16 in Xor [ Not[b.b16]  , And[Not[b.b15], Xor[Not[b.b15], a.b15]]] 
   a.b17 in Xor [ Not[b.b17]  , And[Not[b.b16], Xor[Not[b.b16], a.b16]]] 
   a.b18 in Xor [ Not[b.b18]  , And[Not[b.b17], Xor[Not[b.b17], a.b17]]] 
   a.b19 in Xor [ Not[b.b19]  , And[Not[b.b18], Xor[Not[b.b18], a.b18]]] 
   a.b20 in Xor [ Not[b.b20]  , And[Not[b.b19], Xor[Not[b.b19], a.b19]]] 
   a.b21 in Xor [ Not[b.b21]  , And[Not[b.b20], Xor[Not[b.b20], a.b20]]] 
   a.b22 in Xor [ Not[b.b22]  , And[Not[b.b21], Xor[Not[b.b21], a.b21]]] 
   a.b23 in Xor [ Not[b.b23]  , And[Not[b.b22], Xor[Not[b.b22], a.b22]]] 
   a.b24 in Xor [ Not[b.b24]  , And[Not[b.b23], Xor[Not[b.b23], a.b23]]] 
   a.b25 in Xor [ Not[b.b25]  , And[Not[b.b24], Xor[Not[b.b24], a.b24]]] 
   a.b26 in Xor [ Not[b.b26]  , And[Not[b.b25], Xor[Not[b.b25], a.b25]]] 
   a.b27 in Xor [ Not[b.b27]  , And[Not[b.b26], Xor[Not[b.b26], a.b26]]] 
   a.b28 in Xor [ Not[b.b28]  , And[Not[b.b27], Xor[Not[b.b27], a.b27]]] 
   a.b29 in Xor [ Not[b.b29]  , And[Not[b.b28], Xor[Not[b.b28], a.b28]]] 
   a.b30 in Xor [ Not[b.b30]  , And[Not[b.b29], Xor[Not[b.b29], a.b29]]] 
   a.b31 in Xor [ Not[b.b31]  , And[Not[b.b30], Xor[Not[b.b30], a.b30]]] 
}


pred pred_java_primitive_integer_value_mul[a: JavaPrimitiveIntegerValue, b: JavaPrimitiveIntegerValue, result: JavaPrimitiveIntegerValue, overflow: boolean] {
some 
   a_c00, a_c01, a_c02, a_c03, a_c04, a_c05, a_c06, a_c07, a_c08, a_c09, a_c10, a_c11, a_c12, a_c13, a_c14, a_c15, a_c16, a_c17, a_c18, a_c19, a_c20, a_c21, a_c22, a_c23, a_c24, a_c25, a_c26, a_c27, a_c28, a_c29, a_c30, 
   b_c00, b_c01, b_c02, b_c03, b_c04, b_c05, b_c06, b_c07, b_c08, b_c09, b_c10, b_c11, b_c12, b_c13, b_c14, b_c15, b_c16, b_c17, b_c18, b_c19, b_c20, b_c21, b_c22, b_c23, b_c24, b_c25, b_c26, b_c27, b_c28, b_c29, b_c30, 
   s_0_0, s_0_1, 
   s_1_0, s_1_1, s_1_2, 
   s_2_0, s_2_1, s_2_2, s_2_3, 
   s_3_0, s_3_1, s_3_2, s_3_3, s_3_4, 
   s_4_0, s_4_1, s_4_2, s_4_3, s_4_4, s_4_5, 
   s_5_0, s_5_1, s_5_2, s_5_3, s_5_4, s_5_5, s_5_6, 
   s_6_0, s_6_1, s_6_2, s_6_3, s_6_4, s_6_5, s_6_6, s_6_7, 
   s_7_0, s_7_1, s_7_2, s_7_3, s_7_4, s_7_5, s_7_6, s_7_7, s_7_8, 
   s_8_0, s_8_1, s_8_2, s_8_3, s_8_4, s_8_5, s_8_6, s_8_7, s_8_8, s_8_9, 
   s_9_0, s_9_1, s_9_2, s_9_3, s_9_4, s_9_5, s_9_6, s_9_7, s_9_8, s_9_9, s_9_10, 
   s_10_0, s_10_1, s_10_2, s_10_3, s_10_4, s_10_5, s_10_6, s_10_7, s_10_8, s_10_9, s_10_10, s_10_11, 
   s_11_0, s_11_1, s_11_2, s_11_3, s_11_4, s_11_5, s_11_6, s_11_7, s_11_8, s_11_9, s_11_10, s_11_11, s_11_12, 
   s_12_0, s_12_1, s_12_2, s_12_3, s_12_4, s_12_5, s_12_6, s_12_7, s_12_8, s_12_9, s_12_10, s_12_11, s_12_12, s_12_13, 
   s_13_0, s_13_1, s_13_2, s_13_3, s_13_4, s_13_5, s_13_6, s_13_7, s_13_8, s_13_9, s_13_10, s_13_11, s_13_12, s_13_13, s_13_14, 
   s_14_0, s_14_1, s_14_2, s_14_3, s_14_4, s_14_5, s_14_6, s_14_7, s_14_8, s_14_9, s_14_10, s_14_11, s_14_12, s_14_13, s_14_14, s_14_15, 
   s_15_0, s_15_1, s_15_2, s_15_3, s_15_4, s_15_5, s_15_6, s_15_7, s_15_8, s_15_9, s_15_10, s_15_11, s_15_12, s_15_13, s_15_14, s_15_15, s_15_16, 
   s_16_0, s_16_1, s_16_2, s_16_3, s_16_4, s_16_5, s_16_6, s_16_7, s_16_8, s_16_9, s_16_10, s_16_11, s_16_12, s_16_13, s_16_14, s_16_15, s_16_16, s_16_17, 
   s_17_0, s_17_1, s_17_2, s_17_3, s_17_4, s_17_5, s_17_6, s_17_7, s_17_8, s_17_9, s_17_10, s_17_11, s_17_12, s_17_13, s_17_14, s_17_15, s_17_16, s_17_17, s_17_18, 
   s_18_0, s_18_1, s_18_2, s_18_3, s_18_4, s_18_5, s_18_6, s_18_7, s_18_8, s_18_9, s_18_10, s_18_11, s_18_12, s_18_13, s_18_14, s_18_15, s_18_16, s_18_17, s_18_18, s_18_19, 
   s_19_0, s_19_1, s_19_2, s_19_3, s_19_4, s_19_5, s_19_6, s_19_7, s_19_8, s_19_9, s_19_10, s_19_11, s_19_12, s_19_13, s_19_14, s_19_15, s_19_16, s_19_17, s_19_18, s_19_19, s_19_20, 
   s_20_0, s_20_1, s_20_2, s_20_3, s_20_4, s_20_5, s_20_6, s_20_7, s_20_8, s_20_9, s_20_10, s_20_11, s_20_12, s_20_13, s_20_14, s_20_15, s_20_16, s_20_17, s_20_18, s_20_19, s_20_20, s_20_21, 
   s_21_0, s_21_1, s_21_2, s_21_3, s_21_4, s_21_5, s_21_6, s_21_7, s_21_8, s_21_9, s_21_10, s_21_11, s_21_12, s_21_13, s_21_14, s_21_15, s_21_16, s_21_17, s_21_18, s_21_19, s_21_20, s_21_21, s_21_22, 
   s_22_0, s_22_1, s_22_2, s_22_3, s_22_4, s_22_5, s_22_6, s_22_7, s_22_8, s_22_9, s_22_10, s_22_11, s_22_12, s_22_13, s_22_14, s_22_15, s_22_16, s_22_17, s_22_18, s_22_19, s_22_20, s_22_21, s_22_22, s_22_23, 
   s_23_0, s_23_1, s_23_2, s_23_3, s_23_4, s_23_5, s_23_6, s_23_7, s_23_8, s_23_9, s_23_10, s_23_11, s_23_12, s_23_13, s_23_14, s_23_15, s_23_16, s_23_17, s_23_18, s_23_19, s_23_20, s_23_21, s_23_22, s_23_23, s_23_24, 
   s_24_0, s_24_1, s_24_2, s_24_3, s_24_4, s_24_5, s_24_6, s_24_7, s_24_8, s_24_9, s_24_10, s_24_11, s_24_12, s_24_13, s_24_14, s_24_15, s_24_16, s_24_17, s_24_18, s_24_19, s_24_20, s_24_21, s_24_22, s_24_23, s_24_24, s_24_25, 
   s_25_0, s_25_1, s_25_2, s_25_3, s_25_4, s_25_5, s_25_6, s_25_7, s_25_8, s_25_9, s_25_10, s_25_11, s_25_12, s_25_13, s_25_14, s_25_15, s_25_16, s_25_17, s_25_18, s_25_19, s_25_20, s_25_21, s_25_22, s_25_23, s_25_24, s_25_25, s_25_26, 
   s_26_0, s_26_1, s_26_2, s_26_3, s_26_4, s_26_5, s_26_6, s_26_7, s_26_8, s_26_9, s_26_10, s_26_11, s_26_12, s_26_13, s_26_14, s_26_15, s_26_16, s_26_17, s_26_18, s_26_19, s_26_20, s_26_21, s_26_22, s_26_23, s_26_24, s_26_25, s_26_26, s_26_27, 
   s_27_0, s_27_1, s_27_2, s_27_3, s_27_4, s_27_5, s_27_6, s_27_7, s_27_8, s_27_9, s_27_10, s_27_11, s_27_12, s_27_13, s_27_14, s_27_15, s_27_16, s_27_17, s_27_18, s_27_19, s_27_20, s_27_21, s_27_22, s_27_23, s_27_24, s_27_25, s_27_26, s_27_27, s_27_28, 
   s_28_0, s_28_1, s_28_2, s_28_3, s_28_4, s_28_5, s_28_6, s_28_7, s_28_8, s_28_9, s_28_10, s_28_11, s_28_12, s_28_13, s_28_14, s_28_15, s_28_16, s_28_17, s_28_18, s_28_19, s_28_20, s_28_21, s_28_22, s_28_23, s_28_24, s_28_25, s_28_26, s_28_27, s_28_28, s_28_29, 
   s_29_0, s_29_1, s_29_2, s_29_3, s_29_4, s_29_5, s_29_6, s_29_7, s_29_8, s_29_9, s_29_10, s_29_11, s_29_12, s_29_13, s_29_14, s_29_15, s_29_16, s_29_17, s_29_18, s_29_19, s_29_20, s_29_21, s_29_22, s_29_23, s_29_24, s_29_25, s_29_26, s_29_27, s_29_28, s_29_29, s_29_30, 
   s_30_0, s_30_1, s_30_2, s_30_3, s_30_4, s_30_5, s_30_6, s_30_7, s_30_8, s_30_9, s_30_10, s_30_11, s_30_12, s_30_13, s_30_14, s_30_15, s_30_16, s_30_17, s_30_18, s_30_19, s_30_20, s_30_21, s_30_22, s_30_23, s_30_24, s_30_25, s_30_26, s_30_27, s_30_28, s_30_29, s_30_30, s_30_31, 
   s_31_0, s_31_1, s_31_2, s_31_3, s_31_4, s_31_5, s_31_6, s_31_7, s_31_8, s_31_9, s_31_10, s_31_11, s_31_12, s_31_13, s_31_14, s_31_15, s_31_16, s_31_17, s_31_18, s_31_19, s_31_20, s_31_21, s_31_22, s_31_23, s_31_24, s_31_25, s_31_26, s_31_27, s_31_28, s_31_29, s_31_30, s_31_31, s_31_32, 
   c_0_0, c_0_1, 
   c_1_0, c_1_1, c_1_2, 
   c_2_0, c_2_1, c_2_2, c_2_3, 
   c_3_0, c_3_1, c_3_2, c_3_3, c_3_4, 
   c_4_0, c_4_1, c_4_2, c_4_3, c_4_4, c_4_5, 
   c_5_0, c_5_1, c_5_2, c_5_3, c_5_4, c_5_5, c_5_6, 
   c_6_0, c_6_1, c_6_2, c_6_3, c_6_4, c_6_5, c_6_6, c_6_7, 
   c_7_0, c_7_1, c_7_2, c_7_3, c_7_4, c_7_5, c_7_6, c_7_7, c_7_8, 
   c_8_0, c_8_1, c_8_2, c_8_3, c_8_4, c_8_5, c_8_6, c_8_7, c_8_8, c_8_9, 
   c_9_0, c_9_1, c_9_2, c_9_3, c_9_4, c_9_5, c_9_6, c_9_7, c_9_8, c_9_9, c_9_10, 
   c_10_0, c_10_1, c_10_2, c_10_3, c_10_4, c_10_5, c_10_6, c_10_7, c_10_8, c_10_9, c_10_10, c_10_11, 
   c_11_0, c_11_1, c_11_2, c_11_3, c_11_4, c_11_5, c_11_6, c_11_7, c_11_8, c_11_9, c_11_10, c_11_11, c_11_12, 
   c_12_0, c_12_1, c_12_2, c_12_3, c_12_4, c_12_5, c_12_6, c_12_7, c_12_8, c_12_9, c_12_10, c_12_11, c_12_12, c_12_13, 
   c_13_0, c_13_1, c_13_2, c_13_3, c_13_4, c_13_5, c_13_6, c_13_7, c_13_8, c_13_9, c_13_10, c_13_11, c_13_12, c_13_13, c_13_14, 
   c_14_0, c_14_1, c_14_2, c_14_3, c_14_4, c_14_5, c_14_6, c_14_7, c_14_8, c_14_9, c_14_10, c_14_11, c_14_12, c_14_13, c_14_14, c_14_15, 
   c_15_0, c_15_1, c_15_2, c_15_3, c_15_4, c_15_5, c_15_6, c_15_7, c_15_8, c_15_9, c_15_10, c_15_11, c_15_12, c_15_13, c_15_14, c_15_15, c_15_16, 
   c_16_0, c_16_1, c_16_2, c_16_3, c_16_4, c_16_5, c_16_6, c_16_7, c_16_8, c_16_9, c_16_10, c_16_11, c_16_12, c_16_13, c_16_14, c_16_15, c_16_16, c_16_17, 
   c_17_0, c_17_1, c_17_2, c_17_3, c_17_4, c_17_5, c_17_6, c_17_7, c_17_8, c_17_9, c_17_10, c_17_11, c_17_12, c_17_13, c_17_14, c_17_15, c_17_16, c_17_17, c_17_18, 
   c_18_0, c_18_1, c_18_2, c_18_3, c_18_4, c_18_5, c_18_6, c_18_7, c_18_8, c_18_9, c_18_10, c_18_11, c_18_12, c_18_13, c_18_14, c_18_15, c_18_16, c_18_17, c_18_18, c_18_19, 
   c_19_0, c_19_1, c_19_2, c_19_3, c_19_4, c_19_5, c_19_6, c_19_7, c_19_8, c_19_9, c_19_10, c_19_11, c_19_12, c_19_13, c_19_14, c_19_15, c_19_16, c_19_17, c_19_18, c_19_19, c_19_20, 
   c_20_0, c_20_1, c_20_2, c_20_3, c_20_4, c_20_5, c_20_6, c_20_7, c_20_8, c_20_9, c_20_10, c_20_11, c_20_12, c_20_13, c_20_14, c_20_15, c_20_16, c_20_17, c_20_18, c_20_19, c_20_20, c_20_21, 
   c_21_0, c_21_1, c_21_2, c_21_3, c_21_4, c_21_5, c_21_6, c_21_7, c_21_8, c_21_9, c_21_10, c_21_11, c_21_12, c_21_13, c_21_14, c_21_15, c_21_16, c_21_17, c_21_18, c_21_19, c_21_20, c_21_21, c_21_22, 
   c_22_0, c_22_1, c_22_2, c_22_3, c_22_4, c_22_5, c_22_6, c_22_7, c_22_8, c_22_9, c_22_10, c_22_11, c_22_12, c_22_13, c_22_14, c_22_15, c_22_16, c_22_17, c_22_18, c_22_19, c_22_20, c_22_21, c_22_22, c_22_23, 
   c_23_0, c_23_1, c_23_2, c_23_3, c_23_4, c_23_5, c_23_6, c_23_7, c_23_8, c_23_9, c_23_10, c_23_11, c_23_12, c_23_13, c_23_14, c_23_15, c_23_16, c_23_17, c_23_18, c_23_19, c_23_20, c_23_21, c_23_22, c_23_23, c_23_24, 
   c_24_0, c_24_1, c_24_2, c_24_3, c_24_4, c_24_5, c_24_6, c_24_7, c_24_8, c_24_9, c_24_10, c_24_11, c_24_12, c_24_13, c_24_14, c_24_15, c_24_16, c_24_17, c_24_18, c_24_19, c_24_20, c_24_21, c_24_22, c_24_23, c_24_24, c_24_25, 
   c_25_0, c_25_1, c_25_2, c_25_3, c_25_4, c_25_5, c_25_6, c_25_7, c_25_8, c_25_9, c_25_10, c_25_11, c_25_12, c_25_13, c_25_14, c_25_15, c_25_16, c_25_17, c_25_18, c_25_19, c_25_20, c_25_21, c_25_22, c_25_23, c_25_24, c_25_25, c_25_26, 
   c_26_0, c_26_1, c_26_2, c_26_3, c_26_4, c_26_5, c_26_6, c_26_7, c_26_8, c_26_9, c_26_10, c_26_11, c_26_12, c_26_13, c_26_14, c_26_15, c_26_16, c_26_17, c_26_18, c_26_19, c_26_20, c_26_21, c_26_22, c_26_23, c_26_24, c_26_25, c_26_26, c_26_27, 
   c_27_0, c_27_1, c_27_2, c_27_3, c_27_4, c_27_5, c_27_6, c_27_7, c_27_8, c_27_9, c_27_10, c_27_11, c_27_12, c_27_13, c_27_14, c_27_15, c_27_16, c_27_17, c_27_18, c_27_19, c_27_20, c_27_21, c_27_22, c_27_23, c_27_24, c_27_25, c_27_26, c_27_27, c_27_28, 
   c_28_0, c_28_1, c_28_2, c_28_3, c_28_4, c_28_5, c_28_6, c_28_7, c_28_8, c_28_9, c_28_10, c_28_11, c_28_12, c_28_13, c_28_14, c_28_15, c_28_16, c_28_17, c_28_18, c_28_19, c_28_20, c_28_21, c_28_22, c_28_23, c_28_24, c_28_25, c_28_26, c_28_27, c_28_28, c_28_29, 
   c_29_0, c_29_1, c_29_2, c_29_3, c_29_4, c_29_5, c_29_6, c_29_7, c_29_8, c_29_9, c_29_10, c_29_11, c_29_12, c_29_13, c_29_14, c_29_15, c_29_16, c_29_17, c_29_18, c_29_19, c_29_20, c_29_21, c_29_22, c_29_23, c_29_24, c_29_25, c_29_26, c_29_27, c_29_28, c_29_29, c_29_30, 
   c_30_0, c_30_1, c_30_2, c_30_3, c_30_4, c_30_5, c_30_6, c_30_7, c_30_8, c_30_9, c_30_10, c_30_11, c_30_12, c_30_13, c_30_14, c_30_15, c_30_16, c_30_17, c_30_18, c_30_19, c_30_20, c_30_21, c_30_22, c_30_23, c_30_24, c_30_25, c_30_26, c_30_27, c_30_28, c_30_29, c_30_30, c_30_31, 
   c_31_1, c_31_2, c_31_3, c_31_4, c_31_5, c_31_6, c_31_7, c_31_8, c_31_9, c_31_10, c_31_11, c_31_12, c_31_13, c_31_14, c_31_15, c_31_16, c_31_17, c_31_18, c_31_19, c_31_20, c_31_21, c_31_22, c_31_23, c_31_24, c_31_25, c_31_26, c_31_27, c_31_28, c_31_29, c_31_30, c_31_31, 
   t : boolean |
   a_c00 in Xor[a.b00, a.b31] and
   b_c00 in Xor[b.b00, b.b31] and
   a_c01 in Xor[a.b01, a.b31] and
   b_c01 in Xor[b.b01, b.b31] and
   a_c02 in Xor[a.b02, a.b31] and
   b_c02 in Xor[b.b02, b.b31] and
   a_c03 in Xor[a.b03, a.b31] and
   b_c03 in Xor[b.b03, b.b31] and
   a_c04 in Xor[a.b04, a.b31] and
   b_c04 in Xor[b.b04, b.b31] and
   a_c05 in Xor[a.b05, a.b31] and
   b_c05 in Xor[b.b05, b.b31] and
   a_c06 in Xor[a.b06, a.b31] and
   b_c06 in Xor[b.b06, b.b31] and
   a_c07 in Xor[a.b07, a.b31] and
   b_c07 in Xor[b.b07, b.b31] and
   a_c08 in Xor[a.b08, a.b31] and
   b_c08 in Xor[b.b08, b.b31] and
   a_c09 in Xor[a.b09, a.b31] and
   b_c09 in Xor[b.b09, b.b31] and
   a_c10 in Xor[a.b10, a.b31] and
   b_c10 in Xor[b.b10, b.b31] and
   a_c11 in Xor[a.b11, a.b31] and
   b_c11 in Xor[b.b11, b.b31] and
   a_c12 in Xor[a.b12, a.b31] and
   b_c12 in Xor[b.b12, b.b31] and
   a_c13 in Xor[a.b13, a.b31] and
   b_c13 in Xor[b.b13, b.b31] and
   a_c14 in Xor[a.b14, a.b31] and
   b_c14 in Xor[b.b14, b.b31] and
   a_c15 in Xor[a.b15, a.b31] and
   b_c15 in Xor[b.b15, b.b31] and
   a_c16 in Xor[a.b16, a.b31] and
   b_c16 in Xor[b.b16, b.b31] and
   a_c17 in Xor[a.b17, a.b31] and
   b_c17 in Xor[b.b17, b.b31] and
   a_c18 in Xor[a.b18, a.b31] and
   b_c18 in Xor[b.b18, b.b31] and
   a_c19 in Xor[a.b19, a.b31] and
   b_c19 in Xor[b.b19, b.b31] and
   a_c20 in Xor[a.b20, a.b31] and
   b_c20 in Xor[b.b20, b.b31] and
   a_c21 in Xor[a.b21, a.b31] and
   b_c21 in Xor[b.b21, b.b31] and
   a_c22 in Xor[a.b22, a.b31] and
   b_c22 in Xor[b.b22, b.b31] and
   a_c23 in Xor[a.b23, a.b31] and
   b_c23 in Xor[b.b23, b.b31] and
   a_c24 in Xor[a.b24, a.b31] and
   b_c24 in Xor[b.b24, b.b31] and
   a_c25 in Xor[a.b25, a.b31] and
   b_c25 in Xor[b.b25, b.b31] and
   a_c26 in Xor[a.b26, a.b31] and
   b_c26 in Xor[b.b26, b.b31] and
   a_c27 in Xor[a.b27, a.b31] and
   b_c27 in Xor[b.b27, b.b31] and
   a_c28 in Xor[a.b28, a.b31] and
   b_c28 in Xor[b.b28, b.b31] and
   a_c29 in Xor[a.b29, a.b31] and
   b_c29 in Xor[b.b29, b.b31] and
   a_c30 in Xor[a.b30, a.b31] and
   b_c30 in Xor[b.b30, b.b31] and
   t in Xor[a.b31, b.b31] and

   s_0_0 in AdderSum  [And[a_c00,b.b31], And[a.b31, b_c00], And[a.b31, b.b31]] and
   c_0_0 in AdderCarry[And[a_c00,b.b31], And[a.b31, b_c00], And[a.b31, b.b31]] and

   s_0_1 in AdderSum  [s_0_0, And[a_c00, b_c00], false] and
   c_0_1 in AdderCarry[s_0_0, And[a_c00, b_c00], false] and

   s_1_0 in AdderSum  [And[a_c01,b.b31], And[a.b31, b_c01], false] and
   c_1_0 in AdderCarry[And[a_c01,b.b31], And[a.b31, b_c01], false] and

   s_1_1 in AdderSum  [s_1_0, And[a_c01, b_c00], c_0_0] and
   c_1_1 in AdderCarry[s_1_0, And[a_c01, b_c00], c_0_0] and

   s_1_2 in AdderSum  [s_1_1, And[a_c00, b_c01], c_0_1] and
   c_1_2 in AdderCarry[s_1_1, And[a_c00, b_c01], c_0_1] and

   s_2_0 in AdderSum  [And[a_c02,b.b31], And[a.b31, b_c02], false] and
   c_2_0 in AdderCarry[And[a_c02,b.b31], And[a.b31, b_c02], false] and

   s_2_1 in AdderSum  [s_2_0, And[a_c02, b_c00], c_1_0] and
   c_2_1 in AdderCarry[s_2_0, And[a_c02, b_c00], c_1_0] and

   s_2_2 in AdderSum  [s_2_1, And[a_c01, b_c01], c_1_1] and
   c_2_2 in AdderCarry[s_2_1, And[a_c01, b_c01], c_1_1] and

   s_2_3 in AdderSum  [s_2_2, And[a_c00, b_c02], c_1_2] and
   c_2_3 in AdderCarry[s_2_2, And[a_c00, b_c02], c_1_2] and

   s_3_0 in AdderSum  [And[a_c03,b.b31], And[a.b31, b_c03], false] and
   c_3_0 in AdderCarry[And[a_c03,b.b31], And[a.b31, b_c03], false] and

   s_3_1 in AdderSum  [s_3_0, And[a_c03, b_c00], c_2_0] and
   c_3_1 in AdderCarry[s_3_0, And[a_c03, b_c00], c_2_0] and

   s_3_2 in AdderSum  [s_3_1, And[a_c02, b_c01], c_2_1] and
   c_3_2 in AdderCarry[s_3_1, And[a_c02, b_c01], c_2_1] and

   s_3_3 in AdderSum  [s_3_2, And[a_c01, b_c02], c_2_2] and
   c_3_3 in AdderCarry[s_3_2, And[a_c01, b_c02], c_2_2] and

   s_3_4 in AdderSum  [s_3_3, And[a_c00, b_c03], c_2_3] and
   c_3_4 in AdderCarry[s_3_3, And[a_c00, b_c03], c_2_3] and

   s_4_0 in AdderSum  [And[a_c04,b.b31], And[a.b31, b_c04], false] and
   c_4_0 in AdderCarry[And[a_c04,b.b31], And[a.b31, b_c04], false] and

   s_4_1 in AdderSum  [s_4_0, And[a_c04, b_c00], c_3_0] and
   c_4_1 in AdderCarry[s_4_0, And[a_c04, b_c00], c_3_0] and

   s_4_2 in AdderSum  [s_4_1, And[a_c03, b_c01], c_3_1] and
   c_4_2 in AdderCarry[s_4_1, And[a_c03, b_c01], c_3_1] and

   s_4_3 in AdderSum  [s_4_2, And[a_c02, b_c02], c_3_2] and
   c_4_3 in AdderCarry[s_4_2, And[a_c02, b_c02], c_3_2] and

   s_4_4 in AdderSum  [s_4_3, And[a_c01, b_c03], c_3_3] and
   c_4_4 in AdderCarry[s_4_3, And[a_c01, b_c03], c_3_3] and

   s_4_5 in AdderSum  [s_4_4, And[a_c00, b_c04], c_3_4] and
   c_4_5 in AdderCarry[s_4_4, And[a_c00, b_c04], c_3_4] and

   s_5_0 in AdderSum  [And[a_c05,b.b31], And[a.b31, b_c05], false] and
   c_5_0 in AdderCarry[And[a_c05,b.b31], And[a.b31, b_c05], false] and

   s_5_1 in AdderSum  [s_5_0, And[a_c05, b_c00], c_4_0] and
   c_5_1 in AdderCarry[s_5_0, And[a_c05, b_c00], c_4_0] and

   s_5_2 in AdderSum  [s_5_1, And[a_c04, b_c01], c_4_1] and
   c_5_2 in AdderCarry[s_5_1, And[a_c04, b_c01], c_4_1] and

   s_5_3 in AdderSum  [s_5_2, And[a_c03, b_c02], c_4_2] and
   c_5_3 in AdderCarry[s_5_2, And[a_c03, b_c02], c_4_2] and

   s_5_4 in AdderSum  [s_5_3, And[a_c02, b_c03], c_4_3] and
   c_5_4 in AdderCarry[s_5_3, And[a_c02, b_c03], c_4_3] and

   s_5_5 in AdderSum  [s_5_4, And[a_c01, b_c04], c_4_4] and
   c_5_5 in AdderCarry[s_5_4, And[a_c01, b_c04], c_4_4] and

   s_5_6 in AdderSum  [s_5_5, And[a_c00, b_c05], c_4_5] and
   c_5_6 in AdderCarry[s_5_5, And[a_c00, b_c05], c_4_5] and

   s_6_0 in AdderSum  [And[a_c06,b.b31], And[a.b31, b_c06], false] and
   c_6_0 in AdderCarry[And[a_c06,b.b31], And[a.b31, b_c06], false] and

   s_6_1 in AdderSum  [s_6_0, And[a_c06, b_c00], c_5_0] and
   c_6_1 in AdderCarry[s_6_0, And[a_c06, b_c00], c_5_0] and

   s_6_2 in AdderSum  [s_6_1, And[a_c05, b_c01], c_5_1] and
   c_6_2 in AdderCarry[s_6_1, And[a_c05, b_c01], c_5_1] and

   s_6_3 in AdderSum  [s_6_2, And[a_c04, b_c02], c_5_2] and
   c_6_3 in AdderCarry[s_6_2, And[a_c04, b_c02], c_5_2] and

   s_6_4 in AdderSum  [s_6_3, And[a_c03, b_c03], c_5_3] and
   c_6_4 in AdderCarry[s_6_3, And[a_c03, b_c03], c_5_3] and

   s_6_5 in AdderSum  [s_6_4, And[a_c02, b_c04], c_5_4] and
   c_6_5 in AdderCarry[s_6_4, And[a_c02, b_c04], c_5_4] and

   s_6_6 in AdderSum  [s_6_5, And[a_c01, b_c05], c_5_5] and
   c_6_6 in AdderCarry[s_6_5, And[a_c01, b_c05], c_5_5] and

   s_6_7 in AdderSum  [s_6_6, And[a_c00, b_c06], c_5_6] and
   c_6_7 in AdderCarry[s_6_6, And[a_c00, b_c06], c_5_6] and

   s_7_0 in AdderSum  [And[a_c07,b.b31], And[a.b31, b_c07], false] and
   c_7_0 in AdderCarry[And[a_c07,b.b31], And[a.b31, b_c07], false] and

   s_7_1 in AdderSum  [s_7_0, And[a_c07, b_c00], c_6_0] and
   c_7_1 in AdderCarry[s_7_0, And[a_c07, b_c00], c_6_0] and

   s_7_2 in AdderSum  [s_7_1, And[a_c06, b_c01], c_6_1] and
   c_7_2 in AdderCarry[s_7_1, And[a_c06, b_c01], c_6_1] and

   s_7_3 in AdderSum  [s_7_2, And[a_c05, b_c02], c_6_2] and
   c_7_3 in AdderCarry[s_7_2, And[a_c05, b_c02], c_6_2] and

   s_7_4 in AdderSum  [s_7_3, And[a_c04, b_c03], c_6_3] and
   c_7_4 in AdderCarry[s_7_3, And[a_c04, b_c03], c_6_3] and

   s_7_5 in AdderSum  [s_7_4, And[a_c03, b_c04], c_6_4] and
   c_7_5 in AdderCarry[s_7_4, And[a_c03, b_c04], c_6_4] and

   s_7_6 in AdderSum  [s_7_5, And[a_c02, b_c05], c_6_5] and
   c_7_6 in AdderCarry[s_7_5, And[a_c02, b_c05], c_6_5] and

   s_7_7 in AdderSum  [s_7_6, And[a_c01, b_c06], c_6_6] and
   c_7_7 in AdderCarry[s_7_6, And[a_c01, b_c06], c_6_6] and

   s_7_8 in AdderSum  [s_7_7, And[a_c00, b_c07], c_6_7] and
   c_7_8 in AdderCarry[s_7_7, And[a_c00, b_c07], c_6_7] and

   s_8_0 in AdderSum  [And[a_c08,b.b31], And[a.b31, b_c08], false] and
   c_8_0 in AdderCarry[And[a_c08,b.b31], And[a.b31, b_c08], false] and

   s_8_1 in AdderSum  [s_8_0, And[a_c08, b_c00], c_7_0] and
   c_8_1 in AdderCarry[s_8_0, And[a_c08, b_c00], c_7_0] and

   s_8_2 in AdderSum  [s_8_1, And[a_c07, b_c01], c_7_1] and
   c_8_2 in AdderCarry[s_8_1, And[a_c07, b_c01], c_7_1] and

   s_8_3 in AdderSum  [s_8_2, And[a_c06, b_c02], c_7_2] and
   c_8_3 in AdderCarry[s_8_2, And[a_c06, b_c02], c_7_2] and

   s_8_4 in AdderSum  [s_8_3, And[a_c05, b_c03], c_7_3] and
   c_8_4 in AdderCarry[s_8_3, And[a_c05, b_c03], c_7_3] and

   s_8_5 in AdderSum  [s_8_4, And[a_c04, b_c04], c_7_4] and
   c_8_5 in AdderCarry[s_8_4, And[a_c04, b_c04], c_7_4] and

   s_8_6 in AdderSum  [s_8_5, And[a_c03, b_c05], c_7_5] and
   c_8_6 in AdderCarry[s_8_5, And[a_c03, b_c05], c_7_5] and

   s_8_7 in AdderSum  [s_8_6, And[a_c02, b_c06], c_7_6] and
   c_8_7 in AdderCarry[s_8_6, And[a_c02, b_c06], c_7_6] and

   s_8_8 in AdderSum  [s_8_7, And[a_c01, b_c07], c_7_7] and
   c_8_8 in AdderCarry[s_8_7, And[a_c01, b_c07], c_7_7] and

   s_8_9 in AdderSum  [s_8_8, And[a_c00, b_c08], c_7_8] and
   c_8_9 in AdderCarry[s_8_8, And[a_c00, b_c08], c_7_8] and

   s_9_0 in AdderSum  [And[a_c09,b.b31], And[a.b31, b_c09], false] and
   c_9_0 in AdderCarry[And[a_c09,b.b31], And[a.b31, b_c09], false] and

   s_9_1 in AdderSum  [s_9_0, And[a_c09, b_c00], c_8_0] and
   c_9_1 in AdderCarry[s_9_0, And[a_c09, b_c00], c_8_0] and

   s_9_2 in AdderSum  [s_9_1, And[a_c08, b_c01], c_8_1] and
   c_9_2 in AdderCarry[s_9_1, And[a_c08, b_c01], c_8_1] and

   s_9_3 in AdderSum  [s_9_2, And[a_c07, b_c02], c_8_2] and
   c_9_3 in AdderCarry[s_9_2, And[a_c07, b_c02], c_8_2] and

   s_9_4 in AdderSum  [s_9_3, And[a_c06, b_c03], c_8_3] and
   c_9_4 in AdderCarry[s_9_3, And[a_c06, b_c03], c_8_3] and

   s_9_5 in AdderSum  [s_9_4, And[a_c05, b_c04], c_8_4] and
   c_9_5 in AdderCarry[s_9_4, And[a_c05, b_c04], c_8_4] and

   s_9_6 in AdderSum  [s_9_5, And[a_c04, b_c05], c_8_5] and
   c_9_6 in AdderCarry[s_9_5, And[a_c04, b_c05], c_8_5] and

   s_9_7 in AdderSum  [s_9_6, And[a_c03, b_c06], c_8_6] and
   c_9_7 in AdderCarry[s_9_6, And[a_c03, b_c06], c_8_6] and

   s_9_8 in AdderSum  [s_9_7, And[a_c02, b_c07], c_8_7] and
   c_9_8 in AdderCarry[s_9_7, And[a_c02, b_c07], c_8_7] and

   s_9_9 in AdderSum  [s_9_8, And[a_c01, b_c08], c_8_8] and
   c_9_9 in AdderCarry[s_9_8, And[a_c01, b_c08], c_8_8] and

   s_9_10 in AdderSum  [s_9_9, And[a_c00, b_c09], c_8_9] and
   c_9_10 in AdderCarry[s_9_9, And[a_c00, b_c09], c_8_9] and

   s_10_0 in AdderSum  [And[a_c10,b.b31], And[a.b31, b_c10], false] and
   c_10_0 in AdderCarry[And[a_c10,b.b31], And[a.b31, b_c10], false] and

   s_10_1 in AdderSum  [s_10_0, And[a_c10, b_c00], c_9_0] and
   c_10_1 in AdderCarry[s_10_0, And[a_c10, b_c00], c_9_0] and

   s_10_2 in AdderSum  [s_10_1, And[a_c09, b_c01], c_9_1] and
   c_10_2 in AdderCarry[s_10_1, And[a_c09, b_c01], c_9_1] and

   s_10_3 in AdderSum  [s_10_2, And[a_c08, b_c02], c_9_2] and
   c_10_3 in AdderCarry[s_10_2, And[a_c08, b_c02], c_9_2] and

   s_10_4 in AdderSum  [s_10_3, And[a_c07, b_c03], c_9_3] and
   c_10_4 in AdderCarry[s_10_3, And[a_c07, b_c03], c_9_3] and

   s_10_5 in AdderSum  [s_10_4, And[a_c06, b_c04], c_9_4] and
   c_10_5 in AdderCarry[s_10_4, And[a_c06, b_c04], c_9_4] and

   s_10_6 in AdderSum  [s_10_5, And[a_c05, b_c05], c_9_5] and
   c_10_6 in AdderCarry[s_10_5, And[a_c05, b_c05], c_9_5] and

   s_10_7 in AdderSum  [s_10_6, And[a_c04, b_c06], c_9_6] and
   c_10_7 in AdderCarry[s_10_6, And[a_c04, b_c06], c_9_6] and

   s_10_8 in AdderSum  [s_10_7, And[a_c03, b_c07], c_9_7] and
   c_10_8 in AdderCarry[s_10_7, And[a_c03, b_c07], c_9_7] and

   s_10_9 in AdderSum  [s_10_8, And[a_c02, b_c08], c_9_8] and
   c_10_9 in AdderCarry[s_10_8, And[a_c02, b_c08], c_9_8] and

   s_10_10 in AdderSum  [s_10_9, And[a_c01, b_c09], c_9_9] and
   c_10_10 in AdderCarry[s_10_9, And[a_c01, b_c09], c_9_9] and

   s_10_11 in AdderSum  [s_10_10, And[a_c00, b_c10], c_9_10] and
   c_10_11 in AdderCarry[s_10_10, And[a_c00, b_c10], c_9_10] and

   s_11_0 in AdderSum  [And[a_c11,b.b31], And[a.b31, b_c11], false] and
   c_11_0 in AdderCarry[And[a_c11,b.b31], And[a.b31, b_c11], false] and

   s_11_1 in AdderSum  [s_11_0, And[a_c11, b_c00], c_10_0] and
   c_11_1 in AdderCarry[s_11_0, And[a_c11, b_c00], c_10_0] and

   s_11_2 in AdderSum  [s_11_1, And[a_c10, b_c01], c_10_1] and
   c_11_2 in AdderCarry[s_11_1, And[a_c10, b_c01], c_10_1] and

   s_11_3 in AdderSum  [s_11_2, And[a_c09, b_c02], c_10_2] and
   c_11_3 in AdderCarry[s_11_2, And[a_c09, b_c02], c_10_2] and

   s_11_4 in AdderSum  [s_11_3, And[a_c08, b_c03], c_10_3] and
   c_11_4 in AdderCarry[s_11_3, And[a_c08, b_c03], c_10_3] and

   s_11_5 in AdderSum  [s_11_4, And[a_c07, b_c04], c_10_4] and
   c_11_5 in AdderCarry[s_11_4, And[a_c07, b_c04], c_10_4] and

   s_11_6 in AdderSum  [s_11_5, And[a_c06, b_c05], c_10_5] and
   c_11_6 in AdderCarry[s_11_5, And[a_c06, b_c05], c_10_5] and

   s_11_7 in AdderSum  [s_11_6, And[a_c05, b_c06], c_10_6] and
   c_11_7 in AdderCarry[s_11_6, And[a_c05, b_c06], c_10_6] and

   s_11_8 in AdderSum  [s_11_7, And[a_c04, b_c07], c_10_7] and
   c_11_8 in AdderCarry[s_11_7, And[a_c04, b_c07], c_10_7] and

   s_11_9 in AdderSum  [s_11_8, And[a_c03, b_c08], c_10_8] and
   c_11_9 in AdderCarry[s_11_8, And[a_c03, b_c08], c_10_8] and

   s_11_10 in AdderSum  [s_11_9, And[a_c02, b_c09], c_10_9] and
   c_11_10 in AdderCarry[s_11_9, And[a_c02, b_c09], c_10_9] and

   s_11_11 in AdderSum  [s_11_10, And[a_c01, b_c10], c_10_10] and
   c_11_11 in AdderCarry[s_11_10, And[a_c01, b_c10], c_10_10] and

   s_11_12 in AdderSum  [s_11_11, And[a_c00, b_c11], c_10_11] and
   c_11_12 in AdderCarry[s_11_11, And[a_c00, b_c11], c_10_11] and

   s_12_0 in AdderSum  [And[a_c12,b.b31], And[a.b31, b_c12], false] and
   c_12_0 in AdderCarry[And[a_c12,b.b31], And[a.b31, b_c12], false] and

   s_12_1 in AdderSum  [s_12_0, And[a_c12, b_c00], c_11_0] and
   c_12_1 in AdderCarry[s_12_0, And[a_c12, b_c00], c_11_0] and

   s_12_2 in AdderSum  [s_12_1, And[a_c11, b_c01], c_11_1] and
   c_12_2 in AdderCarry[s_12_1, And[a_c11, b_c01], c_11_1] and

   s_12_3 in AdderSum  [s_12_2, And[a_c10, b_c02], c_11_2] and
   c_12_3 in AdderCarry[s_12_2, And[a_c10, b_c02], c_11_2] and

   s_12_4 in AdderSum  [s_12_3, And[a_c09, b_c03], c_11_3] and
   c_12_4 in AdderCarry[s_12_3, And[a_c09, b_c03], c_11_3] and

   s_12_5 in AdderSum  [s_12_4, And[a_c08, b_c04], c_11_4] and
   c_12_5 in AdderCarry[s_12_4, And[a_c08, b_c04], c_11_4] and

   s_12_6 in AdderSum  [s_12_5, And[a_c07, b_c05], c_11_5] and
   c_12_6 in AdderCarry[s_12_5, And[a_c07, b_c05], c_11_5] and

   s_12_7 in AdderSum  [s_12_6, And[a_c06, b_c06], c_11_6] and
   c_12_7 in AdderCarry[s_12_6, And[a_c06, b_c06], c_11_6] and

   s_12_8 in AdderSum  [s_12_7, And[a_c05, b_c07], c_11_7] and
   c_12_8 in AdderCarry[s_12_7, And[a_c05, b_c07], c_11_7] and

   s_12_9 in AdderSum  [s_12_8, And[a_c04, b_c08], c_11_8] and
   c_12_9 in AdderCarry[s_12_8, And[a_c04, b_c08], c_11_8] and

   s_12_10 in AdderSum  [s_12_9, And[a_c03, b_c09], c_11_9] and
   c_12_10 in AdderCarry[s_12_9, And[a_c03, b_c09], c_11_9] and

   s_12_11 in AdderSum  [s_12_10, And[a_c02, b_c10], c_11_10] and
   c_12_11 in AdderCarry[s_12_10, And[a_c02, b_c10], c_11_10] and

   s_12_12 in AdderSum  [s_12_11, And[a_c01, b_c11], c_11_11] and
   c_12_12 in AdderCarry[s_12_11, And[a_c01, b_c11], c_11_11] and

   s_12_13 in AdderSum  [s_12_12, And[a_c00, b_c12], c_11_12] and
   c_12_13 in AdderCarry[s_12_12, And[a_c00, b_c12], c_11_12] and

   s_13_0 in AdderSum  [And[a_c13,b.b31], And[a.b31, b_c13], false] and
   c_13_0 in AdderCarry[And[a_c13,b.b31], And[a.b31, b_c13], false] and

   s_13_1 in AdderSum  [s_13_0, And[a_c13, b_c00], c_12_0] and
   c_13_1 in AdderCarry[s_13_0, And[a_c13, b_c00], c_12_0] and

   s_13_2 in AdderSum  [s_13_1, And[a_c12, b_c01], c_12_1] and
   c_13_2 in AdderCarry[s_13_1, And[a_c12, b_c01], c_12_1] and

   s_13_3 in AdderSum  [s_13_2, And[a_c11, b_c02], c_12_2] and
   c_13_3 in AdderCarry[s_13_2, And[a_c11, b_c02], c_12_2] and

   s_13_4 in AdderSum  [s_13_3, And[a_c10, b_c03], c_12_3] and
   c_13_4 in AdderCarry[s_13_3, And[a_c10, b_c03], c_12_3] and

   s_13_5 in AdderSum  [s_13_4, And[a_c09, b_c04], c_12_4] and
   c_13_5 in AdderCarry[s_13_4, And[a_c09, b_c04], c_12_4] and

   s_13_6 in AdderSum  [s_13_5, And[a_c08, b_c05], c_12_5] and
   c_13_6 in AdderCarry[s_13_5, And[a_c08, b_c05], c_12_5] and

   s_13_7 in AdderSum  [s_13_6, And[a_c07, b_c06], c_12_6] and
   c_13_7 in AdderCarry[s_13_6, And[a_c07, b_c06], c_12_6] and

   s_13_8 in AdderSum  [s_13_7, And[a_c06, b_c07], c_12_7] and
   c_13_8 in AdderCarry[s_13_7, And[a_c06, b_c07], c_12_7] and

   s_13_9 in AdderSum  [s_13_8, And[a_c05, b_c08], c_12_8] and
   c_13_9 in AdderCarry[s_13_8, And[a_c05, b_c08], c_12_8] and

   s_13_10 in AdderSum  [s_13_9, And[a_c04, b_c09], c_12_9] and
   c_13_10 in AdderCarry[s_13_9, And[a_c04, b_c09], c_12_9] and

   s_13_11 in AdderSum  [s_13_10, And[a_c03, b_c10], c_12_10] and
   c_13_11 in AdderCarry[s_13_10, And[a_c03, b_c10], c_12_10] and

   s_13_12 in AdderSum  [s_13_11, And[a_c02, b_c11], c_12_11] and
   c_13_12 in AdderCarry[s_13_11, And[a_c02, b_c11], c_12_11] and

   s_13_13 in AdderSum  [s_13_12, And[a_c01, b_c12], c_12_12] and
   c_13_13 in AdderCarry[s_13_12, And[a_c01, b_c12], c_12_12] and

   s_13_14 in AdderSum  [s_13_13, And[a_c00, b_c13], c_12_13] and
   c_13_14 in AdderCarry[s_13_13, And[a_c00, b_c13], c_12_13] and

   s_14_0 in AdderSum  [And[a_c14,b.b31], And[a.b31, b_c14], false] and
   c_14_0 in AdderCarry[And[a_c14,b.b31], And[a.b31, b_c14], false] and

   s_14_1 in AdderSum  [s_14_0, And[a_c14, b_c00], c_13_0] and
   c_14_1 in AdderCarry[s_14_0, And[a_c14, b_c00], c_13_0] and

   s_14_2 in AdderSum  [s_14_1, And[a_c13, b_c01], c_13_1] and
   c_14_2 in AdderCarry[s_14_1, And[a_c13, b_c01], c_13_1] and

   s_14_3 in AdderSum  [s_14_2, And[a_c12, b_c02], c_13_2] and
   c_14_3 in AdderCarry[s_14_2, And[a_c12, b_c02], c_13_2] and

   s_14_4 in AdderSum  [s_14_3, And[a_c11, b_c03], c_13_3] and
   c_14_4 in AdderCarry[s_14_3, And[a_c11, b_c03], c_13_3] and

   s_14_5 in AdderSum  [s_14_4, And[a_c10, b_c04], c_13_4] and
   c_14_5 in AdderCarry[s_14_4, And[a_c10, b_c04], c_13_4] and

   s_14_6 in AdderSum  [s_14_5, And[a_c09, b_c05], c_13_5] and
   c_14_6 in AdderCarry[s_14_5, And[a_c09, b_c05], c_13_5] and

   s_14_7 in AdderSum  [s_14_6, And[a_c08, b_c06], c_13_6] and
   c_14_7 in AdderCarry[s_14_6, And[a_c08, b_c06], c_13_6] and

   s_14_8 in AdderSum  [s_14_7, And[a_c07, b_c07], c_13_7] and
   c_14_8 in AdderCarry[s_14_7, And[a_c07, b_c07], c_13_7] and

   s_14_9 in AdderSum  [s_14_8, And[a_c06, b_c08], c_13_8] and
   c_14_9 in AdderCarry[s_14_8, And[a_c06, b_c08], c_13_8] and

   s_14_10 in AdderSum  [s_14_9, And[a_c05, b_c09], c_13_9] and
   c_14_10 in AdderCarry[s_14_9, And[a_c05, b_c09], c_13_9] and

   s_14_11 in AdderSum  [s_14_10, And[a_c04, b_c10], c_13_10] and
   c_14_11 in AdderCarry[s_14_10, And[a_c04, b_c10], c_13_10] and

   s_14_12 in AdderSum  [s_14_11, And[a_c03, b_c11], c_13_11] and
   c_14_12 in AdderCarry[s_14_11, And[a_c03, b_c11], c_13_11] and

   s_14_13 in AdderSum  [s_14_12, And[a_c02, b_c12], c_13_12] and
   c_14_13 in AdderCarry[s_14_12, And[a_c02, b_c12], c_13_12] and

   s_14_14 in AdderSum  [s_14_13, And[a_c01, b_c13], c_13_13] and
   c_14_14 in AdderCarry[s_14_13, And[a_c01, b_c13], c_13_13] and

   s_14_15 in AdderSum  [s_14_14, And[a_c00, b_c14], c_13_14] and
   c_14_15 in AdderCarry[s_14_14, And[a_c00, b_c14], c_13_14] and

   s_15_0 in AdderSum  [And[a_c15,b.b31], And[a.b31, b_c15], false] and
   c_15_0 in AdderCarry[And[a_c15,b.b31], And[a.b31, b_c15], false] and

   s_15_1 in AdderSum  [s_15_0, And[a_c15, b_c00], c_14_0] and
   c_15_1 in AdderCarry[s_15_0, And[a_c15, b_c00], c_14_0] and

   s_15_2 in AdderSum  [s_15_1, And[a_c14, b_c01], c_14_1] and
   c_15_2 in AdderCarry[s_15_1, And[a_c14, b_c01], c_14_1] and

   s_15_3 in AdderSum  [s_15_2, And[a_c13, b_c02], c_14_2] and
   c_15_3 in AdderCarry[s_15_2, And[a_c13, b_c02], c_14_2] and

   s_15_4 in AdderSum  [s_15_3, And[a_c12, b_c03], c_14_3] and
   c_15_4 in AdderCarry[s_15_3, And[a_c12, b_c03], c_14_3] and

   s_15_5 in AdderSum  [s_15_4, And[a_c11, b_c04], c_14_4] and
   c_15_5 in AdderCarry[s_15_4, And[a_c11, b_c04], c_14_4] and

   s_15_6 in AdderSum  [s_15_5, And[a_c10, b_c05], c_14_5] and
   c_15_6 in AdderCarry[s_15_5, And[a_c10, b_c05], c_14_5] and

   s_15_7 in AdderSum  [s_15_6, And[a_c09, b_c06], c_14_6] and
   c_15_7 in AdderCarry[s_15_6, And[a_c09, b_c06], c_14_6] and

   s_15_8 in AdderSum  [s_15_7, And[a_c08, b_c07], c_14_7] and
   c_15_8 in AdderCarry[s_15_7, And[a_c08, b_c07], c_14_7] and

   s_15_9 in AdderSum  [s_15_8, And[a_c07, b_c08], c_14_8] and
   c_15_9 in AdderCarry[s_15_8, And[a_c07, b_c08], c_14_8] and

   s_15_10 in AdderSum  [s_15_9, And[a_c06, b_c09], c_14_9] and
   c_15_10 in AdderCarry[s_15_9, And[a_c06, b_c09], c_14_9] and

   s_15_11 in AdderSum  [s_15_10, And[a_c05, b_c10], c_14_10] and
   c_15_11 in AdderCarry[s_15_10, And[a_c05, b_c10], c_14_10] and

   s_15_12 in AdderSum  [s_15_11, And[a_c04, b_c11], c_14_11] and
   c_15_12 in AdderCarry[s_15_11, And[a_c04, b_c11], c_14_11] and

   s_15_13 in AdderSum  [s_15_12, And[a_c03, b_c12], c_14_12] and
   c_15_13 in AdderCarry[s_15_12, And[a_c03, b_c12], c_14_12] and

   s_15_14 in AdderSum  [s_15_13, And[a_c02, b_c13], c_14_13] and
   c_15_14 in AdderCarry[s_15_13, And[a_c02, b_c13], c_14_13] and

   s_15_15 in AdderSum  [s_15_14, And[a_c01, b_c14], c_14_14] and
   c_15_15 in AdderCarry[s_15_14, And[a_c01, b_c14], c_14_14] and

   s_15_16 in AdderSum  [s_15_15, And[a_c00, b_c15], c_14_15] and
   c_15_16 in AdderCarry[s_15_15, And[a_c00, b_c15], c_14_15] and

   s_16_0 in AdderSum  [And[a_c16,b.b31], And[a.b31, b_c16], false] and
   c_16_0 in AdderCarry[And[a_c16,b.b31], And[a.b31, b_c16], false] and

   s_16_1 in AdderSum  [s_16_0, And[a_c16, b_c00], c_15_0] and
   c_16_1 in AdderCarry[s_16_0, And[a_c16, b_c00], c_15_0] and

   s_16_2 in AdderSum  [s_16_1, And[a_c15, b_c01], c_15_1] and
   c_16_2 in AdderCarry[s_16_1, And[a_c15, b_c01], c_15_1] and

   s_16_3 in AdderSum  [s_16_2, And[a_c14, b_c02], c_15_2] and
   c_16_3 in AdderCarry[s_16_2, And[a_c14, b_c02], c_15_2] and

   s_16_4 in AdderSum  [s_16_3, And[a_c13, b_c03], c_15_3] and
   c_16_4 in AdderCarry[s_16_3, And[a_c13, b_c03], c_15_3] and

   s_16_5 in AdderSum  [s_16_4, And[a_c12, b_c04], c_15_4] and
   c_16_5 in AdderCarry[s_16_4, And[a_c12, b_c04], c_15_4] and

   s_16_6 in AdderSum  [s_16_5, And[a_c11, b_c05], c_15_5] and
   c_16_6 in AdderCarry[s_16_5, And[a_c11, b_c05], c_15_5] and

   s_16_7 in AdderSum  [s_16_6, And[a_c10, b_c06], c_15_6] and
   c_16_7 in AdderCarry[s_16_6, And[a_c10, b_c06], c_15_6] and

   s_16_8 in AdderSum  [s_16_7, And[a_c09, b_c07], c_15_7] and
   c_16_8 in AdderCarry[s_16_7, And[a_c09, b_c07], c_15_7] and

   s_16_9 in AdderSum  [s_16_8, And[a_c08, b_c08], c_15_8] and
   c_16_9 in AdderCarry[s_16_8, And[a_c08, b_c08], c_15_8] and

   s_16_10 in AdderSum  [s_16_9, And[a_c07, b_c09], c_15_9] and
   c_16_10 in AdderCarry[s_16_9, And[a_c07, b_c09], c_15_9] and

   s_16_11 in AdderSum  [s_16_10, And[a_c06, b_c10], c_15_10] and
   c_16_11 in AdderCarry[s_16_10, And[a_c06, b_c10], c_15_10] and

   s_16_12 in AdderSum  [s_16_11, And[a_c05, b_c11], c_15_11] and
   c_16_12 in AdderCarry[s_16_11, And[a_c05, b_c11], c_15_11] and

   s_16_13 in AdderSum  [s_16_12, And[a_c04, b_c12], c_15_12] and
   c_16_13 in AdderCarry[s_16_12, And[a_c04, b_c12], c_15_12] and

   s_16_14 in AdderSum  [s_16_13, And[a_c03, b_c13], c_15_13] and
   c_16_14 in AdderCarry[s_16_13, And[a_c03, b_c13], c_15_13] and

   s_16_15 in AdderSum  [s_16_14, And[a_c02, b_c14], c_15_14] and
   c_16_15 in AdderCarry[s_16_14, And[a_c02, b_c14], c_15_14] and

   s_16_16 in AdderSum  [s_16_15, And[a_c01, b_c15], c_15_15] and
   c_16_16 in AdderCarry[s_16_15, And[a_c01, b_c15], c_15_15] and

   s_16_17 in AdderSum  [s_16_16, And[a_c00, b_c16], c_15_16] and
   c_16_17 in AdderCarry[s_16_16, And[a_c00, b_c16], c_15_16] and

   s_17_0 in AdderSum  [And[a_c17,b.b31], And[a.b31, b_c17], false] and
   c_17_0 in AdderCarry[And[a_c17,b.b31], And[a.b31, b_c17], false] and

   s_17_1 in AdderSum  [s_17_0, And[a_c17, b_c00], c_16_0] and
   c_17_1 in AdderCarry[s_17_0, And[a_c17, b_c00], c_16_0] and

   s_17_2 in AdderSum  [s_17_1, And[a_c16, b_c01], c_16_1] and
   c_17_2 in AdderCarry[s_17_1, And[a_c16, b_c01], c_16_1] and

   s_17_3 in AdderSum  [s_17_2, And[a_c15, b_c02], c_16_2] and
   c_17_3 in AdderCarry[s_17_2, And[a_c15, b_c02], c_16_2] and

   s_17_4 in AdderSum  [s_17_3, And[a_c14, b_c03], c_16_3] and
   c_17_4 in AdderCarry[s_17_3, And[a_c14, b_c03], c_16_3] and

   s_17_5 in AdderSum  [s_17_4, And[a_c13, b_c04], c_16_4] and
   c_17_5 in AdderCarry[s_17_4, And[a_c13, b_c04], c_16_4] and

   s_17_6 in AdderSum  [s_17_5, And[a_c12, b_c05], c_16_5] and
   c_17_6 in AdderCarry[s_17_5, And[a_c12, b_c05], c_16_5] and

   s_17_7 in AdderSum  [s_17_6, And[a_c11, b_c06], c_16_6] and
   c_17_7 in AdderCarry[s_17_6, And[a_c11, b_c06], c_16_6] and

   s_17_8 in AdderSum  [s_17_7, And[a_c10, b_c07], c_16_7] and
   c_17_8 in AdderCarry[s_17_7, And[a_c10, b_c07], c_16_7] and

   s_17_9 in AdderSum  [s_17_8, And[a_c09, b_c08], c_16_8] and
   c_17_9 in AdderCarry[s_17_8, And[a_c09, b_c08], c_16_8] and

   s_17_10 in AdderSum  [s_17_9, And[a_c08, b_c09], c_16_9] and
   c_17_10 in AdderCarry[s_17_9, And[a_c08, b_c09], c_16_9] and

   s_17_11 in AdderSum  [s_17_10, And[a_c07, b_c10], c_16_10] and
   c_17_11 in AdderCarry[s_17_10, And[a_c07, b_c10], c_16_10] and

   s_17_12 in AdderSum  [s_17_11, And[a_c06, b_c11], c_16_11] and
   c_17_12 in AdderCarry[s_17_11, And[a_c06, b_c11], c_16_11] and

   s_17_13 in AdderSum  [s_17_12, And[a_c05, b_c12], c_16_12] and
   c_17_13 in AdderCarry[s_17_12, And[a_c05, b_c12], c_16_12] and

   s_17_14 in AdderSum  [s_17_13, And[a_c04, b_c13], c_16_13] and
   c_17_14 in AdderCarry[s_17_13, And[a_c04, b_c13], c_16_13] and

   s_17_15 in AdderSum  [s_17_14, And[a_c03, b_c14], c_16_14] and
   c_17_15 in AdderCarry[s_17_14, And[a_c03, b_c14], c_16_14] and

   s_17_16 in AdderSum  [s_17_15, And[a_c02, b_c15], c_16_15] and
   c_17_16 in AdderCarry[s_17_15, And[a_c02, b_c15], c_16_15] and

   s_17_17 in AdderSum  [s_17_16, And[a_c01, b_c16], c_16_16] and
   c_17_17 in AdderCarry[s_17_16, And[a_c01, b_c16], c_16_16] and

   s_17_18 in AdderSum  [s_17_17, And[a_c00, b_c17], c_16_17] and
   c_17_18 in AdderCarry[s_17_17, And[a_c00, b_c17], c_16_17] and

   s_18_0 in AdderSum  [And[a_c18,b.b31], And[a.b31, b_c18], false] and
   c_18_0 in AdderCarry[And[a_c18,b.b31], And[a.b31, b_c18], false] and

   s_18_1 in AdderSum  [s_18_0, And[a_c18, b_c00], c_17_0] and
   c_18_1 in AdderCarry[s_18_0, And[a_c18, b_c00], c_17_0] and

   s_18_2 in AdderSum  [s_18_1, And[a_c17, b_c01], c_17_1] and
   c_18_2 in AdderCarry[s_18_1, And[a_c17, b_c01], c_17_1] and

   s_18_3 in AdderSum  [s_18_2, And[a_c16, b_c02], c_17_2] and
   c_18_3 in AdderCarry[s_18_2, And[a_c16, b_c02], c_17_2] and

   s_18_4 in AdderSum  [s_18_3, And[a_c15, b_c03], c_17_3] and
   c_18_4 in AdderCarry[s_18_3, And[a_c15, b_c03], c_17_3] and

   s_18_5 in AdderSum  [s_18_4, And[a_c14, b_c04], c_17_4] and
   c_18_5 in AdderCarry[s_18_4, And[a_c14, b_c04], c_17_4] and

   s_18_6 in AdderSum  [s_18_5, And[a_c13, b_c05], c_17_5] and
   c_18_6 in AdderCarry[s_18_5, And[a_c13, b_c05], c_17_5] and

   s_18_7 in AdderSum  [s_18_6, And[a_c12, b_c06], c_17_6] and
   c_18_7 in AdderCarry[s_18_6, And[a_c12, b_c06], c_17_6] and

   s_18_8 in AdderSum  [s_18_7, And[a_c11, b_c07], c_17_7] and
   c_18_8 in AdderCarry[s_18_7, And[a_c11, b_c07], c_17_7] and

   s_18_9 in AdderSum  [s_18_8, And[a_c10, b_c08], c_17_8] and
   c_18_9 in AdderCarry[s_18_8, And[a_c10, b_c08], c_17_8] and

   s_18_10 in AdderSum  [s_18_9, And[a_c09, b_c09], c_17_9] and
   c_18_10 in AdderCarry[s_18_9, And[a_c09, b_c09], c_17_9] and

   s_18_11 in AdderSum  [s_18_10, And[a_c08, b_c10], c_17_10] and
   c_18_11 in AdderCarry[s_18_10, And[a_c08, b_c10], c_17_10] and

   s_18_12 in AdderSum  [s_18_11, And[a_c07, b_c11], c_17_11] and
   c_18_12 in AdderCarry[s_18_11, And[a_c07, b_c11], c_17_11] and

   s_18_13 in AdderSum  [s_18_12, And[a_c06, b_c12], c_17_12] and
   c_18_13 in AdderCarry[s_18_12, And[a_c06, b_c12], c_17_12] and

   s_18_14 in AdderSum  [s_18_13, And[a_c05, b_c13], c_17_13] and
   c_18_14 in AdderCarry[s_18_13, And[a_c05, b_c13], c_17_13] and

   s_18_15 in AdderSum  [s_18_14, And[a_c04, b_c14], c_17_14] and
   c_18_15 in AdderCarry[s_18_14, And[a_c04, b_c14], c_17_14] and

   s_18_16 in AdderSum  [s_18_15, And[a_c03, b_c15], c_17_15] and
   c_18_16 in AdderCarry[s_18_15, And[a_c03, b_c15], c_17_15] and

   s_18_17 in AdderSum  [s_18_16, And[a_c02, b_c16], c_17_16] and
   c_18_17 in AdderCarry[s_18_16, And[a_c02, b_c16], c_17_16] and

   s_18_18 in AdderSum  [s_18_17, And[a_c01, b_c17], c_17_17] and
   c_18_18 in AdderCarry[s_18_17, And[a_c01, b_c17], c_17_17] and

   s_18_19 in AdderSum  [s_18_18, And[a_c00, b_c18], c_17_18] and
   c_18_19 in AdderCarry[s_18_18, And[a_c00, b_c18], c_17_18] and

   s_19_0 in AdderSum  [And[a_c19,b.b31], And[a.b31, b_c19], false] and
   c_19_0 in AdderCarry[And[a_c19,b.b31], And[a.b31, b_c19], false] and

   s_19_1 in AdderSum  [s_19_0, And[a_c19, b_c00], c_18_0] and
   c_19_1 in AdderCarry[s_19_0, And[a_c19, b_c00], c_18_0] and

   s_19_2 in AdderSum  [s_19_1, And[a_c18, b_c01], c_18_1] and
   c_19_2 in AdderCarry[s_19_1, And[a_c18, b_c01], c_18_1] and

   s_19_3 in AdderSum  [s_19_2, And[a_c17, b_c02], c_18_2] and
   c_19_3 in AdderCarry[s_19_2, And[a_c17, b_c02], c_18_2] and

   s_19_4 in AdderSum  [s_19_3, And[a_c16, b_c03], c_18_3] and
   c_19_4 in AdderCarry[s_19_3, And[a_c16, b_c03], c_18_3] and

   s_19_5 in AdderSum  [s_19_4, And[a_c15, b_c04], c_18_4] and
   c_19_5 in AdderCarry[s_19_4, And[a_c15, b_c04], c_18_4] and

   s_19_6 in AdderSum  [s_19_5, And[a_c14, b_c05], c_18_5] and
   c_19_6 in AdderCarry[s_19_5, And[a_c14, b_c05], c_18_5] and

   s_19_7 in AdderSum  [s_19_6, And[a_c13, b_c06], c_18_6] and
   c_19_7 in AdderCarry[s_19_6, And[a_c13, b_c06], c_18_6] and

   s_19_8 in AdderSum  [s_19_7, And[a_c12, b_c07], c_18_7] and
   c_19_8 in AdderCarry[s_19_7, And[a_c12, b_c07], c_18_7] and

   s_19_9 in AdderSum  [s_19_8, And[a_c11, b_c08], c_18_8] and
   c_19_9 in AdderCarry[s_19_8, And[a_c11, b_c08], c_18_8] and

   s_19_10 in AdderSum  [s_19_9, And[a_c10, b_c09], c_18_9] and
   c_19_10 in AdderCarry[s_19_9, And[a_c10, b_c09], c_18_9] and

   s_19_11 in AdderSum  [s_19_10, And[a_c09, b_c10], c_18_10] and
   c_19_11 in AdderCarry[s_19_10, And[a_c09, b_c10], c_18_10] and

   s_19_12 in AdderSum  [s_19_11, And[a_c08, b_c11], c_18_11] and
   c_19_12 in AdderCarry[s_19_11, And[a_c08, b_c11], c_18_11] and

   s_19_13 in AdderSum  [s_19_12, And[a_c07, b_c12], c_18_12] and
   c_19_13 in AdderCarry[s_19_12, And[a_c07, b_c12], c_18_12] and

   s_19_14 in AdderSum  [s_19_13, And[a_c06, b_c13], c_18_13] and
   c_19_14 in AdderCarry[s_19_13, And[a_c06, b_c13], c_18_13] and

   s_19_15 in AdderSum  [s_19_14, And[a_c05, b_c14], c_18_14] and
   c_19_15 in AdderCarry[s_19_14, And[a_c05, b_c14], c_18_14] and

   s_19_16 in AdderSum  [s_19_15, And[a_c04, b_c15], c_18_15] and
   c_19_16 in AdderCarry[s_19_15, And[a_c04, b_c15], c_18_15] and

   s_19_17 in AdderSum  [s_19_16, And[a_c03, b_c16], c_18_16] and
   c_19_17 in AdderCarry[s_19_16, And[a_c03, b_c16], c_18_16] and

   s_19_18 in AdderSum  [s_19_17, And[a_c02, b_c17], c_18_17] and
   c_19_18 in AdderCarry[s_19_17, And[a_c02, b_c17], c_18_17] and

   s_19_19 in AdderSum  [s_19_18, And[a_c01, b_c18], c_18_18] and
   c_19_19 in AdderCarry[s_19_18, And[a_c01, b_c18], c_18_18] and

   s_19_20 in AdderSum  [s_19_19, And[a_c00, b_c19], c_18_19] and
   c_19_20 in AdderCarry[s_19_19, And[a_c00, b_c19], c_18_19] and

   s_20_0 in AdderSum  [And[a_c20,b.b31], And[a.b31, b_c20], false] and
   c_20_0 in AdderCarry[And[a_c20,b.b31], And[a.b31, b_c20], false] and

   s_20_1 in AdderSum  [s_20_0, And[a_c20, b_c00], c_19_0] and
   c_20_1 in AdderCarry[s_20_0, And[a_c20, b_c00], c_19_0] and

   s_20_2 in AdderSum  [s_20_1, And[a_c19, b_c01], c_19_1] and
   c_20_2 in AdderCarry[s_20_1, And[a_c19, b_c01], c_19_1] and

   s_20_3 in AdderSum  [s_20_2, And[a_c18, b_c02], c_19_2] and
   c_20_3 in AdderCarry[s_20_2, And[a_c18, b_c02], c_19_2] and

   s_20_4 in AdderSum  [s_20_3, And[a_c17, b_c03], c_19_3] and
   c_20_4 in AdderCarry[s_20_3, And[a_c17, b_c03], c_19_3] and

   s_20_5 in AdderSum  [s_20_4, And[a_c16, b_c04], c_19_4] and
   c_20_5 in AdderCarry[s_20_4, And[a_c16, b_c04], c_19_4] and

   s_20_6 in AdderSum  [s_20_5, And[a_c15, b_c05], c_19_5] and
   c_20_6 in AdderCarry[s_20_5, And[a_c15, b_c05], c_19_5] and

   s_20_7 in AdderSum  [s_20_6, And[a_c14, b_c06], c_19_6] and
   c_20_7 in AdderCarry[s_20_6, And[a_c14, b_c06], c_19_6] and

   s_20_8 in AdderSum  [s_20_7, And[a_c13, b_c07], c_19_7] and
   c_20_8 in AdderCarry[s_20_7, And[a_c13, b_c07], c_19_7] and

   s_20_9 in AdderSum  [s_20_8, And[a_c12, b_c08], c_19_8] and
   c_20_9 in AdderCarry[s_20_8, And[a_c12, b_c08], c_19_8] and

   s_20_10 in AdderSum  [s_20_9, And[a_c11, b_c09], c_19_9] and
   c_20_10 in AdderCarry[s_20_9, And[a_c11, b_c09], c_19_9] and

   s_20_11 in AdderSum  [s_20_10, And[a_c10, b_c10], c_19_10] and
   c_20_11 in AdderCarry[s_20_10, And[a_c10, b_c10], c_19_10] and

   s_20_12 in AdderSum  [s_20_11, And[a_c09, b_c11], c_19_11] and
   c_20_12 in AdderCarry[s_20_11, And[a_c09, b_c11], c_19_11] and

   s_20_13 in AdderSum  [s_20_12, And[a_c08, b_c12], c_19_12] and
   c_20_13 in AdderCarry[s_20_12, And[a_c08, b_c12], c_19_12] and

   s_20_14 in AdderSum  [s_20_13, And[a_c07, b_c13], c_19_13] and
   c_20_14 in AdderCarry[s_20_13, And[a_c07, b_c13], c_19_13] and

   s_20_15 in AdderSum  [s_20_14, And[a_c06, b_c14], c_19_14] and
   c_20_15 in AdderCarry[s_20_14, And[a_c06, b_c14], c_19_14] and

   s_20_16 in AdderSum  [s_20_15, And[a_c05, b_c15], c_19_15] and
   c_20_16 in AdderCarry[s_20_15, And[a_c05, b_c15], c_19_15] and

   s_20_17 in AdderSum  [s_20_16, And[a_c04, b_c16], c_19_16] and
   c_20_17 in AdderCarry[s_20_16, And[a_c04, b_c16], c_19_16] and

   s_20_18 in AdderSum  [s_20_17, And[a_c03, b_c17], c_19_17] and
   c_20_18 in AdderCarry[s_20_17, And[a_c03, b_c17], c_19_17] and

   s_20_19 in AdderSum  [s_20_18, And[a_c02, b_c18], c_19_18] and
   c_20_19 in AdderCarry[s_20_18, And[a_c02, b_c18], c_19_18] and

   s_20_20 in AdderSum  [s_20_19, And[a_c01, b_c19], c_19_19] and
   c_20_20 in AdderCarry[s_20_19, And[a_c01, b_c19], c_19_19] and

   s_20_21 in AdderSum  [s_20_20, And[a_c00, b_c20], c_19_20] and
   c_20_21 in AdderCarry[s_20_20, And[a_c00, b_c20], c_19_20] and

   s_21_0 in AdderSum  [And[a_c21,b.b31], And[a.b31, b_c21], false] and
   c_21_0 in AdderCarry[And[a_c21,b.b31], And[a.b31, b_c21], false] and

   s_21_1 in AdderSum  [s_21_0, And[a_c21, b_c00], c_20_0] and
   c_21_1 in AdderCarry[s_21_0, And[a_c21, b_c00], c_20_0] and

   s_21_2 in AdderSum  [s_21_1, And[a_c20, b_c01], c_20_1] and
   c_21_2 in AdderCarry[s_21_1, And[a_c20, b_c01], c_20_1] and

   s_21_3 in AdderSum  [s_21_2, And[a_c19, b_c02], c_20_2] and
   c_21_3 in AdderCarry[s_21_2, And[a_c19, b_c02], c_20_2] and

   s_21_4 in AdderSum  [s_21_3, And[a_c18, b_c03], c_20_3] and
   c_21_4 in AdderCarry[s_21_3, And[a_c18, b_c03], c_20_3] and

   s_21_5 in AdderSum  [s_21_4, And[a_c17, b_c04], c_20_4] and
   c_21_5 in AdderCarry[s_21_4, And[a_c17, b_c04], c_20_4] and

   s_21_6 in AdderSum  [s_21_5, And[a_c16, b_c05], c_20_5] and
   c_21_6 in AdderCarry[s_21_5, And[a_c16, b_c05], c_20_5] and

   s_21_7 in AdderSum  [s_21_6, And[a_c15, b_c06], c_20_6] and
   c_21_7 in AdderCarry[s_21_6, And[a_c15, b_c06], c_20_6] and

   s_21_8 in AdderSum  [s_21_7, And[a_c14, b_c07], c_20_7] and
   c_21_8 in AdderCarry[s_21_7, And[a_c14, b_c07], c_20_7] and

   s_21_9 in AdderSum  [s_21_8, And[a_c13, b_c08], c_20_8] and
   c_21_9 in AdderCarry[s_21_8, And[a_c13, b_c08], c_20_8] and

   s_21_10 in AdderSum  [s_21_9, And[a_c12, b_c09], c_20_9] and
   c_21_10 in AdderCarry[s_21_9, And[a_c12, b_c09], c_20_9] and

   s_21_11 in AdderSum  [s_21_10, And[a_c11, b_c10], c_20_10] and
   c_21_11 in AdderCarry[s_21_10, And[a_c11, b_c10], c_20_10] and

   s_21_12 in AdderSum  [s_21_11, And[a_c10, b_c11], c_20_11] and
   c_21_12 in AdderCarry[s_21_11, And[a_c10, b_c11], c_20_11] and

   s_21_13 in AdderSum  [s_21_12, And[a_c09, b_c12], c_20_12] and
   c_21_13 in AdderCarry[s_21_12, And[a_c09, b_c12], c_20_12] and

   s_21_14 in AdderSum  [s_21_13, And[a_c08, b_c13], c_20_13] and
   c_21_14 in AdderCarry[s_21_13, And[a_c08, b_c13], c_20_13] and

   s_21_15 in AdderSum  [s_21_14, And[a_c07, b_c14], c_20_14] and
   c_21_15 in AdderCarry[s_21_14, And[a_c07, b_c14], c_20_14] and

   s_21_16 in AdderSum  [s_21_15, And[a_c06, b_c15], c_20_15] and
   c_21_16 in AdderCarry[s_21_15, And[a_c06, b_c15], c_20_15] and

   s_21_17 in AdderSum  [s_21_16, And[a_c05, b_c16], c_20_16] and
   c_21_17 in AdderCarry[s_21_16, And[a_c05, b_c16], c_20_16] and

   s_21_18 in AdderSum  [s_21_17, And[a_c04, b_c17], c_20_17] and
   c_21_18 in AdderCarry[s_21_17, And[a_c04, b_c17], c_20_17] and

   s_21_19 in AdderSum  [s_21_18, And[a_c03, b_c18], c_20_18] and
   c_21_19 in AdderCarry[s_21_18, And[a_c03, b_c18], c_20_18] and

   s_21_20 in AdderSum  [s_21_19, And[a_c02, b_c19], c_20_19] and
   c_21_20 in AdderCarry[s_21_19, And[a_c02, b_c19], c_20_19] and

   s_21_21 in AdderSum  [s_21_20, And[a_c01, b_c20], c_20_20] and
   c_21_21 in AdderCarry[s_21_20, And[a_c01, b_c20], c_20_20] and

   s_21_22 in AdderSum  [s_21_21, And[a_c00, b_c21], c_20_21] and
   c_21_22 in AdderCarry[s_21_21, And[a_c00, b_c21], c_20_21] and

   s_22_0 in AdderSum  [And[a_c22,b.b31], And[a.b31, b_c22], false] and
   c_22_0 in AdderCarry[And[a_c22,b.b31], And[a.b31, b_c22], false] and

   s_22_1 in AdderSum  [s_22_0, And[a_c22, b_c00], c_21_0] and
   c_22_1 in AdderCarry[s_22_0, And[a_c22, b_c00], c_21_0] and

   s_22_2 in AdderSum  [s_22_1, And[a_c21, b_c01], c_21_1] and
   c_22_2 in AdderCarry[s_22_1, And[a_c21, b_c01], c_21_1] and

   s_22_3 in AdderSum  [s_22_2, And[a_c20, b_c02], c_21_2] and
   c_22_3 in AdderCarry[s_22_2, And[a_c20, b_c02], c_21_2] and

   s_22_4 in AdderSum  [s_22_3, And[a_c19, b_c03], c_21_3] and
   c_22_4 in AdderCarry[s_22_3, And[a_c19, b_c03], c_21_3] and

   s_22_5 in AdderSum  [s_22_4, And[a_c18, b_c04], c_21_4] and
   c_22_5 in AdderCarry[s_22_4, And[a_c18, b_c04], c_21_4] and

   s_22_6 in AdderSum  [s_22_5, And[a_c17, b_c05], c_21_5] and
   c_22_6 in AdderCarry[s_22_5, And[a_c17, b_c05], c_21_5] and

   s_22_7 in AdderSum  [s_22_6, And[a_c16, b_c06], c_21_6] and
   c_22_7 in AdderCarry[s_22_6, And[a_c16, b_c06], c_21_6] and

   s_22_8 in AdderSum  [s_22_7, And[a_c15, b_c07], c_21_7] and
   c_22_8 in AdderCarry[s_22_7, And[a_c15, b_c07], c_21_7] and

   s_22_9 in AdderSum  [s_22_8, And[a_c14, b_c08], c_21_8] and
   c_22_9 in AdderCarry[s_22_8, And[a_c14, b_c08], c_21_8] and

   s_22_10 in AdderSum  [s_22_9, And[a_c13, b_c09], c_21_9] and
   c_22_10 in AdderCarry[s_22_9, And[a_c13, b_c09], c_21_9] and

   s_22_11 in AdderSum  [s_22_10, And[a_c12, b_c10], c_21_10] and
   c_22_11 in AdderCarry[s_22_10, And[a_c12, b_c10], c_21_10] and

   s_22_12 in AdderSum  [s_22_11, And[a_c11, b_c11], c_21_11] and
   c_22_12 in AdderCarry[s_22_11, And[a_c11, b_c11], c_21_11] and

   s_22_13 in AdderSum  [s_22_12, And[a_c10, b_c12], c_21_12] and
   c_22_13 in AdderCarry[s_22_12, And[a_c10, b_c12], c_21_12] and

   s_22_14 in AdderSum  [s_22_13, And[a_c09, b_c13], c_21_13] and
   c_22_14 in AdderCarry[s_22_13, And[a_c09, b_c13], c_21_13] and

   s_22_15 in AdderSum  [s_22_14, And[a_c08, b_c14], c_21_14] and
   c_22_15 in AdderCarry[s_22_14, And[a_c08, b_c14], c_21_14] and

   s_22_16 in AdderSum  [s_22_15, And[a_c07, b_c15], c_21_15] and
   c_22_16 in AdderCarry[s_22_15, And[a_c07, b_c15], c_21_15] and

   s_22_17 in AdderSum  [s_22_16, And[a_c06, b_c16], c_21_16] and
   c_22_17 in AdderCarry[s_22_16, And[a_c06, b_c16], c_21_16] and

   s_22_18 in AdderSum  [s_22_17, And[a_c05, b_c17], c_21_17] and
   c_22_18 in AdderCarry[s_22_17, And[a_c05, b_c17], c_21_17] and

   s_22_19 in AdderSum  [s_22_18, And[a_c04, b_c18], c_21_18] and
   c_22_19 in AdderCarry[s_22_18, And[a_c04, b_c18], c_21_18] and

   s_22_20 in AdderSum  [s_22_19, And[a_c03, b_c19], c_21_19] and
   c_22_20 in AdderCarry[s_22_19, And[a_c03, b_c19], c_21_19] and

   s_22_21 in AdderSum  [s_22_20, And[a_c02, b_c20], c_21_20] and
   c_22_21 in AdderCarry[s_22_20, And[a_c02, b_c20], c_21_20] and

   s_22_22 in AdderSum  [s_22_21, And[a_c01, b_c21], c_21_21] and
   c_22_22 in AdderCarry[s_22_21, And[a_c01, b_c21], c_21_21] and

   s_22_23 in AdderSum  [s_22_22, And[a_c00, b_c22], c_21_22] and
   c_22_23 in AdderCarry[s_22_22, And[a_c00, b_c22], c_21_22] and

   s_23_0 in AdderSum  [And[a_c23,b.b31], And[a.b31, b_c23], false] and
   c_23_0 in AdderCarry[And[a_c23,b.b31], And[a.b31, b_c23], false] and

   s_23_1 in AdderSum  [s_23_0, And[a_c23, b_c00], c_22_0] and
   c_23_1 in AdderCarry[s_23_0, And[a_c23, b_c00], c_22_0] and

   s_23_2 in AdderSum  [s_23_1, And[a_c22, b_c01], c_22_1] and
   c_23_2 in AdderCarry[s_23_1, And[a_c22, b_c01], c_22_1] and

   s_23_3 in AdderSum  [s_23_2, And[a_c21, b_c02], c_22_2] and
   c_23_3 in AdderCarry[s_23_2, And[a_c21, b_c02], c_22_2] and

   s_23_4 in AdderSum  [s_23_3, And[a_c20, b_c03], c_22_3] and
   c_23_4 in AdderCarry[s_23_3, And[a_c20, b_c03], c_22_3] and

   s_23_5 in AdderSum  [s_23_4, And[a_c19, b_c04], c_22_4] and
   c_23_5 in AdderCarry[s_23_4, And[a_c19, b_c04], c_22_4] and

   s_23_6 in AdderSum  [s_23_5, And[a_c18, b_c05], c_22_5] and
   c_23_6 in AdderCarry[s_23_5, And[a_c18, b_c05], c_22_5] and

   s_23_7 in AdderSum  [s_23_6, And[a_c17, b_c06], c_22_6] and
   c_23_7 in AdderCarry[s_23_6, And[a_c17, b_c06], c_22_6] and

   s_23_8 in AdderSum  [s_23_7, And[a_c16, b_c07], c_22_7] and
   c_23_8 in AdderCarry[s_23_7, And[a_c16, b_c07], c_22_7] and

   s_23_9 in AdderSum  [s_23_8, And[a_c15, b_c08], c_22_8] and
   c_23_9 in AdderCarry[s_23_8, And[a_c15, b_c08], c_22_8] and

   s_23_10 in AdderSum  [s_23_9, And[a_c14, b_c09], c_22_9] and
   c_23_10 in AdderCarry[s_23_9, And[a_c14, b_c09], c_22_9] and

   s_23_11 in AdderSum  [s_23_10, And[a_c13, b_c10], c_22_10] and
   c_23_11 in AdderCarry[s_23_10, And[a_c13, b_c10], c_22_10] and

   s_23_12 in AdderSum  [s_23_11, And[a_c12, b_c11], c_22_11] and
   c_23_12 in AdderCarry[s_23_11, And[a_c12, b_c11], c_22_11] and

   s_23_13 in AdderSum  [s_23_12, And[a_c11, b_c12], c_22_12] and
   c_23_13 in AdderCarry[s_23_12, And[a_c11, b_c12], c_22_12] and

   s_23_14 in AdderSum  [s_23_13, And[a_c10, b_c13], c_22_13] and
   c_23_14 in AdderCarry[s_23_13, And[a_c10, b_c13], c_22_13] and

   s_23_15 in AdderSum  [s_23_14, And[a_c09, b_c14], c_22_14] and
   c_23_15 in AdderCarry[s_23_14, And[a_c09, b_c14], c_22_14] and

   s_23_16 in AdderSum  [s_23_15, And[a_c08, b_c15], c_22_15] and
   c_23_16 in AdderCarry[s_23_15, And[a_c08, b_c15], c_22_15] and

   s_23_17 in AdderSum  [s_23_16, And[a_c07, b_c16], c_22_16] and
   c_23_17 in AdderCarry[s_23_16, And[a_c07, b_c16], c_22_16] and

   s_23_18 in AdderSum  [s_23_17, And[a_c06, b_c17], c_22_17] and
   c_23_18 in AdderCarry[s_23_17, And[a_c06, b_c17], c_22_17] and

   s_23_19 in AdderSum  [s_23_18, And[a_c05, b_c18], c_22_18] and
   c_23_19 in AdderCarry[s_23_18, And[a_c05, b_c18], c_22_18] and

   s_23_20 in AdderSum  [s_23_19, And[a_c04, b_c19], c_22_19] and
   c_23_20 in AdderCarry[s_23_19, And[a_c04, b_c19], c_22_19] and

   s_23_21 in AdderSum  [s_23_20, And[a_c03, b_c20], c_22_20] and
   c_23_21 in AdderCarry[s_23_20, And[a_c03, b_c20], c_22_20] and

   s_23_22 in AdderSum  [s_23_21, And[a_c02, b_c21], c_22_21] and
   c_23_22 in AdderCarry[s_23_21, And[a_c02, b_c21], c_22_21] and

   s_23_23 in AdderSum  [s_23_22, And[a_c01, b_c22], c_22_22] and
   c_23_23 in AdderCarry[s_23_22, And[a_c01, b_c22], c_22_22] and

   s_23_24 in AdderSum  [s_23_23, And[a_c00, b_c23], c_22_23] and
   c_23_24 in AdderCarry[s_23_23, And[a_c00, b_c23], c_22_23] and

   s_24_0 in AdderSum  [And[a_c24,b.b31], And[a.b31, b_c24], false] and
   c_24_0 in AdderCarry[And[a_c24,b.b31], And[a.b31, b_c24], false] and

   s_24_1 in AdderSum  [s_24_0, And[a_c24, b_c00], c_23_0] and
   c_24_1 in AdderCarry[s_24_0, And[a_c24, b_c00], c_23_0] and

   s_24_2 in AdderSum  [s_24_1, And[a_c23, b_c01], c_23_1] and
   c_24_2 in AdderCarry[s_24_1, And[a_c23, b_c01], c_23_1] and

   s_24_3 in AdderSum  [s_24_2, And[a_c22, b_c02], c_23_2] and
   c_24_3 in AdderCarry[s_24_2, And[a_c22, b_c02], c_23_2] and

   s_24_4 in AdderSum  [s_24_3, And[a_c21, b_c03], c_23_3] and
   c_24_4 in AdderCarry[s_24_3, And[a_c21, b_c03], c_23_3] and

   s_24_5 in AdderSum  [s_24_4, And[a_c20, b_c04], c_23_4] and
   c_24_5 in AdderCarry[s_24_4, And[a_c20, b_c04], c_23_4] and

   s_24_6 in AdderSum  [s_24_5, And[a_c19, b_c05], c_23_5] and
   c_24_6 in AdderCarry[s_24_5, And[a_c19, b_c05], c_23_5] and

   s_24_7 in AdderSum  [s_24_6, And[a_c18, b_c06], c_23_6] and
   c_24_7 in AdderCarry[s_24_6, And[a_c18, b_c06], c_23_6] and

   s_24_8 in AdderSum  [s_24_7, And[a_c17, b_c07], c_23_7] and
   c_24_8 in AdderCarry[s_24_7, And[a_c17, b_c07], c_23_7] and

   s_24_9 in AdderSum  [s_24_8, And[a_c16, b_c08], c_23_8] and
   c_24_9 in AdderCarry[s_24_8, And[a_c16, b_c08], c_23_8] and

   s_24_10 in AdderSum  [s_24_9, And[a_c15, b_c09], c_23_9] and
   c_24_10 in AdderCarry[s_24_9, And[a_c15, b_c09], c_23_9] and

   s_24_11 in AdderSum  [s_24_10, And[a_c14, b_c10], c_23_10] and
   c_24_11 in AdderCarry[s_24_10, And[a_c14, b_c10], c_23_10] and

   s_24_12 in AdderSum  [s_24_11, And[a_c13, b_c11], c_23_11] and
   c_24_12 in AdderCarry[s_24_11, And[a_c13, b_c11], c_23_11] and

   s_24_13 in AdderSum  [s_24_12, And[a_c12, b_c12], c_23_12] and
   c_24_13 in AdderCarry[s_24_12, And[a_c12, b_c12], c_23_12] and

   s_24_14 in AdderSum  [s_24_13, And[a_c11, b_c13], c_23_13] and
   c_24_14 in AdderCarry[s_24_13, And[a_c11, b_c13], c_23_13] and

   s_24_15 in AdderSum  [s_24_14, And[a_c10, b_c14], c_23_14] and
   c_24_15 in AdderCarry[s_24_14, And[a_c10, b_c14], c_23_14] and

   s_24_16 in AdderSum  [s_24_15, And[a_c09, b_c15], c_23_15] and
   c_24_16 in AdderCarry[s_24_15, And[a_c09, b_c15], c_23_15] and

   s_24_17 in AdderSum  [s_24_16, And[a_c08, b_c16], c_23_16] and
   c_24_17 in AdderCarry[s_24_16, And[a_c08, b_c16], c_23_16] and

   s_24_18 in AdderSum  [s_24_17, And[a_c07, b_c17], c_23_17] and
   c_24_18 in AdderCarry[s_24_17, And[a_c07, b_c17], c_23_17] and

   s_24_19 in AdderSum  [s_24_18, And[a_c06, b_c18], c_23_18] and
   c_24_19 in AdderCarry[s_24_18, And[a_c06, b_c18], c_23_18] and

   s_24_20 in AdderSum  [s_24_19, And[a_c05, b_c19], c_23_19] and
   c_24_20 in AdderCarry[s_24_19, And[a_c05, b_c19], c_23_19] and

   s_24_21 in AdderSum  [s_24_20, And[a_c04, b_c20], c_23_20] and
   c_24_21 in AdderCarry[s_24_20, And[a_c04, b_c20], c_23_20] and

   s_24_22 in AdderSum  [s_24_21, And[a_c03, b_c21], c_23_21] and
   c_24_22 in AdderCarry[s_24_21, And[a_c03, b_c21], c_23_21] and

   s_24_23 in AdderSum  [s_24_22, And[a_c02, b_c22], c_23_22] and
   c_24_23 in AdderCarry[s_24_22, And[a_c02, b_c22], c_23_22] and

   s_24_24 in AdderSum  [s_24_23, And[a_c01, b_c23], c_23_23] and
   c_24_24 in AdderCarry[s_24_23, And[a_c01, b_c23], c_23_23] and

   s_24_25 in AdderSum  [s_24_24, And[a_c00, b_c24], c_23_24] and
   c_24_25 in AdderCarry[s_24_24, And[a_c00, b_c24], c_23_24] and

   s_25_0 in AdderSum  [And[a_c25,b.b31], And[a.b31, b_c25], false] and
   c_25_0 in AdderCarry[And[a_c25,b.b31], And[a.b31, b_c25], false] and

   s_25_1 in AdderSum  [s_25_0, And[a_c25, b_c00], c_24_0] and
   c_25_1 in AdderCarry[s_25_0, And[a_c25, b_c00], c_24_0] and

   s_25_2 in AdderSum  [s_25_1, And[a_c24, b_c01], c_24_1] and
   c_25_2 in AdderCarry[s_25_1, And[a_c24, b_c01], c_24_1] and

   s_25_3 in AdderSum  [s_25_2, And[a_c23, b_c02], c_24_2] and
   c_25_3 in AdderCarry[s_25_2, And[a_c23, b_c02], c_24_2] and

   s_25_4 in AdderSum  [s_25_3, And[a_c22, b_c03], c_24_3] and
   c_25_4 in AdderCarry[s_25_3, And[a_c22, b_c03], c_24_3] and

   s_25_5 in AdderSum  [s_25_4, And[a_c21, b_c04], c_24_4] and
   c_25_5 in AdderCarry[s_25_4, And[a_c21, b_c04], c_24_4] and

   s_25_6 in AdderSum  [s_25_5, And[a_c20, b_c05], c_24_5] and
   c_25_6 in AdderCarry[s_25_5, And[a_c20, b_c05], c_24_5] and

   s_25_7 in AdderSum  [s_25_6, And[a_c19, b_c06], c_24_6] and
   c_25_7 in AdderCarry[s_25_6, And[a_c19, b_c06], c_24_6] and

   s_25_8 in AdderSum  [s_25_7, And[a_c18, b_c07], c_24_7] and
   c_25_8 in AdderCarry[s_25_7, And[a_c18, b_c07], c_24_7] and

   s_25_9 in AdderSum  [s_25_8, And[a_c17, b_c08], c_24_8] and
   c_25_9 in AdderCarry[s_25_8, And[a_c17, b_c08], c_24_8] and

   s_25_10 in AdderSum  [s_25_9, And[a_c16, b_c09], c_24_9] and
   c_25_10 in AdderCarry[s_25_9, And[a_c16, b_c09], c_24_9] and

   s_25_11 in AdderSum  [s_25_10, And[a_c15, b_c10], c_24_10] and
   c_25_11 in AdderCarry[s_25_10, And[a_c15, b_c10], c_24_10] and

   s_25_12 in AdderSum  [s_25_11, And[a_c14, b_c11], c_24_11] and
   c_25_12 in AdderCarry[s_25_11, And[a_c14, b_c11], c_24_11] and

   s_25_13 in AdderSum  [s_25_12, And[a_c13, b_c12], c_24_12] and
   c_25_13 in AdderCarry[s_25_12, And[a_c13, b_c12], c_24_12] and

   s_25_14 in AdderSum  [s_25_13, And[a_c12, b_c13], c_24_13] and
   c_25_14 in AdderCarry[s_25_13, And[a_c12, b_c13], c_24_13] and

   s_25_15 in AdderSum  [s_25_14, And[a_c11, b_c14], c_24_14] and
   c_25_15 in AdderCarry[s_25_14, And[a_c11, b_c14], c_24_14] and

   s_25_16 in AdderSum  [s_25_15, And[a_c10, b_c15], c_24_15] and
   c_25_16 in AdderCarry[s_25_15, And[a_c10, b_c15], c_24_15] and

   s_25_17 in AdderSum  [s_25_16, And[a_c09, b_c16], c_24_16] and
   c_25_17 in AdderCarry[s_25_16, And[a_c09, b_c16], c_24_16] and

   s_25_18 in AdderSum  [s_25_17, And[a_c08, b_c17], c_24_17] and
   c_25_18 in AdderCarry[s_25_17, And[a_c08, b_c17], c_24_17] and

   s_25_19 in AdderSum  [s_25_18, And[a_c07, b_c18], c_24_18] and
   c_25_19 in AdderCarry[s_25_18, And[a_c07, b_c18], c_24_18] and

   s_25_20 in AdderSum  [s_25_19, And[a_c06, b_c19], c_24_19] and
   c_25_20 in AdderCarry[s_25_19, And[a_c06, b_c19], c_24_19] and

   s_25_21 in AdderSum  [s_25_20, And[a_c05, b_c20], c_24_20] and
   c_25_21 in AdderCarry[s_25_20, And[a_c05, b_c20], c_24_20] and

   s_25_22 in AdderSum  [s_25_21, And[a_c04, b_c21], c_24_21] and
   c_25_22 in AdderCarry[s_25_21, And[a_c04, b_c21], c_24_21] and

   s_25_23 in AdderSum  [s_25_22, And[a_c03, b_c22], c_24_22] and
   c_25_23 in AdderCarry[s_25_22, And[a_c03, b_c22], c_24_22] and

   s_25_24 in AdderSum  [s_25_23, And[a_c02, b_c23], c_24_23] and
   c_25_24 in AdderCarry[s_25_23, And[a_c02, b_c23], c_24_23] and

   s_25_25 in AdderSum  [s_25_24, And[a_c01, b_c24], c_24_24] and
   c_25_25 in AdderCarry[s_25_24, And[a_c01, b_c24], c_24_24] and

   s_25_26 in AdderSum  [s_25_25, And[a_c00, b_c25], c_24_25] and
   c_25_26 in AdderCarry[s_25_25, And[a_c00, b_c25], c_24_25] and

   s_26_0 in AdderSum  [And[a_c26,b.b31], And[a.b31, b_c26], false] and
   c_26_0 in AdderCarry[And[a_c26,b.b31], And[a.b31, b_c26], false] and

   s_26_1 in AdderSum  [s_26_0, And[a_c26, b_c00], c_25_0] and
   c_26_1 in AdderCarry[s_26_0, And[a_c26, b_c00], c_25_0] and

   s_26_2 in AdderSum  [s_26_1, And[a_c25, b_c01], c_25_1] and
   c_26_2 in AdderCarry[s_26_1, And[a_c25, b_c01], c_25_1] and

   s_26_3 in AdderSum  [s_26_2, And[a_c24, b_c02], c_25_2] and
   c_26_3 in AdderCarry[s_26_2, And[a_c24, b_c02], c_25_2] and

   s_26_4 in AdderSum  [s_26_3, And[a_c23, b_c03], c_25_3] and
   c_26_4 in AdderCarry[s_26_3, And[a_c23, b_c03], c_25_3] and

   s_26_5 in AdderSum  [s_26_4, And[a_c22, b_c04], c_25_4] and
   c_26_5 in AdderCarry[s_26_4, And[a_c22, b_c04], c_25_4] and

   s_26_6 in AdderSum  [s_26_5, And[a_c21, b_c05], c_25_5] and
   c_26_6 in AdderCarry[s_26_5, And[a_c21, b_c05], c_25_5] and

   s_26_7 in AdderSum  [s_26_6, And[a_c20, b_c06], c_25_6] and
   c_26_7 in AdderCarry[s_26_6, And[a_c20, b_c06], c_25_6] and

   s_26_8 in AdderSum  [s_26_7, And[a_c19, b_c07], c_25_7] and
   c_26_8 in AdderCarry[s_26_7, And[a_c19, b_c07], c_25_7] and

   s_26_9 in AdderSum  [s_26_8, And[a_c18, b_c08], c_25_8] and
   c_26_9 in AdderCarry[s_26_8, And[a_c18, b_c08], c_25_8] and

   s_26_10 in AdderSum  [s_26_9, And[a_c17, b_c09], c_25_9] and
   c_26_10 in AdderCarry[s_26_9, And[a_c17, b_c09], c_25_9] and

   s_26_11 in AdderSum  [s_26_10, And[a_c16, b_c10], c_25_10] and
   c_26_11 in AdderCarry[s_26_10, And[a_c16, b_c10], c_25_10] and

   s_26_12 in AdderSum  [s_26_11, And[a_c15, b_c11], c_25_11] and
   c_26_12 in AdderCarry[s_26_11, And[a_c15, b_c11], c_25_11] and

   s_26_13 in AdderSum  [s_26_12, And[a_c14, b_c12], c_25_12] and
   c_26_13 in AdderCarry[s_26_12, And[a_c14, b_c12], c_25_12] and

   s_26_14 in AdderSum  [s_26_13, And[a_c13, b_c13], c_25_13] and
   c_26_14 in AdderCarry[s_26_13, And[a_c13, b_c13], c_25_13] and

   s_26_15 in AdderSum  [s_26_14, And[a_c12, b_c14], c_25_14] and
   c_26_15 in AdderCarry[s_26_14, And[a_c12, b_c14], c_25_14] and

   s_26_16 in AdderSum  [s_26_15, And[a_c11, b_c15], c_25_15] and
   c_26_16 in AdderCarry[s_26_15, And[a_c11, b_c15], c_25_15] and

   s_26_17 in AdderSum  [s_26_16, And[a_c10, b_c16], c_25_16] and
   c_26_17 in AdderCarry[s_26_16, And[a_c10, b_c16], c_25_16] and

   s_26_18 in AdderSum  [s_26_17, And[a_c09, b_c17], c_25_17] and
   c_26_18 in AdderCarry[s_26_17, And[a_c09, b_c17], c_25_17] and

   s_26_19 in AdderSum  [s_26_18, And[a_c08, b_c18], c_25_18] and
   c_26_19 in AdderCarry[s_26_18, And[a_c08, b_c18], c_25_18] and

   s_26_20 in AdderSum  [s_26_19, And[a_c07, b_c19], c_25_19] and
   c_26_20 in AdderCarry[s_26_19, And[a_c07, b_c19], c_25_19] and

   s_26_21 in AdderSum  [s_26_20, And[a_c06, b_c20], c_25_20] and
   c_26_21 in AdderCarry[s_26_20, And[a_c06, b_c20], c_25_20] and

   s_26_22 in AdderSum  [s_26_21, And[a_c05, b_c21], c_25_21] and
   c_26_22 in AdderCarry[s_26_21, And[a_c05, b_c21], c_25_21] and

   s_26_23 in AdderSum  [s_26_22, And[a_c04, b_c22], c_25_22] and
   c_26_23 in AdderCarry[s_26_22, And[a_c04, b_c22], c_25_22] and

   s_26_24 in AdderSum  [s_26_23, And[a_c03, b_c23], c_25_23] and
   c_26_24 in AdderCarry[s_26_23, And[a_c03, b_c23], c_25_23] and

   s_26_25 in AdderSum  [s_26_24, And[a_c02, b_c24], c_25_24] and
   c_26_25 in AdderCarry[s_26_24, And[a_c02, b_c24], c_25_24] and

   s_26_26 in AdderSum  [s_26_25, And[a_c01, b_c25], c_25_25] and
   c_26_26 in AdderCarry[s_26_25, And[a_c01, b_c25], c_25_25] and

   s_26_27 in AdderSum  [s_26_26, And[a_c00, b_c26], c_25_26] and
   c_26_27 in AdderCarry[s_26_26, And[a_c00, b_c26], c_25_26] and

   s_27_0 in AdderSum  [And[a_c27,b.b31], And[a.b31, b_c27], false] and
   c_27_0 in AdderCarry[And[a_c27,b.b31], And[a.b31, b_c27], false] and

   s_27_1 in AdderSum  [s_27_0, And[a_c27, b_c00], c_26_0] and
   c_27_1 in AdderCarry[s_27_0, And[a_c27, b_c00], c_26_0] and

   s_27_2 in AdderSum  [s_27_1, And[a_c26, b_c01], c_26_1] and
   c_27_2 in AdderCarry[s_27_1, And[a_c26, b_c01], c_26_1] and

   s_27_3 in AdderSum  [s_27_2, And[a_c25, b_c02], c_26_2] and
   c_27_3 in AdderCarry[s_27_2, And[a_c25, b_c02], c_26_2] and

   s_27_4 in AdderSum  [s_27_3, And[a_c24, b_c03], c_26_3] and
   c_27_4 in AdderCarry[s_27_3, And[a_c24, b_c03], c_26_3] and

   s_27_5 in AdderSum  [s_27_4, And[a_c23, b_c04], c_26_4] and
   c_27_5 in AdderCarry[s_27_4, And[a_c23, b_c04], c_26_4] and

   s_27_6 in AdderSum  [s_27_5, And[a_c22, b_c05], c_26_5] and
   c_27_6 in AdderCarry[s_27_5, And[a_c22, b_c05], c_26_5] and

   s_27_7 in AdderSum  [s_27_6, And[a_c21, b_c06], c_26_6] and
   c_27_7 in AdderCarry[s_27_6, And[a_c21, b_c06], c_26_6] and

   s_27_8 in AdderSum  [s_27_7, And[a_c20, b_c07], c_26_7] and
   c_27_8 in AdderCarry[s_27_7, And[a_c20, b_c07], c_26_7] and

   s_27_9 in AdderSum  [s_27_8, And[a_c19, b_c08], c_26_8] and
   c_27_9 in AdderCarry[s_27_8, And[a_c19, b_c08], c_26_8] and

   s_27_10 in AdderSum  [s_27_9, And[a_c18, b_c09], c_26_9] and
   c_27_10 in AdderCarry[s_27_9, And[a_c18, b_c09], c_26_9] and

   s_27_11 in AdderSum  [s_27_10, And[a_c17, b_c10], c_26_10] and
   c_27_11 in AdderCarry[s_27_10, And[a_c17, b_c10], c_26_10] and

   s_27_12 in AdderSum  [s_27_11, And[a_c16, b_c11], c_26_11] and
   c_27_12 in AdderCarry[s_27_11, And[a_c16, b_c11], c_26_11] and

   s_27_13 in AdderSum  [s_27_12, And[a_c15, b_c12], c_26_12] and
   c_27_13 in AdderCarry[s_27_12, And[a_c15, b_c12], c_26_12] and

   s_27_14 in AdderSum  [s_27_13, And[a_c14, b_c13], c_26_13] and
   c_27_14 in AdderCarry[s_27_13, And[a_c14, b_c13], c_26_13] and

   s_27_15 in AdderSum  [s_27_14, And[a_c13, b_c14], c_26_14] and
   c_27_15 in AdderCarry[s_27_14, And[a_c13, b_c14], c_26_14] and

   s_27_16 in AdderSum  [s_27_15, And[a_c12, b_c15], c_26_15] and
   c_27_16 in AdderCarry[s_27_15, And[a_c12, b_c15], c_26_15] and

   s_27_17 in AdderSum  [s_27_16, And[a_c11, b_c16], c_26_16] and
   c_27_17 in AdderCarry[s_27_16, And[a_c11, b_c16], c_26_16] and

   s_27_18 in AdderSum  [s_27_17, And[a_c10, b_c17], c_26_17] and
   c_27_18 in AdderCarry[s_27_17, And[a_c10, b_c17], c_26_17] and

   s_27_19 in AdderSum  [s_27_18, And[a_c09, b_c18], c_26_18] and
   c_27_19 in AdderCarry[s_27_18, And[a_c09, b_c18], c_26_18] and

   s_27_20 in AdderSum  [s_27_19, And[a_c08, b_c19], c_26_19] and
   c_27_20 in AdderCarry[s_27_19, And[a_c08, b_c19], c_26_19] and

   s_27_21 in AdderSum  [s_27_20, And[a_c07, b_c20], c_26_20] and
   c_27_21 in AdderCarry[s_27_20, And[a_c07, b_c20], c_26_20] and

   s_27_22 in AdderSum  [s_27_21, And[a_c06, b_c21], c_26_21] and
   c_27_22 in AdderCarry[s_27_21, And[a_c06, b_c21], c_26_21] and

   s_27_23 in AdderSum  [s_27_22, And[a_c05, b_c22], c_26_22] and
   c_27_23 in AdderCarry[s_27_22, And[a_c05, b_c22], c_26_22] and

   s_27_24 in AdderSum  [s_27_23, And[a_c04, b_c23], c_26_23] and
   c_27_24 in AdderCarry[s_27_23, And[a_c04, b_c23], c_26_23] and

   s_27_25 in AdderSum  [s_27_24, And[a_c03, b_c24], c_26_24] and
   c_27_25 in AdderCarry[s_27_24, And[a_c03, b_c24], c_26_24] and

   s_27_26 in AdderSum  [s_27_25, And[a_c02, b_c25], c_26_25] and
   c_27_26 in AdderCarry[s_27_25, And[a_c02, b_c25], c_26_25] and

   s_27_27 in AdderSum  [s_27_26, And[a_c01, b_c26], c_26_26] and
   c_27_27 in AdderCarry[s_27_26, And[a_c01, b_c26], c_26_26] and

   s_27_28 in AdderSum  [s_27_27, And[a_c00, b_c27], c_26_27] and
   c_27_28 in AdderCarry[s_27_27, And[a_c00, b_c27], c_26_27] and

   s_28_0 in AdderSum  [And[a_c28,b.b31], And[a.b31, b_c28], false] and
   c_28_0 in AdderCarry[And[a_c28,b.b31], And[a.b31, b_c28], false] and

   s_28_1 in AdderSum  [s_28_0, And[a_c28, b_c00], c_27_0] and
   c_28_1 in AdderCarry[s_28_0, And[a_c28, b_c00], c_27_0] and

   s_28_2 in AdderSum  [s_28_1, And[a_c27, b_c01], c_27_1] and
   c_28_2 in AdderCarry[s_28_1, And[a_c27, b_c01], c_27_1] and

   s_28_3 in AdderSum  [s_28_2, And[a_c26, b_c02], c_27_2] and
   c_28_3 in AdderCarry[s_28_2, And[a_c26, b_c02], c_27_2] and

   s_28_4 in AdderSum  [s_28_3, And[a_c25, b_c03], c_27_3] and
   c_28_4 in AdderCarry[s_28_3, And[a_c25, b_c03], c_27_3] and

   s_28_5 in AdderSum  [s_28_4, And[a_c24, b_c04], c_27_4] and
   c_28_5 in AdderCarry[s_28_4, And[a_c24, b_c04], c_27_4] and

   s_28_6 in AdderSum  [s_28_5, And[a_c23, b_c05], c_27_5] and
   c_28_6 in AdderCarry[s_28_5, And[a_c23, b_c05], c_27_5] and

   s_28_7 in AdderSum  [s_28_6, And[a_c22, b_c06], c_27_6] and
   c_28_7 in AdderCarry[s_28_6, And[a_c22, b_c06], c_27_6] and

   s_28_8 in AdderSum  [s_28_7, And[a_c21, b_c07], c_27_7] and
   c_28_8 in AdderCarry[s_28_7, And[a_c21, b_c07], c_27_7] and

   s_28_9 in AdderSum  [s_28_8, And[a_c20, b_c08], c_27_8] and
   c_28_9 in AdderCarry[s_28_8, And[a_c20, b_c08], c_27_8] and

   s_28_10 in AdderSum  [s_28_9, And[a_c19, b_c09], c_27_9] and
   c_28_10 in AdderCarry[s_28_9, And[a_c19, b_c09], c_27_9] and

   s_28_11 in AdderSum  [s_28_10, And[a_c18, b_c10], c_27_10] and
   c_28_11 in AdderCarry[s_28_10, And[a_c18, b_c10], c_27_10] and

   s_28_12 in AdderSum  [s_28_11, And[a_c17, b_c11], c_27_11] and
   c_28_12 in AdderCarry[s_28_11, And[a_c17, b_c11], c_27_11] and

   s_28_13 in AdderSum  [s_28_12, And[a_c16, b_c12], c_27_12] and
   c_28_13 in AdderCarry[s_28_12, And[a_c16, b_c12], c_27_12] and

   s_28_14 in AdderSum  [s_28_13, And[a_c15, b_c13], c_27_13] and
   c_28_14 in AdderCarry[s_28_13, And[a_c15, b_c13], c_27_13] and

   s_28_15 in AdderSum  [s_28_14, And[a_c14, b_c14], c_27_14] and
   c_28_15 in AdderCarry[s_28_14, And[a_c14, b_c14], c_27_14] and

   s_28_16 in AdderSum  [s_28_15, And[a_c13, b_c15], c_27_15] and
   c_28_16 in AdderCarry[s_28_15, And[a_c13, b_c15], c_27_15] and

   s_28_17 in AdderSum  [s_28_16, And[a_c12, b_c16], c_27_16] and
   c_28_17 in AdderCarry[s_28_16, And[a_c12, b_c16], c_27_16] and

   s_28_18 in AdderSum  [s_28_17, And[a_c11, b_c17], c_27_17] and
   c_28_18 in AdderCarry[s_28_17, And[a_c11, b_c17], c_27_17] and

   s_28_19 in AdderSum  [s_28_18, And[a_c10, b_c18], c_27_18] and
   c_28_19 in AdderCarry[s_28_18, And[a_c10, b_c18], c_27_18] and

   s_28_20 in AdderSum  [s_28_19, And[a_c09, b_c19], c_27_19] and
   c_28_20 in AdderCarry[s_28_19, And[a_c09, b_c19], c_27_19] and

   s_28_21 in AdderSum  [s_28_20, And[a_c08, b_c20], c_27_20] and
   c_28_21 in AdderCarry[s_28_20, And[a_c08, b_c20], c_27_20] and

   s_28_22 in AdderSum  [s_28_21, And[a_c07, b_c21], c_27_21] and
   c_28_22 in AdderCarry[s_28_21, And[a_c07, b_c21], c_27_21] and

   s_28_23 in AdderSum  [s_28_22, And[a_c06, b_c22], c_27_22] and
   c_28_23 in AdderCarry[s_28_22, And[a_c06, b_c22], c_27_22] and

   s_28_24 in AdderSum  [s_28_23, And[a_c05, b_c23], c_27_23] and
   c_28_24 in AdderCarry[s_28_23, And[a_c05, b_c23], c_27_23] and

   s_28_25 in AdderSum  [s_28_24, And[a_c04, b_c24], c_27_24] and
   c_28_25 in AdderCarry[s_28_24, And[a_c04, b_c24], c_27_24] and

   s_28_26 in AdderSum  [s_28_25, And[a_c03, b_c25], c_27_25] and
   c_28_26 in AdderCarry[s_28_25, And[a_c03, b_c25], c_27_25] and

   s_28_27 in AdderSum  [s_28_26, And[a_c02, b_c26], c_27_26] and
   c_28_27 in AdderCarry[s_28_26, And[a_c02, b_c26], c_27_26] and

   s_28_28 in AdderSum  [s_28_27, And[a_c01, b_c27], c_27_27] and
   c_28_28 in AdderCarry[s_28_27, And[a_c01, b_c27], c_27_27] and

   s_28_29 in AdderSum  [s_28_28, And[a_c00, b_c28], c_27_28] and
   c_28_29 in AdderCarry[s_28_28, And[a_c00, b_c28], c_27_28] and

   s_29_0 in AdderSum  [And[a_c29,b.b31], And[a.b31, b_c29], false] and
   c_29_0 in AdderCarry[And[a_c29,b.b31], And[a.b31, b_c29], false] and

   s_29_1 in AdderSum  [s_29_0, And[a_c29, b_c00], c_28_0] and
   c_29_1 in AdderCarry[s_29_0, And[a_c29, b_c00], c_28_0] and

   s_29_2 in AdderSum  [s_29_1, And[a_c28, b_c01], c_28_1] and
   c_29_2 in AdderCarry[s_29_1, And[a_c28, b_c01], c_28_1] and

   s_29_3 in AdderSum  [s_29_2, And[a_c27, b_c02], c_28_2] and
   c_29_3 in AdderCarry[s_29_2, And[a_c27, b_c02], c_28_2] and

   s_29_4 in AdderSum  [s_29_3, And[a_c26, b_c03], c_28_3] and
   c_29_4 in AdderCarry[s_29_3, And[a_c26, b_c03], c_28_3] and

   s_29_5 in AdderSum  [s_29_4, And[a_c25, b_c04], c_28_4] and
   c_29_5 in AdderCarry[s_29_4, And[a_c25, b_c04], c_28_4] and

   s_29_6 in AdderSum  [s_29_5, And[a_c24, b_c05], c_28_5] and
   c_29_6 in AdderCarry[s_29_5, And[a_c24, b_c05], c_28_5] and

   s_29_7 in AdderSum  [s_29_6, And[a_c23, b_c06], c_28_6] and
   c_29_7 in AdderCarry[s_29_6, And[a_c23, b_c06], c_28_6] and

   s_29_8 in AdderSum  [s_29_7, And[a_c22, b_c07], c_28_7] and
   c_29_8 in AdderCarry[s_29_7, And[a_c22, b_c07], c_28_7] and

   s_29_9 in AdderSum  [s_29_8, And[a_c21, b_c08], c_28_8] and
   c_29_9 in AdderCarry[s_29_8, And[a_c21, b_c08], c_28_8] and

   s_29_10 in AdderSum  [s_29_9, And[a_c20, b_c09], c_28_9] and
   c_29_10 in AdderCarry[s_29_9, And[a_c20, b_c09], c_28_9] and

   s_29_11 in AdderSum  [s_29_10, And[a_c19, b_c10], c_28_10] and
   c_29_11 in AdderCarry[s_29_10, And[a_c19, b_c10], c_28_10] and

   s_29_12 in AdderSum  [s_29_11, And[a_c18, b_c11], c_28_11] and
   c_29_12 in AdderCarry[s_29_11, And[a_c18, b_c11], c_28_11] and

   s_29_13 in AdderSum  [s_29_12, And[a_c17, b_c12], c_28_12] and
   c_29_13 in AdderCarry[s_29_12, And[a_c17, b_c12], c_28_12] and

   s_29_14 in AdderSum  [s_29_13, And[a_c16, b_c13], c_28_13] and
   c_29_14 in AdderCarry[s_29_13, And[a_c16, b_c13], c_28_13] and

   s_29_15 in AdderSum  [s_29_14, And[a_c15, b_c14], c_28_14] and
   c_29_15 in AdderCarry[s_29_14, And[a_c15, b_c14], c_28_14] and

   s_29_16 in AdderSum  [s_29_15, And[a_c14, b_c15], c_28_15] and
   c_29_16 in AdderCarry[s_29_15, And[a_c14, b_c15], c_28_15] and

   s_29_17 in AdderSum  [s_29_16, And[a_c13, b_c16], c_28_16] and
   c_29_17 in AdderCarry[s_29_16, And[a_c13, b_c16], c_28_16] and

   s_29_18 in AdderSum  [s_29_17, And[a_c12, b_c17], c_28_17] and
   c_29_18 in AdderCarry[s_29_17, And[a_c12, b_c17], c_28_17] and

   s_29_19 in AdderSum  [s_29_18, And[a_c11, b_c18], c_28_18] and
   c_29_19 in AdderCarry[s_29_18, And[a_c11, b_c18], c_28_18] and

   s_29_20 in AdderSum  [s_29_19, And[a_c10, b_c19], c_28_19] and
   c_29_20 in AdderCarry[s_29_19, And[a_c10, b_c19], c_28_19] and

   s_29_21 in AdderSum  [s_29_20, And[a_c09, b_c20], c_28_20] and
   c_29_21 in AdderCarry[s_29_20, And[a_c09, b_c20], c_28_20] and

   s_29_22 in AdderSum  [s_29_21, And[a_c08, b_c21], c_28_21] and
   c_29_22 in AdderCarry[s_29_21, And[a_c08, b_c21], c_28_21] and

   s_29_23 in AdderSum  [s_29_22, And[a_c07, b_c22], c_28_22] and
   c_29_23 in AdderCarry[s_29_22, And[a_c07, b_c22], c_28_22] and

   s_29_24 in AdderSum  [s_29_23, And[a_c06, b_c23], c_28_23] and
   c_29_24 in AdderCarry[s_29_23, And[a_c06, b_c23], c_28_23] and

   s_29_25 in AdderSum  [s_29_24, And[a_c05, b_c24], c_28_24] and
   c_29_25 in AdderCarry[s_29_24, And[a_c05, b_c24], c_28_24] and

   s_29_26 in AdderSum  [s_29_25, And[a_c04, b_c25], c_28_25] and
   c_29_26 in AdderCarry[s_29_25, And[a_c04, b_c25], c_28_25] and

   s_29_27 in AdderSum  [s_29_26, And[a_c03, b_c26], c_28_26] and
   c_29_27 in AdderCarry[s_29_26, And[a_c03, b_c26], c_28_26] and

   s_29_28 in AdderSum  [s_29_27, And[a_c02, b_c27], c_28_27] and
   c_29_28 in AdderCarry[s_29_27, And[a_c02, b_c27], c_28_27] and

   s_29_29 in AdderSum  [s_29_28, And[a_c01, b_c28], c_28_28] and
   c_29_29 in AdderCarry[s_29_28, And[a_c01, b_c28], c_28_28] and

   s_29_30 in AdderSum  [s_29_29, And[a_c00, b_c29], c_28_29] and
   c_29_30 in AdderCarry[s_29_29, And[a_c00, b_c29], c_28_29] and

   s_30_0 in AdderSum  [And[a_c30,b.b31], And[a.b31, b_c30], false] and
   c_30_0 in AdderCarry[And[a_c30,b.b31], And[a.b31, b_c30], false] and

   s_30_1 in AdderSum  [s_30_0, And[a_c30, b_c00], c_29_0] and
   c_30_1 in AdderCarry[s_30_0, And[a_c30, b_c00], c_29_0] and

   s_30_2 in AdderSum  [s_30_1, And[a_c29, b_c01], c_29_1] and
   c_30_2 in AdderCarry[s_30_1, And[a_c29, b_c01], c_29_1] and

   s_30_3 in AdderSum  [s_30_2, And[a_c28, b_c02], c_29_2] and
   c_30_3 in AdderCarry[s_30_2, And[a_c28, b_c02], c_29_2] and

   s_30_4 in AdderSum  [s_30_3, And[a_c27, b_c03], c_29_3] and
   c_30_4 in AdderCarry[s_30_3, And[a_c27, b_c03], c_29_3] and

   s_30_5 in AdderSum  [s_30_4, And[a_c26, b_c04], c_29_4] and
   c_30_5 in AdderCarry[s_30_4, And[a_c26, b_c04], c_29_4] and

   s_30_6 in AdderSum  [s_30_5, And[a_c25, b_c05], c_29_5] and
   c_30_6 in AdderCarry[s_30_5, And[a_c25, b_c05], c_29_5] and

   s_30_7 in AdderSum  [s_30_6, And[a_c24, b_c06], c_29_6] and
   c_30_7 in AdderCarry[s_30_6, And[a_c24, b_c06], c_29_6] and

   s_30_8 in AdderSum  [s_30_7, And[a_c23, b_c07], c_29_7] and
   c_30_8 in AdderCarry[s_30_7, And[a_c23, b_c07], c_29_7] and

   s_30_9 in AdderSum  [s_30_8, And[a_c22, b_c08], c_29_8] and
   c_30_9 in AdderCarry[s_30_8, And[a_c22, b_c08], c_29_8] and

   s_30_10 in AdderSum  [s_30_9, And[a_c21, b_c09], c_29_9] and
   c_30_10 in AdderCarry[s_30_9, And[a_c21, b_c09], c_29_9] and

   s_30_11 in AdderSum  [s_30_10, And[a_c20, b_c10], c_29_10] and
   c_30_11 in AdderCarry[s_30_10, And[a_c20, b_c10], c_29_10] and

   s_30_12 in AdderSum  [s_30_11, And[a_c19, b_c11], c_29_11] and
   c_30_12 in AdderCarry[s_30_11, And[a_c19, b_c11], c_29_11] and

   s_30_13 in AdderSum  [s_30_12, And[a_c18, b_c12], c_29_12] and
   c_30_13 in AdderCarry[s_30_12, And[a_c18, b_c12], c_29_12] and

   s_30_14 in AdderSum  [s_30_13, And[a_c17, b_c13], c_29_13] and
   c_30_14 in AdderCarry[s_30_13, And[a_c17, b_c13], c_29_13] and

   s_30_15 in AdderSum  [s_30_14, And[a_c16, b_c14], c_29_14] and
   c_30_15 in AdderCarry[s_30_14, And[a_c16, b_c14], c_29_14] and

   s_30_16 in AdderSum  [s_30_15, And[a_c15, b_c15], c_29_15] and
   c_30_16 in AdderCarry[s_30_15, And[a_c15, b_c15], c_29_15] and

   s_30_17 in AdderSum  [s_30_16, And[a_c14, b_c16], c_29_16] and
   c_30_17 in AdderCarry[s_30_16, And[a_c14, b_c16], c_29_16] and

   s_30_18 in AdderSum  [s_30_17, And[a_c13, b_c17], c_29_17] and
   c_30_18 in AdderCarry[s_30_17, And[a_c13, b_c17], c_29_17] and

   s_30_19 in AdderSum  [s_30_18, And[a_c12, b_c18], c_29_18] and
   c_30_19 in AdderCarry[s_30_18, And[a_c12, b_c18], c_29_18] and

   s_30_20 in AdderSum  [s_30_19, And[a_c11, b_c19], c_29_19] and
   c_30_20 in AdderCarry[s_30_19, And[a_c11, b_c19], c_29_19] and

   s_30_21 in AdderSum  [s_30_20, And[a_c10, b_c20], c_29_20] and
   c_30_21 in AdderCarry[s_30_20, And[a_c10, b_c20], c_29_20] and

   s_30_22 in AdderSum  [s_30_21, And[a_c09, b_c21], c_29_21] and
   c_30_22 in AdderCarry[s_30_21, And[a_c09, b_c21], c_29_21] and

   s_30_23 in AdderSum  [s_30_22, And[a_c08, b_c22], c_29_22] and
   c_30_23 in AdderCarry[s_30_22, And[a_c08, b_c22], c_29_22] and

   s_30_24 in AdderSum  [s_30_23, And[a_c07, b_c23], c_29_23] and
   c_30_24 in AdderCarry[s_30_23, And[a_c07, b_c23], c_29_23] and

   s_30_25 in AdderSum  [s_30_24, And[a_c06, b_c24], c_29_24] and
   c_30_25 in AdderCarry[s_30_24, And[a_c06, b_c24], c_29_24] and

   s_30_26 in AdderSum  [s_30_25, And[a_c05, b_c25], c_29_25] and
   c_30_26 in AdderCarry[s_30_25, And[a_c05, b_c25], c_29_25] and

   s_30_27 in AdderSum  [s_30_26, And[a_c04, b_c26], c_29_26] and
   c_30_27 in AdderCarry[s_30_26, And[a_c04, b_c26], c_29_26] and

   s_30_28 in AdderSum  [s_30_27, And[a_c03, b_c27], c_29_27] and
   c_30_28 in AdderCarry[s_30_27, And[a_c03, b_c27], c_29_27] and

   s_30_29 in AdderSum  [s_30_28, And[a_c02, b_c28], c_29_28] and
   c_30_29 in AdderCarry[s_30_28, And[a_c02, b_c28], c_29_28] and

   s_30_30 in AdderSum  [s_30_29, And[a_c01, b_c29], c_29_29] and
   c_30_30 in AdderCarry[s_30_29, And[a_c01, b_c29], c_29_29] and

   s_30_31 in AdderSum  [s_30_30, And[a_c00, b_c30], c_29_30] and
   c_30_31 in AdderCarry[s_30_30, And[a_c00, b_c30], c_29_30] and
   s_31_0 in false and
   s_31_1 in AdderSum  [s_31_0, And[a_c30, b_c01], c_30_0] and
   c_31_1 in AdderCarry[s_31_0, And[a_c30, b_c01], c_30_0] and
   s_31_2 in AdderSum  [s_31_1, And[a_c29, b_c02], c_30_1] and
   c_31_2 in AdderCarry[s_31_1, And[a_c29, b_c02], c_30_1] and
   s_31_3 in AdderSum  [s_31_2, And[a_c28, b_c03], c_30_2] and
   c_31_3 in AdderCarry[s_31_2, And[a_c28, b_c03], c_30_2] and
   s_31_4 in AdderSum  [s_31_3, And[a_c27, b_c04], c_30_3] and
   c_31_4 in AdderCarry[s_31_3, And[a_c27, b_c04], c_30_3] and
   s_31_5 in AdderSum  [s_31_4, And[a_c26, b_c05], c_30_4] and
   c_31_5 in AdderCarry[s_31_4, And[a_c26, b_c05], c_30_4] and
   s_31_6 in AdderSum  [s_31_5, And[a_c25, b_c06], c_30_5] and
   c_31_6 in AdderCarry[s_31_5, And[a_c25, b_c06], c_30_5] and
   s_31_7 in AdderSum  [s_31_6, And[a_c24, b_c07], c_30_6] and
   c_31_7 in AdderCarry[s_31_6, And[a_c24, b_c07], c_30_6] and
   s_31_8 in AdderSum  [s_31_7, And[a_c23, b_c08], c_30_7] and
   c_31_8 in AdderCarry[s_31_7, And[a_c23, b_c08], c_30_7] and
   s_31_9 in AdderSum  [s_31_8, And[a_c22, b_c09], c_30_8] and
   c_31_9 in AdderCarry[s_31_8, And[a_c22, b_c09], c_30_8] and
   s_31_10 in AdderSum  [s_31_9, And[a_c21, b_c10], c_30_9] and
   c_31_10 in AdderCarry[s_31_9, And[a_c21, b_c10], c_30_9] and
   s_31_11 in AdderSum  [s_31_10, And[a_c20, b_c11], c_30_10] and
   c_31_11 in AdderCarry[s_31_10, And[a_c20, b_c11], c_30_10] and
   s_31_12 in AdderSum  [s_31_11, And[a_c19, b_c12], c_30_11] and
   c_31_12 in AdderCarry[s_31_11, And[a_c19, b_c12], c_30_11] and
   s_31_13 in AdderSum  [s_31_12, And[a_c18, b_c13], c_30_12] and
   c_31_13 in AdderCarry[s_31_12, And[a_c18, b_c13], c_30_12] and
   s_31_14 in AdderSum  [s_31_13, And[a_c17, b_c14], c_30_13] and
   c_31_14 in AdderCarry[s_31_13, And[a_c17, b_c14], c_30_13] and
   s_31_15 in AdderSum  [s_31_14, And[a_c16, b_c15], c_30_14] and
   c_31_15 in AdderCarry[s_31_14, And[a_c16, b_c15], c_30_14] and
   s_31_16 in AdderSum  [s_31_15, And[a_c15, b_c16], c_30_15] and
   c_31_16 in AdderCarry[s_31_15, And[a_c15, b_c16], c_30_15] and
   s_31_17 in AdderSum  [s_31_16, And[a_c14, b_c17], c_30_16] and
   c_31_17 in AdderCarry[s_31_16, And[a_c14, b_c17], c_30_16] and
   s_31_18 in AdderSum  [s_31_17, And[a_c13, b_c18], c_30_17] and
   c_31_18 in AdderCarry[s_31_17, And[a_c13, b_c18], c_30_17] and
   s_31_19 in AdderSum  [s_31_18, And[a_c12, b_c19], c_30_18] and
   c_31_19 in AdderCarry[s_31_18, And[a_c12, b_c19], c_30_18] and
   s_31_20 in AdderSum  [s_31_19, And[a_c11, b_c20], c_30_19] and
   c_31_20 in AdderCarry[s_31_19, And[a_c11, b_c20], c_30_19] and
   s_31_21 in AdderSum  [s_31_20, And[a_c10, b_c21], c_30_20] and
   c_31_21 in AdderCarry[s_31_20, And[a_c10, b_c21], c_30_20] and
   s_31_22 in AdderSum  [s_31_21, And[a_c09, b_c22], c_30_21] and
   c_31_22 in AdderCarry[s_31_21, And[a_c09, b_c22], c_30_21] and
   s_31_23 in AdderSum  [s_31_22, And[a_c08, b_c23], c_30_22] and
   c_31_23 in AdderCarry[s_31_22, And[a_c08, b_c23], c_30_22] and
   s_31_24 in AdderSum  [s_31_23, And[a_c07, b_c24], c_30_23] and
   c_31_24 in AdderCarry[s_31_23, And[a_c07, b_c24], c_30_23] and
   s_31_25 in AdderSum  [s_31_24, And[a_c06, b_c25], c_30_24] and
   c_31_25 in AdderCarry[s_31_24, And[a_c06, b_c25], c_30_24] and
   s_31_26 in AdderSum  [s_31_25, And[a_c05, b_c26], c_30_25] and
   c_31_26 in AdderCarry[s_31_25, And[a_c05, b_c26], c_30_25] and
   s_31_27 in AdderSum  [s_31_26, And[a_c04, b_c27], c_30_26] and
   c_31_27 in AdderCarry[s_31_26, And[a_c04, b_c27], c_30_26] and
   s_31_28 in AdderSum  [s_31_27, And[a_c03, b_c28], c_30_27] and
   c_31_28 in AdderCarry[s_31_27, And[a_c03, b_c28], c_30_27] and
   s_31_29 in AdderSum  [s_31_28, And[a_c02, b_c29], c_30_28] and
   c_31_29 in AdderCarry[s_31_28, And[a_c02, b_c29], c_30_28] and
   s_31_30 in AdderSum  [s_31_29, And[a_c01, b_c30], c_30_29] and
   c_31_30 in AdderCarry[s_31_29, And[a_c01, b_c30], c_30_29] and
   s_31_31 in AdderSum  [s_31_30, c_30_30, c_30_31] and
   c_31_31 in AdderCarry[s_31_30, c_30_30, c_30_31] and
   s_31_32 in s_31_31 and
   (
      t in false => (
         result.b00 in s_0_1 and
         result.b01 in s_1_2 and
         result.b02 in s_2_3 and
         result.b03 in s_3_4 and
         result.b04 in s_4_5 and
         result.b05 in s_5_6 and
         result.b06 in s_6_7 and
         result.b07 in s_7_8 and
         result.b08 in s_8_9 and
         result.b09 in s_9_10 and
         result.b10 in s_10_11 and
         result.b11 in s_11_12 and
         result.b12 in s_12_13 and
         result.b13 in s_13_14 and
         result.b14 in s_14_15 and
         result.b15 in s_15_16 and
         result.b16 in s_16_17 and
         result.b17 in s_17_18 and
         result.b18 in s_18_19 and
         result.b19 in s_19_20 and
         result.b20 in s_20_21 and
         result.b21 in s_21_22 and
         result.b22 in s_22_23 and
         result.b23 in s_23_24 and
         result.b24 in s_24_25 and
         result.b25 in s_25_26 and
         result.b26 in s_26_27 and
         result.b27 in s_27_28 and
         result.b28 in s_28_29 and
         result.b29 in s_29_30 and
         result.b30 in s_30_31 and
         result.b31 in s_31_32 
      ) else (
         result.b00 in s_0_1 and
         result.b01 in Xor[Not[s_1_2], Not[s_0_1]] and
         result.b02 in Xor[Not[s_2_3], And[Not[s_1_2], Xor[Not[s_1_2], result.b01]]] and
         result.b03 in Xor[Not[s_3_4], And[Not[s_2_3], Xor[Not[s_2_3], result.b02]]] and
         result.b04 in Xor[Not[s_4_5], And[Not[s_3_4], Xor[Not[s_3_4], result.b03]]] and
         result.b05 in Xor[Not[s_5_6], And[Not[s_4_5], Xor[Not[s_4_5], result.b04]]] and
         result.b06 in Xor[Not[s_6_7], And[Not[s_5_6], Xor[Not[s_5_6], result.b05]]] and
         result.b07 in Xor[Not[s_7_8], And[Not[s_6_7], Xor[Not[s_6_7], result.b06]]] and
         result.b08 in Xor[Not[s_8_9], And[Not[s_7_8], Xor[Not[s_7_8], result.b07]]] and
         result.b09 in Xor[Not[s_9_10], And[Not[s_8_9], Xor[Not[s_8_9], result.b08]]] and
         result.b10 in Xor[Not[s_10_11], And[Not[s_9_10], Xor[Not[s_9_10], result.b09]]] and
         result.b11 in Xor[Not[s_11_12], And[Not[s_10_11], Xor[Not[s_10_11], result.b10]]] and
         result.b12 in Xor[Not[s_12_13], And[Not[s_11_12], Xor[Not[s_11_12], result.b11]]] and
         result.b13 in Xor[Not[s_13_14], And[Not[s_12_13], Xor[Not[s_12_13], result.b12]]] and
         result.b14 in Xor[Not[s_14_15], And[Not[s_13_14], Xor[Not[s_13_14], result.b13]]] and
         result.b15 in Xor[Not[s_15_16], And[Not[s_14_15], Xor[Not[s_14_15], result.b14]]] and
         result.b16 in Xor[Not[s_16_17], And[Not[s_15_16], Xor[Not[s_15_16], result.b15]]] and
         result.b17 in Xor[Not[s_17_18], And[Not[s_16_17], Xor[Not[s_16_17], result.b16]]] and
         result.b18 in Xor[Not[s_18_19], And[Not[s_17_18], Xor[Not[s_17_18], result.b17]]] and
         result.b19 in Xor[Not[s_19_20], And[Not[s_18_19], Xor[Not[s_18_19], result.b18]]] and
         result.b20 in Xor[Not[s_20_21], And[Not[s_19_20], Xor[Not[s_19_20], result.b19]]] and
         result.b21 in Xor[Not[s_21_22], And[Not[s_20_21], Xor[Not[s_20_21], result.b20]]] and
         result.b22 in Xor[Not[s_22_23], And[Not[s_21_22], Xor[Not[s_21_22], result.b21]]] and
         result.b23 in Xor[Not[s_23_24], And[Not[s_22_23], Xor[Not[s_22_23], result.b22]]] and
         result.b24 in Xor[Not[s_24_25], And[Not[s_23_24], Xor[Not[s_23_24], result.b23]]] and
         result.b25 in Xor[Not[s_25_26], And[Not[s_24_25], Xor[Not[s_24_25], result.b24]]] and
         result.b26 in Xor[Not[s_26_27], And[Not[s_25_26], Xor[Not[s_25_26], result.b25]]] and
         result.b27 in Xor[Not[s_27_28], And[Not[s_26_27], Xor[Not[s_26_27], result.b26]]] and
         result.b28 in Xor[Not[s_28_29], And[Not[s_27_28], Xor[Not[s_27_28], result.b27]]] and
         result.b29 in Xor[Not[s_29_30], And[Not[s_28_29], Xor[Not[s_28_29], result.b28]]] and
         result.b30 in Xor[Not[s_30_31], And[Not[s_29_30], Xor[Not[s_29_30], result.b29]]] and
         result.b31 in Xor[Not[s_31_32], And[Not[s_30_31], Xor[Not[s_30_31], result.b30]]] 
      )
   )
   and overflow in (true in (
      c_31_1 + c_31_2 + c_31_3 + c_31_4 + c_31_5 + c_31_6 + c_31_7 + c_31_8 + c_31_9 + c_31_10 + c_31_11 + c_31_12 + c_31_13 + c_31_14 + c_31_15 + c_31_16 + c_31_17 + c_31_18 + c_31_19 + c_31_20 + c_31_21 + c_31_22 + c_31_23 + c_31_24 + c_31_25 + c_31_26 + c_31_27 + c_31_28 + c_31_29 + c_31_30 + c_31_31
    + And[a_c02, b_c30]
    + And[a_c03, b_c29] + And[a_c03, b_c30]
    + And[a_c04, b_c28] + And[a_c04, b_c29] + And[a_c04, b_c30]
    + And[a_c05, b_c27] + And[a_c05, b_c28] + And[a_c05, b_c29] + And[a_c05, b_c30]
    + And[a_c06, b_c26] + And[a_c06, b_c27] + And[a_c06, b_c28] + And[a_c06, b_c29] + And[a_c06, b_c30]
    + And[a_c07, b_c25] + And[a_c07, b_c26] + And[a_c07, b_c27] + And[a_c07, b_c28] + And[a_c07, b_c29] + And[a_c07, b_c30]
    + And[a_c08, b_c24] + And[a_c08, b_c25] + And[a_c08, b_c26] + And[a_c08, b_c27] + And[a_c08, b_c28] + And[a_c08, b_c29] + And[a_c08, b_c30]
    + And[a_c09, b_c23] + And[a_c09, b_c24] + And[a_c09, b_c25] + And[a_c09, b_c26] + And[a_c09, b_c27] + And[a_c09, b_c28] + And[a_c09, b_c29] + And[a_c09, b_c30]
    + And[a_c10, b_c22] + And[a_c10, b_c23] + And[a_c10, b_c24] + And[a_c10, b_c25] + And[a_c10, b_c26] + And[a_c10, b_c27] + And[a_c10, b_c28] + And[a_c10, b_c29] + And[a_c10, b_c30]
    + And[a_c11, b_c21] + And[a_c11, b_c22] + And[a_c11, b_c23] + And[a_c11, b_c24] + And[a_c11, b_c25] + And[a_c11, b_c26] + And[a_c11, b_c27] + And[a_c11, b_c28] + And[a_c11, b_c29] + And[a_c11, b_c30]
    + And[a_c12, b_c20] + And[a_c12, b_c21] + And[a_c12, b_c22] + And[a_c12, b_c23] + And[a_c12, b_c24] + And[a_c12, b_c25] + And[a_c12, b_c26] + And[a_c12, b_c27] + And[a_c12, b_c28] + And[a_c12, b_c29] + And[a_c12, b_c30]
    + And[a_c13, b_c19] + And[a_c13, b_c20] + And[a_c13, b_c21] + And[a_c13, b_c22] + And[a_c13, b_c23] + And[a_c13, b_c24] + And[a_c13, b_c25] + And[a_c13, b_c26] + And[a_c13, b_c27] + And[a_c13, b_c28] + And[a_c13, b_c29] + And[a_c13, b_c30]
    + And[a_c14, b_c18] + And[a_c14, b_c19] + And[a_c14, b_c20] + And[a_c14, b_c21] + And[a_c14, b_c22] + And[a_c14, b_c23] + And[a_c14, b_c24] + And[a_c14, b_c25] + And[a_c14, b_c26] + And[a_c14, b_c27] + And[a_c14, b_c28] + And[a_c14, b_c29] + And[a_c14, b_c30]
    + And[a_c15, b_c17] + And[a_c15, b_c18] + And[a_c15, b_c19] + And[a_c15, b_c20] + And[a_c15, b_c21] + And[a_c15, b_c22] + And[a_c15, b_c23] + And[a_c15, b_c24] + And[a_c15, b_c25] + And[a_c15, b_c26] + And[a_c15, b_c27] + And[a_c15, b_c28] + And[a_c15, b_c29] + And[a_c15, b_c30]
    + And[a_c16, b_c16] + And[a_c16, b_c17] + And[a_c16, b_c18] + And[a_c16, b_c19] + And[a_c16, b_c20] + And[a_c16, b_c21] + And[a_c16, b_c22] + And[a_c16, b_c23] + And[a_c16, b_c24] + And[a_c16, b_c25] + And[a_c16, b_c26] + And[a_c16, b_c27] + And[a_c16, b_c28] + And[a_c16, b_c29] + And[a_c16, b_c30]
    + And[a_c17, b_c15] + And[a_c17, b_c16] + And[a_c17, b_c17] + And[a_c17, b_c18] + And[a_c17, b_c19] + And[a_c17, b_c20] + And[a_c17, b_c21] + And[a_c17, b_c22] + And[a_c17, b_c23] + And[a_c17, b_c24] + And[a_c17, b_c25] + And[a_c17, b_c26] + And[a_c17, b_c27] + And[a_c17, b_c28] + And[a_c17, b_c29] + And[a_c17, b_c30]
    + And[a_c18, b_c14] + And[a_c18, b_c15] + And[a_c18, b_c16] + And[a_c18, b_c17] + And[a_c18, b_c18] + And[a_c18, b_c19] + And[a_c18, b_c20] + And[a_c18, b_c21] + And[a_c18, b_c22] + And[a_c18, b_c23] + And[a_c18, b_c24] + And[a_c18, b_c25] + And[a_c18, b_c26] + And[a_c18, b_c27] + And[a_c18, b_c28] + And[a_c18, b_c29] + And[a_c18, b_c30]
    + And[a_c19, b_c13] + And[a_c19, b_c14] + And[a_c19, b_c15] + And[a_c19, b_c16] + And[a_c19, b_c17] + And[a_c19, b_c18] + And[a_c19, b_c19] + And[a_c19, b_c20] + And[a_c19, b_c21] + And[a_c19, b_c22] + And[a_c19, b_c23] + And[a_c19, b_c24] + And[a_c19, b_c25] + And[a_c19, b_c26] + And[a_c19, b_c27] + And[a_c19, b_c28] + And[a_c19, b_c29] + And[a_c19, b_c30]
    + And[a_c20, b_c12] + And[a_c20, b_c13] + And[a_c20, b_c14] + And[a_c20, b_c15] + And[a_c20, b_c16] + And[a_c20, b_c17] + And[a_c20, b_c18] + And[a_c20, b_c19] + And[a_c20, b_c20] + And[a_c20, b_c21] + And[a_c20, b_c22] + And[a_c20, b_c23] + And[a_c20, b_c24] + And[a_c20, b_c25] + And[a_c20, b_c26] + And[a_c20, b_c27] + And[a_c20, b_c28] + And[a_c20, b_c29] + And[a_c20, b_c30]
    + And[a_c21, b_c11] + And[a_c21, b_c12] + And[a_c21, b_c13] + And[a_c21, b_c14] + And[a_c21, b_c15] + And[a_c21, b_c16] + And[a_c21, b_c17] + And[a_c21, b_c18] + And[a_c21, b_c19] + And[a_c21, b_c20] + And[a_c21, b_c21] + And[a_c21, b_c22] + And[a_c21, b_c23] + And[a_c21, b_c24] + And[a_c21, b_c25] + And[a_c21, b_c26] + And[a_c21, b_c27] + And[a_c21, b_c28] + And[a_c21, b_c29] + And[a_c21, b_c30]
    + And[a_c22, b_c10] + And[a_c22, b_c11] + And[a_c22, b_c12] + And[a_c22, b_c13] + And[a_c22, b_c14] + And[a_c22, b_c15] + And[a_c22, b_c16] + And[a_c22, b_c17] + And[a_c22, b_c18] + And[a_c22, b_c19] + And[a_c22, b_c20] + And[a_c22, b_c21] + And[a_c22, b_c22] + And[a_c22, b_c23] + And[a_c22, b_c24] + And[a_c22, b_c25] + And[a_c22, b_c26] + And[a_c22, b_c27] + And[a_c22, b_c28] + And[a_c22, b_c29] + And[a_c22, b_c30]
    + And[a_c23, b_c09] + And[a_c23, b_c10] + And[a_c23, b_c11] + And[a_c23, b_c12] + And[a_c23, b_c13] + And[a_c23, b_c14] + And[a_c23, b_c15] + And[a_c23, b_c16] + And[a_c23, b_c17] + And[a_c23, b_c18] + And[a_c23, b_c19] + And[a_c23, b_c20] + And[a_c23, b_c21] + And[a_c23, b_c22] + And[a_c23, b_c23] + And[a_c23, b_c24] + And[a_c23, b_c25] + And[a_c23, b_c26] + And[a_c23, b_c27] + And[a_c23, b_c28] + And[a_c23, b_c29] + And[a_c23, b_c30]
    + And[a_c24, b_c08] + And[a_c24, b_c09] + And[a_c24, b_c10] + And[a_c24, b_c11] + And[a_c24, b_c12] + And[a_c24, b_c13] + And[a_c24, b_c14] + And[a_c24, b_c15] + And[a_c24, b_c16] + And[a_c24, b_c17] + And[a_c24, b_c18] + And[a_c24, b_c19] + And[a_c24, b_c20] + And[a_c24, b_c21] + And[a_c24, b_c22] + And[a_c24, b_c23] + And[a_c24, b_c24] + And[a_c24, b_c25] + And[a_c24, b_c26] + And[a_c24, b_c27] + And[a_c24, b_c28] + And[a_c24, b_c29] + And[a_c24, b_c30]
    + And[a_c25, b_c07] + And[a_c25, b_c08] + And[a_c25, b_c09] + And[a_c25, b_c10] + And[a_c25, b_c11] + And[a_c25, b_c12] + And[a_c25, b_c13] + And[a_c25, b_c14] + And[a_c25, b_c15] + And[a_c25, b_c16] + And[a_c25, b_c17] + And[a_c25, b_c18] + And[a_c25, b_c19] + And[a_c25, b_c20] + And[a_c25, b_c21] + And[a_c25, b_c22] + And[a_c25, b_c23] + And[a_c25, b_c24] + And[a_c25, b_c25] + And[a_c25, b_c26] + And[a_c25, b_c27] + And[a_c25, b_c28] + And[a_c25, b_c29] + And[a_c25, b_c30]
    + And[a_c26, b_c06] + And[a_c26, b_c07] + And[a_c26, b_c08] + And[a_c26, b_c09] + And[a_c26, b_c10] + And[a_c26, b_c11] + And[a_c26, b_c12] + And[a_c26, b_c13] + And[a_c26, b_c14] + And[a_c26, b_c15] + And[a_c26, b_c16] + And[a_c26, b_c17] + And[a_c26, b_c18] + And[a_c26, b_c19] + And[a_c26, b_c20] + And[a_c26, b_c21] + And[a_c26, b_c22] + And[a_c26, b_c23] + And[a_c26, b_c24] + And[a_c26, b_c25] + And[a_c26, b_c26] + And[a_c26, b_c27] + And[a_c26, b_c28] + And[a_c26, b_c29] + And[a_c26, b_c30]
    + And[a_c27, b_c05] + And[a_c27, b_c06] + And[a_c27, b_c07] + And[a_c27, b_c08] + And[a_c27, b_c09] + And[a_c27, b_c10] + And[a_c27, b_c11] + And[a_c27, b_c12] + And[a_c27, b_c13] + And[a_c27, b_c14] + And[a_c27, b_c15] + And[a_c27, b_c16] + And[a_c27, b_c17] + And[a_c27, b_c18] + And[a_c27, b_c19] + And[a_c27, b_c20] + And[a_c27, b_c21] + And[a_c27, b_c22] + And[a_c27, b_c23] + And[a_c27, b_c24] + And[a_c27, b_c25] + And[a_c27, b_c26] + And[a_c27, b_c27] + And[a_c27, b_c28] + And[a_c27, b_c29] + And[a_c27, b_c30]
    + And[a_c28, b_c04] + And[a_c28, b_c05] + And[a_c28, b_c06] + And[a_c28, b_c07] + And[a_c28, b_c08] + And[a_c28, b_c09] + And[a_c28, b_c10] + And[a_c28, b_c11] + And[a_c28, b_c12] + And[a_c28, b_c13] + And[a_c28, b_c14] + And[a_c28, b_c15] + And[a_c28, b_c16] + And[a_c28, b_c17] + And[a_c28, b_c18] + And[a_c28, b_c19] + And[a_c28, b_c20] + And[a_c28, b_c21] + And[a_c28, b_c22] + And[a_c28, b_c23] + And[a_c28, b_c24] + And[a_c28, b_c25] + And[a_c28, b_c26] + And[a_c28, b_c27] + And[a_c28, b_c28] + And[a_c28, b_c29] + And[a_c28, b_c30]
    + And[a_c29, b_c03] + And[a_c29, b_c04] + And[a_c29, b_c05] + And[a_c29, b_c06] + And[a_c29, b_c07] + And[a_c29, b_c08] + And[a_c29, b_c09] + And[a_c29, b_c10] + And[a_c29, b_c11] + And[a_c29, b_c12] + And[a_c29, b_c13] + And[a_c29, b_c14] + And[a_c29, b_c15] + And[a_c29, b_c16] + And[a_c29, b_c17] + And[a_c29, b_c18] + And[a_c29, b_c19] + And[a_c29, b_c20] + And[a_c29, b_c21] + And[a_c29, b_c22] + And[a_c29, b_c23] + And[a_c29, b_c24] + And[a_c29, b_c25] + And[a_c29, b_c26] + And[a_c29, b_c27] + And[a_c29, b_c28] + And[a_c29, b_c29] + And[a_c29, b_c30]
    + And[a_c30, b_c02] + And[a_c30, b_c03] + And[a_c30, b_c04] + And[a_c30, b_c05] + And[a_c30, b_c06] + And[a_c30, b_c07] + And[a_c30, b_c08] + And[a_c30, b_c09] + And[a_c30, b_c10] + And[a_c30, b_c11] + And[a_c30, b_c12] + And[a_c30, b_c13] + And[a_c30, b_c14] + And[a_c30, b_c15] + And[a_c30, b_c16] + And[a_c30, b_c17] + And[a_c30, b_c18] + And[a_c30, b_c19] + And[a_c30, b_c20] + And[a_c30, b_c21] + And[a_c30, b_c22] + And[a_c30, b_c23] + And[a_c30, b_c24] + And[a_c30, b_c25] + And[a_c30, b_c26] + And[a_c30, b_c27] + And[a_c30, b_c28] + And[a_c30, b_c29] + And[a_c30, b_c30]
	+ (t in false => result.b31 else And[Not[result.b31], Not[And[Not[s_31_32], Xor[Not[s_31_32], result.b31]]]] )
   ) => true else false)
}



pred pred_java_primitive_char_value_int_mul[a: JavaPrimitiveCharValue, b: JavaPrimitiveIntegerValue, result: JavaPrimitiveIntegerValue, overflow: boolean]{
	some charCastedToInt : JavaPrimitiveIntegerValue | pred_cast_char_to_int[a, charCastedToInt] && pred_java_primitive_integer_value_mul[charCastedToInt, b, result, overflow]
}

pred pred_java_primitive_integer_value_lte_zero[a: JavaPrimitiveIntegerValue] {
   pred_java_primitive_integer_value_lt_zero[a] or pred_java_primitive_integer_value_eq_zero[a] 
}

pred pred_java_primitive_integer_value_gte_zero[a: JavaPrimitiveIntegerValue] {
   pred_java_primitive_integer_value_gt_zero[a] or pred_java_primitive_integer_value_eq_zero[a] 
}

pred pred_java_primitive_integer_value_gt_zero[a: JavaPrimitiveIntegerValue] {
   a.b31 in false and not pred_java_primitive_integer_value_eq_zero[a] 
}

pred pred_java_primitive_integer_value_gte[a: JavaPrimitiveIntegerValue, b: JavaPrimitiveIntegerValue] {
  pred_java_primitive_integer_value_gt[a, b] or pred_java_primitive_integer_value_eq[a, b]
}

pred pred_java_primitive_integer_value_lt[a: JavaPrimitiveIntegerValue, b: JavaPrimitiveIntegerValue] { 
  not pred_java_primitive_integer_value_gte[a, b] 
} 

pred pred_java_primitive_integer_value_lt_zero[a: JavaPrimitiveIntegerValue] {
  a.b31 in true
}

pred pred_java_primitive_integer_value_lte[a: JavaPrimitiveIntegerValue, b: JavaPrimitiveIntegerValue] {
  not pred_java_primitive_integer_value_gt[a, b]
}

pred pred_java_primitive_integer_value_set_has_negative[s : set JavaPrimitiveIntegerValue] {
    true in s.b31
}

pred pred_java_primitive_integer_value_neq[a: JavaPrimitiveIntegerValue, b: JavaPrimitiveIntegerValue] {
  not pred_java_primitive_integer_value_eq[a, b]
}

pred pred_java_primitive_integer_value_gt[a: JavaPrimitiveIntegerValue, b: JavaPrimitiveIntegerValue] {
   (a.b31 in false and b.b31 in true)
   or (a.b31 = b.b31) and (a.b30 in true and b.b30 in false) 
   or (a.b31 = b.b31) and (a.b30 = b.b30) and (a.b29 in true and b.b29 in false) 
   or (a.b31 = b.b31) and (a.b30 = b.b30) and (a.b29 = b.b29) and (a.b28 in true and b.b28 in false) 
   or (a.b31 = b.b31) and (a.b30 = b.b30) and (a.b29 = b.b29) and (a.b28 = b.b28) and (a.b27 in true and b.b27 in false) 
   or (a.b31 = b.b31) and (a.b30 = b.b30) and (a.b29 = b.b29) and (a.b28 = b.b28) and (a.b27 = b.b27) and (a.b26 in true and b.b26 in false) 
   or (a.b31 = b.b31) and (a.b30 = b.b30) and (a.b29 = b.b29) and (a.b28 = b.b28) and (a.b27 = b.b27) and (a.b26 = b.b26) and (a.b25 in true and b.b25 in false) 
   or (a.b31 = b.b31) and (a.b30 = b.b30) and (a.b29 = b.b29) and (a.b28 = b.b28) and (a.b27 = b.b27) and (a.b26 = b.b26) and (a.b25 = b.b25) and (a.b24 in true and b.b24 in false) 
   or (a.b31 = b.b31) and (a.b30 = b.b30) and (a.b29 = b.b29) and (a.b28 = b.b28) and (a.b27 = b.b27) and (a.b26 = b.b26) and (a.b25 = b.b25) and (a.b24 = b.b24) and (a.b23 in true and b.b23 in false) 
   or (a.b31 = b.b31) and (a.b30 = b.b30) and (a.b29 = b.b29) and (a.b28 = b.b28) and (a.b27 = b.b27) and (a.b26 = b.b26) and (a.b25 = b.b25) and (a.b24 = b.b24) and (a.b23 = b.b23) and (a.b22 in true and b.b22 in false) 
   or (a.b31 = b.b31) and (a.b30 = b.b30) and (a.b29 = b.b29) and (a.b28 = b.b28) and (a.b27 = b.b27) and (a.b26 = b.b26) and (a.b25 = b.b25) and (a.b24 = b.b24) and (a.b23 = b.b23) and (a.b22 = b.b22) and (a.b21 in true and b.b21 in false) 
   or (a.b31 = b.b31) and (a.b30 = b.b30) and (a.b29 = b.b29) and (a.b28 = b.b28) and (a.b27 = b.b27) and (a.b26 = b.b26) and (a.b25 = b.b25) and (a.b24 = b.b24) and (a.b23 = b.b23) and (a.b22 = b.b22) and (a.b21 = b.b21) and (a.b20 in true and b.b20 in false) 
   or (a.b31 = b.b31) and (a.b30 = b.b30) and (a.b29 = b.b29) and (a.b28 = b.b28) and (a.b27 = b.b27) and (a.b26 = b.b26) and (a.b25 = b.b25) and (a.b24 = b.b24) and (a.b23 = b.b23) and (a.b22 = b.b22) and (a.b21 = b.b21) and (a.b20 = b.b20) and (a.b19 in true and b.b19 in false) 
   or (a.b31 = b.b31) and (a.b30 = b.b30) and (a.b29 = b.b29) and (a.b28 = b.b28) and (a.b27 = b.b27) and (a.b26 = b.b26) and (a.b25 = b.b25) and (a.b24 = b.b24) and (a.b23 = b.b23) and (a.b22 = b.b22) and (a.b21 = b.b21) and (a.b20 = b.b20) and (a.b19 = b.b19) and (a.b18 in true and b.b18 in false) 
   or (a.b31 = b.b31) and (a.b30 = b.b30) and (a.b29 = b.b29) and (a.b28 = b.b28) and (a.b27 = b.b27) and (a.b26 = b.b26) and (a.b25 = b.b25) and (a.b24 = b.b24) and (a.b23 = b.b23) and (a.b22 = b.b22) and (a.b21 = b.b21) and (a.b20 = b.b20) and (a.b19 = b.b19) and (a.b18 = b.b18) and (a.b17 in true and b.b17 in false) 
   or (a.b31 = b.b31) and (a.b30 = b.b30) and (a.b29 = b.b29) and (a.b28 = b.b28) and (a.b27 = b.b27) and (a.b26 = b.b26) and (a.b25 = b.b25) and (a.b24 = b.b24) and (a.b23 = b.b23) and (a.b22 = b.b22) and (a.b21 = b.b21) and (a.b20 = b.b20) and (a.b19 = b.b19) and (a.b18 = b.b18) and (a.b17 = b.b17) and (a.b16 in true and b.b16 in false) 
   or (a.b31 = b.b31) and (a.b30 = b.b30) and (a.b29 = b.b29) and (a.b28 = b.b28) and (a.b27 = b.b27) and (a.b26 = b.b26) and (a.b25 = b.b25) and (a.b24 = b.b24) and (a.b23 = b.b23) and (a.b22 = b.b22) and (a.b21 = b.b21) and (a.b20 = b.b20) and (a.b19 = b.b19) and (a.b18 = b.b18) and (a.b17 = b.b17) and (a.b16 = b.b16) and (a.b15 in true and b.b15 in false) 
   or (a.b31 = b.b31) and (a.b30 = b.b30) and (a.b29 = b.b29) and (a.b28 = b.b28) and (a.b27 = b.b27) and (a.b26 = b.b26) and (a.b25 = b.b25) and (a.b24 = b.b24) and (a.b23 = b.b23) and (a.b22 = b.b22) and (a.b21 = b.b21) and (a.b20 = b.b20) and (a.b19 = b.b19) and (a.b18 = b.b18) and (a.b17 = b.b17) and (a.b16 = b.b16) and (a.b15 = b.b15) and (a.b14 in true and b.b14 in false) 
   or (a.b31 = b.b31) and (a.b30 = b.b30) and (a.b29 = b.b29) and (a.b28 = b.b28) and (a.b27 = b.b27) and (a.b26 = b.b26) and (a.b25 = b.b25) and (a.b24 = b.b24) and (a.b23 = b.b23) and (a.b22 = b.b22) and (a.b21 = b.b21) and (a.b20 = b.b20) and (a.b19 = b.b19) and (a.b18 = b.b18) and (a.b17 = b.b17) and (a.b16 = b.b16) and (a.b15 = b.b15) and (a.b14 = b.b14) and (a.b13 in true and b.b13 in false) 
   or (a.b31 = b.b31) and (a.b30 = b.b30) and (a.b29 = b.b29) and (a.b28 = b.b28) and (a.b27 = b.b27) and (a.b26 = b.b26) and (a.b25 = b.b25) and (a.b24 = b.b24) and (a.b23 = b.b23) and (a.b22 = b.b22) and (a.b21 = b.b21) and (a.b20 = b.b20) and (a.b19 = b.b19) and (a.b18 = b.b18) and (a.b17 = b.b17) and (a.b16 = b.b16) and (a.b15 = b.b15) and (a.b14 = b.b14) and (a.b13 = b.b13) and (a.b12 in true and b.b12 in false) 
   or (a.b31 = b.b31) and (a.b30 = b.b30) and (a.b29 = b.b29) and (a.b28 = b.b28) and (a.b27 = b.b27) and (a.b26 = b.b26) and (a.b25 = b.b25) and (a.b24 = b.b24) and (a.b23 = b.b23) and (a.b22 = b.b22) and (a.b21 = b.b21) and (a.b20 = b.b20) and (a.b19 = b.b19) and (a.b18 = b.b18) and (a.b17 = b.b17) and (a.b16 = b.b16) and (a.b15 = b.b15) and (a.b14 = b.b14) and (a.b13 = b.b13) and (a.b12 = b.b12) and (a.b11 in true and b.b11 in false) 
   or (a.b31 = b.b31) and (a.b30 = b.b30) and (a.b29 = b.b29) and (a.b28 = b.b28) and (a.b27 = b.b27) and (a.b26 = b.b26) and (a.b25 = b.b25) and (a.b24 = b.b24) and (a.b23 = b.b23) and (a.b22 = b.b22) and (a.b21 = b.b21) and (a.b20 = b.b20) and (a.b19 = b.b19) and (a.b18 = b.b18) and (a.b17 = b.b17) and (a.b16 = b.b16) and (a.b15 = b.b15) and (a.b14 = b.b14) and (a.b13 = b.b13) and (a.b12 = b.b12) and (a.b11 = b.b11) and (a.b10 in true and b.b10 in false) 
   or (a.b31 = b.b31) and (a.b30 = b.b30) and (a.b29 = b.b29) and (a.b28 = b.b28) and (a.b27 = b.b27) and (a.b26 = b.b26) and (a.b25 = b.b25) and (a.b24 = b.b24) and (a.b23 = b.b23) and (a.b22 = b.b22) and (a.b21 = b.b21) and (a.b20 = b.b20) and (a.b19 = b.b19) and (a.b18 = b.b18) and (a.b17 = b.b17) and (a.b16 = b.b16) and (a.b15 = b.b15) and (a.b14 = b.b14) and (a.b13 = b.b13) and (a.b12 = b.b12) and (a.b11 = b.b11) and (a.b10 = b.b10) and (a.b09 in true and b.b09 in false) 
   or (a.b31 = b.b31) and (a.b30 = b.b30) and (a.b29 = b.b29) and (a.b28 = b.b28) and (a.b27 = b.b27) and (a.b26 = b.b26) and (a.b25 = b.b25) and (a.b24 = b.b24) and (a.b23 = b.b23) and (a.b22 = b.b22) and (a.b21 = b.b21) and (a.b20 = b.b20) and (a.b19 = b.b19) and (a.b18 = b.b18) and (a.b17 = b.b17) and (a.b16 = b.b16) and (a.b15 = b.b15) and (a.b14 = b.b14) and (a.b13 = b.b13) and (a.b12 = b.b12) and (a.b11 = b.b11) and (a.b10 = b.b10) and (a.b09 = b.b09) and (a.b08 in true and b.b08 in false) 
   or (a.b31 = b.b31) and (a.b30 = b.b30) and (a.b29 = b.b29) and (a.b28 = b.b28) and (a.b27 = b.b27) and (a.b26 = b.b26) and (a.b25 = b.b25) and (a.b24 = b.b24) and (a.b23 = b.b23) and (a.b22 = b.b22) and (a.b21 = b.b21) and (a.b20 = b.b20) and (a.b19 = b.b19) and (a.b18 = b.b18) and (a.b17 = b.b17) and (a.b16 = b.b16) and (a.b15 = b.b15) and (a.b14 = b.b14) and (a.b13 = b.b13) and (a.b12 = b.b12) and (a.b11 = b.b11) and (a.b10 = b.b10) and (a.b09 = b.b09) and (a.b08 = b.b08) and (a.b07 in true and b.b07 in false) 
   or (a.b31 = b.b31) and (a.b30 = b.b30) and (a.b29 = b.b29) and (a.b28 = b.b28) and (a.b27 = b.b27) and (a.b26 = b.b26) and (a.b25 = b.b25) and (a.b24 = b.b24) and (a.b23 = b.b23) and (a.b22 = b.b22) and (a.b21 = b.b21) and (a.b20 = b.b20) and (a.b19 = b.b19) and (a.b18 = b.b18) and (a.b17 = b.b17) and (a.b16 = b.b16) and (a.b15 = b.b15) and (a.b14 = b.b14) and (a.b13 = b.b13) and (a.b12 = b.b12) and (a.b11 = b.b11) and (a.b10 = b.b10) and (a.b09 = b.b09) and (a.b08 = b.b08) and (a.b07 = b.b07) and (a.b06 in true and b.b06 in false) 
   or (a.b31 = b.b31) and (a.b30 = b.b30) and (a.b29 = b.b29) and (a.b28 = b.b28) and (a.b27 = b.b27) and (a.b26 = b.b26) and (a.b25 = b.b25) and (a.b24 = b.b24) and (a.b23 = b.b23) and (a.b22 = b.b22) and (a.b21 = b.b21) and (a.b20 = b.b20) and (a.b19 = b.b19) and (a.b18 = b.b18) and (a.b17 = b.b17) and (a.b16 = b.b16) and (a.b15 = b.b15) and (a.b14 = b.b14) and (a.b13 = b.b13) and (a.b12 = b.b12) and (a.b11 = b.b11) and (a.b10 = b.b10) and (a.b09 = b.b09) and (a.b08 = b.b08) and (a.b07 = b.b07) and (a.b06 = b.b06) and (a.b05 in true and b.b05 in false) 
   or (a.b31 = b.b31) and (a.b30 = b.b30) and (a.b29 = b.b29) and (a.b28 = b.b28) and (a.b27 = b.b27) and (a.b26 = b.b26) and (a.b25 = b.b25) and (a.b24 = b.b24) and (a.b23 = b.b23) and (a.b22 = b.b22) and (a.b21 = b.b21) and (a.b20 = b.b20) and (a.b19 = b.b19) and (a.b18 = b.b18) and (a.b17 = b.b17) and (a.b16 = b.b16) and (a.b15 = b.b15) and (a.b14 = b.b14) and (a.b13 = b.b13) and (a.b12 = b.b12) and (a.b11 = b.b11) and (a.b10 = b.b10) and (a.b09 = b.b09) and (a.b08 = b.b08) and (a.b07 = b.b07) and (a.b06 = b.b06) and (a.b05 = b.b05) and (a.b04 in true and b.b04 in false) 
   or (a.b31 = b.b31) and (a.b30 = b.b30) and (a.b29 = b.b29) and (a.b28 = b.b28) and (a.b27 = b.b27) and (a.b26 = b.b26) and (a.b25 = b.b25) and (a.b24 = b.b24) and (a.b23 = b.b23) and (a.b22 = b.b22) and (a.b21 = b.b21) and (a.b20 = b.b20) and (a.b19 = b.b19) and (a.b18 = b.b18) and (a.b17 = b.b17) and (a.b16 = b.b16) and (a.b15 = b.b15) and (a.b14 = b.b14) and (a.b13 = b.b13) and (a.b12 = b.b12) and (a.b11 = b.b11) and (a.b10 = b.b10) and (a.b09 = b.b09) and (a.b08 = b.b08) and (a.b07 = b.b07) and (a.b06 = b.b06) and (a.b05 = b.b05) and (a.b04 = b.b04) and (a.b03 in true and b.b03 in false) 
   or (a.b31 = b.b31) and (a.b30 = b.b30) and (a.b29 = b.b29) and (a.b28 = b.b28) and (a.b27 = b.b27) and (a.b26 = b.b26) and (a.b25 = b.b25) and (a.b24 = b.b24) and (a.b23 = b.b23) and (a.b22 = b.b22) and (a.b21 = b.b21) and (a.b20 = b.b20) and (a.b19 = b.b19) and (a.b18 = b.b18) and (a.b17 = b.b17) and (a.b16 = b.b16) and (a.b15 = b.b15) and (a.b14 = b.b14) and (a.b13 = b.b13) and (a.b12 = b.b12) and (a.b11 = b.b11) and (a.b10 = b.b10) and (a.b09 = b.b09) and (a.b08 = b.b08) and (a.b07 = b.b07) and (a.b06 = b.b06) and (a.b05 = b.b05) and (a.b04 = b.b04) and (a.b03 = b.b03) and (a.b02 in true and b.b02 in false) 
   or (a.b31 = b.b31) and (a.b30 = b.b30) and (a.b29 = b.b29) and (a.b28 = b.b28) and (a.b27 = b.b27) and (a.b26 = b.b26) and (a.b25 = b.b25) and (a.b24 = b.b24) and (a.b23 = b.b23) and (a.b22 = b.b22) and (a.b21 = b.b21) and (a.b20 = b.b20) and (a.b19 = b.b19) and (a.b18 = b.b18) and (a.b17 = b.b17) and (a.b16 = b.b16) and (a.b15 = b.b15) and (a.b14 = b.b14) and (a.b13 = b.b13) and (a.b12 = b.b12) and (a.b11 = b.b11) and (a.b10 = b.b10) and (a.b09 = b.b09) and (a.b08 = b.b08) and (a.b07 = b.b07) and (a.b06 = b.b06) and (a.b05 = b.b05) and (a.b04 = b.b04) and (a.b03 = b.b03) and (a.b02 = b.b02) and (a.b01 in true and b.b01 in false) 
   or (a.b31 = b.b31) and (a.b30 = b.b30) and (a.b29 = b.b29) and (a.b28 = b.b28) and (a.b27 = b.b27) and (a.b26 = b.b26) and (a.b25 = b.b25) and (a.b24 = b.b24) and (a.b23 = b.b23) and (a.b22 = b.b22) and (a.b21 = b.b21) and (a.b20 = b.b20) and (a.b19 = b.b19) and (a.b18 = b.b18) and (a.b17 = b.b17) and (a.b16 = b.b16) and (a.b15 = b.b15) and (a.b14 = b.b14) and (a.b13 = b.b13) and (a.b12 = b.b12) and (a.b11 = b.b11) and (a.b10 = b.b10) and (a.b09 = b.b09) and (a.b08 = b.b08) and (a.b07 = b.b07) and (a.b06 = b.b06) and (a.b05 = b.b05) and (a.b04 = b.b04) and (a.b03 = b.b03) and (a.b02 = b.b02) and (a.b01 = b.b01) and (a.b00 in true and b.b00 in false) 
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
   a.b08 in false 
   a.b09 in false 
   a.b10 in false 
   a.b11 in false 
   a.b12 in false 
   a.b13 in false 
   a.b14 in false 
   a.b15 in false 
   a.b16 in false 
   a.b17 in false 
   a.b18 in false 
   a.b19 in false 
   a.b20 in false 
   a.b21 in false 
   a.b22 in false 
   a.b23 in false 
   a.b24 in false 
   a.b25 in false 
   a.b26 in false 
   a.b27 in false 
   a.b28 in false 
   a.b29 in false 
   a.b30 in false 
   a.b31 in false 
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
   a.b08 = b.b08
   a.b09 = b.b09
   a.b10 = b.b10
   a.b11 = b.b11
   a.b12 = b.b12
   a.b13 = b.b13
   a.b14 = b.b14
   a.b15 = b.b15
   a.b16 = b.b16
   a.b17 = b.b17
   a.b18 = b.b18
   a.b19 = b.b19
   a.b20 = b.b20
   a.b21 = b.b21
   a.b22 = b.b22
   a.b23 = b.b23
   a.b24 = b.b24
   a.b25 = b.b25
   a.b26 = b.b26
   a.b27 = b.b27
   a.b28 = b.b28
   a.b29 = b.b29
   a.b30 = b.b30
   a.b31 = b.b31
}


pred pred_java_primitive_integer_value_abs[a: JavaPrimitiveIntegerValue, abs: JavaPrimitiveIntegerValue] {
  pred_java_primitive_integer_value_lt_zero[a] 
  => pred_java_primitive_integer_decrement[a, abs] 
  else pred_java_primitive_integer_value_eq[a, abs]
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
   let s_8 = AdderSum[a.b08, b.b08, c_8] | 
   let c_9 = AdderCarry[a.b08, b.b08, c_8] | 
   let s_9 = AdderSum[a.b09, b.b09, c_9] | 
   let c_10 = AdderCarry[a.b09, b.b09, c_9] | 
   let s_10 = AdderSum[a.b10, b.b10, c_10] | 
   let c_11 = AdderCarry[a.b10, b.b10, c_10] | 
   let s_11 = AdderSum[a.b11, b.b11, c_11] | 
   let c_12 = AdderCarry[a.b11, b.b11, c_11] | 
   let s_12 = AdderSum[a.b12, b.b12, c_12] | 
   let c_13 = AdderCarry[a.b12, b.b12, c_12] | 
   let s_13 = AdderSum[a.b13, b.b13, c_13] | 
   let c_14 = AdderCarry[a.b13, b.b13, c_13] | 
   let s_14 = AdderSum[a.b14, b.b14, c_14] | 
   let c_15 = AdderCarry[a.b14, b.b14, c_14] | 
   let s_15 = AdderSum[a.b15, b.b15, c_15] | 
   let c_16 = AdderCarry[a.b15, b.b15, c_15] | 
   let s_16 = AdderSum[a.b16, b.b16, c_16] | 
   let c_17 = AdderCarry[a.b16, b.b16, c_16] | 
   let s_17 = AdderSum[a.b17, b.b17, c_17] | 
   let c_18 = AdderCarry[a.b17, b.b17, c_17] | 
   let s_18 = AdderSum[a.b18, b.b18, c_18] | 
   let c_19 = AdderCarry[a.b18, b.b18, c_18] | 
   let s_19 = AdderSum[a.b19, b.b19, c_19] | 
   let c_20 = AdderCarry[a.b19, b.b19, c_19] | 
   let s_20 = AdderSum[a.b20, b.b20, c_20] | 
   let c_21 = AdderCarry[a.b20, b.b20, c_20] | 
   let s_21 = AdderSum[a.b21, b.b21, c_21] | 
   let c_22 = AdderCarry[a.b21, b.b21, c_21] | 
   let s_22 = AdderSum[a.b22, b.b22, c_22] | 
   let c_23 = AdderCarry[a.b22, b.b22, c_22] | 
   let s_23 = AdderSum[a.b23, b.b23, c_23] | 
   let c_24 = AdderCarry[a.b23, b.b23, c_23] | 
   let s_24 = AdderSum[a.b24, b.b24, c_24] | 
   let c_25 = AdderCarry[a.b24, b.b24, c_24] | 
   let s_25 = AdderSum[a.b25, b.b25, c_25] | 
   let c_26 = AdderCarry[a.b25, b.b25, c_25] | 
   let s_26 = AdderSum[a.b26, b.b26, c_26] | 
   let c_27 = AdderCarry[a.b26, b.b26, c_26] | 
   let s_27 = AdderSum[a.b27, b.b27, c_27] | 
   let c_28 = AdderCarry[a.b27, b.b27, c_27] | 
   let s_28 = AdderSum[a.b28, b.b28, c_28] | 
   let c_29 = AdderCarry[a.b28, b.b28, c_28] | 
   let s_29 = AdderSum[a.b29, b.b29, c_29] | 
   let c_30 = AdderCarry[a.b29, b.b29, c_29] | 
   let s_30 = AdderSum[a.b30, b.b30, c_30] | 
   let c_31 = AdderCarry[a.b30, b.b30, c_30] | 
   let s_31 = AdderSum[a.b31, b.b31, c_31] | 
   let c_32 = AdderCarry[a.b31, b.b31, c_31] | 
      result.b00 in s_0 and
      result.b01 in s_1 and
      result.b02 in s_2 and
      result.b03 in s_3 and
      result.b04 in s_4 and
      result.b05 in s_5 and
      result.b06 in s_6 and
      result.b07 in s_7 and
      result.b08 in s_8 and
      result.b09 in s_9 and
      result.b10 in s_10 and
      result.b11 in s_11 and
      result.b12 in s_12 and
      result.b13 in s_13 and
      result.b14 in s_14 and
      result.b15 in s_15 and
      result.b16 in s_16 and
      result.b17 in s_17 and
      result.b18 in s_18 and
      result.b19 in s_19 and
      result.b20 in s_20 and
      result.b21 in s_21 and
      result.b22 in s_22 and
      result.b23 in s_23 and
      result.b24 in s_24 and
      result.b25 in s_25 and
      result.b26 in s_26 and
      result.b27 in s_27 and
      result.b28 in s_28 and
      result.b29 in s_29 and
      result.b30 in s_30 and
      result.b31 in s_31 and
      overflow = (Xor[c_32, c_31])
}

pred pred_java_primitive_integer_value_decrement[a: JavaPrimitiveIntegerValue, b: JavaPrimitiveIntegerValue] {
   a.b00 in b.b00 
   a.b01 in Xor [ Not[b.b01]  , And[Not[b.b00], Xor[Not[b.b00], a.b00]]] 
   a.b02 in Xor [ Not[b.b02]  , And[Not[b.b01], Xor[Not[b.b01], a.b01]]] 
   a.b03 in Xor [ Not[b.b03]  , And[Not[b.b02], Xor[Not[b.b02], a.b02]]] 
   a.b04 in Xor [ Not[b.b04]  , And[Not[b.b03], Xor[Not[b.b03], a.b03]]] 
   a.b05 in Xor [ Not[b.b05]  , And[Not[b.b04], Xor[Not[b.b04], a.b04]]] 
   a.b06 in Xor [ Not[b.b06]  , And[Not[b.b05], Xor[Not[b.b05], a.b05]]] 
   a.b07 in Xor [ Not[b.b07]  , And[Not[b.b06], Xor[Not[b.b06], a.b06]]] 
   a.b08 in Xor [ Not[b.b08]  , And[Not[b.b07], Xor[Not[b.b07], a.b07]]] 
   a.b09 in Xor [ Not[b.b09]  , And[Not[b.b08], Xor[Not[b.b08], a.b08]]] 
   a.b10 in Xor [ Not[b.b10]  , And[Not[b.b09], Xor[Not[b.b09], a.b09]]] 
   a.b11 in Xor [ Not[b.b11]  , And[Not[b.b10], Xor[Not[b.b10], a.b10]]] 
   a.b12 in Xor [ Not[b.b12]  , And[Not[b.b11], Xor[Not[b.b11], a.b11]]] 
   a.b13 in Xor [ Not[b.b13]  , And[Not[b.b12], Xor[Not[b.b12], a.b12]]] 
   a.b14 in Xor [ Not[b.b14]  , And[Not[b.b13], Xor[Not[b.b13], a.b13]]] 
   a.b15 in Xor [ Not[b.b15]  , And[Not[b.b14], Xor[Not[b.b14], a.b14]]] 
   a.b16 in Xor [ Not[b.b16]  , And[Not[b.b15], Xor[Not[b.b15], a.b15]]] 
   a.b17 in Xor [ Not[b.b17]  , And[Not[b.b16], Xor[Not[b.b16], a.b16]]] 
   a.b18 in Xor [ Not[b.b18]  , And[Not[b.b17], Xor[Not[b.b17], a.b17]]] 
   a.b19 in Xor [ Not[b.b19]  , And[Not[b.b18], Xor[Not[b.b18], a.b18]]] 
   a.b20 in Xor [ Not[b.b20]  , And[Not[b.b19], Xor[Not[b.b19], a.b19]]] 
   a.b21 in Xor [ Not[b.b21]  , And[Not[b.b20], Xor[Not[b.b20], a.b20]]] 
   a.b22 in Xor [ Not[b.b22]  , And[Not[b.b21], Xor[Not[b.b21], a.b21]]] 
   a.b23 in Xor [ Not[b.b23]  , And[Not[b.b22], Xor[Not[b.b22], a.b22]]] 
   a.b24 in Xor [ Not[b.b24]  , And[Not[b.b23], Xor[Not[b.b23], a.b23]]] 
   a.b25 in Xor [ Not[b.b25]  , And[Not[b.b24], Xor[Not[b.b24], a.b24]]] 
   a.b26 in Xor [ Not[b.b26]  , And[Not[b.b25], Xor[Not[b.b25], a.b25]]] 
   a.b27 in Xor [ Not[b.b27]  , And[Not[b.b26], Xor[Not[b.b26], a.b26]]] 
   a.b28 in Xor [ Not[b.b28]  , And[Not[b.b27], Xor[Not[b.b27], a.b27]]] 
   a.b29 in Xor [ Not[b.b29]  , And[Not[b.b28], Xor[Not[b.b28], a.b28]]] 
   a.b30 in Xor [ Not[b.b30]  , And[Not[b.b29], Xor[Not[b.b29], a.b29]]] 
   a.b31 in Xor [ Not[b.b31]  , And[Not[b.b30], Xor[Not[b.b30], a.b30]]] 
}

pred pred_java_primitive_integer_value_div[a: JavaPrimitiveIntegerValue, 
                                           b: JavaPrimitiveIntegerValue, 
                                           div: JavaPrimitiveIntegerValue] {
 some rem: JavaPrimitiveIntegerValue | pred_java_primitive_integer_value_div_rem[a,b,div,rem] 
}


pred pred_java_primitive_char_int_value_div_rem[a: JavaPrimitiveCharValue, 
                                           b: JavaPrimitiveIntegerValue, 
                                           div: JavaPrimitiveIntegerValue, 
                                           rem: JavaPrimitiveIntegerValue] {
	some charCastedToInt : JavaPrimitiveIntegerValue | pred_cast_char_to_int[a, charCastedToInt] &&
		pred_java_primitive_integer_value_div_rem[charCastedToInt, b, div, rem]
}

pred pred_java_primitive_integer_value_div_rem[a: JavaPrimitiveIntegerValue, 
                                           b: JavaPrimitiveIntegerValue, 
                                           div: JavaPrimitiveIntegerValue, 
                                           rem: JavaPrimitiveIntegerValue] {
  ( some x : JavaPrimitiveIntegerValue | 
	pred_java_primitive_integer_value_mul[b, div, x, false] and 
	pred_java_primitive_integer_value_add[x, rem, a, false]) 
  and 
  (rem.b31 in true implies (rem.b30 in true or rem.b29 in true or rem.b28 in true or rem.b27 in true or rem.b26 in true or rem.b25 in true or rem.b24 in true or rem.b23 in true or rem.b22 in true or rem.b21 in true or rem.b20 in true or rem.b19 in true or rem.b18 in true or rem.b17 in true or rem.b16 in true or rem.b15 in true or rem.b14 in true or rem.b13 in true or rem.b12 in true or rem.b11 in true or rem.b10 in true or rem.b09 in true or rem.b08 in true or rem.b07 in true or rem.b06 in true or rem.b05 in true or rem.b04 in true or rem.b03 in true or rem.b02 in true or rem.b01 in true or rem.b00 in true)) 
  and 
  (some abs_rem, abs_b : JavaPrimitiveIntegerValue | { pred_java_primitive_integer_value_abs[b, abs_b] and 
                                                       pred_java_primitive_integer_value_abs[rem, abs_rem] and
                                                       pred_java_primitive_integer_value_lt[abs_rem, abs_b]} )  
  and
  !pred_java_primitive_integer_value_eq_zero[b]
  and
  (pred_java_primitive_integer_value_gte_zero[a] implies pred_java_primitive_integer_value_gte_zero[rem])
  and
  (pred_java_primitive_integer_value_lt_zero[a] implies pred_java_primitive_integer_value_lte_zero[rem])
}



pred pred_java_primitive_integer_decrement[a: JavaPrimitiveIntegerValue, b: JavaPrimitiveIntegerValue] {
   a.b00 in b.b00 
   a.b01 in Xor [ Not[b.b01]  , And[Not[b.b00], Xor[Not[b.b00], a.b00]]] 
   a.b02 in Xor [ Not[b.b02]  , And[Not[b.b01], Xor[Not[b.b01], a.b01]]] 
   a.b03 in Xor [ Not[b.b03]  , And[Not[b.b02], Xor[Not[b.b02], a.b02]]] 
   a.b04 in Xor [ Not[b.b04]  , And[Not[b.b03], Xor[Not[b.b03], a.b03]]] 
   a.b05 in Xor [ Not[b.b05]  , And[Not[b.b04], Xor[Not[b.b04], a.b04]]] 
   a.b06 in Xor [ Not[b.b06]  , And[Not[b.b05], Xor[Not[b.b05], a.b05]]] 
   a.b07 in Xor [ Not[b.b07]  , And[Not[b.b06], Xor[Not[b.b06], a.b06]]] 
   a.b08 in Xor [ Not[b.b08]  , And[Not[b.b07], Xor[Not[b.b07], a.b07]]] 
   a.b09 in Xor [ Not[b.b09]  , And[Not[b.b08], Xor[Not[b.b08], a.b08]]] 
   a.b10 in Xor [ Not[b.b10]  , And[Not[b.b09], Xor[Not[b.b09], a.b09]]] 
   a.b11 in Xor [ Not[b.b11]  , And[Not[b.b10], Xor[Not[b.b10], a.b10]]] 
   a.b12 in Xor [ Not[b.b12]  , And[Not[b.b11], Xor[Not[b.b11], a.b11]]] 
   a.b13 in Xor [ Not[b.b13]  , And[Not[b.b12], Xor[Not[b.b12], a.b12]]] 
   a.b14 in Xor [ Not[b.b14]  , And[Not[b.b13], Xor[Not[b.b13], a.b13]]] 
   a.b15 in Xor [ Not[b.b15]  , And[Not[b.b14], Xor[Not[b.b14], a.b14]]] 
   a.b16 in Xor [ Not[b.b16]  , And[Not[b.b15], Xor[Not[b.b15], a.b15]]] 
   a.b17 in Xor [ Not[b.b17]  , And[Not[b.b16], Xor[Not[b.b16], a.b16]]] 
   a.b18 in Xor [ Not[b.b18]  , And[Not[b.b17], Xor[Not[b.b17], a.b17]]] 
   a.b19 in Xor [ Not[b.b19]  , And[Not[b.b18], Xor[Not[b.b18], a.b18]]] 
   a.b20 in Xor [ Not[b.b20]  , And[Not[b.b19], Xor[Not[b.b19], a.b19]]] 
   a.b21 in Xor [ Not[b.b21]  , And[Not[b.b20], Xor[Not[b.b20], a.b20]]] 
   a.b22 in Xor [ Not[b.b22]  , And[Not[b.b21], Xor[Not[b.b21], a.b21]]] 
   a.b23 in Xor [ Not[b.b23]  , And[Not[b.b22], Xor[Not[b.b22], a.b22]]] 
   a.b24 in Xor [ Not[b.b24]  , And[Not[b.b23], Xor[Not[b.b23], a.b23]]] 
   a.b25 in Xor [ Not[b.b25]  , And[Not[b.b24], Xor[Not[b.b24], a.b24]]] 
   a.b26 in Xor [ Not[b.b26]  , And[Not[b.b25], Xor[Not[b.b25], a.b25]]] 
   a.b27 in Xor [ Not[b.b27]  , And[Not[b.b26], Xor[Not[b.b26], a.b26]]] 
   a.b28 in Xor [ Not[b.b28]  , And[Not[b.b27], Xor[Not[b.b27], a.b27]]] 
   a.b29 in Xor [ Not[b.b29]  , And[Not[b.b28], Xor[Not[b.b28], a.b28]]] 
   a.b30 in Xor [ Not[b.b30]  , And[Not[b.b29], Xor[Not[b.b29], a.b29]]] 
   a.b31 in Xor [ Not[b.b31]  , And[Not[b.b30], Xor[Not[b.b30], a.b30]]] 
}

pred pred_java_primitive_integer_value_add_marker[
  left    : JavaPrimitiveIntegerValue, 
  right   : JavaPrimitiveIntegerValue, 
  result  : JavaPrimitiveIntegerValue, 
  overflow: boolean] 
{
--marker predicate (empty body)
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

pred pred_java_primitive_char_value_div_rem_int_marker[
  left     : JavaPrimitiveCharValue,
  right    : JavaPrimitiveIntegerValue, 
  result   : JavaPrimitiveIntegerValue, 
  remainder: JavaPrimitiveIntegerValue] 
{
--marker predicate (empty body)
}
  

pred pred_cast_char_to_int[a: JavaPrimitiveCharValue, b:JavaPrimitiveIntegerValue]{
   b.b31 = false and
   b.b30 = false and
   b.b29 = false and
   b.b28 = false and
   b.b27 = false and
   b.b26 = false and
   b.b25 = false and
   b.b24 = false and
   b.b23 = false and
   b.b22 = false and
   b.b21 = false and
   b.b20 = false and
   b.b19 = false and
   b.b18 = false and
   b.b17 = false and
   b.b16 = false and
   b.b15 = a.b15 and
   b.b14 = a.b14 and
   b.b13 = a.b13 and
   b.b12 = a.b12 and
   b.b11 = a.b11 and
   b.b10 = a.b10 and
   b.b09 = a.b09 and
   b.b08 = a.b08 and
   b.b07 = a.b07 and
   b.b06 = a.b06 and
   b.b05 = a.b05 and
   b.b04 = a.b04 and
   b.b03 = a.b03 and
   b.b02 = a.b02 and
   b.b01 = a.b01 and 
   b.b00 = a.b00
}


pred pred_java_primitive_char_value_addCharCharToJavaPrimitiveIntegerValue[a: JavaPrimitiveCharValue, b: JavaPrimitiveCharValue, result: JavaPrimitiveIntegerValue, overflow: boolean] { 
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
   let s_8 = AdderSum[a.b08, b.b08, c_8] | 
   let c_9 = AdderCarry[a.b08, b.b08, c_8] | 
   let s_9 = AdderSum[a.b09, b.b09, c_9] | 
   let c_10 = AdderCarry[a.b09, b.b09, c_9] | 
   let s_10 = AdderSum[a.b10, b.b10, c_10] | 
   let c_11 = AdderCarry[a.b10, b.b10, c_10] | 
   let s_11 = AdderSum[a.b11, b.b11, c_11] | 
   let c_12 = AdderCarry[a.b11, b.b11, c_11] | 
   let s_12 = AdderSum[a.b12, b.b12, c_12] | 
   let c_13 = AdderCarry[a.b12, b.b12, c_12] | 
   let s_13 = AdderSum[a.b13, b.b13, c_13] | 
   let c_14 = AdderCarry[a.b13, b.b13, c_13] | 
   let s_14 = AdderSum[a.b14, b.b14, c_14] | 
   let c_15 = AdderCarry[a.b14, b.b14, c_14] | 
   let s_15 = AdderSum[a.b15, b.b15, c_15] | 
   let c_16 = AdderCarry[a.b15, b.b15, c_15] | 
      result.b00 in s_0 and
      result.b01 in s_1 and
      result.b02 in s_2 and
      result.b03 in s_3 and
      result.b04 in s_4 and
      result.b05 in s_5 and
      result.b06 in s_6 and
      result.b07 in s_7 and
      result.b08 in s_8 and
      result.b09 in s_9 and
      result.b10 in s_10 and
      result.b11 in s_11 and
      result.b12 in s_12 and
      result.b13 in s_13 and
      result.b14 in s_14 and
      result.b15 in s_15 and
      overflow = (Xor[c_16, c_15]) and
      result.b16 in overflow and
      result.b17 in false and
      result.b18 in false and
      result.b19 in false and
      result.b20 in false and
      result.b21 in false and
      result.b22 in false and
      result.b23 in false and
      result.b24 in false and
      result.b25 in false and
      result.b26 in false and
      result.b27 in false and
      result.b28 in false and
      result.b29 in false and
      result.b30 in false and
      result.b31 in false
}



pred pred_java_primitive_char_value_addCharIntToJavaPrimitiveIntegerValue[a: JavaPrimitiveCharValue, b: JavaPrimitiveIntegerValue, result: JavaPrimitiveIntegerValue, overflow: boolean] { 
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
   let s_8 = AdderSum[a.b08, b.b08, c_8] | 
   let c_9 = AdderCarry[a.b08, b.b08, c_8] | 
   let s_9 = AdderSum[a.b09, b.b09, c_9] | 
   let c_10 = AdderCarry[a.b09, b.b09, c_9] | 
   let s_10 = AdderSum[a.b10, b.b10, c_10] | 
   let c_11 = AdderCarry[a.b10, b.b10, c_10] | 
   let s_11 = AdderSum[a.b11, b.b11, c_11] | 
   let c_12 = AdderCarry[a.b11, b.b11, c_11] | 
   let s_12 = AdderSum[a.b12, b.b12, c_12] | 
   let c_13 = AdderCarry[a.b12, b.b12, c_12] | 
   let s_13 = AdderSum[a.b13, b.b13, c_13] | 
   let c_14 = AdderCarry[a.b13, b.b13, c_13] | 
   let s_14 = AdderSum[a.b14, b.b14, c_14] | 
   let c_15 = AdderCarry[a.b14, b.b14, c_14] | 
   let s_15 = AdderSum[a.b15, b.b15, c_15] | 
   let c_16 = AdderCarry[a.b15, b.b15, c_15] | 
   let s_16 = AdderSum[false, b.b16, c_16] | 
   let c_17 = AdderCarry[false, b.b16, c_16] | 
   let s_17 = AdderSum[false, b.b17, c_17] | 
   let c_18 = AdderCarry[false, b.b17, c_17] | 
   let s_18 = AdderSum[false, b.b18, c_18] | 
   let c_19 = AdderCarry[false, b.b18, c_18] | 
   let s_19 = AdderSum[false, b.b19, c_19] | 
   let c_20 = AdderCarry[false, b.b19, c_19] | 
   let s_20 = AdderSum[false, b.b20, c_20] | 
   let c_21 = AdderCarry[false, b.b20, c_20] | 
   let s_21 = AdderSum[false, b.b21, c_21] | 
   let c_22 = AdderCarry[false, b.b21, c_21] | 
   let s_22 = AdderSum[false, b.b22, c_22] | 
   let c_23 = AdderCarry[false, b.b22, c_22] | 
   let s_23 = AdderSum[false, b.b23, c_23] | 
   let c_24 = AdderCarry[false, b.b23, c_23] | 
   let s_24 = AdderSum[false, b.b24, c_24] | 
   let c_25 = AdderCarry[false, b.b24, c_24] | 
   let s_25 = AdderSum[false, b.b25, c_25] | 
   let c_26 = AdderCarry[false, b.b25, c_25] | 
   let s_26 = AdderSum[false, b.b26, c_26] | 
   let c_27 = AdderCarry[false, b.b26, c_26] | 
   let s_27 = AdderSum[false, b.b27, c_27] | 
   let c_28 = AdderCarry[false, b.b27, c_27] | 
   let s_28 = AdderSum[false, b.b28, c_28] | 
   let c_29 = AdderCarry[false, b.b28, c_28] | 
   let s_29 = AdderSum[false, b.b29, c_29] | 
   let c_30 = AdderCarry[false, b.b29, c_29] | 
   let s_30 = AdderSum[false, b.b30, c_30] | 
   let c_31 = AdderCarry[false, b.b30, c_30] | 
   let s_31 = AdderSum[false, b.b31, c_31] | 
   let c_32 = AdderCarry[false, b.b31, c_31] | 
      result.b00 in s_0 and
      result.b01 in s_1 and
      result.b02 in s_2 and
      result.b03 in s_3 and
      result.b04 in s_4 and
      result.b05 in s_5 and
      result.b06 in s_6 and
      result.b07 in s_7 and
      result.b08 in s_8 and
      result.b09 in s_9 and
      result.b10 in s_10 and
      result.b11 in s_11 and
      result.b12 in s_12 and
      result.b13 in s_13 and
      result.b14 in s_14 and
      result.b15 in s_15 and
      result.b16 in s_16 and
      result.b17 in s_17 and
      result.b18 in s_18 and
      result.b19 in s_19 and
      result.b20 in s_20 and
      result.b21 in s_21 and
      result.b22 in s_22 and
      result.b23 in s_23 and
      result.b24 in s_24 and
      result.b25 in s_25 and
      result.b26 in s_26 and
      result.b27 in s_27 and
      result.b28 in s_28 and
      result.b29 in s_29 and
      result.b30 in s_30 and
      result.b31 in s_31 and
      overflow = (Xor[c_32, c_31])
}



pred pred_java_primitive_char_value_addIntCharToJavaPrimitiveIntegerValue[a: JavaPrimitiveIntegerValue, b: JavaPrimitiveCharValue, result: JavaPrimitiveIntegerValue, overflow: boolean] { 
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
   let s_8 = AdderSum[a.b08, b.b08, c_8] | 
   let c_9 = AdderCarry[a.b08, b.b08, c_8] | 
   let s_9 = AdderSum[a.b09, b.b09, c_9] | 
   let c_10 = AdderCarry[a.b09, b.b09, c_9] | 
   let s_10 = AdderSum[a.b10, b.b10, c_10] | 
   let c_11 = AdderCarry[a.b10, b.b10, c_10] | 
   let s_11 = AdderSum[a.b11, b.b11, c_11] | 
   let c_12 = AdderCarry[a.b11, b.b11, c_11] | 
   let s_12 = AdderSum[a.b12, b.b12, c_12] | 
   let c_13 = AdderCarry[a.b12, b.b12, c_12] | 
   let s_13 = AdderSum[a.b13, b.b13, c_13] | 
   let c_14 = AdderCarry[a.b13, b.b13, c_13] | 
   let s_14 = AdderSum[a.b14, b.b14, c_14] | 
   let c_15 = AdderCarry[a.b14, b.b14, c_14] | 
   let s_15 = AdderSum[a.b15, b.b15, c_15] | 
   let c_16 = AdderCarry[a.b15, b.b15, c_15] | 
   let s_16 = AdderSum[a.b16, false, c_16] | 
   let c_17 = AdderCarry[a.b16,false, c_16] | 
   let s_17 = AdderSum[a.b17, false, c_17] | 
   let c_18 = AdderCarry[a.b17, false, c_17] | 
   let s_18 = AdderSum[a.b18, false, c_18] | 
   let c_19 = AdderCarry[a.b18, false, c_18] | 
   let s_19 = AdderSum[a.b19, false, c_19] | 
   let c_20 = AdderCarry[a.b19, false, c_19] | 
   let s_20 = AdderSum[a.b20, false, c_20] | 
   let c_21 = AdderCarry[a.b20, false, c_20] | 
   let s_21 = AdderSum[a.b21, false, c_21] | 
   let c_22 = AdderCarry[a.b21, false, c_21] | 
   let s_22 = AdderSum[a.b22, false, c_22] | 
   let c_23 = AdderCarry[a.b22, false, c_22] | 
   let s_23 = AdderSum[a.b23, false, c_23] | 
   let c_24 = AdderCarry[a.b23, false, c_23] | 
   let s_24 = AdderSum[a.b24, false, c_24] | 
   let c_25 = AdderCarry[a.b24, false, c_24] | 
   let s_25 = AdderSum[a.b25, false, c_25] | 
   let c_26 = AdderCarry[a.b25, false, c_25] | 
   let s_26 = AdderSum[a.b26, false, c_26] | 
   let c_27 = AdderCarry[a.b26, false, c_26] | 
   let s_27 = AdderSum[a.b27, false, c_27] | 
   let c_28 = AdderCarry[a.b27, false, c_27] | 
   let s_28 = AdderSum[a.b28, false, c_28] | 
   let c_29 = AdderCarry[a.b28, false, c_28] | 
   let s_29 = AdderSum[a.b29, false, c_29] | 
   let c_30 = AdderCarry[a.b29, false, c_29] | 
   let s_30 = AdderSum[a.b30, false, c_30] | 
   let c_31 = AdderCarry[a.b30, false, c_30] | 
   let s_31 = AdderSum[a.b31, false, c_31] | 
   let c_32 = AdderCarry[a.b31, false, c_31] | 
      result.b00 in s_0 and
      result.b01 in s_1 and
      result.b02 in s_2 and
      result.b03 in s_3 and
      result.b04 in s_4 and
      result.b05 in s_5 and
      result.b06 in s_6 and
      result.b07 in s_7 and
      result.b08 in s_8 and
      result.b09 in s_9 and
      result.b10 in s_10 and
      result.b11 in s_11 and
      result.b12 in s_12 and
      result.b13 in s_13 and
      result.b14 in s_14 and
      result.b15 in s_15 and
      result.b16 in s_16 and
      result.b17 in s_17 and
      result.b18 in s_18 and
      result.b19 in s_19 and
      result.b20 in s_20 and
      result.b21 in s_21 and
      result.b22 in s_22 and
      result.b23 in s_23 and
      result.b24 in s_24 and
      result.b25 in s_25 and
      result.b26 in s_26 and
      result.b27 in s_27 and
      result.b28 in s_28 and
      result.b29 in s_29 and
      result.b30 in s_30 and
      result.b31 in s_31 and
      overflow = (Xor[c_32, c_31])
}


pred pred_java_primitive_char_value_subCharIntToJavaPrimitiveIntegerValue[a: JavaPrimitiveCharValue, b: JavaPrimitiveIntegerValue, result: JavaPrimitiveIntegerValue, overflow: boolean] { 
	some i : JavaPrimitiveIntegerValue | pred_cast_char_to_int[a,i] and
	         pred_java_primitive_integer_value_sub[i,b,result,overflow]
}

pred pred_java_primitive_char_value_subIntCharToJavaPrimitiveIntegerValue[a: JavaPrimitiveIntegerValue, b: JavaPrimitiveCharValue, result: JavaPrimitiveIntegerValue, overflow: boolean] {
	some i : JavaPrimitiveIntegerValue | pred_cast_char_to_int[b,i] and pred_java_primitive_integer_value_sub[a,i,result,overflow]
}



pred pred_java_primitive_char_value_CharInteq[a: JavaPrimitiveCharValue, b: JavaPrimitiveIntegerValue] {
   a.b00 = b.b00
   a.b01 = b.b01
   a.b02 = b.b02
   a.b03 = b.b03
   a.b04 = b.b04
   a.b05 = b.b05
   a.b06 = b.b06
   a.b07 = b.b07
   a.b08 = b.b08
   a.b09 = b.b09
   a.b10 = b.b10
   a.b11 = b.b11
   a.b12 = b.b12
   a.b13 = b.b13
   a.b14 = b.b14
   a.b15 = b.b15
   false = b.b16
   false = b.b17
   false = b.b18
   false = b.b19
   false = b.b20
   false = b.b21
   false = b.b22
   false = b.b23
   false = b.b24
   false = b.b25
   false = b.b26
   false = b.b27
   false = b.b28
   false = b.b29
   false = b.b30
   false = b.b31
}



pred pred_java_primitive_char_value_IntChareq[a: JavaPrimitiveIntegerValue, b: JavaPrimitiveCharValue] {
   a.b00 = b.b00
   a.b01 = b.b01
   a.b02 = b.b02
   a.b03 = b.b03
   a.b04 = b.b04
   a.b05 = b.b05
   a.b06 = b.b06
   a.b07 = b.b07
   a.b08 = b.b08
   a.b09 = b.b09
   a.b10 = b.b10
   a.b11 = b.b11
   a.b12 = b.b12
   a.b13 = b.b13
   a.b14 = b.b14
   a.b15 = b.b15
   false = a.b16
   false = a.b17
   false = a.b18
   false = a.b19
   false = a.b20
   false = a.b21
   false = a.b22
   false = a.b23
   false = a.b24
   false = a.b25
   false = a.b26
   false = a.b27
   false = a.b28
   false = a.b29
   false = a.b30
   false = a.b31
}



pred pred_java_primitive_char_value_CharIntgt[a: JavaPrimitiveCharValue, b: JavaPrimitiveIntegerValue] {
   (b.b31 in true)
   or (b.b31 in false and b.b30 in false and b.b29 in false and b.b28 in false and b.b27 in false and b.b26 in false and b.b25 in false and b.b24 in false and b.b23 in false and b.b22 in false and b.b21 in false and b.b20 in false and b.b19 in false and b.b18 in false and b.b17 in false and b.b16 in false and a.b15 in true and b.b15 in false)
   or (b.b31 in false and b.b30 in false and b.b29 in false and b.b28 in false and b.b27 in false and b.b26 in false and b.b25 in false and b.b24 in false and b.b23 in false and b.b22 in false and b.b21 in false and b.b20 in false and b.b19 in false and b.b18 in false and b.b17 in false and b.b16 in false and a.b15 = b.b15 and a.b14 in true and b.b14 in false) 
   or (b.b31 in false and b.b30 in false and b.b29 in false and b.b28 in false and b.b27 in false and b.b26 in false and b.b25 in false and b.b24 in false and b.b23 in false and b.b22 in false and b.b21 in false and b.b20 in false and b.b19 in false and b.b18 in false and b.b17 in false and b.b16 in false and a.b15 = b.b15 and a.b14 = b.b14 and a.b13 in true and b.b13 in false)
   or (b.b31 in false and b.b30 in false and b.b29 in false and b.b28 in false and b.b27 in false and b.b26 in false and b.b25 in false and b.b24 in false and b.b23 in false and b.b22 in false and b.b21 in false and b.b20 in false and b.b19 in false and b.b18 in false and b.b17 in false and b.b16 in false and a.b15 = b.b15 and a.b14 = b.b14 and a.b13 = b.b13 and a.b12 in true and b.b12 in false)
   or (b.b31 in false and b.b30 in false and b.b29 in false and b.b28 in false and b.b27 in false and b.b26 in false and b.b25 in false and b.b24 in false and b.b23 in false and b.b22 in false and b.b21 in false and b.b20 in false and b.b19 in false and b.b18 in false and b.b17 in false and b.b16 in false and a.b15 = b.b15 and a.b14 = b.b14 and a.b13 = b.b13 and a.b12 = b.b12 and a.b11 in true and b.b11 in false)
   or (b.b31 in false and b.b30 in false and b.b29 in false and b.b28 in false and b.b27 in false and b.b26 in false and b.b25 in false and b.b24 in false and b.b23 in false and b.b22 in false and b.b21 in false and b.b20 in false and b.b19 in false and b.b18 in false and b.b17 in false and b.b16 in false and a.b15 = b.b15 and a.b14 = b.b14 and a.b13 = b.b13 and a.b12 = b.b12 and a.b11 = b.b11 and a.b10 in true and b.b10 in false)
   or (b.b31 in false and b.b30 in false and b.b29 in false and b.b28 in false and b.b27 in false and b.b26 in false and b.b25 in false and b.b24 in false and b.b23 in false and b.b22 in false and b.b21 in false and b.b20 in false and b.b19 in false and b.b18 in false and b.b17 in false and b.b16 in false and a.b15 = b.b15 and a.b14 = b.b14 and a.b13 = b.b13 and a.b12 = b.b12 and a.b11 = b.b11 and a.b10 = b.b10 and a.b09 in true and b.b09 in false)
   or (b.b31 in false and b.b30 in false and b.b29 in false and b.b28 in false and b.b27 in false and b.b26 in false and b.b25 in false and b.b24 in false and b.b23 in false and b.b22 in false and b.b21 in false and b.b20 in false and b.b19 in false and b.b18 in false and b.b17 in false and b.b16 in false and a.b15 = b.b15 and a.b14 = b.b14 and a.b13 = b.b13 and a.b12 = b.b12 and a.b11 = b.b11 and a.b10 = b.b10 and a.b09 = b.b09 and a.b08 in true and b.b08 in false)
   or (b.b31 in false and b.b30 in false and b.b29 in false and b.b28 in false and b.b27 in false and b.b26 in false and b.b25 in false and b.b24 in false and b.b23 in false and b.b22 in false and b.b21 in false and b.b20 in false and b.b19 in false and b.b18 in false and b.b17 in false and b.b16 in false and a.b15 = b.b15 and a.b14 = b.b14 and a.b13 = b.b13 and a.b12 = b.b12 and a.b11 = b.b11 and a.b10 = b.b10 and a.b09 = b.b09 and a.b08 = b.b08 and a.b07 in true and b.b07 in false)
   or (b.b31 in false and b.b30 in false and b.b29 in false and b.b28 in false and b.b27 in false and b.b26 in false and b.b25 in false and b.b24 in false and b.b23 in false and b.b22 in false and b.b21 in false and b.b20 in false and b.b19 in false and b.b18 in false and b.b17 in false and b.b16 in false and a.b15 = b.b15 and a.b14 = b.b14 and a.b13 = b.b13 and a.b12 = b.b12 and a.b11 = b.b11 and a.b10 = b.b10 and a.b09 = b.b09 and a.b08 = b.b08 and a.b07 = b.b07 and a.b06 in true and b.b06 in false)
   or (b.b31 in false and b.b30 in false and b.b29 in false and b.b28 in false and b.b27 in false and b.b26 in false and b.b25 in false and b.b24 in false and b.b23 in false and b.b22 in false and b.b21 in false and b.b20 in false and b.b19 in false and b.b18 in false and b.b17 in false and b.b16 in false and a.b15 = b.b15 and a.b14 = b.b14 and a.b13 = b.b13 and a.b12 = b.b12 and a.b11 = b.b11 and a.b10 = b.b10 and a.b09 = b.b09 and a.b08 = b.b08 and a.b07 = b.b07 and a.b06 = b.b06 and a.b05 in true and b.b05 in false)
   or (b.b31 in false and b.b30 in false and b.b29 in false and b.b28 in false and b.b27 in false and b.b26 in false and b.b25 in false and b.b24 in false and b.b23 in false and b.b22 in false and b.b21 in false and b.b20 in false and b.b19 in false and b.b18 in false and b.b17 in false and b.b16 in false and a.b15 = b.b15 and a.b14 = b.b14 and a.b13 = b.b13 and a.b12 = b.b12 and a.b11 = b.b11 and a.b10 = b.b10 and a.b09 = b.b09 and a.b08 = b.b08 and a.b07 = b.b07 and a.b06 = b.b06 and a.b05 = b.b05 and a.b04 in true and b.b04 in false)
   or (b.b31 in false and b.b30 in false and b.b29 in false and b.b28 in false and b.b27 in false and b.b26 in false and b.b25 in false and b.b24 in false and b.b23 in false and b.b22 in false and b.b21 in false and b.b20 in false and b.b19 in false and b.b18 in false and b.b17 in false and b.b16 in false and a.b15 = b.b15 and a.b14 = b.b14 and a.b13 = b.b13 and a.b12 = b.b12 and a.b11 = b.b11 and a.b10 = b.b10 and a.b09 = b.b09 and a.b08 = b.b08 and a.b07 = b.b07 and a.b06 = b.b06 and a.b05 = b.b05 and a.b04 = b.b04 and a.b03 in true and b.b03 in false)
   or (b.b31 in false and b.b30 in false and b.b29 in false and b.b28 in false and b.b27 in false and b.b26 in false and b.b25 in false and b.b24 in false and b.b23 in false and b.b22 in false and b.b21 in false and b.b20 in false and b.b19 in false and b.b18 in false and b.b17 in false and b.b16 in false and a.b15 = b.b15 and a.b14 = b.b14 and a.b13 = b.b13 and a.b12 = b.b12 and a.b11 = b.b11 and a.b10 = b.b10 and a.b09 = b.b09 and a.b08 = b.b08 and a.b07 = b.b07 and a.b06 = b.b06 and a.b05 = b.b05 and a.b04 = b.b04 and a.b03 = b.b03 and a.b02 in true and b.b02 in false)
   or (b.b31 in false and b.b30 in false and b.b29 in false and b.b28 in false and b.b27 in false and b.b26 in false and b.b25 in false and b.b24 in false and b.b23 in false and b.b22 in false and b.b21 in false and b.b20 in false and b.b19 in false and b.b18 in false and b.b17 in false and b.b16 in false and a.b15 = b.b15 and a.b14 = b.b14 and a.b13 = b.b13 and a.b12 = b.b12 and a.b11 = b.b11 and a.b10 = b.b10 and a.b09 = b.b09 and a.b08 = b.b08 and a.b07 = b.b07 and a.b06 = b.b06 and a.b05 = b.b05 and a.b04 = b.b04 and a.b03 = b.b03 and a.b02 = b.b02 and a.b01 in true and b.b01 in false)
   or (b.b31 in false and b.b30 in false and b.b29 in false and b.b28 in false and b.b27 in false and b.b26 in false and b.b25 in false and b.b24 in false and b.b23 in false and b.b22 in false and b.b21 in false and b.b20 in false and b.b19 in false and b.b18 in false and b.b17 in false and b.b16 in false and a.b15 = b.b15 and a.b14 = b.b14 and a.b13 = b.b13 and a.b12 = b.b12 and a.b11 = b.b11 and a.b10 = b.b10 and a.b09 = b.b09 and a.b08 = b.b08 and a.b07 = b.b07 and a.b06 = b.b06 and a.b05 = b.b05 and a.b04 = b.b04 and a.b03 = b.b03 and a.b02 = b.b02 and a.b01 = b.b01 and a.b00 in true and b.b00 in false)
}


pred pred_java_primitive_char_value_IntChargt[a: JavaPrimitiveIntegerValue, b: JavaPrimitiveCharValue]{
   (a.b31 in false and (a.b30 in true or a.b29 in true or a.b28 in true or a.b27 in true or a.b26 in true or a.b25 in true or a.b24 in true or a.b23 in true or a.b22 in true or a.b21 in true or a.b20 in true or a.b19 in true or a.b18 in true and a.b17 in true or a.b16 in true))
   or (a.b31 in false and a.b30 in false and a.b29 in false and a.b28 in false and a.b27 in false and a.b26 in false and a.b25 in false and a.b24 in false and a.b23 in false and a.b22 in false and a.b21 in false and a.b20 in false and a.b19 in false and a.b18 in false and a.b17 in false and a.b16 in false and a.b15 in true and b.b15 in false)
   or (a.b31 in false and a.b30 in false and a.b29 in false and a.b28 in false and a.b27 in false and a.b26 in false and a.b25 in false and a.b24 in false and a.b23 in false and a.b22 in false and a.b21 in false and a.b20 in false and a.b19 in false and a.b18 in false and a.b17 in false and a.b16 in false and a.b15 = b.b15 and a.b14 in true and b.b14 in false)
   or (a.b31 in false and a.b30 in false and a.b29 in false and a.b28 in false and a.b27 in false and a.b26 in false and a.b25 in false and a.b24 in false and a.b23 in false and a.b22 in false and a.b21 in false and a.b20 in false and a.b19 in false and a.b18 in false and a.b17 in false and a.b16 in false and a.b15 = b.b15 and a.b14 = b.b14 and a.b13 in true and b.b13 in false)
   or (a.b31 in false and a.b30 in false and a.b29 in false and a.b28 in false and a.b27 in false and a.b26 in false and a.b25 in false and a.b24 in false and a.b23 in false and a.b22 in false and a.b21 in false and a.b20 in false and a.b19 in false and a.b18 in false and a.b17 in false and a.b16 in false and a.b15 = b.b15 and a.b14 = b.b14 and a.b13 = b.b13 and a.b12 in true and b.b12 in false)
   or (a.b31 in false and a.b30 in false and a.b29 in false and a.b28 in false and a.b27 in false and a.b26 in false and a.b25 in false and a.b24 in false and a.b23 in false and a.b22 in false and a.b21 in false and a.b20 in false and a.b19 in false and a.b18 in false and a.b17 in false and a.b16 in false and a.b15 = b.b15 and a.b14 = b.b14 and a.b13 = b.b13 and a.b12 = b.b12 and a.b11 in true and b.b11 in false)
   or (a.b31 in false and a.b30 in false and a.b29 in false and a.b28 in false and a.b27 in false and a.b26 in false and a.b25 in false and a.b24 in false and a.b23 in false and a.b22 in false and a.b21 in false and a.b20 in false and a.b19 in false and a.b18 in false and a.b17 in false and a.b16 in false and a.b15 = b.b15 and a.b14 = b.b14 and a.b13 = b.b13 and a.b12 = b.b12 and a.b11 = b.b11 and a.b10 in true and b.b10 in false)
   or (a.b31 in false and a.b30 in false and a.b29 in false and a.b28 in false and a.b27 in false and a.b26 in false and a.b25 in false and a.b24 in false and a.b23 in false and a.b22 in false and a.b21 in false and a.b20 in false and a.b19 in false and a.b18 in false and a.b17 in false and a.b16 in false and a.b15 = b.b15 and a.b14 = b.b14 and a.b13 = b.b13 and a.b12 = b.b12 and a.b11 = b.b11 and a.b10 = b.b10 and a.b09 in true and b.b09 in false)
   or (a.b31 in false and a.b30 in false and a.b29 in false and a.b28 in false and a.b27 in false and a.b26 in false and a.b25 in false and a.b24 in false and a.b23 in false and a.b22 in false and a.b21 in false and a.b20 in false and a.b19 in false and a.b18 in false and a.b17 in false and a.b16 in false and a.b15 = b.b15 and a.b14 = b.b14 and a.b13 = b.b13 and a.b12 = b.b12 and a.b11 = b.b11 and a.b10 = b.b10 and a.b09 = b.b09 and a.b08 in true and b.b08 in false)
   or (a.b31 in false and a.b30 in false and a.b29 in false and a.b28 in false and a.b27 in false and a.b26 in false and a.b25 in false and a.b24 in false and a.b23 in false and a.b22 in false and a.b21 in false and a.b20 in false and a.b19 in false and a.b18 in false and a.b17 in false and a.b16 in false and a.b15 = b.b15 and a.b14 = b.b14 and a.b13 = b.b13 and a.b12 = b.b12 and a.b11 = b.b11 and a.b10 = b.b10 and a.b09 = b.b09 and a.b08 = b.b08 and a.b07 in true and b.b07 in false)
   or (a.b31 in false and a.b30 in false and a.b29 in false and a.b28 in false and a.b27 in false and a.b26 in false and a.b25 in false and a.b24 in false and a.b23 in false and a.b22 in false and a.b21 in false and a.b20 in false and a.b19 in false and a.b18 in false and a.b17 in false and a.b16 in false and a.b15 = b.b15 and a.b14 = b.b14 and a.b13 = b.b13 and a.b12 = b.b12 and a.b11 = b.b11 and a.b10 = b.b10 and a.b09 = b.b09 and a.b08 = b.b08 and a.b07 = b.b07 and a.b06 in true and b.b06 in false)
   or (a.b31 in false and a.b30 in false and a.b29 in false and a.b28 in false and a.b27 in false and a.b26 in false and a.b25 in false and a.b24 in false and a.b23 in false and a.b22 in false and a.b21 in false and a.b20 in false and a.b19 in false and a.b18 in false and a.b17 in false and a.b16 in false and a.b15 = b.b15 and a.b14 = b.b14 and a.b13 = b.b13 and a.b12 = b.b12 and a.b11 = b.b11 and a.b10 = b.b10 and a.b09 = b.b09 and a.b08 = b.b08 and a.b07 = b.b07 and a.b06 = b.b06 and a.b05 in true and b.b05 in false)
   or (a.b31 in false and a.b30 in false and a.b29 in false and a.b28 in false and a.b27 in false and a.b26 in false and a.b25 in false and a.b24 in false and a.b23 in false and a.b22 in false and a.b21 in false and a.b20 in false and a.b19 in false and a.b18 in false and a.b17 in false and a.b16 in false and a.b15 = b.b15 and a.b14 = b.b14 and a.b13 = b.b13 and a.b12 = b.b12 and a.b11 = b.b11 and a.b10 = b.b10 and a.b09 = b.b09 and a.b08 = b.b08 and a.b07 = b.b07 and a.b06 = b.b06 and a.b05 = b.b05 and a.b04 in true and b.b04 in false)
   or (a.b31 in false and a.b30 in false and a.b29 in false and a.b28 in false and a.b27 in false and a.b26 in false and a.b25 in false and a.b24 in false and a.b23 in false and a.b22 in false and a.b21 in false and a.b20 in false and a.b19 in false and a.b18 in false and a.b17 in false and a.b16 in false and a.b15 = b.b15 and a.b14 = b.b14 and a.b13 = b.b13 and a.b12 = b.b12 and a.b11 = b.b11 and a.b10 = b.b10 and a.b09 = b.b09 and a.b08 = b.b08 and a.b07 = b.b07 and a.b06 = b.b06 and a.b05 = b.b05 and a.b04 = b.b04 and a.b03 in true and b.b03 in false)
   or (a.b31 in false and a.b30 in false and a.b29 in false and a.b28 in false and a.b27 in false and a.b26 in false and a.b25 in false and a.b24 in false and a.b23 in false and a.b22 in false and a.b21 in false and a.b20 in false and a.b19 in false and a.b18 in false and a.b17 in false and a.b16 in false and a.b15 = b.b15 and a.b14 = b.b14 and a.b13 = b.b13 and a.b12 = b.b12 and a.b11 = b.b11 and a.b10 = b.b10 and a.b09 = b.b09 and a.b08 = b.b08 and a.b07 = b.b07 and a.b06 = b.b06 and a.b05 = b.b05 and a.b04 = b.b04 and a.b03 = b.b03 and a.b02 in true and b.b02 in false)
   or (a.b31 in false and a.b30 in false and a.b29 in false and a.b28 in false and a.b27 in false and a.b26 in false and a.b25 in false and a.b24 in false and a.b23 in false and a.b22 in false and a.b21 in false and a.b20 in false and a.b19 in false and a.b18 in false and a.b17 in false and a.b16 in false and a.b15 = b.b15 and a.b14 = b.b14 and a.b13 = b.b13 and a.b12 = b.b12 and a.b11 = b.b11 and a.b10 = b.b10 and a.b09 = b.b09 and a.b08 = b.b08 and a.b07 = b.b07 and a.b06 = b.b06 and a.b05 = b.b05 and a.b04 = b.b04 and a.b03 = b.b03 and a.b02 = b.b02 and a.b01 in true and b.b01 in false)
   or (a.b31 in false and a.b30 in false and a.b29 in false and a.b28 in false and a.b27 in false and a.b26 in false and a.b25 in false and a.b24 in false and a.b23 in false and a.b22 in false and a.b21 in false and a.b20 in false and a.b19 in false and a.b18 in false and a.b17 in false and a.b16 in false and a.b15 = b.b15 and a.b14 = b.b14 and a.b13 = b.b13 and a.b12 = b.b12 and a.b11 = b.b11 and a.b10 = b.b10 and a.b09 = b.b09 and a.b08 = b.b08 and a.b07 = b.b07 and a.b06 = b.b06 and a.b05 = b.b05 and a.b04 = b.b04 and a.b03 = b.b03 and a.b02 = b.b02 and a.b01 = b.b01 and a.b00 in true and b.b00 in false)
}



pred pred_java_primitive_char_value_CharIntgte[a: JavaPrimitiveCharValue, b: JavaPrimitiveIntegerValue]{
	pred_java_primitive_char_value_CharIntgt[a, b] or pred_java_primitive_char_value_CharInteq[a, b]
}


pred pred_java_primitive_char_value_IntChargte[a: JavaPrimitiveIntegerValue, b: JavaPrimitiveCharValue]{
	pred_java_primitive_char_value_IntChargt[a, b] or pred_java_primitive_char_value_IntChareq[a, b]
}


pred pred_java_primitive_char_value_CharIntlt[a: JavaPrimitiveCharValue, b:JavaPrimitiveIntegerValue]{
   not pred_java_primitive_char_value_CharIntgte[a, b]
}


pred pred_java_primitive_char_value_IntCharlt[a: JavaPrimitiveIntegerValue, b: JavaPrimitiveCharValue]{
   not pred_java_primitive_char_value_IntChargte[a, b]
}


pred pred_java_primitive_char_value_CharIntlte[a: JavaPrimitiveCharValue, b:JavaPrimitiveIntegerValue]{
   pred_java_primitive_char_value_CharIntlt[a, b] or pred_java_primitive_char_value_CharInteq[a, b]
}


pred pred_java_primitive_char_value_IntCharlte[a: JavaPrimitiveIntegerValue, b:JavaPrimitiveCharValue]{
   pred_java_primitive_char_value_IntCharlt[a, b] or pred_java_primitive_char_value_IntChareq[a, b]
}


pred pred_narrowing_cast_int_to_char[a : JavaPrimitiveIntegerValue, b : JavaPrimitiveCharValue]{
  b.b00 = a.b00 and
  b.b01 = a.b01 and
  b.b02 = a.b02 and
  b.b03 = a.b03 and
  b.b04 = a.b04 and
  b.b05 = a.b05 and
  b.b06 = a.b06 and
  b.b07 = a.b07 and
  b.b08 = a.b08 and
  b.b09 = a.b09 and
  b.b10 = a.b10 and
  b.b11 = a.b11 and
  b.b12 = a.b12 and
  b.b13 = a.b13 and
  b.b14 = a.b14 and
  b.b15 = a.b15 
}




// FUNCTIONS

fun fun_narrowing_cast_int_to_char[a : JavaPrimitiveIntegerValue] : JavaPrimitiveCharValue {
  {result : JavaPrimitiveCharValue | pred_narrowing_cast_int_to_char[a, result]}
}



fun fun_java_primitive_integer_value_add[a: JavaPrimitiveIntegerValue, b: JavaPrimitiveIntegerValue]: JavaPrimitiveIntegerValue { 
  {result: JavaPrimitiveIntegerValue | some overflow: boolean | pred_java_primitive_integer_value_add[a,b,result,overflow]}
}

fun fun_java_primitive_integer_value_sub[a: JavaPrimitiveIntegerValue, b: JavaPrimitiveIntegerValue]: JavaPrimitiveIntegerValue {
 {result: JavaPrimitiveIntegerValue | some overflow: boolean | pred_java_primitive_integer_value_add[b,result,a,overflow] }
}

fun fun_java_primitive_integer_value_negate[a: JavaPrimitiveIntegerValue]: JavaPrimitiveIntegerValue {
 {result: JavaPrimitiveIntegerValue | pred_java_primitive_integer_value_negate[a,result] }
}

fun fun_java_primitive_integer_value_mul[a: JavaPrimitiveIntegerValue, b: JavaPrimitiveIntegerValue]: JavaPrimitiveIntegerValue {
 {result: JavaPrimitiveIntegerValue | some overflow: boolean | pred_java_primitive_integer_value_mul[a,b,result,overflow] }
}

fun fun_java_primitive_integer_value_div[a: JavaPrimitiveIntegerValue, b: JavaPrimitiveIntegerValue]: JavaPrimitiveIntegerValue {
 {div: JavaPrimitiveIntegerValue | some rem: JavaPrimitiveIntegerValue | pred_java_primitive_integer_value_div_rem[a,b,div,rem] }
}

fun fun_java_primitive_integer_value_rem[a: JavaPrimitiveIntegerValue, b: JavaPrimitiveIntegerValue]: JavaPrimitiveIntegerValue {
 {rem: JavaPrimitiveIntegerValue | some div: JavaPrimitiveIntegerValue | pred_java_primitive_integer_value_div_rem[a,b,div,rem] }
}

fun fun_java_primitive_integer_value_sshr[a: JavaPrimitiveIntegerValue]: JavaPrimitiveIntegerValue {
  { ret: JavaPrimitiveIntegerValue | pred_java_primitive_integer_value_sshr[a,ret] } 
}

fun fun_java_primitive_char_value_addCharCharToJavaPrimitiveIntegerValue[a: JavaPrimitiveCharValue, b: JavaPrimitiveCharValue] : JavaPrimitiveIntegerValue {
  {result: JavaPrimitiveIntegerValue | some overflow: boolean | pred_java_primitive_char_value_addCharCharToJavaPrimitiveIntegerValue[a,b,result,overflow]}
}

fun fun_java_primitive_char_value_addCharIntToJavaPrimitiveIntegerValue[a: JavaPrimitiveCharValue, b: JavaPrimitiveIntegerValue] : JavaPrimitiveIntegerValue {
  {result: JavaPrimitiveIntegerValue | some overflow: boolean | pred_java_primitive_char_value_addCharIntToJavaPrimitiveIntegerValue[a,b,result,overflow]}
}

fun fun_java_primitive_char_value_addIntCharToJavaPrimitiveIntegerValue[a: JavaPrimitiveIntegerValue, b: JavaPrimitiveCharValue] : JavaPrimitiveIntegerValue {
  {result: JavaPrimitiveIntegerValue | some overflow: boolean | pred_java_primitive_char_value_addIntCharToJavaPrimitiveIntegerValue[a,b,result,overflow]}
}


fun fun_java_primitive_char_value_subCharCharToJavaPrimitiveIntegerValue[a: JavaPrimitiveCharValue, b: JavaPrimitiveCharValue] : JavaPrimitiveIntegerValue {
  {result: JavaPrimitiveIntegerValue | some overflow: boolean | pred_java_primitive_char_value_subCharCharToJavaPrimitiveIntegerValue[a,b,result,overflow]}
}

fun fun_java_primitive_char_value_subCharIntToJavaPrimitiveIntegerValue[a: JavaPrimitiveCharValue, b: JavaPrimitiveIntegerValue] : JavaPrimitiveIntegerValue {
  {result: JavaPrimitiveIntegerValue | some overflow: boolean | pred_java_primitive_char_value_subCharIntToJavaPrimitiveIntegerValue[a,b,result,overflow]}
}

fun fun_java_primitive_char_value_subIntCharToJavaPrimitiveIntegerValue[a: JavaPrimitiveIntegerValue, b: JavaPrimitiveCharValue] : JavaPrimitiveIntegerValue {
  {result: JavaPrimitiveIntegerValue | some overflow: boolean | pred_java_primitive_char_value_subIntCharToJavaPrimitiveIntegerValue[a,b,result,overflow]}
}


fun fun_cast_char_to_int[a : JavaPrimitiveCharValue] : JavaPrimitiveIntegerValue {
	{result : JavaPrimitiveIntegerValue | pred_cast_char_to_int[a,result]}
}