package ar.edu.taco.jfsl;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import edu.mit.csail.sdg.annotations.parser.JForgeParser.Node;
import edu.mit.csail.sdg.annotations.spec.ParserException;
import edu.mit.csail.sdg.annotations.spec.PublicVisitor;

import static edu.mit.csail.sdg.annotations.parser.JForgeParser.DECL_NONE;
import static edu.mit.csail.sdg.annotations.parser.JForgeParser.DECL_SEQ;
import static edu.mit.csail.sdg.annotations.parser.JForgeParser.DECL_SET;
import static edu.mit.csail.sdg.annotations.parser.JForgeParser.OP_AND;
import static edu.mit.csail.sdg.annotations.parser.JForgeParser.OP_BIT_AND_OR_INTERSECTION;
import static edu.mit.csail.sdg.annotations.parser.JForgeParser.OP_BIT_NOT_OR_TRANSPOSE;
import static edu.mit.csail.sdg.annotations.parser.JForgeParser.OP_BIT_OR;
import static edu.mit.csail.sdg.annotations.parser.JForgeParser.OP_BIT_XOR;
import static edu.mit.csail.sdg.annotations.parser.JForgeParser.OP_CLOSURE;
import static edu.mit.csail.sdg.annotations.parser.JForgeParser.OP_DIFFERENCE;
import static edu.mit.csail.sdg.annotations.parser.JForgeParser.OP_DIVIDE;
import static edu.mit.csail.sdg.annotations.parser.JForgeParser.OP_EQ;
import static edu.mit.csail.sdg.annotations.parser.JForgeParser.OP_EQUIV;
import static edu.mit.csail.sdg.annotations.parser.JForgeParser.OP_GEQ;
import static edu.mit.csail.sdg.annotations.parser.JForgeParser.OP_GT;
import static edu.mit.csail.sdg.annotations.parser.JForgeParser.OP_IMPLIES;
import static edu.mit.csail.sdg.annotations.parser.JForgeParser.OP_INTERSECTION;
import static edu.mit.csail.sdg.annotations.parser.JForgeParser.OP_LEQ;
import static edu.mit.csail.sdg.annotations.parser.JForgeParser.OP_LT;
import static edu.mit.csail.sdg.annotations.parser.JForgeParser.OP_MINUS;
import static edu.mit.csail.sdg.annotations.parser.JForgeParser.OP_MINUS_OR_DIFFERENCE;
import static edu.mit.csail.sdg.annotations.parser.JForgeParser.OP_MOD;
import static edu.mit.csail.sdg.annotations.parser.JForgeParser.OP_NEQ;
import static edu.mit.csail.sdg.annotations.parser.JForgeParser.OP_NEQUIV;
import static edu.mit.csail.sdg.annotations.parser.JForgeParser.OP_NOT;
import static edu.mit.csail.sdg.annotations.parser.JForgeParser.OP_NSET_SUBSET;
import static edu.mit.csail.sdg.annotations.parser.JForgeParser.OP_OR;
import static edu.mit.csail.sdg.annotations.parser.JForgeParser.OP_PLUS;
import static edu.mit.csail.sdg.annotations.parser.JForgeParser.OP_PLUS_OR_UNION;
import static edu.mit.csail.sdg.annotations.parser.JForgeParser.OP_RELATIONAL_COMPOSE;
import static edu.mit.csail.sdg.annotations.parser.JForgeParser.OP_RELATIONAL_OVERRIDE;
import static edu.mit.csail.sdg.annotations.parser.JForgeParser.OP_SET_ALL;
import static edu.mit.csail.sdg.annotations.parser.JForgeParser.OP_SET_COMPREHENSION;
import static edu.mit.csail.sdg.annotations.parser.JForgeParser.OP_SET_LONE;
import static edu.mit.csail.sdg.annotations.parser.JForgeParser.OP_SET_NO;
import static edu.mit.csail.sdg.annotations.parser.JForgeParser.OP_SET_NUM;
import static edu.mit.csail.sdg.annotations.parser.JForgeParser.OP_SET_ONE;
import static edu.mit.csail.sdg.annotations.parser.JForgeParser.OP_SET_SOME;
import static edu.mit.csail.sdg.annotations.parser.JForgeParser.OP_SET_SUBSET;
import static edu.mit.csail.sdg.annotations.parser.JForgeParser.OP_SET_SUM;
import static edu.mit.csail.sdg.annotations.parser.JForgeParser.OP_SHL;
import static edu.mit.csail.sdg.annotations.parser.JForgeParser.OP_SHR;
import static edu.mit.csail.sdg.annotations.parser.JForgeParser.OP_TIMES;
import static edu.mit.csail.sdg.annotations.parser.JForgeParser.OP_TRANSPOSE;
import static edu.mit.csail.sdg.annotations.parser.JForgeParser.OP_UNION;
import static edu.mit.csail.sdg.annotations.parser.JForgeParser.OP_USHR;

