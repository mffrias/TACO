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

/**
 * The names that {@link FNBlockVisitor} and {@link FNExpressionVisitor} give to instance fields:
 * {@code <class>_<field>}, where {@code <class>} is the class in JDynAlloy form, its package and
 * name joined by underscores ({@code JavaClassNameNormalizer}): field {@code f} of
 * {@code pkg.sub.C} becomes {@code pkg_sub_C_f}. Static fields keep their names. Static methods
 * only, no state.
 */
public class FieldRenameUtil {
	/**
	 * Returns {@code <class>_<ident>} for the class of {@code type}, from its signature with
	 * {@code $} as {@code _}. Not called anywhere: the passes use
	 * {@link #renamedName(String, String)}.
	 *
	 * @param type the type of the class that declares the field
	 * @param ident the field's name
	 * @return the new name of the field
	 */
	public static String renamedName(CType type, String ident) {
		String prefixName = type.getSignature().replace("$", "_");
		JavaClassNameNormalizer javaClassNameNormalizer = new JavaClassNameNormalizer(prefixName);
		String prefixJDynAlloyName = javaClassNameNormalizer.getQualifiedClassName();
		return renamedName(prefixJDynAlloyName, ident);
	}

	/**
	 * Returns {@code <prefixJDynAlloyName>_<ident>}.
	 *
	 * @param prefixJDynAlloyName the class in JDynAlloy form
	 *     ({@link #extractClassNameForFieldRenameSupport(CClass)})
	 * @param ident the field's name
	 * @return the new name of the field
	 */
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

	/**
	 * Same as the four-argument form, with a new {@link FNExpressionVisitor} for
	 * {@code lastVisitedClass}. {@code forceIsField} is not used.
	 *
	 * @param self the store references
	 * @param lastVisitedClass the prefix for every renamed field
	 * @param forceIsField not used
	 * @return the renamed store references
	 */
	public static JmlStoreRefExpression[] convertJmlStoreRefExpression(JmlStoreRefExpression[] self, String lastVisitedClass, boolean forceIsField) {
		return convertJmlStoreRefExpression(self,lastVisitedClass, new FNExpressionVisitor(lastVisitedClass),true);
	}

	//mfrias-mffrias-23-09-2012-JmlStoreRefExpression self ----> JmlStoreRefExpression[] selfArray
	//mfrias-mffrias-23-09-2012-static JmlStoreRefExpression ----> static JmlStoreRefExpression[]	
	/**
	 * Renames the instance fields named in store references (of {@code assignable} clauses,
	 * {@code represents} clauses and {@code \reach} expressions). Walks the names of each
	 * reference from last to first, following its expression: a name is renamed when its
	 * expression is a non-static field access, or when the reference has no expression; static
	 * fields, {@code this}, type names, local variables, {@code *} and {@code [*]} keep their
	 * names.
	 *
	 * <p>Every renamed name gets {@code lastVisitedClass} as prefix, whatever class declares the
	 * field. The new references are built from the names alone: the visited expression and the
	 * renamed {@code getName()} are computed but not used, and neither is {@code forceIsField}.
	 *
	 * @param selfArray the store references
	 * @param lastVisitedClass the prefix for every renamed field
	 * @param visitor the visitor applied to each reference's expression (its result is not used)
	 * @param forceIsField not used
	 * @return the renamed store references, one per input
	 * @throws TacoNotImplementedYetException for a reference whose expression is of another kind
	 */
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

	/**
	 * Returns the JDynAlloy form of a class name, the prefix of its fields' new names: the name
	 * with {@code /} as {@code .} and {@code $} as {@code _}, then package and name joined by
	 * underscores by {@code JavaClassNameNormalizer} ({@code pkg_sub_Outer_Inner}).
	 *
	 * @param clazz the class
	 * @return the class name in JDynAlloy form
	 */
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
