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
package ar.edu.taco.parser;

import antlr.RecognitionException;
import antlr.TokenStreamException;
import ar.edu.jdynalloy.ast.JDynAlloyModule;
import ar.edu.jdynalloy.ast.JAssignment;
import ar.edu.jdynalloy.ast.JBlock;
import ar.edu.jdynalloy.ast.JField;
import ar.edu.jdynalloy.ast.JObjectConstraint;
import ar.edu.jdynalloy.ast.JObjectInvariant;
import ar.edu.jdynalloy.ast.JProgramDeclaration;
import ar.edu.jdynalloy.ast.JRepresents;
import ar.edu.jdynalloy.xlator.JType;
import ar.edu.jdynalloy.xlator.JTypeHelper;
import ar.edu.taco.parser.common.JDynAlloyParserTestBase;
import ar.uba.dc.rfm.alloy.ast.expressions.ExprConstant;
import ar.uba.dc.rfm.alloy.ast.expressions.ExprUnion;
import ar.uba.dc.rfm.alloy.ast.formulas.AlloyFormula;
import ar.uba.dc.rfm.alloy.ast.formulas.EqualsFormula;
import ar.uba.dc.rfm.alloy.ast.formulas.PredicateFormula;

public class JDynAlloyModuleDeclarationTest extends JDynAlloyParserTestBase {