import ar.edu.jdynalloy.factory.JExpressionFactory;
import ar.edu.jdynalloy.factory.JPredicateFactory;
import ar.edu.jdynalloy.factory.JSignatureFactory;
import ar.edu.taco.TacoConfigurator;
import ar.edu.taco.TacoException;
import ar.edu.taco.jfsl.AlloyComposite.DECL;
import ar.edu.taco.simplejml.builtin.JavaPrimitiveIntegerValue;
import ar.uba.dc.rfm.alloy.AlloyVariable;
import ar.uba.dc.rfm.alloy.ast.expressions.AlloyExpression;
import ar.uba.dc.rfm.alloy.ast.expressions.ExprComprehension;
import ar.uba.dc.rfm.alloy.ast.expressions.ExprConstant;
import ar.uba.dc.rfm.alloy.ast.expressions.ExprIfCondition;
import ar.uba.dc.rfm.alloy.ast.expressions.ExprIntLiteral;
import ar.uba.dc.rfm.alloy.ast.expressions.ExprJoin;
import ar.uba.dc.rfm.alloy.ast.expressions.ExprOverride;
import ar.uba.dc.rfm.alloy.ast.expressions.ExprProduct;
import ar.uba.dc.rfm.alloy.ast.expressions.ExprUnary;
import ar.uba.dc.rfm.alloy.ast.expressions.ExprUnion;
import ar.uba.dc.rfm.alloy.ast.expressions.ExprVariable;
import ar.uba.dc.rfm.alloy.ast.formulas.AlloyFormula;
import ar.uba.dc.rfm.alloy.ast.formulas.AndFormula;
import ar.uba.dc.rfm.alloy.ast.formulas.IfFormula;
import ar.uba.dc.rfm.alloy.ast.formulas.IffFormula;
import ar.uba.dc.rfm.alloy.ast.formulas.ImpliesFormula;
import ar.uba.dc.rfm.alloy.ast.formulas.NotFormula;
import ar.uba.dc.rfm.alloy.ast.formulas.OrFormula;
import ar.uba.dc.rfm.alloy.ast.formulas.QuantifiedFormula;
import ar.uba.dc.rfm.alloy.ast.formulas.QuantifiedFormula.Operator;

class JfslVisitor extends PublicVisitor<AlloyComposite, JfslToJDynAlloyEnv> {

	@Override
	protected AlloyComposite visitJoinReflexive(JfslToJDynAlloyEnv env,
			AlloyComposite primary, Node selector) {

		if ((selector.getChildCount() == 1 && selector.getText().equals(
				"JOIN_REFLEXIVE"))) {
			selector = (Node) selector.getChild(0);
		}

		AlloyComposite selec = publicVisit(env, selector);
		AlloyExpression reflexiveClosure = JExpressionFactory
				.reflexiveClosure(selec.expression);

		AlloyExpression join = ExprJoin.join(primary.expression,
				reflexiveClosure);

		return new AlloyComposite(join);
	}

	@Override
	protected AlloyComposite visitMethodCall(JfslToJDynAlloyEnv env,
			AlloyComposite receiver, String id, Node arguments) {
		throw new UnsupportedOperationException();
	}

	@Override
	protected AlloyComposite visitString(JfslToJDynAlloyEnv env, String s) {
		throw new UnsupportedOperationException();
	}

	@Override
	protected AlloyComposite visitBracket(JfslToJDynAlloyEnv env,
			AlloyComposite primary, Node selector) {

		AlloyExpression right = primary.expression;

		if ((selector.getChildCount() == 1 && selector.getText().equals(
				"BRACKET"))) {
			selector = (Node) selector.getChild(0);
		}

		AlloyComposite selector_result = publicVisit(env, selector);
		AlloyExpression left = selector_result.expression;

		return new AlloyComposite(ExprJoin.join(left, right));

	}

