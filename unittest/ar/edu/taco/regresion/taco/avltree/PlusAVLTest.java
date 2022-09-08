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
package ar.edu.taco.regresion.taco.avltree;

import ar.edu.taco.regresion.CollectionTestBase;
import ar.edu.taco.regresion.GenericTestBase;
import ar.uba.dc.rfm.dynalloy.visualization.VizException;

public class PlusAVLTest extends CollectionTestBase {

	@Override
	protected String getClassToCheck() {
		return "examples.avltree.plus.AVL";
	}
	
	public void test_show_instance() throws VizException {
		setConfigKeyRelevantClasses("examples.avltree.plus.AVL,examples.avltree.plus.AVLNode");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyCheckNullDereference(true);
		setConfigKeyUseJavaArithmetic(false);
		setConfigKeyNestedLoopUnroll(true);
		setConfigKeyInferScope(false);
		setConfigKeyUseJavaSBP(true);
		setConfigKeyTypeScopes("examples.avltree.plus.AVL:1,examples.avltree.plus.AVLNode:4");
		setConfigKeySkolemizeInstanceInvariant(true);
		setConfigKeySkolemizeInstanceAbstraction(true);
		setConfigKeyLoopUnroll(1);
		setConfigKeyRemoveQuantifiers(true);
		setConfigKeyGenerateUnitTestCase(true);
		setConfigKeyNoVerify(false);
		runAndCheck(GENERIC_PROPERTIES,"show_instance_0",true);
	}
	
	public void test_remove() throws VizException {
		setConfigKeyRelevantClasses("examples.avltree.plus.AVL,examples.avltree.plus.AVLNode");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyCheckNullDereference(true);
		setConfigKeyUseJavaArithmetic(false);
		setConfigKeyNestedLoopUnroll(true);
		setConfigKeyInferScope(false);
		setConfigKeyUseJavaSBP(true);
		setConfigKeyTypeScopes("examples.avltree.plus.AVL:1,examples.avltree.plus.AVLNode:7");
		setConfigKeySkolemizeInstanceInvariant(true);
		setConfigKeySkolemizeInstanceAbstraction(true);
		setConfigKeyLoopUnroll(2);
		setConfigKeyRemoveQuantifiers(true);
		setConfigKeyGenerateUnitTestCase(true);
		setConfigKeyNoVerify(false);
		runAndCheck(GENERIC_PROPERTIES,"remove_0",true);
	}
}
