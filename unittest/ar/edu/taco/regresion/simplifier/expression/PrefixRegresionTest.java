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
package ar.edu.taco.regresion.simplifier.expression;

import ar.edu.taco.regresion.GenericTestBase;
import ar.uba.dc.rfm.dynalloy.visualization.VizException;

public class PrefixRegresionTest extends GenericTestBase {

	@Override
	protected String getClassToCheck() {
		return "ar.edu.taco.simplifier.prefix.PrefixTest";
	}
	
	public void testRunAndCheck_prefix1TestPass() throws VizException {
		runAndCheck(GENERIC_PROPERTIES,"prefix1TestPass_0", false);
	}

	public void testRunAndCheck_prefix2TestPass() throws VizException {
		runAndCheck(GENERIC_PROPERTIES,"prefix2TestPass_0", false);
	}

	public void testRunAndCheck_prefix3TestPass() throws VizException {
		runAndCheck(GENERIC_PROPERTIES,"prefix3TestPass_0", false);
	}

	public void testRunAndCheck_prefix4TestFail() throws VizException {
		runAndCheck(GENERIC_PROPERTIES,"prefix4TestFail_0", true);
	}

	public void testRunAndCheck_prefix5TestPass() throws VizException {
		setConfigKeyIntBithwidth(5);
		runAndCheck(GENERIC_PROPERTIES,"prefix5TestPass_0", false);
	}

}
