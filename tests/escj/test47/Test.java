package escj.test47;

class Test {

    static int x, y;

    //@ invariant true;   // error
    //@ axiom true;

    //@ invariant x == 0;
    //@ invariant y == 0;

    //@ invariant x == y;
    //@ invariant x == Foo.y;

    //@ invariant (\forall int x; x == 0);  // error
    //@ invariant (\forall Test t; t == null); // error
    //@ invariant (\forall Test t; t.x == 0);

}

class Foo {

    static int x, y;

}


