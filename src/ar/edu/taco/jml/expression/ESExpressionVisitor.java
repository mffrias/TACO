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
package ar.edu.taco.jml.expression;

import java.util.ArrayList;
import java.util.List;

import org.jmlspecs.checker.JmlAddExpression;
import org.jmlspecs.checker.JmlRelationalExpression;
import org.multijava.mjc.*;
import org.multijava.util.compiler.JavaStyleComment;
import org.multijava.util.compiler.TokenReference;

import ar.edu.jdynalloy.ast.JBlock;
import ar.edu.jdynalloy.ast.JSkip;
import ar.edu.taco.TacoNotImplementedYetException;
import ar.edu.taco.jml.utils.ASTUtils;
import ar.edu.taco.simplejml.methodinfo.MethodInformation;
import ar.edu.taco.simplejml.methodinfo.MethodInformationBuilder;
import ar.edu.taco.utils.jml.JmlAstClonerExpressionVisitor;

/**
 * Implementa la simplificacion de la regla 22
 * 
 * @author diegodob
 * 
 */
public class ESExpressionVisitor extends JmlAstClonerExpressionVisitor {

	private static int variableNameIndex = 0;

	private List<JStatement> declarationStatements;

	private List<JStatement> newStatements;
	
	
	/**
	 * postfixNewStatements collects statements that break the natural order of the 
	 * translation due to the presence of a postfix ++ or --. For instance, if we have
	 * if (a = b++) S;
	 * this must result in
	 * c = b; t = a == c; b = b + 1; if (t) S;
	 * rather than the previous translation
	 * c = b; b = b + 1; t = a == c; if (t) S;
	 * While they seem the same, the problem arises when, for instance, a is b.
	 */
	private List<JStatement> postfixNewStatements;

	private boolean inAssignament = false;
	private boolean expressionStatement = false;

	private int expressionDeep = 0;
	private int assignationRightnessCount = 0;

	//	private Map<String, JLocalVariable> variableNamesReplaces = new HashMap<String, JLocalVariable>();

	public ESExpressionVisitor() {
		declarationStatements = new ArrayList<JStatement>();
		newStatements = new ArrayList<JStatement>();
		postfixNewStatements = new ArrayList<JStatement>();
	}

	public List<JStatement> getNewStatements() {
		return newStatements;
	}

	public List<JStatement> getPostfixNewStatements() {
		return postfixNewStatements;
	}
	
	public void setNewPostfixNewStatements() {
		this.postfixNewStatements = new ArrayList<JStatement>();
	}
	
	
	public void setNewStatements(List<JStatement> newStatements) {
		this.newStatements = newStatements;
	}

	public List<JStatement> getDeclarationStatements() {
		return declarationStatements;
	}

	public void setDeclarationStatements(List<JStatement> declarationStatements) {
		this.declarationStatements = declarationStatements;
	}

	public static String createNewVariableName() {
		ESExpressionVisitor.variableNameIndex++;
		String s = "t_" + variableNameIndex;
		return s;
	}

	// Estos son los metodos interesantes

	// aqui tengo que construir la siguiente estructura
	// outer IF
	// if (cond1) {
	// b = true
	// } else {
	// //Inner IF
	// if (cond2) {
	// b = true
	// } else {
	// b = false
	public boolean isExpressionStatement() {
		return expressionStatement;
	}

	public void setExpressionStatement(boolean expressionStatement) {
		this.expressionStatement = expressionStatement;
	}	// }
	// }

