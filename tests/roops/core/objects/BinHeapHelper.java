package roops.core.objects;

import roops.core.objects.BinomialHeap;
import roops.core.objects.BinomialHeapNode;

public class BinHeapHelper {
	
	
	
	//@ invariant true;
	
	public BinHeapHelper(){}
	
    /*@ requires true;
    @ ensures true;
    @*/
  public void unionNodesHelper(/* @ nullable @ */ BinomialHeap bh, /* @ nullable @ */roops.core.objects.BinomialHeapNode binHeap ) {
      mergeHelper(bh,  binHeap );
      roops.core.objects.BinomialHeapNode prevTemp = null;
      roops.core.objects.BinomialHeapNode temp = bh.Nodes;
      roops.core.objects.BinomialHeapNode nextTemp = bh.Nodes.sibling;
      while (nextTemp != null) {
          if (temp.degree != nextTemp.degree || nextTemp.sibling != null && nextTemp.sibling.degree == temp.degree) {
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
                      bh.Nodes = nextTemp;
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

  
  
  private void mergeHelper( /* @ nullable @ */ BinomialHeap bh, /* @ nullable @ */roops.core.objects.BinomialHeapNode binHeap ) {
      roops.core.objects.BinomialHeapNode temp1 = bh.Nodes;
      roops.core.objects.BinomialHeapNode temp2 = binHeap;
      while (temp1 != null && temp2 != null) {
          if (temp1.degree == temp2.degree) {
              roops.core.objects.BinomialHeapNode tmp = temp2;
              temp2 = temp2.sibling;
              tmp.sibling = temp1.sibling;
              temp1.sibling = tmp;
              temp1 = tmp.sibling;
          } else {
              if (temp1.degree < temp2.degree) {
                  if (temp1.sibling == null || temp1.sibling.degree > temp2.degree) {
                      roops.core.objects.BinomialHeapNode tmp = temp2;
                      temp2 = temp2.sibling;
                      tmp.sibling = temp1.sibling;
                      temp1.sibling = tmp;
                      temp1 = tmp.sibling;
                  } else {
                      temp1 = temp1.sibling;
                  }
              } else {
                  roops.core.objects.BinomialHeapNode tmp = temp1;
                  temp1 = temp2;
                  temp2 = temp2.sibling;
                  temp1.sibling = tmp;
                  if (tmp == bh.Nodes) {
                      bh.Nodes = temp1;
                  }
              }
          }
      }
      if (temp1 == null) {
          temp1 = bh.Nodes;
          while (temp1.sibling != null) {
              temp1 = temp1.sibling;
          }
          temp1.sibling = temp2;
      }
  }
  
  
}
