package escj.testA0;

/**
 * $Id: NullArr1b.java,v 1.1 2009/11/16 19:42:33 jgaleotti Exp $
 * Test cases for a fields of array types.
 */
public class NullArr1b {
    private /*@non_null*/ Object /*!non_null*/ [] a = new Object[0];

    public /*@non_null*/ Object m331() {
	if(a.length > 1)
	    return a[0]; // ok under new semantics.
	else
	    return "";
    }

    private /*@non_null*/ int[] ia331 = new int[1];

    public void mi331() {
        if(ia331.length > 1)
            ia331[0] = 1; // ok since ia is not a reference type.
    }
}
