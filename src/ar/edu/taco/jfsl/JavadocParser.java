package ar.edu.taco.jfsl;

import java.util.List;
import java.util.StringTokenizer;

import edu.mit.csail.sdg.annotations.spec.ParserException;

import ar.edu.jdynalloy.factory.JExpressionFactory;
import ar.edu.jdynalloy.factory.JPredicateFactory;
import ar.uba.dc.rfm.alloy.ast.expressions.AlloyExpression;
import ar.uba.dc.rfm.alloy.ast.formulas.AlloyFormula;

public class JavadocParser {

	private static final String EXCEPTIONAL_BEHAVIOR_TAG = "@ExceptionalBehavior";
	private static final String NORMAL_BEHAVIOR_TAG = "@NormalBehavior";
	private static final String ENSURES_TAG = "@Ensures";
	private static final String REQUIRES_TAG = "@Requires";
	private static final String SPEC_FIELD_TAG = "@SpecField";
	private static final String INVARIANT_TAG = "@Invariant";
	private static final String THROWS_TAG = "@Throws";
	private static final String RETURNS_TAG = "@Returns";
	private static final String MODIFIES_TAG = "@Modifies";
	private static final String MODIFIES_EVERYTHING_TAG = "@Modifies_Everything";

	private JfslToJDynAlloyEnv env;

	public JavadocParser(JfslToJDynAlloyEnv javaContext) {
		this.env = javaContext;
	}

	public JfslClassSpecification parse_class_comments(String javadoccomments) {

		StringTokenizer tokenizer = new StringTokenizer(javadoccomments);

		JfslClassSpecification class_specification = new JfslClassSpecification();

		while (tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken();

			if (token.equals(INVARIANT_TAG)) {
				String invariant_str = parse_until_semicolon(tokenizer);
				JfslParser jfslTranslator = new JfslParser(this.env);

				try {
					List<AlloyFormula> invariant_formulas = jfslTranslator
							.translateInvariant(invariant_str);
					class_specification.invariant.addAll(invariant_formulas);
				} catch (ParserException ex) {
					System.err.println("Couldn't parse JFSL Invariant annotation");
					System.err.println("Problematic annotation source");
					System.err.println(invariant_str);
					throw ex;
				}

			} else if (token.equals(SPEC_FIELD_TAG)) {
				String spec_field_str = parse_until_semicolon(tokenizer);
				JfslParser jfslTranslator = new JfslParser(env);
				JfslSpecField spec_field = jfslTranslator
						.translateSpecField(spec_field_str);
				class_specification.spec_fields.add(spec_field);

			} else {
				// skip
			}
		}
		return class_specification;
	}

	private String parse_until_semicolon(StringTokenizer tokenizer) {
		StringBuffer buff = new StringBuffer();
		while (tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken();
			if (token.endsWith(";")) {
				buff.append(" " + token.substring(0, token.length() - 1));
				break;
			} else
				buff.append(" " + token);
		}
		return buff.toString();

	}

	public JfslMethodSpecification parse_method_comments(String javadoccomments) {

		JfslMethodSpecification method_specification = new JfslMethodSpecification();
		JfslBehaviorCase currentBehavior = new JfslBehaviorCase();
		boolean currentBehaviorIsNormal = true;
		method_specification.spec_cases.add(currentBehavior);

		StringTokenizer tokenizer = new StringTokenizer(javadoccomments);
		while (tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken();

			if (token.equals(REQUIRES_TAG)) {
				String requires_str = parse_until_semicolon(tokenizer);
				JfslParser jfslTranslator = new JfslParser(env);
				List<AlloyFormula> requires_formulas = jfslTranslator
						.translateRequires(requires_str);
				currentBehavior.requires.addAll(requires_formulas);

			} else if (token.equals(ENSURES_TAG)) {
				String ensures_str = parse_until_semicolon(tokenizer);

				JfslParser jfslTranslator = new JfslParser(env);
				List<AlloyFormula> ensures_formulas = jfslTranslator
						.translateEnsures(ensures_str);
				currentBehavior.ensures.addAll(ensures_formulas);

			} else if (token.equals(THROWS_TAG)) {

				String throws_str = parse_until_semicolon(tokenizer);

				JfslParser jfslTranslator = new JfslParser(env);
				JfslBehaviorCase throws_behavior_case = jfslTranslator
						.translateThrows(throws_str);
				method_specification.spec_cases.add(throws_behavior_case);

			} else if (token.equals(RETURNS_TAG)) {
				String returns_str = parse_until_semicolon(tokenizer);
				JfslParser jfslTranslator = new JfslParser(env);
				AlloyFormula returns_formula = jfslTranslator
						.translateReturns(returns_str);

				currentBehavior.ensures.add(returns_formula);

			} else if (token.equals(MODIFIES_TAG)) {
				String modifies_str = parse_until_semicolon(tokenizer);

				JfslParser jfslTranslator = new JfslParser(env);
				List<AlloyExpression> modifies_expressions = jfslTranslator
						.translateModifies(modifies_str);

				currentBehavior.modifies.addAll(modifies_expressions);

			} else if (token.equals(MODIFIES_EVERYTHING_TAG)) {
				currentBehavior.modifies_everything = true;
				
			} else if (token.equals(NORMAL_BEHAVIOR_TAG)) {

				if (currentBehavior.isEmpty())
					method_specification.spec_cases.remove(currentBehavior);
				else {
					if (currentBehaviorIsNormal) {

						AlloyFormula normalBehaviorFormula = JPredicateFactory
								.eq(JExpressionFactory.PRIMED_THROW_EXPRESSION,
										JExpressionFactory.NULL_EXPRESSION);

						currentBehavior.ensures.add(normalBehaviorFormula);

					}
				}

				currentBehavior = new JfslBehaviorCase();
				currentBehaviorIsNormal = true;
				method_specification.spec_cases.add(currentBehavior);

				AlloyFormula normalBehaviorFormula = JPredicateFactory.eq(
						JExpressionFactory.PRIMED_THROW_EXPRESSION,
						JExpressionFactory.NULL_EXPRESSION);

				currentBehavior.ensures.add(normalBehaviorFormula);

			} else if (token.equals(EXCEPTIONAL_BEHAVIOR_TAG)) {

				if (currentBehavior.isEmpty())
					method_specification.spec_cases.remove(currentBehavior);
				else {
					if (currentBehaviorIsNormal) {

						AlloyFormula normalBehaviorFormula = JPredicateFactory
								.eq(JExpressionFactory.PRIMED_THROW_EXPRESSION,
										JExpressionFactory.NULL_EXPRESSION);

						currentBehavior.ensures.add(normalBehaviorFormula);

					}
				}

				currentBehavior = new JfslBehaviorCase();
				currentBehaviorIsNormal = false;
				method_specification.spec_cases.add(currentBehavior);

			} else {
				// skip
			}
		}

		if (currentBehavior.isEmpty())
			method_specification.spec_cases.remove(currentBehavior);
		else {
			if (currentBehaviorIsNormal) {

				AlloyFormula normalBehaviorFormula = JPredicateFactory.eq(
						JExpressionFactory.PRIMED_THROW_EXPRESSION,
						JExpressionFactory.NULL_EXPRESSION);

				currentBehavior.ensures.add(normalBehaviorFormula);

			}
		}

		return method_specification;
	}

}
