package examples.avltree.base;

// Basic node stored in AVL trees
// Note that this class is not accessible outside
// of package DataStructures

class AvlNode {
	// Friendly data; accessible by other package routines
	int element; // The data in the node

	/*@ nullable @*/AvlNode left; // Left child

	/*@ nullable @*/AvlNode right; // Right child

	int height; // Height

	// Constructors
	AvlNode(final int theElement) {
		this(theElement, null, null);
	}

	AvlNode(final int theElement, final AvlNode lt, final AvlNode rt) {
		this.element = theElement;
		this.left = lt;
		this.right = rt;
		this.height = 0;
	}

}
