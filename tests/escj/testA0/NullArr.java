package escj.testA0;

/**
 * $Id: NullArr.java,v 1.1 2009/11/16 19:42:33 jgaleotti Exp $
 * Test the new semantics of non_null as applied to array types.
 *
 * This test show the results of an intermediate implementation of the feature
 * under which non_null applied to an array type constrains all array
 * component types to be non_null. Once the feature is fully implemented, then
 * it will be necessary to add non_null at the point indicated by !non_null.
 */
public class NullArr {

    // Test argument types (all cases ok).
    public /*@non_null*/ Object m0a(/*@non_null*/ Object /*!non_null*/ [] a) {
	if(a.length > 1)
	    return a[0]; // Ok
	else
	    return "";
    }

    // Test argument types, error cases.
    public void m0b(/*@non_null*/ Object /*!non_null*/ [] a) {
	if(a.length > 1)
	    a[0] = null; // error
    }

    // Test return type (all cases are ok).
    public /*@non_null*/ Object[] m1a(/*@non_null*/ Object /*!non_null*/ [] a) {
	switch (a.length) {
	case 1:
	    return a;
	case 2:
	    return new Object[0];
	default:
	    Object [] result = { "" };
	    return result;
	}
    }

    // Test return type, error cases.
    public /*@non_null*/ Object[] m1b(/*@non_null*/ Object /*!non_null*/ [] a) {
	if(a.length == 331) {
	    return new Object[3]; // error
	}
	return a;
    }
}
