import java.util.Random;

public class SelectionSortExample {  
	public static void selectionSort(int[] arr){  
		for (int i = 0; i < arr.length - 1; i++) {  
			int index = i;  
			for (int j = i + 1; j < arr.length; j++) {  
				if (arr[j] < arr[index]){  
					index = j;//searching for lowest index  
				}  
			}  
			int smallerNumber = arr[index];   
			arr[index] = arr[i];  
			arr[i] = smallerNumber;  
		}  
	}  

//	public static void main(String a[]){
//		int[] arr1 = new int[400000];
//		for (int index = 0; index < arr1.length; index++) {
//			arr1[index] = index;
//		}
////		System.out.println("Before Selection Sort");
////		for(int i:arr1){
////			System.out.print(i+" ");
////		}
//		System.out.println();
//		long previous = System.currentTimeMillis();
//		selectionSort(arr1);//sorting array using selection sort
//		long elapsed = System.currentTimeMillis() - previous;
//		System.out.println("Actual running time on sorted input = " + elapsed);
////		System.out.println("After Selection Sort");
////		for(int i:arr1){
////			System.out.print(i+" ");
////		}
//
//
//		for (int index = 0; index < arr1.length; index++) {
//			arr1[index] = arr1.length - index - 1;
//		}
//		System.out.println();
//		previous = System.currentTimeMillis();
//		selectionSort(arr1);//sorting array using selection sort
//		elapsed = System.currentTimeMillis() - previous;
//		System.out.println("Actual running time on reverserly sorted input = " + elapsed);
//
//		Random rand = new Random();
//		int upperbound = arr1.length;
//		for (int index = 0; index < arr1.length; index++) {
//			arr1[index] = rand.nextInt(upperbound);
//		}
//		System.out.println();
//		previous = System.currentTimeMillis();
//		selectionSort(arr1);//sorting array using selection sort
//		elapsed = System.currentTimeMillis() - previous;
//		System.out.println("Actual running time on randomly sorted input = " + elapsed);
////		System.out.println("After Selection Sort");
////		for(int i:arr1){
////			System.out.print(i+" ");
////		}
//
//	}


	public static void main(String[] args){

		int n = Integer.MAX_VALUE;
		System.out.println(n);
		n = n + 1;
		System.out.println(n);
	}

}  