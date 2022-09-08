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

import org.apache.log4j.Logger;
import org.jmlspecs.checker.JmlAssignmentStatement;
import org.jmlspecs.checker.JmlLoopStatement;
import org.jmlspecs.checker.JmlVariableDefinition;
import org.jmlspecs.jmlrac.JavaAndJmlPrettyPrint2;
import org.multijava.mjc.JAssertStatement;
import org.multijava.mjc.JBlock;
import org.multijava.mjc.JExpression;
import org.multijava.mjc.JExpressionStatement;
import org.multijava.mjc.JIfStatement;
import org.multijava.mjc.JLocalVariableExpression;
import org.multijava.mjc.JReturnStatement;
import org.multijava.mjc.JStatement;
import org.multijava.mjc.JThrowStatement;
import org.multijava.mjc.JVariableDeclarationStatement;
import org.multijava.mjc.JVariableDefinition;
import org.multijava.mjc.JWhileStatement;

import ar.edu.taco.jml.utils.ASTUtils;
import ar.edu.taco.utils.jml.JmlAstClonerStatementVisitor;

public class ESBlockVisitor extends JmlAstClonerStatementVisitor {
    private static Logger log = Logger.getLogger(ESBlockVisitor.class);
    // BEGIN - ESStatementVisitor

    private List<JStatement> declarationStatements;

    public List<JStatement> getDeclarationStatements() {
        return declarationStatements;
    }

    public void setDeclarationStatements(List<JStatement> declarationStatements) {
        this.declarationStatements = declarationStatements;
    }

    public List<JStatement> getNewStatements() {
        return newStatements;
    }

    public void setNewStatements(List<JStatement> newStatements) {
        this.newStatements = newStatements;
    }

    private List<JStatement> newStatements;

    public ESBlockVisitor() {
        declarationStatements = new ArrayList<JStatement>();
        newStatements = new ArrayList<JStatement>();
    }


    // END - ESStatementVisitor

