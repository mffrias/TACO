package ar.edu.taco.jml;

public class Quantifiers {

	//@ ensures (\exists int i; i >0 && i<0; \result==i);
	public int returnZero() {
		return 1;
	}
	
}
