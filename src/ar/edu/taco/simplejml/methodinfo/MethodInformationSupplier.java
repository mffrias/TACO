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
package ar.edu.taco.simplejml.methodinfo;

/**
 * Information about the methods of one JDK class, for calls that the JML parser did not
 * resolve and whose receiver is a type name. {@link MethodInformationBuilder} finds the
 * supplier by reflection: for a call whose prefix prints as {@code java/lang/Integer} it
 * instantiates {@code MI_JavaLangInteger} of this package. The suppliers here know only
 * {@code valueOf} (and {@code buildInstance} for {@code String} and {@code Character}), all
 * as static methods. They have no state.
 */
public interface MethodInformationSupplier {
	/**
	 * Returns the information about a method of this supplier's class.
	 *
	 * @param methodName the called method's simple name
	 * @return a new {@link ConcreteMethodInformation}; the implementations of this package throw
	 *         {@code ar.edu.taco.TacoException} for a name they do not know
	 */
	public ConcreteMethodInformation getInformationForMethod(String methodName);
}
