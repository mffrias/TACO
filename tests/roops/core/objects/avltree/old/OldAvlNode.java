package roops.core.objects.avltree.old;

// Basic node stored in AVL trees
// Note that this class is not accessible outside
// of package DataStructures

public class OldAvlNode {

	public int element; 

	public/*@ nullable @*/OldAvlNode left; 

	public/*@ nullable @*/OldAvlNode right; 

	public int height; 

	public OldAvlNode() {
	}
}
