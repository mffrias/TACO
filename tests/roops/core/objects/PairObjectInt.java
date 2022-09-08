package roops.core.objects;

public class PairObjectInt {

	Object fst;
	int snd;
	
	PairObjectInt(Object o, int i){
		fst = o;
		snd = i;
	}
	
	Object getFirst(PairObjectInt p){
		return p.fst;
	}
	
	int getSecond(PairObjectInt p){
		return p.snd;
	}

}
