package jason.jml.treeset;


//@ model import org.jmlspecs.models.*;

public class TreeSet {

	private static final boolean RED = false;

	private static final boolean BLACK = true;

	private transient/*@ nullable @*/Entry root;

	private transient int size;

	private transient int modCount;

	//@ public model non_null JMLObjectSet entries;

	/*@ 
	  @ private represents entries \such_that entries.int_size()==\reach(root,Entry,left).int_size() &&
	  @                             (\forall Entry entry ; entries.has(entry) <==> \reach(root,Entry,left).has(entry)); 
	  @*/
	
	/*@
	  @ static invariant RED==false;
	  @ static invariant BLACK==true;
	  @*/
	
	/*@
	  @ private invariant root!=null ==> root.parent==null;
	  @
	  @ private invariant root!=null ==> root.color== BLACK;
	  @
	  @ private invariant (\forall Entry n ; \reach(root, Entry, left).has(n) ;
	  @
	  @            ( n.left != null  ==> n.left.parent == n ) &&
	  @            ( n.right != null ==> n.right.parent == n ) && 
	  @            ( n.parent != null ==> ( n.parent.left==n || n.parent.right==n ) ) &&
	  @            ( ( n.color == RED && n.parent != null ) ==> n.parent.color == BLACK ) && 
	  @
	  @            ( !\reach(n.parent,Entry,parent).has(n) ) &&
	  @           
	  @            (\forall Entry x ; \reach(n.left, Entry, left).has(x) ;  x.key < n.key ) &&
	  @
	  @            (\forall Entry x ; \reach(n.right, Entry, left).has(x) ;  x.key > n.key ) &&
	  @
	  @            ( ( n.left==null && n.right==null ) ==> ( n.blackHeight==1 ) ) &&
	  @ 
	  @            ( ( n.left!=null && n.right==null ) ==> (
	  @			      ( n.left.color==RED ) && 
	  @			      ( n.left.blackHeight== 1 ) && 
	  @			      ( n.blackHeight== 1 ) 
	  @			   )) &&
	  @  
      @            ( ( n.left==null && n.right!=null ) ==>  ( 
	  @			      ( n.right.color==RED ) && 
	  @			      ( n.right.blackHeight== 1 ) && 
	  @			      ( n.blackHeight==1 ) 
	  @			   )) && 
	  @ 
	  @		       ( ( n.left!=null && n.right!=null && n.left.color==RED && n.right.color==RED ) ==> ( 
	  @			      ( n.left.blackHeight == n.right.blackHeight ) && 
	  @			      ( n.blackHeight == n.left.blackHeight ) 
	  @			   )) && 
      @
	  @			   ( ( n.left!=null && n.right!=null && n.left.color==BLACK && n.right.color==BLACK ) ==> ( 
	  @			      ( n.left.blackHeight==n.right.blackHeight ) && 
	  @			      ( n.blackHeight==n.left.blackHeight + 1 )  
	  @			   )) &&
	  @
	  @ 		   ( ( n.left!=null && n.right!=null && n.left.color==RED && n.right.color==BLACK ) ==> ( 
	  @	              ( n.left.blackHeight==n.right.blackHeight + 1 ) && 
	  @ 	          ( n.blackHeight == n.left.blackHeight  )  
	  @			   )) &&
	  @ 
	  @			   ( ( n.left!=null && n.right!=null && n.left.color==BLACK && n.right.color==RED ) ==> ( 
	  @			        ( n.right.blackHeight==n.left.blackHeight + 1 ) && 
	  @			        ( n.blackHeight == n.right.blackHeight  )  
	  @            ))
	  @
	  @           );
	  @*/	
	
	
	
  /*@
    @ assignable \nothing;
    @
    @ ensures \result == (\exists Entry entry ; entries.has(entry) && entry.key==o ) ;
    @*/
	public boolean contains(int o) {
		return m_containsKey(o);
	}

	/*@
	  @ assignable \nothing;
	  @*/
	private/*@ helper @*/boolean m_containsKey(int key2) {
		return getEntry(key2) != null;
	}

	/*@
	  @ assignable \nothing;
	  @*/
	private /*@ helper @*/final/*@ nullable @*/Entry getEntry(int key2) {
		Entry p = root;
		while (p != null) {
			if (key2 < p.key)
				p = p.left;
			else if (key2 > p.key)
				p = p.right;
			else
				return p;
		}
		return null;
	}

	
	
