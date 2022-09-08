package escj.test55;

/* This simple code shows the differences in our three inlining schemes,
   obtained by different combinations of the last three args to the _call_
   routine:

   A) inline a call without checking (true, false, true)
   B) inline an artificial call (true, true, true)
   C) inline a call with checking (true, true, false)

   We get three different warnings on method _p_ below, in the three cases above.
*/

class C_skip {
	int i;

	int p(C_skip c) {
		return m(c);
	}

	//@ requires c.i > 0;
	int m(C_skip c) {
		i = c.i;
		return i;
	}
}
