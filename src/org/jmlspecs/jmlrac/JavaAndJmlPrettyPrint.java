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
package org.jmlspecs.jmlrac;

import org.apache.commons.lang.StringUtils;
import org.jmlspecs.checker.*;
import org.multijava.mjc.JAddExpression;
import org.multijava.mjc.JArrayAccessExpression;
import org.multijava.mjc.JArrayDimsAndInits;
import org.multijava.mjc.JArrayInitializer;
import org.multijava.mjc.JArrayLengthExpression;
import org.multijava.mjc.JAssertStatement;
import org.multijava.mjc.JAssignmentExpression;
import org.multijava.mjc.JBinaryExpression;
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

import ar.edu.taco.TacoNotImplementedYetException;
import ar.edu.taco.simplejml.helpers.JavaOperatorSolver;

/**
 * @author elgaby
 * 
 */
@Deprecated
public class JavaAndJmlPrettyPrint extends JmlAbstractVisitor {
	private static final String TAB_CHAR = "\t";
	
	private String prettyPrint;
	private int indentationAmount;

	public String getPrettyPrint() {
		return prettyPrint;
	}

	public void visitJmlClassDeclaration(/* @non_null */JmlClassDeclaration self) {
		throw new TacoNotImplementedYetException(self.getClass().getName());
	}

	// **** Jml Method Declaration

	public void visitJmlMethodDeclaration(/* @non_null */JmlMethodDeclaration self) {
		StringBuffer outputString = new StringBuffer();

		if (self.isPublic()) {
			outputString.append("public ");
		} else if (self.isPrivate()) {
			outputString.append("private ");
		}

		if (self.isStatic()) {
			outputString.append("static ");
		}

		if (self.isAbstract()) {
			outputString.append("abstract ");
		}

		outputString.append(self.returnType().toVerboseString() + " ");

		outputString.append(self.ident() + "(");
		for (int x = 0; x < self.parameters().length; x++) {
			self.parameters()[x].accept(this);
			outputString.append(this.prettyPrint);
			if (x < self.parameters().length - 1) {
				outputString.append(", ");
			}

		}
		outputString.append(") {");

		this.indentationAmount++;
		self.body().accept(this);
		outputString.append(StringUtils.repeat(prettyPrint, indentationAmount));
		outputString.append(this.prettyPrint);
		this.indentationAmount--;
		
		outputString.append("}");

		this.prettyPrint = outputString.toString();
	}

	// **** END - Jml Method Declaration
	public void visitJmlAbruptSpecBody(/* @non_null */JmlAbruptSpecBody self) {
		throw new TacoNotImplementedYetException(self.getClass().getName());
	}

	public void visitJmlAbruptSpecCase(/* @non_null */JmlAbruptSpecCase self) {
		throw new TacoNotImplementedYetException(self.getClass().getName());
	}

	public void visitJmlAccessibleClause(/* @non_null */JmlAccessibleClause self) {
		throw new TacoNotImplementedYetException(self.getClass().getName());
	}

	public void visitJmlAssertStatement(/* @non_null */JmlAssertStatement self) {
		StringBuffer outputString = new StringBuffer();
		outputString.append("//@ assert (");
		
		self.predicate().accept(this);
		outputString.append(StringUtils.repeat(TAB_CHAR, indentationAmount));
		outputString.append(this.prettyPrint);
		
		outputString.append(")");
		
		this.prettyPrint = outputString.toString();
	}

	public void visitJmlAssignableClause(/* @non_null */JmlAssignableClause self) {
		throw new TacoNotImplementedYetException(self.getClass().getName());
	}

	public void visitJmlAssumeStatement(/* @non_null */JmlAssumeStatement self) {
		throw new TacoNotImplementedYetException(self.getClass().getName());
	}

	public void visitJmlAxiom(/* @non_null */JmlAxiom self) {
		throw new TacoNotImplementedYetException(self.getClass().getName());
	}

	public void visitJmlBehaviorSpec(/* @non_null */JmlBehaviorSpec self) {
		throw new TacoNotImplementedYetException(self.getClass().getName());
	}

	public void visitJmlBreaksClause(/* @non_null */JmlBreaksClause self) {
		throw new TacoNotImplementedYetException(self.getClass().getName());
	}

	public void visitJmlCallableClause(/* @non_null */JmlCallableClause self) {
		throw new TacoNotImplementedYetException(self.getClass().getName());
	}

	public void visitJmlCapturesClause(/* @non_null */JmlCapturesClause self) {
		throw new TacoNotImplementedYetException(self.getClass().getName());
	}

	public void visitJmlClassBlock(/* @non_null */JmlClassBlock self) {
		throw new TacoNotImplementedYetException(self.getClass().getName());
	}

	public void visitJmlClassOrGFImport(/* @non_null */JmlClassOrGFImport self) {
		throw new TacoNotImplementedYetException(self.getClass().getName());
	}

	public void visitJmlCompilationUnit(/* @non_null */JmlCompilationUnit self) {
		throw new TacoNotImplementedYetException(self.getClass().getName());
	}

	public void visitJmlCodeContract(/* @non_null */JmlCodeContract self) {
		throw new TacoNotImplementedYetException(self.getClass().getName());
	}

	public void visitJmlConstraint(/* @non_null */JmlConstraint self) {
		throw new TacoNotImplementedYetException(self.getClass().getName());
	}

	public void visitJmlConstructorDeclaration(/* @non_null */ JmlConstructorDeclaration self) {
		throw new TacoNotImplementedYetException(self.getClass().getName());
	}

	public void visitJmlConstructorName(/* @non_null */JmlConstructorName self) {
		throw new TacoNotImplementedYetException(self.getClass().getName());
	}

	public void visitJmlContinuesClause(/* @non_null */JmlContinuesClause self) {
		throw new TacoNotImplementedYetException(self.getClass().getName());
	}

	public void visitJmlDebugStatement(/* @non_null */JmlDebugStatement self) {
		throw new TacoNotImplementedYetException(self.getClass().getName());
	}

