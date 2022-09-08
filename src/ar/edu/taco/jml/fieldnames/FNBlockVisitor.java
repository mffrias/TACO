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

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.jmlspecs.checker.JmlAssertStatement;
import org.jmlspecs.checker.JmlAssignableClause;
import org.jmlspecs.checker.JmlAssignmentStatement;
import org.jmlspecs.checker.JmlAssumeStatement;
import org.jmlspecs.checker.JmlClassDeclaration;
import org.jmlspecs.checker.JmlFieldDeclaration;
import org.jmlspecs.checker.JmlLoopStatement;
import org.jmlspecs.checker.JmlPredicate;
import org.jmlspecs.checker.JmlRepresentsDecl;
import org.jmlspecs.checker.JmlSpecExpression;
import org.jmlspecs.checker.JmlStoreRef;
import org.jmlspecs.checker.JmlStoreRefExpression;
import org.jmlspecs.checker.JmlVariableDefinition;
import org.jmlspecs.checker.JmlVariantFunction;
import org.jmlspecs.jmlrac.JavaAndJmlPrettyPrint2;
import org.multijava.mjc.CFieldAccessor;
import org.multijava.mjc.CType;
import org.multijava.mjc.JAssertStatement;
import org.multijava.mjc.JBlock;
import org.multijava.mjc.JClassFieldExpression;
import org.multijava.mjc.JExpression;
import org.multijava.mjc.JExpressionStatement;
import org.multijava.mjc.JIfStatement;
import org.multijava.mjc.JLocalVariableExpression;
import org.multijava.mjc.JMethodDeclaration;
import org.multijava.mjc.JReturnStatement;
import org.multijava.mjc.JStatement;
import org.multijava.mjc.JThrowStatement;
import org.multijava.mjc.JVariableDeclarationStatement;
import org.multijava.mjc.JVariableDefinition;
import org.multijava.mjc.JWhileStatement;
import org.multijava.mjc.JmlFieldDeclarationExtension;

import ar.edu.taco.jml.JmlToSimpleJmlContext;
import ar.edu.taco.jml.utils.ASTUtils;
import ar.edu.taco.jml.utils.SpecSimplifierClassBaseVisitor;

public class FNBlockVisitor extends SpecSimplifierClassBaseVisitor {

	private static Logger log = Logger.getLogger(FNBlockVisitor.class);
	
	private String currentClassName = null;
	
	private final JmlToSimpleJmlContext jmlToSimpleJmlContext;
	
	public FNBlockVisitor(JmlToSimpleJmlContext jmlToSimpleJmlContext) {
	    this.jmlToSimpleJmlContext = jmlToSimpleJmlContext;
	}
	
	public FNBlockVisitor(JmlToSimpleJmlContext jmlToSimpleJmlContext,
	        String currentClassName) {
	    this(jmlToSimpleJmlContext);
	    this.currentClassName = currentClassName;
	}
	
	@Override
	public void visitMethodDeclaration(JMethodDeclaration arg0) {
	    super.visitMethodDeclaration(arg0);
	}
	
	@Override
	public void visitJmlClassDeclaration(JmlClassDeclaration self) {
	    currentClassName = FieldRenameUtil
	            .extractClassNameForFieldRenameSupport(self.getCClass());
	    super.visitJmlClassDeclaration(self);
	}
	
	@Override
	public void visitJmlFieldDeclaration(JmlFieldDeclaration self) {
	    String className = FieldRenameUtil
	            .extractClassNameForFieldRenameSupport(self.getField());
	    String new_name;
	    if (self.getField().isStatic()) {
	        new_name = self.ident();
	    } else {
	        new_name = FieldRenameUtil.renamedName(className, self.ident());
	    }
	    JmlFieldDeclarationExtension newSelf = new JmlFieldDeclarationExtension(
	            self, new_name);
	    this.getStack().push(newSelf);
	    String old_name = self.ident();
	    this.jmlToSimpleJmlContext.register_jml_to_simplejml_rename(old_name, new_name);
	}
	
