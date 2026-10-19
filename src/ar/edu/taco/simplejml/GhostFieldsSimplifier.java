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
package ar.edu.taco.simplejml;

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

import ar.edu.taco.jml.utils.SpecSimplifierClassBaseVisitor;
import ar.edu.taco.utils.jml.JmlAstClonerStatementVisitor;

/**
 * The simplifier motivation is avoid implement AST Visitor for Method Scope and
 * is used , as precondition, in ConditionSimplifier, that all statement are
 * eveloped by a block
 * 
 * @author diegodob
 * 
 */
public class GhostFieldsSimplifier extends JmlAstClonerStatementVisitor {

	//Ghost modifier is 0x00200000
	private long stripGhostModifier(long modifs){
		return modifs & 0xFFDFFFFF;
	}



	/**
	 * Turns a ghost field into an ordinary field, so that it is translated like a Java field:
	 * pushes the declaration itself (not a copy) and clears its {@code ghost} modifier
	 * (0x00200000, JML's {@code ACC_GHOST}) in place, so the input AST changes too. Other field
	 * declarations are pushed unchanged. This pass runs during {@code engine.JmlStage}, after
	 * {@code ShortcutRemoverVisitor} and before {@code DefaultConstructorSimplifier}
	 * ({@code jml.ASTSimplifierManager}), although the class sits in this package.
	 *
	 * @param self the field declaration
	 */
	@Override
	public void visitJmlFieldDeclaration(JmlFieldDeclaration self) {

		JmlFieldDeclaration jmlFieldDeclaration = self;
		this.getStack().push(jmlFieldDeclaration);

		if (hasFlag(self.modifiers(), ACC_GHOST)){
			self.setModifiers(stripGhostModifier(self.modifiers()));
		}

	} 	
	
	
	/**
	 * Pushes the assignment itself, without copying or visiting its sides.
	 *
	 * @param self the assignment
	 */
	@Override
	public void visitAssignmentExpression(JAssignmentExpression self){
		getStack().push(self);
	}
	
	/**
	 * Turns {@code //@ set x = e;} into the Java assignment statement {@code x = e;} (a
	 * {@code JmlAssignmentStatement}, with no comments), which is then translated like any other
	 * assignment. The assignment expression is reused, not copied.
	 *
	 * @param self the {@code set} statement
	 */
	@Override
	public void visitJmlSetStatement(JmlSetStatement self){
		self.assignmentExpression().accept(this);
		JAssignmentExpression afterCall = (JAssignmentExpression)this.getStack().pop();
		JExpressionStatement theNewExpreSt = new JExpressionStatement(afterCall.getTokenReference(), afterCall, null);
		JmlAssignmentStatement theNewAssignment = new JmlAssignmentStatement(theNewExpreSt);
		getStack().push(theNewAssignment);
	}

}