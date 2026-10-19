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

import org.jmlspecs.checker.JmlReachExpression;
import org.jmlspecs.checker.JmlStoreRefExpression;
import org.multijava.mjc.JClassFieldExpression;
import org.multijava.mjc.JThisExpression;

import ar.edu.taco.simplejml.helpers.JavaClassNameNormalizer;
import ar.edu.taco.utils.jml.JmlAstClonerExpressionVisitor;

/**
 * 
 * 
 * @author diegodob
 * 
 */
public class FNExpressionVisitor extends JmlAstClonerExpressionVisitor {
	String currentClassName;
	/**
	 * Creates a visitor that renames the instance fields of expressions (see
	 * {@link FieldRenameUtil}); the result of each visit is on top of {@code getArrayStack()}.
	 *
	 * @param currentClassName the class being visited, in JDynAlloy form
	 *     ({@code FieldRenameUtil.extractClassNameForFieldRenameSupport}): the prefix of the
	 *     fields accessed through {@code this}
	 */
	public FNExpressionVisitor(String currentClassName) {
		this.currentClassName = currentClassName;
	}
	
	/**
	 * Renames an instance field access: {@code this.f} to {@code <currentClassName>_f}, and
	 * {@code e.f} for any other {@code e} to {@code <declaring class>_f}; static fields keep
	 * their names. The new access keeps the type and the field of the original. For
	 * {@code this.f} the current class is used even when {@code f} is inherited, while the
	 * declaration of {@code f} gets the declaring class's prefix
	 * ({@link FNBlockVisitor#visitJmlFieldDeclaration}).
	 *
	 * @param self the field access
	 */
	@Override
	public void visitFieldExpression(JClassFieldExpression self) {
		super.visitFieldExpression(self);
		if (!self.getField().isStatic()) {
			JClassFieldExpression fieldExpression = (JClassFieldExpression) this.getArrayStack().pop();
			String newFieldName ;
			if (fieldExpression.prefix() instanceof JThisExpression) {
				newFieldName = FieldRenameUtil.renamedName(this.currentClassName, fieldExpression.ident());
			} else {

				String ownerClassName = FieldRenameUtil.extractClassNameForFieldRenameSupport(self.getField());
				newFieldName = FieldRenameUtil.renamedName(ownerClassName, fieldExpression.ident());
				
			}
			JClassFieldExpression newSelf = new JClassFieldExpression(fieldExpression.getTokenReference(), fieldExpression.prefix(), newFieldName);
			newSelf.setType(fieldExpression.getType());
			newSelf.setField(fieldExpression.getField());
			this.getArrayStack().push(newSelf);
		}
	}
	
	
	
	/**
	 * Copies a {@code \reach} expression and renames the fields of its store references with the
	 * reach's reference type as prefix (the three-argument
	 * {@code FieldRenameUtil.convertJmlStoreRefExpression}).
	 *
	 * @param self the reach expression
	 */
	@Override
	public void visitJmlReachExpression(JmlReachExpression self) {		
		super.visitJmlReachExpression(self);
		JmlReachExpression visitedSelf = (JmlReachExpression) this.getArrayStack().pop();
	
		JavaClassNameNormalizer classNameNormalizer = new JavaClassNameNormalizer(visitedSelf.referenceType().toString());
		String normalizedClassName = classNameNormalizer.getQualifiedClassName();
//mfrias-mffrias-23-09-2012-visitedSelf.storeRefExpression() -----> visitedSelf.storeRefExpressions()
//mfrias-mffrias-23-09-2012-JmlStoreRefExpression jmlStoreRefExpression -----> JmlStoreRefExpression jmlStoreRefExpression[]

		JmlStoreRefExpression jmlStoreRefExpression[] = FieldRenameUtil.convertJmlStoreRefExpression(visitedSelf.storeRefExpressions(),normalizedClassName, true);
		JmlReachExpression newSelf = new  JmlReachExpression( self.getTokenReference(),visitedSelf.specExpression(),visitedSelf.referenceType(),jmlStoreRefExpression);
		this.getArrayStack().push(newSelf);
	}

}
