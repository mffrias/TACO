package examples.avltree.plus.junit;

import static org.junit.Assert.*;

import java.util.LinkedList;

import org.junit.Test;

import examples.avltree.plus.AVL;
import examples.avltree.plus.AVLNode;

public class AVLTester {

	//*************************************************************************
	//************ From now on functions for checking the repOk ***************
	//*************************************************************************
	public static boolean repOk(AVL tree) {
		LinkedList<AVLNode> allNodes = new LinkedList<AVLNode>();
		LinkedList<AVLNode> stack = new LinkedList<AVLNode>();
		LinkedList<Integer> allData = new LinkedList<Integer>();
		
		if (tree.root != null)
			stack.add(tree.root);
		
		while (!stack.isEmpty()) {
			AVLNode node = stack.remove();
			if (allNodes.contains(node)) {
				return false; // Not acyclic.
			}
			else {
				allNodes.add(node);
			}
			
			// if (node.element == null)
			//	return false; // Null data.
			if (allData.contains(node.element)) {
				return false; // Repeated data.
			}
			else {
				allData.add(node.element);
			}
			
			if (node.left != null) {
				
				if (node.left.parent!=node)
					return false;
				
				stack.add(node.left);
			}
			if (node.right != null) {

				if (node.right.parent!=node)
					return false;
				
				stack.add(node.right);
			}
		}
		
		if (!isOrdered(tree.root)) {
			return false;
		}
		
		if(!balanced(tree.root)) {
			return false;
		}
		
		/*AVLNode node = null;
		for (LinkedListIterator allNodesIt = allNodes.iterator(); allNodesIt.hasNext();  node = (AVLNode) allNodesIt.next()) {
			int l_Height = node.left == null ? 0 : node.left.height;
			int r_Height = node.right == null ? 0 : node.right.height;
			int difference = l_Height - r_Height;
			if (difference < -1 || difference > 1)
				return false; // Not balanced.
			int max = l_Height > r_Height ? l_Height : r_Height;
			if (node.height != 1 + max)
				return false; // Wrong height.
		}*/
		
		// if (allNodes.size() != size)
		//	return false; // Wrong size.
		
		return true;
	}
	
	private static boolean balanced(AVLNode n) {
		if (n == null) {
		  return true;
		}
		int lh = 0;
		int rh = 0;
		if (n.left != null)
			lh = n.left.height;
		if (n.right != null)
			rh = n.right.height;
		
		int max = lh > rh ? lh : rh;
		if (n.height != 1 + max)
				return false; // Wrong height.
		
		return ((lh == rh) || (lh == rh + 1) || (lh + 1 == rh))
			&& balanced(n.left) && balanced(n.right);
	}	
	/*boolean repOk() {
		LinkedList<AVLNode> allNodes = new LinkedList<AVLNode>();
		LinkedList<Integer> allData = new LinkedList<Integer>();
		LinkedList<AVLNode> stack = new LinkedList<AVLNode>();
		
		if (root != null)
			stack.add(root);
		
		while (!stack.isEmpty()) {
			AVLNode node = stack.remove();
			if (allNodes.contains(node))
				return false; // Not acyclic.
			else
				allNodes.add(node);
				
			// if (node.element == null)
			//	return false; // Null data.
			if (allData.contains(node.element))
				return false; // Repeated data.
			else
				allData.add(node.element);
			if (node.left != null)
				stack.add(node.left);
			if (node.right != null)
				stack.add(node.right);
		}
		
		if (!isOrdered(root))
			return false;
		
		AVLNode node = null;
		for (LinkedListIterator allNodesIt = allNodes.iterator(); allNodesIt.hasNext();  node = (AVLNode) allNodesIt.next()) {
			int l_Height = node.left == null ? 0 : node.left.height;
			int r_Height = node.right == null ? 0 : node.right.height;
			int difference = l_Height - r_Height;
			if (difference < -1 || difference > 1)
				return false; // Not balanced.
			int max = l_Height > r_Height ? l_Height : r_Height;
			if (node.height != 1 + max)
				return false; // Wrong height.
		}
		
		// if (allNodes.size() != size)
		//	return false; // Wrong size.
		
		return true;
	}
*/	
    private static boolean isOrdered(AVLNode n) {
        return n == null || isOrdered(n, null, null);
    }

