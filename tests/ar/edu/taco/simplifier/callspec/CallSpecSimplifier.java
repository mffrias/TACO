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
package ar.edu.taco.simplifier.callspec;


public class CallSpecSimplifier {

	public Integer my_districts[] = new Integer[4];
	private /*@ spec_public @*/ int anInt = 2;

	// @ ensures \result <==> (my_districts[a_district.f()] != this.number()) && (my_districts[a_district.f()].equals(this.number()));
	public boolean testMethod(CallSpecSimplifier a_district) {
		return false;
	}
	
	//@ ensures \result <==> \old(getAnInt(Integer.valueOf(Integer.parseInt(aString))) == 2);
	public boolean testOldsurraoundedCallSpec(final /*@ non_null @*/ String aString) {
		this.anInt = this.anInt - 1;
		return (getAnInt(Integer.valueOf(Integer.parseInt(aString))) == 2);
	}
	
	public /*@ pure */ int getAnInt(Integer jj) {
		return this.anInt;
	}
	
	

	// @ ensures true == true;
	public/* @ pure */Integer number() {
		return new Integer(1);
	}

	// @ ensures true == true;
	public/* @ pure */int f() {
		return 1;
	}

}
