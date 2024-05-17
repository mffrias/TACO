package ar.edu.taco.utils.jml;

import java.util.*;

import ar.edu.jdynalloy.factory.JSignatureFactory;
import ar.edu.jdynalloy.xlator.JType;
//import com.sun.jmx.remote.internal.ArrayQueue;
//import org.jmlspecs.checker.JmlAssertStatement;
//import org.jmlspecs.checker.JmlAssignableClause;
//import org.jmlspecs.checker.JmlAssignmentStatement;
//import org.jmlspecs.checker.JmlAssumeStatement;
//import org.jmlspecs.checker.JmlClassDeclaration;
//import org.jmlspecs.checker.JmlCompilationUnit;
//import org.jmlspecs.checker.JmlConstraint;
//import org.jmlspecs.checker.JmlConstructorDeclaration;
//import org.jmlspecs.checker.JmlEnsuresClause;
//import org.jmlspecs.checker.JmlExceptionalBehaviorSpec;
//import org.jmlspecs.checker.JmlExceptionalSpecBody;
//import org.jmlspecs.checker.JmlExceptionalSpecCase;
//import org.jmlspecs.checker.JmlFieldDeclaration;
//import org.jmlspecs.checker.JmlGeneralSpecCase;
//import org.jmlspecs.checker.JmlGenericSpecBody;
//import org.jmlspecs.checker.JmlGenericSpecCase;
//import org.jmlspecs.checker.JmlInformalExpression;
//import org.jmlspecs.checker.JmlInterfaceDeclaration;
//import org.jmlspecs.checker.JmlInvariant;
//import org.jmlspecs.checker.JmlLoopStatement;
//import org.jmlspecs.checker.JmlMethodDeclaration;
//import org.jmlspecs.checker.JmlMethodSpecification;
//import org.jmlspecs.checker.JmlNormalBehaviorSpec;
//import org.jmlspecs.checker.JmlNormalSpecBody;
//import org.jmlspecs.checker.JmlNormalSpecCase;
//import org.jmlspecs.checker.JmlPredicate;
//import org.jmlspecs.checker.JmlRefinePrefix;
//import org.jmlspecs.checker.JmlRepresentsDecl;
//import org.jmlspecs.checker.JmlRequiresClause;
//import org.jmlspecs.checker.JmlSetStatement;
//import org.jmlspecs.checker.JmlSignalsClause;
//import org.jmlspecs.checker.JmlSignalsOnlyClause;
//import org.jmlspecs.checker.JmlSourceMethod;
//import org.jmlspecs.checker.JmlSpecBodyClause;
//import org.jmlspecs.checker.JmlSpecCase;
//import org.jmlspecs.checker.JmlSpecification;
//import ar.edu.taco.TacoConfigurator;
import ar.edu.taco.TacoConfigurator;
import ar.edu.taco.TacoException;
//import com.sun.org.apache.xalan.internal.xsltc.compiler.util.StringStack;
import ar.edu.taco.TacoNotImplementedYetException;
import ar.edu.taco.jml.loop.LastStatementCollector;
import ar.edu.taco.jml.loop.WhileBlockVisitor;
import ar.edu.taco.jml.utils.ASTUtils;
import org.jmlspecs.checker.*;
import org.jmlspecs.checker.Constants;
import org.multijava.mjc.*;
//import org.multijava.util.compiler.JavaStyleComment;
//import org.multijava.util.compiler.TokenReference;

//import ar.edu.taco.TacoException;
//import ar.edu.taco.TacoNotImplementedYetException;
//import ar.edu.taco.jml.utils.ASTUtils;
import ar.edu.taco.simplejml.JmlBaseVisitor;
import ar.edu.taco.simplejml.builtin.JMLAuxiliaryConstantsFactory;
import ar.edu.taco.simplejml.builtin.JMLAuxiliaryConstantsFactory.JMLMultAuxiliaryConstants;
import ar.edu.taco.simplejml.helpers.CTypeAdapter;
import ar.edu.taco.simplejml.helpers.ExpressionSolver;
import ar.uba.dc.rfm.alloy.AlloyVariable;
import ar.uba.dc.rfm.alloy.ast.expressions.AlloyExpression;

import org.multijava.util.compiler.JavaStyleComment;
import org.multijava.util.compiler.TokenReference;
import org.multijava.util.compiler.UnpositionedError;

public class JmlAstDeterminizerVisitor extends JmlBaseVisitor {

	//private Stack<Object> stack = new Stack<Object>();
	private Queue<Object> theQueue = new LinkedList<Object>();

	private boolean hasBeenSplit = false;

	public Queue<Object> getQueue() {
		return theQueue;
	}

	public boolean isSplit() {
		return hasBeenSplit;
	}

	public void setIsSplit(boolean b) {
		this.hasBeenSplit = b;
	}

	@Override
	public void visitJmlCompilationUnit(JmlCompilationUnit self) {

		TokenReference where = self.getTokenReference();
		JPackageName package_name = self.packageName();

		CCompilationUnit export = null;//new CCompilationUnit(self.packageNameAsString());

		JPackageImportType[] imported_packages = self.importedPackages();

		@SuppressWarnings("rawtypes")
		ArrayList imported_units = new ArrayList();
		Collections.addAll(imported_units, self.importedUnits());
		JTypeDeclarationType[] typeDeclarations = self.typeDeclarations();
		JTypeDeclarationType[] new_type_declarationsFirst = new JTypeDeclarationType[typeDeclarations.length];
		JTypeDeclarationType[] new_type_declarationsSecond = new JTypeDeclarationType[typeDeclarations.length];

		for (int i = 0; i < typeDeclarations.length; i++) {
			typeDeclarations[i].accept(this);
			JTypeDeclarationType ret_val = (JTypeDeclarationType) this.getQueue().poll();
			JTypeDeclarationType cloned_type_declaration_first = ret_val;
			new_type_declarationsFirst[i] = cloned_type_declaration_first;
			ret_val = (JTypeDeclarationType) this.getQueue().poll();
			JTypeDeclarationType cloned_type_declaration_second = ret_val;
			new_type_declarationsSecond[i] = cloned_type_declaration_second;
		}

		@SuppressWarnings("rawtypes")
		ArrayList<JmlMethodDeclaration> top_level_methods = self.tlMethods();
		JmlRefinePrefix refinePrefix = self.refinePrefix();
		JmlCompilationUnit compilationUnitFirst = new JmlCompilationUnit(
				where, package_name, export, imported_packages, imported_units, new_type_declarationsFirst,
				top_level_methods, refinePrefix
				);
		this.getQueue().offer(compilationUnitFirst);

		JmlCompilationUnit compilationUnitSecond = new JmlCompilationUnit(
				where, package_name, export, imported_packages, imported_units, new_type_declarationsSecond,
				top_level_methods, refinePrefix
				);
		this.getQueue().offer(compilationUnitSecond);
	}


