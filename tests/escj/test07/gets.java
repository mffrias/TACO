package escj.test07;

class gets {
	int m() {
		int i;
		i = 6;
		//@ assert i == 6; 
		return i;
	}

	void n() {
		int i, j;
		j = 6;
		i = j + (j = 2);
		//@ assert i == 8; 
	}
}
