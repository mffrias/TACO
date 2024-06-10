package ar.edu.taco.utils;

import ar.edu.taco.TacoAnalysisResult;

public class Message {
	
	public TranslateThread theWorkingThread;
	public boolean theResult; //false = UNSAT, true = SAT
	public boolean TO = false;
	
	public TranslateThread getTheWorkingThread() {
		return theWorkingThread;
	}
	
	public boolean getTheResult() {
		return theResult;
	}
	
	public boolean getTO() {
		return TO;
	}
	
	public void setTheWorkingThread(TranslateThread translateThread) {
		this.theWorkingThread = translateThread;
	}
	
	public void setTheResult(boolean b) {
		this.theResult = b;
	}
	
	public void setTO(boolean b) {
		this.TO = b;
	}

}
