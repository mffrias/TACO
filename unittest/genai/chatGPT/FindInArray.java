package genai.chatGPT;
public class FindInArray {

    private int[] arr;
    private int key;

    /*@ public invariant arr != null; @*/

    /*@
      @ requires inputArr != null;
      @ ensures arr != inputArr;
      @ ensures arr.length == inputArr.length;
      @ ensures (\forall int i; 0 <= i && i < inputArr.length;
      @              arr[i] == inputArr[i]);
      @ assignable arr, key;
      @*/
    public FindInArray(int[] inputArr) {
        this.arr = inputArr.clone();
    }

    /*@
      @ requires inputArr != null;
      @ ensures arr != inputArr;
      @ ensures arr.length == inputArr.length;
      @ ensures (\forall int i; 0 <= i && i < inputArr.length;
      @              arr[i] == inputArr[i]);
      @ ensures this.key == key;
      @ assignable arr, this.key;
      @*/
    public FindInArray(int[] inputArr, int key) {
        this.arr = inputArr.clone();
        this.key = key;
    }

    /*@
      @ ensures this.key == key;
      @ assignable this.key;
      @*/
    public void setKey(int key) {
        this.key = key;
    }

    /*@
      @ ensures \result == this.key;
      @ pure
      @*/
    public int getKey() {
        return this.key;
    }

    /*@
      @ requires 0 <= i && i < arr.length;
      @ ensures \result == arr[i];
      @ pure
      @*/
    public int getArr(int i) {
        return arr[i];
    }
}