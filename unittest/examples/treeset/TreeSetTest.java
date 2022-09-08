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
package examples.treeset;

import ar.edu.taco.regresion.CollectionTestBase;
import ar.edu.taco.regresion.GenericTestBase;
import ar.uba.dc.rfm.dynalloy.visualization.VizException;

public class TreeSetTest extends CollectionTestBase {

	@Override
	protected String getClassToCheck() {
		return "examples.treeset.TreeSet";
	}
	
	public void test_add() throws VizException {
		setConfigKeyRelevantClasses("examples.treeset.TreeSet,examples.treeset.TreeSetEntry");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyCheckNullDereference(true);
		setConfigKeyUseJavaArithmetic(true);
		setConfigKeyNestedLoopUnroll(true);
		setConfigKeyInferScope(false);
		setConfigKeyUseJavaSBP(true);
		setConfigKeyTypeScopes("examples.treeset.TreeSet:1,examples.treeset.TreeSetEntry:4");
		setConfigKeySkolemizeInstanceInvariant(true);
		setConfigKeySkolemizeInstanceAbstraction(true);
		setConfigKeyLoopUnroll(10);
		setConfigKeyRemoveQuantifiers(true);
		setConfigKeyGenerateUnitTestCase(true);
		setConfigKeyNoVerify(true);
		runAndCheck(GENERIC_PROPERTIES,"add_0",false);
	}

	public void test_remove() throws VizException {
		setConfigKeyRelevantClasses("examples.treeset.TreeSet,examples.treeset.TreeSetEntry");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyCheckNullDereference(false);
		setConfigKeyUseJavaArithmetic(false);
		setConfigKeyNestedLoopUnroll(true);
		setConfigKeyInferScope(false);
		setConfigKeyUseJavaSBP(false);
		setConfigKeyTypeScopes("examples.treeset.TreeSet:1,examples.treeset.TreeSetEntry:5");
		setConfigKeySkolemizeInstanceInvariant(true);
		setConfigKeySkolemizeInstanceAbstraction(true);
		setConfigKeyLoopUnroll(10);
		setConfigKeyRemoveQuantifiers(true);
		setConfigKeyGenerateUnitTestCase(false);
		setConfigKeyNoVerify(true);
		runAndCheck(GENERIC_PROPERTIES,"remove_0",true);
	}
}
