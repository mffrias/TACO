/*****************************************************************************
 * University of Illinois/NCSA Open Source License
 *
 * Copyright (c) 2010 Rohan Sharma
 * All rights reserved.
 *
 * Developed by:  Rohan Sharma <sharma.rohan1990@gmail.com>
 *                University of Illinois at Urbana-Champaign
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal with the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 *  * Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimers.
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimers in the
 *    documentation and/or other materials provided with the distribution.
 *  * Neither the names of Rohan Sharma, University of Illinois at Urbana-
 *    Champaign, nor the names of its contributors may be used to endorse
 *    or promote products derived from this Software without specific prior
 *    written permission.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.  IN NO EVENT SHALL
 * THE CONTRIBUTORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR
 * OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 *****************************************************************************/

/**
 * A map implementation following the specification for the java.util.TreeMap
 * class but using primitive values of type "int" as keys.  The implementation
 * is an adaptation of red black trees described in the book "Introduction to
 * Algorithms" by Cormen, Leiserson, Rivest, and Stein.  The following code
 * can be used for testing research purposes.
 *
 * @author Rohan Sharma <sharma.rohan1990@gmail.com>
 * 
 */

/**
 * Goal 39 seems to be the most difficult branch, and goals 2 and 9 are other
 * difficult branches (provided by Andrea Arcuri <arcuri@simula.no>).
 */

package roops.core.objects;

/**
 * @Invariant 
 *		( this.root.parent in null ) &&
 *		( this.root!=null => this.root.color = true ) && 
 *		( all n: IntRedBlackTreeMapNode | n in this.root.*(left @+ right @+ parent) @- null => ( 
 *				(n.key != null ) &&
 *				( n.left != null => n.left.parent = n ) &&
 *				( n.right != null => n.right.parent = n ) &&
 *				( n.parent != null => n in n.parent.(left @+ right) ) &&
 *				( n !in n.^parent ) &&
 *				( all x : IntRedBlackTreeMapNode | (( x in n.left.^(left @+ right) @+ n.left @- null ) => ( n.key > x.key )) ) &&
 *				( all x : IntRedBlackTreeMapNode | (( x in n.right.^(left @+ right) @+ n.right @- null ) => ( x.key > n.key ))) &&
 *				( n.color = false && n.parent != null => n.parent.color = true ) && 
 *				( ( n.left=null && n.right=null ) => ( n.blackHeight=1 ) ) &&
 *				( n.left!=null && n.right=null => ( 
 *				      ( n.left.color = false ) && 
 *				      ( n.left.blackHeight = 1 ) && 
 *				      ( n.blackHeight = 1 )  
 *				)) &&
 *				( n.left=null && n.right!=null =>  ( 
 *				      ( n.right.color = false ) && 
 *				      ( n.right.blackHeight = 1 ) && 
 *				      ( n.blackHeight = 1 ) 
 *				 )) && 
 *				( n.left!=null && n.right!=null && n.left.color=false && n.right.color=false => ( 
 *				        ( n.left.blackHeight = n.right.blackHeight ) && 
 *				        ( n.blackHeight = n.left.blackHeight ) 
 *				)) && 
 *				( n.left!=null && n.right!=null && n.left.color=true && n.right.color=true => ( 
 *				        ( n.left.blackHeight=n.right.blackHeight ) && 
 *				        ( n.blackHeight=n.left.blackHeight + 1 )  
 *				)) && 
 *				( n.left!=null && n.right!=null && n.left.color=false && n.right.color=true => ( 
 *				        ( n.left.blackHeight=n.right.blackHeight + 1 ) && 
 *				        ( n.blackHeight = n.left.blackHeight  )  
 *				)) && 
 *				( n.left!=null && n.right!=null && n.left.color=true && n.right.color=false => ( 
 *				        ( n.right.blackHeight=n.left.blackHeight + 1 ) && 
 *				        ( n.blackHeight = n.right.blackHeight  )  )) 
 *				)) ; 
 */
public class IntRedBlackTreeMap {

    private int size;

