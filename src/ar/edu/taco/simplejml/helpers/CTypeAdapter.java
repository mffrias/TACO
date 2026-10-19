/*
 * TACO: Translation of Annotated COde
 * Copyright (c) 2010 Universidad de Buenos Aires
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA,
 * 02110-1301, USA
 */
package ar.edu.taco.simplejml.helpers;

import org.multijava.mjc.CArrayType;
import org.multijava.mjc.CBooleanType;
import org.multijava.mjc.CClassNameType;
import org.multijava.mjc.CClassType;
import org.multijava.mjc.CNumericType;
import org.multijava.mjc.CType;
import org.multijava.mjc.CVoidType;
import org.multijava.mjc.Constants;

import ar.edu.jdynalloy.JDynAlloyConfig;
import ar.edu.jdynalloy.factory.JSignatureFactory;
import ar.edu.jdynalloy.factory.JTypeFactory;
import ar.edu.jdynalloy.xlator.JType;
import ar.edu.taco.TacoConfigurator;

/**
 * Translates the JML parser's types (MultiJava {@code CType}s) into JDynAlloy types
 * ({@code JType}): for parameters, fields, local variables, results and operands, during
 * the translation to JDynAlloy ({@code JDynAlloyASTVisitor}, {@code BlockStatementsVisitor},
 * {@code ExpressionVisitor}, {@link MethodDeclarationSolver}). The result depends on
 * {@code useJavaArithmetic}, and for JML model types on {@code JMLObjectSequenceToAlloySequence}
 * and {@code JMLObjectSetToAlloySet}, read at each call. The two fields are never set, so an
 * instance has no state that matters.
 */
final public class CTypeAdapter {

	private JType alloyType;

	private String signatureId;

	/**
	 * Returns the JDynAlloy type of a Java or JML type:
	 * <ul>
	 * <li>{@code boolean}: {@code boolean}.</li>
	 * <li>Numbers, without {@code useJavaArithmetic}: {@code int}, {@code byte}, {@code long},
	 *     {@code char} and {@code \bigint} are Alloy's {@code Int}. With it: {@code byte} and
	 *     {@code int} are {@code JavaPrimitiveIntegerValue}, {@code long}
	 *     {@code JavaPrimitiveLongValue}, {@code float} {@code JavaPrimitiveFloatValue} and
	 *     {@code char} {@code JavaPrimitiveCharValue}.</li>
	 * <li>One-dimensional arrays: {@code int[]} and {@code \bigint[]} are
	 *     {@code java_lang_IntArray+null}, {@code byte[]} and {@code long[]}
	 *     {@code java_lang_LongArray+null}, {@code char[]} {@code java_lang_CharArray+null},
	 *     arrays whose element type is a {@code CClassNameType} of a class
	 *     {@code java_lang_ObjectArray+null}. Two-dimensional: only {@code int[][]}, as
	 *     {@code java_lang_IntArray2d+null}.</li>
	 * <li>Class types: {@code JMLObjectSequence} and {@code JMLObjectSet} are Alloy sequences
	 *     and sets of {@code java_lang_Object} when their configuration key is on; any other
	 *     class {@code C} is {@code C+null}, with {@code C} written as
	 *     {@link JavaClassNameNormalizer} does. {@code JavaSignatures} is looked up with the
	 *     simple class name, which is never one of its keys, so it changes nothing.</li>
	 * <li>{@code void}: null.</li>
	 * </ul>
	 *
	 * @param ctype the type
	 * @return the JDynAlloy type, or null for {@code void}
	 * @throws IllegalArgumentException for any other type ({@code double}, {@code short},
	 *         {@code float} without Java arithmetic, other arrays, type variables, ...)
	 */
	public JType translate(CType ctype) {
		if (ctype instanceof CBooleanType) {
			return translateBoolean((CBooleanType) ctype);
		} else if (ctype instanceof CArrayType) {
			return translateArray((CArrayType) ctype);
		} else if (ctype instanceof CNumericType) {
			return translateNumeric((CNumericType) ctype);
		} else if (ctype instanceof CClassType) {
			return translateClass((CClassType) ctype);
		} else if (ctype instanceof CVoidType) {
			return translateVoid((CVoidType) ctype);
		} else {
			throw new IllegalArgumentException(ctype.getClass().getName()
					+ " is not supported.");
		}
	}

	private JType translateVoid(CVoidType ctype) {
		return null;
	}

