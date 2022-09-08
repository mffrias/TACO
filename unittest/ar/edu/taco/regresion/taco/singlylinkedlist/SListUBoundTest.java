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

public class SListUBoundTest extends CollectionTestBase {

	@Override
	protected String getClassToCheck() {
		return "roops.core.objects.singlylinkedlist.base.SinglyLinkedList";
	}
	
	public void test_containsTest() throws VizException {
		setConfigKeyRelevantClasses("roops.core.objects.singlylinkedlist.base.SinglyLinkedList,roops.core.objects.singlylinkedlist.base.SinglyLinkedListNode");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyCheckNullDereference(true);
		setConfigKeyUseJavaArithmetic(true);
		setConfigKeyRemoveQuantifiers(true);
		setConfigKeySkolemizeInstanceInvariant(false);
		setConfigKeySkolemizeInstanceAbstraction(false);
		setConfigKeyGenerateUnitTestCase(true);
		setConfigKeyUseJavaSBP(true);
		setConfigKeyUseTightUpperBounds(true);

		setConfigKeyInferScope(false);
		setConfigKeyIntBithwidth(4);
		setConfigKeyObjectScope(3);
		setConfigKeyTypeScopes("roops.core.objects.singlylinkedlist.base.SinglyLinkedList:1,roops.core.objects.singlylinkedlist.base.SinglyLinkedListNode:15");
		runAndCheck(GENERIC_PROPERTIES,"contains_0", true);
	}
}
