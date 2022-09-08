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
package ar.edu.taco.jml.static_calls;

import org.multijava.mjc.CClassType;
import org.multijava.mjc.JExpression;
import org.multijava.mjc.JMethodCallExpression;
import org.multijava.mjc.JMethodCallExpressionExtension;
import org.multijava.mjc.JNameExpression;
import org.multijava.mjc.JTypeNameExpression;
import org.multijava.util.compiler.TokenReference;

import ar.edu.taco.utils.jml.JmlAstClonerExpressionVisitor;

/**
 * Qualify static invokations
 * 
 * C.m() -> fullpackage.C.m()
 * 
 * @author jgaleotti
 * 
 */
public class QualifyStaticCallsExprVisitor extends JmlAstClonerExpressionVisitor {

	@Override
	public void visitMethodCallExpression(JMethodCallExpression self) {
		JExpression prefix = self.prefix();

		if (prefix instanceof JTypeNameExpression) {
			JTypeNameExpression type_name_expr = (JTypeNameExpression) prefix;
			JNameExpression name_expr = type_name_expr.sourceName();
			
			JExpression name_expr_prefix = name_expr.getPrefix();

			if (name_expr_prefix == null) {

				String[] type_name_expr_strs = type_name_expr.toString().split("/");
				
				
				CClassType class_type = (CClassType) prefix.getType();

				TokenReference prefix_token_ref = prefix.getTokenReference();
				
				JNameExpression qualified_name_expr = null;
				for (int i = 0; i < type_name_expr_strs.length; i++) {
					String name = type_name_expr_strs[i];
					if (qualified_name_expr==null) {
						qualified_name_expr = new JNameExpression(prefix_token_ref , name);
					} else {
						qualified_name_expr = new JNameExpression(prefix_token_ref , qualified_name_expr, name);
					}
				}

				JExpression newPrefix = new JTypeNameExpression(prefix.getTokenReference(), class_type, qualified_name_expr);

				JExpression[] newArgs = new JExpression[self.args().length];
				for (int i = 0; i < self.args().length; i++) {
					JExpression expression = self.args()[i];
					expression.accept(this);
					newArgs[i] = this.getArrayStack().pop();
				}
				
				JMethodCallExpressionExtension newSelf = new JMethodCallExpressionExtension(self, newPrefix, newArgs);

				this.getArrayStack().push(newSelf);

				return;
			}

		}
		super.visitMethodCallExpression(self);

	}

}
