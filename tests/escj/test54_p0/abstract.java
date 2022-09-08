package escj.test54_p0;

abstract class T {
  /*@ helper */ abstract void m();
  /*@ helper */ native final void n();
}

interface J {
  /*@ helper */ void p();
}
