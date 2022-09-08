/**
 * 
 */
package ar.edu.taco.simplejml.builtin;

import java.util.Arrays;
import java.util.List;

import ar.edu.jdynalloy.ast.JAssume;
import ar.edu.jdynalloy.ast.JHavoc;
import ar.edu.jdynalloy.ast.JVariableDeclaration;
import ar.edu.jdynalloy.factory.JPredicateFactory;
import ar.edu.jdynalloy.factory.JSignatureFactory;
import ar.edu.jdynalloy.xlator.JType;
import ar.edu.taco.simplejml.builtin.JMLAuxiliaryConstantsFactory.JMLDivAuxiliaryConstants;
import ar.edu.taco.simplejml.builtin.JMLAuxiliaryConstantsFactory.JMLModuloAuxiliaryConstants;
import ar.edu.taco.simplejml.builtin.JMLAuxiliaryConstantsFactory.JMLMultAuxiliaryConstants;
import ar.uba.dc.rfm.alloy.AlloyVariable;
import ar.uba.dc.rfm.alloy.ast.expressions.AlloyExpression;
import ar.uba.dc.rfm.alloy.ast.expressions.ExprVariable;
import ar.uba.dc.rfm.alloy.ast.formulas.AlloyFormula;
import ar.uba.dc.rfm.alloy.ast.formulas.PredicateFormula;

public abstract class JMLAuxiliaryConstantsFactory {

	private static abstract class JMLAuxiliaryConstants {

		public JMLAuxiliaryConstants(PredicateFormula pred, ExprVariable ret_variable) {
			this.result_variable = ret_variable;
			this.pred = pred;
		}

		public ExprVariable result_variable;
		public ExprVariable overflow_or_compatibility_variable;
		public PredicateFormula pred;
	}



	//-------------- ADD --------------//



	public final static class JMLAddAuxiliaryConstants extends JMLAuxiliaryConstants {



		public JMLAddAuxiliaryConstants(PredicateFormula pred, ExprVariable ret_variable, ExprVariable overflow_variable) {
			super(pred, ret_variable);
			this.overflow_or_compatibility_variable = overflow_variable;
		}

	}




	private static JMLAddAuxiliaryConstants create_add_auxiliary_formulas(AlloyExpression left, AlloyExpression right, JVariableDeclaration varDeclResult, JVariableDeclaration varDeclOverflow, String predicateId) {

		String sk_add_result = varDeclResult.getVariable().getVariableId().getString();
		String sk_add_overflow = varDeclOverflow.getVariable().getVariableId().getString();

		ExprVariable add_result = ExprVariable.buildNonMutableExprVariable(sk_add_result);
		ExprVariable add_overflow = ExprVariable.buildNonMutableExprVariable(sk_add_overflow);

		List<AlloyExpression> predicate_arguments = Arrays.asList(left, right, add_result, add_overflow);

		PredicateFormula add_marker = new PredicateFormula(null, predicateId, predicate_arguments);

		JMLAddAuxiliaryConstants answer = new JMLAddAuxiliaryConstants(add_marker, add_result, add_overflow);

		return answer;
	}


	private static int integer_jml_add_auxiliary_index = -1;

	private static int integer_char_jml_add_auxiliary_index = -1;

	private static int char_integer_jml_add_auxiliary_index = -1;

	private static int char_char_jml_add_auxiliary_index = -1;


	public static JMLAddAuxiliaryConstants build_integer_add_auxiliary_constants(AlloyExpression left, AlloyExpression right) {

		final int current_add_auxiliary_index = ++integer_jml_add_auxiliary_index;
		final String predicate_id = JPredicateFactory.PRED_JAVA_PRIMITIVE_INTEGER_VALUE_ADD;

		// define sk var names

		String sk_add_result = build_result_aux(predicate_id + "_add", current_add_auxiliary_index);
		String sk_add_overflow = build_overflow_aux(predicate_id + "_add", current_add_auxiliary_index);

		// create sk var declarations

		JVariableDeclaration var_decl_result = new JVariableDeclaration(AlloyVariable.buildNonMutableAlloyVariable(sk_add_result), JSignatureFactory.JAVA_PRIMITIVE_INTEGER_VALUE);
		JVariableDeclaration var_decl_overflow = new JVariableDeclaration(AlloyVariable.buildNonMutableAlloyVariable(sk_add_overflow), JSignatureFactory.BOOLEAN_TYPE);

		JMLAddAuxiliaryConstants addAuxiliaryConstants = create_add_auxiliary_formulas(left, right, var_decl_result,
				var_decl_overflow, predicate_id);

		return addAuxiliaryConstants;
	}


	public static JMLAddAuxiliaryConstants build_integer_char_add_auxiliary_constants(AlloyExpression left, AlloyExpression right) {

		final int current_add_auxiliary_index = ++integer_char_jml_add_auxiliary_index;
		final String predicate_id = JPredicateFactory.PRED_JAVA_PRIMITIVE_intCharToInt_VALUE_ADD;

		// define sk var names

		String sk_add_result = build_result_aux(predicate_id + "_add", current_add_auxiliary_index);
		String sk_add_overflow = build_overflow_aux(predicate_id + "_add", current_add_auxiliary_index);

		// create sk var declarations

		JVariableDeclaration var_decl_result = new JVariableDeclaration(AlloyVariable.buildNonMutableAlloyVariable(sk_add_result), JSignatureFactory.JAVA_PRIMITIVE_INTEGER_VALUE);
		JVariableDeclaration var_decl_overflow = new JVariableDeclaration(AlloyVariable.buildNonMutableAlloyVariable(sk_add_overflow), JSignatureFactory.BOOLEAN_TYPE);

		JMLAddAuxiliaryConstants addAuxiliaryConstants = create_add_auxiliary_formulas(left, right, var_decl_result,
				var_decl_overflow, predicate_id);

		return addAuxiliaryConstants;
	}