	@Override
	public void visitJmlClassDeclaration(JmlClassDeclaration self) {

		JPhylum[] newFieldsAndInitFirst = new JPhylum[self.fieldsAndInits().length];
		JPhylum[] newFieldsAndInitSecond = new JPhylum[self.fieldsAndInits().length];
		for (int i = 0; i < self.fieldsAndInits().length; i++) {
			if (self.fieldsAndInits()[i] instanceof JClassBlock) {
				newFieldsAndInitFirst[i] = self.fieldsAndInits()[i];
				newFieldsAndInitSecond[i] = self.fieldsAndInits()[i];
			} else {
				if (self.fieldsAndInits()[i] instanceof JFieldDeclarationType) {
					if (!(self.fieldsAndInits()[i] instanceof JmlFieldDeclaration)) {
						throw new TacoException("Simplifier error: Must be a JMLField");
					}
					JmlFieldDeclaration jFieldDeclarationType = (JmlFieldDeclaration) self.fieldsAndInits()[i];
					jFieldDeclarationType.accept(this);
					JPhylum newFieldDeclarationTypeFirst = (JPhylum) this.getQueue().poll();
					newFieldsAndInitFirst[i] = newFieldDeclarationTypeFirst;

					JPhylum newFieldDeclarationTypeSecond = (JPhylum) this.getQueue().poll();
					newFieldsAndInitSecond[i] = newFieldDeclarationTypeSecond;
				}
			}
		}

		ArrayList<JmlMethodDeclaration> newMethodsFirst = new ArrayList<JmlMethodDeclaration>();
		ArrayList<JmlMethodDeclaration> newMethodsSecond = new ArrayList<JmlMethodDeclaration>();
		for (JmlMethodDeclaration methodDeclaration : (ArrayList<JmlMethodDeclaration>) self.methods()) {
			// break here for detection
			String currentMethodName = this.getClassName(self) + methodDeclaration.ident();
			String methodToCheck = TacoConfigurator.getInstance().getMethodToCheck();
			methodToCheck = methodToCheck.substring(0, methodToCheck.indexOf('('));
			if (currentMethodName.equals(methodToCheck)) {
				methodDeclaration.accept(this);
				Object ret_val = this.getQueue().poll();
				newMethodsFirst.add((JmlMethodDeclaration) ret_val);
				ret_val = this.getQueue().poll();
				newMethodsSecond.add((JmlMethodDeclaration) ret_val);
				//				newMethodsFirst.add(methodDeclaration);
				//				newMethodsSecond.add(methodDeclaration);
			} else {
				//				newMethodsFirst.add(methodDeclaration);
				//				newMethodsSecond.add(methodDeclaration);
			}
		}

		List<JFieldDeclarationType> jModelFieldDeclarationTypeListFirst = null;
		List<JFieldDeclarationType> jModelFieldDeclarationTypeListSecond = null;

		if (self.getModelFields() != null) {
			jModelFieldDeclarationTypeListFirst = new ArrayList<JFieldDeclarationType>();
			jModelFieldDeclarationTypeListSecond = new ArrayList<JFieldDeclarationType>();
			for (JFieldDeclarationType jFieldDeclarationType : self.getModelFields()) {
				jFieldDeclarationType.accept(this);

				JFieldDeclarationType newFieldDeclarationTypeFirst = (JFieldDeclarationType) this.getQueue().poll();
				jModelFieldDeclarationTypeListFirst.add(newFieldDeclarationTypeFirst);

				JFieldDeclarationType newFieldDeclarationTypeSecond = (JFieldDeclarationType) this.getQueue().poll();
				jModelFieldDeclarationTypeListSecond.add(newFieldDeclarationTypeSecond);

			}

		}

		JmlInvariant[] jmlInvariantListFirst = null;
		JmlInvariant[] jmlInvariantListSecond = null;
		if (self.invariants() != null) {
			jmlInvariantListFirst = new JmlInvariant[self.invariants().length];
			jmlInvariantListSecond = new JmlInvariant[self.invariants().length];

			for (int i = 0; i < self.invariants().length; i++) {
				self.invariants()[i].accept(this);
				jmlInvariantListFirst[i] = (JmlInvariant) this.getQueue().poll();
				jmlInvariantListSecond[i] = (JmlInvariant) this.getQueue().poll();
			}
		}

		JmlRepresentsDecl[] jmlRepresentsDeclListFirst = null;
		JmlRepresentsDecl[] jmlRepresentsDeclListSecond= null;
		if (self.representsDecls() != null) {
			jmlRepresentsDeclListFirst = new JmlRepresentsDecl[self.representsDecls().length];
			jmlRepresentsDeclListSecond = new JmlRepresentsDecl[self.representsDecls().length];
			for (int i = 0; i < self.representsDecls().length; i++) {
				self.representsDecls()[i].accept(this);
				jmlRepresentsDeclListFirst[i] = (JmlRepresentsDecl) this.getQueue().poll();
				jmlRepresentsDeclListSecond[i] = (JmlRepresentsDecl) this.getQueue().poll();
			}
		}

		ArrayList<JmlClassDeclaration> newInnersFirst = null;
		ArrayList<JmlClassDeclaration> newInnersSecond = null;
		if (self.inners() != null) {
			newInnersFirst = new ArrayList<JmlClassDeclaration>();
			newInnersSecond = new ArrayList<JmlClassDeclaration>();
			for (JmlClassDeclaration innerClassDeclaration : (ArrayList<JmlClassDeclaration>) self.inners()) {
				innerClassDeclaration.accept(this);
				newInnersFirst.add((JmlClassDeclaration) this.getQueue().poll());
				newInnersSecond.add((JmlClassDeclaration) this.getQueue().poll());
			}
		}

		JmlConstraint[] newConstraintsFirst = null;
		JmlConstraint[] newConstraintsSecond = null;
		if (self.constraints() != null) {
			newConstraintsFirst = new JmlConstraint[self.constraints().length];
			newConstraintsSecond = new JmlConstraint[self.constraints().length];
			for (int i = 0; i < self.constraints().length; i++) {
				self.constraints()[i].accept(this);
				newConstraintsFirst[i] = (JmlConstraint) this.getQueue().poll();
				newConstraintsSecond[i] = (JmlConstraint) this.getQueue().poll();
			}
		}



		JmlClassDeclaration jmlClassDeclarationFirst = new JmlClassDeclarationExtension(self, jmlInvariantListFirst, jmlRepresentsDeclListFirst, newConstraintsFirst, newMethodsFirst, jModelFieldDeclarationTypeListFirst, newInnersFirst);
		jmlClassDeclarationFirst.setFields(newFieldsAndInitFirst);

		JmlClassDeclaration jmlClassDeclarationSecond = new JmlClassDeclarationExtension(self, jmlInvariantListSecond, jmlRepresentsDeclListSecond, newConstraintsSecond, newMethodsSecond, jModelFieldDeclarationTypeListSecond, newInnersSecond);
		jmlClassDeclarationFirst.setFields(newFieldsAndInitSecond);

		this.getQueue().offer(jmlClassDeclarationFirst);
		this.getQueue().offer(jmlClassDeclarationSecond);
	}


	@Override
	public void visitJmlFieldDeclaration(JmlFieldDeclaration self) {
		String fieldNameFirst = self.variable().ident();
		JVariableDefinition newVarDefiFirst = new JVariableDefinition(self.getTokenReference(), self.variable().modifiers(), self.variable().getType(), fieldNameFirst, self.variable().expr());
		JmlFieldDeclaration selfFirst = JmlFieldDeclaration.makeInstance(self.getTokenReference(), newVarDefiFirst, self.javadocComment(), new JavaStyleComment[0], self.getCombinedVarAssertions(), null );

		String fieldNameSecond = self.variable().ident();
		JVariableDefinition newVarDefiSecond = new JVariableDefinition(self.getTokenReference(), self.variable().modifiers(), self.variable().getType(), fieldNameSecond, self.variable().expr());
		JmlFieldDeclaration selfSecond = JmlFieldDeclaration.makeInstance(self.getTokenReference(), newVarDefiSecond, self.javadocComment(), new JavaStyleComment[0], self.getCombinedVarAssertions(), null );

		this.getQueue().offer(selfFirst);
		this.getQueue().offer(selfSecond);
	}


	@Override
	public void visitJmlRepresentsDecl(JmlRepresentsDecl self) {
		JmlRepresentsDecl newSelfFirst = new JmlRepresentsDecl(self.getTokenReference(), self.modifiers(), self.isRedundantly(), self.storeRef(), self.predicate());
		JmlRepresentsDecl newSelfSecond = new JmlRepresentsDecl(self.getTokenReference(), self.modifiers(), self.isRedundantly(), self.storeRef(), self.predicate());
		this.getQueue().offer(newSelfFirst);
		this.getQueue().offer(newSelfSecond);
	}


	@Override
	public void visitJmlConstraint(JmlConstraint self) {
		JmlConstraint newSelfFirst = new JmlConstraint(self.getTokenReference(), self.modifiers(), self.isRedundantly(), self.predicate(), self.methodNames());
		JmlConstraint newSelfSecond = new JmlConstraint(self.getTokenReference(), self.modifiers(), self.isRedundantly(), self.predicate(), self.methodNames());
		this.getQueue().offer(newSelfFirst);
		this.getQueue().offer(newSelfSecond);
	}


	@Override
	public void visitJmlInterfaceDeclaration(JmlInterfaceDeclaration self) {
		this.getQueue().offer(self);
		this.getQueue().offer(self);
	}



	@Override
	public void visitJmlMethodDeclaration(JmlMethodDeclaration self) {

		JBlock newBodyFirst = null;
		JBlock newBodySecond = null;
		if (self.body() != null) {
			self.body().accept(this);
			newBodyFirst = (JBlock) this.getQueue().poll();
			newBodySecond = (JBlock) this.getQueue().poll();
		}

		JmlMethodSpecification methodSpecificationFirst = null;
		JmlMethodSpecification methodSpecificationSecond = null;
		if (self.hasSpecification()) {
			self.methodSpecification().accept(this);
			methodSpecificationFirst = (JmlMethodSpecification) this.getQueue().poll();
			methodSpecificationSecond = (JmlMethodSpecification) this.getQueue().poll();
		} 

		//		JmlSpecCase[] specCases = null;
		//		if (methodSpecificationFirst != null) {
		//			specCases = self.methodSpecification().specCases();
		//			for (int x = 0; x < methodSpecificationFirst.specCases().length; x++) {
		//				specCases[x] = methodSpecificationFirst.specCases()[x];
		//			}
		//		}
		//		if (methodSpecificationSecond != null) {
		//			specCases = self.methodSpecification().specCases();
		//			for (int x = 0; x < methodSpecificationSecond.specCases().length; x++) {
		//				specCases[x] = methodSpecificationSecond.specCases()[x];
		//			}
		//		}


		JmlMethodDeclaration newMethodDeclFirst = JmlMethodDeclaration.makeInstance(self.getTokenReference(), self.modifiers(), self.typevariables(), self.returnType(), self.ident(), self.parameters(), self.getExceptions(), newBodyFirst, self.javadocComment(), new JavaStyleComment[0], methodSpecificationFirst);
		this.getQueue().offer(newMethodDeclFirst);

		JmlMethodDeclaration newMethodDeclSecond = JmlMethodDeclaration.makeInstance(self.getTokenReference(), self.modifiers(), self.typevariables(), self.returnType(), self.ident(), self.parameters(), self.getExceptions(), newBodySecond, self.javadocComment(), new JavaStyleComment[0], methodSpecificationSecond);
		this.getQueue().offer(newMethodDeclSecond);
	}


