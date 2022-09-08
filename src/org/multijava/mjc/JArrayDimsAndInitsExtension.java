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
package org.multijava.mjc;

import org.multijava.mjc.CContextType;
import org.multijava.mjc.CExpressionContextType;
import org.multijava.mjc.CType;
import org.multijava.mjc.CodeSequence;
import org.multijava.mjc.JArrayDimsAndInits;
import org.multijava.mjc.JArrayInitializer;
import org.multijava.mjc.JExpression;
import org.multijava.mjc.MjcVisitor;
import org.multijava.util.compiler.PositionedError;
import org.multijava.util.compiler.TokenReference;

public class JArrayDimsAndInitsExtension extends JArrayDimsAndInits {
	private JExpression[] newDims;
	private JArrayInitializer newInit;
	private JArrayDimsAndInits wrapped;

	public JArrayDimsAndInitsExtension(JArrayDimsAndInits self, JExpression[] newDims, JArrayInitializer newInit) {
		super(self.getTokenReference(), null, self.dims(), self.init());
		wrapped = self;
		this.newDims = newDims;
		this.newInit = newInit;
	}

	@Override
	public JExpression[] dims() {
		return this.newDims;
	}

	@Override
	public JArrayInitializer init() {
		return this.newInit;
	}

	@Override
	public void accept(MjcVisitor p) {
		p.visitArrayDimsAndInit(this);
	}

	@Override
	public void genCode(CodeSequence code) {

		wrapped.genCode(code);
	}

	@Override
	public boolean isNonNull(CContextType context) {

		return wrapped.isNonNull(context);
	}

	@Override
	public String toString() {

		return wrapped.toString();
	}

	@Override
	public CType typecheck(CExpressionContextType context, CType type) throws PositionedError {

		return wrapped.typecheck(context, type);
	}

	@Override
	public TokenReference getTokenReference() {

		return wrapped.getTokenReference();
	}

	@Override
	public void setTokenReference(TokenReference where) {

		wrapped.setTokenReference(where);
	}

}
