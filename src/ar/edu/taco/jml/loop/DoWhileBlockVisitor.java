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

/**
 * Evaluates the condition of each do-while loop into a fresh variable: {@code do B while (c);}
 * becomes {@code boolean dws_<n>; do { B; dws_<n> = c; } while (dws_<n>);}, the declaration
 * placed before the loop in the enclosing block. The assignment is left out when {@code B} ends
 * with {@code break} ({@link LastStatementCollector}). Annotated loops keep their loop invariants
 * and variant functions.
 *
 * <p>{@code ASTSimplifierManager} runs it after {@link WhileBlockVisitor}.
 * {@link WhileRemoverSimplifier} does not unroll do-while loops, so this pass does see them;
 * simplejml then translates them into JDynAlloy loops
 * ({@code BlockStatementsVisitor.visitDoStatement}).
 *
 * <p>Static state: the counter in {@code dws_<n>} is a static field, never reset, so the names
 * keep growing over all the analyses run in one JVM (Stryker runs many).
 */
public class DoWhileBlockVisitor extends JmlAstClonerStatementVisitor {

    private static int variableNameIndex = 0;

    /**
     * Returns the statements that the loops rewritten by this instance need before them (the
     * declaration of each {@code dws_<n>}); the enclosing block's {@link #visitBlockStatement}
     * inserts them.
     *
     * @return the list itself, not a copy
     */
    public List<JStatement> getNewDoWhileStatements() {
        return newStatements;
    }

    /**
     * Replaces the list of statements to insert before the loop.
     *
     * @param newStatements the new list
     */
    public void setNewDoWhileStatements(List<JStatement> newStatements) {
        this.newStatements = newStatements;
    }

    private List<JStatement> newStatements;

    /** Creates the pass with an empty list of statements to insert. */
    public DoWhileBlockVisitor() {
        newStatements = new ArrayList<JStatement>();
    }

    /**
     * Returns a fresh name {@code dws_<n>}, incrementing the static counter (see the class
     * comment).
     *
     * @return the new name
     */
    public String createNewDoWhileVariableName() {
        DoWhileBlockVisitor.variableNameIndex++;
        String s = "dws_" + variableNameIndex;
        return s;
    }	

    /**
     * Rewrites each statement of the block with a new instance and inserts, before it, the
     * statements that instance collected ({@link #getNewDoWhileStatements()}). Resets this
     * instance's own list after each statement.
     *
     * @param self the block
     */
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

    /**
     * Rewrites the loop of an annotated loop and wraps it in a new {@code JmlLoopStatement} with
     * the same loop invariants and variant functions.
     *
     * @param self the annotated loop
     */
    @Override
    public void visitJmlLoopStatement(JmlLoopStatement self) {
        self.stmt().accept(this);
        JStatement newSelf = (JStatement)this.getStack().pop();
        JmlLoopStatement newLoop = new JmlLoopStatement(self.getTokenReference(), self.loopInvariants(), self.variantFunctions(), newSelf, self.getComments());
        this.getStack().push(newLoop);
    }

    /**
     * Rewrites the body, then pushes {@code do { body; dws_<n> = cond; } while (dws_<n>);} and
     * adds the declaration of {@code dws_<n>}, without a value, to
     * {@link #getNewDoWhileStatements()}. The assignment is left out of the body when the body
     * ends with {@code break}.
     *
     * @param self the do-while loop
     */
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