	@Override
	public void visitJmlConstructorDeclaration(JmlConstructorDeclaration self) {

		JConstructorBlock newBodyFirst = null;
		JConstructorBlock newBodySecond = null;
		if (self.body() != null) {
			self.body().accept(this);
			newBodyFirst = (JConstructorBlock) this.getQueue().poll();
			newBodySecond = (JConstructorBlock) this.getQueue().poll();
		}

		JmlMethodSpecification methodSpecificationFirst = null;
		JmlMethodSpecification methodSpecificationSecond = null;
		if (self.hasSpecification()) {
			self.methodSpecification().accept(this);
			methodSpecificationFirst = (JmlMethodSpecification) this.getQueue().poll();
			methodSpecificationSecond = (JmlMethodSpecification) this.getQueue().poll();
		}

		JmlConstructorDeclaration jmlConstructorDeclarationFirst = JmlConstructorDeclaration.makeInstance(self.getTokenReference(), self.modifiers(), self.ident(),
				self.parameters(), self.getExceptions(), newBodyFirst, self.javadocComment(), new JavaStyleComment[0], methodSpecificationFirst);
		this.getQueue().offer(jmlConstructorDeclarationFirst);

		JmlConstructorDeclaration jmlConstructorDeclarationSecond = JmlConstructorDeclaration.makeInstance(self.getTokenReference(), self.modifiers(), self.ident(),
				self.parameters(), self.getExceptions(), newBodySecond, self.javadocComment(), new JavaStyleComment[0], methodSpecificationSecond);
		this.getQueue().offer(jmlConstructorDeclarationFirst);
	}







	public void visitJmlSpecification(JmlSpecification self) {
		JmlSpecification newSelfFirst;
		JmlSpecification newSelfSecond;
		if (self.hasSpecCases()) {
			List<JmlSpecCase> jmlSpecCasesFirst = new ArrayList<JmlSpecCase>();
			List<JmlSpecCase> jmlSpecCasesSecond = new ArrayList<JmlSpecCase>();

			for (JmlSpecCase jmlSpecCase : self.specCases()) {
				jmlSpecCase.accept(this);
				jmlSpecCasesFirst.add((JmlSpecCase) this.getQueue().poll());
				jmlSpecCasesSecond.add((JmlSpecCase) this.getQueue().poll());

			}
			newSelfFirst = new JmlSpecification(self.getTokenReference(), jmlSpecCasesFirst.toArray(new JmlSpecCase[0]), self.redundantSpec());
			newSelfSecond = new JmlSpecification(self.getTokenReference(), jmlSpecCasesSecond.toArray(new JmlSpecCase[0]), self.redundantSpec());
		} else {
			newSelfFirst = new JmlSpecification(self.getTokenReference(), self.redundantSpec());
			newSelfSecond = new JmlSpecification(self.getTokenReference(), self.redundantSpec());
		}
		this.getQueue().offer(newSelfFirst);
		this.getQueue().offer(newSelfSecond);
	}


	public void visitJmlAssignmentStatement(JmlAssignmentStatement self) {

		self.assignmentStatement().accept(this);
		JExpressionStatement newExpressionStatementFirst = (JExpressionStatement) this.getQueue().poll();
		JExpressionStatement newExpressionStatementSecond = (JExpressionStatement) this.getQueue().poll();

		JmlAssignmentStatement newAssignamentStatementFirst = new JmlAssignmentStatement(newExpressionStatementFirst);
		this.getQueue().offer(newAssignamentStatementFirst);

		JmlAssignmentStatement newAssignamentStatementSecond = new JmlAssignmentStatement(newExpressionStatementSecond);
		this.getQueue().offer(newAssignamentStatementSecond);
	}


	public void visitJmlGenericSpecBody(JmlGenericSpecBody self) {
		List<JmlSpecBodyClause> specClausesFirst;
		List<JmlSpecBodyClause> specClausesSecond;
		JmlGenericSpecBody newSelfFirst;
		JmlGenericSpecBody newSelfSecond;
		if (self.isSpecClauses()) {
			specClausesFirst = new ArrayList<JmlSpecBodyClause>();
			specClausesSecond = new ArrayList<JmlSpecBodyClause>();
			for (JmlSpecBodyClause jmlSpecBodyClause : self.specClauses()) {
				jmlSpecBodyClause.accept(this);
				specClausesFirst.add((JmlSpecBodyClause) this.getQueue().poll());
				specClausesSecond.add((JmlSpecBodyClause) this.getQueue().poll());
			}
			newSelfFirst = new JmlGenericSpecBody(specClausesFirst.toArray(new JmlSpecBodyClause[0]));
			newSelfSecond = new JmlGenericSpecBody(specClausesSecond.toArray(new JmlSpecBodyClause[0]));
		} else {
			newSelfFirst = self;
			newSelfSecond = self;
		}
		this.getQueue().offer(newSelfFirst);
		this.getQueue().offer(newSelfSecond);
	}



	public void visitJmlGenericSpecCase(JmlGenericSpecCase self) {
		List<JmlRequiresClause> jmlSpecHeaderFirst = new ArrayList<JmlRequiresClause>();
		List<JmlRequiresClause> jmlSpecHeaderSecond = new ArrayList<JmlRequiresClause>();
		JmlGenericSpecBody jmlSpecBodyFirst = null;
		JmlGenericSpecBody jmlSpecBodySecond = null;

		if (self.hasSpecHeader()) {
			for (JmlRequiresClause jmlRequiresClause : self.specHeader()) {
				jmlRequiresClause.accept(this);
				jmlSpecHeaderFirst.add((JmlRequiresClause) this.getQueue().poll());
				jmlSpecHeaderSecond.add((JmlRequiresClause) this.getQueue().poll());
			}
		}

		if (self.hasSpecBody()) {
			self.specBody().accept(this);
			jmlSpecBodyFirst = (JmlGenericSpecBody) this.getQueue().poll();
			jmlSpecBodySecond = (JmlGenericSpecBody) this.getQueue().poll();
		}

		JmlGenericSpecCase newSelfFirst = new JmlGenericSpecCase(self.getTokenReference(), self.specVarDecls(), jmlSpecHeaderFirst.toArray(new JmlRequiresClause[0]), jmlSpecBodyFirst);
		this.getQueue().offer(newSelfFirst);

		JmlGenericSpecCase newSelfSecond = new JmlGenericSpecCase(self.getTokenReference(), self.specVarDecls(), jmlSpecHeaderSecond.toArray(new JmlRequiresClause[0]), jmlSpecBodySecond);
		this.getQueue().offer(newSelfSecond);
	}


	@Override
	public void visitJmlExceptionalBehaviorSpec(JmlExceptionalBehaviorSpec self) {
		self.specCase().accept(this);

		JmlGeneralSpecCase theExceptionalBehaviorFirst = (JmlGeneralSpecCase) this.getQueue().poll();
		JmlGeneralSpecCase theExceptionalBehaviorSecond = (JmlGeneralSpecCase) this.getQueue().poll();


		JmlExceptionalBehaviorSpec newSelfFirst = new JmlExceptionalBehaviorSpec(self.getTokenReference(), self.privacy(), theExceptionalBehaviorFirst);
		this.getQueue().offer(newSelfFirst);

		JmlExceptionalBehaviorSpec newSelfSecond = new JmlExceptionalBehaviorSpec(self.getTokenReference(), self.privacy(), theExceptionalBehaviorSecond);
		this.getQueue().offer(newSelfFirst);
	}


	@Override
	public void visitJmlExceptionalSpecBody(JmlExceptionalSpecBody self) {
		List<JmlSpecBodyClause> specClausesFirst;
		List<JmlSpecBodyClause> specClausesSecond;
		if (self.isSpecClauses()) {
			specClausesFirst = new ArrayList<JmlSpecBodyClause>();
			specClausesSecond = new ArrayList<JmlSpecBodyClause>();
			for (JmlSpecBodyClause jmlSpecBodyClause : self.specClauses()) {
				jmlSpecBodyClause.accept(this);
				specClausesFirst.add((JmlSpecBodyClause) this.getQueue().poll());
				specClausesSecond.add((JmlSpecBodyClause) this.getQueue().poll());
			}
		} else {
			specClausesFirst = null;
			specClausesSecond = null;
		}
		JmlExceptionalSpecBody newSelfFirst = new JmlExceptionalSpecBody(specClausesFirst.toArray(new JmlSpecBodyClause[0]));
		JmlExceptionalSpecBody newSelfSecond = new JmlExceptionalSpecBody(specClausesSecond.toArray(new JmlSpecBodyClause[0]));
		this.getQueue().offer(newSelfFirst);
		this.getQueue().offer(newSelfSecond);
	}


