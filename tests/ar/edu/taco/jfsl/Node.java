package ar.edu.taco.jfsl;

/** 
 * @Invariant
 *		( this.left != null => this.left.parent = this ) &&
 *		( this.right != null => this.right.parent = this ) &&
 *		( this.parent != null => this in this.parent.(left @+ right) ) &&
 *		( this !in this.^parent ) &&
 *		( all x : Node | x in this.left.^(left @+ right) @+ this.left @- null => x.key < this.key ) &&
 *		( all x : Node | x in this.right.^(left @+ right) @+ this.right @- null => x.key > this.key ) &&
 *		( this.color = false && this.parent != null => this.parent.color = true )  ;
 */
public class Node {
	public/*@ nullable @*/ Node parent, left, right;
	public boolean color;
	protected int key;

	public Node(int key) {
		this.right = null;
		this.left = this.right ;
		this.parent = this.left ;
		this.color = IntTree.BLACK;
		this.key = key;
	}
}