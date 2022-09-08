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

public class JavaClassNameNormalizer {

	private String packageName;
	private String className;

	// public static String normalizedName(String packageName, String )

	public JavaClassNameNormalizer(char[] packageName, char[] className) {
		this(String.valueOf(packageName), String.valueOf(className));
	}

	public JavaClassNameNormalizer(String packageName, String className) {
		this.packageName = packageName.replaceAll("\\.", "_");
		this.className = className;
	}

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

	public String getPackageName() {
		return packageName;
	}
	
	public String getClassName() {
		return className;
	}

	public String getQualifiedClassName() {
		String s = packageName;
		if (s.length() != 0) {
			s += "_";
		}
		s += className;
		return s;
	}

}
