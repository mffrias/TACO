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
/*
 * TODO: all test fail because in the source class the methods are commented. Check with the "Old" instead of "base" version of the source files
 */
public class RoopsAvlTreeUBoundTest extends CollectionTestBase {

	@Override
	protected String getClassToCheck() {
		return "roops.core.objects.avltree.base.AvlTree";
	}
	
	public void test_fminTest() throws VizException {
		setConfigKeyRelevantClasses("roops.core.objects.avltree.base.AvlTree,roops.core.objects.avltree.base.AvlNode");
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
		setConfigKeyLoopUnroll(1);
		setConfigKeyIntBithwidth(4);
		setConfigKeyObjectScope(3);
		setConfigKeyTypeScopes("roops.core.objects.avltree.base.AvlTree:1,roops.core.objects.avltree.base.AvlNode:15");
		runAndCheck(GENERIC_PROPERTIES,"fminTest_0", true);
	}
}
