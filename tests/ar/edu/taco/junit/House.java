/**
 * 
 */
package ar.edu.taco.junit;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author elgaby
 *
 */
public class House {

	 /**
	  * @author elgaby
	  */
	
	 
	 	public static boolean brandNew;
	 	public static int[] something;
		public static FrameSize frame;
		public static FrameSize frame1;
		public static Map aMap;
	 	
	 	private /*@ spec_public @*/ int doors;
	 	private /*@ spec_public @*/ int[] entranceCode;
		private /*@ spec_public @*/ Window mainWindow;
		private /*@ spec_public @*/ Set neighbordsSons;
		private /*@ spec_public @*/ Map windowsOwners;
	 	
	
	 	/*@
	 	  @ requires House.brandNew == false;
	 	  @ requires House.something.length == 4;
		  @ requires House.frame != null;
	   	  @ requires (\forall int j; 0 <= j && j < House.something.length; House.something[j] > 0 );
	 	  @ requires this.doors == 2;
	 	  @ requires this.entranceCode.length == 4;
		  @ requires this.neighbordsSons != null;
		  @ requires this.windowsOwners != null;
	   	  @ requires (\forall int j; 0 <= j && j < this.entranceCode.length; this.entranceCode[j] > 0 );
	 	  @ requires frontWindow != null && backWindow != null;
	 	  @ requires frontWindow.canRefute == true;
	 	  @ requires frontWindow.squareMeters == 3;
	 	  @ requires frontWindow.frameSize.height == 1;
	 	  @ requires frontWindow.frameSize.width == 2;
		  @ requires mainWindow != null;
	 	  @ requires doors.length == 3;
	 	  @ requires (\forall int j; 0 <= j && j < doors.length; doors[j] > 0 );
		  @ requires value > 0;
		  @ requires aa != null;
		  @ requires ee != null;
	 	  @
	 	  @ ensures \result == true; 
	 	  @*/
		public boolean createHouse(Window frontWindow, Window backWindow, int[] doors, int value, Set aa, Map ee) {
			
	 		return (aMap.isEmpty() || (neighbordsSons.size() == 0) || (aa.size() == 0) || (ee.size() == 0)); 
	 	}
	
		/**
		 * @return the doors
		 */
		public int getDoors() {
			return doors;
		}
	
		/**
		 * @param doors the doors to set
		 */
		public void setDoors(int doors) {
			this.doors = doors;
		}
	
		/**
		 * @return the entranceCode
		 */
		public int[] getEntranceCode() {
			return entranceCode;
		}
	
		/**
		 * @param entranceCode the entranceCode to set
		 */
		public void setEntranceCode(int[] entranceCode) {
			this.entranceCode = entranceCode;
		}
	
		/**
		 * @return the mainWindow
		 */
		public Window getMainWindow() {
			return mainWindow;
		}
	
		/**
		 * @param mainWindow the mainWindow to set
		 */
		public void setMainWindow(Window mainWindow) {
			this.mainWindow = mainWindow;
		}
		
		/**
		 * @return the neighbordsSons
		 */
		public Set getNeighbordsSons() {
			return neighbordsSons;
		}
	
		/**
		 * @param neighbordsSons the neighbordsSons to set
		 */
		public void setNeighbordsSons(Set neighbordsSons) {
			this.neighbordsSons = neighbordsSons;
		}
	
		/**
		 * @return the windowsOwners
		 */
		public Map getWindowsOwners() {
			return windowsOwners;
		}
	
		/**
		 * @param windowsOwners the windowsOwners to set
		 */
		public void setWindowsOwners(Map windowsOwners) {
			this.windowsOwners = windowsOwners;
		}
	 
}
