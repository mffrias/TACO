package ar.edu.taco.jfsl;

import java.util.LinkedList;
import java.util.List;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.tree.TreeAdaptor;

import ar.edu.jdynalloy.factory.JExpressionFactory;
import ar.edu.jdynalloy.factory.JPredicateFactory;
import ar.edu.jdynalloy.xlator.JType;
import ar.edu.taco.jfsl.AlloyComposite.DECL;
import ar.uba.dc.rfm.alloy.ast.expressions.AlloyExpression;
import ar.uba.dc.rfm.alloy.ast.expressions.ExprConstant;
import ar.uba.dc.rfm.alloy.ast.expressions.ExprJoin;
import ar.uba.dc.rfm.alloy.ast.expressions.ExprVariable;
import ar.uba.dc.rfm.alloy.ast.formulas.AlloyFormula;
import ar.uba.dc.rfm.alloy.util.ExpressionPrinter;
import edu.mit.csail.sdg.annotations.parser.JForgeLexer;
import edu.mit.csail.sdg.annotations.parser.JForgeParser;
import edu.mit.csail.sdg.annotations.parser.JForgeParser.Node;
import edu.mit.csail.sdg.annotations.parser.JForgeParser.NodeAdaptor;
import edu.mit.csail.sdg.annotations.spec.ParserException;

class JfslParser {

	private JfslToJDynAlloyEnv env;

	static enum Rule {
		CLAUSE, DECLARATION, FRAME
	};

	public JfslParser(JfslToJDynAlloyEnv env) {
		this.env = env;
	}

	Node parse(String source, Rule rule) {

		// execute ANTLR parser
		final Node node;

		// prepare ANTLR parser
		final JForgeParser parser = buildParser(source);

		// run parser assuming it is a whole expression
		try {
			final Object result;
			switch (rule) {
			case CLAUSE:
				result = parser.clause().getTree();
				break;
			case DECLARATION:
				result = parser.specField().getTree();
				break;
			case FRAME:
				result = parser.modifies().getTree();
				break;
			default:
				result = null;
			}

			if (!(result instanceof Node)) {
				throw new ParserException("cannot produce AST");
			} else
				node = (Node) result;

			return node;
		} catch (ParserException e) {
			throw e;
		} catch (RuntimeException e) {
			throw new ParserException(e);
		} catch (RecognitionException e) {
			throw new ParserException(e);
		}
	}

	protected List<AlloyFormula> translateRequires(String source) {
		List<AlloyFormula> formulas = new LinkedList<AlloyFormula>();
		Node node = parse(source, Rule.CLAUSE);

		JfslVisitor xlator = new JfslVisitor();
		AlloyComposite result = xlator.publicVisit(env, node);

		AlloyFormula formula;
		if (result.formula == null)
			formula = JPredicateFactory.liftExpression(result.expression);
		else
			formula = result.formula;

		formulas.add(formula);
		return formulas;
	}

	JForgeParser buildParser(String source) {
		final ANTLRStringStream cs = new ANTLRStringStream(source);
		final JForgeLexer lexer = new JForgeLexer(cs);
		final CommonTokenStream tokens = new CommonTokenStream();
		tokens.setTokenSource(lexer);
		final JForgeParser parser = new JForgeParser(tokens);
		final TreeAdaptor adaptor = new NodeAdaptor();
		parser.setTreeAdaptor(adaptor);
		return parser;
	}

	protected List<AlloyFormula> translateEnsures(String source)
			throws ParserException {
		List<AlloyFormula> formulas = new LinkedList<AlloyFormula>();
		Node node = parse(source, Rule.CLAUSE);

		JfslVisitor xlator = new JfslVisitor();
		env.enterPost();
		AlloyComposite result = xlator.publicVisit(env, node);
		env.leavePost();

		AlloyFormula formula;
		if (result.formula == null)
			formula = JPredicateFactory.liftExpression(result.expression);
		else
			formula = result.formula;

		formulas.add(formula);

		return formulas;
	}

	protected List<AlloyFormula> translateInvariant(String source)
			throws ParserException {
		Node node = parse(source, Rule.CLAUSE);
		List<AlloyFormula> formulas = new LinkedList<AlloyFormula>();

		JfslVisitor xlator = new JfslVisitor();
		AlloyComposite result = xlator.publicVisit(env, node);

		formulas.add(result.formula);

		return formulas;
	}

	protected List<AlloyExpression> translateModifies(String source)
			throws ParserException {
		List<AlloyExpression> locations = new LinkedList<AlloyExpression>();
		Node node = parse(source, Rule.FRAME);

		JfslVisitor xlator = new JfslVisitor();
		AlloyComposite result = xlator.publicVisit(env, node);

		locations.addAll(result.frame);

		return locations;
	}

	protected AlloyFormula translateReturns(String source)
			throws ParserException {

		Node node = parse(source, Rule.CLAUSE);

		JfslVisitor xlator = new JfslVisitor();
		AlloyComposite result = xlator.publicVisit(env, node);

		AlloyExpression returnValue = result.expression;
		AlloyFormula formula = JPredicateFactory.eq(
				JExpressionFactory.PRIMED_RETURN_EXPRESSION, returnValue);

		return formula;
	}

	protected JfslSpecField translateSpecField(String source)
			throws ParserException {
		Node node = parse(source, Rule.DECLARATION);

		JfslVisitor xlator = new JfslVisitor();
		AlloyComposite composite = xlator.publicVisit(env, node);

		String specFieldId = composite.specField;
		AlloyFormula specFieldFormula = composite.formula;

		ExpressionPrinter printer = new ExpressionPrinter();
		String specFieldTypeStr = (String) composite.expression.accept(printer);

		JType specFieldType;

		if (composite.specFieldMult == DECL.SET)
			specFieldType = JType.parse("set " + specFieldTypeStr);
		else if (composite.specFieldMult == DECL.SEQ)
			specFieldType = JType.parse("seq " + specFieldTypeStr);
		else if (composite.specFieldMult == DECL.NONE)
			specFieldType = JType.parse(specFieldTypeStr);
		else
			throw new IllegalArgumentException("unknown DECL multiplier");

		ExprJoin specFieldExpr = ExprJoin.join(
				JExpressionFactory.THIS_EXPRESSION, ExprVariable
						.buildExprVariable(specFieldId));

		JfslSpecField result = new JfslSpecField(specFieldId, specFieldExpr,
				specFieldType, specFieldFormula);

		return result;
	}

	protected JfslBehaviorCase translateThrows(String source)
			throws ParserException {

		String throwsClass = source.split(":")[0];
		String throwsClause = source.split(":")[1];

		Node node = parse(throwsClause, Rule.CLAUSE);

		JfslVisitor xlator = new JfslVisitor();
		AlloyComposite result = xlator.publicVisit(env, node);

		AlloyFormula throwsPrecondition = result.formula;
		AlloyFormula throwsPostcondition = JPredicateFactory.isSubset(
				JExpressionFactory.PRIMED_THROW_EXPRESSION, new ExprConstant(
						null, throwsClass));

		JfslBehaviorCase throws_spec_case = new JfslBehaviorCase();
		throws_spec_case.requires.add(throwsPrecondition);
		throws_spec_case.ensures.add(throwsPostcondition);

		return throws_spec_case;

	}

}
