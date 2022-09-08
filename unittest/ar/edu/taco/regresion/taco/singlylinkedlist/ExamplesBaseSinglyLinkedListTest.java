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

public class ExamplesBaseSinglyLinkedListTest extends CollectionTestBase {

	@Override
	protected String getClassToCheck() {
		return "examples.singlylinkedlist.base.SinglyLinkedList";
	}
	
	public void test_contains() throws VizException {
		setConfigKeyRelevantClasses("examples.singlylinkedlist.base.SinglyLinkedList,examples.singlylinkedlist.base.SinglyLinkedListNode");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyCheckNullDereference(true);
		setConfigKeyUseJavaArithmetic(false);
		setConfigKeyObjectScope(3);
		setConfigKeyInferScope(false);
		setConfigKeyTypeScopes("examples.singlylinkedlist.base.SinglyLinkedList:1,examples.singlylinkedlist.base.SinglyLinkedListNode:2");
		
		setConfigKeySkolemizeInstanceInvariant(true);
		setConfigKeySkolemizeInstanceAbstraction(true);
		setConfigKeyGenerateUnitTestCase(false);
		simulate(GENERIC_PROPERTIES,"contains_0");
	}
	
	public void test_insertBack() throws VizException {
		setConfigKeyRelevantClasses("examples.singlylinkedlist.base.SinglyLinkedList,examples.singlylinkedlist.base.SinglyLinkedListNode");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyCheckNullDereference(true);
		setConfigKeyUseJavaArithmetic(false);
		setConfigKeyObjectScope(3);
		setConfigKeyInferScope(false);
		setConfigKeyTypeScopes("examples.singlylinkedlist.base.SinglyLinkedList:1,examples.singlylinkedlist.base.SinglyLinkedListNode:2");
		setConfigKeySkolemizeInstanceInvariant(true);
		setConfigKeySkolemizeInstanceAbstraction(true);
		setConfigKeyGenerateUnitTestCase(false);
		simulate(GENERIC_PROPERTIES,"insertBack_0");
	}
	
	public void test_remove() throws VizException {
		setConfigKeyRelevantClasses("examples.singlylinkedlist.base.SinglyLinkedList,examples.singlylinkedlist.base.SinglyLinkedListNode");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyCheckNullDereference(true);
		setConfigKeyUseJavaArithmetic(false);
		setConfigKeyObjectScope(3);
		setConfigKeyInferScope(false);
		setConfigKeyTypeScopes("examples.singlylinkedlist.base.SinglyLinkedList:1,examples.singlylinkedlist.base.SinglyLinkedListNode:2");
		setConfigKeySkolemizeInstanceInvariant(true);
		setConfigKeySkolemizeInstanceAbstraction(true);
		setConfigKeyGenerateUnitTestCase(false);
		simulate(GENERIC_PROPERTIES,"remove_0");
	}

	
}
