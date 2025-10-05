package ar.edu.taco.jml.loop;

import ar.edu.taco.utils.jml.JmlAstClonerStatementVisitor;
import org.jmlspecs.checker.JmlLoopStatement;
import org.multijava.mjc.*;
import org.multijava.util.compiler.TokenReference;

public class ForRemoverVisitor extends JmlAstClonerStatementVisitor {


    public void visitJmlLoopStatement(JmlLoopStatement self) {
        (self.loopStmt()).accept(this);
        JStatement resultingInnerLoop = (JStatement)this.getStack().pop();
        // In case we are coming from a for, we obtain a while.
        // Invariant: the returned code has two statements: a variable declaration and a while loop
        // We need this to add the variant and invariants to the while loop if they existed.
        JStatement resultingLoop = null;
        if (self.loopStmt() instanceof JForStatement){
            JBlock theLoop = (JBlock)resultingInnerLoop;
            JStatement[] theVarDeclAndTheWhile = theLoop.body();
            JmlLoopStatement theNewWhile = new JmlLoopStatement(self.getTokenReference(), self.loopInvariants(), self.variantFunctions(), theVarDeclAndTheWhile[1], self.getComments());
            theVarDeclAndTheWhile[1] = theNewWhile;
            resultingLoop = new JBlock(self.getTokenReference(), theVarDeclAndTheWhile, self.getComments());
        } else {
            resultingLoop = new JmlLoopStatement(self.getTokenReference(), self.loopInvariants(), self.variantFunctions(), resultingInnerLoop, self.getComments());
        }
        this.getStack().push(resultingLoop);
    }


    @Override
    public void visitForStatement(JForStatement self) {
        self.body().accept(this);
        JStatement newBody = (JStatement) this.getStack().pop();
        // for required statements and expressions

        TokenReference theNewReference = self.getTokenReference();
        JExpression theNewCondition = self.cond();

        int amountIncrements = 0;
        if (self.incr() != null) {
            amountIncrements = ((JExpressionListStatement) self.incr()).getExpressions().length;
        }
        JStatement[] theNewBodyArray = new JStatement[amountIncrements + 1];
        theNewBodyArray[0] = newBody;
        for (int index = 0; index < amountIncrements; index++) {
            JStatement theStatement = null;
            Object theFutureStatement = (((JExpressionListStatement) self.incr()).getExpressions())[index];
            if (theFutureStatement instanceof JExpression) {
                theStatement = new JExpressionStatement(self.getTokenReference(), (JExpression) theFutureStatement, self.getComments());
            } else {
                theStatement = (JStatement) theFutureStatement;
            }
            theNewBodyArray[index+1] = theStatement;
        }

        JStatement theNewBody = new JBlock(theNewReference, theNewBodyArray, self.getComments());
        JWhileStatement theNewStatementr = new JWhileStatement(theNewReference, theNewCondition, theNewBody, self.getComments());
        JStatement[] theVarDeclAndWhileArray = new JStatement[2];
        theVarDeclAndWhileArray[0] = self.init();
        theVarDeclAndWhileArray[1] = theNewStatementr;
        JStatement theVarDeclAndTheWhile = new JBlock(theNewReference, theVarDeclAndWhileArray, self.getComments());

        this.getStack().push(theVarDeclAndTheWhile);
    }
}
