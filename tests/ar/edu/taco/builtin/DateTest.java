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

import java.util.Date;

public class DateTest {
	
	/*@ 
	  @ ensures \result == 1; 
	  @*/	
	public int contructor_OK(int b1) {
		Date a = new Date(b1);
		Date b = new Date(b1);
		
		if (a != b) {
			return 1;
		} else {
			return 0;
		}
	}
	
	
	/*@ 
	  @ ensures \result == 0; 
	  @*/	
	public int toString_OK(Date b1) {
		String a = b1.toString();
		String b = b1.toString();
		
		if (!a.equals(b)) {
			return 1;
		} else {
			return 0;
		}
	}
	
	/*@ requires b1 == b2;
	  @ ensures \result == 1;
	  @*/
	public int toString_Fail(Date b1, Date b2) {
		String a = b1.toString();
		String b = b2.toString();

		if (!a.equals(b)) {
			return 1;
		} else {
			return 0;
		}
	}

}
