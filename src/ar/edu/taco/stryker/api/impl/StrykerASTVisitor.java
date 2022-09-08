package ar.edu.taco.stryker.api.impl;

import java.util.List;
import java.util.Set;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.ChildListPropertyDescriptor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.EnhancedForStatement;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.LineComment;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.TryStatement;
import org.eclipse.jdt.core.dom.WhileStatement;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;

import ar.edu.taco.stryker.api.impl.input.OpenJMLInputWrapper;

import com.google.common.collect.Sets;

public class StrykerASTVisitor extends ASTVisitor {

    private final OpenJMLInputWrapper wrapper;
    private final CompilationUnit unit;
    private final String source;
    private final AST ast;
    private final String seqFileName;
    private String methodName;
    private final ASTRewrite rewrite;
    private final Set<ASTNode> customNodes = Sets.newHashSet();
    private int nextMutID;
    private List<Integer> lastMutatedLines;
    private List<Integer> mutableLines;
    private static final String mutIDCommentPrefix = "//mutID ";
    
    public StrykerASTVisitor(final OpenJMLInputWrapper wrapper, CompilationUnit unit, String source, final AST ast, String seqFileName, List<Integer> lastMutatedLines, List<Integer> mutableLines) {
        super();
        this.wrapper = wrapper;
        this.unit = unit;
        this.source = source;
        this.ast = ast;
        this.seqFileName = seqFileName;
        this.rewrite = ASTRewrite.create(ast);
        this.lastMutatedLines = lastMutatedLines;
        this.mutableLines = mutableLines;
    }
    
