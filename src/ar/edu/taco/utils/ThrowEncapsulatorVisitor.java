package ar.edu.taco.utils;

import ar.edu.taco.jml.expression.ESExpressionVisitor;
import ar.edu.taco.utils.jml.JmlAstClonerStatementVisitor;
import org.jmlspecs.checker.JmlMethodDeclaration;
import org.multijava.mjc.*;
import org.multijava.util.compiler.TokenReference;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

public class ThrowEncapsulatorVisitor extends JmlAstClonerStatementVisitor {

  //  private Stack<Object> cleanStack = new Stack<>();

    public boolean removeAfterwards = false;

    public CType theReturnType;

//    public Stack<Object> getCleanStack() {
//        return cleanStack;
//    }

    @Override
    public void visitJmlMethodDeclaration(JmlMethodDeclaration self) {
        theReturnType = self.returnType();
        super.visitJmlMethodDeclaration(self);
    }


    @Override
    public void visitThrowStatement(JThrowStatement self){
        ESExpressionVisitor exprSimplifierVisitor = new ESExpressionVisitor();
        CType retType = this.theReturnType;

        JExpression expr = null;

        if(self.expr() != null){
            self.expr().accept(exprSimplifierVisitor);
            expr = exprSimplifierVisitor.getArrayStack().pop();
        }

        JExpression trueExpr = new JBooleanLiteral(self.getTokenReference(), true);
        JIfStatement ifStatement = new JIfStatement(self.getTokenReference(), trueExpr, self, null, self.getComments());

        JExpression returnExpr = createReturnExpression(retType, self.getTokenReference());

        JBlock blockWithReturn = createBlockWithReturn(ifStatement, returnExpr);

        this.getStack().push(blockWithReturn);
    }

    private JExpression createReturnExpression(CType expr, TokenReference tokenReference){
        if(!expr.isPrimitive()){
            return new JNullLiteral(tokenReference);
        }

        if(expr.equals(CStdType.Integer)){
            return new JOrdinalLiteral(tokenReference, "0");
        }else if(expr.equals(CStdType.Double)){
            return new JRealLiteral(tokenReference, "0.0");
        }else if(expr.equals(CStdType.Boolean)) {
            return new JBooleanLiteral(tokenReference, false);
        }else if(expr.equals(CStdType.Float)) {
            return new JRealLiteral(tokenReference, "0.0f");
        }else if(expr.equals(CStdType.Long)){
            return new JOrdinalLiteral(tokenReference, "0L");
        }else{
            throw new RuntimeException("Type currently not supported.");
        }

    }

    private JBlock createBlockWithReturn(JIfStatement ifStatement, JExpression returnExpr){
        JReturnStatement returnStatement = new JReturnStatement(ifStatement.getTokenReference(), returnExpr, null);

        JStatement[] newBody = new JStatement[] {ifStatement, returnStatement};
        return new JBlock(ifStatement.getTokenReference(), newBody, ifStatement.getComments());
    }

    @Override
    public void visitBlockStatement(JBlock self){
        JStatement[] oldBody = self.body();
        Queue<Object> cleanStatementQueue = new LinkedList<>();
        int finalArraySize = 0;
        int i = 0;

        while(i < oldBody.length && !removeAfterwards){
            JStatement statement = oldBody[i];

            if(statement instanceof JThrowStatement){
                statement.accept(this);

                if (!this.getStack().isEmpty()) {
                    cleanStatementQueue.offer(this.getStack().pop());
                } else {
                    System.out.println("Trying to pop from an empty stack.");
                }

                //cleanStatementQueue.offer(this.getStack().pop());
                finalArraySize++;
                removeAfterwards = true;
            }else{
                statement.accept(this);

                if (!this.getStack().isEmpty()) {
                    cleanStatementQueue.offer(this.getStack().pop());
                } else {
                    System.out.println("Trying to pop from an empty stack.");
                }

                //cleanStatementQueue.offer(this.getStack().pop());
                finalArraySize++;
            }
            i++;
        }

        JStatement[] newBody = new JStatement[finalArraySize];
        int index = 0;
        while(!cleanStatementQueue.isEmpty()){
            newBody[index++] = (JStatement) cleanStatementQueue.poll();
        }

        this.getStack().push(new JBlock(self.getTokenReference(), newBody, self.getComments()));
    }
}