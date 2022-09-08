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
package ar.edu.taco.simplifier.loops;

public class ForLoop extends Object {

	//@ ensures \result == 3;
	public int forTest() {
		int x = 0;
		for (int i = 0; i < 3; i++) {
			x += i;
		}
		
		return x;
	}

	//@ ensures \result == 6;
	public int nestedForTest() {
		int x = 0;
		for (int i = 0; i < 2; i++) {
			for(int j = 0; j < 3; j++) {
				x += 1;
			}
		}
		
		return x;
		
	}
}
