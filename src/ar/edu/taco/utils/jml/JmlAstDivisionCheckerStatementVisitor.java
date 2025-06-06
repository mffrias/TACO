package ar.edu.taco.utils.jml;

import org.jmlspecs.checker.JmlDivideExpression;
import org.jmlspecs.checker.JmlAssignmentStatement;
import org.multijava.mjc.*;
import org.multijava.util.compiler.JavaStyleComment;
import org.multijava.util.compiler.PositionedError;
import org.multijava.util.compiler.TokenReference;
import org.multijava.util.compiler.UnpositionedError;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

public class JmlAstDivisionCheckerStatementVisitor extends JmlAstClonerStatementVisitor {

    public void visitBinaryExpression(JBinaryExpression self) {
        this.getStack().push(self);

        // Verify if the expression is a division
        if (self instanceof JmlDivideExpression) {
            JmlDivideExpression divExpr = (JmlDivideExpression) self;
            JExpression left = divExpr.left();
            JExpression right = divExpr.right();

            // Verify if the denominator is equals to 0
            JExpression constantZero = new JOrdinalLiteral(self.getTokenReference(), 0, CStdType.Integer);
            JExpression rightIsZero = new JRelationalExpression(self.getTokenReference(), 14, right, constantZero);

            CClassType theExceptionType = new CTypeVariable("java.lang.RuntimeException", new CClassType[]{});
            try {
                theExceptionType.checkType(null);
            } catch (UnpositionedError e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            JThrowStatement throwStatement = new JThrowStatement(
                    self.getTokenReference(),
                    new JNewObjectExpression(
                            self.getTokenReference(),
                            theExceptionType,
                            new JThisExpression(self.getTokenReference()),
                            new JExpression[]{}),
                    new JavaStyleComment[]{});


            //If statement to check if the denominator is zero
            JIfStatement ifStatement = new JIfStatement(self.getTokenReference(), rightIsZero, throwStatement, null, null);

            //block that contains the if statement and the expression
            JExpressionStatement expressionStatement = new JExpressionStatement(self.getTokenReference(), self, null);
            JStatement[] statements = new JStatement[2];
            statements[0] = ifStatement;
            statements[1] = expressionStatement;

            JBlock block = new JBlock(self.getTokenReference(), statements, null);

            this.getStack().push(block);
        }

        self.left().accept(this);
        self.right().accept(this);

        this.getStack().pop();
    }

    public void visitIfStatement( JIfStatement self) {
        this.getStack().push(self);

        JExpression condition = self.cond();

        //if the condition is a division, check if the division is by zero
        if (condition instanceof JmlDivideExpression) {
            JmlDivideExpression divExpr = (JmlDivideExpression) condition;
            JExpression right = divExpr.right();

            //create the expression to check if the denominator is zero
            JExpression constantZero = new JOrdinalLiteral(self.getTokenReference(), 0, CStdType.Integer);
            JExpression rightIsZero = new JRelationalExpression(self.getTokenReference(), 14, right, constantZero);

            //Throw exception
            CClassType exceptionType = new CTypeVariable("java.lang.RuntimeException", new CClassType[]{});
            JThrowStatement throwStatement = new JThrowStatement(
                    self.getTokenReference(),
                    new JNewObjectExpression(
                            self.getTokenReference(),
                            exceptionType,
                            new JThisExpression(self.getTokenReference()),
                            new JExpression[]{}),
                    new JavaStyleComment[]{});

            //if statement that contains the throw
            JIfStatement ifStatement = new JIfStatement(self.getTokenReference(), rightIsZero, throwStatement, null, self.getComments());

            self.thenClause().accept(this);
            JStatement newThen = (JStatement) this.getStack().pop();
            JStatement newElse = null;
            if (self.elseClause() != null) {
                self.elseClause().accept(this);
                newElse = (JStatement) this.getStack().pop();
            }

            JIfStatement modifiedIf = new JIfStatement(self.getTokenReference(), self.cond(), newThen, newElse, self.getComments());
            this.getStack().push(new JBlock(self.getTokenReference(), new JStatement[]{ifStatement, modifiedIf}, self.getComments()));

        } else {
            //if the condition is not a division
            self.thenClause().accept(this);
            JStatement newThen = (JStatement) this.getStack().pop();
            JStatement newElse = null;
            if (self.elseClause() != null) {
                self.elseClause().accept(this);
                newElse = (JStatement) this.getStack().pop();
            }

            JIfStatement theModifiedOriginalIf = new JIfStatement(self.getTokenReference(), self.cond(), newThen, newElse, self.getComments());
            this.getStack().push(new JBlock(self.getTokenReference(), new JStatement[]{theModifiedOriginalIf}, self.getComments()));
        }
    }

    public void visitReturnStatement( JReturnStatement self) {
        this.getStack().push(self);
        JExpression returnExpression = self.expr();

        //if the expression is a division, check if the denominator is zero
        if (returnExpression != null) {
            if (returnExpression instanceof JmlDivideExpression) {
                JmlDivideExpression divExpr = (JmlDivideExpression) returnExpression;
                JExpression right = divExpr.right();

                //create the expression
                JExpression constantZero = new JOrdinalLiteral(self.getTokenReference(), 0, CStdType.Integer);
                JExpression rightIsZero = new JRelationalExpression(self.getTokenReference(), 14, right, constantZero);

                //Throw statement
                CClassType exceptionType = new CTypeVariable("java.lang.RuntimeException", new CClassType[]{});
                JThrowStatement throwStatement = new JThrowStatement(
                        self.getTokenReference(),
                        new JNewObjectExpression(
                                self.getTokenReference(),
                                exceptionType,
                                new JThisExpression(self.getTokenReference()),
                                new JExpression[]{}),
                        new JavaStyleComment[]{});

                //If statement that contains the throw
                JIfStatement ifStatement = new JIfStatement(self.getTokenReference(), rightIsZero, throwStatement, null, self.getComments());

                JStatement[] ifsAndReturn = new JStatement[2];
                ifsAndReturn[0] = ifStatement;
                ifsAndReturn[1] = self;

                JBlock block = new JBlock(self.getTokenReference(), ifsAndReturn, self.getComments());
                this.getStack().push(block);
            } else {
                this.getStack().push(new JReturnStatement(self.getTokenReference(), returnExpression, null));
            }
        }
    }

    public void visitJmlAssignmentStatement(JmlAssignmentStatement self) {
        self.assignmentStatement().accept(this);

        //get the assignment expression
        JExpressionStatement assignmentStatement = (JExpressionStatement) this.getStack().pop();
        JAssignmentExpression assignmentExpression = (JAssignmentExpression) assignmentStatement.expr();

        JmlAstDivisionCheckerExpressionVisitor theExpreVisitor = new JmlAstDivisionCheckerExpressionVisitor();

        JExpression left = assignmentExpression.left();
        JExpression right = assignmentExpression.right();

        left.accept(theExpreVisitor);
        JExpression newLeft = theExpreVisitor.getArrayStack().pop();

        right.accept(theExpreVisitor);
        JExpression newRight = theExpreVisitor.getArrayStack().pop();

        JAssignmentExpression theNewAssExpr = new JAssignmentExpression(self.getTokenReference(), newLeft, newRight);
        JExpressionStatement theExprStatement = new JExpressionStatement(self.getTokenReference(), theNewAssExpr, self.getComments());
        JmlAssignmentStatement newSelf = new JmlAssignmentStatement(theExprStatement);

        JStatement[] theControlsAndTheVarDecl = new JStatement[theExpreVisitor.getDivideExpressions().size() + 1];

        boolean isQueueEmpty = theExpreVisitor.getDivideExpressions().isEmpty();
        int index = 0;
        while (!theExpreVisitor.getDivideExpressions().isEmpty()) {
            JExpression rightDivideExpr = (JExpression) theExpreVisitor.getDivideExpressions().poll();

            //expression to check if the denominator is zero
            JExpression constantZero = new JOrdinalLiteral(self.getTokenReference(), 0, CStdType.Integer);
            JExpression rightIsZero = new JRelationalExpression(self.getTokenReference(), 14, rightDivideExpr, constantZero);

            //Throw exception
            CClassType exceptionType = new CTypeVariable("java.lang.RuntimeException", new CClassType[]{});
            JThrowStatement rightThrowStatement = new JThrowStatement(
                    self.getTokenReference(),
                    new JNewObjectExpression(
                            self.getTokenReference(),
                            exceptionType,
                            new JThisExpression(self.getTokenReference()),
                            new JExpression[]{}),
                    new JavaStyleComment[]{});

            //If statement that contains the throw exception
            JIfStatement rightIfStatement = new JIfStatement(self.getTokenReference(), rightIsZero, rightThrowStatement, null, self.getComments());
            theControlsAndTheVarDecl[index] = rightIfStatement;
            index++;
        }

        if (isQueueEmpty){
            this.getStack().push(newSelf);
        } else {
            theControlsAndTheVarDecl[index] = newSelf;
            JBlock rightBlock = new JBlock(self.getTokenReference(), theControlsAndTheVarDecl, self.getComments());
            this.getStack().push(rightBlock);
        }

    }


    public void visitVariableDeclarationStatement(/* @non_null */JVariableDeclarationStatement self) {
        assert self.getVars().length == 1;

        JmlAstDivisionCheckerExpressionVisitor theExprVisitor = new JmlAstDivisionCheckerExpressionVisitor();
        JVariableDefinition theVar = self.getVars()[0];
        JExpression theVarExpr = theVar.expr();
        theVarExpr.accept(theExprVisitor);
        JExpression theNewExpr = theExprVisitor.getArrayStack().pop();
        JVariableDefinition theNewVar = new JVariableDefinition(self.getTokenReference(), theVar.modifiers(), theVar.getType(), theVar.ident(), theNewExpr);
        JVariableDeclarationStatement newSelf = new JVariableDeclarationStatement(self.getTokenReference(), theNewVar, self.getComments());

        JStatement[] theControlsAndTheVarDecl = new JStatement[theExprVisitor.getDivideExpressions().size() + 1];

        boolean isQueueEmpty = theExprVisitor.getDivideExpressions().isEmpty();

        int index = 0;
        while (!theExprVisitor.getDivideExpressions().isEmpty()){
            JExpression rightDivideExpr = (JExpression) theExprVisitor.getDivideExpressions().poll();

            //expression to check if the denominator is zero
            JExpression constantZero = new JOrdinalLiteral(self.getTokenReference(), 0, CStdType.Integer);
            JExpression rightIsZero = new JEqualityExpression(self.getTokenReference(), 18, rightDivideExpr, constantZero);

            //Throw exception
            CClassType exceptionType = new CTypeVariable("java.lang.RuntimeException", new CClassType[]{});
            JThrowStatement rightThrowStatement = new JThrowStatement(
                    self.getTokenReference(),
                    new JNewObjectExpression(
                            self.getTokenReference(),
                            exceptionType,
                            new JThisExpression(self.getTokenReference()),
                            new JExpression[]{}),
                    new JavaStyleComment[]{});

            //If statement that contains the throw exception
            JIfStatement rightIfStatement = new JIfStatement(self.getTokenReference(), rightIsZero, rightThrowStatement, null, self.getComments());
            theControlsAndTheVarDecl[index] = rightIfStatement;
            index++;
        }

        if (isQueueEmpty){
            this.getStack().push(newSelf);
        } else {
            theControlsAndTheVarDecl[index] = newSelf;
            JBlock rightBlock = new JBlock(self.getTokenReference(), theControlsAndTheVarDecl, self.getComments());
            this.getStack().push(rightBlock);
        }
    }


    public void visitBlockStatement(JBlock self){
        JStatement[] theArrayOfStatements = self.body();
        Queue<JStatement> cummulativeQueue = new LinkedList<JStatement>();
        for (JStatement s : theArrayOfStatements){
            s.accept(this);
            if (this.getStack().peek() instanceof JBlock){
                JStatement[] innerSentences = ((JBlock)(this.getStack().pop())).body();
                for (JStatement inner : innerSentences){
                    cummulativeQueue.offer(inner);
                }
            } else {
                cummulativeQueue.offer((JStatement)(this.getStack().pop()));
            }
        }

        JStatement[] theNewArrayOfStatements = new JStatement[cummulativeQueue.size()];
        int arrayIndex = 0;
        for (JStatement finalStatement : cummulativeQueue){
            theNewArrayOfStatements[arrayIndex] = finalStatement;
            arrayIndex++;
        }

        JBlock newSelf = new JBlock(self.getTokenReference(), theNewArrayOfStatements, self.getComments());
        this.getStack().push(newSelf);
    }
}
