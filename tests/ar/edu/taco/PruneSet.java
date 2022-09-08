package ar.edu.taco;

import java.util.Set;

public class PruneSet {

/*@
  @ requires aa != null;         
  @ ensures \result == true;
  @*/
  public boolean getSetSize(Set aa) {       
      return aa.size() <= 0;
  }
	
}
