package forArielGodio.leapYear.bug03;
public class LeapYear {
     /*@    requires 0 < year;
       @    ensures \old(year) % 4 != 0 ==> \result == false;
       @    ensures (\old(year) % 4 == 0 && \old(year) % 100 != 0) ==> \result == true;
       @    ensures (\old(year) % 4 == 0 && \old(year) % 100 == 0 && \old(year) % 400 != 0) ==> \result == false;
       @    ensures (\old(year) % 4 == 0 && \old(year) % 100 == 0 && \old(year) % 400 == 0) ==> \result == true;
       @*/
    public /*@ pure @*/ boolean isLeapYear(int year) {
        boolean leap = false;
         
        if (year % 4 == 0) 
        {
            if ( year % 100 == 0)  // if ( year % 100 == 0)
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


   public static void main(String[] args){

       int x = 42;
       int y = 18;
       int index = 0;

       while (index < 100){
           if (y + index < x){
               System.out.print("Listen ");
               index = index + x;
           } else {
               if (x < y || y + index == x) {
                   System.out.print("walk on water ");
                   index = index + x + y + 15;
               } else {
                   System.out.print("me porto bomito ");
                   index = index - y;
               }
           }
           if (index < 5*y){
               System.out.print("to ");
               index = index - y;
           } else {
               boolean cond4 = true;
               if (index < 100) {
                   System.out.print("by Eminem.");
                   index++;
               } else {
                   System.out.print("by Bad Bunny.");
                   index = index - 2*y;
               }
           }
       }

   }


}