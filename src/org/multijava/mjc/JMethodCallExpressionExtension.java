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
import org.multijava.mjc.CMethod;
import org.multijava.mjc.CType;
import org.multijava.mjc.CodeSequence;
import org.multijava.mjc.JExpression;
import org.multijava.mjc.JLiteral;
import org.multijava.mjc.JMethodCallExpression;
import org.multijava.mjc.JNameExpression;
import org.multijava.mjc.MjcVisitor;
import org.multijava.util.compiler.PositionedError;
import org.multijava.util.compiler.TokenReference;

public class JMethodCallExpressionExtension extends JMethodCallExpression {

	private JExpression[] newArgs;
	private JExpression newPrefix;
	private JMethodCallExpression wrappedMethodCallExpression;

	/*
	 * public JMethodCallExpressionExtension(TokenReference where, JExpression
	 * prefix, String ident, JExpression[] newArgs, boolean doSetContext) {
	 * super(where, prefix, ident, newArgs, doSetContext); this.newArgs =
	 * newArgs; }
	 */
	public JMethodCallExpressionExtension(JMethodCallExpression self, JExpression newPrefix, JExpression[] newArgs) {
		super(self.getTokenReference(), self.prefix(), self.ident(), self.args());

		this.wrappedMethodCallExpression = self;
		this.newArgs = newArgs;
		this.newPrefix = newPrefix;
		this.method = self.method();

	}
	
	@Override
	public Object clone() {
		JMethodCallExpression clonedMethodCall = (JMethodCallExpression)wrappedMethodCallExpression.clone();
		JExpression[] clonedNewArgs = new JExpression[newArgs.length];
		for (int idx = 0; idx < clonedNewArgs.length; idx++){
			clonedNewArgs[idx] = (JExpression)newArgs[idx].clone();
		}
		JExpression clonedNewPrefix = null;
		if (newPrefix != null){
			clonedNewPrefix = (JExpression)newPrefix.clone();
		} 
		JMethodCallExpressionExtension theClonedMethodCallExpressionExtension = new JMethodCallExpressionExtension(clonedMethodCall, clonedNewPrefix, clonedNewArgs);
		
		return theClonedMethodCallExpressionExtension;
	}


	@Override
	public JExpression[] args() {
		return newArgs;
	}

	@Override
	public JExpression prefix() {
		return newPrefix;
	}

	@Override
	public void accept(MjcVisitor p) {
	    p.visitMethodCallExpression( this );
	}

	@Override
	public void genCode(CodeSequence code) {

		wrappedMethodCallExpression.genCode(code);
	}

	@Override
	public CType getType() {

		return wrappedMethodCallExpression.getType();
	}

	@Override
	public String ident() {

		return wrappedMethodCallExpression.ident();
	}

	@Override
	public boolean isNonNull(CContextType context) {

		return wrappedMethodCallExpression.isNonNull(context);
	}

	@Override
	public boolean isStatementExpression() {

		return wrappedMethodCallExpression.isStatementExpression();
	}

	@Override
	public CMethod method() {

		return wrappedMethodCallExpression.method();
	}

	@Override
	public void setPrefix(JExpression expr) {

		wrappedMethodCallExpression.setPrefix(expr);
	}

	@Override
	public void setType(CType type) {

		wrappedMethodCallExpression.setType(type);
	}

	@Override
	public JNameExpression sourceName() {

		return wrappedMethodCallExpression.sourceName();
	}

	@Override
	public String toString() {
	    	StringBuffer params = new StringBuffer();
	    	for (int i = 0; i < args().length; i++) {
				params.append(args[i].toString());
				if (i < args.length - 1)
					params.append(", ");
			}
	    	return prefix() + "." + ident + "(" + params.toString() + ")";
	}

	@Override
	public JExpression typecheck(CExpressionContextType context) throws PositionedError {

		return wrappedMethodCallExpression.typecheck(context);
	}

	@Override
	public void buildUniverseDynChecks(CExpressionContextType context, JExpression var) throws PositionedError {

		wrappedMethodCallExpression.buildUniverseDynChecks(context, var);
	}


	@Override
	public JExpression convertType(CType dest, CExpressionContextType context) throws PositionedError {

		return wrappedMethodCallExpression.convertType(dest, context);
	}

	@Override
	public void dumpArray(String msg, Object[] exprs) {

		wrappedMethodCallExpression.dumpArray(msg, exprs);
	}

	@Override
	public void genUniverseDynCheckCode(CodeSequence code) {

		wrappedMethodCallExpression.genUniverseDynCheckCode(code);
	}

	@Override
	public CType getApparentType() {

		return wrappedMethodCallExpression.getApparentType();
	}

	@Override
	public Object[] getFANonNulls(CContextType context) {

		return wrappedMethodCallExpression.getFANonNulls(context);
	}

	@Override
	public Object[] getFANulls(CContextType context) {

		return wrappedMethodCallExpression.getFANulls(context);
	}

	@Override
	public JLiteral getLiteral() {

		return wrappedMethodCallExpression.getLiteral();
	}

	@Override
	public boolean isAssignableTo(CType dest) {

		return wrappedMethodCallExpression.isAssignableTo(dest);
	}

	@Override
	public boolean isBooleanLiteral() {

		return wrappedMethodCallExpression.isBooleanLiteral();
	}

	@Override
	public boolean isConstant() {

		return wrappedMethodCallExpression.isConstant();
	}

	@Override
	public boolean isDeclaredNonNull() {

		return wrappedMethodCallExpression.isDeclaredNonNull();
	}

	@Override
	public boolean isLiteral() {

		return wrappedMethodCallExpression.isLiteral();
	}

	@Override
	public boolean isMaybeInitializable() {

		return wrappedMethodCallExpression.isMaybeInitializable();
	}

	@Override
	public boolean isOrdinalLiteral() {

		return wrappedMethodCallExpression.isOrdinalLiteral();
	}

	@Override
	public boolean isRealLiteral() {

		return wrappedMethodCallExpression.isRealLiteral();
	}

	@Override
	public boolean isStringLiteral() {

		return wrappedMethodCallExpression.isStringLiteral();
	}

	@Override
	public JExpression unParenthesize() {

		return wrappedMethodCallExpression.unParenthesize();
	}

	@Override
	public TokenReference getTokenReference() {

		return wrappedMethodCallExpression.getTokenReference();
	}

	@Override
	public void setTokenReference(TokenReference where) {

		wrappedMethodCallExpression.setTokenReference(where);
	}

}
