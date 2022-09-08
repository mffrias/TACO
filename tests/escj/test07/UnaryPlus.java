package escj.test07;

class UnaryPlus {
  //@ ensures \result == 1 && \result == -(-1);
  int m() {
    int x = +1;
    return x;
  }
}
