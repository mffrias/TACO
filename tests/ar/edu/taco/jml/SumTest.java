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

/**
 * @author elgaby
 *
 */
public class SumTest {
	public int pp;
	
	//@ ensures \result == 5;
	//Integer.valueOf(5).intValue();
	public /*@ pure @*/ int testValueOf() {
		return 5;
	}
	
	//@ ensures \result == (\sum int i; 0 <= i && i < my_districts.length; (my_districts[i] != null) ? 1 : 0);
	public /*@ pure @*/ int testSum(Object[] my_districts) {
		return 0;
	}
	
	//@ modifies this.pp;
	//@ ensures this.pp == 5;
	public void testModifies() {
		this.pp = 5;
	}
	
}