	public static JMLAddAuxiliaryConstants build_char_integer_add_auxiliary_constants(AlloyExpression left, AlloyExpression right) {

		final int current_add_auxiliary_index = ++char_integer_jml_add_auxiliary_index;
		final String predicate_id = JPredicateFactory.PRED_JAVA_PRIMITIVE_charIntToInt_VALUE_ADD;

		// define sk var names

		String sk_add_result = build_result_aux(predicate_id + "_add", current_add_auxiliary_index);
		String sk_add_overflow = build_overflow_aux(predicate_id + "_add", current_add_auxiliary_index);

		// create sk var declarations

		JVariableDeclaration var_decl_result = new JVariableDeclaration(AlloyVariable.buildNonMutableAlloyVariable(sk_add_result), JSignatureFactory.JAVA_PRIMITIVE_INTEGER_VALUE);
		JVariableDeclaration var_decl_overflow = new JVariableDeclaration(AlloyVariable.buildNonMutableAlloyVariable(sk_add_overflow), JSignatureFactory.BOOLEAN_TYPE);

		JMLAddAuxiliaryConstants addAuxiliaryConstants = create_add_auxiliary_formulas(left, right, var_decl_result,
				var_decl_overflow, predicate_id);

		return addAuxiliaryConstants;
	}


	public static JMLAddAuxiliaryConstants build_char_char_add_auxiliary_constants(AlloyExpression left, AlloyExpression right) {

		final int current_add_auxiliary_index = ++char_char_jml_add_auxiliary_index;
		final String predicate_id = JPredicateFactory.PRED_JAVA_PRIMITIVE_charCharToInt_VALUE_ADD;

		// define sk var names

		String sk_add_result = build_result_aux(predicate_id + "_add", current_add_auxiliary_index);
		String sk_add_overflow = build_overflow_aux(predicate_id + "_add", current_add_auxiliary_index);

		// create sk var declarations

		JVariableDeclaration var_decl_result = new JVariableDeclaration(AlloyVariable.buildNonMutableAlloyVariable(sk_add_result), JSignatureFactory.JAVA_PRIMITIVE_INTEGER_VALUE);
		JVariableDeclaration var_decl_overflow = new JVariableDeclaration(AlloyVariable.buildNonMutableAlloyVariable(sk_add_overflow), JSignatureFactory.BOOLEAN_TYPE);

		JMLAddAuxiliaryConstants addAuxiliaryConstants = create_add_auxiliary_formulas(left, right, var_decl_result,
				var_decl_overflow, predicate_id);

		return addAuxiliaryConstants;
	}


	private static int long_jml_add_auxiliary_index = -1;

	public static JMLAddAuxiliaryConstants build_long_add_auxiliary_constants(AlloyExpression left, AlloyExpression right) {

		final int current_add_auxiliary_index = ++long_jml_add_auxiliary_index;
		final String predicate_id = JPredicateFactory.PRED_JAVA_PRIMITIVE_LONG_VALUE_ADD;

		// define sk var names

		String sk_add_result = build_result_aux(predicate_id + "_add", current_add_auxiliary_index);
		String sk_add_overflow = build_overflow_aux(predicate_id + "_add", current_add_auxiliary_index);

		// create sk var declarations

		JVariableDeclaration var_decl_result = new JVariableDeclaration(AlloyVariable.buildNonMutableAlloyVariable(sk_add_result), JSignatureFactory.JAVA_PRIMITIVE_LONG_VALUE);
		JVariableDeclaration var_decl_overflow = new JVariableDeclaration(AlloyVariable.buildNonMutableAlloyVariable(sk_add_overflow), JSignatureFactory.BOOLEAN_TYPE);

		JMLAddAuxiliaryConstants addAuxiliaryConstants = create_add_auxiliary_formulas(left, right, var_decl_result,
				var_decl_overflow, predicate_id);

		return addAuxiliaryConstants;
	}


	private static int float_add_auxiliary_index = -1;

	public static JMLAddAuxiliaryConstants build_float_add_auxiliary_constants(AlloyExpression left, AlloyExpression right) {

		final int add_auxiliary_index = ++float_add_auxiliary_index;
		final String predicate_id = JPredicateFactory.PRED_JAVA_PRIMITIVE_FLOAT_VALUE_ADD;

		String sk_add_result = build_result_aux(predicate_id + "_add", add_auxiliary_index);
		String sk_add_compatibility = build_compatibility_aux(predicate_id + "_add", add_auxiliary_index);

		// create sk var declarations

		JVariableDeclaration var_decl_result = new JVariableDeclaration(AlloyVariable.buildNonMutableAlloyVariable(sk_add_result), JSignatureFactory.JAVA_PRIMITIVE_FLOAT_VALUE);
		JVariableDeclaration var_decl_compatibility = new JVariableDeclaration(AlloyVariable.buildNonMutableAlloyVariable(sk_add_compatibility), JSignatureFactory.BOOLEAN_TYPE);

		JMLAddAuxiliaryConstants addAuxiliaryConstants = create_add_auxiliary_formulas(left, right, var_decl_result,
				var_decl_compatibility, predicate_id);

		return addAuxiliaryConstants;
	}



	//-------------- MINUS --------------//


	public final static class JMLMinusAuxiliaryConstants extends JMLAuxiliaryConstants {

