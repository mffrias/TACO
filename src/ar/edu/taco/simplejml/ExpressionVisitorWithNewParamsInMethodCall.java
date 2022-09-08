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

public class ExpressionVisitorWithNewParamsInMethodCall extends ExpressionVisitor {

	AlloyTyping varsEncodingValueOfArithmeticOperationsInRequiresAndEnsures;

	public ExpressionVisitorWithNewParamsInMethodCall(AlloyTyping at) {
		this.varsEncodingValueOfArithmeticOperationsInRequiresAndEnsures = at;
	}



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

