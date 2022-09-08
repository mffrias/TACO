package ar.edu.taco.cast;

public class TCast {

	public static class DummyClass {}
	
	/**
	 * @Ensures true ;
	 */
	public TCast cast_object(Object object, DummyClass arg) {
		
		TCast cast;
		if (!(object instanceof TCast)) {
			throw new ClassCastException();
		}
		cast = (TCast)object;
		return cast;
	}
	
	
}