	private JType translateClass(CClassType ctype) {
		JavaClassNameNormalizer javaClassNameNormalizer = new JavaClassNameNormalizer(
				ctype.getSignature());
		// String ident = getClassName(ctype.qualifiedName());
		String ident = javaClassNameNormalizer.getClassName();
		String signatureId;

		JType jType;
		if (ident.equals("JMLObjectSequence")
				&& JDynAlloyConfig.getInstance()
						.getJMLObjectSequenceToAlloySequence()) {
			jType = JTypeFactory.buildReferenceSeq("java_lang_Object");
		} else if (ident.equals("JMLObjectSet")
				&& JDynAlloyConfig.getInstance().getJMLObjectSetToAlloySet()) {
			jType = JTypeFactory.buildReferenceSet("java_lang_Object");

		} else if (JavaSignatures.getInstance().contains(ident)) {
			signatureId = JavaSignatures.getInstance().getSignatureId(ident);
			jType = JTypeFactory.buildReference(signatureId);
		} else {
			signatureId = javaClassNameNormalizer.getQualifiedClassName();
			jType = JTypeFactory.buildReference(signatureId);
		}

		return jType;
	}

	private JType translateNumeric(CNumericType ctype) {
		if (TacoConfigurator.getInstance().getUseJavaArithmetic() == false) {
			switch (ctype.getTypeID()) {
			case Constants.TID_INT:
			case Constants.TID_BYTE:
			case Constants.TID_LONG:
			case Constants.TID_CHAR:
			case org.jmlspecs.checker.Constants.TID_BIGINT:
				return JSignatureFactory.INT.getType();
			case Constants.TID_DOUBLE:
				throw new IllegalArgumentException("double type not supported");
			default:
				throw new IllegalArgumentException("TypeID: " + ctype.getTypeID() + ". Type "
						+ ctype.toString() + " not supported.");
			}
		} else {
			switch (ctype.getTypeID()) {
			case Constants.TID_BYTE:
			case Constants.TID_INT:
				return JSignatureFactory.JAVA_PRIMITIVE_INTEGER_VALUE;
			case Constants.TID_LONG:
				return JSignatureFactory.JAVA_PRIMITIVE_LONG_VALUE;
			case Constants.TID_FLOAT:
				return JSignatureFactory.JAVA_PRIMITIVE_FLOAT_VALUE;
			case Constants.TID_CHAR:
				return JSignatureFactory.JAVA_PRIMITIVE_CHAR_VALUE;
			case org.jmlspecs.checker.Constants.TID_BIGINT:
			case Constants.TID_DOUBLE:
			default:
				throw new IllegalArgumentException("TypeID: "
						+ ctype.getTypeID() + " type not supported");

			}
		}
	}

	private JType translateArray(CArrayType ctype) {
//		if (TacoConfigurator.getInstance().getUseJavaArithmetic() == true) {
		CType base_type = ctype.getBaseType();
		if (ctype.getArrayBound() == 1){
			if (base_type instanceof CNumericType) {
				switch (base_type.getTypeID()) {
				case Constants.TID_INT: 
					return JType.parse("java_lang_IntArray+null");
				case Constants.TID_BYTE:
				case Constants.TID_LONG:
					return JType.parse("java_lang_LongArray+null"); 
				case Constants.TID_CHAR:
					return JType.parse("java_lang_CharArray+null");
				case org.jmlspecs.checker.Constants.TID_BIGINT: 
					return JType.parse("java_lang_IntArray+null");
				case Constants.TID_DOUBLE:
					throw new IllegalArgumentException(
							"double type not supported");
				default:
					throw new IllegalArgumentException("TypeID: "
							+ base_type.getTypeID() + " type not supported");
				}
			} else if ((base_type instanceof CClassNameType)
//					&& ((CClassNameType) base_type).getCClass().toString()
					&& ((CClassNameType) base_type).isClassType()){
//					.equals("java/lang/Object")) {

				return JType.parse("java_lang_ObjectArray+null");
			} else {
				throw new IllegalArgumentException("base type " + base_type + " not supported");
			}
		} else {
			if (ctype.getArrayBound() == 2) {
				if (base_type instanceof CNumericType) {
					switch (base_type.getTypeID()) {
						case Constants.TID_INT:
							return JType.parse("java_lang_IntArray2d+null");
						default:
							throw new IllegalArgumentException("TypeID: "
									+ base_type.getTypeID() + " type not supported");
					}
				} else {
					throw new IllegalArgumentException("TypeID: "
							+ base_type.getTypeID() + " type not supported");
				}
			} else {
				throw new IllegalArgumentException("TypeID: "
						+ base_type.getTypeID() + " type not supported");
			}
		}
	}

	private JType translateBoolean(CBooleanType ctype) {
		return JSignatureFactory.BOOLEAN.getType();
	}

	/**
	 * Returns the field {@code alloyType}, which nothing sets.
	 *
	 * @return null
	 */
	public JType getAlloyType() {
		return alloyType;
	}

	/**
	 * Returns the field {@code signatureId}, which nothing sets ({@link #translate} assigns a
	 * local variable of the same name).
	 *
	 * @return null
	 */
	public String getBaseSignatureId() {
		return signatureId;
	}

}
