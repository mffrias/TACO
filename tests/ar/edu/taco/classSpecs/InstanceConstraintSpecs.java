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

public class InstanceConstraintSpecs {
	private /*@ spec_public @*/ int a = 5;
	//@ constraint \old(a) <= a;
	
	//@ requires a == 5;
	//@ modifies this.a;
	public int invalidateConstraint(int c) {
		a = a - 1;
		return c;
	}

	//@ requires a == 5;
	//@ modifies this.a;
	public int keepValidConstraint(int c) {
		a = a + 1;
		return c;
	}
}
