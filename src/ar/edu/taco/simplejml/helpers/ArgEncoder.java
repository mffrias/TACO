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

import java.util.List;
import java.util.Vector;

import ar.edu.jdynalloy.ast.JVariableDeclaration;
import ar.uba.dc.rfm.alloy.ast.expressions.AlloyExpression;

/**
 * Computes the order of a program's parameters, and of a call's arguments, in the JDynAlloy
 * that TACO produces: the receiver {@code thiz} first, then {@code throw}, then
 * {@code return} if the method returns a value, then the method's own parameters (jDynAlloy's
 * docs/LANGUAGE.md, "Programs"). Every program gets {@code thiz}, static ones too; the
 * {@code isStatic} flag only rejects a static constructor. Used by
 * {@link MethodDeclarationSolver} for declarations, by {@code ExpressionVisitor} for calls,
 * and by the built-in modules ({@code ar.edu.taco.simplejml.builtin}). An instance describes
 * one signature; there is no static state.
 */
public final class ArgEncoder {

	private static final int NO_INDEX = -1;

	private int throwIndex = NO_INDEX;

	private int thisIndex = NO_INDEX;

	private int returnIndex = NO_INDEX;

	private int argumentsBeginIndex = NO_INDEX;

	/**
	 * Reserves the positions: {@code thiz} always at 0, then {@code throw} and {@code return} if
	 * asked for, then the first of the method's own parameters if there are any.
	 *
	 * @param isStatic whether the method is static; only checked against {@code isConstructor}
	 * @param isConstructor whether the method is a constructor; it does not add a
	 *        {@code return} position by itself
	 * @param hasReturnTypeOrReturnValue whether to reserve the {@code return} position
	 * @param hasThrowArgument whether to reserve the {@code throw} position
	 * @param arguments the number of the method's own parameters; only whether it is positive
	 *        matters
	 * @throws IllegalArgumentException if the method is both static and a constructor
	 */
	public ArgEncoder(boolean isStatic, boolean isConstructor, boolean hasReturnTypeOrReturnValue, boolean hasThrowArgument, int arguments) {
		super();

		int currentIndex = 0;

		if (isStatic && isConstructor) {
			throw new IllegalArgumentException("A method cannot be static and constructor at the same time.");			
		}

		//Since parameter thiz is not removed even when the method is static, i am maintaining it.
		//this is why thisIndex is always 0. This is achieved by commenting as done immediately below.
//		if (!isStatic /* && !isConstructor */) {
			thisIndex = currentIndex++;			
//		}
		
		if (hasThrowArgument) {
			throwIndex = currentIndex++;
		}

		if (hasReturnTypeOrReturnValue /* || isConstructor */) {
			returnIndex = currentIndex++;			
		}

		if (arguments > 0) {
			argumentsBeginIndex = currentIndex++;
		}
	}
	
	/**
	 * Same as the five-argument constructor, with a {@code throw} position.
	 *
	 * @param isStatic whether the method is static; only checked against {@code isConstructor}
	 * @param isConstructor whether the method is a constructor
	 * @param hasReturnTypeOrReturnValue whether to reserve the {@code return} position
	 * @param arguments the number of the method's own parameters; only whether it is positive
	 *        matters
	 * @throws IllegalArgumentException if the method is both static and a constructor
	 */
	public ArgEncoder(boolean isStatic, boolean isConstructor, boolean hasReturnTypeOrReturnValue, int arguments) {
		this(isStatic, isConstructor, hasReturnTypeOrReturnValue, true, arguments);
	}

	private int throwIndex() {
		return throwIndex;
	}

	private int thisIndex() {
		return thisIndex;
	}

	private boolean hasThisIndex() {
		return thisIndex != NO_INDEX;
	}

	private int returnIndex() {
		return returnIndex;
	}

	private boolean hasReturnIndex() {
		return returnIndex != NO_INDEX;
	}

	private int argFirstIndex() {
		return argumentsBeginIndex;
	}

	private boolean hasArgFirstIndex() {
		return argumentsBeginIndex != NO_INDEX;
	}

	private boolean hasThrowIndex() {
		return throwIndex != NO_INDEX;
	}