	public void visitJmlDivergesClause(/* @non_null */JmlDivergesClause self) {
		throw new TacoNotImplementedYetException(self.getClass().getName());
	}

	public void visitJmlDurationClause(/* @non_null */JmlDurationClause self) {
		throw new TacoNotImplementedYetException(self.getClass().getName());
	}

	public void visitJmlDurationExpression(/* @non_null */JmlDurationExpression self) {
		throw new TacoNotImplementedYetException(self.getClass().getName());
	}

	public void visitJmlElemTypeExpression(/* @non_null */JmlElemTypeExpression self) {
		throw new TacoNotImplementedYetException(self.getClass().getName());
	}

	public void visitJmlEnsuresClause(/* @non_null */JmlEnsuresClause self) {
		throw new TacoNotImplementedYetException(self.getClass().getName());
	}

	public void visitJmlExample(/* @non_null */JmlExample self) {
		throw new TacoNotImplementedYetException(self.getClass().getName());
	}

	public void visitJmlExceptionalBehaviorSpec(/* @non_null */ JmlExceptionalBehaviorSpec self) {
		throw new TacoNotImplementedYetException(self.getClass().getName());
	}

	public void visitJmlExceptionalExample(/* @non_null */JmlExceptionalExample self) {
		throw new TacoNotImplementedYetException(self.getClass().getName());
	}

	public void visitJmlExceptionalSpecBody(/* @non_null */JmlExceptionalSpecBody self) {
		throw new TacoNotImplementedYetException(self.getClass().getName());
	}

	public void visitJmlExceptionalSpecCase(/* @non_null */JmlExceptionalSpecCase self) {
		throw new TacoNotImplementedYetException(self.getClass().getName());
	}

	public void visitJmlExtendingSpecification(/* @non_null */ JmlExtendingSpecification self) {
		throw new TacoNotImplementedYetException(self.getClass().getName());
	}

	public void visitJmlFieldDeclaration(/* @non_null */JmlFieldDeclaration self) {
		throw new TacoNotImplementedYetException(self.getClass().getName());
	}

	public void visitJmlForAllVarDecl(/* @non_null */JmlForAllVarDecl self) {
		throw new TacoNotImplementedYetException(self.getClass().getName());
	}

	public void visitJmlFormalParameter(/* @non_null */JmlFormalParameter self) {
		StringBuffer outputString = new StringBuffer();
		outputString.append(self.getType().toVerboseString() + " ");
		outputString.append(self.ident());

		this.prettyPrint = outputString.toString();
	}

	public void visitJmlFreshExpression(/* @non_null */JmlFreshExpression self) {
		throw new TacoNotImplementedYetException(self.getClass().getName());
	}

	public void visitJmlGenericSpecBody(/* @non_null */JmlGenericSpecBody self) {
		throw new TacoNotImplementedYetException(self.getClass().getName());
	}

	public void visitJmlGenericSpecCase(/* @non_null */JmlGenericSpecCase self) {
		throw new TacoNotImplementedYetException(self.getClass().getName());
	}

	public void visitJmlGuardedStatement(/* @non_null */JmlGuardedStatement self) {
		throw new TacoNotImplementedYetException(self.getClass().getName());
	}

	public void visitJmlHenceByStatement(/* @non_null */JmlHenceByStatement self) {
		throw new TacoNotImplementedYetException(self.getClass().getName());
	}

	public void visitJmlInGroupClause(/* @non_null */JmlInGroupClause self) {
		throw new TacoNotImplementedYetException(self.getClass().getName());
	}

	public void visitJmlInformalExpression(/* @non_null */JmlInformalExpression self) {
		throw new TacoNotImplementedYetException(self.getClass().getName());
	}

	public void visitJmlInformalStoreRef(/* @non_null */JmlInformalStoreRef self) {
		throw new TacoNotImplementedYetException(self.getClass().getName());
	}

	public void visitJmlInitiallyVarAssertion(/* @non_null */ JmlInitiallyVarAssertion self) {
		throw new TacoNotImplementedYetException(self.getClass().getName());
	}

	public void visitJmlInterfaceDeclaration(/* @non_null */JmlInterfaceDeclaration self) {
		throw new TacoNotImplementedYetException(self.getClass().getName());
	}

	public void visitJmlInvariant(/* @non_null */JmlInvariant self) {
		throw new TacoNotImplementedYetException(self.getClass().getName());
	}

	public void visitJmlInvariantForExpression(/* @non_null */ JmlInvariantForExpression self) {
		throw new TacoNotImplementedYetException(self.getClass().getName());
	}

	public void visitJmlInvariantStatement(/* @non_null */JmlInvariantStatement self) {
		throw new TacoNotImplementedYetException(self.getClass().getName());
	}

	public void visitJmlIsInitializedExpression(/* @non_null */ JmlIsInitializedExpression self) {
		throw new TacoNotImplementedYetException(self.getClass().getName());
	}

	public void visitJmlLabelExpression(/* @non_null */JmlLabelExpression self) {
		throw new TacoNotImplementedYetException(self.getClass().getName());
	}

	public void visitJmlLetVarDecl(/* @non_null */JmlLetVarDecl self) {
		throw new TacoNotImplementedYetException(self.getClass().getName());
	}

	public void visitJmlLockSetExpression(/* @non_null */JmlLockSetExpression self) {
		throw new TacoNotImplementedYetException(self.getClass().getName());
	}

	public void visitJmlLoopInvariant(/* @non_null */JmlLoopInvariant self) {
		throw new TacoNotImplementedYetException(self.getClass().getName());
	}

	public void visitJmlLoopStatement(/* @non_null */JmlLoopStatement self) {
		self.loopStmt().accept(this);
	}

	public void visitJmlMapsIntoClause(/* @non_null */JmlMapsIntoClause self) {
		throw new TacoNotImplementedYetException(self.getClass().getName());
	}

	public void visitJmlMaxExpression(/* @non_null */JmlMaxExpression self) {
		throw new TacoNotImplementedYetException(self.getClass().getName());
	}

	public void visitJmlMeasuredClause(/* @non_null */JmlMeasuredClause self) {
		throw new TacoNotImplementedYetException(self.getClass().getName());
	}

