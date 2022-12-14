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
package ar.edu.itba.alu.crpereyr.linkedlist1;

import ar.edu.taco.regresion.CollectionTestBase;
import ar.uba.dc.rfm.dynalloy.visualization.VizException;

public class SinglyLinkedListTest extends CollectionTestBase {

	@Override
	protected String getClassToCheck() {
		return "ar.edu.itba.alu.crpereyr.impl.linkedlist1.SinglyLinkedList";
	}

	public void test_addTest() throws VizException {
		setConfigKeyRelevantClasses("ar.edu.itba.alu.crpereyr.impl.linkedlist1.SinglyLinkedList,ar.edu.itba.alu.crpereyr.impl.linkedlist1.Node");
        setConfigKeyRelevancyAnalysis(true);
        setConfigKeyCheckNullDereference(true);
        setConfigKeyUseJavaArithmetic(true);
        setConfigKeyObjectScope(3);
        setConfigKeyInferScope(true);
        setConfigKeySkolemizeInstanceInvariant(false);
        setConfigKeySkolemizeInstanceAbstraction(false);
        setConfigKeyGenerateUnitTestCase(true);
		check(GENERIC_PROPERTIES,"add_0",false);
	}

    public void test_getTest() throws VizException {
        setConfigKeyRelevantClasses("ar.edu.itba.alu.crpereyr.impl.linkedlist1.SinglyLinkedList,ar.edu.itba.alu.crpereyr.impl.linkedlist1.Node");
        setConfigKeyRelevancyAnalysis(true);
        setConfigKeyCheckNullDereference(true);
        setConfigKeyUseJavaArithmetic(true);
        setConfigKeyObjectScope(3);
        setConfigKeyInferScope(true);
        setConfigKeySkolemizeInstanceInvariant(false);
        setConfigKeySkolemizeInstanceAbstraction(false);
        setConfigKeyGenerateUnitTestCase(true);
        check(GENERIC_PROPERTIES,"get_0",false);
    }

//    public void test_removeTest() throws VizException {
//        setConfigKeyRelevantClasses("ar.edu.itba.alu.crpereyr.impl.linkedlist1.SinglyLinkedList,ar.edu.itba.alu.crpereyr.impl.linkedlist1.Node");
//        setConfigKeyRelevancyAnalysis(true);
//        setConfigKeyCheckNullDereference(true);
//        setConfigKeyUseJavaArithmetic(true);
//        setConfigKeyObjectScope(3);
//        setConfigKeyInferScope(true);
//        setConfigKeySkolemizeInstanceInvariant(false);
//        setConfigKeySkolemizeInstanceAbstraction(false);
//        setConfigKeyGenerateUnitTestCase(true);
//        check(GENERIC_PROPERTIES,"remove_0",false);
//    }
//

    public void test_removeTest() throws VizException {
    	setConfigKeyRelevantClasses("ar.edu.itba.alu.crpereyr.impl.linkedlist1.SinglyLinkedList,ar.edu.itba.alu.crpereyr.impl.linkedlist1.Node");
		setConfigKeyRelevancyAnalysis(true);
		setConfigKeyCheckNullDereference(true);
		setConfigKeyCheckArithmeticException(false);
		setConfigKeyUseJavaArithmetic(true);
		setConfigKeySkolemizeInstanceInvariant(false);
		setConfigKeySkolemizeInstanceAbstraction(false);
		this.setConfigKeyIntBithwidth(4);
		this.setConfigKeyNumericTypeQuantificationRange(0, 6);
//		this.setConfigKeyObjectScope(6);
		setConfigKeyRemoveQuantifiers(true);
		setConfigKeyInferScope(true);
		setConfigKeyTypeScopes("ar.edu.itba.alu.crpereyr.impl.linkedlist1.SinglyLinkedList:1, ar.edu.itba.alu.crpereyr.impl.linkedlist1.Node:4, int:4");
		setConfigKeyLoopUnroll(5);
		// SBP+BOUND
		setConfigKeyUseJavaSBP(false);
		setConfigKeyUseTightUpperBounds(false);
		// JUNIT
		setConfigKeyGenerateUnitTestCase(true);
        runAndCheck(GENERIC_PROPERTIES,"remove_0",false);
    }

}
