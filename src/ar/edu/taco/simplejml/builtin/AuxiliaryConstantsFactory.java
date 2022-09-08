/**
 * 
 */
package ar.edu.taco.simplejml.builtin;

import java.util.Arrays;
import java.util.List;

import ar.edu.jdynalloy.ast.JAssignment;
import ar.edu.jdynalloy.ast.JAssume;
import ar.edu.jdynalloy.ast.JBlock;
import ar.edu.jdynalloy.ast.JHavoc;
import ar.edu.jdynalloy.ast.JStatement;
import ar.edu.jdynalloy.ast.JVariableDeclaration;
import ar.edu.jdynalloy.factory.JDynAlloyFactory;
import ar.edu.jdynalloy.factory.JExpressionFactory;
import ar.edu.jdynalloy.factory.JPredicateFactory;
import ar.edu.jdynalloy.factory.JSignatureFactory;
import ar.edu.taco.TacoConfigurator;
import ar.uba.dc.rfm.alloy.AlloyVariable;
import ar.uba.dc.rfm.alloy.ast.expressions.AlloyExpression;
import ar.uba.dc.rfm.alloy.ast.expressions.ExprConstant;
import ar.uba.dc.rfm.alloy.ast.expressions.ExprVariable;
import ar.uba.dc.rfm.alloy.ast.formulas.AlloyFormula;
import ar.uba.dc.rfm.alloy.ast.formulas.OrFormula;
import ar.uba.dc.rfm.alloy.ast.formulas.PredicateFormula;

public abstract class AuxiliaryConstantsFactory {

	private static abstract class AuxiliaryConstants {
		public AuxiliaryConstants(JBlock stmt, AlloyExpression ret_expression) {
			this.result_value = ret_expression;
			this.statements = stmt;
		}

		public JBlock statements;
		public AlloyExpression result_value;
	}

	public final static class MultAuxiliaryConstants extends AuxiliaryConstants {

		public MultAuxiliaryConstants(JBlock stmt, AlloyExpression ret_expression) {
			super(stmt, ret_expression);
		}

	}

	public final static class AddAuxiliaryConstants extends AuxiliaryConstants {

		public AddAuxiliaryConstants(JBlock stmt, ExprVariable ret_expression) {
			super(stmt, ret_expression);
		}
	}

	public final static class MinusAuxiliaryConstants extends AuxiliaryConstants {

		public MinusAuxiliaryConstants(JBlock stmt, ExprVariable ret_expression) {
			super(stmt, ret_expression);
		}

	}

	public final static class DivAuxiliaryConstants extends AuxiliaryConstants {

		public AlloyExpression remainder;

		public DivAuxiliaryConstants(JBlock block, AlloyExpression result_expression, AlloyExpression remainder_expression) {
			super(block, result_expression);
			remainder = remainder_expression;
		}
	}

	private static int integer_mul_auxiliary_index = -1;

	private static int long_mul_auxiliary_index = -1;

	public static MultAuxiliaryConstants build_long_mult_auxiliary_constants(AlloyExpression left, AlloyExpression right) {

		final int current_mul_auxiliary_index = ++long_mul_auxiliary_index;
		final String predicate_id = JPredicateFactory.PRED_JAVA_PRIMITIVE_LONG_VALUE_MUL;

		String sk_mul_left = build_left_aux(predicate_id, current_mul_auxiliary_index);
		String sk_mul_right = build_right_aux(predicate_id, current_mul_auxiliary_index);
		String sk_mul_result = build_result_aux(predicate_id, current_mul_auxiliary_index);
		String sk_mul_overflow = build_overflow_aux(predicate_id, current_mul_auxiliary_index);

		JVariableDeclaration var_decl_left = new JVariableDeclaration(new AlloyVariable(sk_mul_left), JSignatureFactory.JAVA_PRIMITIVE_LONG_VALUE);
		JVariableDeclaration var_decl_right = new JVariableDeclaration(new AlloyVariable(sk_mul_right), JSignatureFactory.JAVA_PRIMITIVE_LONG_VALUE);
		JVariableDeclaration var_decl_result = new JVariableDeclaration(new AlloyVariable(sk_mul_result), JSignatureFactory.JAVA_PRIMITIVE_LONG_VALUE);
		JVariableDeclaration var_decl_overflow = new JVariableDeclaration(new AlloyVariable(sk_mul_overflow), JSignatureFactory.BOOLEAN_TYPE);

		String predicateId = JPredicateFactory.PRED_JAVA_PRIMITIVE_LONG_VALUE_MUL_MARKER;

		MultAuxiliaryConstants mulAuxiliaryConstants = create_mult_auxiliary_statements(left, right, var_decl_left, var_decl_right, var_decl_result,
				var_decl_overflow, predicateId);

		return mulAuxiliaryConstants;
	}

	private static int float_mul_auxiliary_index = -1;

	public static MultAuxiliaryConstants build_float_mult_auxiliary_constants(AlloyExpression left, AlloyExpression right) {

		final int current_mul_auxiliary_index = ++float_mul_auxiliary_index;
		final String predicate_id = JPredicateFactory.PRED_JAVA_PRIMITIVE_FLOAT_VALUE_MUL;

		String sk_mul_left = build_left_aux(predicate_id, current_mul_auxiliary_index);
		String sk_mul_right = build_right_aux(predicate_id, current_mul_auxiliary_index);
		String sk_mul_result = build_result_aux(predicate_id, current_mul_auxiliary_index);
		String sk_mul_overflow = build_overflow_aux(predicate_id, current_mul_auxiliary_index);

		JVariableDeclaration var_decl_left = new JVariableDeclaration(new AlloyVariable(sk_mul_left), JSignatureFactory.JAVA_PRIMITIVE_FLOAT_VALUE);
		JVariableDeclaration var_decl_right = new JVariableDeclaration(new AlloyVariable(sk_mul_right), JSignatureFactory.JAVA_PRIMITIVE_FLOAT_VALUE);
		JVariableDeclaration var_decl_result = new JVariableDeclaration(new AlloyVariable(sk_mul_result), JSignatureFactory.JAVA_PRIMITIVE_FLOAT_VALUE);
		JVariableDeclaration var_decl_overflow = new JVariableDeclaration(new AlloyVariable(sk_mul_overflow), JSignatureFactory.BOOLEAN_TYPE);

		String predicateId = JPredicateFactory.PRED_JAVA_PRIMITIVE_FLOAT_VALUE_MUL_MARKER;

		MultAuxiliaryConstants mulAuxiliaryConstants = create_mult_auxiliary_statements(left, right, var_decl_left, var_decl_right, var_decl_result,
				var_decl_overflow, predicateId);

		return mulAuxiliaryConstants;
	}


	private static int char_charIntToInt_add_auxiliary_index = -1;

