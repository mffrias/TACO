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

public class ByteTest {
	
	/*@ requires s1 != null;
	  @ ensures \result == 0; 
	  @*/	
	public int valueOf_String_OK(String s1) {
		Byte a = Byte.valueOf(s1);
		Byte b = Byte.valueOf(s1);
		if (!a.equals(b)) {
			return 1;
		} else {
			return 0;
		}
	}	

	/*@ requires s1 == s2;
	  @ requires s1 != null;
	  @ requires s2 != null;
	  @ ensures \result == 1; 
	  @*/
	public int valueOf_String_Count(String s1, String s2) {
		Byte a = Byte.valueOf(s1);
		Byte b = Byte.valueOf(s2);

		if (!a.equals(b)) {
			return 1;
		} else {
			return 0;
		}
	}
	
	/*@ 
	  @ ensures \result == 0; 
	  @*/	
	public int valueOf_Int_OK(byte b1) {
		Byte a = Byte.valueOf(b1);
		Byte b = Byte.valueOf(b1);
		
		if (!a.equals(b)) {
			return 1;
		} else {
			return 0;
		}
	}
	
	/*@ requires b1 == b2;
	  @ ensures \result == 1; 
	  @*/
	public int valueOf_Int_Count(byte b1, byte b2) {
		Byte a = Byte.valueOf(b1);
		Byte b = Byte.valueOf(b2);

		if (!a.equals(b)) {
			return 1;
		} else {
			return 0;
		}
	}
	
	/*@ 
	  @ ensures \result == 0; 
	  @*/	
	public int toString_OK(byte b1) {
		String a = Byte.toString(b1);
		String b = Byte.toString(b1);
		
		if (!a.equals(b)) {
			return 1;
		} else {
			return 0;
		}
	}
	
	/*@ requires b1 == b2;
	  @ ensures \result == 1;
	  @*/
	public int toString_Count(byte b1, byte b2) {
		String a = Byte.toString(b1);
		String b = Byte.toString(b2);

		if (!a.equals(b)) {
			return 1;
		} else {
			return 0;
		}
	}

	/*@ 
	  @ requires b1!=null && b2!=null && b1 != b2;
	  @ ensures \result == 0; 
	  @*/	
	public int equals_OK(Byte b1, Byte b2) {
		if (b1.equals(b2)) {
			return 1;
		} else {
			return 0;
		}
	}
	
	/*@ 
	  @ requires b1 == b2;
	  @ ensures \result == 0; 
	  @*/	
	public int equals_Count(Byte b1, Byte b2) {
		if (b1.equals(b2)) {
			return 1;
		} else {
			return 0;
		}
	}

	/*@ 
	  @ ensures \result == 1; 
	  @*/	
	public int hashCode_OK() {
		byte b = 1;
		Byte b1 = Byte.valueOf(b);
		
		int hashCode = b1.hashCode();
		
		return hashCode;
	}

	/*@ 
	  @ ensures \result == 2; 
	  @*/	
	public int hashCode_Count() {
		byte b = 1;
		Byte b1 = Byte.valueOf(b);
		
		int hashCode = b1.hashCode();
		
		return hashCode;
	}
	
	
}
