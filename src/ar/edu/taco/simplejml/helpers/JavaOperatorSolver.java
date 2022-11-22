/*
 * TACO: Translation of Annotated COde
 * Copyright (c) 2010 Universidad de Buenos Aires
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA,
 * 02110-1301, USA
 */
package ar.edu.taco.simplejml.helpers;


import org.jmlspecs.checker.Constants;

import ar.edu.jdynalloy.factory.JExpressionFactory;
import ar.edu.jdynalloy.factory.JPredicateFactory;
import ar.edu.jdynalloy.factory.JSignatureFactory;
import ar.edu.jdynalloy.xlator.JType;
import ar.edu.taco.TacoConfigurator;
import ar.edu.taco.TacoException;
import ar.edu.taco.TacoNotImplementedYetException;
import ar.edu.taco.simplejml.builtin.JavaPrimitiveCharValue;
import ar.edu.taco.simplejml.builtin.JavaPrimitiveIntegerValue;
import ar.uba.dc.rfm.alloy.ast.expressions.AlloyExpression;
import ar.uba.dc.rfm.alloy.ast.formulas.AlloyFormula;
import ar.uba.dc.rfm.alloy.ast.formulas.AndFormula;
import ar.uba.dc.rfm.alloy.ast.formulas.ImpliesFormula;
import ar.uba.dc.rfm.alloy.ast.formulas.NotFormula;

/**
 * @author elgaby
 * 
 */
public class JavaOperatorSolver {

	private static abstract class GenericOperatorSolver {

		public final Object getAlloyBinaryExpression(AlloyExpression left,
				AlloyExpression right, int operator) {

			switch (operator) {

			case Constants.OPE_PLUS: // represents "+"
				return add(left, right);
			case Constants.OPE_MINUS: // represents "-"
				return sub(left, right);
			case Constants.OPE_STAR: // represents "*"
				return mul(left, right);
			case Constants.OPE_SLASH: // represents "/"
				return div(left, right);
			case Constants.OPE_PERCENT: // represents "%"
				return rem(left, right);
			case Constants.OPE_SR: // represents ">>"
				return signed_shr(left, right);
			case Constants.OPE_BSR: // represents ">>>"
				return unsigned_shr(left, right);
			case Constants.OPE_SL: // represents "<<"
				return shl(left, right);
			case Constants.OPE_LT: // represents "<"
				return lt(left, right);
			case Constants.OPE_LE: // represents "<="
				return lte(left, right);
			case Constants.OPE_GT: // represents ">"
				return gt(left, right);
			case Constants.OPE_GE: // represents ">="
				return gte(left, right);
			case Constants.OPE_EQ: // represents "=="
				return eq(left, right);
			default:
				throw new IllegalStateException(
						"Unsupported operator for overloading resolution");
			}
		}

		public abstract Object add(AlloyExpression l, AlloyExpression r);

		public abstract Object sub(AlloyExpression l, AlloyExpression r);

		public abstract Object mul(AlloyExpression l, AlloyExpression r);

		public abstract Object div(AlloyExpression l, AlloyExpression r);

		public abstract Object rem(AlloyExpression l, AlloyExpression r);

		public abstract Object unsigned_shr(AlloyExpression l, AlloyExpression r);

		public abstract Object signed_shr(AlloyExpression l, AlloyExpression r);

		public abstract Object shl(AlloyExpression l, AlloyExpression r);

		public abstract Object lt(AlloyExpression l, AlloyExpression r);

		public abstract Object gt(AlloyExpression l, AlloyExpression r);

		public abstract Object lte(AlloyExpression l, AlloyExpression r);

		public abstract Object gte(AlloyExpression l, AlloyExpression r);

		public abstract Object eq(AlloyExpression l, AlloyExpression r);

	}

	private static class JavaIntegerOperatorSolver extends GenericOperatorSolver {

		private static JavaIntegerOperatorSolver instance;

		public static JavaIntegerOperatorSolver getInstance() {
			if (instance == null)
				instance = new JavaIntegerOperatorSolver();

			return instance;
		}

		@Override
		public Object add(AlloyExpression l, AlloyExpression r) {
			return JExpressionFactory
					.fun_java_primitive_integer_value_add(l, r);
		}

		@Override
		public Object div(AlloyExpression l, AlloyExpression r) {
			return JExpressionFactory
					.fun_java_primitive_integer_value_div(l, r);
		}

		@Override
		public Object gt(AlloyExpression l, AlloyExpression r) {
			return JPredicateFactory.pred_java_primitive_integer_value_gt(l, r);
		}

		@Override
		public Object gte(AlloyExpression l, AlloyExpression r) {
			return JPredicateFactory
					.pred_java_primitive_integer_value_gte(l, r);
		}

		@Override
		public Object lt(AlloyExpression l, AlloyExpression r) {
			return JPredicateFactory.pred_java_primitive_integer_value_lt(l, r);
		}

		@Override
		public Object lte(AlloyExpression l, AlloyExpression r) {
			return JPredicateFactory
					.pred_java_primitive_integer_value_lte(l, r);
		}

		@Override 
		public Object eq(AlloyExpression l, AlloyExpression r) {
			return JPredicateFactory
					.pred_java_primitive_integer_value_eq(l, r);
		}

		@Override
		public Object mul(AlloyExpression l, AlloyExpression r) {
			return JExpressionFactory
					.fun_java_primitive_integer_value_mul(l, r);
		}

