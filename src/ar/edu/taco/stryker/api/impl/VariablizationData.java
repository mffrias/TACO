package ar.edu.taco.stryker.api.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.log4j.Logger;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.BodyDeclaration;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.ParameterizedType;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.WildcardType;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.core.dom.rewrite.ListRewrite;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.TextEdit;

import ar.edu.taco.Java7ClassLibrary;
import ar.edu.taco.stryker.api.impl.input.DarwinistInput;
import ar.edu.taco.utils.FileUtils;

import com.google.common.collect.Lists;

/**
 * State of the variabilization of one failing mutant: the k-variabilization of thesis Chapter 3,
 * built one step at a time. Created by {@link #preprocessVariabilization2(DarwinistInput)} from the
 * loop-unrolled, {@code mutID}-annotated {@code sequential/} copy of the mutant, and advanced by
 * {@link #variablizeNext(DarwinistInput)}, which {@code DarwinistController.variablize} calls
 * before each TACO check. That method also stores the outcome here ({@link #isUNSAT()},
 * {@link #isUncompilable()}, {@link #getReachedUnvariablizableExpression()}), and
 * {@link DarwinistController#computateFeedback(DarwinistInput)} turns it into the
 * {@link ar.edu.taco.stryker.api.impl.input.MuJavaFeedback} sent to {@link MuJavaController}.
 *
 * <p>{@link #getExpressions()} maps each 1-based {@code mutID} (written by
 * {@link StrykerJavaFileInstrumenter#insertMutIDs(DarwinistInput)}) to the data collected by
 * {@link StrykerVariablizerVisitor}, as nested pairs:
 * <pre>
 * getLeft().getLeft().getLeft()    type of the left-hand-side terms (only t.f = ... assignments)
 * getLeft().getLeft().getRight()   type of the right-hand-side expressions
 * getLeft().getRight()             stillFatherable flag recorded for the mutID
 * getRight().getLeft()             (left-hand-side terms, variabilizable?), or null
 * getRight().getRight()            (right-hand-side expressions, variabilizable?)
 * </pre>
 * A mutID has several expressions when loop unrolling replicated its statement; each one gets its
 * own fresh variable. The visitor builds the map as a {@code TreeMap}, so mutIDs are in ascending
 * (source) order; steps take them from the largest mutID down, i.e. bottom-up as in the thesis.
 *
 * <p>All replacements go into a single {@link ASTRewrite} over the original source, so each step
 * writes a file containing every replacement made so far (the variabilization is cumulative).
 * Not thread-safe; only used inside the synchronized
 * {@link DarwinistController#computateFeedback(DarwinistInput)}.
 */
public class VariablizationData {

    private static Logger log = Logger.getLogger(VariablizationData.class);

    /** Collected data per mutID; the layout is described in the class comment. */
    private Map<Integer, MutablePair<MutablePair<MutablePair<ITypeBinding, ITypeBinding>, Boolean>, MutablePair<MutablePair<List<Expression>, Boolean>, MutablePair<List<Expression>, Boolean>>>> expressions;
    /**
     * Position, counted from the largest mutID (0), of the entry variabilized by the last
     * successful step; -1 before the first step.
     */
    private int lastVariablizedIndex = -1;
    /** Number of fresh variables created so far; the next one is {@code customvar_<lastVarNumber>}. */
    private int lastVarNumber = 0;
    private CompilationUnit unit;
    /** Method that receives the fresh parameters (the last matching method seen by the visitor). */
    private MethodDeclaration method;
    /** Accumulates every replacement and added parameter across steps. */
    private ASTRewrite rewrite;
    /** Text of the sequential file when it was parsed; each step re-applies {@link #rewrite} to it. */
    private String source;
    /**
     * 1-based mutID of the entry variabilized by the last successful step, or {@code null} if none.
     * The feedback stores it as {@code skipUntilMutID = lastVariablizedMutID - 1}.
     */
    private Integer lastVariablizedMutID;
    /**
     * Side variabilized by the last successful step: {@code true} for the right-hand side,
     * {@code false} for the left-hand side, {@code null} before the first step. Becomes the
     * feedback's {@code mutateRight}.
     */
    private Boolean lastVariablizedMutIDRight;
    /**
     * {@code stillFatherable} flag of the entry variabilized last (see
     * {@link StrykerVariablizerVisitor}); becomes the feedback's {@code fatherable} when
     * fatherization pruning is on.
     */
    private Boolean stillFatherable;
    /**
     * Negation of the last TACO verdict; set by {@code DarwinistController.variablize} unless the
     * loop ended because a step was uncompilable.
     */
    private Boolean UNSAT;
    /** Whether a step failed to compile or could not be translated by TACO. */
    private Boolean uncompilable;
    /**
     * Whether the loop ended because {@link #variablizeNext} returned {@code false}, i.e. every
     * step was UNSAT and there was nothing more (or nothing allowed) to variabilize.
     */
    private Boolean reachedUnvariablizableExpression;
    /** Fully qualified types of the customvar parameters added so far, in the order they were appended. */
    private final List<String> variablizedParameterTypes = new ArrayList<String>();

