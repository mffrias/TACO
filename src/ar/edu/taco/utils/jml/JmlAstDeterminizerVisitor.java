package ar.edu.taco.utils.jml;

import java.util.*;

//import com.sun.jmx.remote.internal.ArrayQueue;
//import org.jmlspecs.checker.JmlAssertStatement;
//import org.jmlspecs.checker.JmlAssignableClause;
//import org.jmlspecs.checker.JmlAssignmentStatement;
//import org.jmlspecs.checker.JmlAssumeStatement;
//import org.jmlspecs.checker.JmlClassDeclaration;
//import org.jmlspecs.checker.JmlCompilationUnit;
//import org.jmlspecs.checker.JmlConstraint;
//import org.jmlspecs.checker.JmlConstructorDeclaration;
//import org.jmlspecs.checker.JmlEnsuresClause;
//import org.jmlspecs.checker.JmlExceptionalBehaviorSpec;
//import org.jmlspecs.checker.JmlExceptionalSpecBody;
//import org.jmlspecs.checker.JmlExceptionalSpecCase;
//import org.jmlspecs.checker.JmlFieldDeclaration;
//import org.jmlspecs.checker.JmlGeneralSpecCase;
//import org.jmlspecs.checker.JmlGenericSpecBody;
//import org.jmlspecs.checker.JmlGenericSpecCase;
//import org.jmlspecs.checker.JmlInformalExpression;
//import org.jmlspecs.checker.JmlInterfaceDeclaration;
//import org.jmlspecs.checker.JmlInvariant;
//import org.jmlspecs.checker.JmlLoopStatement;
//import org.jmlspecs.checker.JmlMethodDeclaration;
//import org.jmlspecs.checker.JmlMethodSpecification;
//import org.jmlspecs.checker.JmlNormalBehaviorSpec;
//import org.jmlspecs.checker.JmlNormalSpecBody;
//import org.jmlspecs.checker.JmlNormalSpecCase;
//import org.jmlspecs.checker.JmlPredicate;
//import org.jmlspecs.checker.JmlRefinePrefix;
//import org.jmlspecs.checker.JmlRepresentsDecl;
//import org.jmlspecs.checker.JmlRequiresClause;
//import org.jmlspecs.checker.JmlSetStatement;
//import org.jmlspecs.checker.JmlSignalsClause;
//import org.jmlspecs.checker.JmlSignalsOnlyClause;
//import org.jmlspecs.checker.JmlSourceMethod;
//import org.jmlspecs.checker.JmlSpecBodyClause;
//import org.jmlspecs.checker.JmlSpecCase;
//import org.jmlspecs.checker.JmlSpecification;
//import ar.edu.taco.TacoConfigurator;
import ar.edu.taco.TacoConfigurator;
import ar.edu.taco.TacoException;
//import com.sun.org.apache.xalan.internal.xsltc.compiler.util.StringStack;
import org.jmlspecs.checker.*;
import org.multijava.mjc.*;
//import org.multijava.util.compiler.JavaStyleComment;
//import org.multijava.util.compiler.TokenReference;

//import ar.edu.taco.TacoException;
//import ar.edu.taco.TacoNotImplementedYetException;
//import ar.edu.taco.jml.utils.ASTUtils;
import ar.edu.taco.simplejml.JmlBaseVisitor;
import org.multijava.util.compiler.JavaStyleComment;
import org.multijava.util.compiler.TokenReference;

public class JmlAstDeterminizerVisitor extends JmlBaseVisitor {

    //private Stack<Object> stack = new Stack<Object>();
    private Queue<Object> theQueue = new LinkedList<Object>();

    private boolean hasBeenSplit = false;

    public Queue<Object> getQueue() {
        return theQueue;
    }

