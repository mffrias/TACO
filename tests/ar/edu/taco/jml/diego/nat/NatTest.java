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
package ar.edu.taco.jml.diego.nat;

/*@ nullable_by_default @*/
public class NatTest {

	public NatTest prev;
	
	//@ensures prev == null;
	public /*@ pure @*/ boolean isZero() {
		boolean b;
		if (prev == null) {
			b = true;
		} else {
			b = false;
		}
		return b;
	}
	
	//@ensures (\result == true) <==> (prev != null && prev.prev != null && prev.prev.isZero());
	public /*@ pure @*/ boolean isOne() {
		boolean b;
		
		if (prev != null) {
			if (prev.prev == null) {
				b = true;
			} else {
				b = false;
			}
		} else {
			b = false;
		}
		return b;
	}
	
	
}
