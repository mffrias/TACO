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

public final class ArgEncoder {

	private static final int NO_INDEX = -1;

	private int throwIndex = NO_INDEX;

	private int thisIndex = NO_INDEX;

	private int returnIndex = NO_INDEX;

	private int argumentsBeginIndex = NO_INDEX;

	public ArgEncoder(boolean isStatic, boolean isConstructor, boolean hasReturnTypeOrReturnValue, boolean hasThrowArgument, int arguments) {
		super();

		int currentIndex = 0;

		if (isStatic && isConstructor) {
			throw new IllegalArgumentException("A method cannot be static and constructor at the same time.");			
		}

		if (!isStatic /* && !isConstructor */) {
			thisIndex = currentIndex++;			
		}
		
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