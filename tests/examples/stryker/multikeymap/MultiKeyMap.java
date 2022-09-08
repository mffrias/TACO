package examples.stryker.multikeymap;

public class MultiKeyMap {
	
	//@ invariant true;

	/*@ requires (entry != null && entry.getKey() != null);
      @ ensures ((entry.getKey().size()==2 && (key1 == entry.getKey().getKey(0) || key1 != null && key1.equals(entry.getKey().getKey(0))) && (key2 == entry.getKey().getKey(1) || key2 != null && key2.equals(entry.getKey().getKey(1)))) <==> (\result == true));
      @ signals (RuntimeException e) false; 
      @*/
	public boolean isEqualKey(final HashEntry entry, final Object key1, final Object key2) {
		final MultiKey multi = entry.getKey();
		return multi.size() == 2 && (key1 == multi.getKey(0) || key1 != null && key1.equals(multi.getKey(0))) && (key2 == multi.getKey(1) || key1 != null && key2.equals(multi.getKey(1))); //mutGenLimit 1
	}


}