		@Override
		public Object rem(AlloyExpression l, AlloyExpression r) {
			return JExpressionFactory
					.fun_java_primitive_integer_value_rem(l, r);
		}

		@Override
		public Object shl(AlloyExpression l, AlloyExpression r) {
			throw new TacoException(
					"shift left operation for integers not yet implemented");
		}

		@Override
		public Object unsigned_shr(AlloyExpression l, AlloyExpression r) {
			throw new TacoException(
					"unsigned shift right operation for integers not yet implemented");
		}

		@Override
		public Object signed_shr(AlloyExpression l, AlloyExpression r) {
			return JExpressionFactory.fun_java_primitive_integer_value_sshr(l,
					r);
		}

		@Override
		public Object sub(AlloyExpression l, AlloyExpression r) {
			return JExpressionFactory
					.fun_java_primitive_integer_value_sub(l, r);
		}
		

	}


	private static class JavaLongOperatorSolver extends GenericOperatorSolver {

		private static JavaLongOperatorSolver instance;

		public static JavaLongOperatorSolver getInstance() {
			if (instance == null)
				instance = new JavaLongOperatorSolver();

			return instance;
		}

		@Override
		public Object add(AlloyExpression l, AlloyExpression r) {
			return JExpressionFactory.fun_java_primitive_long_value_add(l, r);
		}

		@Override
		public Object div(AlloyExpression l, AlloyExpression r) {
			throw new TacoException(
					"division between longs can not be applied using functions");
		}

		@Override
		public Object gt(AlloyExpression l, AlloyExpression r) {
			return JPredicateFactory.pred_java_primitive_long_value_gt(l, r);
		}

		@Override
		public Object gte(AlloyExpression l, AlloyExpression r) {
			return JPredicateFactory.pred_java_primitive_long_value_gte(l, r);
		}

		@Override
		public Object lt(AlloyExpression l, AlloyExpression r) {
			return JPredicateFactory.pred_java_primitive_long_value_lt(l, r);
		}

		@Override
		public Object lte(AlloyExpression l, AlloyExpression r) {
			return JPredicateFactory.pred_java_primitive_long_value_lte(l, r);
		}

		@Override
		public Object mul(AlloyExpression l, AlloyExpression r) {
			throw new TacoException(
					"multiplication between longs can not be applied using functions");
		}

		@Override
		public Object rem(AlloyExpression l, AlloyExpression r) {
			throw new TacoException(
					"remainder between longs can not be applied using functions");
		}

		@Override
		public Object shl(AlloyExpression l, AlloyExpression r) {
			throw new TacoException(
					"shift left operation for longs not yet implemented");
		}

		@Override
		public Object unsigned_shr(AlloyExpression l, AlloyExpression r) {
			throw new TacoException(
					"unsigned shift right operation for longs not yet implemented");
		}

		@Override
		public Object signed_shr(AlloyExpression l, AlloyExpression r) {
			return JExpressionFactory.fun_java_primitive_integer_value_sshr(l,
					r);
		}

		@Override
		public Object sub(AlloyExpression l, AlloyExpression r) {
			return JExpressionFactory.fun_java_primitive_long_value_sub(l, r);
		}

		@Override 
		public Object eq(AlloyExpression l, AlloyExpression r) {
			return JPredicateFactory
					.pred_java_primitive_long_value_eq(l, r);
		}

	}


	private static class AlloyIntOperatorSolver extends GenericOperatorSolver {

		private static AlloyIntOperatorSolver instance;

		public static AlloyIntOperatorSolver getInstance() {
			if (instance == null)
				instance = new AlloyIntOperatorSolver();

			return instance;
		}

		@Override
		public Object add(AlloyExpression l, AlloyExpression r) {
			return JExpressionFactory.alloy_int_add(l, r);
		}

		@Override
		public Object div(AlloyExpression l, AlloyExpression r) {
			return JExpressionFactory.alloy_int_div(l, r);
		}

		@Override
		public Object gt(AlloyExpression l, AlloyExpression r) {
			return JPredicateFactory.alloy_int_gt(l, r);
		}

		@Override
		public Object gte(AlloyExpression l, AlloyExpression r) {
			return JPredicateFactory.alloy_int_gte(l, r);
		}

		@Override
		public Object lt(AlloyExpression l, AlloyExpression r) {
			return JPredicateFactory.alloy_int_lt(l, r);
		}

		@Override
		public Object lte(AlloyExpression l, AlloyExpression r) {
			return JPredicateFactory.alloy_int_lte(l, r);
		}

		@Override
		public Object mul(AlloyExpression l, AlloyExpression r) {
			return JExpressionFactory.alloy_int_mul(l, r);
		}

		@Override
		public Object rem(AlloyExpression l, AlloyExpression r) {
			return JExpressionFactory.alloy_int_rem(l, r);
		}

		@Override
		public Object shl(AlloyExpression l, AlloyExpression r) {
			return JExpressionFactory.alloy_int_shl(l, r);
		}

		@Override
		public Object unsigned_shr(AlloyExpression l, AlloyExpression r) {
			return JExpressionFactory.alloy_iny_ushr(l, r);
		}

		@Override
		public Object signed_shr(AlloyExpression l, AlloyExpression r) {
			return JExpressionFactory.alloy_int_sshr(l, r);
		}

		@Override
		public Object sub(AlloyExpression l, AlloyExpression r) {
			return JExpressionFactory.alloy_int_sub(l, r);
		}

