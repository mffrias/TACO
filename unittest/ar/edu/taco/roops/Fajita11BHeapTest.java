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
package ar.edu.taco.roops;

import ar.edu.taco.regresion.CollectionTestBase;
import ar.edu.taco.regresion.GenericTestBase;
import ar.uba.dc.rfm.dynalloy.visualization.VizException;

public class Fajita11BHeapTest extends CollectionTestBase {

	@Override
	protected String getClassToCheck() {
		return "roops.core.objects.BinomialHeap";
	}
	
	public void test_findMinTest() throws VizException {
		setConfigKeyRelevantClasses("roops.core.objects.BinomialHeap,roops.core.objects.BinomialHeapNode");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyCheckNullDereference(true);
		setConfigKeyUseJavaArithmetic(true);
		setConfigKeySkolemizeInstanceInvariant(false);
		setConfigKeySkolemizeInstanceAbstraction(false);
		setConfigKeyRemoveQuantifiers(true);
		// Infer-Scope
		setConfigKeyInferScope(true);
		setConfigKeyObjectScope(1);
		setConfigKeyLoopUnroll(1);
		// SBP+BOUND
		setConfigKeyUseJavaSBP(true);
		setConfigKeyUseTightUpperBounds(true);
		// JUNIT
		setConfigKeyGenerateUnitTestCase(true);

		runAndCheck(GENERIC_PROPERTIES,"findMinTest_0", true);
	}
	
	public void test_insertTest() throws VizException {
		setConfigKeyRelevantClasses("roops.core.objects.BinomialHeap,roops.core.objects.BinomialHeapNode");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyCheckNullDereference(true);
		setConfigKeyUseJavaArithmetic(true);
		setConfigKeySkolemizeInstanceInvariant(false);
		setConfigKeySkolemizeInstanceAbstraction(false);
		setConfigKeyRemoveQuantifiers(true);
		// Infer-Scope
		setConfigKeyInferScope(true);
		setConfigKeyObjectScope(1);
		setConfigKeyLoopUnroll(1);
		// SBP+BOUND
		setConfigKeyUseJavaSBP(true);
		setConfigKeyUseTightUpperBounds(true);
		// JUNIT
		setConfigKeyGenerateUnitTestCase(true);

		runAndCheck(GENERIC_PROPERTIES,"insertTest_0", true);
	}
	
	public void test_extractMinTest() throws VizException {
		setConfigKeyRelevantClasses("roops.core.objects.BinomialHeap,roops.core.objects.BinomialHeapNode");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyCheckNullDereference(true);
		setConfigKeyUseJavaArithmetic(true);
		setConfigKeySkolemizeInstanceInvariant(false);
		setConfigKeySkolemizeInstanceAbstraction(false);
		setConfigKeyRemoveQuantifiers(true);
		// Infer-Scope
		setConfigKeyInferScope(true);
		setConfigKeyObjectScope(1);
		setConfigKeyLoopUnroll(1);
		// SBP+BOUND
		setConfigKeyUseJavaSBP(true);
		setConfigKeyUseTightUpperBounds(true);
		// JUNIT
		setConfigKeyGenerateUnitTestCase(true);

		runAndCheck(GENERIC_PROPERTIES,"extractMinTest_0", true);
	}
	
	public void test_decreaseKeyNodeTest() throws VizException {
		setConfigKeyRelevantClasses("roops.core.objects.BinomialHeap,roops.core.objects.BinomialHeapNode");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyCheckNullDereference(true);
		setConfigKeyUseJavaArithmetic(true);
		setConfigKeySkolemizeInstanceInvariant(false);
		setConfigKeySkolemizeInstanceAbstraction(false);
		setConfigKeyRemoveQuantifiers(true);
		// Infer-Scope
		setConfigKeyInferScope(true);
		setConfigKeyObjectScope(1);
		setConfigKeyLoopUnroll(1);
		// SBP+BOUND
		setConfigKeyUseJavaSBP(true);
		setConfigKeyUseTightUpperBounds(true);
		// JUNIT
		setConfigKeyGenerateUnitTestCase(true);

		runAndCheck(GENERIC_PROPERTIES,"decreaseKeyNodeTest_0", true);
	}
}