		public JMLMinusAuxiliaryConstants(PredicateFormula pred, ExprVariable ret_variable, ExprVariable overflow_variable) {
			super(pred, ret_variable);
			this.overflow_or_compatibility_variable = overflow_variable;
		}	
	}







	private static JMLMinusAuxiliaryConstants create_sub_auxiliary_formulas(AlloyExpression left, AlloyExpression right, JVariableDeclaration varDeclResult, JVariableDeclaration varDeclOverflow, String predicateId) {

		String sk_sub_result = varDeclResult.getVariable().getVariableId().getString();
		String sk_sub_overflow = varDeclOverflow.getVariable().getVariableId().getString();

		ExprVariable sub_result = ExprVariable.buildNonMutableExprVariable(sk_sub_result);
		ExprVariable sub_overflow = ExprVariable.buildNonMutableExprVariable(sk_sub_overflow);

		List<AlloyExpression> predicate_arguments = Arrays.asList(left, right, sub_result, sub_overflow);

		PredicateFormula minus_marker = new PredicateFormula(null, predicateId, predicate_arguments);

		JMLMinusAuxiliaryConstants answer = new JMLMinusAuxiliaryConstants(minus_marker, sub_result, sub_overflow);

		return answer;
	}


	private static int integer_jml_minus_auxiliary_index = -1;
	private static int char_integer_jml_minus_auxiliary_index = -1;
	private static int integer_char_jml_minus_auxiliary_index = -1;
	private static int char_char_jml_minus_auxiliary_index = -1;
	private static int long_jml_minus_auxiliary_index = -1;
	private static int float_jml_minus_auxiliary_index = -1;


	public static JMLMinusAuxiliaryConstants build_integer_minus_auxiliary_constants(AlloyExpression left, AlloyExpression right) {

		final int current_minus_auxiliary_index = ++integer_jml_minus_auxiliary_index;
		final String predicate_id = JPredicateFactory.PRED_JAVA_PRIMITIVE_INTEGER_VALUE_SUB;

		// define sk var names

		String sk_add_result = build_result_aux(predicate_id + "_minus", current_minus_auxiliary_index);
		String sk_add_overflow = build_overflow_aux(predicate_id + "_minus", current_minus_auxiliary_index);

		// create sk var declarations

		JVariableDeclaration var_decl_result = new JVariableDeclaration(AlloyVariable.buildNonMutableAlloyVariable(sk_add_result), JSignatureFactory.JAVA_PRIMITIVE_INTEGER_VALUE);
		JVariableDeclaration var_decl_overflow = new JVariableDeclaration(AlloyVariable.buildNonMutableAlloyVariable(sk_add_overflow), JSignatureFactory.BOOLEAN_TYPE);

		JMLMinusAuxiliaryConstants subAuxiliaryConstants = create_sub_auxiliary_formulas(left, right, var_decl_result, var_decl_overflow, predicate_id);
		

		return subAuxiliaryConstants;
	}

	public static JMLMinusAuxiliaryConstants build_char_integer_minus_auxiliary_constants(AlloyExpression left, AlloyExpression right) {

		final int current_minus_auxiliary_index = ++char_integer_jml_minus_auxiliary_index;
		final String predicate_id = JPredicateFactory.PRED_JAVA_PRIMITIVE_charIntToInt_VALUE_SUB;

		// define sk var names

		String sk_add_result = build_result_aux(predicate_id + "_minus", current_minus_auxiliary_index);
		String sk_add_overflow = build_overflow_aux(predicate_id + "_minus", current_minus_auxiliary_index);

		// create sk var declarations

		JVariableDeclaration var_decl_result = new JVariableDeclaration(AlloyVariable.buildNonMutableAlloyVariable(sk_add_result), JSignatureFactory.JAVA_PRIMITIVE_INTEGER_VALUE);
		JVariableDeclaration var_decl_overflow = new JVariableDeclaration(AlloyVariable.buildNonMutableAlloyVariable(sk_add_overflow), JSignatureFactory.BOOLEAN_TYPE);

		JMLMinusAuxiliaryConstants subAuxiliaryConstants = create_sub_auxiliary_formulas(left, right, var_decl_result, var_decl_overflow, predicate_id);

		return subAuxiliaryConstants;
	}

	public static JMLMinusAuxiliaryConstants build_integer_char_minus_auxiliary_constants(AlloyExpression left, AlloyExpression right) {

		final int current_minus_auxiliary_index = ++integer_char_jml_minus_auxiliary_index;
		final String predicate_id = JPredicateFactory.PRED_JAVA_PRIMITIVE_intCharToInt_VALUE_SUB;

		// define sk var names

		String sk_add_result = build_result_aux(predicate_id + "_minus", current_minus_auxiliary_index);
		String sk_add_overflow = build_overflow_aux(predicate_id + "_minus", current_minus_auxiliary_index);

		// create sk var declarations

		JVariableDeclaration var_decl_result = new JVariableDeclaration(AlloyVariable.buildNonMutableAlloyVariable(sk_add_result), JSignatureFactory.JAVA_PRIMITIVE_INTEGER_VALUE);
		JVariableDeclaration var_decl_overflow = new JVariableDeclaration(AlloyVariable.buildNonMutableAlloyVariable(sk_add_overflow), JSignatureFactory.BOOLEAN_TYPE);

		JMLMinusAuxiliaryConstants subAuxiliaryConstants = create_sub_auxiliary_formulas(left, right, var_decl_result, var_decl_overflow, predicate_id);

		return subAuxiliaryConstants;
	}


