package forArielGodio;
public class FindInArray {

	public int key;
	public int arr[];



	//@ ensures (\forall int i; 0 <= i && i < inputArr.length; inputArr[i] == arr[i]);
	//@ ensures key == 0;
	public FindInArray(int inputArr[])
	{
		int size = inputArr.length;
		arr = new int[size];
		arr = inputArr; //jml does not support clone
	} 

	//@ ensures this.key == key;
	//@ ensures (\forall int i; 0 <= i && i < inputArr.length; inputArr[i] == arr[i]);
	public FindInArray(int inputArr[], int key)
	{
		int size = inputArr.length;
		arr = new int[size];
		arr = inputArr; //jml does not support clone
		//setKey(key); Removed call is the bug
	} 

}