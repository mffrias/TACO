package ar.edu.taco.stryker.api.impl;

import java.util.HashSet;

/**
 * Set returned by the {@code reach} helper that MuJavaController injects into JML4C batch files (see
 * {@link StringsToWriteInFile#reachMethod}). The helper evaluates JML {@code \reach} expressions during
 * runtime assertion checking. {@link #has(Object)} and {@link #int_size()} provide the method names that
 * the subjects' specifications call on {@code \reach(...)}, for example
 * {@code \reach(Nodes, BinomialHeapNode, sibling + child).has(n)}.
 * <p>
 * Only that generated source refers to this class, by name, so it must be on the classpath when the
 * batch classes are compiled with jml4c and run.
 */
public class ReachSet extends HashSet<Object> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8721369070344632932L;

	/**
	 * Alias of {@link #contains(Object)}.
	 *
	 * @param elem the element to look for
	 * @return whether the set contains {@code elem}
	 */
	public boolean has(Object elem) {
		return contains(elem);
	}

	/**
	 * Alias of {@link #size()}.
	 *
	 * @return the number of elements
	 */
	public int int_size() {
		return size();
	}
	
}