	public void testJDynAlloyModule() throws RecognitionException,
			TokenStreamException {
		String text = ""
				+ "module ar_edu_dynjml4alloy_jml_LinkList"
				+ "\n"
				+ "sig ar_edu_dynjml4alloy_jml_LinkList extends java_lang_Object {} {}"
				+ "\n"
				+ "\n"
				+ "field size:Int {}"
				+ "\n"
				+ "object_invariant equ[0,1]"
				+ "\n"				
				+ "represents (thiz).(size) such that equ[0,1]"
				+ "\n"
				+ "class_invariant neq[10,20]"
				+ "\n"				
				+ "class_constraint or[true,false]"
				+ "\n"								
				+ "object_constraint lt[size,10]"
				+ "\n"												
				+ "program ar_edu_dynjml4alloy_jml_LinkList::get["
				+ "\n"
				+ "var thiz:ar_edu_dynjml4alloy_jml_LinkList,"
				+ "\n"
				+ "var throw:AssertionFailure+java_lang_Exception+null,"
				+ "\n"
				+ "var return:ar_edu_dynjml4alloy_jml_LinkList_inner_Value+null,"
				+ "\n"
				+ "var index:Int] "
				+ "\n"
				+ "Specification "
				+ "\n"
				+ "{ SpecCase #0 { requires { lt[0,index] } ensures { TruePred[] } } }"
				+ "\n" + "Implementation " + "\n" + "{ " + "\n"
				+ "	return:=index;" + "\n" + "}";
		

		JDynAlloyModule moduleDeclaration = this.initializeParser(text).dynJmlAlloyModule(this.getCtx());
		
		assertTrue(this.getCtx().isVariableName("thiz"));		

		// takes the first field
		assertEquals(1, moduleDeclaration.getFields().size());
		JField field = moduleDeclaration.getFields().iterator().next();
		assertEquals("size", field.getFieldVariable().getVariableId()
				.getString());

		JType type = field.getFieldType();
		assertTrue("Type must not include null", !JTypeHelper.fromIncludesNull(type));
		assertEquals("base type must be Int", "Int", type.singletonFrom());
		assertTrue(this.getCtx().isVariableName("size"));

		//program declaration
		assertEquals(1, moduleDeclaration.getPrograms().size());
		JProgramDeclaration programDeclaration = moduleDeclaration
				.getPrograms().iterator().next();
		assertEquals("ar_edu_dynjml4alloy_jml_LinkList", programDeclaration
				.getSignatureId());
		assertEquals("get", programDeclaration.getProgramId());
		assertEquals(4, programDeclaration.getParameters().size());
		assertEquals(1, programDeclaration.getSpecCases().size());
		assertEquals(1, programDeclaration.getSpecCases().get(0).getEnsures()
				.size());
		assertEquals(1, programDeclaration.getSpecCases().get(0).getRequires()
				.size());
		assertEquals(0, programDeclaration.getSpecCases().get(0).getModifies()
				.size());
		
		assertTrue(programDeclaration.getBody() instanceof JBlock);
		assertTrue(((JBlock)programDeclaration.getBody()).getBlock().get(0) instanceof JAssignment);
		{
			//object invariant
			assertEquals(1, moduleDeclaration.getObjectInvariants().size());
			JObjectInvariant objectInvariant = moduleDeclaration.getObjectInvariants().iterator().next();
			AlloyFormula objectInvariantAlloyFormula = objectInvariant.getFormula();
			assertTrue("Instance of PredicateFormula", objectInvariantAlloyFormula instanceof PredicateFormula);
			PredicateFormula objectInvariantPredicateFormula = (PredicateFormula) objectInvariantAlloyFormula;
			assertEquals("equ", objectInvariantPredicateFormula.getPredicateId());
			assertEquals(2, objectInvariantPredicateFormula.getParameters().size());
		}

//		{
//			//class invariant
//			assertEquals(1, moduleDeclaration.getClassInvariants().size());
//			JClassInvariant classInvariant = moduleDeclaration.getClassInvariants().iterator().next();
//			AlloyFormula classInvariantAlloyFormula = classInvariant.getFormula();
//			assertTrue("Instance of PredicateFormula", classInvariantAlloyFormula instanceof PredicateFormula);
//			PredicateFormula classInvariantPredicateFormula = (PredicateFormula) classInvariantAlloyFormula;
//			assertEquals("neq", classInvariantPredicateFormula.getPredicateId());
//			assertEquals(2, classInvariantPredicateFormula.getParameters().size());
//		}
//
//		{
//			//class constraint
//			assertEquals(1, moduleDeclaration.getClassConstraints().size());
//			JClassConstraint classConstraint = moduleDeclaration.getClassConstraints().iterator().next();
//			AlloyFormula classConstraintAlloyFormula = classConstraint.getFormula();
//			assertTrue("Instance of PredicateFormula", classConstraintAlloyFormula instanceof PredicateFormula);
//			PredicateFormula classInvariantPredicateFormula = (PredicateFormula) classConstraintAlloyFormula;
//			assertEquals("or", classInvariantPredicateFormula.getPredicateId());
//			assertEquals(2, classInvariantPredicateFormula.getParameters().size());
//		}
				
		{
			//object constraint
			assertEquals(1, moduleDeclaration.getObjectInvariants().size());
			JObjectConstraint objectConstraint = moduleDeclaration.getObjectConstraints().iterator().next();
			AlloyFormula objectConstraintAlloyFormula = objectConstraint.getFormula();
			assertTrue("Instance of PredicateFormula", objectConstraintAlloyFormula instanceof PredicateFormula);
			PredicateFormula objectConstraintPredicateFormula = (PredicateFormula) objectConstraintAlloyFormula;
			assertEquals("lt", objectConstraintPredicateFormula.getPredicateId());
			assertEquals(2, objectConstraintPredicateFormula.getParameters().size());
		}
		
		//represents
		assertEquals(1, moduleDeclaration.getRepresents().size());
		JRepresents represent = moduleDeclaration.getRepresents().iterator().next();
		JType representType = represent.getExpressionType();
		assertTrue("Type include null", !JTypeHelper.fromIncludesNull(representType));
		assertEquals("base type must be Int", "Int",representType.dpdTypeNameExtract());
		
		AlloyFormula representsAlloyFormula1 = represent.getFormula();
		assertTrue("Instance of PredicateFormula", representsAlloyFormula1 instanceof PredicateFormula);
		PredicateFormula representsPredicateFormula1 = (PredicateFormula) representsAlloyFormula1;
		assertEquals("equ", representsPredicateFormula1.getPredicateId());
		assertEquals(2, representsPredicateFormula1.getParameters().size());
			
		
	}

