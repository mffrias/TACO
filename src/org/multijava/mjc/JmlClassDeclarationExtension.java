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

import java.util.ArrayList;
import java.util.List;

import org.jmlspecs.checker.JClassDeclarationWrapper;
import org.jmlspecs.checker.JmlAxiom;
import org.jmlspecs.checker.JmlBinaryType;
import org.jmlspecs.checker.JmlClassDeclaration;
import org.jmlspecs.checker.JmlConstraint;
import org.jmlspecs.checker.JmlDataGroupMemberMap;
import org.jmlspecs.checker.JmlInvariant;
import org.jmlspecs.checker.JmlMemberAccess;
import org.jmlspecs.checker.JmlMemberDeclaration;
import org.jmlspecs.checker.JmlMethodSpecification;
import org.jmlspecs.checker.JmlRepresentsDecl;
import org.jmlspecs.checker.JmlTypeDeclaration;
import org.jmlspecs.checker.JmlVarAssertion;
import org.jmlspecs.checker.JmlVisitor;
import org.multijava.javadoc.JavadocComment;
import org.multijava.mjc.CClass;
import org.multijava.mjc.CClassContextType;
import org.multijava.mjc.CClassType;
import org.multijava.mjc.CContextType;
import org.multijava.mjc.CField;
import org.multijava.mjc.CMemberHost;
import org.multijava.mjc.CMethod;
import org.multijava.mjc.CTypeVariable;
import org.multijava.mjc.JConstructorDeclarationType;
import org.multijava.mjc.JFieldDeclarationType;
import org.multijava.mjc.JMemberDeclarationType;
import org.multijava.mjc.JPhylum;
import org.multijava.mjc.Main;
import org.multijava.mjc.MjcVisitor;
import org.multijava.util.MessageDescription;
import org.multijava.util.compiler.Compiler;
import org.multijava.util.compiler.PositionedError;
import org.multijava.util.compiler.TokenReference;

import ar.edu.taco.TacoNotImplementedYetException;

public class JmlClassDeclarationExtension extends JmlClassDeclaration {

	private JmlClassDeclaration wrapped;
//	private ArrayList newMethods;
//	private JmlInvariant[] newInvariants;
//	private JmlRepresentsDecl[] newRepresentsDecls;

