package krueger.toronto.edu.cormenImple;

//@nullable_by_default;
public class BinomialHeapNode {

	public int key;

	public /*@ nullable @*/ BinomialHeapNode p;

	public /*@ nullable @*/ BinomialHeapNode child;

	public /*@ nullable @*/ BinomialHeapNode sibling;

	public int degree;

	public BinomialHeapNode(int k) {
		key = k;
		p = null;
		child = null;
		sibling = null;
		degree = 0;
	}


}