    @Override
    public void visitJmlCompilationUnit(JmlCompilationUnit self) {

        TokenReference where = self.getTokenReference();
        JPackageName package_name = self.packageName();
        CCompilationUnit export = null;
        JPackageImportType[] imported_packages = self.importedPackages();

        @SuppressWarnings("rawtypes")
        ArrayList imported_units = new ArrayList();
        Collections.addAll(imported_units, self.importedUnits());
        JTypeDeclarationType[] typeDeclarations = self.typeDeclarations();
        JTypeDeclarationType[] new_type_declarationsFirst = new JTypeDeclarationType[typeDeclarations.length];
        JTypeDeclarationType[] new_type_declarationsSecond = new JTypeDeclarationType[typeDeclarations.length];

        boolean somethingWasSplit = false;
        for (int i = 0; i < typeDeclarations.length; i++) {
            typeDeclarations[i].accept(this);
            if (this.getQueue().size() > 1) {
                somethingWasSplit = true;
                Object ret_val = this.getQueue().poll();
                JTypeDeclarationType cloned_type_declaration_first = (JTypeDeclarationType) ret_val;
                new_type_declarationsFirst[i] = cloned_type_declaration_first;
                ret_val = this.getQueue().poll();
                JTypeDeclarationType cloned_type_declaration_second = (JTypeDeclarationType) ret_val;
                new_type_declarationsSecond[i] = cloned_type_declaration_second;
            } else {    // if (hasBeenSplit)
                Object ret_val = this.getQueue().poll();
                JTypeDeclarationType cloned_type_declaration = (JTypeDeclarationType) ret_val;
                new_type_declarationsFirst[i] = cloned_type_declaration;
                new_type_declarationsSecond[i] =cloned_type_declaration;
            }
        }
        @SuppressWarnings("rawtypes")
        ArrayList<JmlMethodDeclaration> top_level_methods = self.tlMethods();
        JmlRefinePrefix refinePrefix = self.refinePrefix();
        JmlCompilationUnit compilationUnitFirst = new JmlCompilationUnit(where, package_name, export, imported_packages, imported_units, new_type_declarationsFirst,
                top_level_methods, refinePrefix);
        this.getQueue().offer(compilationUnitFirst);
        if (somethingWasSplit) {    // -> if (hasBeenSplit)
            JmlCompilationUnit compilationUnitSecond = new JmlCompilationUnit(where, package_name, export, imported_packages, imported_units, new_type_declarationsSecond,
                    top_level_methods, refinePrefix);
            this.getQueue().offer(compilationUnitSecond);
        }
    }


