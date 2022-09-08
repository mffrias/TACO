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

public class LSBlockVisitor extends JmlAstClonerStatementVisitor {

	//	private List<JStatement> declarationStatements;

	//	public List<JStatement> getDeclarationStatements() {
	//		return declarationStatements;
	//	}

	//	public void setDeclarationStatements(List<JStatement> declarationStatements) {
	//		this.declarationStatements = declarationStatements;
	//	}

	public List<JStatement> getNewStatements() {
		return newStatements;
	}

	public void setNewStatements(List<JStatement> newStatements) {
		this.newStatements = newStatements;
	}

	private List<JStatement> newStatements;

	public LSBlockVisitor() {
		//		declarationStatements = new ArrayList<JStatement>();
		newStatements = new ArrayList<JStatement>();
	}

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
