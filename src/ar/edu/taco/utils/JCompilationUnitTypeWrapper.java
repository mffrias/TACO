package ar.edu.taco.utils;

import org.multijava.mjc.JCompilationUnitType;

public class JCompilationUnitTypeWrapper {

	JCompilationUnitType theUnit;
	boolean fullyDeterminized = false;
	private int timeout;
	boolean timeOuted = false;
	boolean theOutput = false;
	
	public JCompilationUnitTypeWrapper(JCompilationUnitType theUnit) {
		this.theUnit = theUnit;
		this.fullyDeterminized = false;
		this.timeout = 0;
	}
	
	public JCompilationUnitType getUnit() {
		return this.theUnit;
	}
	
	public boolean getDeterminized() {
		return this.fullyDeterminized;
	}

	
	public void setDeterminized() {
		this.fullyDeterminized = true;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}
	
	public int getTimeout() {
		return timeout;
	}
	
	public boolean getTimeOuted() {
		return this.timeOuted;
	}

	
	public void setTimeOuted() {
		this.timeOuted = true;
	}
	
	public boolean getOutput() {
		return theOutput;
		
	}

	public void setOutput(boolean b) {
		this.theOutput = b;
		
	}

}