	@Override
	public void visitJmlExceptionalSpecCase(JmlExceptionalSpecCase self) {
		List<JmlRequiresClause> jmlSpecHeaderFirst = new ArrayList<JmlRequiresClause>();
		List<JmlRequiresClause> jmlSpecHeaderSecond = new ArrayList<JmlRequiresClause>();
		JmlExceptionalSpecBody jmlSpecBodyFirst = null;
		JmlExceptionalSpecBody jmlSpecBodySecond = null;

		if (self.hasSpecHeader()) {
			for (JmlRequiresClause jmlRequiresClause : self.specHeader()) {
				jmlRequiresClause.accept(this);
				jmlSpecHeaderFirst.add((JmlRequiresClause) this.getQueue().poll());
				jmlSpecHeaderSecond.add((JmlRequiresClause) this.getQueue().poll());
			}
		}

		if (self.hasSpecBody()) {
			self.specBody().accept(this);
			jmlSpecBodyFirst = (JmlExceptionalSpecBody) this.getQueue().poll();
			jmlSpecBodySecond = (JmlExceptionalSpecBody) this.getQueue().poll();

		}

		JmlExceptionalSpecCase newSelfFirst = new JmlExceptionalSpecCase(self.getTokenReference(), self.specVarDecls(), jmlSpecHeaderFirst
				.toArray(new JmlRequiresClause[0]), jmlSpecBodyFirst);
		JmlExceptionalSpecCase newSelfSecond = new JmlExceptionalSpecCase(self.getTokenReference(), self.specVarDecls(), jmlSpecHeaderSecond
				.toArray(new JmlRequiresClause[0]), jmlSpecBodySecond);

		this.getQueue().offer(newSelfFirst);
		this.getQueue().offer(newSelfSecond);
	}


	@Override
	public void visitJmlNormalBehaviorSpec(JmlNormalBehaviorSpec self) {
		self.specCase().accept(this);
		JmlNormalBehaviorSpec newSelfFirst = new JmlNormalBehaviorSpec(self.getTokenReference(), self.privacy(), (JmlGeneralSpecCase) this.getQueue().poll());
		JmlNormalBehaviorSpec newSelfSecond = new JmlNormalBehaviorSpec(self.getTokenReference(), self.privacy(), (JmlGeneralSpecCase) this.getQueue().poll());
		this.getQueue().offer(newSelfFirst);
		this.getQueue().offer(newSelfSecond);
	}


	@Override
	public void visitJmlNormalSpecBody(JmlNormalSpecBody self) {
		JmlNormalSpecBody newSelfFirst;
		JmlNormalSpecBody newSelfSecond;
		if (self.isSpecClauses()) {
			List<JmlSpecBodyClause> specClausesFirst;
			List<JmlSpecBodyClause> specClausesSecond;
			specClausesFirst = new ArrayList<JmlSpecBodyClause>();
			specClausesSecond = new ArrayList<JmlSpecBodyClause>();
			for (JmlSpecBodyClause jmlSpecBodyClause : self.specClauses()) {
				jmlSpecBodyClause.accept(this);
				specClausesFirst.add((JmlSpecBodyClause) this.getQueue().poll());
				specClausesSecond.add((JmlSpecBodyClause) this.getQueue().poll());
			}
			newSelfFirst = new JmlNormalSpecBody(specClausesFirst.toArray(new JmlSpecBodyClause[0]));
			newSelfSecond = new JmlNormalSpecBody(specClausesSecond.toArray(new JmlSpecBodyClause[0]));

		} else {
			List<JmlGeneralSpecCase> specClausesFirst;
			List<JmlGeneralSpecCase> specClausesSecond;
			specClausesFirst = new ArrayList<JmlGeneralSpecCase>();
			specClausesSecond = new ArrayList<JmlGeneralSpecCase>();
			for (JmlGeneralSpecCase jmlGeneralSpecCase : self.specCases()) {
				jmlGeneralSpecCase.accept(this);
				specClausesFirst.add((JmlGeneralSpecCase) this.getQueue().poll());
				specClausesSecond.add((JmlGeneralSpecCase) this.getQueue().poll());
			}
			newSelfFirst = new JmlNormalSpecBody(specClausesFirst.toArray(new JmlGeneralSpecCase[0]));
			newSelfSecond = new JmlNormalSpecBody(specClausesFirst.toArray(new JmlGeneralSpecCase[0]));
		}

		this.getQueue().offer(newSelfFirst);
		this.getQueue().offer(newSelfSecond);
	}


	@Override
	public void visitJmlNormalSpecCase(JmlNormalSpecCase self) {
		List<JmlRequiresClause> jmlSpecHeaderFirst = new ArrayList<JmlRequiresClause>();
		List<JmlRequiresClause> jmlSpecHeaderSecond = new ArrayList<JmlRequiresClause>();
		JmlNormalSpecBody jmlSpecBodyFirst = null;
		JmlNormalSpecBody jmlSpecBodySecond = null;

		if (self.hasSpecHeader()) {
			for (JmlRequiresClause jmlRequiresClause : self.specHeader()) {
				jmlRequiresClause.accept(this);
				jmlSpecHeaderFirst.add((JmlRequiresClause) this.getQueue().poll());
				jmlSpecHeaderSecond.add((JmlRequiresClause) this.getQueue().poll());
			}
		}

		if (self.hasSpecBody()) {
			self.specBody().accept(this);
			jmlSpecBodyFirst = (JmlNormalSpecBody) this.getQueue().poll();
			jmlSpecBodySecond = (JmlNormalSpecBody) this.getQueue().poll();
		}

		JmlNormalSpecCase newSelfFirst = new JmlNormalSpecCase(self.getTokenReference(), self.specVarDecls(), jmlSpecHeaderFirst.toArray(new JmlRequiresClause[0]),
				jmlSpecBodyFirst);
		this.getQueue().offer(newSelfFirst);

		JmlNormalSpecCase newSelfSecond = new JmlNormalSpecCase(self.getTokenReference(), self.specVarDecls(), jmlSpecHeaderSecond.toArray(new JmlRequiresClause[0]),
				jmlSpecBodySecond);
		this.getQueue().offer(newSelfSecond);

	}



	@Override
	public void visitJmlSignalsClause(JmlSignalsClause self) {
		if (self.isNotSpecified()) {
			throw new IllegalArgumentException("Signals clause is not specified.");
		}

		JmlSignalsClause newSelfFirst= new JmlSignalsClause(self.getTokenReference(), self.isRedundantly(), self.type(), self.ident(), self.predOrNot(), self.isNotSpecified());
		JmlSignalsClause newSelfSecond= new JmlSignalsClause(self.getTokenReference(), self.isRedundantly(), self.type(), self.ident(), self.predOrNot(), self.isNotSpecified());
		this.getQueue().offer(newSelfFirst);
		this.getQueue().offer(newSelfSecond);
	}


	@Override
	public void visitJmlSignalsOnlyClause(JmlSignalsOnlyClause self) {
		this.getQueue().offer(self);
		this.getQueue().offer(self);
	}

	public void visitJmlEnsuresClause(JmlEnsuresClause self) {
		if (self.isNotSpecified()) {
			throw new IllegalArgumentException("Ensures clause is not specified.");
		}


		JmlEnsuresClause newSelfFirst = new JmlEnsuresClause(self.getTokenReference(), self.isRedundantly(), self.predOrNot());
		JmlEnsuresClause newSelfSecond = new JmlEnsuresClause(self.getTokenReference(), self.isRedundantly(), self.predOrNot());
		this.getQueue().offer(newSelfFirst);
		this.getQueue().offer(newSelfSecond);
	}


	public void visitJmlRequiresClause(JmlRequiresClause self) {
		if (self.isNotSpecified()) {
			throw new IllegalArgumentException("Requires clause is not specified.");
		}

		JmlRequiresClause newSelfFirst = new JmlRequiresClause(self.getTokenReference(), self.isRedundantly(), self.predOrNot());
		JmlRequiresClause newSelfSecond = new JmlRequiresClause(self.getTokenReference(), self.isRedundantly(), self.predOrNot());
		this.getQueue().offer(newSelfFirst);
		this.getQueue().offer(newSelfSecond);
	}


	//	public void visitWhileStatement(/* @non_null */JWhileStatement self) {
	//		self.body().accept(this);
	//		JStatement newBody = (JStatement) this.getStack().pop();
	//		
	//
	//		JWhileStatement newSelf = new JWhileStatement(self.getTokenReference(), self.cond(), newBody, self.getComments());
	//		this.getStack().push(newSelf);
	//	}


	public void visitTryCatchStatement(/* @non_null */JTryCatchStatement self) {
		self.tryClause().accept(this);
		JBlock newTryClauseFirst = (JBlock) this.getQueue().poll();
		JBlock newTryClauseSecond = (JBlock) this.getQueue().poll();

		JCatchClause[] catchsFirst = new JCatchClause[self.catchClauses().length];
		JCatchClause[] catchsSecond = new JCatchClause[self.catchClauses().length];
		for (int j = 0; j < self.catchClauses().length; j++) {
			JCatchClause catchClause = self.catchClauses()[j];
			catchClause.accept(this);
			catchsFirst[j] = (JCatchClause) this.getQueue().poll();
			catchsSecond[j] = (JCatchClause) this.getQueue().poll();
		}

		JTryCatchStatement newSelfFirst = new JTryCatchStatement(self.getTokenReference(), newTryClauseFirst, catchsFirst, self.getComments());
		JTryCatchStatement newSelfSecond = new JTryCatchStatement(self.getTokenReference(), newTryClauseSecond, catchsSecond, self.getComments());
		this.getQueue().offer(newSelfFirst);
		this.getQueue().offer(newSelfSecond);
	}