    @Override
    public void visitBlockStatement(JBlock self) {
        List<JStatement> declarationList = new ArrayList<JStatement>();
        List<JStatement> statementList = new ArrayList<JStatement>();
        for (int i = 0; i < self.body().length; i++) {
            JStatement statement = self.body()[i];
            // if (statement instanceof JExpressionStatement) {
            // // Si es una exprecion, entonces no es requerido enviarla
            // // atravez del ESStatementVisitor
            // // ya que puede ser procesada directamente por el
            // // ESExpressionVisitor.
            // // Esto simplifica mucho el ESStatementVisitor. En caso
            // // contrario el ESStatementVisitor deberia considerar
            // // indivudalmente
            // // cada clase de excepcion.
            // JExpressionStatement jExpressionStatement =
            // (JExpressionStatement) statement;
            //
            // ESExpressionVisitor visitor = new ESExpressionVisitor();
            // jExpressionStatement.expr().accept(visitor);
            // JExpression newExpression = visitor.getArrayStack().pop();
            // JExpressionStatement newExpressionStatement = new
            // JExpressionStatement(self.getTokenReference(), newExpression,
            // self.getComments());
            //
            // declarationList.addAll(visitor.getDeclarationStatements());
            // statementList.addAll(visitor.getNewStatements());
            // statementList.add(newExpressionStatement);
            //
            // } else
            {
                ESBlockVisitor visitor = new ESBlockVisitor();
                statement.accept(visitor);
                declarationList.addAll(visitor.getDeclarationStatements());
                statementList.addAll(visitor.getNewStatements());
                JStatement aStatement = (JStatement) visitor.getStack().pop();
                // If the statement is a Local variable declaration, we are
                // going to skip it.
                if (!(aStatement instanceof JExpressionStatement) || !(((JExpressionStatement) aStatement).expr() instanceof JLocalVariableExpression)) {
                    statementList.add(aStatement);
                }
                // reset statements
                declarationStatements = new ArrayList<JStatement>();
                newStatements = new ArrayList<JStatement>();
            }
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
        JavaAndJmlPrettyPrint2 prettyPrinter = new JavaAndJmlPrettyPrint2();
        newSelf.accept(prettyPrinter);
        log.debug(prettyPrinter.getPrettyPrint());
        // super.visitBlockStatement(new JBlock(self.getTokenReference(),
        // statements, self.getComments()));
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
        ESExpressionVisitor conditionSimplifierVisitor = new ESExpressionVisitor();
        self.cond().accept(conditionSimplifierVisitor);
        JExpression condition = conditionSimplifierVisitor.getArrayStack().pop();
        JIfStatement newIfStatement = ASTUtils.createIfStatement(condition, newThen, newElse, self.getComments());
        this.getStack().push(newIfStatement);
        this.getDeclarationStatements().addAll(conditionSimplifierVisitor.getDeclarationStatements());
        this.getNewStatements().addAll(conditionSimplifierVisitor.getNewStatements());
    }

    @Override 
    public void visitAssertStatement(JAssertStatement self){
        ESExpressionVisitor conditionSimplifierVisitor = new ESExpressionVisitor();
        self.predicate().accept(conditionSimplifierVisitor);
        JExpression condition = conditionSimplifierVisitor.getArrayStack().pop();
        JAssertStatement newJAssertStatement = new JAssertStatement(self.getTokenReference(), condition, self.getComments());
        this.getStack().push(newJAssertStatement);
        this.getDeclarationStatements().addAll(conditionSimplifierVisitor.getDeclarationStatements());
        this.getNewStatements().addAll(conditionSimplifierVisitor.getNewStatements());
    }

    @Override
    public void visitJmlLoopStatement(JmlLoopStatement self) {
        self.stmt().accept(this);
        JStatement newSelf = (JStatement)this.getStack().pop();
        JmlLoopStatement newLoop = new JmlLoopStatement(self.getTokenReference(), self.loopInvariants(), self.variantFunctions(), newSelf, self.getComments());
        this.getStack().push(newLoop);
    }

    @Override
    public void visitWhileStatement(JWhileStatement self) {
        ESExpressionVisitor conditionSimplifierVisitor = new ESExpressionVisitor();
        self.cond().accept(conditionSimplifierVisitor);
        JExpression condition = conditionSimplifierVisitor.getArrayStack().pop();
        self.body().accept(this);
        JStatement newBody = (JStatement) this.getStack().pop();
        JWhileStatement newJWhileStatement = new JWhileStatement(self.getTokenReference(), condition, newBody, self.getComments());
        this.getStack().push(newJWhileStatement);
        this.getDeclarationStatements().addAll(conditionSimplifierVisitor.getDeclarationStatements());
        this.getNewStatements().addAll(conditionSimplifierVisitor.getNewStatements());
    }

    @Override
    public void visitVariableDeclarationStatement(JVariableDeclarationStatement self) {
        JVariableDefinition[] newVars = new JVariableDefinition[self.getVars().length];
        for (int i = 0; i < self.getVars().length; i++) {
            JVariableDefinition variableDefinition = self.getVars()[i];
            variableDefinition.accept(this);
            newVars[i] = (JVariableDefinition) getStack().pop();
        }
        JVariableDeclarationStatement newSelf = new JVariableDeclarationStatement(self.getTokenReference(), newVars, self.getComments());
        this.getStack().push(newSelf);
    }

    @Override
    public void visitJmlVariableDefinition(JmlVariableDefinition self) {
        ESExpressionVisitor conditionSimplifierVisitor = new ESExpressionVisitor();
        self.expr().accept(conditionSimplifierVisitor);
        JmlVariableDefinition newSelf = new JmlVariableDefinition(self.getTokenReference(), self.modifiers(), self.getType(), self.ident(),
                conditionSimplifierVisitor.getArrayStack().pop());
        getStack().push(newSelf);
        this.getDeclarationStatements().addAll(conditionSimplifierVisitor.getDeclarationStatements());
        this.getNewStatements().addAll(conditionSimplifierVisitor.getNewStatements());
    }

    @Override
    public void visitVariableDefinition(JVariableDefinition self) {
        ESExpressionVisitor conditionSimplifierVisitor = new ESExpressionVisitor();
        JExpression newExpr = null;
        if (self.expr() != null) {
            self.expr().accept(conditionSimplifierVisitor);
            newExpr = conditionSimplifierVisitor.getArrayStack().pop();
        }
        JVariableDefinition newSelf = new JVariableDefinition(self.getTokenReference(), self.modifiers(), self.getType(), self.ident(), newExpr);
        getStack().push(newSelf);
        this.getDeclarationStatements().addAll(conditionSimplifierVisitor.getDeclarationStatements());
        this.getNewStatements().addAll(conditionSimplifierVisitor.getNewStatements());
        for (int idx = 0; idx < conditionSimplifierVisitor.getPostfixNewStatements().size(); idx++){
            this.getNewStatements().add(conditionSimplifierVisitor.getPostfixNewStatements().get(idx));
        }
        conditionSimplifierVisitor.setNewPostfixNewStatements();
    }

    @Override
    public void visitJmlAssignmentStatement(JmlAssignmentStatement self) {
        /*
         * ESExpressionVisitor conditionSimplifierVisitor = new
         * ESExpressionVisitor(); JExpression newExpr = null; if (self. != null)
         * { self.expr().accept(conditionSimplifierVisitor); newExpr =
         * conditionSimplifierVisitor.getArrayStack().pop(); }
         */
        self.assignmentStatement().accept(this);
        JExpressionStatement newExpressionStatement = (JExpressionStatement) this.getStack().pop();
        JmlAssignmentStatement newAssignamentStatement = new JmlAssignmentStatement(newExpressionStatement);
        getStack().push(newAssignamentStatement);
        /*
         * JExpression newExpression = visitor.getArrayStack().pop();
         * JExpressionStatement newAssignamentStatement = new
         * JmlAssignmentStatement(self.getTokenReference(), newExpression,
         * self.getComments());
         * 
         * 
         * 
         * 
         * this.getDeclarationStatements().addAll(visitor.getDeclarationStatements
         * ()); this.getNewStatements().addAll(visitor.getNewStatements());
         * getStack().push(newAssignamentStatement);
         */
    }

    @Override
    public void visitExpressionStatement(JExpressionStatement self) {
        ESExpressionVisitor visitor = new ESExpressionVisitor();
        visitor.setExpressionStatement(true);
        self.expr().accept(visitor);
        JExpression newExpression = visitor.getArrayStack().pop();
        JExpressionStatement newExpressionStatement = new JExpressionStatement(self.getTokenReference(), newExpression, self.getComments());
        this.getDeclarationStatements().addAll(visitor.getDeclarationStatements());
        this.getNewStatements().addAll(visitor.getNewStatements());
        for (int idx = 0; idx < visitor.getPostfixNewStatements().size(); idx++){
            this.getNewStatements().add(visitor.getPostfixNewStatements().get(idx));
        }
        visitor.setNewPostfixNewStatements();
        getStack().push(newExpressionStatement);
    }

    @Override
    public void visitReturnStatement(JReturnStatement self) {
        ESExpressionVisitor exprSimplifierVisitor = new ESExpressionVisitor();
        JExpression expr = null;
        if (self.expr() != null) {
            self.expr().accept(exprSimplifierVisitor);
            expr = exprSimplifierVisitor.getArrayStack().pop();
        }

        JReturnStatement newSelf = new JReturnStatement(self.getTokenReference(), expr, self.getComments());

        this.getStack().push(newSelf);
        this.getDeclarationStatements().addAll(exprSimplifierVisitor.getDeclarationStatements());
        this.getNewStatements().addAll(exprSimplifierVisitor.getNewStatements());
    }

    @Override
    public void visitThrowStatement(JThrowStatement self) {
        ESExpressionVisitor exprSimplifierVisitor = new ESExpressionVisitor();
        JExpression expr = null;

        if (self.expr() != null) {
            self.expr().accept(exprSimplifierVisitor);
            expr = exprSimplifierVisitor.getArrayStack().pop();
        }

        JThrowStatement newSelf = new JThrowStatement(self.getTokenReference(), expr, self.getComments());

        this.getStack().push(newSelf);
        this.getDeclarationStatements().addAll(exprSimplifierVisitor.getDeclarationStatements());
        this.getNewStatements().addAll(exprSimplifierVisitor.getNewStatements());
    }

    //    @Override
    //    public void visitJmlAssertStatement(JmlAssertStatement self) {
    //	ESExpressionVisitor exprSimplifierVisitor = new ESExpressionVisitor();
    //	JExpression expr = null;
    //
    //	self.predicate().specExpression().expression().accept(exprSimplifierVisitor);
    //	expr = exprSimplifierVisitor.getArrayStack().pop();
    //	JmlPredicate jmlPredicate = new JmlPredicate(new JmlSpecExpression(expr));
    //	JmlAssertStatement newSelf = new JmlAssertStatement(self.getTokenReference(), self.isRedundantly(), jmlPredicate, self.throwMessage(), self
    //		.getComments());
    //
    //	this.getStack().push(newSelf);
    //	this.getDeclarationStatements().addAll(exprSimplifierVisitor.getDeclarationStatements());
    //	this.getNewStatements().addAll(exprSimplifierVisitor.getNewStatements());
    //    }
    //
    //    @Override
    //    public void visitJmlAssumeStatement(JmlAssumeStatement self) {
    //	ESExpressionVisitor exprSimplifierVisitor = new ESExpressionVisitor();
    //	JExpression expr = null;
    //
    //	self.predicate().specExpression().expression().accept(exprSimplifierVisitor);
    //	expr = exprSimplifierVisitor.getArrayStack().pop();
    //	JmlPredicate jmlPredicate = new JmlPredicate(new JmlSpecExpression(expr));
    //	JmlAssumeStatement newSelf = new JmlAssumeStatement(self.getTokenReference(), self.isRedundantly(), jmlPredicate, self.throwMessage(), self
    //		.getComments());
    //
    //	this.getStack().push(newSelf);
    //	this.getDeclarationStatements().addAll(exprSimplifierVisitor.getDeclarationStatements());
    //	this.getNewStatements().addAll(exprSimplifierVisitor.getNewStatements());
    //    }

    // END - ESStatementVisitor

}
