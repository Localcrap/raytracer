package com.magnus;

// Java implementation of QuickSort
// using Hoare's partition scheme
class GFG {

	static void swap(RObject[] arr, int i, int j)
    {
        RObject temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }
	/* This function takes first element as pivot, and
	places all the elements smaller than the pivot on the
	left side and all the elements greater than the pivot
	on the right side. It returns the index of the last
	element on the smaller side*/
    static int partition(RObject[] arr, int low, int high,int axis)
    {
  
        // pivot
        RObject pivot = arr[high];
  
        // Index of smaller element and
        // indicates the right position
        // of pivot found so far
        int i = (low - 1);
  
        for (int j = low; j <= high - 1; j++) {
  
            // If current element is smaller
            // than the pivot
            if (box_compare(arr[j], pivot, axis)) {
  
                // Increment index of
                // smaller element
                i++;
                swap(arr, i, j);
            }
        }
        swap(arr, i + 1, high);
        return (i + 1);
    }

	/* The main function that
	implements QuickSort
	arr[] --> Array to be sorted,
	low --> Starting index,
	high --> Ending index */
	static void quickSort(RObject[] arr, int low, int high,int axis)
    {
        if (low < high) {
  
            // pi is partitioning index, arr[p]
            // is now at right place
            int pi = partition(arr, low, high,axis);
  
            // Separately sort elements before
            // partition and after partition
            quickSort(arr, low, pi - 1,axis);
            quickSort(arr, pi + 1, high,axis);
        }
    }
	/* Function to print an array */
	static void printArray(int[] arr, int n)
	{
		for (int i = 0; i < n; i++)
			System.out.print(" " + arr[i]);
		System.out.println();
	}
	static void shitSort(RObject[] arr, int len, int axis){
		for (int i=0;i<len-1;++i){

            for(int j=0;j<len-i-1; ++j){
				if(box_compare(arr[j+1], arr[j], axis)){
					swap(arr, j+1, j);
				}
			}
		}
	}

	static boolean box_compare(RObject a, RObject b, int axis){
		BVHValues boxA =new BVHValues(0, 0, null);
		BVHValues boxB =new BVHValues(0, 0, null);

		if(!a.boundingBox(boxA) || !b.boundingBox(boxB)){
			System.out.println("box missing");
		}
		


		return boxA.outputBox.min().itterate(axis) < boxB.outputBox.min().itterate(axis);
	}
}

// This code is contributed by vt_m.
