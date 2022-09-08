package escj.test95;

/**
 ** Make sure can't apply set to special fields like "length"
 **/

class Specials {

    int[] a;
    //@ ghost public int[] b;

    void foo() {
	//@ set a.length = 0;
	//@ set b.length = 0;
    }
}

