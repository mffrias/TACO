package escj.test07;

public class arrayinit {

//@requires true;
//@ensures true;   
  void m() {

    boolean b = (1<2);
    //@ assert b==true;

    boolean[] bs = { true, false };

    //@ assert bs.length == 2;
    //@ assert bs[0] == true;
    //@ assert bs[1] == false;

    bs = new boolean[] { false, true, false };

    //@ assert bs.length == 3;
    //@ assert bs[0] == false;
    //@ assert bs[1] == true;
    //@ assert bs[2] == false;

    boolean[][] bs2;
    bs2 = new boolean[][] { { true, false}, {false}, {} };
    //@ assert bs2.length == 3;
    //@ assert bs2[0].length == 2;
    //@ assert bs2[0][0] == true;
    //@ assert bs2[0][1] == false;
    //@ assert bs2[1].length == 1;
    //@ assert bs2[1][0] == false;
    //@ assert bs2[2].length == 0;

  }
}