	public void visitTryFinallyStatement(/* @non_null */JTryFinallyStatement self) {
		self.tryClause().accept(this);
		JBlock newTryClauseFirst = (JBlock) this.getQueue().poll();
		JBlock newTryClauseSecond = (JBlock) this.getQueue().poll();

		self.finallyClause().accept(this);
		JBlock newFinallyClauseFirst = (JBlock) this.getQueue().poll();
		JBlock newFinallyClauseSecond = (JBlock) this.getQueue().poll();

		JTryFinallyStatement newSelfFirst = new JTryFinallyStatement(self.getTokenReference(), newTryClauseFirst, newFinallyClauseFirst, self.getComments());
		this.getQueue().offer(newSelfFirst);

		JTryFinallyStatement newSelfSecond = new JTryFinallyStatement(self.getTokenReference(), newTryClauseSecond, newFinallyClauseSecond, self.getComments());
		this.getQueue().offer(newSelfSecond);
	}	


	public void visitSynchronizedStatement(/* @non_null */JSynchronizedStatement self) {
		self.body().accept(this);
		JBlock newBodyFirst = (JBlock) this.getQueue().poll();
		JBlock newBodySecond = (JBlock) this.getQueue().poll();

		JSynchronizedStatement newSelfFirst = new JSynchronizedStatement(self.getTokenReference(), self.cond(), newBodyFirst, self.getComments());
		JSynchronizedStatement newSelfSecond = new JSynchronizedStatement(self.getTokenReference(), self.cond(), newBodySecond, self.getComments());
		this.getQueue().offer(newSelfFirst);
		this.getQueue().offer(newSelfSecond);
	}



	@Override
	public void visitAssertStatement(JAssertStatement self) {
		JExpression expre = self.predicate();
		expre.accept(this);

		JExpression expreFirst = (JExpression) this.getQueue().poll();
		JExpression expreSecond = (JExpression) this.getQueue().poll();

		JAssertStatement selfFirst = new JAssertStatement(self.getTokenReference(), expreFirst, self.getComments());
		JAssertStatement selfSecond = new JAssertStatement(self.getTokenReference(), expreSecond, self.getComments());

		this.getQueue().offer(selfFirst);
		this.getQueue().offer(selfSecond);

	}


	/* (non-Javadoc)
	 * @see org.multijava.mjc.MjcVisitor#visitNameExpression(org.multijava.mjc.JNameExpression)
	 */
	@Override
	public void visitNameExpression(JNameExpression self) {
		String identFirst = self.getName();
		String identSecond = self.getName();

		JNameExpression selfFirst = new JNameExpression(self.getTokenReference(), identFirst);
		JNameExpression selfSecond = new JNameExpression(self.getTokenReference(), identSecond);

		this.getQueue().offer(selfFirst);
		this.getQueue().offer(selfSecond);

	}



	/* (non-Javadoc)
	 * @see org.jmlspecs.checker.JmlVisitor#visitJmlInvariant(org.jmlspecs.checker.JmlInvariant)
	 */
	@Override
	public void visitJmlInvariant(JmlInvariant self) {
		JmlPredicate pred = self.predicate();
		pred.accept(this);

		JmlPredicate predFirst = (JmlPredicate) this.getQueue().poll();
		JmlPredicate predSecond = (JmlPredicate) this.getQueue().poll();

		JmlInvariant newSelfFirst = new JmlInvariant(self.getTokenReference(), self.modifiers(), self.isRedundantly(), predFirst);
		JmlInvariant newSelfSecond = new JmlInvariant(self.getTokenReference(), self.modifiers(), self.isRedundantly(), predSecond);

		this.getQueue().offer(newSelfFirst);
		this.getQueue().offer(newSelfSecond);
	}


	/* (non-Javadoc)
	 * @see org.multijava.mjc.MjcVisitor#visitBooleanLiteral(org.multijava.mjc.JBooleanLiteral)
	 */
	@Override
	public void visitBooleanLiteral(JBooleanLiteral self) {
		JBooleanLiteral newSelfFirst = new JBooleanLiteral(self.getTokenReference(), self.booleanValue());
		JBooleanLiteral newSelfSecond = new JBooleanLiteral(self.getTokenReference(), self.booleanValue());

		this.getQueue().offer(newSelfFirst);
		this.getQueue().offer(newSelfSecond);
	}


	@Override
	public void visitParenthesedExpression(JParenthesedExpression self) {
		JExpression theExpre = self.expr();
		theExpre.accept(this);
		JExpression newExpreFirst = (JExpression) this.getQueue().poll();
		JExpression newExpreSecond = (JExpression) this.getQueue().poll();

		JParenthesedExpression selfFirst = new JParenthesedExpression(self.getTokenReference(), newExpreFirst);
		JParenthesedExpression selfSecond = new JParenthesedExpression(self.getTokenReference(), newExpreSecond);

		this.getQueue().offer(selfFirst);
		this.getQueue().offer(selfSecond);
	}


	@Override
	public void visitUnaryPromoteExpression(JUnaryPromote self) {
		JExpression theExpre = self.expr();
		theExpre.accept(this);
		JExpression newExpreFirst = (JExpression) this.getQueue().poll();
		JExpression newExpreSecond = (JExpression) this.getQueue().poll();

		JUnaryPromote selfFirst = new JUnaryPromote(newExpreFirst, self.getApparentType());
		JUnaryPromote selfSecond = new JUnaryPromote(newExpreSecond, self.getApparentType());

		this.getQueue().offer(selfFirst);
		this.getQueue().offer(selfSecond);

	}


	@Override
	public void visitJmlPredicate(JmlPredicate self) {
		JmlSpecExpression theExpre = self.specExpression();
		theExpre.accept(this);

		JmlSpecExpression theExpreFirst = (JmlSpecExpression) this.getQueue().poll();
		JmlSpecExpression theExpreSecond = (JmlSpecExpression) this.getQueue().poll();

		JmlPredicate selfFirst = new JmlPredicate(theExpreFirst);
		JmlPredicate selfSecond = new JmlPredicate(theExpreSecond);

		this.getQueue().offer(selfFirst);
		this.getQueue().offer(selfSecond);
	}


	/* (non-Javadoc)
	 * @see org.jmlspecs.checker.JmlVisitor#visitJmlSpecExpression(org.jmlspecs.checker.JmlSpecExpression)
	 */
	@Override
	public void visitJmlSpecExpression(JmlSpecExpression self) {
		JExpression expre = self.expression();
		expre.accept(this);

		JExpression expreFirst = (JExpression) this.getQueue().poll();
		JExpression expreSecond = (JExpression) this.getQueue().poll();

		JmlSpecExpression selfFirst = new JmlSpecExpression(expreFirst);
		JmlSpecExpression selfSecond = new JmlSpecExpression(expreSecond);

		this.getQueue().offer(selfFirst);
		this.getQueue().offer(selfSecond);
	}


	/* (non-Javadoc)
	 * @see org.multijava.mjc.MjcVisitor#visitConditionalAndExpression(org.multijava.mjc.JConditionalAndExpression)
	 */
	@Override
	public void visitConditionalAndExpression(JConditionalAndExpression self) {
		JExpression left = self.left();
		left.accept(this);

		JExpression leftFirst = (JExpression)this.getQueue().poll();
		JExpression leftSecond = (JExpression)this.getQueue().poll();

		JExpression right = self.right();
		right.accept(this);

		JExpression rightFirst = (JExpression)this.getQueue().poll();
		JExpression rightSecond = (JExpression)this.getQueue().poll();

		JConditionalAndExpression newSelfFirst = new JConditionalAndExpression(self.getTokenReference(), leftFirst, rightFirst);
		JConditionalAndExpression newSelfSecond = new JConditionalAndExpression(self.getTokenReference(), leftSecond, rightSecond);

		this.getQueue().offer(newSelfFirst);
		this.getQueue().offer(newSelfSecond);
	}


	private boolean itIsAWrappedReturn(JIfStatement self) {
		boolean elseIsNull = self.elseClause() == null;
		boolean thenIsJustReturn = self.thenClause() instanceof JReturnStatement;
		boolean condIsTrue = self.cond().isBooleanLiteral() && ((JBooleanLiteral)self.cond()).booleanValue() == true;
		return elseIsNull && thenIsJustReturn && condIsTrue;
	}
	
