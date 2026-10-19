package ar.edu.taco.dynalloy;

import ar.uba.dc.rfm.alloy.ast.expressions.AlloyExpression;
import ar.uba.dc.rfm.alloy.ast.expressions.ExprFunction;
import ar.uba.dc.rfm.alloy.ast.expressions.ExprVariable;
import ar.uba.dc.rfm.alloy.ast.expressions.ExpressionVisitor;
import ar.uba.dc.rfm.alloy.ast.formulas.FormulaVisitor;
import ar.uba.dc.rfm.dynalloy.ast.programs.InvokeAction;
import ar.uba.dc.rfm.dynalloy.util.DfsProgramVisitor;

/**
 * Counts the Java-arithmetic operations in DynAlloy programs and formulas, for TACO's scope
 * inference: {@link DynAlloyProgramScopeInferencePlugin} runs it over the method under
 * analysis, with its loops unrolled and its calls inlined, and over the formulas of its
 * specification. The counts go to an {@link ArithmeticOpCounter}, from which
 * {@code ar.edu.taco.infer.ScopeInference} sizes the primitive-value signatures
 * ({@code JavaPrimitiveIntegerValue}, ...).
 *
 * <p>Operations are recognized by name only, so these names are a contract with the code that
 * produces them:
 * <ul>
 * <li>calls of the Java-arithmetic Alloy functions, wherever they appear in a formula or an
 *     expression: {@code int} and {@code long} additions and subtractions (the
 *     {@code fun_java_primitive_char_value_add...} and {@code sub...} functions included,
 *     counted by their result type), {@code int} shifts ({@code sshr}) and {@code size_of},
 *     the casts to {@code int} and to {@code long}, and the narrowing casts to {@code char},
 *     the only {@code char} operations counted;</li>
 * <li>{@code int} and {@code long} multiplications, divisions and remainders, and the four
 *     {@code float} operations, which are not functions: each {@code havocVariable} of a
 *     variable whose name contains {@code pred_java_primitive_<type>_value_<op>_ARG_result_}
 *     (the result variable that {@code ar.edu.taco.simplejml.builtin.AuxiliaryConstantsFactory}
 *     declares) counts one.</li>
 * </ul>
 *
 * <p>The counts accumulate over every visit and are never reset, so a collector serves one
 * translation. The visitor's results are not used. Holds no static state.
 */
class ArithmeticOpCollector extends DfsProgramVisitor {

    // arithmetic op counter
    private ArithmeticOpCounter arithmetic_op_counter = new ArithmeticOpCounter();

    private class ArithmeticOpExprCollector extends ExpressionVisitor {

        @Override
        public Object visit(ExprFunction n) {

            // char operations
            if (n.getFunctionId().equals("fun_narrowing_cast_int_to_char")) {
                arithmetic_op_counter.charOpCounter.inc_java_primitive_char_value_narrowing_cast();
            } else if (n.getFunctionId().equals("fun_narrowing_cast_long_to_char")) {
                arithmetic_op_counter.charOpCounter.inc_java_primitive_char_value_narrowing_cast();
            }
            // integer operations
            if (n.getFunctionId().equals("fun_java_primitive_integer_value_add")) {
                arithmetic_op_counter.integerOpCounter.inc_add();
            } else if (n.getFunctionId().equals("fun_java_primitive_integer_value_sub")) {
                arithmetic_op_counter.integerOpCounter.inc_sub();
            } else if (n.getFunctionId().equals("fun_java_primitive_integer_value_sshr")) {
                arithmetic_op_counter.integerOpCounter.inc_sshr();
            } else if (n.getFunctionId().equals("fun_cast_char_to_int")) {
                arithmetic_op_counter.integerOpCounter.inc_cast_from_char();
            } else if (n.getFunctionId().equals("fun_narrowing_cast_long_to_int")){
                arithmetic_op_counter.integerOpCounter.inc_narrowing_cast();;
            } else if (n.getFunctionId().equals("fun_java_primitive_char_value_addCharCharToJavaPrimitiveIntegerValue")) {
                arithmetic_op_counter.integerOpCounter.inc_add();
            } else if (n.getFunctionId().equals("fun_java_primitive_char_value_addCharIntToJavaPrimitiveIntegerValue")) {
                arithmetic_op_counter.integerOpCounter.inc_add();
            } else if (n.getFunctionId().equals("fun_java_primitive_char_value_addIntCharToJavaPrimitiveIntegerValue")) {
                arithmetic_op_counter.integerOpCounter.inc_add();
            } else if (n.getFunctionId().equals("fun_java_primitive_char_value_subCharCharToJavaPrimitiveIntegerValue")) {
                arithmetic_op_counter.integerOpCounter.inc_sub();
            } else if (n.getFunctionId().equals("fun_java_primitive_char_value_subCharIntToJavaPrimitiveIntegerValue")) {
                arithmetic_op_counter.integerOpCounter.inc_sub();
            } else if (n.getFunctionId().equals("fun_java_primitive_char_value_subIntCharToJavaPrimitiveIntegerValue")) {
                arithmetic_op_counter.integerOpCounter.inc_sub();
            } else if (n.getFunctionId().equals("fun_java_primitive_integer_value_size_of")){
                arithmetic_op_counter.integerOpCounter.inc_sizeof();
            }

            // long operations
            if (n.getFunctionId().equals("fun_java_primitive_long_value_add")) {
                arithmetic_op_counter.longOpCounter.inc_java_primitive_long_value_add();
            } else if (n.getFunctionId().equals("fun_java_primitive_long_value_sub")) {
                arithmetic_op_counter.longOpCounter.inc_java_primitive_long_value_sub();
            } else if (n.getFunctionId().equals("fun_cast_char_to_long")) {
                arithmetic_op_counter.longOpCounter.inc_java_primitive_long_value_casts();
            } else if (n.getFunctionId().equals("fun_cast_int_to_long")) {
                arithmetic_op_counter.longOpCounter.inc_java_primitive_long_value_casts();
            } else if (n.getFunctionId().equals("fun_java_primitive_char_value_addCharLongToJavaPrimitiveLongValue")) {
                arithmetic_op_counter.longOpCounter.inc_java_primitive_long_value_add();
            } else if (n.getFunctionId().equals("fun_java_primitive_char_value_addLongCharToJavaPrimitiveLongValue")) {
                arithmetic_op_counter.longOpCounter.inc_java_primitive_long_value_add();
            } else if (n.getFunctionId().equals("fun_java_primitive_char_value_subCharLongToJavaPrimitiveLongValue")) {
                arithmetic_op_counter.longOpCounter.inc_java_primitive_long_value_sub();
            } else if (n.getFunctionId().equals("fun_java_primitive_char_value_subLongCharToJavaPrimitiveLongValue")) {
                arithmetic_op_counter.longOpCounter.inc_java_primitive_long_value_sub();
            }

            return super.visit(n);
        }

    }

