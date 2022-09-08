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

import java.util.List;

import org.jmlspecs.checker.*;
import org.multijava.mjc.CType;
import org.multijava.mjc.JAddExpression;
import org.multijava.mjc.JArrayAccessExpression;
import org.multijava.mjc.JArrayDimsAndInits;
import org.multijava.mjc.JArrayInitializer;
import org.multijava.mjc.JArrayLengthExpression;
import org.multijava.mjc.JAssertStatement;
import org.multijava.mjc.JAssignmentExpression;
import org.multijava.mjc.JBitwiseExpression;
import org.multijava.mjc.JBlock;
import org.multijava.mjc.JBooleanLiteral;
import org.multijava.mjc.JBreakStatement;
import org.multijava.mjc.JCastExpression;
import org.multijava.mjc.JCatchClause;
import org.multijava.mjc.JCharLiteral;
import org.multijava.mjc.JClassBlock;
import org.multijava.mjc.JClassDeclaration;
import org.multijava.mjc.JClassExpression;
import org.multijava.mjc.JClassFieldExpression;
import org.multijava.mjc.JClassOrGFImport;
import org.multijava.mjc.JCompilationUnit;
import org.multijava.mjc.JCompoundAssignmentExpression;
import org.multijava.mjc.JCompoundStatement;
import org.multijava.mjc.JConditionalAndExpression;
import org.multijava.mjc.JConditionalExpression;
import org.multijava.mjc.JConditionalOrExpression;
import org.multijava.mjc.JConstructorBlock;
import org.multijava.mjc.JConstructorDeclaration;
import org.multijava.mjc.JContinueStatement;
import org.multijava.mjc.JDivideExpression;
import org.multijava.mjc.JDoStatement;
import org.multijava.mjc.JEmptyStatement;
import org.multijava.mjc.JEqualityExpression;
import org.multijava.mjc.JExplicitConstructorInvocation;
import org.multijava.mjc.JExpression;
import org.multijava.mjc.JExpressionListStatement;
import org.multijava.mjc.JExpressionStatement;
import org.multijava.mjc.JFieldDeclaration;
import org.multijava.mjc.JForStatement;
import org.multijava.mjc.JFormalParameter;
import org.multijava.mjc.JIfStatement;
import org.multijava.mjc.JInitializerDeclaration;
import org.multijava.mjc.JInstanceofExpression;
import org.multijava.mjc.JInterfaceDeclaration;
import org.multijava.mjc.JLabeledStatement;
import org.multijava.mjc.JLocalVariableExpression;
import org.multijava.mjc.JMethodCallExpression;
import org.multijava.mjc.JMethodDeclaration;
import org.multijava.mjc.JMinusExpression;
import org.multijava.mjc.JModuloExpression;
import org.multijava.mjc.JMultExpression;
import org.multijava.mjc.JNameExpression;
import org.multijava.mjc.JNewAnonymousClassExpression;
import org.multijava.mjc.JNewArrayExpression;
import org.multijava.mjc.JNewObjectExpression;
import org.multijava.mjc.JNullLiteral;
import org.multijava.mjc.JOrdinalLiteral;
import org.multijava.mjc.JPackageImport;
import org.multijava.mjc.JPackageName;
import org.multijava.mjc.JParenthesedExpression;
import org.multijava.mjc.JPostfixExpression;
import org.multijava.mjc.JPrefixExpression;
import org.multijava.mjc.JRealLiteral;
import org.multijava.mjc.JRelationalExpression;
import org.multijava.mjc.JReturnStatement;
import org.multijava.mjc.JShiftExpression;
import org.multijava.mjc.JStatement;
import org.multijava.mjc.JStringLiteral;
import org.multijava.mjc.JSuperExpression;
import org.multijava.mjc.JSwitchGroup;
import org.multijava.mjc.JSwitchLabel;
import org.multijava.mjc.JSwitchStatement;
import org.multijava.mjc.JSynchronizedStatement;
import org.multijava.mjc.JThisExpression;
import org.multijava.mjc.JThrowStatement;
import org.multijava.mjc.JTryCatchStatement;
import org.multijava.mjc.JTryFinallyStatement;
import org.multijava.mjc.JTypeDeclarationStatement;
import org.multijava.mjc.JTypeNameExpression;
import org.multijava.mjc.JUnaryExpression;
import org.multijava.mjc.JUnaryPromote;
import org.multijava.mjc.JVariableDeclarationStatement;
import org.multijava.mjc.JVariableDefinition;
import org.multijava.mjc.JWhileStatement;
import org.multijava.mjc.MJGenericFunctionDecl;
import org.multijava.mjc.MJMathModeExpression;
import org.multijava.mjc.MJTopLevelMethodDeclaration;
import org.multijava.mjc.MJWarnExpression;
import org.multijava.util.compiler.JavaStyleComment;
import org.multijava.util.compiler.TokenReference;

import ar.edu.taco.TacoNotImplementedYetException;
import ar.edu.taco.jml.expression.ESExpressionVisitor;
import ar.edu.taco.jml.utils.ASTUtils;
import ar.edu.taco.utils.jml.JmlAstClonerExpressionVisitor;


public class JmlBaseVisitor extends JmlAbstractVisitor implements JmlVisitor {

	public static int variableNameIndex;

	private List<JStatement> newStatements;

	
	/* (non-Javadoc)
	 * @see org.jmlspecs.checker.JmlVisitor#visitJmlAbruptSpecBody(org.jmlspecs.checker.JmlAbruptSpecBody)
	 */
	@Override
	public void visitJmlAbruptSpecBody(JmlAbruptSpecBody arg0) {
		throw new TacoNotImplementedYetException();
	}

	/* (non-Javadoc)
	 * @see org.jmlspecs.checker.JmlVisitor#visitJmlAbruptSpecCase(org.jmlspecs.checker.JmlAbruptSpecCase)
	 */
	@Override
	public void visitJmlAbruptSpecCase(JmlAbruptSpecCase arg0) {
		throw new TacoNotImplementedYetException();
	}

