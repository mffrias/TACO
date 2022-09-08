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
package ar.edu.taco.loops;

/**
 * @author elgaby
 * 
 */
public class WhileLoop extends Object {
	
	public static int pp = 2;
	public int anInt = 5;

	/*@  
	  @ normal_behavior
	  @ 	requires a.length == 3;
	  @ 	requires this.anInt == 2;
	  @ 	requires WhileLoop.pp == 4;
	  @ 	requires (\forall int j; 0 <= j && j < a.length; a[j] > 0 );
	  @ 	modifies \everything;
	  @ 	ensures (\forall int j; 0 <= j && j < a.length - 1; a[j] <= a[j+1]);
	  @*/
	public int[] LoopInvariantInvalidInvariant(int[] a) {
		
		/*@ loop_invariant (\forall int j; 0 <= j && j < i; a[j] <= a[j+1]) &&
		  @ 				(\forall int h; i < h && h < a.length; a[i] <= a[h]);
		  @*/
		for (int i = 0; i < a.length - 1; i++) {
			for (int j = i+1; j < a.length; j++) {
				if (a[i] > a[j]) {
					int temp = a[i];
					a[i] = a[j];
					a[j] = temp;
					j++;
				}
			}
		}
		
		return a;
	}
	
	/*@  
	  @ normal_behavior
	  @ 	requires a.length == 3;
	  @ 	requires (\forall int j; j >= 0 && j < a.length; a[j] > 0 );
	  @ 	modifies \nothing;
	  @ 	ensures \result == (\sum int j; j >= 0 && j < a.length; a[j]);
	  @*/
	public int LoopInvariantDoesNotKeepInvariant(int[] a) {
		int size = a.length;
		int pos = 0;
		int sum = 0;
		
		//@ loop_invariant (pos >= 0 && pos < size) && sum == (\sum int j; j >= 0 && j < pos ; a[0]);
		while ((pos < size)) {
			sum = sum + a[pos];
			pos++;
		}
		return sum;
	}
	
	/*@  
	  @ normal_behavior
	  @ 	requires a.length == 3;
	  @ 	requires (\forall int j; 0 <= j && j < a.length; a[j] > 0 );
	  @ 	modifies \everything;
	  @ 	ensures (\forall int j; 0 <= j && j < a.length; \result >= a[j]);
	  @*/
	public int LoopInvariantKeepInvariant_max(int[] a) {
		int ret_val = a[0];
		/*@ loop_invariant (\forall int j; 0 <= j && j < i; ret_val >= a[j]);
		  @*/
		for (int i = 0; i < a.length; i++) {
			if (ret_val < a[i]) {
				ret_val = a[i];
			}
		}
		
		return ret_val;
	}
	
}
