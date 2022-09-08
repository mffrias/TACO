package roops.core.objects;

/*
 * Node in the Tree.  Doubles as a means to pass key-value pairs back to
 * user (see Map.Entry).
 */
/**
  * @SpecField blackHeight : int from this.left, this.right |
  * (
  *		( this.left=null && this.right=null => this.blackHeight=1 ) && 
  *
  *		( this.left!=null && this.right=null => ( 
  *		        ( ( this in this.left.*(left@+right)@-null ) => this.blackHeight=0 ) && 
  *		        ( ( this !in this.left.*(left@+right)@-null ) => ( 
  *		                ( this.left.color=true  => this.blackHeight=this.left.blackHeight +1 ) && 
  *		                ( this.left.color=false => this.blackHeight=this.left.blackHeight )  
  *		         ))
  *		                                        )) && 
  *		( this.left=null && this.right!=null => ( 
  *		        ( ( this in this.right.*(left@+right)@-null ) => this.blackHeight=0 ) && 
  *		        ( ( this !in this.right.*(left@+right)@-null ) => ( 
  *		                ( this.right.color=true  => this.blackHeight=this.right.blackHeight +1 ) && 
  *		                ( this.right.color=false => this.blackHeight=this.right.blackHeight )  
  *		         ))
  *		                                        )) &&
  * 
  *		( this.left!=null && this.right!=null => ( 
  *		        ( ( this in this.^(left@+right)@-null ) => this.blackHeight=0 ) && 
  *		        ( ( this !in this.^(left@+right)@-null ) => ( 
  *		                ( this.left.color=true  => this.blackHeight=this.left.blackHeight +1 ) && 
  *		                ( this.left.color=false => this.blackHeight=this.left.blackHeight )  
  *		                                        ))
  *		         ))                                  
  *
  * ) ;
  */
public class CopyOfTreeSetEntry {
	public int key;
	public /*@ nullable @*/CopyOfTreeSetEntry parent;

	public boolean color = false;
	public /*@ nullable @*/CopyOfTreeSetEntry left = null;
	public /*@ nullable @*/CopyOfTreeSetEntry right = null;

	public CopyOfTreeSetEntry () {}
}