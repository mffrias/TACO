package escj.test100;

/* 
 * This test checks that if alloc is the target of a loop then it is 
 * havoced to a value greater than its value just before the loop begins.
 */

class AllocTest {

    int i = 5;
    //@ invariant i > 0;

    void m(int n) {

	//@ loop_invariant (\forall AllocTest a; a.i > 0);
	while (i < n) {
	    //@ assert i > 0;
	    i = i + 1;
            int[] junk = new int[10];
	}
    }

}
