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
	
	public static MethodInformationBuilder getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new MethodInformationBuilder(); 
		}
		return INSTANCE;
	}
	
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