		@Override
		public Object eq(AlloyExpression l, AlloyExpression r) {
			return JExpressionFactory.alloy_int_eq(l, r);
		}

	}


	

	private static class JavaCharCharOperatorSolver extends GenericOperatorSolver {

		private static JavaCharCharOperatorSolver instance;

		public static JavaCharCharOperatorSolver getInstance() {
			if (instance == null)
				instance = new JavaCharCharOperatorSolver();

			return instance;
		}

		@Override
		public Object add(AlloyExpression l, AlloyExpression r) {
			return JExpressionFactory
					.fun_java_primitive_charCharToInt_value_add(l, r);
		}

		@Override
		public Object sub(AlloyExpression l, AlloyExpression r) {
			return JExpressionFactory
					.fun_java_primitive_charCharToInt_value_sub(l, r);
		}

		@Override
		public Object gt(AlloyExpression l, AlloyExpression r) {
			return JPredicateFactory.pred_java_primitive_char_value_java_primitive_char_value_gt(l, r);
		}

		@Override
		public Object gte(AlloyExpression l, AlloyExpression r) {
			return JPredicateFactory
					.pred_java_primitive_char_value_java_primitive_char_value_gte(l, r);
		}

		@Override
		public Object lt(AlloyExpression l, AlloyExpression r) {
			return JPredicateFactory.pred_java_primitive_char_value_java_primitive_char_value_lt(l, r);
		}

		@Override
		public Object lte(AlloyExpression l, AlloyExpression r) {
			return JPredicateFactory
					.pred_java_primitive_char_value_java_primitive_char_value_lte(l, r);
		}

		@Override
		public Object eq(AlloyExpression l, AlloyExpression r) {
			return JPredicateFactory
					.pred_java_primitive_char_value_java_primitive_char_value_eq(l, r);
		}

		
		@Override
		public Object mul(AlloyExpression l, AlloyExpression r) {
			return null;
		}

		@Override
		public Object div(AlloyExpression l, AlloyExpression r) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Object rem(AlloyExpression l, AlloyExpression r) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Object unsigned_shr(AlloyExpression l, AlloyExpression r) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Object signed_shr(AlloyExpression l, AlloyExpression r) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Object shl(AlloyExpression l, AlloyExpression r) {
			// TODO Auto-generated method stub
			return null;
		}




	}

	
	private static class JavaCharIntOperatorSolver extends GenericOperatorSolver {

		private static JavaCharIntOperatorSolver instance;

		public static JavaCharIntOperatorSolver getInstance() {
			if (instance == null)
				instance = new JavaCharIntOperatorSolver();

			return instance;
		}

		@Override
		public Object add(AlloyExpression l, AlloyExpression r) {
			return JExpressionFactory
					.fun_java_primitive_charIntToInt_value_add(l, r);
		}

		@Override
		public Object sub(AlloyExpression l, AlloyExpression r) {
			return JExpressionFactory
					.fun_java_primitive_charIntToInt_value_sub(l, r);
		}

		@Override
		public Object gt(AlloyExpression l, AlloyExpression r) {
			return JPredicateFactory.pred_java_primitive_char_value_java_primitive_integer_value_gt(l, r);
		}

		@Override
		public Object gte(AlloyExpression l, AlloyExpression r) {
			return JPredicateFactory
					.pred_java_primitive_char_value_java_primitive_integer_value_gte(l, r);
		}

		@Override
		public Object lt(AlloyExpression l, AlloyExpression r) {
			return JPredicateFactory.pred_java_primitive_char_value_java_primitive_integer_value_lt(l, r);
		}

		@Override
		public Object lte(AlloyExpression l, AlloyExpression r) {
			return JPredicateFactory
					.pred_java_primitive_char_value_java_primitive_integer_value_lte(l, r);
		}

		@Override
		public Object eq(AlloyExpression l, AlloyExpression r) {
			return JPredicateFactory
					.pred_java_primitive_char_value_java_primitive_integer_value_eq(l, r);
		}


		@Override
		public Object mul(AlloyExpression l, AlloyExpression r) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Object div(AlloyExpression l, AlloyExpression r) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Object rem(AlloyExpression l, AlloyExpression r) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Object unsigned_shr(AlloyExpression l, AlloyExpression r) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Object signed_shr(AlloyExpression l, AlloyExpression r) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Object shl(AlloyExpression l, AlloyExpression r) {
			// TODO Auto-generated method stub
			return null;
		}



	}


	
	private static class JavaIntCharOperatorSolver extends GenericOperatorSolver {

		private static JavaIntCharOperatorSolver instance;

		public static JavaIntCharOperatorSolver getInstance() {
			if (instance == null)
				instance = new JavaIntCharOperatorSolver();

			return instance;
		}

		@Override
		public Object add(AlloyExpression l, AlloyExpression r) {
			return JExpressionFactory
					.fun_java_primitive_intCharToInt_value_add(l, r);
		}

		@Override
		public Object sub(AlloyExpression l, AlloyExpression r) {
			return JExpressionFactory
					.fun_java_primitive_intCharToInt_value_sub(l, r);
		}

		@Override
		public Object gt(AlloyExpression l, AlloyExpression r) {
			return JPredicateFactory.pred_java_primitive_integer_value_java_primitive_char_value_gt(l, r);
		}

		@Override
		public Object gte(AlloyExpression l, AlloyExpression r) {
			return JPredicateFactory
					.pred_java_primitive_integer_value_java_primitive_char_value_gte(l, r);
		}

