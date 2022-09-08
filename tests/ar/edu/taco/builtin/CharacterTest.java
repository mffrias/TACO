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
package ar.edu.taco.builtin;

public class CharacterTest {
	
	/*@ 
	  @ requires c1 != null;
	  @ ensures \result == 1;
	  @*/	
	public int toString_OK(Character c1) {
		String a = c1.toString();
		String b = c1.toString();
		
		if (a.equals(b)) {
			return 1;
		} else {
			return 0;
		}
	}
	
	/*@ requires c1 != c2;
	  @ ensures \result == 1;
	  @*/
	public int toString_Count(Character c1, Character c2) {
		String a = c1.toString();
		String b = c2.toString();

		if (a.equals(b)) {
			return 1;
		} else {
			return 0;
		}
	}

	/*@ 
	  @ ensures \result == 0;
	  @*/	
	public int static_toString_OK() {
		String a = Character.toString('a');
		String b = Character.toString('b');
		
		if (a.equals(b)) {
			return 1;
		} else {
			return 0;
		}
	}
	
	/*@ 
	  @ ensures \result == 0;
	  @*/
	public int static_toString_Count() {
		String a = Character.toString('a');
		String b = Character.toString('a');

		if (a.equals(b)) {
			return 1;
		} else {
			return 0;
		}
	}


	
	/*@ 
	  @ requires c1 == c2;
	  @ ensures \result == 0; 
	  @*/	
	public int equals_Count(Character c1, Character c2) {
		if (c1.equals(c2)) {
			return 1;
		} else {
			return 0;
		}
	}

	/*@ 
	  @ ensures \result == 0; 
	  @*/	
	public int hashCode_OK() {
		Character c1 = new Character('b');
		Character c2 = new Character('b');
		
		int hashCode = c1.hashCode();
		int hashCode2 = c2.hashCode();
		
		if (hashCode == hashCode2) {
			return 0;
		} else {
			return 1;
		}
	}

	/*@ 
	  @ ensures \result == 0; 
	  @*/	
	public int hashCode_Count() {
		Character character1 = new Character('b');
		Character character2 = new Character('a');
		
		int hashCode = character1.hashCode();
		int hashCode2 = character2.hashCode();
		
		if (hashCode == hashCode2) {
			return 0;
		} else {
			return 1;
		}
	}
	
}
