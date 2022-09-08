package ar.edu.taco.stryker.api.impl;

import java.io.IOException;
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

import ar.edu.taco.stryker.api.impl.input.DarwinistInput;
import ar.edu.taco.utils.FileUtils;

import com.google.common.collect.Lists;

public class VariablizationData {

    private static Logger log = Logger.getLogger(VariablizationData.class);

    private Map<Integer, MutablePair<MutablePair<MutablePair<ITypeBinding, ITypeBinding>, Boolean>, MutablePair<MutablePair<List<Expression>, Boolean>, MutablePair<List<Expression>, Boolean>>>> expressions;
    private int lastVariablizedIndex = -1;
    private int lastVarNumber = 0;
    private CompilationUnit unit;
    private MethodDeclaration method;
    private ASTRewrite rewrite;
    private String source;
    private Integer lastVariablizedMutID;
    private Boolean lastVariablizedMutIDRight;
    private Boolean stillFatherable;
    private Boolean UNSAT;
    private Boolean uncompilable;
    private Boolean reachedUnvariablizableExpression;

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
                System.getProperty("java.home") + "/lib/rt.jar"
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