	public void visitIfStatement(/* @non_null */JIfStatement self) {
		JStatement FP = null;
		JStatement SP = null;
		if (hasBeenSplit || itIsAWrappedReturn(self)) {
			JmlAstClonerStatementVisitor cloner = new JmlAstClonerStatementVisitor();
			self.accept(cloner);
			this.getQueue().offer(cloner.getStack().pop());
			self.accept(cloner);
			this.getQueue().offer(cloner.getStack().pop());
		} else {
			hasBeenSplit = true;
			JStatement theThenBranchCode = self.thenClause();
			//No need to call recursively because we only want to split the code once.
			//			theThenBranchCode.accept(this);

			JStatement branchAssertThenCase = new JAssertStatement(self.getTokenReference(), self.cond(), self.getComments());
			JStatement[] FPBeforeBeingABlock = new JStatement[2];
			FPBeforeBeingABlock[0] = branchAssertThenCase;
			FPBeforeBeingABlock[1] = theThenBranchCode;
			FP = new JBlock(self.getTokenReference(), FPBeforeBeingABlock, self.getComments());

			JStatement theElseBranchCode = null;
			if (self.elseClause() != null) {
				theElseBranchCode = self.elseClause();
				//				theElseBranchCode.accept(this);
				JExpression theCondition = self.cond();
				JExpression theNegatedCondition = new JUnaryExpression(theCondition.getTokenReference(),Constants.OPE_LNOT, theCondition);
				JStatement branchAssertElseCase = new JAssertStatement(self.getTokenReference(),theNegatedCondition,self.getComments());
				JStatement[] SPBeforeBeingABlock = new JStatement[2];
				SPBeforeBeingABlock[0] = branchAssertElseCase;
				SPBeforeBeingABlock[1] = theElseBranchCode;
				SP = new JBlock(self.getTokenReference(), SPBeforeBeingABlock, self.getComments());
			} else {
				JExpression theCondition = self.cond();
				JExpression theNegatedCondition = new JUnaryExpression(theCondition.getTokenReference(), Constants.OPE_LNOT, theCondition);
				JStatement branchAssertElseCase = new JAssertStatement(self.getTokenReference(),theNegatedCondition,self.getComments());
				JStatement[] SPBeforeBeingABlock = new JStatement[1];
				SPBeforeBeingABlock[0] = branchAssertElseCase;
				SP = new JBlock(self.getTokenReference(), SPBeforeBeingABlock, self.getComments());

			}

			this.getQueue().offer(FP);
			this.getQueue().offer(SP);
		}

	}



	//Still have to consider For Statement


	public void visitCompoundStatement(/* @non_null */JCompoundStatement self) {
		JStatement[] newStatementsFirst = new JStatement[self.body().length];
		JStatement[] newStatementsSecond = new JStatement[self.body().length];

		for (int i = 0; i < self.body().length; i++) {
			self.body()[i].accept(this);
			newStatementsFirst[i] = (JStatement) this.getQueue().poll();
			newStatementsSecond[i] = (JStatement) this.getQueue().poll();
		}

		JCompoundStatement newSelfFirst = new JCompoundStatement(self.getTokenReference(), newStatementsFirst); 
		JCompoundStatement newSelfSecond = new JCompoundStatement(self.getTokenReference(), newStatementsSecond);
		this.getQueue().offer(newSelfFirst);
		this.getQueue().offer(newSelfSecond);
	}



	//Still have to consider Do While Statement


	public void visitBlockStatement(/* @non_null */JBlock self) {
		JStatement[] newBodyFirst = new JStatement[self.body().length];
		JStatement[] newBodySecond = new JStatement[self.body().length];

		for (int i = 0; i < self.body().length; i++) {
			self.body()[i].accept(this);
			newBodyFirst[i] = (JStatement) this.getQueue().poll();
			newBodySecond[i] = (JStatement) this.getQueue().poll();
		}
		this.getQueue().offer(new JBlock(self.getTokenReference(), newBodyFirst, self.getComments()));
		this.getQueue().offer(new JBlock(self.getTokenReference(), newBodySecond, self.getComments()));
	}

	public void visitExpressionStatement(/* @non_null */JExpressionStatement self) {
		self.expr().accept(this);
		JExpression newExpressionFirst = (JExpression)this.getQueue().poll();
		JExpression newExpressionSecond = (JExpression)this.getQueue().poll();

		JExpressionStatement newExpressionStatementFirst = new JExpressionStatement(self.getTokenReference(), newExpressionFirst, self.getComments());
		this.getQueue().offer(newExpressionStatementFirst);

		JExpressionStatement newExpressionStatementSecond = new JExpressionStatement(self.getTokenReference(), newExpressionSecond, self.getComments());
		this.getQueue().offer(newExpressionStatementSecond);
	}


	public void visitConstructorBlock(/* @non_null */JConstructorBlock self) {
		JStatement[] newBodyFirst = null;
		JStatement[] newBodySecond = null;
		if (self.body() != null) {
			JBlock block = ASTUtils.createBlockStatement(self.body());
			block.accept(this);
			JBlock newBlockFirst = (JBlock) this.getQueue().poll();
			JBlock newBlockSecond = (JBlock) this.getQueue().poll();
			newBodyFirst = newBlockFirst.body();
			newBodySecond = newBlockSecond.body();
		}

		this.getQueue().offer(new JConstructorBlock(self.getTokenReference(), newBodyFirst));
		this.getQueue().offer(new JConstructorBlock(self.getTokenReference(), newBodySecond));
	}


	@Override
	public void visitCatchClause(JCatchClause self) {
		JStatement[] newBodyFirst = new JStatement[self.body().body().length];
		JStatement[] newBodySecond = new JStatement[self.body().body().length];

		for (int i = 0; i < self.body().body().length; i++) {
			self.body().body()[i].accept(this);
			newBodyFirst[i] = (JStatement) this.getQueue().poll();
			newBodySecond[i] = (JStatement) this.getQueue().poll();
		}

		JBlock newJBlockFirst = new JBlock(self.body().getTokenReference(), newBodyFirst, self.body().getComments());
		JBlock newJBlockSecond = new JBlock(self.body().getTokenReference(), newBodySecond, self.body().getComments());

		this.getQueue().offer(new JCatchClause(self.getTokenReference(), self.exception(), newJBlockFirst));
		this.getQueue().offer(new JCatchClause(self.getTokenReference(), self.exception(), newJBlockSecond));
	}


	/* (non-Javadoc)
	 * @see org.multijava.mjc.MjcVisitor#visitFieldExpression(org.multijava.mjc.JClassFieldExpression)
	 */
	@Override
	public void visitFieldExpression(JClassFieldExpression self) {
		self.prefix().accept(this);
		JExpression expreFirst = (JExpression) this.getQueue().poll();
		JExpression expreSecond = (JExpression) this.getQueue().poll();

		CFieldAccessor theField = self.getField();

		String idFirst = self.ident();
		String idSecond = self.ident();

		JClassFieldExpression newSelfFirst = new JClassFieldExpression(self.getTokenReference(), expreFirst, idFirst, self.sourceName());
		newSelfFirst.setField(theField);
		newSelfFirst.setType(self.getApparentType());

		JClassFieldExpression newSelfSecond = new JClassFieldExpression(self.getTokenReference(), expreSecond, idSecond, self.sourceName());
		newSelfSecond.setField(theField);
		newSelfSecond.setType(self.getApparentType());


		this.getQueue().offer(newSelfFirst);
		this.getQueue().offer(newSelfSecond);
	}


	@Override
	public void visitArrayLengthExpression(JArrayLengthExpression self) {
		self.prefix().accept(this);

		JExpression expreFirst = (JExpression) this.getQueue().poll();
		JExpression expreSecond = (JExpression) this.getQueue().poll();

		JArrayLengthExpression newSelfFirst = new JArrayLengthExpression(self.getTokenReference(), expreFirst);
		JArrayLengthExpression newSelfSecond = new JArrayLengthExpression(self.getTokenReference(), expreSecond);
		this.getQueue().offer(newSelfFirst);
		this.getQueue().offer(newSelfSecond);
	}


	/** Visits the given minus expression. */
	public void visitMinusExpression(/* @non_null */JMinusExpression self) {

		self.left().accept(this);
		JExpression leftFirst = (JExpression) this.getQueue().poll();
		JExpression leftSecond = (JExpression) this.getQueue().poll();

		self.right().accept(this);
		JExpression rightFirst = (JExpression) this.getQueue().poll();
		JExpression rightSecond = (JExpression) this.getQueue().poll();

		JMinusExpression newSelfFirst = new JMinusExpression(self.getTokenReference(), leftFirst, rightFirst);
		JMinusExpression newSelfSecond = new JMinusExpression(self.getTokenReference(), leftSecond, rightSecond);

		this.getQueue().offer(newSelfFirst);
		this.getQueue().offer(newSelfSecond);
	}


	/** Visits the given minus expression. */
	public void visitAddExpression(/* @non_null */JAddExpression self) {

		self.left().accept(this);
		JExpression leftFirst = (JExpression) this.getQueue().poll();
		JExpression leftSecond = (JExpression) this.getQueue().poll();

		self.right().accept(this);
		JExpression rightFirst = (JExpression) this.getQueue().poll();
		JExpression rightSecond = (JExpression) this.getQueue().poll();

		JAddExpression newSelfFirst = new JAddExpression(self.getTokenReference(), leftFirst, rightFirst);
		JAddExpression newSelfSecond = new JAddExpression(self.getTokenReference(), leftSecond, rightSecond);

		this.getQueue().offer(newSelfFirst);
		this.getQueue().offer(newSelfSecond);
	}


	@Override
	public void visitMultExpression(JMultExpression self) {
		JExpression left = self.left();
		left.accept(this);
		JExpression leftFirst = (JExpression) this.getQueue().poll();
		JExpression leftSecond = (JExpression) this.getQueue().poll();

		JExpression right = self.right();
		right.accept(this);
		JExpression rightFirst = (JExpression) this.getQueue().poll();
		JExpression rightSecond = (JExpression) this.getQueue().poll();

		JMultExpression newSelfFirst = new JMultExpression(self.getTokenReference(), leftFirst, rightFirst);
		JMultExpression newSelfSecond = new JMultExpression(self.getTokenReference(), leftSecond, rightSecond);

		this.getQueue().offer(newSelfFirst);
		this.getQueue().offer(newSelfSecond);
	}

