package ar.edu.taco.loops;

public class LoopNoExit {

	private /*@ nullable @*/ LoopNoExit next;
	
	//@ ensures \result==true;
	public boolean loopNoExit() {
		LoopNoExit curr=this;
		
		while (curr!=null) {
			curr=curr.next;
		}
		if (curr!=null) {
			return false;
		}
		return true;
	}
}