	/* (non-Javadoc)
	 * @see org.jmlspecs.checker.JmlVisitor#visitJmlAccessibleClause(org.jmlspecs.checker.JmlAccessibleClause)
	 */
	@Override
	public void visitJmlAccessibleClause(JmlAccessibleClause arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.jmlspecs.checker.JmlVisitor#visitJmlAssertStatement(org.jmlspecs.checker.JmlAssertStatement)
	 */
	@Override
	public void visitJmlAssertStatement(JmlAssertStatement arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.jmlspecs.checker.JmlVisitor#visitJmlAssignableClause(org.jmlspecs.checker.JmlAssignableClause)
	 */
	@Override
	public void visitJmlAssignableClause(JmlAssignableClause arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.jmlspecs.checker.JmlVisitor#visitJmlAssignmentStatement(org.jmlspecs.checker.JmlAssignmentStatement)
	 */
	@Override
	public void visitJmlAssignmentStatement(JmlAssignmentStatement arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.jmlspecs.checker.JmlVisitor#visitJmlAssumeStatement(org.jmlspecs.checker.JmlAssumeStatement)
	 */
	@Override
	public void visitJmlAssumeStatement(JmlAssumeStatement arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.jmlspecs.checker.JmlVisitor#visitJmlAxiom(org.jmlspecs.checker.JmlAxiom)
	 */
	@Override
	public void visitJmlAxiom(JmlAxiom arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.jmlspecs.checker.JmlVisitor#visitJmlBehaviorSpec(org.jmlspecs.checker.JmlBehaviorSpec)
	 */
	@Override
	public void visitJmlBehaviorSpec(JmlBehaviorSpec arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.jmlspecs.checker.JmlVisitor#visitJmlBreaksClause(org.jmlspecs.checker.JmlBreaksClause)
	 */
	@Override
	public void visitJmlBreaksClause(JmlBreaksClause arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.jmlspecs.checker.JmlVisitor#visitJmlCallableClause(org.jmlspecs.checker.JmlCallableClause)
	 */
	@Override
	public void visitJmlCallableClause(JmlCallableClause arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.jmlspecs.checker.JmlVisitor#visitJmlCapturesClause(org.jmlspecs.checker.JmlCapturesClause)
	 */
	@Override
	public void visitJmlCapturesClause(JmlCapturesClause arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.jmlspecs.checker.JmlVisitor#visitJmlClassBlock(org.jmlspecs.checker.JmlClassBlock)
	 */
	@Override
	public void visitJmlClassBlock(JmlClassBlock arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.jmlspecs.checker.JmlVisitor#visitJmlClassDeclaration(org.jmlspecs.checker.JmlClassDeclaration)
	 */
	@Override
	public void visitJmlClassDeclaration(JmlClassDeclaration arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.jmlspecs.checker.JmlVisitor#visitJmlClassOrGFImport(org.jmlspecs.checker.JmlClassOrGFImport)
	 */
	@Override
	public void visitJmlClassOrGFImport(JmlClassOrGFImport arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.jmlspecs.checker.JmlVisitor#visitJmlCodeContract(org.jmlspecs.checker.JmlCodeContract)
	 */
	@Override
	public void visitJmlCodeContract(JmlCodeContract arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.jmlspecs.checker.JmlVisitor#visitJmlCompilationUnit(org.jmlspecs.checker.JmlCompilationUnit)
	 */
	@Override
	public void visitJmlCompilationUnit(JmlCompilationUnit arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.jmlspecs.checker.JmlVisitor#visitJmlConstraint(org.jmlspecs.checker.JmlConstraint)
	 */
	@Override
	public void visitJmlConstraint(JmlConstraint arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.jmlspecs.checker.JmlVisitor#visitJmlConstructorDeclaration(org.jmlspecs.checker.JmlConstructorDeclaration)
	 */
	@Override
	public void visitJmlConstructorDeclaration(JmlConstructorDeclaration arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.jmlspecs.checker.JmlVisitor#visitJmlConstructorName(org.jmlspecs.checker.JmlConstructorName)
	 */
	@Override
	public void visitJmlConstructorName(JmlConstructorName arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.jmlspecs.checker.JmlVisitor#visitJmlContinuesClause(org.jmlspecs.checker.JmlContinuesClause)
	 */
	@Override
	public void visitJmlContinuesClause(JmlContinuesClause arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.jmlspecs.checker.JmlVisitor#visitJmlDebugStatement(org.jmlspecs.checker.JmlDebugStatement)
	 */
	@Override
	public void visitJmlDebugStatement(JmlDebugStatement arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.jmlspecs.checker.JmlVisitor#visitJmlDeclaration(org.jmlspecs.checker.JmlDeclaration)
	 */
	@Override
	public void visitJmlDeclaration(JmlDeclaration arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.jmlspecs.checker.JmlVisitor#visitJmlDivergesClause(org.jmlspecs.checker.JmlDivergesClause)
	 */
	@Override
	public void visitJmlDivergesClause(JmlDivergesClause arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.jmlspecs.checker.JmlVisitor#visitJmlDurationClause(org.jmlspecs.checker.JmlDurationClause)
	 */
	@Override
	public void visitJmlDurationClause(JmlDurationClause arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.jmlspecs.checker.JmlVisitor#visitJmlDurationExpression(org.jmlspecs.checker.JmlDurationExpression)
	 */
	@Override
	public void visitJmlDurationExpression(JmlDurationExpression arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.jmlspecs.checker.JmlVisitor#visitJmlElemTypeExpression(org.jmlspecs.checker.JmlElemTypeExpression)
	 */
	@Override
	public void visitJmlElemTypeExpression(JmlElemTypeExpression arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.jmlspecs.checker.JmlVisitor#visitJmlEnsuresClause(org.jmlspecs.checker.JmlEnsuresClause)
	 */
	@Override
	public void visitJmlEnsuresClause(JmlEnsuresClause arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.jmlspecs.checker.JmlVisitor#visitJmlExample(org.jmlspecs.checker.JmlExample)
	 */
	@Override
	public void visitJmlExample(JmlExample arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.jmlspecs.checker.JmlVisitor#visitJmlExceptionalBehaviorSpec(org.jmlspecs.checker.JmlExceptionalBehaviorSpec)
	 */
	@Override
	public void visitJmlExceptionalBehaviorSpec(JmlExceptionalBehaviorSpec arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.jmlspecs.checker.JmlVisitor#visitJmlExceptionalExample(org.jmlspecs.checker.JmlExceptionalExample)
	 */
	@Override
	public void visitJmlExceptionalExample(JmlExceptionalExample arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.jmlspecs.checker.JmlVisitor#visitJmlExceptionalSpecBody(org.jmlspecs.checker.JmlExceptionalSpecBody)
	 */
	@Override
	public void visitJmlExceptionalSpecBody(JmlExceptionalSpecBody arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.jmlspecs.checker.JmlVisitor#visitJmlExceptionalSpecCase(org.jmlspecs.checker.JmlExceptionalSpecCase)
	 */
	@Override
	public void visitJmlExceptionalSpecCase(JmlExceptionalSpecCase arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.jmlspecs.checker.JmlVisitor#visitJmlExpression(org.jmlspecs.checker.JmlExpression)
	 */
	@Override
	public void visitJmlExpression(JmlExpression arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.jmlspecs.checker.JmlVisitor#visitJmlExtendingSpecification(org.jmlspecs.checker.JmlExtendingSpecification)
	 */
	@Override
	public void visitJmlExtendingSpecification(JmlExtendingSpecification arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.jmlspecs.checker.JmlVisitor#visitJmlFieldDeclaration(org.jmlspecs.checker.JmlFieldDeclaration)
	 */
	@Override
	public void visitJmlFieldDeclaration(JmlFieldDeclaration arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.jmlspecs.checker.JmlVisitor#visitJmlForAllVarDecl(org.jmlspecs.checker.JmlForAllVarDecl)
	 */
	@Override
	public void visitJmlForAllVarDecl(JmlForAllVarDecl arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.jmlspecs.checker.JmlVisitor#visitJmlFormalParameter(org.jmlspecs.checker.JmlFormalParameter)
	 */
	@Override
	public void visitJmlFormalParameter(JmlFormalParameter arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.jmlspecs.checker.JmlVisitor#visitJmlFreshExpression(org.jmlspecs.checker.JmlFreshExpression)
	 */
	@Override
	public void visitJmlFreshExpression(JmlFreshExpression arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.jmlspecs.checker.JmlVisitor#visitJmlGeneralSpecCase(org.jmlspecs.checker.JmlGeneralSpecCase)
	 */
	@Override
	public void visitJmlGeneralSpecCase(JmlGeneralSpecCase arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.jmlspecs.checker.JmlVisitor#visitJmlGenericSpecBody(org.jmlspecs.checker.JmlGenericSpecBody)
	 */
	@Override
	public void visitJmlGenericSpecBody(JmlGenericSpecBody arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.jmlspecs.checker.JmlVisitor#visitJmlGenericSpecCase(org.jmlspecs.checker.JmlGenericSpecCase)
	 */
	@Override
	public void visitJmlGenericSpecCase(JmlGenericSpecCase arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.jmlspecs.checker.JmlVisitor#visitJmlGuardedStatement(org.jmlspecs.checker.JmlGuardedStatement)
	 */
	@Override
	public void visitJmlGuardedStatement(JmlGuardedStatement arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.jmlspecs.checker.JmlVisitor#visitJmlHenceByStatement(org.jmlspecs.checker.JmlHenceByStatement)
	 */
	@Override
	public void visitJmlHenceByStatement(JmlHenceByStatement arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.jmlspecs.checker.JmlVisitor#visitJmlInGroupClause(org.jmlspecs.checker.JmlInGroupClause)
	 */
	@Override
	public void visitJmlInGroupClause(JmlInGroupClause arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.jmlspecs.checker.JmlVisitor#visitJmlInformalExpression(org.jmlspecs.checker.JmlInformalExpression)
	 */
	@Override
	public void visitJmlInformalExpression(JmlInformalExpression arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.jmlspecs.checker.JmlVisitor#visitJmlInformalStoreRef(org.jmlspecs.checker.JmlInformalStoreRef)
	 */
	@Override
	public void visitJmlInformalStoreRef(JmlInformalStoreRef arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.jmlspecs.checker.JmlVisitor#visitJmlInitiallyVarAssertion(org.jmlspecs.checker.JmlInitiallyVarAssertion)
	 */
	@Override
	public void visitJmlInitiallyVarAssertion(JmlInitiallyVarAssertion arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.jmlspecs.checker.JmlVisitor#visitJmlInterfaceDeclaration(org.jmlspecs.checker.JmlInterfaceDeclaration)
	 */
	@Override
	public void visitJmlInterfaceDeclaration(JmlInterfaceDeclaration arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.jmlspecs.checker.JmlVisitor#visitJmlInvariant(org.jmlspecs.checker.JmlInvariant)
	 */
	@Override
	public void visitJmlInvariant(JmlInvariant arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.jmlspecs.checker.JmlVisitor#visitJmlInvariantForExpression(org.jmlspecs.checker.JmlInvariantForExpression)
	 */
	@Override
	public void visitJmlInvariantForExpression(JmlInvariantForExpression arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.jmlspecs.checker.JmlVisitor#visitJmlInvariantStatement(org.jmlspecs.checker.JmlInvariantStatement)
	 */
	@Override
	public void visitJmlInvariantStatement(JmlInvariantStatement arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.jmlspecs.checker.JmlVisitor#visitJmlIsInitializedExpression(org.jmlspecs.checker.JmlIsInitializedExpression)
	 */
	@Override
	public void visitJmlIsInitializedExpression(JmlIsInitializedExpression arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.jmlspecs.checker.JmlVisitor#visitJmlLabelExpression(org.jmlspecs.checker.JmlLabelExpression)
	 */
	@Override
	public void visitJmlLabelExpression(JmlLabelExpression arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.jmlspecs.checker.JmlVisitor#visitJmlLetVarDecl(org.jmlspecs.checker.JmlLetVarDecl)
	 */
	@Override
	public void visitJmlLetVarDecl(JmlLetVarDecl arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.jmlspecs.checker.JmlVisitor#visitJmlLockSetExpression(org.jmlspecs.checker.JmlLockSetExpression)
	 */
	@Override
	public void visitJmlLockSetExpression(JmlLockSetExpression arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.jmlspecs.checker.JmlVisitor#visitJmlLoopInvariant(org.jmlspecs.checker.JmlLoopInvariant)
	 */
	@Override
	public void visitJmlLoopInvariant(JmlLoopInvariant arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.jmlspecs.checker.JmlVisitor#visitJmlLoopStatement(org.jmlspecs.checker.JmlLoopStatement)
	 */
	@Override
	public void visitJmlLoopStatement(JmlLoopStatement arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.jmlspecs.checker.JmlVisitor#visitJmlMapsIntoClause(org.jmlspecs.checker.JmlMapsIntoClause)
	 */
	@Override
	public void visitJmlMapsIntoClause(JmlMapsIntoClause arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.jmlspecs.checker.JmlVisitor#visitJmlMaxExpression(org.jmlspecs.checker.JmlMaxExpression)
	 */
	@Override
	public void visitJmlMaxExpression(JmlMaxExpression arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.jmlspecs.checker.JmlVisitor#visitJmlMeasuredClause(org.jmlspecs.checker.JmlMeasuredClause)
	 */
	@Override
	public void visitJmlMeasuredClause(JmlMeasuredClause arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.jmlspecs.checker.JmlVisitor#visitJmlMethodDeclaration(org.jmlspecs.checker.JmlMethodDeclaration)
	 */
	@Override
	public void visitJmlMethodDeclaration(JmlMethodDeclaration arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.jmlspecs.checker.JmlVisitor#visitJmlMethodName(org.jmlspecs.checker.JmlMethodName)
	 */
	@Override
	public void visitJmlMethodName(JmlMethodName arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.jmlspecs.checker.JmlVisitor#visitJmlMethodNameList(org.jmlspecs.checker.JmlMethodNameList)
	 */
	@Override
	public void visitJmlMethodNameList(JmlMethodNameList arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.jmlspecs.checker.JmlVisitor#visitJmlMethodSpecification(org.jmlspecs.checker.JmlMethodSpecification)
	 */
	@Override
	public void visitJmlMethodSpecification(JmlMethodSpecification arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.jmlspecs.checker.JmlVisitor#visitJmlModelProgram(org.jmlspecs.checker.JmlModelProgram)
	 */
	@Override
	public void visitJmlModelProgram(JmlModelProgram arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.jmlspecs.checker.JmlVisitor#visitJmlMonitorsForVarAssertion(org.jmlspecs.checker.JmlMonitorsForVarAssertion)
	 */
	@Override
	public void visitJmlMonitorsForVarAssertion(JmlMonitorsForVarAssertion arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.jmlspecs.checker.JmlVisitor#visitJmlName(org.jmlspecs.checker.JmlName)
	 */
	@Override
	public void visitJmlName(JmlName arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.jmlspecs.checker.JmlVisitor#visitJmlNode(org.jmlspecs.checker.JmlNode)
	 */
	@Override
	public void visitJmlNode(JmlNode arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.jmlspecs.checker.JmlVisitor#visitJmlNonNullElementsExpression(org.jmlspecs.checker.JmlNonNullElementsExpression)
	 */
	@Override
	public void visitJmlNonNullElementsExpression(JmlNonNullElementsExpression arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.jmlspecs.checker.JmlVisitor#visitJmlNondetChoiceStatement(org.jmlspecs.checker.JmlNondetChoiceStatement)
	 */
	@Override
	public void visitJmlNondetChoiceStatement(JmlNondetChoiceStatement arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.jmlspecs.checker.JmlVisitor#visitJmlNondetIfStatement(org.jmlspecs.checker.JmlNondetIfStatement)
	 */
	@Override
	public void visitJmlNondetIfStatement(JmlNondetIfStatement arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.jmlspecs.checker.JmlVisitor#visitJmlNormalBehaviorSpec(org.jmlspecs.checker.JmlNormalBehaviorSpec)
	 */
	@Override
	public void visitJmlNormalBehaviorSpec(JmlNormalBehaviorSpec arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.jmlspecs.checker.JmlVisitor#visitJmlNormalExample(org.jmlspecs.checker.JmlNormalExample)
	 */
	@Override
	public void visitJmlNormalExample(JmlNormalExample arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.jmlspecs.checker.JmlVisitor#visitJmlNormalSpecBody(org.jmlspecs.checker.JmlNormalSpecBody)
	 */
	@Override
	public void visitJmlNormalSpecBody(JmlNormalSpecBody arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.jmlspecs.checker.JmlVisitor#visitJmlNormalSpecCase(org.jmlspecs.checker.JmlNormalSpecCase)
	 */
	@Override
	public void visitJmlNormalSpecCase(JmlNormalSpecCase arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.jmlspecs.checker.JmlVisitor#visitJmlNotAssignedExpression(org.jmlspecs.checker.JmlNotAssignedExpression)
	 */
	@Override
	public void visitJmlNotAssignedExpression(JmlNotAssignedExpression arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.jmlspecs.checker.JmlVisitor#visitJmlNotModifiedExpression(org.jmlspecs.checker.JmlNotModifiedExpression)
	 */
	@Override
	public void visitJmlNotModifiedExpression(JmlNotModifiedExpression arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.jmlspecs.checker.JmlVisitor#visitJmlOldExpression(org.jmlspecs.checker.JmlOldExpression)
	 */
	@Override
	public void visitJmlOldExpression(JmlOldExpression arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.jmlspecs.checker.JmlVisitor#visitJmlOnlyAccessedExpression(org.jmlspecs.checker.JmlOnlyAccessedExpression)
	 */
	@Override
	public void visitJmlOnlyAccessedExpression(JmlOnlyAccessedExpression arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.jmlspecs.checker.JmlVisitor#visitJmlOnlyAssignedExpression(org.jmlspecs.checker.JmlOnlyAssignedExpression)
	 */
	@Override
	public void visitJmlOnlyAssignedExpression(JmlOnlyAssignedExpression arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.jmlspecs.checker.JmlVisitor#visitJmlOnlyCalledExpression(org.jmlspecs.checker.JmlOnlyCalledExpression)
	 */
	@Override
	public void visitJmlOnlyCalledExpression(JmlOnlyCalledExpression arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.jmlspecs.checker.JmlVisitor#visitJmlOnlyCapturedExpression(org.jmlspecs.checker.JmlOnlyCapturedExpression)
	 */
	@Override
	public void visitJmlOnlyCapturedExpression(JmlOnlyCapturedExpression arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.jmlspecs.checker.JmlVisitor#visitJmlPackageImport(org.jmlspecs.checker.JmlPackageImport)
	 */
	@Override
	public void visitJmlPackageImport(JmlPackageImport arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.jmlspecs.checker.JmlVisitor#visitJmlPreExpression(org.jmlspecs.checker.JmlPreExpression)
	 */
	@Override
	public void visitJmlPreExpression(JmlPreExpression arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.jmlspecs.checker.JmlVisitor#visitJmlPredicate(org.jmlspecs.checker.JmlPredicate)
	 */
	@Override
	public void visitJmlPredicate(JmlPredicate arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.jmlspecs.checker.JmlVisitor#visitJmlPredicateKeyword(org.jmlspecs.checker.JmlPredicateKeyword)
	 */
	@Override
	public void visitJmlPredicateKeyword(JmlPredicateKeyword arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.jmlspecs.checker.JmlVisitor#visitJmlReachExpression(org.jmlspecs.checker.JmlReachExpression)
	 */
	@Override
	public void visitJmlReachExpression(JmlReachExpression arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.jmlspecs.checker.JmlVisitor#visitJmlReadableIfVarAssertion(org.jmlspecs.checker.JmlReadableIfVarAssertion)
	 */
	@Override
	public void visitJmlReadableIfVarAssertion(JmlReadableIfVarAssertion arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.jmlspecs.checker.JmlVisitor#visitJmlRedundantSpec(org.jmlspecs.checker.JmlRedundantSpec)
	 */
	@Override
	public void visitJmlRedundantSpec(JmlRedundantSpec arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.jmlspecs.checker.JmlVisitor#visitJmlRefinePrefix(org.jmlspecs.checker.JmlRefinePrefix)
	 */
	@Override
	public void visitJmlRefinePrefix(JmlRefinePrefix arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.jmlspecs.checker.JmlVisitor#visitJmlRefiningStatement(org.jmlspecs.checker.JmlRefiningStatement)
	 */
	@Override
	public void visitJmlRefiningStatement(JmlRefiningStatement arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.jmlspecs.checker.JmlVisitor#visitJmlRelationalExpression(org.jmlspecs.checker.JmlRelationalExpression)
	 */
	@Override
	public void visitJmlRelationalExpression(JmlRelationalExpression arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.jmlspecs.checker.JmlVisitor#visitJmlRepresentsDecl(org.jmlspecs.checker.JmlRepresentsDecl)
	 */
	@Override
	public void visitJmlRepresentsDecl(JmlRepresentsDecl arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.jmlspecs.checker.JmlVisitor#visitJmlRequiresClause(org.jmlspecs.checker.JmlRequiresClause)
	 */
	@Override
	public void visitJmlRequiresClause(JmlRequiresClause arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.jmlspecs.checker.JmlVisitor#visitJmlResultExpression(org.jmlspecs.checker.JmlResultExpression)
	 */
	@Override
	public void visitJmlResultExpression(JmlResultExpression arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.jmlspecs.checker.JmlVisitor#visitJmlReturnsClause(org.jmlspecs.checker.JmlReturnsClause)
	 */
	@Override
	public void visitJmlReturnsClause(JmlReturnsClause arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.jmlspecs.checker.JmlVisitor#visitJmlSetComprehension(org.jmlspecs.checker.JmlSetComprehension)
	 */
	@Override
	public void visitJmlSetComprehension(JmlSetComprehension arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.jmlspecs.checker.JmlVisitor#visitJmlSetStatement(org.jmlspecs.checker.JmlSetStatement)
	 */
	@Override
	public void visitJmlSetStatement(JmlSetStatement arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.jmlspecs.checker.JmlVisitor#visitJmlSignalsClause(org.jmlspecs.checker.JmlSignalsClause)
	 */
	@Override
	public void visitJmlSignalsClause(JmlSignalsClause arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.jmlspecs.checker.JmlVisitor#visitJmlSignalsOnlyClause(org.jmlspecs.checker.JmlSignalsOnlyClause)
	 */
	@Override
	public void visitJmlSignalsOnlyClause(JmlSignalsOnlyClause arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.jmlspecs.checker.JmlVisitor#visitJmlSpaceExpression(org.jmlspecs.checker.JmlSpaceExpression)
	 */
	@Override
	public void visitJmlSpaceExpression(JmlSpaceExpression arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.jmlspecs.checker.JmlVisitor#visitJmlSpecBody(org.jmlspecs.checker.JmlSpecBody)
	 */
	@Override
	public void visitJmlSpecBody(JmlSpecBody arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.jmlspecs.checker.JmlVisitor#visitJmlSpecExpression(org.jmlspecs.checker.JmlSpecExpression)
	 */
	@Override
	public void visitJmlSpecExpression(JmlSpecExpression arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.jmlspecs.checker.JmlVisitor#visitJmlSpecQuantifiedExpression(org.jmlspecs.checker.JmlSpecQuantifiedExpression)
	 */
	@Override
	public void visitJmlSpecQuantifiedExpression(JmlSpecQuantifiedExpression arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.jmlspecs.checker.JmlVisitor#visitJmlSpecStatement(org.jmlspecs.checker.JmlSpecStatement)
	 */
	@Override
	public void visitJmlSpecStatement(JmlSpecStatement arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.jmlspecs.checker.JmlVisitor#visitJmlSpecVarDecl(org.jmlspecs.checker.JmlSpecVarDecl)
	 */
	@Override
	public void visitJmlSpecVarDecl(JmlSpecVarDecl arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.jmlspecs.checker.JmlVisitor#visitJmlSpecification(org.jmlspecs.checker.JmlSpecification)
	 */
	@Override
	public void visitJmlSpecification(JmlSpecification arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.jmlspecs.checker.JmlVisitor#visitJmlStoreRef(org.jmlspecs.checker.JmlStoreRef)
	 */
	@Override
	public void visitJmlStoreRef(JmlStoreRef arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.jmlspecs.checker.JmlVisitor#visitJmlStoreRefExpression(org.jmlspecs.checker.JmlStoreRefExpression)
	 */
	@Override
	public void visitJmlStoreRefExpression(JmlStoreRefExpression arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.jmlspecs.checker.JmlVisitor#visitJmlStoreRefKeyword(org.jmlspecs.checker.JmlStoreRefKeyword)
	 */
	@Override
	public void visitJmlStoreRefKeyword(JmlStoreRefKeyword arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.jmlspecs.checker.JmlVisitor#visitJmlTypeExpression(org.jmlspecs.checker.JmlTypeExpression)
	 */
	@Override
	public void visitJmlTypeExpression(JmlTypeExpression arg0) {
		throw new TacoNotImplementedYetException();
	}

	/* (non-Javadoc)
	 * @see org.jmlspecs.checker.JmlVisitor#visitJmlTypeOfExpression(org.jmlspecs.checker.JmlTypeOfExpression)
	 */
	@Override
	public void visitJmlTypeOfExpression(JmlTypeOfExpression arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.jmlspecs.checker.JmlVisitor#visitJmlUnreachableStatement(org.jmlspecs.checker.JmlUnreachableStatement)
	 */
	@Override
	public void visitJmlUnreachableStatement(JmlUnreachableStatement arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.jmlspecs.checker.JmlVisitor#visitJmlVariableDefinition(org.jmlspecs.checker.JmlVariableDefinition)
	 */
	@Override
	public void visitJmlVariableDefinition(JmlVariableDefinition arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.jmlspecs.checker.JmlVisitor#visitJmlVariantFunction(org.jmlspecs.checker.JmlVariantFunction)
	 */
	@Override
	public void visitJmlVariantFunction(JmlVariantFunction arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.jmlspecs.checker.JmlVisitor#visitJmlWhenClause(org.jmlspecs.checker.JmlWhenClause)
	 */
	@Override
	public void visitJmlWhenClause(JmlWhenClause arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.jmlspecs.checker.JmlVisitor#visitJmlWorkingSpaceClause(org.jmlspecs.checker.JmlWorkingSpaceClause)
	 */
	@Override
	public void visitJmlWorkingSpaceClause(JmlWorkingSpaceClause arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.jmlspecs.checker.JmlVisitor#visitJmlWorkingSpaceExpression(org.jmlspecs.checker.JmlWorkingSpaceExpression)
	 */
	@Override
	public void visitJmlWorkingSpaceExpression(JmlWorkingSpaceExpression arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.jmlspecs.checker.JmlVisitor#visitJmlWritableIfVarAssertion(org.jmlspecs.checker.JmlWritableIfVarAssertion)
	 */
	@Override
	public void visitJmlWritableIfVarAssertion(JmlWritableIfVarAssertion arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.multijava.mjc.MjcVisitor#visitAddExpression(org.multijava.mjc.JAddExpression)
	 */
	@Override
	public void visitAddExpression(JAddExpression arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.multijava.mjc.MjcVisitor#visitArrayAccessExpression(org.multijava.mjc.JArrayAccessExpression)
	 */
	@Override
	public void visitArrayAccessExpression(JArrayAccessExpression arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.multijava.mjc.MjcVisitor#visitArrayDimsAndInit(org.multijava.mjc.JArrayDimsAndInits)
	 */
	@Override
	public void visitArrayDimsAndInit(JArrayDimsAndInits arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.multijava.mjc.MjcVisitor#visitArrayInitializer(org.multijava.mjc.JArrayInitializer)
	 */
	@Override
	public void visitArrayInitializer(JArrayInitializer arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.multijava.mjc.MjcVisitor#visitArrayLengthExpression(org.multijava.mjc.JArrayLengthExpression)
	 */
	@Override
	public void visitArrayLengthExpression(JArrayLengthExpression arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.multijava.mjc.MjcVisitor#visitAssertStatement(org.multijava.mjc.JAssertStatement)
	 */
	@Override
	public void visitAssertStatement(JAssertStatement arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.multijava.mjc.MjcVisitor#visitAssignmentExpression(org.multijava.mjc.JAssignmentExpression)
	 */
	@Override
	public void visitAssignmentExpression(JAssignmentExpression arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.multijava.mjc.MjcVisitor#visitBitwiseExpression(org.multijava.mjc.JBitwiseExpression)
	 */
	@Override
	public void visitBitwiseExpression(JBitwiseExpression n) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.multijava.mjc.MjcVisitor#visitBlockStatement(org.multijava.mjc.JBlock)
	 */
	@Override
	public void visitBlockStatement(JBlock arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.multijava.mjc.MjcVisitor#visitBooleanLiteral(org.multijava.mjc.JBooleanLiteral)
	 */
	@Override
	public void visitBooleanLiteral(JBooleanLiteral arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.multijava.mjc.MjcVisitor#visitBreakStatement(org.multijava.mjc.JBreakStatement)
	 */
	@Override
	public void visitBreakStatement(JBreakStatement arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.multijava.mjc.MjcVisitor#visitCastExpression(org.multijava.mjc.JCastExpression)
	 */
	@Override
	public void visitCastExpression(JCastExpression arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.multijava.mjc.MjcVisitor#visitCatchClause(org.multijava.mjc.JCatchClause)
	 */
	@Override
	public void visitCatchClause(JCatchClause arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.multijava.mjc.MjcVisitor#visitCharLiteral(org.multijava.mjc.JCharLiteral)
	 */
	@Override
	public void visitCharLiteral(JCharLiteral arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.multijava.mjc.MjcVisitor#visitClassBlock(org.multijava.mjc.JClassBlock)
	 */
	@Override
	public void visitClassBlock(JClassBlock arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.multijava.mjc.MjcVisitor#visitClassDeclaration(org.multijava.mjc.JClassDeclaration)
	 */
	@Override
	public void visitClassDeclaration(JClassDeclaration arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.multijava.mjc.MjcVisitor#visitClassExpression(org.multijava.mjc.JClassExpression)
	 */
	@Override
	public void visitClassExpression(JClassExpression arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.multijava.mjc.MjcVisitor#visitClassOrGFImport(org.multijava.mjc.JClassOrGFImport)
	 */
	@Override
	public void visitClassOrGFImport(JClassOrGFImport arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.multijava.mjc.MjcVisitor#visitCompilationUnit(org.multijava.mjc.JCompilationUnit)
	 */
	@Override
	public void visitCompilationUnit(JCompilationUnit arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.multijava.mjc.MjcVisitor#visitCompoundAssignmentExpression(org.multijava.mjc.JCompoundAssignmentExpression)
	 */
	@Override
	public void visitCompoundAssignmentExpression(JCompoundAssignmentExpression arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.multijava.mjc.MjcVisitor#visitCompoundStatement(org.multijava.mjc.JCompoundStatement)
	 */
	@Override
	public void visitCompoundStatement(JCompoundStatement arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.multijava.mjc.MjcVisitor#visitConditionalAndExpression(org.multijava.mjc.JConditionalAndExpression)
	 */
	@Override
	public void visitConditionalAndExpression(JConditionalAndExpression arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.multijava.mjc.MjcVisitor#visitConditionalExpression(org.multijava.mjc.JConditionalExpression)
	 */
	@Override
	public void visitConditionalExpression(JConditionalExpression arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.multijava.mjc.MjcVisitor#visitConditionalOrExpression(org.multijava.mjc.JConditionalOrExpression)
	 */
	@Override
	public void visitConditionalOrExpression(JConditionalOrExpression arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.multijava.mjc.MjcVisitor#visitConstructorBlock(org.multijava.mjc.JConstructorBlock)
	 */
	@Override
	public void visitConstructorBlock(JConstructorBlock arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.multijava.mjc.MjcVisitor#visitConstructorDeclaration(org.multijava.mjc.JConstructorDeclaration)
	 */
	@Override
	public void visitConstructorDeclaration(JConstructorDeclaration arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.multijava.mjc.MjcVisitor#visitContinueStatement(org.multijava.mjc.JContinueStatement)
	 */
	@Override
	public void visitContinueStatement(JContinueStatement arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.multijava.mjc.MjcVisitor#visitDivideExpression(org.multijava.mjc.JDivideExpression)
	 */
	@Override
	public void visitDivideExpression(JDivideExpression arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.multijava.mjc.MjcVisitor#visitDoStatement(org.multijava.mjc.JDoStatement)
	 */
	@Override
	public void visitDoStatement(JDoStatement arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.multijava.mjc.MjcVisitor#visitEmptyStatement(org.multijava.mjc.JEmptyStatement)
	 */
	@Override
	public void visitEmptyStatement(JEmptyStatement arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.multijava.mjc.MjcVisitor#visitEqualityExpression(org.multijava.mjc.JEqualityExpression)
	 */
	@Override
	public void visitEqualityExpression(JEqualityExpression arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.multijava.mjc.MjcVisitor#visitExplicitConstructorInvocation(org.multijava.mjc.JExplicitConstructorInvocation)
	 */
	@Override
	public void visitExplicitConstructorInvocation(JExplicitConstructorInvocation arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.multijava.mjc.MjcVisitor#visitExpressionListStatement(org.multijava.mjc.JExpressionListStatement)
	 */
	@Override
	public void visitExpressionListStatement(JExpressionListStatement arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.multijava.mjc.MjcVisitor#visitExpressionStatement(org.multijava.mjc.JExpressionStatement)
	 */
	@Override
	public void visitExpressionStatement(JExpressionStatement arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.multijava.mjc.MjcVisitor#visitFieldDeclaration(org.multijava.mjc.JFieldDeclaration)
	 */
	@Override
	public void visitFieldDeclaration(JFieldDeclaration arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.multijava.mjc.MjcVisitor#visitFieldExpression(org.multijava.mjc.JClassFieldExpression)
	 */
	@Override
	public void visitFieldExpression(JClassFieldExpression arg0) {
		throw new TacoNotImplementedYetException();

	}
	
	
	

	/* (non-Javadoc)
	 * @see org.multijava.mjc.MjcVisitor#visitForStatement(org.multijava.mjc.JForStatement)
	 */
	@Override
	public void visitForStatement(JForStatement arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.multijava.mjc.MjcVisitor#visitFormalParameters(org.multijava.mjc.JFormalParameter)
	 */
	@Override
	public void visitFormalParameters(JFormalParameter arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.multijava.mjc.MjcVisitor#visitGenericFunctionDecl(org.multijava.mjc.MJGenericFunctionDecl)
	 */
	@Override
	public void visitGenericFunctionDecl(MJGenericFunctionDecl arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.multijava.mjc.MjcVisitor#visitIfStatement(org.multijava.mjc.JIfStatement)
	 */
	@Override
	public void visitIfStatement(JIfStatement arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.multijava.mjc.MjcVisitor#visitInitializerDeclaration(org.multijava.mjc.JInitializerDeclaration)
	 */
	@Override
	public void visitInitializerDeclaration(JInitializerDeclaration arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.multijava.mjc.MjcVisitor#visitInstanceofExpression(org.multijava.mjc.JInstanceofExpression)
	 */
	@Override
	public void visitInstanceofExpression(JInstanceofExpression arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.multijava.mjc.MjcVisitor#visitInterfaceDeclaration(org.multijava.mjc.JInterfaceDeclaration)
	 */
	@Override
	public void visitInterfaceDeclaration(JInterfaceDeclaration arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.multijava.mjc.MjcVisitor#visitLabeledStatement(org.multijava.mjc.JLabeledStatement)
	 */
	@Override
	public void visitLabeledStatement(JLabeledStatement arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.multijava.mjc.MjcVisitor#visitLocalVariableExpression(org.multijava.mjc.JLocalVariableExpression)
	 */
	@Override
	public void visitLocalVariableExpression(JLocalVariableExpression arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.multijava.mjc.MjcVisitor#visitMathModeExpression(org.multijava.mjc.MJMathModeExpression)
	 */
	@Override
	public void visitMathModeExpression(MJMathModeExpression arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.multijava.mjc.MjcVisitor#visitMethodCallExpression(org.multijava.mjc.JMethodCallExpression)
	 */
	@Override
	public void visitMethodCallExpression(JMethodCallExpression arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.multijava.mjc.MjcVisitor#visitMethodDeclaration(org.multijava.mjc.JMethodDeclaration)
	 */
	@Override
	public void visitMethodDeclaration(JMethodDeclaration arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.multijava.mjc.MjcVisitor#visitMinusExpression(org.multijava.mjc.JMinusExpression)
	 */
	@Override
	public void visitMinusExpression(JMinusExpression arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.multijava.mjc.MjcVisitor#visitModuloExpression(org.multijava.mjc.JModuloExpression)
	 */
	@Override
	public void visitModuloExpression(JModuloExpression arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.multijava.mjc.MjcVisitor#visitMultExpression(org.multijava.mjc.JMultExpression)
	 */
	@Override
	public void visitMultExpression(JMultExpression arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.multijava.mjc.MjcVisitor#visitNameExpression(org.multijava.mjc.JNameExpression)
	 */
	@Override
	public void visitNameExpression(JNameExpression arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.multijava.mjc.MjcVisitor#visitNewAnonymousClassExpression(org.multijava.mjc.JNewAnonymousClassExpression)
	 */
	@Override
	public void visitNewAnonymousClassExpression(JNewAnonymousClassExpression arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.multijava.mjc.MjcVisitor#visitNewArrayExpression(org.multijava.mjc.JNewArrayExpression)
	 */
	@Override
	public void visitNewArrayExpression(JNewArrayExpression arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.multijava.mjc.MjcVisitor#visitNewObjectExpression(org.multijava.mjc.JNewObjectExpression)
	 */
	@Override
	public void visitNewObjectExpression(JNewObjectExpression arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.multijava.mjc.MjcVisitor#visitNullLiteral(org.multijava.mjc.JNullLiteral)
	 */
	@Override
	public void visitNullLiteral(JNullLiteral arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.multijava.mjc.MjcVisitor#visitOrdinalLiteral(org.multijava.mjc.JOrdinalLiteral)
	 */
	@Override
	public void visitOrdinalLiteral(JOrdinalLiteral arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.multijava.mjc.MjcVisitor#visitPackageImport(org.multijava.mjc.JPackageImport)
	 */
	@Override
	public void visitPackageImport(JPackageImport arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.multijava.mjc.MjcVisitor#visitPackageName(org.multijava.mjc.JPackageName)
	 */
	@Override
	public void visitPackageName(JPackageName arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.multijava.mjc.MjcVisitor#visitParenthesedExpression(org.multijava.mjc.JParenthesedExpression)
	 */
	@Override
	public void visitParenthesedExpression(JParenthesedExpression arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.multijava.mjc.MjcVisitor#visitPostfixExpression(org.multijava.mjc.JPostfixExpression)
	 */
	@Override
	public void visitPostfixExpression(JPostfixExpression arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.multijava.mjc.MjcVisitor#visitPrefixExpression(org.multijava.mjc.JPrefixExpression)
	 */
	@Override
	public void visitPrefixExpression(JPrefixExpression arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.multijava.mjc.MjcVisitor#visitRealLiteral(org.multijava.mjc.JRealLiteral)
	 */
	@Override
	public void visitRealLiteral(JRealLiteral arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.multijava.mjc.MjcVisitor#visitRelationalExpression(org.multijava.mjc.JRelationalExpression)
	 */
	@Override
	public void visitRelationalExpression(JRelationalExpression arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.multijava.mjc.MjcVisitor#visitReturnStatement(org.multijava.mjc.JReturnStatement)
	 */
	@Override
	public void visitReturnStatement(JReturnStatement arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.multijava.mjc.MjcVisitor#visitShiftExpression(org.multijava.mjc.JShiftExpression)
	 */
	@Override
	public void visitShiftExpression(JShiftExpression arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.multijava.mjc.MjcVisitor#visitStringLiteral(org.multijava.mjc.JStringLiteral)
	 */
	@Override
	public void visitStringLiteral(JStringLiteral arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.multijava.mjc.MjcVisitor#visitSuperExpression(org.multijava.mjc.JSuperExpression)
	 */
	@Override
	public void visitSuperExpression(JSuperExpression arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.multijava.mjc.MjcVisitor#visitSwitchGroup(org.multijava.mjc.JSwitchGroup)
	 */
	@Override
	public void visitSwitchGroup(JSwitchGroup arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.multijava.mjc.MjcVisitor#visitSwitchLabel(org.multijava.mjc.JSwitchLabel)
	 */
	@Override
	public void visitSwitchLabel(JSwitchLabel arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.multijava.mjc.MjcVisitor#visitSwitchStatement(org.multijava.mjc.JSwitchStatement)
	 */
	@Override
	public void visitSwitchStatement(JSwitchStatement arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.multijava.mjc.MjcVisitor#visitSynchronizedStatement(org.multijava.mjc.JSynchronizedStatement)
	 */
	@Override
	public void visitSynchronizedStatement(JSynchronizedStatement arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.multijava.mjc.MjcVisitor#visitThisExpression(org.multijava.mjc.JThisExpression)
	 */
	@Override
	public void visitThisExpression(JThisExpression arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.multijava.mjc.MjcVisitor#visitThrowStatement(org.multijava.mjc.JThrowStatement)
	 */
	@Override
	public void visitThrowStatement(JThrowStatement arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.multijava.mjc.MjcVisitor#visitTopLevelMethodDeclaration(org.multijava.mjc.MJTopLevelMethodDeclaration)
	 */
	@Override
	public void visitTopLevelMethodDeclaration(MJTopLevelMethodDeclaration arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.multijava.mjc.MjcVisitor#visitTryCatchStatement(org.multijava.mjc.JTryCatchStatement)
	 */
	@Override
	public void visitTryCatchStatement(JTryCatchStatement arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.multijava.mjc.MjcVisitor#visitTryFinallyStatement(org.multijava.mjc.JTryFinallyStatement)
	 */
	@Override
	public void visitTryFinallyStatement(JTryFinallyStatement arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.multijava.mjc.MjcVisitor#visitTypeDeclarationStatement(org.multijava.mjc.JTypeDeclarationStatement)
	 */
	@Override
	public void visitTypeDeclarationStatement(JTypeDeclarationStatement arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.multijava.mjc.MjcVisitor#visitTypeNameExpression(org.multijava.mjc.JTypeNameExpression)
	 */
	@Override
	public void visitTypeNameExpression(JTypeNameExpression arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.multijava.mjc.MjcVisitor#visitUnaryExpression(org.multijava.mjc.JUnaryExpression)
	 */
	@Override
	public void visitUnaryExpression(JUnaryExpression arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.multijava.mjc.MjcVisitor#visitUnaryPromoteExpression(org.multijava.mjc.JUnaryPromote)
	 */
	@Override
	public void visitUnaryPromoteExpression(JUnaryPromote arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.multijava.mjc.MjcVisitor#visitVariableDeclarationStatement(org.multijava.mjc.JVariableDeclarationStatement)
	 */
	@Override
	public void visitVariableDeclarationStatement(JVariableDeclarationStatement arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.multijava.mjc.MjcVisitor#visitVariableDefinition(org.multijava.mjc.JVariableDefinition)
	 */
	@Override
	public void visitVariableDefinition(JVariableDefinition arg0) {
		throw new TacoNotImplementedYetException();

	}

	/* (non-Javadoc)
	 * @see org.multijava.mjc.MjcVisitor#visitWarnExpression(org.multijava.mjc.MJWarnExpression)
	 */
	@Override
	public void visitWarnExpression(MJWarnExpression arg0) {
		throw new TacoNotImplementedYetException();

	}
	

	public List<JStatement> getNewStatements() {
		return newStatements;
	}


	/* (non-Javadoc)
	 * @see org.multijava.mjc.MjcVisitor#visitWhileStatement(org.multijava.mjc.JWhileStatement)
	 */
	@Override
	public void visitWhileStatement(JWhileStatement arg0) {
		throw new TacoNotImplementedYetException();

	}

}
