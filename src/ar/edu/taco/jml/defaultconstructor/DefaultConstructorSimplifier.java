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
package ar.edu.taco.jml.defaultconstructor;

import java.util.ArrayList;
import java.util.List;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

import org.jmlspecs.checker.JmlClassDeclaration;
import org.jmlspecs.checker.JmlConstructorDeclaration;
import org.jmlspecs.checker.JmlMemberDeclaration;
import org.multijava.mjc.CClassType;
import org.multijava.mjc.JCompilationUnitType;
import org.multijava.mjc.JConstructorBlock;
import org.multijava.mjc.JConstructorDeclarationType;
import org.multijava.mjc.JFormalParameter;
import org.multijava.mjc.JMethodDeclarationType;
import org.multijava.mjc.JStatement;
import org.multijava.mjc.JmlClassDeclarationExtension;
import org.multijava.util.compiler.JavaStyleComment;

import ar.edu.taco.engine.JmlStage;
import ar.edu.taco.engine.StrykerStage;
import ar.edu.taco.jml.ASTSimplifierManager;
import ar.edu.taco.stryker.api.impl.NullOutputStream;
import ar.edu.taco.utils.jml.JmlAstClonerStatementVisitor;

public class DefaultConstructorSimplifier extends JmlAstClonerStatementVisitor {

	@SuppressWarnings("unchecked")
	@Override
	public void visitJmlClassDeclaration(JmlClassDeclaration self) {
		super.visitJmlClassDeclaration(self);
		JmlClassDeclaration newJmlClassDeclaration =  (JmlClassDeclaration) this.getStack().pop();

		JmlConstructorDeclaration theDefaultConstructor = null;
		for (Object method : newJmlClassDeclaration.methods()){
			if (method instanceof JmlConstructorDeclaration){
				if (((JmlConstructorDeclaration) method).isPublic() && ((JmlConstructorDeclaration) method).parameters().length == 0){
					theDefaultConstructor = (JmlConstructorDeclaration) method;
				}
			}
		}

		if (theDefaultConstructor == null) {
			JFormalParameter[] noParams = new JFormalParameter[]{};
			CClassType[] noExceptions = new CClassType[]{};
			JConstructorBlock theBody = new JConstructorBlock(self.getTokenReference(), new JStatement[]{});
			JmlConstructorDeclaration jmlConstructorDeclaration = 
					JmlConstructorDeclaration.makeInstance(newJmlClassDeclaration.getTokenReference(), newJmlClassDeclaration.modifiers(), 
							newJmlClassDeclaration.ident(), noParams, noExceptions, theBody, self.javadocComment(), new JavaStyleComment[0], null);
			newJmlClassDeclaration.setDefaultConstructor(jmlConstructorDeclaration);
			newJmlClassDeclaration.methods().add(jmlConstructorDeclaration);	

		}


		this.getStack().push(newJmlClassDeclaration);

	}
}
