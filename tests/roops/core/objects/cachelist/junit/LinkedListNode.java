/****************************************************************************
Author: Juan Pablo Galeotti and Marcelo Frias, Relational Formal Methods 
Group, University of Buenos Aires and Buenos Aires Institute of Technology,
Buenos Aires, Argentina.
Companion LinkedListNode implementation for a ROOPS class implementing the 
apache.commons.collections class NodeCachingLinkedList.

The class has annotations in JFSL [1] given as ROOPS comments.

[1] http://sdg.csail.mit.edu/forge/plugin.html
****************************************************************************/



package roops.core.objects.cachelist.junit;

public class LinkedListNode {

    /** A pointer to the node before this node */
    /*@ nullable @*/LinkedListNode previous;
    /** A pointer to the node after this node */
    /*@ nullable @*/LinkedListNode next;
    /** The object contained within this node */
    /*@ nullable @*/Object value;

    public LinkedListNode() {}


}
/* end roops.core.objects */

