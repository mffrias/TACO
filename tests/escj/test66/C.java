package escj.test66;

class T {
}

class C0 {
	T a = null;
	T b = new T();
	T c;
	T d = ((T) ((Object) null));
	T e = a;
	/*@ non_null */T k = null;

	C0() {
	}

	void m() {
		a = null;
		b = new T();
		d = ((T) ((Object) null));
		e = a;
		k = null;
	}
}

class C1 {
	/*@ non_null */T l = new T();
	/*@ non_null */T m;
	/*@ non_null */T n = ((T) ((Object) null));

	C1() {
	}

	void m() {
		l = new T();
		n = ((T) ((Object) null));
	}
}

class C2 {
	T b;
	/*@ non_null */T o;

	C2(int x) {
		switch (x) {
		case 0:
			b = null;
			break;
		case 1:
			o = null;
			break;
		case 2:
			b = new T();
			break;
		case 3:
			o = new T();
			break;
		}
	}

	void m(int x) {
		switch (x) {
		case 0:
			b = null;
			break;
		case 1:
			o = null;
			break;
		case 2:
			b = new T();
			break;
		case 3:
			o = new T();
			break;
		}
	}
}

class C3 {
	T b;
	/*@ non_null */T o;

	{
		o = null;
	} // always warns here

	C3() {
	}
}