		@Override
		public Object lt(AlloyExpression l, AlloyExpression r) {
			return JPredicateFactory.pred_java_primitive_integer_value_java_primitive_char_value_lt(l, r);
		}

		@Override
		public Object lte(AlloyExpression l, AlloyExpression r) {
			return JPredicateFactory
					.pred_java_primitive_integer_value_java_primitive_char_value_lte(l, r);
		}
		
		@Override
		public Object eq(AlloyExpression l, AlloyExpression r) {
			return JPredicateFactory
					.pred_java_primitive_integer_value_java_primitive_char_value_eq(l, r);
		}

		@Override
		public Object mul(AlloyExpression l, AlloyExpression r) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Object div(AlloyExpression l, AlloyExpression r) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Object rem(AlloyExpression l, AlloyExpression r) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Object unsigned_shr(AlloyExpression l, AlloyExpression r) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Object signed_shr(AlloyExpression l, AlloyExpression r) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Object shl(AlloyExpression l, AlloyExpression r) {
			// TODO Auto-generated method stub
			return null;
		}
		


	}
	
	
	private static class JavaIntLongOperatorSolver extends GenericOperatorSolver {

		private static JavaIntLongOperatorSolver instance;

		public static JavaIntLongOperatorSolver getInstance() {
			if (instance == null)
				instance = new JavaIntLongOperatorSolver();

			return instance;
		}

		@Override
		public Object add(AlloyExpression l, AlloyExpression r) {
			return JExpressionFactory
					.fun_java_primitive_intLongToLong_value_add(l, r);
		}

		@Override
		public Object sub(AlloyExpression l, AlloyExpression r) {
			return JExpressionFactory
					.fun_java_primitive_intLongToLong_value_sub(l, r);
		}

		@Override
		public Object gt(AlloyExpression l, AlloyExpression r) {
			return JPredicateFactory.pred_java_primitive_long_value_java_primitive_integer_long_value_gt(l, r);
		}

		@Override
		public Object gte(AlloyExpression l, AlloyExpression r) {
			return JPredicateFactory
					.pred_java_primitive_long_value_java_primitive_integer_long_value_gte(l, r);
		}

		@Override
		public Object lt(AlloyExpression l, AlloyExpression r) {
			return JPredicateFactory.pred_java_primitive_long_value_java_primitive_integer_long_value_lt(l, r);
		}

		@Override
		public Object lte(AlloyExpression l, AlloyExpression r) {
			return JPredicateFactory
					.pred_java_primitive_long_value_java_primitive_integer_long_value_lte(l, r);
		}
		
		@Override
		public Object eq(AlloyExpression l, AlloyExpression r) {
			return JPredicateFactory
					.pred_java_primitive_long_value_java_primitive_integer_long_value_eq(l, r);
		}

		@Override
		public Object mul(AlloyExpression l, AlloyExpression r) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Object div(AlloyExpression l, AlloyExpression r) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Object rem(AlloyExpression l, AlloyExpression r) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Object unsigned_shr(AlloyExpression l, AlloyExpression r) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Object signed_shr(AlloyExpression l, AlloyExpression r) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Object shl(AlloyExpression l, AlloyExpression r) {
			// TODO Auto-generated method stub
			return null;
		}
		


	}
	
	private static class JavaLongIntOperatorSolver extends GenericOperatorSolver {

		private static JavaLongIntOperatorSolver instance;

		public static JavaLongIntOperatorSolver getInstance() {
			if (instance == null)
				instance = new JavaLongIntOperatorSolver();

			return instance;
		}

		@Override
		public Object add(AlloyExpression l, AlloyExpression r) {
			return JExpressionFactory
					.fun_java_primitive_longIntToLong_value_add(l, r);
		}

		@Override
		public Object sub(AlloyExpression l, AlloyExpression r) {
			return JExpressionFactory
					.fun_java_primitive_longIntToLong_value_sub(l, r);
		}

		@Override
		public Object gt(AlloyExpression l, AlloyExpression r) {
			return JPredicateFactory.pred_java_primitive_long_value_java_primitive_int_value_gt(l, r);
		}

		@Override
		public Object gte(AlloyExpression l, AlloyExpression r) {
			return JPredicateFactory
					.pred_java_primitive_long_value_java_primitive_int_value_gte(l, r);
		}

		@Override
		public Object lt(AlloyExpression l, AlloyExpression r) {
			return JPredicateFactory.pred_java_primitive_long_value_java_primitive_int_value_lt(l, r);
		}

		@Override
		public Object lte(AlloyExpression l, AlloyExpression r) {
			return JPredicateFactory
					.pred_java_primitive_long_value_java_primitive_int_value_lte(l, r);
		}
		
		@Override
		public Object eq(AlloyExpression l, AlloyExpression r) {
			return JPredicateFactory
					.pred_java_primitive_long_value_java_primitive_int_value_eq(l, r);
		}

		@Override
		public Object mul(AlloyExpression l, AlloyExpression r) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Object div(AlloyExpression l, AlloyExpression r) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Object rem(AlloyExpression l, AlloyExpression r) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Object unsigned_shr(AlloyExpression l, AlloyExpression r) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Object signed_shr(AlloyExpression l, AlloyExpression r) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Object shl(AlloyExpression l, AlloyExpression r) {
			// TODO Auto-generated method stub
			return null;
		}
		


	}
	
	
	private static class JavaLongCharOperatorSolver extends GenericOperatorSolver {