	/** Visits the given boolean OR expression. */
	public void visitConditionalOrExpression(JConditionalOrExpression self) {
		expressionDeep++;

		int newStatementsOriginalSize = this.getNewStatements().size();
		
		String cond = createNewVariableName();
		JVariableDefinition variableDefinition = new JVariableDefinition(self.getTokenReference(), 0, self.getType(), cond, null);
		JVariableDeclarationStatement variableDeclarationStatement = new JVariableDeclarationStatement(self.getTokenReference(), variableDefinition,
				new JavaStyleComment[0]);
		getDeclarationStatements().add(variableDeclarationStatement);

		JLocalVariableExpression condReference = new JLocalVariableExpression(self.getTokenReference(), variableDefinition);

		Class<?> cleft = self.left().getClass();
		String typeNameLeftSide = cleft.getSimpleName();

		if (!typeNameLeftSide.equals("JLocalVariableExpression") && !typeNameLeftSide.equals("JBooleanLiteral")){
			self.left().accept(this);
			
			int newStatementsAddedByLeftAcceptSize = this.getNewStatements().size() - newStatementsOriginalSize;
			
			JExpression newLeftExpression = this.getArrayStack().pop();

			Class<?> cright = self.right().getClass();
			String typeNameRightSide = cright.getSimpleName();

			if (!typeNameRightSide.equals("JLocalVariableExpression") && !typeNameRightSide.equals("JBooleanLiteral")){

				self.right().accept(this);
				
				int newStatementsAddedByRightAcceptSize = this.getNewStatements().size() - (newStatementsOriginalSize + newStatementsAddedByLeftAcceptSize);
				
				JExpression newRightExpression = this.getArrayStack().pop();

				JStatement innerIfThenStatement = ASTUtils.createAssignamentStatement(condReference, new JBooleanLiteral(self.getTokenReference(), true));
				JStatement innerIfElseStatement = ASTUtils.createAssignamentStatement(condReference, new JBooleanLiteral(self.getTokenReference(), false));
				JIfStatement innerIf = ASTUtils.createIfStatement(newRightExpression, innerIfThenStatement, innerIfElseStatement);

				org.multijava.mjc.JBlock innerBlock = ASTUtils.createBlockStatement(innerIf);					
				for (int i = 0; i < newStatementsAddedByRightAcceptSize; i++){
					innerBlock = ASTUtils.createBlockStatement(this.getNewStatements().get(this.getNewStatements().size()-1), innerBlock);
					this.getNewStatements().remove(this.getNewStatements().size()-1);
				}
	
				JStatement outerIfThenStatement = ASTUtils.createAssignamentStatement(condReference, new JBooleanLiteral(self.getTokenReference(), true));

				JStatement outerIf = ASTUtils.createIfStatement(newLeftExpression, outerIfThenStatement, innerBlock);
				
				org.multijava.mjc.JBlock outerBlock = ASTUtils.createBlockStatement(outerIf);
				for (int i = 0; i < newStatementsAddedByLeftAcceptSize; i++){
					outerBlock = ASTUtils.createBlockStatement(this.getNewStatements().get(this.getNewStatements().size()-1), outerBlock);
					this.getNewStatements().remove(this.getNewStatements().size()-1);
				}

				this.getNewStatements().add(outerBlock);
			} else {
				if (typeNameRightSide.equals("JLocalVariableExpression")) {
					self.right().accept(this);
					
					int newStatementsAddedByRightAcceptSize = this.getNewStatements().size() - (newStatementsOriginalSize + newStatementsAddedByLeftAcceptSize);
					
					JExpression newRightExpression = this.getArrayStack().pop();

					JStatement innerIfThenStatement = ASTUtils.createAssignamentStatement(condReference, new JBooleanLiteral(self.getTokenReference(), true));
					JStatement innerIfElseStatement = ASTUtils.createAssignamentStatement(condReference, new JBooleanLiteral(self.getTokenReference(), false));
					JIfStatement innerIf = ASTUtils.createIfStatement(newRightExpression, innerIfThenStatement, innerIfElseStatement);
					
					org.multijava.mjc.JBlock innerBlock = ASTUtils.createBlockStatement(innerIf);
					for (int i = 0; i < newStatementsAddedByRightAcceptSize; i++){
						innerBlock = ASTUtils.createBlockStatement(this.getNewStatements().get(this.getNewStatements().size()-1), innerBlock);
						this.getNewStatements().remove(this.getNewStatements().size()-1);
					}
				
					JStatement outerIfThenStatement = ASTUtils.createAssignamentStatement(condReference, new JBooleanLiteral(self.getTokenReference(), true));

					JStatement outerIf = ASTUtils.createIfStatement(newLeftExpression, outerIfThenStatement, innerBlock);

					org.multijava.mjc.JBlock outerBlock = ASTUtils.createBlockStatement(outerIf);
					for (int i = 0; i < newStatementsAddedByLeftAcceptSize; i++){
						outerBlock = ASTUtils.createBlockStatement(this.getNewStatements().get(this.getNewStatements().size()-1), outerBlock);
						this.getNewStatements().remove(this.getNewStatements().size()-1);
					}

					this.getNewStatements().add(outerBlock);
					
				} else {
					self.right().accept(this);
					
					int newStatementsAddedByRightAcceptSize = this.getNewStatements().size() - (newStatementsOriginalSize + newStatementsAddedByLeftAcceptSize);
					
					JExpression newRightExpression = this.getArrayStack().pop();

					JStatement innerIfThenStatement = ASTUtils.createAssignamentStatement(condReference, new JBooleanLiteral(self.getTokenReference(), true));
					JStatement innerIfElseStatement = ASTUtils.createAssignamentStatement(condReference, new JBooleanLiteral(self.getTokenReference(), false));
					JIfStatement innerIf = ASTUtils.createIfStatement(newRightExpression, innerIfThenStatement, innerIfElseStatement);

					org.multijava.mjc.JBlock innerBlock = ASTUtils.createBlockStatement(innerIf);
					for (int i = 0; i < newStatementsAddedByRightAcceptSize; i++){
						innerBlock = ASTUtils.createBlockStatement(this.getNewStatements().get(this.getNewStatements().size()-1), innerBlock);
						this.getNewStatements().remove(this.getNewStatements().size()-1);
					}

					JStatement outerIfThenStatement = ASTUtils.createAssignamentStatement(condReference, new JBooleanLiteral(self.getTokenReference(), true));

					JStatement outerIf = ASTUtils.createIfStatement(newLeftExpression, outerIfThenStatement, innerIf);
					
					org.multijava.mjc.JBlock outerBlock = ASTUtils.createBlockStatement(outerIf);
					for (int i = 0; i < newStatementsAddedByLeftAcceptSize; i++){
						outerBlock = ASTUtils.createBlockStatement(this.getNewStatements().get(this.getNewStatements().size()-1), outerBlock);
						this.getNewStatements().remove(this.getNewStatements().size()-1);
					}

					this.getNewStatements().add(outerBlock);
				}
			}

		} else {
			if (typeNameLeftSide.equals("JLocalVariableExpression")){
			
				self.left().accept(this);
				
				int newStatementsAddedByLeftAcceptSize = this.getNewStatements().size() - newStatementsOriginalSize;
				
				JExpression newLeftExpression = this.getArrayStack().pop();

				Class<?> cright = self.right().getClass();
				String typeNameRightSide = cright.getSimpleName();

				if (!typeNameRightSide.equals("JLocalVariableExpression") && !typeNameRightSide.equals("JBooleanLiteral")){

					self.right().accept(this);
					
					int newStatementsAddedByRightAcceptSize = this.getNewStatements().size() - (newStatementsOriginalSize + newStatementsAddedByLeftAcceptSize);
					
					JExpression newRightExpression = this.getArrayStack().pop();

					JStatement innerIfThenStatement = ASTUtils.createAssignamentStatement(condReference, new JBooleanLiteral(self.getTokenReference(), true));
					JStatement innerIfElseStatement = ASTUtils.createAssignamentStatement(condReference, new JBooleanLiteral(self.getTokenReference(), false));
					JIfStatement innerIf = ASTUtils.createIfStatement(newRightExpression, innerIfThenStatement, innerIfElseStatement);

					org.multijava.mjc.JBlock innerBlock = ASTUtils.createBlockStatement(innerIf);
					for (int i = 0; i < newStatementsAddedByRightAcceptSize; i++){
						innerBlock = ASTUtils.createBlockStatement(this.getNewStatements().get(this.getNewStatements().size()-1), innerBlock);
						this.getNewStatements().remove(this.getNewStatements().size()-1);
					}

					JStatement outerIfThenStatement = ASTUtils.createAssignamentStatement(condReference, new JBooleanLiteral(self.getTokenReference(), true));

					JStatement outerIf = ASTUtils.createIfStatement(newLeftExpression, outerIfThenStatement, innerBlock);
					
					org.multijava.mjc.JBlock outerBlock = ASTUtils.createBlockStatement(outerIf);
					for (int i = 0; i < newStatementsAddedByLeftAcceptSize; i++){
						outerBlock = ASTUtils.createBlockStatement(this.getNewStatements().get(this.getNewStatements().size()-1), outerBlock);
						this.getNewStatements().remove(this.getNewStatements().size()-1);
					}

					this.getNewStatements().add(outerBlock);
				} else {
					if (typeNameRightSide.equals("JLocalVariableExpression")) {
						self.right().accept(this);
						
						int newStatementsAddedByRightAcceptSize = this.getNewStatements().size() - (newStatementsOriginalSize + newStatementsAddedByLeftAcceptSize);
						
						JExpression newRightExpression = this.getArrayStack().pop();

						JStatement innerIfThenStatement = ASTUtils.createAssignamentStatement(condReference, new JBooleanLiteral(self.getTokenReference(), true));
						JStatement innerIfElseStatement = ASTUtils.createAssignamentStatement(condReference, new JBooleanLiteral(self.getTokenReference(), false));
						JIfStatement innerIf = ASTUtils.createIfStatement(newRightExpression, innerIfThenStatement, innerIfElseStatement);
		
						org.multijava.mjc.JBlock innerBlock = ASTUtils.createBlockStatement(innerIf);
						for (int i = 0; i < newStatementsAddedByRightAcceptSize; i++){
							innerBlock = ASTUtils.createBlockStatement(this.getNewStatements().get(this.getNewStatements().size()-1), innerBlock);
							this.getNewStatements().remove(this.getNewStatements().size()-1);
						}			
						
						JStatement outerIfThenStatement = ASTUtils.createAssignamentStatement(condReference, new JBooleanLiteral(self.getTokenReference(), true));

						JStatement outerIf = ASTUtils.createIfStatement(newLeftExpression, outerIfThenStatement, innerBlock);
						
						org.multijava.mjc.JBlock outerBlock = ASTUtils.createBlockStatement(outerIf);
						for (int i = 0; i < newStatementsAddedByLeftAcceptSize; i++){
							outerBlock = ASTUtils.createBlockStatement(this.getNewStatements().get(this.getNewStatements().size()-1), outerBlock);
							this.getNewStatements().remove(this.getNewStatements().size()-1);
						}


						this.getNewStatements().add(outerBlock);
					} else {
						self.right().accept(this);
						
						int newStatementsAddedByRightAcceptSize = this.getNewStatements().size() - (newStatementsOriginalSize + newStatementsAddedByLeftAcceptSize);

						JExpression newRightExpression = this.getArrayStack().pop();

						JStatement innerIfThenStatement = ASTUtils.createAssignamentStatement(condReference, new JBooleanLiteral(self.getTokenReference(), true));
						JStatement innerIfElseStatement = ASTUtils.createAssignamentStatement(condReference, new JBooleanLiteral(self.getTokenReference(), false));
						JIfStatement innerIf = ASTUtils.createIfStatement(newRightExpression, innerIfThenStatement, innerIfElseStatement);
					
						org.multijava.mjc.JBlock innerBlock = ASTUtils.createBlockStatement(innerIf);
						for (int i = 0; i < newStatementsAddedByRightAcceptSize; i++){
							innerBlock = ASTUtils.createBlockStatement(this.getNewStatements().get(this.getNewStatements().size()-1), innerBlock);
							this.getNewStatements().remove(this.getNewStatements().size()-1);
						}			
					
						JStatement outerIfThenStatement = ASTUtils.createAssignamentStatement(condReference, new JBooleanLiteral(self.getTokenReference(), true));

						JStatement outerIf = ASTUtils.createIfStatement(newLeftExpression, outerIfThenStatement, innerBlock);
						
						org.multijava.mjc.JBlock outerBlock = ASTUtils.createBlockStatement(outerIf);
						for (int i = 0; i < newStatementsAddedByLeftAcceptSize; i++){
							outerBlock = ASTUtils.createBlockStatement(this.getNewStatements().get(this.getNewStatements().size()-1), outerBlock);
							this.getNewStatements().remove(this.getNewStatements().size()-1);
						}

						this.getNewStatements().add(outerBlock);
					}
				}

			} else {				
				
				self.left().accept(this);
				
				int newStatementsAddedByLeftAcceptSize = this.getNewStatements().size() - newStatementsOriginalSize;
				
				JExpression newLeftExpression = this.getArrayStack().pop();

				Class<?> cright = self.right().getClass();
				String typeNameRightSide = cright.getSimpleName();

				if (!typeNameRightSide.equals("JLocalVariableExpression") && !typeNameRightSide.equals("JBooleanLiteral")){

					self.right().accept(this);
					
					int newStatementsAddedByRightAcceptSize = this.getNewStatements().size() - (newStatementsOriginalSize + newStatementsAddedByLeftAcceptSize);

					JExpression newRightExpression = this.getArrayStack().pop();

					JStatement innerIfThenStatement = ASTUtils.createAssignamentStatement(condReference, new JBooleanLiteral(self.getTokenReference(), true));
					JStatement innerIfElseStatement = ASTUtils.createAssignamentStatement(condReference, new JBooleanLiteral(self.getTokenReference(), false));
					JIfStatement innerIf = ASTUtils.createIfStatement(newRightExpression, innerIfThenStatement, innerIfElseStatement);

					org.multijava.mjc.JBlock innerBlock = ASTUtils.createBlockStatement(innerIf);
					for (int i = 0; i < newStatementsAddedByRightAcceptSize; i++){
						innerBlock = ASTUtils.createBlockStatement(this.getNewStatements().get(this.getNewStatements().size()-1), innerBlock);
						this.getNewStatements().remove(this.getNewStatements().size()-1);
					}			

					JStatement outerIfThenStatement = ASTUtils.createAssignamentStatement(condReference, new JBooleanLiteral(self.getTokenReference(), true));

					JStatement outerIf = ASTUtils.createIfStatement(newLeftExpression, outerIfThenStatement, innerBlock);
					
					org.multijava.mjc.JBlock outerBlock = ASTUtils.createBlockStatement(outerIf);
					for (int i = 0; i < newStatementsAddedByLeftAcceptSize; i++){
						outerBlock = ASTUtils.createBlockStatement(this.getNewStatements().get(this.getNewStatements().size()-1), outerBlock);
						this.getNewStatements().remove(this.getNewStatements().size()-1);
					}

					this.getNewStatements().add(outerBlock);
					
				} else {
					if (typeNameRightSide.equals("JLocalVariableExpression")) {
						
						self.right().accept(this);
						
						int newStatementsAddedByRightAcceptSize = this.getNewStatements().size() - (newStatementsOriginalSize + newStatementsAddedByLeftAcceptSize);

						JExpression newRightExpression = this.getArrayStack().pop();

						JStatement innerIfThenStatement = ASTUtils.createAssignamentStatement(condReference, new JBooleanLiteral(self.getTokenReference(), true));
						JStatement innerIfElseStatement = ASTUtils.createAssignamentStatement(condReference, new JBooleanLiteral(self.getTokenReference(), false));
						JIfStatement innerIf = ASTUtils.createIfStatement(newRightExpression, innerIfThenStatement, innerIfElseStatement);

						org.multijava.mjc.JBlock innerBlock = ASTUtils.createBlockStatement(innerIf);
						for (int i = 0; i < newStatementsAddedByRightAcceptSize; i++){
							innerBlock = ASTUtils.createBlockStatement(this.getNewStatements().get(this.getNewStatements().size()-1), innerBlock);
							this.getNewStatements().remove(this.getNewStatements().size()-1);
						}			

						JStatement outerIfThenStatement = ASTUtils.createAssignamentStatement(condReference, new JBooleanLiteral(self.getTokenReference(), true));
						JStatement outerIf = ASTUtils.createIfStatement(newLeftExpression, outerIfThenStatement, innerBlock);
						
						org.multijava.mjc.JBlock outerBlock = ASTUtils.createBlockStatement(outerIf);
						for (int i = 0; i < newStatementsAddedByLeftAcceptSize; i++){
							outerBlock = ASTUtils.createBlockStatement(this.getNewStatements().get(this.getNewStatements().size()-1), outerBlock);
							this.getNewStatements().remove(this.getNewStatements().size()-1);
						}

						this.getNewStatements().add(outerBlock);
					} else {
						self.right().accept(this);
						
						int newStatementsAddedByRightAcceptSize = this.getNewStatements().size() - (newStatementsOriginalSize + newStatementsAddedByLeftAcceptSize);
						
						JExpression newRightExpression = this.getArrayStack().pop();

						JStatement innerIfThenStatement = ASTUtils.createAssignamentStatement(condReference, new JBooleanLiteral(self.getTokenReference(), true));
						JStatement innerIfElseStatement = ASTUtils.createAssignamentStatement(condReference, new JBooleanLiteral(self.getTokenReference(), false));
						JIfStatement innerIf = ASTUtils.createIfStatement(newRightExpression, innerIfThenStatement, innerIfElseStatement);
						org.multijava.mjc.JBlock innerBlock = ASTUtils.createBlockStatement(innerIf);
						for (int i = 0; i < newStatementsAddedByRightAcceptSize; i++){
							innerBlock = ASTUtils.createBlockStatement(this.getNewStatements().get(this.getNewStatements().size()-1), innerBlock);
							this.getNewStatements().remove(this.getNewStatements().size()-1);
						}			

						JStatement outerIfThenStatement = ASTUtils.createAssignamentStatement(condReference, new JBooleanLiteral(self.getTokenReference(), true));
						JStatement outerIf = ASTUtils.createIfStatement(newLeftExpression, outerIfThenStatement, innerBlock);

						org.multijava.mjc.JBlock outerBlock = ASTUtils.createBlockStatement(outerIf);
						for (int i = 0; i < newStatementsAddedByLeftAcceptSize; i++){
							outerBlock = ASTUtils.createBlockStatement(this.getNewStatements().get(this.getNewStatements().size()-1), outerBlock);
							this.getNewStatements().remove(this.getNewStatements().size()-1);
						}

						this.getNewStatements().add(outerBlock);
					}
				}

			}
		}



		getArrayStack().add(condReference);

		expressionDeep--;
	}

