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
package ar.edu.taco.regresion.taco.cachelist;

import ar.edu.taco.regresion.CollectionTestBase;
import ar.edu.taco.regresion.GenericTestBase;
import ar.uba.dc.rfm.dynalloy.visualization.VizException;

public class RoopsNodeCachingLinkedListTest extends CollectionTestBase {

	@Override
	protected String getClassToCheck() {
		return "roops.core.objects.cachelist.base.NodeCachingLinkedList";
	}
	
	public void test_containsTest() throws VizException {
		setConfigKeyRelevantClasses("roops.core.objects.cachelist.base.NodeCachingLinkedList,roops.core.objects.linkedlist.base.LinkedListNode");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyCheckNullDereference(true);
		setConfigKeyUseJavaArithmetic(true);
		setConfigKeyObjectScope(3);
		setConfigKeyInferScope(true);
		setConfigKeySkolemizeInstanceInvariant(false);
		setConfigKeySkolemizeInstanceAbstraction(false);
		setConfigKeyGenerateUnitTestCase(false);
		runAndCheck(GENERIC_PROPERTIES,"containsTest_0", true);
	}
	
	public void test_addLastTest() throws VizException {
		setConfigKeyRelevantClasses("roops.core.objects.cachelist.base.NodeCachingLinkedList,roops.core.objects.linkedlist.base.LinkedListNode");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyCheckNullDereference(true);
		setConfigKeyUseJavaArithmetic(true);
		setConfigKeyObjectScope(3);
		setConfigKeyInferScope(true);
		setConfigKeySkolemizeInstanceInvariant(false);
		setConfigKeySkolemizeInstanceAbstraction(false);
		setConfigKeyGenerateUnitTestCase(false);
		
		setConfigKeyInferScope(true);
		runAndCheck(GENERIC_PROPERTIES,"addLastTest_0", true);
	}
	
	public void test_removeIndexTest() throws VizException {
		setConfigKeyRelevantClasses("roops.core.objects.cachelist.base.NodeCachingLinkedList,roops.core.objects.linkedlist.base.LinkedListNode");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyCheckNullDereference(true);
		setConfigKeyUseJavaArithmetic(true);
		setConfigKeyObjectScope(3);
		setConfigKeyInferScope(true);
		setConfigKeySkolemizeInstanceInvariant(false);
		setConfigKeySkolemizeInstanceAbstraction(false);
		setConfigKeyGenerateUnitTestCase(false);
		runAndCheck(GENERIC_PROPERTIES,"removeIndexTest_0", true);
	}
	
	public void test_setMaximumCacheSizeTest() throws VizException {
		setConfigKeyRelevantClasses("roops.core.objects.cachelist.base.NodeCachingLinkedList,roops.core.objects.linkedlist.base.LinkedListNode");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyCheckNullDereference(true);
		setConfigKeyUseJavaArithmetic(true);
		setConfigKeyObjectScope(3);
		setConfigKeyInferScope(true);
		setConfigKeySkolemizeInstanceInvariant(false);
		setConfigKeySkolemizeInstanceAbstraction(false);
		setConfigKeyGenerateUnitTestCase(false);
		runAndCheck(GENERIC_PROPERTIES,"setMaximumCacheSizeTest_0", true);
	}
}