	public static AddAuxiliaryConstants build_charIntToInt_add_auxiliary_constants(AlloyExpression left, AlloyExpression right) {
		final int add_auxiliary_index = ++char_charIntToInt_add_auxiliary_index;
		final String predicate_id = JPredicateFactory.PRED_JAVA_PRIMITIVE_charIntToInt_VALUE_ADD;

		String sk_add_left = build_left_aux(predicate_id, add_auxiliary_index);
		String sk_add_right = build_right_aux(predicate_id, add_auxiliary_index);
		String sk_add_result = build_result_aux(predicate_id, add_auxiliary_index);
		String sk_add_overflow = build_overflow_aux(predicate_id, add_auxiliary_index);

		// create sk var declarations

		JVariableDeclaration var_decl_left = new JVariableDeclaration(new AlloyVariable(sk_add_left), JSignatureFactory.JAVA_PRIMITIVE_CHAR_VALUE);
		JVariableDeclaration var_decl_right = new JVariableDeclaration(new AlloyVariable(sk_add_right), JSignatureFactory.JAVA_PRIMITIVE_INTEGER_VALUE);
		JVariableDeclaration var_decl_result = new JVariableDeclaration(new AlloyVariable(sk_add_result), JSignatureFactory.JAVA_PRIMITIVE_INTEGER_VALUE);
		JVariableDeclaration var_decl_overflow = new JVariableDeclaration(new AlloyVariable(sk_add_overflow), JSignatureFactory.BOOLEAN_TYPE);

		String marker_predicate_id = JPredicateFactory.PRED_JAVA_PRIMITIVE_CHARINTtoINT_VALUE_ADD_MARKER;
		AddAuxiliaryConstants addAuxiliaryConstants = create_add_auxiliary_statements(left, right, var_decl_left, var_decl_right, var_decl_result,
				var_decl_overflow, marker_predicate_id);

		return addAuxiliaryConstants;
	}


	private static int char_intCharToInt_add_auxiliary_index = -1;

	public static AddAuxiliaryConstants build_intCharToInt_add_auxiliary_constants(AlloyExpression left, AlloyExpression right) {
		final int add_auxiliary_index = ++char_intCharToInt_add_auxiliary_index;
		final String predicate_id = JPredicateFactory.PRED_JAVA_PRIMITIVE_intCharToInt_VALUE_ADD;

		String sk_add_left = build_left_aux(predicate_id, add_auxiliary_index);
		String sk_add_right = build_right_aux(predicate_id, add_auxiliary_index);
		String sk_add_result = build_result_aux(predicate_id, add_auxiliary_index);
		String sk_add_overflow = build_overflow_aux(predicate_id, add_auxiliary_index);

		// create sk var declarations

		JVariableDeclaration var_decl_left = new JVariableDeclaration(new AlloyVariable(sk_add_left), JSignatureFactory.JAVA_PRIMITIVE_INTEGER_VALUE);
		JVariableDeclaration var_decl_right = new JVariableDeclaration(new AlloyVariable(sk_add_right), JSignatureFactory.JAVA_PRIMITIVE_CHAR_VALUE);
		JVariableDeclaration var_decl_result = new JVariableDeclaration(new AlloyVariable(sk_add_result), JSignatureFactory.JAVA_PRIMITIVE_INTEGER_VALUE);
		JVariableDeclaration var_decl_overflow = new JVariableDeclaration(new AlloyVariable(sk_add_overflow), JSignatureFactory.BOOLEAN_TYPE);

		String marker_predicate_id = JPredicateFactory.PRED_JAVA_PRIMITIVE_INTCHARtoINT_VALUE_ADD_MARKER;
		AddAuxiliaryConstants addAuxiliaryConstants = create_add_auxiliary_statements(left, right, var_decl_left, var_decl_right, var_decl_result,
				var_decl_overflow, marker_predicate_id);

		return addAuxiliaryConstants;
	}


	private static int char_charCharToInt_add_auxiliary_index = -1;

	public static AddAuxiliaryConstants build_charCharToInt_add_auxiliary_constants(AlloyExpression left, AlloyExpression right) {
		final int add_auxiliary_index = ++char_charCharToInt_add_auxiliary_index;
		final String predicate_id = JPredicateFactory.PRED_JAVA_PRIMITIVE_charCharToInt_VALUE_ADD;

		String sk_add_left = build_left_aux(predicate_id, add_auxiliary_index);
		String sk_add_right = build_right_aux(predicate_id, add_auxiliary_index);
		String sk_add_result = build_result_aux(predicate_id, add_auxiliary_index);
		String sk_add_overflow = build_overflow_aux(predicate_id, add_auxiliary_index);

		// create sk var declarations

		JVariableDeclaration var_decl_left = new JVariableDeclaration(new AlloyVariable(sk_add_left), JSignatureFactory.JAVA_PRIMITIVE_CHAR_VALUE);
		JVariableDeclaration var_decl_right = new JVariableDeclaration(new AlloyVariable(sk_add_right), JSignatureFactory.JAVA_PRIMITIVE_CHAR_VALUE);
		JVariableDeclaration var_decl_result = new JVariableDeclaration(new AlloyVariable(sk_add_result), JSignatureFactory.JAVA_PRIMITIVE_INTEGER_VALUE);
		JVariableDeclaration var_decl_overflow = new JVariableDeclaration(new AlloyVariable(sk_add_overflow), JSignatureFactory.BOOLEAN_TYPE);

		String marker_predicate_id = JPredicateFactory.PRED_JAVA_PRIMITIVE_CHARCHARtoINT_VALUE_ADD_MARKER;
		AddAuxiliaryConstants addAuxiliaryConstants = create_add_auxiliary_statements(left, right, var_decl_left, var_decl_right, var_decl_result,
				var_decl_overflow, marker_predicate_id);

		return addAuxiliaryConstants;
	}



	private static int char_intCharToInt_sub_auxiliary_index = -1;

	public static MinusAuxiliaryConstants build_intCharToInt_sub_auxiliary_constants(AlloyExpression left, AlloyExpression right) {
		final int minus_auxiliary_index = ++char_intCharToInt_sub_auxiliary_index;
		final String predicate_id = JPredicateFactory.PRED_JAVA_PRIMITIVE_intCharToInt_VALUE_SUB;

		String sk_add_left = build_left_aux(predicate_id, minus_auxiliary_index);
		String sk_add_right = build_right_aux(predicate_id, minus_auxiliary_index);
		String sk_add_result = build_result_aux(predicate_id, minus_auxiliary_index);
		String sk_add_overflow = build_overflow_aux(predicate_id, minus_auxiliary_index);

		// create sk var declarations

		JVariableDeclaration var_decl_left = new JVariableDeclaration(new AlloyVariable(sk_add_left), JSignatureFactory.JAVA_PRIMITIVE_INTEGER_VALUE);
		JVariableDeclaration var_decl_right = new JVariableDeclaration(new AlloyVariable(sk_add_right), JSignatureFactory.JAVA_PRIMITIVE_CHAR_VALUE);
		JVariableDeclaration var_decl_result = new JVariableDeclaration(new AlloyVariable(sk_add_result), JSignatureFactory.JAVA_PRIMITIVE_INTEGER_VALUE);
		JVariableDeclaration var_decl_overflow = new JVariableDeclaration(new AlloyVariable(sk_add_overflow), JSignatureFactory.BOOLEAN_TYPE);

		String marker_predicate_id = JPredicateFactory.PRED_JAVA_PRIMITIVE_INTCHARtoINT_VALUE_SUB_MARKER;
		MinusAuxiliaryConstants minusAuxiliaryConstants = create_sub_auxiliary_statements(left, right, var_decl_left, var_decl_right, var_decl_result,
				var_decl_overflow, marker_predicate_id);

		return minusAuxiliaryConstants;
	}



