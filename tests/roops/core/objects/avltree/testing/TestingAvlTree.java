package roops.core.objects.avltree.testing;

public class TestingAvlTree{
	
	/**
	 * @Invariant true;
	 */
	private  TestingAvlNode root; 
	
	private int size;  
	
		
	
	/**
	 * @Modifies_Everything
	 * 
	 * @Requires
	 *	true;
	 *
	 * @Ensures
	 *	true;
	 */
	public TestingAvlNode insert(TestingAvlNode node, TestingAvlNode freshNode) {
		if (node == null) {
			node = freshNode;
		} else if (freshNode.data < node.data)
			node.left = insert(node.left, freshNode);
		else if (freshNode.data > node.data)		
			node.right = insert(node.right, freshNode);
		else throw new RuntimeException();
		return restoreBalance(node);
	}
	

	
	private TestingAvlNode restoreBalance(TestingAvlNode node) {
		int l_Height = node.left  == null ? 0 : node.left.height;
		int r_Height = node.right == null ? 0 : node.right.height;
		
		if (l_Height > r_Height) {
			int ll_Height = node.left.left  == null ? 0 : node.left.left.height;
			int lr_Height = node.left.right == null ? 0 : node.left.right.height;
			if (ll_Height < lr_Height)
				node.left = rotateLeft(node.left);
			node = rotateRight(node);
			
		} else if (l_Height < r_Height){
			int rl_Height = node.right.left  == null ? 0 : node.right.left.height;
			int rr_Height = node.right.right == null ? 0 : node.right.right.height;
			if (rl_Height > rr_Height)
				node.right = rotateRight(node.right);
			node = rotateLeft(node);
		}
		
		// fixHeights(node);
		return node;
	}
	
	private TestingAvlNode rotateLeft(TestingAvlNode node) {
		TestingAvlNode r_node = node.right;
		TestingAvlNode rl_node = r_node.left;
		r_node.left = node;
		node.right = rl_node;
		fixHeights(node);
		fixHeights(r_node);
		return r_node;
	}
	
	private TestingAvlNode rotateRight(TestingAvlNode rt) {
		TestingAvlNode l_node = rt.left;
		TestingAvlNode lr_node = l_node.right;
		l_node.right = rt;
		rt.left = lr_node;
		fixHeights(l_node);
		fixHeights(rt);
		return l_node;
	}
	
	private void fixHeights(TestingAvlNode node) {
		int leftHeight = node.left == null ? 0 : node.left.height;
		int rightHeight = node.right == null ? 0 : node.right.height;
		// node.height = 1 + (leftHeight > rightHeight ? leftHeight : rightHeight);
		node.height = (leftHeight > rightHeight ? leftHeight : rightHeight);
	}
	


	/** 
	 */	
	

	
	
	
}

