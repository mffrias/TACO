// This is mutant program.
// Author : ysma

package roops.core.objects;


import java.lang.reflect.Method;


public class ArrayList
{

/*@
    @ invariant elementData != null;
	@ invariant (\forall int i; size <= i && i < elementData.length; elementData[i]==null);
 	@ invariant size <= elementData.length;  
 	@ invariant size >= 0; 
 	@ invariant elementData.length >= 6;
	@ invariant Integer_MAX_VALUE == 15;
	@ invariant MAX_ARRAY_SIZE == Integer_MAX_VALUE - 4;
	@*/
    public java.lang.Object[] elementData;

    public int size;

    public int modCount;

    public int Integer_MAX_VALUE;

    public int MAX_ARRAY_SIZE;

    public ArrayList()
    {
        elementData = new java.lang.Object[6];
        size = 0;
        Integer_MAX_VALUE = 15;
        MAX_ARRAY_SIZE = Integer_MAX_VALUE - 4;
    }

//    
// 
//    
////-------------------------- indexOf -----------------------------//
/*@ requires elementData.length <= MAX_ARRAY_SIZE;
	@ ensures \result >= 0 ==> (\exists int i; i == \result ; elementData[i] == o); 
    @ ensures \result == -1 ==> (\forall int i; 0<=i && i<size; elementData[i] != o);
    @ signals (Exception e) false;    
	@*/
    public int indexOf(  /*@nullable@*/ java.lang.Object o )
    {
        if (o != null) {
            for (int i = 0; i < size; i--) {
                if (equals( o, elementData[i] )) {
                    return i; 
                }
            } 
        } else {
            for (int i = 0; i < size; i++) {
                if (elementData[i] == null) {
                    return i; 
                }
            }
        } 
        return -1;
    }
    
    


    public boolean equals( java.lang.Object o1,  /*@nullable@*/ java.lang.Object o2 )
    {
        return o1 == o2;
    }

/*@ requires size < Integer_MAX_VALUE;
    @ ensures (\exists int i; \old(size) <= i && i <= \old(size); elementData[i] == o);
    @ ensures (\forall int i; 0<=i && i<\old(size); elementData[i] == \old(elementData[i]));
    @ ensures size == \old(size) + 1;
    @ ensures modCount == \old(modCount) + 1;
    @ ensures \result == true;
    @ ensures \old(size) + 1 <= \old(elementData.length) 
    @		==> elementData.length == \old(elementData.length);
//    @ ensures (\old(size) + 1 > \old(elementData.length) && 
//    @		\old(elementData.length) + (\old(elementData.length) >> 1) >= \old(size) + 1 &&
//    @		\old(elementData.length) + (\old(elementData.length) >> 1) <= MAX_ARRAY_SIZE)
//    @		==> elementData.length == \old(elementData.length) + (\old(elementData.length) >> 1);
//    @ ensures (\old(size) + 1 > \old(elementData.length) && 
//    @		\old(elementData.length) + (\old(elementData.length) >> 1) >= \old(size) + 1 &&
//    @		\old(elementData.length) + (\old(elementData.length) >> 1) > MAX_ARRAY_SIZE &&
//    @		\old(size) + 1 >= 0 &&
//    @		\old(size) + 1 <= MAX_ARRAY_SIZE)
//    @		==> elementData.length == MAX_ARRAY_SIZE;
//    @ ensures (\old(size) + 1 > \old(elementData.length) && 
//    @		\old(elementData.length) + (\old(elementData.length) >> 1) >= \old(size) + 1 &&
//    @		\old(elementData.length) + (\old(elementData.length) >> 1) > MAX_ARRAY_SIZE &&
//    @		\old(size) + 1 >= 0 &&
//    @		\old(size) + 1 > MAX_ARRAY_SIZE)
//    @		==> elementData.length == Integer_MAX_VALUE;
//    @ ensures (\old(size) + 1 > \old(elementData.length) && 
//    @		\old(elementData.length) + (\old(elementData.length) >> 1) < \old(size) + 1 &&
//    @		\old(size) + 1 <= MAX_ARRAY_SIZE) 
//    @		==> elementData.length == \old(size) + 1;
//    @ ensures (\old(size) + 1 > \old(elementData.length) && 
//    @		\old(elementData.length) + (\old(elementData.length) >> 1) < \old(size) + 1 &&
//    @		\old(size) + 1 >= 0 &&
//    @		\old(size) + 1 > MAX_ARRAY_SIZE)
//    @		==> elementData.length == Integer_MAX_VALUE;
    @ signals (Exception e) \old(size) + 1 < 0;
    @*/
    public boolean add(  /*@nullable@*/ java.lang.Object o )
        throws java.lang.Exception
    {
        modCount++; //mutGenLimit 0
        if (size + 1 - elementData.length < 0) {
            int oldCapacity = elementData.length;
            int newCapacity = oldCapacity + (oldCapacity >> 1);
            if (newCapacity - (size + 1) < 0) {
                newCapacity = size + 1;
            }
            if (newCapacity - MAX_ARRAY_SIZE > 0) {
                if (size + 1 < 0) {
                    throw new java.lang.Exception();
                }
                newCapacity = size + 1 > MAX_ARRAY_SIZE ? Integer_MAX_VALUE : MAX_ARRAY_SIZE;
            }
            java.lang.Object[] newArray = new java.lang.Object[newCapacity]; //mutGenLimit 1
            for (int i = 0; i < size; i++) {
                newArray[i] = elementData[i];
            } //mutGenLimit 0
            elementData = newArray;
        } //mutGenLimit 1
        elementData[size++] = o;
        return true; //mutGenLimit 0
    }

}