	@Override
	public void visitBlockStatement(JBlock self) {
	    List<JStatement> declarationList = new ArrayList<JStatement>();
	    List<JStatement> statementList = new ArrayList<JStatement>();
	    for (int i = 0; i < self.body().length; i++) {
	        JStatement statement = self.body()[i];
	        {
	            FNBlockVisitor visitor = new FNBlockVisitor(
	                    this.jmlToSimpleJmlContext, this.currentClassName);
	            statement.accept(visitor);
	            JStatement aStatement = (JStatement) visitor.getStack().pop();
	            // If the statement is a Local variable declaration, we are
	            // going to skip it.
	            if (!(aStatement instanceof JExpressionStatement)
	                    || !(((JExpressionStatement) aStatement).expr() instanceof JLocalVariableExpression)) {
	                statementList.add(aStatement);
	            }
	        }
	    }
	    JStatement[] statements = new JStatement[declarationList.size()
	                                             + statementList.size()];
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
	    JBlock newSelf = new JBlock(self.getTokenReference(), statements, self
	            .getComments());
	    this.getStack().push(newSelf);
	    JavaAndJmlPrettyPrint2 prettyPrinter = new JavaAndJmlPrettyPrint2();
	    newSelf.accept(prettyPrinter);
	    log.debug(prettyPrinter.getPrettyPrint());
	}
	
	// BEGIN - ESStatementVisitor
	@Override
	public void visitIfStatement(/* @non_null */JIfStatement self) {
	    self.thenClause().accept(this);
	    JStatement newThen = (JStatement) this.getStack().pop();
	    JStatement newElse = null;
	    if (self.elseClause() != null) {
	        self.elseClause().accept(this);
	        newElse = (JStatement) this.getStack().pop();
	    }
	    FNExpressionVisitor conditionSimplifierVisitor = new FNExpressionVisitor(
	            currentClassName);
	    self.cond().accept(conditionSimplifierVisitor);
	    JExpression condition = conditionSimplifierVisitor.getArrayStack()
	            .pop();
	    JIfStatement newIfStatement = ASTUtils.createIfStatement(condition,
	            newThen, newElse, self.getComments());
	    this.getStack().push(newIfStatement);
	}
	
	@Override
	public void visitJmlLoopStatement(JmlLoopStatement self) {
	    self.stmt().accept(this);
	    JStatement newCycle = (JStatement) this.getStack().pop();
	    JmlVariantFunction[] varFuns = self.variantFunctions();
	    JmlVariantFunction[] newVarFuns = new JmlVariantFunction[self.variantFunctions().length];
	    for (int idx = 0; idx < varFuns.length; idx++){
	        varFuns[idx].accept(this);
	        newVarFuns[idx] = (JmlVariantFunction)this.getStack().pop();
	    }
	    JmlLoopStatement newStmt = new JmlLoopStatement(self.getTokenReference(), self.loopInvariants(), newVarFuns, newCycle, self.getComments());
	    this.getStack().push(newStmt);
	}
	
	@Override
	public void visitWhileStatement(JWhileStatement self) {
	    self.body().accept(this);
	    JStatement newBody = (JStatement) this.getStack().pop();
	    FNExpressionVisitor conditionSimplifierVisitor = new FNExpressionVisitor(
	            currentClassName);
	    self.cond().accept(conditionSimplifierVisitor);
	    JExpression condition = conditionSimplifierVisitor.getArrayStack()
	            .pop();
	    JWhileStatement newJWhileStatement = new JWhileStatement(self
	            .getTokenReference(), condition, newBody, self.getComments());
	    this.getStack().push(newJWhileStatement);
	}
	
	@Override
	public void visitAssertStatement(JAssertStatement self){
	    FNExpressionVisitor exprSimplifierVisitor = new FNExpressionVisitor(
	            currentClassName);
	    JExpression expr = null;
	    self.predicate().accept(exprSimplifierVisitor);
	    expr = exprSimplifierVisitor.getArrayStack().pop();
	    JAssertStatement newSelf = new JAssertStatement(self
	            .getTokenReference(), expr, self.getComments());
	    this.getStack().push(newSelf);
	}
	
	@Override
	public void visitVariableDeclarationStatement(
	        JVariableDeclarationStatement self) {
	    JVariableDefinition[] newVars = new JVariableDefinition[self.getVars().length];
	    for (int i = 0; i < self.getVars().length; i++) {
	        JVariableDefinition variableDefinition = self.getVars()[i];
	        variableDefinition.accept(this);
	        newVars[i] = (JVariableDefinition) getStack().pop();
	    }
	    JVariableDeclarationStatement newSelf = new JVariableDeclarationStatement(
	            self.getTokenReference(), newVars, self.getComments());
	    this.getStack().push(newSelf);
	}
	
