package ar.edu.taco.utils.jml;

import org.multijava.mjc.*;

import java.util.LinkedList;
import java.util.Queue;

public class JmlAstNullPointerCheckerExpressionVisitor extends JmlAstClonerExpressionVisitor{

    Queue<JExpression> theNullPointerExpressions = new LinkedList<>();

    public Queue<JExpression> getNullPointerQueue(){
        return theNullPointerExpressions;
    }

    public void setNullPointerQueue(Queue<JExpression> theQueue){
        this.theNullPointerExpressions = theQueue;
    }

    public void visitFieldExpression(/* @non_null */JClassFieldExpression self) {
        JExpression prefix = self.prefix();

        if (self.prefix() == null && !self.getField().isStatic()) {
            prefix = new JThisExpression(self.getTokenReference());
        } else if (self.prefix() == null && self.getField().isStatic()) {
            prefix = new JTypeNameExpression(self.getTokenReference(),self.getField().owner().getType(), new JNameExpression(self.getTokenReference(),self.getField().owner().ident()));
        } else {
            if (!(self.prefix() instanceof JThisExpression)) {
                self.prefix().accept(this);
                prefix = this.getArrayStack().pop();
                this.getNullPointerQueue().offer(prefix);
            }
        }

        JClassFieldExpression newSelf = new JClassFieldExpression(self.getTokenReference(), prefix, self.ident());
        newSelf.setField(self.getField());
        newSelf.setType(self.getType());

        this.getArrayStack().push(newSelf);
    }

}
