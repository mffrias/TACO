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

import java.util.Arrays;
import java.util.IdentityHashMap;
import java.util.List;

import ar.edu.jdynalloy.ast.JProgramCall;
import ar.edu.jdynalloy.ast.JSkip;
import ar.edu.jdynalloy.ast.JStatement;
import ar.edu.jdynalloy.binding.JBindingKey;
import ar.edu.taco.simplejml.NullDerefVisitor;

/**
 * Builds the null-dereference check of a program body, which {@code JDynAlloyASTVisitor} adds
 * to every method and constructor when {@code checkNullDereference} is on. Each statement that
 * dereferences an expression (the left side of a field access, or the receiver of a
 * non-static call) becomes
 * {@code if (one of them is null or empty) throw := java_lang_NullPointerExceptionLit else
 * statement} ({@code NullDerefVisitor}). The header and the tail are {@code skip}
 * placeholders. No state.
 */
public class NullDerefSolver {
	/**
	 * Returns a copy of the body with each statement guarded against null dereferences. The
	 * literal comes from {@code JNullPointerException}'s literal singleton, which exists only with
	 * {@code newExceptionsAreLiterals}; without it a statement that needs a guard throws a
	 * {@code NullPointerException}.
	 *
	 * @param program the body
	 * @param callBindings the bindings of the body's calls, by identity; updated in place so that
	 *        the copied calls keep their bindings
	 * @return the guarded body
	 */
	public static JStatement buildNullDerefBody(JStatement program,
			IdentityHashMap<JProgramCall, JBindingKey> callBindings) {
		return (JStatement) program.accept(new NullDerefVisitor(callBindings));
	}

	/**
	 * Same as {@link #buildNullDerefBody(JStatement, IdentityHashMap)}, with an empty binding
	 * map.
	 *
	 * @param program the body
	 * @return the guarded body
	 */
	public static JStatement buildNullDerefBody(JStatement program) {
		return buildNullDerefBody(program,
				new IdentityHashMap<JProgramCall, JBindingKey>());
	}

	/**
	 * Returns {@code skip}, which {@code JDynAlloyASTVisitor} puts after the guarded body.
	 *
	 * @return a new {@code skip}
	 */
	public static JStatement buildNullDerefTail() {

		return new JSkip();
	}

	/**
	 * Returns a one-element list with {@code skip}, which {@code JDynAlloyASTVisitor} puts before
	 * the guarded body.
	 *
	 * @return a fixed-size list
	 */
	public static List<JStatement> buildNullDerefHeader() {
		JStatement stmt;
			stmt = new JSkip();
				List<JStatement> nullDerefHeader = Arrays
				.asList(new JStatement[] { stmt });
		return nullDerefHeader;
	}

}