	public static JMLMinusAuxiliaryConstants build_char_char_minus_auxiliary_constants(AlloyExpression left, AlloyExpression right) {

		final int current_minus_auxiliary_index = ++char_char_jml_minus_auxiliary_index;
		final String predicate_id = JPredicateFactory.PRED_JAVA_PRIMITIVE_charCharToInt_VALUE_SUB;

		// define sk var names

		String sk_add_result = build_result_aux(predicate_id + "_minus", current_minus_auxiliary_index);
		String sk_add_overflow = build_overflow_aux(predicate_id + "_minus", current_minus_auxiliary_index);

		// create sk var declarations

		JVariableDeclaration var_decl_result = new JVariableDeclaration(AlloyVariable.buildNonMutableAlloyVariable(sk_add_result), JSignatureFactory.JAVA_PRIMITIVE_INTEGER_VALUE);
		JVariableDeclaration var_decl_overflow = new JVariableDeclaration(AlloyVariable.buildNonMutableAlloyVariable(sk_add_overflow), JSignatureFactory.BOOLEAN_TYPE);

		JMLMinusAuxiliaryConstants subAuxiliaryConstants = create_sub_auxiliary_formulas(left, right, var_decl_result, var_decl_overflow, predicate_id);

		return subAuxiliaryConstants;
	}


	public static JMLMinusAuxiliaryConstants build_long_minus_auxiliary_constants(AlloyExpression left, AlloyExpression right) {

		final int current_minus_auxiliary_index = ++long_jml_minus_auxiliary_index;
		final String predicate_id = JPredicateFactory.PRED_JAVA_PRIMITIVE_LONG_VALUE_SUB;

		// define sk var names

		String sk_sub_result = build_result_aux(predicate_id + "_minus", current_minus_auxiliary_index);
		String sk_sub_overflow = build_overflow_aux(predicate_id + "_minus", current_minus_auxiliary_index);

		// create sk var declarations

		JVariableDeclaration var_decl_result = new JVariableDeclaration(AlloyVariable.buildNonMutableAlloyVariable(sk_sub_result), JSignatureFactory.JAVA_PRIMITIVE_LONG_VALUE);
		JVariableDeclaration var_decl_overflow = new JVariableDeclaration(AlloyVariable.buildNonMutableAlloyVariable(sk_sub_overflow), JSignatureFactory.BOOLEAN_TYPE);

		JMLMinusAuxiliaryConstants subAuxiliaryConstants = create_sub_auxiliary_formulas(left, right, var_decl_result, var_decl_overflow, predicate_id);

		return subAuxiliaryConstants;
	}



	public static JMLMinusAuxiliaryConstants build_float_minus_auxiliary_constants(AlloyExpression left, AlloyExpression right) {

		final int current_minus_auxiliary_index = ++float_jml_minus_auxiliary_index;
		final String predicate_id = JPredicateFactory.PRED_JAVA_PRIMITIVE_FLOAT_VALUE_SUB;

		// define sk var names

		String sk_sub_result = build_result_aux(predicate_id + "_minus", current_minus_auxiliary_index);
		String sk_sub_compatibility = build_compatibility_aux(predicate_id + "_minus", current_minus_auxiliary_index);

		// create sk var declarations

		JVariableDeclaration var_decl_result = new JVariableDeclaration(AlloyVariable.buildNonMutableAlloyVariable(sk_sub_result), JSignatureFactory.JAVA_PRIMITIVE_FLOAT_VALUE);
		JVariableDeclaration var_decl_compatibility = new JVariableDeclaration(AlloyVariable.buildNonMutableAlloyVariable(sk_sub_compatibility), JSignatureFactory.BOOLEAN_TYPE);

		JMLMinusAuxiliaryConstants subAuxiliaryConstants = create_sub_auxiliary_formulas(left, right, var_decl_result, var_decl_compatibility, predicate_id);

		return subAuxiliaryConstants;
	}









	//-------------- MULT --------------//

	public final static class JMLMultAuxiliaryConstants extends JMLAuxiliaryConstants {


		public JMLMultAuxiliaryConstants(PredicateFormula pred, ExprVariable ret_variable, ExprVariable overflow_variable) {
			super(pred, ret_variable);
			this.overflow_or_compatibility_variable = overflow_variable;  
		}
	}


	private static int integer_jml_mul_auxiliary_index = -1;

	private static int long_jml_mul_auxiliary_index = -1;

	private static int float_mul_auxiliary_index = -1;

	private static int char_int_jml_mul_auxiliary_index = -1;

	private static int char_long_jml_mul_auxiliary_index = -1;


