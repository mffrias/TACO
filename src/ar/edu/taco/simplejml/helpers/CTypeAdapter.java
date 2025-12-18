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

final public class CTypeAdapter {

	private JType alloyType;

	private String signatureId;

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

	public JType getAlloyType() {
		return alloyType;
	}

	public String getBaseSignatureId() {
		return signatureId;
	}

}
