package roops.core.objects;

import java.lang.reflect.Field;

public class BinomialHeap {

	/*@
    @ invariant (\forall BinomialHeapNode n; \reach(Nodes, BinomialHeapNode, sibling + child).has(n); n.parent != null ==> n.key >= n.parent.key );
    @ invariant (\forall BinomialHeapNode n; \reach(Nodes, BinomialHeapNode, sibling + child).has(n); n.sibling != null ==> \reach(n.sibling, BinomialHeapNode, sibling + child).has(n) == false );
    @ invariant (\forall BinomialHeapNode n; \reach(Nodes, BinomialHeapNode, sibling + child).has(n); n.child != null ==> \reach(n.child, BinomialHeapNode, sibling + child).has(n) == false );
    @ invariant (\forall BinomialHeapNode n; \reach(Nodes, BinomialHeapNode, sibling + child).has(n); ( n.child != null && n.sibling != null ) ==>
    @                                                  (\forall BinomialHeapNode m; \reach(n.child, BinomialHeapNode, child + sibling).has(m); \reach(n.sibling, BinomialHeapNode, child + sibling).has(m) == false) );
    @ invariant (\forall BinomialHeapNode n; \reach(Nodes, BinomialHeapNode, sibling + child).has(n); ( n.child != null && n.sibling != null ) ==>
    @                                                  (\forall BinomialHeapNode m; \reach(n.sibling, BinomialHeapNode, child + sibling).has(m); \reach(n.child, BinomialHeapNode, child + sibling).has(m) == false) );
    @ invariant (\forall BinomialHeapNode n; \reach(Nodes, BinomialHeapNode, sibling + child).has(n); n.degree >= 0 );
    @ invariant (\forall BinomialHeapNode n; \reach(Nodes, BinomialHeapNode, sibling + child).has(n); n.child == null ==> n.degree == 0 );
    @ invariant (\forall BinomialHeapNode n; \reach(Nodes, BinomialHeapNode, sibling + child).has(n); n.child != null ==> n.degree == \reach(n.child, BinomialHeapNode, sibling).int_size() );
    @ invariant (\forall BinomialHeapNode n; \reach(Nodes, BinomialHeapNode, sibling + child).has(n); n.child != null ==> \reach(n.child.child, BinomialHeapNode, child + sibling).int_size() == \reach(n.child.sibling, BinomialHeapNode, child + sibling).int_size() );
    @ invariant (\forall BinomialHeapNode n; \reach(Nodes, BinomialHeapNode, sibling + child).has(n); n.child != null ==>
    @                                                  ( \forall BinomialHeapNode m; \reach(n.child, BinomialHeapNode, sibling).has(m); m.parent == n ) );
    @ invariant (\forall BinomialHeapNode n; \reach(Nodes, BinomialHeapNode, sibling + child).has(n); ( n.sibling != null && n.parent != null ) ==> n.degree > n.sibling.degree );
    @
    @ invariant this.size == \reach(Nodes, BinomialHeapNode, sibling + child).int_size();
    @
    @ invariant ( \forall BinomialHeapNode n; \reach(Nodes, BinomialHeapNode, sibling).has(n); (n.sibling != null ==> n.degree < n.sibling.degree) && (n.parent == null) );
    @
    @ invariant ( \forall BinomialHeapNode n; \reach(Nodes, BinomialHeapNode, sibling).has(n); n.key >= 0 );
    @
    @*/
	public /*@ nullable @*/BinomialHeapNode Nodes;

	public int size;

	public BinomialHeap() {
	}

	/*@
    @ requires true;
    @ ensures (\forall BinomialHeapNode n; \old(\reach(Nodes, BinomialHeapNode, child + sibling)).has(n); \reach(Nodes, BinomialHeapNode, child + sibling).has(n) && \old(n.key) == n.key);
    @ ensures value > 0 ==> (\exists BinomialHeapNode n; !\old(\reach(Nodes, BinomialHeapNode, child + sibling)).has(n); \reach(Nodes, BinomialHeapNode, child + sibling).has(n) && n.key == value);
    @ ensures value > 0 ==> size == \old(size) + 1;
    @ signals (Exception e) false;
    @
    @*/
	public void insert(int value) {
		if (value > 0) {

			BinomialHeapNode temp = new BinomialHeapNode();
			temp.key = value;

			if (Nodes == null) {

				Nodes = temp;
				size = 1;
			} else {

				unionNodes(temp);
				size++;
			}
		} 
	}



