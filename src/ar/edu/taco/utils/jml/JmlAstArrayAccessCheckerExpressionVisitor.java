package ar.edu.taco.utils.jml;

import org.multijava.mjc.JArrayAccessExpression;
import org.multijava.mjc.JArrayAccessExpressionExtension;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;
import java.util.concurrent.ConcurrentLinkedQueue;

public class JmlAstArrayAccessCheckerExpressionVisitor extends JmlAstClonerExpressionVisitor{

    Queue<JArrayAccessExpression> theAccessExpressions = new LinkedList<>();

    public Queue<JArrayAccessExpression> getArrayAccessQueue(){
        return theAccessExpressions;
    }

    public void setArrayAccessQueue(Queue<JArrayAccessExpression> theQueue){
        this.theAccessExpressions = theQueue;
    }

    public void visitArrayAccessExpression(/* @non_null */JArrayAccessExpression self) {
        self.accessor().accept(this);
        self.prefix().accept(this);
        JArrayAccessExpression newSelf = new JArrayAccessExpressionExtension(self,this.getArrayStack().pop(), this.getArrayStack().pop());

        this.getArrayStack().push(newSelf);
        this.getArrayAccessQueue().offer(newSelf);
    }
    
}
