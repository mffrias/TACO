package escj.test262;

public class InnerClassTest {
	private char blankChar = ' ';
	private boolean isTrue = true;
	
	private int followUpCode;
			
	public char getBlankChar(){
		return blankChar;
	}
		
	public void setBlankChar(char inputChar) {
		this.blankChar = inputChar;
	}
	
	// Problems with ESC/Java2 when this method is not commented out,
	// but only on linux and windows, not on Mac OS X
	public void setBlankChar(String inputChars) {
		this.blankChar = inputChars.charAt(0);
	}

	public boolean isTrue() {
		return isTrue;
	}

	public void setTrue(boolean isTrue) {
		this.isTrue = isTrue;
	}

	public int getFollowUpCode() {
		return followUpCode;
	}

	public void setFollowUpCode(int followUpCode) {
		this.followUpCode = followUpCode;
	}
	
}