		private static JavaLongCharOperatorSolver instance;

		public static JavaLongCharOperatorSolver getInstance() {
			if (instance == null)
				instance = new JavaLongCharOperatorSolver();

			return instance;
		}

		@Override
		public Object add(AlloyExpression l, AlloyExpression r) {
			return JExpressionFactory
					.fun_java_primitive_longCharToLong_value_add(l, r);
		}

		@Override
		public Object sub(AlloyExpression l, AlloyExpression r) {
			return JExpressionFactory
					.fun_java_primitive_longCharToLong_value_sub(l, r);
		}

		@Override
		public Object gt(AlloyExpression l, AlloyExpression r) {
			return JPredicateFactory.pred_java_primitive_long_value_java_primitive_char_value_gt(l, r);
		}

		@Override
		public Object gte(AlloyExpression l, AlloyExpression r) {
			return JPredicateFactory
					.pred_java_primitive_long_value_java_primitive_char_value_gte(l, r);
		}

		@Override
		public Object lt(AlloyExpression l, AlloyExpression r) {
			return JPredicateFactory.pred_java_primitive_long_value_java_primitive_char_value_lt(l, r);
		}

		@Override
		public Object lte(AlloyExpression l, AlloyExpression r) {
			return JPredicateFactory
					.pred_java_primitive_long_value_java_primitive_char_value_lte(l, r);
		}
		
		@Override
		public Object eq(AlloyExpression l, AlloyExpression r) {
			return JPredicateFactory
					.pred_java_primitive_long_value_java_primitive_char_value_eq(l, r);
		}

		@Override
		public Object mul(AlloyExpression l, AlloyExpression r) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Object div(AlloyExpression l, AlloyExpression r) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Object rem(AlloyExpression l, AlloyExpression r) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Object unsigned_shr(AlloyExpression l, AlloyExpression r) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Object signed_shr(AlloyExpression l, AlloyExpression r) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Object shl(AlloyExpression l, AlloyExpression r) {
			// TODO Auto-generated method stub
			return null;
		}
		


	}
	
	
	private static class JavaBooleanTypeOperatorSolver extends GenericOperatorSolver {

		private static JavaBooleanTypeOperatorSolver instance;

		public static JavaBooleanTypeOperatorSolver getInstance() {
			if (instance == null)
				instance = new JavaBooleanTypeOperatorSolver();

			return instance;
		}

		@Override
		public Object add(AlloyExpression l, AlloyExpression r) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Object sub(AlloyExpression l, AlloyExpression r) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Object mul(AlloyExpression l, AlloyExpression r) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Object div(AlloyExpression l, AlloyExpression r) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Object rem(AlloyExpression l, AlloyExpression r) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Object unsigned_shr(AlloyExpression l, AlloyExpression r) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Object signed_shr(AlloyExpression l, AlloyExpression r) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Object shl(AlloyExpression l, AlloyExpression r) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Object lt(AlloyExpression l, AlloyExpression r) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Object gt(AlloyExpression l, AlloyExpression r) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Object lte(AlloyExpression l, AlloyExpression r) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Object gte(AlloyExpression l, AlloyExpression r) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Object eq(AlloyExpression l, AlloyExpression r) {
			return JExpressionFactory.fun_boolean_eq(l,r);
		}
		
	}
	
	
	private static class JavaFloatOperatorSolver extends GenericOperatorSolver {

		private static JavaFloatOperatorSolver instance;

		public static JavaFloatOperatorSolver getInstance() {
			if (instance == null)
				instance = new JavaFloatOperatorSolver();

			return instance;
		}

		@Override
		public Object add(AlloyExpression l, AlloyExpression r) {
			throw new TacoException(
					"addition between floats can not be applied using functions");
		}

		@Override
		public Object div(AlloyExpression l, AlloyExpression r) {
			throw new TacoException(
					"division between floats can not be applied using functions");
		}

		@Override
		public Object gt(AlloyExpression l, AlloyExpression r) {
			return JPredicateFactory.pred_java_primitive_float_value_gt(l, r);
		}

		@Override
		public Object gte(AlloyExpression l, AlloyExpression r) {
			return JPredicateFactory.pred_java_primitive_float_value_gte(l, r);
		}

		@Override
		public Object lt(AlloyExpression l, AlloyExpression r) {
			return JPredicateFactory.pred_java_primitive_float_value_lt(l, r);
		}

		@Override
		public Object lte(AlloyExpression l, AlloyExpression r) {
			return JPredicateFactory.pred_java_primitive_float_value_lte(l, r);
		}
		
		@Override
		public Object eq(AlloyExpression l, AlloyExpression r) {
			return JPredicateFactory.pred_java_primitive_float_value_eq(l, r);
		}

		@Override
		public Object mul(AlloyExpression l, AlloyExpression r) {
			throw new TacoException(
					"multiplication between floats can not be applied using functions");
		}

		@Override
		public Object rem(AlloyExpression l, AlloyExpression r) {
			throw new TacoException(
					"remainder between floats not supported.");
		}

		@Override
		public Object shl(AlloyExpression l, AlloyExpression r) {
			throw new TacoException(
					"shift left operation for floats not supported");
		}

		@Override
		public Object unsigned_shr(AlloyExpression l, AlloyExpression r) {
			throw new TacoException(
					"unsigned shift right operation for floats not supported");
		}

		@Override
		public Object signed_shr(AlloyExpression l, AlloyExpression r) {
			throw new TacoException(
					"signed shift right operation for floats not supported");
		}

