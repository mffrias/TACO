package ar.edu.taco.stryker.api.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.log4j.Logger;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.BooleanLiteral;
import org.eclipse.jdt.core.dom.Comment;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.EnhancedForStatement;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.LineComment;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.NullLiteral;
import org.eclipse.jdt.core.dom.NumberLiteral;
import org.eclipse.jdt.core.dom.PostfixExpression;
import org.eclipse.jdt.core.dom.PrefixExpression;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.core.dom.WhileStatement;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import ar.edu.taco.stryker.api.impl.input.DarwinistInput;

/**
 * JDT visitor that collects, for the method under repair, the expressions that variabilization
 * may replace by fresh variables, grouped by the {@code mutID} of each statement. The mutID is
 * read from the statement's {@code //mutGenLimit <k> mutID <n>} comment, written by
 * {@link StrykerJavaFileInstrumenter#insertMutIDs(DarwinistInput)}; statements with a negative
 * mutID (no mutation budget left) are skipped. The visitor does not rewrite anything:
 * {@link #buildVariablizationData()} hands the collected map to a {@link VariablizationData},
 * which performs the replacements. The map layout is described there; despite its name,
 * {@code rhsExpressions} holds both sides.
 *
 * <p>What is collected, by statement kind:
 * <ul>
 * <li>assignment: the right-hand side (typed with the assignment's type, or {@code boolean} for
 * boolean literals). If the left-hand side is {@code t.f} (a {@code QualifiedName} or
 * {@code FieldAccess}), the qualifier {@code t} is also collected as a left-hand-side term,
 * matching the thesis's {@code LHSVar.f = RHSVar}. A plain variable on the left-hand side is not
 * collected;</li>
 * <li>prefix or postfix expression statement ({@code i++}): its operand, so the statement becomes,
 * e.g., {@code customvar_0++} and no longer updates the original variable;</li>
 * <li>local variable declaration: the initializer of its first fragment (typed with the declared
 * type);</li>
 * <li>{@code return e}: {@code e}, typed with the method's return type;</li>
 * <li>{@code if} condition: collected as a {@code boolean} right-hand side under the mutID of the
 * comment(s) located between the {@code if} and the first statement of its then-branch;</li>
 * <li>any other expression statement (e.g. a method call): stored as a right-hand side marked as
 * not variabilizable, so variabilization stops when it reaches it.</li>
 * </ul>
 * Loop conditions are not collected. The first statement of each branch of an {@code if}, and of
 * each loop body, is processed when its parent is visited and remembered in {@code customNodes};
 * the first branch of {@link #preVisit2(ASTNode)} then skips it. Return statements, however, are
 * handled by a branch that does not check {@code customNodes}, so a return statement in that
 * position appears to be collected twice (its expression would then get two fresh variables).
 *
 * <p>The {@code stillFatherable} value stored with each new mutID entry is "sticky": it becomes
 * {@code true} the first time an entry is created for a statement whose {@code mutGenLimit} is
 * not 0 and either is not 1 or belongs to a line in {@code getLastMutatedLines()}, and it is never
 * reset during the visit. These tests are substring matches, so for example
 * {@code mutGenLimit 12} counts as 1.
 */
public class StrykerVariablizerVisitor extends ASTVisitor {

    private static Logger log = Logger.getLogger(StrykerVariablizerVisitor.class);

    private final CompilationUnit unit;
    private final String source;
    private String methodName;
    /** Only used to reach the AST for resolving {@code boolean}; nothing is recorded in it. */
    private final ASTRewrite rewrite;
    /** Statements already processed as the first statement of a branch or loop body. */
    private final Set<ASTNode> customNodes = Sets.newHashSet();
    private static final String mutIDCommentPrefix = "mutID ";
    private static final String mutGenLimitPrefix = "mutGenLimit ";

    /** Sticky flag copied into each new mutID entry; see the class comment. */
    private boolean stillFatherable = false;
    /** Last visited method whose name contains {@link #methodName}; receives the fresh parameters. */
    private MethodDeclaration method = null;
    /** Collected data per mutID (both sides, despite the name); layout in {@link VariablizationData}. */
    private Map<Integer, MutablePair<MutablePair<MutablePair<ITypeBinding, ITypeBinding>, Boolean>, MutablePair<MutablePair<List<Expression>, Boolean>, MutablePair<List<Expression>, Boolean>>>> rhsExpressions = Maps.newTreeMap();
    private DarwinistInput input;

