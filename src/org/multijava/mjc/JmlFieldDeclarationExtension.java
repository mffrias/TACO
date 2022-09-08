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

import org.jmlspecs.checker.JmlDataGroupAccumulator;
import org.jmlspecs.checker.JmlDataGroupMemberMap;
import org.jmlspecs.checker.JmlFieldDeclaration;
import org.jmlspecs.checker.JmlInGroupClause;
import org.jmlspecs.checker.JmlMapsIntoClause;
import org.jmlspecs.checker.JmlMemberAccess;
import org.jmlspecs.checker.JmlMemberDeclaration;
import org.jmlspecs.checker.JmlMethodSpecification;
import org.jmlspecs.checker.JmlNode;
import org.jmlspecs.checker.JmlSourceField;
import org.jmlspecs.checker.JmlVarAssertion;
import org.jmlspecs.checker.JmlVisitor;
import org.multijava.javadoc.JavadocComment;
import org.multijava.mjc.CClass;
import org.multijava.mjc.CClassContextType;
import org.multijava.mjc.CContextType;
import org.multijava.mjc.CField;
import org.multijava.mjc.CFlowControlContextType;
import org.multijava.mjc.CMethod;
import org.multijava.mjc.CSourceField;
import org.multijava.mjc.CType;
import org.multijava.mjc.CodeSequence;
import org.multijava.mjc.JExpression;
import org.multijava.mjc.JStatement;
import org.multijava.mjc.JVariableDefinition;
import org.multijava.mjc.MjcVisitor;
import org.multijava.util.compiler.PositionedError;
import org.multijava.util.compiler.TokenReference;

/**
 * Field wraps a field, but avoid initialization
 * @author diego
 * 
 */
public class JmlFieldDeclarationExtension extends JmlFieldDeclaration {

	JmlFieldDeclaration fieldDeclaration;
	JVariableDefinition variable;
	
//	protected JmlFieldDeclarationExtension(TokenReference where, JmlVarAssertion[] varAssertions, JmlDataGroupAccumulator dataGroups, JFieldDeclaration delegee) {
//		super(where, varAssertions, dataGroups, delegee);
//	}
	public JmlFieldDeclarationExtension(JmlFieldDeclaration jmlFieldDeclaration, String newName) {
		super(jmlFieldDeclaration.getTokenReference(), jmlFieldDeclaration.varAssertions(), /*createAccumulator(jmlFieldDeclaration)*/null, null);
		;		
		this.fieldDeclaration = jmlFieldDeclaration;
		
		//JVariableDefinition variableDefinition = new JVariableDefinition(jmlFieldDeclaration.getTokenReference(),jmlFieldDeclaration.modifiers(),jmlFieldDeclaration.variable().getType(),newName,jmlFieldDeclaration.variable().expr());
				
//		JVariableDefinition var = new JVariableDefinition(jmlFieldDeclaration.variable().getTokenReference(),jmlFieldDeclaration.variable().modifiers(),jmlFieldDeclaration.variable().getType(),jmlFieldDeclaration.variable().ident(),null);
		JVariableDefinition var = new JVariableDefinition(jmlFieldDeclaration.variable().getTokenReference(),jmlFieldDeclaration.variable().modifiers(),jmlFieldDeclaration.variable().getType(),newName,jmlFieldDeclaration.variable().expr());
		
		this.variable = var;
		
	}

	/*
	private static JmlDataGroupAccumulator createAccumulator(JmlFieldDeclaration jmlFieldDeclaration) {
		JmlDataGroupAccumulator accumulator = new JmlDataGroupAccumulator();
		for (JmlInGroupClause element : jmlFieldDeclaration.inGroupClauses()) {
			accumulator.addInGroup(element);
		}
		for (JmlMapsIntoClause element : jmlFieldDeclaration.mapsIntoClauses()) {
			accumulator.addMapsInto(element);
		}
		return accumulator;
	}
	*/
	
	public void accept(MjcVisitor p) {
		//fieldDeclaration.accept(p);
		if (p instanceof JmlVisitor) {
		    ((JmlVisitor)p).visitJmlFieldDeclaration(this);
		}
		else {
		    throw new UnsupportedOperationException(JmlNode.MJCVISIT_MESSAGE);
	    }
		
	}

	public void addSelfToInDataGroups(JmlSourceField self, JmlInGroupClause inGroupClause, JmlDataGroupMemberMap dataGroupMap) {
		fieldDeclaration.addSelfToInDataGroups(self, inGroupClause, dataGroupMap);
	}

	public void addToDataGroups(JmlDataGroupMemberMap dataGroupMap) {
		fieldDeclaration.addToDataGroups(dataGroupMap);
	}

	public JStatement assertionCode() {
		return fieldDeclaration.assertionCode();
	}

	public void checkFieldSpecs(CFlowControlContextType context, JmlSourceField self) throws PositionedError {
		fieldDeclaration.checkFieldSpecs(context, self);
	}

	public CSourceField checkInterface(CClassContextType context) throws PositionedError {
		return fieldDeclaration.checkInterface(context);
	}

	public void checkRefinedModifiers(CContextType context, JmlMemberDeclaration member) throws PositionedError {
		fieldDeclaration.checkRefinedModifiers(context, member);
	}

