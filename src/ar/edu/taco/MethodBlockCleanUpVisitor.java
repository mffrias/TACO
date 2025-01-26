package ar.edu.taco;

import ar.edu.taco.utils.jml.JmlAstClonerStatementVisitor;
import org.multijava.mjc.JBlock;
import org.multijava.mjc.JCompilationUnitType;
import org.multijava.mjc.JStatement;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

public class MethodBlockCleanUpVisitor extends JmlAstClonerStatementVisitor {

    Stack<Object> theCleanStack = new Stack<Object>();

    boolean removeAfterwards = false;

    public Stack<Object> getTheCleanStack(){
        return theCleanStack;
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
                removeAfterwards = true;
                break;
            }
            if (!oldBody[i].getClass().getName().contains("JBlock")){
                cleanStatementsQueue.offer(oldBody[i]);
                finalArraySize++;
            } else {
                oldBody[i].accept(this);
                JBlock cleanedBlock = (JBlock)this.theCleanStack.pop();
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

        this.getTheCleanStack().push(new JBlock(self.getTokenReference(), newBody, self.getComments()));
        this.getStack().push(new JBlock(self.getTokenReference(), newBody, self.getComments()));
    }







}