    @Override
    public void visitJmlClassDeclaration(JmlClassDeclaration self) {

        ArrayList<JPhylum> newFieldsAndInit = new ArrayList<JPhylum>();
        for (int i = 0; i < self.fieldsAndInits().length; i++) {
            if (self.fieldsAndInits()[i] instanceof JClassBlock) {
                newFieldsAndInit.add(self.fieldsAndInits()[i]);
            } else {
                if (self.fieldsAndInits()[i] instanceof JFieldDeclarationType) {
                    if (!(self.fieldsAndInits()[i] instanceof JmlFieldDeclaration)) {
                        throw new TacoException("Simplifier error: Must be a JMLField");
                    }
                    JmlFieldDeclaration jFieldDeclarationType = (JmlFieldDeclaration) self.fieldsAndInits()[i];
                    jFieldDeclarationType.accept(this);
                    JPhylum newFieldDeclarationType = (JPhylum) this.getQueue().poll();
                    newFieldsAndInit.add(newFieldDeclarationType);

                }
            }
        }

        ArrayList<JmlMethodDeclaration> newMethodsFirst = new ArrayList<JmlMethodDeclaration>();
        ArrayList<JmlMethodDeclaration> newMethodsSecond = new ArrayList<JmlMethodDeclaration>();
        boolean somethingWasSplit = false;
        for (JmlMethodDeclaration methodDeclaration : (ArrayList<JmlMethodDeclaration>) self.methods()) {
            // break here for detection
            String currentMethodName = this.getClassName(self) + methodDeclaration.ident();
            String methodToCheck = TacoConfigurator.getInstance().getMethodToCheck();
            methodToCheck = methodToCheck.substring(0, methodToCheck.indexOf('('));
            if (currentMethodName.equals(methodToCheck)) {
                methodDeclaration.accept(this);
                if (this.getQueue().size() > 1) {
                    somethingWasSplit = true;
                    Object ret_val = this.getQueue().poll();
                    newMethodsFirst.add((JmlMethodDeclaration) ret_val);
                    ret_val = this.getQueue().poll();
                    newMethodsSecond.add((JmlMethodDeclaration) ret_val);
                } else {
                    Object ret_val = this.getQueue().poll();
                    newMethodsFirst.add((JmlMethodDeclaration) ret_val);
                    newMethodsSecond.add((JmlMethodDeclaration) ret_val);
                }
            } else {
                newMethodsFirst.add(methodDeclaration);
                newMethodsSecond.add(methodDeclaration);
            }


        }

        List<JFieldDeclarationType> jModelFieldDeclarationTypeList = null;
        if (self.getModelFields() != null) {
            jModelFieldDeclarationTypeList = new ArrayList<JFieldDeclarationType>();
            for (JFieldDeclarationType jFieldDeclarationType : self.getModelFields()) {
                jFieldDeclarationType.accept(this);
                JFieldDeclarationType newFieldDeclarationType = (JFieldDeclarationType) this.getQueue().poll();
                jModelFieldDeclarationTypeList.add(newFieldDeclarationType);
            }

        }

        List<JmlInvariant> jmlInvariantList = null;
        if (self.invariants() != null) {
            jmlInvariantList = new ArrayList<JmlInvariant>();
            for (JmlInvariant jmlInvariant : self.invariants()) {
                jmlInvariant.accept(this);
                jmlInvariantList.add((JmlInvariant) this.getQueue().poll());
            }
        }

        List<JmlRepresentsDecl> jmlRepresentsDeclList = null;
        if (self.representsDecls() != null) {
            jmlRepresentsDeclList = new ArrayList<JmlRepresentsDecl>();
            for (JmlRepresentsDecl jmlRepresentsDecl : self.representsDecls()) {
                jmlRepresentsDecl.accept(this);
                jmlRepresentsDeclList.add((JmlRepresentsDecl) this.getQueue().poll());
            }
        }

        ArrayList<JmlClassDeclaration> newInners = null;
        if (self.inners() != null) {
            newInners = new ArrayList<JmlClassDeclaration>();
            for (JmlClassDeclaration innerClassDeclaration : (ArrayList<JmlClassDeclaration>) self.inners()) {
                innerClassDeclaration.accept(this);
                newInners.add((JmlClassDeclaration) this.getQueue().poll());
            }
        }

        ArrayList<JmlConstraint> newConstraints = null;
        if (self.constraints() != null) {
            newConstraints = new ArrayList<JmlConstraint>();
            for (JmlConstraint constraint : self.constraints()) {
                constraint.accept(this);
                newConstraints.add((JmlConstraint) this.getQueue().poll());
            }
        }


        self.setFields(newFieldsAndInit.toArray(new JPhylum[0]));
        JmlClassDeclaration newJmlClassDeclaration = new JmlClassDeclarationExtension(self, jmlInvariantList.toArray(new JmlInvariant[0]),
                jmlRepresentsDeclList.toArray(new JmlRepresentsDecl[0]), newConstraints.toArray(new JmlConstraint[0]), newMethodsFirst,
                jModelFieldDeclarationTypeList, newInners);
        this.getQueue().offer(newJmlClassDeclaration);
        if (somethingWasSplit) { // if (hasBeenSplit)
            self.setFields(newFieldsAndInit.toArray(new JPhylum[0]));
            newJmlClassDeclaration = new JmlClassDeclarationExtension(self, jmlInvariantList.toArray(new JmlInvariant[0]),
                    jmlRepresentsDeclList.toArray(new JmlRepresentsDecl[0]), newConstraints.toArray(new JmlConstraint[0]), newMethodsSecond,
                    jModelFieldDeclarationTypeList, newInners);
            this.getQueue().offer(newJmlClassDeclaration);
        }
    }