	public void checkRefinementConsistency(CContextType context) throws PositionedError {
		fieldDeclaration.checkRefinementConsistency(context);
	}

	public void combineDataGroups(JmlFieldDeclaration refField) {
		fieldDeclaration.combineDataGroups(refField);
	}

	public void combineSpecifications() {
		fieldDeclaration.combineSpecifications();
	}

	public boolean equals(Object arg0) {
		return fieldDeclaration.equals(arg0);
	}

	public JmlFieldDeclaration findDeclWithInitializer() {
		return fieldDeclaration.findDeclWithInitializer();
	}

	public String findJavaFileInRefinement() {
		return fieldDeclaration.findJavaFileInRefinement();
	}

	public void genCode(CodeSequence code) {
		fieldDeclaration.genCode(code);
	}

	public void genComments(MjcVisitor p) {
		fieldDeclaration.genComments(p);
	}

	public CClass getCClass() {
		return fieldDeclaration.getCClass();
	}

	public JmlInGroupClause[] getCombinedInGroupClauses() {
		return fieldDeclaration.getCombinedInGroupClauses();
	}

	public JmlMapsIntoClause[] getCombinedMapsIntoClauses() {
		return fieldDeclaration.getCombinedMapsIntoClauses();
	}

	public JmlMethodSpecification getCombinedSpecification() {
		return fieldDeclaration.getCombinedSpecification();
	}

	public JmlVarAssertion[] getCombinedVarAssertions() {
		return fieldDeclaration.getCombinedVarAssertions();
	}

	public CField getField() {
		return fieldDeclaration.getField();
	}

	public JExpression getInitializer() {
		return fieldDeclaration.getInitializer();
	}

	public CMethod getMethod() {
		return fieldDeclaration.getMethod();
	}

	public JmlMemberDeclaration getMostRefined() {
		return fieldDeclaration.getMostRefined();
	}

	public JmlMemberDeclaration getRefinedMember() {
		return fieldDeclaration.getRefinedMember();
	}

	public JmlMemberDeclaration getRefiningMember() {
		return fieldDeclaration.getRefiningMember();
	}

	public TokenReference getTokenReference() {
		return fieldDeclaration.getTokenReference();
	}

	public CType getType() {
		return fieldDeclaration.getType();
	}

	public boolean hasAssertionCode() {
		return fieldDeclaration.hasAssertionCode();
	}

	public boolean hasAssertions() {
		return fieldDeclaration.hasAssertions();
	}

	public int hashCode() {
		return fieldDeclaration.hashCode();
	}

	public boolean hasInitializer() {
		return false;
	}

	public String ident() {
		return variable.ident();
	}

	public boolean inBinaryClassFile() {
		return fieldDeclaration.inBinaryClassFile();
	}

	public JmlInGroupClause[] inGroupClauses() {
		return fieldDeclaration.inGroupClauses();
	}

	public boolean inJavaFile() {
		return fieldDeclaration.inJavaFile();
	}

	public ArrayList inners() {
		return fieldDeclaration.inners();
	}

	public boolean isDeprecated() {
		return fieldDeclaration.isDeprecated();
	}

	public boolean isModel() {
		return fieldDeclaration.isModel();
	}

	public boolean isRefined() {
		return fieldDeclaration.isRefined();
	}

	public boolean isRefiningMember() {
		return fieldDeclaration.isRefiningMember();
	}

	public JavadocComment javadocComment() {
		return fieldDeclaration.javadocComment();
	}

	public JmlMemberAccess jmlAccess() {
		return fieldDeclaration.jmlAccess();
	}

	public JmlMapsIntoClause[] mapsIntoClauses() {
		return fieldDeclaration.mapsIntoClauses();
	}

	public long modifiers() {
		return fieldDeclaration.modifiers();
	}

	public boolean needInitialization() {
		return fieldDeclaration.needInitialization();
	}

	public void setAssertionCode(JStatement code) {
		fieldDeclaration.setAssertionCode(code);
	}

	public void setInitializer(JExpression expr) {
		fieldDeclaration.setInitializer(expr);
	}

	public void setModifiers(long mod) {
		fieldDeclaration.setModifiers(mod);
	}

	public void setNonNull() {
		fieldDeclaration.setNonNull();
	}

	public void setRefinedMember(JmlMemberDeclaration refinedDecl) {
		fieldDeclaration.setRefinedMember(refinedDecl);
	}

	public void setRefiningMember(JmlMemberDeclaration refiningDecl) {
		fieldDeclaration.setRefiningMember(refiningDecl);
	}

	public void setSpecstoCombinedSpecs() {
		fieldDeclaration.setSpecstoCombinedSpecs();
	}

	public void setTokenReference(TokenReference where) {
		fieldDeclaration.setTokenReference(where);
	}

	public String stringRepresentation() {
		return fieldDeclaration.stringRepresentation();
	}

	public String toString() {
		return fieldDeclaration.toString();
	}

	public void typecheck(CFlowControlContextType context) throws PositionedError {
		fieldDeclaration.typecheck(context);
	}

	public JmlVarAssertion[] varAssertions() {
		return fieldDeclaration.varAssertions();
	}

	public JVariableDefinition variable() {
		return variable;
	}


}