	/**
	 * Returns the arguments of a call in parameter order.
	 *
	 * @param leftExpression the receiver
	 * @param throwExpression the {@code throw} variable
	 * @param returnExpression where the result goes; needed only with a {@code return} position
	 * @param arguments the call's own arguments; ignored if the encoder was built for none
	 * @return the arguments, receiver first
	 * @throws IllegalArgumentException if an expression for a reserved position, or any
	 *         argument, is null
	 */
	public Vector<AlloyExpression> encode(AlloyExpression leftExpression, AlloyExpression throwExpression, AlloyExpression returnExpression,
			List<AlloyExpression> arguments) {
		
		Vector<AlloyExpression> ps = new Vector<AlloyExpression>();
		if (hasThisIndex()) {
			if (leftExpression == null)
				throw new IllegalArgumentException("invalid left expression value");
			ps.add(thisIndex(), leftExpression);
		}
		if (hasThrowIndex()) {
			if (throwExpression == null) {
				throw new IllegalArgumentException("invalid throw expression value");
			}
			ps.add(throwIndex(), throwExpression);
		}
		if (hasReturnIndex()) {
			if (returnExpression == null)
				throw new IllegalArgumentException("invalid return expression value");
			ps.add(returnIndex(), returnExpression);
		}
		if (hasArgFirstIndex()) {
			for (int i = 0; i < arguments.size(); i++) {
				if (arguments.get(i) == null)
					throw new IllegalArgumentException("invalid argument expression value");

				ps.add(argFirstIndex() + i, arguments.get(i));
			}
		}
		return ps;
	}

	/**
	 * Returns a program's parameter declarations in order. Unlike the other {@code encode}, it
	 * does not check for nulls.
	 *
	 * @param thisDeclaration the receiver's declaration
	 * @param throwDeclaration the {@code throw} variable's declaration
	 * @param returnDeclaration the {@code return} variable's declaration; used only with a
	 *        {@code return} position
	 * @param arguments the method's own parameters; ignored if the encoder was built for none
	 * @return the declarations, receiver first
	 */
	public Vector<JVariableDeclaration> encode(JVariableDeclaration thisDeclaration, JVariableDeclaration throwDeclaration,
			JVariableDeclaration returnDeclaration, List<JVariableDeclaration> arguments) {

		Vector<JVariableDeclaration> ps = new Vector<JVariableDeclaration>();
		if (hasThisIndex())
			ps.add(thisIndex(), thisDeclaration);
		if (hasThrowIndex())
			ps.add(throwIndex(), throwDeclaration);
		if (hasReturnIndex())
			ps.add(returnIndex(), returnDeclaration);
		if (hasArgFirstIndex())
			for (int i = 0; i < arguments.size(); i++) {
				ps.add(argFirstIndex() + i, arguments.get(i));
			}
		return ps;
	}
	
	
	/**
	 * Returns the parameter declarations of a pure method, without {@code throw}: every position
	 * after the receiver moves one place up. Assumes the encoder has a {@code throw} position.
	 * Its only caller is {@code MethodDeclarationSolver.getPureMethodDeclaration}, which nothing
	 * calls.
	 *
	 * @param thisDeclaration the receiver's declaration
	 * @param returnDeclaration the {@code return} variable's declaration; used only with a
	 *        {@code return} position
	 * @param arguments the method's own parameters; ignored if the encoder was built for none
	 * @return the declarations, receiver first
	 */
	public Vector<JVariableDeclaration> encodePure(JVariableDeclaration thisDeclaration,
			JVariableDeclaration returnDeclaration, List<JVariableDeclaration> arguments) {

		//All indices after "this" index are subtracted 1 to account for the lack of throw variable
		//which is not required when translating a pure method. 
		Vector<JVariableDeclaration> ps = new Vector<JVariableDeclaration>();
		if (hasThisIndex())
			ps.add(thisIndex(), thisDeclaration);
		if (hasReturnIndex())
			ps.add(returnIndex()-1, returnDeclaration);
		if (hasArgFirstIndex())
			for (int i = 0; i < arguments.size(); i++) {
				ps.add(argFirstIndex() + i - 1, arguments.get(i));
			}
		return ps;
	}

}