	public JmlClassDeclarationExtension(JmlClassDeclaration wrapped, JmlInvariant[] invariants, JmlRepresentsDecl[] representsDecls,JmlConstraint[] newConstraints, ArrayList newMethods,
			List<JFieldDeclarationType> newModelsFields,ArrayList<JmlClassDeclaration> newInners) {

		super(wrapped.getTokenReference(), wrapped.isWeakSubtype(), wrapped.interfaceWeaklyFlags(), invariants, wrapped.constraints(), representsDecls, wrapped
				.axioms(), wrapped.varAssertions(), new JClassDeclarationWrapper(wrapped.getTokenReference(), wrapped.modifiers(), wrapped.ident(), wrapped
				.typevariables(), wrapped.getCClass().getSuperType(), wrapped.interfaces(), newMethods, newInners, wrapped.fieldsAndInits(), wrapped
				.javadocComment(), null, wrapped.isRefined()));

		//
		// super(wrapped.getTokenReference(), wrapped.modifiers(),
		// wrapped.ident(), wrapped.typevariables(),
		// wrapped.getCClass().getSuperType(), wrapped
		// .isWeakSubtype(), wrapped.interfaces(),
		// wrapped.interfaceWeaklyFlags(), newMethods, wrapped.inners(),
		// wrapped.fieldsAndInits(), wrapped
		// .invariants(), wrapped.constraints(), wrapped.representsDecls(),
		// wrapped.axioms(), wrapped.varAssertions(), wrapped.javadocComment(),
		// null,
		// wrapped.isRefined());
		//		
		this.wrapped = wrapped;
		//this.newMethods = newMethods;
		this.methodList = newMethods;
		this.modelFields = newModelsFields.toArray(new JFieldDeclarationType[0]);
		this.invariants = invariants;
		this.representsDecls = representsDecls;
		this.constraints = newConstraints;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.jmlspecs.checker.JmlClassDeclaration#accept(org.multijava.mjc.MjcVisitor
	 * )
	 */
	@Override
	public void accept(MjcVisitor p) {
		if (p instanceof JmlVisitor)
			((JmlVisitor) p).visitJmlClassDeclaration(this);
		else
			delegee.accept(p);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.jmlspecs.checker.JmlClassDeclaration#createContext(org.multijava.
	 * mjc.CContextType)
	 */
	@Override
	public CClassContextType createContext(CContextType parent) {

		return wrapped.createContext(parent);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jmlspecs.checker.JmlClassDeclaration#hasConstructor()
	 */
	@Override
	public boolean hasConstructor() {

		return wrapped.hasConstructor();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jmlspecs.checker.JmlClassDeclaration#isClass()
	 */
	@Override
	public boolean isClass() {

		return wrapped.isClass();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jmlspecs.checker.JmlClassDeclaration#isRacable()
	 */
	@Override
	public boolean isRacable() {

		return wrapped.isRacable();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jmlspecs.checker.JmlClassDeclaration#isWeakSubtype()
	 */
	@Override
	public boolean isWeakSubtype() {

		return wrapped.isWeakSubtype();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.jmlspecs.checker.JmlClassDeclaration#resolveSpecializers(org.multijava
	 * .mjc.CContextType)
	 */
	@Override
	public void resolveSpecializers(CContextType context) throws PositionedError {

		wrapped.resolveSpecializers(context);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.jmlspecs.checker.JmlClassDeclaration#setInterfaces(org.multijava.
	 * mjc.CClassType[])
	 */
	@Override
	public void setInterfaces(CClassType[] interfaces) {

		wrapped.setInterfaces(interfaces);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jmlspecs.checker.JmlClassDeclaration#setRacable()
	 */
	@Override
	public void setRacable() {

		wrapped.setRacable();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.jmlspecs.checker.JmlClassDeclaration#setSuperClass(org.multijava.
	 * mjc.CClassType)
	 */
	@Override
	public void setSuperClass(CClassType superType) {

		wrapped.setSuperClass(superType);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jmlspecs.checker.JmlClassDeclaration#superName()
	 */
	@Override
	public String superName() {

		return wrapped.superName();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.jmlspecs.checker.JmlTypeDeclaration#accumAllTypeSignatures(java.util
	 * .ArrayList)
	 */
	@Override
	public void accumAllTypeSignatures(ArrayList accum) {

		wrapped.accumAllTypeSignatures(accum);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.jmlspecs.checker.JmlTypeDeclaration#addMember(org.multijava.mjc.
	 * JMemberDeclarationType)
	 */
	@Override
	public void addMember(JMemberDeclarationType newMember) {

		wrapped.addMember(newMember);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jmlspecs.checker.JmlTypeDeclaration#axioms()
	 */
	@Override
	public JmlAxiom[] axioms() {

		return wrapped.axioms();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.jmlspecs.checker.JmlTypeDeclaration#cachePassParameters(org.multijava
	 * .mjc.CContextType)
	 */
	@Override
	public void cachePassParameters(CContextType context) {

		wrapped.cachePassParameters(context);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jmlspecs.checker.JmlTypeDeclaration#checkAssignableClauses()
	 */
	@Override
	public void checkAssignableClauses() throws PositionedError {

		wrapped.checkAssignableClauses();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jmlspecs.checker.JmlTypeDeclaration#checkInitializers()
	 */
	@Override
	public void checkInitializers() throws PositionedError {

		wrapped.checkInitializers();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.jmlspecs.checker.JmlTypeDeclaration#checkInitializers(org.multijava
	 * .mjc.CContextType)
	 */
	@Override
	public void checkInitializers(CContextType context) throws PositionedError {

		wrapped.checkInitializers(context);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jmlspecs.checker.JmlTypeDeclaration#checkInterface()
	 */
	@Override
	public void checkInterface() throws PositionedError {

		wrapped.checkInterface();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.jmlspecs.checker.JmlTypeDeclaration#checkInterface(org.multijava.
	 * mjc.CContextType)
	 */
	@Override
	public void checkInterface(CContextType context) throws PositionedError {

		wrapped.checkInterface(context);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.jmlspecs.checker.JmlTypeDeclaration#checkRacability(org.multijava
	 * .util.compiler.Compiler)
	 */
	@Override
	public boolean checkRacability(Compiler compiler) {

		return wrapped.checkRacability(compiler);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jmlspecs.checker.JmlTypeDeclaration#combineSpecifications()
	 */
	@Override
	public void combineSpecifications() {

		wrapped.combineSpecifications();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jmlspecs.checker.JmlTypeDeclaration#combinedAxioms()
	 */
	@Override
	public JmlAxiom[] combinedAxioms() {

		return wrapped.combinedAxioms();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jmlspecs.checker.JmlTypeDeclaration#combinedConstraints()
	 */
	@Override
	public JmlConstraint[] combinedConstraints() {

		return wrapped.combinedConstraints();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jmlspecs.checker.JmlTypeDeclaration#combinedInvariants()
	 */
	@Override
	public JmlInvariant[] combinedInvariants() {

		return wrapped.combinedInvariants();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jmlspecs.checker.JmlTypeDeclaration#combinedRepresentsDecls()
	 */
	@Override
	public JmlRepresentsDecl[] combinedRepresentsDecls() {

		return wrapped.combinedRepresentsDecls();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jmlspecs.checker.JmlTypeDeclaration#combinedVarAssertions()
	 */
	@Override
	public JmlVarAssertion[] combinedVarAssertions() {

		return wrapped.combinedVarAssertions();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jmlspecs.checker.JmlTypeDeclaration#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Object o) throws ClassCastException {

		return wrapped.compareTo(o);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jmlspecs.checker.JmlTypeDeclaration#constraints()
	 */
	@Override
	public JmlConstraint[] constraints() {

		return this.constraints;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jmlspecs.checker.JmlTypeDeclaration#fields()
	 */
	@Override
	public JFieldDeclarationType[] fields() {

		return wrapped.fields();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jmlspecs.checker.JmlTypeDeclaration#fieldsAndInits()
	 */
	@Override
	public JPhylum[] fieldsAndInits() {

		return wrapped.fieldsAndInits();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.jmlspecs.checker.JmlTypeDeclaration#findTypeWithRepresentsFor(org
	 * .multijava.mjc.CField)
	 */
	@Override
	public JmlTypeDeclaration findTypeWithRepresentsFor(CField field) {

		return wrapped.findTypeWithRepresentsFor(field);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.jmlspecs.checker.JmlTypeDeclaration#generateInterface(org.multijava
	 * .mjc.Main, org.multijava.mjc.CClass, org.multijava.mjc.CMemberHost,
	 * java.lang.String, boolean, boolean)
	 */
	@Override
	public void generateInterface(Main compiler, CClass owner, CMemberHost host, String prefix, boolean isAnon, boolean isMember) {

		wrapped.generateInterface(compiler, owner, host, prefix, isAnon, isMember);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jmlspecs.checker.JmlTypeDeclaration#getAllInterfaceModelFields()
	 */
	@Override
	public JFieldDeclarationType[] getAllInterfaceModelFields() {

		return wrapped.getAllInterfaceModelFields();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jmlspecs.checker.JmlTypeDeclaration#getAllMethods()
	 */
	@Override
	public ArrayList getAllMethods() {

		return wrapped.getAllMethods();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.jmlspecs.checker.JmlTypeDeclaration#getAllSuperClassModelFields()
	 */
	@Override
	public JFieldDeclarationType[] getAllSuperClassModelFields() {

		return wrapped.getAllSuperClassModelFields();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jmlspecs.checker.JmlTypeDeclaration#getCombinedFields()
	 */
	@Override
	public JFieldDeclarationType[] getCombinedFields() {

		return wrapped.getCombinedFields();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jmlspecs.checker.JmlTypeDeclaration#getCombinedInners()
	 */
	@Override
	public JmlMemberDeclaration[] getCombinedInners() {

		return wrapped.getCombinedInners();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jmlspecs.checker.JmlTypeDeclaration#getCombinedMethods()
	 */
	@Override
	public JmlMemberDeclaration[] getCombinedMethods() {

		return wrapped.getCombinedMethods();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jmlspecs.checker.JmlTypeDeclaration#getDataGroupMap()
	 */
	@Override
	public JmlDataGroupMemberMap getDataGroupMap() {

		return wrapped.getDataGroupMap();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jmlspecs.checker.JmlTypeDeclaration#getDefaultConstructor()
	 */
	@Override
	public JConstructorDeclarationType getDefaultConstructor() {

		return wrapped.getDefaultConstructor();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jmlspecs.checker.JmlTypeDeclaration#getModelFields()
	 */
	@Override
	public JFieldDeclarationType[] getModelFields() {

		return modelFields;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jmlspecs.checker.JmlTypeDeclaration#hasSourceInRefinement()
	 */
	@Override
	public boolean hasSourceInRefinement() {

		return wrapped.hasSourceInRefinement();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jmlspecs.checker.JmlTypeDeclaration#ident()
	 */
	@Override
	public String ident() {

		return wrapped.ident();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jmlspecs.checker.JmlTypeDeclaration#inJavaFile()
	 */
	@Override
	public boolean inJavaFile() {

		return wrapped.inJavaFile();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jmlspecs.checker.JmlTypeDeclaration#initializeDataGroupMap()
	 */
	@Override
	protected void initializeDataGroupMap() {

		// wrapped.initializeDataGroupMap();
		throw new TacoNotImplementedYetException();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jmlspecs.checker.JmlTypeDeclaration#inners()
	 */
	@Override
	public ArrayList inners() {

		return super.inners();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jmlspecs.checker.JmlTypeDeclaration#interfaceWeaklyFlags()
	 */
	@Override
	public boolean[] interfaceWeaklyFlags() {

		return wrapped.interfaceWeaklyFlags();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jmlspecs.checker.JmlTypeDeclaration#interfaces()
	 */
	@Override
	public CClassType[] interfaces() {

		return wrapped.interfaces();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jmlspecs.checker.JmlTypeDeclaration#invariants()
	 */
	@Override
	public JmlInvariant[] invariants() {

		return invariants;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jmlspecs.checker.JmlTypeDeclaration#isAtTopLevel()
	 */
	@Override
	public boolean isAtTopLevel() {

		return wrapped.isAtTopLevel();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jmlspecs.checker.JmlTypeDeclaration#isFinal()
	 */
	@Override
	public boolean isFinal() {

		return wrapped.isFinal();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jmlspecs.checker.JmlTypeDeclaration#isInterface()
	 */
	@Override
	public boolean isInterface() {

		return wrapped.isInterface();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jmlspecs.checker.JmlTypeDeclaration#jmlAccess()
	 */
	@Override
	public JmlMemberAccess jmlAccess() {

		return wrapped.jmlAccess();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jmlspecs.checker.JmlTypeDeclaration#methods()
	 */
	@Override
	public ArrayList methods() {
		return this.methodList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jmlspecs.checker.JmlTypeDeclaration#modifiers()
	 */
	@Override
	public long modifiers() {

		return wrapped.modifiers();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jmlspecs.checker.JmlTypeDeclaration#owner()
	 */
	@Override
	public CClass owner() {

		return wrapped.owner();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jmlspecs.checker.JmlTypeDeclaration#preprocessDependencies()
	 */
	@Override
	public void preprocessDependencies() throws PositionedError {

		wrapped.preprocessDependencies();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.jmlspecs.checker.JmlTypeDeclaration#preprocessDependencies(org.multijava
	 * .mjc.CContextType)
	 */
	@Override
	public void preprocessDependencies(CContextType context) throws PositionedError {

		wrapped.preprocessDependencies(context);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jmlspecs.checker.JmlTypeDeclaration#representsDecls()
	 */
	@Override
	public JmlRepresentsDecl[] representsDecls() {

		return this.representsDecls;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jmlspecs.checker.JmlTypeDeclaration#resolveSpecializers()
	 */
	@Override
	public void resolveSpecializers() throws PositionedError {

		wrapped.resolveSpecializers();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jmlspecs.checker.JmlTypeDeclaration#resolveTopMethods()
	 */
	@Override
	public void resolveTopMethods() throws PositionedError {

		wrapped.resolveTopMethods();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.jmlspecs.checker.JmlTypeDeclaration#setDefaultConstructor(org.multijava
	 * .mjc.JConstructorDeclarationType)
	 */
	@Override
	public void setDefaultConstructor(JConstructorDeclarationType defaultConstructor) {

		wrapped.setDefaultConstructor(defaultConstructor);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.jmlspecs.checker.JmlTypeDeclaration#setFields(org.multijava.mjc.JPhylum
	 * [])
	 */
	@Override
	public void setFields(JPhylum[] fields) {

		wrapped.setFields(fields);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.jmlspecs.checker.JmlTypeDeclaration#setFieldsOnly(org.multijava.mjc
	 * .JFieldDeclarationType[])
	 */
	@Override
	public void setFieldsOnly(JFieldDeclarationType[] fields) {

		wrapped.setFieldsOnly(fields);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.jmlspecs.checker.JmlTypeDeclaration#setHasSourceInRefinement(boolean)
	 */
	@Override
	public void setHasSourceInRefinement(boolean flag) {

		wrapped.setHasSourceInRefinement(flag);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jmlspecs.checker.JmlTypeDeclaration#setIdent(java.lang.String)
	 */
	@Override
	public void setIdent(String ident) {

		wrapped.setIdent(ident);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.jmlspecs.checker.JmlTypeDeclaration#setInners(java.util.ArrayList)
	 */
	@Override
	public void setInners(ArrayList v) {

		wrapped.setInners(v);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.jmlspecs.checker.JmlTypeDeclaration#setMembersToCombinedMembers()
	 */
	@Override
	public void setMembersToCombinedMembers() {

		wrapped.setMembersToCombinedMembers();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.jmlspecs.checker.JmlTypeDeclaration#setMethods(java.util.ArrayList)
	 */
	@Override
	public void setMethods(ArrayList m) {

		wrapped.setMethods(m);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.jmlspecs.checker.JmlTypeDeclaration#setRefinedType(org.jmlspecs.checker
	 * .JmlBinaryType)
	 */
	@Override
	public void setRefinedType(JmlBinaryType refinedType) {

		wrapped.setRefinedType(refinedType);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.jmlspecs.checker.JmlTypeDeclaration#setRefinedType(org.jmlspecs.checker
	 * .JmlTypeDeclaration)
	 */
	@Override
	public void setRefinedType(JmlTypeDeclaration refinedType) throws PositionedError {

		wrapped.setRefinedType(refinedType);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jmlspecs.checker.JmlTypeDeclaration#setStatic()
	 */
	@Override
	public void setStatic() {

		wrapped.setStatic();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.jmlspecs.checker.JmlTypeDeclaration#syntheticOuterThisInaccessible()
	 */
	@Override
	public void syntheticOuterThisInaccessible() {

		wrapped.syntheticOuterThisInaccessible();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jmlspecs.checker.JmlTypeDeclaration#translateMJ()
	 */
	@Override
	public void translateMJ() {

		wrapped.translateMJ();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.jmlspecs.checker.JmlTypeDeclaration#translateMJ(org.multijava.mjc
	 * .CContextType)
	 */
	@Override
	public void translateMJ(CContextType context) {

		wrapped.translateMJ(context);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jmlspecs.checker.JmlTypeDeclaration#typecheck()
	 */
	@Override
	public void typecheck() throws PositionedError {

		wrapped.typecheck();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.jmlspecs.checker.JmlTypeDeclaration#typecheck(org.multijava.mjc.
	 * CContextType)
	 */
	@Override
	public void typecheck(CContextType context) throws PositionedError {

		wrapped.typecheck(context);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jmlspecs.checker.JmlTypeDeclaration#typevariables()
	 */
	@Override
	public CTypeVariable[] typevariables() {

		return wrapped.typevariables();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jmlspecs.checker.JmlTypeDeclaration#unsetStatic()
	 */
	@Override
	public void unsetStatic() {

		wrapped.unsetStatic();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jmlspecs.checker.JmlTypeDeclaration#varAssertions()
	 */
	@Override
	public JmlVarAssertion[] varAssertions() {

		return wrapped.varAssertions();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.jmlspecs.checker.JmlMemberDeclaration#checkRefinedModifiers(org.multijava
	 * .mjc.CContextType, org.jmlspecs.checker.JmlMemberDeclaration)
	 */
	@Override
	public void checkRefinedModifiers(CContextType context, JmlMemberDeclaration member) throws PositionedError {

		wrapped.checkRefinedModifiers(context, member);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jmlspecs.checker.JmlMemberDeclaration#findJavaFileInRefinement()
	 */
	@Override
	public String findJavaFileInRefinement() {

		return wrapped.findJavaFileInRefinement();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.jmlspecs.checker.JmlMemberDeclaration#genComments(org.multijava.mjc
	 * .MjcVisitor)
	 */
	@Override
	public void genComments(MjcVisitor p) {

		wrapped.genComments(p);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jmlspecs.checker.JmlMemberDeclaration#getCClass()
	 */
	@Override
	public CClass getCClass() {

		return wrapped.getCClass();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jmlspecs.checker.JmlMemberDeclaration#getCombinedSpecification()
	 */
	@Override
	public JmlMethodSpecification getCombinedSpecification() {

		return wrapped.getCombinedSpecification();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jmlspecs.checker.JmlMemberDeclaration#getCombinedVarAssertions()
	 */
	@Override
	public JmlVarAssertion[] getCombinedVarAssertions() {

		return wrapped.getCombinedVarAssertions();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jmlspecs.checker.JmlMemberDeclaration#getField()
	 */
	@Override
	public CField getField() {

		return wrapped.getField();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jmlspecs.checker.JmlMemberDeclaration#getMethod()
	 */
	@Override
	public CMethod getMethod() {

		return wrapped.getMethod();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jmlspecs.checker.JmlMemberDeclaration#getMostRefined()
	 */
	@Override
	public JmlMemberDeclaration getMostRefined() {

		return wrapped.getMostRefined();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jmlspecs.checker.JmlMemberDeclaration#getRefinedMember()
	 */
	@Override
	public JmlMemberDeclaration getRefinedMember() {

		return wrapped.getRefinedMember();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jmlspecs.checker.JmlMemberDeclaration#getRefiningMember()
	 */
	@Override
	public JmlMemberDeclaration getRefiningMember() {

		return wrapped.getRefiningMember();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jmlspecs.checker.JmlMemberDeclaration#inBinaryClassFile()
	 */
	@Override
	public boolean inBinaryClassFile() {

		return wrapped.inBinaryClassFile();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jmlspecs.checker.JmlMemberDeclaration#isDeprecated()
	 */
	@Override
	public boolean isDeprecated() {

		return wrapped.isDeprecated();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jmlspecs.checker.JmlMemberDeclaration#isRefined()
	 */
	@Override
	public boolean isRefined() {

		return wrapped.isRefined();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jmlspecs.checker.JmlMemberDeclaration#isRefiningMember()
	 */
	@Override
	public boolean isRefiningMember() {

		return wrapped.isRefiningMember();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jmlspecs.checker.JmlMemberDeclaration#javadocComment()
	 */
	@Override
	public JavadocComment javadocComment() {

		return wrapped.javadocComment();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.jmlspecs.checker.JmlMemberDeclaration#setRefinedMember(org.jmlspecs
	 * .checker.JmlMemberDeclaration)
	 */
	@Override
	public void setRefinedMember(JmlMemberDeclaration refinedDecl) {

		wrapped.setRefinedMember(refinedDecl);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.jmlspecs.checker.JmlMemberDeclaration#setRefiningMember(org.jmlspecs
	 * .checker.JmlMemberDeclaration)
	 */
	@Override
	public void setRefiningMember(JmlMemberDeclaration refiningDecl) {

		wrapped.setRefiningMember(refiningDecl);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jmlspecs.checker.JmlMemberDeclaration#stringRepresentation()
	 */
	@Override
	public String stringRepresentation() {

		return wrapped.stringRepresentation();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.multijava.mjc.JPhylum#fail(org.multijava.mjc.CContextType,
	 * org.multijava.util.MessageDescription)
	 */
	@Override
	protected void fail(CContextType context, MessageDescription description) throws PositionedError {

		// wrapped.fail(context, description);
		throw new TacoNotImplementedYetException();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.multijava.mjc.JPhylum#fail(org.multijava.mjc.CContextType,
	 * org.multijava.util.MessageDescription, java.lang.Object[])
	 */
	@Override
	protected void fail(CContextType context, MessageDescription description, Object[] params) throws PositionedError {

		// wrapped.fail(context, description, params);
		throw new TacoNotImplementedYetException();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.multijava.mjc.JPhylum#fail(org.multijava.mjc.CContextType,
	 * org.multijava.util.MessageDescription, java.lang.Object)
	 */
	@Override
	protected void fail(CContextType context, MessageDescription description, Object param) throws PositionedError {

		// wrapped.fail(context, description, param);
		throw new TacoNotImplementedYetException();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.multijava.util.compiler.Phylum#getTokenReference()
	 */
	@Override
	public TokenReference getTokenReference() {

		return wrapped.getTokenReference();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.multijava.util.compiler.Phylum#setTokenReference(org.multijava.util
	 * .compiler.TokenReference)
	 */
	@Override
	public void setTokenReference(TokenReference where) {

		wrapped.setTokenReference(where);
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		return super.clone();
	}

	@Override
	public String toString() {
		return this.getCClass().getCClass().getJavaName();
	}

}
