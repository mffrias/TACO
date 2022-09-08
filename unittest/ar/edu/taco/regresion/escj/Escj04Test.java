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
package ar.edu.taco.regresion.escj;

import ar.uba.dc.rfm.dynalloy.visualization.VizException;

public class Escj04Test extends EscjTestBase {

	@Override
	protected String getClassToCheck() {
	    return "escj.test04.C";
	}

	public void testCheck1() throws VizException {
	    runAndCheck(ESCJ_PROPERTIES,"test1_0",false);		
	}

	public void testCheck2() throws VizException {
	    runAndCheck(ESCJ_PROPERTIES,"test2_0",false);		
	}
	
	public void testCheck3() throws VizException {
		runAndCheck(ESCJ_PROPERTIES,"test03_0",false);		
	}

	public void testCheck4() throws VizException {
	    runAndCheck(ESCJ_PROPERTIES,"test04_0",false);		
	}


}
