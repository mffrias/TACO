package escj.test07;

class fieldsuper {
	int instancevar;
}

class fieldref extends fieldsuper {
	static int classvar; // Used for testing by fieldref class

	public static int m1(fieldref o) {
		//@ assume o != null;
		return fieldref.classvar + o.classvar + o.instancevar;
	}

	public int m2() {
		//@ assume this != null; 
		return instancevar + this.instancevar + super.instancevar;
	}
}