	public static JMLMultAuxiliaryConstants build_integer_mult_auxiliary_constants(AlloyExpression left, AlloyExpression right) {

		final int current_mul_auxiliary_index = ++integer_jml_mul_auxiliary_index;
		final String predicate_id = JPredicateFactory.PRED_JAVA_PRIMITIVE_INTEGER_VALUE_MUL;

		// define sk var names

		String sk_mul_result = build_result_aux(predicate_id + "_mult", current_mul_auxiliary_index);
		String sk_mul_overflow = build_overflow_aux(predicate_id + "_mult", current_mul_auxiliary_index);

		// create sk var declarations

		JVariableDeclaration var_decl_result = new JVariableDeclaration(AlloyVariable.buildNonMutableAlloyVariable(sk_mul_result), JSignatureFactory.JAVA_PRIMITIVE_INTEGER_VALUE);
		JVariableDeclaration var_decl_overflow = new JVariableDeclaration(AlloyVariable.buildNonMutableAlloyVariable(sk_mul_overflow), JSignatureFactory.BOOLEAN_TYPE);

		JMLMultAuxiliaryConstants mulAuxiliaryConstants = create_mult_auxiliary_formulas(left, right, var_decl_result,
				var_decl_overflow, predicate_id, JSignatureFactory.JAVA_PRIMITIVE_INTEGER_VALUE);

		return mulAuxiliaryConstants;
	}
	
	
	public static JMLMultAuxiliaryConstants build_char_int_mult_auxiliary_constants(AlloyExpression left, AlloyExpression right) {

		final int current_mul_auxiliary_index = ++char_int_jml_mul_auxiliary_index;
		final String predicate_id = JPredicateFactory.PRED_JAVA_PRIMITIVE_CHAR_VALUE_INT_MUL;

		// define sk var names

		String sk_mul_result = build_result_aux(predicate_id + "_mult", current_mul_auxiliary_index);
		String sk_mul_overflow = build_overflow_aux(predicate_id + "_mult", current_mul_auxiliary_index);

		// create sk var declarations

		JVariableDeclaration var_decl_result = new JVariableDeclaration(AlloyVariable.buildNonMutableAlloyVariable(sk_mul_result), JSignatureFactory.JAVA_PRIMITIVE_INTEGER_VALUE);
		JVariableDeclaration var_decl_overflow = new JVariableDeclaration(AlloyVariable.buildNonMutableAlloyVariable(sk_mul_overflow), JSignatureFactory.BOOLEAN_TYPE);

		JMLMultAuxiliaryConstants mulAuxiliaryConstants = create_mult_auxiliary_formulas(left, right, var_decl_result,
				var_decl_overflow, predicate_id, JSignatureFactory.JAVA_PRIMITIVE_INTEGER_VALUE);

		return mulAuxiliaryConstants;
	}
	
	
	public static JMLMultAuxiliaryConstants build_char_long_mult_auxiliary_constants(AlloyExpression left, AlloyExpression right) {

		final int current_mul_auxiliary_index = ++char_long_jml_mul_auxiliary_index;
		final String predicate_id = JPredicateFactory.PRED_JAVA_PRIMITIVE_CHAR_VALUE_LONG_MUL;

		// define sk var names

		String sk_mul_result = build_result_aux(predicate_id + "_mult", current_mul_auxiliary_index);
		String sk_mul_overflow = build_overflow_aux(predicate_id + "_mult", current_mul_auxiliary_index);

		// create sk var declarations

		JVariableDeclaration var_decl_result = new JVariableDeclaration(AlloyVariable.buildNonMutableAlloyVariable(sk_mul_result), JSignatureFactory.JAVA_PRIMITIVE_LONG_VALUE);
		JVariableDeclaration var_decl_overflow = new JVariableDeclaration(AlloyVariable.buildNonMutableAlloyVariable(sk_mul_overflow), JSignatureFactory.BOOLEAN_TYPE);

		JMLMultAuxiliaryConstants mulAuxiliaryConstants = create_mult_auxiliary_formulas(left, right, var_decl_result,
				var_decl_overflow, predicate_id, JSignatureFactory.JAVA_PRIMITIVE_LONG_VALUE);

		return mulAuxiliaryConstants;
	}



	public static JMLMultAuxiliaryConstants build_long_mult_auxiliary_constants(AlloyExpression left, AlloyExpression right) {

		final int current_mul_auxiliary_index = ++long_jml_mul_auxiliary_index;
		final String predicateId = JPredicateFactory.PRED_JAVA_PRIMITIVE_LONG_VALUE_MUL;

		String sk_mul_result = build_result_aux(predicateId + "_mult", current_mul_auxiliary_index);
		String sk_mul_overflow = build_overflow_aux(predicateId + "_mult", current_mul_auxiliary_index);

		JVariableDeclaration var_decl_result = new JVariableDeclaration(AlloyVariable.buildNonMutableAlloyVariable(sk_mul_result), JSignatureFactory.JAVA_PRIMITIVE_LONG_VALUE);
		JVariableDeclaration var_decl_overflow = new JVariableDeclaration(AlloyVariable.buildNonMutableAlloyVariable(sk_mul_overflow), JSignatureFactory.BOOLEAN_TYPE);

		JMLMultAuxiliaryConstants mulAuxiliaryConstants = create_mult_auxiliary_formulas(left, right, var_decl_result,
				var_decl_overflow, predicateId, JSignatureFactory.JAVA_PRIMITIVE_LONG_VALUE);

		return mulAuxiliaryConstants;
	}


	public static JMLMultAuxiliaryConstants build_float_mult_auxiliary_constants(AlloyExpression left, AlloyExpression right) {

		final int current_mul_auxiliary_index = ++float_mul_auxiliary_index;
		final String predicateId = JPredicateFactory.PRED_JAVA_PRIMITIVE_FLOAT_VALUE_MUL;

		String sk_mul_result = build_result_aux(predicateId + "_mult", current_mul_auxiliary_index);
		String sk_mul_overflow = build_overflow_aux(predicateId + "_mult", current_mul_auxiliary_index);

		JVariableDeclaration var_decl_result = new JVariableDeclaration(AlloyVariable.buildNonMutableAlloyVariable(sk_mul_result), JSignatureFactory.JAVA_PRIMITIVE_FLOAT_VALUE);
		JVariableDeclaration var_decl_overflow = new JVariableDeclaration(AlloyVariable.buildNonMutableAlloyVariable(sk_mul_overflow), JSignatureFactory.BOOLEAN_TYPE);

		JMLMultAuxiliaryConstants mulAuxiliaryConstants = create_mult_auxiliary_formulas(left, right, var_decl_result,
				var_decl_overflow, predicateId, JSignatureFactory.JAVA_PRIMITIVE_FLOAT_VALUE);

		return mulAuxiliaryConstants;
	}