		@Override
		public Object sub(AlloyExpression l, AlloyExpression r) {
			throw new TacoException(
					"substraction between floats can not be applied using functions");
		}


	}

	/**
	 * Returns the String representation of the given operator id.
	 * 
	 * @param operator
	 * @return
	 */
	public static String getOperatorString(int operator) {
		switch (operator) {
		case Constants.OPE_SIMPLE:
			throw new TacoNotImplementedYetException(
					"Take a look to this operator and write down the corresponding string");
		case Constants.OPE_PLUS:
			return "+";
		case Constants.OPE_MINUS:
			return "-";
		case Constants.OPE_STAR:
			return "*";
		case Constants.OPE_SLASH:
			return "/";
		case Constants.OPE_PERCENT:
			return "%";
		case Constants.OPE_SR:
			throw new TacoNotImplementedYetException(
					"Take a look to this operator and write down the corresponding string");
		case Constants.OPE_BSR:
			throw new TacoNotImplementedYetException(
					"Take a look to this operator and write down the corresponding string");
		case Constants.OPE_SL:
			throw new TacoNotImplementedYetException(
					"Take a look to this operator and write down the corresponding string");
		case Constants.OPE_BAND:
			return "&";
		case Constants.OPE_BXOR:
			return "^";
		case Constants.OPE_BOR:
			return "|";
		case Constants.OPE_BNOT:
			throw new TacoNotImplementedYetException(
					"Take a look to this operator and write down the corresponding string");
		case Constants.OPE_LNOT:
			return "!";
		case Constants.OPE_LT:
			return "<";
		case Constants.OPE_LE:
			return "<=";
		case Constants.OPE_GT:
			return ">";
		case Constants.OPE_GE:
			return ">=";
		case Constants.OPE_EQ:
			return "==";
		case Constants.OPE_NE:
			return "!=";
		case Constants.OPE_LAND:
			return "&&";
		case Constants.OPE_LOR:
			return "||";
		case Constants.OPE_PREINC:
			return "++";
		case Constants.OPE_PREDEC:
			return "--";
		case Constants.OPE_POSTINC:
			return "++";
		case Constants.OPE_POSTDEC:
			return "--";
		case Constants.OPE_IMPLIES:
			return "==>";
		default:
			throw new TacoNotImplementedYetException(
					"Take a look to this operator and write down the corresponding Predicate or Formula");
		}
	}

	public static Object getAlloyBinaryExpression(JType[] expression_types,
			AlloyExpression[] alloyExpression, int operator) {
		switch (operator) {

		case Constants.OPE_PLUS: // represents "+"
		case Constants.OPE_MINUS: // represents "-"
		case Constants.OPE_STAR: // represents "*"
		case Constants.OPE_SLASH: // represents "/"
		case Constants.OPE_PERCENT: // represents "%"
		case Constants.OPE_SR: // represents ">>"
		case Constants.OPE_BSR: // represents ">>>"
		case Constants.OPE_SL: // represents "<<"
		case Constants.OPE_LT: // represents "<"
		case Constants.OPE_LE: // represents "<="
		case Constants.OPE_GT: // represents ">"
		case Constants.OPE_GE: // represents ">="
			return dispatchSolver(expression_types, alloyExpression, operator);

		case Constants.OPE_BOR: // represents "|"
			return JExpressionFactory.boolean_or(alloyExpression);

		case Constants.OPE_BAND: // represents "&"
			return JExpressionFactory.boolean_and(alloyExpression);

		case Constants.OPE_EQ: // represents "=="
		{
			if (TacoConfigurator.getInstance().getUseJavaArithmetic() == true && isPrimitiveJType(expression_types[0]) && isPrimitiveJType(expression_types[1])) {

				JType left_type = expression_types[0];
				JType right_type = expression_types[1];

				if ((left_type.equals(JSignatureFactory.JAVA_PRIMITIVE_FLOAT_VALUE))
						&& (right_type
								.equals(JSignatureFactory.JAVA_PRIMITIVE_FLOAT_VALUE))) {
					return JPredicateFactory.pred_java_primitive_float_value_eq(alloyExpression);

				}
				
//				if (left_type.equals(JSignatureFactory.JAVA_PRIMITIVE_CHAR_VALUE) && 
//						right_type.equals(JSignatureFactory.JAVA_PRIMITIVE_INTEGER_VALUE) &&
//						alloyExpression[1].toString().startsWith("JavaPrimitiveIntegerLiteral")) {
//					
//					int int_value = Integer.parseInt(alloyExpression[1].toString().substring(27));
//					alloyExpression[1] = JavaPrimitiveCharValue.getInstance().toJavaPrimitiveCharLiteral(int_value);
//				}
//				
//				if (right_type.equals(JSignatureFactory.JAVA_PRIMITIVE_CHAR_VALUE) && 
//						left_type.equals(JSignatureFactory.JAVA_PRIMITIVE_INTEGER_VALUE) &&
//						alloyExpression[0].toString().startsWith("JavaPrimitiveIntegerLiteral")) {
//					
//					int int_value = Integer.parseInt(alloyExpression[0].toString().substring(27));
//					alloyExpression[0] = JavaPrimitiveCharValue.getInstance().toJavaPrimitiveCharLiteral(int_value);
//				}

				return dispatchSolver(expression_types, alloyExpression, operator);
			} else {
				return JPredicateFactory.eq(alloyExpression);
			}
		}

		case Constants.OPE_NE: // represents "!="
		{
			if (TacoConfigurator.getInstance().getUseJavaArithmetic() == true) {

				JType left_type = expression_types[0];
				JType right_type = expression_types[0];

				if ((left_type.equals(JSignatureFactory.JAVA_PRIMITIVE_FLOAT_VALUE))
						&& (right_type
								.equals(JSignatureFactory.JAVA_PRIMITIVE_FLOAT_VALUE))) {
					return JPredicateFactory.pred_java_primitive_float_value_neq(alloyExpression);

				}
				
				if (left_type.equals(JSignatureFactory.JAVA_PRIMITIVE_CHAR_VALUE) && 
						right_type.equals(JSignatureFactory.JAVA_PRIMITIVE_INTEGER_VALUE) &&
						alloyExpression[1].toString().startsWith("JavaPrimitiveIntegerLiteral")) {
					
					int int_value = Integer.parseInt(alloyExpression[1].toString().substring(27));
					alloyExpression[1] = JavaPrimitiveCharValue.getInstance().toJavaPrimitiveCharLiteral(int_value, false);
				}
				
				if (right_type.equals(JSignatureFactory.JAVA_PRIMITIVE_CHAR_VALUE) && 
						left_type.equals(JSignatureFactory.JAVA_PRIMITIVE_INTEGER_VALUE) &&
						alloyExpression[0].toString().startsWith("JavaPrimitiveIntegerLiteral")) {
					
					int int_value = Integer.parseInt(alloyExpression[0].toString().substring(27));
					alloyExpression[0] = JavaPrimitiveCharValue.getInstance().toJavaPrimitiveCharLiteral(int_value, false);
				}

			}
			return JPredicateFactory.neq(alloyExpression);
		}

		case Constants.OPE_IMPLIES: {
			AlloyFormula leftSide = JPredicateFactory
					.liftExpression(alloyExpression[0]);
			AlloyFormula rightSide = JPredicateFactory
					.liftExpression(alloyExpression[1]);
			return new ImpliesFormula(leftSide, rightSide);
		}

		case Constants.OPE_BACKWARD_IMPLIES: {
			AlloyFormula leftSide1 = JPredicateFactory
					.liftExpression(alloyExpression[1]);
			AlloyFormula rightSide1 = JPredicateFactory
					.liftExpression(alloyExpression[0]);
			return new ImpliesFormula(leftSide1, rightSide1);
		}

		case Constants.OPE_SIMPLE:
		case Constants.OPE_BXOR: // represents "^"
		case Constants.OPE_BNOT: // boolean not
		case Constants.OPE_LNOT: // represents "!"
		case Constants.OPE_LAND: // represents "&&"
		case Constants.OPE_LOR: // represents "||"

		default:
			throw new TacoNotImplementedYetException(
					"Take a look to this operator and write down the corresponding Predicate or Formula");
		}
	}