	@Override
	public void visitJmlVariableDefinition(JmlVariableDefinition self) {
	    FNExpressionVisitor conditionSimplifierVisitor = new FNExpressionVisitor(
	            currentClassName);
	    self.expr().accept(conditionSimplifierVisitor);
	    JmlVariableDefinition newSelf = new JmlVariableDefinition(self
	            .getTokenReference(), self.modifiers(), self.getType(), self
	            .ident(), conditionSimplifierVisitor.getArrayStack().pop());
	    getStack().push(newSelf);
	}
	
	@Override
	public void visitVariableDefinition(JVariableDefinition self) {
	    FNExpressionVisitor conditionSimplifierVisitor = new FNExpressionVisitor(
	            currentClassName);
	    JExpression expr;
	    if (self.expr() == null) {
	        expr = null;
	    } else {
	        self.expr().accept(conditionSimplifierVisitor);
	        expr = conditionSimplifierVisitor.getArrayStack().pop();
	    }
	    JVariableDefinition newSelf = new JVariableDefinition(self
	            .getTokenReference(), self.modifiers(), self.getType(), self
	            .ident(), expr);
	    getStack().push(newSelf);
	}
	
	@Override
	public void visitJmlAssignmentStatement(JmlAssignmentStatement self) {
	    self.assignmentStatement().accept(this);
	    JExpressionStatement newExpressionStatement = (JExpressionStatement) this
	            .getStack().pop();
	    JmlAssignmentStatement newAssignamentStatement = new JmlAssignmentStatement(
	            newExpressionStatement);
	    getStack().push(newAssignamentStatement);
	}
	
	@Override
	public void visitExpressionStatement(JExpressionStatement self) {
	    FNExpressionVisitor visitor = new FNExpressionVisitor(currentClassName);
	    self.expr().accept(visitor);
	    JExpression newExpression = visitor.getArrayStack().pop();
	    JExpressionStatement newExpressionStatement = new JExpressionStatement(
	            self.getTokenReference(), newExpression, self.getComments());
	    getStack().push(newExpressionStatement);
	}
	
	@Override
	public void visitReturnStatement(JReturnStatement self) {
	    FNExpressionVisitor exprSimplifierVisitor = new FNExpressionVisitor(
	            currentClassName);
	    JExpression expr = null;
	    if (self.expr() != null) {
	        self.expr().accept(exprSimplifierVisitor);
	        expr = exprSimplifierVisitor.getArrayStack().pop();
	    }
	    JReturnStatement newSelf = new JReturnStatement(self
	            .getTokenReference(), expr, self.getComments());
	    this.getStack().push(newSelf);
	}
	
	@Override
	public void visitThrowStatement(JThrowStatement self) {
	    FNExpressionVisitor exprSimplifierVisitor = new FNExpressionVisitor(
	            currentClassName);
	    self.expr().accept(exprSimplifierVisitor);
	    JExpression newExpression = exprSimplifierVisitor.getArrayStack().pop();
	    JThrowStatement newExpressionStatement = new JThrowStatement(self
	            .getTokenReference(), newExpression, self.getComments());
	    this.getStack().push(newExpressionStatement);
	}
	
	@Override
	public void visitJmlAssertStatement(JmlAssertStatement self) {
	    FNExpressionVisitor exprSimplifierVisitor = new FNExpressionVisitor(
	            currentClassName);
	    JExpression expr = null;
	    self.predicate().specExpression().expression().accept(
	            exprSimplifierVisitor);
	    expr = exprSimplifierVisitor.getArrayStack().pop();
	    JmlPredicate jmlPredicate = new JmlPredicate(
	            new JmlSpecExpression(expr));
	    JmlAssertStatement newSelf = new JmlAssertStatement(self
	            .getTokenReference(), self.isRedundantly(), jmlPredicate, self
	            .throwMessage(), self.getComments());
	    this.getStack().push(newSelf);
	}
	
	@Override
	public void visitJmlAssumeStatement(JmlAssumeStatement self) {
	    FNExpressionVisitor exprSimplifierVisitor = new FNExpressionVisitor(
	            currentClassName);
	    JExpression expr = null;
	    self.predicate().specExpression().expression().accept(
	            exprSimplifierVisitor);
	    expr = exprSimplifierVisitor.getArrayStack().pop();
	    JmlPredicate jmlPredicate = new JmlPredicate(
	            new JmlSpecExpression(expr));
	    JmlAssumeStatement newSelf = new JmlAssumeStatement(self
	            .getTokenReference(), self.isRedundantly(), jmlPredicate, self
	            .throwMessage(), self.getComments());
	    this.getStack().push(newSelf);
	}
	
