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
package ar.edu.taco.simplecode;

public class BoundedStack {
	private /*@ spec_public nullable @*/ Object[] elems;
	private /*@ spec_public @*/int size = 0;
	
	//@ invariant 0 <= size;
	//@ invariant elems != null && (\forall int i; size <= i && i < elems.length; elems[i] == null);

	/*@ requires 0 < n;
	  @ 
	  @ assignable elems;
	  @
	  @ ensures elems.length == n;
	  @*/
	public BoundedStack(int n) {
		elems = new Object[n];
	}

	/*@ requires size < elems.length-1;
	  @ 
	  @ assignable elems[size], size;
	  @
	  @ ensures size == \old(size + 1);
	  @ ensures elems[size-1] == x;
	  @ ensures_redundantly (\forall int i; 0 <= i && i < size-1; elems[i] == \old(elems[i]));
	  @*/
	public void push(Object x) {
		elems[size] = x;
		size++;
	}

	/*@ requires size < elems.length-1;
	  @ 
	  @ assignable elems[size], size;
	  @
	  @ ensures size == \old(size + 1);
	  @ ensures elems[size-1] == x;
	  @ ensures_redundantly (\forall int i; 0 <= i && i < size-1; elems[i] == \old(elems[i]));
	  @*/
	public void pushBuggy(Object x) {
		elems[size] = x;
	}
}
