package ar.edu.taco.utils.jml;

import org.jmlspecs.checker.JmlAssignmentStatement;
import org.multijava.mjc.*;
import org.multijava.util.compiler.JavaStyleComment;
import org.multijava.util.compiler.UnpositionedError;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

public class JmlAstArrayAccessCheckerStatementVisitor extends JmlAstClonerStatementVisitor{



    public void visitIfStatement(/* @non_null */JIfStatement self) {
        this.getStack().push(self); //mfrias4: should this be here?
        JmlAstArrayAccessCheckerExpressionVisitor theExpressionVisitor = new JmlAstArrayAccessCheckerExpressionVisitor();
        self.cond().accept(theExpressionVisitor);
        Queue<JArrayAccessExpression> theQueue = theExpressionVisitor.getArrayAccessQueue();
        JStatement[] theIFsAndTheIfArray = new JStatement[theQueue.size() + 1];
        JBlock theIFsAndTheIf = new JBlock(self.getTokenReference(), theIFsAndTheIfArray, self.getComments());
        int index = 0;
        while (!theQueue.isEmpty()){
            JArrayAccessExpression theArrayAccessExpression = (JArrayAccessExpression)theQueue.poll();
            JExpression thePrefix = theArrayAccessExpression.prefix();
            JExpression theAccessor = theArrayAccessExpression.accessor();
            JExpression constantZero = new JOrdinalLiteral(self.getTokenReference(), 0, CStdType.Integer);
            JExpression leftOfOR = new JRelationalExpression(self.getTokenReference(), 14, theAccessor, constantZero);
            JExpression rightOfOR = new JRelationalExpression(self.getTokenReference(), 16, theAccessor, new JArrayLengthExpression(self.getTokenReference(), thePrefix));
            JConditionalOrExpression theOr = new JConditionalOrExpression(self.getTokenReference(), leftOfOR, rightOfOR);
            theOr.setType(self.cond().getType());
            CClassType theExceptionType = new CTypeVariable("java.lang.IndexOutOfBoundsException", new CClassType[]{});

            try {
                theExceptionType.checkType(null);
            } catch (UnpositionedError e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            JThrowStatement theThrow = new JThrowStatement(
                    self.getTokenReference(),
                    new JNewObjectExpression(
                            self.getTokenReference(),
                            theExceptionType,
                            new JThisExpression(self.getTokenReference()),
                            new JExpression[]{}),
                    new JavaStyleComment[]{});


            JIfStatement theIf = new JIfStatement(self.getTokenReference(), theOr, theThrow, null, self.getComments());
            theIFsAndTheIfArray[index] = theIf;
            index++;
        }

        self.thenClause().accept(this);
        JStatement newThen = (JStatement) this.getStack().pop();
        JStatement newElse = null;
        if (self.elseClause() != null) {
            self.elseClause().accept(this);
            newElse = (JStatement) this.getStack().pop();
        }

        JIfStatement theModifiedOriginalIf = new JIfStatement(self.getTokenReference(), self.cond(), newThen, newElse, self.getComments());
        theIFsAndTheIfArray[index] = theModifiedOriginalIf;
        this.getStack().push(theIFsAndTheIf);
    }

    public void visitReturnStatement(/* @non_null */JReturnStatement self) {
        this.getStack().push(self);
        JExpression returnExpression = self.expr();

        if(returnExpression != null){
            JmlAstArrayAccessCheckerExpressionVisitor expressionVisitor = new JmlAstArrayAccessCheckerExpressionVisitor();
            returnExpression.accept(expressionVisitor);
            Queue<JArrayAccessExpression> arrayAccessQueue = expressionVisitor.getArrayAccessQueue();

            JStatement[] ifsAndReturn = new JStatement[arrayAccessQueue.size() + 1];
            int index = 0;

            while ((!arrayAccessQueue.isEmpty())) {
                JArrayAccessExpression arrayAccess = arrayAccessQueue.poll();
                JExpression prefix = arrayAccess.prefix();
                JExpression accessor = arrayAccess.accessor();

                JExpression constantZero = new JOrdinalLiteral(self.getTokenReference(), 0, CStdType.Integer);
                JExpression leftOfOr = new JRelationalExpression(self.getTokenReference(), 14, accessor, constantZero);
                JExpression rightOfOr = new JRelationalExpression(self.getTokenReference(), 16, accessor, new JArrayLengthExpression(self.getTokenReference(), prefix));
                JConditionalOrExpression orExpression = new JConditionalOrExpression(self.getTokenReference(), leftOfOr, rightOfOr);
                orExpression.setType(CStdType.Boolean);
                System.out.println(orExpression.getType());
                CClassType exceptionType = new CTypeVariable("java.lang.RuntimeException", new CClassType[]{});

                try {
                    exceptionType.checkType(null);
                } catch (UnpositionedError e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                JThrowStatement throwStatement = new JThrowStatement(
                        self.getTokenReference(),
                        new JNewObjectExpression(
                                self.getTokenReference(),
                                exceptionType,
                                new JThisExpression(self.getTokenReference()),
                                new JExpression[]{}),
                        new JavaStyleComment[]{});

                JIfStatement ifStatement = new JIfStatement(self.getTokenReference(), orExpression, throwStatement, null, self.getComments());
                ifsAndReturn[index] = ifStatement;
                index++;

            }
            ifsAndReturn[index] = self;
            JBlock block = new JBlock(self.getTokenReference(), ifsAndReturn, self.getComments());
            this.getStack().push(block);
        }
    }

    public void visitJmlAssignmentStatement(JmlAssignmentStatement self) {
        self.assignmentStatement().accept(this);

        JExpressionStatement assignmentStatement = (JExpressionStatement) this.getStack().pop();
        JAssignmentExpression assignmentExpression = (JAssignmentExpression) assignmentStatement.expr();

        //JAssignmentExpression newSelf = (JAssignmentExpression) assignmentExpression.left(), assignmentExpression.right();
        JExpression left = assignmentExpression.left();

        JmlAstArrayAccessCheckerExpressionVisitor leftVisitor = new JmlAstArrayAccessCheckerExpressionVisitor();
        left.accept(leftVisitor);
        Queue<JArrayAccessExpression> arrayAccessQueueLeft = leftVisitor.getArrayAccessQueue();


        JExpression right = assignmentExpression.right();

        JmlAstArrayAccessCheckerExpressionVisitor rightVisitor = new JmlAstArrayAccessCheckerExpressionVisitor();
        right.accept(rightVisitor);
        Queue<JArrayAccessExpression> arrayAccessQueueRight = rightVisitor.getArrayAccessQueue();


        JStatement[] ifsAndAssignment = new JStatement[arrayAccessQueueLeft.size() + arrayAccessQueueRight.size() + 1];
        int indexLeft = 0;

        while(!arrayAccessQueueLeft.isEmpty()){
            JArrayAccessExpression arrayAccess = arrayAccessQueueLeft.poll();
            JExpression prefix = arrayAccess.prefix();
            JExpression accessor = arrayAccess.accessor();

            JExpression constantZero = new JOrdinalLiteral(self.getTokenReference(), 0, CStdType.Integer);
            JExpression leftRelationalCheck = new JRelationalExpression(self.getTokenReference(), 14, accessor, constantZero);
            JExpression leftLengthCheck = new JRelationalExpression(self.getTokenReference(), 16, accessor, new JArrayLengthExpression(self.getTokenReference(), prefix));

            JConditionalOrExpression leftOr = new JConditionalOrExpression(self.getTokenReference(), leftRelationalCheck, leftLengthCheck);
            leftOr.setType(CStdType.Boolean);

            CClassType exceptionType = new CTypeVariable("java.lang.RuntimeException", new CClassType[]{});

            try {
                exceptionType.checkType(null);
            } catch (UnpositionedError e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            JThrowStatement leftThrowStatement = new JThrowStatement(
                    self.getTokenReference(),
                    new JNewObjectExpression(
                            self.getTokenReference(),
                            exceptionType,
                            new JThisExpression(self.getTokenReference()),
                            new JExpression[]{}),
                    new JavaStyleComment[]{});

            JIfStatement leftIf = new JIfStatement(self.getTokenReference(), leftOr, leftThrowStatement, null, null);
            ifsAndAssignment[indexLeft] = leftIf;
            indexLeft++;
        }

        int indexRight = indexLeft;

        while(!arrayAccessQueueRight.isEmpty()){
            JArrayAccessExpression arrayAccess = arrayAccessQueueRight.poll();
            JExpression prefix = arrayAccess.prefix();
            JExpression accessor = arrayAccess.accessor();

            JExpression constantZero = new JOrdinalLiteral(self.getTokenReference(), 0, CStdType.Integer);
            JExpression rightRelationalCheck = new JRelationalExpression(self.getTokenReference(), 14, accessor, constantZero);
            JExpression rightLengthCheck = new JRelationalExpression(self.getTokenReference(), 16, accessor, new JArrayLengthExpression(self.getTokenReference(), prefix));

            JConditionalOrExpression rightOr = new JConditionalOrExpression(self.getTokenReference(), rightRelationalCheck, rightLengthCheck);
            rightOr.setType(CStdType.Boolean);

            CClassType exceptionType = new CTypeVariable("java.lang.RuntimeException", new CClassType[]{});

            try {
                exceptionType.checkType(null);
            } catch (UnpositionedError e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            JThrowStatement rightThrowStatement = new JThrowStatement(
                    self.getTokenReference(),
                    new JNewObjectExpression(
                            self.getTokenReference(),
                            exceptionType,
                            new JThisExpression(self.getTokenReference()),
                            new JExpression[]{}),
                    new JavaStyleComment[]{});
            JIfStatement rightIf = new JIfStatement(self.getTokenReference(), rightOr, rightThrowStatement, null, null);
            ifsAndAssignment[indexRight] = rightIf;
            indexRight++;

        }


        ifsAndAssignment[indexRight] = new JmlAssignmentStatement(assignmentStatement);
        JBlock  theBlock = new JBlock(self.getTokenReference(), ifsAndAssignment, self.getComments());
        this.getStack().push(theBlock);

    }


//    public void visitJmlAssignmentStatement(JmlAssignmentStatement self) {
//        self.assignmentStatement().accept(this);
//
//        JExpressionStatement assignmentStatement = (JExpressionStatement) this.getStack().pop();
//        JAssignmentExpression assignmentExpression = (JAssignmentExpression) assignmentStatement.expr();
//
//        //JAssignmentExpression newSelf = (JAssignmentExpression) assignmentExpression.left(), assignmentExpression.right();
//        JExpression left = assignmentExpression.left();
//        if(left instanceof JArrayAccessExpression){
//            JArrayAccessExpression arrayAccessLeft = (JArrayAccessExpression) left;
//            JExpression leftPrefix = arrayAccessLeft.prefix();
//            JExpression leftAccessor = arrayAccessLeft.accessor();
//
//            JmlAstArrayAccessCheckerExpressionVisitor leftVisitor = new JmlAstArrayAccessCheckerExpressionVisitor();
//            leftAccessor.accept(leftVisitor);
//            Queue<JArrayAccessExpression> arrayAccessQueueLeft = leftVisitor.getArrayAccessQueue();
//
//            JStatement[] ifsAndAssignment = new JStatement[arrayAccessQueueLeft.size() + 1];
//            int indexLeft = 0;
//
//            while(!arrayAccessQueueLeft.isEmpty()){
//                JArrayAccessExpression arrayAccess = arrayAccessQueueLeft.poll();
//                JExpression prefix = arrayAccess.prefix();
//                JExpression accessor = arrayAccess.accessor();
//
//                JExpression constantZero = new JOrdinalLiteral(self.getTokenReference(), 0, CStdType.Integer);
//                JExpression leftRelationalCheck = new JRelationalExpression(self.getTokenReference(), 14, accessor, constantZero);
//                JExpression leftLengthCheck = new JRelationalExpression(self.getTokenReference(), 16, accessor, new JArrayLengthExpression(self.getTokenReference(), prefix));
//
//                JConditionalOrExpression leftOr = new JConditionalOrExpression(self.getTokenReference(), leftRelationalCheck, leftLengthCheck);
//                leftOr.setType(CStdType.Boolean);
//
//                CClassType exceptionType = new CTypeVariable("java.lang.RuntimeException", new CClassType[]{});
//
//                try {
//                    exceptionType.checkType(null);
//                } catch (UnpositionedError e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                }
//
//                JThrowStatement leftThrowStatement = new JThrowStatement(
//                        self.getTokenReference(),
//                        new JNewObjectExpression(
//                                self.getTokenReference(),
//                                exceptionType,
//                                new JThisExpression(self.getTokenReference()),
//                                new JExpression[]{}),
//                        new JavaStyleComment[]{});
//
//                JIfStatement leftIf = new JIfStatement(self.getTokenReference(), leftOr, leftThrowStatement, null, null);
//                ifsAndAssignment[indexLeft] = leftIf;
//                indexLeft++;
//            }
//            ifsAndAssignment[indexLeft] = assignmentStatement;
//            JBlock block = new JBlock(self.getTokenReference(), ifsAndAssignment, self.getComments());
//            this.getStack().push(block);
//
//        }else{
//
//            this.getStack().push(new JmlAssignmentStatement(assignmentStatement));
//        }
//
//        JExpression right = assignmentExpression.right();
//        if(right instanceof JArrayAccessExpression){
//            JArrayAccessExpression arrayAccessRight = (JArrayAccessExpression) right;
//            JExpression rightPrefix = arrayAccessRight.prefix();
//            JExpression rightAccessor = arrayAccessRight.accessor();
//
//            JmlAstArrayAccessCheckerExpressionVisitor rightVisitor = new JmlAstArrayAccessCheckerExpressionVisitor();
//            rightAccessor.accept(rightVisitor);
//            Queue<JArrayAccessExpression> arrayAccessQueueRight = rightVisitor.getArrayAccessQueue();
//
//            JStatement[] ifsAndAssignmentRight = new JStatement[arrayAccessQueueRight.size() + 1];
//            int indexRight = 0;
//
//            while(!arrayAccessQueueRight.isEmpty()){
//                JArrayAccessExpression arrayAccess = arrayAccessQueueRight.poll();
//                JExpression prefix = arrayAccess.prefix();
//                JExpression accessor = arrayAccess.accessor();
//
//                JExpression constantZero = new JOrdinalLiteral(self.getTokenReference(), 0, CStdType.Integer);
//                JExpression rightRelationalCheck = new JRelationalExpression(self.getTokenReference(), 14, accessor, constantZero);
//                JExpression rightLengthCheck = new JRelationalExpression(self.getTokenReference(), 16, accessor, new JArrayLengthExpression(self.getTokenReference(), prefix));
//
//                JConditionalOrExpression rightOr = new JConditionalOrExpression(self.getTokenReference(), rightRelationalCheck, rightLengthCheck);
//                rightOr.setType(CStdType.Boolean);
//
//                CClassType exceptionType = new CTypeVariable("java.lang.RuntimeException", new CClassType[]{});
//
//                try {
//                    exceptionType.checkType(null);
//                } catch (UnpositionedError e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                }
//
//                JThrowStatement rightThrowStatement = new JThrowStatement(
//                        self.getTokenReference(),
//                        new JNewObjectExpression(
//                                self.getTokenReference(),
//                                exceptionType,
//                                new JThisExpression(self.getTokenReference()),
//                                new JExpression[]{}),
//                        new JavaStyleComment[]{});
//                JIfStatement rightIf = new JIfStatement(self.getTokenReference(), rightOr, rightThrowStatement, null, null);
//                ifsAndAssignmentRight[indexRight] = rightIf;
//                indexRight++;
//
//            }
//            ifsAndAssignmentRight[indexRight] = assignmentStatement;
//            JBlock block = new JBlock(self.getTokenReference(), ifsAndAssignmentRight, self.getComments());
//            this.getStack().push(block);
//        }
//
//        this.getStack().push(new JmlAssignmentStatement(assignmentStatement));
//    }




}