	@Override
	public void visitDivideExpression(JDivideExpression self) {
		JExpression left = self.left();
		left.accept(this);
		JExpression leftFirst = (JExpression) this.getQueue().poll();
		JExpression leftSecond = (JExpression) this.getQueue().poll();

		JExpression right = self.right();
		right.accept(this);
		JExpression rightFirst = (JExpression) this.getQueue().poll();
		JExpression rightSecond = (JExpression) this.getQueue().poll();

		JDivideExpression newSelfFirst = new JDivideExpression(self.getTokenReference(), leftFirst, rightFirst);
		JDivideExpression newSelfSecond = new JDivideExpression(self.getTokenReference(), leftSecond, rightSecond);

		this.getQueue().offer(newSelfFirst);
		this.getQueue().offer(newSelfSecond);

	}

	//Notice that we assume no splitting occurs when accepting left and right. 
	//Nevertheless, in order to preserve the invariant, we expect after accepting
	//there are 2 expressions in the queue for left and 2 in the queue for right.
	//We will build 2 copies pairing the first and second from the queues.
	public void visitAssignmentExpression(/* @non_null */JAssignmentExpression self) {
		self.left().accept(this);
		JExpression newLeftFirst = (JExpression)this.getQueue().poll();
		JExpression newLeftSecond = (JExpression)this.getQueue().poll();

		self.right().accept(this);
		JExpression newRightFirst = (JExpression)this.getQueue().poll();
		JExpression newRightSecond = (JExpression)this.getQueue().poll();

		JAssignmentExpression newSelfFirst = new JAssignmentExpression(self.getTokenReference(), newLeftFirst, newRightFirst);
		this.getQueue().offer(newSelfFirst);

		JAssignmentExpression newSelfSecond = new JAssignmentExpression(self.getTokenReference(), newLeftSecond, newRightSecond);
		this.getQueue().offer(newSelfSecond);
	}

	/** Visits the given array access expression. */
	public void visitArrayAccessExpression(/* @non_null */JArrayAccessExpression self) {
		self.accessor().accept(this);
		JExpression newAccesorFirst = (JExpression)this.getQueue().poll();
		JExpression newAccesorSecond = (JExpression)this.getQueue().poll();

		self.prefix().accept(this);
		JExpression newPrefixFirst = (JExpression)this.getQueue().poll();
		JExpression newPrefixSecond = (JExpression)this.getQueue().poll();

		JArrayAccessExpression newSelfFirst = new JArrayAccessExpressionExtension(self, newPrefixFirst, newAccesorFirst);
		this.getQueue().offer(newSelfFirst);

		JArrayAccessExpression newSelfSecond = new JArrayAccessExpressionExtension(self, newPrefixSecond, newAccesorSecond);
		this.getQueue().offer(newSelfSecond);
	}


	@Override
	public void visitArrayInitializer(JArrayInitializer self) {
		JExpression[] newElemsFirst = new JExpression[self.elems().length];
		JExpression[] newElemsSecond = new JExpression[self.elems().length];

		for (int i = 0; i < self.elems().length; i++) {
			JExpression expression = self.elems()[i];
			expression.accept(this);
			newElemsFirst[i] = (JExpression) this.getQueue().poll();
			newElemsSecond[i] = (JExpression) this.getQueue().poll();
		}

		JArrayInitializer newSelfFirst = new JArrayInitializer(self.getTokenReference(), newElemsFirst);
		this.getQueue().offer(newSelfFirst);

		JArrayInitializer newSelfSecond = new JArrayInitializer(self.getTokenReference(), newElemsSecond);
		this.getQueue().offer(newSelfSecond);
	}




	@Override
	public void visitNewArrayExpression(/* @non_null */JNewArrayExpression self) {

		JExpression[] newDimsFirst = new JExpression[self.dims().dims().length];
		JExpression[] newDimsSecond = new JExpression[self.dims().dims().length];
		for (int i = 0; i < self.dims().dims().length; i++) {
			JExpression expression = self.dims().dims()[i];
			if (expression == null) {
				newDimsFirst[i] = null;
				newDimsSecond[i] = null;
			} else {
				expression.accept(this);
				newDimsFirst[i] = (JExpression) this.getQueue().poll();
				newDimsSecond[i] = (JExpression) this.getQueue().poll();

			}
		}

		JArrayInitializer newInitFirst;
		JArrayInitializer newInitSecond;
		if (self.dims().init() == null) {
			newInitFirst = null;
			newInitSecond = null;
		} else {
			self.dims().init().accept(this);
			newInitFirst = (JArrayInitializer) this.getQueue().poll();
			newInitSecond = (JArrayInitializer) this.getQueue().poll();
		}

		JArrayDimsAndInitsExtension newDimsAndInitsFirst = new JArrayDimsAndInitsExtension(self.dims(), newDimsFirst, newInitFirst);
		JArrayDimsAndInitsExtension newDimsAndInitsSecond = new JArrayDimsAndInitsExtension(self.dims(), newDimsSecond, newInitSecond);

		JNewArrayExpression newSelfFirst = new JNewArrayExpression(self.getTokenReference(), self.getType(), newDimsAndInitsFirst);
		this.getQueue().offer(newSelfFirst);

		JNewArrayExpression newSelfSecond = new JNewArrayExpression(self.getTokenReference(), self.getType(), newDimsAndInitsSecond);
		this.getQueue().offer(newSelfSecond);
	}

	@Override
	public void visitEqualityExpression(JEqualityExpression self) {
		self.left().accept(this);
		JExpression expreLeftFirst = (JExpression)this.getQueue().poll();
		JExpression expreLeftSecond = (JExpression)this.getQueue().poll();

		self.right().accept(this);
		JExpression expreRightFirst = (JExpression)this.getQueue().poll();
		JExpression expreRightSecond = (JExpression)this.getQueue().poll();

		JEqualityExpression newSelfFirst = new JEqualityExpression(self.getTokenReference(), self.oper(), expreLeftFirst, expreRightFirst);
		this.theQueue.offer(newSelfFirst);

		JEqualityExpression newSelfSecond = new JEqualityExpression(self.getTokenReference(), self.oper(), expreLeftSecond, expreRightSecond);
		this.theQueue.offer(newSelfSecond);
	}





	public void visitLocalVariableExpression(/* @non_null */JLocalVariableExpression self) {
		self.variable().accept(this);
		JLocalVariable newLocVarFirst = (JLocalVariable)this.getQueue().poll();
		JLocalVariable newLocVarSecond = (JLocalVariable)this.getQueue().poll();

		JLocalVariableExpression newSelfFirst = new JLocalVariableExpression(self.getTokenReference(),newLocVarFirst);
		this.getQueue().offer(newSelfFirst);

		JLocalVariableExpression newSelfSecond = new JLocalVariableExpression(self.getTokenReference(),newLocVarSecond);
		this.getQueue().offer(newSelfSecond);
	}


	public void visitJmlFormalParameter(JmlFormalParameter self) {
		this.getQueue().offer(self);
		this.getQueue().offer(self);
	}


	/* (non-Javadoc)
	 * @see org.multijava.mjc.MjcVisitor#visitThisExpression(org.multijava.mjc.JThisExpression)
	 */
	@Override
	public void visitThisExpression(JThisExpression self) {
		JThisExpression selfFirst= new JThisExpression(self.getTokenReference());
		JThisExpression selfSecond= new JThisExpression(self.getTokenReference());

		this.getQueue().offer(selfFirst);
		this.getQueue().offer(selfSecond);	
	}


	//Notice that we assume no splitting occurs when accepting left and right. 
	//Nevertheless, in order to preserve the invariant, we expect after accepting
	//there are 2 expressions in the queue for left and 2 in the queue for right.
	//We will build 2 copies pairing the first and second from the queues.
	@Override
	public void visitJmlRelationalExpression(JmlRelationalExpression self) {
		JmlRelationalExpression relExpreFirst = null;
		JmlRelationalExpression relExpreSecond = null;

		self.left().accept(this);
		JExpression newLeftFirst = (JExpression)this.getQueue().poll();
		JExpression newLeftSecond = (JExpression)this.getQueue().poll();

		self.right().accept(this);
		JExpression newRightFirst = (JExpression)this.getQueue().poll();
		JExpression newRightSecond = (JExpression)this.getQueue().poll();

		relExpreFirst = new JmlRelationalExpression(self.getTokenReference(), self.oper(), newLeftFirst, newRightFirst);
		this.getQueue().offer(relExpreFirst);

		relExpreSecond = new JmlRelationalExpression(self.getTokenReference(), self.oper(), newLeftSecond, newRightSecond);
		this.getQueue().offer(relExpreSecond);

	}

	public void visitOrdinalLiteral(JOrdinalLiteral self){
		JOrdinalLiteral litFirst = (JOrdinalLiteral)self.clone(); 
		this.getQueue().offer(litFirst);

		JOrdinalLiteral litSecond = (JOrdinalLiteral)self.clone(); 
		this.getQueue().offer(litSecond);

	}

	public void visitUnaryExpression(/* @non_null */JUnaryExpression self) {
		self.expr().accept(this);
		JUnaryExpression unaryFirst = new JUnaryExpression(self.getTokenReference(), self.oper(), (JExpression)this.getQueue().poll());
		JUnaryExpression unarySecond = new JUnaryExpression(self.getTokenReference(), self.oper(), (JExpression)this.getQueue().poll());
		this.getQueue().offer(unaryFirst);
		this.getQueue().offer(unarySecond);
	}


