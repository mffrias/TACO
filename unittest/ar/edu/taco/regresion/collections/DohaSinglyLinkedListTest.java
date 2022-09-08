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
package ar.edu.taco.regresion.collections;

import ar.edu.taco.regresion.CollectionTestBase;
import ar.uba.dc.rfm.dynalloy.visualization.VizException;

public class DohaSinglyLinkedListTest extends CollectionTestBase {

	@Override
	protected String getClassToCheck() {
		return "roops.core.objects.DohaSinglyLinkedList";
	}
	
	public void test_containsTest() throws VizException {
		setConfigKeyRelevantClasses("roops.core.objects.DohaSinglyLinkedList,roops.core.objects.DohaSinglyLinkedListNode");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyCheckNullDereference(true);
		setConfigKeyUseJavaArithmetic(false);
		setConfigKeyInferScope(false);
		setConfigKeyObjectScope(3);
		setConfigKeyIntBithwidth(4);
        setConfigKeyLoopUnroll(7);
		setConfigKeySkolemizeInstanceInvariant(true);
		setConfigKeySkolemizeInstanceAbstraction(true);
		setConfigKeyGenerateUnitTestCase(true);
		setConfigKeyAttemptToCorrectBug(false);
		setConfigKeyMaxStrykerMethodsPerFile(50);
		setConfigKeyRemoveQuantifiers(true);
//		setConfigKeyUseJavaSBP(true);
//		setConfigKeyUseTightUpperBounds(true);
		setConfigKeyTypeScopes("roops.core.objects.DohaSinglyLinkedList:1,roops.core.objects.DohaSinglyLinkedListNode:5");
		check(GENERIC_PROPERTIES,"contains_0",false);
	}
	
	
}