	public void visitJmlMethodName(/* @non_null */JmlMethodName self) {
		throw new TacoNotImplementedYetException(self.getClass().getName());
	}

	public void visitJmlMethodNameList(/* @non_null */JmlMethodNameList self) {
		throw new TacoNotImplementedYetException(self.getClass().getName());
	}

	public void visitJmlModelProgram(/* @non_null */JmlModelProgram self) {
		throw new TacoNotImplementedYetException(self.getClass().getName());
	}

	public void visitJmlMonitorsForVarAssertion(/* @non_null */ JmlMonitorsForVarAssertion self) {
		throw new TacoNotImplementedYetException(self.getClass().getName());
	}

	public void visitJmlName(/* @non_null */JmlName self) {
		throw new TacoNotImplementedYetException(self.getClass().getName());
	}

	public void visitJmlNonNullElementsExpression(/* @non_null */ JmlNonNullElementsExpression self) {
		throw new TacoNotImplementedYetException(self.getClass().getName());
	}

	public void visitJmlAssignmentStatement(/* @non_null */JmlAssignmentStatement jmlAssignmentStatement) {
		StringBuffer outputString = new StringBuffer();

		JAssignmentExpression assignmentExpression = (JAssignmentExpression) jmlAssignmentStatement.assignmentStatement().getExpression();
		assignmentExpression.left().accept(this);

		outputString.append(this.getPrettyPrint());
		outputString.append(" = ");

		assignmentExpression.right().accept(this);
		outputString.append(StringUtils.repeat(TAB_CHAR, indentationAmount));
		outputString.append(this.getPrettyPrint());

		this.prettyPrint = outputString.toString();
	}

	public void visitJmlNondetChoiceStatement(/* @non_null */ JmlNondetChoiceStatement self) {
		throw new TacoNotImplementedYetException(self.getClass().getName());
	}

	public void visitJmlNondetIfStatement(/* @non_null */JmlNondetIfStatement self) {
		throw new TacoNotImplementedYetException(self.getClass().getName());
	}

	public void visitJmlNormalBehaviorSpec(/* @non_null */JmlNormalBehaviorSpec self) {
		throw new TacoNotImplementedYetException(self.getClass().getName());
	}

	public void visitJmlNormalExample(/* @non_null */JmlNormalExample self) {
		throw new TacoNotImplementedYetException(self.getClass().getName());
	}

	public void visitJmlNormalSpecBody(/* @non_null */JmlNormalSpecBody self) {
		throw new TacoNotImplementedYetException(self.getClass().getName());
	}

	public void visitJmlNormalSpecCase(/* @non_null */JmlNormalSpecCase self) {
		throw new TacoNotImplementedYetException(self.getClass().getName());
	}

	public void visitJmlNotAssignedExpression(/* @non_null */ JmlNotAssignedExpression self) {
		throw new TacoNotImplementedYetException(self.getClass().getName());
	}

	public void visitJmlNotModifiedExpression(/* @non_null */ JmlNotModifiedExpression self) {
		throw new TacoNotImplementedYetException(self.getClass().getName());
	}

	public void visitJmlOnlyAccessedExpression(/* @non_null */ JmlOnlyAccessedExpression self) {
		throw new TacoNotImplementedYetException(self.getClass().getName());
	}

	public void visitJmlOnlyAssignedExpression(/* @non_null */ JmlOnlyAssignedExpression self) {
		throw new TacoNotImplementedYetException(self.getClass().getName());
	}

	public void visitJmlOnlyCalledExpression(/* @non_null */ JmlOnlyCalledExpression self) {
		throw new TacoNotImplementedYetException(self.getClass().getName());
	}

	public void visitJmlOnlyCapturedExpression(/* @non_null */ JmlOnlyCapturedExpression self) {
		throw new TacoNotImplementedYetException(self.getClass().getName());
	}

	public void visitJmlOldExpression(/* @non_null */JmlOldExpression self) {
		throw new TacoNotImplementedYetException(self.getClass().getName());
	}

	public void visitJmlPackageImport(/* @non_null */JmlPackageImport self) {
		throw new TacoNotImplementedYetException(self.getClass().getName());
	}

	public void visitJmlPredicate(/* @non_null */JmlPredicate self) {
	}

	public void visitJmlPredicateKeyword(/* @non_null */JmlPredicateKeyword self) {
	}

	public void visitJmlPreExpression(/* @non_null */JmlPreExpression self) {
		throw new TacoNotImplementedYetException(self.getClass().getName());
	}

	public void visitJmlReachExpression(/* @non_null */JmlReachExpression self) {
		throw new TacoNotImplementedYetException(self.getClass().getName());
	}

	public void visitJmlReadableIfVarAssertion(/* @non_null */ JmlReadableIfVarAssertion self) {
		throw new TacoNotImplementedYetException(self.getClass().getName());
	}

	public void visitJmlWritableIfVarAssertion(/* @non_null */ JmlWritableIfVarAssertion self) {
		throw new TacoNotImplementedYetException(self.getClass().getName());
	}

	public void visitJmlRedundantSpec(/* @non_null */JmlRedundantSpec self) {
		throw new TacoNotImplementedYetException(self.getClass().getName());
	}

	public void visitJmlRefinePrefix(/* @non_null */JmlRefinePrefix self) {
		throw new TacoNotImplementedYetException(self.getClass().getName());
	}

	public void visitJmlRelationalExpression(/* @non_null */JmlRelationalExpression self) {
		StringBuffer outputString = new StringBuffer();

		self.left().accept(this);
		outputString.append(this.prettyPrint);

		outputString.append(" " + self.opString() + " ");

		self.right().accept(this);
		outputString.append(this.prettyPrint);

		this.prettyPrint = outputString.toString();

	}

	public void visitJmlRepresentsDecl(/* @non_null */JmlRepresentsDecl self) {
		throw new TacoNotImplementedYetException(self.getClass().getName());
	}

	public void visitJmlRequiresClause(/* @non_null */JmlRequiresClause self) {
		throw new TacoNotImplementedYetException(self.getClass().getName());
	}

