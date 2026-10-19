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

import ar.edu.taco.utils.jml.JmlAstClonerStatementVisitor;
import org.jmlspecs.checker.JmlAssignmentStatement;
import org.jmlspecs.checker.JmlLoopStatement;
import org.multijava.mjc.*;
import org.multijava.util.compiler.JavaStyleComment;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Removes {@code break} statements from loops, before {@link WhileRemoverSimplifier} unrolls
 * them. Each {@code break} becomes {@code breakVariable_<n> = true;}, and:
 *
 * <ul>
 * <li>the enclosing {@code while} loop (or {@code for} loop) gets {@code !breakVariable_<n>}
 *     conjoined in front of its condition, and the declaration
 *     {@code boolean breakVariable_<n> = false;} right before it, both in a new block;</li>
 * <li>in every block, each statement that follows one that may break is wrapped in
 *     {@code if (!breakVariable_<n> && ...)}.</li>
 * </ul>
 *
 * <p>Labels are ignored ({@code break l;} is handled like {@code break;}), and labeled statements
 * are not entered. A {@code break} in a {@code switch} or in a do-while loop is handled as a
 * break of the enclosing {@code while} loop, whose condition then tests it; with no enclosing
 * {@code while} loop its variable is never declared.
 *
 * <p>{@code ASTSimplifierManager} runs it second, after {@link ForRemoverVisitor} (so {@code for}
 * loops are already {@code while} loops), during {@code JmlStage} (docs/ARCHITECTURE.md).
 *
 * <p>Static state: the counter in {@code breakVariable_<n>} is a static field, never reset, so
 * the names keep growing over all the analyses run in one JVM (Stryker runs many).
 */
public class BreakRemoverSimplifier extends JmlAstClonerStatementVisitor {

    private static int variableNameIndex = 0;

    private Set<JVariableDefinition> collectedBreaks = new LinkedHashSet<JVariableDefinition>();

    /**
     * Returns a fresh name {@code breakVariable_<n>}, incrementing the static counter (see the
     * class comment).
     *
     * @return the new name
     */
    public String createNewBreakVariableName() {
        BreakRemoverSimplifier.variableNameIndex++;
        String s = "breakVariable_" + variableNameIndex;
        return s;
    }

    /**
     * Replaces the {@code break} by {@code breakVariable_<n> = true;} (a
     * {@code JmlAssignmentStatement}) and records the new variable among the breaks of the
     * current scope. The variable's definition gets {@code true} as its initial value here; the
     * enclosing loop sets it to {@code false} when it declares it.
     *
     * @param self the break statement; its label, if any, is ignored
     */
    @Override
    public void visitBreakStatement(/* @non_null */JBreakStatement self) {
           String varName = createNewBreakVariableName();
        JVariableDefinition breakVariableDefinition = new JVariableDefinition(self.getTokenReference(), 0, CType.parseSignature("Z"), varName, new JBooleanLiteral(self.getTokenReference(), true));
        breakVariableDefinition.setIndex(this.variableNameIndex);
        JLocalVariableExpression new_breakVariableDefinition_expre = new JLocalVariableExpression(self.getTokenReference(), breakVariableDefinition);
        JAssignmentExpression assExprBreak = new JAssignmentExpression(self.getTokenReference(), new_breakVariableDefinition_expre, new JBooleanLiteral(self.getTokenReference(), true));
        JExpressionStatement breakExprStatement = new JExpressionStatement(self.getTokenReference(), assExprBreak, self.getComments());
        JmlAssignmentStatement breakAssStatement = new JmlAssignmentStatement(breakExprStatement);

        boolean result = this.collectedBreaks.add(breakVariableDefinition);
        this.getStack().push(breakAssStatement);
    }


    /**
     * Rewrites the body with a new set of breaks, then pushes a block: for each break found in
     * the body, the declaration {@code boolean breakVariable_<n> = false;}, then the loop, with
     * {@code !breakVariable_<n> &&} conjoined in front of its condition for each of them. The
     * enclosing scope's set is restored afterwards: the breaks of the loop do not escape it.
     *
     * @param self the loop
     */
    public void visitWhileStatement(/* @non_null */JWhileStatement self) {

        Set<JVariableDefinition> oldBreaks = this.collectedBreaks;
        Set<JVariableDefinition> newBreaks = new LinkedHashSet<JVariableDefinition>();
        this.collectedBreaks = newBreaks;
        self.body().accept(this);
        JExpression newCond = self.cond();
        for (JVariableDefinition var : this.collectedBreaks) {
            JLocalVariableExpression theVar = new JLocalVariableExpression(self.getTokenReference(), var);
            JExpression notVar = new JUnaryExpression(self.getTokenReference(), 13, theVar);
            newCond = new JConditionalAndExpression(self.getTokenReference(), notVar, newCond);
        }
        JStatement newBody = (JStatement) this.getStack().pop();

        JStatement[] whileWithAddedVarDecls = new JStatement[this.collectedBreaks.size() + 1];
        int index = 0;
        for (JVariableDefinition var : this.collectedBreaks) {
            JVariableDeclarationStatement breakVariableDeclarationStatement = new JVariableDeclarationStatement(self.getTokenReference(), var, new JavaStyleComment[0]);
            var.setValue(new JBooleanLiteral(self.getTokenReference(), false));
            whileWithAddedVarDecls[index] = breakVariableDeclarationStatement;
            index++;
        }

        JWhileStatement newSelf = new JWhileStatement(self.getTokenReference(), newCond, newBody, self.getComments());
        whileWithAddedVarDecls[index] = newSelf;
        JBlock blockWithWhileAndAddedVarDecls = new JBlock(self.getTokenReference(), whileWithAddedVarDecls, self.getComments());
        this.getStack().push(blockWithWhileAndAddedVarDecls);
        this.collectedBreaks = oldBreaks;
    }


