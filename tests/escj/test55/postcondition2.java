package escj.test55;

class C_post2 {
    C_post2() {
    }

    //@ ensures \result > 0;
    int m(int j) {
	return j;
    }
}
