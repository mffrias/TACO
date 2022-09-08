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
	public FNExpressionVisitor(String currentClassName) {
		this.currentClassName = currentClassName;
	}
	
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
