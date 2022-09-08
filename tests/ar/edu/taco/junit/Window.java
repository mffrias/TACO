package ar.edu.taco.junit;

public class Window {
	
	public boolean canRefute;
	public int squareMeters;
	public FrameSize frameSize;
	
	public boolean isCanRefute() {
		return canRefute;
	}

	public void setCanRefute(boolean canRefute) {
		this.canRefute = canRefute;
	}

	public int getSquareMeters() {
		return squareMeters;
	}
	
	public void setSquareMeters(int squareMeters) {
		this.squareMeters = squareMeters;
	}

	public FrameSize getFrameSize() {
		return frameSize;
	}

	public void setFrameSize(FrameSize frameSize) {
		this.frameSize = frameSize;
	}
}
