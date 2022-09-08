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
package ar.edu.taco.relevancy;


//@ model import org.jmlspecs.models.*;

/*@ nullable_by_default @*/
public class ListOfPoint {

    //@ model instance non_null JMLObjectSequence seq;
	
    public Node head; //@ in objectState;
    public int size; //@ in objectState;    
    
    /*@ represents seq \such_that    
      @  (size == seq.int_size()) && (head == null ==> seq.isEmpty()) &&
      @  (head != null ==> (head == seq.get(0) )) &&
      @  (\forall int i ; i >= 0 && i < size - 1; ((Node)seq.get(i)).next == seq.get(i + 1));
      @*/
    
    //@ensures size==\result;
    public int getSize() {
		return size;
	}
}
