package escj.test204;

public class Teste {
    private static final short BMASK = 0x00FF;
 
	/**
     * Multiply a by b and stores the result in r.
     * Requires r.length >= a.length + 1.
     * 
     * @param r the array where the result is to be stored.
     * @param a the array containing the value of a.
     * @param value the value to multiply for.
     * @return r = a * value.
     * 
     * @throws NullPointerException if either a or r is null.
     * @throws ArrayOutOfBoundsException if r.length < a.length + 1.
     */
    public static void multiply(byte[] r, byte[] a, byte value) 
    //@requires r != null & a != null;
    //@requires r.length >= a.length + 1;
    
    {
    	short b = (short) (value & BMASK);
    	short v=0;
    	short idxr = (short)(r.length - 1);
    	
       	for(short idxa = (short) (a.length - 1); idxa>=0; idxa--){	
    		v += (short) (b * (a[idxa] & BMASK)); 
   			r[idxr--] = (byte) v;
   			v >>>= 8;
			// >>>= works on int type, therefore the short value may remain negative.
   			// the following line allows the use of this library on any java environment
   			v &= BMASK;
   		}
   		r[idxr] = (byte) v;
   		
   		//clear the remaining of the r array
   		while(idxr!=0)
   			r[--idxr] = 0;
    }
    
}