	// boolean b
	// if (cond1) {
	// if (cond2) { b=true } else { b=false }
	// } else {
	// b = false
	// }
	// if (b==true) { P } else { Q }
	/** Visits the given boolean AND expression. */
	@Override
	public void visitConditionalAndExpression(/* @non_null */JConditionalAndExpression self) {
		expressionDeep++;

		int newStatementsOriginalSize = this.getNewStatements().size();

		String cond = createNewVariableName();
		JVariableDefinition variableDefinition = new JVariableDefinition(self.getTokenReference(), 0, self.getType(), cond, null);
		JVariableDeclarationStatement variableDeclarationStatement = new JVariableDeclarationStatement(self.getTokenReference(), variableDefinition,
				new JavaStyleComment[0]);
		getDeclarationStatements().add(variableDeclarationStatement);

		JLocalVariableExpression condReference = new JLocalVariableExpression(self.getTokenReference(), variableDefinition);

		Class<?> cleft = self.left().getClass();
		String typeNameLeftSide = cleft.getSimpleName();

		if (!typeNameLeftSide.equals("JLocalVariableExpression") && !typeNameLeftSide.equals("JBooleanLiteral")){
			self.left().accept(this);
			
			int newStatementsAddedByLeftAcceptSize = this.getNewStatements().size() - newStatementsOriginalSize;

			JExpression newLeftExpression = this.getArrayStack().pop();

			Class<?> cright = self.right().getClass();
			String typeNameRightSide = cright.getSimpleName();

			if (!typeNameRightSide.equals("JLocalVariableExpression") && !typeNameRightSide.equals("JBooleanLiteral")){

				self.right().accept(this);
				
				int newStatementsAddedByRightAcceptSize = this.getNewStatements().size() - (newStatementsOriginalSize + newStatementsAddedByLeftAcceptSize);
				
				JExpression newRightExpression = this.getArrayStack().pop();

				JStatement innerIfThenStatement = ASTUtils.createAssignamentStatement(condReference, new JBooleanLiteral(self.getTokenReference(), true));
				JStatement innerIfElseStatement = ASTUtils.createAssignamentStatement(condReference, new JBooleanLiteral(self.getTokenReference(), false));
				JIfStatement innerIf = ASTUtils.createIfStatement(newRightExpression, innerIfThenStatement, innerIfElseStatement);

				org.multijava.mjc.JBlock innerBlock = ASTUtils.createBlockStatement(innerIf);					
				for (int i = 0; i < newStatementsAddedByRightAcceptSize; i++){
					innerBlock = ASTUtils.createBlockStatement(this.getNewStatements().get(this.getNewStatements().size()-1), innerBlock);
					this.getNewStatements().remove(this.getNewStatements().size()-1);
				}

				JStatement outerIfElseStatement = ASTUtils.createAssignamentStatement(condReference, new JBooleanLiteral(self.getTokenReference(), false));

				JStatement outerIf = ASTUtils.createIfStatement(newLeftExpression, innerBlock, outerIfElseStatement);
				
				org.multijava.mjc.JBlock outerBlock = ASTUtils.createBlockStatement(outerIf);
				for (int i = 0; i < newStatementsAddedByLeftAcceptSize; i++){
					outerBlock = ASTUtils.createBlockStatement(this.getNewStatements().get(this.getNewStatements().size()-1), outerBlock);
					this.getNewStatements().remove(this.getNewStatements().size()-1);
				}

				this.getNewStatements().add(outerBlock);
			} else {
				if (typeNameRightSide.equals("JLocalVariableExpression")){
					self.right().accept(this);
					
					int newStatementsAddedByRightAcceptSize = this.getNewStatements().size() - (newStatementsOriginalSize + newStatementsAddedByLeftAcceptSize);

					JExpression newRightExpression = this.getArrayStack().pop();

					JStatement innerIfThenStatement = ASTUtils.createAssignamentStatement(condReference, new JBooleanLiteral(self.getTokenReference(), true));
					JStatement innerIfElseStatement = ASTUtils.createAssignamentStatement(condReference, new JBooleanLiteral(self.getTokenReference(), false));
					JIfStatement innerIf = ASTUtils.createIfStatement(newRightExpression, innerIfThenStatement, innerIfElseStatement);

					org.multijava.mjc.JBlock innerBlock = ASTUtils.createBlockStatement(innerIf);					
					for (int i = 0; i < newStatementsAddedByRightAcceptSize; i++){
						innerBlock = ASTUtils.createBlockStatement(this.getNewStatements().get(this.getNewStatements().size()-1), innerBlock);
						this.getNewStatements().remove(this.getNewStatements().size()-1);
					}

					JStatement outerIfElseStatement = ASTUtils.createAssignamentStatement(condReference, new JBooleanLiteral(self.getTokenReference(), false));

					JStatement outerIf = ASTUtils.createIfStatement(newLeftExpression, innerBlock, outerIfElseStatement);
					
					org.multijava.mjc.JBlock outerBlock = ASTUtils.createBlockStatement(outerIf);
					for (int i = 0; i < newStatementsAddedByLeftAcceptSize; i++){
						outerBlock = ASTUtils.createBlockStatement(this.getNewStatements().get(this.getNewStatements().size()-1), outerBlock);
						this.getNewStatements().remove(this.getNewStatements().size()-1);
					}

					this.getNewStatements().add(outerBlock);
					
				} else {
					self.right().accept(this);

					int newStatementsAddedByRightAcceptSize = this.getNewStatements().size() - (newStatementsOriginalSize + newStatementsAddedByLeftAcceptSize);

					JExpression newRightExpression = this.getArrayStack().pop();

					JStatement innerIfThenStatement = ASTUtils.createAssignamentStatement(condReference, new JBooleanLiteral(self.getTokenReference(), true));
					JStatement innerIfElseStatement = ASTUtils.createAssignamentStatement(condReference, new JBooleanLiteral(self.getTokenReference(), false));
					JIfStatement innerIf = ASTUtils.createIfStatement(newRightExpression, innerIfThenStatement, innerIfElseStatement);

					org.multijava.mjc.JBlock innerBlock = ASTUtils.createBlockStatement(innerIf);					
					for (int i = 0; i < newStatementsAddedByRightAcceptSize; i++){
						innerBlock = ASTUtils.createBlockStatement(this.getNewStatements().get(this.getNewStatements().size()-1), innerBlock);
						this.getNewStatements().remove(this.getNewStatements().size()-1);
					}

					JStatement outerIfElseStatement = ASTUtils.createAssignamentStatement(condReference, new JBooleanLiteral(self.getTokenReference(), false));
					JStatement outerIf = ASTUtils.createIfStatement(newLeftExpression, innerBlock, outerIfElseStatement);
					
					org.multijava.mjc.JBlock outerBlock = ASTUtils.createBlockStatement(outerIf);
					for (int i = 0; i < newStatementsAddedByLeftAcceptSize; i++){
						outerBlock = ASTUtils.createBlockStatement(this.getNewStatements().get(this.getNewStatements().size()-1), outerBlock);
						this.getNewStatements().remove(this.getNewStatements().size()-1);
					}
			

					this.getNewStatements().add(outerBlock);
				}
			}
		} else {
			if (typeNameLeftSide.equals("JLocalVariableExpression")){
				self.left().accept(this);
				
				int newStatementsAddedByLeftAcceptSize = this.getNewStatements().size() - newStatementsOriginalSize;

				JExpression newLeftExpression = this.getArrayStack().pop();

				self.right().accept(this);
				
				int newStatementsAddedByRightAcceptSize = this.getNewStatements().size() - (newStatementsOriginalSize + newStatementsAddedByLeftAcceptSize);

				JExpression newRightExpression = this.getArrayStack().pop();

				JStatement innerIfThenStatement = ASTUtils.createAssignamentStatement(condReference, new JBooleanLiteral(self.getTokenReference(), true));
				JStatement innerIfElseStatement = ASTUtils.createAssignamentStatement(condReference, new JBooleanLiteral(self.getTokenReference(), false));
				JIfStatement innerIf = ASTUtils.createIfStatement(newRightExpression, innerIfThenStatement, innerIfElseStatement);

				org.multijava.mjc.JBlock innerBlock = ASTUtils.createBlockStatement(innerIf);					
				for (int i = 0; i < newStatementsAddedByRightAcceptSize; i++){
					innerBlock = ASTUtils.createBlockStatement(this.getNewStatements().get(this.getNewStatements().size()-1), innerBlock);
					this.getNewStatements().remove(this.getNewStatements().size()-1);
				}

				JStatement outerIfElseStatement = ASTUtils.createAssignamentStatement(condReference, new JBooleanLiteral(self.getTokenReference(), false));

				JStatement outerIf = ASTUtils.createIfStatement(newLeftExpression, innerBlock, outerIfElseStatement);

				org.multijava.mjc.JBlock outerBlock = ASTUtils.createBlockStatement(outerIf);
				for (int i = 0; i < newStatementsAddedByLeftAcceptSize; i++){
					outerBlock = ASTUtils.createBlockStatement(this.getNewStatements().get(this.getNewStatements().size()-1), outerBlock);
					this.getNewStatements().remove(this.getNewStatements().size()-1);
				}
		
				this.getNewStatements().add(outerBlock);
				
			} else {

				if (((JBooleanLiteral)self.left()).booleanValue() == true){
					self.left().accept(this);

					int newStatementsAddedByLeftAcceptSize = this.getNewStatements().size() - newStatementsOriginalSize;

					JExpression newLeftExpression = this.getArrayStack().pop();

					self.right().accept(this);
					
					int newStatementsAddedByRightAcceptSize = this.getNewStatements().size() - (newStatementsOriginalSize + newStatementsAddedByLeftAcceptSize);

					JExpression newRightExpression = this.getArrayStack().pop();

					JStatement innerIfThenStatement = ASTUtils.createAssignamentStatement(condReference, new JBooleanLiteral(self.getTokenReference(), true));
					JStatement innerIfElseStatement = ASTUtils.createAssignamentStatement(condReference, new JBooleanLiteral(self.getTokenReference(), false));
					JIfStatement innerIf = ASTUtils.createIfStatement(newRightExpression, innerIfThenStatement, innerIfElseStatement);

					org.multijava.mjc.JBlock innerBlock = ASTUtils.createBlockStatement(innerIf);					
					for (int i = 0; i < newStatementsAddedByRightAcceptSize; i++){
						innerBlock = ASTUtils.createBlockStatement(this.getNewStatements().get(this.getNewStatements().size()-1), innerBlock);
						this.getNewStatements().remove(this.getNewStatements().size()-1);
					}

					JStatement outerIfElseStatement = ASTUtils.createAssignamentStatement(condReference, new JBooleanLiteral(self.getTokenReference(), false));

					JStatement outerIf = ASTUtils.createIfStatement(newLeftExpression, innerBlock, outerIfElseStatement);

					org.multijava.mjc.JBlock outerBlock = ASTUtils.createBlockStatement(outerIf);
					for (int i = 0; i < newStatementsAddedByLeftAcceptSize; i++){
						outerBlock = ASTUtils.createBlockStatement(this.getNewStatements().get(this.getNewStatements().size()-1), outerBlock);
						this.getNewStatements().remove(this.getNewStatements().size()-1);
					}
								
					this.getNewStatements().add(outerBlock);
					
				} else {

					self.left().accept(this);
					int newStatementsAddedByLeftAcceptSize = this.getNewStatements().size() - newStatementsOriginalSize;
					JExpression newLeftExpression = this.getArrayStack().pop();

					self.right().accept(this);
					int newStatementsAddedByRightAcceptSize = this.getNewStatements().size() - (newStatementsOriginalSize + newStatementsAddedByLeftAcceptSize);
					JExpression newRightExpression = this.getArrayStack().pop();

					JStatement innerIfThenStatement = ASTUtils.createAssignamentStatement(condReference, new JBooleanLiteral(self.getTokenReference(), true));
					JStatement innerIfElseStatement = ASTUtils.createAssignamentStatement(condReference, new JBooleanLiteral(self.getTokenReference(), false));
					JIfStatement innerIf = ASTUtils.createIfStatement(newRightExpression, innerIfThenStatement, innerIfElseStatement);
					org.multijava.mjc.JBlock innerBlock = ASTUtils.createBlockStatement(innerIf);					
					for (int i = 0; i < newStatementsAddedByRightAcceptSize; i++){
						innerBlock = ASTUtils.createBlockStatement(this.getNewStatements().get(this.getNewStatements().size()-1), innerBlock);
						this.getNewStatements().remove(this.getNewStatements().size()-1);
					}

					JStatement outerIfElseStatement = ASTUtils.createAssignamentStatement(condReference, new JBooleanLiteral(self.getTokenReference(), false));

					JStatement outerIf = ASTUtils.createIfStatement(newLeftExpression, innerBlock, outerIfElseStatement);

					org.multijava.mjc.JBlock outerBlock = ASTUtils.createBlockStatement(outerIf);
					for (int i = 0; i < newStatementsAddedByLeftAcceptSize; i++){
						outerBlock = ASTUtils.createBlockStatement(this.getNewStatements().get(this.getNewStatements().size()-1), outerBlock);
						this.getNewStatements().remove(this.getNewStatements().size()-1);
					}

					this.getNewStatements().add(outerBlock);

				}
			}
		}


		getArrayStack().add(condReference);

		expressionDeep--;
	}

