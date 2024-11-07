package ar.edu.taco.jml.loop;

import java.util.ArrayList;
import java.util.List;

import ar.edu.taco.jml.utils.ASTUtils;
import ar.edu.taco.utils.jml.JmlAstClonerStatementVisitor;
import org.jmlspecs.checker.JmlLoopStatement;
import org.jmlspecs.checker.JmlRelationalExpression;
import org.jmlspecs.checker.JmlVariantFunction;
import org.multijava.mjc.*;
import org.multijava.util.compiler.JavaStyleComment;
import org.multijava.util.compiler.TokenReference;
import org.multijava.util.compiler.UnpositionedError;

public class ForRemoverVisitor extends JmlAstClonerStatementVisitor {
//    private static int variantFunctionIndex = 0, variableNameIndex = 0;
//    public List<JStatement> getNewForStatements() {return newStatements;}
//
//    public void setNewForStatements(List<JStatement> newStatements){this.newStatements = newStatements;}
//    private List<JStatement> newStatements;
//    public ForBlockVisitor() {newStatements = new ArrayList<JStatement>();}
//
//    public String createNewForVariableName(){
//        ForBlockVisitor.variableNameIndex++;
//        String s = "fs_" + variableNameIndex;
//        return s;
//    }
//
//    public void visitBlockStatement(JBlock self) {
//        List<JStatement> declarationList = new ArrayList<JStatement>();
//        List<JStatement> statementList = new ArrayList<JStatement>();
//        for (int i = 0; i < self.body().length; i++) {
//            JStatement statement = self.body()[i];
//            ForBlockVisitor visitor = new ForBlockVisitor();
//            statement.accept(visitor);
//            statementList.addAll(visitor.getNewForStatements());
//            statementList.add((JStatement) visitor.getStack().pop());
//            // reset statements
//            newStatements = new ArrayList<JStatement>();
//        }
//
//        JStatement[] statements = new JStatement[declarationList.size() + statementList.size()];
//        int i = 0;
//        for (JStatement statement : declarationList) {
//            assert (statement != null);
//            statements[i] = statement;
//            i++;
//        }
//        for (JStatement statement : statementList) {
//            assert (statement != null);
//            statements[i] = statement;
//            i++;
//        }
//        for (int j = 0; j < statements.length; j++) {
//            JStatement statement = statements[j];
//            assert (statement != null);
//        }
//        assert (statements != null);
//        JBlock newSelf = new JBlock(self.getTokenReference(), statements, self.getComments());
//        this.getStack().push(newSelf);
//    }

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
//
//    @Override
//    public void visitJmlLoopStatement(JmlLoopStatement self) {
//
//        JForStatement loopStatement = (JForStatement) self.loopStmt();
//
//        JStatement forBody = loopStatement.body();
//
//        JmlVariantFunction[] varFunctions = self.variantFunctions();
//        if (varFunctions.length > 0){
//            CType type = varFunctions[0].specExpression().getApparentType();
//            String newVarName = "variant_" + variantFunctionIndex;
//            variantFunctionIndex++;
//            JVariableDefinition variableVariantFunction = new JVariableDefinition(loopStatement.getTokenReference(), 0, type, newVarName, varFunctions[0].specExpression());
//            JVariableDeclarationStatement theVariantFunctionVariableDeclaration = new JVariableDeclarationStatement(loopStatement.getTokenReference(), variableVariantFunction, new JavaStyleComment[]{});
//
//            CClassType theExceptionType = new CTypeVariable("java.lang.RuntimeException", new CClassType[]{});
//            try {
//                theExceptionType.checkType(null);
//            } catch (UnpositionedError e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
//
//            JNewObjectExpression theExpre = new JNewObjectExpression(
//                    self.getTokenReference(),
//                    theExceptionType,
//                    new JThisExpression(self.getTokenReference()),
//                    new JExpression[]{});
//
//            JThrowStatement throwStmt = new JThrowStatement(
//                    self.getTokenReference(),
//                    theExpre,
//                    new JavaStyleComment[]{});
//            JExpression expreZero = new JOrdinalLiteral(loopStatement.getTokenReference(), 0, (CNumericType)type);
//            JExpression ifCond = new JmlRelationalExpression(loopStatement.getTokenReference(), OPE_LT, variableVariantFunction.expr(), expreZero);
//            JStatement theIf = new JIfStatement(loopStatement.getTokenReference(), ifCond, throwStmt, null, null);
//            JExpression ifCond2 = new JmlRelationalExpression(loopStatement.getTokenReference(), OPE_GE, varFunctions[0].specExpression(), new JLocalVariableExpression(loopStatement.getTokenReference(), variableVariantFunction));
//            JThrowStatement throwStmt2 = new JThrowStatement(
//                    loopStatement.getTokenReference(),
//                    new JNewObjectExpression(
//                            loopStatement.getTokenReference(),
//                            theExceptionType,
//                            new JThisExpression(loopStatement.getTokenReference()),
//                            new JExpression[]{}),
//                    new JavaStyleComment[]{});
//            org.multijava.mjc.JStatement theIf2 = new JIfStatement(loopStatement.getTokenReference(), ifCond2, throwStmt2, null, null);
//
//            org.multijava.mjc.JStatement newLoopBody = new org.multijava.mjc.JBlock(loopStatement.getTokenReference(), new org.multijava.mjc.JStatement[]{theVariantFunctionVariableDeclaration, theIf, forBody, theIf2}, new JavaStyleComment[]{});
//            JForStatement newLoopStatement = new JForStatement(loopStatement.getTokenReference(), newLoopBody, ifCond, theIf, theIf2, self.getComments());
//            newLoopStatement.accept(this);
//        } else {
//            loopStatement.accept(this);
//        }
//
////		self.loopStmt().accept(this);
//        JStatement newStatement = (JStatement) this.getStack().pop();
//        JmlLoopStatement jmlLoopStatement = new JmlLoopStatement(self.getTokenReference(), self.loopInvariants(), new JmlVariantFunction[]{}, newStatement, self.getComments());
//        JStatement[] blockStatements = new JStatement[this.newStatements.size() + 1];
//        for (int i = 0; i< this.newStatements.size(); i++){
//            blockStatements[i] = this.newStatements.get(i);
//        }
//        blockStatements[this.newStatements.size()] = jmlLoopStatement;
//        this.newStatements = new ArrayList<JStatement>();
//        JBlock newBlockIncludingNewStatements = new JBlock(self.getTokenReference(), blockStatements, self.getComments());
//        this.getStack().push(newBlockIncludingNewStatements);
//    }
}
