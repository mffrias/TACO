package ar.edu.taco.dynalloy;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Allocation counts by type (signature id), filled by this package's
 * {@code ObjectCreationCollector}. A copy of jDynAlloy's
 * {@code ar.edu.jdynalloy.xlator.ObjectCreationCounter} without its {@code getAllocations}
 * and {@code setAllocations}; the scope inference uses jDynAlloy's, and only the unused
 * {@link DynAlloyProgramScopeInferecePlugin} uses this one.
 * {@link #getAllocationCount(String)} throws a {@link NullPointerException} for a type with
 * no allocation.
 */
public class ObjectCreationCounter {

	private Map<String, Integer> allocations = new HashMap<String, Integer>();

	public void inc_alloc_type(String type_str) {
		if (!this.allocations.containsKey(type_str)) {
			this.allocations.put(type_str, 0);
		}
		int alloc_counter = this.allocations.get(type_str);
		alloc_counter++;
		this.allocations.put(type_str, alloc_counter);
	}
	
	public Set<String> signatureSet() {
		return allocations.keySet();
	}
	
	public int getAllocationCount(String signature_id) {
		return allocations.get(signature_id);
	}
}