	@Override
	public void visitConditionalExpression(JConditionalExpression self) {
		expressionDeep++;

		String cond = createNewVariableName();
		//		System.out.println(self.getType());
		//		System.out.println(self.left().getType());
		//		System.out.println(self.right().getType());

		CType type;
		if (self.getType().equals(CStdType.Null)) {			
			if (self.left().getType().equals(CStdType.Null)) {
				type = self.right().getType();
			} else {
				type = self.left().getType();
			}
		} else {
			type = self.getType();
		}

		JVariableDefinition variableDefinition = new JVariableDefinition(self.getTokenReference(), 0, type, cond, null);
		JVariableDeclarationStatement variableDeclarationStatement = new JVariableDeclarationStatement(self.getTokenReference(), variableDefinition,
				new JavaStyleComment[0]);
		getDeclarationStatements().add(variableDeclarationStatement);

		JLocalVariableExpression condReference = new JLocalVariableExpression(self.getTokenReference(), variableDefinition);

		self.cond().accept(this);
		JExpression condition = this.getArrayStack().pop();

		self.left().accept(this);
		JExpression thenExpression = this.getArrayStack().pop();

		self.right().accept(this);
		JExpression elseExpression = this.getArrayStack().pop();

		JStatement thenStatement = ASTUtils.createAssignamentStatement(condReference, thenExpression);
		JStatement elseStatement = ASTUtils.createAssignamentStatement(condReference, elseExpression);

		JStatement ifStatement = ASTUtils.createIfStatement(condition, thenStatement, elseStatement);

		this.getNewStatements().add(ifStatement);

		getArrayStack().add(condReference);

		expressionDeep--;
	}

