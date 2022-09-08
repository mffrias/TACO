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

public class Fajita31TreeSetTest extends CollectionTestBase {

	@Override
	protected String getClassToCheck() {
		return "roops.core.objects.TreeSet";
	}
	
	public void test_addTest() throws VizException {
		setConfigKeyRelevantClasses("roops.core.objects.TreeSet,roops.core.objects.TreeSetEntry");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyCheckNullDereference(true);
		setConfigKeyUseJavaArithmetic(true);
		setConfigKeySkolemizeInstanceInvariant(false);
		setConfigKeySkolemizeInstanceAbstraction(false);
		setConfigKeyRemoveQuantifiers(true);
		// Infer-Scope
		setConfigKeyInferScope(false);
		setConfigKeyObjectScope(1);
		setConfigKeyLoopUnroll(1);
		setConfigKeyTypeScopes("roops.core.objects.TreeSet:1,roops.core.objects.TreeSetEntry:4");

		// SBP+BOUND
		setConfigKeyUseJavaSBP(true);
		setConfigKeyUseTightUpperBounds(true);
		// JUNIT
		setConfigKeyGenerateUnitTestCase(true);

		runAndCheck(GENERIC_PROPERTIES,"addTest_0", true);
	}
}
