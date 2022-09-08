//
// Copyright (C) 2006 United States Government as represented by the
// Administrator of the National Aeronautics and Space Administration
// (NASA).  All Rights Reserved.
// 
// This software is distributed under the NASA Open Source Agreement
// (NOSA), version 1.3.  The NOSA has been approved by the Open Source
// Initiative.  See the file NOSA-1.3-JPF at the top of the distribution
// directory tree for the complete NOSA document.
// 
// THE SUBJECT SOFTWARE IS PROVIDED "AS IS" WITHOUT ANY WARRANTY OF ANY
// KIND, EITHER EXPRESSED, IMPLIED, OR STATUTORY, INCLUDING, BUT NOT
// LIMITED TO, ANY WARRANTY THAT THE SUBJECT SOFTWARE WILL CONFORM TO
// SPECIFICATIONS, ANY IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR
// A PARTICULAR PURPOSE, OR FREEDOM FROM INFRINGEMENT, ANY WARRANTY THAT
// THE SUBJECT SOFTWARE WILL BE ERROR FREE, OR ANY WARRANTY THAT
// DOCUMENTATION, IF PROVIDED, WILL CONFORM TO THE SUBJECT SOFTWARE.
//
package roops.core.objects.bintree.junit;


/**
 * 
 * @Invariant ( all n : BinTreeNode | n in this.root.*(left @+ right ) => ( 
 *            ( n !in n.^(left @+ right) ) && 
 *            ( all m: BinTreeNode | m in n.left.*(left @+right) => m.key < n.key ) && 
 *            ( all m: BinTreeNode | m in n.right.*(left @+right) => n.key < m.key ) && 
 *            ( n.left!=null => n.left.parent=n ) &&
 *            ( n.right!=null=> n.right.parent=n ) && 
 *            ( n=this.root => n.parent=null ) ) )
 *            &&
 *            ( this.size = #(this.root.*(left @+ right) @- null) );
 * 
 */
public class BinTree {

	/**
	 * @Modifies_Everything;
	 * 
	 * @Requires tree.size=4;
	 * 
	 * @Ensures return==true;
	 */
	static public boolean showInstance(/*@ nullable @*/ BinTree tree) {
		return false;
	}


	
	private /*@ nullable @*/ BinTreeNode root;
	
	private int size;

	public BinTree() {}
}