	public void visitJmlResultExpression(/* @non_null */JmlResultExpression self) {
		throw new TacoNotImplementedYetException(self.getClass().getName());
	}

	public void visitJmlReturnsClause(/* @non_null */JmlReturnsClause self) {
		throw new TacoNotImplementedYetException(self.getClass().getName());
	}

	public void visitJmlSetComprehension(/* @non_null */JmlSetComprehension self) {
		throw new TacoNotImplementedYetException(self.getClass().getName());
	}

	public void visitJmlSetStatement(/* @non_null */JmlSetStatement self) {
		throw new TacoNotImplementedYetException(self.getClass().getName());
	}

	public void visitJmlRefiningStatement(/* @non_null */JmlRefiningStatement self) {
		throw new TacoNotImplementedYetException(self.getClass().getName());
	}

	public void visitJmlSignalsOnlyClause(/* @non_null */JmlSignalsOnlyClause self) {
		throw new TacoNotImplementedYetException(self.getClass().getName());
	}

	public void visitJmlSignalsClause(/* @non_null */JmlSignalsClause self) {
		throw new TacoNotImplementedYetException(self.getClass().getName());
	}

	public void visitJmlSpaceExpression(/* @non_null */JmlSpaceExpression self) {
		throw new TacoNotImplementedYetException(self.getClass().getName());
	}

	public void visitJmlSpecExpression(/* @non_null */JmlSpecExpression self) {
		throw new TacoNotImplementedYetException(self.getClass().getName());
	}

	public void visitJmlSpecQuantifiedExpression(/* @non_null */ JmlSpecQuantifiedExpression self) {
		throw new TacoNotImplementedYetException(self.getClass().getName());
	}

	public void visitJmlSpecStatement(/* @non_null */JmlSpecStatement self) {
		throw new TacoNotImplementedYetException(self.getClass().getName());
	}

	public void visitJmlSpecification(/* @non_null */JmlSpecification self) {
		throw new TacoNotImplementedYetException(self.getClass().getName());
	}

	public void visitJmlStoreRefExpression(/* @non_null */JmlStoreRefExpression self) {
		throw new TacoNotImplementedYetException(self.getClass().getName());
	}

	public void visitJmlStoreRefKeyword(/* @non_null */JmlStoreRefKeyword self) {
		throw new TacoNotImplementedYetException(self.getClass().getName());
	}

	public void visitJmlTypeExpression(/* @non_null */JmlTypeExpression self) {
		throw new TacoNotImplementedYetException(self.getClass().getName());
	}

	public void visitJmlTypeOfExpression(/* @non_null */JmlTypeOfExpression self) {
		throw new TacoNotImplementedYetException(self.getClass().getName());
	}

	public void visitJmlUnreachableStatement(/* @non_null */JmlUnreachableStatement self) {
		throw new TacoNotImplementedYetException(self.getClass().getName());
	}

	public void visitJmlVariantFunction(/* @non_null */JmlVariantFunction self) {
		throw new TacoNotImplementedYetException(self.getClass().getName());
	}

	public void visitJmlVariableDefinition(/* @non_null */JmlVariableDefinition self) {
		throw new TacoNotImplementedYetException(self.getClass().getName());
	}

	public void visitJmlWhenClause(/* @non_null */JmlWhenClause self) {
		throw new TacoNotImplementedYetException(self.getClass().getName());
	}

	public void visitJmlWorkingSpaceClause(/* @non_null */JmlWorkingSpaceClause self) {
		throw new TacoNotImplementedYetException(self.getClass().getName());
	}

	public void visitJmlWorkingSpaceExpression(/* @non_null */JmlWorkingSpaceExpression self) {
		throw new TacoNotImplementedYetException(self.getClass().getName());
	}

	// ----------------------------------------------------------------------
	// NOT NEEDED, BUT DEFINED IN JmlVisitor, PERHAPS, SHOULD BE CLEANED OUT!
	// ----------------------------------------------------------------------
	public void visitJmlDeclaration(/* @non_null */JmlDeclaration self) {
		throw new TacoNotImplementedYetException(self.getClass().getName());
	}

	public void visitJmlExpression(/* @non_null */JmlExpression self) {
		throw new TacoNotImplementedYetException(self.getClass().getName());
	}

	public void visitJmlGeneralSpecCase(/* @non_null */JmlGeneralSpecCase self) {
		throw new TacoNotImplementedYetException(self.getClass().getName());
	}

	public void visitJmlMethodSpecification(/* @non_null */JmlMethodSpecification self) {
		throw new TacoNotImplementedYetException(self.getClass().getName());
	}

	public void visitJmlNode(/* @non_null */JmlNode self) {
		throw new TacoNotImplementedYetException(self.getClass().getName());
	}

	public void visitJmlSpecBody(/* @non_null */JmlSpecBody self) {
		throw new TacoNotImplementedYetException(self.getClass().getName());
	}

	public void visitJmlSpecVarDecl(/* @non_null */JmlSpecVarDecl self) {
		throw new TacoNotImplementedYetException(self.getClass().getName());
	}

	public void visitJmlStoreRef(/* @non_null */JmlStoreRef self) {
		throw new TacoNotImplementedYetException(self.getClass().getName());
	}

	// ----------------------------------------------------------------------
	// MJC VISITORS
	// ----------------------------------------------------------------------

	/** Visits the given compilation unit. */
	public void visitCompilationUnit(/* @non_null */JCompilationUnit self) {
		throw new TacoNotImplementedYetException(self.getClass().getName());
	}

	/** Visits the given class declaration. */
	public void visitClassDeclaration(/* @non_null */JClassDeclaration self) {
		throw new TacoNotImplementedYetException(self.getClass().getName());
	}

	/** Visits the given interface declaration. */
	public void visitInterfaceDeclaration(/* @non_null */JInterfaceDeclaration self) {
		throw new TacoNotImplementedYetException(self.getClass().getName());
	}

	/** Visits the given generic function declaration. */
	public void visitGenericFunctionDecl(/* @non_null */MJGenericFunctionDecl self) {
		throw new TacoNotImplementedYetException(self.getClass().getName());
	}

