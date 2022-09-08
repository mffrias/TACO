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
package ar.edu.taco.regresion.infer;

import ar.edu.taco.infer.InferredScope;
import ar.edu.taco.regresion.CollectionTestBase;
import ar.edu.taco.regresion.GenericTestBase;
import ar.uba.dc.rfm.dynalloy.visualization.VizException;

public class PScopeArithmeticOpTest extends CollectionTestBase {

	@Override
	protected String getClassToCheck() {
		return "ar.edu.taco.infer.PScopeArithmetic";
	}
	
	public void test_use_integer_operations() throws VizException {
		setConfigKeyRelevantClasses("ar.edu.taco.infer.PScopeArithmetic");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyCheckNullDereference(true);
		setConfigKeyUseJavaArithmetic(true);
		setConfigKeySkolemizeInstanceInvariant(false);
		setConfigKeySkolemizeInstanceAbstraction(false);
		setConfigKeyLoopUnroll(1);
		setConfigKeyRemoveQuantifiers(true);	
		setConfigKeyGenerateUnitTestCase(true);
		setConfigKeyAbstractSignatureObject(false);
		
		setConfigKeyUseJavaSBP(false);
		setConfigKeyUseTightUpperBounds(false);

		setConfigKeyInferScope(false);
		setConfigKeyIntBithwidth(3);
		setConfigKeyObjectScope(3);
		setConfigKeyTypeScopes("ar.edu.taco.infer.PScopeArithmetic:1,int:15");
		setConfigKeyInferScope(true);
		
		runAndCheck(GENERIC_PROPERTIES,"use_integer_operations_0", true);
		
		InferredScope inferred_scope = InferredScope.getInstance();
		assertNotNull(inferred_scope);
		
		// input scope
		assertEquals(1, inferred_scope.getInferredInputScope("ar_edu_taco_infer_PScopeArithmetic").int_value);

		// program scope
		assertEquals(0, inferred_scope.getInferredProgramScope("ar_edu_taco_infer_PScopeArithmetic"));
		assertEquals(22, inferred_scope.getInferredProgramScope("JavaPrimitiveIntegerValue"));
		assertEquals(0, inferred_scope.getInferredProgramScope("JavaPrimitiveLongValue"));
		assertEquals(0, inferred_scope.getInferredProgramScope("JavaPrimitiveFloatValue"));
	}


	public void test_use_long_operations() throws VizException {
		setConfigKeyRelevantClasses("ar.edu.taco.infer.PScopeArithmetic");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyCheckNullDereference(true);
		setConfigKeyUseJavaArithmetic(true);
		setConfigKeySkolemizeInstanceInvariant(false);
		setConfigKeySkolemizeInstanceAbstraction(false);
		setConfigKeyLoopUnroll(1);
		setConfigKeyRemoveQuantifiers(true);	
		setConfigKeyGenerateUnitTestCase(true);
		setConfigKeyAbstractSignatureObject(false);
		
		setConfigKeyUseJavaSBP(false);
		setConfigKeyUseTightUpperBounds(false);

		setConfigKeyInferScope(false);
		setConfigKeyIntBithwidth(3);
		setConfigKeyObjectScope(3);
		setConfigKeyTypeScopes("ar.edu.taco.infer.PScopeArithmetic:1,long:15");
		setConfigKeyInferScope(true);
		
		runAndCheck(GENERIC_PROPERTIES,"use_long_operations_0", true);
		
		InferredScope inferred_scope = InferredScope.getInstance();
		assertNotNull(inferred_scope);
		
		// input scope
		assertEquals(1, inferred_scope.getInferredInputScope("ar_edu_taco_infer_PScopeArithmetic").int_value);

		// program scope
		assertEquals(0, inferred_scope.getInferredProgramScope("ar_edu_taco_infer_PScopeArithmetic"));
		assertEquals(0, inferred_scope.getInferredProgramScope("JavaPrimitiveIntegerValue"));
		assertEquals(27, inferred_scope.getInferredProgramScope("JavaPrimitiveLongValue"));
		assertEquals(0, inferred_scope.getInferredProgramScope("JavaPrimitiveFloatValue"));
		
	}
	
