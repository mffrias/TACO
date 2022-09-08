package forArielGodio;
public class StudentEnrollment {
	public static final int costPerCredit = 200;  
	public static final int totalCredits = 120;
	public static final int maxSemesterCredits = 20;

	/*@ spec_public @*/ private int firstName;
	/*@ spec_public @*/ private int lastName;
	/*@ spec_public @*/ private int passedCredits;  //number of credits which are passed during previous semesters
	/*@ spec_public @*/ private int enrollmentCredits; //number of credits which will get this semester
	/*@ spec_public @*/ private int tuitionBalance;
	/*@ spec_public @*/ private boolean lateRegistration;

	
	//@ invariant 0 <= enrollmentCredits && enrollmentCredits <= maxSemesterCredits;
	/*@ invariant tuitionBalance <= maxSemesterCredits * costPerCredit + (maxSemesterCredits * ((costPerCredit/100)*6)); @*/

	
	
	/*@ ensures this.firstName == firstName;
      @ ensures this.lastName == lastName;
      @ ensures passedCredits == 0 && enrollmentCredits == 0;
      @ ensures tuitionBalance == 0; @*/
	StudentEnrollment( int firstName,
			 int lastName) 
	{
		this.firstName = firstName;
		this.lastName = lastName;
	}

	
	/*@   	ensures (\old(tuitionBal) <= \old(maxSemesterCredits) * \old(costPerCredit) + \old(maxSemesterCredits) * ((\old(costPerCredit)/100)*6)) ==> this.tuitionBalance == tuitionBal; 
      @   	signals(IllegalArgumentException) (\old(maxSemesterCredits) * \old(costPerCredit) + \old(maxSemesterCredits) * ((\old(costPerCredit)/100)*6) < \old(tuitionBal)); @*/
	public void setTuitionBalance(int tuitionBal)
	{
		int maxTuitionBalance = maxSemesterCredits * costPerCredit + maxSemesterCredits * ((costPerCredit/100)*6);
		if (maxTuitionBalance <= tuitionBal) { //if (maxTuitionBalance < tuitionBalance) {
			throw new IllegalArgumentException();
		} else {
			this.tuitionBalance = tuitionBal;
		}
	}

}