	/** Visits the given field declaration. */
	public void visitFieldDeclaration(/* @non_null */JFieldDeclaration self) {
		throw new TacoNotImplementedYetException(self.getClass().getName());
	}

	/** Visits the given method declaration. */
	public void visitMethodDeclaration(/* @non_null */JMethodDeclaration self) {
		throw new TacoNotImplementedYetException(self.getClass().getName());
	}

	/** Visits the given initializer declaration. */
	public void visitInitializerDeclaration(/* @non_null */JInitializerDeclaration self) {
	}

	/** Visits the given external method declaration. */
	public void visitTopLevelMethodDeclaration(/* @non_null */MJTopLevelMethodDeclaration self) {
		throw new TacoNotImplementedYetException(self.getClass().getName());
	}

	/** Visits the given constructor declaration. */
	public void visitConstructorDeclaration(/* @non_null */JConstructorDeclaration self) {
		throw new TacoNotImplementedYetException(self.getClass().getName());
	}

	// ----------------------------------------------------------------------
	// STATEMENT
	// ----------------------------------------------------------------------

	/** Visits the given while statement. */
	public void visitWhileStatement(/* @non_null */JWhileStatement self) {
		StringBuffer outputString = new StringBuffer();
		outputString.append("while (");

		self.cond().accept(this);
		outputString.append(this.prettyPrint);

		outputString.append(") {");
		outputString.append("\n");
		
		this.indentationAmount++;
		self.body().accept(this);
		outputString.append(StringUtils.repeat(TAB_CHAR, indentationAmount));
		outputString.append(this.prettyPrint);
		this.indentationAmount--;

		outputString.append("}");

		this.prettyPrint = outputString.toString();
	}

	/** Visits the given variable declaration statement. */
	public void visitVariableDeclarationStatement(/* @non_null */JVariableDeclarationStatement self) {
		StringBuffer outputString = new StringBuffer();
		for (int x = 0; x < self.getVars().length; x++) {
			self.getVars()[x].accept(this);
			outputString.append(this.prettyPrint + ";\n");
		}

		this.prettyPrint = outputString.toString();
	}

	/** Visits the given variable declaration statement. */
	public void visitVariableDefinition(/* @non_null */JVariableDefinition self) {
		StringBuffer outputString = new StringBuffer();
		outputString.append(self.getType().toVerboseString() + " ");
		outputString.append(self.ident());

	}

	/** Visits the given try-catch statement. */
	public void visitTryCatchStatement(/* @non_null */JTryCatchStatement self) {
		this.indentationAmount++;
		StringBuffer outputString = new StringBuffer();
		
		outputString.append("try { \n");
		
		self.tryClause().accept(this);
		outputString.append(StringUtils.repeat(TAB_CHAR, indentationAmount));
		outputString.append(this.prettyPrint);
		
		outputString.append("}");
		
		if (self.catchClauses() != null) {
			for (JCatchClause aCatchClause : self.catchClauses()) {
				outputString.append("catch (");
				
				outputString.append(aCatchClause.exception().toString());
				
				outputString.append(") { \n");
				
				aCatchClause.body().accept(this);
				outputString.append(StringUtils.repeat(TAB_CHAR, indentationAmount));
				outputString.append(this.prettyPrint);
				
				outputString.append("}");
			}
		}
		this.indentationAmount--;
		
		this.prettyPrint = outputString.toString();
	}

	/** Visits the given try-finally statement. */
	public void visitTryFinallyStatement(/* @non_null */JTryFinallyStatement self) {
		StringBuffer outputString = new StringBuffer();
		
		self.tryClause().accept(this);
		outputString.append(this.prettyPrint);
		
		if (self.finallyClause() != null) {
			outputString.append("finally { \n");
			
			this.indentationAmount++;
			self.finallyClause().accept(this);
			outputString.append(StringUtils.repeat(TAB_CHAR, indentationAmount));
			outputString.append(this.prettyPrint);
			this.indentationAmount--;
			
			outputString.append("}");
		}
		
		this.prettyPrint = outputString.toString();
	}

	/** Visits the given throw statement. */
	public void visitThrowStatement(/* @non_null */JThrowStatement self) {
		throw new TacoNotImplementedYetException(self.getClass().getName());
	}

	/** Visits the given synchronized statement. */
	public void visitSynchronizedStatement(/* @non_null */JSynchronizedStatement self) {
		throw new TacoNotImplementedYetException(self.getClass().getName());
	}

	/** Visits the given switch statement. */
	public void visitSwitchStatement(/* @non_null */JSwitchStatement self) {
		throw new TacoNotImplementedYetException(self.getClass().getName());
	}

	/** Visits the given assert statement. */
	public void visitAssertStatement(/* @non_null */JAssertStatement self) {
		throw new TacoNotImplementedYetException(self.getClass().getName());
	}

	/** Visits the given return statement. */
	public void visitReturnStatement(/* @non_null */JReturnStatement jReturnStatement) {
		StringBuffer outputString = new StringBuffer();

		jReturnStatement.expr().accept(this);

		outputString.append("return ");
		outputString.append(StringUtils.repeat(TAB_CHAR, indentationAmount));
		outputString.append(this.getPrettyPrint());

		this.prettyPrint = outputString.toString();
	}

	/** Visits the given labeled statement. */
	public void visitLabeledStatement(/* @non_null */JLabeledStatement self) {
		throw new TacoNotImplementedYetException(self.getClass().getName());
	}

	/** Visits the given if statement. */
	public void visitIfStatement(/* @non_null */JIfStatement self) {

		StringBuffer outputString = new StringBuffer();
		outputString.append("if (");
		
		self.cond().accept(this);
		outputString.append(this.prettyPrint);
		
		outputString.append(") { \n");
		
		this.indentationAmount++;
		self.thenClause().accept(this);
		outputString.append(this.prettyPrint);
		this.indentationAmount--;
		
		if (self.elseClause() != null) {
			outputString.append("} else { \n");

			this.indentationAmount++;
			self.elseClause().accept(this);
			outputString.append(this.prettyPrint);
			this.indentationAmount--;
		}
		
		outputString.append("}");
		
		this.prettyPrint = outputString.toString();
	}

