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
package ar.edu.taco.utils.jml;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

import org.jmlspecs.checker.JmlAssertStatement;
import org.jmlspecs.checker.JmlAssignableClause;
import org.jmlspecs.checker.JmlAssignmentStatement;
import org.jmlspecs.checker.JmlAssumeStatement;
import org.jmlspecs.checker.JmlClassDeclaration;
import org.jmlspecs.checker.JmlCompilationUnit;
import org.jmlspecs.checker.JmlConstraint;
import org.jmlspecs.checker.JmlConstructorDeclaration;
import org.jmlspecs.checker.JmlEnsuresClause;
import org.jmlspecs.checker.JmlExceptionalBehaviorSpec;
import org.jmlspecs.checker.JmlExceptionalSpecBody;
import org.jmlspecs.checker.JmlExceptionalSpecCase;
import org.jmlspecs.checker.JmlFieldDeclaration;
import org.jmlspecs.checker.JmlGeneralSpecCase;
import org.jmlspecs.checker.JmlGenericSpecBody;
import org.jmlspecs.checker.JmlGenericSpecCase;
import org.jmlspecs.checker.JmlInformalExpression;
import org.jmlspecs.checker.JmlInterfaceDeclaration;
import org.jmlspecs.checker.JmlInvariant;
import org.jmlspecs.checker.JmlLoopStatement;
import org.jmlspecs.checker.JmlMethodDeclaration;
import org.jmlspecs.checker.JmlMethodSpecification;
import org.jmlspecs.checker.JmlNormalBehaviorSpec;
import org.jmlspecs.checker.JmlNormalSpecBody;
import org.jmlspecs.checker.JmlNormalSpecCase;
import org.jmlspecs.checker.JmlPredicate;
import org.jmlspecs.checker.JmlRefinePrefix;
import org.jmlspecs.checker.JmlRepresentsDecl;
import org.jmlspecs.checker.JmlRequiresClause;
import org.jmlspecs.checker.JmlSetStatement;
import org.jmlspecs.checker.JmlSignalsClause;
import org.jmlspecs.checker.JmlSignalsOnlyClause;
import org.jmlspecs.checker.JmlSourceMethod;
import org.jmlspecs.checker.JmlSpecBodyClause;
import org.jmlspecs.checker.JmlSpecCase;
import org.jmlspecs.checker.JmlSpecification;
import org.multijava.mjc.CClass;
import org.multijava.mjc.CCompilationUnit;
import org.multijava.mjc.CType;
import org.multijava.mjc.JAssertStatement;
import org.multijava.mjc.JBlock;
import org.multijava.mjc.JBreakStatement;
import org.multijava.mjc.JCatchClause;
import org.multijava.mjc.JClassBlock;
import org.multijava.mjc.JCompoundStatement;
import org.multijava.mjc.JConstructorBlock;
import org.multijava.mjc.JContinueStatement;
import org.multijava.mjc.JDoStatement;
import org.multijava.mjc.JEmptyStatement;
import org.multijava.mjc.JExpressionListStatement;
import org.multijava.mjc.JExpressionStatement;
import org.multijava.mjc.JFieldDeclarationType;
import org.multijava.mjc.JForStatement;
import org.multijava.mjc.JIfStatement;
import org.multijava.mjc.JLabeledStatement;
import org.multijava.mjc.JMethodDeclaration;
import org.multijava.mjc.JPackageImportType;
import org.multijava.mjc.JPackageName;
import org.multijava.mjc.JPhylum;
import org.multijava.mjc.JReturnStatement;
import org.multijava.mjc.JStatement;
import org.multijava.mjc.JSwitchGroup;
import org.multijava.mjc.JSwitchStatement;
import org.multijava.mjc.JSynchronizedStatement;
import org.multijava.mjc.JThrowStatement;
import org.multijava.mjc.JTryCatchStatement;
import org.multijava.mjc.JTryFinallyStatement;
import org.multijava.mjc.JTypeDeclarationStatement;
import org.multijava.mjc.JTypeDeclarationType;
import org.multijava.mjc.JVariableDeclarationStatement;
import org.multijava.mjc.JVariableDefinition;
import org.multijava.mjc.JWhileStatement;
import org.multijava.mjc.JmlClassDeclarationExtension;
import org.multijava.util.compiler.JavaStyleComment;
import org.multijava.util.compiler.TokenReference;

