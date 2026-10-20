package ar.edu.taco.jml;

import org.jmlspecs.checker.JmlMethodDeclaration;
import org.jmlspecs.checker.JmlMethodSpecification;
import org.jmlspecs.checker.JmlSpecCase;
import org.multijava.mjc.JBlock;
import org.multijava.mjc.JBooleanLiteral;
import org.multijava.mjc.JExpression;
import org.multijava.mjc.JIfStatement;
import org.multijava.mjc.JNullLiteral;
import org.multijava.mjc.JOrdinalLiteral;
import org.multijava.mjc.JRealLiteral;
import org.multijava.mjc.JReturnStatement;
import org.multijava.mjc.JStatement;

import ar.edu.taco.utils.jml.JmlAstClonerStatementVisitor;

/**
 * Makes every non-void method body end with a {@code return}: wraps each {@code return}
 * statement in {@code if (true) ...} and appends {@code return <default value>;} to the body
 * ({@code null} for reference types, {@code 0} for the integral types and {@code char},
 * {@code 0f} for {@code float} and {@code double}, {@code false} for {@code boolean}). Since an
 * {@code if} statement can complete normally (JLS 14.21), the appended {@code return} is not
 * unreachable after a body whose returns were wrapped. Returns are wrapped in constructors and
 * {@code void} methods too, which get no extra {@code return}.
 *
 * <p>Unclear: which later step needs the final {@code return}. A body that compiles ends in a
 * {@code return} or a {@code throw} on every path, but earlier passes can change that:
 * {@code WhileRemoverSimplifier} turns {@code while (true) { ... return x; }} into code whose
 * end is reachable.
 *
 * <p>{@code ASTSimplifierManager} runs it after {@code AssumeSimplifierVisitor}, as the last pass
 * unless {@code methodBodySimplifier} follows, during {@code JmlStage} (docs/ARCHITECTURE.md). No
 * static state.
 */
public class ReturnStatementWrapperVisitor extends JmlAstClonerStatementVisitor {

	@Override
	public void visitReturnStatement(/* @non_null */JReturnStatement self) {
		JIfStatement newSelf = new JIfStatement(self.getTokenReference(), new JBooleanLiteral(self.getTokenReference(), true), self, null, null);
		this.getStack().push(newSelf);
	}


	/**
	 * Wraps the returns of the body, appends the default {@code return} if the method is not
	 * {@code void} (see the class comment), and copies the visited specification cases back into
	 * the method's own specification. Changes the given declaration in place
	 * ({@code setBody}) and pushes it, not a copy.
	 *
	 * <p>A non-void method without a body (abstract or native) gets one: a block with a
	 * {@code null} statement followed by the default {@code return}.
	 *
	 * @param self the method declaration
	 */
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

			JReturnStatement returnStatement = new JReturnStatement(self.getTokenReference(), defaultValueExpre, null);
			newBody = new JBlock(self.getTokenReference(), new JStatement[]{newBody, returnStatement}, null);
		}
		self.setBody(newBody);

		if (methodSpecification != null) {
			JmlSpecCase[] specCases = self.methodSpecification().specCases();

			for (int x = 0; x < methodSpecification.specCases().length; x++) {
				specCases[x] = methodSpecification.specCases()[x];
			}
		}

		this.getStack().push(self);
	}

}