	@Override
	protected AlloyComposite visitFieldDeclaration(JfslToJDynAlloyEnv env,
			String ident, int op, Node set, Node frame, Node constraint) {

		String specFieldId = ident;

		AlloyComposite constraintResult = publicVisit(env, constraint);
		AlloyFormula specFieldFormula = constraintResult.formula;

		AlloyComposite frameResult = publicVisit(env, frame);
		List<AlloyExpression> specFieldFrame = frameResult.frame;

		AlloyComposite setResult = publicVisit(env, set);
		AlloyExpression specFieldType = setResult.expression;

		AlloyComposite.DECL specFieldMult;
		switch (op) {
		case DECL_SEQ:
			specFieldMult = DECL.SEQ;
			break;
		case DECL_SET:
			specFieldMult = DECL.SET;
			break;
		case DECL_NONE:
			specFieldMult = DECL.NONE;
			break;
		default:
			throw new IllegalArgumentException("Unknown modifier type");

		}

		AlloyComposite fieldDecl = new AlloyComposite(specFieldId,
				specFieldMult, specFieldType, specFieldFrame, specFieldFormula);
		return fieldDecl;
	}

	/**
	 * Responds to ambiguity between package naming, class naming and join
	 * usage. We treat every ambiguity as a join
	 */
	@Override
	protected AlloyComposite visitAmbiguous(JfslToJDynAlloyEnv env,
			List<Node> idents) {
		AlloyComposite primary = visitName(env, idents.get(0));
		if (idents.size() > 1) {
			List<Node> selectors = idents.subList(1, idents.size());
			for (Node selector : selectors) {
				primary = visitJoin(env, primary, selector);
			}
		}
		return primary;
	}

	@Override
	protected AlloyComposite visitArrayType(JfslToJDynAlloyEnv env, Node base) {
		throw new UnsupportedOperationException();
	}

	@Override
	protected AlloyComposite visitConditional(JfslToJDynAlloyEnv env,
			Node condTree, Node leftTree, Node rightTree) {
		AlloyComposite cond = this.publicVisit(env, condTree);
		AlloyComposite left = this.publicVisit(env, leftTree);
		AlloyComposite right = this.publicVisit(env, rightTree);

		if (left.formula != null && right.formula != null) {
			AlloyFormula ifFormula = new IfFormula(cond.formula, left.formula,
					right.formula);
			return new AlloyComposite(ifFormula);
		} else if (left.expression != null && right.expression != null) {
			AlloyExpression ifExpr = new ExprIfCondition(cond.formula,
					left.expression, right.expression);
			return new AlloyComposite(ifExpr);
		} else
			throw new IllegalArgumentException("IfExpression not supported");
	}

	@Override
	protected AlloyComposite visitFieldRelation(JfslToJDynAlloyEnv env,
			Node type, Node ident) {
		throw new UnsupportedOperationException();
	}

	@Override
	protected AlloyComposite visitFrame(JfslToJDynAlloyEnv env,
			List<AlloyComposite> joins, List<Node> fields) {

		List<AlloyExpression> locations = new LinkedList<AlloyExpression>();

		for (int i = 0; i < joins.size(); i++) {
			AlloyComposite join = joins.get(i);
			Node field = fields.get(i);

			AlloyComposite fieldResult = publicVisit(env, field);
			AlloyExpression location = ExprJoin.join(join.expression,
					fieldResult.expression);

			locations.add(location);
		}

		return new AlloyComposite(locations);
	}

	@Override
	protected AlloyComposite visitOld(JfslToJDynAlloyEnv env, Node sub) {
		env.leavePost();
		AlloyComposite composite = publicVisit(env, sub);
		env.enterPost();
		return composite;
	}

	@Override
	protected AlloyComposite visitRefType(JfslToJDynAlloyEnv env, Node source,
			List<Node> idents) {
		throw new UnsupportedOperationException();
	}

	@Override
	protected AlloyComposite visitSuper(JfslToJDynAlloyEnv env) {
		throw new UnsupportedOperationException();
	}

	@Override
	protected AlloyComposite visitArgument(JfslToJDynAlloyEnv env, int i) {
		String parameterId = env.getParameterName(i);
		ExprVariable parameterExpr;
		if (env.post()) {
			AlloyVariable var = new AlloyVariable(parameterId, true);
			parameterExpr = ExprVariable.buildExprVariable(var);
		} else
			parameterExpr = ExprVariable.buildExprVariable(parameterId);

		return new AlloyComposite(parameterExpr);
	}

