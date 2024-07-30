package ar.edu.taco.jml.loop;

import ar.edu.taco.utils.jml.JmlAstClonerStatementVisitor;
import org.multijava.mjc.*;
import org.multijava.util.compiler.TokenReference;

public class ForRemoverVisitor extends JmlAstClonerStatementVisitor {
    @Override
    public void visitForStatement(JForStatement self) {
        self.body().accept(this);
        JStatement newBody = (JStatement) this.getStack().pop();
        // for required statements and expressions

        TokenReference theNewReference = self.getTokenReference();
        JExpression theNewCondition = self.cond();

        int amountIncrements = ((JExpressionListStatement) self.incr()).getExpressions().length;
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