    public VariablizationData(String source, CompilationUnit unit, MethodDeclaration method, 
            Map<Integer, MutablePair<MutablePair<MutablePair<ITypeBinding, ITypeBinding>, Boolean>, 
            MutablePair<MutablePair<List<Expression>, Boolean>, MutablePair<List<Expression>, Boolean>>>> expressions) {
        super();
        this.source = source;
        this.unit = unit;
        this.method = method;
        this.expressions = expressions;
        this.rewrite = ASTRewrite.create(unit.getAST());
    }

    public Map<Integer, MutablePair<MutablePair<MutablePair<ITypeBinding, ITypeBinding>, Boolean>, MutablePair<MutablePair<List<Expression>, Boolean>, MutablePair<List<Expression>, Boolean>>>> getExpressions() {
        return expressions;
    }
    
    /** @return whether the visitor collected at least one mutID to variabilize */
    public boolean isVariablizable() {
        return !expressions.isEmpty();
    }

    public CompilationUnit getUnit() {
        return unit;
    }

    public int getLastVariablizedIndex() {
        return lastVariablizedIndex;
    }

    public int getLastVarNumber() {
        return lastVarNumber;
    }

    public ASTRewrite getRewrite() {
        return rewrite;
    }

    public void setLastVariablizedIndex(int lastVariablizedIndex) {
        this.lastVariablizedIndex = lastVariablizedIndex;
    }

    public void setLastVarNumber(int lastVarNumber) {
        this.lastVarNumber = lastVarNumber;
    }

    public String getSource() {
        return source;
    }

    public MethodDeclaration getMethod() {
        return method;
    }

    public Boolean isLastVariablizedMutIDRight() {
        return lastVariablizedMutIDRight;
    }

    public void setLastVariablizedMutIDRight(Boolean lastVariablizedMutIDRight) {
        this.lastVariablizedMutIDRight = lastVariablizedMutIDRight;
    }

    public Integer getLastVariablizedMutID() {
        return lastVariablizedMutID;
    }

    public void setLastVariablizedMutID(Integer lastVariablizedMutID) {
        this.lastVariablizedMutID = lastVariablizedMutID;
    }

    public Boolean isStillFatherable() {
        return stillFatherable;
    }

    public void setStillFatherable(Boolean stillFatherable) {
        this.stillFatherable = stillFatherable;
    }
    
    public Boolean isUNSAT() {
        return UNSAT;
    }

    public void setUNSAT(Boolean UNSAT) {
        this.UNSAT = UNSAT;
    }

    public Boolean isUncompilable() {
        return uncompilable;
    }

    public void setUncompilable(Boolean uncompilable) {
        this.uncompilable = uncompilable;
    }

    public Boolean getReachedUnvariablizableExpression() {
        return reachedUnvariablizableExpression;
    }

    public void setReachedUnvariablizableExpression(Boolean reachedUnvariablizableExpression) {
        this.reachedUnvariablizableExpression = reachedUnvariablizableExpression;
    }

