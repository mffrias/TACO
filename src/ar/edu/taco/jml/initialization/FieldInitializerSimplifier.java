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
package ar.edu.taco.jml.initialization;

import java.util.ArrayList;
import java.util.List;

import org.jmlspecs.checker.JmlAssignmentStatement;
import org.jmlspecs.checker.JmlClassDeclaration;
import org.jmlspecs.checker.JmlConstructorDeclaration;
import org.jmlspecs.checker.JmlFieldDeclaration;
import org.jmlspecs.checker.JmlSetStatement;
import org.jmlspecs.checker.JmlSourceField;
import org.multijava.mjc.CType;
import org.multijava.mjc.JAssignmentExpression;
import org.multijava.mjc.JBooleanLiteral;
import org.multijava.mjc.JClassFieldExpression;
import org.multijava.mjc.JConstructorBlock;
import org.multijava.mjc.JExpression;
import org.multijava.mjc.JExpressionStatement;
import org.multijava.mjc.JNullLiteral;
import org.multijava.mjc.JOrdinalLiteral;
import org.multijava.mjc.JStatement;
import org.multijava.mjc.JUnaryPromote;

import ar.edu.taco.utils.jml.JmlAstClonerStatementVisitor;

/**
 * The simplifier motivation is avoid implement AST Visitor for Method Scope and
 * is used , as precondition, in ConditionSimplifier, that all statement are
 * eveloped by a block
 * 
 * @author diegodob
 * 
 */
public class FieldInitializerSimplifier extends JmlAstClonerStatementVisitor {

	private List<JmlAssignmentStatement> initializationStatements;

	@Override
	public void visitJmlClassDeclaration(JmlClassDeclaration self) {
		initializationStatements = new ArrayList<JmlAssignmentStatement>();
		super.visitJmlClassDeclaration(self);
		JmlClassDeclaration newJmlClassDeclaration = (JmlClassDeclaration) this.getStack().pop();
		this.getStack().push(newJmlClassDeclaration);
	}

	
	
	
	@Override
	public void visitJmlFieldDeclaration(JmlFieldDeclaration self) {

		JmlFieldDeclaration jmlFieldDeclaration = self;
		this.getStack().push(jmlFieldDeclaration);
		
		if (!self.getField().isStatic()) {

			JClassFieldExpression initializationExpression = new JClassFieldExpression(self.getTokenReference(), jmlFieldDeclaration.ident());
			JmlSourceField jmlSourceField = new JmlSourceField(jmlFieldDeclaration.jmlAccess(), jmlFieldDeclaration.ident(), jmlFieldDeclaration.getType(), false);
			initializationExpression.setField(jmlSourceField);
			
			JExpression value = null;
			if (!self.isModel()) {
				if (self.variable().hasInitializer()) {
					value = self.variable().getValue();
				} else {
					CType type = self.variable().getType();
					switch (type.getTypeID()) {
					case CType.TID_BOOLEAN:
						value = new JBooleanLiteral(self.getTokenReference(), false);
						break;
					case CType.TID_INT:
					case CType.TID_DOUBLE:
					case CType.TID_FLOAT:
					case CType.TID_LONG:
					case CType.TID_SHORT:
					case CType.TID_BYTE:
						value = new JOrdinalLiteral(self.getTokenReference(), "0");
						break;
					case CType.TID_CLASS:
						if (!self.getField().isDeclaredNonNull()) {
							value = new JUnaryPromote(new JNullLiteral(self.getTokenReference()), self.getType());
						}
						break;
					default:
						break;
					}
				}
			}
			
			if (value != null) {
				JAssignmentExpression assignmentExpression = new JAssignmentExpression(self.getTokenReference(), initializationExpression, value);
				
				JExpressionStatement expressionStatement = new JExpressionStatement(self.getTokenReference(), assignmentExpression, null);
				JmlAssignmentStatement jmlAssignmentStatement = new JmlAssignmentStatement(expressionStatement);
				initializationStatements.add(jmlAssignmentStatement);
			}

			self.setInitializer(null);

		} 
		

//		self.setInitializer(null);
	}

	@Override
	public void visitJmlConstructorDeclaration(JmlConstructorDeclaration self) {
		super.visitJmlConstructorDeclaration(self);
		JmlConstructorDeclaration jmlConstructorDeclaration = (JmlConstructorDeclaration) this.getStack().pop();

		JConstructorBlock newBody;
		if (self.body() == null) {
			newBody = null;
		} else {

			JStatement[] bodyStatements = new JStatement[self.body().body().length + initializationStatements.size()];
			for (int i = 0; i < initializationStatements.size(); i++) {
				JStatement statement = initializationStatements.get(i);
				bodyStatements[i] = statement;
			}

			for (int i = 0; i < self.body().body().length; i++) {
				JStatement statement = self.body().body()[i];
				bodyStatements[i + initializationStatements.size()] = statement;
			}

			newBody = new JConstructorBlock(self.body().getTokenReference(), bodyStatements);

		}

		JmlConstructorDeclaration newJmlConstructorDeclaration = JmlConstructorDeclaration.makeInstance(jmlConstructorDeclaration.getTokenReference(),
				jmlConstructorDeclaration.modifiers(), jmlConstructorDeclaration.ident(), jmlConstructorDeclaration.parameters(), jmlConstructorDeclaration
						.getExceptions(), newBody, null, null /* comments */, jmlConstructorDeclaration.methodSpecification());

		this.getStack().push(newJmlConstructorDeclaration);

	}
	



	

}