    /**
     * Rewrites each statement of the block. Once an earlier statement of the block may have
     * broken (it recorded a break variable), each following statement is wrapped in
     * {@code if (!breakVariable_<i> && ...)}. Afterwards the breaks recorded in the block,
     * together with those of the enclosing scope, are the current set.
     *
     * @param self the block
     */
    public void visitBlockStatement(JBlock self) {
        Set<JVariableDefinition> outerBreaks = this.collectedBreaks;
        this.collectedBreaks = new LinkedHashSet<JVariableDefinition>();
        Set<JVariableDefinition> oldBreaks = new LinkedHashSet<JVariableDefinition>();
        JStatement[] newBody = new JStatement[self.body().length];
        for (int i = 0; i < self.body().length; i++) {
            JStatement statement = self.body()[i];
//            statement.accept(this);
//            JStatement output = (JStatement) this.getStack().pop();
            statement.accept(this);
            JStatement currSelf = (JStatement) this.getStack().pop();
            JExpression newCond = null;
            if (!oldBreaks.isEmpty()) {
                int index = 0;
                for (JVariableDefinition var : oldBreaks) {
                    if (index == 0){
                        JLocalVariableExpression theVar = new JLocalVariableExpression(self.getTokenReference(), var);
                        JExpression notVar = new JUnaryExpression(self.getTokenReference(), 13, theVar);
                        newCond = notVar;
                    } else {
                        JLocalVariableExpression theVar = new JLocalVariableExpression(self.getTokenReference(), var);
                        JExpression notVar = new JUnaryExpression(self.getTokenReference(), 13, theVar);
                        newCond = new JConditionalAndExpression(self.getTokenReference(), notVar, newCond);
                    }
                    index++;
                }
                currSelf = new JIfStatement(self.getTokenReference(), newCond, currSelf, null, self.getComments());
            }
            oldBreaks.addAll(collectedBreaks);
            newBody[i] = currSelf;
        }
        JBlock nweBlock = new JBlock(self.getTokenReference(), newBody, self.getComments());
        this.getStack().push(nweBlock);
        this.collectedBreaks.addAll(outerBreaks);
    }


    /**
     * Rewrites both branches with a new set of breaks. If breaks were already recorded in the
     * current scope, wraps the new {@code if} in {@code if (!breakVariable_<i> && ...)} (inside a
     * block, the block adds the same guard too). The breaks of the branches are added to the
     * current set.
     *
     * @param self the if statement
     */
    public void visitIfStatement(JIfStatement self) {
        Set<JVariableDefinition> oldBreaks = this.collectedBreaks;
        Set<JVariableDefinition> newBreaks = new LinkedHashSet<JVariableDefinition>();
        collectedBreaks = newBreaks;
        self.thenClause().accept(this);
        JStatement newThen = (JStatement) this.getStack().pop();

        JStatement newElse = null;
        if (self.elseClause() != null) {
            self.elseClause().accept(this);
            newElse = (JStatement) this.getStack().pop();
        }
        JStatement newSelf = new JIfStatement(self.getTokenReference(), self.cond(), newThen, newElse, self.getComments());
        if (!oldBreaks.isEmpty()) {
            int index = 0;
            JExpression newCond = null;
            for (JVariableDefinition var : oldBreaks) {
                if (index == 0){
                    JLocalVariableExpression theVar = new JLocalVariableExpression(self.getTokenReference(), var);
                    JExpression notVar = new JUnaryExpression(self.getTokenReference(), 13, theVar);
                    newCond = notVar;
                } else {
                    JLocalVariableExpression theVar = new JLocalVariableExpression(self.getTokenReference(), var);
                    JExpression notVar = new JUnaryExpression(self.getTokenReference(), 13, theVar);
                    newCond = new JConditionalAndExpression(self.getTokenReference(), notVar, newCond);
                }
                index++;
            }
            newSelf = new JIfStatement(self.getTokenReference(), newCond, newSelf, null, self.getComments());
        }
        this.getStack().push(newSelf);
        collectedBreaks.addAll(oldBreaks);
    }