    /*@
      @ assignable \everything;
      @
      @ ensures (\forall Entry entry ; 
      @               entries.has(entry) <==> ( \old(entries).has(entry) || entry.key==e ) 
      @         );
      @*/
	public void add(int e) {
		m_put(e);
	}

	private/*@ helper @*/void m_put(int key2) {
		Entry t = root;
		if (t == null) {
			root = new Entry(key2, null);
			size = 1;
			modCount++;
			return;
		}
		Entry parent;

		int cmp;
		do {
			parent = t;
			if (key2 < t.key) {
				t = t.left;
				cmp = -1;
			} else if (key2 > t.key) {
				t = t.right;
				cmp = +1;
			} else {
				return;
			}
		} while (t != null);

		Entry e = new Entry(key2, parent);

		if (cmp < 0)
			parent.left = e;
		else
			parent.right = e;

		fixAfterInsertion(e);
		size++;
		modCount++;
		return;
	}

	/** From CLR */
	private/*@ helper @*/void fixAfterInsertion(/*@ nullable @*/Entry x) {
		x.color = RED;

		while (x != null && x != root && x.parent.color == RED) {
			if (parentOf(x) == leftOf(parentOf(parentOf(x)))) {
				Entry y = rightOf(parentOf(parentOf(x)));
				if (colorOf(y) == RED) {
					setColor(parentOf(x), BLACK);
					setColor(y, BLACK);
					setColor(parentOf(parentOf(x)), RED);
					x = parentOf(parentOf(x));
				} else {
					if (x == rightOf(parentOf(x))) {
						x = parentOf(x);
						rotateLeft(x);
					}
					setColor(parentOf(x), BLACK);
					setColor(parentOf(parentOf(x)), RED);
					rotateRight(parentOf(parentOf(x)));
				}
			} else {
				Entry y = leftOf(parentOf(parentOf(x)));
				if (colorOf(y) == RED) {
					setColor(parentOf(x), BLACK);
					setColor(y, BLACK);
					setColor(parentOf(parentOf(x)), RED);
					x = parentOf(parentOf(x));
				} else {
					if (x == leftOf(parentOf(x))) {
						x = parentOf(x);
						rotateRight(x);
					}
					setColor(parentOf(x), BLACK);
					setColor(parentOf(parentOf(x)), RED);
					rotateLeft(parentOf(parentOf(x)));
				}
			}
		}
		root.color = BLACK;
	}

	private/*@ helper @*/void rotateLeft(/*@ nullable @*/Entry p) {
		if (p != null) {
			Entry r = p.right;
			p.right = r.left;
			if (r.left != null)
				r.left.parent = p;
			r.parent = p.parent;
			if (p.parent == null)
				root = r;
			else if (p.parent.left == p)
				p.parent.left = r;
			else
				p.parent.right = r;
			r.left = p;
			p.parent = r;
		}
	}

	private/*@ helper @*/void rotateRight(/*@ nullable @*/Entry p) {
		if (p != null) {
			Entry l = p.left;
			p.left = l.right;
			if (l.right != null)
				l.right.parent = p;
			l.parent = p.parent;
			if (p.parent == null)
				root = l;
			else if (p.parent.right == p)
				p.parent.right = l;
			else
				p.parent.left = l;
			l.right = p;
			p.parent = l;
		}
	}

	private/*@ helper @*/static boolean colorOf(/*@ nullable @*/Entry p) {
		return (p == null ? BLACK : p.color);
	}

	private/*@ helper @*/static/*@ nullable @*/Entry parentOf(
	/*@ nullable @*/Entry p) {
		return (p == null ? null : p.parent);
	}

	private/*@ helper @*/static void setColor(/*@ nullable @*/Entry p,
			boolean c) {
		if (p != null)
			p.color = c;
	}

	private/*@ helper @*/static/*@ nullable @*/Entry leftOf(
	/*@ nullable @*/Entry p) {
		return (p == null) ? null : p.left;
	}

	private/*@ helper @*/static/*@ nullable @*/Entry rightOf(
	/*@ nullable @*/Entry p) {
		return (p == null) ? null : p.right;
	}

}
