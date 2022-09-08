package escj.test48;

class C_init {

	C_init()
	//@ ensures b == false;
	{
		return;
	}

	public boolean b = true || false;
}
