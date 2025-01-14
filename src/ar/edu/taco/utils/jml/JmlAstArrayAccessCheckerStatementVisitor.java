package ar.edu.taco.utils.jml;

import org.multijava.mjc.*;
import org.multijava.util.compiler.JavaStyleComment;
import org.multijava.util.compiler.UnpositionedError;

import java.util.Queue;
import java.util.Stack;

public class JmlAstArrayAccessCheckerStatementVisitor extends JmlAstClonerStatementVisitor{



    public void visitIfStatement(/* @non_null */JIfStatement self) {
        this.getStack().push(self);
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
            CClassType theExceptionType = new CTypeVariable("java.lang.RuntimeException", new CClassType[]{});

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




}
