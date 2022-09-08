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

import java.util.HashMap;
import java.util.Map;

class JavaSignatures {

	private final Map<String, String> javaSignatures = new HashMap<String, String>();

	private static JavaSignatures instance;

	public static JavaSignatures getInstance() {
		if (instance == null)
			instance = new JavaSignatures();

		return instance;
	}

	private JavaSignatures() {
		super();
		javaSignatures.put("java_util_HashSet", "java_util_Set");
		javaSignatures.put("java_util_HashMap", "java_util_Map");
		javaSignatures.put("java_util_LinkedList", "java_util_List");
		//javaSignatures.put("JMLObjectSet", "Set");
		//javaSignatures.put("JMLObjectSequence", "List");
	}

	public boolean contains(String identifier) {
		return javaSignatures.containsKey(identifier);
	}

	public String getSignatureId(String identifier) {
		return javaSignatures.get(identifier);
	}
}