	private/*@ nullable @*/ IntRedBlackTreeMapNode root;

	/**
	 * @Modifies_Everything;
	 * 
	 * @Ensures false;
	 */
    static public void putTest(/*@ nullable @*/ IntRedBlackTreeMap tree, int key, /*@ nullable @*/ Object value) {
    	if (tree!=null) {
    	  tree.put(key, value);
    	}
    }

	/**
	 * @Modifies_Everything;
	 * 
	 * @Ensures false;
	 */
    static public void removeTest(/*@ nullable @*/ IntRedBlackTreeMap tree, int key) {
    	if (tree!=null) {
    	  tree.remove(key);
    	}
    }

    public void put(int key, Object value) {
        IntRedBlackTreeMapNode x = new IntRedBlackTreeMapNode();
        x.key = key;
        x.value = value;
        treeInsert(x);
        size++;
        /*{roops.util.Goals.reached(0, roops.util.Verdict.REACHABLE);}*/
        /*{roops.util.Goals.reached(1, roops.util.Verdict.REACHABLE);}*/
    }

    public Object remove(int key) {
        IntRedBlackTreeMapNode ret = treeDelete(findNode(key));
        if (ret == null)
            return null;
        size--;
        return ret.value; 
    }

    public int getSize() {
        return size;
    }

    private boolean getColor(IntRedBlackTreeMapNode x) {
        // null nodes are colored black
    	boolean result;
    	if (x == null )
    		result = true;
    	else
    		result = x.color;
        return result;
    }

    private IntRedBlackTreeMapNode findNode(int key) {
        IntRedBlackTreeMapNode cur = root;
        while (cur != null && key!=cur.key)
            if (key < cur.key) {
                /*{roops.util.Goals.reached(26, roops.util.Verdict.REACHABLE);}*/
                cur = cur.left;
            }
            else {
                /*{roops.util.Goals.reached(27, roops.util.Verdict.REACHABLE);}*/
                cur = cur.right;
            }
        return cur;
    }

    private void leftRotate(IntRedBlackTreeMapNode x) {
        IntRedBlackTreeMapNode y = x.right;
        x.right = y.left;
        if (y.left != null) {
            /*{roops.util.Goals.reached(2);}*/
            y.left.parent = x;
        }
        y.parent = x.parent;
        if (x.parent == null) {
            /*{roops.util.Goals.reached(3);}*/
            root = y;
        }
        else if (x == x.parent.left) {
            /*{roops.util.Goals.reached(4);}*/
            x.parent.left = y;
        }
        else {
            /*{roops.util.Goals.reached(5);}*/
            x.parent.right = y;
        }
        y.left = x;
        x.parent = y;
    }

    private void rightRotate(IntRedBlackTreeMapNode x) {
        IntRedBlackTreeMapNode y = x.left;
        x.left = y.right;
        if (y.right != null) {
            /*{roops.util.Goals.reached(6);}*/
            y.right.parent = x;
        }
        y.parent = x.parent;
        if (x.parent == null) {
            /*{roops.util.Goals.reached(7);}*/
            root = y;
        }
        else if (x == x.parent.right) {
            /*{roops.util.Goals.reached(8);}*/
            x.parent.right = y;
        }
        else {
            /*{roops.util.Goals.reached(9);}*/
            x.parent.left = y;
        }
        y.right = x;
        x.parent = y;
    }

    private void treeInsert(IntRedBlackTreeMapNode z) {
        IntRedBlackTreeMapNode y = null;
        IntRedBlackTreeMapNode x = root;
        while (x != null) {
            y = x;
            if (z.key<x.key) {
                /*{roops.util.Goals.reached(10, roops.util.Verdict.REACHABLE);}*/
                x = x.left;
            }
            else if (z.key==x.key) {
                /*{roops.util.Goals.reached(11, roops.util.Verdict.REACHABLE);}*/
                x.value = z.value;
                size--;
                x=null;//fix return
            } else {
                /*{roops.util.Goals.reached(12, roops.util.Verdict.REACHABLE);}*/
                x = x.right;
            }
        }
        z.parent = y;
        if (y == null) {
            /*{roops.util.Goals.reached(13, roops.util.Verdict.REACHABLE);}*/
            root = z;
        }
        else if (z.key<y.key) {
            /*{roops.util.Goals.reached(14, roops.util.Verdict.REACHABLE);}*/
            y.left = z;
        }
        else {
            /*{roops.util.Goals.reached(15, roops.util.Verdict.REACHABLE);}*/
            y.right = z;
        }
        z.left = null;
        z.right = null;
        z.color = false;

        treeInsertFix(z);
    }

