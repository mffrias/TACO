package forArielGodio;
public class FindFirstZero {
	 //@ ensures x.length == 0 ==> \result == -1;
   	 //@ ensures 0 <= \result && \result < x.length ==> x[\result] == 0 && (\forall int i; 0 <= i && i < \result; x[i] != 0);
	 //@ ensures \result == -1 ==> (\forall int i; 0 <= i && i < x.length; x[i] != 0);
    	 public static int findFirstZero(int[] x) {
//         	assert x.length >= 0;  //NOT IMPLEMENTED
         	if (x.length == 0) {//if (x.length == 0) {
            		return -1;
        	} else {
            		int index = 0;
            		//@ decreases x.length - index;
            		while (x.length - index > 0 && x[index] != 0) {
                		index = index + 1;
            		}
            		if (x.length - index == 0) {
               			index = -1;
            		}
            	return index;
        	}
    	}
}