	@Override
	protected AlloyComposite visitIntegralType(JfslToJDynAlloyEnv env) {

		String integerSignatureId;
		if (TacoConfigurator.getInstance().getUseJavaArithmetic() == true) {
			integerSignatureId = JSignatureFactory.JAVA_PRIMITIVE_INTEGER_VALUE
					.singletonFrom();
		} else {
			integerSignatureId = JSignatureFactory.INT.getSignatureId();
		}
		AlloyExpression expr = ExprConstant
				.buildExprConstant(integerSignatureId);
		return new AlloyComposite(expr);
	}

	@Override
	protected AlloyComposite visitDecimal(JfslToJDynAlloyEnv env, int i) {
		AlloyExpression expression;
		if (TacoConfigurator.getInstance().getUseJavaArithmetic() == true) {
			expression = JavaPrimitiveIntegerValue.getInstance().toJavaPrimitiveIntegerLiteral(i, false);
		} else {
			expression = new ExprIntLiteral(i);
		}
		return new AlloyComposite(expression);
	}

	@Override
	protected AlloyComposite visitCastExpression(JfslToJDynAlloyEnv env,
			Node type, Node sub) {
		throw new UnsupportedOperationException(
				"visitCastExpression not supported");
	}

	@Override
	protected AlloyComposite visitBooleanType(JfslToJDynAlloyEnv env) {
		String booleanSignatureId = JSignatureFactory.BOOLEAN.getSignatureId();
		AlloyExpression booleanType = ExprConstant
				.buildExprConstant(booleanSignatureId);
		return new AlloyComposite(booleanType);
	}