    private void treeInsertFix(IntRedBlackTreeMapNode z) {
        while (getColor(z.parent) == false) {
            if (z.parent == z.parent.parent.left) {
                /*{roops.util.Goals.reached(16, roops.util.Verdict.REACHABLE);}*/
                IntRedBlackTreeMapNode y = z.parent.parent.right;
                if (getColor(y) == false) {
                    /*{roops.util.Goals.reached(17, roops.util.Verdict.REACHABLE);}*/
                    z.parent.color = true;
                    y.color = true;
                    z.parent.parent.color = false;
                    z = z.parent.parent;
                } else {
                    /*{roops.util.Goals.reached(18, roops.util.Verdict.REACHABLE);}*/
                    if (z == z.parent.right) {
                        /*{roops.util.Goals.reached(19, roops.util.Verdict.REACHABLE);}*/
                        z = z.parent;
                        leftRotate(z);
                    }
                    z.parent.color = true;
                    z.parent.parent.color = false;
                    rightRotate(z.parent.parent);
                }
            } else {
                /*{roops.util.Goals.reached(20, roops.util.Verdict.REACHABLE);}*/
                IntRedBlackTreeMapNode y = z.parent.parent.left;
                if (getColor(y) == false) {
                    /*{roops.util.Goals.reached(21, roops.util.Verdict.REACHABLE);}*/
                    z.parent.color = true;
                    y.color = true;
                    z.parent.parent.color = false;
                    z = z.parent.parent;
                } else {
                    /*{roops.util.Goals.reached(22, roops.util.Verdict.REACHABLE);}*/
                    if (z == z.parent.left) {
                        /*{roops.util.Goals.reached(23, roops.util.Verdict.REACHABLE);}*/
                        z = z.parent;
                        rightRotate(z);
                    }
                    z.parent.color = true;
                    z.parent.parent.color = false;
                    leftRotate(z.parent.parent);
                }
            }
        }
        root.color = true;
    }

    private IntRedBlackTreeMapNode treeDelete(IntRedBlackTreeMapNode z) {
        if (z == null) {
            /*{roops.util.Goals.reached(0, roops.util.Verdict.REACHABLE);}*/
            return null;
        }
        IntRedBlackTreeMapNode x, y;
        if (z.left == null || z.right == null) {
            /*{roops.util.Goals.reached(1, roops.util.Verdict.REACHABLE);}*/
            y = z;
        }
        else {
            /*{roops.util.Goals.reached(2, roops.util.Verdict.REACHABLE);}*/
            y = getIOS(z);
        }
        if (y.left != null) {
            /*{roops.util.Goals.reached(3, roops.util.Verdict.REACHABLE);}*/
            x = y.left;
        }
        else {
            /*{roops.util.Goals.reached(4, roops.util.Verdict.REACHABLE);}*/
            x = y.right;
        }
        if (x != null) {
            /*{roops.util.Goals.reached(5, roops.util.Verdict.REACHABLE);}*/
            x.parent = y.parent;
        }
        if (y.parent == null) {
            /*{roops.util.Goals.reached(6, roops.util.Verdict.REACHABLE);}*/
            root = x;
        }
        else if (y == y.parent.left) {
            /*{roops.util.Goals.reached(7, roops.util.Verdict.REACHABLE);}*/
            y.parent.left = x;
        }
        else {
            /*{roops.util.Goals.reached(8, roops.util.Verdict.REACHABLE);}*/
            y.parent.right = x;
        }
        if (y != z) {
            /*{roops.util.Goals.reached(9, roops.util.Verdict.REACHABLE);}*/
            z.key = y.key;
            z.value = y.value;
        }
        if (getColor(y) == true) {
            if (x == null) {
                /*{roops.util.Goals.reached(10, roops.util.Verdict.REACHABLE);}*/
                treeDeleteFix(y);
            }
            else {
                /*{roops.util.Goals.reached(11, roops.util.Verdict.REACHABLE);}*/
                treeDeleteFix(x);
            }
        }
        return y;
    }