    /**
     * Parses the sequential copy of the mutant and collects what can be variabilized. The file is
     * {@code getSeqVariablizedFilename()}, which is set from {@code getSeqFilesPrefix()} if still
     * {@code null}. It is parsed with JDT (JLS4), resolving bindings against
     * {@code <user.dir>/bin} and {@code <java.home>/lib/rt.jar} (a pre-Java 9 JRE layout), and
     * {@link StrykerVariablizerVisitor} is run over every method of a top-level class whose name
     * contains {@code getMethod()}. This is a substring match: when repairing {@code add}, a method
     * {@code addAll} would be visited too and, if it comes later in the class, recorded as the
     * method that receives the fresh parameters. A read error is ignored (the source is then
     * empty).
     *
     * <p>The inline comments below describe an earlier design that inserted output statements;
     * the visitor now only collects expressions.
     *
     * @param darwinistInput the feedback request
     * @return the variabilization state, before any step
     */
    public static VariablizationData preprocessVariabilization2(DarwinistInput darwinistInput) {
        String variablizedFilename = darwinistInput.getSeqVariablizedFilename();
        if (variablizedFilename == null) {
            variablizedFilename = darwinistInput.getSeqFilesPrefix();
            darwinistInput.setSeqVariablizedFilename(variablizedFilename);
        }
        String source = "";

        try {
            source = FileUtils.readFile(variablizedFilename);
        } catch (final IOException e1) {
            // TODO: Define what to do!
        }

        final IDocument document = new Document(source);

        final org.eclipse.jdt.core.dom.ASTParser parser = org.eclipse.jdt.core.dom.ASTParser.newParser(org.eclipse.jdt.core.dom.AST.JLS4);
        parser.setKind(org.eclipse.jdt.core.dom.ASTParser.K_COMPILATION_UNIT);
        parser.setResolveBindings(true);

        parser.setEnvironment(new String[] {
                System.getProperty("user.dir") + OpenJMLController.FILE_SEP + "bin", 
                Java7ClassLibrary.path()
        }, 
        null, null, false);
        parser.setUnitName(variablizedFilename);
        parser.setSource(document.get().toCharArray());
        // Parse the source code and generate an AST.
        final CompilationUnit unit = (CompilationUnit) parser.createAST(null);
        // to iterate through methods
        StrykerVariablizerVisitor visitor = new StrykerVariablizerVisitor(darwinistInput, unit, source, unit.getAST());

        // to iterate through methods
        @SuppressWarnings("unchecked")
        final List<AbstractTypeDeclaration> types = unit.types();
        for (final AbstractTypeDeclaration type : types) {
            if (type.getNodeType() == ASTNode.TYPE_DECLARATION) {
                // Class def found
                @SuppressWarnings("unchecked")
                final List<BodyDeclaration> bodies = type.bodyDeclarations();
                for (final BodyDeclaration body : bodies) {
                    if (body.getNodeType() == ASTNode.METHOD_DECLARATION) {
                        final MethodDeclaration method = (MethodDeclaration)body;
                        if (method.getName().toString().contains(darwinistInput.getMethod())) {
                            //First, we want to add some instructions as first lines of the method to create the output
                            //file for this method, where the sequential code is going to be outputted.
                            //Then, the visitor has to inspect every line of code and insert an output instruction to the
                            //previously created file, containing the exact line that just run, to obtain
                            //the secuential code branch. If it is a guard, replace it and brackets with an assert.

                            //To do this, we will implement an ASTVisitor that does everything we want, and we will
                            //give it the AST Tree to visit starting at this method.
                            visitor.setMethodName(method.getName().toString());
                            method.accept(visitor);
                        }
                    }
                }
            }
        }

        return visitor.buildVariablizationData();

    }

