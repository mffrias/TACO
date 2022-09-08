package qu.edu.qa;


public class Fraction {
	
    public int num;
    public int denum;
    
//    public static Fraction PI;
    //initialization apparently not yet supported. Can be solved 
    //by adding the initialization in the invariant as below.
    
  /*@
    @ invariant denum!=0;
    @*/
    
    
  /*@ 
    @ requires denum!=0;
    @*/
    public Fraction(int num, int denum){
        this.num=num;
        this.denum=denum;
    }
    
  /*@ 
    @ ensures \result == num;
    @*/
    public /*@ pure */ int getNum() {
		return num;
	}
    
  /*@ 
    @ ensures denum == \result;
    @*/
	public /*@ pure */ int getDenum() {
		return denum;
	}
	

	
  /*@ 
    @ requires f1.denum != 0;
    @ requires f2.denum != 0;
    @ ensures f3.num == \old(f1.num) * \old(f2.num);
    @ ensures f3.denum == \old(f1.denum) * \old(f2.denum);
    @ signals (Exception e) true;
    @*/
    public Fraction mul(Fraction f1, Fraction f2, Fraction f3){
    	int n = f1.num*f2.num;
    	int d = f1.denum*f2.denum;
    	f3.num = n;
    	f3.denum = d;
    	return f3;
    }
   
    

  /*@ 
    @ ensures i - 3f == 4f;
    @*/
    public float test(float i){
//    	i++;
    	return i;
    }
    

    
  /*@ 
    @ requires f1.denum != 0;
    @ requires f2.denum != 0;
    @ ensures \result.num==f1.num*f2.denum;
    @ ensures \result.denum==f1.denum*f2.num;
    @*/
    public static Fraction div(Fraction f1, Fraction f2, Fraction f3){
    	f3.num = f1.num*f2.denum;
    	f3.denum = f1.denum*f2.num;
        return f3;
        
    }


  /*@ 
    @ ensures \result.num==f1.num;
    @ ensures \result.denum==f1.denum*f2;
    @*/
    public static Fraction div2(Fraction f1, int f2){
        return new Fraction(f1.num,f1.denum*f2);
    }
    
    
//  /*@ 
//    @ public static model int tdenum1;
//    @ public static model int tdenum2;
//    @ static represents tdenum1 = f1.getDenum();
//    @ static represents tdenum2 = f2.getDenum();
//    @ public static ghost int p1 = ppcm(tdenum1,tdenum2);
//    @ ensures p1 % tdenum1==0;
//    @ ensures p1 % tdenum2==0;
//    @ ensures (double)\result.num / (double)\result.denum== (double)f1.num / (double)f1.denum + (double)f2.num / (double)f2.denum;
//    @*/
//    public static Fraction add(Fraction f1, Fraction f2){
//        int temp=ppcm(f1.denum,f2.denum);
//        return new Fraction((temp/f1.denum)*f1.num + (temp/f2.denum)*f2.num,temp);
//    }
//    
//  /*@ 
//    @ public static ghost int p2 = ppcm(f1.getDenum(),f2.getDenum());
//    @ ensures p2 % f1.getDenum()==0;
//    @ ensures p2 % f2.getDenum()==0;
//    @ ensures (double)\result.num / (double)\result.denum== (double)f1.num / (double)f1.denum - (double)f2.num / (double)f2.denum;
//    @*/
//    public static Fraction sub(Fraction f1, Fraction f2){
//        int temp=ppcm(f1.denum,f2.denum);
//        return new Fraction((temp/f1.denum)*f1.num - (temp/f2.denum)*f2.num,temp);
//    }


//  /*@ 
//    @ ensures \result.num / \result.denum==d;
//    @*/
//    public static Fraction toFraction(double d){
//        String temp=String.valueOf(d);
//        System.out.println(temp.substring(0,temp.indexOf(".")));
//        System.out.println(temp.substring(temp.indexOf(".")+1));
//        int l=(int)Math.pow(10, temp.substring(temp.indexOf(".")+1).length());
//        return new Fraction((int)(d*l),l);
//    }

//  /*@ 
//    @ ensures \result==(double)f.num / (double)f.denum;
//    @*/
//    public static double toDouble(Fraction f){
//        return ((double)f.num) / ((double)f.denum);
//    }
//    

  /*@ 
    @ ensures \result % Nb1==0;
    @ ensures \result % Nb2==0;
    @*/
    private static int ppcm(int Nb1, int Nb2) {
        int Produit, Reste, PPCM;
        Produit = Nb1 * Nb2;
        Reste = Nb1 % Nb2;
        while (Reste != 0) {
            Nb1 = Nb2;
            Nb2 = Reste;
            Reste = Nb1 % Nb2;
        }
        PPCM = Produit / Nb2;
        return PPCM;
    } 

   /*@ 
     @ assignable num;
     @ assignable denum;
     @ ensures num == f.num;
     @ ensures denum == f.denum;
     @*/
    public void setValue(Fraction f) {
        num=f.num;
        denum=f.denum;
    }

    
//    /*@
//    @ ensures \result != null;
//    @*/
//    public String toString() {
//        return "Fraction{" + "num=" + num + ", denum=" + denum + '}';
//    }
}