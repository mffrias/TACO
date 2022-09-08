package escj.test306;

public class Test306 {
	/*@ non_null*/final String s331 = "a";
	/*@ nullable*/String s555;

	//@ invariant s331.length() == s331.length();
	//@ invariant s555.length() == s555.length();

	//@ requires s331.length() > 336;
	//@ requires s555.length() > 556;
	void m333() {
		int i = s331.hashCode();
	}

	//@ requires i > 0;
	void m444(int i) {
	}

	//@ invariant (\forall int i; s331.length() == s331.length());
	//@ invariant (\forall int i; s555.length() == s555.length());
}