    public void setNextMutID(int nextMutID) {
        this.nextMutID = nextMutID;
    }
    
    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }
    
    public static int getLineNumber(CompilationUnit compilationUnit, ASTNode node) {
        return compilationUnit.getLineNumber(node.getStartPosition()) - 1;
    }

    //    @Override
    //    public void endVisit(BreakStatement node) {
    //        System.out.println("---------------------------------------------------------------------");
    //        System.out.println("Este es un BreakStatement:");
    //        System.out.println(node);
    //        System.out.println("---------------------------------------------------------------------");
    //        super.endVisit(node);
    //    }
    //    
    //    @Override
    //    public void endVisit(ContinueStatement node) {
    //        System.out.println("---------------------------------------------------------------------");
    //        System.out.println("Este es un ContinueStatement:");
    //        System.out.println(node);
    //        System.out.println("---------------------------------------------------------------------");
    //        super.endVisit(node);
    //    }
    //    
    //    @Override
    //    public void endVisit(DoStatement node) {
    //        System.out.println("---------------------------------------------------------------------");
    //        System.out.println("Este es un DoStatement:");
    //        System.out.println(node);
    //        System.out.println("---------------------------------------------------------------------");
    //        super.endVisit(node);
    //    }
    //    
    //    @Override
    //    public void endVisit(LabeledStatement node) {
    //        System.out.println("---------------------------------------------------------------------");
    //        System.out.println("Este es un LabeledStatement:");
    //        System.out.println(node);
    //        System.out.println("---------------------------------------------------------------------");
    //        super.endVisit(node);
    //    }
    //    
    //    @Override
    //    public void endVisit(SwitchCase node) {
    //        System.out.println("---------------------------------------------------------------------");
    //        System.out.println("Este es un SwitchCase:");
    //        System.out.println(node);
    //        System.out.println("---------------------------------------------------------------------");
    //        super.endVisit(node);
    //    }
    //    
    //    @Override
    //    public void endVisit(SwitchStatement node) {
    //        System.out.println("---------------------------------------------------------------------");
    //        System.out.println("Este es un SwitchStatement:");
    //        System.out.println(node);
    //        System.out.println("---------------------------------------------------------------------");
    //        super.endVisit(node);
    //    }
    //    
    //    @Override
    //    public void endVisit(SynchronizedStatement node) {
    //        System.out.println("---------------------------------------------------------------------");
    //        System.out.println("Este es un SynchronizedStatement:");
    //        System.out.println(node);
    //        System.out.println("---------------------------------------------------------------------");
    //        super.endVisit(node);
    //    }
    //    
    //    @Override
    //    public void endVisit(ThrowStatement node) {
    //        System.out.println("---------------------------------------------------------------------");
    //        System.out.println("Este es un ThrowStatement:");
    //        System.out.println(node);
    //        System.out.println("---------------------------------------------------------------------");
    //        super.endVisit(node);
    //    }
    //    
    //    @Override
    //    public void endVisit(TryStatement node) {
    //        System.out.println("---------------------------------------------------------------------");
    //        System.out.println("Este es un TryStatement:");
    //        System.out.println(node);
    //        System.out.println("---------------------------------------------------------------------");
    //        super.endVisit(node);
    //    }
    //    
    //    // Las siguientes no extienden de statement pero cuidado!
    //    
    //    @Override
    //    public void endVisit(AnonymousClassDeclaration node) {
    //        // TODO: Auto-generated method stub
    //        super.endVisit(node);
    //    }
    //    
    //    @Override
    //    public void endVisit(ConditionalExpression node) {
    //        // TODO: Auto-generated method stub
    //        super.endVisit(node);
    //    }
    
    public String getLineComment(int commentIndex) {
        LineComment lineCommentNode;
        //Tiene comentario el statement, potencialmente sea el de mutacion que necesito
        String lineComment = "";
        boolean flag = true;
        while (flag) {
            lineCommentNode = ((LineComment) unit.getCommentList().get(commentIndex));
            lineComment = source.substring(lineCommentNode.getStartPosition(), 
                    lineCommentNode.getStartPosition() + lineCommentNode.getLength());
            if (!lineComment.contains("mutGenLimit")) {
                --commentIndex;
            } else {
                break;
            }
        }
        lineComment += '\n';
        return lineComment;
    }

    @Override
    public boolean preVisit2(ASTNode node) {
        if (node instanceof Statement 
                && !(node instanceof Block) 
                && !(node instanceof IfStatement) 
                && !(node instanceof WhileStatement)
                && !(node instanceof ForStatement)
                && !(node instanceof EnhancedForStatement)
                && !(node instanceof ReturnStatement)
                && !customNodes.contains(node)) {
            ASTNode newNode = ASTNode.copySubtree(ast, node);
            int commentIndex = unit.lastTrailingCommentIndex(node);
            if (commentIndex >= 0) {
                String mutGenLimitComment = getLineComment(commentIndex);
                if (mutGenLimitComment.contains("//mutGenLimit 0") && !lastMutatedLines.contains(mutableLines.get(nextMutID))) {
                    ASTNode nodes[] = {getAppendToFileExpressionStatement(
                            newNode.toString().substring(0, newNode.toString().length() - 1) + 
                            " " + getLineComment(commentIndex)), newNode};
                    rewrite.replace(node, rewrite.createGroupNode(nodes), null);
                } else {
                    ASTNode nodes[] = {getAppendToFileExpressionStatement(mutIDCommentPrefix + nextMutID++ + "\n" + 
                            newNode.toString().substring(0, newNode.toString().length() - 1) + 
                            " " + getLineComment(commentIndex)), newNode};
                    rewrite.replace(node, rewrite.createGroupNode(nodes), null);
                }
            } else {
                ASTNode nodes[] = {getAppendToFileExpressionStatement(newNode.toString()), newNode};
                rewrite.replace(node, rewrite.createGroupNode(nodes), null);
            }

            return false;
        } else if (node instanceof IfStatement){

            //Ojo con los replace, no se si al reemplazar un padre, y luego reemplazar el hijo, si el hijo sera reemplazado o no, 
            // porque en el rewrite ya se cambio ese hijo por una copia, entonces quizáz no lo encuentra.

            Expression ifExpression = ((IfStatement) node).getExpression();

            Statement thenStatement = ((IfStatement) node).getThenStatement();

            Statement elseStatement = ((IfStatement) node).getElseStatement();

            ASTNode newThenNode;

            if (thenStatement instanceof Block) {
                Statement thenFirstStatement = (Statement)((Block)thenStatement).statements().get(0);
                if (!(thenFirstStatement instanceof IfStatement)
                        && !(thenFirstStatement instanceof WhileStatement)
                        && !(thenFirstStatement instanceof ForStatement)
                        && !(thenFirstStatement instanceof EnhancedForStatement)) {
                    newThenNode = ASTNode.copySubtree(ast, thenFirstStatement);
                    customNodes.add(thenFirstStatement);
                    int commentIndex = unit.lastTrailingCommentIndex(thenFirstStatement);
                    if (commentIndex >= 0) {
                        String mutGenLimitComment = getLineComment(commentIndex);
                        if (mutGenLimitComment.contains("//mutGenLimit 0") && !lastMutatedLines.contains(mutableLines.get(nextMutID))) {
                            ASTNode nodes[] = {
                                    getAppendToFileExpressionStatement("if(!(" + ifExpression.toString() + ")){throw new NoSuchElementException();}" + '\n'),
                                    getAppendToFileExpressionStatement(
                                    newThenNode.toString().substring(0, newThenNode.toString().length() - 1) + 
                                    " " + getLineComment(commentIndex)), newThenNode};
                            rewrite.replace(thenFirstStatement, rewrite.createGroupNode(nodes), null);
                        } else {
                            ASTNode nodes[] = {
                                    getAppendToFileExpressionStatement("if(!(" + ifExpression.toString() + ")){throw new NoSuchElementException();}" + '\n'),
                                    getAppendToFileExpressionStatement(mutIDCommentPrefix + nextMutID++ + "\n" + 
                                    newThenNode.toString().substring(0, newThenNode.toString().length() - 1) + 
                                    " " + getLineComment(commentIndex)), newThenNode};
                            rewrite.replace(thenFirstStatement, rewrite.createGroupNode(nodes), null);
                        }
                    } else {
                        ASTNode thenNodes[] = {
                                getAppendToFileExpressionStatement("if(!(" + ifExpression.toString() + ")){throw new NoSuchElementException();}" + '\n'), 
//                                getAppendToFileExpressionStatement("assert(" + ifExpression.toString() + ");" + '\n'), 
                                getAppendToFileExpressionStatement(newThenNode.toString()), newThenNode};
                        rewrite.replace(thenFirstStatement, rewrite.createGroupNode(thenNodes), null);
                    }
                } else {
                    rewrite.getListRewrite(thenStatement, Block.STATEMENTS_PROPERTY).insertFirst(
                            getAppendToFileExpressionStatement("if(!(" + ifExpression.toString() + ")){throw new NoSuchElementException();}" + '\n'), null);
//                            getAppendToFileExpressionStatement("assert(" + ifExpression.toString() + ");" + '\n'), null);
                    //return true;
                }
            } else if (!(thenStatement instanceof IfStatement)
                    && !(thenStatement instanceof WhileStatement)
                    && !(thenStatement instanceof ForStatement)
                    && !(thenStatement instanceof EnhancedForStatement)) {
                newThenNode = ASTNode.copySubtree(ast, thenStatement);
                customNodes.add(thenStatement);
                int commentIndex = unit.lastTrailingCommentIndex(thenStatement);
                if (commentIndex >= 0) {
                    String mutGenLimitComment = getLineComment(commentIndex);
                    if (mutGenLimitComment.contains("//mutGenLimit 0") && !lastMutatedLines.contains(mutableLines.get(nextMutID))) {
                        ASTNode nodes[] = {
                                getAppendToFileExpressionStatement("if(!(" + ifExpression.toString() + ")){throw new NoSuchElementException();}" + '\n'),
                                getAppendToFileExpressionStatement(
                                newThenNode.toString().substring(0, newThenNode.toString().length() - 1) + 
                                " " + getLineComment(commentIndex)), newThenNode};
                        rewrite.replace(thenStatement, rewrite.createGroupNode(nodes), null);
                    } else {
                        ASTNode nodes[] = {
                                getAppendToFileExpressionStatement("if(!(" + ifExpression.toString() + ")){throw new NoSuchElementException();}" + '\n'),
                                getAppendToFileExpressionStatement(mutIDCommentPrefix + nextMutID++ + "\n" + 
                                newThenNode.toString().substring(0, newThenNode.toString().length() - 1) + 
                                " " + getLineComment(commentIndex)), newThenNode};
                        rewrite.replace(thenStatement, rewrite.createGroupNode(nodes), null);
                    }
                } else {
                    ASTNode thenNodes[] = {
                            getAppendToFileExpressionStatement("if(!(" + ifExpression.toString() + ")){throw new NoSuchElementException();}" + '\n'),
//                            getAppendToFileExpressionStatement("assert(" + ifExpression.toString() + ");" + '\n'),
                            getAppendToFileExpressionStatement(newThenNode.toString()), newThenNode};
                    rewrite.replace(thenStatement, rewrite.createGroupNode(thenNodes), null);
                }
            } else {
                newThenNode = ASTNode.copySubtree(ast, thenStatement);
                customNodes.add(thenStatement);
                
                ASTNode thenNodes[] = {
                        getAppendToFileExpressionStatement("if(!(" + ifExpression.toString() + ")){throw new NoSuchElementException();}" + '\n'), newThenNode};
//                getAppendToFileExpressionStatement("assert(" + ifExpression.toString() + ");" + '\n'), newThenNode};
                rewrite.replace(thenStatement, rewrite.createGroupNode(thenNodes), null);
                //return true;
            }

            if (elseStatement != null) {
                ASTNode newElseNode;
                if (elseStatement instanceof Block) {
                    Statement elseFirstStatement = (Statement)((Block)elseStatement).statements().get(0);

                    if (!(elseFirstStatement instanceof IfStatement)
                            && !(elseFirstStatement instanceof WhileStatement)
                            && !(elseFirstStatement instanceof ForStatement)
                            && !(elseFirstStatement instanceof EnhancedForStatement)) {
                        newElseNode = ASTNode.copySubtree(ast, elseFirstStatement);
                        customNodes.add(elseFirstStatement);
                        int commentIndex = unit.lastTrailingCommentIndex(elseFirstStatement);
                        if (commentIndex >= 0) {
                            String mutGenLimitComment = getLineComment(commentIndex);
                            if (mutGenLimitComment.contains("//mutGenLimit 0") && !lastMutatedLines.contains(mutableLines.get(nextMutID))) {
                                ASTNode nodes[] = {
                                        getAppendToFileExpressionStatement("if(" + ifExpression.toString() + "){throw new NoSuchElementException();}" + '\n'), 
                                        getAppendToFileExpressionStatement(
                                        newElseNode.toString().substring(0, newElseNode.toString().length() - 1) + 
                                        " " + getLineComment(commentIndex)), newElseNode};
                                rewrite.replace(elseFirstStatement, rewrite.createGroupNode(nodes), null);
                            } else {
                                ASTNode nodes[] = {
                                        getAppendToFileExpressionStatement("if(" + ifExpression.toString() + "){throw new NoSuchElementException();}" + '\n'), 
                                        getAppendToFileExpressionStatement(mutIDCommentPrefix + nextMutID++ + "\n" + 
                                        newElseNode.toString().substring(0, newElseNode.toString().length() - 1) + 
                                        " " + getLineComment(commentIndex)), newElseNode};
                                rewrite.replace(elseFirstStatement, rewrite.createGroupNode(nodes), null);
                            }
                        } else {
                            ASTNode elseNodes[] = {
                                    getAppendToFileExpressionStatement("if(" + ifExpression.toString() + "){throw new NoSuchElementException();}" + '\n'), 
//                                    ASTNode elseNodes[] = {getAppendToFileExpressionStatement("assert(!(" + ifExpression.toString() + "));" + '\n'), 
                                    getAppendToFileExpressionStatement(newElseNode.toString()), newElseNode};

                            rewrite.replace(elseFirstStatement, rewrite.createGroupNode(elseNodes), null);
                        }
                    } else {
                        rewrite.getListRewrite(elseStatement, Block.STATEMENTS_PROPERTY).insertFirst(
                                getAppendToFileExpressionStatement("if(" + ifExpression.toString() + "){throw new NoSuchElementException();}" + '\n'), null);
//                        getAppendToFileExpressionStatement("assert(!(" + ifExpression.toString() + "));" + '\n'), null);
                        //return true;
                    }
                } else if (!(elseStatement instanceof IfStatement)
                        && !(elseStatement instanceof WhileStatement)
                        && !(elseStatement instanceof ForStatement)
                        && !(elseStatement instanceof EnhancedForStatement)) {
                    newElseNode = ASTNode.copySubtree(ast, elseStatement);
                    customNodes.add(elseStatement);
                    int commentIndex = unit.lastTrailingCommentIndex(elseStatement);
                    if (commentIndex >= 0) {
                        String mutGenLimitComment = getLineComment(commentIndex);
                        if (mutGenLimitComment.contains("//mutGenLimit 0") && !lastMutatedLines.contains(mutableLines.get(nextMutID))) {
                            ASTNode nodes[] = {
                                    getAppendToFileExpressionStatement("if(" + ifExpression.toString() + "){throw new NoSuchElementException()}" + '\n'),
                                    getAppendToFileExpressionStatement(
                                    newElseNode.toString().substring(0, newElseNode.toString().length() - 1) + 
                                    " " + getLineComment(commentIndex)), newElseNode};
                            rewrite.replace(elseStatement, rewrite.createGroupNode(nodes), null);
                        } else {
                            ASTNode nodes[] = {
                                    getAppendToFileExpressionStatement("if(" + ifExpression.toString() + "){throw new NoSuchElementException()}" + '\n'),
                                    getAppendToFileExpressionStatement(mutIDCommentPrefix + nextMutID++ + "\n" + 
                                    newElseNode.toString().substring(0, newElseNode.toString().length() - 1) + 
                                    " " + getLineComment(commentIndex)), newElseNode};
                            rewrite.replace(elseStatement, rewrite.createGroupNode(nodes), null);
                        }
                    } else {

                        ASTNode elseNodes[] = {
                                getAppendToFileExpressionStatement("if(" + ifExpression.toString() + "){throw new NoSuchElementException()}" + '\n'),
//                                getAppendToFileExpressionStatement("assert(!(" + ifExpression.toString() + "));" + '\n'),
                                getAppendToFileExpressionStatement(newElseNode.toString()), newElseNode};
                        rewrite.replace(elseStatement, rewrite.createGroupNode(elseNodes), null);
                    }
                } else {
                    newElseNode = ASTNode.copySubtree(ast, elseStatement);
                    customNodes.add(elseStatement);
                    
                    ASTNode elseNodes[] = {
                            getAppendToFileExpressionStatement("if(" + ifExpression.toString() + "){throw new NoSuchElementException()}" + '\n'), newElseNode};
//                    getAppendToFileExpressionStatement("assert(!(" + ifExpression.toString() + "));" + '\n'), newElseNode};
                    rewrite.replace(elseStatement, rewrite.createGroupNode(elseNodes), null);
                    //return true;
                }
            }
            
            return true;
        } else if (node instanceof WhileStatement) {
            Expression whileExpression = ((WhileStatement) node).getExpression();

            Statement whileBody = ((WhileStatement) node).getBody();

            ASTNode newWhileBodyFirstStatementNode;
            
            if (whileBody instanceof Block) {
                Statement whileBodyFirstStatement = (Statement)((Block)whileBody).statements().get(0);
                if (!(whileBodyFirstStatement instanceof IfStatement)
                        && !(whileBodyFirstStatement instanceof WhileStatement)
                        && !(whileBodyFirstStatement instanceof ForStatement)
                        && !(whileBodyFirstStatement instanceof EnhancedForStatement)) {
                    newWhileBodyFirstStatementNode = ASTNode.copySubtree(ast, whileBodyFirstStatement);
                    customNodes.add(whileBodyFirstStatement);
                    int commentIndex = unit.lastTrailingCommentIndex(whileBodyFirstStatement);
                    if (commentIndex >= 0) {
                        String mutGenLimitComment = getLineComment(commentIndex);
                        if (mutGenLimitComment.contains("//mutGenLimit 0") && !lastMutatedLines.contains(mutableLines.get(nextMutID))) {
                            ASTNode nodes[] = {
                                    getAppendToFileExpressionStatement("if(!(" + whileExpression.toString() + ")){throw new NoSuchElementException();}" + '\n'), 
                                    getAppendToFileExpressionStatement(
                                    newWhileBodyFirstStatementNode.toString().substring(0, newWhileBodyFirstStatementNode.toString().length() - 1) + 
                                    " " + getLineComment(commentIndex)), newWhileBodyFirstStatementNode};
                            rewrite.replace(whileBodyFirstStatement, rewrite.createGroupNode(nodes), null);
                        } else {
                            ASTNode nodes[] = {
                                    getAppendToFileExpressionStatement("if(!(" + whileExpression.toString() + ")){throw new NoSuchElementException();}" + '\n'), 
                                    getAppendToFileExpressionStatement(mutIDCommentPrefix + nextMutID++ + "\n" + 
                                    newWhileBodyFirstStatementNode.toString().substring(0, newWhileBodyFirstStatementNode.toString().length() - 1) + 
                                    " " + getLineComment(commentIndex)), newWhileBodyFirstStatementNode};
                            rewrite.replace(whileBodyFirstStatement, rewrite.createGroupNode(nodes), null);
                        }
                    } else {
                        ASTNode whileBodyFirstNodes[] = {
//                                getAppendToFileExpressionStatement("assert(" + whileExpression.toString() + ");" + '\n'), 
                                getAppendToFileExpressionStatement("if(!(" + whileExpression.toString() + ")){throw new NoSuchElementException();}" + '\n'), 
                                getAppendToFileExpressionStatement(newWhileBodyFirstStatementNode.toString()), 
                                newWhileBodyFirstStatementNode};
                        rewrite.replace(whileBodyFirstStatement, rewrite.createGroupNode(whileBodyFirstNodes), null);
                    }
                } else {
                    rewrite.getListRewrite(whileBody, Block.STATEMENTS_PROPERTY).insertFirst(
                            getAppendToFileExpressionStatement("if(!(" + whileExpression.toString() + ")){throw new NoSuchElementException();}" + '\n'), null);
//                    getAppendToFileExpressionStatement("assert(" + whileExpression.toString() + ");" + '\n'), null);
                    //return true;
                }
            } else if (!(whileBody instanceof IfStatement)
                    && !(whileBody instanceof WhileStatement)
                    && !(whileBody instanceof ForStatement)
                    && !(whileBody instanceof EnhancedForStatement)) {
                newWhileBodyFirstStatementNode = ASTNode.copySubtree(ast, whileBody);
                customNodes.add(whileBody);
                int commentIndex = unit.lastTrailingCommentIndex(whileBody);
                if (commentIndex >= 0) {
                    String mutGenLimitComment = getLineComment(commentIndex);
                    if (mutGenLimitComment.contains("//mutGenLimit 0") && !lastMutatedLines.contains(mutableLines.get(nextMutID))) {
                        ASTNode nodes[] = {
                                getAppendToFileExpressionStatement("if(!(" + whileExpression.toString() + ")){throw new NoSuchElementException();}" + '\n'),
                                getAppendToFileExpressionStatement(
                                newWhileBodyFirstStatementNode.toString().substring(0, newWhileBodyFirstStatementNode.toString().length() - 1) + 
                                " " + getLineComment(commentIndex)), newWhileBodyFirstStatementNode};
                        rewrite.replace(whileBody, rewrite.createGroupNode(nodes), null);
                    } else {
                        ASTNode nodes[] = {
                                getAppendToFileExpressionStatement("if(!(" + whileExpression.toString() + ")){throw new NoSuchElementException();}" + '\n'),
                                getAppendToFileExpressionStatement(mutIDCommentPrefix + nextMutID++ + "\n" + 
                                newWhileBodyFirstStatementNode.toString().substring(0, newWhileBodyFirstStatementNode.toString().length() - 1) + 
                                " " + getLineComment(commentIndex)), newWhileBodyFirstStatementNode};
                        rewrite.replace(whileBody, rewrite.createGroupNode(nodes), null);
                    }
                } else {
                    ASTNode whileBodyNodes[] = {
                            getAppendToFileExpressionStatement("if(!(" + whileExpression.toString() + ")){throw new NoSuchElementException();}" + '\n'),
//                            getAppendToFileExpressionStatement("assert(" + whileExpression.toString() + ");" + '\n'),
                            getAppendToFileExpressionStatement(newWhileBodyFirstStatementNode.toString()), newWhileBodyFirstStatementNode};
                    //TODO It was whileExpression instead of whileBody... maybe that's why you are looking at this ;)
                    rewrite.replace(whileBody, rewrite.createGroupNode(whileBodyNodes), null);
                }
            } else {
                newWhileBodyFirstStatementNode = ASTNode.copySubtree(ast, whileBody);
                customNodes.add(whileBody);
                
                ASTNode whileBodyNodes[] = {
                        getAppendToFileExpressionStatement("if(!(" + whileExpression.toString() + ")){throw new NoSuchElementException();}" + '\n'), newWhileBodyFirstStatementNode};
//                getAppendToFileExpressionStatement("assert(" + whileExpression.toString() + ");" + '\n'), newWhileBodyFirstStatementNode};
                rewrite.replace(whileBody, rewrite.createGroupNode(whileBodyNodes), null);
                //return true;
            }
            
            ASTNode assertNode = getAppendToFileExpressionStatement("if(" + whileExpression.toString() + "){throw new NoSuchElementException();}" + '\n');
//            ASTNode assertNode = getAppendToFileExpressionStatement("assert(!(" + whileExpression.toString() + "));" + '\n');
            rewrite.getListRewrite(node.getParent(), (ChildListPropertyDescriptor) node.getLocationInParent())
            .insertAfter(assertNode, node, null);
            
            return true;
        } else if (node instanceof ForStatement) {
            Expression forExpression = ((ForStatement) node).getExpression();

            Statement forBody = ((ForStatement) node).getBody();

            ASTNode newForBodyFirstStatementNode;
            
            if (forBody instanceof Block) {
                Statement forBodyFirstStatement = (Statement)((Block)forBody).statements().get(0);
                if (!(forBodyFirstStatement instanceof IfStatement)
                        && !(forBodyFirstStatement instanceof WhileStatement)
                        && !(forBodyFirstStatement instanceof ForStatement)
                        && !(forBodyFirstStatement instanceof EnhancedForStatement)) {
                    newForBodyFirstStatementNode = ASTNode.copySubtree(ast, forBodyFirstStatement);
                    customNodes.add(forBodyFirstStatement);
                    int commentIndex = unit.lastTrailingCommentIndex(forBodyFirstStatement);
                    if (commentIndex >= 0) {
                        String mutGenLimitComment = getLineComment(commentIndex);
                        if (mutGenLimitComment.contains("//mutGenLimit 0") && !lastMutatedLines.contains(mutableLines.get(nextMutID))) {
                            ASTNode nodes[] = {
                                    getAppendToFileExpressionStatement("if(!(" + forExpression.toString() + ")){throw new NoSuchElementException();}" + '\n'), 
                                    getAppendToFileExpressionStatement(
                                    newForBodyFirstStatementNode.toString().substring(0, newForBodyFirstStatementNode.toString().length() - 1) + 
                                    " " + getLineComment(commentIndex)), newForBodyFirstStatementNode};
                            rewrite.replace(forBodyFirstStatement, rewrite.createGroupNode(nodes), null);
                        } else {
                            ASTNode nodes[] = {
                                    getAppendToFileExpressionStatement("if(!(" + forExpression.toString() + ")){throw new NoSuchElementException();}" + '\n'), 
                                    getAppendToFileExpressionStatement(mutIDCommentPrefix + nextMutID++ + "\n" + 
                                    newForBodyFirstStatementNode.toString().substring(0, newForBodyFirstStatementNode.toString().length() - 1) + 
                                    " " + getLineComment(commentIndex)), newForBodyFirstStatementNode};
                            rewrite.replace(forBodyFirstStatement, rewrite.createGroupNode(nodes), null);
                        }
                    } else {
                        ASTNode forBodyFirstNodes[] = {
                                getAppendToFileExpressionStatement("if(!(" + forExpression.toString() + ")){throw new NoSuchElementException();}" + '\n'), 
//                                getAppendToFileExpressionStatement("assert(" + forExpression.toString() + ");" + '\n'), 
                                getAppendToFileExpressionStatement(newForBodyFirstStatementNode.toString()), 
                                newForBodyFirstStatementNode};
                        rewrite.replace(forBodyFirstStatement, rewrite.createGroupNode(forBodyFirstNodes), null);
                    }
                } else {
                    rewrite.getListRewrite(forBody, Block.STATEMENTS_PROPERTY).insertFirst(
                            getAppendToFileExpressionStatement("if(!(" + forExpression.toString() + ")){throw new NoSuchElementException();}" + '\n'), null);
//                    getAppendToFileExpressionStatement("assert(" + forExpression.toString() + ");" + '\n'), null);
                    //return true;
                }
            } else if (!(forBody instanceof IfStatement)
                    && !(forBody instanceof WhileStatement)
                    && !(forBody instanceof ForStatement)
                    && !(forBody instanceof EnhancedForStatement)) {
                newForBodyFirstStatementNode = ASTNode.copySubtree(ast, forBody);
                customNodes.add(forBody);
                int commentIndex = unit.lastTrailingCommentIndex(forBody);
                if (commentIndex >= 0) {
                    String mutGenLimitComment = getLineComment(commentIndex);
                    if (mutGenLimitComment.contains("//mutGenLimit 0") && !lastMutatedLines.contains(mutableLines.get(nextMutID))) {
                        ASTNode nodes[] = {
                                getAppendToFileExpressionStatement("if(!(" + forExpression.toString() + ")){throw new NoSuchElementException();}" + '\n'),
                                getAppendToFileExpressionStatement(
                                newForBodyFirstStatementNode.toString().substring(0, newForBodyFirstStatementNode.toString().length() - 1) + 
                                " " + getLineComment(commentIndex)), newForBodyFirstStatementNode};
                        rewrite.replace(forBody, rewrite.createGroupNode(nodes), null);
                    } else {
                        ASTNode nodes[] = {
                                getAppendToFileExpressionStatement("if(!(" + forExpression.toString() + ")){throw new NoSuchElementException();}" + '\n'),
                                getAppendToFileExpressionStatement(mutIDCommentPrefix + nextMutID++ + "\n" + 
                                newForBodyFirstStatementNode.toString().substring(0, newForBodyFirstStatementNode.toString().length() - 1) + 
                                " " + getLineComment(commentIndex)), newForBodyFirstStatementNode};
                        rewrite.replace(forBody, rewrite.createGroupNode(nodes), null);
                    }
                } else {
                    ASTNode forBodyNodes[] = {
                            getAppendToFileExpressionStatement("if(!(" + forExpression.toString() + ")){throw new NoSuchElementException();}" + '\n'),
//                            getAppendToFileExpressionStatement("assert(" + forExpression.toString() + ");" + '\n'),
                            getAppendToFileExpressionStatement(newForBodyFirstStatementNode.toString()), newForBodyFirstStatementNode};
                    //TODO same as while TODO
                    rewrite.replace(forBody, rewrite.createGroupNode(forBodyNodes), null);
                }
            } else {
                newForBodyFirstStatementNode = ASTNode.copySubtree(ast, forBody);
                customNodes.add(forBody);
                
                ASTNode forBodyNodes[] = {
                        getAppendToFileExpressionStatement("if(!(" + forExpression.toString() + ")){throw new NoSuchElementException();}" + '\n'), newForBodyFirstStatementNode};
//                        getAppendToFileExpressionStatement("assert(" + forExpression.toString() + ");" + '\n'), newForBodyFirstStatementNode};
                rewrite.replace(forBody, rewrite.createGroupNode(forBodyNodes), null);
                //return true;
            }
            
            ASTNode assertNode = getAppendToFileExpressionStatement("if(" + forExpression.toString() + "){throw new NoSuchElementException();}" + '\n');
//            ASTNode assertNode = getAppendToFileExpressionStatement("assert(!(" + forExpression.toString() + "));" + '\n');
            rewrite.getListRewrite(node.getParent(), (ChildListPropertyDescriptor) node.getLocationInParent())
            .insertAfter(assertNode, node, null);
            
            return true;
        } else if (node instanceof EnhancedForStatement) {
            Expression forExpression = ((EnhancedForStatement) node).getExpression();

            Statement forBody = ((EnhancedForStatement) node).getBody();

            ASTNode newForBodyFirstStatementNode;
            
            if (forBody instanceof Block) {
                Statement forBodyFirstStatement = (Statement)((Block)forBody).statements().get(0);
                if (!(forBodyFirstStatement instanceof IfStatement)
                        && !(forBodyFirstStatement instanceof WhileStatement)
                        && !(forBodyFirstStatement instanceof ForStatement)
                        && !(forBodyFirstStatement instanceof EnhancedForStatement)) {
                    newForBodyFirstStatementNode = ASTNode.copySubtree(ast, forBodyFirstStatement);
                    customNodes.add(forBodyFirstStatement);
                    int commentIndex = unit.lastTrailingCommentIndex(forBodyFirstStatement);
                    if (commentIndex >= 0) {
                        String mutGenLimitComment = getLineComment(commentIndex);
                        if (mutGenLimitComment.contains("//mutGenLimit 0") && !lastMutatedLines.contains(mutableLines.get(nextMutID))) {
                            ASTNode nodes[] = {
                                    getAppendToFileExpressionStatement("if(!(" + forExpression.toString() + ")){throw new NoSuchElementException();}" + '\n'), 
                                    getAppendToFileExpressionStatement(
                                    newForBodyFirstStatementNode.toString().substring(0, newForBodyFirstStatementNode.toString().length() - 1) + 
                                    " " + getLineComment(commentIndex)), newForBodyFirstStatementNode};
                            rewrite.replace(forBodyFirstStatement, rewrite.createGroupNode(nodes), null);
                        } else {
                            ASTNode nodes[] = {
                                    getAppendToFileExpressionStatement("if(!(" + forExpression.toString() + ")){throw new NoSuchElementException();}" + '\n'), 
                                    getAppendToFileExpressionStatement(mutIDCommentPrefix + nextMutID++ + "\n" + 
                                    newForBodyFirstStatementNode.toString().substring(0, newForBodyFirstStatementNode.toString().length() - 1) + 
                                    " " + getLineComment(commentIndex)), newForBodyFirstStatementNode};
                            rewrite.replace(forBodyFirstStatement, rewrite.createGroupNode(nodes), null);
                        }
                    } else {
                        ASTNode forBodyFirstNodes[] = {
//                                getAppendToFileExpressionStatement("assert(" + forExpression.toString() + ");" + '\n'), 
                                getAppendToFileExpressionStatement("if(!(" + forExpression.toString() + ")){throw new NoSuchElementException();}" + '\n'), 
                                getAppendToFileExpressionStatement(newForBodyFirstStatementNode.toString()), 
                                newForBodyFirstStatementNode};
                        rewrite.replace(forBodyFirstStatement, rewrite.createGroupNode(forBodyFirstNodes), null);
                    }
                } else {
                    rewrite.getListRewrite(forBody, Block.STATEMENTS_PROPERTY).insertFirst(
//                            getAppendToFileExpressionStatement("assert(" + forExpression.toString() + ");" + '\n'), null);
                              getAppendToFileExpressionStatement("if(!(" + forExpression.toString() + ")){throw new RuntimeExcpetion();}" + '\n'), null);
                    //return true;
                }
            } else if (!(forBody instanceof IfStatement)
                    && !(forBody instanceof WhileStatement)
                    && !(forBody instanceof ForStatement)
                    && !(forBody instanceof EnhancedForStatement)) {
                newForBodyFirstStatementNode = ASTNode.copySubtree(ast, forBody);
                customNodes.add(forBody);
                int commentIndex = unit.lastTrailingCommentIndex(forBody);
                if (commentIndex >= 0) {
                    String mutGenLimitComment = getLineComment(commentIndex);
                    if (mutGenLimitComment.contains("//mutGenLimit 0") && !lastMutatedLines.contains(mutableLines.get(nextMutID))) {
                        ASTNode nodes[] = {
                                getAppendToFileExpressionStatement("if(!(" + forExpression.toString() + ")){throw new NoSuchElementException();}" + '\n'),
                                getAppendToFileExpressionStatement(
                                newForBodyFirstStatementNode.toString().substring(0, newForBodyFirstStatementNode.toString().length() - 1) + 
                                " " + getLineComment(commentIndex)), newForBodyFirstStatementNode};
                        rewrite.replace(forBody, rewrite.createGroupNode(nodes), null);
                    } else {
                        ASTNode nodes[] = {
                                getAppendToFileExpressionStatement("if(!(" + forExpression.toString() + ")){throw new NoSuchElementException();}" + '\n'),
                                getAppendToFileExpressionStatement(mutIDCommentPrefix + nextMutID++ + "\n" + 
                                newForBodyFirstStatementNode.toString().substring(0, newForBodyFirstStatementNode.toString().length() - 1) + 
                                " " + getLineComment(commentIndex)), newForBodyFirstStatementNode};
                        rewrite.replace(forBody, rewrite.createGroupNode(nodes), null);
                    }
                } else {
                    ASTNode forBodyNodes[] = {
//                            getAppendToFileExpressionStatement("assert(" + forExpression.toString() + ");" + '\n'),
                            getAppendToFileExpressionStatement("if(!(" + forExpression.toString() + ")){throw new NoSuchElementException();}" + '\n'),
                            getAppendToFileExpressionStatement(newForBodyFirstStatementNode.toString()), newForBodyFirstStatementNode};
                    rewrite.replace(forBody, rewrite.createGroupNode(forBodyNodes), null);
                }
            } else {
                newForBodyFirstStatementNode = ASTNode.copySubtree(ast, forBody);
                customNodes.add(forBody);
                
                ASTNode forBodyNodes[] = {
                        getAppendToFileExpressionStatement("if(!(" + forExpression.toString() + ")){throw new NoSuchElementException()}" + '\n'), newForBodyFirstStatementNode};
//                        getAppendToFileExpressionStatement("assert(" + forExpression.toString() + ");" + '\n'), newForBodyFirstStatementNode};
                rewrite.replace(forBody, rewrite.createGroupNode(forBodyNodes), null);
                //return true;
            }
            
//            ASTNode assertNode = getAppendToFileExpressionStatement("assert(!(" + forExpression.toString() + "));" + '\n');
            ASTNode assertNode = getAppendToFileExpressionStatement("if(" + forExpression.toString() + "){throw new NoSuchElementException();}" + '\n');
            rewrite.getListRewrite(node.getParent(), (ChildListPropertyDescriptor) node.getLocationInParent())
            .insertAfter(assertNode, node, null);
 
            return true;
        } else if (node instanceof ReturnStatement) {
            ASTNode newNode = ASTNode.copySubtree(ast, node);

            int commentIndex = unit.lastTrailingCommentIndex(node);
            if (commentIndex >= 0) {
                String mutGenLimitComment = getLineComment(commentIndex);
                if (mutGenLimitComment.contains("//mutGenLimit 0") && !lastMutatedLines.contains(mutableLines.get(nextMutID))) {
                    ASTNode nodes[] = {getAppendToFileExpressionStatement(
                            newNode.toString().substring(0, newNode.toString().length() - 1) + 
                            " " + getLineComment(commentIndex)), newNode};
                    rewrite.replace(node, rewrite.createGroupNode(nodes), null);
                } else {
                    ASTNode nodes[] = {getAppendToFileExpressionStatement(mutIDCommentPrefix + nextMutID++ + "\n" + 
                            newNode.toString().substring(0, newNode.toString().length() - 1) + 
                            " " + getLineComment(commentIndex)), newNode};
                    rewrite.replace(node, rewrite.createGroupNode(nodes), null);
                }
            } else {
                ASTNode nodes[] = {getAppendToFileExpressionStatement(newNode.toString()), newNode};
                rewrite.replace(node, rewrite.createGroupNode(nodes), null);
            }            
            return true;

        } else if (node instanceof MethodDeclaration) {
            MethodDeclaration newNode = (MethodDeclaration) node;

            if (newNode.getName().getFullyQualifiedName().contains(wrapper.getMethod())) {
                @SuppressWarnings("rawtypes")
                List statements = newNode.getBody().statements();

                Statement firstStatement = (Statement) statements.get(0);

                ASTNode newFirstStatement = ASTNode.copySubtree(ast, firstStatement);

                @SuppressWarnings("unused")
                TryStatement es, es3;

//                ASTNode nodes[] = {
//                        es = getWriteToFileExpressionStatement(newNode.toString().substring(0, newNode.toString().indexOf('\n'))), 
//                                es3 = getAppendToFileExpressionStatement(newFirstStatement.toString()), newFirstStatement};
                ASTNode nodes[] = {es3 = getAppendToFileExpressionStatement(newFirstStatement.toString()), newFirstStatement};

//                customNodes.add(es);
                customNodes.add(es3);
                customNodes.add(firstStatement);

                rewrite.replace(firstStatement, rewrite.createGroupNode(nodes), null);

                Statement lastStatement = (Statement) statements.get(statements.size() - 1);

                ASTNode newLastStatement = ASTNode.copySubtree(ast, lastStatement);

                @SuppressWarnings("unused")
                TryStatement es2, es4;

//                ASTNode lastNodes[] = {es4 = getAppendToFileExpressionStatement(newLastStatement.toString()), 
//                        es2 = getAppendToFileExpressionStatement("}"), newLastStatement};

                ASTNode lastNodes[] = {es4 = getAppendToFileExpressionStatement(newLastStatement.toString()), newLastStatement};

//                customNodes.add(es2);
                customNodes.add(es4);
                customNodes.add(lastStatement);

                rewrite.replace(lastStatement, rewrite.createGroupNode(lastNodes), null);
                return true;
            } else {
                return super.preVisit2(node);
            }
        }

        return super.preVisit2(node);
    }

    public ASTRewrite getRewrite() {
        return rewrite;
    }

    @SuppressWarnings("unchecked")
    public TryStatement getAppendToFileExpressionStatement(String value) {
        
        StringLiteral fileNameLiteral1 = ast.newStringLiteral();
        fileNameLiteral1.setLiteralValue(seqFileName + "_" + methodName);
        StringLiteral fileNameLiteral2 = ast.newStringLiteral();
        fileNameLiteral2.setLiteralValue(seqFileName + "_" + methodName);

        MethodInvocation wtfMI = ast.newMethodInvocation();
        StringLiteral methodLiteral = ast.newStringLiteral();
        methodLiteral.setLiteralValue(value);

        wtfMI.arguments().add(0, methodLiteral);
        wtfMI.arguments().add(0, fileNameLiteral1);

        wtfMI.setExpression(ast.newSimpleName("FileUtils"));
        wtfMI.setName(ast.newSimpleName("appendToFile"));

        ExpressionStatement es = ast.newExpressionStatement(wtfMI);
        
        TryStatement tryState = ast.newTryStatement();
        Block block = ast.newBlock();
        block.statements().add(es);
        tryState.setBody(block);
        
        CatchClause catchClause = ast.newCatchClause();
        SingleVariableDeclaration exception = ast.newSingleVariableDeclaration();
        exception.setType(ast.newSimpleType(ast.newSimpleName("IOException")));
        exception.setName(ast.newSimpleName("ioexception"));
        catchClause.setException(exception);
        tryState.catchClauses().add(catchClause);

        return tryState;
    }
    
    @SuppressWarnings("unchecked")
    public TryStatement getWriteToFileExpressionStatement(String value) {
        
        StringLiteral fileNameLiteral1 = ast.newStringLiteral();
        fileNameLiteral1.setLiteralValue(seqFileName + "_" + methodName);
        StringLiteral fileNameLiteral2 = ast.newStringLiteral();
        fileNameLiteral2.setLiteralValue(seqFileName + "_" + methodName);

        MethodInvocation wtfMI = ast.newMethodInvocation();
        StringLiteral methodLiteral = ast.newStringLiteral();
        methodLiteral.setLiteralValue(value);

        wtfMI.arguments().add(0, methodLiteral);
        wtfMI.arguments().add(0, fileNameLiteral1);

        wtfMI.setExpression(ast.newSimpleName("FileUtils"));
        wtfMI.setName(ast.newSimpleName("writeToFile"));

        ExpressionStatement es = ast.newExpressionStatement(wtfMI);
        
        TryStatement tryState = ast.newTryStatement();
        Block block = ast.newBlock();
        block.statements().add(es);
        tryState.setBody(block);
        
        CatchClause catchClause = ast.newCatchClause();
        SingleVariableDeclaration exception = ast.newSingleVariableDeclaration();
        exception.setType(ast.newSimpleType(ast.newSimpleName("IOException")));
        exception.setName(ast.newSimpleName("ioexception"));
        catchClause.setException(exception);
        tryState.catchClauses().add(catchClause);

        return tryState;
    }
    
}
