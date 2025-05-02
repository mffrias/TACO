package ar.edu.taco.utils.jml;

import org.jmlspecs.checker.JmlAssignmentStatement;
import org.multijava.mjc.*;
import org.multijava.util.compiler.JavaStyleComment;
import org.multijava.util.compiler.UnpositionedError;

import java.util.LinkedList;
import java.util.Queue;

public class JmlAstNullPointerCheckerStatementVisitor extends JmlAstClonerStatementVisitor{


    //visit IfStatement and add null checks if any expressions could cause NPE
    public void visitIfStatement(/* @non_null */JIfStatement self) {
        this.getStack().push(self);

        //visitor for expressions to find null-dereferencable expressions
        JmlAstNullPointerCheckerExpressionVisitor theExpressionVisitor = new JmlAstNullPointerCheckerExpressionVisitor();
        self.cond().accept(theExpressionVisitor);

        Queue<JExpression> theQueue = theExpressionVisitor.getNullPointerQueue();

        JStatement[] theIFsAndTheIfArray = new JStatement[theQueue.size() + 1];
        JBlock theIFsAndTheIf = new JBlock(self.getTokenReference(), theIFsAndTheIfArray, self.getComments());

        int index = 0;
        while (!theQueue.isEmpty()){
            JExpression nullableExpression = theQueue.poll();

            //check: if (expression == null)
            JExpression nullLiteral = new JNullLiteral(self.getTokenReference());
            JExpression nullCheck = new JEqualityExpression(self.getTokenReference(), 18, nullableExpression, nullLiteral);

            //create exception to throw
            CClassType theExceptionType = new CTypeVariable("java.lang.NullPointerException", new CClassType[]{});

            try {
                theExceptionType.checkType(null);
            } catch (UnpositionedError e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            //create throw statement
            JThrowStatement theThrow = new JThrowStatement(
                    self.getTokenReference(),
                    new JNewObjectExpression(
                            self.getTokenReference(),
                            theExceptionType,
                            new JThisExpression(self.getTokenReference()),
                            new JExpression[]{}),
                    new JavaStyleComment[]{});

            //if statement: if (expression == null) throw new NullPointerException();
            JIfStatement theIf = new JIfStatement(self.getTokenReference(), nullCheck, theThrow, null, self.getComments());
            theIFsAndTheIfArray[index] = theIf;
            index++;
        }

        //process then and else clause
        self.thenClause().accept(this);
        JStatement newThen = (JStatement) this.getStack().pop();
        JStatement newElse = null;
        if (self.elseClause() != null) {
            self.elseClause().accept(this);
            newElse = (JStatement) this.getStack().pop();
        }

        //modified if statement w/ processed then and else clauses
        JIfStatement theModifiedOriginalIf = new JIfStatement(self.getTokenReference(), self.cond(), newThen, newElse, self.getComments());
        theIFsAndTheIfArray[index] = theModifiedOriginalIf;
        this.getStack().push(theIFsAndTheIf);
    }

    //visit ReturnStatement and add null checks if the return value could cause NPE
    public void visitReturnStatement(/* @non_null */JReturnStatement self) {
        this.getStack().push(self);
        JExpression returnExpression = self.expr();

        if(returnExpression != null){
            JmlAstNullPointerCheckerExpressionVisitor expressionVisitor = new JmlAstNullPointerCheckerExpressionVisitor();
            returnExpression.accept(expressionVisitor);

            Queue<JExpression> theQueue = expressionVisitor.getNullPointerQueue();
            JStatement[] ifsAndReturn = new JStatement[theQueue.size() + 1];

            int index = 0;
            while ((!theQueue.isEmpty())) {
                JExpression nullableExpression = theQueue.poll();

                //check: if (expression == null)
                JExpression nullLiteral = new JNullLiteral(self.getTokenReference());
                JExpression nullCheck = new JEqualityExpression(self.getTokenReference(), 18, nullableExpression, nullLiteral);

                //create exception to throw
                CClassType theExceptionType = new CTypeVariable("java.lang.NullPointerException", new CClassType[]{});
                try {
                    theExceptionType.checkType(null);
                } catch (UnpositionedError e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                //create throw statement
                JThrowStatement theThrow = new JThrowStatement(
                        self.getTokenReference(),
                        new JNewObjectExpression(
                                self.getTokenReference(),
                                theExceptionType,
                                new JThisExpression(self.getTokenReference()),
                                new JExpression[]{}),
                        new JavaStyleComment[]{});

                //if statement: if (expression == null) throw new NullPointerException();
                JIfStatement theIf = new JIfStatement(self.getTokenReference(), nullCheck, theThrow, null, self.getComments());
                ifsAndReturn[index] = theIf;
                index++;
            }
            ifsAndReturn[index] = self;
            JBlock block = new JBlock(self.getTokenReference(), ifsAndReturn, self.getComments());
            this.getStack().push(block);
        }
    }

    //visit AssignmentStatement and add null checks for both left and right side if they involve null objects
    public void visitJmlAssignmentStatement(JmlAssignmentStatement self) {
        self.assignmentStatement().accept(this);

        JExpressionStatement assignmentStatement = (JExpressionStatement) this.getStack().pop();
        JAssignmentExpression assignmentExpression = (JAssignmentExpression) assignmentStatement.expr();

        //queues to store null checks for left and right expressions
        Queue<JStatement> exprsToCheckQueue = new LinkedList<>();

        //check left and right expressions of assignment
        JExpression left = assignmentExpression.left();
        if (left instanceof JClassFieldExpression || left instanceof JMethodCallExpression) {
            checkNullDereference(left, self, exprsToCheckQueue);
        }

        JExpression right = assignmentExpression.right();
        if (right instanceof JClassFieldExpression || right instanceof JMethodCallExpression) {
            checkNullDereference(right, self, exprsToCheckQueue);
        }

        //create an array to hold null checks and the assignment statement
        JStatement[] theBlockArray = new JStatement[exprsToCheckQueue.size() + 1];

        int index = 0;
        while (!exprsToCheckQueue.isEmpty()) {
            theBlockArray[index++] = exprsToCheckQueue.poll();
        }

        theBlockArray[index] = assignmentStatement;
        JBlock nullCheckBlock = new JBlock(self.getTokenReference(), theBlockArray, self.getComments());
        getStack().push(nullCheckBlock);
    }

    //helper method to create null check for expression
    private void checkNullDereference(JExpression expression, JStatement self, Queue<JStatement> queue) {
        JmlAstNullPointerCheckerExpressionVisitor expressionVisitor = new JmlAstNullPointerCheckerExpressionVisitor();
        expression.accept(expressionVisitor);


        Queue<JExpression> nullPointerQueue = expressionVisitor.getNullPointerQueue();
        while(!nullPointerQueue.isEmpty()){
            JExpression nullableExpression = nullPointerQueue.poll();

            //check: if (nullableExpression == null)
            JExpression nullLiteral = new JNullLiteral(self.getTokenReference());
            JExpression nullCheck = new JEqualityExpression(self.getTokenReference(), 18, nullableExpression, nullLiteral);

            //create exception to throw
            CClassType theExceptionType = new CTypeVariable("java.lang.NullPointerException", new CClassType[]{});
            try {
                theExceptionType.checkType(null);
            } catch (UnpositionedError e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            //create throw statement
            JThrowStatement theThrow = new JThrowStatement(
                    self.getTokenReference(),
                    new JNewObjectExpression(
                            self.getTokenReference(),
                            theExceptionType,
                            new JThisExpression(self.getTokenReference()),
                            new JExpression[]{}),
                    new JavaStyleComment[]{});

            //if statement: if (expression == null) throw new NullPointerException();
            JIfStatement theIf = new JIfStatement(self.getTokenReference(), nullCheck, theThrow, null, self.getComments());
            queue.offer(theIf);
        }
    }
}





















