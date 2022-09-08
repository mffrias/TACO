package escj.test45;

class AllocMorass {
  AllocMorass f;
  
  void m0(AllocMorass am) {
    AllocMorass n = new AllocMorass();
    //@ assert am.f != n;
  }

  void m1() {
    AllocMorass n = new AllocMorass();
    //@ assert this.f != n;
  }

  //@ requires am != null;
  //@ ensures this != am.f;
  AllocMorass(AllocMorass am) {
  }

  AllocMorass() {
  }
}
