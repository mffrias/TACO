package roops.core.objects.junit;

/**
 * 
 * @author Apache Harmony software http://harmony.apache.org/
 *         http://www.docjar.com/html/api/java/util/ArrayList.java.html
 * 
 */
public class ArrayList {

	/**
	 * @Modifies_Everything;
	 * 
	 * @Requires arrayList!=null;
	 * @Ensures return==true;
	 */
    static public boolean showInstance(/*@ nullable @*/ ArrayList arrayList) {

    	if (arrayList.array==null)
    		return true;

    	if (arrayList.array.length!=3)
    		return true;

    	if (arrayList.array[0]==null && arrayList.array[1]!=null && arrayList.array[2]==null )
    		return true;

		return false;
	}

	private int firstIndex;

	private int lastIndex;

	private Object[] array;

	private int modCount;

	public ArrayList() {}
}