	private static int char_charIntToInt_sub_auxiliary_index = -1;

	public static MinusAuxiliaryConstants build_charIntToInt_sub_auxiliary_constants(AlloyExpression left, AlloyExpression right) {
		final int minus_auxiliary_index = ++char_charIntToInt_sub_auxiliary_index;
		final String predicate_id = JPredicateFactory.PRED_JAVA_PRIMITIVE_charIntToInt_VALUE_SUB;

		String sk_add_left = build_left_aux(predicate_id, minus_auxiliary_index);
		String sk_add_right = build_right_aux(predicate_id, minus_auxiliary_index);
		String sk_add_result = build_result_aux(predicate_id, minus_auxiliary_index);
		String sk_add_overflow = build_overflow_aux(predicate_id, minus_auxiliary_index);

		// create sk var declarations

		JVariableDeclaration var_decl_left = new JVariableDeclaration(new AlloyVariable(sk_add_left), JSignatureFactory.JAVA_PRIMITIVE_CHAR_VALUE);
		JVariableDeclaration var_decl_right = new JVariableDeclaration(new AlloyVariable(sk_add_right), JSignatureFactory.JAVA_PRIMITIVE_INTEGER_VALUE);
		JVariableDeclaration var_decl_result = new JVariableDeclaration(new AlloyVariable(sk_add_result), JSignatureFactory.JAVA_PRIMITIVE_INTEGER_VALUE);
		JVariableDeclaration var_decl_overflow = new JVariableDeclaration(new AlloyVariable(sk_add_overflow), JSignatureFactory.BOOLEAN_TYPE);

		String marker_predicate_id = JPredicateFactory.PRED_JAVA_PRIMITIVE_CHARINTtoINT_VALUE_SUB_MARKER;
		MinusAuxiliaryConstants minusAuxiliaryConstants = create_sub_auxiliary_statements(left, right, var_decl_left, var_decl_right, var_decl_result,
				var_decl_overflow, marker_predicate_id);

		return minusAuxiliaryConstants;
	}




	private static int char_charCharToInt_sub_auxiliary_index = -1;

	public static MinusAuxiliaryConstants build_charCharToInt_sub_auxiliary_constants(AlloyExpression left, AlloyExpression right) {
		final int minus_auxiliary_index = ++char_charCharToInt_sub_auxiliary_index;
		final String predicate_id = JPredicateFactory.PRED_JAVA_PRIMITIVE_charCharToInt_VALUE_SUB;

		String sk_add_left = build_left_aux(predicate_id, minus_auxiliary_index);
		String sk_add_right = build_right_aux(predicate_id, minus_auxiliary_index);
		String sk_add_result = build_result_aux(predicate_id, minus_auxiliary_index);
		String sk_add_overflow = build_overflow_aux(predicate_id, minus_auxiliary_index);

		// create sk var declarations

		JVariableDeclaration var_decl_left = new JVariableDeclaration(new AlloyVariable(sk_add_left), JSignatureFactory.JAVA_PRIMITIVE_CHAR_VALUE);
		JVariableDeclaration var_decl_right = new JVariableDeclaration(new AlloyVariable(sk_add_right), JSignatureFactory.JAVA_PRIMITIVE_CHAR_VALUE);
		JVariableDeclaration var_decl_result = new JVariableDeclaration(new AlloyVariable(sk_add_result), JSignatureFactory.JAVA_PRIMITIVE_INTEGER_VALUE);
		JVariableDeclaration var_decl_overflow = new JVariableDeclaration(new AlloyVariable(sk_add_overflow), JSignatureFactory.BOOLEAN_TYPE);

		String marker_predicate_id = JPredicateFactory.PRED_JAVA_PRIMITIVE_CHARCHARtoINT_VALUE_SUB_MARKER;
		MinusAuxiliaryConstants minusAuxiliaryConstants = create_sub_auxiliary_statements(left, right, var_decl_left, var_decl_right, var_decl_result,
				var_decl_overflow, marker_predicate_id);

		return minusAuxiliaryConstants;
	}




	private static int float_add_auxiliary_index = -1;


	public static AddAuxiliaryConstants build_float_add_auxiliary_constants(AlloyExpression left, AlloyExpression right) {

		final int add_auxiliary_index = ++float_add_auxiliary_index;
		final String predicate_id = JPredicateFactory.PRED_JAVA_PRIMITIVE_FLOAT_VALUE_ADD;

		String sk_add_left = build_left_aux(predicate_id, add_auxiliary_index);
		String sk_add_right = build_right_aux(predicate_id, add_auxiliary_index);
		String sk_add_result = build_result_aux(predicate_id, add_auxiliary_index);
		String sk_add_overflow = build_overflow_aux(predicate_id, add_auxiliary_index);

		// create sk var declarations

		JVariableDeclaration var_decl_left = new JVariableDeclaration(new AlloyVariable(sk_add_left), JSignatureFactory.JAVA_PRIMITIVE_FLOAT_VALUE);
		JVariableDeclaration var_decl_right = new JVariableDeclaration(new AlloyVariable(sk_add_right), JSignatureFactory.JAVA_PRIMITIVE_FLOAT_VALUE);
		JVariableDeclaration var_decl_result = new JVariableDeclaration(new AlloyVariable(sk_add_result), JSignatureFactory.JAVA_PRIMITIVE_FLOAT_VALUE);
		JVariableDeclaration var_decl_overflow = new JVariableDeclaration(new AlloyVariable(sk_add_overflow), JSignatureFactory.BOOLEAN_TYPE);

		String marker_predicate_id = JPredicateFactory.PRED_JAVA_PRIMITIVE_FLOAT_VALUE_ADD_MARKER;
		AddAuxiliaryConstants auxiliaryConstants = new AddAuxiliaryConstants(new JBlock(new JStatement[]{var_decl_left, var_decl_right, var_decl_result, var_decl_overflow, 
				create_add_auxiliary_statements(left, right, var_decl_left, var_decl_right, var_decl_result,
						var_decl_overflow, marker_predicate_id).statements}), ExprVariable.buildExprVariable(var_decl_result.getVariable()));


		return auxiliaryConstants;
	}