    /**
     * Pushes the assignment unchanged.
     *
     * @param self the assignment
     */
    public void visitJmlAssignmentStatement(JmlAssignmentStatement self) {
//        self.assignmentStatement().accept(this);
//        JmlAssignmentStatement newAssignment = new JmlAssignmentStatement((JExpressionStatement) this.getStack().pop());
//
//        Set<JVariableDefinition> oldBreaks = this.collectedBreaks;
//        JStatement newSelf = self;
//        if (!this.collectedBreaks.isEmpty()) {
//            JExpression newCond = new JBooleanLiteral(self.getTokenReference(), true);
//            for (JVariableDefinition var : this.collectedBreaks) {
//                JLocalVariableExpression theVar = new JLocalVariableExpression(self.getTokenReference(), var);
//                JExpression notVar = new JUnaryExpression(self.getTokenReference(), 13, theVar);
//                newCond = new JConditionalAndExpression(self.getTokenReference(), notVar, newCond);
//            }
//
//            newSelf = new JIfStatement(self.getTokenReference(), newCond, newAssignment, null, self.getComments());
//        }
        this.getStack().push(self);
    }


    /**
     * Rewrites the loop of an annotated loop, which gives a block ending with the loop
     * ({@link #visitWhileStatement}, {@link #visitForStatement}), and wraps that last statement
     * again in a {@code JmlLoopStatement} with the original loop invariants and variant
     * functions.
     *
     * @param self the annotated loop. Its loop must be a {@code while} or {@code for} loop: for
     *     an annotated do-while loop the cast to {@code JBlock} fails
     *     ({@code ClassCastException}).
     */
    public void visitJmlLoopStatement(JmlLoopStatement self){
        self.loopStmt().accept(this);
        JBlock nonBreakLoop = (JBlock)this.getStack().pop();
        JStatement[] arrayWithLoopAtTheEnd = nonBreakLoop.body();
        int posLoop = arrayWithLoopAtTheEnd.length - 1;
        JStatement theActualLoop = arrayWithLoopAtTheEnd[posLoop];
        JmlLoopStatement theNewLoopStatement = new JmlLoopStatement(self.getTokenReference(), self.loopInvariants(), self.variantFunctions(), theActualLoop, self.getComments());
        arrayWithLoopAtTheEnd[posLoop] = theNewLoopStatement;
        JBlock theNewBlock = new JBlock(nonBreakLoop.getTokenReference(), arrayWithLoopAtTheEnd, nonBreakLoop.getComments());
        this.getStack().push(theNewBlock);
    }

    /**
     * Same as {@link #visitWhileStatement}, for a {@code for} loop, which stays a {@code for}
     * loop. In the pipeline {@link ForRemoverVisitor} runs first, so no {@code for} loop is left.
     *
     * @param self the loop
     */
    public void visitForStatement(/* @non_null */JForStatement self) {
        Set<JVariableDefinition> oldBreaks = this.collectedBreaks;
        Set<JVariableDefinition> newBreaks = new LinkedHashSet<JVariableDefinition>();
        this.collectedBreaks = newBreaks;
        self.body().accept(this);
        JExpression newCond = self.cond();
        for (JVariableDefinition var : this.collectedBreaks) {
            JLocalVariableExpression theVar = new JLocalVariableExpression(self.getTokenReference(), var);
            JExpression notVar = new JUnaryExpression(self.getTokenReference(), 13, theVar);
            newCond = new JConditionalAndExpression(self.getTokenReference(), notVar, newCond);
        }
        JStatement newBody = (JStatement) this.getStack().pop();

        JStatement[] forWithAddedVarDecls = new JStatement[this.collectedBreaks.size() + 1];
        int index = 0;
        for (JVariableDefinition var : this.collectedBreaks) {
            JVariableDeclarationStatement breakVariableDeclarationStatement = new JVariableDeclarationStatement(self.getTokenReference(), var, new JavaStyleComment[0]);
            var.setValue(new JBooleanLiteral(self.getTokenReference(), false));
            forWithAddedVarDecls[index] = breakVariableDeclarationStatement;
            index++;
        }

        JForStatement newSelf = new JForStatement(self.getTokenReference(), self.init(), newCond, self.incr(), newBody, self.getComments());
        forWithAddedVarDecls[index] = newSelf;
        JBlock blockWithForAndAddedVarDecls = new JBlock(self.getTokenReference(), forWithAddedVarDecls, self.getComments());
        this.getStack().push(blockWithForAndAddedVarDecls);
        this.collectedBreaks = oldBreaks;
    }






}


