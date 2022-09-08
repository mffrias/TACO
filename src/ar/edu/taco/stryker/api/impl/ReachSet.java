package ar.edu.taco.stryker.api.impl;

import java.util.HashSet;

public class ReachSet extends HashSet<Object> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8721369070344632932L;

	public boolean has(Object elem) {
		return contains(elem);
	}

	public int int_size() {
		return size();
	}
	
}
