package forArielGodio.findInArray.bug01;
public class FindInArray {
	private /*@ spec_public @*/ int key;
	private /*@ spec_public @*/ int arr[];

	//@ ensures (\forall int i; 0 <= i && i < inputArr.length; inputArr[i] == arr[i]);
	//@ ensures key == 0;
	public FindInArray(int inputArr[])
	{
		int size = inputArr.length;
		arr = new int[size];
		arr = inputArr;//.clone(); jml does not support clone
	} 

	//@ ensures this.key == key;
	//@ ensures (\forall int i; 0 <= i && i < inputArr.length; inputArr[i] == arr[i]);
	public FindInArray(int inputArr[], int key)
	{
		int size = inputArr.length;
		arr = new int[size];
		arr = inputArr;//.clone(); jml does not support clone
		//setKey(key); //Removed call is the bug
	} 

	//@ assignable this.key;
	//@ ensures this.key == key;
	void setKey(int key) 
	{
		this.key = key;
	}

	//@ ensures \result == this.key;
	/*@ pure @*/ int getKey() 
	{
		return this.key;
	}

	//@ requires 0 <= i && i < arr.length;
	//@ ensures \result == this.arr[i];	
	/*@ pure @*/ int getArr(int i) 
	{
		return this.arr[i];
	}

	//@ ensures \result == arr.length;	
	/*@ pure @*/ int size() 
	{
		return arr.length;
	}

	/*@ ensures 0 <= \result && \result < arr.length ==> (arr[\result] == key && 
      @			(\forall int i; \result < i && i < arr.length; arr[i] != key)); 
      @ ensures \result == -1 ==> (\forall int i; 0 <= i && i < arr.length; arr[i] != key); @*/
	/*@ pure @*/ int findLast() {
		int index = size() - 1;
		//@ decreasing index;
		while (0 <= index) {
			if (getArr(index) == getKey())
				return index;
			index--;
		}
		return -1;
	}

	/*@ ensures 0 <= \result && \result < arr.length ==> (arr[\result] == key && 
      @			(\forall int i; 0 <= i && i < \result; arr[i] != key)); 
      @ ensures \result == -1 ==> (\forall int i; 0 <= i && i < arr.length; arr[i] != key); @*/
	/*@ pure @*/ int findFirst() {	
		//@ decreasing arr.length - index;
		for (int index = 0; index < size(); index++) {
			if (getArr(index) == getKey())
				return index;
		}
		return -1;
	}

	//@ ensures \result <==> findLast() != findFirst();
	/*@ pure @*/ boolean isMoreThanOneKey() {
		int first = findFirst();
		int last = findLast();
		return (first != last);
	}
}