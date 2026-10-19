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
package ar.edu.taco.jml.varnames;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jmlspecs.checker.JmlAssertStatement;
import org.jmlspecs.checker.JmlAssignmentStatement;
import org.jmlspecs.checker.JmlAssumeStatement;
import org.jmlspecs.checker.JmlLoopInvariant;
import org.jmlspecs.checker.JmlLoopStatement;
import org.jmlspecs.checker.JmlPredicate;
import org.jmlspecs.checker.JmlSpecExpression;
import org.jmlspecs.checker.JmlVariableDefinition;
import org.jmlspecs.checker.JmlVariantFunction;
import org.jmlspecs.jmlrac.JavaAndJmlPrettyPrint2;
import org.multijava.mjc.CClassType;
import org.multijava.mjc.CExpressionContextType;
import org.multijava.mjc.CType;
import org.multijava.mjc.JAssertStatement;
import org.multijava.mjc.JBlock;
import org.multijava.mjc.JCastExpression;
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
import org.multijava.util.compiler.FastStringBuffer;
import org.multijava.util.compiler.PositionedError;
import org.multijava.util.compiler.UnpositionedError;

import ar.edu.taco.jml.utils.ASTUtils;
import ar.edu.taco.utils.jml.JmlAstClonerStatementVisitor;

/**
 * Gives every local variable of the method bodies a name of its own: {@code x} becomes
 * {@code var_<n>_x}, with one counter for all instances. The renaming follows Java's scopes: a
 * declaration maps its name for the rest of its block, and each block starts from a copy of the
 * enclosing mapping. Formal parameters, fields and JML variable definitions keep their names.
 * Declarations, conditions, expression statements, returns, throws, Java and JML assertions,
 * assumptions and loop annotations are rewritten with {@link VNExpressionVisitor}; other
 * statements are copied by {@link JmlAstClonerStatementVisitor} without renaming. Each block also
 * drops the expression statements made of a local variable alone ({@code x;}).
 *
 * <p>{@link VNExpressionVisitor} renames a reference by changing the name of the variable object
 * it points to, which is shared with the AST this pass is given: that AST changes too.
 *
 * <p>{@code ASTSimplifierManager} runs it after {@code SpecMethodCallRemoverVisitor} and before
 * {@code FNBlockVisitor}, during {@code JmlStage} (docs/ARCHITECTURE.md).
 *
 * <p>Static state: the counter in {@code var_<n>_x} is a static field, never reset, so the names
 * change from one analysis to the next in one JVM (Stryker runs many).
 */
public class VNBlockVisitor extends JmlAstClonerStatementVisitor {
    private static Logger log = Logger.getLogger(VNBlockVisitor.class);
    private static int variableNameIndex = 0;

    /**
     * Returns {@code var_<n>_<originalName>}, incrementing the static counter (see the class
     * comment).
     *
     * @param originalName the variable's name in the source
     * @return the new name
     */
    public String createNewName(String originalName) {
        variableNameIndex++;
        String s = "var_" + variableNameIndex + "_" + originalName;
        return s;
    }

    private Map<String, String> variableMapping;

    /** Creates the pass with an empty mapping. */
    public VNBlockVisitor() {
        variableMapping = new HashMap<String, String>();
    }

    /**
     * Creates a visitor that reads and extends the given mapping (not a copy); used for the
     * statements of a block.
     *
     * @param variableMapping original name to new name in the current scope
     */
    public VNBlockVisitor(Map<String, String> variableMapping) {
        this.variableMapping = variableMapping;
    }

    /**
     * Copies a plain method declaration, then starts a new, empty mapping. The JML parser gives
     * {@code JmlMethodDeclaration}s, which go to the inherited {@code visitJmlMethodDeclaration}
     * instead.
     *
     * @param arg0 the method declaration
     */
    @Override
    public void visitMethodDeclaration(JMethodDeclaration arg0) {
        super.visitMethodDeclaration(arg0);
        variableMapping = new HashMap<String, String>();
    }