	private static int float_sub_auxiliary_index = -1;

	public static MinusAuxiliaryConstants build_float_sub_auxiliary_constants(AlloyExpression left, AlloyExpression right) {

		int sub_auxiliary_index = ++float_sub_auxiliary_index;
		final String predicate_id = JPredicateFactory.PRED_JAVA_PRIMITIVE_FLOAT_VALUE_SUB;

		String sk_sub_left = build_left_aux(predicate_id, sub_auxiliary_index);
		String sk_sub_right = build_right_aux(predicate_id, sub_auxiliary_index);
		String sk_sub_result = build_result_aux(predicate_id, sub_auxiliary_index);
		String sk_sub_overflow = build_overflow_aux(predicate_id, sub_auxiliary_index);

		JVariableDeclaration var_decl_left = new JVariableDeclaration(new AlloyVariable(sk_sub_left), JSignatureFactory.JAVA_PRIMITIVE_FLOAT_VALUE);
		JVariableDeclaration var_decl_right = new JVariableDeclaration(new AlloyVariable(sk_sub_right), JSignatureFactory.JAVA_PRIMITIVE_FLOAT_VALUE);
		JVariableDeclaration var_decl_result = new JVariableDeclaration(new AlloyVariable(sk_sub_result), JSignatureFactory.JAVA_PRIMITIVE_FLOAT_VALUE);
		JVariableDeclaration var_decl_overflow = new JVariableDeclaration(new AlloyVariable(sk_sub_overflow), JSignatureFactory.BOOLEAN_TYPE);

		String marker_predicate_id = JPredicateFactory.PRED_JAVA_PRIMITIVE_INTEGER_VALUE_SUB_MARKER;

		MinusAuxiliaryConstants subAuxiliaryConstants = create_sub_auxiliary_statements(left, right, var_decl_left, var_decl_right, var_decl_result,
				var_decl_overflow, marker_predicate_id);

		return subAuxiliaryConstants;
	}

	private static String build_left_aux(String predicate_id, int current_mul_auxiliary_index) {
		return "SK_" + predicate_id + "_ARG_" + "left_" + current_mul_auxiliary_index;
	}

	private static String build_right_aux(String predicate_id, int current_mul_auxiliary_index) {
		return "SK_" + predicate_id + "_ARG_" + "right_" + current_mul_auxiliary_index;
	}

	private static String build_result_aux(String predicate_id, int current_mul_auxiliary_index) {
		return "SK_" + predicate_id + "_ARG_" + "result_" + current_mul_auxiliary_index;
	}

	private static String build_overflow_aux(String predicate_id, int current_mul_auxiliary_index) {
		return "SK_" + predicate_id + "_ARG_" + "overflow_" + current_mul_auxiliary_index;
	}

	private static String build_remainder_aux(String predicate_id, int current_mul_auxiliary_index) {
		return "SK_" + predicate_id + "_ARG_" + "remainder_" + current_mul_auxiliary_index;
	}














	public static MultAuxiliaryConstants build_integer_mult_auxiliary_constants(AlloyExpression left, AlloyExpression right) {

		final int current_mul_auxiliary_index = ++integer_mul_auxiliary_index;
		final String predicate_id = JPredicateFactory.PRED_JAVA_PRIMITIVE_INTEGER_VALUE_MUL;

		// define sk var names

		String sk_mul_left = build_left_aux(predicate_id, current_mul_auxiliary_index);
		String sk_mul_right = build_right_aux(predicate_id, current_mul_auxiliary_index);
		String sk_mul_result = build_result_aux(predicate_id, current_mul_auxiliary_index);
		String sk_mul_overflow = build_overflow_aux(predicate_id, current_mul_auxiliary_index);

		// create sk var declarations

		JVariableDeclaration var_decl_left = new JVariableDeclaration(new AlloyVariable(sk_mul_left), JSignatureFactory.JAVA_PRIMITIVE_INTEGER_VALUE);
		JVariableDeclaration var_decl_right = new JVariableDeclaration(new AlloyVariable(sk_mul_right), JSignatureFactory.JAVA_PRIMITIVE_INTEGER_VALUE);
		JVariableDeclaration var_decl_result = new JVariableDeclaration(new AlloyVariable(sk_mul_result), JSignatureFactory.JAVA_PRIMITIVE_INTEGER_VALUE);
		JVariableDeclaration var_decl_overflow = new JVariableDeclaration(new AlloyVariable(sk_mul_overflow), JSignatureFactory.BOOLEAN_TYPE);

		String marker_predicate_id = JPredicateFactory.PRED_JAVA_PRIMITIVE_INTEGER_VALUE_MUL_MARKER;
		MultAuxiliaryConstants mulAuxiliaryConstants = create_mult_auxiliary_statements(left, right, var_decl_left, var_decl_right, var_decl_result,
				var_decl_overflow, marker_predicate_id);

		return mulAuxiliaryConstants;
	}

	private static MultAuxiliaryConstants create_mult_auxiliary_statements(AlloyExpression left, AlloyExpression right, JVariableDeclaration varDeclLeft,
			JVariableDeclaration varDeclRight, JVariableDeclaration varDeclResult, JVariableDeclaration varDeclOverflow, String marker_predicateId) {

		String sk_mul_left = varDeclLeft.getVariable().getVariableId().getString();
		String sk_mul_right = varDeclRight.getVariable().getVariableId().getString();
		String sk_mul_result = varDeclResult.getVariable().getVariableId().getString();
		String sk_mul_overflow = varDeclOverflow.getVariable().getVariableId().getString();

		ExprVariable mul_left = ExprVariable.buildExprVariable(sk_mul_left);
		ExprVariable mul_right = ExprVariable.buildExprVariable(sk_mul_right);
		ExprVariable mul_result = ExprVariable.buildExprVariable(sk_mul_result);
		ExprVariable mul_overflow = ExprVariable.buildExprVariable(sk_mul_overflow);

		List<AlloyExpression> predicate_arguments = Arrays.<AlloyExpression> asList(mul_left, mul_right, mul_result, mul_overflow);
		AlloyFormula mult_marker = new PredicateFormula(null, marker_predicateId, predicate_arguments);

		JHavoc havoc_left = new JHavoc(mul_left);
		JHavoc havoc_right = new JHavoc(mul_right);
		JHavoc havoc_result = new JHavoc(mul_result);
		JHavoc havoc_overflow = new JHavoc(mul_overflow);

		JAssume assume_left = new JAssume(JPredicateFactory.eq(mul_left, left));
		JAssume assume_right = new JAssume(JPredicateFactory.eq(mul_right, right));
		JAssume assume_marker = new JAssume(mult_marker);

		JBlock stmt = new JBlock(Arrays
				.<JStatement> asList(varDeclLeft, varDeclRight, varDeclResult, varDeclOverflow, 
						havoc_left,havoc_right,havoc_result,havoc_overflow,
						assume_left, assume_right, assume_marker));

		MultAuxiliaryConstants auxiliaryConstants = new MultAuxiliaryConstants(stmt, mul_result);

		return auxiliaryConstants;
	}