	@Override
	protected AlloyComposite visitBinary(JfslToJDynAlloyEnv env, Node tree,
			int op, Node leftTree, Node rightTree) {

		AlloyComposite left = publicVisit(env, leftTree);
		AlloyComposite right = publicVisit(env, rightTree);

		AlloyExpression leftExpr = left.expression;
		AlloyExpression rightExpr = right.expression;

		AlloyFormula leftFormula = left.formula;
		AlloyFormula rightFormula = right.formula;

		final AlloyComposite result;

		switch (op) {
		case OP_LT: {
			AlloyFormula lt;
			if (TacoConfigurator.getInstance().getUseJavaArithmetic() == true) {
				lt = JPredicateFactory.pred_java_primitive_integer_value_lt(
						leftExpr, rightExpr);
			} else {
				lt = JPredicateFactory.alloy_int_lt(leftExpr, rightExpr);
			}
			result = new AlloyComposite(lt);
		}
			break;
		case OP_GT: {
			AlloyFormula gt;
			if (TacoConfigurator.getInstance().getUseJavaArithmetic() == true) {
				gt = JPredicateFactory.pred_java_primitive_integer_value_gt(
						leftExpr, rightExpr);
			} else {
				gt = JPredicateFactory.alloy_int_gt(leftExpr, rightExpr);
			}
			result = new AlloyComposite(gt);
		}
			break;
		case OP_LEQ: {
			AlloyFormula lte;
			if (TacoConfigurator.getInstance().getUseJavaArithmetic() == true) {
				lte = JPredicateFactory.pred_java_primitive_integer_value_lte(
						leftExpr, rightExpr);
			} else {
				lte = JPredicateFactory.alloy_int_lte(leftExpr, rightExpr);
			}
			result = new AlloyComposite(lte);
		}
			break;
		case OP_GEQ: {
			AlloyFormula gte;
			if (TacoConfigurator.getInstance().getUseJavaArithmetic() == true) {
				gte = JPredicateFactory.pred_java_primitive_integer_value_gte(
						leftExpr, rightExpr);
			} else {
				gte = JPredicateFactory.alloy_int_gte(leftExpr, rightExpr);
			}
			result = new AlloyComposite(gte);
		}
			break;
		case OP_EQ: {
			AlloyFormula eq = JPredicateFactory.eq(leftExpr, rightExpr);
			result = new AlloyComposite(eq);
		}
			break;
		case OP_NEQ: {
			AlloyFormula neq = JPredicateFactory.neq(leftExpr, rightExpr);
			result = new AlloyComposite(neq);
		}
			break;
		case OP_OR: {
			AlloyFormula or = OrFormula.buildOrFormula(leftFormula,
					rightFormula);
			result = new AlloyComposite(or);
		}
			break;
		case OP_AND: {
			AlloyFormula and = AndFormula.buildAndFormula(leftFormula,
					rightFormula);
			result = new AlloyComposite(and);
		}
			break;
		case OP_IMPLIES: {
			AlloyFormula implies = new ImpliesFormula(leftFormula, rightFormula);
			result = new AlloyComposite(implies);
		}
			break;
		case OP_PLUS: {
			AlloyExpression add;
			if (TacoConfigurator.getInstance().getUseJavaArithmetic() == true)
				add = JExpressionFactory.fun_java_primitive_integer_value_add(
						leftExpr, rightExpr);
			else
				add = JExpressionFactory.alloy_int_add(leftExpr, rightExpr);
			result = new AlloyComposite(add);
		}
			break;
		case OP_MINUS: {
			AlloyExpression sub;
			if (TacoConfigurator.getInstance().getUseJavaArithmetic() == true)
				sub = JExpressionFactory.fun_java_primitive_integer_value_sub(
						leftExpr, rightExpr);
			else
				sub = JExpressionFactory.alloy_int_sub(leftExpr, rightExpr);
			result = new AlloyComposite(sub);
		}
			break;
		case OP_TIMES: {
			AlloyExpression mul;
			if (TacoConfigurator.getInstance().getUseJavaArithmetic() == true) {
				throw new TacoException(
						"mul operation not supported in specifications!");
			} else
				mul = JExpressionFactory.alloy_int_mul(leftExpr, rightExpr);

			result = new AlloyComposite(mul);
		}
			break;
		case OP_DIVIDE: {
			AlloyExpression div;
			if (TacoConfigurator.getInstance().getUseJavaArithmetic() == true) {
				throw new TacoException(
						"mul operation not supported in specifications!");
			} else
				div = JExpressionFactory.alloy_int_div(leftExpr, rightExpr);
			result = new AlloyComposite(div);
		}
			break;
		case OP_MOD: {
			AlloyExpression rem;
			if (TacoConfigurator.getInstance().getUseJavaArithmetic() == true)
				rem = JExpressionFactory.fun_java_primitive_integer_value_rem(
						leftExpr, rightExpr);
			else
				rem = JExpressionFactory.alloy_int_rem(leftExpr, rightExpr);
			result = new AlloyComposite(rem);
		}
			break;
		case OP_UNION: {
			ExprUnion union = new ExprUnion(leftExpr, rightExpr);
			result = new AlloyComposite(union);
		}
			break;
		case OP_EQUIV: {
			AlloyFormula iff = new IffFormula(leftFormula, rightFormula);
			result = new AlloyComposite(iff);
		}
			break;
		case OP_NEQUIV: {
			AlloyFormula iff = new IffFormula(leftFormula, rightFormula);
			AlloyFormula niff = new NotFormula(iff);
			result = new AlloyComposite(niff);
		}
			break;
		case OP_RELATIONAL_OVERRIDE: {
			AlloyExpression override = new ExprOverride(leftExpr, rightExpr);
			result = new AlloyComposite(override);
		}
			break;
		case OP_RELATIONAL_COMPOSE: {
			AlloyExpression product = new ExprProduct(leftExpr, rightExpr);
			result = new AlloyComposite(product);
		}
			break;
		case OP_SET_SUBSET: {
			AlloyFormula subSet = JPredicateFactory.isSubset(leftExpr,
					rightExpr);
			result = new AlloyComposite(subSet);
		}
			break;
		case OP_NSET_SUBSET: {
			AlloyFormula subSet = JPredicateFactory.isNotSubset(leftExpr,
					rightExpr);
			result = new AlloyComposite(subSet);
		}
			break;
		case OP_INTERSECTION:
		case OP_BIT_AND_OR_INTERSECTION: {
			AlloyExpression intersection = JExpressionFactory.intersection(
					leftExpr, rightExpr);
			result = new AlloyComposite(intersection);
		}
			break;
		case OP_DIFFERENCE: {
			AlloyExpression difference = JExpressionFactory.difference(
					leftExpr, rightExpr);
			result = new AlloyComposite(difference);
		}
			break;

		case OP_PLUS_OR_UNION: {
			AlloyExpression add;
			if (TacoConfigurator.getInstance().getUseJavaArithmetic() == true)
				add = JExpressionFactory.fun_java_primitive_integer_value_add(
						leftExpr, rightExpr);
			else
				add = JExpressionFactory.alloy_int_add(leftExpr, rightExpr);
			result = new AlloyComposite(add);
		}
			break;
		case OP_MINUS_OR_DIFFERENCE: {
			AlloyExpression sub;
			if (TacoConfigurator.getInstance().getUseJavaArithmetic() == true)
				sub = JExpressionFactory.fun_java_primitive_integer_value_sub(
						leftExpr, rightExpr);
			else
				sub = JExpressionFactory.alloy_int_sub(leftExpr, rightExpr);
			result = new AlloyComposite(sub);
		}
			break;

		case OP_SHL:
		case OP_SHR:
		case OP_USHR:
		case OP_BIT_XOR:
		case OP_BIT_OR:
			throw new ParserException("bit operations are not supported");
		default:
			assert false;
			result = null;
			throw new ParserException("unknown binary operator");
		}
		return result;
	}

