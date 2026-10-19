package ar.edu.taco.jml.loop;

import ar.edu.taco.utils.jml.JmlAstClonerStatementVisitor;
import org.jmlspecs.checker.JmlLoopStatement;
import org.multijava.mjc.*;
import org.multijava.util.compiler.TokenReference;

/**
 * The first JML simplification pass: turns every {@code for} loop it reaches into a {@code while}
 * loop. {@code for (init; cond; incr) body} becomes the block
 * {@code { init; while (cond) { body; incr; } }}, each expression of {@code incr} as a statement
 * of its own. Other statements are copied by {@link JmlAstClonerStatementVisitor}, which does not
 * enter labeled statements, so a labeled loop is left as it is.
 *
 * <p>{@code ASTSimplifierManager} runs it first, during {@code JmlStage} (see
 * docs/ARCHITECTURE.md); {@link BreakRemoverSimplifier} and {@link WhileRemoverSimplifier} come
 * next, and the latter unrolls only {@code while} loops.
 */
public class ForRemoverVisitor extends JmlAstClonerStatementVisitor {


    /**
     * Rewrites the loop of a {@code JmlLoopStatement} (a loop with its JML loop invariants and
     * variant functions) and keeps the annotations on the result. For a {@code for} loop the
     * result is the block of {@link #visitForStatement}, whose second statement, the
     * {@code while} loop, is wrapped in a new {@code JmlLoopStatement}; any other loop is
     * rewritten and wrapped in a new one.
     *
     * @param self the annotated loop; its rewritten copy is pushed on the stack
     */
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


    /**
     * Replaces a {@code for} loop by {@code { init; while (cond) { body; incr; } }}, rewriting
     * the body first, so that nested loops are converted too. The initialization and the
     * condition are taken as they are; each expression of the increment becomes an expression
     * statement at the end of the new body. Pushes the new block.
     *
     * <p>Assumes an initialization and a condition: for {@code for (; c; i++)} the block gets a
     * null first statement, and for {@code for (int i = 0; ; i++)} the loop a null condition.
     *
     * @param self the loop to replace
     */
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
