package ar.edu.itba.wp;

public class ArbolBinarioDeBusqueda
{


	/*@ invariant (\forall Nodo n; \reach(raiz, Nodo, left+right).has(n) == true; 
    		(\forall Nodo m1; \reach(n.left, Nodo, left+right).has(m1) == true; m1.dato < n.dato) &&
    		(\forall Nodo m2; \reach(n.right, Nodo, left+right).has(m2) == true; m2.dato > n.dato)
		 );
	@*/

	/*@ invariant size == \reach(raiz, Nodo, left+right).int_size(); @*/

	/*@ invariant (\forall Nodo n; \reach(raiz, Nodo, left+right).has(n) == true; n.left != null ==> n.left.padre == n);
	    invariant (\forall Nodo n; \reach(raiz, Nodo, left+right).has(n) == true; n.right != null ==> n.right.padre == n);
	 @*/

	/*@ invariant raiz != null ==> raiz.padre == null; @*/


	public /*@ nullable @*/ Nodo raiz;

	public int size = 0;



	//@ requires true;
	//@ ensures true;
	//@ signals (Exception e) false;
	public int mostrarArbol() {
		return 0;
	}


	//@ requires true;
	//@ ensures \result == true <==> 
	//@	   (\exists Nodo n; \reach(raiz, Nodo, left+right).has(n) == true; n.dato == k);

	public boolean find(int k, ArbolBinarioDeBusqueda a, Nodo b, int l) {
        
		Nodo aux = a.raiz.left;
		while (aux != null){
			if (aux.dato == k)
				return true;
			if(k < aux.dato){
				aux = aux.left;
			}else {
				aux = aux.right;
			}
		}
		return false;
	}
	
	
}
