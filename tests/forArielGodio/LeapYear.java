package forArielGodio;
public class LeapYear {
    /*@ requires 0 < year;
      @ ensures \old(year) % 4 != 0 ==> \result == false;
      @ ensures (\old(year) % 4 == 0 && \old(year) % 100 != 0) ==> \result == true;
      @ ensures (\old(year) % 4 == 0 && \old(year) % 100 == 0 && \old(year) % 400 != 0) ==> \result == false;
      @ ensures (\old(year) % 4 == 0 && \old(year) % 100 == 0 && \old(year) % 400 == 0) ==> \result == true;
      @*/
    public /*@ pure @*/ boolean isLeapYear(int year) {
        boolean leap = false;
         
        if (year % 4 == 0) // if (year % 4 == 0)
        {
            if ( year % 100 == 0)
            {
                if ( year % 400 == 0)
                    leap = true;
                else
                    leap = false;
            }
            else
                leap = true;
        }
        else
            leap = false;
	
	return leap;
   }
}