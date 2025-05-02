package ar.edu.taco.utils.jml;

import org.jmlspecs.checker.JmlDivideExpression;
import org.multijava.mjc.*;

import java.util.LinkedList;
import java.util.Queue;

public class JmlAstDivisionCheckerExpressionVisitor extends JmlAstClonerExpressionVisitor {

    // Queue to store expressions of divisions
    private Queue<JExpression> divideExpressions = new LinkedList<>();

    public Queue<JExpression> getDivideExpressions() { return divideExpressions;}

    public void setDivideExpressions(Queue<JExpression> divideExpressions) { this.divideExpressions = divideExpressions;}

    @Override
    public void visitDivideExpression(/* @non_null */JDivideExpression self) {

        self.left().accept(this);
        JExpression left = this.getArrayStack().pop();

        self.right().accept(this);
        JExpression right = this.getArrayStack().pop();

        JDivideExpression newSelf = new JDivideExpression(self.getTokenReference(), left, right);

        //divideExpressions.offer(right);
        divideExpressions.offer(right);

        this.getArrayStack().push(newSelf);
    }
}

