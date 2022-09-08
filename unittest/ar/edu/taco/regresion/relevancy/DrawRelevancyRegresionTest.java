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
package ar.edu.taco.regresion.relevancy;

import ar.edu.jdynalloy.JDynAlloyConfig;
import ar.edu.taco.regresion.GenericTestBase;
import ar.uba.dc.rfm.dynalloy.visualization.VizException;

public class DrawRelevancyRegresionTest extends GenericTestBase {

	@Override
	protected String getClassToCheck() {
		return "ar.edu.taco.relevancy.Draw";
	}

	public void testRule3_a_OK() throws VizException {
		setConfigKeyClasses("ar.edu.taco.relevancy.Rectangle, ar.edu.taco.relevancy.Point, ar.edu.taco.relevancy.Draw");
		setConfigKeyRelevancyAnalysis(true);
		
		runAndCheck(GENERIC_PROPERTIES,"f_0", false);
		assertTrue(JDynAlloyConfig.getInstance().getRelevantClasses().contains("ar_edu_taco_relevancy_Point"));
		assertFalse(JDynAlloyConfig.getInstance().getRelevantClasses().contains("ar_edu_taco_relevancy_Circle"));
		assertFalse(JDynAlloyConfig.getInstance().getRelevantClasses().contains("ar_edu_taco_relevancy_Rectangle"));
	}

	public void testRule3_b_OK() throws VizException {
		setConfigKeyClasses("ar.edu.taco.relevancy.Circle, ar.edu.taco.relevancy.Point, ar.edu.taco.relevancy.Rectangle, ar.edu.taco.relevancy.Draw");
		setConfigKeyRelevancyAnalysis(true);
		
		runAndCheck(GENERIC_PROPERTIES,"g_0", false);
		assertFalse(JDynAlloyConfig.getInstance().getRelevantClasses().contains("ar_edu_taco_relevancy_Point"));
		assertTrue(JDynAlloyConfig.getInstance().getRelevantClasses().contains("ar_edu_taco_relevancy_Circle"));
		assertFalse(JDynAlloyConfig.getInstance().getRelevantClasses().contains("ar_edu_taco_relevancy_Rectangle"));
		
	}
	

	public void testRule4_b_OK() throws VizException {
		setConfigKeyClasses("ar.edu.taco.relevancy.Rectangle,ar.edu.taco.relevancy.Circle, ar.edu.taco.relevancy.Point, ar.edu.taco.relevancy.Draw");
		setConfigKeyRelevancyAnalysis(true);
		
		runAndCheck(GENERIC_PROPERTIES,"rule4_b_0", false);
		assertTrue(JDynAlloyConfig.getInstance().getRelevantClasses().contains("ar_edu_taco_relevancy_Rectangle"));		
		assertFalse(JDynAlloyConfig.getInstance().getRelevantClasses().contains("ar_edu_taco_relevancy_Point"));
		assertFalse(JDynAlloyConfig.getInstance().getRelevantClasses().contains("ar_edu_taco_relevancy_Circle"));
	}
	
}