	@Override
	protected AlloyComposite visitName(JfslToJDynAlloyEnv env, Node ident) {
		String name = asText(ident);

		AlloyExpression expr;
		if (env.containsJmlTypename(name)) {
			if (env.has_new_name(name)) {
				name = env.get_new_name(name);
			}
			expr = ExprConstant.buildExprConstant(name);
		} else {
			String varName = name;
			if (env.has_new_name(varName)) {
				varName = env.get_new_name(varName);
			}
			if ((env.post()) && (!isBounded(varName))) {
				AlloyVariable var = new AlloyVariable(varName, true);
				expr = ExprVariable.buildExprVariable(var);
			} else {
				expr = ExprVariable.buildExprVariable(varName);
			}
		}

		return new AlloyComposite(expr);
	}

	@Override
	protected AlloyComposite visitUnary(JfslToJDynAlloyEnv env, Node tree,
			int op, Node expr) {
		final AlloyComposite sub = publicVisit(env, expr);

		AlloyExpression expression = sub.expression;
		AlloyFormula formula = sub.formula;

		AlloyComposite result;

		switch (op) {
		case OP_PLUS:
			result = sub;
			break;
		case OP_MINUS: {

			AlloyExpression minusExpr;
			if (TacoConfigurator.getInstance().getUseJavaArithmetic() == true) {
				minusExpr = JExpressionFactory
						.fun_java_primitive_integer_value_negate(expression);
			} else {
				minusExpr = JExpressionFactory.alloy_int_negate(expression);
			}
			result = new AlloyComposite(minusExpr);
		}
			break;
		case OP_NOT: {
			AlloyFormula notFormula = new NotFormula(formula);
			result = new AlloyComposite(notFormula);
		}
			break;

		case OP_SET_SOME: {
			AlloyFormula someSet = JPredicateFactory.someSet(expression);
			result = new AlloyComposite(someSet);
		}
			break;
		case OP_SET_NO: {
			AlloyFormula emptySet = JPredicateFactory.emptySet(expression);
			result = new AlloyComposite(emptySet);
		}
			break;
		case OP_SET_ONE: {
			AlloyFormula oneSet = JPredicateFactory.oneSet(expression);
			result = new AlloyComposite(oneSet);
		}
			break;
		case OP_SET_LONE: {
			AlloyFormula loneSet = JPredicateFactory.loneSet(expression);
			result = new AlloyComposite(loneSet);
		}
			break;

		case OP_SET_NUM: {
			AlloyExpression cardinal_of_expression;
			cardinal_of_expression = new ExprUnary(
					ExprUnary.Operator.CARDINALITY, expression);
			if (TacoConfigurator.getInstance().getUseJavaArithmetic() == true) {
				cardinal_of_expression = JExpressionFactory
						.fun_java_primitive_integer_value_size_of(expression);

			} else {
				cardinal_of_expression = new ExprUnary(
						ExprUnary.Operator.CARDINALITY, expression);
			}

			result = new AlloyComposite(cardinal_of_expression);
		}
			break;

		case OP_CLOSURE: {
			AlloyExpression closure = JExpressionFactory.closure(expression);
			result = new AlloyComposite(closure);
		}
			break;

		/*
		 * case OP_REFLEXIVE_CLOSURE: { AlloyExpression reflexiveClosure =
		 * JExpressionFactory .reflexiveClosure(expression); result = new
		 * AlloyComposite(reflexiveClosure); } break;
		 */

		case OP_TRANSPOSE:
		case OP_BIT_NOT_OR_TRANSPOSE: {
			AlloyExpression transpose = JExpressionFactory
					.transpose(expression);
			result = new AlloyComposite(transpose);
		}
			break;
		case OP_SET_SUM: {
			AlloyExpression setSum = JExpressionFactory.setSum(expression);
			result = new AlloyComposite(setSum);
		}
			break;

		default:
			assert false;
			throw new UnsupportedOperationException();
		}

		return result;
	}

