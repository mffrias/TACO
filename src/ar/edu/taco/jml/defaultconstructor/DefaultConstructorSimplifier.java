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

/**
 * Gives each class a constructor without parameters when it has no public one: after copying a
 * class, it adds a constructor with an empty body and no specification (see
 * {@link #visitJmlClassDeclaration}). Nested classes are handled the same way; interfaces are
 * copied as they are ({@link JmlAstClonerStatementVisitor}).
 *
 * <p>{@code ASTSimplifierManager} runs it after {@code GhostFieldsSimplifier} and right before
 * {@code FieldInitializerSimplifier}, which puts the field initializations at the start of
 * every constructor, this one included, during {@code JmlStage} (docs/ARCHITECTURE.md).
 */
public class DefaultConstructorSimplifier extends JmlAstClonerStatementVisitor {

	/**
	 * Copies the class and, if none of its constructors is public and without parameters, adds
	 * one to the copy's methods. The new constructor has the class's name, position, modifiers
	 * and Javadoc comment, no parameters, no exceptions, an empty body and no specification.
	 * With the class's modifiers, it is public only in a public class, and a {@code final},
	 * {@code abstract} or {@code static} class gives a constructor with that modifier. It is
	 * added also when the class has a constructor without parameters that is not public.
	 *
	 * <p>Side effect: the new constructor is also set as the default constructor
	 * ({@code setDefaultConstructor}), which {@code JmlClassDeclarationExtension} forwards to
	 * the class it wraps, so the input AST's class declaration changes too.
	 *
	 * @param self the class declaration
	 */
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
