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

	/**
	 * Moves the initialization of the class's instance fields into its constructors: starts a
	 * new, empty list of initializations, then copies the class.
	 * {@link JmlAstClonerStatementVisitor} visits the fields ({@link #visitJmlFieldDeclaration},
	 * which fills the list) before the methods ({@link #visitJmlConstructorDeclaration}, which
	 * uses it), and the nested classes, which start their own list, last.
	 *
	 * <p>{@code ASTSimplifierManager} runs this pass right after
	 * {@code DefaultConstructorSimplifier}, so every class has a constructor to receive the
	 * initializations, during {@code JmlStage} (docs/ARCHITECTURE.md).
	 *
	 * @param self the class declaration
	 */
	@Override
	public void visitJmlClassDeclaration(JmlClassDeclaration self) {
		initializationStatements = new ArrayList<JmlAssignmentStatement>();
		super.visitJmlClassDeclaration(self);
		JmlClassDeclaration newJmlClassDeclaration = (JmlClassDeclaration) this.getStack().pop();
		this.getStack().push(newJmlClassDeclaration);
	}

	
	
	
	/**
	 * Keeps the field declaration (the same object, not a copy) and, for an instance field,
	 * records the assignment {@code f = <value>} for the constructors and removes the
	 * declaration's initializer. The value is the initializer, if there is one; otherwise
	 * {@code false} for {@code boolean}, {@code 0} for {@code int}, {@code long}, {@code short},
	 * {@code byte}, {@code float} and {@code double}, and {@code null} (promoted to the field's
	 * type) for a class type whose field is not declared non-null
	 * ({@code isDeclaredNonNull()}). Fields of other types ({@code char}, arrays), non-null
	 * class fields without an initializer, and model fields get no assignment. Static fields
	 * are left as they are.
	 *
	 * <p>Side effect: the initializer is removed from the declaration of the input AST, since
	 * the declaration is not copied.
	 *
	 * @param self the field declaration
	 */
	@Override
	public void visitJmlFieldDeclaration(JmlFieldDeclaration self) {

		JmlFieldDeclaration jmlFieldDeclaration = self;
		this.getStack().push(jmlFieldDeclaration);
		
		if (!self.getField().isStatic()) {

			JClassFieldExpression initializationExpression = new JClassFieldExpression(self.getTokenReference(), jmlFieldDeclaration.ident());
			initializationExpression.setType(jmlFieldDeclaration.getType());
			JmlSourceField jmlSourceField = new JmlSourceField(jmlFieldDeclaration.jmlAccess(), jmlFieldDeclaration.ident(), jmlFieldDeclaration.getType(), false);
			initializationExpression.setField(jmlSourceField);
			initializationExpression.setType(jmlSourceField.getType());
			
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

	/**
	 * Copies the constructor and puts the recorded field initializations (see
	 * {@link #visitJmlFieldDeclaration}) at the start of its body, in the order of the field
	 * declarations, before the original statements. Every constructor of the class gets the same
	 * statement objects. The copy has no Javadoc comment and no comments; its specification is
	 * that of the copy made by {@code super}.
	 *
	 * @param self the constructor declaration
	 */
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