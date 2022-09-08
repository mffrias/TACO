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
package ar.edu.taco.regresion.taco.singlylinkedlist;

import ar.edu.taco.regresion.CollectionTestBase;
import ar.edu.taco.regresion.GenericTestBase;
import ar.uba.dc.rfm.dynalloy.visualization.VizException;

public class RoopsSinglyLinkedListTest extends CollectionTestBase {

	@Override
	protected String getClassToCheck() {
		return "roops.core.objects.singlylinkedlist.base.SinglyLinkedList";
	}
	
	public void test_getNodeTest() throws VizException {
		setConfigKeyRelevantClasses("roops.core.objects.singlylinkedlist.base.SinglyLinkedList,roops.core.objects.singlylinkedlist.base.SinglyLinkedListNode");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyCheckNullDereference(true);
		setConfigKeyUseJavaArithmetic(true);
		setConfigKeyObjectScope(3);
		setConfigKeyInferScope(true);
//		this.setConfigKeyRemoveQuantifiers(false);
		setConfigKeySkolemizeInstanceInvariant(false);
		setConfigKeySkolemizeInstanceAbstraction(false);
		setConfigKeyGenerateUnitTestCase(true);
		check(GENERIC_PROPERTIES,"getNode_0", false);
	}
	
	
	
	public void test_containsTest() throws VizException {
		setConfigKeyRelevantClasses("roops.core.objects.singlylinkedlist.base.SinglyLinkedList,roops.core.objects.singlylinkedlist.base.SinglyLinkedListNode");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyCheckNullDereference(true);
		setConfigKeyUseJavaArithmetic(true);
		setConfigKeyObjectScope(3);
		setConfigKeyInferScope(true);
		setConfigKeySkolemizeInstanceInvariant(false);
		setConfigKeySkolemizeInstanceAbstraction(false);
		setConfigKeyGenerateUnitTestCase(false);
		runAndCheck(GENERIC_PROPERTIES,"contains_0", true);
	}

	
/*  FIXME: Test disabled because class roops.core.objects.singlylinkedlist.base.SinglyLinkedList doesn't have an insertFront method (although it has an insertBack). 
	public void test_insertFrontTest() throws VizException {
		setConfigKeyRelevantClasses("roops.core.objects.singlylinkedlist.base.SinglyLinkedList,roops.core.objects.singlylinkedlist.base.SinglyLinkedListNode");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyCheckNullDereference(true);
		setConfigKeyUseJavaArithmetic(true);
		setConfigKeyObjectScope(3);
		setConfigKeyInferScope(true);
		setConfigKeySkolemizeInstanceInvariant(false);
		setConfigKeySkolemizeInstanceAbstraction(false);
		setConfigKeyGenerateUnitTestCase(false);
		runAndCheck(GENERIC_PROPERTIES,"insertFront_0", true);
	}
	*/
	
	/*  FIXME: Test disabled because class roops.core.objects.singlylinkedlist.base.SinglyLinkedList doesn't have a remove method. 
	public void test_removeTest() throws VizException {
		setConfigKeyRelevantClasses("roops.core.objects.singlylinkedlist.base.SinglyLinkedList,roops.core.objects.singlylinkedlist.base.SinglyLinkedListNode");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyCheckNullDereference(true);
		setConfigKeyUseJavaArithmetic(true);
		setConfigKeyObjectScope(3);
		setConfigKeyInferScope(true);
		setConfigKeySkolemizeInstanceInvariant(false);
		setConfigKeySkolemizeInstanceAbstraction(false);
		setConfigKeyGenerateUnitTestCase(false);
		runAndCheck(GENERIC_PROPERTIES,"removeTest_0", true);
	}
	*/
	
}
