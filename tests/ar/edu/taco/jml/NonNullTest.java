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
/*@ nullable_by_default @*/
public class NonNullTest {
	private /*@ spec_public non_null @*/ String juanita_non_null;
	private /*@ spec_public nullable @*/ String juanita;
	/*@ invariant juanita_non_null != juanita; @*/
	
	/*@
	  @	requires pp.intValue() > 0;
	  @	ensures \result.intValue() < 0; 
	 @*/
	public /*@ pure non_null @*/ Integer returnAsInt(/*@ non_null @*/ String aString, Integer pp) {
		return new Integer(5);
	}

}
