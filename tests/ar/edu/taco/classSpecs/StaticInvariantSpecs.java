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
package ar.edu.taco.classSpecs;

public class StaticInvariantSpecs {
	public static int a = 5;
	//@ static invariant a >= 5;
	
	public int b = 3;
	//@ invariant b > 2;
	
	//@ requires a == 5;
	//@ modifies a;
	public int invalidateInvariant(int c) {
		a = a - 1;
		return c;
	}

	//@ requires a == 5;
	//@ modifies a;
	public int keepValidInvariant(int c) {
		a = a + 1;
		return c;
	}
	
	//@ requires FixedDoorHouse.doors == 1;
	//@ modifies FixedDoorHouse.doors;
	public int invalidateForeignInvariant(int c) {
		FixedDoorHouse.doors = FixedDoorHouse.doors + 1;
		return c;
	}

	//@ requires FixedDoorHouse.doors == 1;
	//@ modifies FixedDoorHouse.doors;
	public int keepValidForeignInvariant(int c) {
		FixedDoorHouse.doors = FixedDoorHouse.doors - 1;
		return c;
	}
	
	//@ requires a == 5;
	//@ modifies a;
	public static int invalidateInvariant_static(int c) {
		a = a - 1;
		return c;
	} 
}
