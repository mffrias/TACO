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
package org.jmlspecs.jmlrac;

import java.io.StringWriter;

import org.apache.log4j.Logger;
import org.jmlspecs.checker.JmlClassDeclaration;
import org.jmlspecs.checker.JmlFieldDeclaration;
import org.jmlspecs.checker.JmlMethodDeclaration;
import org.jmlspecs.jmlrac.JmlModifier;
import org.multijava.javadoc.JavadocComment;
import org.multijava.mjc.*;
import org.multijava.util.compiler.TabbedPrintWriter;

import ar.edu.taco.TacoException;

/**
 * @author elgaby
 * 
 */
public class JavaAndJmlPrettyPrint2 extends RacPrettyPrinter2 {

	private static Logger log = Logger.getLogger(JavaAndJmlPrettyPrint2.class);

	private StringWriter stringWriter = new StringWriter();

	public String getPrettyPrint() {
		return this.stringWriter.toString();
	}

	public JavaAndJmlPrettyPrint2() {
		super(new StringWriter(), new JmlModifier());

		this.stringWriter = new StringWriter();
		this.p = new TabbedPrintWriter(stringWriter);
	}

	@Override
	public void close() {

	}

	public static String print(JCompilationUnitType compilationUnitType) {
		try {
			org.jmlspecs.jmlrac.JavaAndJmlPrettyPrint2 prettyPrinter2 = new org.jmlspecs.jmlrac.JavaAndJmlPrettyPrint2();

			compilationUnitType.accept(prettyPrinter2);

			String pretty_print_str = prettyPrinter2.getPrettyPrint();

			return pretty_print_str;

		} catch (Throwable t) {
			log.error(t);
			throw new TacoException(t.getMessage());
		}
	}

	@Override
	public void visitJmlMethodDeclaration(JmlMethodDeclaration self) {
//		JFormalParameter[] parameters = self.parameters();
		JavadocComment comment = self.javadocComment();
		if (comment != null) {
			this.stringWriter.append(comment.toString());
		}
		super.visitJmlMethodDeclaration(self);
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		return super.clone();
	}

	@Override
	public void visitJmlClassDeclaration(JmlClassDeclaration self) {
		JavadocComment javadocComment = self.javadocComment();
		if (javadocComment != null) {
			stringWriter.append(javadocComment.toString());
		}
		super.visitJmlClassDeclaration(self);
	}

	@Override
	public void visitClassDeclaration(JClassDeclaration self) {
		JavadocComment javadocComment = self.javadocComment();
		if (javadocComment != null) {
			stringWriter.append(javadocComment.toString());
		}
		super.visitClassDeclaration(self);
	}

	@Override
	public void visitFormalParameters(JFormalParameter self) {
		super.visitFormalParameters(self);
	}


	public void visitUnaryExpression(JUnaryExpression var1) {
		int var2 = var1.oper();
		JExpression var3 = var1.expr();
		switch (var2) {
			case 1:
				this.print("+ ");
				var3.accept(this);
				break;
			case 2:
				this.print("- ");
				var3.accept(this);
				break;
			case 12:
				this.print("~ ");
				var3.accept(this);
				break;
			case 13:
				this.print("!( ");
				var3.accept(this);
				this.print(" )");
				break;
			default:
				fail();
		}

	}




}