	private static boolean isPrimitiveJType(JType jType) {
		return jType.equals(JType.parse("JavaPrimitiveIntegerValue")) || jType.toString().startsWith("JavaPrimitiveIntegerLiteral") ||
				jType.equals(JType.parse("JavaPrimitiveLongValue")) || jType.toString().startsWith("JavaPrimitiveLongLiteral") ||
						jType.equals(JType.parse("JavaPrimitiveCharValue")) || jType.toString().startsWith("JavaPrimitiveCharLiteral") ||
								jType.equals(JType.parse("JavaPrimitiveFloatValue")) || jType.toString().startsWith("JavaPrimitiveFloatLiteral") ||
										jType.equals(JType.parse("boolean"));
		
	}

	public static Object getAlloyUnaryExpression(
			AlloyExpression alloyExpression, int operator) {
		switch (operator) {
		case Constants.OPE_MINUS:
			// represents "-"
			if (TacoConfigurator.getInstance().getUseJavaArithmetic() == true) {
				return JExpressionFactory
						.fun_java_primitive_integer_value_negate(alloyExpression);
			}
			else
				return JExpressionFactory.alloy_int_negate(alloyExpression);
		case Constants.OPE_LNOT:
			return JExpressionFactory.not(alloyExpression);

		default:
			throw new TacoNotImplementedYetException(
					"Take a look to this operator and write down the corresponding Predicate or Formula");
		}
	}

	public static AlloyFormula getAlloyBinaryFormula(AlloyFormula leftSide,
			AlloyFormula rightSide, int operator) {
		switch (operator) {
		case Constants.OPE_IMPLIES:
			// Represents '==>'
			return new ImpliesFormula(leftSide, rightSide);
		case Constants.OPE_BACKWARD_IMPLIES:
			// Represents '<=='
			return new ImpliesFormula(rightSide, leftSide);
		case Constants.OPE_EQUIV:
			// Represents '<==>'
			AlloyFormula leftToRightImplication = new ImpliesFormula(leftSide,
					rightSide);
			AlloyFormula rightToLeftImplication = new ImpliesFormula(rightSide,
					leftSide);

			return new AndFormula(leftToRightImplication,
					rightToLeftImplication);
		default:
			throw new TacoNotImplementedYetException(
					"Take a look to this operator and write down the corresponding Predicate or Formula");
		}
	}

	public static AlloyFormula getAlloyUnaryFormula(AlloyFormula af, int operator){
		switch (operator){
		case Constants.OPE_LNOT:
			return new NotFormula(af);	
		default:
			throw new TacoNotImplementedYetException(
					"Take a look to this operator and write down the corresponding Predicate or Formula");
		}
	}

