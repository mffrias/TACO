/*
Authors: Marcelo Frias

PREDICATES:

pred_java_primitive_char_value_decrementToChar (char--)
pred_java_primitive_char_value_subCharCharToJavaPrimitiveIntegerValue (- : char x char -> int)
pred_java_primitive_char_value_subCharIntToJavaPrimitiveIntegerValue (- : char x int -> int)
pred_java_primitive_char_value_subIntCharToJavaPrimitiveIntegerValue (- : int x char -> int)
pred_java_primitive_char_value_CharInteq (== in char x int)
pred_java_primitive_char_value_IntChareq (== in int x char)
pred_java_primitive_char_value_CharChareq (== in char x char)
pred_java_primitive_char_value_CharIntgt (> in char x int)
pred_java_primitive_char_value_IntChargt (> in int x char)
pred_java_primitive_char_value_CharChargt (> in char x char)
pred_java_primitive_char_value_CharIntgte (>= in char x int)
pred_java_primitive_char_value_IntChargte (>= in int x char)
pred_java_primitive_char_value_CharChargte (>= in char x char)
pred_java_primitive_char_value_CharIntlt (< in char x int)
pred_java_primitive_char_value_IntCharlt (< in int x char)
pred_java_primitive_char_value_CharCharlt (< in char x char)
pred_java_primitive_char_value_CharIntlte (< in char x int)
pred_java_primitive_char_value_IntCharlte (< in int x char)
pred_java_primitive_char_value_CharCharlte (< in char x char)



FUNCTIONS:




*/

// PREDICATES





pred pred_java_primitive_char_value_subCharCharToJavaPrimitiveIntegerValue[a: JavaPrimitiveCharValue, b: JavaPrimitiveCharValue, result: JavaPrimitiveIntegerValue, overflow: boolean] { 
	some i1, i2 : JavaPrimitiveIntegerValue | 
	     pred_cast_char_to_int[a,i1] and 
	     pred_cast_char_to_int[b,i2] and
	     pred_java_primitive_integer_value_sub[i1,i2,result,overflow]
}



pred pred_java_primitive_char_value_CharChareq[a: JavaPrimitiveCharValue, b: JavaPrimitiveCharValue] {
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
}






pred pred_java_primitive_char_value_CharChargt[a: JavaPrimitiveCharValue, b: JavaPrimitiveCharValue]{
   (a.b15 in true and b.b15 in false) 
   or (a.b15 = b.b15 and a.b14 in true and b.b14 in false) 
   or (a.b15 = b.b15 and a.b14 = b.b14 and a.b13 in true and b.b13 in false) 
   or (a.b15 = b.b15 and a.b14 = b.b14 and a.b13 = b.b13 and a.b12 in true and b.b12 in false) 
   or (a.b15 = b.b15 and a.b14 = b.b14 and a.b13 = b.b13 and a.b12 = b.b12 and a.b11 in true and b.b11 in false) 
   or (a.b15 = b.b15 and a.b14 = b.b14 and a.b13 = b.b13 and a.b12 = b.b12 and a.b11 = b.b11 and a.b10 in true and b.b10 in false) 
   or (a.b15 = b.b15 and a.b14 = b.b14 and a.b13 = b.b13 and a.b12 = b.b12 and a.b11 = b.b11 and a.b10 = b.b10 and a.b09 in true and b.b09 in false) 
   or (a.b15 = b.b15 and a.b14 = b.b14 and a.b13 = b.b13 and a.b12 = b.b12 and a.b11 = b.b11 and a.b10 = b.b10 and a.b09 = b.b09 and a.b08 in true and b.b08 in false) 
   or (a.b15 = b.b15 and a.b14 = b.b14 and a.b13 = b.b13 and a.b12 = b.b12 and a.b11 = b.b11 and a.b10 = b.b10 and a.b09 = b.b09 and a.b08 = b.b08 and a.b07 in true and b.b07 in false) 
   or (a.b15 = b.b15 and a.b14 = b.b14 and a.b13 = b.b13 and a.b12 = b.b12 and a.b11 = b.b11 and a.b10 = b.b10 and a.b09 = b.b09 and a.b08 = b.b08 and a.b07 = b.b07 and a.b06 in true and b.b06 in false) 
   or (a.b15 = b.b15 and a.b14 = b.b14 and a.b13 = b.b13 and a.b12 = b.b12 and a.b11 = b.b11 and a.b10 = b.b10 and a.b09 = b.b09 and a.b08 = b.b08 and a.b07 = b.b07 and a.b06 = b.b06 and a.b05 in true and b.b05 in false) 
   or (a.b15 = b.b15 and a.b14 = b.b14 and a.b13 = b.b13 and a.b12 = b.b12 and a.b11 = b.b11 and a.b10 = b.b10 and a.b09 = b.b09 and a.b08 = b.b08 and a.b07 = b.b07 and a.b06 = b.b06 and a.b05 = b.b05 and a.b04 in true and b.b04 in false) 
   or (a.b15 = b.b15 and a.b14 = b.b14 and a.b13 = b.b13 and a.b12 = b.b12 and a.b11 = b.b11 and a.b10 = b.b10 and a.b09 = b.b09 and a.b08 = b.b08 and a.b07 = b.b07 and a.b06 = b.b06 and a.b05 = b.b05 and a.b04 = b.b04 and a.b03 in true and b.b03 in false) 
   or (a.b15 = b.b15 and a.b14 = b.b14 and a.b13 = b.b13 and a.b12 = b.b12 and a.b11 = b.b11 and a.b10 = b.b10 and a.b09 = b.b09 and a.b08 = b.b08 and a.b07 = b.b07 and a.b06 = b.b06 and a.b05 = b.b05 and a.b04 = b.b04 and a.b03 = b.b03 and a.b02 in true and b.b02 in false) 
   or (a.b15 = b.b15 and a.b14 = b.b14 and a.b13 = b.b13 and a.b12 = b.b12 and a.b11 = b.b11 and a.b10 = b.b10 and a.b09 = b.b09 and a.b08 = b.b08 and a.b07 = b.b07 and a.b06 = b.b06 and a.b05 = b.b05 and a.b04 = b.b04 and a.b03 = b.b03 and a.b02 = b.b02 and a.b01 in true and b.b01 in false) 
   or (a.b15 = b.b15 and a.b14 = b.b14 and a.b13 = b.b13 and a.b12 = b.b12 and a.b11 = b.b11 and a.b10 = b.b10 and a.b09 = b.b09 and a.b08 = b.b08 and a.b07 = b.b07 and a.b06 = b.b06 and a.b05 = b.b05 and a.b04 = b.b04 and a.b03 = b.b03 and a.b02 = b.b02 and a.b01 = b.b01 and a.b00 in true and b.b00 in false) 
   
}



	
pred pred_java_primitive_char_value_CharChargte[a: JavaPrimitiveCharValue, b: JavaPrimitiveCharValue]{
	pred_java_primitive_char_value_CharChargt[a, b] or pred_java_primitive_char_value_CharChareq[a, b]
}



pred pred_java_primitive_char_value_CharCharlt[a: JavaPrimitiveCharValue, b: JavaPrimitiveCharValue]{
   not pred_java_primitive_char_value_CharChargte[a, b]
}




pred pred_java_primitive_char_value_CharCharlte[a: JavaPrimitiveCharValue, b:JavaPrimitiveCharValue]{
   pred_java_primitive_char_value_CharCharlt[a, b] or pred_java_primitive_char_value_CharChareq[a, b]
}



// FUNCTIONS