	private static BinomialHeapNode findMinNode(BinomialHeapNode arg) {
		BinomialHeapNode x = arg, y = arg;
		int min = x.key;

		while (x != null) {

			if (x.key < min) {
				y = x;
				min = x.key;
			}
			x = x.sibling;
		}

		return y;
	}


	/*@ requires true;
    @ ensures \old(Nodes) != null ==> \old(\reach(Nodes, BinomialHeapNode, child + sibling)).has(\result);
    @ ensures (\forall BinomialHeapNode n; \reach(Nodes, BinomialHeapNode, child + sibling).has(n); \result.key <= n.key);
    @ ensures (\forall BinomialHeapNode n; \reach(Nodes, BinomialHeapNode, child + sibling).has(n); \old(n.key) == n.key);
    @ signals (Exception e) false;
    @*/
	public /* @ nullable @ */BinomialHeapNode extractMin() {

		if (Nodes == null) 
			return null;

		int old_size = size;

		BinomialHeapNode temp = Nodes, prevTemp = null;
		BinomialHeapNode minNode = findMinNode(Nodes);
		while (temp.key != minNode.key) {
			prevTemp = temp;
			temp = temp.sibling;
		}

		if (prevTemp == null) {
			Nodes = temp.sibling;
		} else {
			prevTemp.sibling = temp.sibling;
		}
		temp = temp.child;
		BinomialHeapNode fakeNode = temp;
		while (temp != null) {
			temp.parent = null;
			temp = temp.sibling;
		}

		if ((Nodes == null) && (fakeNode == null)) {
			size = 0;
		} else {
			if ((Nodes == null) && (fakeNode != null)) {
				Nodes = fakeNode.reverse(null);
				size--;
			} else {
				if ((Nodes != null) && (fakeNode == null)) {
					size--;
				} else {
					unionNodes(fakeNode.reverse(null));
					size--;
				}
			}
		}

		//		if (this.size==12) {
		//		}
		return minNode;
	}

	// 3. Unite two binomial heaps
	// helper procedure
	private void merge( /* @ nullable @ */BinomialHeapNode binHeap ) {

		BinomialHeapNode temp1 = Nodes, temp2 = binHeap;
		while ((temp1 != null) && (temp2 != null)) {
			if (temp1.degree == temp2.degree) {
				BinomialHeapNode tmp = temp2;
				temp2 = temp2.sibling;
				tmp.sibling = temp1.sibling;
				temp1.sibling = tmp;
				temp1 = tmp.sibling;
			} else {
				if (temp1.degree < temp2.degree) {
					if ((temp1.sibling == null)
							|| (temp1.sibling.degree > temp2.degree)) {
						BinomialHeapNode tmp = temp2;
						temp2 = temp2.sibling;
						tmp.sibling = temp1.sibling;
						temp1.sibling = tmp;
						temp1 = tmp.sibling;
					} else {
						temp1 = temp1.sibling;
					}
				} else {
					BinomialHeapNode tmp = temp1;
					temp1 = temp2;
					temp2 = temp2.sibling;
					temp1.sibling = tmp;
					if (tmp == Nodes) {
						Nodes = temp1;
					} 
				}
			}
		}

		if (temp1 == null) {
			temp1 = Nodes;
			while (temp1.sibling != null) {
				temp1 = temp1.sibling;
			}
			temp1.sibling = temp2;
		} 

	}

	// another helper procedure
	private void unionNodes( /* @ nullable @ */BinomialHeapNode binHeap ) {
		merge(binHeap);

		BinomialHeapNode prevTemp = null, temp = Nodes , nextTemp = Nodes.sibling;

		while (nextTemp != null) {
			if ((temp.degree != nextTemp.degree)
					|| ((nextTemp.sibling != null) && (nextTemp.sibling.degree == temp.degree))) {
				prevTemp = temp;
				temp = nextTemp;
			} else {
				if (temp.key <= nextTemp.key) {
					temp.sibling = nextTemp.sibling;
					nextTemp.parent = temp;
					nextTemp.sibling = temp.child;
					temp.child = nextTemp;
					temp.degree++;
				} else {
					if (prevTemp == null) {
						Nodes = nextTemp;
					} else {
						prevTemp.sibling = nextTemp;
					}
					temp.parent = nextTemp;
					temp.sibling = nextTemp.child;
					nextTemp.child = temp;
					nextTemp.degree++;
					temp = nextTemp;
				}
			}

			nextTemp = temp.sibling;
		}
	}