    private ArithmeticOpExprCollector arithmeticOpExprCollector;

    /**
     * Creates a collector with empty counts. The function calls are counted by its formula
     * visitor ({@code getDfsFormulaVisitor()}), so that visitor can be run on a formula alone,
     * as {@link DynAlloyProgramScopeInferencePlugin} does for the specification; on a formula,
     * only the function calls are counted.
     */
    public ArithmeticOpCollector() {
        super(new FormulaVisitor());
        arithmeticOpExprCollector = new ArithmeticOpExprCollector();
        this.getDfsFormulaVisitor().setExpressionVisitor(arithmeticOpExprCollector);
    }

    /**
     * Counts one multiplication, division, remainder or {@code float} operation if the call is
     * {@code havocVariable} of one variable whose name marks it as the result of such an
     * operation (see the class comment), then visits the call as usual.
     *
     * @param u the action call
     * @return the result of the default traversal
     */
    @Override
    public Object visit(InvokeAction u) {
        if (u.getActionId().equals("havocVariable")) {
            if (u.getActualParameters().size() == 1) {
                AlloyExpression actual_parameter = u.getActualParameters().get(0);
                if (actual_parameter instanceof ExprVariable) {
                    ExprVariable expr_variable_parameter = (ExprVariable) actual_parameter;
                    String expr_variable_str = expr_variable_parameter.getVariable().toString();

                    // integer operations
                    if (expr_variable_str.contains("pred_java_primitive_integer_value_mul_ARG_result_")) {
                        arithmetic_op_counter.integerOpCounter.inc_mul();
                    } else if (expr_variable_str.contains("pred_java_primitive_integer_value_div_rem_ARG_result_")) {
                        arithmetic_op_counter.integerOpCounter.inc_div_rem();
                    }

                    // long operations
                    if (expr_variable_str.contains("pred_java_primitive_long_value_mul_ARG_result_")) {
                        arithmetic_op_counter.longOpCounter.inc_java_primitive_long_value_mul();
                    } else if (expr_variable_str.contains("pred_java_primitive_long_value_div_rem_ARG_result_")) {
                        arithmetic_op_counter.longOpCounter.inc_java_primitive_long_value_div_rem();
                    }

                    // float operations
                    if (expr_variable_str.contains("pred_java_primitive_float_value_mul_ARG_result_")) {
                        arithmetic_op_counter.floatOpCounter.inc_java_primitive_float_value_mul();
                    } else if (expr_variable_str.contains("pred_java_primitive_float_value_div_ARG_result_")) {
                        arithmetic_op_counter.floatOpCounter.inc_java_primitive_float_value_div();
                    } else if (expr_variable_str.contains("pred_java_primitive_float_value_add_ARG_result_")) {
                        arithmetic_op_counter.floatOpCounter.inc_java_primitive_float_value_add();
                    } else if (expr_variable_str.contains("pred_java_primitive_float_value_sub_ARG_result_")) {
                        arithmetic_op_counter.floatOpCounter.inc_java_primitive_float_value_sub();
                    }

                }
            }

        }
        return super.visit(u);
    }

    public ArithmeticOpCounter getArithmeticOpCounter() {
        return this.arithmetic_op_counter;
    }
}