package ar.edu.taco;

import ar.edu.taco.simplejml.builtin.JInteger;
import ar.edu.taco.utils.jml.JmlAstClonerStatementVisitor;
import org.jmlspecs.checker.JmlMethodDeclaration;
import org.jmlspecs.checker.JmlMethodSpecification;
import org.jmlspecs.checker.JmlSpecCase;
import org.jmlspecs.models.JMLFloat;
import org.jmlspecs.models.JMLInteger;
import org.jmlspecs.models.JMLLong;
import org.multijava.mjc.*;
import org.multijava.util.compiler.JavaStyleComment;
import org.multijava.util.compiler.PositionedError;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

public class MethodBlockCleanUpVisitor extends JmlAstClonerStatementVisitor {

//    Stack<Object> theCleanStack = new Stack<Object>();

    boolean removeAfterwards = false;

//    public Stack<Object> getTheCleanStack(){
//        return theCleanStack;
//    }


    @Override
    public void visitJmlMethodDeclaration(JmlMethodDeclaration self) {
        JBlock newBody;
        if (self.body() == null) {
            newBody = null;
        } else {
            self.body().accept(this);
            newBody = (JBlock) this.getStack().pop();
        }

        JmlMethodSpecification methodSpecification;
        if (self.hasSpecification()) {
            self.methodSpecification().accept(this);
            methodSpecification = (JmlMethodSpecification) this.getStack().pop();
        } else {
            methodSpecification = null;
        }



        if (methodSpecification != null) {
            JmlSpecCase[] specCases = self.methodSpecification().specCases();

            for (int x = 0; x < methodSpecification.specCases().length; x++) {
                specCases[x] = methodSpecification.specCases()[x];
            }
        }


        JmlMethodDeclaration theClonedMethodDecl =
                JmlMethodDeclaration.makeInstance(self.getTokenReference(), self.modifiers(), self.typevariables(), self.returnType(), self.ident(), self.parameters(), self.getExceptions(), newBody, self.javadocComment(), new JavaStyleComment[0], self.methodSpecification());

        removeAfterwards = false;
        this.getStack().push(theClonedMethodDecl);
    }


    public void visitBlockStatement(/* @non_null */JBlock self) {
        JStatement[] oldBody = self.body();
        int i = 0;
        Queue<Object> cleanStatementsQueue = new LinkedList<Object>();
        int finalArraySize = 0;

        while (i < self.body().length && !removeAfterwards){
            if (oldBody[i].getClass().getName().contains("JThrowStatement")){
                cleanStatementsQueue.offer(oldBody[i]);
                finalArraySize++;

                JExpression trueExpr = new JOrdinalLiteral(self.getTokenReference(), 1, CStdType.Integer);

                JIfStatement ifStatement = new JIfStatement(
                        self.getTokenReference(),
                        trueExpr,
                        self,
                        null,
                        self.getComments()
                );
                this.getStack().push(ifStatement);

                removeAfterwards = true;
                break;
            }
            if (!oldBody[i].getClass().getName().contains("JBlock")){
                cleanStatementsQueue.offer(oldBody[i]);
                finalArraySize++;
            } else {
                oldBody[i].accept(this);
                JBlock cleanedBlock = (JBlock)this.getStack().pop();
                finalArraySize += cleanedBlock.body().length;
                cleanStatementsQueue.offer(cleanedBlock.body());
            }
            i++;
        }

        JStatement[] newBody = new JStatement[finalArraySize];
        int newPositionIndex = 0;
        while (!cleanStatementsQueue.isEmpty()){
            Object theFront = cleanStatementsQueue.poll();
            if (theFront.getClass().isArray()){
                for (int arrayIndex = 0; arrayIndex < ((Object[])theFront).length; arrayIndex++){
                    newBody[newPositionIndex] = ((JStatement[])theFront)[arrayIndex];
                    newPositionIndex++;
                }
            } else {
                newBody[newPositionIndex] = (JStatement)theFront;
                newPositionIndex++;
            }
        }

//        this.getTheCleanStack().push(new JBlock(self.getTokenReference(), newBody, self.getComments()));
        this.getStack().push(new JBlock(self.getTokenReference(), newBody, self.getComments()));
    }

}