	public void testJDynAlloyModuleInterface() throws RecognitionException, TokenStreamException {
		String text = "module Geometric" + "\n" 
		+ "sig Geometric in java_lang_Object {} { Geometric = Point + Circle}" + "\n";

		JDynAlloyModule moduleDeclaration = this.initializeParser(text).dynJmlAlloyModule(this.getCtx());
		assertEquals("java_lang_Object", moduleDeclaration.getSignature().getInSignatureId());
		assertEquals(1,moduleDeclaration.getSignature().getFacts().size());
		AlloyFormula fact = moduleDeclaration.getSignature().getFacts().iterator().next(); 
		assertTrue(fact instanceof EqualsFormula);
		EqualsFormula equFormula = (EqualsFormula) fact;

		assertTrue(equFormula.getLeft() instanceof ExprConstant);
		
		
		assertTrue(equFormula.getRight() instanceof ExprUnion);
		ExprUnion exprUnion = (ExprUnion) equFormula.getRight();
		assertTrue(exprUnion.getLeft() instanceof ExprConstant);
		ExprConstant left = (ExprConstant) exprUnion.getLeft();
		assertEquals("Point", left.getConstantId());
		
		assertTrue(exprUnion.getRight() instanceof ExprConstant);
		ExprConstant right = (ExprConstant) exprUnion.getRight();
		assertEquals("Circle", right.getConstantId());
		
		assertTrue(this.getCtx().isVariableName("thiz"));

	}
	
	public void testJDynAlloyModuleWithoutExtendsOrIn() throws RecognitionException,
	TokenStreamException {
String text = ""
		+ "module ar_edu_dynjml4alloy_jml_LinkList"
		+ "\n"
		+ "sig ar_edu_dynjml4alloy_jml_LinkList {} {}"
		+ "\n"
		+ "\n"
		+ "program ar_edu_dynjml4alloy_jml_LinkList::get["
		+ "\n"
		+ "var thiz:ar_edu_dynjml4alloy_jml_LinkList,"
		+ "\n"
		+ "var throw:AssertionFailure+java_lang_Exception+null,"
		+ "\n"
		+ "var return:ar_edu_dynjml4alloy_jml_LinkList_inner_Value+null,"
		+ "\n"
		+ "var index:Int] "
		+ "\n"
		+ "Specification "
		+ "\n"
		+ "{ SpecCase #0 { requires { lt[0,index] } ensures { TruePred[] } } }"
		+ "\n" + "Implementation " + "\n" + "{ " + "\n"
		+ "	return:=index;" + "\n" + "}";


		JDynAlloyModule moduleDeclaration = this.initializeParser(text).dynJmlAlloyModule(this.getCtx());

		assertTrue(this.getCtx().isVariableName("thiz"));

		// program declaration
		assertEquals(1, moduleDeclaration.getPrograms().size());
		JProgramDeclaration programDeclaration = moduleDeclaration.getPrograms().iterator().next();
		assertEquals("ar_edu_dynjml4alloy_jml_LinkList", programDeclaration.getSignatureId());
		assertEquals("get", programDeclaration.getProgramId());
		assertEquals(4, programDeclaration.getParameters().size());
		assertEquals(1, programDeclaration.getSpecCases().size());
		assertEquals(1, programDeclaration.getSpecCases().get(0).getEnsures().size());
		assertEquals(1, programDeclaration.getSpecCases().get(0).getRequires().size());
		assertEquals(0, programDeclaration.getSpecCases().get(0).getModifies().size());

		assertTrue(programDeclaration.getBody() instanceof JBlock);
		assertTrue(((JBlock) programDeclaration.getBody()).getBlock().get(0) instanceof JAssignment);


	}	
}