	@Override
	public void visitNewObjectExpression(JNewObjectExpression self) {
		expressionDeep++;

		String createdVar = createNewVariableName();
		JVariableDefinition variableDefinition = new JVariableDefinition(self.getTokenReference(), 0, self.getType(), createdVar, null);
		JVariableDeclarationStatement variableDeclarationStatement = new JVariableDeclarationStatement(self.getTokenReference(), variableDefinition,
				new JavaStyleComment[0]);
		getDeclarationStatements().add(variableDeclarationStatement);

		JLocalVariableExpression createVarReference = new JLocalVariableExpression(self.getTokenReference(), variableDefinition);

		// ********** SUPER CALL ******************
		super.visitNewObjectExpression(self);
		JNewObjectExpression newSelf = (JNewObjectExpression) this.getArrayStack().pop();
		// ***************************************
		JStatement assignamentStatement = ASTUtils.createAssignamentStatement(createVarReference, newSelf);
		this.getNewStatements().add(assignamentStatement);

		getArrayStack().add(createVarReference);

		expressionDeep--;
	}

	@Override
	public void visitNewArrayExpression(JNewArrayExpression self) {
		expressionDeep++;

		String createdVar = createNewVariableName();
		JVariableDefinition variableDefinition = new JVariableDefinition(self.getTokenReference(), 0, self.getType(), createdVar, null);
		JVariableDeclarationStatement variableDeclarationStatement = new JVariableDeclarationStatement(self.getTokenReference(), variableDefinition,
				new JavaStyleComment[0]);
		getDeclarationStatements().add(variableDeclarationStatement);

		JLocalVariableExpression createVarReference = new JLocalVariableExpression(self.getTokenReference(), variableDefinition);
		// ********** SUPER CALL ******************
		super.visitNewArrayExpression(self);
		JNewArrayExpression newSelf = (JNewArrayExpression) this.getArrayStack().pop();
		// ***************************************

		JStatement assignamentStatement = ASTUtils.createAssignamentStatement(createVarReference, newSelf);
		this.getNewStatements().add(assignamentStatement);

		getArrayStack().add(createVarReference);

		expressionDeep--;
	}

