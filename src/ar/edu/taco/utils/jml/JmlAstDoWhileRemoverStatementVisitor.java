package ar.edu.taco.utils.jml;

import org.jmlspecs.checker.JmlLoopStatement;
import org.multijava.mjc.*;

public class JmlAstDoWhileRemoverStatementVisitor extends JmlAstClonerStatementVisitor{

    @Override
    public void visitJmlLoopStatement(JmlLoopStatement self) {
        (self.loopStmt()).accept(this);
        JStatement unrolledJmlLoop = (JStatement)this.getStack().pop();
        this.getStack().push(unrolledJmlLoop);
    }

    @Override
    public void visitDoStatement(/* @non_null */JDoStatement self) {
        self.body().accept(this);
        JStatement body = (JStatement) this.getStack().pop();

        JmlAstClonerExpressionVisitor exprVisitor = new JmlAstClonerExpressionVisitor();
        self.cond().accept(exprVisitor);
        JExpression clonedCond = exprVisitor.getArrayStack().pop();

        JWhileStatement whileStmt = new JWhileStatement(self.getTokenReference(), clonedCond, body, self.getComments());

//        JStatement[] stmts = new JStatement[2];
//        stmts[0] = body;
//        stmts[1] = whileStmt;

        JStatement[] stmts = new JStatement[] { body, whileStmt };

        JBlock newBlock = new JBlock(self.getTokenReference(), stmts, self.getComments());
        this.getStack().push(newBlock);
    }

}
