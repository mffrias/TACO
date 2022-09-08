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
package ar.edu.taco.jml.fieldnames;

import org.jmlspecs.checker.JmlName;
import org.jmlspecs.checker.JmlStoreRefExpression;
import org.multijava.mjc.CClass;
import org.multijava.mjc.CField;
import org.multijava.mjc.CFieldAccessor;
import org.multijava.mjc.CType;
import org.multijava.mjc.JArrayAccessExpression;
import org.multijava.mjc.JClassFieldExpression;
import org.multijava.mjc.JExpression;
import org.multijava.mjc.JLocalVariableExpression;
import org.multijava.mjc.JThisExpression;
import org.multijava.mjc.JTypeNameExpression;
import org.multijava.mjc.JmlStoreRefExpressionExtension;

import ar.edu.taco.TacoNotImplementedYetException;
import ar.edu.taco.simplejml.helpers.JavaClassNameNormalizer;

public class FieldRenameUtil {
	public static String renamedName(CType type, String ident) {
		String prefixName = type.getSignature().replace("$", "_");
		JavaClassNameNormalizer javaClassNameNormalizer = new JavaClassNameNormalizer(prefixName);
		String prefixJDynAlloyName = javaClassNameNormalizer.getQualifiedClassName();
		return renamedName(prefixJDynAlloyName, ident);
	}

	public static String renamedName(String prefixJDynAlloyName, String ident) {
		String returnValue = prefixJDynAlloyName + "_" + ident;
		return returnValue;
	}


	//mfrias-mffrias-23-09-2012-JmlStoreRefExpression self ----> JmlStoreRefExpression[] self
	//mfrias-mffrias-23-09-2012-static JmlStoreRefExpression ----> static JmlStoreRefExpression[]

	public static JmlStoreRefExpression[] convertJmlStoreRefExpression(JmlStoreRefExpression[] self, String lastVisitedClass) {
		return convertJmlStoreRefExpression(self,lastVisitedClass, new FNExpressionVisitor(lastVisitedClass),false);
	}

	//mfrias-mffrias-23-09-2012-JmlStoreRefExpression self ----> JmlStoreRefExpression[] self
	//mfrias-mffrias-23-09-2012-static JmlStoreRefExpression ----> static JmlStoreRefExpression[]

	public static JmlStoreRefExpression[] convertJmlStoreRefExpression(JmlStoreRefExpression[] self, String lastVisitedClass, boolean forceIsField) {
		return convertJmlStoreRefExpression(self,lastVisitedClass, new FNExpressionVisitor(lastVisitedClass),true);
	}

	//mfrias-mffrias-23-09-2012-JmlStoreRefExpression self ----> JmlStoreRefExpression[] selfArray
	//mfrias-mffrias-23-09-2012-static JmlStoreRefExpression ----> static JmlStoreRefExpression[]	
	public static JmlStoreRefExpression[] convertJmlStoreRefExpression(JmlStoreRefExpression[] selfArray, String lastVisitedClass, FNExpressionVisitor visitor,boolean forceIsField) {
		JmlStoreRefExpression[] newSelfArray = new JmlStoreRefExpression[selfArray.length]; //mfrias
		for (int j = 0; j<selfArray.length; j++) { //mfrias

			JmlStoreRefExpression self = selfArray[j]; //mfrias
			boolean isField = false;
			boolean isWildcard = false;
			JmlName[] jmlNames = new JmlName[self.names().length];
			//for (int i = 0; i < self.names().length; i++) {
			JExpression expression = self.expression();
			
			
			for (int i = self.names().length-1; i >= 0; i--) {
				boolean mustBeRenamed;
				String oldName = self.names()[i].getName();
				if (oldName.equals("[*]") && expression instanceof JArrayAccessExpression) {
					jmlNames[i] = self.names()[i];
					mustBeRenamed = false;
					continue;
				} else if (expression instanceof JArrayAccessExpression) {
					expression = ((JArrayAccessExpression) expression).prefix();
				} 

				if (oldName.equals("*")) {					
					mustBeRenamed = false;
					isWildcard = true;
				} else if (expression instanceof JClassFieldExpression) {
					JClassFieldExpression classFieldExpression = (JClassFieldExpression) expression;
					if (classFieldExpression.getField().isStatic()) {
						mustBeRenamed = false;
					} else { 
						mustBeRenamed = true;
					}

					expression = classFieldExpression.prefix();
				}
				else if (expression instanceof JTypeNameExpression) {
					mustBeRenamed = false;
				} else if (expression instanceof JThisExpression) {
					mustBeRenamed = false;				
				} else if (expression instanceof JLocalVariableExpression) {
					mustBeRenamed = false;
				} else if (expression==null) {
					mustBeRenamed = true;
				} else {
					throw new TacoNotImplementedYetException("Unsupported JmlStoreRefExpression expression: " + expression.getClass().getName());
				}
				//				if (expression instanceof JFieldE|) {
				//				
				//				}

				//String oldName = self.names()[i].getName();
				//if ( !mustBeRenamed && (!self.names()[i].isFields() || oldName.equals("*"))) {
				if ( !mustBeRenamed ) {
					//leaves old value
					jmlNames[i] = self.names()[i];;

					//					if (oldName.equals("*")) {
					//						isWildcard = true;
					//					}
				} else {
					String newName = FieldRenameUtil.renamedName(lastVisitedClass, self.names()[i].getName());
					JmlName jmlName = new JmlName(self.getTokenReference(), newName);
					jmlNames[i] = jmlName;
					isField = true;
				}
			}

			JExpression myExpr;
			if (self.expression() == null) {
				myExpr = null;
			} else {
				self.expression().accept(visitor);
				myExpr = visitor.getArrayStack().pop();
			}
			String newName;
			if (isField && !isWildcard) {
				newName = FieldRenameUtil.renamedName(lastVisitedClass, self.getName());
			} else {
				//leaves old value
				newName = self.getName();
			}

			newSelfArray[j] = new JmlStoreRefExpression(self.getTokenReference(), jmlNames);//mfrias
		}
		return newSelfArray;//mfrias

	}

	static public String extractClassNameForFieldRenameSupport(CClass clazz) {
		String withDotAndnotSlashes = clazz.toString().replace("/", ".");
		String withoutDolarSymbol = withDotAndnotSlashes.replace("$", "_"); 
		JavaClassNameNormalizer classNameNormalizer = new JavaClassNameNormalizer(withoutDolarSymbol);		
		return classNameNormalizer.getQualifiedClassName();
	}
	static public String extractClassNameForFieldRenameSupport(CField self) {
		return extractClassNameForFieldRenameSupport(self.owner());
	}

	public static String extractClassNameForFieldRenameSupport(CFieldAccessor self) {
		return extractClassNameForFieldRenameSupport(self.owner());
	}


}
