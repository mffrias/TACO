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
package ar.edu.taco.jml.cast;

import org.jmlspecs.checker.JmlPredicate;

import ar.edu.taco.jml.utils.SpecSimplifierClassBaseVisitor;

public class CastSClassVisitor extends SpecSimplifierClassBaseVisitor {
    
//	@Override
//	public void visitJmlInvariant(JmlInvariant self) {
//		JmlInvariant newSelf = new JmlInvariant(self.getTokenReference(), self.modifiers(), self.isRedundantly(), simplifyPredicateSupport(self.predicate()));
//		this.getStack().push(newSelf);
//
//	}
//	
//	@Override
//	public void visitJmlRepresentsDecl(JmlRepresentsDecl self) {
//		JmlPredicate newPredicate;
//		if (self.predicate() == null) {
//			newPredicate = null;
//		} else {
//			newPredicate = simplifyPredicateSupport(self.predicate());
//		}
//	    JmlRepresentsDecl newSelf = new JmlRepresentsDecl(self.getTokenReference(), self.modifiers(),self.isRedundantly(),self.storeRef(),newPredicate); 
//	    this.getStack().push(newSelf);
//	}
//	
//	@Override
//	public void visitJmlEnsuresClause(JmlEnsuresClause self) {
//		if (self.isNotSpecified()) {
//			throw new IllegalArgumentException("Ensures clause is not specified.");
//		}
//		JmlPredicate newPredicate = simplifyPredicateSupport(self.predOrNot());
//		JmlEnsuresClause newSelf = new JmlEnsuresClause(self.getTokenReference(),self.isRedundantly(),newPredicate);
//		
//		this.getStack().push(newSelf );
//	}		
//	
//	@Override
//	public void visitJmlRequiresClause(JmlRequiresClause self) {
//		if (self.isNotSpecified()) {
//			throw new IllegalArgumentException("Ensures clause is not specified.");
//		}
//		JmlPredicate newPredicate = simplifyPredicateSupport(self.predOrNot());
//		JmlRequiresClause newSelf = new JmlRequiresClause(self.getTokenReference(),self.isRedundantly(),newPredicate);
//		
//		this.getStack().push(newSelf );
//	}			
	
    	@Override
	protected JmlPredicate simplifyPredicateSupport(JmlPredicate predicate) {
		
		JmlPredicate accumulatedPredicate = predicate;
		CastSExpressionVisitor visitor = new CastSExpressionVisitor();
		accumulatedPredicate.accept(visitor);
		JmlPredicate newPredicate = (JmlPredicate) visitor.getArrayStack().pop();
		return newPredicate;
	}	
}