	//---------------------------


	private static int integer_add_auxiliary_index = -1;


	public static AddAuxiliaryConstants build_integer_add_auxiliary_constants(AlloyExpression left, AlloyExpression right) {

		AddAuxiliaryConstants auxiliaryConstants = null;

		final int add_auxiliary_index = ++integer_add_auxiliary_index;
		final String predicate_id = JPredicateFactory.PRED_JAVA_PRIMITIVE_INTEGER_VALUE_ADD;

		String sk_add_left = build_left_aux(predicate_id, add_auxiliary_index);
		String sk_add_right = build_right_aux(predicate_id, add_auxiliary_index);
		String sk_add_result = build_result_aux(predicate_id, add_auxiliary_index);
		String sk_add_overflow = build_overflow_aux(predicate_id, add_auxiliary_index);

		JVariableDeclaration var_decl_left = new JVariableDeclaration(new AlloyVariable(sk_add_left), JSignatureFactory.JAVA_PRIMITIVE_INTEGER_VALUE);
		JVariableDeclaration var_decl_right = new JVariableDeclaration(new AlloyVariable(sk_add_right), JSignatureFactory.JAVA_PRIMITIVE_INTEGER_VALUE);
		JVariableDeclaration var_decl_result = new JVariableDeclaration(new AlloyVariable(sk_add_result), JSignatureFactory.JAVA_PRIMITIVE_INTEGER_VALUE);
		JVariableDeclaration var_decl_overflow = new JVariableDeclaration(new AlloyVariable(sk_add_overflow), JSignatureFactory.BOOLEAN_TYPE);

		String marker_predicate_id = JPredicateFactory.PRED_JAVA_PRIMITIVE_INTEGER_VALUE_ADD_MARKER;

		auxiliaryConstants = new AddAuxiliaryConstants(new JBlock(new JStatement[]{var_decl_left, var_decl_right, var_decl_result, var_decl_overflow, 
				create_add_auxiliary_statements(left, right, var_decl_left, var_decl_right, var_decl_result,
						var_decl_overflow, marker_predicate_id).statements}), ExprVariable.buildExprVariable(var_decl_result.getVariable()));


		return auxiliaryConstants;
	} 




	//---------------------------




	private static AddAuxiliaryConstants create_add_auxiliary_statements(AlloyExpression left, AlloyExpression right, JVariableDeclaration varDeclLeft,
			JVariableDeclaration varDeclRight, JVariableDeclaration varDeclResult, JVariableDeclaration varDeclOverflow, String marker_predicateId) {

		String sk_add_left = varDeclLeft.getVariable().getVariableId().getString();
		String sk_add_right = varDeclRight.getVariable().getVariableId().getString();
		String sk_add_result = varDeclResult.getVariable().getVariableId().getString();
		String sk_add_overflow = varDeclOverflow.getVariable().getVariableId().getString();

		ExprVariable add_left = ExprVariable.buildExprVariable(sk_add_left);
		ExprVariable add_right = ExprVariable.buildExprVariable(sk_add_right);
		ExprVariable add_result = ExprVariable.buildExprVariable(sk_add_result);
		ExprVariable add_overflow = ExprVariable.buildExprVariable(sk_add_overflow);

		List<AlloyExpression> predicate_arguments = Arrays.<AlloyExpression> asList(add_left, add_right, add_result, add_overflow);
		AlloyFormula add_marker = new PredicateFormula(null, marker_predicateId, predicate_arguments);

		JHavoc havoc_left = new JHavoc(add_left);
		JHavoc havoc_right = new JHavoc(add_right);
		JHavoc havoc_result = new JHavoc(add_result);
		JHavoc havoc_overflow = new JHavoc(add_overflow);

		JAssume assume_left = new JAssume(JPredicateFactory.eq(add_left, left));
		JAssume assume_right = new JAssume(JPredicateFactory.eq(add_right, right));
		JAssume assume_marker = new JAssume(add_marker);

		JBlock stmt = new JBlock(Arrays
				.<JStatement> asList(
						havoc_left,havoc_right, havoc_result, havoc_overflow,
						assume_left, assume_right, assume_marker));

		AddAuxiliaryConstants auxiliaryConstants = new AddAuxiliaryConstants(stmt, add_result);

		return auxiliaryConstants;
	}

	private static MinusAuxiliaryConstants create_sub_auxiliary_statements(AlloyExpression left, AlloyExpression right, JVariableDeclaration varDeclLeft,
			JVariableDeclaration varDeclRight, JVariableDeclaration varDeclResult, JVariableDeclaration varDeclOverflow, String marker_predicateId) {

		String sk_sub_left = varDeclLeft.getVariable().getVariableId().getString();
		String sk_sub_right = varDeclRight.getVariable().getVariableId().getString();
		String sk_sub_result = varDeclResult.getVariable().getVariableId().getString();
		String sk_sub_overflow = varDeclOverflow.getVariable().getVariableId().getString();

		ExprVariable sub_left = ExprVariable.buildExprVariable(sk_sub_left);
		ExprVariable sub_right = ExprVariable.buildExprVariable(sk_sub_right);
		ExprVariable sub_result = ExprVariable.buildExprVariable(sk_sub_result);
		ExprVariable sub_overflow = ExprVariable.buildExprVariable(sk_sub_overflow);

		List<AlloyExpression> predicate_arguments = Arrays.<AlloyExpression> asList(sub_left, sub_right, sub_result, sub_overflow);
		AlloyFormula add_marker = new PredicateFormula(null, marker_predicateId, predicate_arguments);

		JHavoc havoc_left = new JHavoc(sub_left);
		JHavoc havoc_right = new JHavoc(sub_right);
		JHavoc havoc_result = new JHavoc(sub_result);
		JHavoc havoc_overflow = new JHavoc(sub_overflow);

		JAssume assume_left = new JAssume(JPredicateFactory.eq(sub_left, left));
		JAssume assume_right = new JAssume(JPredicateFactory.eq(sub_right, right));
		JAssume assume_marker = new JAssume(add_marker);

		JBlock stmt = new JBlock(Arrays
				.<JStatement> asList(varDeclLeft, varDeclRight, varDeclResult, varDeclOverflow, 
						havoc_left,havoc_right,havoc_result,havoc_overflow,
						assume_left, assume_right, assume_marker));

		MinusAuxiliaryConstants auxiliaryConstants = new MinusAuxiliaryConstants(stmt, sub_result);

		return auxiliaryConstants;
	}

