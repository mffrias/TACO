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

public class NullDerefSolver {
	public static JStatement buildNullDerefBody(JStatement program,
			IdentityHashMap<JProgramCall, JBindingKey> callBindings) {
		return (JStatement) program.accept(new NullDerefVisitor(callBindings));
	}

	public static JStatement buildNullDerefBody(JStatement program) {
		return buildNullDerefBody(program,
				new IdentityHashMap<JProgramCall, JBindingKey>());
	}

	public static JStatement buildNullDerefTail() {

		return new JSkip();
	}

	public static List<JStatement> buildNullDerefHeader() {
		JStatement stmt;
			stmt = new JSkip();
				List<JStatement> nullDerefHeader = Arrays
				.asList(new JStatement[] { stmt });
		return nullDerefHeader;
	}

}