	/** Visits the given for statement. */
	public void visitForStatement(/* @non_null */JForStatement self) {
		throw new TacoNotImplementedYetException(self.getClass().getName());
	}

	/** Visits the given compound statement. */
	public void visitCompoundStatement(/* @non_null */JCompoundStatement self) {
		throw new TacoNotImplementedYetException(self.getClass().getName());
	}

	/** Visits the given compound statement. */
	public void visitCompoundStatement(/* @non_null */org.multijava.mjc.JStatement[] body) {
		throw new TacoNotImplementedYetException(body.getClass().getName());
	}

	/** Visits the given expression statement. */
	public void visitExpressionStatement(/* @non_null */JExpressionStatement self) {
		StringBuffer outputString = new StringBuffer();

		self.expr().accept(this);
		outputString.append(this.prettyPrint);

		this.prettyPrint = outputString.toString();
	}

	/** Visits the given expression list statement. */
	public void visitExpressionListStatement(/* @non_null */JExpressionListStatement self) {
		throw new TacoNotImplementedYetException(self.getClass().getName());
	}

	/** Visits the given empty statement. */
	public void visitEmptyStatement(/* @non_null */JEmptyStatement self) {
		throw new TacoNotImplementedYetException(self.getClass().getName());
	}

	/** Visits the given do statement. */
	public void visitDoStatement(/* @non_null */JDoStatement self) {
		throw new TacoNotImplementedYetException(self.getClass().getName());
	}

	/** Visits the given continue statement. */
	public void visitContinueStatement(/* @non_null */JContinueStatement self) {
		throw new TacoNotImplementedYetException(self.getClass().getName());
	}

	/** Visits the given break statement. */
	public void visitBreakStatement(/* @non_null */JBreakStatement self) {
		throw new TacoNotImplementedYetException(self.getClass().getName());
	}

	/** Visits the given block statement. */
	public void visitBlockStatement(/* @non_null */JBlock self) {
		StringBuffer outputString = new StringBuffer();
		for (int x = 0; x < self.body().length; x++) {
			if (self.body()[x] != null) {
				self.body()[x].accept(this);
				outputString.append(StringUtils.repeat(TAB_CHAR, indentationAmount));
				outputString.append(this.prettyPrint + "\n");
			} else {
				outputString.append("**NULL**");
			}
		}
		this.prettyPrint = outputString.toString();
	}

	/** Visits the given constructor block. */
	public void visitConstructorBlock(/* @non_null */JConstructorBlock self) {
		throw new TacoNotImplementedYetException(self.getClass().getName());
	}

	/** Visits the given class block. */
	public void visitClassBlock(/* @non_null */JClassBlock self) {
		throw new TacoNotImplementedYetException(self.getClass().getName());
	}

	/** Visits the given type declaration statement. */
	public void visitTypeDeclarationStatement(/* @non_null */JTypeDeclarationStatement self) {
		throw new TacoNotImplementedYetException(self.getClass().getName());
	}

	// ----------------------------------------------------------------------
	// EXPRESSION
	// ----------------------------------------------------------------------

	/** Visits the given unary expression. */
	public void visitUnaryExpression(/* @non_null */JUnaryExpression self) {
		throw new TacoNotImplementedYetException(self.getClass().getName());
	}

	/** Visits the given type name expression. */
	public void visitTypeNameExpression(/* @non_null */JTypeNameExpression self) {
		this.prettyPrint = self.qualifiedName();
	}

	/** Visits the given this expression. */
	public void visitThisExpression(/* @non_null */JThisExpression self) {
		throw new TacoNotImplementedYetException(self.getClass().getName());
	}

	/** Visits the given super expression. */
	public void visitSuperExpression(/* @non_null */JSuperExpression self) {
		throw new TacoNotImplementedYetException(self.getClass().getName());
	}

	/** Visits the given shift expression. */
	public void visitShiftExpression(/* @non_null */JShiftExpression self) {
		throw new TacoNotImplementedYetException(self.getClass().getName());
	}

	/** Visits the given relational expression. */
	public void visitRelationalExpression(/* @non_null */JRelationalExpression self) {
		throw new TacoNotImplementedYetException(self.getClass().getName());
	}

	/** Visits the given prefix expression. */
	public void visitPrefixExpression(/* @non_null */JPrefixExpression self) {
		throw new TacoNotImplementedYetException(self.getClass().getName());
	}

	/** Visits the given postfix expression. */
	public void visitPostfixExpression(/* @non_null */JPostfixExpression self) {
		StringBuffer outputString = new StringBuffer();

		self.expr().accept(this);
		outputString.append(StringUtils.repeat(TAB_CHAR, indentationAmount));
		outputString.append(this.prettyPrint);
		outputString.append(JavaOperatorSolver.getOperatorString(self.oper()));

		this.prettyPrint = outputString.toString();
	}

	/** Visits the given parenthesed expression. */
	public void visitParenthesedExpression(/* @non_null */JParenthesedExpression self) {
		throw new TacoNotImplementedYetException(self.getClass().getName());
	}

	/** Visits the given object allocator expression. */
	public void visitNewObjectExpression(/* @non_null */JNewObjectExpression self) {
		StringBuffer outputString = new StringBuffer();
		
		outputString.append(StringUtils.repeat(TAB_CHAR, indentationAmount));
		outputString.append("new ");
		outputString.append(self.constructor().getJavaName());
		outputString.append("(");
		
		for (int x = 0; x < self.params().length; x++) {
			self.params()[x].accept(this);
			outputString.append(this.prettyPrint);
			if (x < self.params().length - 1) {
				outputString.append(", ");
			}	
		}
		
		outputString.append(")");
		
		this.prettyPrint = outputString.toString();
	}

	/** Visits the given object allocator expression for an anonymous class. */
	public void visitNewAnonymousClassExpression(/* @non_null */JNewAnonymousClassExpression self) {
		throw new TacoNotImplementedYetException(self.getClass().getName());
	}

