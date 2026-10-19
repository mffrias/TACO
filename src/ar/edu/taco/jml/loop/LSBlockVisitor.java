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
package ar.edu.taco.jml.loop;

import java.util.ArrayList;
import java.util.List;

import org.jmlspecs.checker.JmlLoopStatement;
import org.multijava.mjc.JBlock;
import org.multijava.mjc.JForStatement;
import org.multijava.mjc.JStatement;
import org.multijava.mjc.JWhileStatement;

import ar.edu.taco.jml.utils.ASTUtils;
import ar.edu.taco.utils.jml.JmlAstClonerStatementVisitor;

/**
 * Turns {@code for} loops into {@code while} loops, as {@link ForRemoverVisitor} does:
 * {@code for (init; cond; incr) body} becomes {@code { init; while (cond) { body; incr } }}.
 * {@code ASTSimplifierManager} runs it after {@code FieldInitializerSimplifier}, when
 * {@link ForRemoverVisitor} and {@link WhileRemoverSimplifier} have already removed the
 * {@code for} loops it could reach, so in the current pipeline it only copies the AST. Unclear:
 * whether it is still needed in that order.
 *
 * <p>Each statement of a block is visited by a new instance; that instance's
 * {@link #getNewStatements()} are inserted before the statement, but nothing in this class adds
 * to them.
 */
public class LSBlockVisitor extends JmlAstClonerStatementVisitor {

	//	private List<JStatement> declarationStatements;

	//	public List<JStatement> getDeclarationStatements() {
	//		return declarationStatements;
	//	}

	//	public void setDeclarationStatements(List<JStatement> declarationStatements) {
	//		this.declarationStatements = declarationStatements;
	//	}

	/**
	 * Returns the statements to insert before the statement this instance visited; nothing in
	 * this class adds to them.
	 *
	 * @return the list itself, not a copy
	 */
	public List<JStatement> getNewStatements() {
		return newStatements;
	}

	/**
	 * Replaces the list of statements to insert.
	 *
	 * @param newStatements the new list
	 */
	public void setNewStatements(List<JStatement> newStatements) {
		this.newStatements = newStatements;
	}

	private List<JStatement> newStatements;

	/** Creates the pass with an empty list of statements to insert. */
	public LSBlockVisitor() {
		//		declarationStatements = new ArrayList<JStatement>();
		newStatements = new ArrayList<JStatement>();
	}

	/**
	 * Rewrites each statement of the block with a new instance and inserts, before it, the
	 * statements that instance collected.
	 *
	 * @param self the block
	 */
	@Override
	public void visitBlockStatement(JBlock self) {
		List<JStatement> declarationList = new ArrayList<JStatement>();
		List<JStatement> statementList = new ArrayList<JStatement>();

		for (int i = 0; i < self.body().length; i++) {
			JStatement statement = self.body()[i];

			LSBlockVisitor visitor = new LSBlockVisitor();
			statement.accept(visitor);

			//			declarationList.addAll(visitor.getDeclarationStatements());
			statementList.addAll(visitor.getNewStatements());
			statementList.add((JStatement) visitor.getStack().pop());
			// reset statements
			//			declarationStatements = new ArrayList<JStatement>();
			newStatements = new ArrayList<JStatement>();

		}

		JStatement[] statements = new JStatement[declarationList.size() + statementList.size()];
		int i = 0;
		for (JStatement statement : declarationList) {
			assert (statement != null);

			statements[i] = statement;
			i++;
		}

		for (JStatement statement : statementList) {
			assert (statement != null);
			statements[i] = statement;
			i++;
		}

		for (int j = 0; j < statements.length; j++) {
			JStatement statement = statements[j];
			assert (statement != null);
		}

		assert (statements != null);
		JBlock newSelf = new JBlock(self.getTokenReference(), statements, self.getComments());
		this.getStack().push(newSelf);

	}

	// BEGIN - LSStatementVisitor

	/**
	 * For an annotated {@code for} loop, rewrites it ({@link #visitForStatement}) and pushes the
	 * block {@code { init; <annotated while loop> }}, the {@code while} loop wrapped in a new
	 * {@code JmlLoopStatement} with the same annotations. Pushes any other annotated loop
	 * unchanged.
	 *
	 * @param self the annotated loop. A {@code for} loop must have an initialization: the
	 *     {@code while} loop is taken as the second statement of the block.
	 */
	@Override
	public void visitJmlLoopStatement(JmlLoopStatement self) {
		if (self.stmt() instanceof JForStatement) {
			self.stmt().accept(this);

			JBlock block = (JBlock) this.getStack().pop();

			JWhileStatement newWhileStatement = (JWhileStatement) block.body()[1];

			JmlLoopStatement newJmlLoopStatement = new JmlLoopStatement(self.getTokenReference(), self.loopInvariants(), self.variantFunctions(), newWhileStatement, self.getComments());

			JBlock generatedBlock = ASTUtils.createBlockStatement(block.body()[0], newJmlLoopStatement);

			this.getStack().push(generatedBlock);
		} else {
			this.getStack().push(self);			
		}


	}


	/**
	 * Replaces a {@code for} loop by {@code { init; while (cond) { body; incr } }}, with the
	 * initialization, the increment and the body rewritten first, and pushes the new block.
	 *
	 * @param self the loop
	 */
	@Override
	public void visitForStatement(JForStatement self) {
		JStatement newInit = null;
		if (self.init() != null){
			self.init().accept(this);
			newInit = (JStatement) this.getStack().pop();
		}
		JStatement newIncr = null;
		if (self.incr() != null) {
			self.incr().accept(this);
			newIncr = (JStatement) this.getStack().pop();
		}
		self.body().accept(this);
		JStatement newBody = (JStatement) this.getStack().pop();
		//declarationStatements.add(newInit);

		if (newIncr != null) {
			// Add the increment statement (if exists) at end of body
			JBlock newIncrAsBlock = ASTUtils.createBlockStatement(newIncr);
			newBody = (JBlock) ASTUtils.createBlockStatement(newBody, newIncrAsBlock);
		}

		JWhileStatement whileStatement = new JWhileStatement(self.getTokenReference(), self.cond(), newBody, self.getComments());

		JBlock generatedBlock = ASTUtils.createBlockStatement(whileStatement);
		if (newInit != null){
			generatedBlock = ASTUtils.createBlockStatement(newInit, whileStatement);
		}
		this.getStack().push(generatedBlock);
	}

	// END - LSStatementVisitor
}
