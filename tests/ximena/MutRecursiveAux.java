package ximena;

public class MutRecursiveAux {

    public int m(int input){
        if (input == 0)
            return 1;
        else
            return 1 + m(input - 1);
    }

}