	/** Visits the given array allocator expression. */
	public void visitNewArrayExpression(/* @non_null */JNewArrayExpression self) {
		StringBuffer outputString = new StringBuffer();
		outputString.append(StringUtils.repeat(TAB_CHAR, indentationAmount));
		outputString.append(self.toString());
		
		this.prettyPrint = outputString.toString();
	}

	/** Visits the given name expression. */
	public void visitNameExpression(/* @non_null */JNameExpression self) {
		throw new TacoNotImplementedYetException(self.getClass().getName());
	}

	/** Visits the given binary expression with the given operator. */
	protected void visitBinaryExpression(/* @non_null */JBinaryExpression self, String oper) {
		throw new TacoNotImplementedYetException(self.getClass().getName());
	}

	/** Visits the given add expression. */
	public void visitAddExpression(/* @non_null */JAddExpression jAddExpression) {
		StringBuffer outputString = new StringBuffer();

		jAddExpression.left().accept(this);
		outputString.append(StringUtils.repeat(TAB_CHAR, indentationAmount));
		outputString.append(this.getPrettyPrint());
		outputString.append(" + ");

		jAddExpression.right().accept(this);
		outputString.append(this.getPrettyPrint());

		this.prettyPrint = outputString.toString();
	}

	/** Visits the given boolean AND expression. */
	public void visitConditionalAndExpression(/* @non_null */JConditionalAndExpression self) {
		StringBuffer outputString = new StringBuffer();
		outputString.append("(");
		self.left().accept(this);
		outputString.append(this.prettyPrint);
		outputString.append(" && ");
		self.right().accept(this);
		outputString.append(this.prettyPrint);
		outputString.append(")");
			}

	/** Visits the given boolean OR expression. */
	public void visitConditionalOrExpression(/* @non_null */JConditionalOrExpression self) {
		throw new TacoNotImplementedYetException(self.getClass().getName());
	}

	/** Visits the given divide expression. */
	public void visitDivideExpression(/* @non_null */JDivideExpression self) {
		throw new TacoNotImplementedYetException(self.getClass().getName());
	}

	/** Visits the given minus expression. */
	public void visitMinusExpression(/* @non_null */JMinusExpression self) {
		throw new TacoNotImplementedYetException(self.getClass().getName());
	}

	/** Visits the given modulo division expression. */
	public void visitModuloExpression(/* @non_null */JModuloExpression self) {
		throw new TacoNotImplementedYetException(self.getClass().getName());
	}

	/** Visits the given multiplication expression. */
	public void visitMultExpression(/* @non_null */JMultExpression self) {
		throw new TacoNotImplementedYetException(self.getClass().getName());
	}

	/** Visits the given method call expression. */
	public void visitMethodCallExpression(/* @non_null */JMethodCallExpression self) {
		StringBuffer outputString = new StringBuffer();
		
		outputString.append(StringUtils.repeat(TAB_CHAR, indentationAmount));
		if (self.prefix() instanceof JSuperExpression) {
			outputString.append("super");
		} else {
			outputString.append(self.prefix().toString());
		}
		outputString.append(".");
		outputString.append(self.method().getIdent());
		outputString.append("(");
		
		for (int x = 0; x < self.method().parametersSize(); x++) {
			self.method().parameters()[x].dynamicType();
		}
			
		outputString.append(")");
		
		this.prettyPrint = outputString.toString();
	}

	/** Visits the given local variable expression. */
	public void visitLocalVariableExpression(/* @non_null */JLocalVariableExpression jLocalVariableExpression) {
		StringBuffer outputString = new StringBuffer();

		outputString.append(jLocalVariableExpression.ident());

		this.prettyPrint = outputString.toString();
	}

	/** Visits the given instanceof expression. */
	public void visitInstanceofExpression(/* @non_null */JInstanceofExpression self) {
		StringBuffer outputString = new StringBuffer();
		
		self.expr().accept(this);
		outputString.append(this.prettyPrint);
		outputString.append(" instanceof ");
		outputString.append(self.dest().toVerboseString());
		
		this.prettyPrint = outputString.toString();
	}

	/** Visits the given equality expression. */
	public void visitEqualityExpression(/* @non_null */JEqualityExpression self) {
		StringBuffer outputString = new StringBuffer();
		outputString.append("(");
		self.left().accept(this);
		outputString.append(this.prettyPrint);
		outputString.append(" == ");
		self.right().accept(this);
		outputString.append(this.prettyPrint);
		outputString.append(")");
		
		
		this.prettyPrint = outputString.toString();
	}

	/** Visits the given conditional expression. */
	public void visitConditionalExpression(/* @non_null */JConditionalExpression self) {
		throw new TacoNotImplementedYetException(self.getClass().getName());
	}

	/** Visits the given compound expression. */
	public void visitCompoundAssignmentExpression(/* @non_null */JCompoundAssignmentExpression self) {
		StringBuffer outputString = new StringBuffer();
		
		self.left().accept(this);
		outputString.append(StringUtils.repeat(TAB_CHAR, indentationAmount));
		outputString.append(this.prettyPrint);
		outputString.append(" " + JavaOperatorSolver.getOperatorString(self.oper()) + "= ");
		
		self.right().accept(this);
		outputString.append(this.prettyPrint);
		
		this.prettyPrint = outputString.toString();
	}

	/** Visits the given field expression. */
	public void visitFieldExpression(/* @non_null */JClassFieldExpression self) {
		throw new TacoNotImplementedYetException(self.getClass().getName());
	}

	/** Visits the given class expression. */
	public void visitClassExpression(/* @non_null */JClassExpression self) {
		throw new TacoNotImplementedYetException(self.getClass().getName());
	}

	/** Visits the given cast expression. */
	public void visitCastExpression(/* @non_null */JCastExpression self) {
		throw new TacoNotImplementedYetException(self.getClass().getName());
	}

	/** Visits the given unary promotion expression. */
	public void visitUnaryPromoteExpression(/* @non_null */JUnaryPromote self) {
		throw new TacoNotImplementedYetException(self.getClass().getName());
	}

