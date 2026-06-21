package ar.edu.taco.jml.block;

import ar.edu.taco.utils.jml.JmlAstClonerStatementVisitor;
import org.jmlspecs.checker.JmlMethodDeclaration;
import org.multijava.mjc.*;

public class methodBodySimplifier extends JmlAstClonerStatementVisitor {

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