	private static JMLMultAuxiliaryConstants create_mult_auxiliary_formulas(AlloyExpression left, AlloyExpression right,
			JVariableDeclaration varDeclResult, JVariableDeclaration varDeclOverflow, String predicateId, JType varType) {

		String sk_mul_result = varDeclResult.getVariable().getVariableId().getString();
		String sk_mul_overflow = varDeclOverflow.getVariable().getVariableId().getString();

		ExprVariable mul_result = ExprVariable.buildNonMutableExprVariable(sk_mul_result);
		ExprVariable mul_overflow = ExprVariable.buildNonMutableExprVariable(sk_mul_overflow);


		List<AlloyExpression> predicate_arguments = Arrays.asList(left, right, mul_result, mul_overflow);

		PredicateFormula mult_marker = new PredicateFormula(null, predicateId, predicate_arguments);



		JMLMultAuxiliaryConstants answer = new JMLMultAuxiliaryConstants(mult_marker, mul_result, mul_overflow);

		return answer;
	}




	//-------------- DIV --------------//

	public final static class JMLDivAuxiliaryConstants extends JMLAuxiliaryConstants {

		public ExprVariable remainder;

		public JMLDivAuxiliaryConstants(PredicateFormula pred, ExprVariable result_variable, ExprVariable remainder_variable) {
			super(pred, result_variable);
			this.remainder = remainder_variable;
		}
	}


	private static JMLDivAuxiliaryConstants create_divide_auxiliary_formulas(AlloyExpression left, AlloyExpression right, 
			JVariableDeclaration varDeclResult, JVariableDeclaration varDeclRemainder, String predicateId) {

		String sk_div_result = varDeclResult.getVariable().getVariableId().getString();
		String sk_div_remainder = varDeclRemainder.getVariable().getVariableId().getString();

		ExprVariable div_result = ExprVariable.buildNonMutableExprVariable(sk_div_result);
		ExprVariable div_remainder = ExprVariable.buildNonMutableExprVariable(sk_div_remainder);

		List<AlloyExpression> predicate_arguments = Arrays.asList(left, right, div_result, div_remainder);

		PredicateFormula div_marker = new PredicateFormula(null, predicateId, predicate_arguments);

		JMLDivAuxiliaryConstants answer = new JMLDivAuxiliaryConstants(div_marker, div_result, div_remainder);

		return answer;
	}




	private static int integer_divide_auxiliary_index = -1;

	private static int long_divide_auxiliary_index = -1;

	private static int float_divide_auxiliary_index = -1;



	public static JMLDivAuxiliaryConstants build_integer_divide_auxiliary_constants(AlloyExpression left, AlloyExpression right) {

		final int div_auxiliary_index = ++integer_divide_auxiliary_index;
		final String predicate_id = JPredicateFactory.PRED_JAVA_PRIMITIVE_INTEGER_VALUE_DIV_REM;

		String sk_div_result = build_result_aux(predicate_id + "_divide", div_auxiliary_index);
		String sk_div_remainder = build_remainder_aux(predicate_id + "_divide", div_auxiliary_index);

		JVariableDeclaration var_decl_result = new JVariableDeclaration(AlloyVariable.buildNonMutableAlloyVariable(sk_div_result), JSignatureFactory.JAVA_PRIMITIVE_INTEGER_VALUE);
		JVariableDeclaration var_decl_remainder = new JVariableDeclaration(AlloyVariable.buildNonMutableAlloyVariable(sk_div_remainder), JSignatureFactory.JAVA_PRIMITIVE_INTEGER_VALUE);

		JMLDivAuxiliaryConstants auxiliaryConstants = create_divide_auxiliary_formulas(left, right, var_decl_result, var_decl_remainder, predicate_id);

		return auxiliaryConstants;
	}


	public static JMLDivAuxiliaryConstants build_long_divide_auxiliary_constants(AlloyExpression left, AlloyExpression right) {

		final int div_auxiliary_index = ++long_divide_auxiliary_index;
		final String predicate_id = JPredicateFactory.PRED_JAVA_PRIMITIVE_LONG_VALUE_DIV_REM;

		String sk_div_result = build_result_aux(predicate_id + "_divide", div_auxiliary_index);
		String sk_div_remainder = build_remainder_aux(predicate_id + "_divide", div_auxiliary_index);

		JVariableDeclaration var_decl_result = new JVariableDeclaration(AlloyVariable.buildNonMutableAlloyVariable(sk_div_result), JSignatureFactory.JAVA_PRIMITIVE_LONG_VALUE);
		JVariableDeclaration var_decl_remainder = new JVariableDeclaration(AlloyVariable.buildNonMutableAlloyVariable(sk_div_remainder), JSignatureFactory.JAVA_PRIMITIVE_LONG_VALUE);

		String predicateId = JPredicateFactory.PRED_JAVA_PRIMITIVE_LONG_VALUE_DIV_REM_MARKER;

		JMLDivAuxiliaryConstants auxiliaryConstants = create_divide_auxiliary_formulas(left, right, var_decl_result, var_decl_remainder, predicateId);

		return auxiliaryConstants;
	}




	public static JMLDivAuxiliaryConstants build_float_divide_auxiliary_constants(AlloyExpression left, AlloyExpression right) {

		final int div_auxiliary_index = ++float_divide_auxiliary_index;
		final String predicate_id = JPredicateFactory.PRED_JAVA_PRIMITIVE_FLOAT_VALUE_DIV;

		String sk_div_result = build_result_aux(predicate_id + "_divide", div_auxiliary_index);
		String sk_div_remainder = build_remainder_aux(predicate_id + "_divide", div_auxiliary_index);

		JVariableDeclaration var_decl_result = new JVariableDeclaration(AlloyVariable.buildNonMutableAlloyVariable(sk_div_result), JSignatureFactory.JAVA_PRIMITIVE_FLOAT_VALUE);
		JVariableDeclaration var_decl_remainder = new JVariableDeclaration(AlloyVariable.buildNonMutableAlloyVariable(sk_div_remainder), JSignatureFactory.JAVA_PRIMITIVE_FLOAT_VALUE);

		JMLDivAuxiliaryConstants auxiliaryConstants = create_divide_auxiliary_formulas(left, right, var_decl_result,
				var_decl_remainder, predicate_id);

		return auxiliaryConstants;
	}