    @Override
    public void visitJmlMethodDeclaration(JmlMethodDeclaration self) {
        // hasBeenSplit
        boolean somethingWasSplit = false;
        JBlock newBodyFirst = null;
        JBlock newBodySecond = null;
        if (self.body() == null) {
            newBodyFirst = null;
        } else {
            self.body().accept(this);
            if (this.getQueue().size() > 1)
                // hasBeenSplit = true;
                somethingWasSplit = true;
            newBodyFirst = (JBlock) this.getQueue().poll();
            if (somethingWasSplit){ // -> if (hasBeenSplit)
                // hasBeenSplit = false; ?
                newBodySecond = (JBlock) this.getQueue().poll();
            }
        }

        JmlMethodSpecification methodSpecification;
        if (self.hasSpecification()) {
            self.methodSpecification().accept(this);
            methodSpecification = (JmlMethodSpecification) this.getQueue().poll();
        } else {
            methodSpecification = null;
        }

        JmlSpecCase[] specCases = null;
        if (methodSpecification != null) {
            specCases = self.methodSpecification().specCases();

            for (int x = 0; x < methodSpecification.specCases().length; x++) {
                specCases[x] = methodSpecification.specCases()[x];
            }
        }

        JmlMethodDeclaration newMethodDeclFirst = JmlMethodDeclaration.makeInstance(self.getTokenReference(), self.modifiers(), self.typevariables(), self.returnType(), self.ident(), self.parameters(), self.getExceptions(), newBodyFirst, self.javadocComment(), new JavaStyleComment[0], self.methodSpecification());
        this.getQueue().offer(newMethodDeclFirst);
        if (somethingWasSplit){

            JmlMethodDeclaration newMethodDeclSecond = JmlMethodDeclaration.makeInstance(self.getTokenReference(), self.modifiers(), self.typevariables(), self.returnType(), self.ident(), self.parameters(), self.getExceptions(), newBodySecond, self.javadocComment(), new JavaStyleComment[0], self.methodSpecification());

            this.getQueue().offer(newMethodDeclSecond);
        }
    }

    public void visitBlockStatement(/* @non_null */JBlock self) {
        JStatement[] newBodyFirst = new JStatement[self.body().length];
        JStatement[] newBodySecond = new JStatement[self.body().length];
        boolean somethingWasSplit = false;
        for (int i = 0; i < self.body().length; i++) {
            self.body()[i].accept(this);
            if (this.getQueue().size() == 2){

                somethingWasSplit = true;
                newBodyFirst[i] = (JStatement) this.getQueue().poll();
                newBodySecond[i] = (JStatement) this.getQueue().poll();
            }
//            newBodyFirst[i] = (JStatement) this.getQueue().peek();
//            newBodySecond[i] = (JStatement) this.getQueue().poll();
        }
        this.getQueue().offer(new JBlock(self.getTokenReference(), newBodyFirst, self.getComments()));

        if (somethingWasSplit){
            this.getQueue().offer(new JBlock(self.getTokenReference(), newBodySecond, self.getComments()));
        }
    }

    public void visitReturnStatement(/* @non_null */JReturnStatement self) {
        this.getQueue().offer(self);
    }
    public void visitJmlSpecification(JmlSpecification self) {
        JmlSpecification newSelf;
        if (self.hasSpecCases()) {
            List<JmlSpecCase> jmlSpecCases = null;
            jmlSpecCases = new ArrayList<JmlSpecCase>();

            for (JmlSpecCase jmlSpecCase : self.specCases()) {
                jmlSpecCase.accept(this);
                jmlSpecCases.add((JmlSpecCase) this.getQueue().poll());
            }
            newSelf = new JmlSpecification(self.getTokenReference(), jmlSpecCases.toArray(new JmlSpecCase[0]), self.redundantSpec());
        } else {
            newSelf = new JmlSpecification(self.getTokenReference(), self.redundantSpec());
        }

        this.getQueue().offer(newSelf);
    }