    /**
     * Rewrites each statement of the block with a new visitor that shares a copy of the current
     * mapping, drops the statements of the form {@code x;}, and restores the mapping afterwards.
     * Logs the new block at debug level.
     *
     * @param self the block
     */
    @Override
    public void visitBlockStatement(JBlock self) {
        Map<String, String> variableMappingOld = this.variableMapping;
        variableMapping = new HashMap<String, String>(this.variableMapping);

        List<JStatement> declarationList = new ArrayList<JStatement>();
        List<JStatement> statementList = new ArrayList<JStatement>();

        for (int i = 0; i < self.body().length; i++) {
            JStatement statement = self.body()[i];
            {
                VNBlockVisitor visitor = new VNBlockVisitor(variableMapping);
                statement.accept(visitor);

                JStatement aStatement = (JStatement) visitor.getStack().pop();

                // If the statement is a Local variable declaration, we are
                // going to skip it.
                if (!(aStatement instanceof JExpressionStatement) || !(((JExpressionStatement) aStatement).expr() instanceof JLocalVariableExpression)) {
                    statementList.add(aStatement);
                }
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

        this.variableMapping = variableMappingOld;
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

        VNExpressionVisitor conditionSimplifierVisitor = new VNExpressionVisitor(variableMapping);
        self.cond().accept(conditionSimplifierVisitor);
        JExpression condition = conditionSimplifierVisitor.getArrayStack().pop();

        JIfStatement newIfStatement = ASTUtils.createIfStatement(condition, newThen, newElse, self.getComments());

        this.getStack().push(newIfStatement);
    }


    @Override
    public void visitAssertStatement(JAssertStatement self){
        VNExpressionVisitor conditionSimplifierVisitor = new VNExpressionVisitor(variableMapping);
        self.predicate().accept(conditionSimplifierVisitor);
        JExpression condition = conditionSimplifierVisitor.getArrayStack().pop();

        JAssertStatement newAssertStatement = new JAssertStatement(self.getTokenReference(), condition, self.getComments());

        this.getStack().push(newAssertStatement);

    }

    @Override
    public void visitJmlLoopStatement(JmlLoopStatement self) {
        JmlLoopInvariant[] newJmlLoopInvariants = new JmlLoopInvariant[self.loopInvariants().length];
        for (int x = 0; x < self.loopInvariants().length; x++) {
            JmlLoopInvariant aJmlLoopInvariant = self.loopInvariants()[x];
            VNExpressionVisitor exprSimplifierVisitor = new VNExpressionVisitor(variableMapping);

            aJmlLoopInvariant.predicate().specExpression().expression().accept(exprSimplifierVisitor);
            JExpression expr = exprSimplifierVisitor.getArrayStack().pop();
            JmlPredicate newJmlPredicate = new JmlPredicate(new JmlSpecExpression(expr));

            JmlLoopInvariant newJmlLoopInvariant = new JmlLoopInvariant(aJmlLoopInvariant.getTokenReference(), aJmlLoopInvariant.isRedundantly(), newJmlPredicate);
            newJmlLoopInvariants[x] = newJmlLoopInvariant;
        }


        JmlVariantFunction[] newJmlLoopVariants = new JmlVariantFunction[self.variantFunctions().length];
        for (int x = 0; x < self.variantFunctions().length; x++) {
            JmlVariantFunction aJmlLoopVariant = self.variantFunctions()[x];
            VNExpressionVisitor exprSimplifierVisitor = new VNExpressionVisitor(variableMapping);

            aJmlLoopVariant.specExpression().expression().accept(exprSimplifierVisitor);
            JExpression expr = exprSimplifierVisitor.getArrayStack().pop();
            JmlSpecExpression newJmlExpression = new JmlSpecExpression(expr);

            JmlVariantFunction newJmlLoopVariant = new JmlVariantFunction(self.getTokenReference(), self.acceptsBreak(), newJmlExpression);
            newJmlLoopVariants[x] = newJmlLoopVariant;
        }

        VNBlockVisitor visitor = new VNBlockVisitor(variableMapping);
        self.stmt().accept(visitor);

        JStatement newStatement = (JStatement) visitor.getStack().pop();

        JmlLoopStatement newJmlLoopStatement = new JmlLoopStatement(self.getTokenReference(), newJmlLoopInvariants, newJmlLoopVariants, newStatement, self.getComments());

        this.getStack().push(newJmlLoopStatement);
    }

    @Override
    public void visitWhileStatement(JWhileStatement self) {
        self.body().accept(this);
        JStatement newBody = (JStatement) this.getStack().pop();

        VNExpressionVisitor conditionSimplifierVisitor = new VNExpressionVisitor(variableMapping);
        self.cond().accept(conditionSimplifierVisitor);
        JExpression condition = conditionSimplifierVisitor.getArrayStack().pop();

        JWhileStatement newJWhileStatement = new JWhileStatement(self.getTokenReference(), condition, newBody, self.getComments());

        this.getStack().push(newJWhileStatement);
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
        VNExpressionVisitor conditionSimplifierVisitor = new VNExpressionVisitor(variableMapping);
        self.expr().accept(conditionSimplifierVisitor);

        JmlVariableDefinition newSelf = new JmlVariableDefinition(self.getTokenReference(), self.modifiers(), self.getType(), self.ident(),
                conditionSimplifierVisitor.getArrayStack().pop());
        getStack().push(newSelf);

    }

    /**
     * Gives the variable a new name ({@link #createNewName}), maps the old name to it for the
     * rest of the scope, and renames in its initializer, if any.
     *
     * @param self the definition
     */
    @Override
    public void visitVariableDefinition(JVariableDefinition self) {

        String newIdent = createNewName(self.ident());

        this.variableMapping.put(self.ident(), newIdent);

        VNExpressionVisitor conditionSimplifierVisitor = new VNExpressionVisitor(variableMapping);
        JExpression newExpr = null;
        if (self.expr() != null) {
            self.expr().accept(conditionSimplifierVisitor);
            newExpr = conditionSimplifierVisitor.getArrayStack().pop();
        }
        JVariableDefinition newSelf = new JVariableDefinition(self.getTokenReference(), self.modifiers(), self.getType(), newIdent, newExpr);
        getStack().push(newSelf);

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
         * 
         * this.getDeclarationStatements().addAll(visitor.getDeclarationStatements
         * ()); this.getNewStatements().addAll(visitor.getNewStatements());
         * getStack().push(newAssignamentStatement);
         */

    }

    @Override
    public void visitExpressionStatement(JExpressionStatement self) {
        VNExpressionVisitor visitor = new VNExpressionVisitor(variableMapping);
        self.expr().accept(visitor);
        JExpression newExpression = visitor.getArrayStack().pop();
        JExpressionStatement newExpressionStatement = new JExpressionStatement(self.getTokenReference(), newExpression, self.getComments());

        getStack().push(newExpressionStatement);

    }

    @Override
    public void visitReturnStatement(JReturnStatement self) {
        VNExpressionVisitor exprSimplifierVisitor = new VNExpressionVisitor(variableMapping);
        JExpression expr = null;

        if (self.expr() != null) {
            self.expr().accept(exprSimplifierVisitor);
            expr = exprSimplifierVisitor.getArrayStack().pop();
        }

        JReturnStatement newSelf = new JReturnStatement(self.getTokenReference(), expr, self.getComments());

        this.getStack().push(newSelf);

    }

    @Override
    public void visitThrowStatement(JThrowStatement self) {
        VNExpressionVisitor exprSimplifierVisitor = new VNExpressionVisitor(variableMapping);

        self.expr().accept(exprSimplifierVisitor);
        JExpression newExpression = exprSimplifierVisitor.getArrayStack().pop();
        JThrowStatement newExpressionStatement = new JThrowStatement(self.getTokenReference(), newExpression, self.getComments());

        this.getStack().push(newExpressionStatement);
    }

    // @Override
    // public void visitAssertStatement(JAssertStatement self) {
    // VNExpressionVisitor exprSimplifierVisitor = new
    // VNExpressionVisitor(variableMapping);
    // JExpression expr = null;
    //
    // self.predicate().accept(exprSimplifierVisitor);
    // expr = exprSimplifierVisitor.getArrayStack().pop();
    //
    // JAssertStatement newSelf = new JAssertStatement(self.getTokenReference(),
    // expr, self.getComments());
    //
    // this.getStack().push(newSelf);
    // }

    @Override
    public void visitJmlAssertStatement(JmlAssertStatement self) {
        VNExpressionVisitor exprSimplifierVisitor = new VNExpressionVisitor(variableMapping);
        JExpression expr = null;

        self.predicate().specExpression().expression().accept(exprSimplifierVisitor);
        expr = exprSimplifierVisitor.getArrayStack().pop();
        JmlPredicate jmlPredicate = new JmlPredicate(new JmlSpecExpression(expr));
        JmlAssertStatement newSelf = new JmlAssertStatement(self.getTokenReference(), self.isRedundantly(), jmlPredicate, self.throwMessage(),
                self.getComments());

        this.getStack().push(newSelf);
    }

    @Override
    public void visitJmlAssumeStatement(JmlAssumeStatement self) {
        VNExpressionVisitor exprSimplifierVisitor = new VNExpressionVisitor(variableMapping);
        JExpression expr = null;

        self.predicate().specExpression().expression().accept(exprSimplifierVisitor);
        expr = exprSimplifierVisitor.getArrayStack().pop();
        JmlPredicate jmlPredicate = new JmlPredicate(new JmlSpecExpression(expr));
        JmlAssumeStatement newSelf = new JmlAssumeStatement(self.getTokenReference(), self.isRedundantly(), jmlPredicate, self.throwMessage(),
                self.getComments());

        this.getStack().push(newSelf);
    }

    @Override
    public void visitJmlLoopInvariant(JmlLoopInvariant self) {
        VNExpressionVisitor exprSimplifierVisitor = new VNExpressionVisitor(variableMapping);
        JExpression expr = null;

        self.predicate().specExpression().expression().accept(exprSimplifierVisitor);

        expr = exprSimplifierVisitor.getArrayStack().pop();

        JmlPredicate jmlPredicate = new JmlPredicate(new JmlSpecExpression(expr));

        JmlLoopInvariant newSelf = new JmlLoopInvariant(self.getTokenReference(), self.isRedundantly(), jmlPredicate);

        this.getStack().push(newSelf);
    }

    // END - ESStatementVisitor

}
