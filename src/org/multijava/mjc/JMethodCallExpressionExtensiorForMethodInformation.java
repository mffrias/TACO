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

import org.multijava.mjc.CType;
import org.multijava.mjc.JExpression;
import org.multijava.mjc.JMethodCallExpression;
import org.multijava.util.compiler.TokenReference;

import ar.edu.taco.simplejml.methodinfo.MethodInformation;
import ar.edu.taco.simplejml.methodinfo.MethodInformationBuilder;

public class JMethodCallExpressionExtensiorForMethodInformation extends JMethodCallExpression {

	private MethodInformation wrapped;

	public JMethodCallExpressionExtensiorForMethodInformation(TokenReference where, JExpression prefix, String ident, JExpression[] args) {
		super(where, prefix, ident, args);
		this.wrapped = MethodInformationBuilder.getInstance().getMethodInformation(this);
	}
	
	@Override
	public CType getType() {
		return this.wrapped.getReturnType();
	}

}
