package roops.core.objects.avltree.base;

// Basic node stored in AVL trees
// Note that this class is not accessible outside
// of package DataStructures

/*@nullable_by_default@*/
public class AvlNode {

	public int element; 

	public AvlNode left; 

	public AvlNode right; 

	public int height; 

	public AvlNode() {
	}
	
//	public AvlNode(int x, AvlNode object, AvlNode object2) {
//		this.element = x;
//		this.left = object;
//		this.right = object2;
//		this.height = 0;
//
//	}

}