	public void test_use_float_operations() throws VizException {
		setConfigKeyRelevantClasses("ar.edu.taco.infer.PScopeArithmetic");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyCheckNullDereference(true);
		setConfigKeyUseJavaArithmetic(true);
		setConfigKeySkolemizeInstanceInvariant(false);
		setConfigKeySkolemizeInstanceAbstraction(false);
		setConfigKeyLoopUnroll(1);
		setConfigKeyRemoveQuantifiers(true);	
		setConfigKeyGenerateUnitTestCase(true);
		setConfigKeyAbstractSignatureObject(false);
		
		setConfigKeyUseJavaSBP(false);
		setConfigKeyUseTightUpperBounds(false);

		setConfigKeyInferScope(false);
		setConfigKeyIntBithwidth(3);
		setConfigKeyObjectScope(3);
		setConfigKeyTypeScopes("ar.edu.taco.infer.PScopeArithmetic:1,float:15");
		setConfigKeyInferScope(true);
		
		runAndCheck(GENERIC_PROPERTIES,"use_float_operations_0", true);
		
		InferredScope inferred_scope = InferredScope.getInstance();
		assertNotNull(inferred_scope);

		// input scope
		assertEquals(1, inferred_scope.getInferredInputScope("ar_edu_taco_infer_PScopeArithmetic").int_value);

		// program scope
		assertEquals(0, inferred_scope.getInferredProgramScope("ar_edu_taco_infer_PScopeArithmetic"));
		assertEquals(0, inferred_scope.getInferredProgramScope("JavaPrimitiveIntegerValue"));
		assertEquals(0, inferred_scope.getInferredProgramScope("JavaPrimitiveLongValue"));
		assertEquals(6, inferred_scope.getInferredProgramScope("JavaPrimitiveFloatValue"));
		
		
	}

	public void test_use_float_operations_unroll() throws VizException {
		setConfigKeyRelevantClasses("ar.edu.taco.infer.PScopeArithmetic");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyCheckNullDereference(true);
		setConfigKeyUseJavaArithmetic(true);
		setConfigKeySkolemizeInstanceInvariant(false);
		setConfigKeySkolemizeInstanceAbstraction(false);
		setConfigKeyRemoveQuantifiers(true);	
		setConfigKeyGenerateUnitTestCase(true);
		setConfigKeyAbstractSignatureObject(false);
		
		setConfigKeyInferScope(true);
		setConfigKeyUseJavaSBP(false);
		setConfigKeyUseTightUpperBounds(false);

		setConfigKeyInferScope(false);
		setConfigKeyIntBithwidth(3);
		setConfigKeyObjectScope(3);
		setConfigKeyLoopUnroll(4);
		setConfigKeyTypeScopes("ar.edu.taco.infer.PScopeArithmetic:1,float:18,int:7");
		setConfigKeyInferScope(true);
		
		runAndCheck(GENERIC_PROPERTIES,"use_float_operations_unroll_0", true);
		
		InferredScope inferred_scope = InferredScope.getInstance();
		assertNotNull(inferred_scope);
		
		// input scope
		assertEquals(1, inferred_scope.getInferredInputScope("ar_edu_taco_infer_PScopeArithmetic").int_value);

		// program scope
		assertEquals(0, inferred_scope.getInferredProgramScope("ar_edu_taco_infer_PScopeArithmetic"));
		assertEquals(7, inferred_scope.getInferredProgramScope("JavaPrimitiveIntegerValue"));
		assertEquals(0, inferred_scope.getInferredProgramScope("JavaPrimitiveLongValue"));
		assertEquals(18, inferred_scope.getInferredProgramScope("JavaPrimitiveFloatValue"));
		
	}



}
