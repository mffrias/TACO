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

import org.jmlspecs.checker.JmlName;
import org.jmlspecs.checker.JmlNode;
import org.jmlspecs.checker.JmlStoreRef;
import org.jmlspecs.checker.JmlStoreRefExpression;
import org.jmlspecs.checker.JmlVisitor;
import org.multijava.mjc.CExpressionContextType;
import org.multijava.mjc.CFieldAccessor;
import org.multijava.mjc.CType;
import org.multijava.mjc.JExpression;
import org.multijava.mjc.MjcVisitor;
import org.multijava.util.compiler.PositionedError;
import org.multijava.util.compiler.TokenReference;

public class JmlStoreRefExpressionExtension extends JmlStoreRefExpression {

	JmlStoreRefExpression wrapped;
	JmlName[] jmlNames;
	String name;
	JExpression myExpr;
	public JmlStoreRefExpressionExtension(JmlStoreRefExpression self, String name,JmlName[] jmlNames, JExpression expr ) {
		super(self.getTokenReference(),jmlNames);
		wrapped = self;
		this.jmlNames = jmlNames;
		myExpr = expr;
		this.name = name;
	}
	@Override
	public void accept(MjcVisitor p) {
		if (p instanceof JmlVisitor)
		    ((JmlVisitor)p).visitJmlStoreRefExpression(this);
		else
		    throw new UnsupportedOperationException(JmlNode.MJCVISIT_MESSAGE);
	}

	@Override
	public JExpression expression() {
		return this.myExpr;
	}

	@Override
	public CFieldAccessor getField() {
		
		return wrapped.getField();
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public CType getType() {
		return wrapped.getType();
	}

	@Override
	public boolean isFieldOfReceiver() {
		return wrapped.isFieldOfReceiver();
	}

	@Override
	public boolean isInvalidModelFieldRef() {
		return wrapped.isInvalidModelFieldRef();
	}

	@Override
	public boolean isLocalVarReference() {
		
		return wrapped.isLocalVarReference();
	}

	@Override
	public boolean isModelField() {
		
		return wrapped.isModelField();
	}

	@Override
	public boolean isStoreRefExpression() {
		
		return wrapped.isStoreRefExpression();
	}

//	public boolean iswrappedExpression() {
//		
//		return wrapped.iswrappedExpression();
//	}

	@Override
	public boolean isThisExpression() {
		
		return wrapped.isThisExpression();
	}

	@Override
	public JmlName[] names() {
		
		return this.jmlNames;
	}

	@Override
	public String toString() {
		
		return wrapped.toString();
	}

	@Override
	public JmlStoreRef typecheck(CExpressionContextType context, long privacy) throws PositionedError {
		
		return wrapped.typecheck(context, privacy);
	}

	@Override
	public JmlStoreRef typecheck(CExpressionContextType context, long privacy, CType type) throws PositionedError {
		
		return wrapped.typecheck(context, privacy, type);
	}

	@Override
	public Object clone() {
		
		return wrapped.clone();
	}

	@Override
	public boolean isEverything() {
		
		return wrapped.isEverything();
	}

	@Override
	public boolean isInformalStoreRef() {
		
		return wrapped.isInformalStoreRef();
	}

	@Override
	public boolean isNotSpecified() {
		
		return wrapped.isNotSpecified();
	}

	@Override
	public boolean isNothing() {
		
		return wrapped.isNothing();
	}

	@Override
	public boolean isNothingOrNotSpecified() {
		
		return wrapped.isNothingOrNotSpecified();
	}

	@Override
	public boolean isPrivateData() {
		
		return wrapped.isPrivateData();
	}

//	@Override
//	protected void fail(CContextType context, MessageDescription description) throws PositionedError {
//		
//		wrapped.fail(context, description);
//	}
//
//	@Override
//	protected void fail(CContextType context, MessageDescription description, Object[] params) throws PositionedError {
//		
//		wrapped.fail(context, description, params);
//	}

//	@Override
//	protected void fail(CContextType context, MessageDescription description, Object param) throws PositionedError {
//		
//		wrapped.fail(context, description, param);
//	}

	@Override
	public TokenReference getTokenReference() {
		
		return wrapped.getTokenReference();
	}

	@Override
	public void setTokenReference(TokenReference where) {
		
		wrapped.setTokenReference(where);
	}

}
