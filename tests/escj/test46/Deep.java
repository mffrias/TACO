package escj.test46;
/**
 ** Test escjava's reasoning about member inner classes, part IV
 **/


/*
 * Make sure many levels of this$0 derefereces don't confuse us:
 */
class Outside_1 {
    int x;

    class Level1 {
	class Level2 {
	    class Inner {
		//@ requires x>0;
		//@ modifies x;
		//@ ensures x>5;
		void m() { x = 10; }
	    }
	}
    }

    void test1() {
	Level1 l1 = new Level1();
	Level1.Level2 l2 = l1.new Level2();
	Level1.Level2.Inner I = l2.new Inner();

	I.m();           // error: precondition not meet
    }


    void test2() {
	Level1 l1 = new Level1();
	Level1.Level2 l2 = l1.new Level2();
	Level1.Level2.Inner I = l2.new Inner();

	x = 1;
	I.m();
	//@ assert x>5;
    }
}