	/*@ requires Nodes != null;
    @ ensures (\exists BinomialHeapNode n; \reach(Nodes, BinomialHeapNode, child + sibling).has(n); n.key == \result);
    @ ensures (\forall BinomialHeapNode n; \reach(Nodes, BinomialHeapNode, child + sibling).has(n); \result <= n.key);
    @ ensures (\forall BinomialHeapNode n; \reach(Nodes, BinomialHeapNode, child + sibling).has(n); \old(n.key) == n.key);
    @ ensures (\forall BinomialHeapNode n; \reach(Nodes, BinomialHeapNode, child + sibling).has(n); \old(n.degree) == n.degree);
    @ ensures (\forall BinomialHeapNode n; \reach(Nodes, BinomialHeapNode, child + sibling).has(n); \old(n.parent) == n.parent);
    @ ensures (\forall BinomialHeapNode n; \reach(Nodes, BinomialHeapNode, child + sibling).has(n); \old(n.sibling) == n.sibling);
    @ ensures (\forall BinomialHeapNode n; \reach(Nodes, BinomialHeapNode, child + sibling).has(n); \old(n.child) == n.child);
    @ signals (Exception e) false;
    @*/
	public int findMinimum() {
		BinomialHeapNode x = Nodes; //mutGenLimit 0
		BinomialHeapNode y = Nodes; //mutGenLimit 0
		int min = x.key; //mutGenLimit 0
		//@decreasing \reach(x, BinomialHeapNode, sibling).int_size();
		while (x != null) { //mutGenLimit 0
			if (x.key < min) { //mutGenLimit 0
				y = x; //mutGenLimit 0
				min = x.key; //mutGenLimit 0
			}
			x = x.sibling; //mutGenLimit 0
		}
		return y.key; //mutGenLimit 0
	}


	//	public static void main(String[] args) {
	//		roops.core.objects.BinomialHeap instance = new roops.core.objects.BinomialHeap();
	//		roops.core.objects.BinomialHeapNode _BinomialHeapNode_1 = new roops.core.objects.BinomialHeapNode();
	//		roops.core.objects.BinomialHeapNode _BinomialHeapNode_2 = new roops.core.objects.BinomialHeapNode();
	//		roops.core.objects.BinomialHeapNode _BinomialHeapNode_3 = new roops.core.objects.BinomialHeapNode();
	//		roops.core.objects.BinomialHeapNode _BinomialHeapNode_4 = new roops.core.objects.BinomialHeapNode();
	//	    roops.core.objects.BinomialHeapNode _BinomialHeapNode_5 = new roops.core.objects.BinomialHeapNode();
	//		// Fields Initialization for 'instance'
	//		// Fields Initialization for '_BinomialHeapNode_1'
	//		_BinomialHeapNode_1.key = 541093901;
	//		_BinomialHeapNode_1.degree = 0;
	//		_BinomialHeapNode_1.parent = null;
	//		_BinomialHeapNode_1.sibling = _BinomialHeapNode_2;
	//		// Fields Initialization for '_BinomialHeapNode_2'
	//		_BinomialHeapNode_2.key = 1161757636;
	//		_BinomialHeapNode_2.degree = 2;
	//		_BinomialHeapNode_2.parent = null;
	//		// Fields Initialization for '_BinomialHeapNode_3'
	//		_BinomialHeapNode_3.key = 1161757636;
	//		_BinomialHeapNode_3.degree = 1;
	//		_BinomialHeapNode_3.parent = _BinomialHeapNode_2;
	//		_BinomialHeapNode_3.sibling = _BinomialHeapNode_4;
	//		_BinomialHeapNode_3.child = _BinomialHeapNode_5;
	//		_BinomialHeapNode_2.sibling = null;
	//		// Fields Initialization for '_BinomialHeapNode_4'
	//		_BinomialHeapNode_4.key = 1161757636;
	//		_BinomialHeapNode_4.degree = 0;
	//		_BinomialHeapNode_4.parent = _BinomialHeapNode_2;
	//		_BinomialHeapNode_4.sibling = null;
	//		_BinomialHeapNode_4.child = null;
	//		_BinomialHeapNode_2.child = _BinomialHeapNode_3;
	//		_BinomialHeapNode_1.child = null;
	//
	//		_BinomialHeapNode_5.key = 1161757636;
	//		_BinomialHeapNode_5.degree = 0;
	//		_BinomialHeapNode_5.parent = _BinomialHeapNode_3;
	//		_BinomialHeapNode_5.sibling = null;
	//		_BinomialHeapNode_5.child = null;
	//
	//		
	//		instance.Nodes = _BinomialHeapNode_1;
	//		instance.size = 5;
	//		BinomialHeapNode bhn = instance.extractMin();
	//
	//
	//	}

