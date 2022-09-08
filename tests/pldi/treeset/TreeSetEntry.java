package pldi.treeset;



public class TreeSetEntry {
	public int key;
	public /*@ nullable @*/TreeSetEntry parent;
	public boolean color = false;
	public /*@ nullable @*/TreeSetEntry left = null;
	public /*@ nullable @*/TreeSetEntry right = null;
	
	public int blackHeight;

	public TreeSetEntry () {}
}