	//-------------- MOD --------------//

	public final static class JMLModuloAuxiliaryConstants extends JMLAuxiliaryConstants {

		public ExprVariable remainder;

		public JMLModuloAuxiliaryConstants(PredicateFormula pred, ExprVariable quotient_variable, ExprVariable remainder_variable) {
			super(pred, quotient_variable);
			this.remainder = remainder_variable;
		}
	}


	private static JMLModuloAuxiliaryConstants create_modulo_auxiliary_formulas(AlloyExpression left, AlloyExpression right, 
			JVariableDeclaration varDeclQuotient, JVariableDeclaration varDeclRemainder, String predicateId) {

		String sk_mod_quotient = varDeclQuotient.getVariable().getVariableId().getString();
		String sk_mod_remainder = varDeclRemainder.getVariable().getVariableId().getString();

		ExprVariable mod_quotient = ExprVariable.buildNonMutableExprVariable(sk_mod_quotient);
		ExprVariable mod_remainder = ExprVariable.buildNonMutableExprVariable(sk_mod_remainder);

		List<AlloyExpression> predicate_arguments = Arrays.asList(left, right, mod_quotient, mod_remainder);

		PredicateFormula mod_marker = new PredicateFormula(null, predicateId, predicate_arguments);

		JMLModuloAuxiliaryConstants answer = new JMLModuloAuxiliaryConstants(mod_marker, mod_quotient, mod_remainder);

		return answer;
	}



	private static int integer_modulo_auxiliary_index = -1;

	private static int long_modulo_auxiliary_index = -1;

	private static int char_modulo_int_auxiliary_index = -1;

	private static int char_modulo_long_auxiliary_index = -1;

	private static int char_int_divide_auxiliary_index = -1;

	private static int char_long_divide_auxiliary_index = -1;

	





	public static JMLModuloAuxiliaryConstants build_integer_modulo_auxiliary_constants(AlloyExpression left, AlloyExpression right) {

		final int mod_auxiliary_index = ++integer_modulo_auxiliary_index;
		final String predicate_id = JPredicateFactory.PRED_JAVA_PRIMITIVE_INTEGER_VALUE_DIV_REM;

		String sk_mod_quotient = build_result_aux(predicate_id + "_modulo", mod_auxiliary_index);
		String sk_mod_remainder = build_remainder_aux(predicate_id + "_modulo", mod_auxiliary_index);

		JVariableDeclaration var_decl_quotient = new JVariableDeclaration(AlloyVariable.buildNonMutableAlloyVariable(sk_mod_quotient), JSignatureFactory.JAVA_PRIMITIVE_INTEGER_VALUE);
		JVariableDeclaration var_decl_remainder = new JVariableDeclaration(AlloyVariable.buildNonMutableAlloyVariable(sk_mod_remainder), JSignatureFactory.JAVA_PRIMITIVE_INTEGER_VALUE);

		JMLModuloAuxiliaryConstants auxiliaryConstants = create_modulo_auxiliary_formulas(left, right, var_decl_quotient, var_decl_remainder, predicate_id);

		return auxiliaryConstants;
	}


	public static JMLModuloAuxiliaryConstants build_long_modulo_auxiliary_constants(AlloyExpression left, AlloyExpression right) {

		final int mod_auxiliary_index = ++long_modulo_auxiliary_index;
		final String predicate_id = JPredicateFactory.PRED_JAVA_PRIMITIVE_LONG_VALUE_DIV_REM;

		String sk_mod_quotient = build_result_aux(predicate_id + "_modulo", mod_auxiliary_index);
		String sk_mod_remainder = build_remainder_aux(predicate_id + "_modulo", mod_auxiliary_index);

		JVariableDeclaration var_decl_quotient = new JVariableDeclaration(AlloyVariable.buildNonMutableAlloyVariable(sk_mod_quotient), JSignatureFactory.JAVA_PRIMITIVE_LONG_VALUE);
		JVariableDeclaration var_decl_remainder = new JVariableDeclaration(AlloyVariable.buildNonMutableAlloyVariable(sk_mod_remainder), JSignatureFactory.JAVA_PRIMITIVE_LONG_VALUE);


		JMLModuloAuxiliaryConstants auxiliaryConstants = create_modulo_auxiliary_formulas(left, right, var_decl_quotient, var_decl_remainder, predicate_id);

		return auxiliaryConstants;
	}


	public static JMLModuloAuxiliaryConstants build_char_modulo_int_auxiliary_constants(AlloyExpression left, AlloyExpression right) {
		final int mod_auxiliary_index = ++char_modulo_int_auxiliary_index;
		final String predicate_id = JPredicateFactory.PRED_JAVA_PRIMITIVE_CHAR_INT_VALUE_DIV_REM;


		String sk_mod_quotient = build_result_aux(predicate_id + "_modulo", mod_auxiliary_index);
		String sk_mod_remainder = build_remainder_aux(predicate_id + "_modulo", mod_auxiliary_index);

		JVariableDeclaration var_decl_quotient = new JVariableDeclaration(AlloyVariable.buildNonMutableAlloyVariable(sk_mod_quotient), JSignatureFactory.JAVA_PRIMITIVE_INTEGER_VALUE);
		JVariableDeclaration var_decl_remainder = new JVariableDeclaration(AlloyVariable.buildNonMutableAlloyVariable(sk_mod_remainder), JSignatureFactory.JAVA_PRIMITIVE_INTEGER_VALUE);


		JMLModuloAuxiliaryConstants auxiliaryConstants = create_modulo_auxiliary_formulas(left, right, var_decl_quotient, var_decl_remainder, predicate_id);

		return auxiliaryConstants;	
	}