	@Override
	protected AlloyComposite visitJoin(JfslToJDynAlloyEnv env,
			AlloyComposite primary, Node tree) {
		AlloyComposite result = primary;

		if ((tree.getChildCount() == 1 && tree.getText().equals("JOIN"))) {
			tree = (Node) tree.getChild(0);
		}

		AlloyComposite right = publicVisit(env, tree);
		AlloyExpression expr = ExprJoin.join(result.expression,
				right.expression);
		result = new AlloyComposite(expr);

		return result;
	}

	@Override
	protected AlloyComposite visitReturn(JfslToJDynAlloyEnv env) {
		AlloyExpression expression = JExpressionFactory.PRIMED_RETURN_EXPRESSION;
		return new AlloyComposite(expression);
	}

	@Override
	protected AlloyComposite visitThrow(JfslToJDynAlloyEnv env) {
		AlloyExpression expression = JExpressionFactory.PRIMED_THROW_EXPRESSION;
		return new AlloyComposite(expression);
	}

	private Stack<List<String>> boundVariables = new Stack<List<String>>();

	private boolean isBounded(String varName) {
		for (List<String> elems : boundVariables) {
			if (elems.contains(varName))
				return true;
		}
		return false;
	}

	@Override
	protected AlloyComposite visitQuantification(JfslToJDynAlloyEnv env,
			int op, List<String> names, List<Node> sets, Node expr) {

		boundVariables.push(new LinkedList<String>());

		List<String> nameList = new LinkedList<String>();
		List<AlloyExpression> setList = new LinkedList<AlloyExpression>();
		for (int i = 0; i < names.size(); i++) {
			String name = names.get(i);
			boundVariables.peek().add(name);
			Node type = sets.get(i);
			AlloyComposite composite = publicVisit(env, type);
			AlloyExpression setExpr = composite.expression;

			nameList.add(name);
			setList.add(setExpr);
		}
		AlloyComposite composite = publicVisit(env, expr);
		AlloyFormula subFormula = composite.formula;

		boundVariables.pop();

		final Operator operator;
		switch (op) {
		case OP_SET_ALL:
			operator = Operator.FOR_ALL;
			break;
		case OP_SET_SOME:
			operator = Operator.EXISTS;
			break;
		case OP_SET_ONE:
			operator = Operator.ONE;
			break;
		case OP_SET_LONE:
			operator = Operator.LONE;
			break;
		case OP_SET_NO:
			operator = Operator.NONE;
			break;
		case OP_SET_COMPREHENSION: {
			ExprComprehension expression = new ExprComprehension(nameList,
					setList, subFormula);
			return new AlloyComposite(expression);
		}
		case OP_SET_NUM:
		case OP_SET_SUM:
		default:
			throw new IllegalArgumentException(
					"Quantification operator not supported");
		}

		AlloyFormula formula = new QuantifiedFormula(operator, nameList,
				setList, subFormula);

		return new AlloyComposite(formula);
	}

	@Override
	protected AlloyComposite visitFalse(JfslToJDynAlloyEnv env) {
		AlloyExpression expression = JExpressionFactory.FALSE_EXPRESSION;
		return new AlloyComposite(expression);
	}

	@Override
	protected AlloyComposite visitNull(JfslToJDynAlloyEnv env) {
		AlloyExpression expression = JExpressionFactory.NULL_EXPRESSION;
		return new AlloyComposite(expression);
	}

	@Override
	protected AlloyComposite visitTrue(JfslToJDynAlloyEnv env) {
		AlloyExpression expression = JExpressionFactory.TRUE_EXPRESSION;
		return new AlloyComposite(expression);
	}

	@Override
	protected AlloyComposite visitThis(JfslToJDynAlloyEnv env) {
		AlloyExpression expression = JExpressionFactory.THIS_EXPRESSION;
		return new AlloyComposite(expression);
	}

}