    private static boolean isOrdered(AVLNode n, Integer min, Integer max) {
        // if (n.element == null)
        //    return false;
        if ((min != null && n.element < min) ||
        	(max != null && n.element > max))
            return false;
        if (n.left != null)
            if (!isOrdered(n.left, min, n.element))
                return false;
        if (n.right != null)
            if (!isOrdered(n.right, n.element, max))
                return false;
        return true;
    }

    @Test
    public void test_empty() {
    	AVL tree = new AVL();
    	assertTrue(repOk(tree));
    }
    
    @Test 
    public void test_singleton() {
    	AVLNode node = new AVLNode();
    	node.height = 1;
    	node.balance = 0;
    	
    	AVL tree = new AVL();
    	tree.root=node;
    	assertTrue(repOk(tree));
    }
    
    @Test
    public void test_full_size_3() {
    	AVLNode n0 = new AVLNode();
    	AVLNode n1 =new AVLNode();
    	AVLNode n2 =new AVLNode();
    	
    	n0.element=2;
    	n0.height =2;
    	n0.left = n1;
    	n0.right = n2;
    	
    	n1.element = 1;
    	n1.parent = n0;
    	n1.height = 1;

    	n2.element = 3;
    	n2.parent = n0;
    	n2.height = 1;

    	AVL tree = new AVL();
    	tree.root = n0;
    	
    	assertTrue(repOk(tree));
    }

    @Test
    public void test_wrong_size_3() {
    	AVLNode n0 = new AVLNode();
    	AVLNode n1 =new AVLNode();
    	AVLNode n2 =new AVLNode();
    	
    	n0.element=2;
    	n0.height =2;
    	n0.left = n1;
    	n0.right = n2;
    	
    	n1.element = 3;
    	n1.parent = n0;
    	n1.height = 1;

    	n2.element = 1;
    	n2.parent = n0;
    	n2.height = 1;

    	AVL tree = new AVL();
    	tree.root = n0;
    	
    	assertTrue(!repOk(tree));
    }


    @Test 
    public void test_remove_from_singleton() {
    	AVLNode node = new AVLNode();
    	node.height = 1;
    	node.balance = 0;
    	
    	AVL tree = new AVL();
    	tree.root=node;
    	assertTrue(repOk(tree));
    	
    	tree.remove(0);
    	
    	assertTrue(repOk(tree));
    	
    }


    @Test
    public void testBugSize7() {
    	AVLNode n0 = new AVLNode();
    	AVLNode n1 = new AVLNode();
    	AVLNode n2 = new AVLNode();
    	AVLNode n3 = new AVLNode();
    	AVLNode n4 = new AVLNode();
    	AVLNode n5 = new AVLNode();
    	AVLNode n6 = new AVLNode();

    	n0.element = 0;
    	n0.height = 4;
    	n0.balance = -1;
    	n0.left = n1;
    	n0.right = n2;
    	
    	n1.element = -14;
    	n1.height = 3;
    	n1.balance = -1;
    	n1.parent = n0;
    	n1.left = n3;
    	n1.right = n4;
    	
    	n2.element = 1;
    	n2.height = 2;
    	n2.balance = 1;
    	n2.parent = n0;
    	n2.left = null;
    	n2.right = n5;
    	
    	n3.element = -15;
    	n3.height = 2;
    	n3.balance = -1;
    	n3.parent = n1;
    	n3.left = n6;
    	n3.right = null;
    	
    	n4.element = -13;
    	n4.height = 1;
    	n4.balance = 0;
    	n4.parent = n1;
    	n4.left = null;
    	n4.right = null;
    	
    	n5.element = 2;
    	n5.height = 1;
    	n5.balance = 0;
    	n5.parent = n2;
    	n5.left = null;
    	n5.right = null;

    	n6.element = -16;
    	n6.height = 1;
    	n6.balance = 0;
    	n6.parent = n3;
    	n6.left = null;
    	n6.right = null;


    	AVL t = new AVL();
    	t.root = n0;
    	
    	assertTrue(repOk(t));
    	
    	t.remove(0);
    	boolean repOk_post = repOk(t);
    	assertTrue(repOk_post);
    	
    }
    
}
