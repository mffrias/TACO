package ar.edu.taco.simplifier;

public class RepresentsFieldName {

	Object container;

	//@ model boolean isEmpty;
	//@ represents isEmpty \such_that isEmpty==true <==> container==null;
	
	//@ ensures isEmpty==true;
	void clear() {
		container=null;
	}
	

}
