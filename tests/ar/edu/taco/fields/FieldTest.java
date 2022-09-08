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
package ar.edu.taco.fields;

/**
 * Issue #17 Problema en el traductor a Alloy con los parameros por copia. Un
 * ejemplo: Se pasa un field int como parametro de un metodo que toma un
 * parametro "i" y le realiza una asignacion.
 * 
 * @author diegodob
 * 
 */
public class FieldTest extends Object {

	private int myVar;
	private String pp = null;

	public void assignField() {
		takeAnInt(this.myVar);
	}

	public void takeAnInt(int i) {
		i = i + 1;
	}

}