    public StrykerVariablizerVisitor(final DarwinistInput input, CompilationUnit unit, String source, final AST ast) {
        super();
        this.unit = unit;
        this.source = source;
        this.rewrite = ASTRewrite.create(ast);
        this.input = input;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    /** Zero-based line of {@code node}. Not called anywhere in {@code src/} or {@code unittest/}. */
    public static int getLineNumber(CompilationUnit compilationUnit, ASTNode node) {
        return compilationUnit.getLineNumber(node.getStartPosition()) - 1;
    }

    /**
     * Returns the text of the comment at {@code commentIndex} in the unit's comment list or, if it
     * does not contain {@code "mutGenLimit "}, of the closest preceding comment that does, followed
     * by a newline. A statement whose trailing comment is not a {@code mutGenLimit} comment is
     * therefore attributed to the previous annotated line. Assumes the comments it visits are
     * {@link LineComment}s; if no annotated comment precedes the index, the list access fails.
     */
    public String getLineComment(int commentIndex) {
        LineComment lineCommentNode;
        //Tiene comentario el statement, potencialmente sea el de mutacion que necesito
        String lineComment = "";
        boolean flag = true;
        while (flag) {
            lineCommentNode = ((LineComment) unit.getCommentList().get(commentIndex));
            lineComment = source.substring(lineCommentNode.getStartPosition(), 
                    lineCommentNode.getStartPosition() + lineCommentNode.getLength());
            if (!lineComment.contains(mutGenLimitPrefix)) {
                --commentIndex;
            } else {
                break;
            }
        }
        lineComment += '\n';
        return lineComment;
    }

    /**
     * @return a {@link VariablizationData} over this visitor's source, unit, recorded target
     *         method and collected expressions
     */
    public VariablizationData buildVariablizationData() {
        return new VariablizationData(source, unit, method, rhsExpressions); 
    }

    /**
     * Collects the returned expression of a mutable {@code return} statement as a right-hand side
     * typed with the enclosing method's return type.
     */
    public void processReturnNode(ReturnStatement statement) {

        String mutIDComment = getLineComment(unit.lastTrailingCommentIndex(statement));

        int mutIDIndex = mutIDComment.indexOf("mutID") + 6;
        String mutIDString = mutIDComment.substring(mutIDIndex, mutIDComment.length() - 1);

        Integer mutIDNumber = Integer.valueOf(mutIDString);

        if (mutIDNumber < 0) {
            return;
        }
        
        Expression expression = statement.getExpression();
        if (rhsExpressions.containsKey(mutIDNumber) 
                && rhsExpressions.get(mutIDNumber).getRight() != null 
                && rhsExpressions.get(mutIDNumber).getRight().getRight() != null) {
            rhsExpressions.get(mutIDNumber).getRight().getRight().getLeft().add(expression);
        } else {
            String mutGenLimit = getLineComment(unit.lastTrailingCommentIndex(statement));
            if (!mutGenLimit.contains(mutGenLimitPrefix + 0) &&  
                    (!mutGenLimit.contains(mutGenLimitPrefix + 1) 
                            || input.getFeedback().getLastMutatedLines().contains(input.getFeedback().getMutableLines().get(mutIDNumber - 1)))) {
                stillFatherable = true;
            }

            ASTNode parentNode = statement.getParent();
            while (parentNode.getNodeType() != ASTNode.METHOD_DECLARATION) {
                parentNode = parentNode.getParent();
            }

            MethodDeclaration md = (MethodDeclaration) parentNode;
            ITypeBinding binding = md.resolveBinding().getReturnType();

            MutablePair<MutablePair<MutablePair<ITypeBinding, ITypeBinding>, Boolean>, MutablePair<MutablePair<List<Expression>, Boolean>, MutablePair<List<Expression>, Boolean>>> outerPair = 
                    rhsExpressions.containsKey(mutIDNumber) ? rhsExpressions.get(mutIDNumber) : 
                        new MutablePair<MutablePair<MutablePair<ITypeBinding, ITypeBinding>,Boolean>, MutablePair<MutablePair<List<Expression>, Boolean>, MutablePair<List<Expression>, Boolean>>>(
                                new MutablePair<MutablePair<ITypeBinding, ITypeBinding>, Boolean>(), new MutablePair<MutablePair<List<Expression>, Boolean>, MutablePair<List<Expression>, Boolean>>());
                    MutablePair<MutablePair<List<Expression>, Boolean>, MutablePair<List<Expression>, Boolean>> expressionsPair = outerPair.getRight() == null ? 
                            new MutablePair<MutablePair<List<Expression>, Boolean>, MutablePair<List<Expression>, Boolean>>() : outerPair.getRight();
                            MutablePair<MutablePair<ITypeBinding, ITypeBinding>, Boolean> bindingPair = outerPair.getLeft() == null ? new MutablePair<MutablePair<ITypeBinding, ITypeBinding>, Boolean>() : outerPair.getLeft();
                            outerPair.setLeft(bindingPair);
                            outerPair.setRight(expressionsPair);
                            MutablePair<List<Expression>, Boolean> expressionsInnerPair = expressionsPair.getRight() == null ? new MutablePair<List<Expression>, Boolean>() : expressionsPair.getRight();
                            List<Expression> expressions = expressionsInnerPair.getLeft();
                            Boolean variablizable = expressionsInnerPair.getRight() == null ? true : expressionsInnerPair.getRight();
                            expressionsInnerPair.setRight(variablizable);
                            if (expressions == null) { 
                                expressions = Lists.newArrayList();
                                expressionsInnerPair.setLeft(expressions);
                            }
                            expressions.add(expression);
                            expressionsPair.setRight(expressionsInnerPair);
                            MutablePair<ITypeBinding, ITypeBinding> typeBindings = new MutablePair<ITypeBinding, ITypeBinding>();
                            typeBindings.setRight(binding);
                            bindingPair.setLeft(typeBindings);
                            bindingPair.setRight(stillFatherable);
                            rhsExpressions.put(mutIDNumber, outerPair);
        }
    }

    /**
     * Collects the variabilizable parts of a mutable statement other than {@code return}:
     * assignment, prefix/postfix expression, any other expression statement, or local variable
     * declaration (see the class comment). Logs an error for any other statement kind and when a
     * declaration has more than one fragment.
     */
    public void processNode(Statement statement) {
        String mutIDComment = getLineComment(unit.lastTrailingCommentIndex(statement));

        int mutIDIndex = mutIDComment.indexOf(mutIDCommentPrefix) + 6;
        String mutIDString = mutIDComment.substring(mutIDIndex, mutIDComment.length() - 1);

        Integer mutIDNumber = Integer.valueOf(mutIDString);

        if (mutIDNumber < 0) {
            return;
        }

        if (statement instanceof ExpressionStatement) {
            // to iterate through methods
            //Es expression statement y tiene comentario
            Expression expression = ((ExpressionStatement) statement).getExpression();
            if (expression instanceof Assignment) {
                //Es una asignacion
                //Tomar el id de mutante

                Assignment assignment = (Assignment) expression;

                ///LHS de la asignacion
                Expression lhs = assignment.getLeftHandSide();
                if (lhs instanceof QualifiedName || lhs instanceof FieldAccess /*&& !visitor.getLineComment(unit.lastTrailingCommentIndex(statement)).contains("mutGenLimit 1")*/) {
                    //Es un FieldAccess, se variabiliza para PRVOL
                    Expression term = lhs instanceof QualifiedName ? ((QualifiedName)lhs).getQualifier() : ((FieldAccess)lhs).getExpression();
                    ITypeBinding binding = term.resolveTypeBinding();
                    if (rhsExpressions.containsKey(mutIDNumber) 
                            && rhsExpressions.get(mutIDNumber).getRight() != null 
                            && rhsExpressions.get(mutIDNumber).getRight().getLeft() != null) {
                        MutablePair<MutablePair<MutablePair<ITypeBinding, ITypeBinding>, Boolean>, MutablePair<MutablePair<List<Expression>, Boolean>, MutablePair<List<Expression>, Boolean>>> outerPair = rhsExpressions.get(mutIDNumber);
                        outerPair.getRight().getLeft().getLeft().add(term);
                        if (outerPair.getLeft().getLeft().getLeft() == null) {
                            outerPair.getLeft().getLeft().setLeft(binding);
                        }
                    } else {
                        String mutGenLimit = getLineComment(unit.lastTrailingCommentIndex(statement));
                        if (!mutGenLimit.contains(mutGenLimitPrefix + 0) &&  
                                (!mutGenLimit.contains(mutGenLimitPrefix + 1) 
                                        || input.getFeedback().getLastMutatedLines().contains(input.getFeedback().getMutableLines().get(mutIDNumber - 1)))) {
                            stillFatherable = true;
                        }

                        MutablePair<MutablePair<MutablePair<ITypeBinding, ITypeBinding>, Boolean>, MutablePair<MutablePair<List<Expression>, Boolean>, MutablePair<List<Expression>, Boolean>>> outerPair = 
                                rhsExpressions.containsKey(mutIDNumber) ? rhsExpressions.get(mutIDNumber) : 
                                    new MutablePair<MutablePair<MutablePair<ITypeBinding, ITypeBinding>,Boolean>, MutablePair<MutablePair<List<Expression>, Boolean>, MutablePair<List<Expression>, Boolean>>>(
                                            new MutablePair<MutablePair<ITypeBinding, ITypeBinding>, Boolean>(), new MutablePair<MutablePair<List<Expression>, Boolean>, MutablePair<List<Expression>, Boolean>>());
                                MutablePair<MutablePair<List<Expression>, Boolean>, MutablePair<List<Expression>, Boolean>> expressionsPair = outerPair.getRight() == null ? 
                                        new MutablePair<MutablePair<List<Expression>, Boolean>, MutablePair<List<Expression>, Boolean>>() : outerPair.getRight();
                                        MutablePair<MutablePair<ITypeBinding, ITypeBinding>, Boolean> bindingPair = outerPair.getLeft() == null ? new MutablePair<MutablePair<ITypeBinding, ITypeBinding>, Boolean>() : outerPair.getLeft();
                                        outerPair.setLeft(bindingPair);
                                        outerPair.setRight(expressionsPair);
                                        MutablePair<List<Expression>, Boolean> expressionsInnerPair = expressionsPair.getLeft() == null ? new MutablePair<List<Expression>, Boolean>() : expressionsPair.getLeft();
                                        List<Expression> expressions = expressionsInnerPair.getLeft();
                                        Boolean variablizable = expressionsInnerPair.getRight() == null ? true : expressionsInnerPair.getRight();
                                        expressionsInnerPair.setRight(variablizable);
                                        if (expressions == null) { 
                                            expressions = Lists.newArrayList();
                                            expressionsInnerPair.setLeft(expressions);
                                        }
                                        expressions.add(term);
                                        expressionsPair.setLeft(expressionsInnerPair);
                                        MutablePair<ITypeBinding, ITypeBinding> typeBindings = bindingPair.getLeft();
                                        if (typeBindings == null) {
                                            typeBindings = new MutablePair<ITypeBinding, ITypeBinding>();
                                        }
                                        typeBindings.setLeft(binding);
                                        bindingPair.setLeft(typeBindings);
                                        bindingPair.setRight(stillFatherable);
                                        rhsExpressions.put(mutIDNumber, outerPair);
                    }
                } else {
                    //                    if (rhsExpressions.containsKey(mutIDNumber) 
                    //                            && rhsExpressions.get(mutIDNumber).getRight() != null 
                    //                            && rhsExpressions.get(mutIDNumber).getRight().getLeft() != null) {
                    //                        rhsExpressions.get(mutIDNumber).getRight().getLeft().getLeft().add(lhs);
                    //                    } else {
                    //                        String mutGenLimit = getLineComment(unit.lastTrailingCommentIndex(statement));
                    //                        if (!mutGenLimit.contains(mutGenLimitPrefix + 0) &&  
                    //                                (!mutGenLimit.contains(mutGenLimitPrefix + 1) 
                    //                                        || input.getFeedback().getLastMutatedLines().contains(input.getFeedback().getMutableLines().get(mutIDNumber - 1)))) {
                    //                            stillFatherable = true;
                    //                        }
                    //
                    //                        ITypeBinding binding = assignment.resolveTypeBinding();
                    //                        MutablePair<MutablePair<ITypeBinding, Boolean>, MutablePair<MutablePair<List<Expression>, Boolean>, MutablePair<List<Expression>, Boolean>>> outerPair = 
                    //                                rhsExpressions.containsKey(mutIDNumber) ? rhsExpressions.get(mutIDNumber) : 
                    //                                    new MutablePair<MutablePair<ITypeBinding,Boolean>, MutablePair<MutablePair<List<Expression>, Boolean>, MutablePair<List<Expression>, Boolean>>>(
                    //                                            new MutablePair<ITypeBinding, Boolean>(), new MutablePair<MutablePair<List<Expression>, Boolean>, MutablePair<List<Expression>, Boolean>>());
                    //                                MutablePair<MutablePair<List<Expression>, Boolean>, MutablePair<List<Expression>, Boolean>> expressionsPair = outerPair.getRight() == null ? 
                    //                                        new MutablePair<MutablePair<List<Expression>, Boolean>, MutablePair<List<Expression>, Boolean>>() : outerPair.getRight();
                    //                                        MutablePair<ITypeBinding, Boolean> bindingPair = outerPair.getLeft() == null ? new MutablePair<ITypeBinding, Boolean>() : outerPair.getLeft();
                    //                                        outerPair.setLeft(bindingPair);
                    //                                        outerPair.setRight(expressionsPair);
                    //                                        MutablePair<List<Expression>, Boolean> expressionsInnerPair = expressionsPair.getLeft() == null ? new MutablePair<List<Expression>, Boolean>() : expressionsPair.getLeft();
                    //                                        List<Expression> expressions = expressionsInnerPair.getLeft();
                    //                                        Boolean variablizable = expressionsInnerPair.getRight() == null ? false : expressionsInnerPair.getRight();
                    //                                        expressionsInnerPair.setRight(variablizable);
                    //                                        if (expressions == null) { 
                    //                                            expressions = Lists.newArrayList();
                    //                                            expressionsInnerPair.setLeft(expressions);
                    //                                        }
                    //                                        expressions.add(lhs);
                    //                                        expressionsPair.setLeft(expressionsInnerPair);
                    //                                        bindingPair.setLeft(binding);
                    //                                        bindingPair.setRight(stillFatherable);
                    //                                        rhsExpressions.put(mutIDNumber, outerPair);
                    //                    }
                }

                ///RHS de la asignacion
                Expression rhs = assignment.getRightHandSide();
                if (rhs instanceof NullLiteral) {
                    ITypeBinding binding = assignment.resolveTypeBinding();
                    if (rhsExpressions.containsKey(mutIDNumber) 
                            && rhsExpressions.get(mutIDNumber).getRight() != null 
                            && rhsExpressions.get(mutIDNumber).getRight().getRight() != null) {
                        MutablePair<MutablePair<MutablePair<ITypeBinding, ITypeBinding>, Boolean>, MutablePair<MutablePair<List<Expression>, Boolean>, MutablePair<List<Expression>, Boolean>>> outerPair = rhsExpressions.get(mutIDNumber);
                        outerPair.getRight().getRight().getLeft().add(rhs);
                        if (outerPair.getLeft().getLeft().getRight() == null) {
                            outerPair.getLeft().getLeft().setRight(binding);
                        }
                    } else {
                        String mutGenLimit = getLineComment(unit.lastTrailingCommentIndex(statement));
                        if (!mutGenLimit.contains(mutGenLimitPrefix + 0) &&  
                                (!mutGenLimit.contains(mutGenLimitPrefix + 1) 
                                        || input.getFeedback().getLastMutatedLines().contains(input.getFeedback().getMutableLines().get(mutIDNumber - 1)))) {
                            stillFatherable = true;
                        }

                        MutablePair<MutablePair<MutablePair<ITypeBinding, ITypeBinding>, Boolean>, MutablePair<MutablePair<List<Expression>, Boolean>, MutablePair<List<Expression>, Boolean>>> outerPair = 
                                rhsExpressions.containsKey(mutIDNumber) ? rhsExpressions.get(mutIDNumber) : 
                                    new MutablePair<MutablePair<MutablePair<ITypeBinding, ITypeBinding>,Boolean>, MutablePair<MutablePair<List<Expression>, Boolean>, MutablePair<List<Expression>, Boolean>>>(
                                            new MutablePair<MutablePair<ITypeBinding, ITypeBinding>, Boolean>(), new MutablePair<MutablePair<List<Expression>, Boolean>, MutablePair<List<Expression>, Boolean>>());
                                MutablePair<MutablePair<List<Expression>, Boolean>, MutablePair<List<Expression>, Boolean>> expressionsPair = outerPair.getRight() == null ? 
                                        new MutablePair<MutablePair<List<Expression>, Boolean>, MutablePair<List<Expression>, Boolean>>() : outerPair.getRight();
                                        MutablePair<MutablePair<ITypeBinding, ITypeBinding>, Boolean> bindingPair = outerPair.getLeft() == null ? new MutablePair<MutablePair<ITypeBinding, ITypeBinding>, Boolean>() : outerPair.getLeft();
                                        outerPair.setLeft(bindingPair);
                                        outerPair.setRight(expressionsPair);
                                        MutablePair<List<Expression>, Boolean> expressionsInnerPair = expressionsPair.getRight() == null ? new MutablePair<List<Expression>, Boolean>() : expressionsPair.getRight();
                                        List<Expression> expressions = expressionsInnerPair.getLeft();
                                        Boolean variablizable = expressionsInnerPair.getRight() == null ? true : expressionsInnerPair.getRight();
                                        expressionsInnerPair.setRight(variablizable);
                                        if (expressions == null) { 
                                            expressions = Lists.newArrayList();
                                            expressionsInnerPair.setLeft(expressions);
                                        }
                                        expressions.add(rhs);
                                        expressionsPair.setRight(expressionsInnerPair);
                                        MutablePair<ITypeBinding, ITypeBinding> typeBindings = bindingPair.getLeft();
                                        if (typeBindings == null) {
                                            typeBindings = new MutablePair<ITypeBinding, ITypeBinding>();
                                        }
                                        typeBindings.setRight(binding);
                                        bindingPair.setLeft(typeBindings);
                                        bindingPair.setRight(stillFatherable);
                                        rhsExpressions.put(mutIDNumber, outerPair);
                    } 

                } else if (rhs instanceof BooleanLiteral) {
                    ITypeBinding binding = getRewrite().getAST().resolveWellKnownType("boolean");
                    if (rhsExpressions.containsKey(mutIDNumber) 
                            && rhsExpressions.get(mutIDNumber).getRight() != null 
                            && rhsExpressions.get(mutIDNumber).getRight().getRight() != null) {
                        MutablePair<MutablePair<MutablePair<ITypeBinding, ITypeBinding>, Boolean>, MutablePair<MutablePair<List<Expression>, Boolean>, MutablePair<List<Expression>, Boolean>>> outerPair = rhsExpressions.get(mutIDNumber);
                        outerPair.getRight().getRight().getLeft().add(rhs);
                        if (outerPair.getLeft().getLeft().getRight() == null) {
                            outerPair.getLeft().getLeft().setRight(binding);
                        }
                    } else {
                        String mutGenLimit = getLineComment(unit.lastTrailingCommentIndex(statement));
                        if (!mutGenLimit.contains(mutGenLimitPrefix + 0) &&  
                                (!mutGenLimit.contains(mutGenLimitPrefix + 1) 
                                        || input.getFeedback().getLastMutatedLines().contains(input.getFeedback().getMutableLines().get(mutIDNumber - 1)))) {
                            stillFatherable = true;
                        }

                        MutablePair<MutablePair<MutablePair<ITypeBinding, ITypeBinding>, Boolean>, MutablePair<MutablePair<List<Expression>, Boolean>, MutablePair<List<Expression>, Boolean>>> outerPair = 
                                rhsExpressions.containsKey(mutIDNumber) ? rhsExpressions.get(mutIDNumber) : 
                                    new MutablePair<MutablePair<MutablePair<ITypeBinding, ITypeBinding>,Boolean>, MutablePair<MutablePair<List<Expression>, Boolean>, MutablePair<List<Expression>, Boolean>>>(
                                            new MutablePair<MutablePair<ITypeBinding, ITypeBinding>, Boolean>(), new MutablePair<MutablePair<List<Expression>, Boolean>, MutablePair<List<Expression>, Boolean>>());
                                MutablePair<MutablePair<List<Expression>, Boolean>, MutablePair<List<Expression>, Boolean>> expressionsPair = outerPair.getRight() == null ? 
                                        new MutablePair<MutablePair<List<Expression>, Boolean>, MutablePair<List<Expression>, Boolean>>() : outerPair.getRight();
                                        MutablePair<MutablePair<ITypeBinding, ITypeBinding>, Boolean> bindingPair = outerPair.getLeft() == null ? new MutablePair<MutablePair<ITypeBinding, ITypeBinding>, Boolean>() : outerPair.getLeft();
                                        outerPair.setLeft(bindingPair);
                                        outerPair.setRight(expressionsPair);
                                        MutablePair<List<Expression>, Boolean> expressionsInnerPair = expressionsPair.getRight() == null ? new MutablePair<List<Expression>, Boolean>() : expressionsPair.getRight();
                                        List<Expression> expressions = expressionsInnerPair.getLeft();
                                        Boolean variablizable = expressionsInnerPair.getRight() == null ? true : expressionsInnerPair.getRight();
                                        expressionsInnerPair.setRight(variablizable);
                                        if (expressions == null) { 
                                            expressions = Lists.newArrayList();
                                            expressionsInnerPair.setLeft(expressions);
                                        }
                                        expressions.add(rhs);
                                        expressionsPair.setRight(expressionsInnerPair);
                                        MutablePair<ITypeBinding, ITypeBinding> typeBindings = bindingPair.getLeft();
                                        if (typeBindings == null) {
                                            typeBindings = new MutablePair<ITypeBinding, ITypeBinding>();
                                        }
                                        typeBindings.setRight(binding);
                                        bindingPair.setLeft(typeBindings);
                                        bindingPair.setRight(stillFatherable);
                                        rhsExpressions.put(mutIDNumber, outerPair);
                    } 
                } else if (rhs instanceof NumberLiteral) {
                    ITypeBinding binding = assignment.resolveTypeBinding();
                    if (rhsExpressions.containsKey(mutIDNumber) 
                            && rhsExpressions.get(mutIDNumber).getRight() != null 
                            && rhsExpressions.get(mutIDNumber).getRight().getRight() != null) {
                        MutablePair<MutablePair<MutablePair<ITypeBinding, ITypeBinding>, Boolean>, MutablePair<MutablePair<List<Expression>, Boolean>, MutablePair<List<Expression>, Boolean>>> outerPair = rhsExpressions.get(mutIDNumber);
                        outerPair.getRight().getRight().getLeft().add(rhs);
                        if (outerPair.getLeft().getLeft().getRight() == null) {
                            outerPair.getLeft().getLeft().setRight(binding);
                        }
                    } else {
                        String mutGenLimit = getLineComment(unit.lastTrailingCommentIndex(statement));
                        if (!mutGenLimit.contains(mutGenLimitPrefix + 0) &&  
                                (!mutGenLimit.contains(mutGenLimitPrefix + 1) 
                                        || input.getFeedback().getLastMutatedLines().contains(input.getFeedback().getMutableLines().get(mutIDNumber - 1)))) {
                            stillFatherable = true;
                        }

                        MutablePair<MutablePair<MutablePair<ITypeBinding, ITypeBinding>, Boolean>, MutablePair<MutablePair<List<Expression>, Boolean>, MutablePair<List<Expression>, Boolean>>> outerPair = 
                                rhsExpressions.containsKey(mutIDNumber) ? rhsExpressions.get(mutIDNumber) : 
                                    new MutablePair<MutablePair<MutablePair<ITypeBinding, ITypeBinding>,Boolean>, MutablePair<MutablePair<List<Expression>, Boolean>, MutablePair<List<Expression>, Boolean>>>(
                                            new MutablePair<MutablePair<ITypeBinding, ITypeBinding>, Boolean>(), new MutablePair<MutablePair<List<Expression>, Boolean>, MutablePair<List<Expression>, Boolean>>());
                                MutablePair<MutablePair<List<Expression>, Boolean>, MutablePair<List<Expression>, Boolean>> expressionsPair = outerPair.getRight() == null ? 
                                        new MutablePair<MutablePair<List<Expression>, Boolean>, MutablePair<List<Expression>, Boolean>>() : outerPair.getRight();
                                        MutablePair<MutablePair<ITypeBinding, ITypeBinding>, Boolean> bindingPair = outerPair.getLeft() == null ? new MutablePair<MutablePair<ITypeBinding, ITypeBinding>, Boolean>() : outerPair.getLeft();
                                        outerPair.setLeft(bindingPair);
                                        outerPair.setRight(expressionsPair);
                                        MutablePair<List<Expression>, Boolean> expressionsInnerPair = expressionsPair.getRight() == null ? new MutablePair<List<Expression>, Boolean>() : expressionsPair.getRight();
                                        List<Expression> expressions = expressionsInnerPair.getLeft();
                                        Boolean variablizable = expressionsInnerPair.getRight() == null ? true : expressionsInnerPair.getRight();
                                        expressionsInnerPair.setRight(variablizable);
                                        if (expressions == null) { 
                                            expressions = Lists.newArrayList();
                                            expressionsInnerPair.setLeft(expressions);
                                        }
                                        expressions.add(rhs);
                                        expressionsPair.setRight(expressionsInnerPair);
                                        MutablePair<ITypeBinding, ITypeBinding> typeBindings = bindingPair.getLeft();
                                        if (typeBindings == null) {
                                            typeBindings = new MutablePair<ITypeBinding, ITypeBinding>();
                                        }
                                        typeBindings.setRight(binding);
                                        bindingPair.setLeft(typeBindings);
                                        bindingPair.setRight(stillFatherable);
                                        rhsExpressions.put(mutIDNumber, outerPair);
                    } 

                } else {
                    ITypeBinding binding = assignment.resolveTypeBinding();
                    if (rhsExpressions.containsKey(mutIDNumber) 
                            && rhsExpressions.get(mutIDNumber).getRight() != null 
                            && rhsExpressions.get(mutIDNumber).getRight().getRight() != null) {
                        MutablePair<MutablePair<MutablePair<ITypeBinding, ITypeBinding>, Boolean>, MutablePair<MutablePair<List<Expression>, Boolean>, MutablePair<List<Expression>, Boolean>>> outerPair = rhsExpressions.get(mutIDNumber);
                        outerPair.getRight().getRight().getLeft().add(rhs);
                        if (outerPair.getLeft().getLeft().getRight() == null) {
                            outerPair.getLeft().getLeft().setRight(binding);
                        }
                    } else {
                        String mutGenLimit = getLineComment(unit.lastTrailingCommentIndex(statement));
                        if (!mutGenLimit.contains(mutGenLimitPrefix + 0) &&  
                                (!mutGenLimit.contains(mutGenLimitPrefix + 1) 
                                        || input.getFeedback().getLastMutatedLines().contains(input.getFeedback().getMutableLines().get(mutIDNumber - 1)))) {
                            stillFatherable = true;
                        }

                        MutablePair<MutablePair<MutablePair<ITypeBinding, ITypeBinding>, Boolean>, MutablePair<MutablePair<List<Expression>, Boolean>, MutablePair<List<Expression>, Boolean>>> outerPair = 
                                rhsExpressions.containsKey(mutIDNumber) ? rhsExpressions.get(mutIDNumber) : 
                                    new MutablePair<MutablePair<MutablePair<ITypeBinding, ITypeBinding>,Boolean>, MutablePair<MutablePair<List<Expression>, Boolean>, MutablePair<List<Expression>, Boolean>>>(
                                            new MutablePair<MutablePair<ITypeBinding, ITypeBinding>, Boolean>(), new MutablePair<MutablePair<List<Expression>, Boolean>, MutablePair<List<Expression>, Boolean>>());
                                MutablePair<MutablePair<List<Expression>, Boolean>, MutablePair<List<Expression>, Boolean>> expressionsPair = outerPair.getRight() == null ? 
                                        new MutablePair<MutablePair<List<Expression>, Boolean>, MutablePair<List<Expression>, Boolean>>() : outerPair.getRight();
                                        MutablePair<MutablePair<ITypeBinding, ITypeBinding>, Boolean> bindingPair = outerPair.getLeft() == null ? new MutablePair<MutablePair<ITypeBinding, ITypeBinding>, Boolean>() : outerPair.getLeft();
                                        outerPair.setLeft(bindingPair);
                                        outerPair.setRight(expressionsPair);
                                        MutablePair<List<Expression>, Boolean> expressionsInnerPair = expressionsPair.getRight() == null ? new MutablePair<List<Expression>, Boolean>() : expressionsPair.getRight();
                                        List<Expression> expressions = expressionsInnerPair.getLeft();
                                        Boolean variablizable = expressionsInnerPair.getRight() == null ? true : expressionsInnerPair.getRight();
                                        expressionsInnerPair.setRight(variablizable);
                                        if (expressions == null) { 
                                            expressions = Lists.newArrayList();
                                            expressionsInnerPair.setLeft(expressions);
                                        }
                                        expressions.add(rhs);
                                        expressionsPair.setRight(expressionsInnerPair);
                                        MutablePair<ITypeBinding, ITypeBinding> typeBindings = bindingPair.getLeft();
                                        if (typeBindings == null) {
                                            typeBindings = new MutablePair<ITypeBinding, ITypeBinding>();
                                        }
                                        typeBindings.setRight(binding);
                                        bindingPair.setLeft(typeBindings);
                                        bindingPair.setRight(stillFatherable);
                                        rhsExpressions.put(mutIDNumber, outerPair);
                    } 
                }
            } else if (expression instanceof PostfixExpression) {
                //Tomar el id de mutante
                Expression toVariablize = ((PostfixExpression)expression).getOperand();

                if (rhsExpressions.containsKey(mutIDNumber) 
                        && rhsExpressions.get(mutIDNumber).getRight() != null 
                        && rhsExpressions.get(mutIDNumber).getRight().getRight() != null) {
                    rhsExpressions.get(mutIDNumber).getRight().getRight().getLeft().add(toVariablize);
                } else {
                    String mutGenLimit = getLineComment(unit.lastTrailingCommentIndex(statement));
                    if (!mutGenLimit.contains(mutGenLimitPrefix + 0) &&  
                            (!mutGenLimit.contains(mutGenLimitPrefix + 1) 
                                    || input.getFeedback().getLastMutatedLines().contains(input.getFeedback().getMutableLines().get(mutIDNumber - 1)))) {
                        stillFatherable = true;
                    }

                    ITypeBinding binding = toVariablize.resolveTypeBinding();
                    MutablePair<MutablePair<MutablePair<ITypeBinding, ITypeBinding>, Boolean>, MutablePair<MutablePair<List<Expression>, Boolean>, MutablePair<List<Expression>, Boolean>>> outerPair = 
                            rhsExpressions.containsKey(mutIDNumber) ? rhsExpressions.get(mutIDNumber) : 
                                new MutablePair<MutablePair<MutablePair<ITypeBinding, ITypeBinding>,Boolean>, MutablePair<MutablePair<List<Expression>, Boolean>, MutablePair<List<Expression>, Boolean>>>(
                                        new MutablePair<MutablePair<ITypeBinding, ITypeBinding>, Boolean>(), new MutablePair<MutablePair<List<Expression>, Boolean>, MutablePair<List<Expression>, Boolean>>());
                            MutablePair<MutablePair<List<Expression>, Boolean>, MutablePair<List<Expression>, Boolean>> expressionsPair = outerPair.getRight() == null ? 
                                    new MutablePair<MutablePair<List<Expression>, Boolean>, MutablePair<List<Expression>, Boolean>>() : outerPair.getRight();
                                    MutablePair<MutablePair<ITypeBinding, ITypeBinding>, Boolean> bindingPair = outerPair.getLeft() == null ? new MutablePair<MutablePair<ITypeBinding, ITypeBinding>, Boolean>() : outerPair.getLeft();
                                    outerPair.setLeft(bindingPair);
                                    outerPair.setRight(expressionsPair);
                                    MutablePair<List<Expression>, Boolean> expressionsInnerPair = expressionsPair.getRight() == null ? new MutablePair<List<Expression>, Boolean>() : expressionsPair.getRight();
                                    List<Expression> expressions = expressionsInnerPair.getLeft();
                                    Boolean variablizable = expressionsInnerPair.getRight() == null ? true : expressionsInnerPair.getRight();
                                    expressionsInnerPair.setRight(variablizable);
                                    if (expressions == null) { 
                                        expressions = Lists.newArrayList();
                                        expressionsInnerPair.setLeft(expressions);
                                    }
                                    expressions.add(toVariablize);
                                    expressionsPair.setRight(expressionsInnerPair);
                                    MutablePair<ITypeBinding, ITypeBinding> typeBindings = new MutablePair<ITypeBinding, ITypeBinding>();
                                    typeBindings.setRight(binding);
                                    bindingPair.setLeft(typeBindings);
                                    bindingPair.setRight(stillFatherable);
                                    rhsExpressions.put(mutIDNumber, outerPair);
                }
            } else if (expression instanceof PrefixExpression) {
                //Tomar el id de mutante
                Expression toVariablize = ((PrefixExpression)expression).getOperand();
                if (rhsExpressions.containsKey(mutIDNumber) 
                        && rhsExpressions.get(mutIDNumber).getRight() != null 
                        && rhsExpressions.get(mutIDNumber).getRight().getRight() != null) {
                    rhsExpressions.get(mutIDNumber).getRight().getRight().getLeft().add(toVariablize);
                } else {
                    String mutGenLimit = getLineComment(unit.lastTrailingCommentIndex(statement));
                    if (!mutGenLimit.contains(mutGenLimitPrefix + 0) &&  
                            (!mutGenLimit.contains(mutGenLimitPrefix + 1) 
                                    || input.getFeedback().getLastMutatedLines().contains(input.getFeedback().getMutableLines().get(mutIDNumber - 1)))) {
                        stillFatherable = true;
                    }

                    ITypeBinding binding = toVariablize.resolveTypeBinding();
                    MutablePair<MutablePair<MutablePair<ITypeBinding, ITypeBinding>, Boolean>, MutablePair<MutablePair<List<Expression>, Boolean>, MutablePair<List<Expression>, Boolean>>> outerPair = 
                            rhsExpressions.containsKey(mutIDNumber) ? rhsExpressions.get(mutIDNumber) : 
                                new MutablePair<MutablePair<MutablePair<ITypeBinding, ITypeBinding>,Boolean>, MutablePair<MutablePair<List<Expression>, Boolean>, MutablePair<List<Expression>, Boolean>>>(
                                        new MutablePair<MutablePair<ITypeBinding, ITypeBinding>, Boolean>(), new MutablePair<MutablePair<List<Expression>, Boolean>, MutablePair<List<Expression>, Boolean>>());
                            MutablePair<MutablePair<List<Expression>, Boolean>, MutablePair<List<Expression>, Boolean>> expressionsPair = outerPair.getRight() == null ? 
                                    new MutablePair<MutablePair<List<Expression>, Boolean>, MutablePair<List<Expression>, Boolean>>() : outerPair.getRight();
                                    MutablePair<MutablePair<ITypeBinding, ITypeBinding>, Boolean> bindingPair = outerPair.getLeft() == null ? new MutablePair<MutablePair<ITypeBinding, ITypeBinding>, Boolean>() : outerPair.getLeft();
                                    outerPair.setLeft(bindingPair);
                                    outerPair.setRight(expressionsPair);
                                    MutablePair<List<Expression>, Boolean> expressionsInnerPair = expressionsPair.getRight() == null ? new MutablePair<List<Expression>, Boolean>() : expressionsPair.getRight();
                                    List<Expression> expressions = expressionsInnerPair.getLeft();
                                    Boolean variablizable = expressionsInnerPair.getRight() == null ? true : expressionsInnerPair.getRight();
                                    expressionsInnerPair.setRight(variablizable);
                                    if (expressions == null) { 
                                        expressions = Lists.newArrayList();
                                        expressionsInnerPair.setLeft(expressions);
                                    }
                                    expressions.add(toVariablize);
                                    expressionsPair.setRight(expressionsInnerPair);
                                    MutablePair<ITypeBinding, ITypeBinding> typeBindings = new MutablePair<ITypeBinding, ITypeBinding>();
                                    typeBindings.setRight(binding);
                                    bindingPair.setLeft(typeBindings);
                                    bindingPair.setRight(stillFatherable);
                                    rhsExpressions.put(mutIDNumber, outerPair);
                }
            } else {
                if (rhsExpressions.containsKey(mutIDNumber) 
                        && rhsExpressions.get(mutIDNumber).getRight() != null 
                        && rhsExpressions.get(mutIDNumber).getRight().getLeft() != null) {
                    rhsExpressions.get(mutIDNumber).getRight().getLeft().getLeft().add(expression);
                } else {
                    String mutGenLimit = getLineComment(unit.lastTrailingCommentIndex(statement));
                    if (!mutGenLimit.contains(mutGenLimitPrefix + 0) &&  
                            (!mutGenLimit.contains(mutGenLimitPrefix + 1) 
                                    || input.getFeedback().getLastMutatedLines().contains(input.getFeedback().getMutableLines().get(mutIDNumber - 1)))) {
                        stillFatherable = true;
                    }

                    ITypeBinding binding = expression.resolveTypeBinding();
                    MutablePair<MutablePair<MutablePair<ITypeBinding, ITypeBinding>, Boolean>, MutablePair<MutablePair<List<Expression>, Boolean>, MutablePair<List<Expression>, Boolean>>> outerPair = 
                            rhsExpressions.containsKey(mutIDNumber) ? rhsExpressions.get(mutIDNumber) : 
                                new MutablePair<MutablePair<MutablePair<ITypeBinding, ITypeBinding>,Boolean>, MutablePair<MutablePair<List<Expression>, Boolean>, MutablePair<List<Expression>, Boolean>>>(
                                        new MutablePair<MutablePair<ITypeBinding, ITypeBinding>, Boolean>(), new MutablePair<MutablePair<List<Expression>, Boolean>, MutablePair<List<Expression>, Boolean>>());
                            MutablePair<MutablePair<List<Expression>, Boolean>, MutablePair<List<Expression>, Boolean>> expressionsPair = outerPair.getRight() == null ? 
                                    new MutablePair<MutablePair<List<Expression>, Boolean>, MutablePair<List<Expression>, Boolean>>() : outerPair.getRight();
                                    MutablePair<MutablePair<ITypeBinding, ITypeBinding>, Boolean> bindingPair = outerPair.getLeft() == null ? new MutablePair<MutablePair<ITypeBinding, ITypeBinding>, Boolean>() : outerPair.getLeft();
                                    outerPair.setLeft(bindingPair);
                                    outerPair.setRight(expressionsPair);
                                    MutablePair<List<Expression>, Boolean> expressionsInnerPair = expressionsPair.getLeft() == null ? new MutablePair<List<Expression>, Boolean>() : expressionsPair.getLeft();
                                    List<Expression> expressions = expressionsInnerPair.getLeft();
                                    Boolean variablizable = expressionsInnerPair.getRight() == null ? false : expressionsInnerPair.getRight();
                                    expressionsInnerPair.setRight(variablizable);
                                    if (expressions == null) { 
                                        expressions = Lists.newArrayList();
                                        expressionsInnerPair.setLeft(expressions);
                                    }
                                    expressions.add(expression);
                                    expressionsPair.setRight(expressionsInnerPair);
                                    MutablePair<ITypeBinding, ITypeBinding> typeBindings = new MutablePair<ITypeBinding, ITypeBinding>();
                                    typeBindings.setRight(binding);
                                    bindingPair.setLeft(typeBindings);
                                    bindingPair.setRight(stillFatherable);
                                    rhsExpressions.put(mutIDNumber, outerPair);
                }
            }
        } else if (statement instanceof VariableDeclarationStatement) {
            VariableDeclarationStatement vds = (VariableDeclarationStatement) statement;
            @SuppressWarnings("unchecked")
            List<VariableDeclarationFragment> fragments = vds.fragments();
            if (fragments.size() != 1) {
                log.error("Variablization: VDStatement of more than 1 fragment. Not yet supported");
            }
            VariableDeclarationFragment frag = fragments.get(0);
            Expression rhs = frag.getInitializer();

            if (rhsExpressions.containsKey(mutIDNumber) 
                    && rhsExpressions.get(mutIDNumber).getRight() != null 
                    && rhsExpressions.get(mutIDNumber).getRight().getRight() != null) {
                rhsExpressions.get(mutIDNumber).getRight().getRight().getLeft().add(rhs);
            } else {
                String mutGenLimit = getLineComment(unit.lastTrailingCommentIndex(statement));
                if (!mutGenLimit.contains(mutGenLimitPrefix + 0) &&  
                        (!mutGenLimit.contains(mutGenLimitPrefix + 1) 
                                || input.getFeedback().getLastMutatedLines().contains(input.getFeedback().getMutableLines().get(mutIDNumber - 1)))) {
                    stillFatherable = true;
                }

                ITypeBinding binding = vds.getType().resolveBinding();
                MutablePair<MutablePair<MutablePair<ITypeBinding, ITypeBinding>, Boolean>, MutablePair<MutablePair<List<Expression>, Boolean>, MutablePair<List<Expression>, Boolean>>> outerPair = 
                        rhsExpressions.containsKey(mutIDNumber) ? rhsExpressions.get(mutIDNumber) : 
                            new MutablePair<MutablePair<MutablePair<ITypeBinding, ITypeBinding>,Boolean>, MutablePair<MutablePair<List<Expression>, Boolean>, MutablePair<List<Expression>, Boolean>>>(
                                    new MutablePair<MutablePair<ITypeBinding, ITypeBinding>, Boolean>(), new MutablePair<MutablePair<List<Expression>, Boolean>, MutablePair<List<Expression>, Boolean>>());
                        MutablePair<MutablePair<List<Expression>, Boolean>, MutablePair<List<Expression>, Boolean>> expressionsPair = outerPair.getRight() == null ? 
                                new MutablePair<MutablePair<List<Expression>, Boolean>, MutablePair<List<Expression>, Boolean>>() : outerPair.getRight();
                                MutablePair<MutablePair<ITypeBinding, ITypeBinding>, Boolean> bindingPair = outerPair.getLeft() == null ? new MutablePair<MutablePair<ITypeBinding, ITypeBinding>, Boolean>() : outerPair.getLeft();
                                outerPair.setLeft(bindingPair);
                                outerPair.setRight(expressionsPair);
                                MutablePair<List<Expression>, Boolean> expressionsInnerPair = expressionsPair.getRight() == null ? new MutablePair<List<Expression>, Boolean>() : expressionsPair.getRight();
                                List<Expression> expressions = expressionsInnerPair.getLeft();
                                Boolean variablizable = expressionsInnerPair.getRight() == null ? true : expressionsInnerPair.getRight();
                                expressionsInnerPair.setRight(variablizable);
                                if (expressions == null) { 
                                    expressions = Lists.newArrayList();
                                    expressionsInnerPair.setLeft(expressions);
                                }
                                expressions.add(rhs);
                                expressionsPair.setRight(expressionsInnerPair);
                                MutablePair<ITypeBinding, ITypeBinding> typeBindings = new MutablePair<ITypeBinding, ITypeBinding>();
                                typeBindings.setRight(binding);
                                bindingPair.setLeft(typeBindings);
                                bindingPair.setRight(stillFatherable);
                                rhsExpressions.put(mutIDNumber, outerPair);

            }
        } else {
            log.error("Variablization: Found really weird statement");
        }
    }

    /**
     * Collects {@code rhs}, the condition of {@code statement}, as a {@code boolean} right-hand
     * side under the mutID found from the comment at {@code commentIndex} (see
     * {@link #getLineComment(int)}).
     */
    public void processBooleanNode(Statement statement, Expression rhs, int commentIndex) {
        String mutIDComment = getLineComment(commentIndex);

        int mutIDIndex = mutIDComment.indexOf(mutIDCommentPrefix) + 6;
        String mutIDString = mutIDComment.substring(mutIDIndex, mutIDComment.length() - 1);

        Integer mutIDNumber = Integer.valueOf(mutIDString);

        if (mutIDNumber < 0) {
            return;
        }

        if (rhsExpressions.containsKey(mutIDNumber) 
                && rhsExpressions.get(mutIDNumber).getRight() != null 
                && rhsExpressions.get(mutIDNumber).getRight().getRight() != null) {
            rhsExpressions.get(mutIDNumber).getRight().getRight().getLeft().add(rhs);
        } else {
            String mutGenLimit = getLineComment(commentIndex);
            if (!mutGenLimit.contains(mutGenLimitPrefix + 0) &&  
                    (!mutGenLimit.contains(mutGenLimitPrefix + 1) 
                            || input.getFeedback().getLastMutatedLines().contains(input.getFeedback().getMutableLines().get(mutIDNumber - 1)))) {
                stillFatherable = true;
            }

            ITypeBinding binding = getRewrite().getAST().resolveWellKnownType("boolean");
            MutablePair<MutablePair<MutablePair<ITypeBinding, ITypeBinding>, Boolean>, MutablePair<MutablePair<List<Expression>, Boolean>, MutablePair<List<Expression>, Boolean>>> outerPair = 
                    rhsExpressions.containsKey(mutIDNumber) ? rhsExpressions.get(mutIDNumber) : 
                        new MutablePair<MutablePair<MutablePair<ITypeBinding, ITypeBinding>,Boolean>, MutablePair<MutablePair<List<Expression>, Boolean>, MutablePair<List<Expression>, Boolean>>>(
                                new MutablePair<MutablePair<ITypeBinding, ITypeBinding>, Boolean>(), new MutablePair<MutablePair<List<Expression>, Boolean>, MutablePair<List<Expression>, Boolean>>());
                    MutablePair<MutablePair<List<Expression>, Boolean>, MutablePair<List<Expression>, Boolean>> expressionsPair = outerPair.getRight() == null ? 
                            new MutablePair<MutablePair<List<Expression>, Boolean>, MutablePair<List<Expression>, Boolean>>() : outerPair.getRight();
                            MutablePair<MutablePair<ITypeBinding, ITypeBinding>, Boolean> bindingPair = outerPair.getLeft() == null ? new MutablePair<MutablePair<ITypeBinding, ITypeBinding>, Boolean>() : outerPair.getLeft();
                            outerPair.setLeft(bindingPair);
                            outerPair.setRight(expressionsPair);
                            MutablePair<List<Expression>, Boolean> expressionsInnerPair = expressionsPair.getRight() == null ? new MutablePair<List<Expression>, Boolean>() : expressionsPair.getRight();
                            List<Expression> expressions = expressionsInnerPair.getLeft();
                            Boolean variablizable = expressionsInnerPair.getRight() == null ? true : expressionsInnerPair.getRight();
                            expressionsInnerPair.setRight(variablizable);
                            if (expressions == null) { 
                                expressions = Lists.newArrayList();
                                expressionsInnerPair.setLeft(expressions);
                            }
                            expressions.add(rhs);
                            expressionsPair.setRight(expressionsInnerPair);
                            MutablePair<ITypeBinding, ITypeBinding> typeBindings = new MutablePair<ITypeBinding, ITypeBinding>();
                            typeBindings.setRight(binding);
                            bindingPair.setLeft(typeBindings);
                            bindingPair.setRight(stillFatherable);
                            rhsExpressions.put(mutIDNumber, outerPair);
        }
    }

    /**
     * Dispatches on the node kind. Statements other than blocks, {@code if}, loops and
     * {@code return} that have a trailing comment and were not processed already go to
     * {@link #processNode(Statement)}, and their children are not visited. {@code if},
     * {@code while}, {@code for} and enhanced {@code for} statements process the first statement
     * of their branches or body right away ({@code if} also its condition) and then visit their
     * children. {@code return} statements with a trailing comment go to
     * {@link #processReturnNode(ReturnStatement)}. A method declaration whose name contains
     * {@link #methodName} is recorded as the target method.
     */
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
            int commentIndex = unit.lastTrailingCommentIndex(node);
            if (commentIndex >= 0) {
                if (node instanceof ReturnStatement) {
                    processReturnNode((ReturnStatement)node);
                } else {
                    processNode((Statement)node);
                }
            }

            return false;
        } else if (node instanceof IfStatement){

            //Ojo con los replace, no se si al reemplazar un padre, y luego reemplazar el hijo, si el hijo sera reemplazado o no, 
            // porque en el rewrite ya se cambio ese hijo por una copia, entonces quizáz no lo encuentra.

            Statement thenStatement = ((IfStatement) node).getThenStatement();

            Statement elseStatement = ((IfStatement) node).getElseStatement();

            if (thenStatement instanceof Block) {
                Statement thenFirstStatement = (Statement)((Block)thenStatement).statements().get(0);
                if (!(thenFirstStatement instanceof IfStatement)
                        && !(thenFirstStatement instanceof WhileStatement)
                        && !(thenFirstStatement instanceof ForStatement)
                        && !(thenFirstStatement instanceof EnhancedForStatement)) {
                    customNodes.add(thenFirstStatement);
                    int commentIndex = unit.lastTrailingCommentIndex(thenFirstStatement);
                    if (commentIndex >= 0) {
                        if (thenFirstStatement instanceof ReturnStatement) {
                            processReturnNode((ReturnStatement)thenFirstStatement);
                        } else {
                            processNode(thenFirstStatement);
                        }
                    }
                }
            } else if (!(thenStatement instanceof IfStatement)
                    && !(thenStatement instanceof WhileStatement)
                    && !(thenStatement instanceof ForStatement)
                    && !(thenStatement instanceof EnhancedForStatement)) {
                customNodes.add(thenStatement);
                int commentIndex = unit.lastTrailingCommentIndex(thenStatement);
                if (commentIndex >= 0) {
                    if (thenStatement instanceof ReturnStatement) {
                        processReturnNode((ReturnStatement)thenStatement);
                    } else {
                        processNode(thenStatement);
                    }
                }
            } else {
                customNodes.add(thenStatement);
            }

            if (elseStatement != null) {
                if (elseStatement instanceof Block) {
                    Statement elseFirstStatement = (Statement)((Block)elseStatement).statements().get(0);

                    if (!(elseFirstStatement instanceof IfStatement)
                            && !(elseFirstStatement instanceof WhileStatement)
                            && !(elseFirstStatement instanceof ForStatement)
                            && !(elseFirstStatement instanceof EnhancedForStatement)) {
                        customNodes.add(elseFirstStatement);
                        int commentIndex = unit.lastTrailingCommentIndex(elseFirstStatement);
                        if (commentIndex >= 0) {
                            if (elseFirstStatement instanceof ReturnStatement) {
                                processReturnNode((ReturnStatement)elseFirstStatement);
                            } else {
                                processNode(elseFirstStatement);
                            }
                        }
                    }
                } else if (!(elseStatement instanceof IfStatement)
                        && !(elseStatement instanceof WhileStatement)
                        && !(elseStatement instanceof ForStatement)
                        && !(elseStatement instanceof EnhancedForStatement)) {
                    customNodes.add(elseStatement);
                    int commentIndex = unit.lastTrailingCommentIndex(elseStatement);
                    if (commentIndex >= 0) {
                        if (elseStatement instanceof ReturnStatement) {
                            processReturnNode((ReturnStatement)elseStatement);
                        } else {
                            processNode(elseStatement);
                        }
                    }
                } else {
                    customNodes.add(elseStatement);
                }
            }
            int commentIndex = unit.lastTrailingCommentIndex((IfStatement) node);
            Statement thenFirstStatement = null;
            if (((IfStatement) node).getThenStatement() instanceof Block) {
                Block thenBlock = (Block)((IfStatement) node).getThenStatement();
                thenFirstStatement = (Statement)thenBlock.statements().get(0);
            } else {
                thenFirstStatement = (Statement)((IfStatement) node).getThenStatement();
            }
            List<Comment> comments = (List<Comment>)unit.getCommentList();
            for (int i = 0; i < comments.size(); ++i) {
                Comment comment = comments.get(i);
                if (comment.getStartPosition() < thenFirstStatement.getStartPosition() && comment.getStartPosition() > node.getStartPosition()) {
                    processBooleanNode(((IfStatement) node), ((IfStatement) node).getExpression(), i);
                }
            }
            
//            int commentIndex3 = unit.firstLeadingCommentIndex(((IfStatement) node).getElseStatement());
//            int commentIndex4 = unit.firstLeadingCommentIndex(((IfStatement) node).getThenStatement());
//            int commentIndex5 = unit.lastTrailingCommentIndex(((IfStatement) node).getElseStatement());
//            int commentIndex6 = unit.lastTrailingCommentIndex(((IfStatement) node).getThenStatement());
            if (commentIndex >= 0) {
            }
            return true;
        } else if (node instanceof WhileStatement) {

            Statement whileBody = ((WhileStatement) node).getBody();

            if (whileBody instanceof Block) {
                Statement whileBodyFirstStatement = (Statement)((Block)whileBody).statements().get(0);
                if (!(whileBodyFirstStatement instanceof IfStatement)
                        && !(whileBodyFirstStatement instanceof WhileStatement)
                        && !(whileBodyFirstStatement instanceof ForStatement)
                        && !(whileBodyFirstStatement instanceof EnhancedForStatement)) {
                    customNodes.add(whileBodyFirstStatement);
                    int commentIndex = unit.lastTrailingCommentIndex(whileBodyFirstStatement);
                    if (commentIndex >= 0) {
                        if (whileBodyFirstStatement instanceof ReturnStatement) {
                            processReturnNode((ReturnStatement)whileBodyFirstStatement);
                        } else {
                            processNode(whileBodyFirstStatement);
                        }
                    }
                }
            } else if (!(whileBody instanceof IfStatement)
                    && !(whileBody instanceof WhileStatement)
                    && !(whileBody instanceof ForStatement)
                    && !(whileBody instanceof EnhancedForStatement)) {
                customNodes.add(whileBody);
                int commentIndex = unit.lastTrailingCommentIndex(whileBody);
                if (commentIndex >= 0) {
                    if (whileBody instanceof ReturnStatement) {
                        processReturnNode((ReturnStatement)whileBody);
                    } else {
                        processNode(whileBody);
                    }
                }
            } else {
                customNodes.add(whileBody);
            }
//            int commentIndex = unit.lastTrailingCommentIndex((WhileStatement) node);
//            if (commentIndex >= 0) {
//                processBooleanNode(((WhileStatement) node), ((WhileStatement) node).getExpression());
//            }
            return true;
        } else if (node instanceof ForStatement) {
            Statement forBody = ((ForStatement) node).getBody();

            if (forBody instanceof Block) {
                Statement forBodyFirstStatement = (Statement)((Block)forBody).statements().get(0);
                if (!(forBodyFirstStatement instanceof IfStatement)
                        && !(forBodyFirstStatement instanceof WhileStatement)
                        && !(forBodyFirstStatement instanceof ForStatement)
                        && !(forBodyFirstStatement instanceof EnhancedForStatement)) {
                    customNodes.add(forBodyFirstStatement);
                    int commentIndex = unit.lastTrailingCommentIndex(forBodyFirstStatement);
                    if (commentIndex >= 0) {
                        if (forBodyFirstStatement instanceof ReturnStatement) {
                            processReturnNode((ReturnStatement)forBodyFirstStatement);
                        } else {
                            processNode(forBodyFirstStatement);
                        }
                    }
                }
            } else if (!(forBody instanceof IfStatement)
                    && !(forBody instanceof WhileStatement)
                    && !(forBody instanceof ForStatement)
                    && !(forBody instanceof EnhancedForStatement)) {
                customNodes.add(forBody);
                int commentIndex = unit.lastTrailingCommentIndex(forBody);
                if (commentIndex >= 0) {
                    if (forBody instanceof ReturnStatement) {
                        processReturnNode((ReturnStatement)forBody);
                    } else {
                        processNode(forBody);
                    }
                }
            } else {
                customNodes.add(forBody);
            }
//            int commentIndex = unit.lastTrailingCommentIndex((ForStatement) node);
//            if (commentIndex >= 0) {
//                processBooleanNode(((ForStatement) node), ((ForStatement) node).getExpression());
//            }
            return true;
        } else if (node instanceof EnhancedForStatement) {
            Statement forBody = ((EnhancedForStatement) node).getBody();

            if (forBody instanceof Block) {
                Statement forBodyFirstStatement = (Statement)((Block)forBody).statements().get(0);
                if (!(forBodyFirstStatement instanceof IfStatement)
                        && !(forBodyFirstStatement instanceof WhileStatement)
                        && !(forBodyFirstStatement instanceof ForStatement)
                        && !(forBodyFirstStatement instanceof EnhancedForStatement)) {
                    customNodes.add(forBodyFirstStatement);
                    int commentIndex = unit.lastTrailingCommentIndex(forBodyFirstStatement);
                    if (commentIndex >= 0) {
                        if (forBodyFirstStatement instanceof ReturnStatement) {
                            processReturnNode((ReturnStatement)forBodyFirstStatement);
                        } else {
                            processNode(forBodyFirstStatement);
                        }
                    }
                }
            } else if (!(forBody instanceof IfStatement)
                    && !(forBody instanceof WhileStatement)
                    && !(forBody instanceof ForStatement)
                    && !(forBody instanceof EnhancedForStatement)) {
                customNodes.add(forBody);
                int commentIndex = unit.lastTrailingCommentIndex(forBody);
                if (commentIndex >= 0) {
                    if (forBody instanceof ReturnStatement) {
                        processReturnNode((ReturnStatement)forBody);
                    } else {
                        processNode(forBody);
                    }
                }
            } else {
                customNodes.add(forBody);
            }

            return true;
        } else if (node instanceof ReturnStatement) {
            int commentIndex = unit.lastTrailingCommentIndex(node);
            if (commentIndex >= 0) {
                processReturnNode((ReturnStatement)node);
            }        

            return true;
        } else if (node instanceof MethodDeclaration) {
            MethodDeclaration newNode = (MethodDeclaration) node;

            if (newNode.getName().getFullyQualifiedName().contains(methodName)) {
//                @SuppressWarnings("rawtypes")
//                List statements = newNode.getBody().statements();

//                Statement firstStatement = (Statement) statements.get(0);

//                customNodes.add(firstStatement);

//                Statement lastStatement = (Statement) statements.get(statements.size() - 1);

//                customNodes.add(lastStatement);

                this.method = newNode;

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

}
