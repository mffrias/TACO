package escj.test07;

class getsplus {
    int m() {
	int i;
	i= 6;
	i+= 6;
	//@ assert i == 12; 
	return i;
    }
}
