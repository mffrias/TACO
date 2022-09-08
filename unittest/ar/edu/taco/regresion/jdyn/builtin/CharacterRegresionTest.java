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
package ar.edu.taco.regresion.jdyn.builtin;

import ar.edu.taco.regresion.GenericTestBase;
import ar.uba.dc.rfm.dynalloy.visualization.VizException;

public class CharacterRegresionTest extends GenericTestBase {

	@Override
	protected String getClassToCheck() {
		return "ar.edu.taco.builtin.CharacterTest";
	}


	public void testRunAndCheck_equals_Count() throws VizException {
		runAndCheck(GENERIC_PROPERTIES,"equals_Count_0", true);		
	}

	public void testRunAndCheck_hashCode_OK() throws VizException {
		runAndCheck(GENERIC_PROPERTIES,"hashCode_OK_0", false);		
	}

	public void testRunAndCheck_hashCode_Count() throws VizException {
		runAndCheck(GENERIC_PROPERTIES,"hashCode_Count_0", true);		
	}

	public void testRunAndCheck_toString_OK() throws VizException {
		runAndCheck(GENERIC_PROPERTIES,"toString_OK_0", false);		
	}

	public void testRunAndCheck_toString_Count() throws VizException {
		setConfigKeyObjectScope(4);
		runAndCheck(GENERIC_PROPERTIES,"toString_Count_0", true);		
	}

	public void testRunAndCheck_static_toString_OK() throws VizException {
		runAndCheck(GENERIC_PROPERTIES,"static_toString_OK_0", false);		
	}

	public void testRunAndCheck_static_toString_Count() throws VizException {
		runAndCheck(GENERIC_PROPERTIES,"static_toString_Count_0", true);		
	}
}
