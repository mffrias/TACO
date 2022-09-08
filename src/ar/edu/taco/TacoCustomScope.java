package ar.edu.taco;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class TacoCustomScope {

	public static final int DEFAULT_TOP_LEVEL_SCOPE = 3;
	
	public static final int DEFAULT_ALLOY_BITWIDTH = 4;

	public static final int DEFAULT_MAX_ALLOY_SEQUENCE_LENGTH = 3;
	
	public static final int DEFAULT_UNROLL = 3;
	
	private int topLevelScope = DEFAULT_TOP_LEVEL_SCOPE;
	
	private Map<String,Integer> customTypeScope = new HashMap<String,Integer>();
	
	private int alloyBitwidth = DEFAULT_ALLOY_BITWIDTH;
	
	@SuppressWarnings("unused")
	private int unroll = DEFAULT_UNROLL;

	private int alloyMaxSequenceLength = DEFAULT_MAX_ALLOY_SEQUENCE_LENGTH;
	
	public void setUnroll(int new_unroll) {
		this.unroll = new_unroll;
	}
	
	public void setAlloyBitwidth(int new_alloy_bitwidth) {
		this.alloyBitwidth = new_alloy_bitwidth;
	}
	
	public void setTopLevelScope(int new_top_level_scope) {
		this.topLevelScope = new_top_level_scope;
	}
	
	public void setTypeScope(String typename, int scope) {
		this.customTypeScope.put(typename, scope);
	}
	
	public void removeTypeScope(String typename) {
		this.customTypeScope.remove(typename);
	}

	public Integer getAlloyBitwidth() {
		return alloyBitwidth;
	}

	public void setMaxAlloySequenceLength(int new_max_alloy_seq_length) {
		this.alloyMaxSequenceLength = new_max_alloy_seq_length;
	}

	public int getTopLevelScope() {
		return topLevelScope;
	}

	public Integer getAlloyMaxSequenceLength() {
		return this.alloyMaxSequenceLength ;
	}
	
	public Set<String> getCustomTypes() {
		return this.customTypeScope.keySet();
	}
	
	public int getScopeForType(String typename) {
		return this.customTypeScope.get(typename);
	}
	
}