	@Override
	public void visitMethodCallExpression(JMethodCallExpression self) {
		expressionDeep++;

		// ********** SUPER CALL ******************
		super.visitMethodCallExpression(self);
		JMethodCallExpression newSelf = (JMethodCallExpression) this.getArrayStack().pop();
		// ***************************************

		if (isVoidType(newSelf.getType())) {
			getArrayStack().add(newSelf);
		} else {			
			
			MethodInformation methodInformation = MethodInformationBuilder.getInstance().getMethodInformation(self);
			String createdVar = createNewVariableName();
			CType returnedType = methodInformation.getReturnType();

			JVariableDefinition variableDefinition = new JVariableDefinition(self.getTokenReference(), 0, returnedType, createdVar, null);
			JVariableDeclarationStatement variableDeclarationStatement = new JVariableDeclarationStatement(self.getTokenReference(), variableDefinition,
					new JavaStyleComment[0]);
			getDeclarationStatements().add(variableDeclarationStatement);

			JLocalVariableExpression createVarReference = new JLocalVariableExpression(self.getTokenReference(), variableDefinition);

			JStatement assignamentStatement = ASTUtils.createAssignamentStatement(createVarReference, newSelf);
			this.getNewStatements().add(assignamentStatement);

			getArrayStack().add(createVarReference);



		}

		expressionDeep--;
	}

	private boolean isVoidType(CType type) {
		return type instanceof CVoidType;
	}

	@Override
	public void visitCastExpression(JCastExpression self) {
		expressionDeep++;
		// ********** SUPER CALL ******************
		super.visitCastExpression(self);
		JCastExpression newSelf = (JCastExpression) this.getArrayStack().pop();
		// ***************************************

		String createdVar = createNewVariableName();
		JVariableDefinition variableDefinition = new JVariableDefinition(self.getTokenReference(), 0, self.getType(), createdVar, null);
		JVariableDeclarationStatement variableDeclarationStatement = new JVariableDeclarationStatement(self.getTokenReference(), variableDefinition,
				new JavaStyleComment[0]);
		getDeclarationStatements().add(variableDeclarationStatement);

		JLocalVariableExpression createVarReference = new JLocalVariableExpression(self.getTokenReference(), variableDefinition);

		JStatement assignamentStatement = ASTUtils.createAssignamentStatement(createVarReference, newSelf);
		this.getNewStatements().add(assignamentStatement);

		getArrayStack().add(createVarReference);

		expressionDeep--;
	}

	@Override
	public void visitArrayAccessExpression(JArrayAccessExpression self) {
		expressionDeep++;

		// ********** SUPER CALL ******************
		super.visitArrayAccessExpression(self);
		JArrayAccessExpression newSelf = (JArrayAccessExpression) this.getArrayStack().pop();
		// ***************************************
		if (isInLeftSizeOfAssignament()) {

			getArrayStack().add(newSelf);

		} else {

			String createdVar = createNewVariableName();
			JVariableDefinition variableDefinition = new JVariableDefinition(self.getTokenReference(), 0, self.getType(), createdVar, null);
			JVariableDeclarationStatement variableDeclarationStatement = new JVariableDeclarationStatement(self.getTokenReference(), variableDefinition,
					new JavaStyleComment[0]);
			getDeclarationStatements().add(variableDeclarationStatement);

			JLocalVariableExpression createVarReference = new JLocalVariableExpression(self.getTokenReference(), variableDefinition);

			JStatement assignamentStatement = ASTUtils.createAssignamentStatement(createVarReference, newSelf);
			this.getNewStatements().add(assignamentStatement);

			getArrayStack().add(createVarReference);
		}

		expressionDeep--;
	}

	/** Visits the given assignment expression. */
	public void visitAssignmentExpression(/* @non_null */JAssignmentExpression self) {
		expressionDeep++;

		inAssignament = true;
		assignationRightnessCount++;
		self.right().accept(this);
		assignationRightnessCount--;
		JExpression rightExpression = this.getArrayStack().pop();

		// this will does not work for recursive case! (ej: a[i]=b[j]=1)		
		self.left().accept(this);
		JExpression leftExpression = this.getArrayStack().pop();

		JAssignmentExpression newSelf = new JAssignmentExpression(self.getTokenReference(), leftExpression, rightExpression);		
		if (isInLeftSizeOfAssignament()) {
			if (isExpressionStatement()) {
				this.getArrayStack().push(newSelf);
			} else {

				JLocalVariableExpression createVarReference = createNewVariable(self.getTokenReference(),self.left().getType());
				JStatement assignamentStatement = ASTUtils.createAssignamentStatement(createVarReference, newSelf);
				this.getNewStatements().add(new JExpressionStatement(newSelf.getTokenReference(), newSelf, null));
				this.getNewStatements().add(assignamentStatement);
				getArrayStack().push(createVarReference);
			}
		} else {
			JLocalVariableExpression createVarReference = createNewVariable(self.getTokenReference(),self.left().getType());

			JStatement assignamentStatement = ASTUtils.createAssignamentStatement(createVarReference, leftExpression);
			this.getNewStatements().add(new JExpressionStatement(newSelf.getTokenReference(), newSelf, null));
			this.getNewStatements().add(assignamentStatement);

			getArrayStack().push(createVarReference);
		}
		if (assignationRightnessCount == 0) {
			inAssignament = false;
		}

		expressionDeep--;
	}






	public JLocalVariableExpression createNewVariable(
			TokenReference tokenReference, CType type) {
		String createdVar = createNewVariableName();
		JVariableDefinition variableDefinition = new JVariableDefinition(tokenReference, 0, type, createdVar, null);
		JVariableDeclarationStatement variableDeclarationStatement = new JVariableDeclarationStatement(tokenReference, variableDefinition,
				new JavaStyleComment[0]);
		getDeclarationStatements().add(variableDeclarationStatement);

		JLocalVariableExpression createVarReference = new JLocalVariableExpression(tokenReference, variableDefinition);
		return createVarReference;
	}


	private boolean isInLeftSizeOfAssignament() {
		//return expressionStatement && inAssignament && assignationRightnessCount == 0;
		return inAssignament && assignationRightnessCount == 0;
	}

	//	/** Visits the given prefix expression. */
	//	public void visitPrefixExpression(/* @non_null */JPrefixExpression self) {
	//		self.expr().accept(this);
	//		JPrefixExpression newSelf = new JPrefixExpression(self.getTokenReference(), self.oper(), this.getArrayStack().pop());
	//		this.getArrayStack().push(newSelf);
	//	}