import ar.edu.taco.TacoException;
import ar.edu.taco.TacoNotImplementedYetException;
import ar.edu.taco.jml.utils.ASTUtils;
import ar.edu.taco.simplejml.JmlBaseVisitor;

public class JmlAstClonerStatementVisitor extends JmlBaseVisitor {

	private Stack<Object> stack = new Stack<Object>();

	public Stack<Object> getStack() {
		return stack;
	}



	@SuppressWarnings("unchecked")
	@Override
	public void visitJmlCompilationUnit(JmlCompilationUnit self) {

		TokenReference where = self.getTokenReference();
		JPackageName package_name = self.packageName();
		CCompilationUnit export = null;
		JPackageImportType[] imported_packages = self.importedPackages();

		@SuppressWarnings("rawtypes")
		ArrayList imported_units = new ArrayList();
		Collections.addAll(imported_units, self.importedUnits());
		JTypeDeclarationType[] typeDeclarations = self.typeDeclarations();
		JTypeDeclarationType[] new_type_declarations = new JTypeDeclarationType[typeDeclarations.length];
		for (int i = 0; i < typeDeclarations.length; i++) {
			typeDeclarations[i].accept(this);
			Object ret_val = this.getStack().pop();
			JTypeDeclarationType cloned_type_declaration = (JTypeDeclarationType) ret_val;
			new_type_declarations[i] = cloned_type_declaration;
		}
		@SuppressWarnings("rawtypes")
		ArrayList<JmlMethodDeclaration> top_level_methods = self.tlMethods();
		JmlRefinePrefix refinePrefix = self.refinePrefix();
		JmlCompilationUnit compilationUnit = new JmlCompilationUnit(where, package_name, export, imported_packages, imported_units, new_type_declarations,
				top_level_methods, refinePrefix);


		this.getStack().push(compilationUnit);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void visitJmlClassDeclaration(JmlClassDeclaration self) {

		ArrayList<JPhylum> newFieldsAndInit = new ArrayList<JPhylum>();
		for (int i = 0; i < self.fieldsAndInits().length; i++) {
			if (self.fieldsAndInits()[i] instanceof JClassBlock) {
				newFieldsAndInit.add(self.fieldsAndInits()[i]);
			} else {
				if (self.fieldsAndInits()[i] instanceof JFieldDeclarationType) {
					if (!(self.fieldsAndInits()[i] instanceof JmlFieldDeclaration)) {
						throw new TacoException("Simplifier error: Must be a JMLField");
					}
					JmlFieldDeclaration jFieldDeclarationType = (JmlFieldDeclaration) self.fieldsAndInits()[i];
					jFieldDeclarationType.accept(this);
					JPhylum newFieldDeclarationType = (JPhylum) this.getStack().pop();
					newFieldsAndInit.add(newFieldDeclarationType);

				}
			}
		}

		ArrayList<JmlMethodDeclaration> newMethods = new ArrayList<JmlMethodDeclaration>();
		for (JmlMethodDeclaration methodDeclaration : (ArrayList<JmlMethodDeclaration>) self.methods()) {
			methodDeclaration.accept(this);
			newMethods.add((JmlMethodDeclaration) this.getStack().pop());
		}

		List<JFieldDeclarationType> jModelFieldDeclarationTypeList = null;
		if (self.getModelFields() != null) {
			jModelFieldDeclarationTypeList = new ArrayList<JFieldDeclarationType>();
			for (JFieldDeclarationType jFieldDeclarationType : self.getModelFields()) {
				jFieldDeclarationType.accept(this);
				JFieldDeclarationType newFieldDeclarationType = (JFieldDeclarationType) this.getStack().pop();
				jModelFieldDeclarationTypeList.add(newFieldDeclarationType);
			}

		}

		List<JmlInvariant> jmlInvariantList = null;
		if (self.invariants() != null) {
			jmlInvariantList = new ArrayList<JmlInvariant>();
			for (JmlInvariant jmlInvariant : self.invariants()) {
				jmlInvariant.accept(this);
				jmlInvariantList.add((JmlInvariant) this.getStack().pop());
			}
		}

		List<JmlRepresentsDecl> jmlRepresentsDeclList = null;
		if (self.representsDecls() != null) {
			jmlRepresentsDeclList = new ArrayList<JmlRepresentsDecl>();
			for (JmlRepresentsDecl jmlRepresentsDecl : self.representsDecls()) {
				jmlRepresentsDecl.accept(this);
				jmlRepresentsDeclList.add((JmlRepresentsDecl) this.getStack().pop());
			}
		}

		ArrayList<JmlClassDeclaration> newInners = null;
		if (self.inners() != null) {
			newInners = new ArrayList<JmlClassDeclaration>();
			for (JmlClassDeclaration innerClassDeclaration : (ArrayList<JmlClassDeclaration>) self.inners()) {
				innerClassDeclaration.accept(this);
				newInners.add((JmlClassDeclaration) this.getStack().pop());
			}
		}

		ArrayList<JmlConstraint> newConstraints = null;
		if (self.constraints() != null) {
			newConstraints = new ArrayList<JmlConstraint>();
			for (JmlConstraint constraint : self.constraints()) {
				constraint.accept(this);
				newConstraints.add((JmlConstraint) this.getStack().pop());
			}
		}

		self.setFields(newFieldsAndInit.toArray(new JPhylum[0]));
		JmlClassDeclaration newJmlClassDeclaration = new JmlClassDeclarationExtension(self, jmlInvariantList.toArray(new JmlInvariant[0]),
				jmlRepresentsDeclList.toArray(new JmlRepresentsDecl[0]), newConstraints.toArray(new JmlConstraint[0]), newMethods,
				jModelFieldDeclarationTypeList, newInners);
		this.getStack().push(newJmlClassDeclaration);

	}

	@Override
	public void visitJmlRepresentsDecl(JmlRepresentsDecl self) {
		JmlRepresentsDecl newSelf = new JmlRepresentsDecl(self.getTokenReference(), self.modifiers(), self.isRedundantly(), self.storeRef(), self.predicate());
		this.getStack().push(newSelf);
	}

	@Override
	public void visitJmlConstraint(JmlConstraint self) {
		JmlConstraint newSelf = new JmlConstraint(self.getTokenReference(), self.modifiers(), self.isRedundantly(), self.predicate(), self.methodNames());
		this.getStack().push(newSelf);
	}

	@Override
	public void visitJmlInterfaceDeclaration(JmlInterfaceDeclaration self) {
		this.getStack().push(self);
	}

	@Override
	public void visitJmlMethodDeclaration(JmlMethodDeclaration self) {
		
		JBlock newBody;
		if (self.body() == null) {
			newBody = null;
		} else {
			self.body().accept(this);
			newBody = (JBlock) this.getStack().pop();
		}

		JmlMethodSpecification methodSpecification;
		if (self.hasSpecification()) {
			self.methodSpecification().accept(this);
			methodSpecification = (JmlMethodSpecification) this.getStack().pop();
		} else {
			methodSpecification = null;
		}

		if (methodSpecification != null) {
			JmlSpecCase[] specCases = self.methodSpecification().specCases();

			for (int x = 0; x < methodSpecification.specCases().length; x++) {
				specCases[x] = methodSpecification.specCases()[x];
			}
		}
			
		JmlMethodDeclaration theClonedMethodDecl = 
				JmlMethodDeclaration.makeInstance(self.getTokenReference(), self.modifiers(), self.typevariables(), self.returnType(), self.ident(), self.parameters(), self.getExceptions(), newBody, self.javadocComment(), new JavaStyleComment[0], self.methodSpecification());

		
		this.getStack().push(theClonedMethodDecl);
	}

	@Override
	public void visitJmlConstructorDeclaration(JmlConstructorDeclaration self) {
		self.body().accept(this);
		JConstructorBlock newConstructorBlock = (JConstructorBlock) this.getStack().pop();

		JmlMethodSpecification methodSpecification;
		if (self.methodSpecification() == null) {
			methodSpecification = null;
		} else {
			self.methodSpecification().accept(this);
			methodSpecification = (JmlMethodSpecification) this.getStack().pop();
		}

		JmlConstructorDeclaration jmlConstructorDeclaration = JmlConstructorDeclaration.makeInstance(self.getTokenReference(), self.modifiers(), self.ident(),
				self.parameters(), self.getExceptions(), newConstructorBlock, self.javadocComment(), new JavaStyleComment[0], methodSpecification);

		// .makeInstance(self.getTokenReference(), self.modifiers(),
		// self.typevariables(), self
		// .returnType(), self.ident(), self.parameters(), self.getExceptions(),
		// newBody, self.javadocComment(), null /* comments */, self
		// .methodSpecification());

		this.getStack().push(jmlConstructorDeclaration);
	}

	@Override
	public void visitJmlSpecification(JmlSpecification self) {
		JmlSpecification newSelf;
		if (self.hasSpecCases()) {
			List<JmlSpecCase> jmlSpecCases = null;
			jmlSpecCases = new ArrayList<JmlSpecCase>();

			for (JmlSpecCase jmlSpecCase : self.specCases()) {
				jmlSpecCase.accept(this);
				jmlSpecCases.add((JmlSpecCase) this.getStack().pop());
			}
			newSelf = new JmlSpecification(self.getTokenReference(), jmlSpecCases.toArray(new JmlSpecCase[0]), self.redundantSpec());
		} else {
			newSelf = new JmlSpecification(self.getTokenReference(), self.redundantSpec());
		}

		this.getStack().push(newSelf);
	}

	@Override
	public void visitJmlAssignmentStatement(JmlAssignmentStatement self) {
		self.assignmentStatement().accept(this);
		JmlAssignmentStatement newSelf = new JmlAssignmentStatement((JExpressionStatement) this.getStack().pop());
		this.getStack().push(newSelf);
	}



	@Override
	public void visitJmlGenericSpecBody(JmlGenericSpecBody self) {
		List<JmlSpecBodyClause> specClauses;
		JmlGenericSpecBody newSelf;
		if (self.isSpecClauses()) {
			specClauses = new ArrayList<JmlSpecBodyClause>();
			for (JmlSpecBodyClause jmlSpecBodyClause : self.specClauses()) {
				jmlSpecBodyClause.accept(this);
				specClauses.add((JmlSpecBodyClause) this.getStack().pop());
			}
			newSelf = new JmlGenericSpecBody(specClauses.toArray(new JmlSpecBodyClause[0]));
		} else {
			newSelf = self;
		}
		this.getStack().push(newSelf);

	}


@Override
public void visitJmlGenericSpecCase(JmlGenericSpecCase self) {
	List<JmlRequiresClause> jmlSpecHeader = new ArrayList<JmlRequiresClause>();
	JmlGenericSpecBody jmlSpecBodys = null;

	if (self.hasSpecHeader()) {
		for (JmlRequiresClause jmlRequiresClause : self.specHeader()) {
			jmlRequiresClause.accept(this);
			jmlSpecHeader.add((JmlRequiresClause) this.getStack().pop());
		}
	}

	if (self.hasSpecBody()) {
		self.specBody().accept(this);
		jmlSpecBodys = (JmlGenericSpecBody) this.getStack().pop();
	}

	JmlGenericSpecCase newSelf = new JmlGenericSpecCase(self.getTokenReference(), self.specVarDecls(), jmlSpecHeader.toArray(new JmlRequiresClause[0]),
			jmlSpecBodys);
	this.getStack().push(newSelf);

}

@Override
public void visitJmlExceptionalBehaviorSpec(JmlExceptionalBehaviorSpec self) {
	self.specCase().accept(this);
	JmlExceptionalBehaviorSpec newSelf = new JmlExceptionalBehaviorSpec(self.getTokenReference(), self.privacy(), (JmlGeneralSpecCase) this.getStack()
			.pop());
	this.getStack().push(newSelf);
}

@Override
public void visitJmlExceptionalSpecBody(JmlExceptionalSpecBody self) {
	List<JmlSpecBodyClause> specClauses;
	if (self.isSpecClauses()) {
		specClauses = new ArrayList<JmlSpecBodyClause>();
		for (JmlSpecBodyClause jmlSpecBodyClause : self.specClauses()) {
			jmlSpecBodyClause.accept(this);
			specClauses.add((JmlSpecBodyClause) this.getStack().pop());
		}
	} else {
		specClauses = null;
	}
	JmlExceptionalSpecBody newSelf = new JmlExceptionalSpecBody(specClauses.toArray(new JmlSpecBodyClause[0]));
	this.getStack().push(newSelf);
}

@Override
public void visitJmlExceptionalSpecCase(JmlExceptionalSpecCase self) {
	List<JmlRequiresClause> jmlSpecHeader = new ArrayList<JmlRequiresClause>();
	JmlExceptionalSpecBody jmlSpecBodys = null;

	if (self.hasSpecHeader()) {
		for (JmlRequiresClause jmlRequiresClause : self.specHeader()) {
			jmlRequiresClause.accept(this);
			jmlSpecHeader.add((JmlRequiresClause) this.getStack().pop());
		}
	}

	if (self.hasSpecBody()) {
		self.specBody().accept(this);
		jmlSpecBodys = (JmlExceptionalSpecBody) this.getStack().pop();
	}

	JmlExceptionalSpecCase newSelf = new JmlExceptionalSpecCase(self.getTokenReference(), self.specVarDecls(), jmlSpecHeader
			.toArray(new JmlRequiresClause[0]), jmlSpecBodys);
	this.getStack().push(newSelf);
}

@Override
public void visitJmlNormalBehaviorSpec(JmlNormalBehaviorSpec self) {
	self.specCase().accept(this);
	JmlNormalBehaviorSpec newSelf = new JmlNormalBehaviorSpec(self.getTokenReference(), self.privacy(), (JmlGeneralSpecCase) this.getStack().pop());
	this.getStack().push(newSelf);
}

@Override
public void visitJmlNormalSpecBody(JmlNormalSpecBody self) {
	JmlNormalSpecBody newSelf;
	if (self.isSpecClauses()) {
		List<JmlSpecBodyClause> specClauses;
		specClauses = new ArrayList<JmlSpecBodyClause>();
		for (JmlSpecBodyClause jmlSpecBodyClause : self.specClauses()) {
			jmlSpecBodyClause.accept(this);
			specClauses.add((JmlSpecBodyClause) this.getStack().pop());
		}
		newSelf = new JmlNormalSpecBody(specClauses.toArray(new JmlSpecBodyClause[0]));
	} else {
		List<JmlGeneralSpecCase> specClauses;
		specClauses = new ArrayList<JmlGeneralSpecCase>();
		for (JmlGeneralSpecCase jmlGeneralSpecCase : self.specCases()) {
			jmlGeneralSpecCase.accept(this);
			specClauses.add((JmlGeneralSpecCase) this.getStack().pop());
		}
		newSelf = new JmlNormalSpecBody(specClauses.toArray(new JmlGeneralSpecCase[0]));
	}

	this.getStack().push(newSelf);
}

@Override
public void visitJmlNormalSpecCase(JmlNormalSpecCase self) {
	List<JmlRequiresClause> jmlSpecHeader = new ArrayList<JmlRequiresClause>();
	JmlNormalSpecBody jmlSpecBodys = null;

	if (self.hasSpecHeader()) {
		for (JmlRequiresClause jmlRequiresClause : self.specHeader()) {
			jmlRequiresClause.accept(this);
			jmlSpecHeader.add((JmlRequiresClause) this.getStack().pop());
		}
	}

	if (self.hasSpecBody()) {
		self.specBody().accept(this);
		jmlSpecBodys = (JmlNormalSpecBody) this.getStack().pop();
	}

	JmlNormalSpecCase newSelf = new JmlNormalSpecCase(self.getTokenReference(), self.specVarDecls(), jmlSpecHeader.toArray(new JmlRequiresClause[0]),
			jmlSpecBodys);
	this.getStack().push(newSelf);

}

@Override
public void visitJmlSignalsClause(JmlSignalsClause self) {
	//		if (self.isNotSpecified()) {
	//			throw new IllegalArgumentException("Signals clause is not specified.");
	//		}
	//		
	////		self.predOrNot().accept(this);
	////		JmlPredicate pred = (JmlPredicate) this.getStack().pop();
	//		
	//		JmlPredicate newPredicate = simplifyPredicateSupport(self.predOrNot());
	//
	//		JmlSignalsClause newSelf = new JmlSignalsClause(self.getTokenReference(), self.isRedundantly(), self.type(), self.ident(), pred, false);
	////		
	this.getStack().push(self);
}

@Override
public void visitJmlSignalsOnlyClause(JmlSignalsOnlyClause self) {
	this.getStack().push(self);
}

@Override
public void visitJmlInformalExpression(JmlInformalExpression jmlInformalExpression) {
	this.getStack().push(jmlInformalExpression);
}

@Override
public void visitJmlEnsuresClause(JmlEnsuresClause self) {
	if (self.isNotSpecified()) {
		throw new IllegalArgumentException("Ensures clause is not specified.");
	}

	JmlEnsuresClause newSelf = new JmlEnsuresClause(self.getTokenReference(), self.isRedundantly(), self.predOrNot());
	this.getStack().push(newSelf);
}

@Override
public void visitJmlRequiresClause(JmlRequiresClause self) {
	if (self.isNotSpecified()) {
		throw new IllegalArgumentException("Requires clause is not specified.");
	}

	JmlRequiresClause newSelf = new JmlRequiresClause(self.getTokenReference(), self.isRedundantly(), self.predOrNot());
	this.getStack().push(newSelf);

}


// ----------------------------------------------------------------------
// STATEMENT
// ----------------------------------------------------------------------

/** Visits the given while statement. */
public void visitWhileStatement(/* @non_null */JWhileStatement self) {

	self.body().accept(this);
	JStatement newBody = (JStatement) this.getStack().pop();
	;

	JWhileStatement newSelf = new JWhileStatement(self.getTokenReference(), self.cond(), newBody, self.getComments());
	this.getStack().push(newSelf);
}

/** Visits the given try-catch statement. */
public void visitTryCatchStatement(/* @non_null */JTryCatchStatement self) {
	self.tryClause().accept(this);
	JBlock newTryClause = (JBlock) this.getStack().pop();

	JCatchClause[] catchs = new JCatchClause[self.catchClauses().length];
	for (int j = 0; j < self.catchClauses().length; j++) {
		JCatchClause catchClause = self.catchClauses()[j];
		catchClause.accept(this);
		catchs[j] = (JCatchClause) this.getStack().pop();
	}

	JTryCatchStatement newSelf = new JTryCatchStatement(self.getTokenReference(), newTryClause, catchs, self.getComments());
	this.getStack().push(newSelf);
}

/** Visits the given try-finally statement. */
public void visitTryFinallyStatement(/* @non_null */JTryFinallyStatement self) {
	self.tryClause().accept(this);
	JBlock newTryClause = (JBlock) this.getStack().pop();

	self.finallyClause().accept(this);
	JBlock newFinallyClause = (JBlock) this.getStack().pop();

	JTryFinallyStatement newSelf = new JTryFinallyStatement(self.getTokenReference(), newTryClause, newFinallyClause, self.getComments());
	this.getStack().push(newSelf);
}

/** Visits the given synchronized statement. */
public void visitSynchronizedStatement(/* @non_null */JSynchronizedStatement self) {
	self.body().accept(this);
	JBlock newBody = (JBlock) this.getStack().pop();

	JSynchronizedStatement newSelf = new JSynchronizedStatement(self.getTokenReference(), self.cond(), newBody, self.getComments());
	this.getStack().push(newSelf);
}

/** Visits the given switch statement. */
public void visitSwitchStatement(/* @non_null */JSwitchStatement self) {
	JSwitchGroup[] switchGroups = new JSwitchGroup[self.groups().length];
	for (int j = 0; j < self.groups().length; j++) {
		JSwitchGroup catchClause = self.groups()[j];
		catchClause.accept(this);
		switchGroups[j] = (JSwitchGroup) this.getStack().pop();
	}

	JSwitchStatement newSelf = new JSwitchStatement(self.getTokenReference(), self.expr(), switchGroups, self.getComments());
	this.getStack().push(newSelf);
}

@Override
public void visitSwitchGroup(JSwitchGroup self) {
	JStatement[] theStatements = self.getStatements();
	JStatement[] theNewStatements = new JStatement[theStatements.length];
	for (int j = 0; j < theStatements.length; j++){
		JStatement theCurrentStatement = theStatements[j];
		theCurrentStatement.accept(this);
		theNewStatements[j] = (JStatement) this.getStack().pop();
	}
	this.getStack().push(new JSwitchGroup(self.getTokenReference(), self.labels(), theNewStatements));
}


/** Visits the given if statement. */
public void visitIfStatement(/* @non_null */JIfStatement self) {
	this.getStack().push(self);
	self.thenClause().accept(this);
	JStatement newThen = (JStatement) this.getStack().pop();
	JStatement newElse = null;
	if (self.elseClause() != null) {
		self.elseClause().accept(this);
		newElse = (JStatement) this.getStack().pop();
	}

	this.getStack().push(new JIfStatement(self.getTokenReference(), self.cond(), newThen, newElse, self.getComments()));
}

/** Visits the given for statement. */
public void visitForStatement(/* @non_null */JForStatement self) {

	JStatement newInit = null;
	if (self.init() != null){
		self.init().accept(this);
		newInit = (JStatement) this.getStack().pop();
	}
	JStatement newIncr = null;
	if (self.incr() != null) {
		self.incr().accept(this);
		newIncr = (JStatement) this.getStack().pop();
	}
	self.body().accept(this);
	JStatement newBody = (JStatement) this.getStack().pop();

	this.getStack().push(new JForStatement(self.getTokenReference(), newInit, self.cond(), newIncr, newBody, self.getComments()));
}

/** Visits the given compound statement. */
public void visitCompoundStatement(/* @non_null */JCompoundStatement self) {
	JStatement[] newStatements = new JStatement[self.body().length];
	for (int i = 0; i < self.body().length; i++) {
		self.body()[i].accept(this);
		newStatements[i] = (JStatement) this.getStack().pop();
	}
	this.getStack().push(new JCompoundStatement(self.getTokenReference(), newStatements));
}

/** Visits the given compound statement. */
public void visitCompoundStatement(/* @non_null */JStatement[] body) {
	JStatement[] newBody = new JStatement[body.length];
	for (int i = 0; i < body.length; i++) {
		body[i].accept(this);
		newBody[i] = (JStatement) this.getStack().pop();
	}
	this.getStack().push(newBody);
}

/** Visits the given do statement. */
public void visitDoStatement(/* @non_null */JDoStatement self) {

	self.body().accept(this);

	this.getStack().push(new JDoStatement(self.getTokenReference(), self.cond(), (JStatement) this.getStack().pop(), self.getComments()));

}

/** Visits the given block statement. */
public void visitBlockStatement(/* @non_null */JBlock self) {
	JStatement[] newBody = new JStatement[self.body().length];
	for (int i = 0; i < self.body().length; i++) {
		self.body()[i].accept(this);
		newBody[i] = (JStatement) this.getStack().pop();
	}
	this.getStack().push(new JBlock(self.getTokenReference(), newBody, self.getComments()));
}

/** Visits the given constructor block. */
public void visitConstructorBlock(/* @non_null */JConstructorBlock self) {
	JStatement[] newBody;
	if (self.body() != null) {
		JBlock block = ASTUtils.createBlockStatement(self.body());
		block.accept(this);
		JBlock newBlock = (JBlock) this.getStack().pop();
		newBody = newBlock.body();

	} else {
		newBody = null;
	}
	this.getStack().push(new JConstructorBlock(self.getTokenReference(), newBody));
}

@Override
public void visitCatchClause(JCatchClause self) {
	JStatement[] newBody = new JStatement[self.body().body().length];
	for (int i = 0; i < self.body().body().length; i++) {
		self.body().body()[i].accept(this);
		newBody[i] = (JStatement) this.getStack().pop();
	}
	JBlock newJBlock = new JBlock(self.body().getTokenReference(), newBody, self.body().getComments());
	this.getStack().push(new JCatchClause(self.getTokenReference(), self.exception(), newJBlock));
}

@Override
public void visitJmlInvariant(JmlInvariant self) {
	JmlInvariant newSelf = new JmlInvariant(self.getTokenReference(), self.modifiers(), self.isRedundantly(), self.predicate());
	this.getStack().push(newSelf);
}



// ----------------------------------------------------------------------
// NOT RECURSIVES
// ----------------------------------------------------------------------
/** Visits the given variable declaration statement. */
public void visitVariableDeclarationStatement(/* @non_null */JVariableDeclarationStatement self) {
	this.getStack().push(self);

}

/** Visits the given variable declaration statement. */
public void visitVariableDefinition(/* @non_null */JVariableDefinition self) {
	this.getStack().push(self);
}

/** Visits the given assert statement. */
public void visitAssertStatement(/* @non_null */JAssertStatement self) {
	this.getStack().push(self);
}

/** Visits the given return statement. */
public void visitReturnStatement(/* @non_null */JReturnStatement self) {
	this.getStack().push(self);
}

/** Visits the given labeled statement. */
public void visitLabeledStatement(/* @non_null */JLabeledStatement self) {
	this.getStack().push(self);
}

/** Visits the given expression statement. */
public void visitExpressionStatement(/* @non_null */JExpressionStatement self) {
	this.getStack().push(self);
}

/** Visits the given expression list statement. */
public void visitExpressionListStatement(/* @non_null */JExpressionListStatement self) {
	this.getStack().push(self);
}

/** Visits the given empty statement. */
public void visitEmptyStatement(/* @non_null */JEmptyStatement self) {
	this.getStack().push(self);
}

/** Visits the given throw statement. */
public void visitThrowStatement(/* @non_null */JThrowStatement self) {
	this.getStack().push(self);
}

/** Visits the given continue statement. */
public void visitContinueStatement(/* @non_null */JContinueStatement self) {
	this.getStack().push(self);
}

/** Visits the given break statement. */
public void visitBreakStatement(/* @non_null */JBreakStatement self) {
	this.getStack().push(self);
}

@Override
public void visitJmlAssertStatement(JmlAssertStatement self) {
	this.getStack().push(self);
}

@Override
public void visitJmlAssumeStatement(JmlAssumeStatement self) {
	this.getStack().push(self);
}

@Override
public void visitJmlLoopStatement(JmlLoopStatement self) {
	this.getStack().push(self);
}

@Override
public void visitJmlSetStatement(JmlSetStatement self) {
	this.getStack().push(self);
}

@Override
public void visitJmlAssignableClause(JmlAssignableClause self) {
	this.getStack().push(self);
}

@Override
public void visitJmlFieldDeclaration(JmlFieldDeclaration self) {
	this.getStack().push(self);
}

// ----------------------------------------------------------------------
// NOT IMPLEMENTED
// ----------------------------------------------------------------------

/** Visits the given class block. */
public void visitClassBlock(/* @non_null */JClassBlock self) {
	throw new TacoNotImplementedYetException();
}

/** Visits the given type declaration statement. */
public void visitTypeDeclarationStatement(/* @non_null */JTypeDeclarationStatement self) {
	throw new TacoNotImplementedYetException();
}

@Override
protected Object clone() throws CloneNotSupportedException {
	// TODO Auto-generated method stub
	return super.clone();
}


}
