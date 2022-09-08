package examples.stryker.multikeymap;


public class HashEntry {

	/** An object for masking null */
	protected static final Object NULL = new Object();

	/** The next entry in the hash chain */
	protected HashEntry next;
	/** The hash code of the key */
	protected int hashCode;
	/** The key */
	protected MultiKey key;
	/** The value */
	protected Object value;

	protected HashEntry(final HashEntry next, final int hashCode, final MultiKey key, final Object value) {
		super();
		this.next = next;
		this.hashCode = hashCode;
		this.key = key;
		this.value = value;
	}

	/*@ pure @*/ public MultiKey getKey() {
		if (key == NULL) {
			return null;
		}
		return key;
	}

	public Object getValue() {
		return (Object) value;
	}

	public Object setValue(final Object value) {
		final Object old = this.value;
		this.value = value;
		return (Object) old;
	}


	
	public int hashCode() {
		return (getKey() == null ? 0 : getKey().hashCode()) ^
				(getValue() == null ? 0 : getValue().hashCode());
	}

	
	public String toString() {
		return new StringBuilder().append(getKey()).append('=').append(getValue()).toString();
	}
}