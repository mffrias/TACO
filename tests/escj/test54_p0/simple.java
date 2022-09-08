package escj.test54_p0;

class C {
    
    /*@ non_null*/ Integer i;
    /*@ non_null*/ Integer j;
    

    C() {
	init();
	init();
	init();
    }

    /*@ helper */ private void init() {
	i = new Integer(13);
	j = new Integer (55);
    }
}