	//mfrias-mffrias 12-04-2013
	//	@Override
	//	public void visitFieldExpression(JClassFieldExpression self) {
	//		JExpression prefix;
	//		if (self.prefix() == null && !self.getField().isStatic()) {
	//			prefix = new JThisExpression(self.getTokenReference());
	//		} else if (self.prefix() == null && self.getField().isStatic()) {
	//			prefix = new JTypeNameExpression(self.getTokenReference(),self.getField().owner().getType(), new JNameExpression(self.getTokenReference(),self.getField().owner().ident()));
	//		} else {
	//			self.prefix().accept(this);
	//			prefix = this.getArrayStack().pop();
	//		}
	//			
	//		String newName;
	//		
	//		String ident = self.ident();
	//		if (this.variableNamesReplaces.containsKey( ident )) {
	//			JLocalVariable localVariable = this.variableNamesReplaces.get(ident);
	//			JLocalVariableExpression newSelf = new JLocalVariableExpression(self.getTokenReference(), localVariable);
	//			this.getArrayStack().push(newSelf);
	//
	//		} else {
	//			this.getArrayStack().push(self);
	//		}
	//		
	//		JClassFieldExpression newSelf = new JClassFieldExpression(self.getTokenReference(), prefix, newName);
	//		newSelf.setField(self.getField());
	//		newSelf.setType(self.getType());
	//
	//		this.getArrayStack().push(newSelf);
	//	}



	/** Visits the given postfix expression. */
	public void visitPostfixExpression(/* @non_null */JPostfixExpression self) {
		expressionDeep++;

		self.expr().accept(this);		
		JExpression paramExpr = this.getArrayStack().pop();

		if (expressionDeep == 1 && paramExpr instanceof JLocalVariableExpression) {
			JPostfixExpression newSelf = new JPostfixExpression(self.getTokenReference(), self.oper(), paramExpr);
			getArrayStack().push(newSelf);
		} else {

			String createdVar = createNewVariableName();
			JVariableDefinition variableDefinition = new JVariableDefinition(self.getTokenReference(), 0, self.expr().getType(), createdVar, null);
			JVariableDeclarationStatement variableDeclarationStatement = new JVariableDeclarationStatement(self.getTokenReference(), variableDefinition,
					new JavaStyleComment[0]);
			getDeclarationStatements().add(variableDeclarationStatement);

			JLocalVariableExpression createVarReference = new JLocalVariableExpression(self.getTokenReference(), variableDefinition);

			String oper;
			if (self.oper() == org.multijava.mjc.Constants.OPE_POSTINC) {
				oper = "1";
			} else {
				oper = "-1";
			}

			JAssignmentExpression newSelf = new JAssignmentExpression(self.getTokenReference(), paramExpr, new JmlAddExpression(self.getTokenReference(), paramExpr, new JOrdinalLiteral(self.getTokenReference(), oper)));


			JStatement assignamentStatement = ASTUtils.createAssignamentStatement(createVarReference, paramExpr);
			this.getNewStatements().add(assignamentStatement);
			this.getPostfixNewStatements().add(new JExpressionStatement(newSelf.getTokenReference(), newSelf, null));


			getArrayStack().push(createVarReference);
		}

		expressionDeep--;
	}

	public void visitPrefixExpression(/* @non_null */JPrefixExpression self) {
		expressionDeep++;

		self.expr().accept(this);		
		JExpression paramExpr = this.getArrayStack().pop();


		String createdVar = createNewVariableName();
		JVariableDefinition variableDefinition = new JVariableDefinition(self.getTokenReference(), 0, self.expr().getType(), createdVar, null);
		JVariableDeclarationStatement variableDeclarationStatement = new JVariableDeclarationStatement(self.getTokenReference(), variableDefinition,
				new JavaStyleComment[0]);
		getDeclarationStatements().add(variableDeclarationStatement);

		JLocalVariableExpression createVarReference = new JLocalVariableExpression(self.getTokenReference(), variableDefinition);

		String oper;
		if (self.oper() == org.multijava.mjc.Constants.OPE_PREINC) {
			oper = "1";
		} else {
			oper = "-1";
		}

		JAssignmentExpression newSelf = new JAssignmentExpression(self.getTokenReference(), paramExpr, new JmlAddExpression(self.getTokenReference(),paramExpr, new JOrdinalLiteral(self.getTokenReference(), oper))) ;


		JStatement assignamentStatement = ASTUtils.createAssignamentStatement(createVarReference, paramExpr);
		this.getNewStatements().add(new JExpressionStatement(newSelf.getTokenReference(), newSelf, null));
		this.getNewStatements().add(assignamentStatement);


		getArrayStack().push(createVarReference);

		expressionDeep--;
	}

	/** Visits the given unary expression. */
	public void visitUnaryExpression(/* @non_null */JUnaryExpression self) {
		boolean needSimplification = needSimplification();
		expressionDeep++;

		self.expr().accept(this);
		JExpression expr = this.getArrayStack().pop();
		JUnaryExpression newSelf = new JUnaryExpression(self.getTokenReference(), self.oper(), expr);
		if (needSimplification) {
			JLocalVariableExpression createVarReference = createNewVariable(self.getTokenReference(),expr.getType());
			JStatement assignamentStatement = ASTUtils.createAssignamentStatement(createVarReference, newSelf);
			this.getNewStatements().add(assignamentStatement);
			getArrayStack().push(createVarReference);

		} else {
			this.getArrayStack().push(newSelf);
		}

		expressionDeep--;
	}

	private boolean needSimplification() {
		if (isExpressionStatement()) {
			if (assignationRightnessCount != 0) {
				return expressionDeep > 1;
			} else {
				return expressionDeep > 0;
			}
		} else {
			return true;
		}
	}

	public void visitShiftExpression(/* @non_null */JShiftExpression self) {
		boolean needSimplification = needSimplification();

		expressionDeep++;

		self.left().accept(this);
		JExpression left = this.getArrayStack().pop();

		self.right().accept(this);
		JExpression right = this.getArrayStack().pop();

		JShiftExpression newSelf = new JShiftExpression(self.getTokenReference(), self.oper(), left, right);
		if (needSimplification) {
			JLocalVariableExpression createVarReference = createNewVariable(self.getTokenReference(),left.getType());
			JStatement assignamentStatement = ASTUtils.createAssignamentStatement(createVarReference, newSelf);
			this.getNewStatements().add(assignamentStatement);
			getArrayStack().push(createVarReference);

		} else {
			this.getArrayStack().push(newSelf);
		}

		expressionDeep--;
	}

	/** Visits the given relational expression. */
	public void visitRelationalExpression(/* @non_null */JRelationalExpression self) {
		boolean needSimplification = needSimplification();

		expressionDeep++;

		self.left().accept(this);
		JExpression left = this.getArrayStack().pop();

		self.right().accept(this);
		JExpression right = this.getArrayStack().pop();

		JRelationalExpression newSelf = new JRelationalExpression(self.getTokenReference(), self.oper(), left, right);
		if (needSimplification) {
			JLocalVariableExpression createVarReference = createNewVariable(self.getTokenReference(),CStdType.Boolean);
			JStatement assignamentStatement = ASTUtils.createAssignamentStatement(createVarReference, newSelf);
			this.getNewStatements().add(assignamentStatement);
			getArrayStack().push(createVarReference);

		} else {
			this.getArrayStack().push(newSelf);
		}

		expressionDeep--;
	}

	@Override
	public void visitJmlRelationalExpression(JmlRelationalExpression self) {

		boolean needSimplification = needSimplification();

		expressionDeep++;

		self.left().accept(this);
		JExpression left = this.getArrayStack().pop();

		self.right().accept(this);
		JExpression right = this.getArrayStack().pop();

		JmlRelationalExpression newSelf = new JmlRelationalExpression(self.getTokenReference(), self.oper(), left, right);
		if (needSimplification || !this.getPostfixNewStatements().isEmpty()) {
			JLocalVariableExpression createVarReference = createNewVariable(self.getTokenReference(),CStdType.Boolean);
			JStatement assignamentStatement = ASTUtils.createAssignamentStatement(createVarReference, newSelf);
			this.getNewStatements().add(assignamentStatement);
			getArrayStack().push(createVarReference);

		} else {
			this.getArrayStack().push(newSelf);
		}

		for (int idx = 0; idx < this.getPostfixNewStatements().size(); idx++){
			this.getNewStatements().add(this.getPostfixNewStatements().get(idx));
		}
		this.setNewPostfixNewStatements();

		expressionDeep--;		
	}	


