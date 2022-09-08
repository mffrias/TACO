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
package ar.edu.taco.binding;


public class BindingTest extends Object {

    public int test(int i) {
	A a = new A();
	B b = new B();
	
	int x;
	x = bindme(b);
	return x;
    }

    public int bindme(Object x) {
	return 0;	
    }

    public int bindme(A x) {
	return 0;	
    }
    public int bindme(int i) {
	return 1;
    }

    public int bindme(String x) {
	return 2;
    }

}
