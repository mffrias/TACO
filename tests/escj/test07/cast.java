package escj.test07;


class Cast {

  void m1(Object o) {
    //@ assume o instanceof String;
    String s = (String)o;
  }

  void m2(Object o) {
    //@ assume \typeof(o) <: \type(String);
    String s = (String)o;
  }

}
