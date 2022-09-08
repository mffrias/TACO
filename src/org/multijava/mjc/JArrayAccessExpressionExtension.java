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
import org.multijava.mjc.JArrayAccessExpression;
import org.multijava.mjc.JExpression;
import org.multijava.mjc.JLiteral;
import org.multijava.mjc.MjcVisitor;
import org.multijava.util.MessageDescription;
import org.multijava.util.compiler.PositionedError;
import org.multijava.util.compiler.TokenReference;

public class JArrayAccessExpressionExtension extends JArrayAccessExpression {

	private JArrayAccessExpression wrapped;
	private JExpression newPrefix; 
	private JExpression newAccessor;
	public JArrayAccessExpressionExtension(JArrayAccessExpression self,JExpression newPrefix,JExpression newAccessor) {
		super(self.getTokenReference(), newPrefix, newAccessor);
		wrapped = self;		
		this.newAccessor = newAccessor;
		this.newPrefix = newPrefix;
	}
	@Override
	public void accept(MjcVisitor p) {
		p.visitArrayAccessExpression(this);
	}
	@Override
	public JExpression accessor() {
		
		return newAccessor;
	}
	@Override
	public void genCode(CodeSequence code) {
		wrapped.genCode(code);
	}
	@Override
	public void genEndStoreCode(CodeSequence code, boolean discardValue) {
		
		wrapped.genEndStoreCode(code, discardValue);
	}
	@Override
	public void genStartStoreCode(CodeSequence code) {
		
		wrapped.genStartStoreCode(code);
	}
	@Override
	public CType getType() {
		
		return wrapped.getType();
	}
	@Override
	public void initialize(CContextType ctxt) {
		
		wrapped.initialize(ctxt);
	}
	@Override
	public boolean isDeclaredNonNull() {
		
		return wrapped.isDeclaredNonNull();
	}
	@Override
	public boolean isDefinitelyAssigned(CContextType context) {
		
		return wrapped.isDefinitelyAssigned(context);
	}
	@Override
	public boolean isLValue(CExpressionContextType context) {
		return wrapped.isLValue(context);
	}
	@Override
	public boolean isMaybeInitializable() {
		return wrapped.isMaybeInitializable();
	}
	@Override
	public boolean isNonNull(CContextType context) {
		return wrapped.isNonNull(context);
	}
	@Override
	public JExpression prefix() {
		return newPrefix;
	}
	@Override
	public String toString() {
		return super.toString();
	}
	@Override
	public JExpression typecheck(CExpressionContextType context) throws PositionedError {
		return wrapped.typecheck(context);
	}
	@Override
	public void buildUniverseDynChecks(CExpressionContextType context, JExpression var) throws PositionedError {
		wrapped.buildUniverseDynChecks(context, var);
	}
	@Override
	public Object clone() {
		return wrapped.clone();
	}
	@Override
	public JExpression convertType(CType dest, CExpressionContextType context) throws PositionedError {
		return wrapped.convertType(dest, context);
	}
	@Override
	public void dumpArray(String msg, Object[] exprs) {
		wrapped.dumpArray(msg, exprs);
	}
	@Override
	protected void fail(CContextType context, MessageDescription key, Object[] params) throws PositionedError {
		super.fail(context, key, params);
	}

	@Override
	public void genUniverseDynCheckCode(CodeSequence code) {
		
		wrapped.genUniverseDynCheckCode(code);
	}
	@Override
	public CType getApparentType() {
		
		return wrapped.getApparentType();
	}
	@Override
	public Object[] getFANonNulls(CContextType context) {
		
		return wrapped.getFANonNulls(context);
	}
	@Override
	public Object[] getFANulls(CContextType context) {
		return wrapped.getFANulls(context);
	}
	@Override
	public JLiteral getLiteral() {
		
		return wrapped.getLiteral();
	}
	@Override
	public boolean isAssignableTo(CType dest) {
		
		return wrapped.isAssignableTo(dest);
	}
	@Override
	public boolean isBooleanLiteral() {
		
		return wrapped.isBooleanLiteral();
	}
	@Override
	public boolean isConstant() {
		return wrapped.isConstant();
	}
	@Override
	public boolean isLiteral() {
		return wrapped.isLiteral();
	}
	@Override
	public boolean isOrdinalLiteral() {
		return wrapped.isOrdinalLiteral();
	}
	@Override
	public boolean isRealLiteral() {
		return wrapped.isRealLiteral();
	}
	@Override
	public boolean isStatementExpression() {
		return wrapped.isStatementExpression();
	}
	@Override
	public boolean isStringLiteral() {
		return wrapped.isStringLiteral();
	}
	@Override
	public JExpression unParenthesize() {
		return wrapped.unParenthesize();
	}
	@Override
	protected void fail(CContextType context, MessageDescription description, Object param) throws PositionedError {
		super.fail(context, description, param);
	}
	@Override
	protected void fail(CContextType context, MessageDescription description) throws PositionedError {
		super.fail(context, description);
	}
	@Override
	public TokenReference getTokenReference() {
		return wrapped.getTokenReference();
	}
	@Override
	public void setTokenReference(TokenReference where) {
		wrapped.setTokenReference(where);
	}
	@Override
	public boolean equals(Object obj) {
		return wrapped.equals(obj);
	}
	@Override
	public int hashCode() {
		return wrapped.hashCode();
	}

}
