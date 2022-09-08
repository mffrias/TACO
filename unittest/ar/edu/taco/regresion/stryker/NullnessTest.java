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
package ar.edu.taco.regresion.stryker;

import ar.edu.taco.regresion.CollectionTestBase;
import ar.uba.dc.rfm.dynalloy.visualization.VizException;

public class NullnessTest extends CollectionTestBase {

	@Override
	protected String getClassToCheck() {
		return "roops.core.objects.Nullness";
	}
	
	public void test_Test000() throws VizException {
        setConfigKeyRelevantClasses("roops.core.objects.Nullness,roops.core.objects.NullnessHelper");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyCheckNullDereference(true);
		setConfigKeyUseJavaArithmetic(false);// fun_set_size
		setConfigKeyInferScope(false);
	    setConfigKeyIntBithwidth(4);
	    setConfigKeyLoopUnroll(10);
	    setConfigKeyObjectScope(0);
		setConfigKeySkolemizeInstanceInvariant(true);
		setConfigKeySkolemizeInstanceAbstraction(true);
		setConfigKeyGenerateUnitTestCase(true);
		setConfigKeyAttemptToCorrectBug(true);
		setConfigKeyRemoveQuantifiers(true);
		setConfigKeyUseJavaSBP(true);
		setConfigKeyUseTightUpperBounds(true);
		setConfigKeyMaxStrykerMethodsPerFile(50);
		setConfigKeyTypeScopes("roops.core.objects.Nullness:1,roops.core.objects.NullnessHelper:5");
		check(GENERIC_PROPERTIES,"Test000_0",false);
	}
}