	public void visitVariableDeclarationStatement(/* @non_null */JVariableDeclarationStatement self) {
		JVariableDefinition[] varsFirst = new JVariableDefinition[self.getVars().length];
		JVariableDefinition[] varsSecond = new JVariableDefinition[self.getVars().length];

		for (int i = 0; i < self.getVars().length; i++) {
			JVariableDefinition varDef = self.getVars()[i];
			varDef.accept(this);
			varsFirst[i] = (JVariableDefinition) this.getQueue().poll();
			varsSecond[i] = (JVariableDefinition) this.getQueue().poll();	
		}

		JVariableDeclarationStatement newSelfFirst = new JVariableDeclarationStatement(self.getTokenReference(),varsFirst ,self.getComments());
		this.getQueue().offer(newSelfFirst);

		JVariableDeclarationStatement newSelfSecond = new JVariableDeclarationStatement(self.getTokenReference(),varsSecond ,self.getComments());
		this.getQueue().offer(newSelfSecond);
	}


	@Override
	public void visitVariableDefinition(/* @non_null */ JVariableDefinition self) {
		JVariableDefinition varDefFirst = new JVariableDefinition(self.getTokenReference(), self.modifiers(), self.getType(), self.ident(), self.expr());
		this.getQueue().offer(varDefFirst);

		JVariableDefinition varDefSecond = new JVariableDefinition(self.getTokenReference(), self.modifiers(), self.getType(), self.ident(), self.expr());
		this.getQueue().offer(varDefSecond);
	}


	public void visitCompilationUnitType(JCompilationUnitType self) {
		JTypeDeclarationType[] type_declarations = self.typeDeclarations();
		JTypeDeclarationType[] typeDeclarationsFirst = new JTypeDeclarationType[type_declarations.length];
		JTypeDeclarationType[] typeDeclarationsSecond = new JTypeDeclarationType[type_declarations.length];

		for (int i = 0; i < type_declarations.length; i++) {
			JTypeDeclarationType jTypeDeclarationType = type_declarations[i];
			jTypeDeclarationType.accept(this);
			typeDeclarationsFirst[i] = (JTypeDeclarationType)this.getQueue().poll();
			typeDeclarationsSecond[i] = (JTypeDeclarationType)this.getQueue().poll();
		}

		JCompilationUnitType newSelfFirst = null;
	}

<<<<<<< HEAD
	//	@Override
	//	public void visitCompilationUnit(JCompilationUnit n) {
	//		visitCompilationUnitType(n);
	//	}

	//    public void visitJmlLoopStatement(JmlLoopStatement self) {
	//
	//        JWhileStatement loopStatement = (JWhileStatement)self.loopStmt();
	//        //Will modify the while loop so that it handles the variant function.
	//
	//        JStatement whileBody = loopStatement.body();
	//
	//        JmlVariantFunction[] varFunctions = self.variantFunctions();
	//        if (varFunctions.length > 0){
	//            CType type = varFunctions[0].specExpression().getApparentType();
	//            String newVarName = "variant_" + WhileBlockVisitor.variantFunctions();
	//            WhileBlockVisitor.variantFunctionIndex++;
	//            JVariableDefinition variableVariantFunction = new JVariableDefinition(loopStatement.getTokenReference(), 0, type, newVarName, varFunctions[0].specExpression());
	//            JVariableDeclarationStatement theVariantFunctionVariableDeclaration = new JVariableDeclarationStatement(loopStatement.getTokenReference(), variableVariantFunction, new JavaStyleComment[]{});
	//
	//            CClassType theExceptionType = new CTypeVariable("java.lang.RuntimeException", new CClassType[]{});
	//            try {
	//                theExceptionType.checkType(null);
	//            } catch (UnpositionedError e) {
	//                // TODO Auto-generated catch block
	//                e.printStackTrace();
	//            }
	//
	//            JNewObjectExpression theExpre = new JNewObjectExpression(
	//                    self.getTokenReference(),
	//                    theExceptionType,
	//                    new JThisExpression(self.getTokenReference()),
	//                    new JExpression[]{});
	//
	//            JThrowStatement throwStmt = new JThrowStatement(
	//                    self.getTokenReference(),
	//                    theExpre,
	//                    new JavaStyleComment[]{});
	//            JExpression expreZero = new JOrdinalLiteral(loopStatement.getTokenReference(), 0, (CNumericType)type);
	//            JExpression ifCond = new JmlRelationalExpression(loopStatement.getTokenReference(), OPE_LT, variableVariantFunction.expr(), expreZero);
	//            JStatement theIf = new JIfStatement(loopStatement.getTokenReference(), ifCond, throwStmt, null, null);
	//            JExpression ifCond2 = new JmlRelationalExpression(loopStatement.getTokenReference(), OPE_GE, varFunctions[0].specExpression(), new JLocalVariableExpression(loopStatement.getTokenReference(), variableVariantFunction));
	//            JThrowStatement throwStmt2 = new JThrowStatement(
	//                    loopStatement.getTokenReference(),
	//                    new JNewObjectExpression(
	//                            loopStatement.getTokenReference(),
	//                            theExceptionType,
	//                            new JThisExpression(loopStatement.getTokenReference()),
	//                            new JExpression[]{}),
	//                    new JavaStyleComment[]{});
	//            org.multijava.mjc.JStatement theIf2 = new JIfStatement(loopStatement.getTokenReference(), ifCond2, throwStmt2, null, null);
	//
	//            org.multijava.mjc.JStatement newLoopBody = new org.multijava.mjc.JBlock(loopStatement.getTokenReference(), new org.multijava.mjc.JStatement[]{theVariantFunctionVariableDeclaration, theIf, whileBody, theIf2}, new JavaStyleComment[]{});
	//            JWhileStatement newLoopStatement = new JWhileStatement(loopStatement.getTokenReference(), loopStatement.cond(), newLoopBody, loopStatement.getComments());
	//            newLoopStatement.accept(this);
	//        } else {
	//            loopStatement.accept(this);
	//        }
	//
	////		self.loopStmt().accept(this);
	//        JStatement newStatement = (JStatement) this.getStack().pop();
	//        JmlLoopStatement jmlLoopStatement = new JmlLoopStatement(self.getTokenReference(), self.loopInvariants(), new JmlVariantFunction[]{}, newStatement, self.getComments());
	//        JStatement[] blockStatements = new JStatement[this.newStatements.size() + 1];
	//        for (int i = 0; i< this.newStatements.size(); i++){
	//            blockStatements[i] = this.newStatements.get(i);
	//        }
	//        blockStatements[this.newStatements.size()] = jmlLoopStatement;
	//        this.newStatements = new ArrayList<JStatement>();
	//        JBlock newBlockIncludingNewStatements = new JBlock(self.getTokenReference(), blockStatements, self.getComments());
	//        this.getStack().push(newBlockIncludingNewStatements);
	//    }
	//    public void visitWhileStatement(JWhileStatement self) {
	//        self.body().accept(this);
	//        JStatement newBody = (JStatement) this.getQueue().poll();
	//        JWhileStatement whileStatement = null;
	//        String cond = newWhileVar();
	//        JVariableDefinition variableDefinition = new JVariableDefinition(self.getTokenReference(), 0, self.cond().getType(), cond, null);
	//        JVariableDeclarationStatement variableDeclarationStatement = new JVariableDeclarationStatement(self.getTokenReference(), variableDefinition,
	//                new JavaStyleComment[0]);
	//        getNewWhileStatements().add(variableDeclarationStatement);
	//        JLocalVariableExpression condReference = new JLocalVariableExpression(self.getTokenReference(), variableDefinition);
	//        JStatement assignamentStatement = ASTUtils.createAssignamentStatement(condReference, self.cond());
	//        getNewWhileStatements().add(assignamentStatement);
	//        LastStatementCollector lsc = new LastStatementCollector();
	//        newBody.accept(lsc);
	//        if (lsc.lastStatementClass != JBreakStatement.class){
	//            JBlock generatedBlock = ASTUtils.createBlockStatement(newBody, assignamentStatement);
	//            whileStatement = new JWhileStatement(self.getTokenReference(), condReference, generatedBlock, self.getComments());
	//        } else {
	//            whileStatement = new JWhileStatement(self.getTokenReference(), condReference, newBody, self.getComments());
	//        }
	//        this.getStack().push(whileStatement);
	//    }


=======
>>>>>>> 2137881e422e556fc146f9fe738856ba2549b254
	public void visitReturnStatement(/* @non_null */JReturnStatement self) {
		JBlock newSelfFirst = new JBlock(self.getTokenReference(), new JStatement[]{self},self.getComments());
		JBlock newSelfSecond = new JBlock(self.getTokenReference(), new JStatement[]{self},self.getComments());
		this.getQueue().offer(newSelfFirst);
		this.getQueue().offer(newSelfSecond);
	}



	private String getClassName(JmlClassDeclaration self) {
		String cname = self.getCClass().qualifiedName();
		cname = cname.replace('/', '_');
		cname = cname + "_";
		return cname;
	}

}
