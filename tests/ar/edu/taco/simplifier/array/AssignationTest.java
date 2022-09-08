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
package ar.edu.taco.simplifier.array;


public class AssignationTest {
	
	public int x;
	public int y;
	public int z;

	//@ ensures a[2] == -1;
	//@ ensures x == 1;
	//@ ensures y == 1;
	//@ ensures z == 1;
	public void assignationTestPass(int a[]) {
		x = y = z = 1;
		a[f()] = -1;
	}

	//@ ensures a[2] == -2;
	//@ ensures x == 3;
	//@ ensures y == 3;
	//@ ensures z == 3;
	public void assignationTestFail(int a[]) {
		x = y = z = 1;
		a[f()] = -1;
	}
	public int f() 	{
		return 2;
	}
	
}