	public static JMLModuloAuxiliaryConstants build_char_modulo_long_auxiliary_constants(AlloyExpression left,
			AlloyExpression right) {
		final int mod_auxiliary_index = ++char_modulo_long_auxiliary_index;
		final String predicate_id = JPredicateFactory.PRED_JAVA_PRIMITIVE_CHAR_LONG_VALUE_DIV_REM;


		String sk_mod_quotient = build_result_aux(predicate_id + "_modulo", mod_auxiliary_index);
		String sk_mod_remainder = build_remainder_aux(predicate_id + "_modulo", mod_auxiliary_index);

		JVariableDeclaration var_decl_quotient = new JVariableDeclaration(AlloyVariable.buildNonMutableAlloyVariable(sk_mod_quotient), JSignatureFactory.JAVA_PRIMITIVE_INTEGER_VALUE);
		JVariableDeclaration var_decl_remainder = new JVariableDeclaration(AlloyVariable.buildNonMutableAlloyVariable(sk_mod_remainder), JSignatureFactory.JAVA_PRIMITIVE_INTEGER_VALUE);


		JMLModuloAuxiliaryConstants auxiliaryConstants = create_modulo_auxiliary_formulas(left, right, var_decl_quotient, var_decl_remainder, predicate_id);

		return auxiliaryConstants;		
	}
	
	




	//----------------- AUX FUNCTIONS ------------------//

	private static String build_result_aux(String predicate_id, int current_mul_auxiliary_index) {
		return "SK_jml_" + predicate_id + "_ARG_" + "result_" + current_mul_auxiliary_index;
	}

	private static String build_overflow_aux(String predicate_id, int current_mul_auxiliary_index) {
		return "SK_jml_" + predicate_id + "_ARG_" + "overflow_" + current_mul_auxiliary_index;
	}

	private static String build_remainder_aux(String predicate_id, int current_mul_auxiliary_index) {
		return "SK_jml_" + predicate_id + "_ARG_" + "remainder_" + current_mul_auxiliary_index;
	}

	private static String build_compatibility_aux(String predicate_id, int current_add_auxiliary_index) {
		return "SK_jml_" + predicate_id + "_ARG_" + "compatibility_" + current_add_auxiliary_index;
	}


	public static JMLDivAuxiliaryConstants build_char_int_divide_auxiliary_constants(AlloyExpression left,
			AlloyExpression right) {
		final int div_auxiliary_index = ++char_int_divide_auxiliary_index;
		final String predicate_id = JPredicateFactory.PRED_JAVA_PRIMITIVE_CHAR_INT_VALUE_DIV_REM;

		String sk_div_result = build_result_aux(predicate_id + "_divide", div_auxiliary_index);
		String sk_div_remainder = build_remainder_aux(predicate_id + "_divide", div_auxiliary_index);

		JVariableDeclaration var_decl_result = new JVariableDeclaration(AlloyVariable.buildNonMutableAlloyVariable(sk_div_result), JSignatureFactory.JAVA_PRIMITIVE_INTEGER_VALUE);
		JVariableDeclaration var_decl_remainder = new JVariableDeclaration(AlloyVariable.buildNonMutableAlloyVariable(sk_div_remainder), JSignatureFactory.JAVA_PRIMITIVE_INTEGER_VALUE);

		String predicateId = JPredicateFactory.PRED_JAVA_PRIMITIVE_CHAR_VALUE_DIV_REM_INT_MARKER;

		JMLDivAuxiliaryConstants auxiliaryConstants = create_divide_auxiliary_formulas(left, right, var_decl_result, var_decl_remainder, predicateId);

		return auxiliaryConstants;
	}


	public static JMLDivAuxiliaryConstants build_char_long_divide_auxiliary_constants(AlloyExpression left,
			AlloyExpression right) {
		final int div_auxiliary_index = ++char_long_divide_auxiliary_index;
		final String predicate_id = JPredicateFactory.PRED_JAVA_PRIMITIVE_CHAR_LONG_VALUE_DIV_REM;

		String sk_div_result = build_result_aux(predicate_id + "_divide", div_auxiliary_index);
		String sk_div_remainder = build_remainder_aux(predicate_id + "_divide", div_auxiliary_index);

		JVariableDeclaration var_decl_result = new JVariableDeclaration(AlloyVariable.buildNonMutableAlloyVariable(sk_div_result), JSignatureFactory.JAVA_PRIMITIVE_LONG_VALUE);
		JVariableDeclaration var_decl_remainder = new JVariableDeclaration(AlloyVariable.buildNonMutableAlloyVariable(sk_div_remainder), JSignatureFactory.JAVA_PRIMITIVE_LONG_VALUE);

		String predicateId = JPredicateFactory.PRED_JAVA_PRIMITIVE_CHAR_VALUE_DIV_REM_LONG_MARKER;

		JMLDivAuxiliaryConstants auxiliaryConstants = create_divide_auxiliary_formulas(left, right, var_decl_result, var_decl_remainder, predicateId);

		return auxiliaryConstants;
	}


































}