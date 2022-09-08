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
import org.multijava.mjc.JBreakStatement;
import org.multijava.mjc.JDoStatement;
import org.multijava.mjc.JLocalVariableExpression;
import org.multijava.mjc.JStatement;
import org.multijava.mjc.JVariableDeclarationStatement;
import org.multijava.mjc.JVariableDefinition;
import org.multijava.util.compiler.JavaStyleComment;

import ar.edu.taco.jml.utils.ASTUtils;
import ar.edu.taco.utils.jml.JmlAstClonerStatementVisitor;

public class DoWhileBlockVisitor extends JmlAstClonerStatementVisitor {

    private static int variableNameIndex = 0;

    public List<JStatement> getNewDoWhileStatements() {
        return newStatements;
    }

    public void setNewDoWhileStatements(List<JStatement> newStatements) {
        this.newStatements = newStatements;
    }

    private List<JStatement> newStatements;

    public DoWhileBlockVisitor() {
        newStatements = new ArrayList<JStatement>();
    }

    public String createNewDoWhileVariableName() {
        DoWhileBlockVisitor.variableNameIndex++;
        String s = "dws_" + variableNameIndex;
        return s;
    }	

    @Override
    public void visitBlockStatement(JBlock self) {
        List<JStatement> declarationList = new ArrayList<JStatement>();
        List<JStatement> statementList = new ArrayList<JStatement>();
        for (int i = 0; i < self.body().length; i++) {
            JStatement statement = self.body()[i];
            DoWhileBlockVisitor visitor = new DoWhileBlockVisitor();
            statement.accept(visitor);
            statementList.addAll(visitor.getNewDoWhileStatements());
            statementList.add((JStatement) visitor.getStack().pop());
            // reset statements
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

    @Override
    public void visitJmlLoopStatement(JmlLoopStatement self) {
        self.stmt().accept(this);
        JStatement newSelf = (JStatement)this.getStack().pop();
        JmlLoopStatement newLoop = new JmlLoopStatement(self.getTokenReference(), self.loopInvariants(), self.variantFunctions(), newSelf, self.getComments());
        this.getStack().push(newLoop);
    }

    @Override
    public void visitDoStatement(JDoStatement self) {
        self.body().accept(this);
        JStatement newBody = (JStatement) this.getStack().pop();
        JDoStatement dowhileStatement = null;
        String cond = createNewDoWhileVariableName();
        JVariableDefinition variableDefinition = new JVariableDefinition(self.getTokenReference(), 0, self.cond().getType(), cond, null);
        JVariableDeclarationStatement variableDeclarationStatement = new JVariableDeclarationStatement(self.getTokenReference(), variableDefinition,
                new JavaStyleComment[0]);
        getNewDoWhileStatements().add(variableDeclarationStatement);
        JLocalVariableExpression condReference = new JLocalVariableExpression(self.getTokenReference(), variableDefinition);
        JStatement assignamentStatement = ASTUtils.createAssignamentStatement(condReference, self.cond());
        //		getNewDoWhileStatements().add(assignamentStatement);
        LastStatementCollector lsc = new LastStatementCollector();
        newBody.accept(lsc);
        if (lsc.lastStatementClass != JBreakStatement.class){
            JBlock generatedBlock = ASTUtils.createBlockStatement(newBody, assignamentStatement);
            dowhileStatement = new JDoStatement(self.getTokenReference(), condReference, generatedBlock, self.getComments());
        } else {
            dowhileStatement = new JDoStatement(self.getTokenReference(), condReference, newBody, self.getComments());
        }
        this.getStack().push(dowhileStatement);
    }

}
