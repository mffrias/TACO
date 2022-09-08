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
package ar.edu.taco;


/**
 * @j2daType
 */
public class EjemploDG extends Object {
	
//	public int x = 5;
//	/*@ 
//	  @ invariant x!=0; 
//	  @*/
//	 
//	/*@  
//	  @ requires x!=0;
//	  @ ensures \result==\old(x)+1;
//	  @*/	
//	public int ifSimplify() {
//
//
//	    if (x == 1 || x == 2 && x == 3) {
//		x++;
//	    }
//	    
//	    return x;
//	}
//	    
//	
	/*@  
	  @ requires i!=0;
	  @ ensures \result==\old(i)+1;
	  @*/
	public int testOk(int i) {
		int j;
		int a, b = 5;
		j = i + 1;
		String[] stringArray = new String[3];
		Integer intVar = new Integer(2);
		if (j > 0) {
			j++;
			j--;
			j+=1;
			stringArray[0] = "Hola";
			intVar = Integer.valueOf("2");
		} else {
			j-= 1;
			j = dameUno();
			stringArray[0] = "chau";
			int intAsFloat = intVar.intValue();
		}
//		//@ assert j > i;
		return j;
	}
	
	//@ requires i!=0;
	//@ ensures \result==i;
	public int testBuggy(int i) {
		int j = 0;
		j = i + 1;
		//@ loop_invariant j > i && i > 0;
		while (j < 5) {
			j--;
		}
		
		return j;
	}
	
	private int dameUno() {
		int j = 0;
		if (true) {
			j++;
		}

		if (false) {
			j++;
		}

		boolean isTrueVariable = isTrue();
		if (isTrueVariable) {
			j--;
		}
		
		return j;
	}
	
	private boolean isTrue() {
		return true;
	}
//	
//	private void testForStatement() {
//		int j = 0;
//		for (int x = 0; x < 5; x++) {
//			j++;
//		}
//		
//		return;
//	}
//	
//	private void testForEachStatement() {
////		List aList = new ArrayList();
////		Integer intVal = new Integer(1);
//		//aList.add(intVal);
////		for (Object object : aList) {
////			Integer intVal2 = new Integer(2); 
////			boolean fake = aList.add(intVal2);
////		}
//	}
//	
	private void testInstanceOf() {
		Integer pp = new Integer(4);
		String str = super.toString();
		if (pp instanceof Integer) {
			int juan = pp.intValue();
		}
	}
	
	private int testTryCatch() {
		int j = 0;
		try {
			j++;
			while (j < 5) {
				j--;
			}
		} catch (ArrayStoreException e2) {
			j++;
			e2.printStackTrace();
		} catch (IndexOutOfBoundsException e) {
			j--;
			e.printStackTrace();
		} finally {
			j--;
		}
		
		return j;
	}
}
