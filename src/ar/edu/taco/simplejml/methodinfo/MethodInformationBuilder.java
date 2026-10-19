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
package ar.edu.taco.simplejml.methodinfo;


import org.apache.commons.lang.StringUtils;
import org.multijava.mjc.CStdType;
import org.multijava.mjc.CType;
import org.multijava.mjc.JLocalVariableExpression;
import org.multijava.mjc.JMethodCallExpression;
import org.multijava.mjc.JNameExpression;

import ar.edu.taco.simplejml.helpers.JavaClassNameNormalizer;

/**
 * @author elgaby
 *
 */
public class MethodInformationBuilder {
	private static MethodInformationBuilder INSTANCE;
	
//	private Map<String, MethodInformation> methods;
	
	private MethodInformationBuilder() {
	}
	
	/**
	 * Returns the single builder, created on first use and kept for the whole JVM. It holds no
	 * other state, so sharing it between runs has no effect.
	 *
	 * @return the builder
	 */
	public static MethodInformationBuilder getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new MethodInformationBuilder(); 
		}
		return INSTANCE;
	}
	
	/**
	 * Returns what the translation needs to know about a called method: whether it is static or a
	 * constructor, its return type, and the class that declares it. There are three cases:
	 * <ul>
	 * <li>The parser resolved the method ({@code method()} is not null): static and constructor
	 *     come from the method, the return type from the call's type (left null if it has none),
	 *     and the receiver type is the method's receiver type through
	 *     {@code JavaClassNameNormalizer} ({@code pkg_Class}). Here {@code isModelMethod} is
	 *     computed as "no prefix and no method", which is always false.</li>
	 * <li>Not resolved, and the prefix is a local variable or a name: a model method, not static,
	 *     not a constructor, returning the call's type or {@code java.lang.Object}; no receiver
	 *     type.</li>
	 * <li>Otherwise the prefix is taken as a type: its printed form is split at {@code /}, each
	 *     part capitalized ({@code java/lang/Integer} gives {@code JavaLangInteger}), and the
	 *     {@link MethodInformationSupplier} {@code MI_<that>} of this package is instantiated by
	 *     reflection and asked about the method's name.</li>
	 * </ul>
	 * Callers: {@code ExpressionVisitor}, {@code ExpressionVisitorWithNewParamsInMethodCall},
	 * {@code ESExpressionVisitor} and {@code JMethodCallExpressionExtensiorForMethodInformation}.
	 *
	 * @param jMethodCallExpression the call
	 * @return the information; null if the supplier class of the third case does not exist or
	 *         cannot be instantiated (the stack trace is printed), which callers do not expect.
	 *         The third case throws a {@code NullPointerException} if the call has no prefix, and
	 *         the supplier throws {@code ar.edu.taco.TacoException} for a method it does not know
	 */
	public MethodInformation getMethodInformation(JMethodCallExpression jMethodCallExpression) {
		ConcreteMethodInformation concreteMethodInformation = null;
		
		if (jMethodCallExpression.method() != null) {
			concreteMethodInformation = new ConcreteMethodInformation();
			
			boolean isStatic = jMethodCallExpression.method().isStatic();
			boolean isModelMethod = (jMethodCallExpression.prefix() == null) && (jMethodCallExpression.method() == null);
			boolean isConstructor = jMethodCallExpression.method().isConstructor();
			
			concreteMethodInformation.setStatic(isStatic);
			concreteMethodInformation.setConstructor(isConstructor);
			concreteMethodInformation.setModelMethod(isModelMethod);
			if (jMethodCallExpression.getType() != null) {
				CType cType = jMethodCallExpression.getType();
				concreteMethodInformation.setReturnType(cType);
			}
			
			JavaClassNameNormalizer classNameNormalizer = new JavaClassNameNormalizer(jMethodCallExpression.method().receiverType().toVerboseString());
			concreteMethodInformation.setQualifiedReceiverType(classNameNormalizer.getQualifiedClassName());
			
		} else if (jMethodCallExpression.prefix() instanceof JLocalVariableExpression || jMethodCallExpression.prefix() instanceof JNameExpression) {
			concreteMethodInformation = new ConcreteMethodInformation();

			concreteMethodInformation.setStatic(false);
			concreteMethodInformation.setConstructor(false);
			concreteMethodInformation.setModelMethod(true);
			if (jMethodCallExpression.getType() != null) {
				CType cType = jMethodCallExpression.getType();
				concreteMethodInformation.setReturnType(cType);
			} else {
				
				concreteMethodInformation.setReturnType(CStdType.Object);
			}
			
		} else {
			String className = getCapitalizedClassName(jMethodCallExpression.prefix().toString());
			try {
				String packageName = MethodInformationSupplier.class.getPackage().getName();
				MethodInformationSupplier methodInformationSupplier = (MethodInformationSupplier) Class.forName(packageName +".MI_" + className).newInstance();
				concreteMethodInformation = methodInformationSupplier.getInformationForMethod(jMethodCallExpression.ident());
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		
		return concreteMethodInformation;
	}
	
	private String getCapitalizedClassName(String className) {
		StringBuffer capitalizedClassName = new StringBuffer();
		String[] classNameParts = className.split("/");
		
		for (String aString : classNameParts) {
			capitalizedClassName.append(StringUtils.capitalize(aString));
		}
		return capitalizedClassName.toString();
	}

}
