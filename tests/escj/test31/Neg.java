package escj.test31;

class Neg {

    //@ invariant \nonnullelements(names);
    String[] names = new String[0];
}

class Neg2 {

    //@ invariant foos != null;
    //@ invariant foos.length>1;
    String[] foos = new String[10];

}


class NegUser {

    //@ requires X != null;
    //@ requires Y != null;
    void foo(Neg X, Neg2 Y) {
	Y.foos[0] = null;
    }
}
