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

public class Escj03Test extends EscjTestBase {

	@Override
	protected String getClassToCheck() {
	    return "escj.test03.C";
	}

	public void testCheck0() throws VizException {
	    runAndCheck(ESCJ_PROPERTIES,"test0_0",false);		
	}

	public void testCheck1() throws VizException {
	    runAndCheck(ESCJ_PROPERTIES,"test1_0",false);		
	}
	
	public void testCheck2() throws VizException {
	    runAndCheck(ESCJ_PROPERTIES,"test2_0",true);		
	}

	public void testCheck3() throws VizException {
	    runAndCheck(ESCJ_PROPERTIES,"test3_0",false);		
	}

	public void testCheck4() throws VizException {
	    runAndCheck(ESCJ_PROPERTIES,"test4_0",false);		
	}
	
	public void testCheck5() throws VizException {
	    runAndCheck(ESCJ_PROPERTIES,"test5_0",true);		
	}

	public void testCheck6() throws VizException {	
	    notInstance(ESCJ_PROPERTIES,"test6_0");
	}

	public void testCheck7() throws VizException {
	    notInstance(ESCJ_PROPERTIES,"test7_0");		
	}

}