	private static DivAuxiliaryConstants create_divide_auxiliary_statements(AlloyExpression left, AlloyExpression right, JVariableDeclaration varDeclLeft,
			JVariableDeclaration varDeclRight, JVariableDeclaration varDeclResult, JVariableDeclaration varDeclRemainder, String marker_predicate_Id) {

		String sk_div_left = varDeclLeft.getVariable().getVariableId().getString();
		String sk_div_right = varDeclRight.getVariable().getVariableId().getString();
		String sk_div_result = varDeclResult.getVariable().getVariableId().getString();
		String sk_div_remainder = varDeclRemainder.getVariable().getVariableId().getString();

		ExprVariable div_left = ExprVariable.buildExprVariable(sk_div_left);
		ExprVariable div_right = ExprVariable.buildExprVariable(sk_div_right);
		ExprVariable div_result = ExprVariable.buildExprVariable(sk_div_result);
		ExprVariable div_remainder = ExprVariable.buildExprVariable(sk_div_remainder);

		List<AlloyExpression> predicate_arguments = Arrays.<AlloyExpression> asList(div_left, div_right, div_result, div_remainder);

		AlloyFormula mult_marker = new PredicateFormula(null, marker_predicate_Id, predicate_arguments);

		JHavoc havoc_left = new JHavoc(div_left);
		JHavoc havoc_right = new JHavoc(div_right);
		JHavoc havoc_result = new JHavoc(div_result);
		JHavoc havoc_remainder = new JHavoc(div_remainder);

		JAssume assume_left = new JAssume(JPredicateFactory.eq(div_left, left));
		JAssume assume_right = new JAssume(JPredicateFactory.eq(div_right, right));
		JAssume assume_marker = new JAssume(mult_marker);

		JBlock stmt = new JBlock(Arrays.<JStatement> asList( 
				havoc_left,havoc_right,havoc_result,havoc_remainder,
				assume_left, assume_right,assume_marker));

		DivAuxiliaryConstants auxiliaryConstants = new DivAuxiliaryConstants(stmt, div_result, div_remainder);

		return auxiliaryConstants;
	}






	private static int integer_divide_auxiliary_index = -1;


	public static DivAuxiliaryConstants build_integer_divide_auxiliary_constants(AlloyExpression left, AlloyExpression right) {

		DivAuxiliaryConstants auxiliaryConstants = null;

		if (TacoConfigurator.getInstance().getCheckDivisionByZero() == true){

			DivAuxiliaryConstants auxiliaryConstantsThen = null;
			DivAuxiliaryConstants auxiliaryConstantsElse = null;

			int div_auxiliary_index = ++integer_divide_auxiliary_index;
			final String predicate_id = JPredicateFactory.PRED_JAVA_PRIMITIVE_INTEGER_VALUE_DIV_REM;

			String sk_div_right_old = "auxVarForArithmeticExceptionDivisionByZeroJavaPrimitiveIntegerValue_" + div_auxiliary_index;
			JVariableDeclaration var_decl_right_old = new JVariableDeclaration(new AlloyVariable(sk_div_right_old), JSignatureFactory.JAVA_PRIMITIVE_INTEGER_VALUE);
			ExprVariable div_right_old = ExprVariable.buildExprVariable(sk_div_right_old);

			JHavoc havoc_right_old = new JHavoc(div_right_old);
			JAssume assume_right_old = new JAssume(JPredicateFactory.eq(div_right_old, right));

			AlloyFormula isZero = JPredicateFactory.equZero(new ExprVariable(var_decl_right_old.getVariable()), "JavaPrimitiveIntegerValue");

			AlloyFormula arithmeticExceptionCondition = OrFormula.buildOrFormula(new AlloyFormula[]{isZero});


			div_auxiliary_index = ++integer_divide_auxiliary_index;

			String sk_div_left = build_left_aux(predicate_id, div_auxiliary_index);
			String sk_div_right_new = build_right_aux(predicate_id, div_auxiliary_index);
			String sk_div_result = build_result_aux(predicate_id, div_auxiliary_index);
			String sk_div_remainder = build_remainder_aux(predicate_id, div_auxiliary_index);

			JVariableDeclaration var_decl_left = new JVariableDeclaration(new AlloyVariable(sk_div_left), JSignatureFactory.JAVA_PRIMITIVE_INTEGER_VALUE);
			JVariableDeclaration var_decl_right_new = new JVariableDeclaration(new AlloyVariable(sk_div_right_new), JSignatureFactory.JAVA_PRIMITIVE_INTEGER_VALUE);
			JVariableDeclaration var_decl_result = new JVariableDeclaration(new AlloyVariable(sk_div_result), JSignatureFactory.JAVA_PRIMITIVE_INTEGER_VALUE);
			JVariableDeclaration var_decl_remainder = new JVariableDeclaration(new AlloyVariable(sk_div_remainder), JSignatureFactory.JAVA_PRIMITIVE_INTEGER_VALUE);


			String marker_predicate_id = JPredicateFactory.PRED_JAVA_PRIMITIVE_INTEGER_VALUE_DIV_REM_MARKER;

			auxiliaryConstantsThen = create_divide_auxiliary_statements_for_Arithmetic_Exception(left, right, var_decl_left, var_decl_right_old, var_decl_right_new, var_decl_result,
					var_decl_remainder, marker_predicate_id, JavaPrimitiveIntegerValue.getInstance().toJavaPrimitiveIntegerLiteral(1, false));

			String arithmetic_exception_literal = JArithmeticException
					.getInstance().getModule().getLiteralSingleton()
					.getSignatureId();

			JAssignment assign_throw = JDynAlloyFactory.assign(
					JExpressionFactory.THROW_EXPRESSION, ExprConstant
					.buildExprConstant(arithmetic_exception_literal));

			JBlock thenBranchExceptionHandling = new JBlock(new JStatement[]{assign_throw, auxiliaryConstantsThen.statements});


			auxiliaryConstantsElse = create_divide_auxiliary_statements(left, right, var_decl_left, var_decl_right_new, var_decl_result,
					var_decl_remainder, marker_predicate_id);

			JBlock elseBranchExceptionHandling = auxiliaryConstantsElse.statements;

			JStatement arithmeticExceptionITE = JDynAlloyFactory.ifThenElse(
					arithmeticExceptionCondition, thenBranchExceptionHandling, elseBranchExceptionHandling);

			JBlock arithmeticExceptionBlock = 
					new JBlock(
							new JStatement[]{
									var_decl_right_old, 
									havoc_right_old, 
									assume_right_old, 
									var_decl_left,
									var_decl_right_new,
									var_decl_result,
									var_decl_remainder,
									arithmeticExceptionITE});

			auxiliaryConstants = 
					new DivAuxiliaryConstants(arithmeticExceptionBlock, ExprVariable.buildExprVariable(var_decl_result.getVariable()), ExprVariable.buildExprVariable(var_decl_remainder.getVariable()));

		} else {


			final int div_auxiliary_index = ++integer_divide_auxiliary_index;
			final String predicate_id = JPredicateFactory.PRED_JAVA_PRIMITIVE_INTEGER_VALUE_DIV_REM;

			String sk_div_left = build_left_aux(predicate_id, div_auxiliary_index);
			String sk_div_right = build_right_aux(predicate_id, div_auxiliary_index);
			String sk_div_result = build_result_aux(predicate_id, div_auxiliary_index);
			String sk_div_remainder = build_remainder_aux(predicate_id, div_auxiliary_index);

			JVariableDeclaration var_decl_left = new JVariableDeclaration(new AlloyVariable(sk_div_left), JSignatureFactory.JAVA_PRIMITIVE_INTEGER_VALUE);
			JVariableDeclaration var_decl_right = new JVariableDeclaration(new AlloyVariable(sk_div_right), JSignatureFactory.JAVA_PRIMITIVE_INTEGER_VALUE);
			JVariableDeclaration var_decl_result = new JVariableDeclaration(new AlloyVariable(sk_div_result), JSignatureFactory.JAVA_PRIMITIVE_INTEGER_VALUE);
			JVariableDeclaration var_decl_remainder = new JVariableDeclaration(new AlloyVariable(sk_div_remainder), JSignatureFactory.JAVA_PRIMITIVE_INTEGER_VALUE);

			String marker_predicate_id = JPredicateFactory.PRED_JAVA_PRIMITIVE_INTEGER_VALUE_DIV_REM_MARKER;

			auxiliaryConstants = new DivAuxiliaryConstants(new JBlock(new JStatement[]{var_decl_left, var_decl_right, var_decl_result, var_decl_remainder, 
					create_divide_auxiliary_statements(left, right, var_decl_left, var_decl_right, var_decl_result,
							var_decl_remainder, marker_predicate_id).statements}), ExprVariable.buildExprVariable(var_decl_result.getVariable()), ExprVariable.buildExprVariable(var_decl_remainder.getVariable()));
		}

		return auxiliaryConstants;
	} 





