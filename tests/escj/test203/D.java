package escj.test203;

class D extends B {
	private/*@ spec_public */C a;

	//@ represents isInit = (a!=null && a.isInit);

	public int m() {
		return a.m();
	}
}
