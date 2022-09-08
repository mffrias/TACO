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
package ar.edu.taco.exception;

public class ThrowUsage {

	/*@
	  @   requires i > 0;
	  @*/	
	public int testThrowWithoutSpecifyingBehavior(int i) {
		if (i > 0) {
			return i;
		} else {
			throw new IndexOutOfBoundsException();
		}
	}


	/*@
	  @ normal_behavior
	  @   	requires i > 0;
	  @ also
	  @ normal_behavior
	  @   	requires i < 0;
	  @	  	ensures \result == i + 1;
	  @*/	
	public int testThrowSpecifyingNormalBehavior(int i) {
		if (i > 0) {
			return i;
		} else {
			throw new IndexOutOfBoundsException();
		}
	}

	/*@
	  @ normal_behavior
	  @   	requires i > 0;
	  @	  	ensures \result == i;
      @ also
      @ exceptional_behaviour
      @		requires i < 0;
      @   	signals_only IndexOutOfBoundsException;
	  @*/	
	public int testThrowSpecifyingBehaviors(int i) {
		if (i > 0) {
			return i;
		} else {
			throw new IndexOutOfBoundsException();
		}
	}
}