	@Override
	public void visitJmlAssignableClause(JmlAssignableClause self) {
	    JmlStoreRef[] storeRefs = new JmlStoreRef[self.storeRefs().length];
	    for (int i = 0; i < self.storeRefs().length; i++) {
	        JmlStoreRef jmlStoreRef;
	        if (self.storeRefs()[i] instanceof JmlStoreRefExpression) {
	            JmlStoreRefExpression jmlStoreRefExpression = (JmlStoreRefExpression) self.storeRefs()[i];
	            //				assert(jmlStoreRefExpression.expression() instanceof JClassFieldExpression);
	            //				JClassFieldExpression jClassFieldExpression = (JClassFieldExpression) jmlStoreRefExpression.expression();
	            //				boolean isStaticField =  jClassFieldExpression.getField().isFieldStatic();
	            //				boolean mustBeRenamed = !isStaticField;
	            //				if (mustBeRenamed) {
	            //mfrias-mffrias como tiene que retornar un arreglo, lo castee a un arreglo de size 1, y lo descastee despues.				
	            JmlStoreRefExpression[] jmlStoreRefExpressionToArray = new JmlStoreRefExpression[1];
	            jmlStoreRefExpressionToArray[0] = jmlStoreRefExpression;
	            JmlStoreRef[] jmlStoreRefToArray = FieldRenameUtil.convertJmlStoreRefExpression(
	                    jmlStoreRefExpressionToArray ,
	                    this.currentClassName, true);
	            jmlStoreRef = jmlStoreRefToArray[0];
	            //				} else {
	            //					jmlStoreRef = self.storeRefs()[i];
	            //				}
	        } else {
	            jmlStoreRef = self.storeRefs()[i];
	        }
	        storeRefs[i] = jmlStoreRef;
	    }
	    JmlAssignableClause newSelf = new JmlAssignableClause(self
	            .getTokenReference(), self.isRedundantly(), storeRefs);
	    this.getStack().push(newSelf);
	}
	
	@Override
	protected JmlPredicate simplifyPredicateSupport(JmlPredicate predicate) {
	    FNExpressionVisitor visitor = new FNExpressionVisitor(currentClassName);
	    predicate.accept(visitor);
	    JmlPredicate newPredicate = (JmlPredicate) visitor.getArrayStack()
	            .pop();
	    return newPredicate;
	}
	
	@Override
	public void visitJmlRepresentsDecl(JmlRepresentsDecl self) {
	    FNExpressionVisitor visitor = new FNExpressionVisitor(currentClassName);
	    self.predicate().accept(visitor);
	    JmlPredicate newPredicate = (JmlPredicate) (JmlPredicate) visitor.getArrayStack().pop();
	    //		boolean isStaticField =  self.getField().isFieldStatic();
	    //		boolean mustBeRenamed = !isStaticField;
	    JmlStoreRefExpression jmlStoreRefExpression;
	    //		
	    //		if (mustBeRenamed) {
	    String className = FieldRenameUtil
	            .extractClassNameForFieldRenameSupport(self.getField());
	    JmlStoreRefExpression[] jmlStoreRefExpressionToArray = new JmlStoreRefExpression[1];
	    jmlStoreRefExpressionToArray = FieldRenameUtil
	            .convertJmlStoreRefExpression(new JmlStoreRefExpression[]{self.storeRef()}, className, true);
	    jmlStoreRefExpression = jmlStoreRefExpressionToArray[0];
	    //		} else {
	    //			jmlStoreRefExpression = self.storeRef();
	    //		}
	    JmlRepresentsDecl newSelf = new JmlRepresentsDecl(self
	            .getTokenReference(), self.modifiers(), self.isRedundantly(),
	            jmlStoreRefExpression, newPredicate);
	    this.getStack().push(newSelf);
	}
	
	@Override
	public void visitJmlVariantFunction(JmlVariantFunction self){
	    JmlSpecExpression spec = self.specExpression();
	    JExpression specExpre = spec.expression();
	    FNExpressionVisitor fnev = new FNExpressionVisitor(this.currentClassName);
	    specExpre.accept(fnev);
	    JExpression newSpecExpre = fnev.getArrayStack().pop();
	    JmlSpecExpression newSpec = new JmlSpecExpression(newSpecExpre);
	    JmlVariantFunction newSelf = new JmlVariantFunction(self.getTokenReference(), self.isRedundantly(), newSpec);
	    this.getStack().push(newSelf);
	}
	
	@Override
	protected Object clone() throws CloneNotSupportedException {
	    // TODO Auto-generated method stub
	    return super.clone();
	}
	
	// END - ESStatementVisitor

}
