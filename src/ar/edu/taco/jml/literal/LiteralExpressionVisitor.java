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
package ar.edu.taco.jml.literal;

import org.multijava.mjc.CStdType;
import org.multijava.mjc.JExpression;
import org.multijava.mjc.JMethodCallExpression;
import org.multijava.mjc.JMethodCallExpressionExtensiorForMethodInformation;
import org.multijava.mjc.JNameExpression;
import org.multijava.mjc.JNumberLiteral;
import org.multijava.mjc.JStringLiteral;
import org.multijava.mjc.JTypeNameExpression;

import ar.edu.taco.utils.jml.JmlAstClonerExpressionVisitor;

/**
 * Implementa la simplificacion de la regla 22
 * 
 * @author diegodob
 * 
 */
public class LiteralExpressionVisitor extends JmlAstClonerExpressionVisitor {

//	private class CClassFQNameTypeExtended extends CClassFQNameType {
//
//		protected CClassFQNameTypeExtended(String arg0) {
//			super(arg0);
//		}
//		
//	}
	
//	@Override
//	public void visitStringLiteral(JStringLiteral self) {
//
//		JNameExpression jNameExpression = new JNameExpression(self.getTokenReference() ,"String");
//
//		JTypeNameExpression typeNameExpression = new JTypeNameExpression(self.getTokenReference(), CStdType.String,jNameExpression);
//		
//		JExpression length = JNumberLiteral.createLiteral(CStdType.Integer, self.stringValue().length());
//		JMethodCallExpression methodCallExpression = new JMethodCallExpressionExtensiorForMethodInformation(self.getTokenReference(), typeNameExpression,"buildInstance", new JExpression[] { self, length });
//		getArrayStack().add(methodCallExpression);
//	}
	
//	@Override
//	public void visitCharLiteral(JCharLiteral self) {
//		JNameExpression jPrefixNameExpression = new JNameExpression(self.getTokenReference() ,"Character");
//		
//		CClassFQNameType pp = new CClassFQNameTypeExtended("java/lang/Character");
//
//		JTypeNameExpression typePrefixNameExpression = new JTypeNameExpression(self.getTokenReference(), pp, jPrefixNameExpression);
//		
//		JMethodCallExpression methodCallExpression = new JMethodCallExpressionExtensiorForMethodInformation(self.getTokenReference(), typePrefixNameExpression,"buildInstance", new JExpression[] { self });
//
//		getArrayStack().add(methodCallExpression);
//	}

}
