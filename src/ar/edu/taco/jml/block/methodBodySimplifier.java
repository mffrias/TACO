package ar.edu.taco.jml.block;

import ar.edu.taco.utils.jml.JmlAstClonerStatementVisitor;
import org.jmlspecs.checker.JmlMethodDeclaration;
import org.multijava.mjc.*;

/**
 * Replaces the body of every method with a single {@code return} of a default value, so that
 * only the specifications are left: {@code return null} for reference types (arrays and type
 * variables included), {@code return 0} for the integral types and {@code char},
 * {@code return 0f} for {@code float} and {@code double}, {@code return false} for
 * {@code boolean}, and {@code return;} for {@code void}. This applies to the methods of every
 * class it visits, nested classes and the pure methods used in specifications included.
 * Constructors keep their bodies (they go to {@code visitJmlConstructorDeclaration}, which this
 * class does not override), and so do interfaces, which {@link JmlAstClonerStatementVisitor}
 * copies as they are.
 *
 * <p>{@code ASTSimplifierManager} adds it last, after {@code ReturnStatementWrapperVisitor}, only
 * if its static {@code isCheckAndAfterRunSpec} is set when the manager is created: in the
 * spec-output run of {@code checkAndRunSpecIfFaulty} and in every later run in the same JVM,
 * since {@code TacoMain.isCheckAndRunSpecAssertionSupport} is never reset
 * ({@code JmlStage.isCheckAndAfterRunSpec}). See docs/SPEC-OUTPUT.md
 * ("checkAndRunSpecIfFaulty") and docs/OPEN-QUESTIONS.md (item 10).
 */
public class methodBodySimplifier extends JmlAstClonerStatementVisitor {

    /**
     * Copies the method (body and specification, by {@code super}) and replaces the copy's body
     * with {@code { return <default value>; }} (see the class comment). The body is replaced
     * also when the method has none, so an abstract or native method gets a body too.
     *
     * @param self the method declaration
     */
    @Override
    public void visitJmlMethodDeclaration(JmlMethodDeclaration self) {
        super.visitJmlMethodDeclaration(self);

        JBlock newBody = null;
        JmlMethodDeclaration clonedDeclaration = (JmlMethodDeclaration)this.getStack().pop();
        JReturnStatement returnStatement = null;
        if (!self.returnType().isVoid()){
            JExpression defaultValueExpre = null;
            if (self.returnType().isReference())
                defaultValueExpre = new JNullLiteral(self.getTokenReference());
            if (self.returnType().isOrdinal())
                defaultValueExpre = new JOrdinalLiteral(self.getTokenReference(), "0");
            if (self.returnType().isFloatingPoint())
                defaultValueExpre = new JRealLiteral(self.getTokenReference(), "0f");
            if (self.returnType().isBoolean())
                defaultValueExpre = new JBooleanLiteral(self.getTokenReference(), false);

            returnStatement = new JReturnStatement(self.getTokenReference(), defaultValueExpre, null);

        } else {
            returnStatement = new JReturnStatement(self.getTokenReference(), null, null);
        }
        newBody = new JBlock(self.getTokenReference(), new JStatement[]{returnStatement}, null);
        clonedDeclaration.setBody(newBody);
        this.getStack().push(clonedDeclaration);
    }
}