    public void visitJmlGenericSpecCase(JmlGenericSpecCase self) {
        List<JmlRequiresClause> jmlSpecHeader = new ArrayList<JmlRequiresClause>();
        JmlGenericSpecBody jmlSpecBodys = null;

        if (self.hasSpecHeader()) {
            for (JmlRequiresClause jmlRequiresClause : self.specHeader()) {
                jmlRequiresClause.accept(this);
                jmlSpecHeader.add((JmlRequiresClause) this.getQueue().poll());
            }
        }

        if (self.hasSpecBody()) {
            self.specBody().accept(this);
            jmlSpecBodys = (JmlGenericSpecBody) this.getQueue().poll();
        }

        JmlGenericSpecCase newSelf = new JmlGenericSpecCase(self.getTokenReference(), self.specVarDecls(), jmlSpecHeader.toArray(new JmlRequiresClause[0]),
                jmlSpecBodys);
        this.getQueue().offer(newSelf);

    }

    public void visitJmlGenericSpecBody(JmlGenericSpecBody self) {
        List<JmlSpecBodyClause> specClauses;
        JmlGenericSpecBody newSelf;
        if (self.isSpecClauses()) {
            specClauses = new ArrayList<JmlSpecBodyClause>();
            for (JmlSpecBodyClause jmlSpecBodyClause : self.specClauses()) {
                jmlSpecBodyClause.accept(this);
                specClauses.add((JmlSpecBodyClause) this.getQueue().poll());
            }
            newSelf = new JmlGenericSpecBody(specClauses.toArray(new JmlSpecBodyClause[0]));
        } else {
            newSelf = self;
        }
        this.getQueue().offer(newSelf);

    }

    public void visitJmlEnsuresClause(JmlEnsuresClause self) {
        if (self.isNotSpecified()) {
            throw new IllegalArgumentException("Ensures clause is not specified.");
        }

        JmlEnsuresClause newSelf = new JmlEnsuresClause(self.getTokenReference(), self.isRedundantly(), self.predOrNot());
        this.getQueue().offer(newSelf);
    }

    public void visitVariableDeclarationStatement(/* @non_null */JVariableDeclarationStatement self) {
        this.getQueue().offer(self);

    }

    public void visitJmlRequiresClause(JmlRequiresClause self) {
        if (self.isNotSpecified()) {
            throw new IllegalArgumentException("Requires clause is not specified.");
        }

        JmlRequiresClause newSelf = new JmlRequiresClause(self.getTokenReference(), self.isRedundantly(), self.predOrNot());
        this.getQueue().offer(newSelf);

    }

    public void visitIfStatement(/* @non_null */JIfStatement self) {
        JStatement FP = null;
        JStatement SP = null;
        if (!hasBeenSplit) {
            hasBeenSplit = true;
            JStatement theThenBranchCode = self.thenClause();
            theThenBranchCode.accept(this);
            JStatement theClonedThenBranchCode = (JStatement) this.theQueue.poll();
            JStatement branchAssertThenCase = new JAssertStatement(self.getTokenReference(), self.cond(), self.getComments());
            JStatement[] FPBeforeBeingABlock = new JStatement[2];
            FPBeforeBeingABlock[0] = branchAssertThenCase;
            FPBeforeBeingABlock[1] = theClonedThenBranchCode;
            FP = new JBlock(self.getTokenReference(), FPBeforeBeingABlock, self.getComments());

            if (self.elseClause() != null) {
                JStatement theElseBranchCode = self.elseClause();
                theElseBranchCode.accept(this);

                JStatement theClonedElseBranchCode = (JStatement) this.theQueue.poll();
                JExpression theCondition = self.cond();
                JExpression theNegatedCondition = new JUnaryExpression(theCondition.getTokenReference(),13, theCondition);
                JStatement branchAssertElseCase = new JAssertStatement(self.getTokenReference(),theNegatedCondition,self.getComments());
                JStatement[] SPBeforeBeingABlock = new JStatement[2];
                SPBeforeBeingABlock[0] = branchAssertElseCase;
                SPBeforeBeingABlock[1] = theClonedElseBranchCode;
                SP = new JBlock(self.getTokenReference(), SPBeforeBeingABlock, self.getComments());

            }

            this.getQueue().offer(FP);
            if (self.elseClause() != null)
                this.getQueue().offer(SP);




        }
    }

    private String getClassName(JmlClassDeclaration self) {
        String cname = self.getCClass().qualifiedName();
        cname = cname.replace('/', '_');
        cname = cname + "_";
        return cname;
    }

}
