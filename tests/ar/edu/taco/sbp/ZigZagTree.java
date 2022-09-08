package ar.edu.taco.sbp;

public class ZigZagTree {

	
	public static class Zig {
		public /*@ nullable @*/ Zag zag;
	}
	
	public static class Zag {
		public /*@ nullable @*/ Zig zig;
	}
	
	public /*@ nullable @*/ Zig root_zig;
	public /*@ nullable @*/ Zag root_zag;

	//@ ensures \result==true;
	public boolean showInstance() {
		
		if (root_zig==null)
			return true;
		
		if (root_zig.zag==null)
			return true;
		
		if (root_zig.zag.zig==null)
			return true;

		if (root_zig.zag.zig.zag!=root_zag)
			return true;

		return false;
	}
	
}
