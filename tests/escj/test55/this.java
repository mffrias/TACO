package escj.test55;

class T {
    int x;
    
    T() {
    }

    //@ requires t != this
    //@ requires t != null
    //@ ensures this.x == 3
    void m(T t) {
	x = 3;
	t.set();
    }

    void set() {
	x = 4;
    }

}