	/** Visits the given bitwise expression. */
	public void visitBitwiseExpression(/* @non_null */JBitwiseExpression self) {
		throw new TacoNotImplementedYetException(self.getClass().getName());
	}

	/** Visits the given assignment expression. */
	public void visitAssignmentExpression(/* @non_null */JAssignmentExpression self) {
		StringBuffer outputString = new StringBuffer();
		
		self.left().accept(this);
		outputString.append(StringUtils.repeat(TAB_CHAR, indentationAmount));
		outputString.append(this.prettyPrint);
		
		outputString.append(" = ");
		
		self.right().accept(this);
		outputString.append(this.prettyPrint);
		
		this.prettyPrint = outputString.toString();		
	}

	/** Visits the given array length expression. */
	public void visitArrayLengthExpression(/* @non_null */JArrayLengthExpression self) {
		throw new TacoNotImplementedYetException(self.getClass().getName());
	}

	/** Visits the given array access expression. */
	public void visitArrayAccessExpression(/* @non_null */JArrayAccessExpression self) {
		StringBuffer outputString = new StringBuffer();
		
		outputString.append(StringUtils.repeat(TAB_CHAR, indentationAmount));
		// TODO: Ver como obtener el nombre de la variable de manera mas prolija.
		outputString.append(self.prefix().toString().substring(self.prefix().toString().indexOf('\'')+1, self.prefix().toString().lastIndexOf('\'')));
		outputString.append("[");
		outputString.append(self.accessor().toString());
		outputString.append("]");
		
		this.prettyPrint = outputString.toString();		
	}

	/** Visits the given warn expression. */
	public void visitWarnExpression(/* @non_null */MJWarnExpression self) {
		throw new TacoNotImplementedYetException(self.getClass().getName());
	}

	/** Visits the given math mode expression. */
	public void visitMathModeExpression(/* @non_null */MJMathModeExpression self) {
		throw new TacoNotImplementedYetException(self.getClass().getName());
	}

	// ----------------------------------------------------------------------
	// UTILS
	// ----------------------------------------------------------------------

	/* Visits the given switch label. */
	public void visitSwitchLabel(/* @non_null */JSwitchLabel self) {
		throw new TacoNotImplementedYetException(self.getClass().getName());
	}

	/** Visits the given switch group. */
	public void visitSwitchGroup(/* @non_null */JSwitchGroup self) {
		throw new TacoNotImplementedYetException(self.getClass().getName());
	}

	/** Visits the given catch clause. */
	public void visitCatchClause(/* @non_null */JCatchClause self) {
		throw new TacoNotImplementedYetException(self.getClass().getName());
	}

	/** Visits the given boolean literal. */
	public void visitBooleanLiteral(/* @non_null */JBooleanLiteral self) {
		if (self.booleanValue()) {
			this.prettyPrint = "true";
		} else {
			this.prettyPrint = "false";
		}
	}

	/** Visits the given character literal. */
	public void visitCharLiteral(/* @non_null */JCharLiteral self) {
		throw new TacoNotImplementedYetException(self.getClass().getName());
	}

	/** Visits the given ordinal literal. */
	public void visitOrdinalLiteral(/* @non_null */JOrdinalLiteral self) {
		this.prettyPrint = self.toString();
	}

	/** Visits the given byte literal. */
	protected void visitByteLiteral(byte value) {
		this.prettyPrint = String.valueOf(value);
	}

	/** Visits the given int literal. */
	protected void visitIntLiteral(int value) {
		this.prettyPrint = String.valueOf(value);
	}

	/** Visits the given long literal. */
	protected void visitLongLiteral(long value) {
		this.prettyPrint = String.valueOf(value);
	}

	/** Visits the given short literal. */
	protected void visitShortLiteral(short value) {
		this.prettyPrint = String.valueOf(value);
	}

	/** Visits the given real literal. */
	public void visitRealLiteral(/* @non_null */JRealLiteral self) {
		throw new TacoNotImplementedYetException(self.getClass().getName());
	}

	/** Visits the given double literal. */
	protected void visitDoubleLiteral(double value) {
		this.prettyPrint = String.valueOf(value);
	}

	/** Visits the given float literal. */
	protected void visitFloatLiteral(float value) {
		this.prettyPrint = String.valueOf(value);
	}

	/** Visits the given string literal. */
	public void visitStringLiteral(/* @non_null */JStringLiteral self) {
		this.prettyPrint = self.toString();
	}

	/** Visits the given null literal. */
	public void visitNullLiteral(/* @non_null */JNullLiteral self) {
		throw new TacoNotImplementedYetException(self.getClass().getName());
	}

	/** Visits the given package name statement. */
	public void visitPackageName(/* @non_null */JPackageName self) {
		throw new TacoNotImplementedYetException(self.getClass().getName());
	}

	/** Visits the given package import statement. */
	public void visitPackageImport(/* @non_null */JPackageImport self) {
		throw new TacoNotImplementedYetException(self.getClass().getName());
	}

	/** Visits the given class import statement. */
	public void visitClassOrGFImport(/* @non_null */JClassOrGFImport self) {
		throw new TacoNotImplementedYetException(self.getClass().getName());
	}

	/** Visits the given formal parameters. */
	public void visitFormalParameters(/* @non_null */JFormalParameter self) {
		throw new TacoNotImplementedYetException(self.getClass().getName());
	}

	/** Visits the given explicit constructor invocation. */
	public void visitExplicitConstructorInvocation(/* @non_null */JExplicitConstructorInvocation self) {
		throw new TacoNotImplementedYetException(self.getClass().getName());
	}

	/** Visits the given array initializer. */
	public void visitArrayInitializer(/* @non_null */JArrayInitializer self) {
		throw new TacoNotImplementedYetException(self.getClass().getName());
	}

	/** Visits the given array dimension and initialization expression. */
	public void visitArrayDimsAndInit(/* @non_null */JArrayDimsAndInits self) {
		throw new TacoNotImplementedYetException(self.getClass().getName());
	}

}