	public static void main(String[] args) {
		roops.core.objects.BinomialHeap instance = new roops.core.objects.BinomialHeap();
		roops.core.objects.BinomialHeapNode _BinomialHeapNode_1 = new roops.core.objects.BinomialHeapNode();
		roops.core.objects.BinomialHeapNode _BinomialHeapNode_2 = new roops.core.objects.BinomialHeapNode();
		roops.core.objects.BinomialHeapNode _BinomialHeapNode_3 = new roops.core.objects.BinomialHeapNode();
		roops.core.objects.BinomialHeapNode _BinomialHeapNode_4 = new roops.core.objects.BinomialHeapNode();
		// Fields Initialization for 'instance'
		// Fields Initialization for '_BinomialHeapNode_1'
		updateValue(_BinomialHeapNode_1, "key", 1);
		updateValue(_BinomialHeapNode_1, "degree", 2);
		updateValue(_BinomialHeapNode_1, "parent", null);
		updateValue(_BinomialHeapNode_1, "sibling", null);
		// Fields Initialization for '_BinomialHeapNode_2'
		updateValue(_BinomialHeapNode_2, "key", 2);
		updateValue(_BinomialHeapNode_2, "degree", 1);
		updateValue(_BinomialHeapNode_2, "parent", _BinomialHeapNode_1);
		// Fields Initialization for '_BinomialHeapNode_3'
		updateValue(_BinomialHeapNode_3, "key", 1210483340);
		updateValue(_BinomialHeapNode_3, "degree", 0);
		updateValue(_BinomialHeapNode_3, "parent", _BinomialHeapNode_1);
		updateValue(_BinomialHeapNode_3, "sibling", null);
		updateValue(_BinomialHeapNode_3, "child", null);
		updateValue(_BinomialHeapNode_2, "sibling", _BinomialHeapNode_3);
		// Fields Initialization for '_BinomialHeapNode_4'
		updateValue(_BinomialHeapNode_4, "key", 134217728);
		updateValue(_BinomialHeapNode_4, "degree", 0);
		updateValue(_BinomialHeapNode_4, "parent", _BinomialHeapNode_2);
		updateValue(_BinomialHeapNode_4, "sibling", null);
		updateValue(_BinomialHeapNode_4, "child", null);
		updateValue(_BinomialHeapNode_2, "child", _BinomialHeapNode_4);
		updateValue(_BinomialHeapNode_1, "child", _BinomialHeapNode_2);
		updateValue(instance, "Nodes", _BinomialHeapNode_1);
		updateValue(instance, "size", 4);
		
		BinomialHeapNode bhn = instance.extractMin();

	}

	private static void updateValue(Object instance, String fieldName, Object value) {
		for (Field aField : instance.getClass().getDeclaredFields()) {
			if (aField.getName().equals(fieldName)) {
				try {
					boolean isAccessible = true;
					if (!aField.isAccessible()){
						aField.setAccessible(true);
						isAccessible = false;
					}
					if (aField.getType().isPrimitive()) {
						String typeSimpleName = aField.getType().getSimpleName();
						if (typeSimpleName.equals("boolean")) {
							aField.setBoolean(instance, (Boolean) value);
						} else if (typeSimpleName.endsWith("byte")) {
							aField.setByte(instance, (Byte) value);
						} else if (typeSimpleName.endsWith("char")) {
							aField.setChar(instance, (Character) value);
						} else if (typeSimpleName.endsWith("double")) {
							aField.setDouble(instance, (Double) value);
						} else if (typeSimpleName.endsWith("float")) {
							aField.setFloat(instance, (Float) value);
						} else if (typeSimpleName.endsWith("int")) {
							aField.setInt(instance, (Integer) value);
						} else if (typeSimpleName.endsWith("long")) {
							aField.setLong(instance, (Long) value);
						} else if (typeSimpleName.endsWith("short")) {
							aField.setShort(instance, (Short) value);
						} else {
							System.out.println("ERROR: No difinida");
						}
					} else {
						aField.set(instance, value);
					};
					if (!isAccessible)
						aField.setAccessible(false);
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
