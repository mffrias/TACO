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

import org.multijava.mjc.CType;

/**
 * @author elgaby
 *
 */
public interface MethodInformation {
	/**
	 * Returns whether the called method is static. {@code ExpressionVisitor} names a static call
	 * {@code <receiver type>_<method>} (see docs/METHOD-NAMING.md); the call still passes a
	 * receiver, since every program TACO produces has {@code thiz}
	 * ({@code ar.edu.taco.simplejml.helpers.ArgEncoder}).
	 *
	 * @return true if the method is static
	 */
	public boolean isStatic();
	/**
	 * Returns whether the called method is a constructor.
	 *
	 * @return true for a constructor
	 */
	public boolean isConstructor();
	/**
	 * Returns whether the call has a result: a known return type other than {@code void}. It
	 * decides whether the call passes a {@code return} argument
	 * ({@code ar.edu.taco.simplejml.helpers.ArgEncoder}).
	 *
	 * @return true if the call returns a value
	 */
	public boolean hasReturnType();
	/**
	 * Returns whether the call was taken for a call to a JML model method: one the parser did not
	 * resolve, whose receiver is a local variable or a name ({@link MethodInformationBuilder}).
	 * Nothing outside this package reads it.
	 *
	 * @return true for a model method call
	 */
	public boolean isModelMethod();
	/**
	 * Returns the class that declares the called method, with underscores for dots
	 * ({@code pkg_Class}, as {@code JavaClassNameNormalizer} writes it). {@code ExpressionVisitor}
	 * prefixes it to the program name of static calls and of calls into the classes TACO
	 * translates (see docs/METHOD-NAMING.md).
	 *
	 * @return the declaring class, or null when the parser did not resolve the method and the
	 *         receiver is a local variable or a name
	 */
	public String getQualifiedReceiverType();
	/**
	 * Returns the type of the call's result as the JML parser typed it. {@code ESExpressionVisitor}
	 * declares the temporary variable that holds the result with it.
	 *
	 * @return the result type, or null if the parser gave the call no type
	 */
	public CType getReturnType();
}