	/** Visits the given add expression. */
	public void visitAddExpression(/* @non_null */JAddExpression self) {
		boolean needSimplification = needSimplification();

		expressionDeep++;

		self.left().accept(this);
		JExpression left = this.getArrayStack().pop();

		self.right().accept(this);
		JExpression right = this.getArrayStack().pop();

		JAddExpression newSelf = new JAddExpression(self.getTokenReference(), left, right);
		if (needSimplification) {
			JLocalVariableExpression createVarReference = createNewVariable(self.getTokenReference(),left.getType());
			JStatement assignamentStatement = ASTUtils.createAssignamentStatement(createVarReference, newSelf);
			this.getNewStatements().add(assignamentStatement);
			getArrayStack().push(createVarReference);

		} else {
			this.getArrayStack().push(newSelf);
		}

		expressionDeep--;
	}


	/** Visits the given divide expression. */
	public void visitDivideExpression(/* @non_null */JDivideExpression self) {
		boolean needSimplification = needSimplification();

		expressionDeep++;

		self.left().accept(this);
		JExpression left = this.getArrayStack().pop();

		self.right().accept(this);
		JExpression right = this.getArrayStack().pop();

		JDivideExpression newSelf = new JDivideExpression(self.getTokenReference(), left, right);
		if (needSimplification) {
			JLocalVariableExpression createVarReference = createNewVariable(self.getTokenReference(),left.getType());
			JStatement assignamentStatement = ASTUtils.createAssignamentStatement(createVarReference, newSelf);
			this.getNewStatements().add(assignamentStatement);
			getArrayStack().push(createVarReference);

		} else {
			this.getArrayStack().push(newSelf);
		}

		expressionDeep--;
	}

	/** Visits the given minus expression. */
	public void visitMinusExpression(/* @non_null */JMinusExpression self) {
		boolean needSimplification = needSimplification();

		expressionDeep++;

		self.left().accept(this);
		JExpression left = this.getArrayStack().pop();

		self.right().accept(this);
		JExpression right = this.getArrayStack().pop();

		JMinusExpression newSelf = new JMinusExpression(self.getTokenReference(), left, right);
		if (needSimplification) {
			JLocalVariableExpression createVarReference = createNewVariable(self.getTokenReference(),left.getType());
			JStatement assignamentStatement = ASTUtils.createAssignamentStatement(createVarReference, newSelf);
			this.getNewStatements().add(assignamentStatement);
			getArrayStack().push(createVarReference);

		} else {
			this.getArrayStack().push(newSelf);
		}

		expressionDeep--;
	}

	/** Visits the given modulo division expression. */
	public void visitModuloExpression(/* @non_null */JModuloExpression self) {
		boolean needSimplification = needSimplification();

		expressionDeep++;

		self.left().accept(this);
		JExpression left = this.getArrayStack().pop();

		self.right().accept(this);
		JExpression right = this.getArrayStack().pop();

		JModuloExpression newSelf = new JModuloExpression(self.getTokenReference(), left, right);
		if (needSimplification) {
			JLocalVariableExpression createVarReference = createNewVariable(self.getTokenReference(),left.getType());
			JStatement assignamentStatement = ASTUtils.createAssignamentStatement(createVarReference, newSelf);
			this.getNewStatements().add(assignamentStatement);
			getArrayStack().push(createVarReference);

		} else {
			this.getArrayStack().push(newSelf);
		}

		expressionDeep--;
	}

	/** Visits the given multiplication expression. */
	public void visitMultExpression(/* @non_null */JMultExpression self) {
		boolean needSimplification = needSimplification();

		expressionDeep++;

		self.left().accept(this);
		JExpression left = this.getArrayStack().pop();

		self.right().accept(this);
		JExpression right = this.getArrayStack().pop();

		JMultExpression newSelf = new JMultExpression(self.getTokenReference(), left, right);
		if (needSimplification) {
			JLocalVariableExpression createVarReference = createNewVariable(self.getTokenReference(),left.getType());
			JStatement assignamentStatement = ASTUtils.createAssignamentStatement(createVarReference, newSelf);
			this.getNewStatements().add(assignamentStatement);
			getArrayStack().push(createVarReference);

		} else {
			this.getArrayStack().push(newSelf);
		}

		expressionDeep--;
	}


	/** Visits the given equality expression. */
	public void visitEqualityExpression(/* @non_null */JEqualityExpression self) {
		boolean needSimplification = needSimplification();

		expressionDeep++;

		self.left().accept(this);
		JExpression left = this.getArrayStack().pop();

		self.right().accept(this);
		JExpression right = this.getArrayStack().pop();

		JEqualityExpression newSelf = new JEqualityExpression(self.getTokenReference(), self.oper(), left, right);
		if (needSimplification || !this.getPostfixNewStatements().isEmpty()) {
			JLocalVariableExpression createVarReference = createNewVariable(self.getTokenReference(),CStdType.Boolean);
			JStatement assignamentStatement = ASTUtils.createAssignamentStatement(createVarReference, newSelf);
			this.getNewStatements().add(assignamentStatement);
			getArrayStack().push(createVarReference);

		} else {
			this.getArrayStack().push(newSelf);
		}
		for (int idx = 0; idx < this.getPostfixNewStatements().size(); idx++){
			this.getNewStatements().add(this.getPostfixNewStatements().get(idx));
		}
		this.setNewPostfixNewStatements();

		expressionDeep--;
	}

	/** Visits the given bitwise expression. */
	public void visitBitwiseExpression(/* @non_null */JBitwiseExpression self) {
		boolean needSimplification = needSimplification();

		expressionDeep++;

		self.left().accept(this);
		JExpression left = this.getArrayStack().pop();

		self.right().accept(this);
		JExpression right = this.getArrayStack().pop();
		JBitwiseExpression newSelf = new JBitwiseExpression(self.getTokenReference(), self.oper(), left, right);
		if (needSimplification) {
			JLocalVariableExpression createVarReference = createNewVariable(self.getTokenReference(),left.getType());
			JStatement assignamentStatement = ASTUtils.createAssignamentStatement(createVarReference, newSelf);
			this.getNewStatements().add(assignamentStatement);
			getArrayStack().push(createVarReference);

		} else {
			this.getArrayStack().push(newSelf);
		}

		expressionDeep--;		
	}

	/** Visits the given instanceof expression. */
	public void visitInstanceofExpression(/* @non_null */JInstanceofExpression self) {
		boolean needSimplification = needSimplification();

		expressionDeep++;

		self.expr().accept(this);
		JInstanceofExpression newSelf = new JInstanceofExpression(self.getTokenReference(), this.getArrayStack().pop(), self.dest());
		if (needSimplification) {
			JLocalVariableExpression createVarReference = createNewVariable(self.getTokenReference(),CStdType.Boolean);
			JStatement assignamentStatement = ASTUtils.createAssignamentStatement(createVarReference, newSelf);
			this.getNewStatements().add(assignamentStatement);
			getArrayStack().push(createVarReference);

		} else {
			this.getArrayStack().push(newSelf);
		}

		expressionDeep--;			
	}

	//	@Override
	//	public void visitLocalVariableExpression(JLocalVariableExpression self) {
	//		String ident = self.variable().ident();
	//		if (this.variableNamesReplaces.containsKey( ident )) {
	//			JLocalVariable localVariable = this.variableNamesReplaces.get(ident);
	//			JLocalVariableExpression newSelf = new JLocalVariableExpression(self.getTokenReference(), localVariable);
	//			this.getArrayStack().push(newSelf);
	//
	//		} else {
	//			this.getArrayStack().push(self);
	//		}
	//		
	//	}







}

