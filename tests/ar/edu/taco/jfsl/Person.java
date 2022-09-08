package ar.edu.taco.jfsl;

/**
 * @Invariant ( this.first_name!=null ) &&
 *            ( this.last_name != null ) &&
 *            ( ( this.spouse != null && this.is_male==true  ) => this.spouse.is_male==false ) &&
 *            ( ( this.spouse != null && this.is_male==false ) => this.spouse.is_male==true );
 *
 */
public class Person {

	/*@ nullable @*/String first_name;

	/*@ nullable @*/String last_name;

	boolean is_male;

	/*@ nullable @*/Person spouse;

	/**
	 * @Requires ( new_spouse!=null ) &&
	 *           ( new_spouse.spouse==null );
	 *           
	 * @Ensures this.spouse!=null ;
	 */
	public void get_married(Person new_spouse) {
		new_spouse.spouse = this;
		this.spouse = new_spouse;
	}

	/**
	 * 
	 * @Invariant this.street!=null && this.number>0 ;
	 */
	class PersonAddress {
		/*@ nullable @*/String street;
		int number;

		boolean is_home;

		/**
		 * @Ensures return==this.is_home ;
		 */
		public/*@ pure @*/boolean is_home() {
			return is_home;
		}

	}
}