    private void treeDeleteFix(IntRedBlackTreeMapNode x) {
        while (x.parent != null && getColor(x) == true) {
            if (x == x.parent.left || x.parent.left == null) {
                /*{roops.util.Goals.reached(12, roops.util.Verdict.REACHABLE);}*/
                IntRedBlackTreeMapNode w = x.parent.right;
                if (w == null) {
                    /*{roops.util.Goals.reached(13, roops.util.Verdict.UNKNOWN);}*/
                    return;
                }
                if (getColor(w) == false) {
                    /*{roops.util.Goals.reached(14, roops.util.Verdict.REACHABLE);}*/
                    w.color = true;
                    x.parent.color = false;
                    leftRotate(x.parent);
                    w = x.parent.right;
                }
                if (getColor(w.left) == true && getColor(w.right) == true) {
                    /*{roops.util.Goals.reached(15, roops.util.Verdict.REACHABLE);}*/
                    w.color = false;
                    x = x.parent;
                } else {
                    /*{roops.util.Goals.reached(16, roops.util.Verdict.REACHABLE);}*/
                    if (getColor(w.right) == true) {
                        /*{roops.util.Goals.reached(17, roops.util.Verdict.REACHABLE);}*/
                        w.left.color = true;
                        rightRotate(w);
                        w = x.parent.right;
                    }
                    w.color = x.parent.color;
                    x.parent.color = true;
                    if (w.right != null) {
                        /*{roops.util.Goals.reached(18, roops.util.Verdict.REACHABLE);}*/
                        w.right.color = true;
                    }
                    leftRotate(x.parent);
                    x = root;
                }
            } else {
                /*{roops.util.Goals.reached(19, roops.util.Verdict.REACHABLE);}*/
                IntRedBlackTreeMapNode w = x.parent.left;
                if (w == null) {
                    /*{roops.util.Goals.reached(20, roops.util.Verdict.UNKNOWN);}*/
                    return;
                }
                if (getColor(w) == false) {
                    /*{roops.util.Goals.reached(21, roops.util.Verdict.REACHABLE);}*/
                    w.color = true;
                    x.parent.color = false;
                    rightRotate(x.parent);
                    w = x.parent.left;
                }
                if (getColor(w.right) == true && getColor(w.left) == true) {
                    /*{roops.util.Goals.reached(22, roops.util.Verdict.REACHABLE);}*/
                    w.color = false;
                    x = x.parent;
                } else {
                    /*{roops.util.Goals.reached(23, roops.util.Verdict.REACHABLE);}*/
                    if (getColor(w.left) == true) {
                        /*{roops.util.Goals.reached(24, roops.util.Verdict.REACHABLE);}*/
                        w.right.color = true;
                        leftRotate(w);
                        w = x.parent.left;
                    }
                    w.color = x.parent.color;
                    x.parent.color = true;
                    if (w.left != null) {
                        /*{roops.util.Goals.reached(25, roops.util.Verdict.REACHABLE);}*/
                        w.left.color = true;
                    }
                    rightRotate(x.parent);
                    x = root;
                }
            }
        }
        x.color = true;
    }

    private IntRedBlackTreeMapNode getIOS(IntRedBlackTreeMapNode z) {
        IntRedBlackTreeMapNode x, y = null;
        x = z.right;
        while (x != null) {
            y = x;
            x = x.left;
        }
        return y;
    }
}
/* end roops.util */

