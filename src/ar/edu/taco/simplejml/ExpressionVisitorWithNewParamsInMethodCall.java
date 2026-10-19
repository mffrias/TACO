package ar.edu.taco.simplejml;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.multijava.mjc.JExpression;
import org.multijava.mjc.JMethodCallExpression;
import org.multijava.mjc.JNewObjectExpression;
import org.multijava.mjc.JSuperExpression;

import ar.edu.jdynalloy.ast.JProgramCall;
import ar.edu.jdynalloy.factory.JExpressionFactory;
import ar.edu.taco.simplejml.helpers.ArgEncoder;
import ar.edu.taco.simplejml.helpers.ExpressionSolver;
import ar.edu.taco.simplejml.methodinfo.MethodInformation;
import ar.edu.taco.simplejml.methodinfo.MethodInformationBuilder;
import ar.uba.dc.rfm.alloy.AlloyTyping;
import ar.uba.dc.rfm.alloy.AlloyVariable;
import ar.uba.dc.rfm.alloy.ast.expressions.AlloyExpression;
import ar.uba.dc.rfm.alloy.ast.expressions.ExprVariable;
import ar.uba.dc.rfm.alloy.ast.formulas.IProgramCall;

/**
 * A variant of {@link ExpressionVisitor} whose only change is the translation of calls
 * ({@link #visitMethodCallExpression}), an older copy of {@code ExpressionVisitor}'s. The typing
 * given to the constructor is kept in a field that only commented-out code reads (it added the
 * typing's variables as extra arguments of each call). Nothing in the three repositories creates
 * this class (2026-10-19). No static state.
 *
 * <p>Unclear: nothing uses it; whether it is still needed is an open question.
 */
public class ExpressionVisitorWithNewParamsInMethodCall extends ExpressionVisitor {

	AlloyTyping varsEncodingValueOfArithmeticOperationsInRequiresAndEnsures;

	public ExpressionVisitorWithNewParamsInMethodCall(AlloyTyping at) {
		this.varsEncodingValueOfArithmeticOperationsInRequiresAndEnsures = at;
	}



	/**
	 * Translates a call like {@link ExpressionVisitor#visitMethodCallExpression}, with the earlier
	 * rules: only a static call's name gets the prefix ({@code <receiver type>_<method>}; an
	 * instance call keeps the bare method name), and a static call gets no receiver. Since
	 * {@code helpers.ArgEncoder} always reserves the receiver position, {@code ArgEncoder.encode}
	 * throws for every static call.
	 *
	 * @param jMethodCallExpression the call
	 * @throws UnsupportedOperationException if an argument is an object creation
	 * @throws IllegalArgumentException (from {@code ArgEncoder}) for a static call, an instance
	 *     call without prefix, or a call that returns a value and has no target
	 */
	public void visitMethodCallExpression(JMethodCallExpression jMethodCallExpression) {
		//		jMethodCallExpression.accept(prettyPrint);
		//		log.debug("Visiting: " + jMethodCallExpression.getClass().getName());
		//		log.debug("Statement: " + prettyPrint.getPrettyPrint());

		List<AlloyExpression> argumentsList = new ArrayList<AlloyExpression>();
		if (jMethodCallExpression.args() != null) {
			for (JExpression expression : jMethodCallExpression.args()) {
				if (expression instanceof JNewObjectExpression) {
					throw new UnsupportedOperationException("Object creation as parameter is not yet supported, please Split this statement");
				}

				expression.accept(this);
				argumentsList.add(this.getAlloyExpression());
			}
		}
		
//		for (AlloyVariable av : this.varsEncodingValueOfArithmeticOperationsInRequiresAndEnsures)
//			argumentsList.add(new ExprVariable(av));
		
		MethodInformation methodInformation = MethodInformationBuilder
				.getInstance().getMethodInformation(jMethodCallExpression);

		boolean isStatic = methodInformation.isStatic();
		boolean isConstructor = methodInformation.isConstructor();
		boolean hasReturnTypeOrReturnValue = methodInformation.hasReturnType();

		ArgEncoder convention = new ArgEncoder(isStatic, isConstructor,
				hasReturnTypeOrReturnValue, argumentsList.size());

		boolean isSuper = (jMethodCallExpression.prefix() instanceof JSuperExpression);

		// Resolve the left side of the call (e.g. this.something() or
		// Integer.something())
		AlloyExpression leftExpression = null;
		if (!isStatic) {
			leftExpression = ExpressionSolver.getLeftExpression(this,
					jMethodCallExpression.prefix());
		}

		Vector<AlloyExpression> encodedArguments = convention.encode(
				leftExpression, JExpressionFactory.THROW_EXPRESSION, this
				.getLeftAssignmentExpression(), argumentsList);
		String methodName = jMethodCallExpression.ident();

		if (isStatic) {
			// JavaClassNameNormalizer classNameNormalizer = new
			// JavaClassNameNormalizer(jMethodCallExpression.method().receiverType().toVerboseString());
			// String classQualifiedName =
			// classNameNormalizer.getQualifiedClassName();
			// methodName = classQualifiedName + "_" + methodName;
			methodName = methodInformation.getQualifiedReceiverType() + "_"
					+ methodName;
		}

		IProgramCall jProgramCall = new JProgramCall(isSuper, methodName,
				encodedArguments);

		super.getStack().push(jProgramCall);
	}
	
	
}

