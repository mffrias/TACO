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
package ar.edu.taco.simplejml.helpers;

/**
 * Turns a Java class name into the name of its JDynAlloy module and signature: the package
 * with underscores for dots, an underscore, and the class name ({@code ar.edu.Foo} gives
 * {@code ar_edu_Foo}). With the one-argument constructor a nested class's {@code $} becomes
 * {@code _inner_} ({@code pkg.Outer$Inner} gives {@code pkg_Outer_inner_Inner}), since
 * DynAlloy and Alloy names cannot contain {@code $}. No static state.
 */
public class JavaClassNameNormalizer {

	private String packageName;
	private String className;

	// public static String normalizedName(String packageName, String )

	/**
	 * Same as {@link #JavaClassNameNormalizer(String, String)}.
	 *
	 * @param packageName the package, with dots
	 * @param className the class's simple name
	 */
	public JavaClassNameNormalizer(char[] packageName, char[] className) {
		this(String.valueOf(packageName), String.valueOf(className));
	}

	/**
	 * Takes the package and the class apart. The package's dots become underscores; the class
	 * name is kept as it is ({@code $} included).
	 *
	 * @param packageName the package, with dots; empty for the default package
	 * @param className the class's simple name
	 */
	public JavaClassNameNormalizer(String packageName, String className) {
		this.packageName = packageName.replaceAll("\\.", "_");
		this.className = className;
	}

	/**
	 * Takes a qualified name, either with dots ({@code pkg.Outer$Inner}) or as a JVM type
	 * descriptor ({@code Lpkg/Outer$Inner;}), and splits it at the last separator; a name
	 * without one is in the default package. Package separators become underscores, and
	 * {@code $} in the class name becomes {@code _inner_}.
	 *
	 * @param className the qualified name
	 */
	public JavaClassNameNormalizer(String className) {
		String newClassName = className;
		char packageCharSeparator = '.';
		if (newClassName.startsWith("L") && newClassName.endsWith(";")) {
			newClassName = newClassName.substring(1, newClassName.length() - 1);
			packageCharSeparator = '/';
		}
		
		int pos = newClassName.lastIndexOf(packageCharSeparator);
		if (pos >= 0) {
			// if "." found
			this.packageName = newClassName.substring(0, pos).replaceAll("\\" + packageCharSeparator, "_");
			this.className = newClassName.substring(pos + 1);
		} else {
			// if not "." found
			this.packageName = "";
			this.className = newClassName;
		}	
		
		//DOB::inner class fix (dynalloy and alloy dont's support "$" character in identifier name.
		this.className = this.className.replace("$", "_inner_");
	}

	/**
	 * Returns the package with underscores ({@code ar_edu}), or the empty string.
	 *
	 * @return the package
	 */
	public String getPackageName() {
		return packageName;
	}
	
	/**
	 * Returns the class's simple name ({@code Outer_inner_Inner} for a nested class, with the
	 * one-argument constructor).
	 *
	 * @return the class name
	 */
	public String getClassName() {
		return className;
	}

	/**
	 * Returns the package and the class name joined by an underscore ({@code ar_edu_Foo}), or
	 * the class name alone in the default package: the module and signature name TACO uses.
	 *
	 * @return the qualified name with underscores
	 */
	public String getQualifiedClassName() {
		String s = packageName;
		if (s.length() != 0) {
			s += "_";
		}
		s += className;
		return s;
	}

}