	private static DivAuxiliaryConstants create_divide_auxiliary_statements_for_Arithmetic_Exception(
			AlloyExpression left, AlloyExpression right,
			JVariableDeclaration varDeclLeft,
			JVariableDeclaration varDeclRightOld,
			JVariableDeclaration varDeclRightNew,
			JVariableDeclaration varDeclResult,
			JVariableDeclaration varDeclRemainder, String marker_predicate_id, ExprConstant constantOne) {

		String sk_div_left = varDeclLeft.getVariable().getVariableId().getString();
		String sk_div_right_old = varDeclRightOld.getVariable().getVariableId().getString();
		String sk_div_right_new = varDeclRightNew.getVariable().getVariableId().getString();
		String sk_div_result = varDeclResult.getVariable().getVariableId().getString();
		String sk_div_remainder = varDeclRemainder.getVariable().getVariableId().getString();

		ExprVariable div_left = ExprVariable.buildExprVariable(sk_div_left);
		ExprVariable div_right_old = ExprVariable.buildExprVariable(sk_div_right_old);
		ExprVariable div_right_new = ExprVariable.buildExprVariable(sk_div_right_new);
		ExprVariable div_result = ExprVariable.buildExprVariable(sk_div_result);
		ExprVariable div_remainder = ExprVariable.buildExprVariable(sk_div_remainder);

		List<AlloyExpression> predicate_arguments = Arrays.<AlloyExpression> asList(div_left, div_right_new, div_result, div_remainder);

		AlloyFormula mult_marker = new PredicateFormula(null, marker_predicate_id, predicate_arguments);

		JHavoc havoc_left = new JHavoc(div_left);
		JHavoc havoc_right_old = new JHavoc(div_right_old);
		JHavoc havoc_right_new = new JHavoc(div_right_new);
		JHavoc havoc_result = new JHavoc(div_result);
		JHavoc havoc_remainder = new JHavoc(div_remainder);

		JAssume assume_left = new JAssume(JPredicateFactory.eq(div_left, left));
		JAssume assume_right_old = new JAssume(JPredicateFactory.eq(div_right_old, right));
		JAssume assume_right_new = new JAssume(JPredicateFactory.eq(div_right_new, constantOne));
		JAssume assume_marker = new JAssume(mult_marker);

		JBlock stmt = new JBlock(Arrays.<JStatement> asList( 
				havoc_left, havoc_right_old, havoc_right_new,havoc_result,havoc_remainder,
				assume_left, assume_right_old, assume_right_new, assume_marker));

		DivAuxiliaryConstants auxiliaryConstants = new DivAuxiliaryConstants(stmt, div_result, div_remainder);

		return auxiliaryConstants;


	}



	private static int long_divide_auxiliary_index = -1;

