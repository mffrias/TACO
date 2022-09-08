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
package ar.edu.taco.simplejml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import ar.edu.jdynalloy.ast.JModifies;
import ar.edu.jdynalloy.ast.JSpecCase;
import ar.edu.jdynalloy.xlator.JDynAlloyTyping;
import ar.edu.jdynalloy.xlator.JType;
import ar.uba.dc.rfm.alloy.ast.expressions.AlloyExpression;
import ar.uba.dc.rfm.alloy.ast.formulas.AlloyFormula;

/**
 * @author elgaby
 *
 */
public class JmlBaseExpressionVisitor extends ExpressionVisitor {
	
	public static class JmlRepresentsData {

		public final AlloyExpression expression;
		public final JType expressionType;
		public final AlloyFormula formula;

		public JmlRepresentsData(AlloyExpression expression, JType expressionType,
				AlloyFormula formula) {
			super();
			this.expression = expression;
			this.expressionType = expressionType;
			this.formula = formula;
		}
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((expression == null) ? 0 : expression.hashCode());
			result = prime
					* result
					+ ((expressionType == null) ? 0 : expressionType.hashCode());
			result = prime * result
					+ ((formula == null) ? 0 : formula.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			JmlRepresentsData other = (JmlRepresentsData) obj;
			if (expression == null) {
				if (other.expression != null)
					return false;
			} else if (!expression.equals(other.expression))
				return false;
			if (expressionType == null) {
				if (other.expressionType != null)
					return false;
			} else if (!expressionType.equals(other.expressionType))
				return false;
			if (formula == null) {
				if (other.formula != null)
					return false;
			} else if (!formula.equals(other.formula))
				return false;
			return true;
		}
	}

	public static class JmlClassDeclarationResult {

		final JDynAlloyTyping modelFields = new JDynAlloyTyping();
		final JDynAlloyTyping staticModelFields = new JDynAlloyTyping();
		final Vector<AlloyFormula> invariants = new Vector<AlloyFormula>();
		final Vector<AlloyFormula> staticInvariants = new Vector<AlloyFormula>();
		final Vector<AlloyFormula> constraints = new Vector<AlloyFormula>();
		final Vector<AlloyFormula> staticConstraints = new Vector<AlloyFormula>();
		final Vector<JmlRepresentsData> represents = new Vector<JmlRepresentsData>();

		public Iterator<AlloyFormula> invariants() {
			return invariants.iterator();
		}

		public Iterator<AlloyFormula> staticInvariants() {
			return staticInvariants.iterator();
		}

		public Iterator<AlloyFormula> constraints() {
			return constraints.iterator();
		}

		public Iterator<AlloyFormula> staticConstraints() {
			return staticConstraints.iterator();
		}

		public Iterator<JmlRepresentsData> represents() {
			return represents.iterator();
		}

		public Iterator<JDynAlloyTyping.Entry> modelFields() {
			return modelFields.entry_iterator();
		}

		public Iterator<JDynAlloyTyping.Entry> staticModelFields() {
			return staticModelFields.entry_iterator();
		}

	}

	public static class JmlMethodDeclarationResult {
		final Vector<JModifies> modifiables = new Vector<JModifies>();
		final Vector<JSpecCase> jSpecCases = new Vector<JSpecCase>();
		
		/**
		 * @return the jSpecCases
		 */
		public Iterator<JSpecCase> getJSpecCases() {
			return jSpecCases.iterator();
		}
		
		
//		final Vector<AlloyFormula> ensures = new Vector<AlloyFormula>();
//		final Vector<AlloyFormula> requires = new Vector<AlloyFormula>();
//
//		public Iterator<AlloyFormula> requires() {
//			return requires.iterator();
//		}
//
//		public Iterator<AlloyFormula> ensures() {
//			return ensures.iterator();
//		}
	}
	
	public enum Instant {
		PRE_INSTANT, POST_INSTANT
	}
	
	protected JmlClassDeclarationResult jmlClassDeclarationResult = new JmlClassDeclarationResult();
	protected JmlMethodDeclarationResult jmlMethodDeclarationResult = new JmlMethodDeclarationResult();
	
	// Used to indicate if the JML specification corresponds to Normal Behavior or to a Exception Behavior 
	protected boolean normalBehavior = true;
	
	// Used to indicate if the JML specification corresponds to a Class specification or a Method specification 
	protected boolean classSpecification = false;
	
	protected List<String> notAllowsPrimedState = new ArrayList<String>();
	
	protected static final Map<String, Integer> VARIABLE_NAME_COUNTER = new HashMap<String, Integer>();
	
	/**
	 * @return the jmlClassDeclarationResult
	 */
	public JmlClassDeclarationResult getJmlClassDeclarationResult() {
		return jmlClassDeclarationResult;
	}

	/**
	 * @return the jmlMethodDeclarationResult
	 */
	public JmlMethodDeclarationResult getJmlMethodDeclarationResult() {
		return jmlMethodDeclarationResult;
	}

	/**
	 * @return the normalBehavior
	 */
	public boolean isNormalBehavior() {
		return normalBehavior;
	}

	/**
	 * @param normalBehavior the normalBehavior to set
	 */
	public void setNormalBehavior(boolean normalBehavior) {
		this.normalBehavior = normalBehavior;
	}

	/**
	 * @return the classSpecification
	 */
	public boolean isClassSpecification() {
		return classSpecification;
	}

	/**
	 * @param classSpecification the classSpecification to set
	 */
	public void setClassSpecification(boolean classSpecification) {
		this.classSpecification = classSpecification;
	}

}