    /**
     * Performs one variabilization step and writes the cumulative result to
     * {@code darwinistInput.getSeqVariablizedFilename()}. Entries are taken from the largest mutID
     * down (bottom-up). The right-hand side of an entry is variabilized first; the next call
     * variabilizes the left-hand side of the same entry if it has one (only {@code t.f = ...}
     * assignments do), and otherwise moves to the right-hand side of the next entry. Since
     * {@code DarwinistController.variablize} runs TACO after every step, the two sides of a
     * statement are checked separately.
     *
     * <p>Every expression of the chosen side is replaced by a fresh variable
     * {@code customvar_<n>} (numbered across steps) that is appended to the method's parameters,
     * with the type given by the recorded binding ({@link #typeFromBinding}). On success it
     * updates {@code lastVariablizedIndex}, {@code lastVarNumber},
     * {@code lastVariablizedMutIDRight}, {@code lastVariablizedMutID} and {@code stillFatherable}.
     * If a recorded binding is {@code null}, the expression is appended to
     * {@code typebindingnullexpressions.txt} in {@code user.dir} and {@link #typeFromBinding} then
     * throws a {@link NullPointerException} that propagates to the caller. Errors while applying
     * the rewrite or writing the file are ignored.
     *
     * @param darwinistInput the feedback request (only its filenames are used)
     * @return {@code false}, leaving the state unchanged, if every entry has already been
     *         variabilized or the chosen side is marked as not variabilizable (see
     *         {@link StrykerVariablizerVisitor}); {@code true} otherwise
     */
    public boolean variablizeNext(final DarwinistInput darwinistInput) {

        String variablizedFilename = darwinistInput.getSeqVariablizedFilename();
        if (variablizedFilename == null) {
            variablizedFilename = darwinistInput.getSeqFilesPrefix();
            darwinistInput.setSeqVariablizedFilename(variablizedFilename);
        }

        Integer previousVar = getLastVarNumber();

        final IDocument document = new Document(getSource());

        String varPrefix = "customvar_";
        ASTRewrite rewrite = getRewrite();

        Map<Integer, MutablePair<MutablePair<MutablePair<ITypeBinding, ITypeBinding>, Boolean>, MutablePair<MutablePair<List<Expression>, Boolean>, MutablePair<List<Expression>, Boolean>>>> expressions = getExpressions();
        List<Integer> mutIDs = Lists.newArrayList(expressions.keySet());

        int curVariablizationIndex;
        boolean right = true;

        if ((isLastVariablizedMutIDRight() == null || !isLastVariablizedMutIDRight() || expressions.get(mutIDs.get(expressions.size() - 1 - getLastVariablizedIndex())).getRight().getLeft() == null)) {
            if (getLastVariablizedIndex() + 1 == getExpressions().size()) {
                return false;
            } else {
                curVariablizationIndex = getLastVariablizedIndex() + 1;
            }
        } else {
            curVariablizationIndex = getLastVariablizedIndex();
            right = false;
        }

        AST ast = getUnit().getAST();
        MethodDeclaration method = getMethod();

        MutablePair<MutablePair<MutablePair<ITypeBinding, ITypeBinding>, Boolean>, MutablePair<MutablePair<List<Expression>, Boolean>, MutablePair<List<Expression>, Boolean>>> curMut = expressions.get(mutIDs.get(expressions.size() - 1 - curVariablizationIndex));
        ITypeBinding binding = right ? curMut.getLeft().getLeft().getRight() : curMut.getLeft().getLeft().getLeft();
        MutablePair<List<Expression>, Boolean> expressionsToVariablizePair = right ? curMut.getRight().getRight() : curMut.getRight().getLeft();

        if (!expressionsToVariablizePair.getRight()) {
            return false;
        }
        
        for (Expression expression : expressionsToVariablizePair.getLeft()) {
            //Generamos un nuevo nombre de variable en funcion de los ya asignados
            String variableName = varPrefix + previousVar;
            //Debo reemplazar la RHS por una variable del mismo tipo
            //Dicha variable hay que agregarla como argumento al metodo
            //Nueva variable:
            SingleVariableDeclaration variableDeclaration = ast.newSingleVariableDeclaration();
            //Tipo de la asignacion
            if (binding == null) {
                log.error("Variablization: The binding is null");
                try {
                    FileUtils.appendToFile(System.getProperty("user.dir") + OpenJMLController.FILE_SEP + 
                            "typebindingnullexpressions.txt", expression.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                     // TODO: Define what to do!
                }
            }
            Type assignmentType = typeFromBinding(ast, binding);
            variablizedParameterTypes.add(qualifiedTypeName(binding));
            //Seteamos el tipo
            try {
                variableDeclaration.setType(assignmentType);
            } catch (Exception e) {
                log.error(e.getMessage());
            }
            //Seteamos el nombre de la variable
            SimpleName variableSimpleName = ast.newSimpleName(variableName);
            variableDeclaration.setName(variableSimpleName);
            //Agregamos la nueva variable a los parametros del metodo
            ListRewrite lr = rewrite.getListRewrite(method, MethodDeclaration.PARAMETERS_PROPERTY);
            lr.insertLast(variableDeclaration, null);
            //Reemplazamos la parte derecha de la asignacion por la nueva variable
            rewrite.replace(expression, variableSimpleName, null);
            previousVar++;
        }

        setLastVarNumber(previousVar);

        setLastVariablizedIndex(curVariablizationIndex);

        setLastVariablizedMutIDRight(right);

        //Reescribimos el archivo con los metodos secuenciales que fallaron pero variabilizados
        final TextEdit edits = rewrite.rewriteAST(document, null);
        try {
            edits.apply(document);
        } catch (MalformedTreeException | BadLocationException e) {
            // TODO: Define what to do!
        }
        try {
            FileUtils.writeToFile(variablizedFilename, document.get());
            log.warn("Variablization: Current variabilization filename is: " + variablizedFilename);
        } catch (final IOException e) {
            // TODO: Define what to do!
        }

        setStillFatherable(curMut.getLeft().getRight());
        setLastVariablizedMutID(mutIDs.get(expressions.size() - 1 - curVariablizationIndex));

        return true;
    }

    /**
     * The types of the customvar parameters that variabilization has appended
     * to the method so far, fully qualified and in order. TACO needs them to
     * name the variabilized method (see {@link ar.edu.taco.stryker.MethodToCheck}).
     */
    public List<String> getVariablizedParameterTypes() {
        return variablizedParameterTypes;
    }

    private static String qualifiedTypeName(ITypeBinding typeBinding) {
        if (typeBinding.isPrimitive()) {
            return typeBinding.getName();
        }
        return typeBinding.getErasure().getQualifiedName();
    }

    /**
     * Converts a JDT type binding into a type node of {@code ast}, used as the declared type of a
     * fresh variable. Primitive, array and parameterized types are rebuilt structurally, capture
     * bindings become wildcard types, and any other type is written with its fully qualified name.
     *
     * @return the type node, or {@code null} (after logging) if the name cannot be turned into a
     *         simple type
     * @throws NullPointerException if {@code ast} or {@code typeBinding} is {@code null}
     * @throws IllegalArgumentException if the type has no qualified name (e.g. local or anonymous
     *         classes)
     */
    public Type typeFromBinding(AST ast, ITypeBinding typeBinding) {
        if( ast == null ) 
            throw new NullPointerException("ast is null");
        if( typeBinding == null )
            throw new NullPointerException("typeBinding is null");

        if( typeBinding.isPrimitive() ) {
            return ast.newPrimitiveType(
                    PrimitiveType.toCode(typeBinding.getName()));
        }

        if( typeBinding.isCapture() ) {
            ITypeBinding wildCard = typeBinding.getWildcard();
            WildcardType capType = ast.newWildcardType();
            capType.setBound(typeFromBinding(ast, wildCard.getBound()),
                    wildCard.isUpperbound());
            return capType;
        }

        if( typeBinding.isArray() ) {
            Type elType = typeFromBinding(ast, typeBinding.getElementType());
            return ast.newArrayType(elType, typeBinding.getDimensions());
        }

        if( typeBinding.isParameterizedType() ) {
            ParameterizedType type = ast.newParameterizedType(
                    typeFromBinding(ast, typeBinding.getErasure()));

            @SuppressWarnings("unchecked")
            List<Type> newTypeArgs = type.typeArguments();
            for( ITypeBinding typeArg : typeBinding.getTypeArguments() ) {
                newTypeArgs.add(typeFromBinding(ast, typeArg));
            }

            return type;
        }

        // simple or raw type
        String qualName = typeBinding.getQualifiedName();
        if( "".equals(qualName) ) {
            throw new IllegalArgumentException("No name for type binding.");
        }

        SimpleType ret = null;
        try {
            ret = ast.newSimpleType(ast.newName(qualName));
        } catch (Exception e) {
            log.error(e);
        }
        return ret; 
    }
}
