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
package ar.edu.taco.jml.diego.adam;

/*@ nullable_by_default @*/
public class Person extends Object {

       //@ invariant (\reach(this,Person,father).has(this)) == false;

       public Person father;

       //@ requires ppp() != null;
       //@ ensures \result <==> (this.father != null);
       public/*@pure */boolean isAdam() {
               boolean b;
               if (this.father == null) {
                       b = true;
               } else {
                       b = false;
               }
               return b;
       }
       
       //@requires 1==1;
       //@ensures father == null;
       public /*@pure */ Person ppp() {
    	   return null;
       }
}
///*@ nullable_by_default @*/
//public class Person extends Object {
//
//    public Person father;
//
//    //@ requires father == null;
//    //@ ensures \result <==> uno(1);
//    public/*@pure */boolean isAdam() {
//            boolean b;
//            if (this.father == null) {
//                    b = true;
//            } else {
//                    b = false;
//            }
//            return b;
//    }
//    
//  //@ensures dos(x) == true;
//  public /*@pure */ boolean uno(int x) {
//	  boolean b;
//	  if (x == 1) {
//		b = true;  
//	  } else {
//		b = false;
//	  }
//	  
//	  return b;
//  }
//
//  //@ensures y == 1;
//  public /*@pure */ boolean dos(int y) {
//	   boolean b;
//	   if (y != 5) {
//			b = true;
//		} else {
//			b = false;
//		}
//	   return b;
//  }
//
//}