	public static DivAuxiliaryConstants build_long_divide_auxiliary_constants(AlloyExpression left, AlloyExpression right) {


		DivAuxiliaryConstants auxiliaryConstants = null;

		if (TacoConfigurator.getInstance().getCheckDivisionByZero() == true){

			DivAuxiliaryConstants auxiliaryConstantsThen = null;
			DivAuxiliaryConstants auxiliaryConstantsElse = null;

			int div_auxiliary_index = ++long_divide_auxiliary_index;
			final String predicate_id = JPredicateFactory.PRED_JAVA_PRIMITIVE_LONG_VALUE_DIV_REM;

			String sk_div_right_old = "auxVarForArithmeticExceptionDivisionByZeroJavaPrimitiveLongValue_" + div_auxiliary_index;
			JVariableDeclaration var_decl_right_old = new JVariableDeclaration(new AlloyVariable(sk_div_right_old), JSignatureFactory.JAVA_PRIMITIVE_LONG_VALUE);
			ExprVariable div_right_old = ExprVariable.buildExprVariable(sk_div_right_old);

			JHavoc havoc_right_old = new JHavoc(div_right_old);
			JAssume assume_right_old = new JAssume(JPredicateFactory.eq(div_right_old, right));

			AlloyFormula isZero = JPredicateFactory.equZero(new ExprVariable(var_decl_right_old.getVariable()), "JavaPrimitiveLongValue");

			AlloyFormula arithmeticExceptionCondition = OrFormula.buildOrFormula(new AlloyFormula[]{isZero});


			div_auxiliary_index = ++long_divide_auxiliary_index;

			String sk_div_left = build_left_aux(predicate_id, div_auxiliary_index);
			String sk_div_right_new = build_right_aux(predicate_id, div_auxiliary_index);
			String sk_div_result = build_result_aux(predicate_id, div_auxiliary_index);
			String sk_div_remainder = build_remainder_aux(predicate_id, div_auxiliary_index);

			JVariableDeclaration var_decl_left = new JVariableDeclaration(new AlloyVariable(sk_div_left), JSignatureFactory.JAVA_PRIMITIVE_LONG_VALUE);
			JVariableDeclaration var_decl_right_new = new JVariableDeclaration(new AlloyVariable(sk_div_right_new), JSignatureFactory.JAVA_PRIMITIVE_LONG_VALUE);
			JVariableDeclaration var_decl_result = new JVariableDeclaration(new AlloyVariable(sk_div_result), JSignatureFactory.JAVA_PRIMITIVE_LONG_VALUE);
			JVariableDeclaration var_decl_remainder = new JVariableDeclaration(new AlloyVariable(sk_div_remainder), JSignatureFactory.JAVA_PRIMITIVE_LONG_VALUE);


			String marker_predicate_id = JPredicateFactory.PRED_JAVA_PRIMITIVE_LONG_VALUE_DIV_REM_MARKER;

			auxiliaryConstantsThen = create_divide_auxiliary_statements_for_Arithmetic_Exception(left, right, var_decl_left, var_decl_right_old, var_decl_right_new, var_decl_result,
					var_decl_remainder, marker_predicate_id, JavaPrimitiveLongValue.getInstance().toJavaPrimitiveLongLiteral(1, false));

			String arithmetic_exception_literal = JArithmeticException
					.getInstance().getModule().getLiteralSingleton()
					.getSignatureId();

			JAssignment assign_throw = JDynAlloyFactory.assign(
					JExpressionFactory.THROW_EXPRESSION, ExprConstant
					.buildExprConstant(arithmetic_exception_literal));

			JBlock thenBranchExceptionHandling = new JBlock(new JStatement[]{assign_throw, auxiliaryConstantsThen.statements});


			auxiliaryConstantsElse = create_divide_auxiliary_statements(left, right, var_decl_left, var_decl_right_new, var_decl_result,
					var_decl_remainder, marker_predicate_id);

			JBlock elseBranchExceptionHandling = auxiliaryConstantsElse.statements;

			JStatement arithmeticExceptionITE = JDynAlloyFactory.ifThenElse(
					arithmeticExceptionCondition, thenBranchExceptionHandling, elseBranchExceptionHandling);

			JBlock arithmeticExceptionBlock = 
					new JBlock(
							new JStatement[]{
									var_decl_right_old, 
									havoc_right_old, 
									assume_right_old, 
									var_decl_left,
									var_decl_right_new,
									var_decl_result,
									var_decl_remainder,
									arithmeticExceptionITE});

			auxiliaryConstants = 
					new DivAuxiliaryConstants(arithmeticExceptionBlock, ExprVariable.buildExprVariable(var_decl_result.getVariable()), ExprVariable.buildExprVariable(var_decl_remainder.getVariable()));

		} else {


			final int div_auxiliary_index = ++long_divide_auxiliary_index;
			final String predicate_id = JPredicateFactory.PRED_JAVA_PRIMITIVE_LONG_VALUE_DIV_REM;

			String sk_div_left = build_left_aux(predicate_id, div_auxiliary_index);
			String sk_div_right = build_right_aux(predicate_id, div_auxiliary_index);
			String sk_div_result = build_result_aux(predicate_id, div_auxiliary_index);
			String sk_div_remainder = build_remainder_aux(predicate_id, div_auxiliary_index);

			JVariableDeclaration var_decl_left = new JVariableDeclaration(new AlloyVariable(sk_div_left), JSignatureFactory.JAVA_PRIMITIVE_LONG_VALUE);
			JVariableDeclaration var_decl_right = new JVariableDeclaration(new AlloyVariable(sk_div_right), JSignatureFactory.JAVA_PRIMITIVE_LONG_VALUE);
			JVariableDeclaration var_decl_result = new JVariableDeclaration(new AlloyVariable(sk_div_result), JSignatureFactory.JAVA_PRIMITIVE_LONG_VALUE);
			JVariableDeclaration var_decl_remainder = new JVariableDeclaration(new AlloyVariable(sk_div_remainder), JSignatureFactory.JAVA_PRIMITIVE_LONG_VALUE);

			String marker_predicate_id = JPredicateFactory.PRED_JAVA_PRIMITIVE_LONG_VALUE_DIV_REM_MARKER;

			auxiliaryConstants = new DivAuxiliaryConstants(new JBlock(new JStatement[]{var_decl_left, var_decl_right, var_decl_result, var_decl_remainder, 
					create_divide_auxiliary_statements(left, right, var_decl_left, var_decl_right, var_decl_result,
							var_decl_remainder, marker_predicate_id).statements}), ExprVariable.buildExprVariable(var_decl_result.getVariable()), ExprVariable.buildExprVariable(var_decl_remainder.getVariable()));
		}

		return auxiliaryConstants;
	}



	private static int float_divide_auxiliary_index = -1;

	public static DivAuxiliaryConstants build_float_divide_auxiliary_constants(AlloyExpression left, AlloyExpression right) {

		final int div_auxiliary_index = ++float_divide_auxiliary_index;
		final String predicate_id = JPredicateFactory.PRED_JAVA_PRIMITIVE_FLOAT_VALUE_DIV;

		String sk_div_left = build_left_aux(predicate_id, div_auxiliary_index);
		String sk_div_right = build_right_aux(predicate_id, div_auxiliary_index);
		String sk_div_result = build_result_aux(predicate_id, div_auxiliary_index);
		String sk_div_remainder = build_remainder_aux(predicate_id, div_auxiliary_index);

		JVariableDeclaration var_decl_left = new JVariableDeclaration(new AlloyVariable(sk_div_left), JSignatureFactory.JAVA_PRIMITIVE_FLOAT_VALUE);
		JVariableDeclaration var_decl_right = new JVariableDeclaration(new AlloyVariable(sk_div_right), JSignatureFactory.JAVA_PRIMITIVE_FLOAT_VALUE);
		JVariableDeclaration var_decl_result = new JVariableDeclaration(new AlloyVariable(sk_div_result), JSignatureFactory.JAVA_PRIMITIVE_FLOAT_VALUE);
		JVariableDeclaration var_decl_remainder = new JVariableDeclaration(new AlloyVariable(sk_div_remainder), JSignatureFactory.JAVA_PRIMITIVE_FLOAT_VALUE);

		String marker_predicate_id = JPredicateFactory.PRED_JAVA_PRIMITIVE_FLOAT_VALUE_DIV_MARKER;

		DivAuxiliaryConstants auxiliaryConstants = new DivAuxiliaryConstants(new JBlock(new JStatement[]{var_decl_left, var_decl_right, var_decl_result, var_decl_remainder, 
				create_divide_auxiliary_statements(left, right, var_decl_left, var_decl_right, var_decl_result,
						var_decl_remainder, marker_predicate_id).statements}), ExprVariable.buildExprVariable(var_decl_result.getVariable()), ExprVariable.buildExprVariable(var_decl_remainder.getVariable()));

		return auxiliaryConstants;
	}

}