	public static AlloyFormula getAlloyBinaryFormula(
			AlloyExpression leftExpression, AlloyExpression righExpression,
			int operator) {
		switch (operator) {
		case Constants.OPE_SUBTYPE:
			// represents "<:"
			return JPredicateFactory.instanceOf(leftExpression, righExpression
					.toString());
		default:
			throw new TacoNotImplementedYetException(
					"Take a look to this operator and write down the corresponding Predicate or Formula");
		}
	}

	//	public static AlloyFormula getAlloyUnaryFormula(AlloyFormula formula,
	//			int operator) {
	//		switch (operator) {
	//		case Constants.OPE_LNOT:
	//			// represents "!"
	//			return new NotFormula(formula);
	//			
	//		default:
	//			throw new TacoNotImplementedYetException(
	//					"Take a look to this operator and write down the corresponding Predicate or Formula");
	//		}
	//	}

	private static Object dispatchSolver(JType[] expression_types,
			AlloyExpression[] expressions, int operator) {

		AlloyExpression left_expr = expressions[0];
		AlloyExpression right_expr = expressions[1];

		GenericOperatorSolver genericOperatorSolver;

		if (TacoConfigurator.getInstance().getUseJavaArithmetic() == true) {

			JType left_type = expression_types[0];
			JType right_type = expression_types[1];

			if ((left_type.equals(JSignatureFactory.JAVA_PRIMITIVE_LONG_VALUE))
					&& (right_type.equals(JSignatureFactory.JAVA_PRIMITIVE_LONG_VALUE))) {

				// select long expressions overloading resolver

				genericOperatorSolver = JavaLongOperatorSolver.getInstance();

			} else if ((left_type.equals(JSignatureFactory.JAVA_PRIMITIVE_INTEGER_VALUE))
					&& (right_type.equals(JSignatureFactory.JAVA_PRIMITIVE_INTEGER_VALUE))) {

				// select integer expressions overloading resolver

				genericOperatorSolver = JavaIntegerOperatorSolver.getInstance();

			} else if ((left_type.equals(JSignatureFactory.JAVA_PRIMITIVE_FLOAT_VALUE))
					&& (right_type.equals(JSignatureFactory.JAVA_PRIMITIVE_FLOAT_VALUE))) {

				// select float expressions overloading resolver
				genericOperatorSolver = JavaFloatOperatorSolver.getInstance();

			} else if (left_type.equals(JSignatureFactory.JAVA_PRIMITIVE_CHAR_VALUE) && 
					right_type.equals(JSignatureFactory.JAVA_PRIMITIVE_CHAR_VALUE)) {

				genericOperatorSolver = JavaCharCharOperatorSolver.getInstance();

			} else if (left_type.equals(JSignatureFactory.JAVA_PRIMITIVE_CHAR_VALUE) && 
					right_type.equals(JSignatureFactory.JAVA_PRIMITIVE_INTEGER_VALUE)) {

				genericOperatorSolver = JavaCharIntOperatorSolver.getInstance();

			} else if (left_type.equals(JSignatureFactory.JAVA_PRIMITIVE_LONG_VALUE) && 
					right_type.equals(JSignatureFactory.JAVA_PRIMITIVE_CHAR_VALUE)) {

				genericOperatorSolver = JavaLongCharOperatorSolver.getInstance();

			} else if (left_type.equals(JSignatureFactory.JAVA_PRIMITIVE_INTEGER_VALUE) && 
					right_type.equals(JSignatureFactory.JAVA_PRIMITIVE_CHAR_VALUE)) {

				genericOperatorSolver = JavaIntCharOperatorSolver.getInstance();
				
			} else if (left_type.equals(JSignatureFactory.JAVA_PRIMITIVE_LONG_VALUE) && 
					right_type.equals(JSignatureFactory.JAVA_PRIMITIVE_INTEGER_VALUE)) {

				genericOperatorSolver = JavaLongIntOperatorSolver.getInstance();
				
			} else if (left_type.equals(JSignatureFactory.JAVA_PRIMITIVE_INTEGER_VALUE) && 
					right_type.equals(JSignatureFactory.JAVA_PRIMITIVE_LONG_VALUE)) {
				
				genericOperatorSolver = JavaIntLongOperatorSolver.getInstance();

			} else if (left_type.equals(JSignatureFactory.JAVA_PRIMITIVE_SHORT_VALUE) && 
					right_type.equals(JSignatureFactory.JAVA_PRIMITIVE_SHORT_VALUE)) {
				
//				genericOperatorSolver = JavaShortOperatorSolver.getInstance();
				throw new RuntimeException("Short is not yet supported");
				
			} else if (left_type.equals(JSignatureFactory.BOOLEAN_TYPE) && 
					right_type.equals(JSignatureFactory.BOOLEAN_TYPE)) {
				genericOperatorSolver = JavaBooleanTypeOperatorSolver.getInstance();
			} else {
				throw new TacoException("Cannot apply operator "
						+ getOperatorString(operator) + " on types :"
						+ left_type + " and " + right_type);
			}

		} else {

			// select Alloy int expression overloading resolver

			genericOperatorSolver = AlloyIntOperatorSolver.getInstance();

		}

		return genericOperatorSolver.getAlloyBinaryExpression(left_expr,
				right_expr, operator);
	}
	
	
	
	

}
