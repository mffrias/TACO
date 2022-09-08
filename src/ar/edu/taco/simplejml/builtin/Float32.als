/*
Module "float" modeling real numbers represented according to standard IEEE 754. 
- Addition uses Round, Guard and Sticky bit. 
- It is costrained to normalized numbers.
- Authors: Marcelo Frias and Pablo Abad

IMPORTANT: Negation "!" does not distribute over equality (i.e., "!=" is not the negation of "==".


The following predicates are provided:

- pred pred_java_primitive_float_value_integrity_check[]
- pred pred_java_primitive_float_value_eq[f1, f2 : JavaPrimitiveFloatValue]{ --This predicate models "=="
- pred pred_java_primitive_float_value_neq[f1, f2 : JavaPrimitiveFloatValue]{ --This predicate models "!="
- pred pred_java_primitive_float_value_gt[f1, f2 : JavaPrimitiveFloatValue] -- This predicate models ">"
- pred pred_java_primitive_float_value_gte[f1, f2 : JavaPrimitiveFloatValue] -- This predicate models ">="
- pred pred_java_primitive_float_value_lt[f1,f2 : JavaPrimitiveFloatValue] -- This predicate models "<"
- pred pred_java_primitive_float_value_lte[f1, f2 : JavaPrimitiveFloatValue] -- This predicate models "<="
- pred pred_java_primitive_float_value_is_infinite[f : JavaPrimitiveFloatValue]
- pred pred_java_primitive_float_value_is_NaN[f : JavaPrimitiveFloatValue]


The following operations are provided:

- pred pred_java_primitive_float_value_add[n1,n2,r : JavaPrimitiveFloatValue, compatibility_arg: boolean] -- This operation models "+"
- pred pred_java_primitive_float_value_sub[f1, f2, r : JavaPrimitiveFloatValue, compatibility_arg: boolean] -- This operation modesl "-"
- pred pred_java_primitive_float_value_mul[f1, f2, r : JavaPrimitiveFloatValue, compatibility_arg: boolean] -- This operation modesl "*"
- pred pred_java_primitive_float_value_div[f1, f2, r : JavaPrimitiveFloatValue, compatibility_arg:JavaPrimitiveFloatValue ] -- This operation modesl "/"

Marker predicates
- pred pred_java_primitive_float_value_mul_marker[f1, f2, r : JavaPrimitiveFloatValue, compatibility_arg: boolean] 
- pred pred_java_primitive_float_value_div_marker[f1, f2, r : JavaPrimitiveFloatValue, compatibility_arg:JavaPrimitiveFloatValue ] 

*/

fun fun_java_primitive_float_value_add[a: JavaPrimitiveFloatValue, b: JavaPrimitiveFloatValue]: JavaPrimitiveFloatValue { 
  {result: JavaPrimitiveFloatValue | some compatibility_arg: boolean | pred_java_primitive_float_value_add[a,b,result,compatibility_arg]}
}

fun fun_java_primitive_float_value_sub[a: JavaPrimitiveFloatValue, b: JavaPrimitiveFloatValue]: JavaPrimitiveFloatValue { 
  {result: JavaPrimitiveFloatValue | some compatibility_arg: boolean | pred_java_primitive_float_value_sub[a,b,result,compatibility_arg]}
}

fun fun_java_primitive_float_value_mul[a: JavaPrimitiveFloatValue, b: JavaPrimitiveFloatValue]: JavaPrimitiveFloatValue { 
  {result: JavaPrimitiveFloatValue | some compatibility_arg: boolean | pred_java_primitive_float_value_mul[a,b,result,compatibility_arg]}
}

fun fun_java_primitive_float_value_div[a: JavaPrimitiveFloatValue, b: JavaPrimitiveFloatValue]: JavaPrimitiveFloatValue { 
  {result: JavaPrimitiveFloatValue | some compatibility_arg: JavaPrimitiveFloatValue | pred_java_primitive_float_value_div[a,b,result,compatibility_arg]}
}

pred pred_java_primitive_float_value_integrity_check[] {
  all f1, f2 : JavaPrimitiveFloatValue | (isBitNormalized[f1.b31,f1.b30,f1.b29,f1.b28,f1.b27,f1.b26,f1.b25,f1.b24,f1.b23,f1.b22,f1.b21,f1.b20,f1.b19,f1.b18,f1.b17,f1.b16,f1.b15,f1.b14,f1.b13,f1.b12,f1.b11,f1.b10,f1.b09,f1.b08,f1.b07,f1.b06,f1.b05,f1.b04,f1.b03,f1.b02,f1.b01,f1.b00] and isBitNormalized[f2.b31,f2.b30,f2.b29,f2.b28,f2.b27,f2.b26,f2.b25,f2.b24,f2.b23,f2.b22,f2.b21,f2.b20,f2.b19,f2.b18,f2.b17,f2.b16,f2.b15,f2.b14,f2.b13,f2.b12,f2.b11,f2.b10,f2.b09,f2.b08,f2.b07,f2.b06,f2.b05,f2.b04,f2.b03,f2.b02,f2.b01,f2.b00] and f1.b31 = f2.b31 and f1.b30 = f2.b30 and f1.b29 = f2.b29 and f1.b28 = f2.b28 and f1.b27 = f2.b27 and f1.b26 = f2.b26 and f1.b25= f2.b25 and f1.b24 = f2.b24 and f1.b23 = f2.b23 and f1.b22 = f2.b22 and f1.b21 = f2.b21 and f1.b20 = f2.b20 and f1.b19 = f2.b19 and f1.b18 = f2.b18 and f1.b17 = f2.b17 and f1.b16 = f2.b16 and f1.b15 = f2.b15 and f1.b14 = f2.b14 and f1.b13 = f2.b13 and f1.b12 = f2.b12 and f1.b11 = f2.b11 and f1.b10 = f2.b10 and f1.b09 = f2.b09 and f1.b08 = f2.b08 and f1.b07 = f2.b07 and f1.b06 = f2.b06 and f1.b05 = f2.b05 and f1.b04 = f2.b04 and f1.b03 = f2.b03 and f1.b02 = f2.b02 and f1.b01 = f2.b01 and f1.b00 = f2.b00) implies f1=f2

}



pred pred_java_primitive_float_value_eq[f1, f2 : JavaPrimitiveFloatValue]{ --This predicate models ==
	(isNormalized[f1] and isNormalized[f2] and sameBits[f1,f2])
	or
	(pred_java_primitive_float_value_is_infinite[f1] and pred_java_primitive_float_value_is_infinite[f2] and f1.b31 = f2.b31)
	or 
	isZero[f1] and isZero[f2]
}


pred pred_java_primitive_float_value_neq[f1, f2 : JavaPrimitiveFloatValue]{ --This predicate models !=
	(isNormalized[f1] and isNormalized[f2] and !sameBits[f1,f2])
	or
	(isNormalized[f1] and isZero[f2])
	or 
	(isNormalized[f1] and pred_java_primitive_float_value_is_infinite[f2])
	or
	(isZero[f1] and isNormalized[f2])
	or
	(isZero[f1] and pred_java_primitive_float_value_is_infinite[f2])
	or
	(pred_java_primitive_float_value_is_infinite[f1] and isNormalized[f2])
	or	
	(pred_java_primitive_float_value_is_infinite[f1] and isZero[f2])
	or
	(pred_java_primitive_float_value_is_infinite[f1] and pred_java_primitive_float_value_is_infinite[f2] and f1.b31!=f2.b31)
	or	
	pred_java_primitive_float_value_is_NaN[f1]
	or
	pred_java_primitive_float_value_is_NaN[f2]
}


pred pred_java_primitive_float_value_gt[f1, f2 : JavaPrimitiveFloatValue]{
	!isBitNaN[f1.b31,f1.b30,f1.b29,f1.b28,f1.b27,f1.b26,f1.b25,f1.b24,f1.b23,f1.b22,f1.b21,f1.b20,f1.b19,f1.b18,f1.b17,f1.b16,f1.b15,f1.b14,f1.b13,f1.b12,f1.b11,f1.b10,f1.b09,f1.b08,f1.b07,f1.b06,f1.b05,f1.b04,f1.b03,f1.b02,f1.b01,f1.b00] 
	and
	!isBitNaN[f2.b31,f2.b30,f2.b29,f2.b28,f2.b27,f2.b26,f2.b25,f2.b24,f2.b23,f2.b22,f2.b21,f2.b20,f2.b19,f2.b18,f2.b17,f2.b16,f2.b15,f2.b14,f2.b13,f2.b12,f2.b11,f2.b10,f2.b09,f2.b08,f2.b07,f2.b06,f2.b05,f2.b04,f2.b03,f2.b02,f2.b01,f2.b00] 
	and
	(
		(f1.b31=false and isBitInfinity[f1.b31,f1.b30,f1.b29,f1.b28,f1.b27,f1.b26,f1.b25,f1.b24,f1.b23,f1.b22,f1.b21,f1.b20,f1.b19,f1.b18,f1.b17,f1.b16,f1.b15,f1.b14,f1.b13,f1.b12,f1.b11,f1.b10,f1.b09,f1.b08,f1.b07,f1.b06,f1.b05,f1.b04,f1.b03,f1.b02,f1.b01,f1.b00]) implies
			(f2.b31 = false implies !isBitInfinity[f2.b31,f2.b30,f2.b29,f2.b28,f2.b27,f2.b26,f2.b25,f2.b24,f2.b23,f2.b22,f2.b21,f2.b20,f2.b19,f2.b18,f2.b17,f2.b16,f2.b15,f2.b14,f2.b13,f2.b12,f2.b11,f2.b10,f2.b09,f2.b08,f2.b07,f2.b06,f2.b05,f2.b04,f2.b03,f2.b02,f2.b01,f2.b00])
	)
	and
	(
		(isBitZero[f1.b31,f1.b30,f1.b29,f1.b28,f1.b27,f1.b26,f1.b25,f1.b24,f1.b23,f1.b22,f1.b21,f1.b20,f1.b19,f1.b18,f1.b17,f1.b16,f1.b15,f1.b14,f1.b13,f1.b12,f1.b11,f1.b10,f1.b09,f1.b08,f1.b07,f1.b06,f1.b05,f1.b04,f1.b03,f1.b02,f1.b01,f1.b00]) implies
		(
			(	isBitNormalized[f2.b31,f2.b30,f2.b29,f2.b28,f2.b27,f2.b26,f2.b25,f2.b24,f2.b23,f2.b22,f2.b21,f2.b20,f2.b19,f2.b18,f2.b17,f2.b16,f2.b15,f2.b14,f2.b13,f2.b12,f2.b11,f2.b10,f2.b09,f2.b08,f2.b07,f2.b06,f2.b05,f2.b04,f2.b03,f2.b02,f2.b01,f2.b00] implies 
					f2.b31=true
			)
			and
			(
				isBitInfinity[f2.b31,f2.b30,f2.b29,f2.b28,f2.b27,f2.b26,f2.b25,f2.b24,f2.b23,f2.b22,f2.b21,f2.b20,f2.b19,f2.b18,f2.b17,f2.b16,f2.b15,f2.b14,f2.b13,f2.b12,f2.b11,f2.b10,f2.b09,f2.b08,f2.b07,f2.b06,f2.b05,f2.b04,f2.b03,f2.b02,f2.b01,f2.b00] implies
					f2.b31 = true
			)
			and
				!isBitZero[f2.b31,f2.b30,f2.b29,f2.b28,f2.b27,f2.b26,f2.b25,f2.b24,f2.b23,f2.b22,f2.b21,f2.b20,f2.b19,f2.b18,f2.b17,f2.b16,f2.b15,f2.b14,f2.b13,f2.b12,f2.b11,f2.b10,f2.b09,f2.b08,f2.b07,f2.b06,f2.b05,f2.b04,f2.b03,f2.b02,f2.b01,f2.b00]
		)
	)
	and
	(
		isBitInfinity[f1.b31,f1.b30,f1.b29,f1.b28,f1.b27,f1.b26,f1.b25,f1.b24,f1.b23,f1.b22,f1.b21,f1.b20,f1.b19,f1.b18,f1.b17,f1.b16,f1.b15,f1.b14,f1.b13,f1.b12,f1.b11,f1.b10,f1.b09,f1.b08,f1.b07,f1.b06,f1.b05,f1.b04,f1.b03,f1.b02,f1.b01,f1.b00] implies
			f1.b31 = false
	)
	and
	(
		(isBitNormalized[f1.b31,f1.b30,f1.b29,f1.b28,f1.b27,f1.b26,f1.b25,f1.b24,f1.b23,f1.b22,f1.b21,f1.b20,f1.b19,f1.b18,f1.b17,f1.b16,f1.b15,f1.b14,f1.b13,f1.b12,f1.b11,f1.b10,f1.b09,f1.b08,f1.b07,f1.b06,f1.b05,f1.b04,f1.b03,f1.b02,f1.b01,f1.b00] and isBitZero[f2.b31,f2.b30,f2.b29,f2.b28,f2.b27,f2.b26,f2.b25,f2.b24,f2.b23,f2.b22,f2.b21,f2.b20,f2.b19,f2.b18,f2.b17,f2.b16,f2.b15,f2.b14,f2.b13,f2.b12,f2.b11,f2.b10,f2.b09,f2.b08,f2.b07,f2.b06,f2.b05,f2.b04,f2.b03,f2.b02,f2.b01,f2.b00]) implies
			f1.b31=false
	)
	and
	(
		(isBitNormalized[f1.b31,f1.b30,f1.b29,f1.b28,f1.b27,f1.b26,f1.b25,f1.b24,f1.b23,f1.b22,f1.b21,f1.b20,f1.b19,f1.b18,f1.b17,f1.b16,f1.b15,f1.b14,f1.b13,f1.b12,f1.b11,f1.b10,f1.b09,f1.b08,f1.b07,f1.b06,f1.b05,f1.b04,f1.b03,f1.b02,f1.b01,f1.b00] and isBitInfinity[f2.b31,f2.b30,f2.b29,f2.b28,f2.b27,f2.b26,f2.b25,f2.b24,f2.b23,f2.b22,f2.b21,f2.b20,f2.b19,f2.b18,f2.b17,f2.b16,f2.b15,f2.b14,f2.b13,f2.b12,f2.b11,f2.b10,f2.b09,f2.b08,f2.b07,f2.b06,f2.b05,f2.b04,f2.b03,f2.b02,f2.b01,f2.b00]) implies
			f2.b31=true
	)
	and 
	(
		(isBitNormalized[f1.b31,f1.b30,f1.b29,f1.b28,f1.b27,f1.b26,f1.b25,f1.b24,f1.b23,f1.b22,f1.b21,f1.b20,f1.b19,f1.b18,f1.b17,f1.b16,f1.b15,f1.b14,f1.b13,f1.b12,f1.b11,f1.b10,f1.b09,f1.b08,f1.b07,f1.b06,f1.b05,f1.b04,f1.b03,f1.b02,f1.b01,f1.b00] and isBitNormalized[f2.b31,f2.b30,f2.b29,f2.b28,f2.b27,f2.b26,f2.b25,f2.b24,f2.b23,f2.b22,f2.b21,f2.b20,f2.b19,f2.b18,f2.b17,f2.b16,f2.b15,f2.b14,f2.b13,f2.b12,f2.b11,f2.b10,f2.b09,f2.b08,f2.b07,f2.b06,f2.b05,f2.b04,f2.b03,f2.b02,f2.b01,f2.b00]) implies
			(
				f2.b31=false implies
				( 
					f1.b31=false 
					and
					(
						booleanwiseGT8[f1.b30,f1.b29,f1.b28,f1.b27,f1.b26,f1.b25,f1.b24,f1.b23,f2.b30,f2.b29,f2.b28,f2.b27,f2.b26,f2.b25,f2.b24,f2.b23]
						or
						(	
							(f2.b30=f1.b30 and f2.b29=f1.b29 and f2.b28=f1.b28 and f2.b27=f1.b27 and f2.b26=f1.b26 and f2.b25=f1.b25 and f2.b24=f1.b24 and f2.b23=f1.b23) and 
								booleanwiseGT24[	true,f1.b22,f1.b21,f1.b20,f1.b19,f1.b18,f1.b17,f1.b16,f1.b15,f1.b14,f1.b13,f1.b12,f1.b11,f1.b10,f1.b09,f1.b08,f1.b07,f1.b06,f1.b05,f1.b04,f1.b03,f1.b02,f1.b01,f1.b00,
														true,f2.b22,f2.b21,f2.b20,f2.b19,f2.b18,f2.b17,f2.b16,f2.b15,f2.b14,f2.b13,f2.b12,f2.b11,f2.b10,f2.b09,f2.b08,f2.b07,f2.b06,f2.b05,f2.b04,f2.b03,f2.b02,f2.b01,f2.b00]
						)
					)
				)
				else
				(
					f1.b31=false
					or
					booleanwiseGT8[f2.b30,f2.b29,f2.b28,f2.b27,f2.b26,f2.b25,f2.b24,f2.b23,f1.b30,f1.b29,f1.b28,f1.b27,f1.b26,f1.b25,f1.b24,f1.b23]
					or
					(
						(f2.b30=f1.b30 and f2.b29=f1.b29 and f2.b28=f1.b28 and f2.b27=f1.b27 and f2.b26=f1.b26 and f2.b25=f1.b25 and f2.b24=f1.b24 and f2.b23=f1.b23) and 
								booleanwiseGT24[	true,f2.b22,f2.b21,f2.b20,f2.b19,f2.b18,f2.b17,f2.b16,f2.b15,f2.b14,f2.b13,f2.b12,f2.b11,f2.b10,f2.b09,f2.b08,f2.b07,f2.b06,f2.b05,f2.b04,f2.b03,f2.b02,f2.b01,f2.b00,
														true,f1.b22,f1.b21,f1.b20,f1.b19,f1.b18,f1.b17,f1.b16,f1.b15,f1.b14,f1.b13,f1.b12,f1.b11,f1.b10,f1.b09,f1.b08,f1.b07,f1.b06,f1.b05,f1.b04,f1.b03,f1.b02,f1.b01,f1.b00]
					)
				)
			)
	)
}

pred pred_java_primitive_float_value_gte[f1, f2 : JavaPrimitiveFloatValue]{
	pred_java_primitive_float_value_gt[f1,f2] or pred_java_primitive_float_value_eq[f1,f2]
}


pred pred_java_primitive_float_value_lt[f1,f2 : JavaPrimitiveFloatValue]{
	pred_java_primitive_float_value_gt[f2,f1]
}

pred pred_java_primitive_float_value_lte[f1, f2 : JavaPrimitiveFloatValue]{
	pred_java_primitive_float_value_lt[f1,f2] or  pred_java_primitive_float_value_eq[f1,f2]
}

pred pred_java_primitive_float_value_is_infinite[f : JavaPrimitiveFloatValue]{
	isBitInfinity[f.b31,f.b30,f.b29,f.b28,f.b27,f.b26,f.b25,f.b24,f.b23,f.b22,f.b21,f.b20,f.b19,f.b18,f.b17,f.b16,f.b15,f.b14,f.b13,f.b12,f.b11,f.b10,f.b09,f.b08,f.b07,f.b06,f.b05,f.b04,f.b03,f.b02,f.b01,f.b00]
}

pred pred_java_primitive_float_value_is_NaN[f : JavaPrimitiveFloatValue]{
	isBitNaN[f.b31,f.b30,f.b29,f.b28,f.b27,f.b26,f.b25,f.b24,f.b23,f.b22,f.b21,f.b20,f.b19,f.b18,f.b17,f.b16,f.b15,f.b14,f.b13,f.b12,f.b11,f.b10,f.b09,f.b08,f.b07,f.b06,f.b05,f.b04,f.b03,f.b02,f.b01,f.b00]
}



pred pred_java_primitive_float_value_add[n1,n2,r : JavaPrimitiveFloatValue, compatibility_argument: boolean]{
		floatAdd[	n1.b31,n1.b30,n1.b29,n1.b28,n1.b27,n1.b26,n1.b25,n1.b24,n1.b23,n1.b22,n1.b21,n1.b20,n1.b19,n1.b18,n1.b17,n1.b16,n1.b15,n1.b14,n1.b13,n1.b12,n1.b11,n1.b10,n1.b09,n1.b08,n1.b07,n1.b06,n1.b05,n1.b04,n1.b03,n1.b02,n1.b01,n1.b00, 
						n2.b31,n2.b30,n2.b29,n2.b28,n2.b27,n2.b26,n2.b25,n2.b24,n2.b23,n2.b22,n2.b21,n2.b20,n2.b19,n2.b18,n2.b17,n2.b16,n2.b15,n2.b14,n2.b13,n2.b12,n2.b11,n2.b10,n2.b09,n2.b08,n2.b07,n2.b06,n2.b05,n2.b04,n2.b03,n2.b02,n2.b01,n2.b00, 
						r.b31,r.b30,r.b29,r.b28,r.b27,r.b26,r.b25,r.b24,r.b23,r.b22,r.b21,r.b20,r.b19,r.b18,r.b17,r.b16,r.b15,r.b14,r.b13,r.b12,r.b11,r.b10,r.b09,r.b08,r.b07,r.b06,r.b05,r.b04,r.b03,r.b02,r.b01,r.b00]
}


pred pred_java_primitive_float_value_sub[f1, f2, r : JavaPrimitiveFloatValue, compatibility_argument: boolean]{
	floatAdd[	f1.b31,f1.b30,f1.b29,f1.b28,f1.b27,f1.b26,f1.b25,f1.b24,f1.b23,f1.b22,f1.b21,f1.b20,f1.b19,f1.b18,f1.b17,f1.b16,f1.b15,f1.b14,f1.b13,f1.b12,f1.b11,f1.b10,f1.b09,f1.b08,f1.b07,f1.b06,f1.b05,f1.b04,f1.b03,f1.b02,f1.b01,f1.b00, 
	Not[f2.b31],f2.b30,f2.b29,f2.b28,f2.b27,f2.b26,f2.b25,f2.b24,f2.b23,f2.b22,f2.b21,f2.b20,f2.b19,f2.b18,f2.b17,f2.b16,f2.b15,f2.b14,f2.b13,f2.b12,f2.b11,f2.b10,f2.b09,f2.b08,f2.b07,f2.b06,f2.b05,f2.b04,f2.b03,f2.b02,f2.b01,f2.b00, 
	r.b31,r.b30,r.b29,r.b28,r.b27,r.b26,r.b25,r.b24,r.b23,r.b22,r.b21,r.b20,r.b19,r.b18,r.b17,r.b16,r.b15,r.b14,r.b13,r.b12,r.b11,r.b10,r.b09,r.b08,r.b07,r.b06,r.b05,r.b04,r.b03,r.b02,r.b01,r.b00]
}


pred pred_java_primitive_float_value_mul[f1, f2, r : JavaPrimitiveFloatValue, compatibility_arg: boolean]{
	some o1,m107,m106,m105,m104,m103,m102,m101,m100 : boolean,
			o2,m207,m206,m205,m204,m203,m202,m201,m200 : boolean,
			o3,m307,m306,m305,m304,m303,m302,m301,m300 : boolean,
			i27,i26,i25,i24,i23,i22,i21,i20,i19,i18,i17,i16,i15,i14,i13,i12,i11,i10,i09,i08,i07,i06,i05,i04,i03,i02,i01,i00 : boolean,
			overflowAfterRound,u23,u22,u21,u20,u19,u18,u17,u16,u15,u14,u13,u12,u11,u10,u09,u08,u07,u06,u05,u04,u03,u02,u01,u00 : boolean |
		(isNormalized[f1] and isNormalized[f2]) implies
		(
			r.b31 = Xor[f1.b31, f2.b31]
			and
			booleanwiseAdd8[f1.b30, f1.b29, f1.b28, f1.b27, f1.b26, f1.b25, f1.b24, f1.b23,
								f2.b30, f2.b29, f2.b28, f2.b27, f2.b26, f2.b25, f2.b24, f2.b23,
								o1, m107, m106, m105, m104, m103, m102, m101, m100]
			and

			booleanwise9bitsSub127[o1, m107, m106, m105, m104, m103, m102, m101, m100,
											o2,m207,m206,m205,m204,m203,m202,m201,m200]
			and

			(
				(o2 = true or (m207=true and m206=true and m205=true and m204=true and m203=true and m202=true and m201=true and m200=true)) 
				implies -- Set to infinity
					(
						r.b30=true and r.b29=true and r.b28=true and r.b27=true and r.b26=true and r.b25=true and r.b24=true and r.b23=true and
						r.b22=false and r.b21=false and r.b20=false and r.b19=false and r.b18=false and r.b17=false and r.b16=false and r.b15=false and
						r.b14=false and r.b13=false and r.b12=false and r.b11=false and r.b10=false and r.b09=false and r.b08=false and r.b07=false and
						r.b06=false and r.b05=false and r.b04=false and r.b03=false and r.b02=false and r.b01=false and r.b00=false
					)
			)
			and -- Occurrences of true in the invocation to booleanwiseMantissaMul show the use of normalized floats.
			booleanwiseMantissaMul[
												true,f1.b22,f1.b21,f1.b20,f1.b19,f1.b18,f1.b17,f1.b16,f1.b15,f1.b14,f1.b13,f1.b12,f1.b11,f1.b10,f1.b09,f1.b08,f1.b07,f1.b06,f1.b05,f1.b04,f1.b03,f1.b02,f1.b01,f1.b00,
												true,f2.b22,f2.b21,f2.b20,f2.b19,f2.b18,f2.b17,f2.b16,f2.b15,f2.b14,f2.b13,f2.b12,f2.b11,f2.b10,f2.b09,f2.b08,f2.b07,f2.b06,f2.b05,f2.b04,f2.b03,f2.b02,f2.b01,f2.b00,
												i27,i26,i25,i24,i23,i22,i21,i20,i19,i18,i17,i16,i15,i14,i13,i12,i11,i10,i09,i08,i07,i06,i05,i04,i03,i02,i01,i00]

			and
			(
				i27 = true 
					implies
						(
							addOne8bits[m207,m206,m205,m204,m203,m202,m201,m200,
											o3,m307,m306,m305,m304,m303,m302,m301,m300]
							and
							(
								(o3 = true or (m307=true and m306=true and m305=true and m304=true and m303=true and m302=true and m301=true and m300=true)) 
								implies 
									(
										r.b30=true and r.b29=true and r.b28=true and r.b27=true and r.b26=true and r.b25=true and r.b24=true and r.b23=true and
										r.b22=false and r.b21=false and r.b20=false and r.b19=false and r.b18=false and r.b17=false and r.b16=false and r.b15=false and
										r.b14=false and r.b13=false and r.b12=false and r.b11=false and r.b10=false and r.b09=false and r.b08=false and r.b07=false and
										r.b06=false and r.b05=false and r.b04=false and r.b03=false and r.b02=false and r.b01=false and r.b00=false
									)
								else
									(
--										shift32OnePositionAndRoundUp[i26,i25,i24,i23,i22,i21,i20,i19,i18,i17,i16,i15,i14,i13,i12,i11,i10,i09,i08,i07,i06,i05,i04,i03,i02,i01,i00,
--																							overflowAfterRound,u23,u22,u21,u20,u19,u18,u17,u16,u15,u14,u13,u12,u11,u10,u09,u08,u07,u06,u05,u04,u03,u02,u01,u00]
										shift32OnePositionAndRoundUp[i27,i26,i25,i24,i23,i22,i21,i20,i19,i18,i17,i16,i15,i14,i13,i12,i11,i10,i09,i08,i07,i06,i05,i04,i03,i02,Or[i01,i00],
																							overflowAfterRound,u23,u22,u21,u20,u19,u18,u17,u16,u15,u14,u13,u12,u11,u10,u09,u08,u07,u06,u05,u04,u03,u02,u01,u00]
												
				and
										(
											overflowAfterRound = true implies
											(
												addOne8bits[m307,m306,m305,m304,m303,m302,m301,m300,false,r.b30,r.b29,r.b28,r.b27,r.b26,r.b25,r.b24,r.b23] 
												and
									 			(
													(r.b30=true and r.b29=true and r.b28=true and r.b27=true and r.b26=true and r.b25=true and r.b24=true and r.b23=true) implies
														(	r.b22=false and r.b21=false and r.b20=false and r.b19=false and r.b18=false and r.b17=false and r.b16=false and r.b15=false and
															r.b14=false and r.b13=false and r.b12=false and r.b11=false and r.b10=false and r.b09=false and r.b08=false and r.b07=false and
															r.b06=false and r.b05=false and r.b04=false and r.b03=false and r.b02=false and r.b01=false and r.b00=false
														)
													else
														(	r.b22 = u23 and r.b21 = u22 and r.b20 = u21 and r.b19 = u20 and r.b18 = u19 and r.b17 = u18 and r.b16 = u17 and r.b15 = u16 and r.b14 = u15 and 
															r.b13 = u14 and r.b12 = u13 and r.b11 = u12 and r.b10 = u11 and r.b09 = u10 and r.b08 = u09 and r.b07 = u08 and r.b06 = u07 and r.b05 = u06 and 
															r.b04 = u05 and r.b03 = u04 and r.b02 = u03 and r.b01 = u02 and r.b00 = u01 
														)
												)
											)
											else
											(
												r.b30 = m307 and r.b29 = m306 and r.b28 = m305 and r.b27 = m304 and r.b26 = m303 and r.b25 = m302 and r.b24 = m301 and r.b23 = m300 and
												r.b22 = u22 and r.b21 = u21 and r.b20 = u20 and r.b19 = u19 and r.b18 = u18 and r.b17 = u17 and r.b16 = u16 and r.b15 = u15 and 
												r.b14 = u14 and r.b13 = u13 and r.b12 = u12 and r.b11 = u11 and r.b10 = u10 and r.b09 = u09 and r.b08 = u08 and r.b07 = u07 and
												r.b06 = u06 and r.b05 = u05 and r.b04 = u04 and r.b03 = u03 and r.b02 = u02 and r.b01 = u01 and r.b00 = u00
											)
											
										)
									)
							)	
						)
						else
						(
							roundsUp[i03,i02,i01,i00] implies
							(
								booleanwiseAddOne24bits[i26,i25,i24,i23,i22,i21,i20,i19,i18,i17,i16,i15,i14,i13,i12,i11,i10,i09,i08,i07,i06,i05,i04,i03,
																	overflowAfterRound,u23,u22,u21,u20,u19,u18,u17,u16,u15,u14,u13,u12,u11,u10,u09,u08,u07,u06,u05,u04,u03,u02,u01,u00]
								and
								(
									overflowAfterRound = true implies
									(
										addOne8bits[m207,m206,m205,m204,m203,m202,m201,m200,false,r.b30,r.b29,r.b28,r.b27,r.b26,r.b25,r.b24,r.b23] 
										and
									 	(
											(r.b30=true and r.b29=true and r.b28=true and r.b27=true and r.b26=true and r.b25=true and r.b24=true and r.b23=true) implies
											(	
												r.b22 = false and r.b21 = false and r.b20 = false and r.b19 = false and r.b18 = false and r.b17 = false and r.b16 = false and r.b15 = false and r.b14 = false and 
												r.b13 = false and r.b12 = false and r.b11 = false and r.b10 = false and r.b09 = false and r.b08 = false and r.b07 = false and r.b06 = false and r.b05 = false and 
												r.b04 = false and r.b03 = false and r.b02 = false and r.b01 = false and r.b00 = false 
											)
											else
											(
												r.b22 = u22 and r.b21 = u21 and r.b20 = u20 and r.b19 = u19 and r.b18 = u18 and r.b17 = u17 and r.b16 = u16 and r.b15 = u15 and r.b14 = u14 and 
												r.b13 = u13 and r.b12 = u12 and r.b11 = u11 and r.b10 = u10 and r.b09 = u09 and r.b08 = u08 and r.b07 = u07 and r.b06 = u06 and r.b05 = u05 and 
												r.b04 = u04 and r.b03 = u03 and r.b02 = u02 and r.b01 = u01 and r.b00 = u00 
											)
										)
									)
									else
									(
												r.b30 = m207 and r.b29 = m206 and r.b28 = m205 and r.b27 = m204 and r.b26 = m203 and r.b25 = m202 and r.b24 = m201 and r.b23 = m200 and
												r.b22 = u22 and r.b21 = u21 and r.b20 = u20 and r.b19 = u19 and r.b18 = u18 and r.b17 = u17 and r.b16 = u16 and r.b15 = u15 and r.b14 = u14 and 
												r.b13 = u13 and r.b12 = u12 and r.b11 = u11 and r.b10 = u10 and r.b09 = u09 and r.b08 = u08 and r.b07 = u07 and r.b06 = u06 and r.b05 = u05 and 
												r.b04 = u04 and r.b03 = u03 and r.b02 = u02 and r.b01 = u01 and r.b00 = u00 
									)
								)
							)
							else
							(
								r.b30 = m207 and r.b29 = m206 and r.b28 = m205 and r.b27 = m204 and r.b26 = m203 and r.b25 = m202 and r.b24 = m201 and r.b23 = m200 and
								r.b22 = i25 and r.b21 = i24 and r.b20 = i23 and r.b19 = i22 and r.b18 = i21 and r.b17 = i20 and r.b16 = i19 and r.b15 = i18 and r.b14 = i17 and 
								r.b13 = i16 and r.b12 = i15 and r.b11 = i14 and r.b10 = i13 and r.b09 = i12 and r.b08 = i11 and r.b07 = i10 and r.b06 = i09 and r.b05 = i08 and 
								r.b04 = i07 and r.b03 = i06 and r.b02 = i05 and r.b01 = i04 and r.b00 = i03 
							)
						)
			)
		)
		else
		(
			isNormalized[f1]
			implies
			(
				isZero[f2]
				implies
					(r.b31 = Xor[f1.b31,f2.b31] and isZero[r])
				else
				(
					pred_java_primitive_float_value_is_infinite[f2]
					implies
						(r.b31 = Xor[f1.b31,f2.b31] and pred_java_primitive_float_value_is_infinite[r])
					else
					(
						pred_java_primitive_float_value_is_NaN[f2]
						implies
							pred_java_primitive_float_value_is_NaN[r]
					)
				)				
			)
			else
				isZero[f1]
				implies
				(
					isNormalized[f2]
					implies
						(r.b31 = Xor[f1.b31,f2.b31] and isZero[r])
					else
					(
						isZero[f2]
						implies
							(r.b31 = Xor[f1.b31,f2.b31] and isZero[r])
						else
						(
							pred_java_primitive_float_value_is_infinite[f2]
							implies
								pred_java_primitive_float_value_is_NaN[r]
							else
							(
								pred_java_primitive_float_value_is_NaN[f2]
								implies
									pred_java_primitive_float_value_is_NaN[r]
							)
						)
					)
				)
				else
					pred_java_primitive_float_value_is_infinite[f1]
					implies
					(
						isNormalized[f2]
						implies
							(r.b31 = Xor[f1.b31,f2.b31] and pred_java_primitive_float_value_is_infinite[r])
						else
						(
							isZero[f2]
							implies
								pred_java_primitive_float_value_is_NaN[r]
							else
							(
								pred_java_primitive_float_value_is_infinite[f2]
								implies
									(r.b31 = Xor[f1.b31,f2.b31] and pred_java_primitive_float_value_is_infinite[r])
								else
								(
									pred_java_primitive_float_value_is_NaN[f2]
									implies
										pred_java_primitive_float_value_is_NaN[r]
								)
							)
						)
					)
					else
						pred_java_primitive_float_value_is_NaN[f1]
						implies
							pred_java_primitive_float_value_is_NaN[r]
						
		)
		
}


pred pred_java_primitive_float_value_div[f1, f2, r , compatibility_arg : JavaPrimitiveFloatValue]{ 
	some o1,m107,m106,m105,m104,m103,m102,m101,m100 : boolean,
			o2,m207,m206,m205,m204,m203,m202,m201,m200 : boolean,
			o3,m307,m306,m305,m304,m303,m302,m301,m300 : boolean,
			i27,i26,i25,i24,i23,i22,i21,i20,i19,i18,i17,i16,i15,i14,i13,i12,i11,i10,i09,i08,i07,i06,i05,i04,i03,i02,i01,i00 : boolean,
			overflowAfterRound,u23,u22,u21,u20,u19,u18,u17,u16,u15,u14,u13,u12,u11,u10,u09,u08,u07,u06,u05,u04,u03,u02,u01,u00 : boolean |
		(isNormalized[f1] and isNormalized[f2]) implies
		(
			f1.b31 = Xor[f2.b31, r.b31]
			and
			booleanwiseAdd8[f2.b30, f2.b29, f2.b28, f2.b27, f2.b26, f2.b25, f2.b24, f2.b23,
								r.b30, r.b29, r.b28, r.b27, r.b26, r.b25, r.b24, r.b23,
								o1, m107, m106, m105, m104, m103, m102, m101, m100]
			and

			booleanwise9bitsSub127[o1, m107, m106, m105, m104, m103, m102, m101, m100,
											o2,m207,m206,m205,m204,m203,m202,m201,m200]
			and

			(
				(o2 = true or (m207=true and m206=true and m205=true and m204=true and m203=true and m202=true and m201=true and m200=true)) 
				implies -- Set to infinity
					(
						f1.b30=true and f1.b29=true and f1.b28=true and f1.b27=true and f1.b26=true and f1.b25=true and f1.b24=true and f1.b23=true and
						f1.b22=false and f1.b21=false and f1.b20=false and f1.b19=false and f1.b18=false and f1.b17=false and f1.b16=false and f1.b15=false and
						f1.b14=false and f1.b13=false and f1.b12=false and f1.b11=false and f1.b10=false and f1.b09=false and f1.b08=false and f1.b07=false and
						f1.b06=false and f1.b05=false and f1.b04=false and f1.b03=false and f1.b02=false and f1.b01=false and f1.b00=false
					)
			)
			and -- Occurrences of true in the invocation to booleanwiseMantissaMul show the use of normalized floats.
			booleanwiseMantissaMul[
												true,f2.b22,f2.b21,f2.b20,f2.b19,f2.b18,f2.b17,f2.b16,f2.b15,f2.b14,f2.b13,f2.b12,f2.b11,f2.b10,f2.b09,f2.b08,f2.b07,f2.b06,f2.b05,f2.b04,f2.b03,f2.b02,f2.b01,f2.b00,
												true,r.b22,r.b21,r.b20,r.b19,r.b18,r.b17,r.b16,r.b15,r.b14,r.b13,r.b12,r.b11,r.b10,r.b09,r.b08,r.b07,r.b06,r.b05,r.b04,r.b03,r.b02,r.b01,r.b00,
												i27,i26,i25,i24,i23,i22,i21,i20,i19,i18,i17,i16,i15,i14,i13,i12,i11,i10,i09,i08,i07,i06,i05,i04,i03,i02,i01,i00]

			and
			(
				i27 = true 
					implies
						(
							addOne8bits[m207,m206,m205,m204,m203,m202,m201,m200,
											o3,m307,m306,m305,m304,m303,m302,m301,m300]
							and
							(
								(o3 = true or (m307=true and m306=true and m305=true and m304=true and m303=true and m302=true and m301=true and m300=true)) 
								implies 
									(
										f1.b30=true and f1.b29=true and f1.b28=true and f1.b27=true and f1.b26=true and f1.b25=true and f1.b24=true and f1.b23=true and
										f1.b22=false and f1.b21=false and f1.b20=false and f1.b19=false and f1.b18=false and f1.b17=false and f1.b16=false and f1.b15=false and
										f1.b14=false and f1.b13=false and f1.b12=false and f1.b11=false and f1.b10=false and f1.b09=false and f1.b08=false and f1.b07=false and
										f1.b06=false and f1.b05=false and f1.b04=false and f1.b03=false and f1.b02=false and f1.b01=false and f1.b00=false
									)
								else
									(
										shift32OnePositionAndRoundUp[i27,i26,i25,i24,i23,i22,i21,i20,i19,i18,i17,i16,i15,i14,i13,i12,i11,i10,i09,i08,i07,i06,i05,i04,i03,i02,Or[i01,i00],
																							overflowAfterRound,u23,u22,u21,u20,u19,u18,u17,u16,u15,u14,u13,u12,u11,u10,u09,u08,u07,u06,u05,u04,u03,u02,u01,u00]
												
				and
										(
											overflowAfterRound = true implies
											(
												addOne8bits[m307,m306,m305,m304,m303,m302,m301,m300,false,f1.b30,f1.b29,f1.b28,f1.b27,f1.b26,f1.b25,f1.b24,f1.b23] 
												and
									 			(
													(f1.b30=true and f1.b29=true and f1.b28=true and f1.b27=true and f1.b26=true and f1.b25=true and f1.b24=true and f1.b23=true) implies
														(	f1.b22=false and f1.b21=false and f1.b20=false and f1.b19=false and f1.b18=false and f1.b17=false and f1.b16=false and f1.b15=false and
															f1.b14=false and f1.b13=false and f1.b12=false and f1.b11=false and f1.b10=false and f1.b09=false and f1.b08=false and f1.b07=false and
															f1.b06=false and f1.b05=false and f1.b04=false and f1.b03=false and f1.b02=false and f1.b01=false and f1.b00=false
														)
													else
														(	f1.b22 = u23 and f1.b21 = u22 and f1.b20 = u21 and f1.b19 = u20 and f1.b18 = u19 and f1.b17 = u18 and f1.b16 = u17 and f1.b15 = u16 and f1.b14 = u15 and 
															f1.b13 = u14 and f1.b12 = u13 and f1.b11 = u12 and f1.b10 = u11 and f1.b09 = u10 and f1.b08 = u09 and f1.b07 = u08 and f1.b06 = u07 and f1.b05 = u06 and 
															f1.b04 = u05 and f1.b03 = u04 and f1.b02 = u03 and f1.b01 = u02 and f1.b00 = u01 
														)
												)
											)
											else
											(
												f1.b30 = m307 and f1.b29 = m306 and f1.b28 = m305 and f1.b27 = m304 and f1.b26 = m303 and f1.b25 = m302 and f1.b24 = m301 and f1.b23 = m300 and
												f1.b22 = u22 and f1.b21 = u21 and f1.b20 = u20 and f1.b19 = u19 and f1.b18 = u18 and f1.b17 = u17 and f1.b16 = u16 and f1.b15 = u15 and 
												f1.b14 = u14 and f1.b13 = u13 and f1.b12 = u12 and f1.b11 = u11 and f1.b10 = u10 and f1.b09 = u09 and f1.b08 = u08 and f1.b07 = u07 and
												f1.b06 = u06 and f1.b05 = u05 and f1.b04 = u04 and f1.b03 = u03 and f1.b02 = u02 and f1.b01 = u01 and f1.b00 = u00
											)
											
										)
									)
							)	
						)
						else
						(
							roundsUp[i03,i02,i01,i00] implies
							(
								booleanwiseAddOne24bits[i26,i25,i24,i23,i22,i21,i20,i19,i18,i17,i16,i15,i14,i13,i12,i11,i10,i09,i08,i07,i06,i05,i04,i03,
																	overflowAfterRound,u23,u22,u21,u20,u19,u18,u17,u16,u15,u14,u13,u12,u11,u10,u09,u08,u07,u06,u05,u04,u03,u02,u01,u00]
								and
								(
									overflowAfterRound = true implies
									(
										addOne8bits[m207,m206,m205,m204,m203,m202,m201,m200,false,f1.b30,f1.b29,f1.b28,f1.b27,f1.b26,f1.b25,f1.b24,f1.b23] 
										and
									 	(
											(f1.b30=true and f1.b29=true and f1.b28=true and f1.b27=true and f1.b26=true and f1.b25=true and f1.b24=true and f1.b23=true) implies
											(	
												f1.b22 = false and f1.b21 = false and f1.b20 = false and f1.b19 = false and f1.b18 = false and f1.b17 = false and f1.b16 = false and f1.b15 = false and f1.b14 = false and 
												f1.b13 = false and f1.b12 = false and f1.b11 = false and f1.b10 = false and f1.b09 = false and f1.b08 = false and f1.b07 = false and f1.b06 = false and f1.b05 = false and 
												f1.b04 = false and f1.b03 = false and f1.b02 = false and f1.b01 = false and f1.b00 = false 
											)
											else
											(
												f1.b22 = u22 and f1.b21 = u21 and f1.b20 = u20 and f1.b19 = u19 and f1.b18 = u18 and f1.b17 = u17 and f1.b16 = u16 and f1.b15 = u15 and f1.b14 = u14 and 
												f1.b13 = u13 and f1.b12 = u12 and f1.b11 = u11 and f1.b10 = u10 and f1.b09 = u09 and f1.b08 = u08 and f1.b07 = u07 and f1.b06 = u06 and f1.b05 = u05 and 
												f1.b04 = u04 and f1.b03 = u03 and f1.b02 = u02 and f1.b01 = u01 and f1.b00 = u00 
											)
										)
									)
									else
									(
												f1.b30 = m207 and f1.b29 = m206 and f1.b28 = m205 and f1.b27 = m204 and f1.b26 = m203 and f1.b25 = m202 and f1.b24 = m201 and f1.b23 = m200 and
												f1.b22 = u22 and f1.b21 = u21 and f1.b20 = u20 and f1.b19 = u19 and f1.b18 = u18 and f1.b17 = u17 and f1.b16 = u16 and f1.b15 = u15 and f1.b14 = u14 and 
												f1.b13 = u13 and f1.b12 = u12 and f1.b11 = u11 and f1.b10 = u10 and f1.b09 = u09 and f1.b08 = u08 and f1.b07 = u07 and f1.b06 = u06 and f1.b05 = u05 and 
												f1.b04 = u04 and f1.b03 = u03 and f1.b02 = u02 and f1.b01 = u01 and f1.b00 = u00 
									)
								)
							)
							else
							(
								f1.b30 = m207 and f1.b29 = m206 and f1.b28 = m205 and f1.b27 = m204 and f1.b26 = m203 and f1.b25 = m202 and f1.b24 = m201 and f1.b23 = m200 and
								f1.b22 = i25 and f1.b21 = i24 and f1.b20 = i23 and f1.b19 = i22 and f1.b18 = i21 and f1.b17 = i20 and f1.b16 = i19 and f1.b15 = i18 and f1.b14 = i17 and 
								f1.b13 = i16 and f1.b12 = i15 and f1.b11 = i14 and f1.b10 = i13 and f1.b09 = i12 and f1.b08 = i11 and f1.b07 = i10 and f1.b06 = i09 and f1.b05 = i08 and 
								f1.b04 = i07 and f1.b03 = i06 and f1.b02 = i05 and f1.b01 = i04 and f1.b00 = i03 
							)
						)
			)
		)
		else
		(
			isNormalized[f1]
			implies
			(
				isZero[f2]
				implies
					(r.b31 = Xor[f1.b31,f2.b31] and pred_java_primitive_float_value_is_infinite[r])
				else
				(
					pred_java_primitive_float_value_is_infinite[f2]
					implies
						(r.b31 = Xor[f1.b31,f2.b31] and isZero[r])
					else
					(
						pred_java_primitive_float_value_is_NaN[f2]
						implies
							pred_java_primitive_float_value_is_NaN[r]
					)
				)				
			)
			else
				isZero[f1]
				implies
				(
					isNormalized[f2]
					implies
						(r.b31 = Xor[f1.b31,f2.b31] and isZero[r])
					else
					(
						isZero[f2]
						implies
							(pred_java_primitive_float_value_is_NaN[r])
						else
						(
							pred_java_primitive_float_value_is_infinite[f2]
							implies
								(r.b31 = Xor[f1.b31,f2.b31] and isZero[r])
							else
							(
								pred_java_primitive_float_value_is_NaN[f2]
								implies
									pred_java_primitive_float_value_is_NaN[r]
							)
						)
					)
				)
				else
					pred_java_primitive_float_value_is_infinite[f1]
					implies
					(
						isNormalized[f2]
						implies
							(r.b31 = Xor[f1.b31,f2.b31] and pred_java_primitive_float_value_is_infinite[r])
						else
						(
							isZero[f2]
							implies
								(r.b31 = Xor[f1.b31,f2.b31] and pred_java_primitive_float_value_is_infinite[r])
							else
							(
								pred_java_primitive_float_value_is_infinite[f2]
								implies
									pred_java_primitive_float_value_is_NaN[r]
								else
								(
									pred_java_primitive_float_value_is_NaN[f2]
									implies
										pred_java_primitive_float_value_is_NaN[r]
								)
							)
						)
					)
					else
						pred_java_primitive_float_value_is_NaN[f1]
						implies
							pred_java_primitive_float_value_is_NaN[r]
						
		)
		
}



pred floatAdd[
	s31,s30,s29,s28,s27,s26,s25,s24,s23,s22,s21,s20,s19,s18,s17,s16,s15,s14,s13,s12,s11,s10,s09,s08,s07,s06,s05,s04,s03,s02,s01,s00 : boolean, 
	t31,t30,t29,t28,t27,t26,t25,t24,t23,t22,t21,t20,t19,t18,t17,t16,t15,t14,t13,t12,t11,t10,t09,t08,t07,t06,t05,t04,t03,t02,t01,t00 : boolean, 
	r31,r30,r29,r28,r27,r26,r25,r24,r23,r22,r21,r20,r19,r18,r17,r16,r15,r14,r13,r12,r11,r10,r09,r08,r07,r06,r05,r04,r03,r02,r01,r00 : boolean 
]
{  

some 	i30, i29, i28, i27, i26, i25, i24, i23 : boolean,
			v26,v25,v24,v23,v22,v21,v20,v19,v18,v17,v16,v15,v14,v13,v12,v11,v10,v09,v08,v07,v06,v05,v04,v03,v02,v01,v00 : boolean,
			overflowAfterRound, ir27,ir26,ir25,ir24,ir23,ir22,ir21,ir20,ir19,ir18,ir17,ir16,ir15,ir14,ir13,ir12,ir11,ir10,ir09,ir08,ir07,ir06,ir05,ir04,ir03,ir02,ir01,ir00 : boolean ,
			flagGT : boolean,
			u23,u22,u21,u20,u19,u18,u17,u16,u15,u14,u13,u12,u11,u10,u09,u08,u07,u06,u05,u04,u03,u02,u01,u00 : boolean,
			e30,e29,e28,e27,e26,e25,e24,e23 : boolean,
			f30, f29, f28, f27, f26, f25, f24, f23 : boolean | 

			(isBitNormalized[s31,s30,s29,s28,s27,s26,s25,s24,s23,s22,s21,s20,s19,s18,s17,s16,s15,s14,s13,s12,s11,s10,s09,s08,s07,s06,s05,s04,s03,s02,s01,s00] and isBitNormalized[t31,t30,t29,t28,t27,t26,t25,t24,t23,t22,t21,t20,t19,t18,t17,t16,t15,t14,t13,t12,t11,t10,t09,t08,t07,t06,t05,t04,t03,t02,t01,t00]) implies
			(
				(
					booleanwiseGT8[s30,s29,s28,s27,s26,s25,s24,s23,t30,t29,t28,t27,t26,t25,t24,t23] implies 
					(		
							booleanwiseSub8[s30,s29,s28,s27,s26,s25,s24,s23,
												t30,t29,t28,t27,t26,t25,t24,t23,
												i30,i29,i28,i27,i26,i25,i24,i23]
						 	and
							shift32[	true,t22,t21,t20,t19,t18,t17,t16,t15,t14,t13,t12,t11,t10,t09,t08,t07,t06,t05,t04,t03,t02,t01,t00,false,false,false,
										i30,i29,i28,i27,i26,i25,i24,i23,
										v26,v25,v24,v23,v22,v21,v20,v19,v18,v17,v16,v15,v14,v13,v12,v11,v10,v09,v08,v07,v06,v05,v04,v03,v02,v01,v00]
							and
							flagGT = true
					)
					else
					(
							booleanwiseSub8[t30,t29,t28,t27,t26,t25,t24,t23,
												s30,s29,s28,s27,s26,s25,s24,s23,
								 				i30,i29,i28,i27,i26,i25,i24,i23]
						 	and
							shift32[	true,s22,s21,s20,s19,s18,s17,s16,s15,s14,s13,s12,s11,s10,s09,s08,s07,s06,s05,s04,s03,s02,s01,s00,false,false,false,
										i30,i29,i28,i27,i26,i25,i24,i23,
										v26,v25,v24,v23,v22,v21,v20,v19,v18,v17,v16,v15,v14,v13,v12,v11,v10,v09,v08,v07,v06,v05,v04,v03,v02,v01,v00]
							and
							flagGT = false
					)
				)
				and
				(
						s31 = t31 implies 
						(
							(flagGT = true implies
           			   		booleanwiseAdd27[	v26,v25,v24,v23,v22,v21,v20,v19,v18,v17,v16,v15,v14,v13,v12,v11,v10,v09,v08,v07,v06,v05,v04,v03,v02,v01,v00,
													true,s22,s21,s20,s19,s18,s17,s16,s15,s14,s13,s12,s11,s10,s09,s08,s07,s06,s05,s04,s03,s02,s01,s00,false,false,false,
													ir27,ir26,ir25,ir24,ir23,ir22,ir21,ir20,ir19,ir18,ir17,ir16,ir15,ir14,ir13,ir12,ir11,ir10,ir09,ir08,ir07,ir06,ir05,ir04,ir03,ir02,ir01,ir00] 
							else
           			   		booleanwiseAdd27[	v26,v25,v24,v23,v22,v21,v20,v19,v18,v17,v16,v15,v14,v13,v12,v11,v10,v09,v08,v07,v06,v05,v04,v03,v02,v01,v00,
													true,t22,t21,t20,t19,t18,t17,t16,t15,t14,t13,t12,t11,t10,t09,t08,t07,t06,t05,t04,t03,t02,t01,t00,false,false,false,
 													ir27,ir26,ir25,ir24,ir23,ir22,ir21,ir20,ir19,ir18,ir17,ir16,ir15,ir14,ir13,ir12,ir11,ir10,ir09,ir08,ir07,ir06,ir05,ir04,ir03,ir02,ir01,ir00]
							)
							and
							r31 = s31
						)
						else 
						(
							(flagGT = true implies
								(   
									booleanwiseGT27[v26,v25,v24,v23,v22,v21,v20,v19,v18,v17,v16,v15,v14,v13,v12,v11,v10,v09,v08,v07,v06,v05,v04,v03,v02,v01,v00,
														true,s22,s21,s20,s19,s18,s17,s16,s15,s14,s13,s12,s11,s10,s09,s08,s07,s06,s05,s04,s03,s02,s01,s00,false,false,false] implies 
									(
										booleanwiseSub27[	v26,v25,v24,v23,v22,v21,v20,v19,v18,v17,v16,v15,v14,v13,v12,v11,v10,v09,v08,v07,v06,v05,v04,v03,v02,v01,v00,
																	true,s22,s21,s20,s19,s18,s17,s16,s15,s14,s13,s12,s11,s10,s09,s08,s07,s06,s05,s04,s03,s02,s01,s00,false,false,false,
																	ir26,ir25,ir24,ir23,ir22,ir21,ir20,ir19,ir18,ir17,ir16,ir15,ir14,ir13,ir12,ir11,ir10,ir09,ir08,ir07,ir06,ir05,ir04,ir03,ir02,ir01,ir00] 
										and
										(
											r31 = true iff t31 = true
										)								
									)
									else
									(
										booleanwiseSub27[	true,s22,s21,s20,s19,s18,s17,s16,s15,s14,s13,s12,s11,s10,s09,s08,s07,s06,s05,s04,s03,s02,s01,s00,false,false,false,
																	v26,v25,v24,v23,v22,v21,v20,v19,v18,v17,v16,v15,v14,v13,v12,v11,v10,v09,v08,v07,v06,v05,v04,v03,v02,v01,v00,
																	ir26,ir25,ir24,ir23,ir22,ir21,ir20,ir19,ir18,ir17,ir16,ir15,ir14,ir13,ir12,ir11,ir10,ir09,ir08,ir07,ir06,ir05,ir04,ir03,ir02,ir01,ir00] 
										and	
										(
											r31 = true iff s31 = true
										)
									)
								)		
								else
								(
									booleanwiseGT27[	v26,v25,v24,v23,v22,v21,v20,v19,v18,v17,v16,v15,v14,v13,v12,v11,v10,v09,v08,v07,v06,v05,v04,v03,v02,v01,v00,
															true,t22,t21,t20,t19,t18,t17,t16,t15,t14,t13,t12,t11,t10,t09,t08,t07,t06,t05,t04,t03,t02,t01,t00,false,false,false] implies 
										(
											booleanwiseSub27[	v26,v25,v24,v23,v22,v21,v20,v19,v18,v17,v16,v15,v14,v13,v12,v11,v10,v09,v08,v07,v06,v05,v04,v03,v02,v01,v00,
																		true,t22,t21,t20,t19,t18,t17,t16,t15,t14,t13,t12,t11,t10,t09,t08,t07,t06,t05,t04,t03,t02,t01,t00,false,false,false,
																		ir26,ir25,ir24,ir23,ir22,ir21,ir20,ir19,ir18,ir17,ir16,ir15,ir14,ir13,ir12,ir11,ir10,ir09,ir08,ir07,ir06,ir05,ir04,ir03,ir02,ir01,ir00] 
											and
											(
												r31 = true iff s31 = true
											)
										)
										else
										(
											booleanwiseSub27[	true,t22,t21,t20,t19,t18,t17,t16,t15,t14,t13,t12,t11,t10,t09,t08,t07,t06,t05,t04,t03,t02,t01,t00,false,false,false,
																		v26,v25,v24,v23,v22,v21,v20,v19,v18,v17,v16,v15,v14,v13,v12,v11,v10,v09,v08,v07,v06,v05,v04,v03,v02,v01,v00,
																		ir26,ir25,ir24,ir23,ir22,ir21,ir20,ir19,ir18,ir17,ir16,ir15,ir14,ir13,ir12,ir11,ir10,ir09,ir08,ir07,ir06,ir05,ir04,ir03,ir02,ir01,ir00]
											and
											(
												r31 = true iff t31 = true
											)
										)
								)	
							)			
							and
							ir27 = false
						)
				)			
				and
				(
							ir27 = true implies 
							(		
									(	flagGT = true implies 
											addOne8bits[s30,s29,s28,s27,s26,s25,s24,s23,false,e30,e29,e28,e27,e26,e25,e24,e23] 
										else
											addOne8bits[t30,t29,t28,t27,t26,t25,t24,t23,false,e30,e29,e28,e27,e26,e25,e24,e23]
									)
									and
									(
										(e30=true and e29=true and e28=true and e27=true and e26=true and e25=true and e24=true and e23=true) implies
										(	r22 = false and r21 = false and r20 = false and r19 = false and r18 = false and r17 = false and r16 = false and r15 = false and r14 = false and 
											r13 = false and r12 = false and r11 = false and r10 = false and r09 = false and r08 = false and r07 = false and r06 = false and r05 = false and 
											r04 = false and r03 = false and r02 = false and r01 = false and r00 = false 
										)
										else
										(	
--											shift32OnePositionAndRoundUp[ir26,ir25,ir24,ir23,ir22,ir21,ir20,ir19,ir18,ir17,ir16,ir15,ir14,ir13,ir12,ir11,ir10,ir09,ir08,ir07,ir06,ir05,ir04,ir03,ir02,ir01,ir00,
--																								overflowAfterRound,u23,u22,u21,u20,u19,u18,u17,u16,u15,u14,u13,u12,u11,u10,u09,u08,u07,u06,u05,u04,u03,u02,u01,u00]
											shift32OnePositionAndRoundUp[ir27,ir26,ir25,ir24,ir23,ir22,ir21,ir20,ir19,ir18,ir17,ir16,ir15,ir14,ir13,ir12,ir11,ir10,ir09,ir08,ir07,ir06,ir05,ir04,ir03,ir02,Or[ir01,ir00],
																								overflowAfterRound,u23,u22,u21,u20,u19,u18,u17,u16,u15,u14,u13,u12,u11,u10,u09,u08,u07,u06,u05,u04,u03,u02,u01,u00]
									
							and
										   (
												overflowAfterRound = true implies
														(
															addOne8bits[e30,e29,e28,e27,e26,e25,e24,e23,false,r30,r29,r28,r27,r26,r25,r24,r23] 
															and
									 						(
																(r30=true and r29=true and r28=true and r27=true and r26=true and r25=true and r24=true and r23=true) implies
																	(	r22 = false and r21 = false and r20 = false and r19 = false and r18 = false and r17 = false and r16 = false and r15 = false and r14 = false and 
																		r13 = false and r12 = false and r11 = false and r10 = false and r09 = false and r08 = false and r07 = false and r06 = false and r05 = false and 
																		r04 = false and r03 = false and r02 = false and r01 = false and r00 = false 
																	)
																else
																	(	r22 = u23 and r21 = u22 and r20 = u21 and r19 = u20 and r18 = u19 and r17 = u18 and r16 = u17 and r15 = u16 and r14 = u15 and 
																		r13 = u14 and r12 = u13 and r11 = u12 and r10 = u11 and r09 = u10 and r08 = u09 and r07 = u08 and r06 = u07 and r05 = u06 and 
																		r04 = u05 and r03 = u04 and r02 = u03 and r01 = u02 and r00 = u01 
																	)
															)
														)
												else
													(
														r30 = e30 and r29 = e29 and r28 = e28 and r27 = e27 and r26 = e26 and r25 = e25 and r24 = e24 and r23 = e23 and
														r22 = u22 and r21 = u21 and r20 = u20 and r19 = u19 and r18 = u18 and r17 = u17 and r16 = u16 and r15 = u15 and 
														r14 = u14 and r13 = u13 and r12 = u12 and r11 = u11 and r10 = u10 and r09 = u09 and r08 = u08 and r07 = u07 and
														r06 = u06 and r05 = u05 and r04 = u04 and r03 = u03 and r02 = u02 and r01 = u01 and r00 = u00
													)
											
											)
										)
									)
							) 
							else 
							(   
								(
									flagGT = true implies 
									(
										(e30 = s30 and e29 = s29 and e28 = s28 and e27 = s27 and e26 = s26 and e25 = s25 and e24 = s24 and e23 = s23)
									)		
									else
									(
										(e30 = t30 and e29 = t29 and e28 = t28 and e27 = t27 and e26 = t26 and e25 = t25 and e24 = t24 and e23 = t23)
									)
								)	
								and
								(
									roundsUp[ir03,ir02,ir01,ir00] implies
									(
											booleanwiseAddOne24bits[ir26,ir25,ir24,ir23,ir22,ir21,ir20,ir19,ir18,ir17,ir16,ir15,ir14,ir13,ir12,ir11,ir10,ir09,ir08,ir07,ir06,ir05,ir04,ir03,
																			overflowAfterRound,u23,u22,u21,u20,u19,u18,u17,u16,u15,u14,u13,u12,u11,u10,u09,u08,u07,u06,u05,u04,u03,u02,u01,u00]
											and
										   	(
												overflowAfterRound = true implies
														(
															addOne8bits[e30,e29,e28,e27,e26,e25,e24,e23,false,f30,f29,f28,f27,f26,f25,f24,f23] 
															and
									 						(
																(f30=true and f29=true and f28=true and f27=true and f26=true and f25=true and f24=true and f23=true) implies
																	(	r30 = true and r29 = true and r28 = true and r27 = true and r26 = true and r25 = true and r24 = true and r23 = true and
																		r22 = false and r21 = false and r20 = false and r19 = false and r18 = false and r17 = false and r16 = false and r15 = false and r14 = false and 
																		r13 = false and r12 = false and r11 = false and r10 = false and r09 = false and r08 = false and r07 = false and r06 = false and r05 = false and 
																		r04 = false and r03 = false and r02 = false and r01 = false and r00 = false 
																	)
																else
																	(	
																		normalize[	f30,f29,f28,f27,f26,f25,f24,f23,true,u23,u22,u21,u20,u19,u18,u17,u16,u15,u14,u13,u12,u11,u10,u09,u08,u07,u06,u05,u04,u03,u02,u01,
																						r30,r29,r28,r27,r26,r25,r24,r23,r22,r21,r20,r19,r18,r17,r16,r15,r14,r13,r12,r11,r10,r09,r08,r07,r06,r05,r04,r03,r02,r01,r00]
																	)
															)
														)
												else
													(
														normalize[	e30,e29,e28,e27,e26,e25,e24,e23,u23,u22,u21,u20,u19,u18,u17,u16,u15,u14,u13,u12,u11,u10,u09,u08,u07,u06,u05,u04,u03,u02,u01,u00,
																		r30,r29,r28,r27,r26,r25,r24,r23,r22,r21,r20,r19,r18,r17,r16,r15,r14,r13,r12,r11,r10,r09,r08,r07,r06,r05,r04,r03,r02,r01,r00]
													)
											)
									)	
									else
									(
										normalize[	e30,e29,e28,e27,e26,e25,e24,e23,ir26,ir25,ir24,ir23,ir22,ir21,ir20,ir19,ir18,ir17,ir16,ir15,ir14,ir13,ir12,ir11,ir10,ir09,ir08,ir07,ir06,ir05,ir04,ir03,
														r30,r29,r28,r27,r26,r25,r24,r23,r22,r21,r20,r19,r18,r17,r16,r15,r14,r13,r12,r11,r10,r09,r08,r07,r06,r05,r04,r03,r02,r01,r00]
									)	
								)	
							)	
				)
			)
			else
			(
				(isBitNormalized[s31,s30,s29,s28,s27,s26,s25,s24,s23,s22,s21,s20,s19,s18,s17,s16,s15,s14,s13,s12,s11,s10,s09,s08,s07,s06,s05,s04,s03,s02,s01,s00] and isBitZero[t31,t30,t29,t28,t27,t26,t25,t24,t23,t22,t21,t20,t19,t18,t17,t16,t15,t14,t13,t12,t11,t10,t09,t08,t07,t06,t05,t04,t03,t02,t01,t00]) implies
					(r31=s31 and r30=s30 and r29=s29 and r28=s28 and r27=s27 and r26=s26 and r25=s25 and r24=s24 and r23=s23 and r22=s22 and r21=s21 and r20=s20 and r19=s19 and r18=s18 and r17=s17 and r16=s16 and r15=s15 and r14=s14 and r13=s13 and r12=s12 and r11=s11 and r10=s10 and r09=s09 and r08=s08 and r07=s07 and r06=s06 and r05=s05 and r04=s04 and r03=s03 and r02=s02 and r01=s01 and r00=s00)
				else
				(
					(isBitZero[s31,s30,s29,s28,s27,s26,s25,s24,s23,s22,s21,s20,s19,s18,s17,s16,s15,s14,s13,s12,s11,s10,s09,s08,s07,s06,s05,s04,s03,s02,s01,s00] and isBitNormalized[t31,t30,t29,t28,t27,t26,t25,t24,t23,t22,t21,t20,t19,t18,t17,t16,t15,t14,t13,t12,t11,t10,t09,t08,t07,t06,t05,t04,t03,t02,t01,t00]) implies
						(r31=t31 and r30=t30 and r29=t29 and r28=t28 and r27=t27 and r26=t26 and r25=t25 and r24=t24 and r23=t23 and r22=t22 and r21=t21 and r20=t20 and r19=t19 and r18=t18 and r17=t17 and r16=t16 and r15=t15 and r14=t14 and r13=t13 and r12=t12 and r11=t11 and r10=t10 and r09=t09 and r08=t08 and r07=t07 and r06=t06 and r05=t05 and r04=t04 and r03=t03 and r02=t02 and r01=t01 and r00=t00)
					else
					(
						(isBitZero[s31,s30,s29,s28,s27,s26,s25,s24,s23,s22,s21,s20,s19,s18,s17,s16,s15,s14,s13,s12,s11,s10,s09,s08,s07,s06,s05,s04,s03,s02,s01,s00] and isBitZero[t31,t30,t29,t28,t27,t26,t25,t24,t23,t22,t21,t20,t19,t18,t17,t16,t15,t14,t13,t12,t11,t10,t09,t08,t07,t06,t05,t04,t03,t02,t01,t00]) implies	
						(
							(	(s31=true and t31=true) implies
									r31=true
								else
									r31=false
							)
							and
							isBitZero[r31,r30,r29,r28,r27,r26,r25,r24,r23,r22,r21,r20,r19,r18,r17,r16,r15,r14,r13,r12,r11,r10,r09,r08,r07,r06,r05,r04,r03,r02,r01,r00]
						)
						else
						(
							((isBitNormalized[s31,s30,s29,s28,s27,s26,s25,s24,s23,s22,s21,s20,s19,s18,s17,s16,s15,s14,s13,s12,s11,s10,s09,s08,s07,s06,s05,s04,s03,s02,s01,s00] or isBitZero[s31,s30,s29,s28,s27,s26,s25,s24,s23,s22,s21,s20,s19,s18,s17,s16,s15,s14,s13,s12,s11,s10,s09,s08,s07,s06,s05,s04,s03,s02,s01,s00]) and isBitInfinity[t31,t30,t29,t28,t27,t26,t25,t24,t23,t22,t21,t20,t19,t18,t17,t16,t15,t14,t13,t12,t11,t10,t09,t08,t07,t06,t05,t04,t03,t02,t01,t00]) implies
								(r31=t31 and r30=t30 and r29=t29 and r28=t28 and r27=t27 and r26=t26 and r25=t25 and r24=t24 and r23=t23 and r22=t22 and r21=t21 and r20=t20 and r19=t19 and r18=t18 and r17=t17 and r16=t16 and r15=t15 and r14=t14 and r13=t13 and r12=t12 and r11=t11 and r10=t10 and r09=t09 and r08=t08 and r07=t07 and r06=t06 and r05=t05 and r04=t04 and r03=t03 and r02=t02 and r01=t01 and r00=t00)
							else
							(
		 						(isBitInfinity[s31,s30,s29,s28,s27,s26,s25,s24,s23,s22,s21,s20,s19,s18,s17,s16,s15,s14,s13,s12,s11,s10,s09,s08,s07,s06,s05,s04,s03,s02,s01,s00] and (isBitNormalized[t31,t30,t29,t28,t27,t26,t25,t24,t23,t22,t21,t20,t19,t18,t17,t16,t15,t14,t13,t12,t11,t10,t09,t08,t07,t06,t05,t04,t03,t02,t01,t00] or isBitZero[t31,t30,t29,t28,t27,t26,t25,t24,t23,t22,t21,t20,t19,t18,t17,t16,t15,t14,t13,t12,t11,t10,t09,t08,t07,t06,t05,t04,t03,t02,t01,t00])) implies
									(r31=s31 and r30=s30 and r29=s29 and r28=s28 and r27=s27 and r26=s26 and r25=s25 and r24=s24 and r23=s23 and r22=s22 and r21=s21 and r20=s20 and r19=s19 and r18=s18 and r17=s17 and r16=s16 and r15=s15 and r14=s14 and r13=s13 and r12=s12 and r11=s11 and r10=s10 and r09=s09 and r08=s08 and r07=s07 and r06=s06 and r05=s05 and r04=s04 and r03=s03 and r02=s02 and r01=s01 and r00=s00)
								else
								(
									(isBitInfinity[s31,s30,s29,s28,s27,s26,s25,s24,s23,s22,s21,s20,s19,s18,s17,s16,s15,s14,s13,s12,s11,s10,s09,s08,s07,s06,s05,s04,s03,s02,s01,s00] and isBitInfinity[t31,t30,t29,t28,t27,t26,t25,t24,t23,t22,t21,t20,t19,t18,t17,t16,t15,t14,t13,t12,t11,t10,t09,t08,t07,t06,t05,t04,t03,t02,t01,t00]) implies
									(
										((s31=true and t31=true) or (s31=false and t31=false)) implies
											(r31=s31 and r30=s30 and r29=s29 and r28=s28 and r27=s27 and r26=s26 and r25=s25 and r24=s24 and r23=s23 and r22=s22 and r21=s21 and r20=s20 and r19=s19 and r18=s18 and r17=s17 and r16=s16 and r15=s15 and r14=s14 and r13=s13 and r12=s12 and r11=s11 and r10=s10 and r09=s09 and r08=s08 and r07=s07 and r06=s06 and r05=s05 and r04=s04 and r03=s03 and r02=s02 and r01=s01 and r00=s00)
										else
											isBitNaN[r31,r30,r29,r28,r27,r26,r25,r24,r23,r22,r21,r20,r19,r18,r17,r16,r15,r14,r13,r12,r11,r10,r09,r08,r07,r06,r05,r04,r03,r02,r01,r00]
									)
									else
									(
										isBitNaN[s31,s30,s29,s28,s27,s26,s25,s24,s23,s22,s21,s20,s19,s18,s17,s16,s15,s14,s13,s12,s11,s10,s09,s08,s07,s06,s05,s04,s03,s02,s01,s00] implies
											(r31=s31 and r30=s30 and r29=s29 and r28=s28 and r27=s27 and r26=s26 and r25=s25 and r24=s24 and r23=s23 and r22=s22 and r21=s21 and r20=s20 and r19=s19 and r18=s18 and r17=s17 and r16=s16 and r15=s15 and r14=s14 and r13=s13 and r12=s12 and r11=s11 and r10=s10 and r09=s09 and r08=s08 and r07=s07 and r06=s06 and r05=s05 and r04=s04 and r03=s03 and r02=s02 and r01=s01 and r00=s00)
										else
										(
											isBitNaN[t31,t30,t29,t28,t27,t26,t25,t24,t23,t22,t21,t20,t19,t18,t17,t16,t15,t14,t13,t12,t11,t10,t09,t08,t07,t06,t05,t04,t03,t02,t01,t00] implies
												(r31=t31 and r30=t30 and r29=t29 and r28=t28 and r27=t27 and r26=t26 and r25=t25 and r24=t24 and r23=t23 and r22=t22 and r21=t21 and r20=t20 and r19=t19 and r18=t18 and r17=t17 and r16=t16 and r15=t15 and r14=t14 and r13=t13 and r12=t12 and r11=t11 and r10=t10 and r09=t09 and r08=t08 and r07=t07 and r06=t06 and r05=t05 and r04=t04 and r03=t03 and r02=t02 and r01=t01 and r00=t00)
										)
									)
								)
							)
						)
					)
				)	
			)
}	











pred booleanwise9bitsSub127[i8, i7, i6, i5, i4, i3, i2, i1, i0,
											o8, o7, o6, o5, o4, o3, o2, o1, o0 : boolean ]{
	booleanwise9bitsAdd127[o8, o7, o6, o5, o4, o3, o2, o1, o0,
										false, i8, i7, i6, i5, i4, i3, i2, i1, i0]
}


pred booleanwise9bitsAdd127[a08, a07, a06, a05, a04, a03, a02, a01, a00,
											o, r08, r07, r06, r05, r04, r03, r02, r01, r00 : boolean ]{
   let c_0 = false | 
   let s_0 = Xor[Not[a00], c_0] | 
   let c_1 = Or[a00, c_0] | 
   let s_1 = Xor[Not[a01], c_1] | 
   let c_2 = Or[a01, c_1] | 
   let s_2 = Xor[Not[a02], c_2] | 
   let c_3 = Or[a02, c_2] | 
   let s_3 = Xor[Not[a03], c_3] | 
   let c_4 = Or[a03, c_3] | 
   let s_4 = Xor[Not[a04], c_4] | 
   let c_5 = Or[a04, c_4] | 
   let s_5 = Xor[Not[a05], c_5] | 
   let c_6 = Or[a05, c_5] | 
   let s_6 = Xor[Not[a06], c_6] | 
   let c_7 = Or[a06, c_6] | 
   let s_7 = Xor[a07, c_7] | 
   let c_8 = And[a07, c_7] | 
   let s_8 = Xor[a08, c_8] | 
   let c_9 = And[a08, c_8] | 
      r00 in s_0 and
      r01 in s_1 and
      r02 in s_2 and
      r03 in s_3 and
      r04 in s_4 and
      r05 in s_5 and
      r06 in s_6 and
      r07 in s_7 and
	  r08 in s_8 and
      o = (c_9)
}






pred booleanwiseAdd8[a07,a06,a05,a04,a03,a02,a01,a00, b07,b06,b05,b04,b03,b02,b01,b00, overflow,r07,r06,r05,r04,r03,r02,r01,r00 : boolean] {
   let c_0 = false | 
   let s_0 = AdderSum[a00, b00, c_0] | 
   let c_1 = AdderCarry[a00, b00, c_0] | 
   let s_1 = AdderSum[a01, b01, c_1] | 
   let c_2 = AdderCarry[a01, b01, c_1] | 
   let s_2 = AdderSum[a02, b02, c_2] | 
   let c_3 = AdderCarry[a02, b02, c_2] | 
   let s_3 = AdderSum[a03, b03, c_3] | 
   let c_4 = AdderCarry[a03, b03, c_3] | 
   let s_4 = AdderSum[a04, b04, c_4] | 
   let c_5 = AdderCarry[a04, b04, c_4] | 
   let s_5 = AdderSum[a05, b05, c_5] | 
   let c_6 = AdderCarry[a05, b05, c_5] | 
   let s_6 = AdderSum[a06, b06, c_6] | 
   let c_7 = AdderCarry[a06, b06, c_6] | 
   let s_7 = AdderSum[a07, b07, c_7] | 
   let c_8 = AdderCarry[a07, b07, c_7] | 
      r00 in s_0 and
      r01 in s_1 and
      r02 in s_2 and
      r03 in s_3 and
      r04 in s_4 and
      r05 in s_5 and
      r06 in s_6 and
      r07 in s_7 and
      overflow = (c_8)
}





pred booleanwiseMantissaMul[
	aa23,aa22,aa21,aa20,aa19,aa18,aa17,aa16,aa15,aa14,aa13,aa12,aa11,aa10,aa09,aa08,aa07,aa06,aa05,aa04,aa03,aa02,aa01,aa00 : boolean, 
	bb23,bb22,bb21,bb20,bb19,bb18,bb17,bb16,bb15,bb14,bb13,bb12,bb11,bb10,bb09,bb08,bb07,bb06,bb05,bb04,bb03,bb02,bb01,bb00 : boolean, 
	rr27,rr26,rr25,rr24,rr23,rr22,rr21,rr20,rr19,rr18,rr17,rr16,rr15,rr14,rr13,rr12,rr11,rr10,rr09,rr08,rr07,rr06,rr05,rr04,rr03,rr02,rr01,rr00 : boolean] {
   let c_0_0 = false | 
   let s_0_0 = false | 
   let s_0_1 = AdderSum  [s_0_0,  And[aa00, bb00], c_0_0] | 
   let c_1_0 = false | 
   let c_1_1 = AdderCarry[s_0_0,  And[aa00, bb00], c_0_0] | 
   let s_1_0 = false | 
   let s_1_1 = AdderSum  [s_1_0,  And[aa01, bb00], c_1_0] | 
   let s_1_2 = AdderSum  [s_1_1,  And[aa00, bb01], c_1_1] | 
   let c_2_0 = false | 
   let c_2_1 = AdderCarry[s_1_0,  And[aa01, bb00], c_1_0] | 
   let c_2_2 = AdderCarry[s_1_1,  And[aa00, bb01], c_1_1] | 
   let s_2_0 = false | 
   let s_2_1 = AdderSum  [s_2_0,  And[aa02, bb00], c_2_0] | 
   let s_2_2 = AdderSum  [s_2_1,  And[aa01, bb01], c_2_1] | 
   let s_2_3 = AdderSum  [s_2_2,  And[aa00, bb02], c_2_2] | 
   let c_3_0 = false | 
   let c_3_1 = AdderCarry[s_2_0,  And[aa02, bb00], c_2_0] | 
   let c_3_2 = AdderCarry[s_2_1,  And[aa01, bb01], c_2_1] | 
   let c_3_3 = AdderCarry[s_2_2,  And[aa00, bb02], c_2_2] | 
   let s_3_0 = false | 
   let s_3_1 = AdderSum  [s_3_0,  And[aa03, bb00], c_3_0] | 
   let s_3_2 = AdderSum  [s_3_1,  And[aa02, bb01], c_3_1] | 
   let s_3_3 = AdderSum  [s_3_2,  And[aa01, bb02], c_3_2] | 
   let s_3_4 = AdderSum  [s_3_3,  And[aa00, bb03], c_3_3] | 
   let c_4_0 = false | 
   let c_4_1 = AdderCarry[s_3_0,  And[aa03, bb00], c_3_0] | 
   let c_4_2 = AdderCarry[s_3_1,  And[aa02, bb01], c_3_1] | 
   let c_4_3 = AdderCarry[s_3_2,  And[aa01, bb02], c_3_2] | 
   let c_4_4 = AdderCarry[s_3_3,  And[aa00, bb03], c_3_3] | 
   let s_4_0 = false | 
   let s_4_1 = AdderSum  [s_4_0,  And[aa04, bb00], c_4_0] | 
   let s_4_2 = AdderSum  [s_4_1,  And[aa03, bb01], c_4_1] | 
   let s_4_3 = AdderSum  [s_4_2,  And[aa02, bb02], c_4_2] | 
   let s_4_4 = AdderSum  [s_4_3,  And[aa01, bb03], c_4_3] | 
   let s_4_5 = AdderSum  [s_4_4,  And[aa00, bb04], c_4_4] | 
   let c_5_0 = false | 
   let c_5_1 = AdderCarry[s_4_0,  And[aa04, bb00], c_4_0] | 
   let c_5_2 = AdderCarry[s_4_1,  And[aa03, bb01], c_4_1] | 
   let c_5_3 = AdderCarry[s_4_2,  And[aa02, bb02], c_4_2] | 
   let c_5_4 = AdderCarry[s_4_3,  And[aa01, bb03], c_4_3] | 
   let c_5_5 = AdderCarry[s_4_4,  And[aa00, bb04], c_4_4] | 
   let s_5_0 = false | 
   let s_5_1 = AdderSum  [s_5_0,  And[aa05, bb00], c_5_0] | 
   let s_5_2 = AdderSum  [s_5_1,  And[aa04, bb01], c_5_1] | 
   let s_5_3 = AdderSum  [s_5_2,  And[aa03, bb02], c_5_2] | 
   let s_5_4 = AdderSum  [s_5_3,  And[aa02, bb03], c_5_3] | 
   let s_5_5 = AdderSum  [s_5_4,  And[aa01, bb04], c_5_4] | 
   let s_5_6 = AdderSum  [s_5_5,  And[aa00, bb05], c_5_5] | 
   let c_6_0 = false | 
   let c_6_1 = AdderCarry[s_5_0,  And[aa05, bb00], c_5_0] | 
   let c_6_2 = AdderCarry[s_5_1,  And[aa04, bb01], c_5_1] | 
   let c_6_3 = AdderCarry[s_5_2,  And[aa03, bb02], c_5_2] | 
   let c_6_4 = AdderCarry[s_5_3,  And[aa02, bb03], c_5_3] | 
   let c_6_5 = AdderCarry[s_5_4,  And[aa01, bb04], c_5_4] | 
   let c_6_6 = AdderCarry[s_5_5,  And[aa00, bb05], c_5_5] | 
   let s_6_0 = false | 
   let s_6_1 = AdderSum  [s_6_0,  And[aa06, bb00], c_6_0] | 
   let s_6_2 = AdderSum  [s_6_1,  And[aa05, bb01], c_6_1] | 
   let s_6_3 = AdderSum  [s_6_2,  And[aa04, bb02], c_6_2] | 
   let s_6_4 = AdderSum  [s_6_3,  And[aa03, bb03], c_6_3] | 
   let s_6_5 = AdderSum  [s_6_4,  And[aa02, bb04], c_6_4] | 
   let s_6_6 = AdderSum  [s_6_5,  And[aa01, bb05], c_6_5] | 
   let s_6_7 = AdderSum  [s_6_6,  And[aa00, bb06], c_6_6] | 
   let c_7_0 = false | 
   let c_7_1 = AdderCarry[s_6_0,  And[aa06, bb00], c_6_0] | 
   let c_7_2 = AdderCarry[s_6_1,  And[aa05, bb01], c_6_1] | 
   let c_7_3 = AdderCarry[s_6_2,  And[aa04, bb02], c_6_2] | 
   let c_7_4 = AdderCarry[s_6_3,  And[aa03, bb03], c_6_3] | 
   let c_7_5 = AdderCarry[s_6_4,  And[aa02, bb04], c_6_4] | 
   let c_7_6 = AdderCarry[s_6_5,  And[aa01, bb05], c_6_5] | 
   let c_7_7 = AdderCarry[s_6_6,  And[aa00, bb06], c_6_6] | 
   let s_7_0 = false | 
   let s_7_1 = AdderSum  [s_7_0,  And[aa07, bb00], c_7_0] | 
   let s_7_2 = AdderSum  [s_7_1,  And[aa06, bb01], c_7_1] | 
   let s_7_3 = AdderSum  [s_7_2,  And[aa05, bb02], c_7_2] | 
   let s_7_4 = AdderSum  [s_7_3,  And[aa04, bb03], c_7_3] | 
   let s_7_5 = AdderSum  [s_7_4,  And[aa03, bb04], c_7_4] | 
   let s_7_6 = AdderSum  [s_7_5,  And[aa02, bb05], c_7_5] | 
   let s_7_7 = AdderSum  [s_7_6,  And[aa01, bb06], c_7_6] | 
   let s_7_8 = AdderSum  [s_7_7,  And[aa00, bb07], c_7_7] | 
   let c_8_0 = false | 
   let c_8_1 = AdderCarry[s_7_0,  And[aa07, bb00], c_7_0] | 
   let c_8_2 = AdderCarry[s_7_1,  And[aa06, bb01], c_7_1] | 
   let c_8_3 = AdderCarry[s_7_2,  And[aa05, bb02], c_7_2] | 
   let c_8_4 = AdderCarry[s_7_3,  And[aa04, bb03], c_7_3] | 
   let c_8_5 = AdderCarry[s_7_4,  And[aa03, bb04], c_7_4] | 
   let c_8_6 = AdderCarry[s_7_5,  And[aa02, bb05], c_7_5] | 
   let c_8_7 = AdderCarry[s_7_6,  And[aa01, bb06], c_7_6] | 
   let c_8_8 = AdderCarry[s_7_7,  And[aa00, bb07], c_7_7] | 
   let s_8_0 = false | 
   let s_8_1 = AdderSum  [s_8_0,  And[aa08, bb00], c_8_0] | 
   let s_8_2 = AdderSum  [s_8_1,  And[aa07, bb01], c_8_1] | 
   let s_8_3 = AdderSum  [s_8_2,  And[aa06, bb02], c_8_2] | 
   let s_8_4 = AdderSum  [s_8_3,  And[aa05, bb03], c_8_3] | 
   let s_8_5 = AdderSum  [s_8_4,  And[aa04, bb04], c_8_4] | 
   let s_8_6 = AdderSum  [s_8_5,  And[aa03, bb05], c_8_5] | 
   let s_8_7 = AdderSum  [s_8_6,  And[aa02, bb06], c_8_6] | 
   let s_8_8 = AdderSum  [s_8_7,  And[aa01, bb07], c_8_7] | 
   let s_8_9 = AdderSum  [s_8_8,  And[aa00, bb08], c_8_8] | 
   let c_9_0 = false | 
   let c_9_1 = AdderCarry[s_8_0,  And[aa08, bb00], c_8_0] | 
   let c_9_2 = AdderCarry[s_8_1,  And[aa07, bb01], c_8_1] | 
   let c_9_3 = AdderCarry[s_8_2,  And[aa06, bb02], c_8_2] | 
   let c_9_4 = AdderCarry[s_8_3,  And[aa05, bb03], c_8_3] | 
   let c_9_5 = AdderCarry[s_8_4,  And[aa04, bb04], c_8_4] | 
   let c_9_6 = AdderCarry[s_8_5,  And[aa03, bb05], c_8_5] | 
   let c_9_7 = AdderCarry[s_8_6,  And[aa02, bb06], c_8_6] | 
   let c_9_8 = AdderCarry[s_8_7,  And[aa01, bb07], c_8_7] | 
   let c_9_9 = AdderCarry[s_8_8,  And[aa00, bb08], c_8_8] | 
   let s_9_0 = false | 
   let s_9_1 = AdderSum  [s_9_0,  And[aa09, bb00], c_9_0] | 
   let s_9_2 = AdderSum  [s_9_1,  And[aa08, bb01], c_9_1] | 
   let s_9_3 = AdderSum  [s_9_2,  And[aa07, bb02], c_9_2] | 
   let s_9_4 = AdderSum  [s_9_3,  And[aa06, bb03], c_9_3] | 
   let s_9_5 = AdderSum  [s_9_4,  And[aa05, bb04], c_9_4] | 
   let s_9_6 = AdderSum  [s_9_5,  And[aa04, bb05], c_9_5] | 
   let s_9_7 = AdderSum  [s_9_6,  And[aa03, bb06], c_9_6] | 
   let s_9_8 = AdderSum  [s_9_7,  And[aa02, bb07], c_9_7] | 
   let s_9_9 = AdderSum  [s_9_8,  And[aa01, bb08], c_9_8] | 
   let s_9_10 = AdderSum  [s_9_9,  And[aa00, bb09], c_9_9] | 
   let c_10_0 = false | 
   let c_10_1 = AdderCarry[s_9_0,  And[aa09, bb00], c_9_0] | 
   let c_10_2 = AdderCarry[s_9_1,  And[aa08, bb01], c_9_1] | 
   let c_10_3 = AdderCarry[s_9_2,  And[aa07, bb02], c_9_2] | 
   let c_10_4 = AdderCarry[s_9_3,  And[aa06, bb03], c_9_3] | 
   let c_10_5 = AdderCarry[s_9_4,  And[aa05, bb04], c_9_4] | 
   let c_10_6 = AdderCarry[s_9_5,  And[aa04, bb05], c_9_5] | 
   let c_10_7 = AdderCarry[s_9_6,  And[aa03, bb06], c_9_6] | 
   let c_10_8 = AdderCarry[s_9_7,  And[aa02, bb07], c_9_7] | 
   let c_10_9 = AdderCarry[s_9_8,  And[aa01, bb08], c_9_8] | 
   let c_10_10 = AdderCarry[s_9_9,  And[aa00, bb09], c_9_9] | 
   let s_10_0 = false | 
   let s_10_1 = AdderSum  [s_10_0,  And[aa10, bb00], c_10_0] | 
   let s_10_2 = AdderSum  [s_10_1,  And[aa09, bb01], c_10_1] | 
   let s_10_3 = AdderSum  [s_10_2,  And[aa08, bb02], c_10_2] | 
   let s_10_4 = AdderSum  [s_10_3,  And[aa07, bb03], c_10_3] | 
   let s_10_5 = AdderSum  [s_10_4,  And[aa06, bb04], c_10_4] | 
   let s_10_6 = AdderSum  [s_10_5,  And[aa05, bb05], c_10_5] | 
   let s_10_7 = AdderSum  [s_10_6,  And[aa04, bb06], c_10_6] | 
   let s_10_8 = AdderSum  [s_10_7,  And[aa03, bb07], c_10_7] | 
   let s_10_9 = AdderSum  [s_10_8,  And[aa02, bb08], c_10_8] | 
   let s_10_10 = AdderSum  [s_10_9,  And[aa01, bb09], c_10_9] | 
   let s_10_11 = AdderSum  [s_10_10,  And[aa00, bb10], c_10_10] | 
   let c_11_0 = false | 
   let c_11_1 = AdderCarry[s_10_0,  And[aa10, bb00], c_10_0] | 
   let c_11_2 = AdderCarry[s_10_1,  And[aa09, bb01], c_10_1] | 
   let c_11_3 = AdderCarry[s_10_2,  And[aa08, bb02], c_10_2] | 
   let c_11_4 = AdderCarry[s_10_3,  And[aa07, bb03], c_10_3] | 
   let c_11_5 = AdderCarry[s_10_4,  And[aa06, bb04], c_10_4] | 
   let c_11_6 = AdderCarry[s_10_5,  And[aa05, bb05], c_10_5] | 
   let c_11_7 = AdderCarry[s_10_6,  And[aa04, bb06], c_10_6] | 
   let c_11_8 = AdderCarry[s_10_7,  And[aa03, bb07], c_10_7] | 
   let c_11_9 = AdderCarry[s_10_8,  And[aa02, bb08], c_10_8] | 
   let c_11_10 = AdderCarry[s_10_9,  And[aa01, bb09], c_10_9] | 
   let c_11_11 = AdderCarry[s_10_10,  And[aa00, bb10], c_10_10] | 
   let s_11_0 = false | 
   let s_11_1 = AdderSum  [s_11_0,  And[aa11, bb00], c_11_0] | 
   let s_11_2 = AdderSum  [s_11_1,  And[aa10, bb01], c_11_1] | 
   let s_11_3 = AdderSum  [s_11_2,  And[aa09, bb02], c_11_2] | 
   let s_11_4 = AdderSum  [s_11_3,  And[aa08, bb03], c_11_3] | 
   let s_11_5 = AdderSum  [s_11_4,  And[aa07, bb04], c_11_4] | 
   let s_11_6 = AdderSum  [s_11_5,  And[aa06, bb05], c_11_5] | 
   let s_11_7 = AdderSum  [s_11_6,  And[aa05, bb06], c_11_6] | 
   let s_11_8 = AdderSum  [s_11_7,  And[aa04, bb07], c_11_7] | 
   let s_11_9 = AdderSum  [s_11_8,  And[aa03, bb08], c_11_8] | 
   let s_11_10 = AdderSum  [s_11_9,  And[aa02, bb09], c_11_9] | 
   let s_11_11 = AdderSum  [s_11_10,  And[aa01, bb10], c_11_10] | 
   let s_11_12 = AdderSum  [s_11_11,  And[aa00, bb11], c_11_11] | 
   let c_12_0 = false | 
   let c_12_1 = AdderCarry[s_11_0,  And[aa11, bb00], c_11_0] | 
   let c_12_2 = AdderCarry[s_11_1,  And[aa10, bb01], c_11_1] | 
   let c_12_3 = AdderCarry[s_11_2,  And[aa09, bb02], c_11_2] | 
   let c_12_4 = AdderCarry[s_11_3,  And[aa08, bb03], c_11_3] | 
   let c_12_5 = AdderCarry[s_11_4,  And[aa07, bb04], c_11_4] | 
   let c_12_6 = AdderCarry[s_11_5,  And[aa06, bb05], c_11_5] | 
   let c_12_7 = AdderCarry[s_11_6,  And[aa05, bb06], c_11_6] | 
   let c_12_8 = AdderCarry[s_11_7,  And[aa04, bb07], c_11_7] | 
   let c_12_9 = AdderCarry[s_11_8,  And[aa03, bb08], c_11_8] | 
   let c_12_10 = AdderCarry[s_11_9,  And[aa02, bb09], c_11_9] | 
   let c_12_11 = AdderCarry[s_11_10,  And[aa01, bb10], c_11_10] | 
   let c_12_12 = AdderCarry[s_11_11,  And[aa00, bb11], c_11_11] | 
   let s_12_0 = false | 
   let s_12_1 = AdderSum  [s_12_0,  And[aa12, bb00], c_12_0] | 
   let s_12_2 = AdderSum  [s_12_1,  And[aa11, bb01], c_12_1] | 
   let s_12_3 = AdderSum  [s_12_2,  And[aa10, bb02], c_12_2] | 
   let s_12_4 = AdderSum  [s_12_3,  And[aa09, bb03], c_12_3] | 
   let s_12_5 = AdderSum  [s_12_4,  And[aa08, bb04], c_12_4] | 
   let s_12_6 = AdderSum  [s_12_5,  And[aa07, bb05], c_12_5] | 
   let s_12_7 = AdderSum  [s_12_6,  And[aa06, bb06], c_12_6] | 
   let s_12_8 = AdderSum  [s_12_7,  And[aa05, bb07], c_12_7] | 
   let s_12_9 = AdderSum  [s_12_8,  And[aa04, bb08], c_12_8] | 
   let s_12_10 = AdderSum  [s_12_9,  And[aa03, bb09], c_12_9] | 
   let s_12_11 = AdderSum  [s_12_10,  And[aa02, bb10], c_12_10] | 
   let s_12_12 = AdderSum  [s_12_11,  And[aa01, bb11], c_12_11] | 
   let s_12_13 = AdderSum  [s_12_12,  And[aa00, bb12], c_12_12] | 
   let c_13_0 = false | 
   let c_13_1 = AdderCarry[s_12_0,  And[aa12, bb00], c_12_0] | 
   let c_13_2 = AdderCarry[s_12_1,  And[aa11, bb01], c_12_1] | 
   let c_13_3 = AdderCarry[s_12_2,  And[aa10, bb02], c_12_2] | 
   let c_13_4 = AdderCarry[s_12_3,  And[aa09, bb03], c_12_3] | 
   let c_13_5 = AdderCarry[s_12_4,  And[aa08, bb04], c_12_4] | 
   let c_13_6 = AdderCarry[s_12_5,  And[aa07, bb05], c_12_5] | 
   let c_13_7 = AdderCarry[s_12_6,  And[aa06, bb06], c_12_6] | 
   let c_13_8 = AdderCarry[s_12_7,  And[aa05, bb07], c_12_7] | 
   let c_13_9 = AdderCarry[s_12_8,  And[aa04, bb08], c_12_8] | 
   let c_13_10 = AdderCarry[s_12_9,  And[aa03, bb09], c_12_9] | 
   let c_13_11 = AdderCarry[s_12_10,  And[aa02, bb10], c_12_10] | 
   let c_13_12 = AdderCarry[s_12_11,  And[aa01, bb11], c_12_11] | 
   let c_13_13 = AdderCarry[s_12_12,  And[aa00, bb12], c_12_12] | 
   let s_13_0 = false | 
   let s_13_1 = AdderSum  [s_13_0,  And[aa13, bb00], c_13_0] | 
   let s_13_2 = AdderSum  [s_13_1,  And[aa12, bb01], c_13_1] | 
   let s_13_3 = AdderSum  [s_13_2,  And[aa11, bb02], c_13_2] | 
   let s_13_4 = AdderSum  [s_13_3,  And[aa10, bb03], c_13_3] | 
   let s_13_5 = AdderSum  [s_13_4,  And[aa09, bb04], c_13_4] | 
   let s_13_6 = AdderSum  [s_13_5,  And[aa08, bb05], c_13_5] | 
   let s_13_7 = AdderSum  [s_13_6,  And[aa07, bb06], c_13_6] | 
   let s_13_8 = AdderSum  [s_13_7,  And[aa06, bb07], c_13_7] | 
   let s_13_9 = AdderSum  [s_13_8,  And[aa05, bb08], c_13_8] | 
   let s_13_10 = AdderSum  [s_13_9,  And[aa04, bb09], c_13_9] | 
   let s_13_11 = AdderSum  [s_13_10,  And[aa03, bb10], c_13_10] | 
   let s_13_12 = AdderSum  [s_13_11,  And[aa02, bb11], c_13_11] | 
   let s_13_13 = AdderSum  [s_13_12,  And[aa01, bb12], c_13_12] | 
   let s_13_14 = AdderSum  [s_13_13,  And[aa00, bb13], c_13_13] | 
   let c_14_0 = false | 
   let c_14_1 = AdderCarry[s_13_0,  And[aa13, bb00], c_13_0] | 
   let c_14_2 = AdderCarry[s_13_1,  And[aa12, bb01], c_13_1] | 
   let c_14_3 = AdderCarry[s_13_2,  And[aa11, bb02], c_13_2] | 
   let c_14_4 = AdderCarry[s_13_3,  And[aa10, bb03], c_13_3] | 
   let c_14_5 = AdderCarry[s_13_4,  And[aa09, bb04], c_13_4] | 
   let c_14_6 = AdderCarry[s_13_5,  And[aa08, bb05], c_13_5] | 
   let c_14_7 = AdderCarry[s_13_6,  And[aa07, bb06], c_13_6] | 
   let c_14_8 = AdderCarry[s_13_7,  And[aa06, bb07], c_13_7] | 
   let c_14_9 = AdderCarry[s_13_8,  And[aa05, bb08], c_13_8] | 
   let c_14_10 = AdderCarry[s_13_9,  And[aa04, bb09], c_13_9] | 
   let c_14_11 = AdderCarry[s_13_10,  And[aa03, bb10], c_13_10] | 
   let c_14_12 = AdderCarry[s_13_11,  And[aa02, bb11], c_13_11] | 
   let c_14_13 = AdderCarry[s_13_12,  And[aa01, bb12], c_13_12] | 
   let c_14_14 = AdderCarry[s_13_13,  And[aa00, bb13], c_13_13] | 
   let s_14_0 = false | 
   let s_14_1 = AdderSum  [s_14_0,  And[aa14, bb00], c_14_0] | 
   let s_14_2 = AdderSum  [s_14_1,  And[aa13, bb01], c_14_1] | 
   let s_14_3 = AdderSum  [s_14_2,  And[aa12, bb02], c_14_2] | 
   let s_14_4 = AdderSum  [s_14_3,  And[aa11, bb03], c_14_3] | 
   let s_14_5 = AdderSum  [s_14_4,  And[aa10, bb04], c_14_4] | 
   let s_14_6 = AdderSum  [s_14_5,  And[aa09, bb05], c_14_5] | 
   let s_14_7 = AdderSum  [s_14_6,  And[aa08, bb06], c_14_6] | 
   let s_14_8 = AdderSum  [s_14_7,  And[aa07, bb07], c_14_7] | 
   let s_14_9 = AdderSum  [s_14_8,  And[aa06, bb08], c_14_8] | 
   let s_14_10 = AdderSum  [s_14_9,  And[aa05, bb09], c_14_9] | 
   let s_14_11 = AdderSum  [s_14_10,  And[aa04, bb10], c_14_10] | 
   let s_14_12 = AdderSum  [s_14_11,  And[aa03, bb11], c_14_11] | 
   let s_14_13 = AdderSum  [s_14_12,  And[aa02, bb12], c_14_12] | 
   let s_14_14 = AdderSum  [s_14_13,  And[aa01, bb13], c_14_13] | 
   let s_14_15 = AdderSum  [s_14_14,  And[aa00, bb14], c_14_14] | 
   let c_15_0 = false | 
   let c_15_1 = AdderCarry[s_14_0,  And[aa14, bb00], c_14_0] | 
   let c_15_2 = AdderCarry[s_14_1,  And[aa13, bb01], c_14_1] | 
   let c_15_3 = AdderCarry[s_14_2,  And[aa12, bb02], c_14_2] | 
   let c_15_4 = AdderCarry[s_14_3,  And[aa11, bb03], c_14_3] | 
   let c_15_5 = AdderCarry[s_14_4,  And[aa10, bb04], c_14_4] | 
   let c_15_6 = AdderCarry[s_14_5,  And[aa09, bb05], c_14_5] | 
   let c_15_7 = AdderCarry[s_14_6,  And[aa08, bb06], c_14_6] | 
   let c_15_8 = AdderCarry[s_14_7,  And[aa07, bb07], c_14_7] | 
   let c_15_9 = AdderCarry[s_14_8,  And[aa06, bb08], c_14_8] | 
   let c_15_10 = AdderCarry[s_14_9,  And[aa05, bb09], c_14_9] | 
   let c_15_11 = AdderCarry[s_14_10,  And[aa04, bb10], c_14_10] | 
   let c_15_12 = AdderCarry[s_14_11,  And[aa03, bb11], c_14_11] | 
   let c_15_13 = AdderCarry[s_14_12,  And[aa02, bb12], c_14_12] | 
   let c_15_14 = AdderCarry[s_14_13,  And[aa01, bb13], c_14_13] | 
   let c_15_15 = AdderCarry[s_14_14,  And[aa00, bb14], c_14_14] | 
   let s_15_0 = false | 
   let s_15_1 = AdderSum  [s_15_0,  And[aa15, bb00], c_15_0] | 
   let s_15_2 = AdderSum  [s_15_1,  And[aa14, bb01], c_15_1] | 
   let s_15_3 = AdderSum  [s_15_2,  And[aa13, bb02], c_15_2] | 
   let s_15_4 = AdderSum  [s_15_3,  And[aa12, bb03], c_15_3] | 
   let s_15_5 = AdderSum  [s_15_4,  And[aa11, bb04], c_15_4] | 
   let s_15_6 = AdderSum  [s_15_5,  And[aa10, bb05], c_15_5] | 
   let s_15_7 = AdderSum  [s_15_6,  And[aa09, bb06], c_15_6] | 
   let s_15_8 = AdderSum  [s_15_7,  And[aa08, bb07], c_15_7] | 
   let s_15_9 = AdderSum  [s_15_8,  And[aa07, bb08], c_15_8] | 
   let s_15_10 = AdderSum  [s_15_9,  And[aa06, bb09], c_15_9] | 
   let s_15_11 = AdderSum  [s_15_10,  And[aa05, bb10], c_15_10] | 
   let s_15_12 = AdderSum  [s_15_11,  And[aa04, bb11], c_15_11] | 
   let s_15_13 = AdderSum  [s_15_12,  And[aa03, bb12], c_15_12] | 
   let s_15_14 = AdderSum  [s_15_13,  And[aa02, bb13], c_15_13] | 
   let s_15_15 = AdderSum  [s_15_14,  And[aa01, bb14], c_15_14] | 
   let s_15_16 = AdderSum  [s_15_15,  And[aa00, bb15], c_15_15] | 
   let c_16_0 = false | 
   let c_16_1 = AdderCarry[s_15_0,  And[aa15, bb00], c_15_0] | 
   let c_16_2 = AdderCarry[s_15_1,  And[aa14, bb01], c_15_1] | 
   let c_16_3 = AdderCarry[s_15_2,  And[aa13, bb02], c_15_2] | 
   let c_16_4 = AdderCarry[s_15_3,  And[aa12, bb03], c_15_3] | 
   let c_16_5 = AdderCarry[s_15_4,  And[aa11, bb04], c_15_4] | 
   let c_16_6 = AdderCarry[s_15_5,  And[aa10, bb05], c_15_5] | 
   let c_16_7 = AdderCarry[s_15_6,  And[aa09, bb06], c_15_6] | 
   let c_16_8 = AdderCarry[s_15_7,  And[aa08, bb07], c_15_7] | 
   let c_16_9 = AdderCarry[s_15_8,  And[aa07, bb08], c_15_8] | 
   let c_16_10 = AdderCarry[s_15_9,  And[aa06, bb09], c_15_9] | 
   let c_16_11 = AdderCarry[s_15_10,  And[aa05, bb10], c_15_10] | 
   let c_16_12 = AdderCarry[s_15_11,  And[aa04, bb11], c_15_11] | 
   let c_16_13 = AdderCarry[s_15_12,  And[aa03, bb12], c_15_12] | 
   let c_16_14 = AdderCarry[s_15_13,  And[aa02, bb13], c_15_13] | 
   let c_16_15 = AdderCarry[s_15_14,  And[aa01, bb14], c_15_14] | 
   let c_16_16 = AdderCarry[s_15_15,  And[aa00, bb15], c_15_15] | 
   let s_16_0 = false | 
   let s_16_1 = AdderSum  [s_16_0,  And[aa16, bb00], c_16_0] | 
   let s_16_2 = AdderSum  [s_16_1,  And[aa15, bb01], c_16_1] | 
   let s_16_3 = AdderSum  [s_16_2,  And[aa14, bb02], c_16_2] | 
   let s_16_4 = AdderSum  [s_16_3,  And[aa13, bb03], c_16_3] | 
   let s_16_5 = AdderSum  [s_16_4,  And[aa12, bb04], c_16_4] | 
   let s_16_6 = AdderSum  [s_16_5,  And[aa11, bb05], c_16_5] | 
   let s_16_7 = AdderSum  [s_16_6,  And[aa10, bb06], c_16_6] | 
   let s_16_8 = AdderSum  [s_16_7,  And[aa09, bb07], c_16_7] | 
   let s_16_9 = AdderSum  [s_16_8,  And[aa08, bb08], c_16_8] | 
   let s_16_10 = AdderSum  [s_16_9,  And[aa07, bb09], c_16_9] | 
   let s_16_11 = AdderSum  [s_16_10,  And[aa06, bb10], c_16_10] | 
   let s_16_12 = AdderSum  [s_16_11,  And[aa05, bb11], c_16_11] | 
   let s_16_13 = AdderSum  [s_16_12,  And[aa04, bb12], c_16_12] | 
   let s_16_14 = AdderSum  [s_16_13,  And[aa03, bb13], c_16_13] | 
   let s_16_15 = AdderSum  [s_16_14,  And[aa02, bb14], c_16_14] | 
   let s_16_16 = AdderSum  [s_16_15,  And[aa01, bb15], c_16_15] | 
   let s_16_17 = AdderSum  [s_16_16,  And[aa00, bb16], c_16_16] | 
   let c_17_0 = false | 
   let c_17_1 = AdderCarry[s_16_0,  And[aa16, bb00], c_16_0] | 
   let c_17_2 = AdderCarry[s_16_1,  And[aa15, bb01], c_16_1] | 
   let c_17_3 = AdderCarry[s_16_2,  And[aa14, bb02], c_16_2] | 
   let c_17_4 = AdderCarry[s_16_3,  And[aa13, bb03], c_16_3] | 
   let c_17_5 = AdderCarry[s_16_4,  And[aa12, bb04], c_16_4] | 
   let c_17_6 = AdderCarry[s_16_5,  And[aa11, bb05], c_16_5] | 
   let c_17_7 = AdderCarry[s_16_6,  And[aa10, bb06], c_16_6] | 
   let c_17_8 = AdderCarry[s_16_7,  And[aa09, bb07], c_16_7] | 
   let c_17_9 = AdderCarry[s_16_8,  And[aa08, bb08], c_16_8] | 
   let c_17_10 = AdderCarry[s_16_9,  And[aa07, bb09], c_16_9] | 
   let c_17_11 = AdderCarry[s_16_10,  And[aa06, bb10], c_16_10] | 
   let c_17_12 = AdderCarry[s_16_11,  And[aa05, bb11], c_16_11] | 
   let c_17_13 = AdderCarry[s_16_12,  And[aa04, bb12], c_16_12] | 
   let c_17_14 = AdderCarry[s_16_13,  And[aa03, bb13], c_16_13] | 
   let c_17_15 = AdderCarry[s_16_14,  And[aa02, bb14], c_16_14] | 
   let c_17_16 = AdderCarry[s_16_15,  And[aa01, bb15], c_16_15] | 
   let c_17_17 = AdderCarry[s_16_16,  And[aa00, bb16], c_16_16] | 
   let s_17_0 = false | 
   let s_17_1 = AdderSum  [s_17_0,  And[aa17, bb00], c_17_0] | 
   let s_17_2 = AdderSum  [s_17_1,  And[aa16, bb01], c_17_1] | 
   let s_17_3 = AdderSum  [s_17_2,  And[aa15, bb02], c_17_2] | 
   let s_17_4 = AdderSum  [s_17_3,  And[aa14, bb03], c_17_3] | 
   let s_17_5 = AdderSum  [s_17_4,  And[aa13, bb04], c_17_4] | 
   let s_17_6 = AdderSum  [s_17_5,  And[aa12, bb05], c_17_5] | 
   let s_17_7 = AdderSum  [s_17_6,  And[aa11, bb06], c_17_6] | 
   let s_17_8 = AdderSum  [s_17_7,  And[aa10, bb07], c_17_7] | 
   let s_17_9 = AdderSum  [s_17_8,  And[aa09, bb08], c_17_8] | 
   let s_17_10 = AdderSum  [s_17_9,  And[aa08, bb09], c_17_9] | 
   let s_17_11 = AdderSum  [s_17_10,  And[aa07, bb10], c_17_10] | 
   let s_17_12 = AdderSum  [s_17_11,  And[aa06, bb11], c_17_11] | 
   let s_17_13 = AdderSum  [s_17_12,  And[aa05, bb12], c_17_12] | 
   let s_17_14 = AdderSum  [s_17_13,  And[aa04, bb13], c_17_13] | 
   let s_17_15 = AdderSum  [s_17_14,  And[aa03, bb14], c_17_14] | 
   let s_17_16 = AdderSum  [s_17_15,  And[aa02, bb15], c_17_15] | 
   let s_17_17 = AdderSum  [s_17_16,  And[aa01, bb16], c_17_16] | 
   let s_17_18 = AdderSum  [s_17_17,  And[aa00, bb17], c_17_17] | 
   let c_18_0 = false | 
   let c_18_1 = AdderCarry[s_17_0,  And[aa17, bb00], c_17_0] | 
   let c_18_2 = AdderCarry[s_17_1,  And[aa16, bb01], c_17_1] | 
   let c_18_3 = AdderCarry[s_17_2,  And[aa15, bb02], c_17_2] | 
   let c_18_4 = AdderCarry[s_17_3,  And[aa14, bb03], c_17_3] | 
   let c_18_5 = AdderCarry[s_17_4,  And[aa13, bb04], c_17_4] | 
   let c_18_6 = AdderCarry[s_17_5,  And[aa12, bb05], c_17_5] | 
   let c_18_7 = AdderCarry[s_17_6,  And[aa11, bb06], c_17_6] | 
   let c_18_8 = AdderCarry[s_17_7,  And[aa10, bb07], c_17_7] | 
   let c_18_9 = AdderCarry[s_17_8,  And[aa09, bb08], c_17_8] | 
   let c_18_10 = AdderCarry[s_17_9,  And[aa08, bb09], c_17_9] | 
   let c_18_11 = AdderCarry[s_17_10,  And[aa07, bb10], c_17_10] | 
   let c_18_12 = AdderCarry[s_17_11,  And[aa06, bb11], c_17_11] | 
   let c_18_13 = AdderCarry[s_17_12,  And[aa05, bb12], c_17_12] | 
   let c_18_14 = AdderCarry[s_17_13,  And[aa04, bb13], c_17_13] | 
   let c_18_15 = AdderCarry[s_17_14,  And[aa03, bb14], c_17_14] | 
   let c_18_16 = AdderCarry[s_17_15,  And[aa02, bb15], c_17_15] | 
   let c_18_17 = AdderCarry[s_17_16,  And[aa01, bb16], c_17_16] | 
   let c_18_18 = AdderCarry[s_17_17,  And[aa00, bb17], c_17_17] | 
   let s_18_0 = false | 
   let s_18_1 = AdderSum  [s_18_0,  And[aa18, bb00], c_18_0] | 
   let s_18_2 = AdderSum  [s_18_1,  And[aa17, bb01], c_18_1] | 
   let s_18_3 = AdderSum  [s_18_2,  And[aa16, bb02], c_18_2] | 
   let s_18_4 = AdderSum  [s_18_3,  And[aa15, bb03], c_18_3] | 
   let s_18_5 = AdderSum  [s_18_4,  And[aa14, bb04], c_18_4] | 
   let s_18_6 = AdderSum  [s_18_5,  And[aa13, bb05], c_18_5] | 
   let s_18_7 = AdderSum  [s_18_6,  And[aa12, bb06], c_18_6] | 
   let s_18_8 = AdderSum  [s_18_7,  And[aa11, bb07], c_18_7] | 
   let s_18_9 = AdderSum  [s_18_8,  And[aa10, bb08], c_18_8] | 
   let s_18_10 = AdderSum  [s_18_9,  And[aa09, bb09], c_18_9] | 
   let s_18_11 = AdderSum  [s_18_10,  And[aa08, bb10], c_18_10] | 
   let s_18_12 = AdderSum  [s_18_11,  And[aa07, bb11], c_18_11] | 
   let s_18_13 = AdderSum  [s_18_12,  And[aa06, bb12], c_18_12] | 
   let s_18_14 = AdderSum  [s_18_13,  And[aa05, bb13], c_18_13] | 
   let s_18_15 = AdderSum  [s_18_14,  And[aa04, bb14], c_18_14] | 
   let s_18_16 = AdderSum  [s_18_15,  And[aa03, bb15], c_18_15] | 
   let s_18_17 = AdderSum  [s_18_16,  And[aa02, bb16], c_18_16] | 
   let s_18_18 = AdderSum  [s_18_17,  And[aa01, bb17], c_18_17] | 
   let s_18_19 = AdderSum  [s_18_18,  And[aa00, bb18], c_18_18] | 
   let c_19_0 = false | 
   let c_19_1 = AdderCarry[s_18_0,  And[aa18, bb00], c_18_0] | 
   let c_19_2 = AdderCarry[s_18_1,  And[aa17, bb01], c_18_1] | 
   let c_19_3 = AdderCarry[s_18_2,  And[aa16, bb02], c_18_2] | 
   let c_19_4 = AdderCarry[s_18_3,  And[aa15, bb03], c_18_3] | 
   let c_19_5 = AdderCarry[s_18_4,  And[aa14, bb04], c_18_4] | 
   let c_19_6 = AdderCarry[s_18_5,  And[aa13, bb05], c_18_5] | 
   let c_19_7 = AdderCarry[s_18_6,  And[aa12, bb06], c_18_6] | 
   let c_19_8 = AdderCarry[s_18_7,  And[aa11, bb07], c_18_7] | 
   let c_19_9 = AdderCarry[s_18_8,  And[aa10, bb08], c_18_8] | 
   let c_19_10 = AdderCarry[s_18_9,  And[aa09, bb09], c_18_9] | 
   let c_19_11 = AdderCarry[s_18_10,  And[aa08, bb10], c_18_10] | 
   let c_19_12 = AdderCarry[s_18_11,  And[aa07, bb11], c_18_11] | 
   let c_19_13 = AdderCarry[s_18_12,  And[aa06, bb12], c_18_12] | 
   let c_19_14 = AdderCarry[s_18_13,  And[aa05, bb13], c_18_13] | 
   let c_19_15 = AdderCarry[s_18_14,  And[aa04, bb14], c_18_14] | 
   let c_19_16 = AdderCarry[s_18_15,  And[aa03, bb15], c_18_15] | 
   let c_19_17 = AdderCarry[s_18_16,  And[aa02, bb16], c_18_16] | 
   let c_19_18 = AdderCarry[s_18_17,  And[aa01, bb17], c_18_17] | 
   let c_19_19 = AdderCarry[s_18_18,  And[aa00, bb18], c_18_18] | 
   let s_19_0 = false | 
   let s_19_1 = AdderSum  [s_19_0,  And[aa19, bb00], c_19_0] | 
   let s_19_2 = AdderSum  [s_19_1,  And[aa18, bb01], c_19_1] | 
   let s_19_3 = AdderSum  [s_19_2,  And[aa17, bb02], c_19_2] | 
   let s_19_4 = AdderSum  [s_19_3,  And[aa16, bb03], c_19_3] | 
   let s_19_5 = AdderSum  [s_19_4,  And[aa15, bb04], c_19_4] | 
   let s_19_6 = AdderSum  [s_19_5,  And[aa14, bb05], c_19_5] | 
   let s_19_7 = AdderSum  [s_19_6,  And[aa13, bb06], c_19_6] | 
   let s_19_8 = AdderSum  [s_19_7,  And[aa12, bb07], c_19_7] | 
   let s_19_9 = AdderSum  [s_19_8,  And[aa11, bb08], c_19_8] | 
   let s_19_10 = AdderSum  [s_19_9,  And[aa10, bb09], c_19_9] | 
   let s_19_11 = AdderSum  [s_19_10,  And[aa09, bb10], c_19_10] | 
   let s_19_12 = AdderSum  [s_19_11,  And[aa08, bb11], c_19_11] | 
   let s_19_13 = AdderSum  [s_19_12,  And[aa07, bb12], c_19_12] | 
   let s_19_14 = AdderSum  [s_19_13,  And[aa06, bb13], c_19_13] | 
   let s_19_15 = AdderSum  [s_19_14,  And[aa05, bb14], c_19_14] | 
   let s_19_16 = AdderSum  [s_19_15,  And[aa04, bb15], c_19_15] | 
   let s_19_17 = AdderSum  [s_19_16,  And[aa03, bb16], c_19_16] | 
   let s_19_18 = AdderSum  [s_19_17,  And[aa02, bb17], c_19_17] | 
   let s_19_19 = AdderSum  [s_19_18,  And[aa01, bb18], c_19_18] | 
   let s_19_20 = AdderSum  [s_19_19,  And[aa00, bb19], c_19_19] | 
   let c_20_0 = false | 
   let c_20_1 = AdderCarry[s_19_0,  And[aa19, bb00], c_19_0] | 
   let c_20_2 = AdderCarry[s_19_1,  And[aa18, bb01], c_19_1] | 
   let c_20_3 = AdderCarry[s_19_2,  And[aa17, bb02], c_19_2] | 
   let c_20_4 = AdderCarry[s_19_3,  And[aa16, bb03], c_19_3] | 
   let c_20_5 = AdderCarry[s_19_4,  And[aa15, bb04], c_19_4] | 
   let c_20_6 = AdderCarry[s_19_5,  And[aa14, bb05], c_19_5] | 
   let c_20_7 = AdderCarry[s_19_6,  And[aa13, bb06], c_19_6] | 
   let c_20_8 = AdderCarry[s_19_7,  And[aa12, bb07], c_19_7] | 
   let c_20_9 = AdderCarry[s_19_8,  And[aa11, bb08], c_19_8] | 
   let c_20_10 = AdderCarry[s_19_9,  And[aa10, bb09], c_19_9] | 
   let c_20_11 = AdderCarry[s_19_10,  And[aa09, bb10], c_19_10] | 
   let c_20_12 = AdderCarry[s_19_11,  And[aa08, bb11], c_19_11] | 
   let c_20_13 = AdderCarry[s_19_12,  And[aa07, bb12], c_19_12] | 
   let c_20_14 = AdderCarry[s_19_13,  And[aa06, bb13], c_19_13] | 
   let c_20_15 = AdderCarry[s_19_14,  And[aa05, bb14], c_19_14] | 
   let c_20_16 = AdderCarry[s_19_15,  And[aa04, bb15], c_19_15] | 
   let c_20_17 = AdderCarry[s_19_16,  And[aa03, bb16], c_19_16] | 
   let c_20_18 = AdderCarry[s_19_17,  And[aa02, bb17], c_19_17] | 
   let c_20_19 = AdderCarry[s_19_18,  And[aa01, bb18], c_19_18] | 
   let c_20_20 = AdderCarry[s_19_19,  And[aa00, bb19], c_19_19] | 
   let s_20_0 = false | 
   let s_20_1 = AdderSum  [s_20_0,  And[aa20, bb00], c_20_0] | 
   let s_20_2 = AdderSum  [s_20_1,  And[aa19, bb01], c_20_1] | 
   let s_20_3 = AdderSum  [s_20_2,  And[aa18, bb02], c_20_2] | 
   let s_20_4 = AdderSum  [s_20_3,  And[aa17, bb03], c_20_3] | 
   let s_20_5 = AdderSum  [s_20_4,  And[aa16, bb04], c_20_4] | 
   let s_20_6 = AdderSum  [s_20_5,  And[aa15, bb05], c_20_5] | 
   let s_20_7 = AdderSum  [s_20_6,  And[aa14, bb06], c_20_6] | 
   let s_20_8 = AdderSum  [s_20_7,  And[aa13, bb07], c_20_7] | 
   let s_20_9 = AdderSum  [s_20_8,  And[aa12, bb08], c_20_8] | 
   let s_20_10 = AdderSum  [s_20_9,  And[aa11, bb09], c_20_9] | 
   let s_20_11 = AdderSum  [s_20_10,  And[aa10, bb10], c_20_10] | 
   let s_20_12 = AdderSum  [s_20_11,  And[aa09, bb11], c_20_11] | 
   let s_20_13 = AdderSum  [s_20_12,  And[aa08, bb12], c_20_12] | 
   let s_20_14 = AdderSum  [s_20_13,  And[aa07, bb13], c_20_13] | 
   let s_20_15 = AdderSum  [s_20_14,  And[aa06, bb14], c_20_14] | 
   let s_20_16 = AdderSum  [s_20_15,  And[aa05, bb15], c_20_15] | 
   let s_20_17 = AdderSum  [s_20_16,  And[aa04, bb16], c_20_16] | 
   let s_20_18 = AdderSum  [s_20_17,  And[aa03, bb17], c_20_17] | 
   let s_20_19 = AdderSum  [s_20_18,  And[aa02, bb18], c_20_18] | 
   let s_20_20 = AdderSum  [s_20_19,  And[aa01, bb19], c_20_19] | 
   let s_20_21 = AdderSum  [s_20_20,  And[aa00, bb20], c_20_20] | 
   let c_21_0 = false | 
   let c_21_1 = AdderCarry[s_20_0,  And[aa20, bb00], c_20_0] | 
   let c_21_2 = AdderCarry[s_20_1,  And[aa19, bb01], c_20_1] | 
   let c_21_3 = AdderCarry[s_20_2,  And[aa18, bb02], c_20_2] | 
   let c_21_4 = AdderCarry[s_20_3,  And[aa17, bb03], c_20_3] | 
   let c_21_5 = AdderCarry[s_20_4,  And[aa16, bb04], c_20_4] | 
   let c_21_6 = AdderCarry[s_20_5,  And[aa15, bb05], c_20_5] | 
   let c_21_7 = AdderCarry[s_20_6,  And[aa14, bb06], c_20_6] | 
   let c_21_8 = AdderCarry[s_20_7,  And[aa13, bb07], c_20_7] | 
   let c_21_9 = AdderCarry[s_20_8,  And[aa12, bb08], c_20_8] | 
   let c_21_10 = AdderCarry[s_20_9,  And[aa11, bb09], c_20_9] | 
   let c_21_11 = AdderCarry[s_20_10,  And[aa10, bb10], c_20_10] | 
   let c_21_12 = AdderCarry[s_20_11,  And[aa09, bb11], c_20_11] | 
   let c_21_13 = AdderCarry[s_20_12,  And[aa08, bb12], c_20_12] | 
   let c_21_14 = AdderCarry[s_20_13,  And[aa07, bb13], c_20_13] | 
   let c_21_15 = AdderCarry[s_20_14,  And[aa06, bb14], c_20_14] | 
   let c_21_16 = AdderCarry[s_20_15,  And[aa05, bb15], c_20_15] | 
   let c_21_17 = AdderCarry[s_20_16,  And[aa04, bb16], c_20_16] | 
   let c_21_18 = AdderCarry[s_20_17,  And[aa03, bb17], c_20_17] | 
   let c_21_19 = AdderCarry[s_20_18,  And[aa02, bb18], c_20_18] | 
   let c_21_20 = AdderCarry[s_20_19,  And[aa01, bb19], c_20_19] | 
   let c_21_21 = AdderCarry[s_20_20,  And[aa00, bb20], c_20_20] | 
   let s_21_0 = false | 
   let s_21_1 = AdderSum  [s_21_0,  And[aa21, bb00], c_21_0] | 
   let s_21_2 = AdderSum  [s_21_1,  And[aa20, bb01], c_21_1] | 
   let s_21_3 = AdderSum  [s_21_2,  And[aa19, bb02], c_21_2] | 
   let s_21_4 = AdderSum  [s_21_3,  And[aa18, bb03], c_21_3] | 
   let s_21_5 = AdderSum  [s_21_4,  And[aa17, bb04], c_21_4] | 
   let s_21_6 = AdderSum  [s_21_5,  And[aa16, bb05], c_21_5] | 
   let s_21_7 = AdderSum  [s_21_6,  And[aa15, bb06], c_21_6] | 
   let s_21_8 = AdderSum  [s_21_7,  And[aa14, bb07], c_21_7] | 
   let s_21_9 = AdderSum  [s_21_8,  And[aa13, bb08], c_21_8] | 
   let s_21_10 = AdderSum  [s_21_9,  And[aa12, bb09], c_21_9] | 
   let s_21_11 = AdderSum  [s_21_10,  And[aa11, bb10], c_21_10] | 
   let s_21_12 = AdderSum  [s_21_11,  And[aa10, bb11], c_21_11] | 
   let s_21_13 = AdderSum  [s_21_12,  And[aa09, bb12], c_21_12] | 
   let s_21_14 = AdderSum  [s_21_13,  And[aa08, bb13], c_21_13] | 
   let s_21_15 = AdderSum  [s_21_14,  And[aa07, bb14], c_21_14] | 
   let s_21_16 = AdderSum  [s_21_15,  And[aa06, bb15], c_21_15] | 
   let s_21_17 = AdderSum  [s_21_16,  And[aa05, bb16], c_21_16] | 
   let s_21_18 = AdderSum  [s_21_17,  And[aa04, bb17], c_21_17] | 
   let s_21_19 = AdderSum  [s_21_18,  And[aa03, bb18], c_21_18] | 
   let s_21_20 = AdderSum  [s_21_19,  And[aa02, bb19], c_21_19] | 
   let s_21_21 = AdderSum  [s_21_20,  And[aa01, bb20], c_21_20] | 
   let s_21_22 = AdderSum  [s_21_21,  And[aa00, bb21], c_21_21] | 
   let c_22_0 = false | 
   let c_22_1 = AdderCarry[s_21_0,  And[aa21, bb00], c_21_0] | 
   let c_22_2 = AdderCarry[s_21_1,  And[aa20, bb01], c_21_1] | 
   let c_22_3 = AdderCarry[s_21_2,  And[aa19, bb02], c_21_2] | 
   let c_22_4 = AdderCarry[s_21_3,  And[aa18, bb03], c_21_3] | 
   let c_22_5 = AdderCarry[s_21_4,  And[aa17, bb04], c_21_4] | 
   let c_22_6 = AdderCarry[s_21_5,  And[aa16, bb05], c_21_5] | 
   let c_22_7 = AdderCarry[s_21_6,  And[aa15, bb06], c_21_6] | 
   let c_22_8 = AdderCarry[s_21_7,  And[aa14, bb07], c_21_7] | 
   let c_22_9 = AdderCarry[s_21_8,  And[aa13, bb08], c_21_8] | 
   let c_22_10 = AdderCarry[s_21_9,  And[aa12, bb09], c_21_9] | 
   let c_22_11 = AdderCarry[s_21_10,  And[aa11, bb10], c_21_10] | 
   let c_22_12 = AdderCarry[s_21_11,  And[aa10, bb11], c_21_11] | 
   let c_22_13 = AdderCarry[s_21_12,  And[aa09, bb12], c_21_12] | 
   let c_22_14 = AdderCarry[s_21_13,  And[aa08, bb13], c_21_13] | 
   let c_22_15 = AdderCarry[s_21_14,  And[aa07, bb14], c_21_14] | 
   let c_22_16 = AdderCarry[s_21_15,  And[aa06, bb15], c_21_15] | 
   let c_22_17 = AdderCarry[s_21_16,  And[aa05, bb16], c_21_16] | 
   let c_22_18 = AdderCarry[s_21_17,  And[aa04, bb17], c_21_17] | 
   let c_22_19 = AdderCarry[s_21_18,  And[aa03, bb18], c_21_18] | 
   let c_22_20 = AdderCarry[s_21_19,  And[aa02, bb19], c_21_19] | 
   let c_22_21 = AdderCarry[s_21_20,  And[aa01, bb20], c_21_20] | 
   let c_22_22 = AdderCarry[s_21_21,  And[aa00, bb21], c_21_21] | 
   let s_22_0 = false | 
   let s_22_1 = AdderSum  [s_22_0,  And[aa22, bb00], c_22_0] | 
   let s_22_2 = AdderSum  [s_22_1,  And[aa21, bb01], c_22_1] | 
   let s_22_3 = AdderSum  [s_22_2,  And[aa20, bb02], c_22_2] | 
   let s_22_4 = AdderSum  [s_22_3,  And[aa19, bb03], c_22_3] | 
   let s_22_5 = AdderSum  [s_22_4,  And[aa18, bb04], c_22_4] | 
   let s_22_6 = AdderSum  [s_22_5,  And[aa17, bb05], c_22_5] | 
   let s_22_7 = AdderSum  [s_22_6,  And[aa16, bb06], c_22_6] | 
   let s_22_8 = AdderSum  [s_22_7,  And[aa15, bb07], c_22_7] | 
   let s_22_9 = AdderSum  [s_22_8,  And[aa14, bb08], c_22_8] | 
   let s_22_10 = AdderSum  [s_22_9,  And[aa13, bb09], c_22_9] | 
   let s_22_11 = AdderSum  [s_22_10,  And[aa12, bb10], c_22_10] | 
   let s_22_12 = AdderSum  [s_22_11,  And[aa11, bb11], c_22_11] | 
   let s_22_13 = AdderSum  [s_22_12,  And[aa10, bb12], c_22_12] | 
   let s_22_14 = AdderSum  [s_22_13,  And[aa09, bb13], c_22_13] | 
   let s_22_15 = AdderSum  [s_22_14,  And[aa08, bb14], c_22_14] | 
   let s_22_16 = AdderSum  [s_22_15,  And[aa07, bb15], c_22_15] | 
   let s_22_17 = AdderSum  [s_22_16,  And[aa06, bb16], c_22_16] | 
   let s_22_18 = AdderSum  [s_22_17,  And[aa05, bb17], c_22_17] | 
   let s_22_19 = AdderSum  [s_22_18,  And[aa04, bb18], c_22_18] | 
   let s_22_20 = AdderSum  [s_22_19,  And[aa03, bb19], c_22_19] | 
   let s_22_21 = AdderSum  [s_22_20,  And[aa02, bb20], c_22_20] | 
   let s_22_22 = AdderSum  [s_22_21,  And[aa01, bb21], c_22_21] | 
   let s_22_23 = AdderSum  [s_22_22,  And[aa00, bb22], c_22_22] | 
   let c_23_0 = false | 
   let c_23_1 = AdderCarry[s_22_0,  And[aa22, bb00], c_22_0] | 
   let c_23_2 = AdderCarry[s_22_1,  And[aa21, bb01], c_22_1] | 
   let c_23_3 = AdderCarry[s_22_2,  And[aa20, bb02], c_22_2] | 
   let c_23_4 = AdderCarry[s_22_3,  And[aa19, bb03], c_22_3] | 
   let c_23_5 = AdderCarry[s_22_4,  And[aa18, bb04], c_22_4] | 
   let c_23_6 = AdderCarry[s_22_5,  And[aa17, bb05], c_22_5] | 
   let c_23_7 = AdderCarry[s_22_6,  And[aa16, bb06], c_22_6] | 
   let c_23_8 = AdderCarry[s_22_7,  And[aa15, bb07], c_22_7] | 
   let c_23_9 = AdderCarry[s_22_8,  And[aa14, bb08], c_22_8] | 
   let c_23_10 = AdderCarry[s_22_9,  And[aa13, bb09], c_22_9] | 
   let c_23_11 = AdderCarry[s_22_10,  And[aa12, bb10], c_22_10] | 
   let c_23_12 = AdderCarry[s_22_11,  And[aa11, bb11], c_22_11] | 
   let c_23_13 = AdderCarry[s_22_12,  And[aa10, bb12], c_22_12] | 
   let c_23_14 = AdderCarry[s_22_13,  And[aa09, bb13], c_22_13] | 
   let c_23_15 = AdderCarry[s_22_14,  And[aa08, bb14], c_22_14] | 
   let c_23_16 = AdderCarry[s_22_15,  And[aa07, bb15], c_22_15] | 
   let c_23_17 = AdderCarry[s_22_16,  And[aa06, bb16], c_22_16] | 
   let c_23_18 = AdderCarry[s_22_17,  And[aa05, bb17], c_22_17] | 
   let c_23_19 = AdderCarry[s_22_18,  And[aa04, bb18], c_22_18] | 
   let c_23_20 = AdderCarry[s_22_19,  And[aa03, bb19], c_22_19] | 
   let c_23_21 = AdderCarry[s_22_20,  And[aa02, bb20], c_22_20] | 
   let c_23_22 = AdderCarry[s_22_21,  And[aa01, bb21], c_22_21] | 
   let c_23_23 = AdderCarry[s_22_22,  And[aa00, bb22], c_22_22] | 
   let s_23_0 = false | 
   let s_23_1 = AdderSum  [s_23_0,  And[aa23, bb00], c_23_0] | 
   let s_23_2 = AdderSum  [s_23_1,  And[aa22, bb01], c_23_1] | 
   let s_23_3 = AdderSum  [s_23_2,  And[aa21, bb02], c_23_2] | 
   let s_23_4 = AdderSum  [s_23_3,  And[aa20, bb03], c_23_3] | 
   let s_23_5 = AdderSum  [s_23_4,  And[aa19, bb04], c_23_4] | 
   let s_23_6 = AdderSum  [s_23_5,  And[aa18, bb05], c_23_5] | 
   let s_23_7 = AdderSum  [s_23_6,  And[aa17, bb06], c_23_6] | 
   let s_23_8 = AdderSum  [s_23_7,  And[aa16, bb07], c_23_7] | 
   let s_23_9 = AdderSum  [s_23_8,  And[aa15, bb08], c_23_8] | 
   let s_23_10 = AdderSum  [s_23_9,  And[aa14, bb09], c_23_9] | 
   let s_23_11 = AdderSum  [s_23_10,  And[aa13, bb10], c_23_10] | 
   let s_23_12 = AdderSum  [s_23_11,  And[aa12, bb11], c_23_11] | 
   let s_23_13 = AdderSum  [s_23_12,  And[aa11, bb12], c_23_12] | 
   let s_23_14 = AdderSum  [s_23_13,  And[aa10, bb13], c_23_13] | 
   let s_23_15 = AdderSum  [s_23_14,  And[aa09, bb14], c_23_14] | 
   let s_23_16 = AdderSum  [s_23_15,  And[aa08, bb15], c_23_15] | 
   let s_23_17 = AdderSum  [s_23_16,  And[aa07, bb16], c_23_16] | 
   let s_23_18 = AdderSum  [s_23_17,  And[aa06, bb17], c_23_17] | 
   let s_23_19 = AdderSum  [s_23_18,  And[aa05, bb18], c_23_18] | 
   let s_23_20 = AdderSum  [s_23_19,  And[aa04, bb19], c_23_19] | 
   let s_23_21 = AdderSum  [s_23_20,  And[aa03, bb20], c_23_20] | 
   let s_23_22 = AdderSum  [s_23_21,  And[aa02, bb21], c_23_21] | 
   let s_23_23 = AdderSum  [s_23_22,  And[aa01, bb22], c_23_22] | 
   let s_23_24 = AdderSum  [s_23_23,  And[aa00, bb23], c_23_23] | 
   let c_24_0 = false | 
   let c_24_1 = AdderCarry[s_23_0,  And[aa23, bb00], c_23_0] | 
   let c_24_2 = AdderCarry[s_23_1,  And[aa22, bb01], c_23_1] | 
   let c_24_3 = AdderCarry[s_23_2,  And[aa21, bb02], c_23_2] | 
   let c_24_4 = AdderCarry[s_23_3,  And[aa20, bb03], c_23_3] | 
   let c_24_5 = AdderCarry[s_23_4,  And[aa19, bb04], c_23_4] | 
   let c_24_6 = AdderCarry[s_23_5,  And[aa18, bb05], c_23_5] | 
   let c_24_7 = AdderCarry[s_23_6,  And[aa17, bb06], c_23_6] | 
   let c_24_8 = AdderCarry[s_23_7,  And[aa16, bb07], c_23_7] | 
   let c_24_9 = AdderCarry[s_23_8,  And[aa15, bb08], c_23_8] | 
   let c_24_10 = AdderCarry[s_23_9,  And[aa14, bb09], c_23_9] | 
   let c_24_11 = AdderCarry[s_23_10,  And[aa13, bb10], c_23_10] | 
   let c_24_12 = AdderCarry[s_23_11,  And[aa12, bb11], c_23_11] | 
   let c_24_13 = AdderCarry[s_23_12,  And[aa11, bb12], c_23_12] | 
   let c_24_14 = AdderCarry[s_23_13,  And[aa10, bb13], c_23_13] | 
   let c_24_15 = AdderCarry[s_23_14,  And[aa09, bb14], c_23_14] | 
   let c_24_16 = AdderCarry[s_23_15,  And[aa08, bb15], c_23_15] | 
   let c_24_17 = AdderCarry[s_23_16,  And[aa07, bb16], c_23_16] | 
   let c_24_18 = AdderCarry[s_23_17,  And[aa06, bb17], c_23_17] | 
   let c_24_19 = AdderCarry[s_23_18,  And[aa05, bb18], c_23_18] | 
   let c_24_20 = AdderCarry[s_23_19,  And[aa04, bb19], c_23_19] | 
   let c_24_21 = AdderCarry[s_23_20,  And[aa03, bb20], c_23_20] | 
   let c_24_22 = AdderCarry[s_23_21,  And[aa02, bb21], c_23_21] | 
   let c_24_23 = AdderCarry[s_23_22,  And[aa01, bb22], c_23_22] | 
   let c_24_24 = AdderCarry[s_23_23,  And[aa00, bb23], c_23_23] | 
   let s_24_0 = false | 
   let s_24_1 = Xor[s_24_0, c_24_0] | 
   let s_24_2 = AdderSum  [s_24_1,  And[aa23, bb01], c_24_1] | 
   let s_24_3 = AdderSum  [s_24_2,  And[aa22, bb02], c_24_2] | 
   let s_24_4 = AdderSum  [s_24_3,  And[aa21, bb03], c_24_3] | 
   let s_24_5 = AdderSum  [s_24_4,  And[aa20, bb04], c_24_4] | 
   let s_24_6 = AdderSum  [s_24_5,  And[aa19, bb05], c_24_5] | 
   let s_24_7 = AdderSum  [s_24_6,  And[aa18, bb06], c_24_6] | 
   let s_24_8 = AdderSum  [s_24_7,  And[aa17, bb07], c_24_7] | 
   let s_24_9 = AdderSum  [s_24_8,  And[aa16, bb08], c_24_8] | 
   let s_24_10 = AdderSum  [s_24_9,  And[aa15, bb09], c_24_9] | 
   let s_24_11 = AdderSum  [s_24_10,  And[aa14, bb10], c_24_10] | 
   let s_24_12 = AdderSum  [s_24_11,  And[aa13, bb11], c_24_11] | 
   let s_24_13 = AdderSum  [s_24_12,  And[aa12, bb12], c_24_12] | 
   let s_24_14 = AdderSum  [s_24_13,  And[aa11, bb13], c_24_13] | 
   let s_24_15 = AdderSum  [s_24_14,  And[aa10, bb14], c_24_14] | 
   let s_24_16 = AdderSum  [s_24_15,  And[aa09, bb15], c_24_15] | 
   let s_24_17 = AdderSum  [s_24_16,  And[aa08, bb16], c_24_16] | 
   let s_24_18 = AdderSum  [s_24_17,  And[aa07, bb17], c_24_17] | 
   let s_24_19 = AdderSum  [s_24_18,  And[aa06, bb18], c_24_18] | 
   let s_24_20 = AdderSum  [s_24_19,  And[aa05, bb19], c_24_19] | 
   let s_24_21 = AdderSum  [s_24_20,  And[aa04, bb20], c_24_20] | 
   let s_24_22 = AdderSum  [s_24_21,  And[aa03, bb21], c_24_21] | 
   let s_24_23 = AdderSum  [s_24_22,  And[aa02, bb22], c_24_22] | 
   let s_24_24 = AdderSum  [s_24_23,  And[aa01, bb23], c_24_23] | 
   let s_24_25 = Xor[s_24_24, c_24_24] | 
   let c_25_0 = false | 
   let c_25_1 = And[s_24_0, c_24_0] | 
   let c_25_2 = AdderCarry[s_24_1,  And[aa23, bb01], c_24_1] | 
   let c_25_3 = AdderCarry[s_24_2,  And[aa22, bb02], c_24_2] | 
   let c_25_4 = AdderCarry[s_24_3,  And[aa21, bb03], c_24_3] | 
   let c_25_5 = AdderCarry[s_24_4,  And[aa20, bb04], c_24_4] | 
   let c_25_6 = AdderCarry[s_24_5,  And[aa19, bb05], c_24_5] | 
   let c_25_7 = AdderCarry[s_24_6,  And[aa18, bb06], c_24_6] | 
   let c_25_8 = AdderCarry[s_24_7,  And[aa17, bb07], c_24_7] | 
   let c_25_9 = AdderCarry[s_24_8,  And[aa16, bb08], c_24_8] | 
   let c_25_10 = AdderCarry[s_24_9,  And[aa15, bb09], c_24_9] | 
   let c_25_11 = AdderCarry[s_24_10,  And[aa14, bb10], c_24_10] | 
   let c_25_12 = AdderCarry[s_24_11,  And[aa13, bb11], c_24_11] | 
   let c_25_13 = AdderCarry[s_24_12,  And[aa12, bb12], c_24_12] | 
   let c_25_14 = AdderCarry[s_24_13,  And[aa11, bb13], c_24_13] | 
   let c_25_15 = AdderCarry[s_24_14,  And[aa10, bb14], c_24_14] | 
   let c_25_16 = AdderCarry[s_24_15,  And[aa09, bb15], c_24_15] | 
   let c_25_17 = AdderCarry[s_24_16,  And[aa08, bb16], c_24_16] | 
   let c_25_18 = AdderCarry[s_24_17,  And[aa07, bb17], c_24_17] | 
   let c_25_19 = AdderCarry[s_24_18,  And[aa06, bb18], c_24_18] | 
   let c_25_20 = AdderCarry[s_24_19,  And[aa05, bb19], c_24_19] | 
   let c_25_21 = AdderCarry[s_24_20,  And[aa04, bb20], c_24_20] | 
   let c_25_22 = AdderCarry[s_24_21,  And[aa03, bb21], c_24_21] | 
   let c_25_23 = AdderCarry[s_24_22,  And[aa02, bb22], c_24_22] | 
   let c_25_24 = AdderCarry[s_24_23,  And[aa01, bb23], c_24_23] | 
   let c_25_25 = And[s_24_24, c_24_24] | 
   let s_25_0 = false | 
   let s_25_1 = Xor[s_25_0, c_25_0] | 
   let s_25_2 = Xor[s_25_1, c_25_1] | 
   let s_25_3 = AdderSum  [s_25_2,  And[aa23, bb02], c_25_2] | 
   let s_25_4 = AdderSum  [s_25_3,  And[aa22, bb03], c_25_3] | 
   let s_25_5 = AdderSum  [s_25_4,  And[aa21, bb04], c_25_4] | 
   let s_25_6 = AdderSum  [s_25_5,  And[aa20, bb05], c_25_5] | 
   let s_25_7 = AdderSum  [s_25_6,  And[aa19, bb06], c_25_6] | 
   let s_25_8 = AdderSum  [s_25_7,  And[aa18, bb07], c_25_7] | 
   let s_25_9 = AdderSum  [s_25_8,  And[aa17, bb08], c_25_8] | 
   let s_25_10 = AdderSum  [s_25_9,  And[aa16, bb09], c_25_9] | 
   let s_25_11 = AdderSum  [s_25_10,  And[aa15, bb10], c_25_10] | 
   let s_25_12 = AdderSum  [s_25_11,  And[aa14, bb11], c_25_11] | 
   let s_25_13 = AdderSum  [s_25_12,  And[aa13, bb12], c_25_12] | 
   let s_25_14 = AdderSum  [s_25_13,  And[aa12, bb13], c_25_13] | 
   let s_25_15 = AdderSum  [s_25_14,  And[aa11, bb14], c_25_14] | 
   let s_25_16 = AdderSum  [s_25_15,  And[aa10, bb15], c_25_15] | 
   let s_25_17 = AdderSum  [s_25_16,  And[aa09, bb16], c_25_16] | 
   let s_25_18 = AdderSum  [s_25_17,  And[aa08, bb17], c_25_17] | 
   let s_25_19 = AdderSum  [s_25_18,  And[aa07, bb18], c_25_18] | 
   let s_25_20 = AdderSum  [s_25_19,  And[aa06, bb19], c_25_19] | 
   let s_25_21 = AdderSum  [s_25_20,  And[aa05, bb20], c_25_20] | 
   let s_25_22 = AdderSum  [s_25_21,  And[aa04, bb21], c_25_21] | 
   let s_25_23 = AdderSum  [s_25_22,  And[aa03, bb22], c_25_22] | 
   let s_25_24 = AdderSum  [s_25_23,  And[aa02, bb23], c_25_23] | 
   let s_25_25 = Xor[s_25_24, c_25_24] | 
   let s_25_26 = Xor[s_25_25, c_25_25] | 
   let c_26_0 = false | 
   let c_26_1 = And[s_25_0, c_25_0] | 
   let c_26_2 = And[s_25_1, c_25_1] | 
   let c_26_3 = AdderCarry[s_25_2,  And[aa23, bb02], c_25_2] | 
   let c_26_4 = AdderCarry[s_25_3,  And[aa22, bb03], c_25_3] | 
   let c_26_5 = AdderCarry[s_25_4,  And[aa21, bb04], c_25_4] | 
   let c_26_6 = AdderCarry[s_25_5,  And[aa20, bb05], c_25_5] | 
   let c_26_7 = AdderCarry[s_25_6,  And[aa19, bb06], c_25_6] | 
   let c_26_8 = AdderCarry[s_25_7,  And[aa18, bb07], c_25_7] | 
   let c_26_9 = AdderCarry[s_25_8,  And[aa17, bb08], c_25_8] | 
   let c_26_10 = AdderCarry[s_25_9,  And[aa16, bb09], c_25_9] | 
   let c_26_11 = AdderCarry[s_25_10,  And[aa15, bb10], c_25_10] | 
   let c_26_12 = AdderCarry[s_25_11,  And[aa14, bb11], c_25_11] | 
   let c_26_13 = AdderCarry[s_25_12,  And[aa13, bb12], c_25_12] | 
   let c_26_14 = AdderCarry[s_25_13,  And[aa12, bb13], c_25_13] | 
   let c_26_15 = AdderCarry[s_25_14,  And[aa11, bb14], c_25_14] | 
   let c_26_16 = AdderCarry[s_25_15,  And[aa10, bb15], c_25_15] | 
   let c_26_17 = AdderCarry[s_25_16,  And[aa09, bb16], c_25_16] | 
   let c_26_18 = AdderCarry[s_25_17,  And[aa08, bb17], c_25_17] | 
   let c_26_19 = AdderCarry[s_25_18,  And[aa07, bb18], c_25_18] | 
   let c_26_20 = AdderCarry[s_25_19,  And[aa06, bb19], c_25_19] | 
   let c_26_21 = AdderCarry[s_25_20,  And[aa05, bb20], c_25_20] | 
   let c_26_22 = AdderCarry[s_25_21,  And[aa04, bb21], c_25_21] | 
   let c_26_23 = AdderCarry[s_25_22,  And[aa03, bb22], c_25_22] | 
   let c_26_24 = AdderCarry[s_25_23,  And[aa02, bb23], c_25_23] | 
   let c_26_25 = And[s_25_24, c_25_24] | 
   let c_26_26 = And[s_25_25, c_25_25] | 
   let s_26_0 = false | 
   let s_26_1 = Xor[s_26_0, c_26_0] | 
   let s_26_2 = Xor[s_26_1, c_26_1] | 
   let s_26_3 = Xor[s_26_2, c_26_2] | 
   let s_26_4 = AdderSum  [s_26_3,  And[aa23, bb03], c_26_3] | 
   let s_26_5 = AdderSum  [s_26_4,  And[aa22, bb04], c_26_4] | 
   let s_26_6 = AdderSum  [s_26_5,  And[aa21, bb05], c_26_5] | 
   let s_26_7 = AdderSum  [s_26_6,  And[aa20, bb06], c_26_6] | 
   let s_26_8 = AdderSum  [s_26_7,  And[aa19, bb07], c_26_7] | 
   let s_26_9 = AdderSum  [s_26_8,  And[aa18, bb08], c_26_8] | 
   let s_26_10 = AdderSum  [s_26_9,  And[aa17, bb09], c_26_9] | 
   let s_26_11 = AdderSum  [s_26_10,  And[aa16, bb10], c_26_10] | 
   let s_26_12 = AdderSum  [s_26_11,  And[aa15, bb11], c_26_11] | 
   let s_26_13 = AdderSum  [s_26_12,  And[aa14, bb12], c_26_12] | 
   let s_26_14 = AdderSum  [s_26_13,  And[aa13, bb13], c_26_13] | 
   let s_26_15 = AdderSum  [s_26_14,  And[aa12, bb14], c_26_14] | 
   let s_26_16 = AdderSum  [s_26_15,  And[aa11, bb15], c_26_15] | 
   let s_26_17 = AdderSum  [s_26_16,  And[aa10, bb16], c_26_16] | 
   let s_26_18 = AdderSum  [s_26_17,  And[aa09, bb17], c_26_17] | 
   let s_26_19 = AdderSum  [s_26_18,  And[aa08, bb18], c_26_18] | 
   let s_26_20 = AdderSum  [s_26_19,  And[aa07, bb19], c_26_19] | 
   let s_26_21 = AdderSum  [s_26_20,  And[aa06, bb20], c_26_20] | 
   let s_26_22 = AdderSum  [s_26_21,  And[aa05, bb21], c_26_21] | 
   let s_26_23 = AdderSum  [s_26_22,  And[aa04, bb22], c_26_22] | 
   let s_26_24 = AdderSum  [s_26_23,  And[aa03, bb23], c_26_23] | 
   let s_26_25 = Xor[s_26_24, c_26_24] | 
   let s_26_26 = Xor[s_26_25, c_26_25] | 
   let s_26_27 = Xor[s_26_26, c_26_26] | 
   let c_27_0 = false | 
   let c_27_1 = And[s_26_0, c_26_0] | 
   let c_27_2 = And[s_26_1, c_26_1] | 
   let c_27_3 = And[s_26_2, c_26_2] | 
   let c_27_4 = AdderCarry[s_26_3,  And[aa23, bb03], c_26_3] | 
   let c_27_5 = AdderCarry[s_26_4,  And[aa22, bb04], c_26_4] | 
   let c_27_6 = AdderCarry[s_26_5,  And[aa21, bb05], c_26_5] | 
   let c_27_7 = AdderCarry[s_26_6,  And[aa20, bb06], c_26_6] | 
   let c_27_8 = AdderCarry[s_26_7,  And[aa19, bb07], c_26_7] | 
   let c_27_9 = AdderCarry[s_26_8,  And[aa18, bb08], c_26_8] | 
   let c_27_10 = AdderCarry[s_26_9,  And[aa17, bb09], c_26_9] | 
   let c_27_11 = AdderCarry[s_26_10,  And[aa16, bb10], c_26_10] | 
   let c_27_12 = AdderCarry[s_26_11,  And[aa15, bb11], c_26_11] | 
   let c_27_13 = AdderCarry[s_26_12,  And[aa14, bb12], c_26_12] | 
   let c_27_14 = AdderCarry[s_26_13,  And[aa13, bb13], c_26_13] | 
   let c_27_15 = AdderCarry[s_26_14,  And[aa12, bb14], c_26_14] | 
   let c_27_16 = AdderCarry[s_26_15,  And[aa11, bb15], c_26_15] | 
   let c_27_17 = AdderCarry[s_26_16,  And[aa10, bb16], c_26_16] | 
   let c_27_18 = AdderCarry[s_26_17,  And[aa09, bb17], c_26_17] | 
   let c_27_19 = AdderCarry[s_26_18,  And[aa08, bb18], c_26_18] | 
   let c_27_20 = AdderCarry[s_26_19,  And[aa07, bb19], c_26_19] | 
   let c_27_21 = AdderCarry[s_26_20,  And[aa06, bb20], c_26_20] | 
   let c_27_22 = AdderCarry[s_26_21,  And[aa05, bb21], c_26_21] | 
   let c_27_23 = AdderCarry[s_26_22,  And[aa04, bb22], c_26_22] | 
   let c_27_24 = AdderCarry[s_26_23,  And[aa03, bb23], c_26_23] | 
   let c_27_25 = And[s_26_24, c_26_24] | 
   let c_27_26 = And[s_26_25, c_26_25] | 
   let c_27_27 = And[s_26_26, c_26_26] | 
   let s_27_0 = false | 
   let s_27_1 = Xor[s_27_0, c_27_0] | 
   let s_27_2 = Xor[s_27_1, c_27_1] | 
   let s_27_3 = Xor[s_27_2, c_27_2] | 
   let s_27_4 = Xor[s_27_3, c_27_3] | 
   let s_27_5 = AdderSum  [s_27_4,  And[aa23, bb04], c_27_4] | 
   let s_27_6 = AdderSum  [s_27_5,  And[aa22, bb05], c_27_5] | 
   let s_27_7 = AdderSum  [s_27_6,  And[aa21, bb06], c_27_6] | 
   let s_27_8 = AdderSum  [s_27_7,  And[aa20, bb07], c_27_7] | 
   let s_27_9 = AdderSum  [s_27_8,  And[aa19, bb08], c_27_8] | 
   let s_27_10 = AdderSum  [s_27_9,  And[aa18, bb09], c_27_9] | 
   let s_27_11 = AdderSum  [s_27_10,  And[aa17, bb10], c_27_10] | 
   let s_27_12 = AdderSum  [s_27_11,  And[aa16, bb11], c_27_11] | 
   let s_27_13 = AdderSum  [s_27_12,  And[aa15, bb12], c_27_12] | 
   let s_27_14 = AdderSum  [s_27_13,  And[aa14, bb13], c_27_13] | 
   let s_27_15 = AdderSum  [s_27_14,  And[aa13, bb14], c_27_14] | 
   let s_27_16 = AdderSum  [s_27_15,  And[aa12, bb15], c_27_15] | 
   let s_27_17 = AdderSum  [s_27_16,  And[aa11, bb16], c_27_16] | 
   let s_27_18 = AdderSum  [s_27_17,  And[aa10, bb17], c_27_17] | 
   let s_27_19 = AdderSum  [s_27_18,  And[aa09, bb18], c_27_18] | 
   let s_27_20 = AdderSum  [s_27_19,  And[aa08, bb19], c_27_19] | 
   let s_27_21 = AdderSum  [s_27_20,  And[aa07, bb20], c_27_20] | 
   let s_27_22 = AdderSum  [s_27_21,  And[aa06, bb21], c_27_21] | 
   let s_27_23 = AdderSum  [s_27_22,  And[aa05, bb22], c_27_22] | 
   let s_27_24 = AdderSum  [s_27_23,  And[aa04, bb23], c_27_23] | 
   let s_27_25 = Xor[s_27_24, c_27_24] | 
   let s_27_26 = Xor[s_27_25, c_27_25] | 
   let s_27_27 = Xor[s_27_26, c_27_26] | 
   let s_27_28 = Xor[s_27_27, c_27_27] | 
   let c_28_0 = false | 
   let c_28_1 = And[s_27_0, c_27_0] | 
   let c_28_2 = And[s_27_1, c_27_1] | 
   let c_28_3 = And[s_27_2, c_27_2] | 
   let c_28_4 = And[s_27_3, c_27_3] | 
   let c_28_5 = AdderCarry[s_27_4,  And[aa23, bb04], c_27_4] | 
   let c_28_6 = AdderCarry[s_27_5,  And[aa22, bb05], c_27_5] | 
   let c_28_7 = AdderCarry[s_27_6,  And[aa21, bb06], c_27_6] | 
   let c_28_8 = AdderCarry[s_27_7,  And[aa20, bb07], c_27_7] | 
   let c_28_9 = AdderCarry[s_27_8,  And[aa19, bb08], c_27_8] | 
   let c_28_10 = AdderCarry[s_27_9,  And[aa18, bb09], c_27_9] | 
   let c_28_11 = AdderCarry[s_27_10,  And[aa17, bb10], c_27_10] | 
   let c_28_12 = AdderCarry[s_27_11,  And[aa16, bb11], c_27_11] | 
   let c_28_13 = AdderCarry[s_27_12,  And[aa15, bb12], c_27_12] | 
   let c_28_14 = AdderCarry[s_27_13,  And[aa14, bb13], c_27_13] | 
   let c_28_15 = AdderCarry[s_27_14,  And[aa13, bb14], c_27_14] | 
   let c_28_16 = AdderCarry[s_27_15,  And[aa12, bb15], c_27_15] | 
   let c_28_17 = AdderCarry[s_27_16,  And[aa11, bb16], c_27_16] | 
   let c_28_18 = AdderCarry[s_27_17,  And[aa10, bb17], c_27_17] | 
   let c_28_19 = AdderCarry[s_27_18,  And[aa09, bb18], c_27_18] | 
   let c_28_20 = AdderCarry[s_27_19,  And[aa08, bb19], c_27_19] | 
   let c_28_21 = AdderCarry[s_27_20,  And[aa07, bb20], c_27_20] | 
   let c_28_22 = AdderCarry[s_27_21,  And[aa06, bb21], c_27_21] | 
   let c_28_23 = AdderCarry[s_27_22,  And[aa05, bb22], c_27_22] | 
   let c_28_24 = AdderCarry[s_27_23,  And[aa04, bb23], c_27_23] | 
   let c_28_25 = And[s_27_24, c_27_24] | 
   let c_28_26 = And[s_27_25, c_27_25] | 
   let c_28_27 = And[s_27_26, c_27_26] | 
   let c_28_28 = And[s_27_27, c_27_27] | 
   let s_28_0 = false | 
   let s_28_1 = Xor[s_28_0, c_28_0] | 
   let s_28_2 = Xor[s_28_1, c_28_1] | 
   let s_28_3 = Xor[s_28_2, c_28_2] | 
   let s_28_4 = Xor[s_28_3, c_28_3] | 
   let s_28_5 = Xor[s_28_4, c_28_4] | 
   let s_28_6 = AdderSum  [s_28_5,  And[aa23, bb05], c_28_5] | 
   let s_28_7 = AdderSum  [s_28_6,  And[aa22, bb06], c_28_6] | 
   let s_28_8 = AdderSum  [s_28_7,  And[aa21, bb07], c_28_7] | 
   let s_28_9 = AdderSum  [s_28_8,  And[aa20, bb08], c_28_8] | 
   let s_28_10 = AdderSum  [s_28_9,  And[aa19, bb09], c_28_9] | 
   let s_28_11 = AdderSum  [s_28_10,  And[aa18, bb10], c_28_10] | 
   let s_28_12 = AdderSum  [s_28_11,  And[aa17, bb11], c_28_11] | 
   let s_28_13 = AdderSum  [s_28_12,  And[aa16, bb12], c_28_12] | 
   let s_28_14 = AdderSum  [s_28_13,  And[aa15, bb13], c_28_13] | 
   let s_28_15 = AdderSum  [s_28_14,  And[aa14, bb14], c_28_14] | 
   let s_28_16 = AdderSum  [s_28_15,  And[aa13, bb15], c_28_15] | 
   let s_28_17 = AdderSum  [s_28_16,  And[aa12, bb16], c_28_16] | 
   let s_28_18 = AdderSum  [s_28_17,  And[aa11, bb17], c_28_17] | 
   let s_28_19 = AdderSum  [s_28_18,  And[aa10, bb18], c_28_18] | 
   let s_28_20 = AdderSum  [s_28_19,  And[aa09, bb19], c_28_19] | 
   let s_28_21 = AdderSum  [s_28_20,  And[aa08, bb20], c_28_20] | 
   let s_28_22 = AdderSum  [s_28_21,  And[aa07, bb21], c_28_21] | 
   let s_28_23 = AdderSum  [s_28_22,  And[aa06, bb22], c_28_22] | 
   let s_28_24 = AdderSum  [s_28_23,  And[aa05, bb23], c_28_23] | 
   let s_28_25 = Xor[s_28_24, c_28_24] | 
   let s_28_26 = Xor[s_28_25, c_28_25] | 
   let s_28_27 = Xor[s_28_26, c_28_26] | 
   let s_28_28 = Xor[s_28_27, c_28_27] | 
   let s_28_29 = Xor[s_28_28, c_28_28] | 
   let c_29_0 = false | 
   let c_29_1 = And[s_28_0, c_28_0] | 
   let c_29_2 = And[s_28_1, c_28_1] | 
   let c_29_3 = And[s_28_2, c_28_2] | 
   let c_29_4 = And[s_28_3, c_28_3] | 
   let c_29_5 = And[s_28_4, c_28_4] | 
   let c_29_6 = AdderCarry[s_28_5,  And[aa23, bb05], c_28_5] | 
   let c_29_7 = AdderCarry[s_28_6,  And[aa22, bb06], c_28_6] | 
   let c_29_8 = AdderCarry[s_28_7,  And[aa21, bb07], c_28_7] | 
   let c_29_9 = AdderCarry[s_28_8,  And[aa20, bb08], c_28_8] | 
   let c_29_10 = AdderCarry[s_28_9,  And[aa19, bb09], c_28_9] | 
   let c_29_11 = AdderCarry[s_28_10,  And[aa18, bb10], c_28_10] | 
   let c_29_12 = AdderCarry[s_28_11,  And[aa17, bb11], c_28_11] | 
   let c_29_13 = AdderCarry[s_28_12,  And[aa16, bb12], c_28_12] | 
   let c_29_14 = AdderCarry[s_28_13,  And[aa15, bb13], c_28_13] | 
   let c_29_15 = AdderCarry[s_28_14,  And[aa14, bb14], c_28_14] | 
   let c_29_16 = AdderCarry[s_28_15,  And[aa13, bb15], c_28_15] | 
   let c_29_17 = AdderCarry[s_28_16,  And[aa12, bb16], c_28_16] | 
   let c_29_18 = AdderCarry[s_28_17,  And[aa11, bb17], c_28_17] | 
   let c_29_19 = AdderCarry[s_28_18,  And[aa10, bb18], c_28_18] | 
   let c_29_20 = AdderCarry[s_28_19,  And[aa09, bb19], c_28_19] | 
   let c_29_21 = AdderCarry[s_28_20,  And[aa08, bb20], c_28_20] | 
   let c_29_22 = AdderCarry[s_28_21,  And[aa07, bb21], c_28_21] | 
   let c_29_23 = AdderCarry[s_28_22,  And[aa06, bb22], c_28_22] | 
   let c_29_24 = AdderCarry[s_28_23,  And[aa05, bb23], c_28_23] | 
   let c_29_25 = And[s_28_24, c_28_24] | 
   let c_29_26 = And[s_28_25, c_28_25] | 
   let c_29_27 = And[s_28_26, c_28_26] | 
   let c_29_28 = And[s_28_27, c_28_27] | 
   let c_29_29 = And[s_28_28, c_28_28] | 
   let s_29_0 = false | 
   let s_29_1 = Xor[s_29_0, c_29_0] | 
   let s_29_2 = Xor[s_29_1, c_29_1] | 
   let s_29_3 = Xor[s_29_2, c_29_2] | 
   let s_29_4 = Xor[s_29_3, c_29_3] | 
   let s_29_5 = Xor[s_29_4, c_29_4] | 
   let s_29_6 = Xor[s_29_5, c_29_5] | 
   let s_29_7 = AdderSum  [s_29_6,  And[aa23, bb06], c_29_6] | 
   let s_29_8 = AdderSum  [s_29_7,  And[aa22, bb07], c_29_7] | 
   let s_29_9 = AdderSum  [s_29_8,  And[aa21, bb08], c_29_8] | 
   let s_29_10 = AdderSum  [s_29_9,  And[aa20, bb09], c_29_9] | 
   let s_29_11 = AdderSum  [s_29_10,  And[aa19, bb10], c_29_10] | 
   let s_29_12 = AdderSum  [s_29_11,  And[aa18, bb11], c_29_11] | 
   let s_29_13 = AdderSum  [s_29_12,  And[aa17, bb12], c_29_12] | 
   let s_29_14 = AdderSum  [s_29_13,  And[aa16, bb13], c_29_13] | 
   let s_29_15 = AdderSum  [s_29_14,  And[aa15, bb14], c_29_14] | 
   let s_29_16 = AdderSum  [s_29_15,  And[aa14, bb15], c_29_15] | 
   let s_29_17 = AdderSum  [s_29_16,  And[aa13, bb16], c_29_16] | 
   let s_29_18 = AdderSum  [s_29_17,  And[aa12, bb17], c_29_17] | 
   let s_29_19 = AdderSum  [s_29_18,  And[aa11, bb18], c_29_18] | 
   let s_29_20 = AdderSum  [s_29_19,  And[aa10, bb19], c_29_19] | 
   let s_29_21 = AdderSum  [s_29_20,  And[aa09, bb20], c_29_20] | 
   let s_29_22 = AdderSum  [s_29_21,  And[aa08, bb21], c_29_21] | 
   let s_29_23 = AdderSum  [s_29_22,  And[aa07, bb22], c_29_22] | 
   let s_29_24 = AdderSum  [s_29_23,  And[aa06, bb23], c_29_23] | 
   let s_29_25 = Xor[s_29_24, c_29_24] | 
   let s_29_26 = Xor[s_29_25, c_29_25] | 
   let s_29_27 = Xor[s_29_26, c_29_26] | 
   let s_29_28 = Xor[s_29_27, c_29_27] | 
   let s_29_29 = Xor[s_29_28, c_29_28] | 
   let s_29_30 = Xor[s_29_29, c_29_29] | 
   let c_30_0 = false | 
   let c_30_1 = And[s_29_0, c_29_0] | 
   let c_30_2 = And[s_29_1, c_29_1] | 
   let c_30_3 = And[s_29_2, c_29_2] | 
   let c_30_4 = And[s_29_3, c_29_3] | 
   let c_30_5 = And[s_29_4, c_29_4] | 
   let c_30_6 = And[s_29_5, c_29_5] | 
   let c_30_7 = AdderCarry[s_29_6,  And[aa23, bb06], c_29_6] | 
   let c_30_8 = AdderCarry[s_29_7,  And[aa22, bb07], c_29_7] | 
   let c_30_9 = AdderCarry[s_29_8,  And[aa21, bb08], c_29_8] | 
   let c_30_10 = AdderCarry[s_29_9,  And[aa20, bb09], c_29_9] | 
   let c_30_11 = AdderCarry[s_29_10,  And[aa19, bb10], c_29_10] | 
   let c_30_12 = AdderCarry[s_29_11,  And[aa18, bb11], c_29_11] | 
   let c_30_13 = AdderCarry[s_29_12,  And[aa17, bb12], c_29_12] | 
   let c_30_14 = AdderCarry[s_29_13,  And[aa16, bb13], c_29_13] | 
   let c_30_15 = AdderCarry[s_29_14,  And[aa15, bb14], c_29_14] | 
   let c_30_16 = AdderCarry[s_29_15,  And[aa14, bb15], c_29_15] | 
   let c_30_17 = AdderCarry[s_29_16,  And[aa13, bb16], c_29_16] | 
   let c_30_18 = AdderCarry[s_29_17,  And[aa12, bb17], c_29_17] | 
   let c_30_19 = AdderCarry[s_29_18,  And[aa11, bb18], c_29_18] | 
   let c_30_20 = AdderCarry[s_29_19,  And[aa10, bb19], c_29_19] | 
   let c_30_21 = AdderCarry[s_29_20,  And[aa09, bb20], c_29_20] | 
   let c_30_22 = AdderCarry[s_29_21,  And[aa08, bb21], c_29_21] | 
   let c_30_23 = AdderCarry[s_29_22,  And[aa07, bb22], c_29_22] | 
   let c_30_24 = AdderCarry[s_29_23,  And[aa06, bb23], c_29_23] | 
   let c_30_25 = And[s_29_24, c_29_24] | 
   let c_30_26 = And[s_29_25, c_29_25] | 
   let c_30_27 = And[s_29_26, c_29_26] | 
   let c_30_28 = And[s_29_27, c_29_27] | 
   let c_30_29 = And[s_29_28, c_29_28] | 
   let c_30_30 = And[s_29_29, c_29_29] | 
   let s_30_0 = false | 
   let s_30_1 = Xor[s_30_0, c_30_0] | 
   let s_30_2 = Xor[s_30_1, c_30_1] | 
   let s_30_3 = Xor[s_30_2, c_30_2] | 
   let s_30_4 = Xor[s_30_3, c_30_3] | 
   let s_30_5 = Xor[s_30_4, c_30_4] | 
   let s_30_6 = Xor[s_30_5, c_30_5] | 
   let s_30_7 = Xor[s_30_6, c_30_6] | 
   let s_30_8 = AdderSum  [s_30_7,  And[aa23, bb07], c_30_7] | 
   let s_30_9 = AdderSum  [s_30_8,  And[aa22, bb08], c_30_8] | 
   let s_30_10 = AdderSum  [s_30_9,  And[aa21, bb09], c_30_9] | 
   let s_30_11 = AdderSum  [s_30_10,  And[aa20, bb10], c_30_10] | 
   let s_30_12 = AdderSum  [s_30_11,  And[aa19, bb11], c_30_11] | 
   let s_30_13 = AdderSum  [s_30_12,  And[aa18, bb12], c_30_12] | 
   let s_30_14 = AdderSum  [s_30_13,  And[aa17, bb13], c_30_13] | 
   let s_30_15 = AdderSum  [s_30_14,  And[aa16, bb14], c_30_14] | 
   let s_30_16 = AdderSum  [s_30_15,  And[aa15, bb15], c_30_15] | 
   let s_30_17 = AdderSum  [s_30_16,  And[aa14, bb16], c_30_16] | 
   let s_30_18 = AdderSum  [s_30_17,  And[aa13, bb17], c_30_17] | 
   let s_30_19 = AdderSum  [s_30_18,  And[aa12, bb18], c_30_18] | 
   let s_30_20 = AdderSum  [s_30_19,  And[aa11, bb19], c_30_19] | 
   let s_30_21 = AdderSum  [s_30_20,  And[aa10, bb20], c_30_20] | 
   let s_30_22 = AdderSum  [s_30_21,  And[aa09, bb21], c_30_21] | 
   let s_30_23 = AdderSum  [s_30_22,  And[aa08, bb22], c_30_22] | 
   let s_30_24 = AdderSum  [s_30_23,  And[aa07, bb23], c_30_23] | 
   let s_30_25 = Xor[s_30_24, c_30_24] | 
   let s_30_26 = Xor[s_30_25, c_30_25] | 
   let s_30_27 = Xor[s_30_26, c_30_26] | 
   let s_30_28 = Xor[s_30_27, c_30_27] | 
   let s_30_29 = Xor[s_30_28, c_30_28] | 
   let s_30_30 = Xor[s_30_29, c_30_29] | 
   let s_30_31 = Xor[s_30_30, c_30_30] | 
   let c_31_0 = false | 
   let c_31_1 = And[s_30_0, c_30_0] | 
   let c_31_2 = And[s_30_1, c_30_1] | 
   let c_31_3 = And[s_30_2, c_30_2] | 
   let c_31_4 = And[s_30_3, c_30_3] | 
   let c_31_5 = And[s_30_4, c_30_4] | 
   let c_31_6 = And[s_30_5, c_30_5] | 
   let c_31_7 = And[s_30_6, c_30_6] | 
   let c_31_8 = AdderCarry[s_30_7,  And[aa23, bb07], c_30_7] | 
   let c_31_9 = AdderCarry[s_30_8,  And[aa22, bb08], c_30_8] | 
   let c_31_10 = AdderCarry[s_30_9,  And[aa21, bb09], c_30_9] | 
   let c_31_11 = AdderCarry[s_30_10,  And[aa20, bb10], c_30_10] | 
   let c_31_12 = AdderCarry[s_30_11,  And[aa19, bb11], c_30_11] | 
   let c_31_13 = AdderCarry[s_30_12,  And[aa18, bb12], c_30_12] | 
   let c_31_14 = AdderCarry[s_30_13,  And[aa17, bb13], c_30_13] | 
   let c_31_15 = AdderCarry[s_30_14,  And[aa16, bb14], c_30_14] | 
   let c_31_16 = AdderCarry[s_30_15,  And[aa15, bb15], c_30_15] | 
   let c_31_17 = AdderCarry[s_30_16,  And[aa14, bb16], c_30_16] | 
   let c_31_18 = AdderCarry[s_30_17,  And[aa13, bb17], c_30_17] | 
   let c_31_19 = AdderCarry[s_30_18,  And[aa12, bb18], c_30_18] | 
   let c_31_20 = AdderCarry[s_30_19,  And[aa11, bb19], c_30_19] | 
   let c_31_21 = AdderCarry[s_30_20,  And[aa10, bb20], c_30_20] | 
   let c_31_22 = AdderCarry[s_30_21,  And[aa09, bb21], c_30_21] | 
   let c_31_23 = AdderCarry[s_30_22,  And[aa08, bb22], c_30_22] | 
   let c_31_24 = AdderCarry[s_30_23,  And[aa07, bb23], c_30_23] | 
   let c_31_25 = And[s_30_24, c_30_24] | 
   let c_31_26 = And[s_30_25, c_30_25] | 
   let c_31_27 = And[s_30_26, c_30_26] | 
   let c_31_28 = And[s_30_27, c_30_27] | 
   let c_31_29 = And[s_30_28, c_30_28] | 
   let c_31_30 = And[s_30_29, c_30_29] | 
   let c_31_31 = And[s_30_30, c_30_30] | 
   let s_31_0 = false | 
   let s_31_1 = Xor[s_31_0, c_31_0] | 
   let s_31_2 = Xor[s_31_1, c_31_1] | 
   let s_31_3 = Xor[s_31_2, c_31_2] | 
   let s_31_4 = Xor[s_31_3, c_31_3] | 
   let s_31_5 = Xor[s_31_4, c_31_4] | 
   let s_31_6 = Xor[s_31_5, c_31_5] | 
   let s_31_7 = Xor[s_31_6, c_31_6] | 
   let s_31_8 = Xor[s_31_7, c_31_7] | 
   let s_31_9 = AdderSum  [s_31_8,  And[aa23, bb08], c_31_8] | 
   let s_31_10 = AdderSum  [s_31_9,  And[aa22, bb09], c_31_9] | 
   let s_31_11 = AdderSum  [s_31_10,  And[aa21, bb10], c_31_10] | 
   let s_31_12 = AdderSum  [s_31_11,  And[aa20, bb11], c_31_11] | 
   let s_31_13 = AdderSum  [s_31_12,  And[aa19, bb12], c_31_12] | 
   let s_31_14 = AdderSum  [s_31_13,  And[aa18, bb13], c_31_13] | 
   let s_31_15 = AdderSum  [s_31_14,  And[aa17, bb14], c_31_14] | 
   let s_31_16 = AdderSum  [s_31_15,  And[aa16, bb15], c_31_15] | 
   let s_31_17 = AdderSum  [s_31_16,  And[aa15, bb16], c_31_16] | 
   let s_31_18 = AdderSum  [s_31_17,  And[aa14, bb17], c_31_17] | 
   let s_31_19 = AdderSum  [s_31_18,  And[aa13, bb18], c_31_18] | 
   let s_31_20 = AdderSum  [s_31_19,  And[aa12, bb19], c_31_19] | 
   let s_31_21 = AdderSum  [s_31_20,  And[aa11, bb20], c_31_20] | 
   let s_31_22 = AdderSum  [s_31_21,  And[aa10, bb21], c_31_21] | 
   let s_31_23 = AdderSum  [s_31_22,  And[aa09, bb22], c_31_22] | 
   let s_31_24 = AdderSum  [s_31_23,  And[aa08, bb23], c_31_23] | 
   let s_31_25 = Xor[s_31_24, c_31_24] | 
   let s_31_26 = Xor[s_31_25, c_31_25] | 
   let s_31_27 = Xor[s_31_26, c_31_26] | 
   let s_31_28 = Xor[s_31_27, c_31_27] | 
   let s_31_29 = Xor[s_31_28, c_31_28] | 
   let s_31_30 = Xor[s_31_29, c_31_29] | 
   let s_31_31 = Xor[s_31_30, c_31_30] | 
   let s_31_32 = Xor[s_31_31, c_31_31] | 
   let c_32_0 = false | 
   let c_32_1 = And[s_31_0, c_31_0] | 
   let c_32_2 = And[s_31_1, c_31_1] | 
   let c_32_3 = And[s_31_2, c_31_2] | 
   let c_32_4 = And[s_31_3, c_31_3] | 
   let c_32_5 = And[s_31_4, c_31_4] | 
   let c_32_6 = And[s_31_5, c_31_5] | 
   let c_32_7 = And[s_31_6, c_31_6] | 
   let c_32_8 = And[s_31_7, c_31_7] | 
   let c_32_9 = AdderCarry[s_31_8,  And[aa23, bb08], c_31_8] | 
   let c_32_10 = AdderCarry[s_31_9,  And[aa22, bb09], c_31_9] | 
   let c_32_11 = AdderCarry[s_31_10,  And[aa21, bb10], c_31_10] | 
   let c_32_12 = AdderCarry[s_31_11,  And[aa20, bb11], c_31_11] | 
   let c_32_13 = AdderCarry[s_31_12,  And[aa19, bb12], c_31_12] | 
   let c_32_14 = AdderCarry[s_31_13,  And[aa18, bb13], c_31_13] | 
   let c_32_15 = AdderCarry[s_31_14,  And[aa17, bb14], c_31_14] | 
   let c_32_16 = AdderCarry[s_31_15,  And[aa16, bb15], c_31_15] | 
   let c_32_17 = AdderCarry[s_31_16,  And[aa15, bb16], c_31_16] | 
   let c_32_18 = AdderCarry[s_31_17,  And[aa14, bb17], c_31_17] | 
   let c_32_19 = AdderCarry[s_31_18,  And[aa13, bb18], c_31_18] | 
   let c_32_20 = AdderCarry[s_31_19,  And[aa12, bb19], c_31_19] | 
   let c_32_21 = AdderCarry[s_31_20,  And[aa11, bb20], c_31_20] | 
   let c_32_22 = AdderCarry[s_31_21,  And[aa10, bb21], c_31_21] | 
   let c_32_23 = AdderCarry[s_31_22,  And[aa09, bb22], c_31_22] | 
   let c_32_24 = AdderCarry[s_31_23,  And[aa08, bb23], c_31_23] | 
   let c_32_25 = And[s_31_24, c_31_24] | 
   let c_32_26 = And[s_31_25, c_31_25] | 
   let c_32_27 = And[s_31_26, c_31_26] | 
   let c_32_28 = And[s_31_27, c_31_27] | 
   let c_32_29 = And[s_31_28, c_31_28] | 
   let c_32_30 = And[s_31_29, c_31_29] | 
   let c_32_31 = And[s_31_30, c_31_30] | 
   let c_32_32 = And[s_31_31, c_31_31] | 
   let s_32_0 = false | 
   let s_32_1 = Xor[s_32_0, c_32_0] | 
   let s_32_2 = Xor[s_32_1, c_32_1] | 
   let s_32_3 = Xor[s_32_2, c_32_2] | 
   let s_32_4 = Xor[s_32_3, c_32_3] | 
   let s_32_5 = Xor[s_32_4, c_32_4] | 
   let s_32_6 = Xor[s_32_5, c_32_5] | 
   let s_32_7 = Xor[s_32_6, c_32_6] | 
   let s_32_8 = Xor[s_32_7, c_32_7] | 
   let s_32_9 = Xor[s_32_8, c_32_8] | 
   let s_32_10 = AdderSum  [s_32_9,  And[aa23, bb09], c_32_9] | 
   let s_32_11 = AdderSum  [s_32_10,  And[aa22, bb10], c_32_10] | 
   let s_32_12 = AdderSum  [s_32_11,  And[aa21, bb11], c_32_11] | 
   let s_32_13 = AdderSum  [s_32_12,  And[aa20, bb12], c_32_12] | 
   let s_32_14 = AdderSum  [s_32_13,  And[aa19, bb13], c_32_13] | 
   let s_32_15 = AdderSum  [s_32_14,  And[aa18, bb14], c_32_14] | 
   let s_32_16 = AdderSum  [s_32_15,  And[aa17, bb15], c_32_15] | 
   let s_32_17 = AdderSum  [s_32_16,  And[aa16, bb16], c_32_16] | 
   let s_32_18 = AdderSum  [s_32_17,  And[aa15, bb17], c_32_17] | 
   let s_32_19 = AdderSum  [s_32_18,  And[aa14, bb18], c_32_18] | 
   let s_32_20 = AdderSum  [s_32_19,  And[aa13, bb19], c_32_19] | 
   let s_32_21 = AdderSum  [s_32_20,  And[aa12, bb20], c_32_20] | 
   let s_32_22 = AdderSum  [s_32_21,  And[aa11, bb21], c_32_21] | 
   let s_32_23 = AdderSum  [s_32_22,  And[aa10, bb22], c_32_22] | 
   let s_32_24 = AdderSum  [s_32_23,  And[aa09, bb23], c_32_23] | 
   let s_32_25 = Xor[s_32_24, c_32_24] | 
   let s_32_26 = Xor[s_32_25, c_32_25] | 
   let s_32_27 = Xor[s_32_26, c_32_26] | 
   let s_32_28 = Xor[s_32_27, c_32_27] | 
   let s_32_29 = Xor[s_32_28, c_32_28] | 
   let s_32_30 = Xor[s_32_29, c_32_29] | 
   let s_32_31 = Xor[s_32_30, c_32_30] | 
   let s_32_32 = Xor[s_32_31, c_32_31] | 
   let s_32_33 = Xor[s_32_32, c_32_32] | 
   let c_33_0 = false | 
   let c_33_1 = And[s_32_0, c_32_0] | 
   let c_33_2 = And[s_32_1, c_32_1] | 
   let c_33_3 = And[s_32_2, c_32_2] | 
   let c_33_4 = And[s_32_3, c_32_3] | 
   let c_33_5 = And[s_32_4, c_32_4] | 
   let c_33_6 = And[s_32_5, c_32_5] | 
   let c_33_7 = And[s_32_6, c_32_6] | 
   let c_33_8 = And[s_32_7, c_32_7] | 
   let c_33_9 = And[s_32_8, c_32_8] | 
   let c_33_10 = AdderCarry[s_32_9,  And[aa23, bb09], c_32_9] | 
   let c_33_11 = AdderCarry[s_32_10,  And[aa22, bb10], c_32_10] | 
   let c_33_12 = AdderCarry[s_32_11,  And[aa21, bb11], c_32_11] | 
   let c_33_13 = AdderCarry[s_32_12,  And[aa20, bb12], c_32_12] | 
   let c_33_14 = AdderCarry[s_32_13,  And[aa19, bb13], c_32_13] | 
   let c_33_15 = AdderCarry[s_32_14,  And[aa18, bb14], c_32_14] | 
   let c_33_16 = AdderCarry[s_32_15,  And[aa17, bb15], c_32_15] | 
   let c_33_17 = AdderCarry[s_32_16,  And[aa16, bb16], c_32_16] | 
   let c_33_18 = AdderCarry[s_32_17,  And[aa15, bb17], c_32_17] | 
   let c_33_19 = AdderCarry[s_32_18,  And[aa14, bb18], c_32_18] | 
   let c_33_20 = AdderCarry[s_32_19,  And[aa13, bb19], c_32_19] | 
   let c_33_21 = AdderCarry[s_32_20,  And[aa12, bb20], c_32_20] | 
   let c_33_22 = AdderCarry[s_32_21,  And[aa11, bb21], c_32_21] | 
   let c_33_23 = AdderCarry[s_32_22,  And[aa10, bb22], c_32_22] | 
   let c_33_24 = AdderCarry[s_32_23,  And[aa09, bb23], c_32_23] | 
   let c_33_25 = And[s_32_24, c_32_24] | 
   let c_33_26 = And[s_32_25, c_32_25] | 
   let c_33_27 = And[s_32_26, c_32_26] | 
   let c_33_28 = And[s_32_27, c_32_27] | 
   let c_33_29 = And[s_32_28, c_32_28] | 
   let c_33_30 = And[s_32_29, c_32_29] | 
   let c_33_31 = And[s_32_30, c_32_30] | 
   let c_33_32 = And[s_32_31, c_32_31] | 
   let c_33_33 = And[s_32_32, c_32_32] | 
   let s_33_0 = false | 
   let s_33_1 = Xor[s_33_0, c_33_0] | 
   let s_33_2 = Xor[s_33_1, c_33_1] | 
   let s_33_3 = Xor[s_33_2, c_33_2] | 
   let s_33_4 = Xor[s_33_3, c_33_3] | 
   let s_33_5 = Xor[s_33_4, c_33_4] | 
   let s_33_6 = Xor[s_33_5, c_33_5] | 
   let s_33_7 = Xor[s_33_6, c_33_6] | 
   let s_33_8 = Xor[s_33_7, c_33_7] | 
   let s_33_9 = Xor[s_33_8, c_33_8] | 
   let s_33_10 = Xor[s_33_9, c_33_9] | 
   let s_33_11 = AdderSum  [s_33_10,  And[aa23, bb10], c_33_10] | 
   let s_33_12 = AdderSum  [s_33_11,  And[aa22, bb11], c_33_11] | 
   let s_33_13 = AdderSum  [s_33_12,  And[aa21, bb12], c_33_12] | 
   let s_33_14 = AdderSum  [s_33_13,  And[aa20, bb13], c_33_13] | 
   let s_33_15 = AdderSum  [s_33_14,  And[aa19, bb14], c_33_14] | 
   let s_33_16 = AdderSum  [s_33_15,  And[aa18, bb15], c_33_15] | 
   let s_33_17 = AdderSum  [s_33_16,  And[aa17, bb16], c_33_16] | 
   let s_33_18 = AdderSum  [s_33_17,  And[aa16, bb17], c_33_17] | 
   let s_33_19 = AdderSum  [s_33_18,  And[aa15, bb18], c_33_18] | 
   let s_33_20 = AdderSum  [s_33_19,  And[aa14, bb19], c_33_19] | 
   let s_33_21 = AdderSum  [s_33_20,  And[aa13, bb20], c_33_20] | 
   let s_33_22 = AdderSum  [s_33_21,  And[aa12, bb21], c_33_21] | 
   let s_33_23 = AdderSum  [s_33_22,  And[aa11, bb22], c_33_22] | 
   let s_33_24 = AdderSum  [s_33_23,  And[aa10, bb23], c_33_23] | 
   let s_33_25 = Xor[s_33_24, c_33_24] | 
   let s_33_26 = Xor[s_33_25, c_33_25] | 
   let s_33_27 = Xor[s_33_26, c_33_26] | 
   let s_33_28 = Xor[s_33_27, c_33_27] | 
   let s_33_29 = Xor[s_33_28, c_33_28] | 
   let s_33_30 = Xor[s_33_29, c_33_29] | 
   let s_33_31 = Xor[s_33_30, c_33_30] | 
   let s_33_32 = Xor[s_33_31, c_33_31] | 
   let s_33_33 = Xor[s_33_32, c_33_32] | 
   let s_33_34 = Xor[s_33_33, c_33_33] | 
   let c_34_0 = false | 
   let c_34_1 = And[s_33_0, c_33_0] | 
   let c_34_2 = And[s_33_1, c_33_1] | 
   let c_34_3 = And[s_33_2, c_33_2] | 
   let c_34_4 = And[s_33_3, c_33_3] | 
   let c_34_5 = And[s_33_4, c_33_4] | 
   let c_34_6 = And[s_33_5, c_33_5] | 
   let c_34_7 = And[s_33_6, c_33_6] | 
   let c_34_8 = And[s_33_7, c_33_7] | 
   let c_34_9 = And[s_33_8, c_33_8] | 
   let c_34_10 = And[s_33_9, c_33_9] | 
   let c_34_11 = AdderCarry[s_33_10,  And[aa23, bb10], c_33_10] | 
   let c_34_12 = AdderCarry[s_33_11,  And[aa22, bb11], c_33_11] | 
   let c_34_13 = AdderCarry[s_33_12,  And[aa21, bb12], c_33_12] | 
   let c_34_14 = AdderCarry[s_33_13,  And[aa20, bb13], c_33_13] | 
   let c_34_15 = AdderCarry[s_33_14,  And[aa19, bb14], c_33_14] | 
   let c_34_16 = AdderCarry[s_33_15,  And[aa18, bb15], c_33_15] | 
   let c_34_17 = AdderCarry[s_33_16,  And[aa17, bb16], c_33_16] | 
   let c_34_18 = AdderCarry[s_33_17,  And[aa16, bb17], c_33_17] | 
   let c_34_19 = AdderCarry[s_33_18,  And[aa15, bb18], c_33_18] | 
   let c_34_20 = AdderCarry[s_33_19,  And[aa14, bb19], c_33_19] | 
   let c_34_21 = AdderCarry[s_33_20,  And[aa13, bb20], c_33_20] | 
   let c_34_22 = AdderCarry[s_33_21,  And[aa12, bb21], c_33_21] | 
   let c_34_23 = AdderCarry[s_33_22,  And[aa11, bb22], c_33_22] | 
   let c_34_24 = AdderCarry[s_33_23,  And[aa10, bb23], c_33_23] | 
   let c_34_25 = And[s_33_24, c_33_24] | 
   let c_34_26 = And[s_33_25, c_33_25] | 
   let c_34_27 = And[s_33_26, c_33_26] | 
   let c_34_28 = And[s_33_27, c_33_27] | 
   let c_34_29 = And[s_33_28, c_33_28] | 
   let c_34_30 = And[s_33_29, c_33_29] | 
   let c_34_31 = And[s_33_30, c_33_30] | 
   let c_34_32 = And[s_33_31, c_33_31] | 
   let c_34_33 = And[s_33_32, c_33_32] | 
   let c_34_34 = And[s_33_33, c_33_33] | 
   let s_34_0 = false | 
   let s_34_1 = Xor[s_34_0, c_34_0] | 
   let s_34_2 = Xor[s_34_1, c_34_1] | 
   let s_34_3 = Xor[s_34_2, c_34_2] | 
   let s_34_4 = Xor[s_34_3, c_34_3] | 
   let s_34_5 = Xor[s_34_4, c_34_4] | 
   let s_34_6 = Xor[s_34_5, c_34_5] | 
   let s_34_7 = Xor[s_34_6, c_34_6] | 
   let s_34_8 = Xor[s_34_7, c_34_7] | 
   let s_34_9 = Xor[s_34_8, c_34_8] | 
   let s_34_10 = Xor[s_34_9, c_34_9] | 
   let s_34_11 = Xor[s_34_10, c_34_10] | 
   let s_34_12 = AdderSum  [s_34_11,  And[aa23, bb11], c_34_11] | 
   let s_34_13 = AdderSum  [s_34_12,  And[aa22, bb12], c_34_12] | 
   let s_34_14 = AdderSum  [s_34_13,  And[aa21, bb13], c_34_13] | 
   let s_34_15 = AdderSum  [s_34_14,  And[aa20, bb14], c_34_14] | 
   let s_34_16 = AdderSum  [s_34_15,  And[aa19, bb15], c_34_15] | 
   let s_34_17 = AdderSum  [s_34_16,  And[aa18, bb16], c_34_16] | 
   let s_34_18 = AdderSum  [s_34_17,  And[aa17, bb17], c_34_17] | 
   let s_34_19 = AdderSum  [s_34_18,  And[aa16, bb18], c_34_18] | 
   let s_34_20 = AdderSum  [s_34_19,  And[aa15, bb19], c_34_19] | 
   let s_34_21 = AdderSum  [s_34_20,  And[aa14, bb20], c_34_20] | 
   let s_34_22 = AdderSum  [s_34_21,  And[aa13, bb21], c_34_21] | 
   let s_34_23 = AdderSum  [s_34_22,  And[aa12, bb22], c_34_22] | 
   let s_34_24 = AdderSum  [s_34_23,  And[aa11, bb23], c_34_23] | 
   let s_34_25 = Xor[s_34_24, c_34_24] | 
   let s_34_26 = Xor[s_34_25, c_34_25] | 
   let s_34_27 = Xor[s_34_26, c_34_26] | 
   let s_34_28 = Xor[s_34_27, c_34_27] | 
   let s_34_29 = Xor[s_34_28, c_34_28] | 
   let s_34_30 = Xor[s_34_29, c_34_29] | 
   let s_34_31 = Xor[s_34_30, c_34_30] | 
   let s_34_32 = Xor[s_34_31, c_34_31] | 
   let s_34_33 = Xor[s_34_32, c_34_32] | 
   let s_34_34 = Xor[s_34_33, c_34_33] | 
   let s_34_35 = Xor[s_34_34, c_34_34] | 
   let c_35_0 = false | 
   let c_35_1 = And[s_34_0, c_34_0] | 
   let c_35_2 = And[s_34_1, c_34_1] | 
   let c_35_3 = And[s_34_2, c_34_2] | 
   let c_35_4 = And[s_34_3, c_34_3] | 
   let c_35_5 = And[s_34_4, c_34_4] | 
   let c_35_6 = And[s_34_5, c_34_5] | 
   let c_35_7 = And[s_34_6, c_34_6] | 
   let c_35_8 = And[s_34_7, c_34_7] | 
   let c_35_9 = And[s_34_8, c_34_8] | 
   let c_35_10 = And[s_34_9, c_34_9] | 
   let c_35_11 = And[s_34_10, c_34_10] | 
   let c_35_12 = AdderCarry[s_34_11,  And[aa23, bb11], c_34_11] | 
   let c_35_13 = AdderCarry[s_34_12,  And[aa22, bb12], c_34_12] | 
   let c_35_14 = AdderCarry[s_34_13,  And[aa21, bb13], c_34_13] | 
   let c_35_15 = AdderCarry[s_34_14,  And[aa20, bb14], c_34_14] | 
   let c_35_16 = AdderCarry[s_34_15,  And[aa19, bb15], c_34_15] | 
   let c_35_17 = AdderCarry[s_34_16,  And[aa18, bb16], c_34_16] | 
   let c_35_18 = AdderCarry[s_34_17,  And[aa17, bb17], c_34_17] | 
   let c_35_19 = AdderCarry[s_34_18,  And[aa16, bb18], c_34_18] | 
   let c_35_20 = AdderCarry[s_34_19,  And[aa15, bb19], c_34_19] | 
   let c_35_21 = AdderCarry[s_34_20,  And[aa14, bb20], c_34_20] | 
   let c_35_22 = AdderCarry[s_34_21,  And[aa13, bb21], c_34_21] | 
   let c_35_23 = AdderCarry[s_34_22,  And[aa12, bb22], c_34_22] | 
   let c_35_24 = AdderCarry[s_34_23,  And[aa11, bb23], c_34_23] | 
   let c_35_25 = And[s_34_24, c_34_24] | 
   let c_35_26 = And[s_34_25, c_34_25] | 
   let c_35_27 = And[s_34_26, c_34_26] | 
   let c_35_28 = And[s_34_27, c_34_27] | 
   let c_35_29 = And[s_34_28, c_34_28] | 
   let c_35_30 = And[s_34_29, c_34_29] | 
   let c_35_31 = And[s_34_30, c_34_30] | 
   let c_35_32 = And[s_34_31, c_34_31] | 
   let c_35_33 = And[s_34_32, c_34_32] | 
   let c_35_34 = And[s_34_33, c_34_33] | 
   let c_35_35 = And[s_34_34, c_34_34] | 
   let s_35_0 = false | 
   let s_35_1 = Xor[s_35_0, c_35_0] | 
   let s_35_2 = Xor[s_35_1, c_35_1] | 
   let s_35_3 = Xor[s_35_2, c_35_2] | 
   let s_35_4 = Xor[s_35_3, c_35_3] | 
   let s_35_5 = Xor[s_35_4, c_35_4] | 
   let s_35_6 = Xor[s_35_5, c_35_5] | 
   let s_35_7 = Xor[s_35_6, c_35_6] | 
   let s_35_8 = Xor[s_35_7, c_35_7] | 
   let s_35_9 = Xor[s_35_8, c_35_8] | 
   let s_35_10 = Xor[s_35_9, c_35_9] | 
   let s_35_11 = Xor[s_35_10, c_35_10] | 
   let s_35_12 = Xor[s_35_11, c_35_11] | 
   let s_35_13 = AdderSum  [s_35_12,  And[aa23, bb12], c_35_12] | 
   let s_35_14 = AdderSum  [s_35_13,  And[aa22, bb13], c_35_13] | 
   let s_35_15 = AdderSum  [s_35_14,  And[aa21, bb14], c_35_14] | 
   let s_35_16 = AdderSum  [s_35_15,  And[aa20, bb15], c_35_15] | 
   let s_35_17 = AdderSum  [s_35_16,  And[aa19, bb16], c_35_16] | 
   let s_35_18 = AdderSum  [s_35_17,  And[aa18, bb17], c_35_17] | 
   let s_35_19 = AdderSum  [s_35_18,  And[aa17, bb18], c_35_18] | 
   let s_35_20 = AdderSum  [s_35_19,  And[aa16, bb19], c_35_19] | 
   let s_35_21 = AdderSum  [s_35_20,  And[aa15, bb20], c_35_20] | 
   let s_35_22 = AdderSum  [s_35_21,  And[aa14, bb21], c_35_21] | 
   let s_35_23 = AdderSum  [s_35_22,  And[aa13, bb22], c_35_22] | 
   let s_35_24 = AdderSum  [s_35_23,  And[aa12, bb23], c_35_23] | 
   let s_35_25 = Xor[s_35_24, c_35_24] | 
   let s_35_26 = Xor[s_35_25, c_35_25] | 
   let s_35_27 = Xor[s_35_26, c_35_26] | 
   let s_35_28 = Xor[s_35_27, c_35_27] | 
   let s_35_29 = Xor[s_35_28, c_35_28] | 
   let s_35_30 = Xor[s_35_29, c_35_29] | 
   let s_35_31 = Xor[s_35_30, c_35_30] | 
   let s_35_32 = Xor[s_35_31, c_35_31] | 
   let s_35_33 = Xor[s_35_32, c_35_32] | 
   let s_35_34 = Xor[s_35_33, c_35_33] | 
   let s_35_35 = Xor[s_35_34, c_35_34] | 
   let s_35_36 = Xor[s_35_35, c_35_35] | 
   let c_36_0 = false | 
   let c_36_1 = And[s_35_0, c_35_0] | 
   let c_36_2 = And[s_35_1, c_35_1] | 
   let c_36_3 = And[s_35_2, c_35_2] | 
   let c_36_4 = And[s_35_3, c_35_3] | 
   let c_36_5 = And[s_35_4, c_35_4] | 
   let c_36_6 = And[s_35_5, c_35_5] | 
   let c_36_7 = And[s_35_6, c_35_6] | 
   let c_36_8 = And[s_35_7, c_35_7] | 
   let c_36_9 = And[s_35_8, c_35_8] | 
   let c_36_10 = And[s_35_9, c_35_9] | 
   let c_36_11 = And[s_35_10, c_35_10] | 
   let c_36_12 = And[s_35_11, c_35_11] | 
   let c_36_13 = AdderCarry[s_35_12,  And[aa23, bb12], c_35_12] | 
   let c_36_14 = AdderCarry[s_35_13,  And[aa22, bb13], c_35_13] | 
   let c_36_15 = AdderCarry[s_35_14,  And[aa21, bb14], c_35_14] | 
   let c_36_16 = AdderCarry[s_35_15,  And[aa20, bb15], c_35_15] | 
   let c_36_17 = AdderCarry[s_35_16,  And[aa19, bb16], c_35_16] | 
   let c_36_18 = AdderCarry[s_35_17,  And[aa18, bb17], c_35_17] | 
   let c_36_19 = AdderCarry[s_35_18,  And[aa17, bb18], c_35_18] | 
   let c_36_20 = AdderCarry[s_35_19,  And[aa16, bb19], c_35_19] | 
   let c_36_21 = AdderCarry[s_35_20,  And[aa15, bb20], c_35_20] | 
   let c_36_22 = AdderCarry[s_35_21,  And[aa14, bb21], c_35_21] | 
   let c_36_23 = AdderCarry[s_35_22,  And[aa13, bb22], c_35_22] | 
   let c_36_24 = AdderCarry[s_35_23,  And[aa12, bb23], c_35_23] | 
   let c_36_25 = And[s_35_24, c_35_24] | 
   let c_36_26 = And[s_35_25, c_35_25] | 
   let c_36_27 = And[s_35_26, c_35_26] | 
   let c_36_28 = And[s_35_27, c_35_27] | 
   let c_36_29 = And[s_35_28, c_35_28] | 
   let c_36_30 = And[s_35_29, c_35_29] | 
   let c_36_31 = And[s_35_30, c_35_30] | 
   let c_36_32 = And[s_35_31, c_35_31] | 
   let c_36_33 = And[s_35_32, c_35_32] | 
   let c_36_34 = And[s_35_33, c_35_33] | 
   let c_36_35 = And[s_35_34, c_35_34] | 
   let c_36_36 = And[s_35_35, c_35_35] | 
   let s_36_0 = false | 
   let s_36_1 = Xor[s_36_0, c_36_0] | 
   let s_36_2 = Xor[s_36_1, c_36_1] | 
   let s_36_3 = Xor[s_36_2, c_36_2] | 
   let s_36_4 = Xor[s_36_3, c_36_3] | 
   let s_36_5 = Xor[s_36_4, c_36_4] | 
   let s_36_6 = Xor[s_36_5, c_36_5] | 
   let s_36_7 = Xor[s_36_6, c_36_6] | 
   let s_36_8 = Xor[s_36_7, c_36_7] | 
   let s_36_9 = Xor[s_36_8, c_36_8] | 
   let s_36_10 = Xor[s_36_9, c_36_9] | 
   let s_36_11 = Xor[s_36_10, c_36_10] | 
   let s_36_12 = Xor[s_36_11, c_36_11] | 
   let s_36_13 = Xor[s_36_12, c_36_12] | 
   let s_36_14 = AdderSum  [s_36_13,  And[aa23, bb13], c_36_13] | 
   let s_36_15 = AdderSum  [s_36_14,  And[aa22, bb14], c_36_14] | 
   let s_36_16 = AdderSum  [s_36_15,  And[aa21, bb15], c_36_15] | 
   let s_36_17 = AdderSum  [s_36_16,  And[aa20, bb16], c_36_16] | 
   let s_36_18 = AdderSum  [s_36_17,  And[aa19, bb17], c_36_17] | 
   let s_36_19 = AdderSum  [s_36_18,  And[aa18, bb18], c_36_18] | 
   let s_36_20 = AdderSum  [s_36_19,  And[aa17, bb19], c_36_19] | 
   let s_36_21 = AdderSum  [s_36_20,  And[aa16, bb20], c_36_20] | 
   let s_36_22 = AdderSum  [s_36_21,  And[aa15, bb21], c_36_21] | 
   let s_36_23 = AdderSum  [s_36_22,  And[aa14, bb22], c_36_22] | 
   let s_36_24 = AdderSum  [s_36_23,  And[aa13, bb23], c_36_23] | 
   let s_36_25 = Xor[s_36_24, c_36_24] | 
   let s_36_26 = Xor[s_36_25, c_36_25] | 
   let s_36_27 = Xor[s_36_26, c_36_26] | 
   let s_36_28 = Xor[s_36_27, c_36_27] | 
   let s_36_29 = Xor[s_36_28, c_36_28] | 
   let s_36_30 = Xor[s_36_29, c_36_29] | 
   let s_36_31 = Xor[s_36_30, c_36_30] | 
   let s_36_32 = Xor[s_36_31, c_36_31] | 
   let s_36_33 = Xor[s_36_32, c_36_32] | 
   let s_36_34 = Xor[s_36_33, c_36_33] | 
   let s_36_35 = Xor[s_36_34, c_36_34] | 
   let s_36_36 = Xor[s_36_35, c_36_35] | 
   let s_36_37 = Xor[s_36_36, c_36_36] | 
   let c_37_0 = false | 
   let c_37_1 = And[s_36_0, c_36_0] | 
   let c_37_2 = And[s_36_1, c_36_1] | 
   let c_37_3 = And[s_36_2, c_36_2] | 
   let c_37_4 = And[s_36_3, c_36_3] | 
   let c_37_5 = And[s_36_4, c_36_4] | 
   let c_37_6 = And[s_36_5, c_36_5] | 
   let c_37_7 = And[s_36_6, c_36_6] | 
   let c_37_8 = And[s_36_7, c_36_7] | 
   let c_37_9 = And[s_36_8, c_36_8] | 
   let c_37_10 = And[s_36_9, c_36_9] | 
   let c_37_11 = And[s_36_10, c_36_10] | 
   let c_37_12 = And[s_36_11, c_36_11] | 
   let c_37_13 = And[s_36_12, c_36_12] | 
   let c_37_14 = AdderCarry[s_36_13,  And[aa23, bb13], c_36_13] | 
   let c_37_15 = AdderCarry[s_36_14,  And[aa22, bb14], c_36_14] | 
   let c_37_16 = AdderCarry[s_36_15,  And[aa21, bb15], c_36_15] | 
   let c_37_17 = AdderCarry[s_36_16,  And[aa20, bb16], c_36_16] | 
   let c_37_18 = AdderCarry[s_36_17,  And[aa19, bb17], c_36_17] | 
   let c_37_19 = AdderCarry[s_36_18,  And[aa18, bb18], c_36_18] | 
   let c_37_20 = AdderCarry[s_36_19,  And[aa17, bb19], c_36_19] | 
   let c_37_21 = AdderCarry[s_36_20,  And[aa16, bb20], c_36_20] | 
   let c_37_22 = AdderCarry[s_36_21,  And[aa15, bb21], c_36_21] | 
   let c_37_23 = AdderCarry[s_36_22,  And[aa14, bb22], c_36_22] | 
   let c_37_24 = AdderCarry[s_36_23,  And[aa13, bb23], c_36_23] | 
   let c_37_25 = And[s_36_24, c_36_24] | 
   let c_37_26 = And[s_36_25, c_36_25] | 
   let c_37_27 = And[s_36_26, c_36_26] | 
   let c_37_28 = And[s_36_27, c_36_27] | 
   let c_37_29 = And[s_36_28, c_36_28] | 
   let c_37_30 = And[s_36_29, c_36_29] | 
   let c_37_31 = And[s_36_30, c_36_30] | 
   let c_37_32 = And[s_36_31, c_36_31] | 
   let c_37_33 = And[s_36_32, c_36_32] | 
   let c_37_34 = And[s_36_33, c_36_33] | 
   let c_37_35 = And[s_36_34, c_36_34] | 
   let c_37_36 = And[s_36_35, c_36_35] | 
   let c_37_37 = And[s_36_36, c_36_36] | 
   let s_37_0 = false | 
   let s_37_1 = Xor[s_37_0, c_37_0] | 
   let s_37_2 = Xor[s_37_1, c_37_1] | 
   let s_37_3 = Xor[s_37_2, c_37_2] | 
   let s_37_4 = Xor[s_37_3, c_37_3] | 
   let s_37_5 = Xor[s_37_4, c_37_4] | 
   let s_37_6 = Xor[s_37_5, c_37_5] | 
   let s_37_7 = Xor[s_37_6, c_37_6] | 
   let s_37_8 = Xor[s_37_7, c_37_7] | 
   let s_37_9 = Xor[s_37_8, c_37_8] | 
   let s_37_10 = Xor[s_37_9, c_37_9] | 
   let s_37_11 = Xor[s_37_10, c_37_10] | 
   let s_37_12 = Xor[s_37_11, c_37_11] | 
   let s_37_13 = Xor[s_37_12, c_37_12] | 
   let s_37_14 = Xor[s_37_13, c_37_13] | 
   let s_37_15 = AdderSum  [s_37_14,  And[aa23, bb14], c_37_14] | 
   let s_37_16 = AdderSum  [s_37_15,  And[aa22, bb15], c_37_15] | 
   let s_37_17 = AdderSum  [s_37_16,  And[aa21, bb16], c_37_16] | 
   let s_37_18 = AdderSum  [s_37_17,  And[aa20, bb17], c_37_17] | 
   let s_37_19 = AdderSum  [s_37_18,  And[aa19, bb18], c_37_18] | 
   let s_37_20 = AdderSum  [s_37_19,  And[aa18, bb19], c_37_19] | 
   let s_37_21 = AdderSum  [s_37_20,  And[aa17, bb20], c_37_20] | 
   let s_37_22 = AdderSum  [s_37_21,  And[aa16, bb21], c_37_21] | 
   let s_37_23 = AdderSum  [s_37_22,  And[aa15, bb22], c_37_22] | 
   let s_37_24 = AdderSum  [s_37_23,  And[aa14, bb23], c_37_23] | 
   let s_37_25 = Xor[s_37_24, c_37_24] | 
   let s_37_26 = Xor[s_37_25, c_37_25] | 
   let s_37_27 = Xor[s_37_26, c_37_26] | 
   let s_37_28 = Xor[s_37_27, c_37_27] | 
   let s_37_29 = Xor[s_37_28, c_37_28] | 
   let s_37_30 = Xor[s_37_29, c_37_29] | 
   let s_37_31 = Xor[s_37_30, c_37_30] | 
   let s_37_32 = Xor[s_37_31, c_37_31] | 
   let s_37_33 = Xor[s_37_32, c_37_32] | 
   let s_37_34 = Xor[s_37_33, c_37_33] | 
   let s_37_35 = Xor[s_37_34, c_37_34] | 
   let s_37_36 = Xor[s_37_35, c_37_35] | 
   let s_37_37 = Xor[s_37_36, c_37_36] | 
   let s_37_38 = Xor[s_37_37, c_37_37] | 
   let c_38_0 = false | 
   let c_38_1 = And[s_37_0, c_37_0] | 
   let c_38_2 = And[s_37_1, c_37_1] | 
   let c_38_3 = And[s_37_2, c_37_2] | 
   let c_38_4 = And[s_37_3, c_37_3] | 
   let c_38_5 = And[s_37_4, c_37_4] | 
   let c_38_6 = And[s_37_5, c_37_5] | 
   let c_38_7 = And[s_37_6, c_37_6] | 
   let c_38_8 = And[s_37_7, c_37_7] | 
   let c_38_9 = And[s_37_8, c_37_8] | 
   let c_38_10 = And[s_37_9, c_37_9] | 
   let c_38_11 = And[s_37_10, c_37_10] | 
   let c_38_12 = And[s_37_11, c_37_11] | 
   let c_38_13 = And[s_37_12, c_37_12] | 
   let c_38_14 = And[s_37_13, c_37_13] | 
   let c_38_15 = AdderCarry[s_37_14,  And[aa23, bb14], c_37_14] | 
   let c_38_16 = AdderCarry[s_37_15,  And[aa22, bb15], c_37_15] | 
   let c_38_17 = AdderCarry[s_37_16,  And[aa21, bb16], c_37_16] | 
   let c_38_18 = AdderCarry[s_37_17,  And[aa20, bb17], c_37_17] | 
   let c_38_19 = AdderCarry[s_37_18,  And[aa19, bb18], c_37_18] | 
   let c_38_20 = AdderCarry[s_37_19,  And[aa18, bb19], c_37_19] | 
   let c_38_21 = AdderCarry[s_37_20,  And[aa17, bb20], c_37_20] | 
   let c_38_22 = AdderCarry[s_37_21,  And[aa16, bb21], c_37_21] | 
   let c_38_23 = AdderCarry[s_37_22,  And[aa15, bb22], c_37_22] | 
   let c_38_24 = AdderCarry[s_37_23,  And[aa14, bb23], c_37_23] | 
   let c_38_25 = And[s_37_24, c_37_24] | 
   let c_38_26 = And[s_37_25, c_37_25] | 
   let c_38_27 = And[s_37_26, c_37_26] | 
   let c_38_28 = And[s_37_27, c_37_27] | 
   let c_38_29 = And[s_37_28, c_37_28] | 
   let c_38_30 = And[s_37_29, c_37_29] | 
   let c_38_31 = And[s_37_30, c_37_30] | 
   let c_38_32 = And[s_37_31, c_37_31] | 
   let c_38_33 = And[s_37_32, c_37_32] | 
   let c_38_34 = And[s_37_33, c_37_33] | 
   let c_38_35 = And[s_37_34, c_37_34] | 
   let c_38_36 = And[s_37_35, c_37_35] | 
   let c_38_37 = And[s_37_36, c_37_36] | 
   let c_38_38 = And[s_37_37, c_37_37] | 
   let s_38_0 = false | 
   let s_38_1 = Xor[s_38_0, c_38_0] | 
   let s_38_2 = Xor[s_38_1, c_38_1] | 
   let s_38_3 = Xor[s_38_2, c_38_2] | 
   let s_38_4 = Xor[s_38_3, c_38_3] | 
   let s_38_5 = Xor[s_38_4, c_38_4] | 
   let s_38_6 = Xor[s_38_5, c_38_5] | 
   let s_38_7 = Xor[s_38_6, c_38_6] | 
   let s_38_8 = Xor[s_38_7, c_38_7] | 
   let s_38_9 = Xor[s_38_8, c_38_8] | 
   let s_38_10 = Xor[s_38_9, c_38_9] | 
   let s_38_11 = Xor[s_38_10, c_38_10] | 
   let s_38_12 = Xor[s_38_11, c_38_11] | 
   let s_38_13 = Xor[s_38_12, c_38_12] | 
   let s_38_14 = Xor[s_38_13, c_38_13] | 
   let s_38_15 = Xor[s_38_14, c_38_14] | 
   let s_38_16 = AdderSum  [s_38_15,  And[aa23, bb15], c_38_15] | 
   let s_38_17 = AdderSum  [s_38_16,  And[aa22, bb16], c_38_16] | 
   let s_38_18 = AdderSum  [s_38_17,  And[aa21, bb17], c_38_17] | 
   let s_38_19 = AdderSum  [s_38_18,  And[aa20, bb18], c_38_18] | 
   let s_38_20 = AdderSum  [s_38_19,  And[aa19, bb19], c_38_19] | 
   let s_38_21 = AdderSum  [s_38_20,  And[aa18, bb20], c_38_20] | 
   let s_38_22 = AdderSum  [s_38_21,  And[aa17, bb21], c_38_21] | 
   let s_38_23 = AdderSum  [s_38_22,  And[aa16, bb22], c_38_22] | 
   let s_38_24 = AdderSum  [s_38_23,  And[aa15, bb23], c_38_23] | 
   let s_38_25 = Xor[s_38_24, c_38_24] | 
   let s_38_26 = Xor[s_38_25, c_38_25] | 
   let s_38_27 = Xor[s_38_26, c_38_26] | 
   let s_38_28 = Xor[s_38_27, c_38_27] | 
   let s_38_29 = Xor[s_38_28, c_38_28] | 
   let s_38_30 = Xor[s_38_29, c_38_29] | 
   let s_38_31 = Xor[s_38_30, c_38_30] | 
   let s_38_32 = Xor[s_38_31, c_38_31] | 
   let s_38_33 = Xor[s_38_32, c_38_32] | 
   let s_38_34 = Xor[s_38_33, c_38_33] | 
   let s_38_35 = Xor[s_38_34, c_38_34] | 
   let s_38_36 = Xor[s_38_35, c_38_35] | 
   let s_38_37 = Xor[s_38_36, c_38_36] | 
   let s_38_38 = Xor[s_38_37, c_38_37] | 
   let s_38_39 = Xor[s_38_38, c_38_38] | 
   let c_39_0 = false | 
   let c_39_1 = And[s_38_0, c_38_0] | 
   let c_39_2 = And[s_38_1, c_38_1] | 
   let c_39_3 = And[s_38_2, c_38_2] | 
   let c_39_4 = And[s_38_3, c_38_3] | 
   let c_39_5 = And[s_38_4, c_38_4] | 
   let c_39_6 = And[s_38_5, c_38_5] | 
   let c_39_7 = And[s_38_6, c_38_6] | 
   let c_39_8 = And[s_38_7, c_38_7] | 
   let c_39_9 = And[s_38_8, c_38_8] | 
   let c_39_10 = And[s_38_9, c_38_9] | 
   let c_39_11 = And[s_38_10, c_38_10] | 
   let c_39_12 = And[s_38_11, c_38_11] | 
   let c_39_13 = And[s_38_12, c_38_12] | 
   let c_39_14 = And[s_38_13, c_38_13] | 
   let c_39_15 = And[s_38_14, c_38_14] | 
   let c_39_16 = AdderCarry[s_38_15,  And[aa23, bb15], c_38_15] | 
   let c_39_17 = AdderCarry[s_38_16,  And[aa22, bb16], c_38_16] | 
   let c_39_18 = AdderCarry[s_38_17,  And[aa21, bb17], c_38_17] | 
   let c_39_19 = AdderCarry[s_38_18,  And[aa20, bb18], c_38_18] | 
   let c_39_20 = AdderCarry[s_38_19,  And[aa19, bb19], c_38_19] | 
   let c_39_21 = AdderCarry[s_38_20,  And[aa18, bb20], c_38_20] | 
   let c_39_22 = AdderCarry[s_38_21,  And[aa17, bb21], c_38_21] | 
   let c_39_23 = AdderCarry[s_38_22,  And[aa16, bb22], c_38_22] | 
   let c_39_24 = AdderCarry[s_38_23,  And[aa15, bb23], c_38_23] | 
   let c_39_25 = And[s_38_24, c_38_24] | 
   let c_39_26 = And[s_38_25, c_38_25] | 
   let c_39_27 = And[s_38_26, c_38_26] | 
   let c_39_28 = And[s_38_27, c_38_27] | 
   let c_39_29 = And[s_38_28, c_38_28] | 
   let c_39_30 = And[s_38_29, c_38_29] | 
   let c_39_31 = And[s_38_30, c_38_30] | 
   let c_39_32 = And[s_38_31, c_38_31] | 
   let c_39_33 = And[s_38_32, c_38_32] | 
   let c_39_34 = And[s_38_33, c_38_33] | 
   let c_39_35 = And[s_38_34, c_38_34] | 
   let c_39_36 = And[s_38_35, c_38_35] | 
   let c_39_37 = And[s_38_36, c_38_36] | 
   let c_39_38 = And[s_38_37, c_38_37] | 
   let c_39_39 = And[s_38_38, c_38_38] | 
   let s_39_0 = false | 
   let s_39_1 = Xor[s_39_0, c_39_0] | 
   let s_39_2 = Xor[s_39_1, c_39_1] | 
   let s_39_3 = Xor[s_39_2, c_39_2] | 
   let s_39_4 = Xor[s_39_3, c_39_3] | 
   let s_39_5 = Xor[s_39_4, c_39_4] | 
   let s_39_6 = Xor[s_39_5, c_39_5] | 
   let s_39_7 = Xor[s_39_6, c_39_6] | 
   let s_39_8 = Xor[s_39_7, c_39_7] | 
   let s_39_9 = Xor[s_39_8, c_39_8] | 
   let s_39_10 = Xor[s_39_9, c_39_9] | 
   let s_39_11 = Xor[s_39_10, c_39_10] | 
   let s_39_12 = Xor[s_39_11, c_39_11] | 
   let s_39_13 = Xor[s_39_12, c_39_12] | 
   let s_39_14 = Xor[s_39_13, c_39_13] | 
   let s_39_15 = Xor[s_39_14, c_39_14] | 
   let s_39_16 = Xor[s_39_15, c_39_15] | 
   let s_39_17 = AdderSum  [s_39_16,  And[aa23, bb16], c_39_16] | 
   let s_39_18 = AdderSum  [s_39_17,  And[aa22, bb17], c_39_17] | 
   let s_39_19 = AdderSum  [s_39_18,  And[aa21, bb18], c_39_18] | 
   let s_39_20 = AdderSum  [s_39_19,  And[aa20, bb19], c_39_19] | 
   let s_39_21 = AdderSum  [s_39_20,  And[aa19, bb20], c_39_20] | 
   let s_39_22 = AdderSum  [s_39_21,  And[aa18, bb21], c_39_21] | 
   let s_39_23 = AdderSum  [s_39_22,  And[aa17, bb22], c_39_22] | 
   let s_39_24 = AdderSum  [s_39_23,  And[aa16, bb23], c_39_23] | 
   let s_39_25 = Xor[s_39_24, c_39_24] | 
   let s_39_26 = Xor[s_39_25, c_39_25] | 
   let s_39_27 = Xor[s_39_26, c_39_26] | 
   let s_39_28 = Xor[s_39_27, c_39_27] | 
   let s_39_29 = Xor[s_39_28, c_39_28] | 
   let s_39_30 = Xor[s_39_29, c_39_29] | 
   let s_39_31 = Xor[s_39_30, c_39_30] | 
   let s_39_32 = Xor[s_39_31, c_39_31] | 
   let s_39_33 = Xor[s_39_32, c_39_32] | 
   let s_39_34 = Xor[s_39_33, c_39_33] | 
   let s_39_35 = Xor[s_39_34, c_39_34] | 
   let s_39_36 = Xor[s_39_35, c_39_35] | 
   let s_39_37 = Xor[s_39_36, c_39_36] | 
   let s_39_38 = Xor[s_39_37, c_39_37] | 
   let s_39_39 = Xor[s_39_38, c_39_38] | 
   let s_39_40 = Xor[s_39_39, c_39_39] | 
   let c_40_0 = false | 
   let c_40_1 = And[s_39_0, c_39_0] | 
   let c_40_2 = And[s_39_1, c_39_1] | 
   let c_40_3 = And[s_39_2, c_39_2] | 
   let c_40_4 = And[s_39_3, c_39_3] | 
   let c_40_5 = And[s_39_4, c_39_4] | 
   let c_40_6 = And[s_39_5, c_39_5] | 
   let c_40_7 = And[s_39_6, c_39_6] | 
   let c_40_8 = And[s_39_7, c_39_7] | 
   let c_40_9 = And[s_39_8, c_39_8] | 
   let c_40_10 = And[s_39_9, c_39_9] | 
   let c_40_11 = And[s_39_10, c_39_10] | 
   let c_40_12 = And[s_39_11, c_39_11] | 
   let c_40_13 = And[s_39_12, c_39_12] | 
   let c_40_14 = And[s_39_13, c_39_13] | 
   let c_40_15 = And[s_39_14, c_39_14] | 
   let c_40_16 = And[s_39_15, c_39_15] | 
   let c_40_17 = AdderCarry[s_39_16,  And[aa23, bb16], c_39_16] | 
   let c_40_18 = AdderCarry[s_39_17,  And[aa22, bb17], c_39_17] | 
   let c_40_19 = AdderCarry[s_39_18,  And[aa21, bb18], c_39_18] | 
   let c_40_20 = AdderCarry[s_39_19,  And[aa20, bb19], c_39_19] | 
   let c_40_21 = AdderCarry[s_39_20,  And[aa19, bb20], c_39_20] | 
   let c_40_22 = AdderCarry[s_39_21,  And[aa18, bb21], c_39_21] | 
   let c_40_23 = AdderCarry[s_39_22,  And[aa17, bb22], c_39_22] | 
   let c_40_24 = AdderCarry[s_39_23,  And[aa16, bb23], c_39_23] | 
   let c_40_25 = And[s_39_24, c_39_24] | 
   let c_40_26 = And[s_39_25, c_39_25] | 
   let c_40_27 = And[s_39_26, c_39_26] | 
   let c_40_28 = And[s_39_27, c_39_27] | 
   let c_40_29 = And[s_39_28, c_39_28] | 
   let c_40_30 = And[s_39_29, c_39_29] | 
   let c_40_31 = And[s_39_30, c_39_30] | 
   let c_40_32 = And[s_39_31, c_39_31] | 
   let c_40_33 = And[s_39_32, c_39_32] | 
   let c_40_34 = And[s_39_33, c_39_33] | 
   let c_40_35 = And[s_39_34, c_39_34] | 
   let c_40_36 = And[s_39_35, c_39_35] | 
   let c_40_37 = And[s_39_36, c_39_36] | 
   let c_40_38 = And[s_39_37, c_39_37] | 
   let c_40_39 = And[s_39_38, c_39_38] | 
   let c_40_40 = And[s_39_39, c_39_39] | 
   let s_40_0 = false | 
   let s_40_1 = Xor[s_40_0, c_40_0] | 
   let s_40_2 = Xor[s_40_1, c_40_1] | 
   let s_40_3 = Xor[s_40_2, c_40_2] | 
   let s_40_4 = Xor[s_40_3, c_40_3] | 
   let s_40_5 = Xor[s_40_4, c_40_4] | 
   let s_40_6 = Xor[s_40_5, c_40_5] | 
   let s_40_7 = Xor[s_40_6, c_40_6] | 
   let s_40_8 = Xor[s_40_7, c_40_7] | 
   let s_40_9 = Xor[s_40_8, c_40_8] | 
   let s_40_10 = Xor[s_40_9, c_40_9] | 
   let s_40_11 = Xor[s_40_10, c_40_10] | 
   let s_40_12 = Xor[s_40_11, c_40_11] | 
   let s_40_13 = Xor[s_40_12, c_40_12] | 
   let s_40_14 = Xor[s_40_13, c_40_13] | 
   let s_40_15 = Xor[s_40_14, c_40_14] | 
   let s_40_16 = Xor[s_40_15, c_40_15] | 
   let s_40_17 = Xor[s_40_16, c_40_16] | 
   let s_40_18 = AdderSum  [s_40_17,  And[aa23, bb17], c_40_17] | 
   let s_40_19 = AdderSum  [s_40_18,  And[aa22, bb18], c_40_18] | 
   let s_40_20 = AdderSum  [s_40_19,  And[aa21, bb19], c_40_19] | 
   let s_40_21 = AdderSum  [s_40_20,  And[aa20, bb20], c_40_20] | 
   let s_40_22 = AdderSum  [s_40_21,  And[aa19, bb21], c_40_21] | 
   let s_40_23 = AdderSum  [s_40_22,  And[aa18, bb22], c_40_22] | 
   let s_40_24 = AdderSum  [s_40_23,  And[aa17, bb23], c_40_23] | 
   let s_40_25 = Xor[s_40_24, c_40_24] | 
   let s_40_26 = Xor[s_40_25, c_40_25] | 
   let s_40_27 = Xor[s_40_26, c_40_26] | 
   let s_40_28 = Xor[s_40_27, c_40_27] | 
   let s_40_29 = Xor[s_40_28, c_40_28] | 
   let s_40_30 = Xor[s_40_29, c_40_29] | 
   let s_40_31 = Xor[s_40_30, c_40_30] | 
   let s_40_32 = Xor[s_40_31, c_40_31] | 
   let s_40_33 = Xor[s_40_32, c_40_32] | 
   let s_40_34 = Xor[s_40_33, c_40_33] | 
   let s_40_35 = Xor[s_40_34, c_40_34] | 
   let s_40_36 = Xor[s_40_35, c_40_35] | 
   let s_40_37 = Xor[s_40_36, c_40_36] | 
   let s_40_38 = Xor[s_40_37, c_40_37] | 
   let s_40_39 = Xor[s_40_38, c_40_38] | 
   let s_40_40 = Xor[s_40_39, c_40_39] | 
   let s_40_41 = Xor[s_40_40, c_40_40] | 
   let c_41_0 = false | 
   let c_41_1 = And[s_40_0, c_40_0] | 
   let c_41_2 = And[s_40_1, c_40_1] | 
   let c_41_3 = And[s_40_2, c_40_2] | 
   let c_41_4 = And[s_40_3, c_40_3] | 
   let c_41_5 = And[s_40_4, c_40_4] | 
   let c_41_6 = And[s_40_5, c_40_5] | 
   let c_41_7 = And[s_40_6, c_40_6] | 
   let c_41_8 = And[s_40_7, c_40_7] | 
   let c_41_9 = And[s_40_8, c_40_8] | 
   let c_41_10 = And[s_40_9, c_40_9] | 
   let c_41_11 = And[s_40_10, c_40_10] | 
   let c_41_12 = And[s_40_11, c_40_11] | 
   let c_41_13 = And[s_40_12, c_40_12] | 
   let c_41_14 = And[s_40_13, c_40_13] | 
   let c_41_15 = And[s_40_14, c_40_14] | 
   let c_41_16 = And[s_40_15, c_40_15] | 
   let c_41_17 = And[s_40_16, c_40_16] | 
   let c_41_18 = AdderCarry[s_40_17,  And[aa23, bb17], c_40_17] | 
   let c_41_19 = AdderCarry[s_40_18,  And[aa22, bb18], c_40_18] | 
   let c_41_20 = AdderCarry[s_40_19,  And[aa21, bb19], c_40_19] | 
   let c_41_21 = AdderCarry[s_40_20,  And[aa20, bb20], c_40_20] | 
   let c_41_22 = AdderCarry[s_40_21,  And[aa19, bb21], c_40_21] | 
   let c_41_23 = AdderCarry[s_40_22,  And[aa18, bb22], c_40_22] | 
   let c_41_24 = AdderCarry[s_40_23,  And[aa17, bb23], c_40_23] | 
   let c_41_25 = And[s_40_24, c_40_24] | 
   let c_41_26 = And[s_40_25, c_40_25] | 
   let c_41_27 = And[s_40_26, c_40_26] | 
   let c_41_28 = And[s_40_27, c_40_27] | 
   let c_41_29 = And[s_40_28, c_40_28] | 
   let c_41_30 = And[s_40_29, c_40_29] | 
   let c_41_31 = And[s_40_30, c_40_30] | 
   let c_41_32 = And[s_40_31, c_40_31] | 
   let c_41_33 = And[s_40_32, c_40_32] | 
   let c_41_34 = And[s_40_33, c_40_33] | 
   let c_41_35 = And[s_40_34, c_40_34] | 
   let c_41_36 = And[s_40_35, c_40_35] | 
   let c_41_37 = And[s_40_36, c_40_36] | 
   let c_41_38 = And[s_40_37, c_40_37] | 
   let c_41_39 = And[s_40_38, c_40_38] | 
   let c_41_40 = And[s_40_39, c_40_39] | 
   let c_41_41 = And[s_40_40, c_40_40] | 
   let s_41_0 = false | 
   let s_41_1 = Xor[s_41_0, c_41_0] | 
   let s_41_2 = Xor[s_41_1, c_41_1] | 
   let s_41_3 = Xor[s_41_2, c_41_2] | 
   let s_41_4 = Xor[s_41_3, c_41_3] | 
   let s_41_5 = Xor[s_41_4, c_41_4] | 
   let s_41_6 = Xor[s_41_5, c_41_5] | 
   let s_41_7 = Xor[s_41_6, c_41_6] | 
   let s_41_8 = Xor[s_41_7, c_41_7] | 
   let s_41_9 = Xor[s_41_8, c_41_8] | 
   let s_41_10 = Xor[s_41_9, c_41_9] | 
   let s_41_11 = Xor[s_41_10, c_41_10] | 
   let s_41_12 = Xor[s_41_11, c_41_11] | 
   let s_41_13 = Xor[s_41_12, c_41_12] | 
   let s_41_14 = Xor[s_41_13, c_41_13] | 
   let s_41_15 = Xor[s_41_14, c_41_14] | 
   let s_41_16 = Xor[s_41_15, c_41_15] | 
   let s_41_17 = Xor[s_41_16, c_41_16] | 
   let s_41_18 = Xor[s_41_17, c_41_17] | 
   let s_41_19 = AdderSum  [s_41_18,  And[aa23, bb18], c_41_18] | 
   let s_41_20 = AdderSum  [s_41_19,  And[aa22, bb19], c_41_19] | 
   let s_41_21 = AdderSum  [s_41_20,  And[aa21, bb20], c_41_20] | 
   let s_41_22 = AdderSum  [s_41_21,  And[aa20, bb21], c_41_21] | 
   let s_41_23 = AdderSum  [s_41_22,  And[aa19, bb22], c_41_22] | 
   let s_41_24 = AdderSum  [s_41_23,  And[aa18, bb23], c_41_23] | 
   let s_41_25 = Xor[s_41_24, c_41_24] | 
   let s_41_26 = Xor[s_41_25, c_41_25] | 
   let s_41_27 = Xor[s_41_26, c_41_26] | 
   let s_41_28 = Xor[s_41_27, c_41_27] | 
   let s_41_29 = Xor[s_41_28, c_41_28] | 
   let s_41_30 = Xor[s_41_29, c_41_29] | 
   let s_41_31 = Xor[s_41_30, c_41_30] | 
   let s_41_32 = Xor[s_41_31, c_41_31] | 
   let s_41_33 = Xor[s_41_32, c_41_32] | 
   let s_41_34 = Xor[s_41_33, c_41_33] | 
   let s_41_35 = Xor[s_41_34, c_41_34] | 
   let s_41_36 = Xor[s_41_35, c_41_35] | 
   let s_41_37 = Xor[s_41_36, c_41_36] | 
   let s_41_38 = Xor[s_41_37, c_41_37] | 
   let s_41_39 = Xor[s_41_38, c_41_38] | 
   let s_41_40 = Xor[s_41_39, c_41_39] | 
   let s_41_41 = Xor[s_41_40, c_41_40] | 
   let s_41_42 = Xor[s_41_41, c_41_41] | 
   let c_42_0 = false | 
   let c_42_1 = And[s_41_0, c_41_0] | 
   let c_42_2 = And[s_41_1, c_41_1] | 
   let c_42_3 = And[s_41_2, c_41_2] | 
   let c_42_4 = And[s_41_3, c_41_3] | 
   let c_42_5 = And[s_41_4, c_41_4] | 
   let c_42_6 = And[s_41_5, c_41_5] | 
   let c_42_7 = And[s_41_6, c_41_6] | 
   let c_42_8 = And[s_41_7, c_41_7] | 
   let c_42_9 = And[s_41_8, c_41_8] | 
   let c_42_10 = And[s_41_9, c_41_9] | 
   let c_42_11 = And[s_41_10, c_41_10] | 
   let c_42_12 = And[s_41_11, c_41_11] | 
   let c_42_13 = And[s_41_12, c_41_12] | 
   let c_42_14 = And[s_41_13, c_41_13] | 
   let c_42_15 = And[s_41_14, c_41_14] | 
   let c_42_16 = And[s_41_15, c_41_15] | 
   let c_42_17 = And[s_41_16, c_41_16] | 
   let c_42_18 = And[s_41_17, c_41_17] | 
   let c_42_19 = AdderCarry[s_41_18,  And[aa23, bb18], c_41_18] | 
   let c_42_20 = AdderCarry[s_41_19,  And[aa22, bb19], c_41_19] | 
   let c_42_21 = AdderCarry[s_41_20,  And[aa21, bb20], c_41_20] | 
   let c_42_22 = AdderCarry[s_41_21,  And[aa20, bb21], c_41_21] | 
   let c_42_23 = AdderCarry[s_41_22,  And[aa19, bb22], c_41_22] | 
   let c_42_24 = AdderCarry[s_41_23,  And[aa18, bb23], c_41_23] | 
   let c_42_25 = And[s_41_24, c_41_24] | 
   let c_42_26 = And[s_41_25, c_41_25] | 
   let c_42_27 = And[s_41_26, c_41_26] | 
   let c_42_28 = And[s_41_27, c_41_27] | 
   let c_42_29 = And[s_41_28, c_41_28] | 
   let c_42_30 = And[s_41_29, c_41_29] | 
   let c_42_31 = And[s_41_30, c_41_30] | 
   let c_42_32 = And[s_41_31, c_41_31] | 
   let c_42_33 = And[s_41_32, c_41_32] | 
   let c_42_34 = And[s_41_33, c_41_33] | 
   let c_42_35 = And[s_41_34, c_41_34] | 
   let c_42_36 = And[s_41_35, c_41_35] | 
   let c_42_37 = And[s_41_36, c_41_36] | 
   let c_42_38 = And[s_41_37, c_41_37] | 
   let c_42_39 = And[s_41_38, c_41_38] | 
   let c_42_40 = And[s_41_39, c_41_39] | 
   let c_42_41 = And[s_41_40, c_41_40] | 
   let c_42_42 = And[s_41_41, c_41_41] | 
   let s_42_0 = false | 
   let s_42_1 = Xor[s_42_0, c_42_0] | 
   let s_42_2 = Xor[s_42_1, c_42_1] | 
   let s_42_3 = Xor[s_42_2, c_42_2] | 
   let s_42_4 = Xor[s_42_3, c_42_3] | 
   let s_42_5 = Xor[s_42_4, c_42_4] | 
   let s_42_6 = Xor[s_42_5, c_42_5] | 
   let s_42_7 = Xor[s_42_6, c_42_6] | 
   let s_42_8 = Xor[s_42_7, c_42_7] | 
   let s_42_9 = Xor[s_42_8, c_42_8] | 
   let s_42_10 = Xor[s_42_9, c_42_9] | 
   let s_42_11 = Xor[s_42_10, c_42_10] | 
   let s_42_12 = Xor[s_42_11, c_42_11] | 
   let s_42_13 = Xor[s_42_12, c_42_12] | 
   let s_42_14 = Xor[s_42_13, c_42_13] | 
   let s_42_15 = Xor[s_42_14, c_42_14] | 
   let s_42_16 = Xor[s_42_15, c_42_15] | 
   let s_42_17 = Xor[s_42_16, c_42_16] | 
   let s_42_18 = Xor[s_42_17, c_42_17] | 
   let s_42_19 = Xor[s_42_18, c_42_18] | 
   let s_42_20 = AdderSum  [s_42_19,  And[aa23, bb19], c_42_19] | 
   let s_42_21 = AdderSum  [s_42_20,  And[aa22, bb20], c_42_20] | 
   let s_42_22 = AdderSum  [s_42_21,  And[aa21, bb21], c_42_21] | 
   let s_42_23 = AdderSum  [s_42_22,  And[aa20, bb22], c_42_22] | 
   let s_42_24 = AdderSum  [s_42_23,  And[aa19, bb23], c_42_23] | 
   let s_42_25 = Xor[s_42_24, c_42_24] | 
   let s_42_26 = Xor[s_42_25, c_42_25] | 
   let s_42_27 = Xor[s_42_26, c_42_26] | 
   let s_42_28 = Xor[s_42_27, c_42_27] | 
   let s_42_29 = Xor[s_42_28, c_42_28] | 
   let s_42_30 = Xor[s_42_29, c_42_29] | 
   let s_42_31 = Xor[s_42_30, c_42_30] | 
   let s_42_32 = Xor[s_42_31, c_42_31] | 
   let s_42_33 = Xor[s_42_32, c_42_32] | 
   let s_42_34 = Xor[s_42_33, c_42_33] | 
   let s_42_35 = Xor[s_42_34, c_42_34] | 
   let s_42_36 = Xor[s_42_35, c_42_35] | 
   let s_42_37 = Xor[s_42_36, c_42_36] | 
   let s_42_38 = Xor[s_42_37, c_42_37] | 
   let s_42_39 = Xor[s_42_38, c_42_38] | 
   let s_42_40 = Xor[s_42_39, c_42_39] | 
   let s_42_41 = Xor[s_42_40, c_42_40] | 
   let s_42_42 = Xor[s_42_41, c_42_41] | 
   let s_42_43 = Xor[s_42_42, c_42_42] | 
   let c_43_0 = false | 
   let c_43_1 = And[s_42_0, c_42_0] | 
   let c_43_2 = And[s_42_1, c_42_1] | 
   let c_43_3 = And[s_42_2, c_42_2] | 
   let c_43_4 = And[s_42_3, c_42_3] | 
   let c_43_5 = And[s_42_4, c_42_4] | 
   let c_43_6 = And[s_42_5, c_42_5] | 
   let c_43_7 = And[s_42_6, c_42_6] | 
   let c_43_8 = And[s_42_7, c_42_7] | 
   let c_43_9 = And[s_42_8, c_42_8] | 
   let c_43_10 = And[s_42_9, c_42_9] | 
   let c_43_11 = And[s_42_10, c_42_10] | 
   let c_43_12 = And[s_42_11, c_42_11] | 
   let c_43_13 = And[s_42_12, c_42_12] | 
   let c_43_14 = And[s_42_13, c_42_13] | 
   let c_43_15 = And[s_42_14, c_42_14] | 
   let c_43_16 = And[s_42_15, c_42_15] | 
   let c_43_17 = And[s_42_16, c_42_16] | 
   let c_43_18 = And[s_42_17, c_42_17] | 
   let c_43_19 = And[s_42_18, c_42_18] | 
   let c_43_20 = AdderCarry[s_42_19,  And[aa23, bb19], c_42_19] | 
   let c_43_21 = AdderCarry[s_42_20,  And[aa22, bb20], c_42_20] | 
   let c_43_22 = AdderCarry[s_42_21,  And[aa21, bb21], c_42_21] | 
   let c_43_23 = AdderCarry[s_42_22,  And[aa20, bb22], c_42_22] | 
   let c_43_24 = AdderCarry[s_42_23,  And[aa19, bb23], c_42_23] | 
   let c_43_25 = And[s_42_24, c_42_24] | 
   let c_43_26 = And[s_42_25, c_42_25] | 
   let c_43_27 = And[s_42_26, c_42_26] | 
   let c_43_28 = And[s_42_27, c_42_27] | 
   let c_43_29 = And[s_42_28, c_42_28] | 
   let c_43_30 = And[s_42_29, c_42_29] | 
   let c_43_31 = And[s_42_30, c_42_30] | 
   let c_43_32 = And[s_42_31, c_42_31] | 
   let c_43_33 = And[s_42_32, c_42_32] | 
   let c_43_34 = And[s_42_33, c_42_33] | 
   let c_43_35 = And[s_42_34, c_42_34] | 
   let c_43_36 = And[s_42_35, c_42_35] | 
   let c_43_37 = And[s_42_36, c_42_36] | 
   let c_43_38 = And[s_42_37, c_42_37] | 
   let c_43_39 = And[s_42_38, c_42_38] | 
   let c_43_40 = And[s_42_39, c_42_39] | 
   let c_43_41 = And[s_42_40, c_42_40] | 
   let c_43_42 = And[s_42_41, c_42_41] | 
   let c_43_43 = And[s_42_42, c_42_42] | 
   let s_43_0 = false | 
   let s_43_1 = Xor[s_43_0, c_43_0] | 
   let s_43_2 = Xor[s_43_1, c_43_1] | 
   let s_43_3 = Xor[s_43_2, c_43_2] | 
   let s_43_4 = Xor[s_43_3, c_43_3] | 
   let s_43_5 = Xor[s_43_4, c_43_4] | 
   let s_43_6 = Xor[s_43_5, c_43_5] | 
   let s_43_7 = Xor[s_43_6, c_43_6] | 
   let s_43_8 = Xor[s_43_7, c_43_7] | 
   let s_43_9 = Xor[s_43_8, c_43_8] | 
   let s_43_10 = Xor[s_43_9, c_43_9] | 
   let s_43_11 = Xor[s_43_10, c_43_10] | 
   let s_43_12 = Xor[s_43_11, c_43_11] | 
   let s_43_13 = Xor[s_43_12, c_43_12] | 
   let s_43_14 = Xor[s_43_13, c_43_13] | 
   let s_43_15 = Xor[s_43_14, c_43_14] | 
   let s_43_16 = Xor[s_43_15, c_43_15] | 
   let s_43_17 = Xor[s_43_16, c_43_16] | 
   let s_43_18 = Xor[s_43_17, c_43_17] | 
   let s_43_19 = Xor[s_43_18, c_43_18] | 
   let s_43_20 = Xor[s_43_19, c_43_19] | 
   let s_43_21 = AdderSum  [s_43_20,  And[aa23, bb20], c_43_20] | 
   let s_43_22 = AdderSum  [s_43_21,  And[aa22, bb21], c_43_21] | 
   let s_43_23 = AdderSum  [s_43_22,  And[aa21, bb22], c_43_22] | 
   let s_43_24 = AdderSum  [s_43_23,  And[aa20, bb23], c_43_23] | 
   let s_43_25 = Xor[s_43_24, c_43_24] | 
   let s_43_26 = Xor[s_43_25, c_43_25] | 
   let s_43_27 = Xor[s_43_26, c_43_26] | 
   let s_43_28 = Xor[s_43_27, c_43_27] | 
   let s_43_29 = Xor[s_43_28, c_43_28] | 
   let s_43_30 = Xor[s_43_29, c_43_29] | 
   let s_43_31 = Xor[s_43_30, c_43_30] | 
   let s_43_32 = Xor[s_43_31, c_43_31] | 
   let s_43_33 = Xor[s_43_32, c_43_32] | 
   let s_43_34 = Xor[s_43_33, c_43_33] | 
   let s_43_35 = Xor[s_43_34, c_43_34] | 
   let s_43_36 = Xor[s_43_35, c_43_35] | 
   let s_43_37 = Xor[s_43_36, c_43_36] | 
   let s_43_38 = Xor[s_43_37, c_43_37] | 
   let s_43_39 = Xor[s_43_38, c_43_38] | 
   let s_43_40 = Xor[s_43_39, c_43_39] | 
   let s_43_41 = Xor[s_43_40, c_43_40] | 
   let s_43_42 = Xor[s_43_41, c_43_41] | 
   let s_43_43 = Xor[s_43_42, c_43_42] | 
   let s_43_44 = Xor[s_43_43, c_43_43] | 
   let c_44_0 = false | 
   let c_44_1 = And[s_43_0, c_43_0] | 
   let c_44_2 = And[s_43_1, c_43_1] | 
   let c_44_3 = And[s_43_2, c_43_2] | 
   let c_44_4 = And[s_43_3, c_43_3] | 
   let c_44_5 = And[s_43_4, c_43_4] | 
   let c_44_6 = And[s_43_5, c_43_5] | 
   let c_44_7 = And[s_43_6, c_43_6] | 
   let c_44_8 = And[s_43_7, c_43_7] | 
   let c_44_9 = And[s_43_8, c_43_8] | 
   let c_44_10 = And[s_43_9, c_43_9] | 
   let c_44_11 = And[s_43_10, c_43_10] | 
   let c_44_12 = And[s_43_11, c_43_11] | 
   let c_44_13 = And[s_43_12, c_43_12] | 
   let c_44_14 = And[s_43_13, c_43_13] | 
   let c_44_15 = And[s_43_14, c_43_14] | 
   let c_44_16 = And[s_43_15, c_43_15] | 
   let c_44_17 = And[s_43_16, c_43_16] | 
   let c_44_18 = And[s_43_17, c_43_17] | 
   let c_44_19 = And[s_43_18, c_43_18] | 
   let c_44_20 = And[s_43_19, c_43_19] | 
   let c_44_21 = AdderCarry[s_43_20,  And[aa23, bb20], c_43_20] | 
   let c_44_22 = AdderCarry[s_43_21,  And[aa22, bb21], c_43_21] | 
   let c_44_23 = AdderCarry[s_43_22,  And[aa21, bb22], c_43_22] | 
   let c_44_24 = AdderCarry[s_43_23,  And[aa20, bb23], c_43_23] | 
   let c_44_25 = And[s_43_24, c_43_24] | 
   let c_44_26 = And[s_43_25, c_43_25] | 
   let c_44_27 = And[s_43_26, c_43_26] | 
   let c_44_28 = And[s_43_27, c_43_27] | 
   let c_44_29 = And[s_43_28, c_43_28] | 
   let c_44_30 = And[s_43_29, c_43_29] | 
   let c_44_31 = And[s_43_30, c_43_30] | 
   let c_44_32 = And[s_43_31, c_43_31] | 
   let c_44_33 = And[s_43_32, c_43_32] | 
   let c_44_34 = And[s_43_33, c_43_33] | 
   let c_44_35 = And[s_43_34, c_43_34] | 
   let c_44_36 = And[s_43_35, c_43_35] | 
   let c_44_37 = And[s_43_36, c_43_36] | 
   let c_44_38 = And[s_43_37, c_43_37] | 
   let c_44_39 = And[s_43_38, c_43_38] | 
   let c_44_40 = And[s_43_39, c_43_39] | 
   let c_44_41 = And[s_43_40, c_43_40] | 
   let c_44_42 = And[s_43_41, c_43_41] | 
   let c_44_43 = And[s_43_42, c_43_42] | 
   let c_44_44 = And[s_43_43, c_43_43] | 
   let s_44_0 = false | 
   let s_44_1 = Xor[s_44_0, c_44_0] | 
   let s_44_2 = Xor[s_44_1, c_44_1] | 
   let s_44_3 = Xor[s_44_2, c_44_2] | 
   let s_44_4 = Xor[s_44_3, c_44_3] | 
   let s_44_5 = Xor[s_44_4, c_44_4] | 
   let s_44_6 = Xor[s_44_5, c_44_5] | 
   let s_44_7 = Xor[s_44_6, c_44_6] | 
   let s_44_8 = Xor[s_44_7, c_44_7] | 
   let s_44_9 = Xor[s_44_8, c_44_8] | 
   let s_44_10 = Xor[s_44_9, c_44_9] | 
   let s_44_11 = Xor[s_44_10, c_44_10] | 
   let s_44_12 = Xor[s_44_11, c_44_11] | 
   let s_44_13 = Xor[s_44_12, c_44_12] | 
   let s_44_14 = Xor[s_44_13, c_44_13] | 
   let s_44_15 = Xor[s_44_14, c_44_14] | 
   let s_44_16 = Xor[s_44_15, c_44_15] | 
   let s_44_17 = Xor[s_44_16, c_44_16] | 
   let s_44_18 = Xor[s_44_17, c_44_17] | 
   let s_44_19 = Xor[s_44_18, c_44_18] | 
   let s_44_20 = Xor[s_44_19, c_44_19] | 
   let s_44_21 = Xor[s_44_20, c_44_20] | 
   let s_44_22 = AdderSum  [s_44_21,  And[aa23, bb21], c_44_21] | 
   let s_44_23 = AdderSum  [s_44_22,  And[aa22, bb22], c_44_22] | 
   let s_44_24 = AdderSum  [s_44_23,  And[aa21, bb23], c_44_23] | 
   let s_44_25 = Xor[s_44_24, c_44_24] | 
   let s_44_26 = Xor[s_44_25, c_44_25] | 
   let s_44_27 = Xor[s_44_26, c_44_26] | 
   let s_44_28 = Xor[s_44_27, c_44_27] | 
   let s_44_29 = Xor[s_44_28, c_44_28] | 
   let s_44_30 = Xor[s_44_29, c_44_29] | 
   let s_44_31 = Xor[s_44_30, c_44_30] | 
   let s_44_32 = Xor[s_44_31, c_44_31] | 
   let s_44_33 = Xor[s_44_32, c_44_32] | 
   let s_44_34 = Xor[s_44_33, c_44_33] | 
   let s_44_35 = Xor[s_44_34, c_44_34] | 
   let s_44_36 = Xor[s_44_35, c_44_35] | 
   let s_44_37 = Xor[s_44_36, c_44_36] | 
   let s_44_38 = Xor[s_44_37, c_44_37] | 
   let s_44_39 = Xor[s_44_38, c_44_38] | 
   let s_44_40 = Xor[s_44_39, c_44_39] | 
   let s_44_41 = Xor[s_44_40, c_44_40] | 
   let s_44_42 = Xor[s_44_41, c_44_41] | 
   let s_44_43 = Xor[s_44_42, c_44_42] | 
   let s_44_44 = Xor[s_44_43, c_44_43] | 
   let s_44_45 = Xor[s_44_44, c_44_44] | 
   let c_45_0 = false | 
   let c_45_1 = And[s_44_0, c_44_0] | 
   let c_45_2 = And[s_44_1, c_44_1] | 
   let c_45_3 = And[s_44_2, c_44_2] | 
   let c_45_4 = And[s_44_3, c_44_3] | 
   let c_45_5 = And[s_44_4, c_44_4] | 
   let c_45_6 = And[s_44_5, c_44_5] | 
   let c_45_7 = And[s_44_6, c_44_6] | 
   let c_45_8 = And[s_44_7, c_44_7] | 
   let c_45_9 = And[s_44_8, c_44_8] | 
   let c_45_10 = And[s_44_9, c_44_9] | 
   let c_45_11 = And[s_44_10, c_44_10] | 
   let c_45_12 = And[s_44_11, c_44_11] | 
   let c_45_13 = And[s_44_12, c_44_12] | 
   let c_45_14 = And[s_44_13, c_44_13] | 
   let c_45_15 = And[s_44_14, c_44_14] | 
   let c_45_16 = And[s_44_15, c_44_15] | 
   let c_45_17 = And[s_44_16, c_44_16] | 
   let c_45_18 = And[s_44_17, c_44_17] | 
   let c_45_19 = And[s_44_18, c_44_18] | 
   let c_45_20 = And[s_44_19, c_44_19] | 
   let c_45_21 = And[s_44_20, c_44_20] | 
   let c_45_22 = AdderCarry[s_44_21,  And[aa23, bb21], c_44_21] | 
   let c_45_23 = AdderCarry[s_44_22,  And[aa22, bb22], c_44_22] | 
   let c_45_24 = AdderCarry[s_44_23,  And[aa21, bb23], c_44_23] | 
   let c_45_25 = And[s_44_24, c_44_24] | 
   let c_45_26 = And[s_44_25, c_44_25] | 
   let c_45_27 = And[s_44_26, c_44_26] | 
   let c_45_28 = And[s_44_27, c_44_27] | 
   let c_45_29 = And[s_44_28, c_44_28] | 
   let c_45_30 = And[s_44_29, c_44_29] | 
   let c_45_31 = And[s_44_30, c_44_30] | 
   let c_45_32 = And[s_44_31, c_44_31] | 
   let c_45_33 = And[s_44_32, c_44_32] | 
   let c_45_34 = And[s_44_33, c_44_33] | 
   let c_45_35 = And[s_44_34, c_44_34] | 
   let c_45_36 = And[s_44_35, c_44_35] | 
   let c_45_37 = And[s_44_36, c_44_36] | 
   let c_45_38 = And[s_44_37, c_44_37] | 
   let c_45_39 = And[s_44_38, c_44_38] | 
   let c_45_40 = And[s_44_39, c_44_39] | 
   let c_45_41 = And[s_44_40, c_44_40] | 
   let c_45_42 = And[s_44_41, c_44_41] | 
   let c_45_43 = And[s_44_42, c_44_42] | 
   let c_45_44 = And[s_44_43, c_44_43] | 
   let c_45_45 = And[s_44_44, c_44_44] | 
   let s_45_0 = false | 
   let s_45_1 = Xor[s_45_0, c_45_0] | 
   let s_45_2 = Xor[s_45_1, c_45_1] | 
   let s_45_3 = Xor[s_45_2, c_45_2] | 
   let s_45_4 = Xor[s_45_3, c_45_3] | 
   let s_45_5 = Xor[s_45_4, c_45_4] | 
   let s_45_6 = Xor[s_45_5, c_45_5] | 
   let s_45_7 = Xor[s_45_6, c_45_6] | 
   let s_45_8 = Xor[s_45_7, c_45_7] | 
   let s_45_9 = Xor[s_45_8, c_45_8] | 
   let s_45_10 = Xor[s_45_9, c_45_9] | 
   let s_45_11 = Xor[s_45_10, c_45_10] | 
   let s_45_12 = Xor[s_45_11, c_45_11] | 
   let s_45_13 = Xor[s_45_12, c_45_12] | 
   let s_45_14 = Xor[s_45_13, c_45_13] | 
   let s_45_15 = Xor[s_45_14, c_45_14] | 
   let s_45_16 = Xor[s_45_15, c_45_15] | 
   let s_45_17 = Xor[s_45_16, c_45_16] | 
   let s_45_18 = Xor[s_45_17, c_45_17] | 
   let s_45_19 = Xor[s_45_18, c_45_18] | 
   let s_45_20 = Xor[s_45_19, c_45_19] | 
   let s_45_21 = Xor[s_45_20, c_45_20] | 
   let s_45_22 = Xor[s_45_21, c_45_21] | 
   let s_45_23 = AdderSum  [s_45_22,  And[aa23, bb22], c_45_22] | 
   let s_45_24 = AdderSum  [s_45_23,  And[aa22, bb23], c_45_23] | 
   let s_45_25 = Xor[s_45_24, c_45_24] | 
   let s_45_26 = Xor[s_45_25, c_45_25] | 
   let s_45_27 = Xor[s_45_26, c_45_26] | 
   let s_45_28 = Xor[s_45_27, c_45_27] | 
   let s_45_29 = Xor[s_45_28, c_45_28] | 
   let s_45_30 = Xor[s_45_29, c_45_29] | 
   let s_45_31 = Xor[s_45_30, c_45_30] | 
   let s_45_32 = Xor[s_45_31, c_45_31] | 
   let s_45_33 = Xor[s_45_32, c_45_32] | 
   let s_45_34 = Xor[s_45_33, c_45_33] | 
   let s_45_35 = Xor[s_45_34, c_45_34] | 
   let s_45_36 = Xor[s_45_35, c_45_35] | 
   let s_45_37 = Xor[s_45_36, c_45_36] | 
   let s_45_38 = Xor[s_45_37, c_45_37] | 
   let s_45_39 = Xor[s_45_38, c_45_38] | 
   let s_45_40 = Xor[s_45_39, c_45_39] | 
   let s_45_41 = Xor[s_45_40, c_45_40] | 
   let s_45_42 = Xor[s_45_41, c_45_41] | 
   let s_45_43 = Xor[s_45_42, c_45_42] | 
   let s_45_44 = Xor[s_45_43, c_45_43] | 
   let s_45_45 = Xor[s_45_44, c_45_44] | 
   let s_45_46 = Xor[s_45_45, c_45_45] | 
   let c_46_0 = false | 
   let c_46_1 = And[s_45_0, c_45_0] | 
   let c_46_2 = And[s_45_1, c_45_1] | 
   let c_46_3 = And[s_45_2, c_45_2] | 
   let c_46_4 = And[s_45_3, c_45_3] | 
   let c_46_5 = And[s_45_4, c_45_4] | 
   let c_46_6 = And[s_45_5, c_45_5] | 
   let c_46_7 = And[s_45_6, c_45_6] | 
   let c_46_8 = And[s_45_7, c_45_7] | 
   let c_46_9 = And[s_45_8, c_45_8] | 
   let c_46_10 = And[s_45_9, c_45_9] | 
   let c_46_11 = And[s_45_10, c_45_10] | 
   let c_46_12 = And[s_45_11, c_45_11] | 
   let c_46_13 = And[s_45_12, c_45_12] | 
   let c_46_14 = And[s_45_13, c_45_13] | 
   let c_46_15 = And[s_45_14, c_45_14] | 
   let c_46_16 = And[s_45_15, c_45_15] | 
   let c_46_17 = And[s_45_16, c_45_16] | 
   let c_46_18 = And[s_45_17, c_45_17] | 
   let c_46_19 = And[s_45_18, c_45_18] | 
   let c_46_20 = And[s_45_19, c_45_19] | 
   let c_46_21 = And[s_45_20, c_45_20] | 
   let c_46_22 = And[s_45_21, c_45_21] | 
   let c_46_23 = AdderCarry[s_45_22,  And[aa23, bb22], c_45_22] | 
   let c_46_24 = AdderCarry[s_45_23,  And[aa22, bb23], c_45_23] | 
   let c_46_25 = And[s_45_24, c_45_24] | 
   let c_46_26 = And[s_45_25, c_45_25] | 
   let c_46_27 = And[s_45_26, c_45_26] | 
   let c_46_28 = And[s_45_27, c_45_27] | 
   let c_46_29 = And[s_45_28, c_45_28] | 
   let c_46_30 = And[s_45_29, c_45_29] | 
   let c_46_31 = And[s_45_30, c_45_30] | 
   let c_46_32 = And[s_45_31, c_45_31] | 
   let c_46_33 = And[s_45_32, c_45_32] | 
   let c_46_34 = And[s_45_33, c_45_33] | 
   let c_46_35 = And[s_45_34, c_45_34] | 
   let c_46_36 = And[s_45_35, c_45_35] | 
   let c_46_37 = And[s_45_36, c_45_36] | 
   let c_46_38 = And[s_45_37, c_45_37] | 
   let c_46_39 = And[s_45_38, c_45_38] | 
   let c_46_40 = And[s_45_39, c_45_39] | 
   let c_46_41 = And[s_45_40, c_45_40] | 
   let c_46_42 = And[s_45_41, c_45_41] | 
   let c_46_43 = And[s_45_42, c_45_42] | 
   let c_46_44 = And[s_45_43, c_45_43] | 
   let c_46_45 = And[s_45_44, c_45_44] | 
   let c_46_46 = And[s_45_45, c_45_45] | 
   let s_46_0 = false | 
   let s_46_1 = Xor[s_46_0, c_46_0] | 
   let s_46_2 = Xor[s_46_1, c_46_1] | 
   let s_46_3 = Xor[s_46_2, c_46_2] | 
   let s_46_4 = Xor[s_46_3, c_46_3] | 
   let s_46_5 = Xor[s_46_4, c_46_4] | 
   let s_46_6 = Xor[s_46_5, c_46_5] | 
   let s_46_7 = Xor[s_46_6, c_46_6] | 
   let s_46_8 = Xor[s_46_7, c_46_7] | 
   let s_46_9 = Xor[s_46_8, c_46_8] | 
   let s_46_10 = Xor[s_46_9, c_46_9] | 
   let s_46_11 = Xor[s_46_10, c_46_10] | 
   let s_46_12 = Xor[s_46_11, c_46_11] | 
   let s_46_13 = Xor[s_46_12, c_46_12] | 
   let s_46_14 = Xor[s_46_13, c_46_13] | 
   let s_46_15 = Xor[s_46_14, c_46_14] | 
   let s_46_16 = Xor[s_46_15, c_46_15] | 
   let s_46_17 = Xor[s_46_16, c_46_16] | 
   let s_46_18 = Xor[s_46_17, c_46_17] | 
   let s_46_19 = Xor[s_46_18, c_46_18] | 
   let s_46_20 = Xor[s_46_19, c_46_19] | 
   let s_46_21 = Xor[s_46_20, c_46_20] | 
   let s_46_22 = Xor[s_46_21, c_46_21] | 
   let s_46_23 = Xor[s_46_22, c_46_22] | 
   let s_46_24 = AdderSum  [s_46_23,  And[aa23, bb23], c_46_23] | 
   let s_46_25 = Xor[s_46_24, c_46_24] | 
   let s_46_26 = Xor[s_46_25, c_46_25] | 
   let s_46_27 = Xor[s_46_26, c_46_26] | 
   let s_46_28 = Xor[s_46_27, c_46_27] | 
   let s_46_29 = Xor[s_46_28, c_46_28] | 
   let s_46_30 = Xor[s_46_29, c_46_29] | 
   let s_46_31 = Xor[s_46_30, c_46_30] | 
   let s_46_32 = Xor[s_46_31, c_46_31] | 
   let s_46_33 = Xor[s_46_32, c_46_32] | 
   let s_46_34 = Xor[s_46_33, c_46_33] | 
   let s_46_35 = Xor[s_46_34, c_46_34] | 
   let s_46_36 = Xor[s_46_35, c_46_35] | 
   let s_46_37 = Xor[s_46_36, c_46_36] | 
   let s_46_38 = Xor[s_46_37, c_46_37] | 
   let s_46_39 = Xor[s_46_38, c_46_38] | 
   let s_46_40 = Xor[s_46_39, c_46_39] | 
   let s_46_41 = Xor[s_46_40, c_46_40] | 
   let s_46_42 = Xor[s_46_41, c_46_41] | 
   let s_46_43 = Xor[s_46_42, c_46_42] | 
   let s_46_44 = Xor[s_46_43, c_46_43] | 
   let s_46_45 = Xor[s_46_44, c_46_44] | 
   let s_46_46 = Xor[s_46_45, c_46_45] | 
   let s_46_47 = Xor[s_46_46, c_46_46] | 
   let c_47_0 = false | 
   let c_47_1 = And[s_46_0, c_46_0] | 
   let c_47_2 = And[s_46_1, c_46_1] | 
   let c_47_3 = And[s_46_2, c_46_2] | 
   let c_47_4 = And[s_46_3, c_46_3] | 
   let c_47_5 = And[s_46_4, c_46_4] | 
   let c_47_6 = And[s_46_5, c_46_5] | 
   let c_47_7 = And[s_46_6, c_46_6] | 
   let c_47_8 = And[s_46_7, c_46_7] | 
   let c_47_9 = And[s_46_8, c_46_8] | 
   let c_47_10 = And[s_46_9, c_46_9] | 
   let c_47_11 = And[s_46_10, c_46_10] | 
   let c_47_12 = And[s_46_11, c_46_11] | 
   let c_47_13 = And[s_46_12, c_46_12] | 
   let c_47_14 = And[s_46_13, c_46_13] | 
   let c_47_15 = And[s_46_14, c_46_14] | 
   let c_47_16 = And[s_46_15, c_46_15] | 
   let c_47_17 = And[s_46_16, c_46_16] | 
   let c_47_18 = And[s_46_17, c_46_17] | 
   let c_47_19 = And[s_46_18, c_46_18] | 
   let c_47_20 = And[s_46_19, c_46_19] | 
   let c_47_21 = And[s_46_20, c_46_20] | 
   let c_47_22 = And[s_46_21, c_46_21] | 
   let c_47_23 = And[s_46_22, c_46_22] | 
   let c_47_24 = AdderCarry[s_46_23,  And[aa23, bb23], c_46_23] | 
   let c_47_25 = And[s_46_24, c_46_24] | 
   let c_47_26 = And[s_46_25, c_46_25] | 
   let c_47_27 = And[s_46_26, c_46_26] | 
   let c_47_28 = And[s_46_27, c_46_27] | 
   let c_47_29 = And[s_46_28, c_46_28] | 
   let c_47_30 = And[s_46_29, c_46_29] | 
   let c_47_31 = And[s_46_30, c_46_30] | 
   let c_47_32 = And[s_46_31, c_46_31] | 
   let c_47_33 = And[s_46_32, c_46_32] | 
   let c_47_34 = And[s_46_33, c_46_33] | 
   let c_47_35 = And[s_46_34, c_46_34] | 
   let c_47_36 = And[s_46_35, c_46_35] | 
   let c_47_37 = And[s_46_36, c_46_36] | 
   let c_47_38 = And[s_46_37, c_46_37] | 
   let c_47_39 = And[s_46_38, c_46_38] | 
   let c_47_40 = And[s_46_39, c_46_39] | 
   let c_47_41 = And[s_46_40, c_46_40] | 
   let c_47_42 = And[s_46_41, c_46_41] | 
   let c_47_43 = And[s_46_42, c_46_42] | 
   let c_47_44 = And[s_46_43, c_46_43] | 
   let c_47_45 = And[s_46_44, c_46_44] | 
   let c_47_46 = And[s_46_45, c_46_45] | 
   let c_47_47 = And[s_46_46, c_46_46] | 
      rr00 = Or[s_20_21, Or[s_19_20, Or[s_18_19, Or[s_17_18, Or[s_16_17, Or[s_15_16, Or[s_14_15, Or[s_13_14, Or[s_12_13, Or[s_11_12, Or[s_10_11, Or[s_9_10, Or[s_8_9, Or[s_7_8, Or[s_6_7, Or[s_5_6, Or[s_4_5, Or[s_3_4, Or[s_2_3, Or[s_1_2, s_0_1]]]]]]]]]]]]]]]]]]]] and
      rr01 = s_21_22 and
      rr02 = s_22_23 and
      rr03 = s_23_24 and
      rr04 = s_24_25 and
      rr05 = s_25_26 and
      rr06 = s_26_27 and
      rr07 = s_27_28 and
      rr08 = s_28_29 and
      rr09 = s_29_30 and
      rr10 = s_30_31 and
      rr11 = s_31_32 and
      rr12 = s_32_33 and
      rr13 = s_33_34 and
      rr14 = s_34_35 and
      rr15 = s_35_36 and
      rr16 = s_36_37 and
      rr17 = s_37_38 and
      rr18 = s_38_39 and
      rr19 = s_39_40 and
      rr20 = s_40_41 and
      rr21 = s_41_42 and
      rr22 = s_42_43 and
      rr23 = s_43_44 and
      rr24 = s_44_45 and
      rr25 = s_45_46 and
      rr26 = s_46_47 and
rr27 = (true in (
      c_47_0 + c_47_1 + c_47_2 + c_47_3 + c_47_4 + c_47_5 + c_47_6 + c_47_7 + c_47_8 + c_47_9 + c_47_10 + c_47_11 + c_47_12 + c_47_13 + c_47_14 + c_47_15 + c_47_16 + c_47_17 + c_47_18 + c_47_19 + c_47_20 + c_47_21 + c_47_22 + c_47_23 + c_47_24 + c_47_25 + c_47_26 + c_47_27 + c_47_28 + c_47_29 + c_47_30 + c_47_31 + c_47_32 + c_47_33 + c_47_34 + c_47_35 + c_47_36 + c_47_37 + c_47_38 + c_47_39 + c_47_40 + c_47_41 + c_47_42 + c_47_43 + c_47_44 + c_47_45 + c_47_46 + c_47_47
) => true else false)
}

pred addOne8bits[b07,b06,b05,b04,b03,b02,b01,b00, overflow,r07,r06,r05,r04,r03,r02,r01,r00 : boolean]{
   let s_0 = Not[b00]| 
   let s_1 = Xor[b01, b00] |
   let c_2 = And[b00, b01] |
   let s_2 = Xor[b02, c_2] |
   let c_3 = And[c_2, b02] |
   let s_3 = Xor[b03, c_3] |
   let c_4 = And[c_3, b03] |
   let s_4 = Xor[b04, c_4] | 
   let c_5 = And[c_4, b04] | 
   let s_5 = Xor[b05, c_5] | 
   let c_6 = And[c_5, b05] | 
   let s_6 = Xor[b06, c_6] | 
   let c_7 = And[c_6, b06] | 
   let s_7 = Xor[b07, c_7] | 
   let c_8 = And[c_7, b07] | 
      r00 in s_0 and
      r01 in s_1 and
      r02 in s_2 and
      r03 in s_3 and
      r04 in s_4 and
      r05 in s_5 and
      r06 in s_6 and
      r07 in s_7 and
      overflow = (c_8)
}


pred booleanwiseAdd27[	a26,a25,a24,a23,a22,a21,a20,a19,a18,a17,a16,a15,a14,a13,a12,a11,a10,a09,a08,a07,a06,a05,a04,a03,a02,a01,a00,
								b26,b25,b24,b23,b22,b21,b20,b19,b18,b17,b16,b15,b14,b13,b12,b11,b10,b09,b08,b07,b06,b05,b04,b03,b02,b01,b00,
								o,r26,r25,r24,r23,r22,r21,r20,r19,r18,r17,r16,r15,r14,r13,r12,r11,r10,r09,r08,r07,r06,r05,r04,r03,r02,r01,r00: boolean] {
   let c_0 = false | 
   let s_0 = AdderSum[a00, b00, c_0] | 
   let c_1 = AdderCarry[a00, b00, c_0] | 
   let s_1 = AdderSum[a01, b01, c_1] | 
   let c_2 = AdderCarry[a01, b01, c_1] | 
   let s_2 = AdderSum[a02, b02, c_2] | 
   let c_3 = AdderCarry[a02, b02, c_2] | 
   let s_3 = AdderSum[a03, b03, c_3] | 
   let c_4 = AdderCarry[a03, b03, c_3] | 
   let s_4 = AdderSum[a04, b04, c_4] | 
   let c_5 = AdderCarry[a04, b04, c_4] | 
   let s_5 = AdderSum[a05, b05, c_5] | 
   let c_6 = AdderCarry[a05, b05, c_5] | 
   let s_6 = AdderSum[a06, b06, c_6] | 
   let c_7 = AdderCarry[a06, b06, c_6] | 
   let s_7 = AdderSum[a07, b07, c_7] | 
   let c_8 = AdderCarry[a07, b07, c_7] | 
   let s_8 = AdderSum[a08, b08, c_8] | 
   let c_9 = AdderCarry[a08, b08, c_8] | 
   let s_9 = AdderSum[a09, b09, c_9] | 
   let c_10 = AdderCarry[a09, b09, c_9] | 
   let s_10 = AdderSum[a10, b10, c_10] | 
   let c_11 = AdderCarry[a10, b10, c_10] | 
   let s_11 = AdderSum[a11, b11, c_11] | 
   let c_12 = AdderCarry[a11, b11, c_11] | 
   let s_12 = AdderSum[a12, b12, c_12] | 
   let c_13 = AdderCarry[a12, b12, c_12] | 
   let s_13 = AdderSum[a13, b13, c_13] | 
   let c_14 = AdderCarry[a13, b13, c_13] | 
   let s_14 = AdderSum[a14, b14, c_14] | 
   let c_15 = AdderCarry[a14, b14, c_14] | 
   let s_15 = AdderSum[a15, b15, c_15] | 
   let c_16 = AdderCarry[a15, b15, c_15] | 
   let s_16 = AdderSum[a16, b16, c_16] | 
   let c_17 = AdderCarry[a16, b16, c_16] | 
   let s_17 = AdderSum[a17, b17, c_17] | 
   let c_18 = AdderCarry[a17, b17, c_17] | 
   let s_18 = AdderSum[a18, b18, c_18] | 
   let c_19 = AdderCarry[a18, b18, c_18] | 
   let s_19 = AdderSum[a19, b19, c_19] | 
   let c_20 = AdderCarry[a19, b19, c_19] | 
   let s_20 = AdderSum[a20, b20, c_20] | 
   let c_21 = AdderCarry[a20, b20, c_20] | 
   let s_21 = AdderSum[a21, b21, c_21] | 
   let c_22 = AdderCarry[a21, b21, c_21] | 
   let s_22 = AdderSum[a22, b22, c_22] | 
   let c_23 = AdderCarry[a22, b22, c_22] | 
   let s_23 = AdderSum[a23, b23, c_23] | 
   let c_24 = AdderCarry[a23, b23, c_23] | 
   let s_24 = AdderSum[a24, b24, c_24] |  
   let c_25 = AdderCarry[a24, b24, c_24] | 
   let s_25 = AdderSum[a25, b25, c_25] |  
   let c_26 = AdderCarry[a25, b25, c_25] | 
   let s_26 = AdderSum[a26, b26, c_26] |  
   let c_27 = AdderCarry[a26, b26, c_26] | 
     r00 in s_0 and
      r01 in s_1 and
      r02 in s_2 and
      r03 in s_3 and
      r04 in s_4 and
      r05 in s_5 and
      r06 in s_6 and
      r07 in s_7 and
      r08 in s_8 and
      r09 in s_9 and
      r10 in s_10 and
      r11 in s_11 and
      r12 in s_12 and
      r13 in s_13 and
      r14 in s_14 and
      r15 in s_15 and
      r16 in s_16 and
      r17 in s_17 and
      r18 in s_18 and
      r19 in s_19 and
      r20 in s_20 and
      r21 in s_21 and
      r22 in s_22 and
      r23 in s_23 and
      r24 in s_24 and
      r25 in s_25 and
      r26 in s_26 and
      o = (c_27)
}



pred isNormalized[f : JavaPrimitiveFloatValue]{
	isBitNormalized[f.b31,f.b30,f.b29,f.b28,f.b27,f.b26,f.b25,f.b24,f.b23,f.b22,f.b21,f.b20,f.b19,f.b18,f.b17,f.b16,f.b15,f.b14,f.b13,f.b12,f.b11,f.b10,f.b09,f.b08,f.b07,f.b06,f.b05,f.b04,f.b03,f.b02,f.b01,f.b00]
}



pred isZero[f : JavaPrimitiveFloatValue]{
	isBitZero[f.b31,f.b30,f.b29,f.b28,f.b27,f.b26,f.b25,f.b24,f.b23,f.b22,f.b21,f.b20,f.b19,f.b18,f.b17,f.b16,f.b15,f.b14,f.b13,f.b12,f.b11,f.b10,f.b09,f.b08,f.b07,f.b06,f.b05,f.b04,f.b03,f.b02,f.b01,f.b00]
}



pred sameBits[f1,f2 : JavaPrimitiveFloatValue]{
	f1.b31 = f2.b31 and
	f1.b30 = f2.b30 and
	f1.b29 = f2.b29 and
	f1.b28 = f2.b28 and
	f1.b27 = f2.b27 and
	f1.b26 = f2.b26 and
	f1.b25 = f2.b25 and
	f1.b24 = f2.b24 and
	f1.b23 = f2.b23 and
	f1.b22 = f2.b22 and
	f1.b21 = f2.b21 and
	f1.b20 = f2.b20 and
	f1.b19 = f2.b19 and
	f1.b18 = f2.b18 and
	f1.b17 = f2.b17 and
	f1.b16 = f2.b16 and
	f1.b15 = f2.b15 and
	f1.b14 = f2.b14 and
	f1.b13 = f2.b13 and
	f1.b12 = f2.b12 and
	f1.b11 = f2.b11 and
	f1.b10 = f2.b10 and
	f1.b09 = f2.b09 and
	f1.b08 = f2.b08 and
	f1.b07 = f2.b07 and
	f1.b06 = f2.b06 and
	f1.b05 = f2.b05 and
	f1.b04 = f2.b04 and
	f1.b03 = f2.b03 and
	f1.b02 = f2.b02 and
	f1.b01 = f2.b01 and
	f1.b00 = f2.b00
}


pred booleanwiseSub27[	a26,a25,a24,a23,a22,a21,a20,a19,a18,a17,a16,a15,a14,a13,a12,a11,a10,a09,a08,a07,a06,a05,a04,a03,a02,a01,a00,
								b26,b25,b24,b23,b22,b21,b20,b19,b18,b17,b16,b15,b14,b13,b12,b11,b10,b09,b08,b07,b06,b05,b04,b03,b02,b01,b00,
								r26,r25,r24,r23,r22,r21,r20,r19,r18,r17,r16,r15,r14,r13,r12,r11,r10,r09,r08,r07,r06,r05,r04,r03,r02,r01,r00: boolean] {
	booleanwiseAdd27[	b26,b25,b24,b23,b22,b21,b20,b19,b18,b17,b16,b15,b14,b13,b12,b11,b10,b09,b08,b07,b06,b05,b04,b03,b02,b01,b00,
							r26,r25,r24,r23,r22,r21,r20,r19,r18,r17,r16,r15,r14,r13,r12,r11,r10,r09,r08,r07,r06,r05,r04,r03,r02,r01,r00,
							false,a26,a25,a24,a23,a22,a21,a20,a19,a18,a17,a16,a15,a14,a13,a12,a11,a10,a09,a08,a07,a06,a05,a04,a03,a02,a01,a00]
}

pred booleanwiseGT8[a07,a06,a05,a04,a03,a02,a01,a00, b07,b06,b05,b04,b03,b02,b01,b00 : boolean] {
   (a07 in true and b07 in false) 
   or (a07 = b07) and (a06 in true and b06 in false) 
   or (a07 = b07) and (a06 = b06) and (a05 in true and b05 in false) 
   or (a07 = b07) and (a06 = b06) and (a05 = b05) and (a04 in true and b04 in false) 
   or (a07 = b07) and (a06 = b06) and (a05 = b05) and (a04 = b04) and (a03 in true and b03 in false) 
   or (a07 = b07) and (a06 = b06) and (a05 = b05) and (a04 = b04) and (a03 = b03) and (a02 in true and b02 in false) 
   or (a07 = b07) and (a06 = b06) and (a05 = b05) and (a04 = b04) and (a03 = b03) and (a02 = b02) and (a01 in true and b01 in false) 
   or (a07 = b07) and (a06 = b06) and (a05 = b05) and (a04 = b04) and (a03 = b03) and (a02 = b02) and (a01 = b01) and (a00 in true and b00 in false) 
}


pred booleanwiseAddOne24bits[	b23,b22,b21,b20,b19,b18,b17,b16,b15,b14,b13,b12,b11,b10,b09,b08,b07,b06,b05,b04,b03,b02,b01,b00,
										overflow,r23,r22,r21,r20,r19,r18,r17,r16,r15,r14,r13,r12,r11,r10,r09,r08,r07,r06,r05,r04,r03,r02,r01,r00 : boolean]
{

   let s_0 = Not[b00] |
   let s_1 = Xor[b01, b00] |
   let c_2 = And[b00, b01] |
   let s_2 = Xor[b02, c_2] |
   let c_3 = And[c_2, b02] |
   let s_3 = Xor[b03, c_3] | 
   let c_4 = And[c_3, b03] | 
   let s_4 = Xor[b04, c_4] | 
   let c_5 = And[c_4, b04] | 
   let s_5 = Xor[b05, c_5] | 
   let c_6 = And[c_5, b05] | 
   let s_6 = Xor[b06, c_6] | 
   let c_7 = And[c_6, b06] | 
   let s_7 = Xor[b07, c_7] | 
   let c_8 = And[c_7, b07] | 
   let s_8 = Xor[b08, c_8] | 
   let c_9 = And[c_8, b08] | 
   let s_9 = Xor[b09, c_9] | 
   let c_10 = And[c_9, b09] | 
   let s_10 = Xor[b10, c_10] | 
   let c_11 = And[c_10, b10] | 
   let s_11 = Xor[b11, c_11] | 
   let c_12 = And[c_11, b11] | 
   let s_12 = Xor[b12, c_12] | 
   let c_13 = And[c_12, b12] | 
   let s_13 = Xor[b13, c_13] | 
   let c_14 = And[c_13, b13] | 
   let s_14 = Xor[b14, c_14] | 
   let c_15 = And[c_14, b14] | 
   let s_15 = Xor[b15, c_15] | 
   let c_16 = And[c_15, b15] | 
   let s_16 = Xor[b16, c_16] | 
   let c_17 = And[c_16, b16] | 
   let s_17 = Xor[b17, c_17] | 
   let c_18 = And[c_17, b17] | 
   let s_18 = Xor[b18, c_18] | 
   let c_19 = And[c_18, b18] | 
   let s_19 = Xor[b19, c_19] | 
   let c_20 = And[c_19, b19] | 
   let s_20 = Xor[b20, c_20] | 
   let c_21 = And[c_20, b20] | 
   let s_21 = Xor[b21, c_21] | 
   let c_22 = And[c_21, b21] | 
   let s_22 = Xor[b22, c_22] | 
   let c_23 = And[c_22, b22] | 
   let s_23 = Xor[b23, c_23] | 
   let c_24 = And[c_23, b23] | 
      r00 in s_0 and
      r01 in s_1 and
      r02 in s_2 and
      r03 in s_3 and
      r04 in s_4 and
      r05 in s_5 and
      r06 in s_6 and
      r07 in s_7 and
      r08 in s_8 and
      r09 in s_9 and
      r10 in s_10 and
      r11 in s_11 and
      r12 in s_12 and
      r13 in s_13 and
      r14 in s_14 and
      r15 in s_15 and
      r16 in s_16 and
      r17 in s_17 and
      r18 in s_18 and
      r19 in s_19 and
      r20 in s_20 and
      r21 in s_21 and
      r22 in s_22 and
      r23 in s_23 and
      overflow = c_24
}

pred booleanwiseGT27[	a26,a25,a24,a23,a22,a21,a20,a19,a18,a17,a16,a15,a14,a13,a12,a11,a10,a09,a08,a07,a06,a05,a04,a03,a02,a01,a00,
								b26,b25,b24,b23,b22,b21,b20,b19,b18,b17,b16,b15,b14,b13,b12,b11,b10,b09,b08,b07,b06,b05,b04,b03,b02,b01,b00 : boolean] {
	(a26 in true and b26 in false)
	or (a26 = b26) and (a25 in true and b25 in false)
	or (a26 = b26) and (a25 = b25) and (a24 in true and b24 in false)
	or (a26 = b26) and (a25 = b25) and (a24 = b24) and (a23 in true and b23 in false) 
	or (a26 = b26) and (a25 = b25) and (a24 = b24) and (a23 = b23) and (a22 in true and b22 in false) 
	or (a26 = b26) and (a25 = b25) and (a24 = b24) and (a23 = b23) and (a22 = b22) and (a21 in true and b21 in false) 
	or (a26 = b26) and (a25 = b25) and (a24 = b24) and (a23 = b23) and (a22 = b22) and (a21 = b21) and (a20 in true and b20 in false) 
	or (a26 = b26) and (a25 = b25) and (a24 = b24) and (a23 = b23) and (a22 = b22) and (a21 = b21) and (a20 = b20) and (a19 in true and b19 in false) 
	or (a26 = b26) and (a25 = b25) and (a24 = b24) and (a23 = b23) and (a22 = b22) and (a21 = b21) and (a20 = b20) and (a19 = b19) and (a18 in true and b18 in false) 
	or (a26 = b26) and (a25 = b25) and (a24 = b24) and (a23 = b23) and (a22 = b22) and (a21 = b21) and (a20 = b20) and (a19 = b19) and (a18 = b18) and (a17 in true and b17 in false) 
	or (a26 = b26) and (a25 = b25) and (a24 = b24) and (a23 = b23) and (a22 = b22) and (a21 = b21) and (a20 = b20) and (a19 = b19) and (a18 = b18) and (a17 = b17) and (a16 in true and b16 in false) 
	or (a26 = b26) and (a25 = b25) and (a24 = b24) and (a23 = b23) and (a22 = b22) and (a21 = b21) and (a20 = b20) and (a19 = b19) and (a18 = b18) and (a17 = b17) and (a16 = b16) and (a15 in true and b15 in false) 
	or (a26 = b26) and (a25 = b25) and (a24 = b24) and (a23 = b23) and (a22 = b22) and (a21 = b21) and (a20 = b20) and (a19 = b19) and (a18 = b18) and (a17 = b17) and (a16 = b16) and (a15 = b15) and (a14 in true and b14 in false) 
	or (a26 = b26) and (a25 = b25) and (a24 = b24) and (a23 = b23) and (a22 = b22) and (a21 = b21) and (a20 = b20) and (a19 = b19) and (a18 = b18) and (a17 = b17) and (a16 = b16) and (a15 = b15) and (a14 = b14) and (a13 in true and b13 in false) 
	or (a26 = b26) and (a25 = b25) and (a24 = b24) and (a23 = b23) and (a22 = b22) and (a21 = b21) and (a20 = b20) and (a19 = b19) and (a18 = b18) and (a17 = b17) and (a16 = b16) and (a15 = b15) and (a14 = b14) and (a13 = b13) and (a12 in true and b12 in false) 
	or (a26 = b26) and (a25 = b25) and (a24 = b24) and (a23 = b23) and (a22 = b22) and (a21 = b21) and (a20 = b20) and (a19 = b19) and (a18 = b18) and (a17 = b17) and (a16 = b16) and (a15 = b15) and (a14 = b14) and (a13 = b13) and (a12 = b12) and (a11 in true and b11 in false) 
	or (a26 = b26) and (a25 = b25) and (a24 = b24) and (a23 = b23) and (a22 = b22) and (a21 = b21) and (a20 = b20) and (a19 = b19) and (a18 = b18) and (a17 = b17) and (a16 = b16) and (a15 = b15) and (a14 = b14) and (a13 = b13) and (a12 = b12) and (a11 = b11) and (a10 in true and b10 in false) 
	or (a26 = b26) and (a25 = b25) and (a24 = b24) and (a23 = b23) and (a22 = b22) and (a21 = b21) and (a20 = b20) and (a19 = b19) and (a18 = b18) and (a17 = b17) and (a16 = b16) and (a15 = b15) and (a14 = b14) and (a13 = b13) and (a12 = b12) and (a11 = b11) and (a10 = b10) and (a09 in true and b09 in false) 
	or (a26 = b26) and (a25 = b25) and (a24 = b24) and (a23 = b23) and (a22 = b22) and (a21 = b21) and (a20 = b20) and (a19 = b19) and (a18 = b18) and (a17 = b17) and (a16 = b16) and (a15 = b15) and (a14 = b14) and (a13 = b13) and (a12 = b12) and (a11 = b11) and (a10 = b10) and (a09 = b09) and (a08 in true and b08 in false) 
	or (a26 = b26) and (a25 = b25) and (a24 = b24) and (a23 = b23) and (a22 = b22) and (a21 = b21) and (a20 = b20) and (a19 = b19) and (a18 = b18) and (a17 = b17) and (a16 = b16) and (a15 = b15) and (a14 = b14) and (a13 = b13) and (a12 = b12) and (a11 = b11) and (a10 = b10) and (a09 = b09) and (a08 = b08) and (a07 in true and b07 in false) 
	or (a26 = b26) and (a25 = b25) and (a24 = b24) and (a23 = b23) and (a22 = b22) and (a21 = b21) and (a20 = b20) and (a19 = b19) and (a18 = b18) and (a17 = b17) and (a16 = b16) and (a15 = b15) and (a14 = b14) and (a13 = b13) and (a12 = b12) and (a11 = b11) and (a10 = b10) and (a09 = b09) and (a08 = b08) and (a07 = b07) and (a06 in true and b06 in false) 
	or (a26 = b26) and (a25 = b25) and (a24 = b24) and (a23 = b23) and (a22 = b22) and (a21 = b21) and (a20 = b20) and (a19 = b19) and (a18 = b18) and (a17 = b17) and (a16 = b16) and (a15 = b15) and (a14 = b14) and (a13 = b13) and (a12 = b12) and (a11 = b11) and (a10 = b10) and (a09 = b09) and (a08 = b08) and (a07 = b07) and (a06 = b06) and (a05 in true and b05 in false) 
	or (a26 = b26) and (a25 = b25) and (a24 = b24) and (a23 = b23) and (a22 = b22) and (a21 = b21) and (a20 = b20) and (a19 = b19) and (a18 = b18) and (a17 = b17) and (a16 = b16) and (a15 = b15) and (a14 = b14) and (a13 = b13) and (a12 = b12) and (a11 = b11) and (a10 = b10) and (a09 = b09) and (a08 = b08) and (a07 = b07) and (a06 = b06) and (a05 = b05) and (a04 in true and b04 in false) 
	or (a26 = b26) and (a25 = b25) and (a24 = b24) and (a23 = b23) and (a22 = b22) and (a21 = b21) and (a20 = b20) and (a19 = b19) and (a18 = b18) and (a17 = b17) and (a16 = b16) and (a15 = b15) and (a14 = b14) and (a13 = b13) and (a12 = b12) and (a11 = b11) and (a10 = b10) and (a09 = b09) and (a08 = b08) and (a07 = b07) and (a06 = b06) and (a05 = b05) and (a04 = b04) and (a03 in true and b03 in false) 
	or (a26 = b26) and (a25 = b25) and (a24 = b24) and (a23 = b23) and (a22 = b22) and (a21 = b21) and (a20 = b20) and (a19 = b19) and (a18 = b18) and (a17 = b17) and (a16 = b16) and (a15 = b15) and (a14 = b14) and (a13 = b13) and (a12 = b12) and (a11 = b11) and (a10 = b10) and (a09 = b09) and (a08 = b08) and (a07 = b07) and (a06 = b06) and (a05 = b05) and (a04 = b04) and (a03 = b03) and (a02 in true and b02 in false) 
	or (a26 = b26) and (a25 = b25) and (a24 = b24) and (a23 = b23) and (a22 = b22) and (a21 = b21) and (a20 = b20) and (a19 = b19) and (a18 = b18) and (a17 = b17) and (a16 = b16) and (a15 = b15) and (a14 = b14) and (a13 = b13) and (a12 = b12) and (a11 = b11) and (a10 = b10) and (a09 = b09) and (a08 = b08) and (a07 = b07) and (a06 = b06) and (a05 = b05) and (a04 = b04) and (a03 = b03) and (a02 = b02) and (a01 in true and b01 in false) 
	or (a26 = b26) and (a25 = b25) and (a24 = b24) and (a23 = b23) and (a22 = b22) and (a21 = b21) and (a20 = b20) and (a19 = b19) and (a18 = b18) and (a17 = b17) and (a16 = b16) and (a15 = b15) and (a14 = b14) and (a13 = b13) and (a12 = b12) and (a11 = b11) and (a10 = b10) and (a09 = b09) and (a08 = b08) and (a07 = b07) and (a06 = b06) and (a05 = b05) and (a04 = b04) and (a03 = b03) and (a02 = b02) and (a01 = b01) and (a00 in true and b00 in false) 
}



pred booleanwiseSub8[a07,a06,a05,a04,a03,a02,a01,a00, b07,b06,b05,b04,b03,b02,b01,b00, r07,r06,r05,r04,r03,r02,r01,r00 : boolean]{
	booleanwiseAdd8[b07,b06,b05,b04,b03,b02,b01,b00, r07,r06,r05,r04,r03,r02,r01,r00, false,a07,a06,a05,a04,a03,a02,a01,a00]
} 


pred shift32OnePositionAndRoundUp[s27,s26,s25,s24,s23,s22,s21,s20,s19,s18,s17,s16,s15,s14,s13,s12,s11,s10,s09,s08,s07,s06,s05,s04,s03,s02,s01 : boolean, 
										overflow, t23,t22,t21,t20,t19,t18,t17,t16,t15,t14,t13,t12,t11,t10,t09,t08,t07,t06,t05,t04,t03,t02,t01,t00 : boolean]{
	roundsUp[s04,s03,s02,s01] implies 
			booleanwiseAddOne24bits[	s27,s26,s25,s24,s23,s22,s21,s20,s19,s18,s17,s16,s15,s14,s13,s12,s11,s10,s09,s08,s07,s06,s05,s04,
													overflow, t23,t22,t21,t20,t19,t18,t17,t16,t15,t14,t13,t12,t11,t10,t09,t08,t07,t06,t05,t04,t03,t02,t01,t00]
	else
			(t23 = s27 and t22 = s26 and t21 = s25 and t20 = s24 and t19 = s23 and t18 = s22 and t17 = s21 and t16 = s20 and t15 = s19 and t14 = s18 and t13 = s17 and t12 = s16 and t11 = s15 and t10 = s14 and t09 = s13 and t08 = s12 and t07 = s11 and t06 = s10 and t05 = s09 and t04 = s08 and t03 = s07 and t02 = s06 and t01 = s05 and t00 = s04 and overflow = false)

}

pred shift32[
	s26,s25,s24,s23,s22,s21,s20,s19,s18,s17,s16,s15,s14,s13,s12,s11,s10,s09,s08,s07,s06,s05,s04,s03,s02,s01,s00 : boolean, 
	i07,i06,i05,i04,i03,i02,i01,i00 : boolean,
	t26,t25,t24,t23,t22,t21,t20,t19,t18,t17,t16,t15,t14,t13,t12,t11,t10,t09,t08,t07,t06,t05,t04,t03,t02,t01,t00 : boolean
]
{
(i07 = false and i06 = false and i05 = false and i04 = false and i03 = false and i02 = false and i01 = false and i00 = false) implies (t26 = s26 and t25 = s25 and t24 = s24 and t23 = s23 and t22 = s22 and t21 = s21 and t20 = s20 and t19 = s19 and t18 = s18 and t17 = s17 and t16 = s16 and t15 = s15 and t14 = s14 and t13 = s13 and t12 = s12 and t11 = s11 and t10 = s10 and t09 = s09 and t08 = s08 and t07 = s07 and t06 = s06 and t05 = s05 and t04 = s04 and t03 = s03 and t02 = s02 and t01 = s01 and t00 = s00)
else
	(i07 = false and i06 = false and i05 = false and i04 = false and i03 = false and i02 = false and i01 = false and i00 = true) implies (t26 = false and t25 = s26 and t24 = s25 and t23 = s24 and t22 = s23 and t21 = s22 and t20 = s21 and t19 = s20 and t18 = s19 and t17 = s18 and t16 = s17 and t15 = s16 and t14 = s15 and t13 = s14 and t12 = s13 and t11 = s12 and t10 = s11 and t09 = s10 and t08 = s09 and t07 = s08 and t06 = s07 and t05 = s06 and t04 = s05 and t03 = s04 and t02 = s03 and t01 = s02 and t00 = Or[s01,s00])
	else 
		(i07 = false and i06 = false and i05 = false and i04 = false and i03 = false and i02 = false and i01 = true and i00 = false) implies (t26 = false and t25 = false and t24 = s26 and t23 = s25 and t22 = s24 and t21 = s23 and t20 = s22 and t19 = s21 and t18 = s20 and t17 = s19 and t16 = s18 and t15 = s17 and t14 = s16 and t13 = s15 and t12 = s14 and t11 = s13 and t10 = s12 and t09 = s11 and t08 = s10 and t07 = s09 and t06 = s08 and t05 = s07 and t04 = s06 and t03 = s05 and t02 = s04 and t01 = s03 and t00 = Or[s00,Or[s01,s02]])
		else 
			(i07 = false and i06 = false and i05 = false and i04 = false and i03 = false and i02 = false and i01 = true and i00 = true) implies (t26 = false and t25 = false and t24 = false and t23 = s26 and t22 = s25 and t21 = s24 and t20 = s23 and t19 = s22 and t18 = s21 and t17 = s20 and t16 = s19 and t15 = s18 and t14 = s17 and t13 = s16 and t12 = s15 and t11 = s14 and t10 = s13 and t09 = s12 and t08 = s11 and t07 = s10 and t06 = s09 and t05 = s08 and t04 = s07 and t03 = s06 and t02 = s05 and t01 = s04 and t00 = Or[s00,Or[s01,Or[s02,s03]]])
			else
				(i07 = false and i06 = false and i05 = false and i04 = false and i03 = false and i02 = true and i01 = false and i00 = false) implies (t26 = false and t25 = false and t24 = false and t23 = false and t22 = s26 and t21 = s25 and t20 = s24 and t19 = s23 and t18 = s22 and t17 = s21 and t16 = s20 and t15 = s19 and t14 = s18 and t13 = s17 and t12 = s16 and t11 = s15 and t10 = s14 and t09 = s13 and t08 = s12 and t07 = s11 and t06 = s10 and t05 = s09 and t04 = s08 and t03 = s07 and t02 = s06 and t01 = s05 and t00 = Or[s00,Or[s01,Or[s02,Or[s03,s04]]]])
				else
					(i07 = false and i06 = false and i05 = false and i04 = false and i03 = false and i02 = true and i01 = false and i00 = true) implies (t26 = false and t25 = false and t24 = false and t23 = false and t22 = false and t21 = s26 and t20 = s25 and t19 = s24 and t18 = s23 and t17 = s22 and t16 = s21 and t15 = s20 and t14 = s19 and t13 = s18 and t12 = s17 and t11 = s16 and t10 = s15 and t09 = s14 and t08 = s13 and t07 = s12 and t06 = s11 and t05 = s10 and t04 = s09 and t03 = s08 and t02 = s07 and t01 = s06 and t00 = Or[s00,Or[s01,Or[s02,Or[s03,Or[s04,s05]]]]])
					else
						(i07 = false and i06 = false and i05 = false and i04 = false and i03 = false and i02 = true and i01 = true and i00 = false) implies (t26 = false and t25 = false and t24 = false and t23 = false and t22 = false and t21 = false and t20 = s26 and t19 = s25 and t18 = s24 and t17 = s23 and t16 = s22 and t15 = s21 and t14 = s20 and t13 = s19 and t12 = s18 and t11 = s17 and t10 = s16 and t09 = s15 and t08 = s14 and t07 = s13 and t06 = s12 and t05 = s11 and t04 = s10 and t03 = s09 and t02 = s08 and t01 = s07 and t00 = Or[s00,Or[s01,Or[s02,Or[s03,Or[s04,Or[s05,s06]]]]]])
						else
							(i07 = false and i06 = false and i05 = false and i04 = false and i03 = false and i02 = true and i01 = true and i00 = true) implies (t26 = false and t25 = false and t24 = false and t23 = false and t22 = false and t21 = false and t20 = false and t19 = s26 and t18 = s25 and t17 = s24 and t16 = s23 and t15 = s22 and t14 = s21 and t13 = s20 and t12 = s19 and t11 = s18 and t10 = s17 and t09 = s16 and t08 = s15 and t07 = s14 and t06 = s13 and t05 = s12 and t04 = s11 and t03 = s10 and t02 = s09 and t01 = s08 and t00 = Or[s00,Or[s01,Or[s02,Or[s03,Or[s04,Or[s05,Or[s06,s07]]]]]]])
							else
								(i07 = false and i06 = false and i05 = false and i04 = false and i03 = true and i02 = false and i01 = false and i00 = false) implies (t26 = false and t25 = false and t24 = false and t23 = false and t22 = false and t21 = false and t20 = false and t19 = false and t18 = s26 and t17 = s25 and t16 = s24 and t15 = s23 and t14 = s22 and t13 = s21 and t12 = s20 and t11 = s19 and t10 = s18 and t09 = s17 and t08 = s16 and t07 = s15 and t06 = s14 and t05 = s13 and t04 = s12 and t03 = s11 and t02 = s10 and t01 = s09 and t00 = Or[s00,Or[s01,Or[s02,Or[s03,Or[s04,Or[s05,Or[s06,Or[s07,s08]]]]]]]])
								else
									(i07 = false and i06 = false and i05 = false and i04 = false and i03 = true and i02 = false and i01 = false and i00 = true) implies (t26 = false and t25 = false and t24 = false and t23 = false and t22 = false and t21 = false and t20 = false and t19 = false and t18 = false and t17 = s26 and t16 = s25 and t15 = s24 and t14 = s23 and t13 = s22 and t12 = s21 and t11 = s20 and t10 = s19 and t09 = s18 and t08 = s17 and t07 = s16 and t06 = s15 and t05 = s14 and t04 = s13 and t03 = s12 and t02 = s11 and t01 = s10 and t00 = Or[s00,Or[s01,Or[s02,Or[s03,Or[s04,Or[s05,Or[s06,Or[s07,Or[s08,s09]]]]]]]]])
									else
										(i07 = false and i06 = false and i05 = false and i04 = false and i03 = true and i02 = false and i01 = true and i00 = false) implies (t26 = false and t25 = false and t24 = false and t23 = false and t22 = false and t21 = false and t20 = false and t19 = false and t18 = false and t17 = false and t16 = s26 and t15 = s25 and t14 = s24 and t13 = s23 and t12 = s22 and t11 = s21 and t10 = s20 and t09 = s19 and t08 = s18 and t07 = s17 and t06 = s16 and t05 = s15 and t04 = s14 and t03 = s13 and t02 = s12 and t01 = s11 and t00 = Or[s00,Or[s01,Or[s02,Or[s03,Or[s04,Or[s05,Or[s06,Or[s07,Or[s08,Or[s09,s10]]]]]]]]]])
										else
											(i07 = false and i06 = false and i05 = false and i04 = false and i03 = true and i02 = false and i01 = true and i00 = true) implies (t26 = false and t25 = false and t24 = false and t23 = false and t22 = false and t21 = false and t20 = false and t19 = false and t18 = false and t17 = false and t16 = false and t15 = s26 and t14 = s25 and t13 = s24 and t12 = s23 and t11 = s22 and t10 = s21 and t09 = s20 and t08 = s19 and t07 = s18 and t06 = s17 and t05 = s16 and t04 = s15 and t03 = s14 and t02 = s13 and t01 = s12 and t00 = Or[s00,Or[s01,Or[s02,Or[s03,Or[s04,Or[s05,Or[s06,Or[s07,Or[s08,Or[s09,Or[s10,s11]]]]]]]]]]])
											else
												(i07 = false and i06 = false and i05 = false and i04 = false and i03 = true and i02 = true and i01 = false and i00 = false) implies (t26 = false and t25 = false and t24 = false and t23 = false and t22 = false and t21 = false and t20 = false and t19 = false and t18 = false and t17 = false and t16 = false and t15 = false and t14 = s26 and t13 = s25 and t12 = s24 and t11 = s23 and t10 = s22 and t09 = s21 and t08 = s20 and t07 = s19 and t06 = s18 and t05 = s17 and t04 = s16 and t03 = s15 and t02 = s14 and t01 = s13 and t00 = Or[s00,Or[s01,Or[s02,Or[s03,Or[s04,Or[s05,Or[s06,Or[s07,Or[s08,Or[s09,Or[s10,Or[s11,s12]]]]]]]]]]]])
												else
													(i07 = false and i06 = false and i05 = false and i04 = false and i03 = true and i02 = true and i01 = false and i00 = true) implies (t26 = false and t25 = false and t24 = false and t23 = false and t22 = false and t21 = false and t20 = false and t19 = false and t18 = false and t17 = false and t16 = false and t15 = false and t14 = false and t13 = s26 and t12 = s25 and t11 = s24 and t10 = s23 and t09 = s22 and t08 = s21 and t07 = s20 and t06 = s19 and t05 = s18 and t04 = s17 and t03 = s16 and t02 = s15 and t01 = s14 and t00 = Or[s00,Or[s01,Or[s02,Or[s03,Or[s04,Or[s05,Or[s06,Or[s07,Or[s08,Or[s09,Or[s10,Or[s11,Or[s12,s13]]]]]]]]]]]]])
													else
														(i07 = false and i06 = false and i05 = false and i04 = false and i03 = true and i02 = true and i01 = true and i00 = false) implies (t26 = false and t25 = false and t24 = false and t23 = false and t22 = false and t21 = false and t20 = false and t19 = false and t18 = false and t17 = false and t16 = false and t15 = false and t14 = false and t13 = false and t12 = t26 and t11 = s25 and t10 = s24 and t09 = s23 and t08 = s22 and t07 = s21 and t06 = s20 and t05 = s19 and t04 = s18 and t03 = s17 and t02 = s16 and t01 = s15 and t00 = Or[s00,Or[s01,Or[s02,Or[s03,Or[s04,Or[s05,Or[s06,Or[s07,Or[s08,Or[s09,Or[s10,Or[s11,Or[s12,Or[s13,s14]]]]]]]]]]]]]])
														else
															(i07 = false and i06 = false and i05 = false and i04 = false and i03 = true and i02 = true and i01 = true and i00 = true) implies (t26 = false and t25 = false and t24 = false and t23 = false and t22 = false and t21 = false and t20 = false and t19 = false and t18 = false and t17 = false and t16 = false and t15 = false and t14 = false and t13 = false and t12 = false and t11 = s26 and t10 = s25 and t09 = s24 and t08 = s23 and t07 = s22 and t06 = s21 and t05 = s20 and t04 = s19 and t03 = s18 and t02 = s17 and t01 = s16 and t00 = Or[s00,Or[s01,Or[s02,Or[s03,Or[s04,Or[s05,Or[s06,Or[s07,Or[s08,Or[s09,Or[s10,Or[s11,Or[s12,Or[s13,Or[s14,s15]]]]]]]]]]]]]]])
															else
																(i07 = false and i06 = false and i05 = false and i04 = true and i03 = false and i02 = false and i01 = false and i00 = false) implies (t26 = false and t25 = false and t24 = false and t23 = false and t22 = false and t21 = false and t20 = false and t19 = false and t18 = false and t17 = false and t16 = false and t15 = false and t14 = false and t13 = false and t12 = false and t11 = false and t10 = s26 and t09 = s25 and t08 = s24 and t07 = s23 and t06 = s22 and t05 = s21 and t04 = s20 and t03 = s19 and t02 = s18 and t01 = s17 and t00 = Or[s00,Or[s01,Or[s02,Or[s03,Or[s04,Or[s05,Or[s06,Or[s07,Or[s08,Or[s09,Or[s10,Or[s11,Or[s12,Or[s13,Or[s14,Or[s15,s16]]]]]]]]]]]]]]]])
																else
																	(i07 = false and i06 = false and i05 = false and i04 = true and i03 = false and i02 = false and i01 = false and i00 = true) implies (t26 = false and t25 = false and t24 = false and t23 = false and t22 = false and t21 = false and t20 = false and t19 = false and t18 = false and t17 = false and t16 = false and t15 = false and t14 = false and t13 = false and t12 = false and t11 = false and t10 = false and t09 = s26 and t08 = s25 and t07 = s24 and t06 = s23 and t05 = s22 and t04 = s21 and t03 = s20 and t02 = s19 and t01 = s18 and t00 = Or[s00,Or[s01,Or[s02,Or[s03,Or[s04,Or[s05,Or[s06,Or[s07,Or[s08,Or[s09,Or[s10,Or[s11,Or[s12,Or[s13,Or[s14,Or[s15,Or[s16,s17]]]]]]]]]]]]]]]]])
																	else
																		(i07 = false and i06 = false and i05 = false and i04 = true and i03 = false and i02 = false and i01 = true and i00 = false) implies (t26 = false and t25 = false and t24 = false and t23 = false and t22 = false and t21 = false and t20 = false and t19 = false and t18 = false and t17 = false and t16 = false and t15 = false and t14 = false and t13 = false and t12 = false and t11 = false and t10 = false and t09 = false and t08 = s26 and t07 = s25 and t06 = s24 and t05 = s23 and t04 = s22 and t03 = s21 and t02 = s20 and t01 = s19 and t00 = Or[s00,Or[s01,Or[s02,Or[s03,Or[s04,Or[s05,Or[s06,Or[s07,Or[s08,Or[s09,Or[s10,Or[s11,Or[s12,Or[s13,Or[s14,Or[s15,Or[s16,Or[s17,s18]]]]]]]]]]]]]]]]]])
																		else
																			(i07 = false and i06 = false and i05 = false and i04 = true and i03 = false and i02 = false and i01 = true and i00 = true) implies (t26 = false and t25 = false and t24 = false and t23 = false and t22 = false and t21 = false and t20 = false and t19 = false and t18 = false and t17 = false and t16 = false and t15 = false and t14 = false and t13 = false and t12 = false and t11 = false and t10 = false and t09 = false and t08 = false and t07 = s26 and t06 = s25 and t05 = s24 and t04 = s23 and t03 = s22 and t02 = s21 and t01 = s20 and t00 = Or[s00,Or[s01,Or[s02,Or[s03,Or[s04,Or[s05,Or[s06,Or[s07,Or[s08,Or[s09,Or[s10,Or[s11,Or[s12,Or[s13,Or[s14,Or[s15,Or[s16,Or[s17,Or[s18,s19]]]]]]]]]]]]]]]]]]])
																			else
																				(i07 = false and i06 = false and i05 = false and i04 = true and i03 = false and i02 = true and i01 = false and i00 = false) implies (t26 = false and t25 = false and t24 = false and t23 = false and t22 = false and t21 = false and t20 = false and t19 = false and t18 = false and t17 = false and t16 = false and t15 = false and t14 = false and t13 = false and t12 = false and t11 = false and t10 = false and t09 = false and t08 = false and t07 = false and t06 = s26 and t05 = s25 and t04 = s24 and t03 = s23 and t02 = s22 and t01 = s21 and t00 = Or[s00,Or[s01,Or[s02,Or[s03,Or[s04,Or[s05,Or[s06,Or[s07,Or[s08,Or[s09,Or[s10,Or[s11,Or[s12,Or[s13,Or[s14,Or[s15,Or[s16,Or[s17,Or[s18,Or[s19,s20]]]]]]]]]]]]]]]]]]]])																			
																				else
																					(i07 = false and i06 = false and i05 = false and i04 = true and i03 = false and i02 = true and i01 = false and i00 = true) implies (t26 = false and t25 = false and t24 = false and t23 = false and t22 = false and t21 = false and t20 = false and t19 = false and t18 = false and t17 = false and t16 = false and t15 = false and t14 = false and t13 = false and t12 = false and t11 = false and t10 = false and t09 = false and t08 = false and t07 = false and t06 = false and t05 = s26 and t04 = s25 and t03 = s24 and t02 = s23 and t01 = s22 and t00 = Or[s00,Or[s01,Or[s02,Or[s03,Or[s04,Or[s05,Or[s06,Or[s07,Or[s08,Or[s09,Or[s10,Or[s11,Or[s12,Or[s13,Or[s14,Or[s15,Or[s16,Or[s17,Or[s18,Or[s19,Or[s20,s21]]]]]]]]]]]]]]]]]]]]])																			
																					else
																						(i07 = false and i06 = false and i05 = false and i04 = true and i03 = false and i02 = true and i01 = true and i00 = false) implies (t26 = false and t25 = false and t24 = false and t23 = false and t22 = false and t21 = false and t20 = false and t19 = false and t18 = false and t17 = false and t16 = false and t15 = false and t14 = false and t13 = false and t12 = false and t11 = false and t10 = false and t09 = false and t08 = false and t07 = false and t06 = false and t05 = false and t04 = s26 and t03 = s25 and t02 = s24 and t01 = s23 and t00 = Or[s00,Or[s01,Or[s02,Or[s03,Or[s04,Or[s05,Or[s06,Or[s07,Or[s08,Or[s09,Or[s10,Or[s11,Or[s12,Or[s13,Or[s14,Or[s15,Or[s16,Or[s17,Or[s18,Or[s19,Or[s20,Or[s21,s22]]]]]]]]]]]]]]]]]]]]]])																			
																						else
																							(i07 = false and i06 = false and i05 = false and i04 = true and i03 = false and i02 = true and i01 = true and i00 = true) implies (t26 = false and t25 = false and t24 = false and t23 = false and t22 = false and t21 = false and t20 = false and t19 = false and t18 = false and t17 = false and t16 = false and t15 = false and t14 = false and t13 = false and t12 = false and t11 = false and t10 = false and t09 = false and t08 = false and t07 = false and t06 = false and t05 = false and t04 = false and t03 = s26 and t02 = s25 and t01 = s24 and t00 = Or[s00,Or[s01,Or[s02,Or[s03,Or[s04,Or[s05,Or[s06,Or[s07,Or[s08,Or[s09,Or[s10,Or[s11,Or[s12,Or[s13,Or[s14,Or[s15,Or[s16,Or[s17,Or[s18,Or[s19,Or[s20,Or[s21,Or[s22,s23]]]]]]]]]]]]]]]]]]]]]]])																			
																							else
																								(t26 = false and t25 = false and t24 = false and t23 = false and t22 = false and t21 = false and t20 = false and t19 = false and t18 = false and t17 = false and t16 = false and t15 = false and t14 = false and t13 = false and t12 = false and t11 = false and t10 = false and t09 = false and t08 = false and t07 = false and t06 = false and t05 = false and t04 = false and t03 = false and t02 = false and t01 = false and t00 = false)																			
}	
					


pred normalize[	s30,s29,s28,s27,s26,s25,s24,s23,ir23,ir22,ir21,ir20,ir19,ir18,ir17,ir16,ir15,ir14,ir13,ir12,ir11,ir10,ir09,ir08,ir07,ir06,ir05,ir04,ir03,ir02,ir01,ir00,
							r30,r29,r28,r27,r26,r25,r24,r23,r22,r21,r20,r19,r18,r17,r16,r15,r14,r13,r12,r11,r10,r09,r08,r07,r06,r05,r04,r03,r02,r01,r00 : boolean]{
	some i30, i29, i28, i27, i26, i25, i24, i23 : boolean |	
	(	(ir23=true) implies (i30=false and i29=false and i28=false and i27=false and i26=false and i25=false and i24=false and i23=false and r22=ir22 and r21=ir21 and r20=ir20 and r19=ir19 and r18=ir18 and r17=ir17 and r16=ir16 and r15=ir15 and r14=ir14 and r13=ir13 and r12=ir12 and r11=ir11 and r10=ir10 and r09=ir09 and r08=ir08 and r07=ir07 and r06=ir06 and r05=ir05 and r04=ir04 and r03=ir03 and r02=ir02 and r01=ir01 and r00=ir00)
		else
			(ir23=false and ir22=true) implies (i30=false and i29=false and i28=false and i27=false and i26=false and i25=false and i24=false and i23=true and r22=ir21 and r21=ir20 and r20=ir19 and r19=ir18 and r18=ir17 and r17=ir16 and r16=ir15 and r15=ir14 and r14=ir13 and r13=ir12 and r12=ir11 and r11=ir10 and r10=ir09 and r09=ir08 and r08=ir07 and r07=ir06 and r06=ir05 and r05=ir04 and r04=ir03 and r03=ir02 and r02=ir01 and r01=ir00 and r00=false)
			else
				(ir23=false and ir22=false and ir21=true) implies (i30=false and i29=false and i28=false and i27=false and i26=false and i25=false and i24=true and i23=false and r22=ir20 and r21=ir19 and r20=ir18 and r19=ir17 and r18=ir16 and r17=ir15 and r16=ir14 and r15=ir13 and r14=ir12 and r13=ir11 and r12=ir10 and r11=ir09 and r10=ir08 and r09=ir07 and r08=ir06 and r07=ir05 and r06=ir04 and r05=ir03 and r04=ir02 and r03=ir01 and r02=ir00 and r01=false and r00=false)			
				else
					(ir23=false and ir22=false and ir21=false and ir20=true) implies (i30=false and i29=false and i28=false and i27=false and i26=false and i25=false and i24=true and i23=true and r22=ir19 and r21=ir18 and r20=ir17 and r19=ir16 and r18=ir15 and r17=ir14 and r16=ir13 and r15=ir12 and r14=ir11 and r13=ir10 and r12=ir09 and r11=ir08 and r10=ir07 and r09=ir06 and r08=ir05 and r07=ir04 and r06=ir03 and r05=ir02 and r04=ir01 and r03=ir00 and r02=false and r01=false and r00=false)			
					else
						(ir23=false and ir22=false and ir21=false and ir20=false and ir19=true) implies (i30=false and i29=false and i28=false and i27=false and i26=false and i25=true and i24=false and i23=false and r22=ir18 and r21=ir17 and r20=ir16 and r19=ir15 and r18=ir14 and r17=ir13 and r16=ir12 and r15=ir11 and r14=ir10 and r13=ir09 and r12=ir08 and r11=ir07 and r10=ir06 and r09=ir05 and r08=ir04 and r07=ir03 and r06=ir02 and r05=ir01 and r04=ir00 and r03=false and r02=false and r01=false and r00=false)									
						else
							(ir23=false and ir22=false and ir21=false and ir20=false and ir19=true) implies (i30=false and i29=false and i28=false and i27=false and i26=false and i25=true and i24=false and i23=false and r22=ir18 and r21=ir17 and r20=ir16 and r19=ir15 and r18=ir14 and r17=ir13 and r16=ir12 and r15=ir11 and r14=ir10 and r13=ir09 and r12=ir08 and r11=ir07 and r10=ir06 and r09=ir05 and r08=ir04 and r07=ir03 and r06=ir02 and r05=ir01 and r04=ir00 and r03=false and r02=false and r01=false and r00=false)																
							else
								(ir23=false and ir22=false and ir21=false and ir20=false and ir19=false and ir18=true) implies (i30=false and i29=false and i28=false and i27=false and i26=false and i25=true and i24=false and i23=true and r22=ir17 and r21=ir16 and r20=ir15 and r19=ir14 and r18=ir13 and r17=ir12 and r16=ir11 and r15=ir10 and r14=ir09 and r13=ir08 and r12=ir07 and r11=ir06 and r10=ir05 and r09=ir04 and r08=ir03 and r07=ir02 and r06=ir01 and r05=ir00 and r04=false and r03=false and r02=false and r01=false and r00=false)																								
								else
									(ir23=false and ir22=false and ir21=false and ir20=false and ir19=false and ir18=false and ir17=true) implies (i30=false and i29=false and i28=false and i27=false and i26=false and i25=true and i24=true and i23=false and r22=ir16 and r21=ir15 and r20=ir14 and r19=ir13 and r18=ir12 and r17=ir11 and r16=ir10 and r15=ir09 and r14=ir08 and r13=ir07 and r12=ir06 and r11=ir05 and r10=ir04 and r09=ir03 and r08=ir02 and r07=ir01 and r06=ir00 and r05=false and r04=false and r03=false and r02=false and r01=false and r00=false)																								
									else
										(ir23=false and ir22=false and ir21=false and ir20=false and ir19=false and ir18=false and ir17=false and ir16=true) implies (i30=false and i29=false and i28=false and i27=false and i26=false and i25=true and i24=true and i23=true and r22=ir15 and r21=ir14 and r20=ir13 and r19=ir12 and r18=ir11 and r17=ir10 and r16=ir09 and r15=ir08 and r14=ir07 and r13=ir06 and r12=ir05 and r11=ir04 and r10=ir03 and r09=ir02 and r08=ir01 and r07=ir00 and r06=false and r05=false and r04=false and r03=false and r02=false and r01=false and r00=false)																								
										else
											(ir23=false and ir22=false and ir21=false and ir20=false and ir19=false and ir18=false and ir17=false and ir16=false and ir15=true) implies (i30=false and i29=false and i28=false and i27=false and i26=true and i25=false and i24=false and i23=false and r22=ir14 and r21=ir13 and r20=ir12 and r19=ir11 and r18=ir10 and r17=ir09 and r16=ir08 and r15=ir07 and r14=ir06 and r13=ir05 and r12=ir04 and r11=ir03 and r10=ir02 and r09=ir01 and r08=ir00 and r07=false and r06=false and r05=false and r04=false and r03=false and r02=false and r01=false and r00=false)																								
											else
												(ir23=false and ir22=false and ir21=false and ir20=false and ir19=false and ir18=false and ir17=false and ir16=false and ir15=false and ir14=true) implies (i30=false and i29=false and i28=false and i27=false and i26=true and i25=false and i24=false and i23=true and r22=ir13 and r21=ir12 and r20=ir11 and r19=ir10 and r18=ir09 and r17=ir08 and r16=ir07 and r15=ir06 and r14=ir05 and r13=ir04 and r12=ir03 and r11=ir02 and r10=ir01 and r09=ir00 and r08=false and r07=false and r06=false and r05=false and r04=false and r03=false and r02=false and r01=false and r00=false)																								
												else
													(ir23=false and ir22=false and ir21=false and ir20=false and ir19=false and ir18=false and ir17=false and ir16=false and ir15=false and ir14=false and ir13=true) implies (i30=false and i29=false and i28=false and i27=false and i26=true and i25=false and i24=true and i23=false and r22=ir12 and r21=ir11 and r20=ir10 and r19=ir09 and r18=ir08 and r17=ir07 and r16=ir06 and r15=ir05 and r14=ir04 and r13=ir03 and r12=ir02 and r11=ir01 and r10=ir00 and r09=false and r08=false and r07=false and r06=false and r05=false and r04=false and r03=false and r02=false and r01=false and r00=false)																								
													else
														(ir23=false and ir22=false and ir21=false and ir20=false and ir19=false and ir18=false and ir17=false and ir16=false and ir15=false and ir14=false and ir13=false and ir12=true) implies (i30=false and i29=false and i28=false and i27=false and i26=true and i25=false and i24=true and i23=true and r22=ir11 and r21=ir10 and r20=ir09 and r19=ir08 and r18=ir07 and r17=ir06 and r16=ir05 and r15=ir04 and r14=ir03 and r13=ir02 and r12=ir01 and r11=ir00 and r10=false and r09=false and r08=false and r07=false and r06=false and r05=false and r04=false and r03=false and r02=false and r01=false and r00=false)																								
														else
															(ir23=false and ir22=false and ir21=false and ir20=false and ir19=false and ir18=false and ir17=false and ir16=false and ir15=false and ir14=false and ir13=false and ir12=false and ir11=true) implies (i30=false and i29=false and i28=false and i27=false and i26=true and i25=true and i24=false and i23=false and r22=ir10 and r21=ir09 and r20=ir08 and r19=ir07 and r18=ir06 and r17=ir05 and r16=ir04 and r15=ir03 and r14=ir02 and r13=ir01 and r12=ir00 and r11=false and r10=false and r09=false and r08=false and r07=false and r06=false and r05=false and r04=false and r03=false and r02=false and r01=false and r00=false)																								
															else
																(ir23=false and ir22=false and ir21=false and ir20=false and ir19=false and ir18=false and ir17=false and ir16=false and ir15=false and ir14=false and ir13=false and ir12=false and ir11=false and ir10=true) implies (i30=false and i29=false and i28=false and i27=false and i26=true and i25=true and i24=false and i23=true and r22=ir09 and r21=ir08 and r20=ir07 and r19=ir06 and r18=ir05 and r17=ir04 and r16=ir03 and r15=ir02 and r14=ir01 and r13=ir00 and r12=false and r11=false and r10=false and r09=false and r08=false and r07=false and r06=false and r05=false and r04=false and r03=false and r02=false and r01=false and r00=false)																								
																else
																	(ir23=false and ir22=false and ir21=false and ir20=false and ir19=false and ir18=false and ir17=false and ir16=false and ir15=false and ir14=false and ir13=false and ir12=false and ir11=false and ir10=false and ir09=true) implies (i30=false and i29=false and i28=false and i27=false and i26=true and i25=true and i24=true and i23=false and r22=ir08 and r21=ir07 and r20=ir06 and r19=ir05 and r18=ir04 and r17=ir03 and r16=ir02 and r15=ir01 and r14=ir00 and r13=false and r12=false and r11=false and r10=false and r09=false and r08=false and r07=false and r06=false and r05=false and r04=false and r03=false and r02=false and r01=false and r00=false)																								
																	else
																		(ir23=false and ir22=false and ir21=false and ir20=false and ir19=false and ir18=false and ir17=false and ir16=false and ir15=false and ir14=false and ir13=false and ir12=false and ir11=false and ir10=false and ir09=false and ir08=true) implies (i30=false and i29=false and i28=false and i27=false and i26=true and i25=true and i24=true and i23=true and r22=ir07 and r21=ir06 and r20=ir05 and r19=ir04 and r18=ir03 and r17=ir02 and r16=ir01 and r15=ir00 and r14=false and r13=false and r12=false and r11=false and r10=false and r09=false and r08=false and r07=false and r06=false and r05=false and r04=false and r03=false and r02=false and r01=false and r00=false)																								
																		else
																			(ir23=false and ir22=false and ir21=false and ir20=false and ir19=false and ir18=false and ir17=false and ir16=false and ir15=false and ir14=false and ir13=false and ir12=false and ir11=false and ir10=false and ir09=false and ir08=false and ir07=true) implies (i30=false and i29=false and i28=false and i27=true and i26=false and i25=false and i24=false and i23=false and r22=ir06 and r21=ir05 and r20=ir04 and r19=ir03 and r18=ir02 and r17=ir01 and r16=ir00 and r15=false and r14=false and r13=false and r12=false and r11=false and r10=false and r09=false and r08=false and r07=false and r06=false and r05=false and r04=false and r03=false and r02=false and r01=false and r00=false)																								
																			else
																				(ir23=false and ir22=false and ir21=false and ir20=false and ir19=false and ir18=false and ir17=false and ir16=false and ir15=false and ir14=false and ir13=false and ir12=false and ir11=false and ir10=false and ir09=false and ir08=false and ir07=false and ir06=true) implies (i30=false and i29=false and i28=false and i27=true and i26=false and i25=false and i24=false and i23=true and r22=ir05 and r21=ir04 and r20=ir03 and r19=ir02 and r18=ir01 and r17=ir00 and r16=false and r15=false and r14=false and r13=false and r12=false and r11=false and r10=false and r09=false and r08=false and r07=false and r06=false and r05=false and r04=false and r03=false and r02=false and r01=false and r00=false)																								
																				else
																					(ir23=false and ir22=false and ir21=false and ir20=false and ir19=false and ir18=false and ir17=false and ir16=false and ir15=false and ir14=false and ir13=false and ir12=false and ir11=false and ir10=false and ir09=false and ir08=false and ir07=false and ir06=false and ir05=true) implies (i30=false and i29=false and i28=false and i27=true and i26=false and i25=false and i24=true and i23=false and r22=ir04 and r21=ir03 and r20=ir02 and r19=ir01 and r18=ir00 and r17=false and r16=false and r15=false and r14=false and r13=false and r12=false and r11=false and r10=false and r09=false and r08=false and r07=false and r06=false and r05=false and r04=false and r03=false and r02=false and r01=false and r00=false)																								
																					else
																						(ir23=false and ir22=false and ir21=false and ir20=false and ir19=false and ir18=false and ir17=false and ir16=false and ir15=false and ir14=false and ir13=false and ir12=false and ir11=false and ir10=false and ir09=false and ir08=false and ir07=false and ir06=false and ir05=false and ir04=true) implies (i30=false and i29=false and i28=false and i27=true and i26=false and i25=false and i24=true and i23=true and r22=ir03 and r21=ir02 and r20=ir01 and r19=ir00 and r18=false and r17=false and r16=false and r15=false and r14=false and r13=false and r12=false and r11=false and r10=false and r09=false and r08=false and r07=false and r06=false and r05=false and r04=false and r03=false and r02=false and r01=false and r00=false)																								
																						else
																							(ir23=false and ir22=false and ir21=false and ir20=false and ir19=false and ir18=false and ir17=false and ir16=false and ir15=false and ir14=false and ir13=false and ir12=false and ir11=false and ir10=false and ir09=false and ir08=false and ir07=false and ir06=false and ir05=false and ir04=false and ir03=true) implies (i30=false and i29=false and i28=false and i27=true and i26=false and i25=true and i24=false and i23=false and r22=ir02 and r21=ir01 and r20=ir00 and r19=false and r18=false and r17=false and r16=false and r15=false and r14=false and r13=false and r12=false and r11=false and r10=false and r09=false and r08=false and r07=false and r06=false and r05=false and r04=false and r03=false and r02=false and r01=false and r00=false)																								
																							else
																								(ir23=false and ir22=false and ir21=false and ir20=false and ir19=false and ir18=false and ir17=false and ir16=false and ir15=false and ir14=false and ir13=false and ir12=false and ir11=false and ir10=false and ir09=false and ir08=false and ir07=false and ir06=false and ir05=false and ir04=false and ir03=false and ir02=true) implies (i30=false and i29=false and i28=false and i27=true and i26=false and i25=true and i24=false and i23=true and r22=ir01 and r21=ir00 and r20=false and r19=false and r18=false and r17=false and r16=false and r15=false and r14=false and r13=false and r12=false and r11=false and r10=false and r09=false and r08=false and r07=false and r06=false and r05=false and r04=false and r03=false and r02=false and r01=false and r00=false)																								
																								else
																									(ir23=false and ir22=false and ir21=false and ir20=false and ir19=false and ir18=false and ir17=false and ir16=false and ir15=false and ir14=false and ir13=false and ir12=false and ir11=false and ir10=false and ir09=false and ir08=false and ir07=false and ir06=false and ir05=false and ir04=false and ir03=false and ir02=false and ir01=true) implies (i30=false and i29=false and i28=false and i27=true and i26=false and i25=true and i24=true and i23=false and r22=ir00 and r21=false and r20=false and r19=false and r18=false and r17=false and r16=false and r15=false and r14=false and r13=false and r12=false and r11=false and r10=false and r09=false and r08=false and r07=false and r06=false and r05=false and r04=false and r03=false and r02=false and r01=false and r00=false)																								
																									else
/*es posible el ultimo caso?*/															(i30=false and i29=false and i28=false and i27=true and i26=false and i25=true and i24=true and i23=true and r22=false and r21=false and r20=false and r19=false and r18=false and r17=false and r16=false and r15=false and r14=false and r13=false and r12=false and r11=false and r10=false and r09=false and r08=false and r07=false and r06=false and r05=false and r04=false and r03=false and r02=false and r01=false and r00=false)				
	)
	and
	booleanwiseSub8[s30,s29,s28,s27,s26,s25,s24,s23,i30, i29, i28, i27, i26, i25, i24, i23, r30,r29,r28,r27,r26,r25,r24,r23]																	
}


pred isBitNormalized[b31, b30,b29,b28,b27,b26,b25,b24,b23,b22,b21,b20,b19,b18,b17,b16,b15,b14,b13,b12,b11,b10,b09,b08,b07,b06,b05,b04,b03,b02,b01,b00 : boolean]{
	--!isZero
	(true in b30+b29+b28+b27+b26+b25+b24+b23+b22+b21+b20+b19+b18+b17+b16+b15+b14+b13+b12+b11+b10+b09+b08+b07+b06+b05+b04+b03+b02+b01+b00) 
	and 
	--!isInfinity
	(b30+b29+b28+b27+b26+b25+b24+b23 = true implies true in b22+b21+b20+b19+b18+b17+b16+b15+b14+b13+b12+b11+b10+b09+b08+b07+b06+b05+b04+b03+b02+b01+b00) 
	and 
	--!pred_java_primitive_float_value_is_NaN
	(b30+b29+b28+b27+b26+b25+b24+b23 = true implies false = b22+b21+b20+b19+b18+b17+b16+b15+b14+b13+b12+b11+b10+b09+b08+b07+b06+b05+b04+b03+b02+b01+b00) 
		
}




pred isBitZero[b31, b30,b29,b28,b27,b26,b25,b24,b23,b22,b21,b20,b19,b18,b17,b16,b15,b14,b13,b12,b11,b10,b09,b08,b07,b06,b05,b04,b03,b02,b01,b00 : boolean]{
	(b30=false and b29=false and b28=false and b27=false and b26=false and b25=false and b24=false and b23=false and b22=false and b21=false and b20=false and b19=false and b18=false and b17=false and b16=false and b15=false and b14=false and b13=false and b12=false and b11=false and b10=false and b09=false and b08=false and b07=false and b06=false and b05=false and b04=false and b03=false and b02=false and b01=false and b00=false)
}

pred isBitInfinity[b31, b30,b29,b28,b27,b26,b25,b24,b23,b22,b21,b20,b19,b18,b17,b16,b15,b14,b13,b12,b11,b10,b09,b08,b07,b06,b05,b04,b03,b02,b01,b00 : boolean]{
	(b30=true and b29=true and b28=true and b27=true and b26=true and b25=true and b24=true and b23=true and b22=false and b21=false and b20=false and b19=false and b18=false and b17=false and b16=false and b15=false and b14=false and b13=false and b12=false and b11=false and b10=false and b09=false and b08=false and b07=false and b06=false and b05=false and b04=false and b03=false and b02=false and b01=false and b00=false)
}

pred isBitNaN[b31, b30,b29,b28,b27,b26,b25,b24,b23,b22,b21,b20,b19,b18,b17,b16,b15,b14,b13,b12,b11,b10,b09,b08,b07,b06,b05,b04,b03,b02,b01,b00 : boolean]{
	(b30=true and b29=true and b28=true and b27=true and b26=true and b25=true and b24=true and b23=true) and (b22=true or b21=true or b20=true or b19=true or b18=true or b17=true or b16=true or b15=true or b14=true or b13=true or b12=true or b11=true or b10=true or b09=true or b08=true or b07=true or b06=true or b05=true or b04=true or b03=true or b02=true or b01=true or b00=true)
}




pred booleanwiseAddOne27bits[	b26,b25,b24,b23,b22,b21,b20,b19,b18,b17,b16,b15,b14,b13,b12,b11,b10,b09,b08,b07,b06,b05,b04,b03,b02,b01,b00,
										overflow, r26,r25,r24,r23,r22,r21,r20,r19,r18,r17,r16,r15,r14,r13,r12,r11,r10,r09,r08,r07,r06,r05,r04,r03,r02,r01,r00 : boolean]
{

   let s_0 = Not[b00] |
   let s_1 = Xor[b01, b00] |
   let c_2 = And[b00, b01] |
   let s_2 = Xor[b02, c_2] |
   let c_3 = And[c_2, b02] |
   let s_3 = Xor[b03, c_3] | 
   let c_4 = And[c_3, b03] | 
   let s_4 = Xor[b04, c_4] | 
   let c_5 = And[c_4, b04] | 
   let s_5 = Xor[b05, c_5] | 
   let c_6 = And[c_5, b05] | 
   let s_6 = Xor[b06, c_6] | 
   let c_7 = And[c_6, b06] | 
   let s_7 = Xor[b07, c_7] | 
   let c_8 = And[c_7, b07] | 
   let s_8 = Xor[b08, c_8] | 
   let c_9 = And[c_8, b08] | 
   let s_9 = Xor[b09, c_9] | 
   let c_10 = And[c_9, b09] | 
   let s_10 = Xor[b10, c_10] | 
   let c_11 = And[c_10, b10] | 
   let s_11 = Xor[b11, c_11] | 
   let c_12 = And[c_11, b11] | 
   let s_12 = Xor[b12, c_12] | 
   let c_13 = And[c_12, b12] | 
   let s_13 = Xor[b13, c_13] | 
   let c_14 = And[c_13, b13] | 
   let s_14 = Xor[b14, c_14] | 
   let c_15 = And[c_14, b14] | 
   let s_15 = Xor[b15, c_15] | 
   let c_16 = And[c_15, b15] | 
   let s_16 = Xor[b16, c_16] | 
   let c_17 = And[c_16, b16] | 
   let s_17 = Xor[b17, c_17] | 
   let c_18 = And[c_17, b17] | 
   let s_18 = Xor[b18, c_18] | 
   let c_19 = And[c_18, b18] | 
   let s_19 = Xor[b19, c_19] | 
   let c_20 = And[c_19, b19] | 
   let s_20 = Xor[b20, c_20] | 
   let c_21 = And[c_20, b20] | 
   let s_21 = Xor[b21, c_21] | 
   let c_22 = And[c_21, b21] | 
   let s_22 = Xor[b22, c_22] | 
   let c_23 = And[c_22, b22] | 
   let s_23 = Xor[b23, c_23] |
   let c_24 = And[c_23, b23] | 
   let s_24 = Xor[b24, c_24] |
   let c_25 = And[c_24, b24] | 
   let s_25 = Xor[b25, c_25] |
   let c_26 = And[c_25, b25] | 
   let s_26 = Xor[b26, c_26] |
   let c_27 = And[c_26, b26] | 
      r00 in s_0 and
      r01 in s_1 and
      r02 in s_2 and
      r03 in s_3 and
      r04 in s_4 and
      r05 in s_5 and
      r06 in s_6 and
      r07 in s_7 and
      r08 in s_8 and
      r09 in s_9 and
      r10 in s_10 and
      r11 in s_11 and
      r12 in s_12 and
      r13 in s_13 and
      r14 in s_14 and
      r15 in s_15 and
      r16 in s_16 and
      r17 in s_17 and
      r18 in s_18 and
      r19 in s_19 and
      r20 in s_20 and
      r21 in s_21 and
      r22 in s_22 and
      r23 in s_23 and
      r24 in s_24 and
      r25 in s_25 and
      r26 in s_26 and
		overflow = c_27
}

pred booleanwiseGT24[a23,a22,a21,a20,a19,a18,a17,a16,a15,a14,a13,a12,a11,a10,a09,a08,a07,a06,a05,a04,a03,a02,a01,a00,
								b23,b22,b21,b20,b19,b18,b17,b16,b15,b14,b13,b12,b11,b10,b09,b08,b07,b06,b05,b04,b03,b02,b01,b00 : boolean] {
   (a23 in true and b23 in false) 
   or (a23 = b23) and (a22 in true and b22 in false) 
   or (a23 = b23) and (a22 = b22) and (a21 in true and b21 in false) 
   or (a23 = b23) and (a22 = b22) and (a21 = b21) and (a20 in true and b20 in false) 
   or (a23 = b23) and (a22 = b22) and (a21 = b21) and (a20 = b20) and (a19 in true and b19 in false) 
   or (a23 = b23) and (a22 = b22) and (a21 = b21) and (a20 = b20) and (a19 = b19) and (a18 in true and b18 in false) 
   or (a23 = b23) and (a22 = b22) and (a21 = b21) and (a20 = b20) and (a19 = b19) and (a18 = b18) and (a17 in true and b17 in false) 
   or (a23 = b23) and (a22 = b22) and (a21 = b21) and (a20 = b20) and (a19 = b19) and (a18 = b18) and (a17 = b17) and (a16 in true and b16 in false) 
   or (a23 = b23) and (a22 = b22) and (a21 = b21) and (a20 = b20) and (a19 = b19) and (a18 = b18) and (a17 = b17) and (a16 = b16) and (a15 in true and b15 in false) 
   or (a23 = b23) and (a22 = b22) and (a21 = b21) and (a20 = b20) and (a19 = b19) and (a18 = b18) and (a17 = b17) and (a16 = b16) and (a15 = b15) and (a14 in true and b14 in false) 
   or (a23 = b23) and (a22 = b22) and (a21 = b21) and (a20 = b20) and (a19 = b19) and (a18 = b18) and (a17 = b17) and (a16 = b16) and (a15 = b15) and (a14 = b14) and (a13 in true and b13 in false) 
   or (a23 = b23) and (a22 = b22) and (a21 = b21) and (a20 = b20) and (a19 = b19) and (a18 = b18) and (a17 = b17) and (a16 = b16) and (a15 = b15) and (a14 = b14) and (a13 = b13) and (a12 in true and b12 in false) 
   or (a23 = b23) and (a22 = b22) and (a21 = b21) and (a20 = b20) and (a19 = b19) and (a18 = b18) and (a17 = b17) and (a16 = b16) and (a15 = b15) and (a14 = b14) and (a13 = b13) and (a12 = b12) and (a11 in true and b11 in false) 
   or (a23 = b23) and (a22 = b22) and (a21 = b21) and (a20 = b20) and (a19 = b19) and (a18 = b18) and (a17 = b17) and (a16 = b16) and (a15 = b15) and (a14 = b14) and (a13 = b13) and (a12 = b12) and (a11 = b11) and (a10 in true and b10 in false) 
   or (a23 = b23) and (a22 = b22) and (a21 = b21) and (a20 = b20) and (a19 = b19) and (a18 = b18) and (a17 = b17) and (a16 = b16) and (a15 = b15) and (a14 = b14) and (a13 = b13) and (a12 = b12) and (a11 = b11) and (a10 = b10) and (a09 in true and b09 in false) 
   or (a23 = b23) and (a22 = b22) and (a21 = b21) and (a20 = b20) and (a19 = b19) and (a18 = b18) and (a17 = b17) and (a16 = b16) and (a15 = b15) and (a14 = b14) and (a13 = b13) and (a12 = b12) and (a11 = b11) and (a10 = b10) and (a09 = b09) and (a08 in true and b08 in false) 
   or (a23 = b23) and (a22 = b22) and (a21 = b21) and (a20 = b20) and (a19 = b19) and (a18 = b18) and (a17 = b17) and (a16 = b16) and (a15 = b15) and (a14 = b14) and (a13 = b13) and (a12 = b12) and (a11 = b11) and (a10 = b10) and (a09 = b09) and (a08 = b08) and (a07 in true and b07 in false) 
   or (a23 = b23) and (a22 = b22) and (a21 = b21) and (a20 = b20) and (a19 = b19) and (a18 = b18) and (a17 = b17) and (a16 = b16) and (a15 = b15) and (a14 = b14) and (a13 = b13) and (a12 = b12) and (a11 = b11) and (a10 = b10) and (a09 = b09) and (a08 = b08) and (a07 = b07) and (a06 in true and b06 in false) 
   or (a23 = b23) and (a22 = b22) and (a21 = b21) and (a20 = b20) and (a19 = b19) and (a18 = b18) and (a17 = b17) and (a16 = b16) and (a15 = b15) and (a14 = b14) and (a13 = b13) and (a12 = b12) and (a11 = b11) and (a10 = b10) and (a09 = b09) and (a08 = b08) and (a07 = b07) and (a06 = b06) and (a05 in true and b05 in false) 
   or (a23 = b23) and (a22 = b22) and (a21 = b21) and (a20 = b20) and (a19 = b19) and (a18 = b18) and (a17 = b17) and (a16 = b16) and (a15 = b15) and (a14 = b14) and (a13 = b13) and (a12 = b12) and (a11 = b11) and (a10 = b10) and (a09 = b09) and (a08 = b08) and (a07 = b07) and (a06 = b06) and (a05 = b05) and (a04 in true and b04 in false) 
   or (a23 = b23) and (a22 = b22) and (a21 = b21) and (a20 = b20) and (a19 = b19) and (a18 = b18) and (a17 = b17) and (a16 = b16) and (a15 = b15) and (a14 = b14) and (a13 = b13) and (a12 = b12) and (a11 = b11) and (a10 = b10) and (a09 = b09) and (a08 = b08) and (a07 = b07) and (a06 = b06) and (a05 = b05) and (a04 = b04) and (a03 in true and b03 in false) 
   or (a23 = b23) and (a22 = b22) and (a21 = b21) and (a20 = b20) and (a19 = b19) and (a18 = b18) and (a17 = b17) and (a16 = b16) and (a15 = b15) and (a14 = b14) and (a13 = b13) and (a12 = b12) and (a11 = b11) and (a10 = b10) and (a09 = b09) and (a08 = b08) and (a07 = b07) and (a06 = b06) and (a05 = b05) and (a04 = b04) and (a03 = b03) and (a02 in true and b02 in false) 
   or (a23 = b23) and (a22 = b22) and (a21 = b21) and (a20 = b20) and (a19 = b19) and (a18 = b18) and (a17 = b17) and (a16 = b16) and (a15 = b15) and (a14 = b14) and (a13 = b13) and (a12 = b12) and (a11 = b11) and (a10 = b10) and (a09 = b09) and (a08 = b08) and (a07 = b07) and (a06 = b06) and (a05 = b05) and (a04 = b04) and (a03 = b03) and (a02 = b02) and (a01 in true and b01 in false) 
   or (a23 = b23) and (a22 = b22) and (a21 = b21) and (a20 = b20) and (a19 = b19) and (a18 = b18) and (a17 = b17) and (a16 = b16) and (a15 = b15) and (a14 = b14) and (a13 = b13) and (a12 = b12) and (a11 = b11) and (a10 = b10) and (a09 = b09) and (a08 = b08) and (a07 = b07) and (a06 = b06) and (a05 = b05) and (a04 = b04) and (a03 = b03) and (a02 = b02) and (a01 = b01) and (a00 in true and b00 in false) 
}


pred roundsUp[b3,b2,b1,b0 : boolean]{
	(b2 = true and b1 = false and b0 = true) -- 5
	or 
	(b2 = true and b1 = true and b0 = false) -- 6
	or
	(b2 = true and b1 = true and b0 = true) --7
	or
	(b2 = true and b1 = false and b0 = false and b3 = true) -- 4 and least significand bit is 1
}

pred pred_java_primitive_float_value_mul_marker[
  left:  JavaPrimitiveFloatValue,
  right: JavaPrimitiveFloatValue,
  result:JavaPrimitiveFloatValue,
  overflow:boolean]
{
--marker predicate (empty body)
}


pred pred_java_primitive_float_value_div_marker[
  left: JavaPrimitiveFloatValue,
  right: JavaPrimitiveFloatValue,
  result: JavaPrimitiveFloatValue,
  remainder: JavaPrimitiveFloatValue]
{
--marker predicate (empty body)
}


pred pred_java_primitive_float_value_add_marker[
  left:  JavaPrimitiveFloatValue,
  right: JavaPrimitiveFloatValue,
  result:JavaPrimitiveFloatValue,
  overflow:boolean]
{
--marker predicate (empty body)
}

pred pred_java_primitive_float_value_sub_marker[
  left:  JavaPrimitiveFloatValue,
  right: JavaPrimitiveFloatValue,
  result:JavaPrimitiveFloatValue,
  overflow:boolean]
{
--marker predicate (empty body)
}

