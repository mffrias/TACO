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
package ar.edu.taco.jml;

public class CallSpecTest extends Object {
	//@ requires b > CallSpecTest.giveMeFive();
	//@ ensures b > 0;
	public /*@ pure @*/ boolean isPositive(int b) {
		//@ loop_invariant b > 3;
		while (b > 6) {
			b--;
		}
		return true;
	}
	
	//@ requires CallSpecTest.giveMeFive() == CallSpecTest.giveMeFive();
	//@ ensures Integer.valueOf(5) == \result;
	public int isFive() {
		Integer pp = Integer.valueOf(5);
		return pp;
	}
	
	//@ requires isPositive(b);
	public /*@ pure @*/  int oneMethod(int b) {
		int a;
		a = 5/b;
		return a;
	}
	
	//@ ensures \result == 5;
	public /*@ pure @*/  static int giveMeFive() {
		return 5;
	}
	
	public int fromByte(byte number) {
		return number;
	} 
}
